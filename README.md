# CafeJava

Reactive API for invoking MobileFirst Platform (MFP) procedures from an Android client. Support for auto-serialization of JSON responses is also provided in a reactive manner.

Connecting to an MFP instance from an `Activity` is as simple as:

``` java
new CafeJava().connect(this)
              .subscribe(new Action1<WLResponse>() {
                  @Override public void call(WLResponse wlResponse) {
                      // onSuccess
                  }
              }, new Action1<Throwable>() {
                  @Override public void call(Throwable throwable) {
                      // onFailure
                  }
              });
```

Invoking a procedure for a given adapter is done in similar fashion:

``` java
new CafeJava().invokeProcedure("adapter", "procedure")
              .subscribe(new Action1<WLResponse>() {
                  @Override public void call(WLResponse wlResponse) {
                      // onSuccess
                  }
              }, new Action1<Throwable>() {
                  @Override public void call(Throwable throwable) {
                      // onFailure
                  }
              });
```

We can use [retrolambda](https://github.com/orfjackal/retrolambda) to reduce the clunky syntax of anonymous classes:

``` java
new CafeJava().invokeProcedure("adapter", "procedure")
              .subscribe(response -> {}, throwable -> {});
```

Furthermore, we can easily provide the necessary parameters for a procedure invocation:

``` java
new CafeJava().invokeProcedure("adapter", "procedure", "arg1", "arg2", "arg3");
```

Request options, such as a timeout, can be specified on a `CafeJava` instance. This instance can then be shared across multiple procedure invocations:

``` java
CafeJava cafeJava = new CafeJava().setTimeout(5000);
cafeJava.invokeProcedure("adapter", "procedureOne");
cafeJava.invokeProcedure("adapter", "procedureTwo");
```

CafeJava also provides auto-serialization support for procedure invocations that return valid JSON. We can supply the `Class` literal of the type we want to serialize to and chain our `Observable<WLResponse>` with the compose operator:

``` json
{
  "name": "John Smith",
  "age": 42,
  "isDeveloper": true
}
```

``` java
Observable<Person> personObservable =
    observable.compose(CafeJava.serializeTo(Person.class));
```

For more complex responses where the desired data for serialization is nested, we can supply the list of member names that will obtain the serializable data:

``` json
{
  "result": {
    "person": {
      "name": "John Smith",
      "age": 42,
      "isDeveloper": true
    }
  }
}
```

``` java
observable.compose(CafeJava.serializeTo(Person.class, "result", "person"));
```

If the response returns an array, we can use `TypeToken` from the Gson library to help us:

``` json
{
  "result": [
    {
      "name": "John Smith",
      "age": 42,
      "isDeveloper": true
    },
    {
      "name": "Robert Jones",
      "age": 26,
      "isDeveloper": false
    },
    {
      "name": "Mary Davis",
      "age": 33,
      "isDeveloper": true
    }
  ]
}
```

``` java
TypeToken<List<Person>> typeToken = new TypeToken<List<Person>>(){};
Observable<List<Person>> peopleObservable =
    observable.compose(CafeJava.serializeTo(typeToken, "result"));
```

## Installation

In your app's `build.gradle` file:

``` gradle
compile 'com.ibm.mil:cafejava:1.0.0'
```

In the same file, add the following `packagingOptions` to the `android` closure:

``` gradle
android {
    ...
    packagingOptions {
        pickFirst 'META-INF/ASL2.0'
        pickFirst 'META-INF/LICENSE'
        pickFirst 'META-INF/NOTICE'
    }
}
```

The `wlclient.properties` file generated with your MFP project also needs to be added to your app's `assets` folder.

CafeJava requires a **minimum SDK version of 9** or above.

## Running the sample app
We will use the [MFP CLI](https://developer.ibm.com/mobilefirstplatform/install/#clui) to deploy the MFP sample project to our localhost.

From the `/cafejava-sample/MFPSampleProject` directory, issue the following command to build and deploy the MFP adapters:
```
mfp bd
```
We can verify that our local instance is working properly by invoking one of the sample procedures:
```
mfp adapter call SampleAdapter/getPersonFlat
```
This should return the following response:
```
{
  "isDeveloper": true,
  "isSuccessful": true,
  "age": 22,
  "name": "Johnny Appleseed"
}
```
If we are deploying the sample app to the Android emulator, there is no need to update wlclient.properties, which is found under the sample project's assets folder. Otherwise, we will need to change the `wlServerHost` key to our machine's IP address.

Note: A new wlclient.properties file gets generated under `/cafejava-sample/MFPSampleProject/apps/SampleAndroidFramework` each time you deploy the adapter. This file can simply replace the one found in the sample project's assets folder.

## License
```
Licensed Materials - Property of IBM
© Copyright IBM Corporation 2015. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

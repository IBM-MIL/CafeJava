# CafeJava

Reactive API for invoking MFP procedures from an Android client. Support for auto-serialization of JSON responses is also provided in a reactive manner.

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
CafeJava cafeJava = new CafeJava.setTimeout(5000);
cafeJava.invokeProcedure("adapter", "procedureOne");
cafeJava.invokeProcedure("adapter", "procedureTwo");
```

CafeJava also provides auto-serialization support for procedure invocations that return valid JSON. We can supply the `Class` object of the type we want to serialize to and chain our `Observable<WLResponse>` with the compose operator:

``` java
Observable<Person> personObservable =
    observable.compose(CafeJava.serializeTo(Person.class));
```

For more complex responses where the desired data for serialization is nested, we can supply the list of member names that will obtain the serializable data:

``` java
observable.compose(CafeJava.serializeTo(Person.class, "result", "person"));
```

If the response returns an array, we can use `TypeToken` from the Gson library to help us:

``` java
TypeToken<List<Person>> typeToken = new TypeToken<List<Person>>(){};
Observable<List<Person>> peopleObservable =
    observable.compose(CafeJava.serializeTo(typeToken, "result"));
```

## Running the sample app
We will use the [MFP CLI](https://developer.ibm.com/mobilefirstplatform/install/#clui) to deploy the MFP sample project to our localhost.

From the `/cafejava-sample/MFPSampleProject` directory, issue the following command:
```
mfp start
```
Once the MFP server has successfully started, we can deploy the adapters to our local instance:
```
mfp deploy
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
  "name": "FirstName LastName"
}
```
In order for the Android sample app to connect to our local instance, we need to update wlclient.properties (found under the sample project's assets folder) by changing the value of the `wlServerHost` key to our machine's IP address.

Note: A new wlclient.properties file gets generated under `/cafejava-sample/MFPSampleProject/apps/SampleAndroidFramework` each time you deploy the adapter. This file can simply replace the one found in the sample project's assets folder.

## Download

For now, clone the project and add the `:cafejava` module as a library dependency to your project. We will soon upload an AAR to jcenter for easy gradle support.

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

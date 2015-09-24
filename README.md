# CafeJava

Reactive API for invoking [MobileFirst Platform](http://www-03.ibm.com/software/products/en/mobilefirstplatform) (MFP) procedures from an Android client.

To connect to an MFP instance, simply call:

``` java
Observable<WLResponse> connect = CafeJava.connect(context);
connect.subscribe();
```

With MFP 7.0 came the introduction of Java adapters. We can create a `JavaProcedureInvoker` that will handle the request for a procedure invocation:

``` java
ProcedureInvoker invoker = new JavaProcedureInvoker.Builder("adapterName", "path/{path_param}")
        .pathParam("path_param", value)
        .queryParam("param_name", value)
        .httpMethod(HttpMethod.GET /* default is GET */)
        .build();
```

We can then pass our `ProcedureInvoker` instance to CafeJava to trigger an invocation:

``` java
Observable<WLResponse> invocation = CafeJava.invokeProcedure(invoker);
invocation.subscribe(); // invocation is performed per subscriber
```

If we have a JavaScript based adapter, we can use a `JSProcedureInvoker` object instead.

For any `WLResponse` containing a valid JSON payload, CafeJava can serialize it automatically for us:

``` java
Observable<Person> person = invocation.compose(CafeJava.serializeTo(Person.class));
```

If we're expecting back an array of objects, we can alternatively supply a `TypeToken`:

``` java
TypeToken<List<Person>> token = new TypeToken<List<Person>>() {};
Observable<List<Person>> people = invocation.compose(CafeJava.serializeTo(token));
```

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

## Download

Download [the latest AAR](https://bintray.com/artifact/download/milbuild/maven/com/ibm/mil/cafejava/1.0.0/cafejava-1.0.0.aar) or grab via Gradle:

``` groovy
compile 'com.ibm.mil:cafejava:1.0.0'
```

or Maven:

``` xml
<dependency>
  <groupId>com.ibm.mil</groupId>
  <artifactId>cafejava</artifactId>
  <version>1.0.0</version>
</dependency>
```

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

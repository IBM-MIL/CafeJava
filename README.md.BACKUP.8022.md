# CafeJava

<<<<<<< HEAD
Reactive API for invoking [MobileFirst Platform](http://www-03.ibm.com/software/products/en/mobilefirstplatform) (MFP) procedures from an Android client.
=======
Reactive API for invoking MobileFirst Platform (MFP) procedures from an Android client. Support for auto-serialization of JSON responses is also provided in a reactive manner.
>>>>>>> master

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

<<<<<<< HEAD
For any `WLResponse` containing a valid JSON payload, CafeJava can serialize it automatically for us:
=======
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
>>>>>>> master

``` java
Observable<Person> person = invocation.compose(CafeJava.serializeTo(Person.class));
```

If we're expecting back an array of objects, we can alternatively supply a `TypeToken`:

``` java
TypeToken<List<Person>> token = new TypeToken<List<Person>>() {};
Observable<List<Person>> people = invocation.compose(CafeJava.serializeTo(token));
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

<<<<<<< HEAD
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
=======
CafeJava requires a **minimum SDK version of 9** or above.
>>>>>>> master

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

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

## Installation
In your app's `build.gradle` file:

``` gradle
compile 'com.ibm.mil:cafejava:2.0.0'
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

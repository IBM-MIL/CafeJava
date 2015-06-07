/*
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 */

package com.ibm.mil.cafejava;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.worklight.wlclient.api.WLClient;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLProcedureInvocationData;
import com.worklight.wlclient.api.WLRequestOptions;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

import static rx.Observable.Transformer;

/**
 * @author John Petitto  (github @jpetitto)
 * @author Tanner Preiss (github @t-preiss)
 */
public final class CafeJava {
    private int timeout = 30_000;
    private Object invocationContext;

    /**
     * setTimeout() sets the timeout for all MFP procedure and connection calls made using
     * this API.
     *
     * @param timeout The current timeout, in milliseconds, to wait for a procedure or connection to respond.
     * @return CafeJava returns the instance of CafaJava class, which is useful for chaining calls.
     */
    public CafeJava setTimeout(int timeout) {
        if (timeout >= 0) {
            this.timeout = timeout;
        }
        return this;
    }

    /**
     * getTimeout() returns the current timeout used when waiting for a procedure or connection to respond.
     * If no value has been set using setTimeout() then getTimeout() will return
     * the default timeout of 30_000 (milliseconds).
     *
     * @return timeout The current timeout, in milliseconds, to wait for a procedure or connection to respond.
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * setInvocationContext() User can add to the request any object, that will be available on the callback (success/failure) functions.
     *
     * @param invocationContext An object that is returned with WLResponse to the listener methods onSuccess and onFailure.
     *                          You can use this object to identify and distinguish different invokeProcedure calls.
     *                          This object is returned as is to the listener methods.
     * @return CafeJava returns the instance of CafaJava class, which is useful for chaining calls.
     */
    @NonNull
    public CafeJava setInvocationContext(@Nullable Object invocationContext) {
        this.invocationContext = invocationContext;
        return this;
    }

    /**
     * getInvocationContext() return the user invocation context
     *
     * @return An object that is returned with WLResponse to the listener methods onSuccess and onFailure.
     * You can use this object to identify and distinguish different invokeProcedure calls.
     * This object is returned as is to the listener methods
     */
    @Nullable
    public Object getInvocationContext() {
        return invocationContext;
    }

    /**
     * connect() creates an Observable that emits responses for the connection call to the
     * MobileFirst Platform Server.
     *
     * @param context object that is returned with WLResponse to the listener methods onSuccess an
     *                onFailure.
     *                You can use this object to identify and distinguish different invokeProcedure calls.
     *                This object is returned as is to the listener methods.
     * @return an Observable that will emit a WLResponse for the connection.
     */
    @NonNull
    public Observable<WLResponse> connect(@NonNull final Context context) {
        return Observable.create(new Observable.OnSubscribe<WLResponse>() {
            @Override
            public void call(Subscriber<? super WLResponse> subscriber) {
                WLClient client = WLClient.createInstance(context);
                client.connect(new RxResponseListener(subscriber), getRequestOptions());
            }
        });
    }

    /**
     * invokeProcedure() creates an Observable that emits responses for the given procedure call.
     *
     * @param adapterName   the adapter name on Worklight's server.
     * @param procedureName the procedure name on Worklight's server.
     * @param parameters    the Worklight method request parameters. The order of the object in the
     *                      array will be the order sending them to the adapter.
     * @return an Observable that will emit a WLResponse for the connection.
     */
    @NonNull
    public Observable<WLResponse> invokeProcedure(@NonNull final String adapterName,
                                                  @NonNull final String procedureName,
                                                  @Nullable final Object... parameters) {

        return Observable.create(new Observable.OnSubscribe<WLResponse>() {
            @Override
            public void call(Subscriber<? super WLResponse> subscriber) {
                WLClient client = WLClient.getInstance();
                if (client == null) {
                    subscriber.onError(new Throwable("WLClient instance does not exist"));
                    return;
                }

                WLProcedureInvocationData invocationData = new WLProcedureInvocationData(adapterName, procedureName, false);
                invocationData.setParameters(parameters);

                client.invokeProcedure(invocationData, new RxResponseListener(subscriber), getRequestOptions());
            }
        });
    }

    /**
     * serializeTo() uses a Transformer to transform a WLResponse into the given class.
     *
     * @param clazz       The class for which the WLResponse Json will be serialized to.
     * @param memberNames the names of the fields in the JSON returned in the WLResponse.
     *                    If more than one memberName is specified this method serialize the
     *                    final memberName specified and serialize this name to the provided
     *                    class type.
     *                    ex:
     *                    if the JSON object returned is as follows and the user wishes to
     *                    serialize field3 to a Person then the method would be:
     *                    <p/>
     *                    new CafeJava.serializeTo(Person.class, "field1", "field2", "field3")
     *                    <p/>
     *                    {
     *                    field1 : {
     *                    field2: {
     *                    field3: {
     *                    name: "FirstName"
     *                    age: 22
     *                    }
     *                    }
     *                    }
     *                    }
     * @param <T>
     * @return
     */
    @NonNull
    public static <T> Transformer<WLResponse, T> serializeTo(@NonNull final Class<T> clazz,
                                                             @NonNull final String... memberNames) {
        return transformJson(new Func1<WLResponse, T>() {
            @Override
            public T call(WLResponse wlResponse) {
                JsonElement element = parseNestedJson(wlResponse, memberNames);
                return new Gson().fromJson(element, clazz);
            }
        });
    }

    @NonNull
    public static <T> Transformer<WLResponse, T> serializeTo(@NonNull final TypeToken<T> typeToken,
                                                             @NonNull final String... memberNames) {
        return transformJson(new Func1<WLResponse, T>() {
            @Override
            public T call(WLResponse wlResponse) {
                JsonElement element = parseNestedJson(wlResponse, memberNames);
                return new Gson().fromJson(element, typeToken.getType());
            }
        });
    }

    private static <T> Transformer<WLResponse, T> transformJson(final Func1<WLResponse, T> func) {
        return new Transformer<WLResponse, T>() {
            @Override
            public Observable<T> call(Observable<WLResponse> wlResponseObservable) {
                return wlResponseObservable.map(func);
            }
        };
    }

    private static JsonElement parseNestedJson(WLResponse wlResponse, String... memberNames) {
        String json = wlResponse.getResponseJSON().toString();
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();

        for (int i = 0, size = memberNames.length; i < size; i++) {
            String member = memberNames[i];

            if (i == size - 1) {
                return jsonObject.get(member);
            } else {
                jsonObject = jsonObject.getAsJsonObject(member);
            }
        }

        return jsonObject;
    }

    private WLRequestOptions getRequestOptions() {
        WLRequestOptions requestOptions = new WLRequestOptions();
        requestOptions.setTimeout(timeout);
        requestOptions.setInvocationContext(invocationContext);
        return requestOptions;
    }

    private static class RxResponseListener implements WLResponseListener {
        private Subscriber<? super WLResponse> subscriber;

        RxResponseListener(Subscriber<? super WLResponse> subscriber) {
            this.subscriber = subscriber;
        }

        @Override
        public void onSuccess(WLResponse wlResponse) {
            subscriber.onNext(wlResponse);
            subscriber.onCompleted();
        }

        @Override
        public void onFailure(WLFailResponse wlFailResponse) {
            subscriber.onError(new Throwable(wlFailResponse.getErrorMsg()));
        }
    }

}

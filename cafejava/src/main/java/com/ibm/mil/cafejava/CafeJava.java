/*
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 */

package com.ibm.mil.cafejava;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.worklight.wlclient.api.WLClient;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLProcedureInvocationData;
import com.worklight.wlclient.api.WLRequestOptions;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;

import java.lang.reflect.Type;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * @author John Petitto  (github @jpetitto)
 * @author Tanner Preiss (github @t-preiss)
 */
public final class CafeJava {
    private int timeout = 30_000;
    private Object invocationContext;

    /**
     * setTimeout() sets the timeout for all MFP procedure and connection calls made using
     *      this API.
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
     *      If no value has been set using setTimeout() then getTimeout() will return
     *      the default timeout of 30_000 (milliseconds).
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
     *      You can use this object to identify and distinguish different invokeProcedure calls.
     *      This object is returned as is to the listener methods.
     * @return
     */
    public CafeJava setInvocationContext(Object invocationContext) {
        this.invocationContext = invocationContext;
        return this;
    }

    /**
     * getInvocationContext() return the user invocation context
     *
     * @return An object that is returned with WLResponse to the listener methods onSuccess and onFailure.
     *      You can use this object to identify and distinguish different invokeProcedure calls.
     *      This object is returned as is to the listener methods
     */
    public Object getInvocationContext(Object invocationContext) {
        return this.invocationContext;
    }

    public Observable<WLResponse> connect(final Context context) {
        return Observable.create(new Observable.OnSubscribe<WLResponse>() {
            @Override public void call(Subscriber<? super WLResponse> subscriber) {
                WLClient client = WLClient.createInstance(context);
                client.connect(new RxResponseListener(subscriber), getRequestOptions());
            }
        });
    }

    public Observable<WLResponse> invokeProcedure(final String adapterName,
                                                  final String procedureName,
                                                  final Object... parameters) {

        return Observable.create(new Observable.OnSubscribe<WLResponse>() {
            @Override public void call(Subscriber<? super WLResponse> subscriber) {
                WLClient client = WLClient.getInstance();
                if (client == null) {
                    subscriber.onError(new Throwable("WLClient instance does not exist"));
                    return;
                }

                WLProcedureInvocationData invocationData =
                        new WLProcedureInvocationData(adapterName, procedureName, false);
                invocationData.setParameters(parameters);

                client.invokeProcedure(invocationData, new RxResponseListener(subscriber),
                        getRequestOptions());
            }
        });
    }

    public static <T> Observable.Transformer<WLResponse, T> serializeTo(final Class<T> clazz,
                                                                        final String... memberNames) {
        return transformJson(new Func1<WLResponse, T>() {
            @Override public T call(WLResponse wlResponse) {
                JsonElement element = parseNestedJson(wlResponse, memberNames);
                return new Gson().fromJson(element, clazz);
            }
        });
    }

    public static <T> Observable.Transformer<WLResponse, T> serializeTo(final Type type,
                                                                        final String... memberNames) {
        return transformJson(new Func1<WLResponse, T>() {
            @Override public T call(WLResponse wlResponse) {
                JsonElement element = parseNestedJson(wlResponse, memberNames);
                return new Gson().fromJson(element, type);
            }
        });
    }

    private static <T> Observable.Transformer<WLResponse, T> transformJson(final Func1<WLResponse, T> func) {
        return new Observable.Transformer<WLResponse, T>() {
            @Override public Observable<T> call(Observable<WLResponse> wlResponseObservable) {
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
    };

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

        @Override public void onSuccess(WLResponse wlResponse) {
            subscriber.onNext(wlResponse);
            subscriber.onCompleted();
        }

        @Override public void onFailure(WLFailResponse wlFailResponse) {
            subscriber.onError(new Throwable(wlFailResponse.getErrorMsg()));
        }
    }

}

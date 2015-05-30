/*
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 */

package com.ibm.mil.cafejava;

import android.content.Context;
import android.util.Log;

import com.worklight.wlclient.api.WLClient;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLProcedureInvocationData;
import com.worklight.wlclient.api.WLRequestOptions;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;

import rx.Observable;
import rx.Subscriber;

public final class CafeJava {
    private static final String TAG = CafeJava.class.getName();
    private static final int DEFAULT_TIMEOUT = 30_000;

    private Object[] parameters = new Object[] {}; // TODO: make varargs to createProduceObservable()
    private int timeout = DEFAULT_TIMEOUT;
    private Object invocationContext;

    // TODO: deal with raw WL response and generics
    public <T> Observable<T> createProcedureObservable(final String adapterName, final String procedureName) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override public void call(final Subscriber<? super T> subscriber) {
                Log.i(TAG, "createProduceObservable called");

                WLClient client = WLClient.getInstance();
                if (client == null) {
                    subscriber.onError(new Throwable("WLClient instance does not exist"));
                    return;
                }

                // TODO: encode params and deal with compression argument
                WLProcedureInvocationData invocationData =
                        new WLProcedureInvocationData(adapterName, procedureName, false);
                invocationData.setParameters(parameters);

                client.invokeProcedure(invocationData, new WLResponseListener() {
                    @Override public void onSuccess(WLResponse wlResponse) {
                        subscriber.onNext(null);
                    }

                    @Override public void onFailure(WLFailResponse wlFailResponse) {
                        subscriber.onError(new Throwable(wlFailResponse.getErrorMsg()));
                    }
                }, getRequestOptions());
            }
        });
    }

    public <T> Observable<T> createConnectionObservable(final Context context) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override public void call(final Subscriber<? super T> subscriber) {
                Log.i(TAG, "createConnectionObservable called");

                WLClient client = WLClient.createInstance(context);
                client.connect(new WLResponseListener() {
                    @Override public void onSuccess(WLResponse wlResponse) {
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }

                    @Override public void onFailure(WLFailResponse wlFailResponse) {
                        subscriber.onError(new Throwable(wlFailResponse.getErrorMsg()));
                    }
                }, getRequestOptions());
            }
        });
    }

    public CafeJava parameters(Object... parameters) {
        this.parameters = parameters;
        return this;
    }

    public CafeJava timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public CafeJava invocationContext(Object invocationContext) {
        this.invocationContext = invocationContext;
        return this;
    }

    private WLRequestOptions getRequestOptions() {
        WLRequestOptions requestOptions = new WLRequestOptions();
        requestOptions.setTimeout(timeout);
        requestOptions.setInvocationContext(invocationContext);
        return requestOptions;
    }

}

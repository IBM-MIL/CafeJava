/*
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 */

package com.ibm.mil.cafejava;

import android.util.Log;

import rx.Observable;
import rx.Subscriber;

public final class CafeJava {
    private static final String TAG = CafeJava.class.getName();
    private static final int DEFAULT_TIMEOUT = 30_000;

    private Object[] parameters = new Object[] {};
    private int timeout = DEFAULT_TIMEOUT;
    private Object invocationContext;

    public <T> Observable<T> createProcedureObservable(String adapterName, String procedureName) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override public void call(Subscriber<? super T> subscriber) {
                Log.i(TAG, "createProduceObservable called");
            }
        });
    }

    public <T> Observable<T> createConnectionObservable() {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override public void call(Subscriber<? super T> subscriber) {
                Log.i(TAG, "createConnectionObservable called");
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

}

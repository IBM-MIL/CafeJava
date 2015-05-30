/*
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 */

package com.ibm.mil.cafejava;

import rx.Observable;
import rx.Subscriber;

public final class ProcedureCaller {
    private final static int DEFAULT_TIMEOUT = 30_000;

    private Object[] parameters = new Object[] {};
    private int timeout = DEFAULT_TIMEOUT;
    private Object invocationContext;

    public <T> Observable<T> createObservable(String adapterName, String procedureName) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override public void call(Subscriber<? super T> subscriber) {

            }
        });
    }

    public ProcedureCaller setParameters(Object... parameters) {
        this.parameters = parameters;
        return this;
    }

    public ProcedureCaller setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public ProcedureCaller setInvocationContext(Object invocationContext) {
        this.invocationContext = invocationContext;
        return this;
    }

}

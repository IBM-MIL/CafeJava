/*
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 */

package com.ibm.mil.cafejava;

import rx.Observable;
import rx.Subscriber;

public final class ProcedureCaller<T> extends Observable<T> {
    private final String adapterName;
    private final String procedureName;
    private final Object[] parameters;
    private final int timeout;
    private final Object invocationContext;

    public final class Builder {
        private final static int DEFAULT_TIMEOUT = 30_000;

        private final String adapterName;
        private final String procedureName;

        private Object[] parameters = new Object[] {};
        private int timeout = DEFAULT_TIMEOUT;
        private Object invocationContext;

        public Builder(String adapterName, String procedureName) {
            this.adapterName = adapterName;
            this.procedureName = procedureName;
        }

        public Builder setParameters(Object... parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder setTimeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public Builder setInvocationContext(Object invocationContext) {
            this.invocationContext = invocationContext;
            return this;
        }

        public Observable<T> build() {
            return ProcedureCaller.create(new OnSubscribe<T>() {
                @Override public void call(Subscriber<? super T> subscriber) {

                }
            });
            // return new ProcedureObservable(this);
        }
    }

    private ProcedureCaller(Builder builder) {
        adapterName = builder.adapterName;
        procedureName = builder.procedureName;
        parameters = builder.parameters;
        timeout = builder.timeout;
        invocationContext = builder.invocationContext;
    }

}

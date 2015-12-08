/*
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 */

package com.ibm.mil.cafejava;

import android.content.Context;
import android.support.annotation.NonNull;

import com.worklight.wlclient.api.WLClient;
import com.worklight.wlclient.api.WLFailResponse;
import com.worklight.wlclient.api.WLResponse;
import com.worklight.wlclient.api.WLResponseListener;

import rx.Observable;
import rx.Subscriber;

/**
 * Configurable MFP client for establishing connections and invoking procedures in a reactive
 * manner. For a detailed guide on using this class, visit
 * <a href="https://github.com/IBM-MIL/CafeJava" target="_blank">the project's GitHub page</a> and
 * view the README.
 *
 * @author John Petitto  (github @jpetitto)
 * @author Tanner Preiss (github @t-preiss)
 */
public final class CafeJava {

    private CafeJava() {
        throw new AssertionError(CafeJava.class.getName() + " is non-instantiable");
    }

    /**
     * Creates an {@code Observable} that emits a {@code WLResponse} after attempting connection
     * to the MFP server instance defined in the {@code wlclient.properties} file. This
     * connection is only performed when there is a new {@code Subscriber} to the {@code
     * Observable}. The {@code Observable} will automatically perform its work on a dedicated
     * background thread, so there is usually no need to use the {@code subscribeOn} method of
     * RxJava.
     *
     * @param context
     * @return {@code Observable} that emits a {@code WLResponse} for an MFP connection.
     */
    @NonNull
    public static Observable<WLResponse> connect(@NonNull final Context context) {
        return Observable.create(new Observable.OnSubscribe<WLResponse>() {
            @Override
            public void call(Subscriber<? super WLResponse> subscriber) {
                WLClient client = WLClient.createInstance(context);
                client.connect(new RxResponseListener(subscriber));
            }
        });
    }

    /**
     * Creates an {@code Observable} that emits a {@code WLResponse} after attempting to invoke
     * the {@code ProcedureInvoker} parameter that is passed in. This invocation is only
     * performed when there is a new {@code Subscriber} to the {@code Observable}. The {@code
     * Observable} will automatically perform its work on a dedicated background thread, so there
     * is usually no need to use the {@code subscribeOn} method of RxJava.
     *
     * @param invoker Implementation for a procedure invocation, most commonly
     *                {@link JavaProcedureInvoker} or {@link JSProcedureInvoker}.
     * @return {@code Observable} that emits a {@code WLResponse} for an MFP procedure invocation.
     */
    @NonNull
    public static Observable<WLResponse> invokeProcedure(@NonNull final ProcedureInvoker invoker) {
        return Observable.create(new Observable.OnSubscribe<WLResponse>() {
            @Override public void call(Subscriber<? super WLResponse> subscriber) {
                invoker.invoke(new RxResponseListener(subscriber));
            }
        });
    }

    private static class RxResponseListener implements WLResponseListener {
        private final Subscriber<? super WLResponse> subscriber;

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
            String failureMessage = String.format("Error Code: %s\nError Message: %s",
                    wlFailResponse.getErrorCode(), wlFailResponse.getErrorMsg());
            subscriber.onError(new Throwable(failureMessage));
        }
    }
}

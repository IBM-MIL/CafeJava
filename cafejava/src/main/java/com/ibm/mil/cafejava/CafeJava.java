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
 * Configurable MFP client for establishing connections and invoking procedures in a reactive
 * manner. For a detailed guide on using this class, visit
 * <a href="https://github.com/t-preiss/CafeJava" target="_blank">the project's GitHub page</a> and
 * view the README.
 *
 * @author John Petitto  (github @jpetitto)
 * @author Tanner Preiss (github @t-preiss)
 */
public final class CafeJava {
    private int timeout = 30_000;
    private Object invocationContext;

    /**
     * The timeout that will be used for any MFP call invoked from this instance of {@code
     * CafeJava}.
     *
     * @param timeout Number of millis to wait for an MFP call to respond.
     * @return The current instance of {@code CafeJava} to allow for easy call chaining.
     */
    public CafeJava setTimeout(int timeout) {
        if (timeout >= 0) {
            this.timeout = timeout;
        }
        return this;
    }

    /**
     * @return Number of millis to wait for an MFP call to respond.
     */
    public int getTimeout() {
        return timeout;
    }

    /**
     * An {@code invocationContext} serves as a mechanism for tagging a {@code WLResponse}. This
     * can be useful when the source of a {@code WLResponse} is unknown. This context will be used
     * for any MFP call invoked from this instance of {@code CafeJava}.
     *
     * @param invocationContext Returned as part of any {@code WLResponse} that was originally
     *                          invoked from this instance of {@code CafeJava}.
     * @return The current instance of {@code CafeJava} to allow for easy call chaining.
     */
    @NonNull
    public CafeJava setInvocationContext(@Nullable Object invocationContext) {
        this.invocationContext = invocationContext;
        return this;
    }

    /**
     * @return The context (object) for any {@code WLResponse} that was originally invoked from
     * this instance of {@code CafeJava}.
     */
    @Nullable
    public Object getInvocationContext() {
        return invocationContext;
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
     * Creates an {@code Observable} that emits a {@code WLResponse} after attempting invocation
     * of the specified procedure for the given adapter. This invocation is only performed when
     * there is a new {@code Subscriber} to the {@code Observable}. The {@code Observable} will
     * automatically perform its work on a dedicated background thread, so there is usually no
     * need to use the {@code subscribeOn} method of RxJava.
     *
     * @param adapterName   Name of the targeted adapter.
     * @param procedureName Name of the targeted procedure for the specified adapter.
     * @param parameters    Variable number of parameters that the specified procedure is
     *                      expecting. The types of each parameter need to match the type that
     *                      the procedure is expecting on the server.
     * @return {@code Observable} that emits a {@code WLResponse} for an MFP procedure invocation.
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

                WLProcedureInvocationData invocationData =
                        new WLProcedureInvocationData(adapterName, procedureName, false);
                invocationData.setParameters(parameters);

                client.invokeProcedure(invocationData, new RxResponseListener(subscriber),
                        getRequestOptions());
            }
        });
    }

    public Observable<WLResponse> invokeProcedure(final ProcedureInvoker invoker) {
        return Observable.create(new Observable.OnSubscribe<WLResponse>() {
            @Override public void call(Subscriber<? super WLResponse> subscriber) {
                invoker.invoke(new RxResponseListener(subscriber));
            }
        });
    }

    /**
     * Transforms an {@code Observable} that emits a {@code WLResponse} with a valid JSON payload
     * into a new {@code Observable} with the targeted {@code Class} type. This can be done by
     * passing the result of this method to the {@code compose} operator of RxJava. A variable
     * number of member names can be provided for accessing JSON data that is nested arbitrarily
     * deep inside the response payload.
     *
     * @param clazz       Targeted {@code Class} type for the JSON payload to be serialized into.
     * @param memberNames Variable number of member names for accessing JSON data that is nested
     *                    arbitrarily deep inside the response payload.
     * @return {@code Transformer} that can be supplied to the {@code compose} operator of RxJava
     * . The input {@code Observable} must emit a {@code WLResponse} with a valid JSON payload.
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

    /**
     * Transforms an {@code Observable} that emits a {@code WLResponse} with a valid JSON payload
     * into a new Observable for the targeted {@code TypeToken}. This can be done by passing the
     * result of this method to the {@code compose} operator of RxJava. A {@code TypeToken} is
     * necessary when the targeted type is a parameterized type, such as {@code List}. A variable
     * number of member names can be provided for accessing JSON data that is nested arbitrarily
     * deep inside the response payload.
     *
     * @param typeToken   Captures the necessary type information for the targeted parameterized
     *                    type, such as {@code List}.
     * @param memberNames Variable number of member names for accessing JSON data that is nested
     *                    arbitrarily deep inside the response payload.
     * @return {@code Transformer} that can be supplied to the {@code compose} operator of RxJava
     * . The input {@code Observable} must emit a {@code WLResponse} with a valid JSON payload.
     */
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

        // For each member name, fetch the object it maps to until you reach the final member name.
        // Once the final member name is reached, return its corresponding value.
        for (int i = 0, size = memberNames.length; i < size; i++) {
            String member = memberNames[i];

            if (i == size - 1) {
                // last member name reached; return its value
                return jsonObject.get(member);
            } else {
                // more member names remain, therefore current member must map to an object
                jsonObject = jsonObject.getAsJsonObject(member);
            }
        }

        // no nesting required; return top-level object
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

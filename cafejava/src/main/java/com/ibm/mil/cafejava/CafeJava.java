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

public final class CafeJava {
    private static final int DEFAULT_TIMEOUT = 30_000;

    private int timeout = DEFAULT_TIMEOUT;
    private Object invocationContext;

    public CafeJava timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public CafeJava invocationContext(Object invocationContext) {
        this.invocationContext = invocationContext;
        return this;
    }

    public Observable<WLResponse> createConnectionObservable(final Context context) {
        return Observable.create(new Observable.OnSubscribe<WLResponse>() {
            @Override public void call(Subscriber<? super WLResponse> subscriber) {
                WLClient client = WLClient.createInstance(context);
                client.connect(new RxResponseListener(subscriber), getRequestOptions());
            }
        });
    }

    public Observable<WLResponse> createProcedureObservable(final String adapterName,
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

    public static <T> Observable.Transformer<WLResponse, T> serializeTo(final Class<T> clazz) {
        return new Observable.Transformer<WLResponse, T>() {
            @Override public Observable<T> call(Observable<WLResponse> wlResponseObservable) {
                return wlResponseObservable.map(new Func1<WLResponse, T>() {
                    @Override public T call(WLResponse wlResponse) {
                        return new Gson().fromJson(wlResponse.getResponseJSON().toString(), clazz);
                    }
                });
            }
        };
    }

    public static <T> Observable.Transformer<WLResponse, T> serializeTo(final Class<T> clazz,
                                                                        final String memberName) {
        return new Observable.Transformer<WLResponse, T>() {
            @Override public Observable<T> call(Observable<WLResponse> wlResponseObservable) {
                return wlResponseObservable.map(new Func1<WLResponse, T>() {
                    @Override public T call(WLResponse wlResponse) {
                        JsonElement value = parseJsonResponse(wlResponse, memberName);
                        return new Gson().fromJson(value, clazz);
                    }
                });
            }
        };
    }

    public static <T> Observable.Transformer<WLResponse, T> serializeTo(final Class<T> clazz,
                                                                        final JsonConfigurator config) {
        return new Observable.Transformer<WLResponse, T>() {
            @Override public Observable<T> call(Observable<WLResponse> wlResponseObservable) {
                return wlResponseObservable.map(new Func1<WLResponse, T>() {
                    @Override public T call(WLResponse wlResponse) {
                        String configuredJson = applyJsonConfigurator(wlResponse, config);;
                        return new Gson().fromJson(configuredJson, clazz);
                    }
                });
            }
        };
    }

    public static <T> Observable.Transformer<WLResponse, T> serializeTo(final Type type,
                                                                        final String memberName) {
        return new Observable.Transformer<WLResponse, T>() {
            @Override public Observable<T> call(Observable<WLResponse> wlResponseObservable) {
                return wlResponseObservable.map(new Func1<WLResponse, T>() {
                    @Override public T call(WLResponse wlResponse) {
                        JsonElement value = parseJsonResponse(wlResponse, memberName);
                        return new Gson().fromJson(value, type);
                    }
                });
            }
        };
    }

    public static <T> Observable.Transformer<WLResponse, T> serializeTo(final Type type,
                                                                        final JsonConfigurator config) {
        return new Observable.Transformer<WLResponse, T>() {
            @Override public Observable<T> call(Observable<WLResponse> wlResponseObservable) {
                return wlResponseObservable.map(new Func1<WLResponse, T>() {
                    @Override public T call(WLResponse wlResponse) {
                        String configuredJson = applyJsonConfigurator(wlResponse, config);
                        return new Gson().fromJson(configuredJson, type);
                    }
                });
            }
        };
    }

    private WLRequestOptions getRequestOptions() {
        WLRequestOptions requestOptions = new WLRequestOptions();
        requestOptions.setTimeout(timeout);
        requestOptions.setInvocationContext(invocationContext);
        return requestOptions;
    }

    private static JsonElement parseJsonResponse(WLResponse wlResponse, String memberName) {
        String json = wlResponse.getResponseJSON().toString();
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        return jsonObject.get(memberName);
    }

    private static String applyJsonConfigurator(WLResponse wlResponse, JsonConfigurator config) {
        String json = wlResponse.getResponseJSON().toString();
        return config.configure(json);
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

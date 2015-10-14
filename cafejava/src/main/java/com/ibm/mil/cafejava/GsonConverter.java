package com.ibm.mil.cafejava;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.worklight.wlclient.api.WLResponse;

import java.lang.reflect.Type;

import rx.Observable;
import rx.Subscriber;

public class GsonConverter<T> implements Observable.Operator<T, WLResponse> {
    @Override public Subscriber<? super WLResponse> call(final Subscriber<? super T> subscriber) {
        return new Subscriber<WLResponse>() {
            @Override public void onCompleted() {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }

            @Override public void onError(Throwable e) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onError(e);
                }
            }

            @Override public void onNext(WLResponse wlResponse) {
                if (!subscriber.isUnsubscribed()) {
                    String json = wlResponse.getResponseJSON().toString();
                    Type type = new TypeToken<T>() {}.getType();
                    T converted = new Gson().fromJson(json, type);

                    if (converted == null) {
                        Throwable e = new Throwable("Could not convert JSON payload: " + json);
                        subscriber.onError(e);
                    } else {
                        subscriber.onNext(converted);
                    }
                }
            }
        };
    }
}

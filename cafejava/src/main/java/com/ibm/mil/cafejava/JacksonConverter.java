package com.ibm.mil.cafejava;

import com.worklight.wlclient.api.WLResponse;

import java.io.IOException;

import rx.Observable;
import rx.Subscriber;

public abstract class JacksonConverter<T> implements Observable.Operator<T, WLResponse> {
    abstract T convert(String json) throws IOException;

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
                    T converted = null;

                    try {
                        converted = convert(json);
                    } catch (IOException e) {
                        subscriber.onError(e); // this should never happen
                    }

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

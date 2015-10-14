package com.ibm.mil.cafejava;

import com.worklight.wlclient.api.WLResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;

import rx.Observable;
import rx.Subscriber;

public class JacksonConverter<T> implements Observable.Operator<T, WLResponse> {
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
                    TypeReference<T> type = new TypeReference<T>() {};
                    T converted = null;

                    try {
                        converted = new ObjectMapper().readValue(json, type);
                    } catch (IOException e) {
                        subscriber.onError(e); // this should never happen
                    }

                    if (converted == null) {
                        Throwable e = new Throwable("Could not convert JSON payload: " + json);
                    } else {
                        subscriber.onNext(converted);
                    }
                }
            }
        };
    }
}

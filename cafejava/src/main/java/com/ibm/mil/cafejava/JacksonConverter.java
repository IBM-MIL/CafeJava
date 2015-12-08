package com.ibm.mil.cafejava;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.worklight.wlclient.api.WLResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;

import rx.Observable;
import rx.Subscriber;

public class JacksonConverter<T> implements Observable.Operator<T, WLResponse> {
    private Class<T> clazz;
    private TypeReference<T> reference;

    public JacksonConverter(@NonNull Class<T> clazz) {
        this.clazz = clazz;
    }

    public JacksonConverter(@NonNull TypeReference<T> reference) {
        this.reference = reference;
    }

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
                    T converted = convert(json);

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

    @Nullable private T convert(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            if (clazz != null) {
                return mapper.readValue(json, clazz);
            } else {
                return mapper.readValue(json, reference);
            }
        } catch (IOException e) {
            e.printStackTrace(); // this should never happen
            return null;
        }
    }
}

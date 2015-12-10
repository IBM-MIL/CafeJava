package com.ibm.mil.cafejava;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.worklight.wlclient.api.WLResponse;
import java.io.IOException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import rx.Observable;
import rx.Subscriber;

/**
 * A custom RxJava operator for converting a valid JSON payload from a WLResponse into the
 * corresponding Java class representation. To apply the operator to an existing
 * {@code Observable}, pass an instance of this class to the {@code lift} operator.
 *
 * For converting objects, use the {@code Class<T>} constructor. For arrays, use the
 * {@code TypeReference<T>} constructor.
 *
 * @param <T> Type of object that the response will be converted into
 *
 * @author John Petitto  (github @jpetitto)
 * @author Tanner Preiss (github @t-preiss)
 */
public class JsonConverter<T> implements Observable.Operator<T, WLResponse> {
  private Class<T> clazz;
  private TypeReference<T> reference;

  /**
   * Allows for conversion from JSON objects.
   *
   * @param clazz The targeted class for conversion
   */
  public JsonConverter(@NonNull Class<T> clazz) {
    this.clazz = clazz;
  }

  /**
   * Allows for conversion from JSON arrays. {@code TypeReference<T>} must be used for converting
   * to a type of {@code List<T>}. To do this, you would call:
   * {@code new TypeReference<List<T>>() {}}.
   *
   * @param reference The targeted {@code List<T>} for conversion
   */
  public JsonConverter(@NonNull TypeReference<T> reference) {
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

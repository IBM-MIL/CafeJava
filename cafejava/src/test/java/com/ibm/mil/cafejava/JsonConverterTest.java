package com.ibm.mil.cafejava;

import com.worklight.wlclient.api.WLResponse;
import java.util.Arrays;
import java.util.List;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONObject;
import org.junit.Test;
import rx.Observable;
import rx.functions.Func1;
import rx.observers.TestSubscriber;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JsonConverterTest {
  private Observable<WLResponse> observable;
  private TestSubscriber<Person> subscriber;

  @Test public void testSimpleObject() {
    Person person = new Person("John", 25, true);

    createMocks(person);

    observable.lift(new JsonConverter<>(Person.class)).subscribe(subscriber);

    subscriber.assertNoErrors();
    subscriber.assertTerminalEvent();

    List<Person> persons = subscriber.getOnNextEvents();
    assertEquals(1, persons.size());
    assertEquals(person, persons.get(0));
  }

  @Test public void testSimpleArray() {
    Person firstPerson = new Person("John", 25, true);
    Person secondPerson = new Person("Megan", 21, false);
    List<Person> persons = Arrays.asList(firstPerson, secondPerson);

    createMocks(persons);

    observable.lift(new JsonConverter<>(new TypeReference<List<Person>>() {
    })).flatMap(new Func1<List<Person>, Observable<Person>>() {
      @Override public Observable<Person> call(List<Person> persons) {
        return Observable.from(persons);
      }
    }).subscribe(subscriber);

    subscriber.assertValues(firstPerson, secondPerson);
  }

  @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
  @Test public void testBadObject() {
    createMocks("Not a Person");

    observable.lift(new JsonConverter<>(Person.class)).subscribe(subscriber);

    subscriber.assertNoValues();
    subscriber.assertTerminalEvent();

    List<Throwable> errors = subscriber.getOnErrorEvents();
    assertEquals(1, errors.size());

    Throwable t = errors.get(0);
    assertEquals("Could not convert JSON payload: Not a Person", t.getMessage());
  }

  @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
  @Test public void testBadArray() {
    Person person = new Person("John", 25, true);
    createMocks(person);

    observable.lift(new JsonConverter<>(new TypeReference<List<Person>>() {
    })).flatMap(new Func1<List<Person>, Observable<Person>>() {
      @Override public Observable<Person> call(List<Person> persons) {
        return Observable.from(persons);
      }
    }).subscribe(subscriber);

    subscriber.assertNoValues();
    subscriber.assertTerminalEvent();

    List<Throwable> errors = subscriber.getOnErrorEvents();
    assertEquals(1, errors.size());

    Throwable t = errors.get(0);
    assertEquals("Could not convert JSON payload: " + person.toString(), t.getMessage());
  }

  private void createMocks(Object object) {
    JSONObject jsonObject = mock(JSONObject.class);
    when(jsonObject.toString()).thenReturn(object.toString());

    WLResponse wlResponse = mock(WLResponse.class);
    when(wlResponse.getResponseJSON()).thenReturn(jsonObject);

    observable = Observable.just(wlResponse);
    subscriber = new TestSubscriber<>();
  }
}

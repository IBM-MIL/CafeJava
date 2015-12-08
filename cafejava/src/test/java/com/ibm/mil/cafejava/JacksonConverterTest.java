package com.ibm.mil.cafejava;

import com.worklight.wlclient.api.WLResponse;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JacksonConverterTest {
    private Observable<WLResponse> observable;

    @Before public void before() {
        Person person = new Person();
        person.setName("John");
        person.setAge(25);
        person.setDev(true);

        JSONObject jsonObject = mock(JSONObject.class);
        when(jsonObject.toString()).thenReturn(person.toString());

        WLResponse wlResponse = mock(WLResponse.class);
        when(wlResponse.getResponseJSON()).thenReturn(jsonObject);

        observable = Observable.just(wlResponse);
    }

    @Test public void testSimpleObject() {
        TestSubscriber<Person> subscriber = new TestSubscriber<>();
        observable.lift(new JacksonObjectConverter<>(Person.class)).subscribe(subscriber);

        subscriber.assertNoErrors();
        subscriber.assertTerminalEvent();

        List<Person> persons = subscriber.getOnNextEvents();
        assertEquals(1, persons.size());

        Person person = persons.get(0);
        assertEquals("John", person.getName());
        assertEquals(25, person.getAge());
        assertTrue(person.isDev());
    }
}

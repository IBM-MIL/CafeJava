package com.ibm.mil.cafejava;

import com.worklight.wlclient.api.WLResponse;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;

public class GsonConverterTest {
    private Observable<WLResponse> observable;

    @Before public void before() {
        Person person = new Person("John", 25, true);

        JSONObject jsonObject = Mockito.mock(JSONObject.class);
        Mockito.when(jsonObject.toString()).thenReturn(person.toString());

        WLResponse wlResponse = Mockito.mock(WLResponse.class);
        Mockito.when(wlResponse.getResponseJSON()).thenReturn(jsonObject);

        observable = Observable.just(wlResponse);
    }

    @Test public void testSimpleObject() {
        TestSubscriber<Person> subscriber = new TestSubscriber<>();
        observable.lift(new GsonConverter<Person>()).subscribe(subscriber);

        subscriber.assertNoErrors();
        subscriber.assertTerminalEvent();

        List<Person> persons = subscriber.getOnNextEvents();
        Assert.assertEquals(1, persons.size());

        Person person = persons.get(0);
        Assert.assertEquals("John", person.name);
        Assert.assertEquals(25, person.age);
        Assert.assertEquals(true, person.isDev);
    }
}

/*
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 */

package com.ibm.mil.cafejava;

import com.google.gson.Gson;
import com.worklight.wlclient.api.WLResponse;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;

public class JsonSerializationTest {
    private Observable<WLResponse> mockObservable;

    @Before
    public void setUp() {
        Person person = new Person("John", 25, true);

        JSONObject jsonObject = Mockito.mock(JSONObject.class);
        Mockito.when(jsonObject.toString()).thenReturn(new Gson().toJson(person));

        WLResponse wlResponse = Mockito.mock(WLResponse.class);
        Mockito.when(wlResponse.getResponseJSON()).thenReturn(jsonObject);

        mockObservable = Observable.just(wlResponse);
    }

    @Test
    public void testClassSerial() {
        TestSubscriber<Person> testSubscriber = new TestSubscriber<>();
        mockObservable
                .compose(CafeJava.serializeTo(Person.class))
                .subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        testSubscriber.assertTerminalEvent();

        List<Person> persons = testSubscriber.getOnNextEvents();
        Assert.assertFalse(persons.isEmpty());

        Person person = persons.get(0);
        Assert.assertEquals(person.name, "John");
        Assert.assertEquals(person.age, 25);
        Assert.assertTrue(person.isDeveloper);
    }

    static class Person {
        String name;
        int age;
        boolean isDeveloper;

        Person(String name, int age, boolean isDeveloper) {
            this.name = name;
            this.age = age;
            this.isDeveloper = isDeveloper;
        }
    }

}

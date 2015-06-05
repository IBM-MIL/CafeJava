/*
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 */

package com.ibm.mil.cafejava;

import com.google.gson.Gson;
import com.worklight.wlclient.api.WLResponse;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import rx.Observable;
import rx.functions.Action1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class JsonSerializationTest {
    static class MockResponse extends WLResponse {
        MockResponse(HttpResponse httpResponse) {
            super(httpResponse);
        }

        @Override public JSONObject getResponseJSON() {
            Person person = new Person();
            person.name = "John";
            person.age = 25;
            person.isDeveloper = true;

            JSONObject jsonObject = null;
            try {
                return new JSONObject(new Gson().toJson(person));
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                return jsonObject;
            }
        }
    }

    static class Person {
        String name;
        int age;
        boolean isDeveloper;
    }

    private Observable<MockResponse> mockObservable;

    @Before
    public void setUp() {
        mockObservable = Observable.just(new MockResponse(null));
    }

    @Test
    public void testClassSerial() {
        mockObservable
                .compose(CafeJava.serializeTo(Person.class))
                .subscribe(new Action1<Person>() {
                    @Override public void call(Person person) {
                        assertNotNull("Person is null after serialization", person);
                        assertEquals(person.name, "John");
                        assertEquals(person.age, 25);
                        assertEquals(person.isDeveloper, true);
                    }
                }, new Action1<Throwable>() {
                    @Override public void call(Throwable throwable) {
                        fail("Exception thrown: " + throwable.getMessage());
                    }
                });
    }

}

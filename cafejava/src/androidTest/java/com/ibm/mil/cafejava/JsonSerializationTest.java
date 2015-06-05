/*
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 */

package com.ibm.mil.cafejava;

import android.test.suitebuilder.TestSuiteBuilder;

import com.google.gson.Gson;
import com.worklight.wlclient.api.WLResponse;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.apache.http.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import rx.Observable;
import rx.functions.Action1;

public class JsonSerializationTest extends TestSuite {
    static class MockResponse extends WLResponse {
        MockResponse(HttpResponse httpResponse) {
            super(httpResponse);
        }

        @Override public JSONObject getResponseJSON() {
            Person person = new Person();
            person.name = "John";
            person.age = 25;
            person.isDeveloper = true;

            try {
                return new JSONObject(new Gson().toJson(person));
            } catch (JSONException e) {
                e.printStackTrace();
                return new JSONObject();
            }
        }
    }

    static class Person {
        String name;
        int age;
        boolean isDeveloper;
    }

    private Observable<MockResponse> mockObservable;

    public void setUp() {
        mockObservable = Observable.just(new MockResponse(null));
    }

    public void testClassSerial() {
        mockObservable
                .compose(CafeJava.serializeTo(Person.class))
                .subscribe(new Action1<Person>() {
                    @Override public void call(Person person) {
                        Assert.assertNotNull("Person is null after serialization", person);
                        Assert.assertEquals(person.name, "John");
                        Assert.assertEquals(person.age, 25);
                        Assert.assertEquals(person.isDeveloper, true);
                    }
                }, new Action1<Throwable>() {
                    @Override public void call(Throwable throwable) {
                        Assert.fail("Exception thrown: " + throwable.getMessage());
                    }
                });
    }

    public static Test suite() {
        return new TestSuiteBuilder(JsonSerializationTest.class)
                .includeAllPackagesUnderHere()
                .build();
    }

}

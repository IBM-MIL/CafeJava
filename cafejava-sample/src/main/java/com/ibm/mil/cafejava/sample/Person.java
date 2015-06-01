/*
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2015. All Rights Reserved.
 */

package com.ibm.mil.cafejava.sample;

import com.google.gson.Gson;

public class Person {
    private String name;
    private int age;
    private boolean isDeveloper;

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public boolean isDeveloper() {
        return isDeveloper;
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}

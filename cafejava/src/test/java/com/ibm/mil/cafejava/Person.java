package com.ibm.mil.cafejava;

import com.google.gson.Gson;

class Person {
    String name;
    int age;
    boolean isDev;

    Person(String name, int age, boolean isDev) {
        this.name = name;
        this.age = age;
        this.isDev = isDev;
    }

    @Override public String toString() {
        /*
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        */

        return new Gson().toJson(this);
    }
}

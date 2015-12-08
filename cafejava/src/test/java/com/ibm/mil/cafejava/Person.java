package com.ibm.mil.cafejava;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

class Person {
    private String name;
    private int age;
    private boolean dev;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isDev() {
        return dev;
    }

    public void setDev(boolean dev) {
        this.dev = dev;
    }

    @Override public String toString() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}

package com.ibm.mil.cafejava;

import java.io.IOException;
import org.codehaus.jackson.map.ObjectMapper;

public class Person {
  private String name;
  private int age;
  private boolean dev;

  public Person() {
    // required empty constructor for Jackson ObjectMapper
  }

  public Person(String name, int age, boolean dev) {
    this.name = name;
    this.age = age;
    this.dev = dev;
  }

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

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Person)) return false;

    Person that = (Person) o;

    if (name != null ? !name.equals(that.name) : that.name != null) return false;
    if (age != that.age) return false;
    if (dev != that.dev) return false;

    return true;
  }

  @Override public int hashCode() {
    int result = 1;
    result = 31 * result + (name != null ? name.hashCode() : 0);
    result = 31 * result + age;
    result = 31 * result + (dev ? 1 : 0);
    return result;
  }

  @Override public String toString() {
    try {
      return new ObjectMapper().writeValueAsString(this);
    } catch (IOException e) {
      e.printStackTrace();
      return "";
    }
  }
}

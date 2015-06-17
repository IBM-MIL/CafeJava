package com.ibm.mil.database;

import java.util.HashMap;

import com.ibm.mil.database.models.User;

public class SampleDatabase {
	private static SampleDatabase INSTANCE = new SampleDatabase();
	private HashMap<String, User> users;

	// initailzed during class loading

	// to prevent creating another instance of Singleton
	private SampleDatabase() {
		users = new HashMap<String, User>();
		this.createUser(new User.UserBuilder("bobcat").name("Robert Caterone")
				.age(51).isDeveloper(false).build());
		this.createUser(new User.UserBuilder("girl").name("Princess Peachy")
				.age(12).isDeveloper(true).build());
	}

	public static SampleDatabase getSingleton() {
		return INSTANCE;
	}

	public User getUser(String username) {
		return users.get(username);
	}

	public User updateUser(String username, User updatedUser) {
		assert (updatedUser != null);
		return users.put(updatedUser.getUsername(), updatedUser);
	}

	public User deleteUser(String username) {
		return users.remove(username);
	}

	public User createUser(User newUser) {
		return users.put(newUser.getUsername(), newUser);
	}
}

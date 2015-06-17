package com.ibm.mil.database.models;

public class User {
	private String username;
	private String name;
	private int age;
	private boolean isDeveloper;

	private User(UserBuilder builder) {
		this.username = builder.username;
		this.name = builder.name;
		this.age = builder.age;
		this.isDeveloper = builder.isDeveloper;
	}

	public String getUsername() {
		return username;
	}

	public String getName() {
		return name;
	}

	public int getAge() {
		return age;
	}


	public boolean isDeveloper() {
		return isDeveloper;
	}

	public static class UserBuilder {
		private String username;
		private String name;
		private int age;
		private boolean isDeveloper;

		public UserBuilder(String username) {
			this.username = username;
		}

		public UserBuilder name(String name) {
			this.name = name;
			return this;
		}

		public UserBuilder age(int age) {
			this.age = age;
			return this;
		}

		public UserBuilder isDeveloper(boolean isDeveloper) {
			this.isDeveloper = isDeveloper;
			return this;
		}

		public User build() {
			return new User(this);
		}

	}
}

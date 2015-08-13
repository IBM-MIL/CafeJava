package com.ibm.mil.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ibm.mil.database.models.President;

public class SampleDatabase {
	private static final String PRESIDENTS_JSON_FILENAME = "presidents.json";
	private static Gson gson = new Gson();
	private static SampleDatabase INSTANCE = new SampleDatabase();
	private HashMap<String, President> presidents;

	// initailzed during class loading

	// to prevent creating another instance of Singleton
	private SampleDatabase() {
		presidents = loadPresidentsFromFile(PRESIDENTS_JSON_FILENAME);
	}

	public static SampleDatabase getSingleton() {
		return INSTANCE;
	}

	public President getPresident(String username) {
		return presidents.get(username);
	}

	public President updateUser(String username, President updatedUser) {
		assert (updatedUser != null);
		return presidents.put(username, updatedUser);
	}

	public President deleteUser(String username) {
		return presidents.remove(username);
	}

	public President createUser(President newPresident) {
		return presidents.put(newPresident.getName(), newPresident);
	}

	private HashMap<String, President> loadPresidentsFromFile(String jsonfile) {
		TypeToken<List<President>> presidentToken = new TypeToken<List<President>>() {
		};
		List<President> presidentsList = this.getCollection(presidentToken,
				jsonfile);
		HashMap<String, President> presidentsMap = new HashMap<String, President>();
		for (President president : presidentsList) {
			presidentsMap.put(president.getName(), president);
		}
		return presidentsMap;
	}

	@SuppressWarnings("unchecked")
	public <T extends List<U>, U> T getCollection(TypeToken<T> typeToken,
			String jsonFile) {
		T collection;
		try {
			URL url = SampleDatabase.class.getClassLoader().getResource(
					jsonFile);
			BufferedReader br = new BufferedReader(
					new FileReader(url.getFile()));
			collection = gson.fromJson(br, typeToken.getType());
		} catch (IOException ioe) {
			collection = (T) new ArrayList<U>();
		}
		return collection;
	}

}

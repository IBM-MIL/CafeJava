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
	private static final String PRESIDENTS_JSON_FILENAME = "presidents_clean.json";
	private static Gson gson = new Gson();
	private static SampleDatabase INSTANCE = new SampleDatabase();
	private HashMap<Integer, President> presidents;

	// initailzed during class loading

	// to prevent creating another instance of Singleton
	private SampleDatabase() {
//		presidents = loadPresidentsFromFile(PRESIDENTS_JSON_FILENAME);
		presidents = new HashMap<Integer, President>();
		presidents.put(1, new President.PresidentBuilder("George Washington").birthYear(1732).deathYear(1799).tookOffice("1789-04-30").leftOffice("1797-03-04").party("No Party").build());
		presidents.put(2, new President.PresidentBuilder("John Adams").birthYear(1735).deathYear(1826).tookOffice("1797-03-04").leftOffice("1801-03-04").party("Federalist").build());
	}

	public static SampleDatabase getSingleton() {
		return INSTANCE;
	}

	public President getPresident(int presidentNumber) {
		return presidents.get(presidentNumber);
	}

	public President updateUser(int presidentNumber, President updatedUser) {
		assert (updatedUser != null);
		return presidents.put(presidentNumber, updatedUser);
	}

	public President deleteUser(int presidentNumber) {
		return presidents.remove(presidentNumber);
	}

	public President createUser(President newPresident) {
		return presidents.put(newPresident.getNumber(), newPresident);
	}

	private HashMap<Integer, President> loadPresidentsFromFile(String jsonfile) {
		TypeToken<List<President>> presidentToken = new TypeToken<List<President>>() {
		};
		List<President> presidentsList = this.getCollection(presidentToken,
				jsonfile);
		HashMap<Integer, President> presidentsMap = new HashMap<Integer, President>();
		for (President president : presidentsList) {
			presidentsMap.put(president.getNumber(), president);
		}
		return presidentsMap;
	}

	@SuppressWarnings("unchecked")
	public <T extends List<U>, U> T getCollection(TypeToken<T> typeToken,
			String jsonFile) {
		T collection;
		try {
			URL url = SampleDatabase.class.getClassLoader().getResource(jsonFile);
			BufferedReader br = new BufferedReader(
					new FileReader(url.getFile()));
			collection = gson.fromJson(br, typeToken.getType());
		} catch (IOException ioe) {
			collection = (T) new ArrayList<U>();
		}
		return collection;
	}

	public static void main(String... args) {
		President president = SampleDatabase.getSingleton().getPresident(2);
		System.out.println(president.getName());
	}
	
	
}

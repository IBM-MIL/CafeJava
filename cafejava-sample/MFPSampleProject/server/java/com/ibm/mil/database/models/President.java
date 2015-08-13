package com.ibm.mil.database.models;

public class President {
	private String name;
	private int number;
	private int birthYear;
	private int deathYear;
	private String tookOffice;
	private String leftOffice;
	private String party;

	private President(PresidentBuilder builder) {
		this.name = builder.name;
		this.number = builder.number;
		this.birthYear = builder.birthYear;
		this.deathYear = builder.deathYear;
		this.tookOffice = builder.tookOffice;
		this.leftOffice = builder.leftOffice;
		this.party = builder.party;
	}

	public String getName() {
		return name;
	}

	public int getNumber() {
		return number;
	}

	public int getBirthYear() {
		return birthYear;
	}

	public int getDeathYear() {
		return deathYear;
	}

	public String getTookOffice() {
		return tookOffice;
	}

	public String getLeftOffice() {
		return leftOffice;
	}

	public String getParty() {
		return party;
	}

	public static class PresidentBuilder {
		private String name;
		private int number;
		private int birthYear;
		private int deathYear;
		private String tookOffice;
		private String leftOffice;
		private String party;

		public PresidentBuilder(String name) {
			this.name = name;
		}

		public PresidentBuilder number(int number) {
			this.number = number;
			return this;
		}

		public PresidentBuilder birthYear(int birthYear) {
			this.birthYear = birthYear;
			return this;
		}

		public PresidentBuilder deathYear(int deathYear) {
			this.deathYear = deathYear;
			return this;
		}

		public PresidentBuilder tookOffice(String tookOffice) {
			this.tookOffice = tookOffice;
			return this;
		}

		public PresidentBuilder party(String party) {
			this.party = party;
			return this;
		}

		public President build() {
			return new President(this);
		}

	}
}

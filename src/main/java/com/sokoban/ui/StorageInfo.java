package com.sokoban.ui;

public class StorageInfo {
	private String name;
	private String date;

	public StorageInfo(String name, String date) {
		this.name = name;
		this.date = date;
	}

	// getter和setter
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
}

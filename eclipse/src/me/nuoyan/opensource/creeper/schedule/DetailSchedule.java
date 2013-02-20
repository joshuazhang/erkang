package me.nuoyan.opensource.creeper.schedule;

import java.util.List;

import me.nuoyan.opensource.creeper.persistence.DBConnection;


public class DetailSchedule {
	
	private List<Catcher> catchers;
	
	private String className;
	
	private DBConnection dbConnection;

	public DBConnection getDbConnection() {
		return dbConnection;
	}

	public void setDbConnection(DBConnection dbConnection) {
		this.dbConnection = dbConnection;
	}

	public List<Catcher> getCatchers() {
		return catchers;
	}

	public void setCatchers(List<Catcher> catchers) {
		this.catchers = catchers;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
}

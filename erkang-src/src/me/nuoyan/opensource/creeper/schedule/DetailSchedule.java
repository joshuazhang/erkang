package me.nuoyan.opensource.creeper.schedule;

import java.util.ArrayList;
import java.util.List;

import me.nuoyan.opensource.creeper.catching.Catch;
import me.nuoyan.opensource.creeper.persistence.impl.DBPersist;
import me.nuoyan.opensource.creeper.plugin.ErkangPlugin;


public class DetailSchedule {
	
	private List<Catch> catchers;
	
	private String className;
	
	private DBPersist dbConnection;
	
	private List<ErkangPlugin> plugins = new ArrayList<ErkangPlugin>();

	public List<ErkangPlugin> getPlugins() {
		return plugins;
	}

	public void setPlugins(List<ErkangPlugin> plugins) {
		this.plugins = plugins;
	}

	public DBPersist getDbConnection() {
		return dbConnection;
	}

	public void setDbConnection(DBPersist dbConnection) {
		this.dbConnection = dbConnection;
	}

	public List<Catch> getCatchers() {
		return catchers;
	}

	public void setCatchers(List<Catch> catchers) {
		this.catchers = catchers;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}
	
}

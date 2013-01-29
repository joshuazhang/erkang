package me.nuoyan.opensource.creeper.schedule;

import java.util.List;


public class DetailSchedule {
	
	private List<Catcher> catchers;
	
	private String className;

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

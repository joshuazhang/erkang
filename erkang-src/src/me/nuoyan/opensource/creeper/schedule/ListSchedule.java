package me.nuoyan.opensource.creeper.schedule;

import java.util.HashMap;

import me.nuoyan.opensource.creeper.catching.Catch;
import me.nuoyan.opensource.creeper.paging.NextPage;


public class ListSchedule {
	
	private Long sleepTime;
	
	private NextPage nextPage;
	
	private Catch catcher = new Catch();
	
	private String entry;
	
	private DetailSchedule detailSchedule;
	
	private String processLogFile;
	
	public Long getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(Long sleepTime) {
		this.sleepTime = sleepTime;
	}

	public String getProcessLogFile() {
		return processLogFile;
	}

	public void setProcessLogFile(String processLogFile) {
		this.processLogFile = processLogFile;
	}

	public NextPage getNextPage() {
		return nextPage;
	}

	public void setNextPage(NextPage nextPage) {
		this.nextPage = nextPage;
	}

	public DetailSchedule getDetailSchedule() {
		return detailSchedule;
	}

	public void setDetailSchedule(DetailSchedule detailSchedule) {
		this.detailSchedule = detailSchedule;
	}

	public Catch getCatcher() {
		return catcher;
	}

	public void setCatcher(Catch catcher) {
		this.catcher = catcher;
	}

	public String getEntry() {
		return entry;
	}

	public void setEntry(String entry) {
		this.entry = entry;
	}
}

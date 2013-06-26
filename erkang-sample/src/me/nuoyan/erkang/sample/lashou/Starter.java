package me.nuoyan.erkang.sample.lashou;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;

import me.nuoyan.opensource.creeper.filter.ParseException;
import me.nuoyan.opensource.creeper.schedule.ListSchedule;
import me.nuoyan.opensource.creeper.schedule.Scheduler;

import org.dom4j.DocumentException;
import org.htmlparser.util.ParserException;

public class Starter {
	
	public static void main(String[] args) throws DocumentException, ParseException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, ParserException, FileNotFoundException {
		ListSchedule schedule = Scheduler.getSchedule(
				Starter.class.getResourceAsStream("/com/search/tuan800/lashou/schedule.xml")
				);
		Scheduler.doSchedule(schedule);
	}

}

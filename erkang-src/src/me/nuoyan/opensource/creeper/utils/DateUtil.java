package me.nuoyan.opensource.creeper.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static String getFormattedDate(Date date) {
		return sdf.format(date);
	}
	
	public static String getFormattedNow() {
		return sdf.format(new Date());
	}
}

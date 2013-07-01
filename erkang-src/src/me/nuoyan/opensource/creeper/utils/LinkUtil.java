package me.nuoyan.opensource.creeper.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkUtil {

	public static String getAbstractLink(String link, String refer) {
		// TODO Auto-generated method stub
		if (link.substring(0, 7).toLowerCase().equals("http://")) {
			return link;
		}
		String directory = refer;
		String root = refer;
		if (refer.lastIndexOf("/") < 7) {
			directory = refer;
		} else {
			int i = refer.lastIndexOf("/");
			directory = refer.substring(0, i);
			String rootRegex = "(http://.*?)/.*?";
			Pattern p = Pattern.compile(rootRegex);
			Matcher m = p.matcher(refer);
			if (m.find()) {
				root = m.group(1);
			}
		}
		
		if (link.startsWith("/")) {
			return root + link;
		} else {
			return directory + "/" + link;
		}
	}
	
	public static void main(String[] args) {
		System.out.println(getAbstractLink("Service/HelpCenter.aspx?sysno=45", "http://www.newegg.com.cn/dsds/?cm_mmc=AFC-_-YIMA-_-460235|00bc46b5bd3a30d00f45-_-christmaspricedown"));
	}

}

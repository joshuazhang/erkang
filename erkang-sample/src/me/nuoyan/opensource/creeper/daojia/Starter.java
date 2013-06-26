package me.nuoyan.opensource.creeper.daojia;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import me.nuoyan.opensource.creeper.schedule.ListSchedule;
import me.nuoyan.opensource.creeper.schedule.Scheduler;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.LinkRegexFilter;
import org.htmlparser.filters.TagNameFilter;

public class Starter {

	private static AndFilter shopLinkFilter;
	
	private static AndFilter menuLinkFilter;
	
	static {
		shopLinkFilter = new AndFilter(new HasParentFilter(new TagNameFilter("h3"), false), new LinkRegexFilter("/rest/[0-9]+/[0-9]+"));
		menuLinkFilter = new AndFilter(new HasAttributeFilter("class", "hfcL"), new TagNameFilter("a"));
		
	}
	
	public static void main(String[] args) throws Exception {
		InputStream inputStream = Starter.class.getResourceAsStream("/com/search/daojia/erkang/schedule.xml");
		StringBuffer docText = new StringBuffer();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			String line;
			while ((line=br.readLine()) != null) {
				docText.append(line + "\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				inputStream.close();
			} catch (Exception e2) {
			}
		}
		
		String docStr = docText.toString();
		int totalCnt = 101;
		for (int i = 1; i <= totalCnt; i++) {
			String docStrTmp = docStr.replaceAll("<entry>http://www.daojia.com.cn/area/1/</entry>", "<entry>http://www.daojia.com.cn/area/"+i+"/</entry>");
			Document document = DocumentHelper.parseText(docStrTmp);
			ListSchedule schedule = Scheduler.getSchedule(document);
			
			Scheduler.doSchedule(schedule);
		}
	}

}

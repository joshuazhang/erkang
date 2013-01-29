package me.nuoyan.opensource.creeper.schedule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import me.nuoyan.opensource.creeper.action.DownloadAction;
import me.nuoyan.opensource.creeper.action.Downloader;
import me.nuoyan.opensource.creeper.action.FieldAction;
import me.nuoyan.opensource.creeper.action.HTMLGetter;
import me.nuoyan.opensource.creeper.filter.FilterBuilder;
import me.nuoyan.opensource.creeper.filter.ParseException;
import me.nuoyan.opensource.creeper.pick.AttrPick;
import me.nuoyan.opensource.creeper.pick.Pick;
import me.nuoyan.opensource.creeper.pick.Picker;
import me.nuoyan.opensource.creeper.pick.TextPick;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class Scheduler {
	
	public static ListSchedule getSchedule(InputStream inputStream) throws DocumentException, ParseException {
		StringBuffer docText = new StringBuffer();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			String line;
			while ((line=br.readLine()) != null) {
				docText.append(line);
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
		
		Document document = DocumentHelper.parseText(docText.toString());
		
		return getSchedule(document);
	}
	
	
	public static ListSchedule getSchedule(Document document) throws ParseException {
		Element rootElement = document.getRootElement();
		List<Element> elements = rootElement.elements();
		ListSchedule schedule = null;
		if ("list".equals(rootElement.attributeValue("type"))) {
			schedule = new ListSchedule();
			
			for (Element element : elements) {
				if ("filter".equals(element.getName())) {
					NodeFilter filter = FilterBuilder.getNodeFilter((Element) element.elements().get(0));
					schedule.getCatcher().setNodeFilter(filter);
				} else if ("pick".equals(element.getName())) {
					String type = element.attributeValue("type");
					if ("attr".equals(type)) {
						AttrPick pick = new AttrPick();
						List<Element> pickChildren = element.elements();
						for (Element pickElement : pickChildren) {
							if ("field".equals(pickElement.getName())) {
								pick.setAttrName(pickElement.getText());
								schedule.getCatcher().setPick(pick);
							}
						}
					} 
//					else if (condition) {
//						
//					}
				} else if ("action".equals(element.getName())) {
					String action = element.attributeValue("type");
					if ("download".equals(action)) {
						DownloadAction downloadAction = new DownloadAction();
						List<Element> ce = element.elements();
						for (Element e : ce) {
							if ("savedir".equals(e.getName())) {
								downloadAction.setSaveDir(e.getTextTrim());
							}
						}
						schedule.getCatcher().setAction(downloadAction);
					}
				} else if ("entry".equals(element.getName())) {
					schedule.setEntry(element.getText());
				} else if ("schedule".equals(element.getName())) {
					//抓详情的schedule
					if ("detail".equals(element.attributeValue("type"))) {
						DetailSchedule detailSchedule = new DetailSchedule();
						List<Catcher> catchers = new ArrayList<Catcher>();
						List<Element> des = element.elements();
						for (Element el : des) {
							if ("catch".equals(el.getName())) {
								Catcher catcher = buildCatcher(el);
								catchers.add(catcher);
							}
						}
						detailSchedule.setCatchers(catchers);
						detailSchedule.setClassName(element.attributeValue("class"));
						schedule.setDetailSchedule(detailSchedule);
					} else {
						System.out.println("wrong schedule type: " + element.attribute("type"));
					}
				} else if ("nextpage".equals(element.getName())) {
					NextPage nextPage = new NextPage();
					nextPage.setPrefix(element.elementText("prefix"));
					nextPage.setFilter(FilterBuilder.getNodeFilter((Element) element.element("filter").elements().get(0)));
					schedule.setNextPage(nextPage);
				} else if ("processlogfile".equals(element.getName())) {
					schedule.setProcessLogFile(element.getTextTrim());
				} else if ("sleeptime".equals(element.getName())) {
					schedule.setSleepTime(Long.valueOf(element.getTextTrim()));
				}
			}
		}
		
		return schedule;
	}
	
	public static Catcher buildCatcher(Element el) throws ParseException {
		Catcher catcher = new Catcher();
		if ("download".equals(el.attributeValue("action"))) {
			DownloadAction downloadAction = new DownloadAction();
			downloadAction.setSaveDir(el.elementText("saveDir"));
			downloadAction.setFolder(el.elementText("folder"));
			catcher.setAction(downloadAction);
		} else if ("field".equals(el.attributeValue("action"))) {
			FieldAction fieldAction = new FieldAction();
			fieldAction.setFieldName(el.attributeValue("field"));
			catcher.setAction(fieldAction);
		}
		NodeFilter downloadFilter = FilterBuilder.getNodeFilter((Element) el.element("filter").elements().get(0));
		catcher.setNodeFilter(downloadFilter);
		catcher.setPick(buildPick(el.element("pick")));
		return catcher;
	}
	
	public static Pick buildPick(Element el) {
		Pick pick = null;
		if ("attr".equals(el.attributeValue("type"))) {
			AttrPick attrpick = new AttrPick();
			attrpick.setAttrName(el.elementText("field"));
			pick = attrpick;
		} else if ("text".equals(el.attributeValue("type"))) {
			TextPick textpick = new TextPick();
			pick = textpick;
		}
		
		return pick;
	}
	
	
	public static void doSchedule(ListSchedule schedule) throws ParserException, SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, FileNotFoundException {
		String pageUrl = schedule.getEntry();
		while (pageUrl != null && pageUrl.length() > 0) {
			
			//检查是否已经抓过
			int cnt = checkIfDone(pageUrl, schedule.getProcessLogFile());
			
			//下一页的链接
			Parser parser = HTMLGetter.getHtmlParser(pageUrl, "UTF-8", null, "", "");
			NodeList nextPageNodeList = parser.extractAllNodesThatMatch(schedule.getNextPage().getFilter());
			if (nextPageNodeList.size() <= 0) {
				pageUrl = null;
				System.out.println("没有找到下一页");
			} else {
				pageUrl = schedule.getNextPage().getPrefix() + ((LinkTag)nextPageNodeList.elementAt(nextPageNodeList.size()-1)).getLink();
				System.out.println("nextpage : " + pageUrl);
			}
			
			//如果已经抓过这一页，继续抓下一页
			if (cnt > 0) {
				continue;
			}
			
			parser.reset();
			
//			AndFilter parentFilter = new AndFilter(new HasAttributeFilter("class", "deal "), new TagNameFilter("div"));
//			HasParentFilter hasParentFilter = new HasParentFilter(parentFilter, true);
//			AndFilter andFilter = new AndFilter(hasParentFilter, new TagNameFilter("img"));
			
			NodeList nodeList = parser.extractAllNodesThatMatch(schedule.getCatcher().getNodeFilter());
			for (int i = 0; i < nodeList.size(); i++) {
				Node node = nodeList.elementAt(i);
				String target = Picker.pickAttr((TagNode)node, schedule.getCatcher().getPick());
				//抓去详情
				doScheduleDetail(schedule.getDetailSchedule(), target);
			}
			//记录这一页已经抓取完毕
			PrintWriter logOS = null;
			try {
				logOS = new PrintWriter(new FileOutputStream(schedule.getProcessLogFile(), true));
				logOS.append(pageUrl + "\n");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				throw e;
			} finally {
				try {logOS.flush();logOS.close();} catch (Exception e2) {e2.printStackTrace();}
			}
			
		}
		
	}
	
	/**
	 * 检查是否已经抓过此页
	 * @param districtLinkStrFirstPage
	 * @return
	 */
	public static int checkIfDone(String pageUrl, String logFile) {
		BufferedReader br = null;
		int cnt = 0;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(logFile)));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.indexOf(pageUrl) >= 0) {
					System.out.println("找到【结束】抓的日志-" + pageUrl);
					cnt++;
				}
			}
			System.out.println("找到 " +cnt+ " 次 【结束】抓的日志-" + pageUrl);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return cnt;
	}
	
	
	public static Object doScheduleDetail(DetailSchedule detailSchedule, String url) throws ParserException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Object object = null;
		try {
			object = Class.forName(detailSchedule.getClassName()).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<Catcher> catchers = detailSchedule.getCatchers();
		Parser parser = HTMLGetter.getHtmlParser(url, "UTF-8", null, "", "");
		for (Catcher catcher : catchers) {
			doCatch(parser, catcher, object);
		}
		System.out.println(object);
		return object;
	}
	
	public static void doCatch(Parser parser, Catcher catcher, Object object) throws ParserException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		parser.reset();
		NodeList nodeList = parser.extractAllNodesThatMatch(catcher.getNodeFilter());
		if (catcher.getAction() instanceof FieldAction) {
			FieldAction fieldAction = (FieldAction) catcher.getAction();
			for (int i = 0; i < nodeList.size(); i++) {//其实只走一遍，因为整个页面只有一个属性值
				Method setterMethod = object.getClass().getDeclaredMethod("set" + fieldAction.getFieldName().substring(0, 1).toUpperCase() + fieldAction.getFieldName().substring(1), String.class);
				setterMethod.invoke(object, ((TagNode)nodeList.elementAt(i)).toHtml().replaceAll("<.*?>", ""));
				break;//先按只有一个符合条件的node，以后增加list等。。。
			}
		} else if (catcher.getAction() instanceof DownloadAction) {
			DownloadAction downloadAction = (DownloadAction) catcher.getAction();
			if (nodeList.size() == 0) {
				System.out.println("##########" + "没有找到可以下载的资源：" + parser.getURL());
			}
			for (int i = 0; i < nodeList.size(); i++) {//下载的话可以走多遍
				Method getterMethod = object.getClass().getDeclaredMethod("get" + downloadAction.getFolder().substring(0, 1).toUpperCase() + downloadAction.getFolder().substring(1));
				String folderValue = (String) getterMethod.invoke(object);
				if (!downloadAction.getSaveDir().endsWith("/")) {
					throw new ParserException("The \"saveDir\" should ends with \"/\"");
				}
				if (folderValue == null) {
					throw new ParserException("the download action catcher should below the folder field catcher element");
				}
				File saveFolder = new File(downloadAction.getSaveDir() + folderValue);
				if (!saveFolder.exists()) {
					saveFolder.mkdir();
				}
				String target = Picker.pickAttr((TagNode)nodeList.elementAt(i), catcher.getPick());
				Downloader.downloadFileToPath(target, null, saveFolder.getAbsolutePath(), null, "", "");
			}
		}
	}
	
	public static NextPage buildNextPage(Element el) throws ParseException {
		NextPage nextPage = new NextPage();
		nextPage.setPrefix(el.elementText("prefix"));
		NodeFilter nodeFilter = FilterBuilder.getNodeFilter(el.element("filter"));
		nextPage.setFilter(nodeFilter);
		return nextPage;
	}

}

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
import me.nuoyan.opensource.creeper.catching.Catch;
import me.nuoyan.opensource.creeper.filter.FilterBuilder;
import me.nuoyan.opensource.creeper.filter.ParseException;
import me.nuoyan.opensource.creeper.paging.NextPage;
import me.nuoyan.opensource.creeper.persistence.impl.DBPersist;
import me.nuoyan.opensource.creeper.picking.AttrPick;
import me.nuoyan.opensource.creeper.picking.Pick;
import me.nuoyan.opensource.creeper.picking.Picker;
import me.nuoyan.opensource.creeper.picking.RegexFindPick;
import me.nuoyan.opensource.creeper.picking.TextPick;
import me.nuoyan.opensource.creeper.plugin.CaptureMorePlugin;
import me.nuoyan.opensource.creeper.plugin.ErkangPlugin;
import me.nuoyan.opensource.creeper.utils.DBUtil;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class Scheduler {
	
	private static DBUtil dbUtil;
	
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
	
	/**
	 * 根据dom4j的文档对象，生成Schedule对象
	 * @param document
	 * @return
	 * @throws ParseException
	 */
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
							if ("savePrefix".equals(e.getName())) {
								downloadAction.setSavePrefix(e.getTextTrim());
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
						List<Catch> catchers = new ArrayList<Catch>();
						List<Element> des = element.elements();
						for (Element el : des) {
							if ("catch".equals(el.getName())) {
								Catch catcher = buildCatcher(el);
								catchers.add(catcher);
							} else if ("catchers".equals(el.getName())) {
								List<Element> catchersEl = el.elements();
								for (Element caEl : catchersEl) {
									Catch catcher = buildCatcher(caEl);
									catchers.add(catcher);
								}
							} else if ("plugins".equals(el.getName())) {
								//插件
								List<ErkangPlugin> plugins = new ArrayList<ErkangPlugin>();
								List<Element> plugEls = el.elements();
								for (Element plEl : plugEls) {
									//挂载插件
									String pluginClass = plEl.attributeValue("class");
									ErkangPlugin plugin = null;
									try {
										plugin = (ErkangPlugin)Class.forName(pluginClass).newInstance();
										plugins.add(plugin);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								detailSchedule.setPlugins(plugins);
							}
						}
						detailSchedule.setCatchers(catchers);
						detailSchedule.setClassName(element.attributeValue("class"));
						
						if (element.attributeValue("dburl")!=null && element.attributeValue("dburl").length() > 0) {
							DBPersist connection = new DBPersist(element.attributeValue("driver")
									, element.attributeValue("dburl")
									, element.attributeValue("username"), element.attributeValue("password")
									, element.attributeValue("tablename"));
							detailSchedule.setDbConnection(connection);
						}
						
						
						
						
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
	
	public static Catch buildCatcher(Element el) throws ParseException {
		Catch catcher = new Catch();
		if ("download".equals(el.attributeValue("action"))) {
			DownloadAction downloadAction = new DownloadAction();
			downloadAction.setSaveDir(el.elementText("saveDir"));
			downloadAction.setFolder(el.elementText("folder"));
			downloadAction.setSavePrefix(el.elementText("savePrefix"));
			catcher.setAction(downloadAction);
		} else if ("field".equals(el.attributeValue("action"))) {
			FieldAction fieldAction = new FieldAction();
			fieldAction.setFieldName(el.attributeValue("field"));
			fieldAction.setType(el.attributeValue("type"));
			catcher.setAction(fieldAction);
		}
		if (el.element("filter") != null) {
			NodeFilter downloadFilter = FilterBuilder.getNodeFilter((Element) el.element("filter").elements().get(0));
			catcher.setNodeFilter(downloadFilter);
		}
		if (el.element("pick") != null) {
			catcher.setPick(buildPick(el.element("pick")));
		}
		
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
		} else if ("regexFindText".equals(el.attributeValue("type"))) {
			RegexFindPick regexFindPick = new RegexFindPick();
			regexFindPick.setPattern(el.attributeValue("pattern"));
			pick = regexFindPick;
		}
		if (el.attributeValue("index") != null && el.attributeValue("index").length() > 0) {
			pick.setIndex(Integer.valueOf(el.attributeValue("index")));
		}
		
		if (el.attributeValue("fromIndex") != null && el.attributeValue("fromIndex").length() > 0) {
			pick.setToIndex(Integer.valueOf(el.attributeValue("fromIndex")));
		}
		
		if (el.attributeValue("toIndex") != null && el.attributeValue("toIndex").length() > 0) {
			pick.setToIndex(Integer.valueOf(el.attributeValue("toIndex")));
		}
		
		return pick;
	}
	
	
	public static void doSchedule(ListSchedule schedule) throws Exception {
		if (schedule.getDetailSchedule().getDbConnection()!=null &&
				schedule.getDetailSchedule().getDbConnection().getDriver() != null &&
				schedule.getDetailSchedule().getDbConnection().getDriver().length() > 0) {
			dbUtil = new DBUtil(schedule.getDetailSchedule().getDbConnection().getDriver(), 
					schedule.getDetailSchedule().getDbConnection().getUrl(), 
					schedule.getDetailSchedule().getDbConnection().getUsername(), 
					schedule.getDetailSchedule().getDbConnection().getPassword(), 
					schedule.getDetailSchedule().getDbConnection().getTableName());
		}
		
		String pageUrl = schedule.getEntry();
		while (pageUrl != null && pageUrl.length() > 0) {
			String prePageUrl = pageUrl;
			
			//下一页的链接
			Parser parser = HTMLGetter.getHtmlParser(pageUrl, "UTF-8", null, "", null);
			try {
				Thread.sleep(schedule.getSleepTime() == 0 ? 1000 : schedule.getSleepTime());
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			NodeList nextPageNodeList = parser.extractAllNodesThatMatch(schedule.getNextPage().getFilter());
			if (nextPageNodeList.size() <= 0) {
				pageUrl = null;
				System.out.println("没有找到下一页");
			} else if (((LinkTag)nextPageNodeList.elementAt(nextPageNodeList.size()-1)).getLink() == null) {
				System.out.println("没有找到下一页");
			} else if (((LinkTag)nextPageNodeList.elementAt(nextPageNodeList.size()-1)).getLink().indexOf("void") >= 0) {//以免最后一页时抓到空
				System.out.println("没有找到下一页");
			} else {
				String link = ((LinkTag)nextPageNodeList.elementAt(nextPageNodeList.size()-1)).getLink();
				if (!link.startsWith("http:")) {
					if (link.startsWith("/")) {
						pageUrl = schedule.getNextPage().getPrefix() + link;
					} else if (link.startsWith("?")) {
						int lastQmarkIndex = prePageUrl.lastIndexOf("?");
						if (lastQmarkIndex < 0) {
							pageUrl = prePageUrl + link;
						} else {
							pageUrl = prePageUrl.substring(0, lastQmarkIndex) + link;
						}
					} else {
						pageUrl = prePageUrl.substring(0, prePageUrl.lastIndexOf("/") + 1) + link;
					}
					
				} else {
					pageUrl = link;
				}
				System.out.println("nextpage : " + pageUrl);
				//如果下一页已经抓过，说明现在已经到了最后一页
//				if (checkIfDone(pageUrl, schedule.getProcessLogFile()) > 0) {
//					System.out.println("下一页的连接已经抓过了：" + pageUrl);
//					pageUrl = null;
//				}
			}
			//检查是否已经抓过
			int cnt = checkIfDone(prePageUrl, schedule.getProcessLogFile());
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
				String target = Picker.pickValue(node, schedule.getCatcher().getPick());
				if (!target.startsWith("http://")) {
					target = schedule.getNextPage().getPrefix() + target;
				}
				//抓去详情
				try {
					doScheduleDetail(schedule.getDetailSchedule(), target, schedule.getSleepTime());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			//记录这一页已经抓取完毕
			PrintWriter logOS = null;
			try {
				logOS = new PrintWriter(new FileOutputStream(schedule.getProcessLogFile(), true));
				logOS.append(prePageUrl + "\n");
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
				if (line.equals(pageUrl)) {
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
	
	
	public static Object doScheduleDetail(DetailSchedule detailSchedule, String url, long sleepTime) throws Exception {
		Object object = null;
		try {
			object = Class.forName(detailSchedule.getClassName()).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		List<Catch> catchers = detailSchedule.getCatchers();
		
		//看是否已经抓过，抓过就不抓了，省时间
		for (Catch catcher : catchers) {
			if (catcher.getAction() instanceof FieldAction) {
				FieldAction fieldAction = (FieldAction) catcher.getAction();
				if ("url".equals(fieldAction.getType())) {
					if (dbUtil != null) {
						List list = dbUtil.getList(object, 0, 1, fieldAction.getFieldName() + "='" + url + "'", "");
						if (list.size() > 0) {
							System.out.println("【【已抓，跳过】】====" + url);
							return object;
						}
					}
				}
			}
		}
				try {
					
					Thread.sleep(sleepTime == 0 ? 1000 : sleepTime);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
		Parser parser = HTMLGetter.getHtmlParser(url, "UTF-8", null, "", null);
		for (Catch catcher : catchers) {
			if (!(catcher.getAction() instanceof DownloadAction)) {
				doCatch(parser, catcher, object, url, sleepTime);
			}
		}
		//走插件
		for (ErkangPlugin plugin : detailSchedule.getPlugins()) {
			if (plugin != null && plugin instanceof CaptureMorePlugin) {
				((CaptureMorePlugin)plugin).doCapture(parser, object);
			}
			//...其他插件业务逻辑
		}
		
		//最后再做下载
		for (Catch catcher : catchers) {
			if (catcher.getAction() instanceof DownloadAction) {
				doCatch(parser, catcher, object, url, sleepTime);
			}
		}
		
		//如果配置了数据库，就持久化该对象
		if (detailSchedule.getDbConnection() != null && detailSchedule.getDbConnection().getUrl() != null) {
			detailSchedule.getDbConnection().persistMulti(object);
		}
		return object;
	}
	
	public static void doCatch(Parser parser, Catch catcher, Object object, String url, long sleepTime) throws ParserException, SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		parser.reset();
		if (catcher.getAction() instanceof FieldAction) {
			FieldAction fieldAction = (FieldAction) catcher.getAction();
			if ("recommended".equals(fieldAction.getFieldName())) {
				System.out.println("recommended");
			}
			if ("url".equals(fieldAction.getType())) {
				Method setterMethod = object.getClass().getDeclaredMethod("set" + fieldAction.getFieldName().substring(0, 1).toUpperCase() + fieldAction.getFieldName().substring(1), String.class);
				setterMethod.invoke(object, url);
			} else {
				NodeList nodeList = parser.extractAllNodesThatMatch(catcher.getNodeFilter());
				
				int fromIndex = catcher.getPick().getFromIndex();
				int toIndex = catcher.getPick().getToIndex();
				if (toIndex - fromIndex > 0) {
					//[from, to]闭区间
					String value = "";
					for (int i = fromIndex; i < toIndex + 1 && i < nodeList.size(); i++) {
						value += Picker.pickValue(nodeList.elementAt(i), catcher.getPick()) + "@@@@";
					}
					if (value.endsWith("@@@@")) {
						value = value.substring(0, value.length() - 4);
					}
					Method setterMethod = object.getClass().getDeclaredMethod("set" + fieldAction.getFieldName().substring(0, 1).toUpperCase() + fieldAction.getFieldName().substring(1), String.class);
					setterMethod.invoke(object, value);
				} else {
					if (nodeList.size() >= catcher.getPick().getIndex() + 1) {
						Method setterMethod = object.getClass().getDeclaredMethod("set" + fieldAction.getFieldName().substring(0, 1).toUpperCase() + fieldAction.getFieldName().substring(1), String.class);
						setterMethod.invoke(object, Picker.pickValue(nodeList.elementAt(catcher.getPick().getIndex()), catcher.getPick()));
					}
				}
			}
			
		} else if (catcher.getAction() instanceof DownloadAction) {
			NodeList nodeList = parser.extractAllNodesThatMatch(catcher.getNodeFilter());
			
			DownloadAction downloadAction = (DownloadAction) catcher.getAction();
			if (nodeList.size() == 0) {
				System.out.println("##########" + "没有找到可以下载的资源：" + parser.getURL());
			}
			for (int i = 0; i < nodeList.size(); i++) {//下载的话可以走多遍
				Method getterMethod = object.getClass().getDeclaredMethod("get" + downloadAction.getFolder().substring(0, 1).toUpperCase() + downloadAction.getFolder().substring(1));
				String folderValue = getterMethod.invoke(object) + "";
				if (folderValue.equals("null")) {
					System.out.println("文件夹值是空的");
					return;
				}
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
				String target = Picker.pickValue(nodeList.elementAt(i), catcher.getPick());
				try {
					if (target != null) {
						Downloader.downloadFileToPath(target, null, downloadAction.getSavePrefix(), saveFolder.getAbsolutePath(), null, "", "");
						Thread.sleep(sleepTime);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
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

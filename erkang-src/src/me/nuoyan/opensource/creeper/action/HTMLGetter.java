package me.nuoyan.opensource.creeper.action;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import me.nuoyan.opensource.creeper.htmlparser.tag.AddressTag;
import me.nuoyan.opensource.creeper.htmlparser.tag.Dd;
import me.nuoyan.opensource.creeper.htmlparser.tag.DlTag;
import me.nuoyan.opensource.creeper.htmlparser.tag.DtTag;
import me.nuoyan.opensource.creeper.htmlparser.tag.EmTag;
import me.nuoyan.opensource.creeper.htmlparser.tag.H1Tag;
import me.nuoyan.opensource.creeper.htmlparser.tag.H2Tag;
import me.nuoyan.opensource.creeper.htmlparser.tag.Li;
import me.nuoyan.opensource.creeper.htmlparser.tag.PTag;
import me.nuoyan.opensource.creeper.htmlparser.tag.StrongTag;
import me.nuoyan.opensource.creeper.htmlparser.tag.TbodyTag;
import me.nuoyan.opensource.creeper.htmlparser.tag.UlTag;

import org.htmlparser.Parser;
import org.htmlparser.PrototypicalNodeFactory;

public class HTMLGetter {
	
	public static ArrayList<String> userAgents = new ArrayList<String>();
	
	public static HashMap<String, String> cookies = new HashMap<String, String>();
	
	static {
		userAgents.add("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_3; zh-CN) AppleWebKit/533.4 (KHTML, like Gecko) Chrome/5.0.366.0 Safari/533.4 ");
		userAgents.add("Mozilla/5.0 (Macintosh; U; PPC Mac OS X; zh) AppleWebKit/418.9 (KHTML, like Gecko, Safari) Safari/419.3 Cheshire/1.0.ALPHA ");
		userAgents.add("Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; Deepnet Explorer 1.5.2; Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1) ; ");
		userAgents.add("Mozilla/5.0 (Windows; U; Windows NT 6.0; zh-CN; rv:1.9.1.6) Gecko/20091201 MRA 5.4 (build 02647) Firefox/3.5.6 (.NET CLR 3.5.30729)");
		userAgents.add("Mozilla/5.0 (Windows; U; Windows NT 6.0; zh-CN; rv:1.9.1.3) Gecko/20090824 Firefox/3.5.3 (.NET CLR 3.5.30729)");
		userAgents.add("Mozilla/5.0 (Windows; U; Windows NT 6.0; zh-CN; rv:1.9.0.12) Gecko/2009070611 Firefox/3.5.12");
		userAgents.add("Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9b2) Gecko/2007121120 Firefox/3.0b2");
		userAgents.add("Mozilla/5.0 (X11; U; Linux i686; zh-CN; rv:1.9.0.6) Gecko/2009011913 Firefox/3.0.6");
		userAgents.add("Mozilla/5.0 (X11; U; Linux x86_64; cs-CZ; rv:1.9.0.4) Gecko/2008111318 Ubuntu/8.04 (hardy) Firefox/3.0.4");
		userAgents.add("Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_3; en-US) AppleWebKit/533.4 (KHTML, like Gecko) Chrome/5.0.366.0 Safari/533.4 ");
		userAgents.add("Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.0.14) Gecko/2009082707 Firefox/3.0.14 GTB6");

	}
	public static Parser getHtmlParser(String url, String charString, HashMap<String, String> cookie, String referer, String userAgent) {
		
		try {
			long start = System.currentTimeMillis();

			HttpURLConnection connection = null;
			BufferedReader br = null;
			try {
				connection = (HttpURLConnection) new URL(url).openConnection();
				if (userAgent == null) {
					connection.addRequestProperty("User-Agent", userAgents.get(new Random().nextInt(11)));
				} else {
					connection.addRequestProperty("User-Agent", userAgent);
				}
				
				
				connection.setConnectTimeout(5000);
				connection.setReadTimeout(5000);
//				connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_3) AppleWebKit/537.22 (KHTML, like Gecko) Chrome/25.0.1364.172 Safari/537.22");
				if (cookie == null) {
					cookie = cookies;
				}
				if (cookie != null) {
					StringBuffer cookieSB = new StringBuffer();
					Set<String> cookieSet = cookie.keySet();
					for (String cookieKey : cookieSet) {
						cookieSB.append(cookieKey + "=" + cookie.get(cookieKey) + ";");
//						System.out.println("cookie >> "+cookieKey + "=" + cookie.get(cookieKey));
					}
					System.out.println("请求使用cookie >>" + cookieSB.toString());
					connection.addRequestProperty(
							"Cookie",
							cookieSB.toString());
				}
				System.out.println("User-Agent >> " + connection.getRequestProperty("User-Agent"));
				
				connection.addRequestProperty("Referer",
						referer);
				
				connection.addRequestProperty("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
				
				connection.addRequestProperty("Accept-Encoding",
						"gzip,deflate,sdch");
				
				connection.addRequestProperty("Accept-Charset",
						"GBK,utf-8;q=0.7,*;q=0.3");
				
				connection.addRequestProperty("Accept-Language",
						"zh-CN,zh;q=0.8");
				connection.addRequestProperty("Connection",
						"close");
				
				
				if ("gzip".equals(connection.getContentEncoding())) {//判断是否gzip压缩
					br = new BufferedReader(new InputStreamReader(new GZIPInputStream(connection.getInputStream()), charString));
				} else {
					br = new BufferedReader(new InputStreamReader(connection.getInputStream(), charString));
				}
				String line;
				StringBuffer sBuffer = new StringBuffer();
				while ((line = br.readLine())!=null) {
					sBuffer.append(line + "\n");
				}
				System.out.println(HTMLGetter.class + " time:"
						+ (System.currentTimeMillis() - start));
				
				System.out.println("下载完成 : - " + url);
				
				//====trace Cookies 追踪Cookies
				Map<String, List<String>> responseHeaders = connection.getHeaderFields();
				List<String> cookies = responseHeaders.get("Set-Cookie");
				if (cookies != null) {
					for (String c : cookies) {
						int e = c.indexOf("=");
						int f = c.indexOf(";");
						cookie.put(c.substring(0, e), c.substring(e+1, f));
					}
				}
				
				//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
				try {
//					Thread.sleep(1000L);这里不sleep了
				} catch (Exception e) {
					e.printStackTrace();
				}
//				System.out.println(sBuffer.toString());
				String noTbody = sBuffer.toString().replaceAll("<tbody id=\"rankingBody\">", "<table>");
				noTbody = noTbody.replaceAll("</tbody>", "</table>");
				Parser parser = Parser.createParser(noTbody, charString);
//				System.out.println(noTbody);
				parser.setNodeFactory(pnodefactory);
//				parser.setURL(url);//test
				//parser.setNodeFactory(DataTools.getPrototypicalNodeFactory());
				return parser;
			} catch (FileNotFoundException exx) {
				exx.printStackTrace();
				System.out.println("不存在的url：" + url);
			} catch (SocketTimeoutException exx) {
				exx.printStackTrace();
				System.out.println("下载网页时请求超时,一分钟后重试~~~~~~~~~~~~~~~~~~~~~" + url);
				Thread.sleep(60 * 1000);
				return getHtmlParser(url, charString, cookie, referer, userAgent);
			} catch (ConnectException e) {
				e.printStackTrace();
				System.out.println("下载时连接错误,一分钟后重试~~~~~~~~~~~~~~~~~~~~~" + url);
				Thread.sleep(60 * 1000);
				return getHtmlParser(url, charString, cookie, referer, userAgent);
			} catch (Exception e) {
				e.printStackTrace();
				if (e instanceof IOException) {
					try {
						connection.disconnect();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					System.out.println("下载网页时请求网页失败");
				}
			} finally {
				try {connection.disconnect();} catch (Exception e2) {e2.printStackTrace();}
				try {br.close();} catch (Exception e2) {e2.printStackTrace();}
			}
//		} catch (InterruptedException e) {
//			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	public static PrototypicalNodeFactory pnodefactory = null;
	static {
		if(pnodefactory==null){
			pnodefactory = new PrototypicalNodeFactory();
			pnodefactory.registerTag(new Li());
			pnodefactory.registerTag(new Dd());
			pnodefactory.registerTag(new H1Tag());
			pnodefactory.registerTag(new UlTag());
			pnodefactory.registerTag(new StrongTag());
			pnodefactory.registerTag(new AddressTag());
			pnodefactory.registerTag(new DtTag());
			pnodefactory.registerTag(new EmTag());
			pnodefactory.registerTag(new DlTag());
			pnodefactory.registerTag(new H2Tag());
			pnodefactory.registerTag(new PTag());
			pnodefactory.registerTag(new TbodyTag());
		}
	}
	
	
	public static String getHtmlAsString(String url, String charString, HashMap<String, String> cookie, String referer, String userAgent) {
		
		try {
			long start = System.currentTimeMillis();

			HttpURLConnection connection = null;
			BufferedReader br = null;
			try {
				connection = (HttpURLConnection) new URL(url).openConnection();
				if (userAgent == null) {
					connection.addRequestProperty("User-Agent", userAgents.get(new Random().nextInt(11)));
				} else {
					connection.addRequestProperty("User-Agent", userAgent);
				}
				connection.setConnectTimeout(5000);
				connection.setReadTimeout(5000);
				if (cookie == null) {
					cookie = cookies;
				}
				if (cookie != null) {
					StringBuffer cookieSB = new StringBuffer();
					Set<String> cookieSet = cookie.keySet();
					for (String cookieKey : cookieSet) {
						cookieSB.append(cookieKey + "=" + cookie.get(cookieKey) + ";");
//						System.out.println("cookie >> "+cookieKey + "=" + cookie.get(cookieKey));
					}
					System.out.println("请求使用cookie >>" + cookieSB.toString());
					connection.addRequestProperty(
							"Cookie",
							cookieSB.toString());
				}
				System.out.println("User-Agent >> " + connection.getRequestProperty("User-Agent"));
				
				connection.addRequestProperty("Referer",
						referer);
				
				connection.addRequestProperty("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
				
				connection.addRequestProperty("Accept-Encoding",
						"gzip,deflate,sdch");
				
				connection.addRequestProperty("Accept-Charset",
						"GBK,utf-8;q=0.7,*;q=0.3");
				
				connection.addRequestProperty("Accept-Language",
						"zh-CN,zh;q=0.8");
				connection.addRequestProperty("Connection",
						"close");
				
				String encoding = charString;
				String charSetRegex = "charset=(.*?)($|;)";
				Pattern p = Pattern.compile(charSetRegex);
				Matcher matcher = p.matcher(connection.getContentType());
				if (matcher.find()) {
					encoding = matcher.group(1);
				}
				if ("gzip".equals(connection.getContentEncoding())) {//判断是否gzip压缩
					br = new BufferedReader(new InputStreamReader(new GZIPInputStream(connection.getInputStream()), encoding));
				} else {
					br = new BufferedReader(new InputStreamReader(connection.getInputStream(), encoding));
				}
				String line;
				StringBuffer sBuffer = new StringBuffer();
				while ((line = br.readLine())!=null) {
					sBuffer.append(line + "\n");
				}
				System.out.println(HTMLGetter.class + " time:"
						+ (System.currentTimeMillis() - start));
				
				System.out.println("下载完成 : - " + url);
				
				//====trace Cookies 追踪Cookies
				Map<String, List<String>> responseHeaders = connection.getHeaderFields();
				List<String> cookies = responseHeaders.get("Set-Cookie");
				if (cookies != null) {
					for (String c : cookies) {
						int e = c.indexOf("=");
						int f = c.indexOf(";");
						cookie.put(c.substring(0, e), c.substring(e+1, f));
					}
				}
				
				//^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
				String result = sBuffer.toString();
				return result;
			} catch (FileNotFoundException exx) {
				exx.printStackTrace();
//				System.out.println("不存在的url：" + url);
			} catch (SocketTimeoutException exx) {
				exx.printStackTrace();
//				System.out.println("下载网页时请求超时,一分钟后重试~~~~~~~~~~~~~~~~~~~~~" + url);
//				Thread.sleep(60 * 1000);
//				return getHtmlAsString(url, charString, cookie, referer, userAgent);
			} catch (ConnectException e) {
				e.printStackTrace();
//				System.out.println("下载时连接错误,一分钟后重试~~~~~~~~~~~~~~~~~~~~~" + url);
//				Thread.sleep(60 * 1000);
//				return getHtmlAsString(url, charString, cookie, referer, userAgent);
			} catch (Exception e) {
				e.printStackTrace();
				if (e instanceof IOException) {
					try {
						connection.disconnect();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					System.out.println("下载网页时请求网页失败");
				}
			} finally {
				try {connection.disconnect();} catch (Exception e2) {e2.printStackTrace();}
				try {br.close();} catch (Exception e2) {e2.printStackTrace();}
			}
//		} catch (InterruptedException e) {
//			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	/**
	 * 解决在不知道编码情况下的乱码问题
	 * @param url
	 * @return
	 */
	public static String getUnMessyString(String url, HashMap<String, String> cookie, String referer, String userAgent) {
		System.out.println(">>>>>>>>>" + url);
		String html = HTMLGetter.getHtmlAsString(url, "GBK", cookie, referer, userAgent);
		if (html.indexOf("�") > 0) {
			html =  HTMLGetter.getHtmlAsString(url, "UTF-8", cookie, referer, userAgent);
		}
		if (html.indexOf("�") > 0) {
			html =  HTMLGetter.getHtmlAsString(url, "GBK", cookie, referer, userAgent);
		}
		
		return html;
	}

}

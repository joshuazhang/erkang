package me.nuoyan.opensource.creeper.action;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

import org.htmlparser.Parser;

public class HTMLGetter {
	
	public static Parser getHtmlParser(String url, String charString, HashMap<String, String> cookie, String referer, String userAgent) {
		
		try {
			long start = System.currentTimeMillis();

			HttpURLConnection connection = null;
			BufferedReader br = null;
			try {
				connection = (HttpURLConnection) new URL(url).openConnection();
				connection.addRequestProperty("User-Agent", userAgent);
				if (cookie != null) {
					StringBuffer cookieSB = new StringBuffer();
					Set<String> cookieSet = cookie.keySet();
					for (String cookieKey : cookieSet) {
						cookieSB.append(cookieKey + "=" + cookie.get(cookieKey) + ";");
						System.out.println("cookie >> "+cookieKey + "=" + cookie.get(cookieKey));
					}
					System.out.println("请求使用cookie >>" + cookieSB.toString());
					connection.addRequestProperty(
							"Cookie",
							cookieSB.toString());
				}
				
				connection.addRequestProperty("Referer",
						referer);
				
				connection.addRequestProperty("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
				
				connection.addRequestProperty("Accept-Encoding",
						"deflate,sdch");
				
				connection.addRequestProperty("Accept-Language",
						"zh-CN,zh;q=0.8");
				connection.addRequestProperty("Connection",
						"close");
				
				
				
				br = new BufferedReader(new InputStreamReader(connection.getInputStream(), charString));
				String line;
				StringBuffer sBuffer = new StringBuffer();
				while ((line = br.readLine())!=null) {
					sBuffer.append(line);
				}
				System.out.println(HTMLGetter.class + " time:"
						+ (System.currentTimeMillis() - start));
				
				System.out.println("下载完成 : - " + url);
				
				try {
					Thread.sleep(1000L);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				Parser parser = Parser.createParser(sBuffer.toString(), charString);
				//parser.setNodeFactory(DataTools.getPrototypicalNodeFactory());
				return parser;
			} catch (FileNotFoundException exx) {
				exx.printStackTrace();
				System.out.println("不存在的url：" + url);
			} catch (SocketTimeoutException exx) {
				exx.printStackTrace();
				System.out.println("下载网页时请求超时,重试~~~~~~~~~~~~~~~~~~~~~" + url);
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

}

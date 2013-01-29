package me.nuoyan.opensource.creeper.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;


public class Downloader {
	
	public static void downloadFileToPath(String url, String fileName, String directory, HashMap<String, String> cookie, String referer, String userAgent) {
		if (fileName == null || fileName.length() <= 0) {
			fileName = getFileName(url);
		}
		File dir = new File(directory);
		if (!dir.exists()) {
			dir.mkdir();// 如果路径不存在，就创建
		}
		
		File targetFile = new File(dir.getAbsolutePath() + "/" + fileName);
		try {
			if (targetFile.exists()) {
				throw new Exception("文件已存在" + targetFile.getAbsolutePath());
			}
			long start = System.currentTimeMillis();

			HttpURLConnection connection = null;
			FileOutputStream fileOs = null;
			InputStream connIs = null;
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
				
				fileOs = new FileOutputStream(
						dir.getAbsolutePath() + "/" + fileName);
				connIs = connection.getInputStream();
				byte[] buf = new byte[10*1024*1024];
				int cnt = 0;
				while ((cnt = connIs.read(buf)) > 0) {
					fileOs.write(buf, 0, cnt);
				}
				System.out.println(Downloader.class + " time:"
						+ (System.currentTimeMillis() - start));
				
				System.out.println("下载完成：" + fileName + " - " + url);
				try {
					Thread.sleep(1000L);
				} catch (Exception e) {
					e.printStackTrace();
				}
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
				try {fileOs.flush();} catch (Exception e2) {e2.printStackTrace();}
				try {fileOs.close();} catch (Exception e2) {e2.printStackTrace();}
				try {connIs.close();} catch (Exception e2) {e2.printStackTrace();}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	public static String getFileName(String url) {
		int i = url.lastIndexOf("/");
		if (i < 0) {
			return System.currentTimeMillis() + new Random().nextInt(999999) + "";
		} else {
			return url.substring(i +1);
		}
	}
	
	public static void main(String [] args) {
		
//		Downloader.downloadFileToPath("http://p11.tuan800.net/deal/deal_image/0553/1469/normal/d7680b04-6c8a-42ad-8545-e3f70fe25ca9.jpg", 
//				"test.jpg", "/Users/joshuazhang/Documents/work/creeper/", null, "tuan800.net", "");
		
		System.out.println(getFileName("http://p11.tuan800.net/deal/deal_image/0553/1469/normal/d7680b04-6c8a-42ad-8545-e3f70fe25ca9.jpg"));
		
	}
	
}

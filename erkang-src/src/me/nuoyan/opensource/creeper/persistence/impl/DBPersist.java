package me.nuoyan.opensource.creeper.persistence.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import me.nuoyan.opensource.creeper.persistence.Persist;
import me.nuoyan.opensource.creeper.persistence.Transparent;

/**
 * 保存、建立数据库连接，并且将抓取到的保存有数据的对象，入库
 * @author joshuazhang
 *
 */
public class DBPersist implements Persist {
	
	private String driver;
	
	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	private String url;
	
	private String username;
	
	private String password;
	
	private String tableName;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	
	public DBPersist(String driver, String url, String username, String password,
			String tableName) {
		super();
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.password = password;
		this.tableName = tableName;
	}

	private Connection conn;
	
	public void persist(Object object) throws Exception {
		if (conn == null) {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, username, password);
		}
		
		
		Field[] fields = object.getClass().getDeclaredFields();
		String fieldsStr = "";
		String values = "";
		for (Field field : fields) {
			if (field.isAnnotationPresent(Transparent.class)) {
				System.out.println("跳过属性：" + field.getName());
				continue;
			}
			fieldsStr += "," + field.getName();
			Method getterMethod = object.getClass().getDeclaredMethod("get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1));
			if (getterMethod.invoke(object) != null) {
				fieldsStr += "," + field.getName();
				String valueTmp = getterMethod.invoke(object) + "";
				if (valueTmp.indexOf("'") > 0) {
					System.out.println(valueTmp);
				}
				valueTmp = valueTmp.replaceAll("\'", "`");
				values += ",\'" +  valueTmp + "\'";
			}
			
		}
		fieldsStr = fieldsStr.substring(1);
		values = values.substring(1);
		
		String sql = "insert into " + tableName + "("+fieldsStr+")  values ("+values+")";
		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement(sql);
			statement.execute();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				statement.close();
			} catch (Exception e2) {
				
			}
		}
	}
	
	public void persistMulti(Object object) throws Exception {
		if (conn == null) {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, username, password);
		}
		
		
		Field[] fields = object.getClass().getDeclaredFields();
		String fieldsStr = "";
		List<String> values = new ArrayList<String>();
		//先吧所有的字段的名和值拿出来
		for (Field field : fields) {
			if (field.isAnnotationPresent(Transparent.class)) {
				System.out.println("跳过属性：" + field.getName());
				continue;
			}
			
			Method getterMethod = object.getClass().getDeclaredMethod("get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1));
			if (getterMethod.invoke(object) != null) {
				fieldsStr += "," + field.getName();
				String valueTmp = getterMethod.invoke(object) + "";
				if (valueTmp.indexOf("'") > 0) {
					System.out.println(valueTmp);
				}
				valueTmp = valueTmp.replaceAll("\'", "`");
				values.add(valueTmp);
			}
			
		}
		fieldsStr = fieldsStr.substring(1);
		
		//多值字段处理
		List<String[]> multiValues = new ArrayList<String[]>();
		int maxLength = 1;
		for (String value : values) {
//			if (value.indexOf("@@@@") >= 0) {
				String[] multiValueArr = value.split("@@@@");
				multiValues.add(multiValueArr);
				if (multiValueArr.length > maxLength) {
					maxLength = multiValueArr.length;//记录最大长度
				}
//			}
		}
		
		List<String> sqlList = new ArrayList<String>();
		for (int i = 0; i < maxLength; i++) {
			String insertValues = "";
			for (String[] valueArr : multiValues) {
				insertValues += ",'" + valueArr[valueArr.length > i ? i : valueArr.length -1] + "'";//如果不够长，就取最后一个值
			}
			insertValues = insertValues.substring(1);
			String sql = "insert into " + tableName + "("+fieldsStr+")  values ("+insertValues+")";
			sqlList.add(sql);
		}
		
		for (String sql : sqlList) {
			PreparedStatement statement = null;
			try {
				statement = conn.prepareStatement(sql);			
				statement.execute();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					statement.close();
				} catch (Exception e2) {
					
				}
			}
		}
		
	}
	
	public static void main(String[] args) {
//		String str = "fjdklsj";
//		String[] ss = str.split("@@@@");
//		
//		System.out.println(ss);
		
		String name = "Yasmine's Steakhouse and Butcher Shop";
		System.out.println(name.replaceAll("'", "`"));
		
	}
}

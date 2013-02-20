package me.nuoyan.opensource.creeper.persistence;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.htmlparser.nodes.TagNode;

public class DBConnection {
	
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
	
	
	public DBConnection(String driver, String url, String username, String password,
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
			fieldsStr += "," + field.getName();
			Method getterMethod = object.getClass().getDeclaredMethod("get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1));
			values += ",\'" +  getterMethod.invoke(object) + "\'";
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
}

package me.nuoyan.opensource.creeper.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import me.nuoyan.opensource.creeper.persistence.Transparent;

import com.mysql.jdbc.Statement;

public class DBUtil {
	
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
	
	public DBUtil(String driver, String url, String username, String password,
			String tableName) {
		super();
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.password = password;
		this.tableName = tableName;
	}

	private Connection conn;
	
	public Integer persist(Object object) throws Exception {
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
			
			Method getterMethod = object.getClass().getDeclaredMethod("get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1));
			if (getterMethod.invoke(object) != null) {
				fieldsStr += "," + field.getName();
				String valueTmp = getterMethod.invoke(object) + "";
				if (valueTmp.indexOf("'") > 0) {
					System.out.println(valueTmp);
				}
				valueTmp = valueTmp.replaceAll("\\\\", "\\\\\\\\").replaceAll("'", "\\\\\'").replaceAll("\0", " ").replaceAll(",", "\\\\,");
				valueTmp = valueTmp.replaceAll("[\uE000-\uF8FF]|\uD83C[\uDF00-\uDFFF]|\uD83D[\uDC00-\uDDFF]", "");
				values += ",\'" +  valueTmp + "\'";
			}
			
		}
		fieldsStr = fieldsStr.substring(1);
		values = values.substring(1);
		
		String sql = "insert into " + tableName + "("+fieldsStr+")  values ("+values+")";
		PreparedStatement statement = null;
		ResultSet rs = null;
		Integer id = null;
		try {
			statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			statement.execute();
			rs = statement.getGeneratedKeys();
			if (rs.next()) {
				id = rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(sql);
		} finally {
			try {
				statement.close();
				rs.close();
			} catch (Exception e2) {
				
			}
		}
		
		return id;
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
	
	public List getList(Object sample, int start, int limit, String whereClause, String orderBy) throws Exception {
		
		List resultList = new ArrayList();
		if (conn == null) {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, username, password);
		}
		Field[] fields = sample.getClass().getDeclaredFields();
		List<Field> setFields = new ArrayList<Field>();
		List<Method> setMethods = new ArrayList<Method>();
		String fieldsStr = "";
		for (Field field : fields) {
			if (field.isAnnotationPresent(Transparent.class)) {
				System.out.println("跳过属性：" + field.getName());
				continue;
			}
			fieldsStr += "," + field.getName();
			setFields.add(field);
			Method setterMethod = sample.getClass().getDeclaredMethod("set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1), field.getType());
			setMethods.add(setterMethod);
		}
		fieldsStr = fieldsStr.substring(1);
		if (whereClause == null || whereClause.trim().length() == 0) {
			whereClause = "1=1";
		}
		
		if (orderBy == null || orderBy.trim().length() == 0) {
			orderBy = "";
		}
		String sql = "select "+fieldsStr+" from " + tableName + " where "+whereClause+ " " + orderBy +" limit " + start + ", " + limit;
		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				Object object = sample.getClass().newInstance();
				for (Method method : setMethods) {
					Object value = rs.getObject(method.getName().toLowerCase().substring(3));
					try {
						method.invoke(object, value);
					} catch (IllegalArgumentException e) {
						if (value instanceof Integer) {
							try {
								method.invoke(object, Long.parseLong(value.toString()));
							} catch (Exception e1) {
								method.invoke(object, value != null ? value + "" : value);
							}
						} else {
							method.invoke(object, value != null ? value + "" : value);
						}
					}
				}
				resultList.add(object);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				statement.close();
			} catch (Exception e2) {
				
			}
		}
		
		return resultList;
	}
	
	public List getListBySql(Object sample, int start, int limit, String sql) throws Exception {
		
		List resultList = new ArrayList();
		if (conn == null) {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, username, password);
		}
		Field[] fields = sample.getClass().getDeclaredFields();
		List<Field> setFields = new ArrayList<Field>();
		List<Method> setMethods = new ArrayList<Method>();
		String fieldsStr = "";
		for (Field field : fields) {
			if (field.isAnnotationPresent(Transparent.class)) {
				System.out.println("跳过属性：" + field.getName());
				continue;
			}
			fieldsStr += "," + field.getName();
			setFields.add(field);
			Method setterMethod = sample.getClass().getDeclaredMethod("set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1), field.getType());
			setMethods.add(setterMethod);
		}
		fieldsStr = fieldsStr.substring(1);
		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				Object object = sample.getClass().newInstance();
				for (Method method : setMethods) {
					Object value = rs.getObject(method.getName().toLowerCase().substring(3));
					try {
						method.invoke(object, value);
					} catch (IllegalArgumentException e) {
						if (value instanceof Integer) {
							try {
								method.invoke(object, Long.parseLong(value.toString()));
							} catch (Exception e1) {
								method.invoke(object, value != null ? value + "" : value);
							}
						} else {
							method.invoke(object, value != null ? value + "" : value);
						}
					}
				}
				resultList.add(object);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				statement.close();
			} catch (Exception e2) {
				
			}
		}
		
		return resultList;
	}
	
	public void update(String setString, String whereClause) throws Exception {
		
		if (conn == null) {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, username, password);
		}
		if (whereClause == null || whereClause.trim().length() == 0) {
			whereClause = "1=1";
		}
		
		if (setString == null || setString.trim().length() == 0) {
			return;
		}
		String sql = "update " + tableName + " set " + setString + " where "+whereClause;
		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement(sql);
			statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				statement.close();
			} catch (Exception e2) {
				
			}
		}
	}
	
	public void delete(String whereClause) throws Exception {
		
		if (conn == null) {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, username, password);
		}
		if (whereClause == null || whereClause.trim().length() == 0) {
			System.err.println("WARNING: No where clause, nothing excuted!!");
			return;
		}
		String sql = "delete from " + tableName + " where "+whereClause;
		PreparedStatement statement = null;
		try {
			statement = conn.prepareStatement(sql);
			statement.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			try {
				statement.close();
			} catch (Exception e2) {
				
			}
		}
	}
	
	public void closeConn() {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			conn = null;
		}
	}
	
	/**
	 * 将字符串中的需要转义的字符串转义
	 * @param str
	 * @return
	 */
	public String transferStr(String str) {
		str = str.replaceAll("\\\\", "\\\\\\\\").replaceAll("'", "\\\\'").replaceAll(",", "\\\\,");
		return str;
	}
	
	public static void main(String[] args) {
		String name = "Yasmine's Steakhouse and Butcher Shop";
		System.out.println(name.replaceAll("'", "`"));
		
		
		DBUtil dbUtil = new DBUtil("com.mysql.jdbc.Driver", 
				"jdbc:mysql://localhost:3306/dianping?useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8", 
				"root", "root", "directly_city");
	}
}

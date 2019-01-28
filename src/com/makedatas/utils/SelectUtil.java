package com.makedatas.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.makedatas.datasmaker.DatasMaker.DBType;

public class  SelectUtil {

	private String jdbcDriver;
	private String dbUrl;

	private String userName;
	private String password;

	private final String dbTypeMySQL = "com.mysql.jdbc.Driver";
	private final String dbTypeOracle = "oracle.jdbc.driver.OracleDriver";

	public SelectUtil(){

	}

	public SelectUtil(DBType dbType, String ip, int port, String dataBaseName, String userName, String password){
		if (dbType == DBType.MySQL){
			this.jdbcDriver =  dbTypeMySQL;
			this.dbUrl =  "jdbc:mysql://" + ip + ":" + port + "/" + dataBaseName;
		} else if(dbType == DBType.Oracle) {
			this.jdbcDriver =  dbTypeOracle;
			this.dbUrl =  "jdbc:oracle:thin:@//" + ip + ":" + port + "/" + dataBaseName;
		}
		this.dbUrl =  "jdbc:mysql://" + ip + ":" + port + "/" + dataBaseName;
		this.userName = userName;
		this.password = password;

		try{
			// 加载驱动
			Class.forName(jdbcDriver);
			DriverManager.getConnection(dbUrl, userName, password);
		} catch (SQLException | ClassNotFoundException e1) {
			e1.printStackTrace();
		}
	}

	public String selectString(String resColumnName, String tableName) throws SQLException
	{
		Connection conn = null;
		Statement stmt = null;
		try{
			conn = DriverManager.getConnection(dbUrl, userName, password);
			stmt = conn.createStatement();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		ResultSet rs = stmt.executeQuery("select " + resColumnName + " from " + tableName);
		rs.next();
		return rs.getString(resColumnName);
	}

	public String selectString(String resColumnName, String tableName, String extra)
	{
		Connection conn = null;
		Statement stmt = null;
		try{
			conn = DriverManager.getConnection(dbUrl, userName, password);
			stmt = conn.createStatement();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		ResultSet rs;
		String result = null;
		try {
			rs = stmt.executeQuery("select " + resColumnName + " from " + tableName + " where " + extra);
			rs.next();
			result = rs.getString(resColumnName);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("搜索内容不合法, 退出程序. ");
			System.exit(0);
		}

		try {
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return result;
	}

}

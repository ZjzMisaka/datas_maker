package com.datasmaker.utils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;

import com.datasmaker.datasmaker.DatasMaker.DBType;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class  SelectUtil {
	private JSch jsch;
	private Session session;

	private String jdbcDriver;
	private String dbUrl;

	ResultSet rs;
	private Statement stmt = null;
	private Connection conn = null;

	private final String dbTypeMySQL = "com.mysql.jdbc.Driver";
	private final String dbTypeOracle = "oracle.jdbc.driver.OracleDriver";

	public SelectUtil(){

	}

	//直接连接数据库
	public SelectUtil(DBType dbType, String ip, int port, String dataBaseName, String dbUserName, String dbPassword){
		if (dbType == DBType.MySQL){
			this.jdbcDriver =  dbTypeMySQL;
			this.dbUrl =  "jdbc:mysql://" + ip + ":" + port + "/" + dataBaseName;
		} else if(dbType == DBType.Oracle) {
			this.jdbcDriver =  dbTypeOracle;
			this.dbUrl =  "jdbc:oracle:thin:@//" + ip + ":" + port + "/" + dataBaseName;
		}
		this.dbUrl =  "jdbc:mysql://" + ip + ":" + port + "/" + dataBaseName;

		try{
			// 加载驱动
			Class.forName(jdbcDriver);
			DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
		} catch (SQLException | ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		try{
			conn = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
			stmt = conn.createStatement();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	// 通过ssh进行连接数据库
	public SelectUtil(DBType dbType, String ip, int sshPort, int localPort, int dbPort, String sshUserName, String sshPassword, String dataBaseName, String dbUserName, String dbPassword){
		if (dbType == DBType.MySQL){
			this.jdbcDriver =  dbTypeMySQL;
			this.dbUrl =  "jdbc:mysql://localhost:" + localPort + "/" + dataBaseName;
		} else if(dbType == DBType.Oracle) {
			this.jdbcDriver =  dbTypeOracle;
			this.dbUrl =  "jdbc:oracle:thin:@//localhost:" + localPort + "/" + dataBaseName;
		}

		jsch = new JSch();
		try {
			session = jsch.getSession(sshUserName, ip, sshPort);
			session.setPassword(sshPassword);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			// 设置SSH本地端口转发,本地转发到远程
			session.setPortForwardingL(localPort, ip, dbPort);
		} catch (JSchException e) {
			e.printStackTrace();
		}

		try{
			conn = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
			stmt = conn.createStatement();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	//进行查询, 返回String类型结果
	//参数依次为: 需要得到的字段名, 需要查询的表名
	public String selectString(String resColumnName, String tableName)
	{
		String result = null;
		try {
			rs = stmt.executeQuery("select " + resColumnName + " from " + tableName);
			rs.next();
			result = rs.getString(resColumnName);
		} catch (SQLException e) {
			e.printStackTrace();
			close();
		}
		return result;
	}

	//进行查询, 返回String类型结果
	//参数依次为: 需要得到的字段名, 需要查询的表名, 查询语句WHERE后的判断条件
	public String selectString(String resColumnName, String tableName, String extra)
	{
		String result = null;
		try {
			rs = stmt.executeQuery("select " + resColumnName + " from " + tableName + " where " + extra);
			rs.next();
			result = rs.getString(resColumnName);
		} catch (SQLException e) {
			e.printStackTrace();
			close();
		}
		return result;
	}

	public Integer selectInt(String resColumnName, String tableName)
	{
		Integer result = null;
		try {
			rs = stmt.executeQuery("select " + resColumnName + " from " + tableName);
			rs.next();
			result = rs.getInt(resColumnName);
		} catch (SQLException e) {
			e.printStackTrace();
			close();
			result = null;
		}
		return result;
	}

	public Integer selectInt(String resColumnName, String tableName, String extra)
	{
		Integer result = null;
		try {
			rs = stmt.executeQuery("select " + resColumnName + " from " + tableName + " where " + extra);
			rs.next();
			result = rs.getInt(resColumnName);
		} catch (SQLException e) {
			e.printStackTrace();
			close();
		}
		return result;
	}

	public Date selectDate(String resColumnName, String tableName, String extra)
	{
		Date result = null;
		try {
			rs = stmt.executeQuery("select " + resColumnName + " from " + tableName + " where " + extra);
			rs.next();
			result = rs.getDate(resColumnName);
		} catch (SQLException e) {
			e.printStackTrace();
			close();
		}
		return result;
	}

	public Date selectDate(String resColumnName, String tableName)
	{
		Date result = null;
		try {
			rs = stmt.executeQuery("select " + resColumnName + " from " + tableName);
			rs.next();
			result = rs.getDate(resColumnName);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("搜索内容不合法, 退出程序. ");
			close();
			System.exit(0);
		}
		return result;
	}

	public Time selectTime(String resColumnName, String tableName, String extra)
	{
		Time result = null;
		try {
			rs = stmt.executeQuery("select " + resColumnName + " from " + tableName + " where " + extra);
			rs.next();
			result = rs.getTime(resColumnName);
		} catch (SQLException e) {
			e.printStackTrace();
			close();
		}
		return result;
	}

	public Time selectTime(String resColumnName, String tableName)
	{
		Time result = null;
		try {
			rs = stmt.executeQuery("select " + resColumnName + " from " + tableName);
			rs.next();
			result = rs.getTime(resColumnName);
		} catch (SQLException e) {
			e.printStackTrace();
			close();
		}
		return result;
	}

	public Timestamp selectTimestamp(String resColumnName, String tableName, String extra)
	{
		Timestamp result = null;
		try {
			rs = stmt.executeQuery("select " + resColumnName + " from " + tableName + " where " + extra);
			rs.next();
			result = rs.getTimestamp(resColumnName);
		} catch (SQLException e) {
			e.printStackTrace();
			close();
		}
		return result;
	}

	public Timestamp selectTimestamp(String resColumnName, String tableName)
	{
		Timestamp result = null;
		try {
			rs = stmt.executeQuery("select " + resColumnName + " from " + tableName);
			rs.next();
			result = rs.getTimestamp(resColumnName);
		} catch (SQLException e) {
			e.printStackTrace();
			close();
		}
		return result;
	}

	public Double selectDouble(String resColumnName, String tableName)
	{
		Double result = null;
		try {
			rs = stmt.executeQuery("select " + resColumnName + " from " + tableName);
			rs.next();
			result = rs.getDouble(resColumnName);
		} catch (SQLException e) {
			e.printStackTrace();
			close();
		}
		return result;
	}

	public Double selectDouble(String resColumnName, String tableName, String extra)
	{
		Double result = null;
		try {
			rs = stmt.executeQuery("select " + resColumnName + " from " + tableName + " where " + extra);
			rs.next();
			result = rs.getDouble(resColumnName);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("搜索内容不合法, 退出程序. ");
			close();
			System.exit(0);
		}
		return result;
	}

	public ResultSet select(String resColumnName, String tableName)
	{
		try {
			rs = stmt.executeQuery("select " + resColumnName + " from " + tableName);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("搜索内容不合法, 退出程序. ");
			close();
			System.exit(0);
		}
		return rs;
	}

	public ResultSet select(String resColumnName, String tableName, String extra)
	{
		try {
			rs = stmt.executeQuery("select " + resColumnName + " from " + tableName + " where " + extra);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("搜索内容不合法, 退出程序. ");
			close();
			System.exit(0);
		}
		return rs;
	}

	public String nextString(String resColumnName)
	{
		try {
			rs.next();
			return rs.getString(resColumnName);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Integer nextInt(String resColumnName)
	{
		try {
			rs.next();
			return rs.getInt(resColumnName);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Double nextDouble(String resColumnName)
	{
		try {
			rs.next();
			return rs.getDouble(resColumnName);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Date nextDate(String resColumnName)
	{
		try {
			rs.next();
			return rs.getDate(resColumnName);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Time nextTime(String resColumnName)
	{
		try {
			rs.next();
			return rs.getTime(resColumnName);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Timestamp nextTimestamp(String resColumnName)
	{
		try {
			rs.next();
			return rs.getTimestamp(resColumnName);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	//关闭连接
	public void close()
	{
		try {
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}

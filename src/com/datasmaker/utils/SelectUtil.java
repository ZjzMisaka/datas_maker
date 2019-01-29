package com.datasmaker.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.datasmaker.datasmaker.DatasMaker.DBType;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class  SelectUtil {
	private JSch jsch;
	private Session session;

	private String jdbcDriver;
	private String dbUrl;

	private Statement stmt = null;
	private Connection conn = null;

	private final String dbTypeMySQL = "com.mysql.jdbc.Driver";
	private final String dbTypeOracle = "oracle.jdbc.driver.OracleDriver";

	public SelectUtil(){

	}

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

		Connection conn = null;
		stmt = null;
		try{
			conn = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
			stmt = conn.createStatement();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
	}

	public String selectString(String resColumnName, String tableName)
	{
		ResultSet rs;
		String result = null;
		try {
			rs = stmt.executeQuery("select " + resColumnName + " from " + tableName);
			rs.next();
			result = rs.getString(resColumnName);
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			System.out.println("搜索内容不合法, 退出程序. ");
			close();
			System.exit(0);
		}
		return result;
	}

	public String selectString(String resColumnName, String tableName, String extra)
	{
		ResultSet rs;
		String result = null;
		try {
			rs = stmt.executeQuery("select " + resColumnName + " from " + tableName + " where " + extra);
			rs.next();
			result = rs.getString(resColumnName);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("搜索内容不合法, 退出程序. ");
			close();
			System.exit(0);
		}
		return result;
	}

	public void close()
	{
		try {
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
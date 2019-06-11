package com.datasmaker.datasmaker;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 *
 * @author ZjzMisaka
 *
 */

public class DatasMaker {
	//上轮添加数据是否完成.
	boolean hasFinishedTurnLastInvoke = false;
	// 上轮添加数据是否成功.
	boolean hasSucceedLastTurn = false;
	// 目前可以选择的数据库.
	public enum DBType{
		MySQL, Oracle
	}

	private JSch jsch;
	private Session session;
	private int assinged_port;

	private DBType dbType;

	private String jdbcDriver;
	private String dbUrl;

	private String dbUserName;
	private String dbPassword;

	private String tableName;

	private boolean isUseSSL = false;

	//private final String dbTypeMySQL = "com.mysql.jdbc.Driver";	//老版本jdbc
	private final String dbTypeMySQL = "com.mysql.cj.jdbc.Driver";
	private final String dbTypeOracle = "oracle.jdbc.driver.OracleDriver";

	// 获取当前表的名字.
	public String getTableName() {
		return tableName;
	}

	// 设置表名, 可以在调用结束后改变表名继续添加数据.
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setIsUseSSL(boolean isUseSSL) {
		if (dbType == DBType.MySQL) {
			this.isUseSSL = isUseSSL;
			this.dbUrl = this.dbUrl.split("[?]")[0].concat("?" + getIsUseSSL());
		} else {
			System.out.println("错误: 非MySQL数据库无需指定useSSL参数. ");
		}
	}

	public boolean getIsUseSSL() {
		return this.isUseSSL;
	}

	public DatasMaker(){
	}

	//直接连接数据库.
	// 参数依次为: 数据库类型, 数据库地址, 数据库接口, 数据库名, 数据库账号, 数据库密码.
	public DatasMaker(DBType dbType, String ip, int port, String dataBaseName, String dbUserName, String dbPassword){
		if (dbType == DBType.MySQL){
			this.jdbcDriver =  dbTypeMySQL;
			this.dbUrl =  "jdbc:mysql://" + ip + ":" + port + "/" + dataBaseName + "?useSSL=" + this.isUseSSL;
		} else if(dbType == DBType.Oracle) {
			this.jdbcDriver =  dbTypeOracle;
			this.dbUrl =  "jdbc:oracle:thin:@//" + ip + ":" + port + "/" + dataBaseName;
		}
		this.dbType = dbType;
		this.dbUrl =  "jdbc:mysql://" + ip + ":" + port + "/" + dataBaseName;
		this.dbUserName = dbUserName;
		this.dbPassword = dbPassword;
	}

	//直接连接数据库.
	// 参数依次为: 数据库类型, 数据库地址, 数据库接口, 数据库名, 数据库账号, 数据库密码, 数据库表名.
	public DatasMaker(DBType dbType, String ip, int port, String dataBaseName, String dbUserName, String dbPassword, String tableName){
		if (dbType == DBType.MySQL){
			this.jdbcDriver =  dbTypeMySQL;
			this.dbUrl =  "jdbc:mysql://" + ip + ":" + port + "/" + dataBaseName;
		} else if(dbType == DBType.Oracle) {
			this.jdbcDriver =  dbTypeOracle;
			this.dbUrl =  "jdbc:oracle:thin:@//" + ip + ":" + port + "/" + dataBaseName;
		}
		this.dbType = dbType;
		this.dbUserName = dbUserName;
		this.dbPassword = dbPassword;
		this.setTableName(tableName);
	}

	// 通过ssh连接数据库.
	// 参数依次为: 数据库类型, 地址, ssh端口, 本地端口, 数据库端口, ssh用户名, ssh密码, 数据库用户名, 数据库密码.
	public DatasMaker(DBType dbType, String ip, int sshPort, int localPort, int dbPort, String sshUserName, String sshPassword, String dataBaseName, String dbUserName, String dbPassword){
		if (dbType == DBType.MySQL){
			this.jdbcDriver =  dbTypeMySQL;
			this.dbUrl =  "jdbc:mysql://localhost:" + localPort + "/" + dataBaseName;
		} else if(dbType == DBType.Oracle) {
			this.jdbcDriver =  dbTypeOracle;
			this.dbUrl =  "jdbc:oracle:thin:@//localhost:" + localPort + "/" + dataBaseName;
		}
		this.dbType = dbType;
		this.dbUserName = dbUserName;
		this.dbPassword = dbPassword;

		jsch = new JSch();
		try {
			session = jsch.getSession(sshUserName, ip, sshPort);
			session.setPassword(sshPassword);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			//这里打印SSH服务器版本信息.
			System.out.println(session.getServerVersion());
			// 设置SSH本地端口转发,本地转发到远程.
			assinged_port = session.setPortForwardingL(localPort, ip, dbPort);
			System.out.println("localhost:" + assinged_port);
		} catch (JSchException e) {
			e.printStackTrace();
		}
	}

	// 通过ssh连接数据库.
	// 参数依次为: 数据库类型, 地址, ssh端口, 本地端口, 数据库端口, ssh用户名, ssh密码, 数据库用户名, 数据库密码, 表名.
	public DatasMaker(DBType dbType, String ip, int sshPort, int localPort, int dbPort, String sshUserName, String sshPassword, String dataBaseName, String dbUserName, String dbPassword, String tableName){
		if (dbType == DBType.MySQL){
			this.jdbcDriver =  dbTypeMySQL;
			this.dbUrl =  "jdbc:mysql://localhost:" + localPort + "/" + dataBaseName;
		} else if(dbType == DBType.Oracle) {
			this.jdbcDriver =  dbTypeOracle;
			this.dbUrl =  "jdbc:oracle:thin:@//localhost:" + localPort + "/" + dataBaseName;
		}
		this.dbType = dbType;
		this.dbUserName = dbUserName;
		this.dbPassword = dbPassword;
		this.setTableName(tableName);

		jsch = new JSch();
		try {
			session = jsch.getSession(sshUserName, ip, sshPort);
			session.setPassword(sshPassword);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			//这里打印SSH服务器版本信息.
			System.out.println(session.getServerVersion());
			// 设置SSH本地端口转发,本地转发到远程.
			assinged_port = session.setPortForwardingL(localPort, ip, dbPort);
			System.out.println("localhost:" + assinged_port);
		} catch (JSchException e) {
			e.printStackTrace();
		}
	}

	// methodName为此方法需要调用的制造数据的方法的名字, 如"makeData", callerClassName为制造数据的方法所属的类的名字, 如"com.test.DataMakerTest".
	// 制造数据方法和它的所属类的访问修饰符必须为public.
	public void makeDatas (int allDataTotalCount, int oneTurnDataTotalCount, String fields, String callerClassName, String methodName) {
		Class<?> callerCalss;
		Object classObj = null;
		Method method = null;
		try {
			callerCalss = Class.forName(callerClassName);
			// 获取类.
			classObj = callerCalss.newInstance();
			// 获取方法.
			method = classObj.getClass().getDeclaredMethod(methodName, boolean.class, boolean.class, int.class);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException e2) {
			e2.printStackTrace();
		}

		StringBuilder sqlDatas;		//用来存储用括号逗号拼接起来的数据.
		int dataCountNow = 0;

		Connection conn = null;
		Statement stmt = null;

		int dataCountThisTurnNow = 0;

		try{
			// 加载驱动.
			Class.forName(jdbcDriver);
			DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
			conn = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
			conn.setAutoCommit(false);
		} catch (SQLException | ClassNotFoundException e1) {
			e1.printStackTrace();
		}

		// 添加数据条数少于需要的条数, 开始新的一轮添加.
		while (dataCountNow < allDataTotalCount){
			//当下批次上传数据后数据量会超过需要的条数, 将这批次的数量改为剩下的条数.
			if(allDataTotalCount - dataCountNow < oneTurnDataTotalCount) {
				oneTurnDataTotalCount = allDataTotalCount - dataCountNow;
			}

			dataCountThisTurnNow = 0;
			sqlDatas = new StringBuilder();
			try {
				stmt = conn.createStatement();
			} catch (SQLException e1) {
				e1.printStackTrace();
				continue;
			}

			while (dataCountThisTurnNow < oneTurnDataTotalCount){
				String result = null;
				try {
					// 调用数据获取方法.
					result = (String)method.invoke(classObj, hasFinishedTurnLastInvoke, hasSucceedLastTurn, oneTurnDataTotalCount);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
					System.out.println("遇到未知错误, 退出程序. ");
					return;
				}
				hasFinishedTurnLastInvoke = false;

				// 拼接获取到的数据.
				if (dbType == DBType.MySQL) {
					sqlDatas.append("(").append(result).append("),");
				} else if (dbType == DBType.Oracle) {
					sqlDatas.append(" INTO ").append(tableName).append("(").append(fields).append(")").append(" VALUES (").append(result).append(")");
				}
				++dataCountThisTurnNow;
				++dataCountNow;
				System.out.println(dataCountNow + "/" + allDataTotalCount + "\t\t\t" + String.format("%.6f", dataCountNow / (allDataTotalCount * 1.0)) + "\t\t\t" + dataCountThisTurnNow + "/" + oneTurnDataTotalCount + "\t\t\t" + String.format("%.6f", dataCountThisTurnNow / (oneTurnDataTotalCount * 1.0)) + "\t\t\t" + "MAKING");
			}
			// 去除拼接完毕的数据字符串最后多余的逗号.
			if (dbType == DBType.MySQL) {
				sqlDatas.deleteCharAt(sqlDatas.length() - 1);
			}
			try{
				// 执行添加数据的sql语句, 一轮添加完成.
				System.out.println(dataCountNow + "/" + allDataTotalCount + "\t\t\t" + String.format("%.6f", dataCountNow / (allDataTotalCount * 1.0)) + "\t\t\t" + dataCountThisTurnNow + "/" + oneTurnDataTotalCount + "\t\t\t" + String.format("%.6f", dataCountThisTurnNow / (oneTurnDataTotalCount * 1.0)) + "\t\t\t" + "UPDATING");
				if (dbType == DBType.MySQL) {
					stmt.executeUpdate("INSERT INTO `" + tableName +"` (" + fields + ") VALUES " + sqlDatas.toString());
				} else if (dbType == DBType.Oracle) {
					stmt.executeUpdate("INSERT ALL" + sqlDatas.toString() + " SELECT * FROM DUAL");
				}
				System.out.println(dataCountNow + "/" + allDataTotalCount + "\t\t\t" + String.format("%.6f", dataCountNow / (allDataTotalCount * 1.0)) + "\t\t\t" + dataCountThisTurnNow + "/" + oneTurnDataTotalCount + "\t\t\t" + String.format("%.6f", dataCountThisTurnNow / (oneTurnDataTotalCount * 1.0)) + "\t\t\t" + "DONE");
				conn.commit();
				hasSucceedLastTurn = true;
			} catch (SQLException e) {
				// 当某条数据不合法, 这次添加不作数, 重新获取数据.
				e.printStackTrace();
				System.out.println("遇到错误, 重新获取数据. ");
				dataCountNow -= oneTurnDataTotalCount;
				// 添加数据不成功, hasSucceedLastInvoke置为false, 在下次invoke方法调用时传递给制造数据的方法.
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				hasSucceedLastTurn = false;
			}

			hasFinishedTurnLastInvoke = true;

			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void makeDatas(int allDataTotalCount, String fields, String callerClassName, String methodName){
		int maxAllowedPacket = 0;
		String characterSetDatabase = null;

		Class<?> callerCalss;
		Object classObj = null;
		Method method = null;
		try {
			callerCalss = Class.forName(callerClassName);
			// 获取类.
			classObj = callerCalss.newInstance();
			// 获取方法.
			method = classObj.getClass().getDeclaredMethod(methodName, boolean.class, boolean.class, int.class);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException e2) {
			e2.printStackTrace();
		}

		String result;
		String resultTemp = null;

		StringBuilder sqlDatas;		//用来存储用括号逗号拼接起来的数据.
		int sqlDatasBytesLenth = 0;	//字符串的字节数, 下同.
		int insertSqlOtherStrBytesLenth = 0;
		int tableNameBytesLenth = 0;
		int fieldsBytesLenth = 0;
		int resultBytesLenth = 0;
		int extraBytesLenth = 0;

		int dataCountNow = 0;

		Connection conn = null;
		Statement stmt = null;

		int resultGetCountThisTurnNow = 0;
		int dataAppendCountThisTurnNow = 0;
		int dataGetCountLastTurn = 0;

		try{
			// 加载驱动.
			Class.forName(jdbcDriver);
			DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
			conn = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
			conn.setAutoCommit(false);
		} catch (SQLException | ClassNotFoundException e1) {
			e1.printStackTrace();
		}

		//获取数据库允许的最大数据包大小与数据库使用的编码格式.
		try {
			stmt = conn.createStatement();
			ResultSet resultSet = null;
			if (dbType == DBType.MySQL) {
				resultSet = stmt.executeQuery("show VARIABLES like '%max_allowed_packet%';");
				resultSet.next();
				maxAllowedPacket = resultSet.getInt("Value");

				resultSet =  stmt.executeQuery("show variables like 'character_set_database';");
				resultSet.next();
				characterSetDatabase = resultSet.getString("Value");

				// 高版本MySQL增加了utf8mb4编码, java无法处理. 但是utf8mb4完全兼容utf8, 因此直接使用utf8计算.
				if (characterSetDatabase == "utf8mb4") {
					characterSetDatabase = "utf8";
				}
			} else if (dbType == DBType.Oracle) {
				resultSet = stmt.executeQuery("select value from v$parameter where name='db_block_size'");
				resultSet.next();
				maxAllowedPacket = resultSet.getInt("Value") * 4194303;

				resultSet =  stmt.executeQuery("select * from nls_database_parameters where parameter ='NLS_CHARACTERSET'");
				resultSet.next();
				characterSetDatabase = resultSet.getString("Value");
			}

			resultSet.close();
			stmt.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		try {
			//根据数据库使用的编码初始化字节数.
			if (dbType == DBType.MySQL) {
				insertSqlOtherStrBytesLenth = "INSERT INTO `` () VALUES ".getBytes(characterSetDatabase).length;
				tableNameBytesLenth = tableName.getBytes(characterSetDatabase).length;
				fieldsBytesLenth = fields.getBytes(characterSetDatabase).length;
				extraBytesLenth = "(),".getBytes(characterSetDatabase).length;
			} else if (dbType == DBType.Oracle) {
				insertSqlOtherStrBytesLenth = "INSERT ALL SELECT * FROM DUAL".getBytes(characterSetDatabase).length;
				tableNameBytesLenth = tableName.getBytes(characterSetDatabase).length;
				fieldsBytesLenth = fields.getBytes(characterSetDatabase).length;
				extraBytesLenth = " INTO () VALUES ()".getBytes(characterSetDatabase).length;
			}
		} catch (UnsupportedEncodingException e2) {
			System.out.println("不支持的字符集: " + e2.getMessage());
			return;
		}

		// 添加数据条数少于需要的条数, 开始新的一轮添加.
		while (dataCountNow < allDataTotalCount){
			sqlDatasBytesLenth = 0;

			resultGetCountThisTurnNow = 0;
			dataAppendCountThisTurnNow = 0;

			sqlDatas = new StringBuilder();
			try {
				stmt = conn.createStatement();
			} catch (SQLException e1) {
				e1.printStackTrace();
				continue;
			}

			while (dataCountNow < allDataTotalCount){
				try {
					// 获取上轮数据获取中最后一条因为数据包大小限制没有上传数据库的数据或者调用数据获取方法.
					if (hasFinishedTurnLastInvoke && hasSucceedLastTurn){
						result = resultTemp;
					} else if (hasFinishedTurnLastInvoke && !hasSucceedLastTurn) {
						result = (String)method.invoke(classObj, hasFinishedTurnLastInvoke, hasSucceedLastTurn, dataGetCountLastTurn);
					} else {
						result = (String)method.invoke(classObj, hasFinishedTurnLastInvoke, hasSucceedLastTurn, 0);
					}
					++resultGetCountThisTurnNow;
					resultBytesLenth = result.getBytes(characterSetDatabase).length;
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | UnsupportedEncodingException e) {
					e.printStackTrace();
					System.out.println("遇到未知错误, 退出程序. ");
					return;
				}
				hasFinishedTurnLastInvoke = false;

				// 如果拼接这条数据后的字节总数没有超出数据库允许的最大数据包大小限制则拼接这条数据, 如果超出限制则保存这条数据, 在下一轮中添加.
				if (dbType == DBType.MySQL && sqlDatasBytesLenth + insertSqlOtherStrBytesLenth + tableNameBytesLenth + fieldsBytesLenth + resultBytesLenth + extraBytesLenth <= maxAllowedPacket){
					sqlDatas.append("(").append(result).append("),");
					sqlDatasBytesLenth += (resultBytesLenth + extraBytesLenth);
					++dataAppendCountThisTurnNow;
					++dataCountNow;
					System.out.println(dataCountNow + "/" + allDataTotalCount + "\t\t\t" + String.format("%.6f", dataCountNow / (allDataTotalCount * 1.0)) + "\t\t\t" + dataAppendCountThisTurnNow + "\t\t\t" + "MAKING");
				// MAYBE TODO
				} else if (dbType == DBType.Oracle && sqlDatasBytesLenth + insertSqlOtherStrBytesLenth + tableNameBytesLenth + fieldsBytesLenth + resultBytesLenth + extraBytesLenth <= maxAllowedPacket) {
					sqlDatas.append(" INTO ").append(tableName).append("(").append(fields).append(")").append(" VALUES (").append(result).append(")");
					sqlDatasBytesLenth += (resultBytesLenth + extraBytesLenth);
					++dataAppendCountThisTurnNow;
					++dataCountNow;
				} else {
					resultTemp = result;
					break;
				}
			}
			// 去除拼接完毕的数据字符串最后多余的逗号.
			if (sqlDatas.length() > 0) {
				sqlDatas.deleteCharAt(sqlDatas.length() - 1);
			} else {
				System.out.println("单条数据长度过长, 无法上传. ");
				return;
			}
			try {
				// 执行添加数据的sql语句, 一轮添加完成.
				System.out.println(dataCountNow + "/" + allDataTotalCount + "\t\t\t" + String.format("%.6f", dataCountNow / (allDataTotalCount * 1.0)) + "\t\t\t" + dataAppendCountThisTurnNow + "\t\t\t" + "UPDATING");
				if (dbType == DBType.MySQL) {
					stmt.executeUpdate("INSERT INTO `" + tableName +"` (" + fields + ") VALUES " + sqlDatas.toString());
				} else if (dbType == DBType.Oracle) {
					stmt.executeUpdate("INSERT ALL" + sqlDatas.toString() + " SELECT * FROM DUAL");
				}
				System.out.println(dataCountNow + "/" + allDataTotalCount + "\t\t\t" + String.format("%.6f", dataCountNow / (allDataTotalCount * 1.0)) + "\t\t\t" + dataAppendCountThisTurnNow + "\t\t\t" + "DONE");
				conn.commit();
				hasSucceedLastTurn = true;
			} catch (SQLException e) {
				// 当某条数据不合法, 这次添加不作数, 重新获取数据.
				e.printStackTrace();
				System.out.println("遇到错误, 重新获取数据. ");
				dataCountNow -= dataAppendCountThisTurnNow;
				dataGetCountLastTurn = resultGetCountThisTurnNow;
				// 添加数据不成功, hasSucceedLastTurn置为false, 在下次invoke方法调用时传递给制造数据的方法.
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				hasSucceedLastTurn = false;
			}

			hasFinishedTurnLastInvoke = true;

			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}

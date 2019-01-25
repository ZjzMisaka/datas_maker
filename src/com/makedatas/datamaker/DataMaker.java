package com.makedatas.datamaker;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 
 * @author ZjzMisaka
 *
 */

// 可以增加带数据长度参数的makeDatas方法, 得到一条数据的长度后自动计算oneTurnDataCount的值.

public class DataMaker {
	// 上次添加数据是否成功
	boolean hasSuccessedLastInvoke = true;
	// 目前可以选择的数据库
	public enum DBType{
		MySQL, Oracle
	}

	private String jdbcDriver;
	private String dbUrl;

	private String userName;
	private String password;

	private String tableName;

	private final String dbTypeMySQL = "com.mysql.jdbc.Driver";
	private final String dbTypeOracle = "oracle.jdbc.driver.OracleDriver";

	// 获取当前表的名字
	public String getTableName() {
		return tableName;
	}

	// 设置表名, 可以在调用结束后改变表名继续添加数据
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public DataMaker(){
	}

	// 参数依次为: 数据库类型, 数据库地址, 数据库接口, 数据库名, 数据库账号, 数据库密码
	public DataMaker(DBType dbType, String ip, int port, String dataBaseName, String userName, String password){
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
	}

	// 参数依次为: 数据库类型, 数据库地址, 数据库接口, 数据库名, 数据库账号, 数据库密码, 数据库表名
	public DataMaker(DBType dbType, String ip, int port, String dataBaseName, String userName, String password, String tableName){
		if (dbType == DBType.MySQL){
			this.jdbcDriver =  dbTypeMySQL;
			this.dbUrl =  "jdbc:mysql://" + ip + ":" + port + "/" + dataBaseName;
		} else if(dbType == DBType.Oracle) {
			this.jdbcDriver =  dbTypeOracle;
			this.dbUrl =  "jdbc:oracle:thin:@//" + ip + ":" + port + "/" + dataBaseName;
		}
		this.userName = userName;
		this.password = password;
		this.setTableName(tableName);
	}

	// methodName为此方法需要调用的制造数据的方法的名字, 如"makeData", callerClassName为制造数据的方法所属的类的名字, 如"com.test.DataMakerTest".
	// 制造数据方法和它的所属类的访问修饰符必须为public.
	public void makeDatas(int allDataTotalCount, int oneTurnDataTotalCount, String fields, String callerClassName, String methodName){
		Class<?> callerCalss;
		Object classObj = null;
		Method method = null;
		try {
			callerCalss = Class.forName(callerClassName);
			// 获取类
			classObj = callerCalss.newInstance();
			// 获取方法
			method = classObj.getClass().getDeclaredMethod(methodName, boolean.class);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException e2) {
			e2.printStackTrace();
		}

		StringBuffer sqlDatas;
		int dataCountNow = 1;

		Connection conn = null;
		Statement stmt = null;

		int dataCountThisTurnNow = 1;

		try{
			// 加载驱动
			Class.forName(jdbcDriver);
			DriverManager.getConnection(dbUrl, userName, password);
		} catch (SQLException | ClassNotFoundException e1) {
			e1.printStackTrace();
		}

		// 添加数据总数少于需要的总数, 开始新的一轮添加
		while (dataCountNow <= allDataTotalCount){
			//当下批次上传数据后数据量会超过需要的总数, 将这批次的数量改为剩下的数目. 
			if(allDataTotalCount - dataCountNow < oneTurnDataTotalCount) {
				oneTurnDataTotalCount = allDataTotalCount - dataCountNow + 1;
			}
			
			dataCountThisTurnNow = 1;
			sqlDatas = new StringBuffer();
			try{
				conn = DriverManager.getConnection(dbUrl, userName, password);
				stmt = conn.createStatement();
			} catch (SQLException e1) {
				e1.printStackTrace();
				continue;
			}

			while (dataCountThisTurnNow <= oneTurnDataTotalCount){
				String result = null;
				try {
					// 调用数据获取方法
					result = (String)method.invoke(classObj, hasSuccessedLastInvoke);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
					System.out.println("遇到未知错误, 退出程序");
					return;
				}

				hasSuccessedLastInvoke = true;

				// 拼接获取到的数据
				sqlDatas.append("(" + result + "),");
				System.out.println(dataCountNow + "/" + allDataTotalCount + "\t\t\t" + String.format("%.6f", dataCountNow / (allDataTotalCount * 1.0)) + "\t\t\t" + dataCountThisTurnNow + "/" + oneTurnDataTotalCount + "\t\t\t" + String.format("%.6f", dataCountThisTurnNow / (oneTurnDataTotalCount * 1.0)));
				++dataCountThisTurnNow;
				++dataCountNow;
			}
			// 去除拼接完毕的数据字符串最后多余的逗号
			sqlDatas.deleteCharAt(sqlDatas.length() - 1);
			try{
				// 执行添加数据的sql语句, 一轮添加完成.
				stmt.executeUpdate("INSERT INTO " + tableName +" (" + fields + ") VALUES " + sqlDatas.toString());
			} catch (SQLException e) {
				// 当某条数据不合法, 这次添加不作数, 重新获取数据.
				e.printStackTrace();
				System.out.println("遇到错误, 重新获取数据");
				dataCountNow -= oneTurnDataTotalCount;
				// 添加数据不成功, hasSuccessedLastInvoke置为false, 在下次invoke方法调用时传递给制造数据的方法
				hasSuccessedLastInvoke = false;
			}

			try {
				stmt.close();
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}

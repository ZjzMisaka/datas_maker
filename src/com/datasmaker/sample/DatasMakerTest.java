package com.datasmaker.sample;
import java.util.Random;

import com.datasmaker.datasmaker.DatasMaker;
import com.datasmaker.datasmaker.DatasMaker.DBType;
import com.datasmaker.utils.SelectUtil;

/**
 *
 * @author ZjzMisaka
 *
 */

public class DatasMakerTest {

	static SelectUtil selectUtil;
	static int aint = 0;

	//直接连接数据库
	/*public static void main(String[] args) {
		// 参数依次为: 数据库类型, 数据库地址, 数据库接口, 数据库名, 数据库账号, 数据库密码, 数据库表名
		DatasMaker dataMaker = new DatasMaker(DBType.MySQL, "192.111.11.11", 3306, "database_name", "root", "root", "table_name");
		// 总共往test表添加87654321行数据, 每次添加12345条数据.
		// 参数依次为: 需要的数据条数, 一轮批次添加的数据条数, 需要传递的字段名列表, 用作制造数据的方法所属的类名, 用作制造数据的方法的名字.
		dataMaker.makeDatas(87654321, 12345, "aint, astring, adate", "com.makedatas.sample.DataMakerTest", "makeData");
		System.exit(0);
	}*/

	//通过ssh连接数据库
	public static void main(String[] args) {
		// 参数依次为: 数据库类型, 数据库地址, 数据库接口, 数据库名, 数据库账号, 数据库密码, 数据库表名
		DatasMaker dataMaker = new DatasMaker(DBType.MySQL, "192.111.11.11", 22, 5217, 3306, "sshUserName", "sshPwd", "database_name", "dbUserName", "dbPwd", "table_name");
		//如果数据库是MySQL, 可以设置是否使用SSL与服务器通信. 如果不设置, 默认为false.
		dataMaker.setIsUseSSL(true);
		// 初始化查询工具
		// 参数依次为: 数据库类型, 地址, ssh接口, 本地接口, 数据库接口, ssh用户名, ssh密码, 数据库名, 数据库用户名, 数据库密码.
		selectUtil = new SelectUtil(DBType.MySQL, "192.111.11.11", 22, 5218, 3306, "sshUserName", "sshPwd", "database_name", "dbUserName", "dbPwd");
		// 总共往test表添加87654321行数据, 自动计算每批次的条数
		// 参数依次为: 需要的数据总数, 需要传递的字段名列表, 用作制造数据的方法所属的类名, 用作制造数据的方法的名字.
		dataMaker.makeDatas(87654321, "`aint`, `astring`, `adate`", "com.datasmaker.sample.DatasMakerTest", "makeData");
		selectUtil.close();
		System.exit(0);
	}

	// 此方法用于产生数据, 一次调用生成一条.
	// 参数hasSucceedLastInvoke含义为上轮添加数据是否成功, 若未成功可能需要把数据还原成一轮之前的状态.
	// 如下面的aint减去了一轮批次添加数据的条数以保持字段的内容连贯.
	public String makeData(boolean hasFinishedTurnLastInvoke, boolean hasSucceedLastTurn, int lastTurnDataTotalCount){
		if(hasFinishedTurnLastInvoke && !hasSucceedLastTurn){
			aint -= lastTurnDataTotalCount;
		}
		++aint;
		Random random = new Random();
		int r = random.nextInt(20000);
		if (r == 0){
			// 1/20000的概率出现不合法的数据.
			return "";
		} else {
			// 返回一个合法的数据.
			String astring = "'" + String.valueOf(random.nextInt(99999)) + "'";
			String time = "'" + selectUtil.selectTimestamp("CREATE_TIME", "TABLE_NAME", "CUSTOMER_ID = " + aint) + "'";
			return aint + ", " + astring + ", "  + time;
		}
	}
}

package com.makedatas.sample;
import java.text.SimpleDateFormat;
import java.util.Random;

import com.makedatas.datamaker.DataMaker;
import com.makedatas.datamaker.DataMaker.DBType;

/**
 * 
 * @author ZjzMisaka
 *
 */

public class DataMakerTest {

	static int aint = 0;

	public static void main(String[] args) {
		// 参数依次为: 数据库类型, 数据库地址, 数据库接口, 数据库名, 数据库账号, 数据库密码, 数据库表名
		DataMaker dataMaker = new DataMaker(DBType.MySQL, "192.111.11.11", 3306, "database_name", "root", "root", "table_name");
		// 参数依次为: 需要的数据总数, 一轮批次添加的数据总数, 需要传递的字段名列表, 用作制造数据的方法所属的类名, 用作制造数据的方法的名字.
		// 总共往test表添加46000行数据, 每次添加40条数据.
		dataMaker.makeDatas(16000, 40, "aint, astring, adate", "com.makedatas.sample.DataMakerTest", "makeData");
	}

	// 此方法用于产生数据, 一次调用生成一条.
	// 参数含义为上轮添加数据是否成功, 若未成功可能需要把数据还原成一轮之前的状态.
	// 如下面的aint减去了一轮批次添加数据的总数以保持字段的内容连贯.
	public String makeData(boolean hasSuccessedLastInvoke){
		if(!hasSuccessedLastInvoke){
			aint -= 40;
		}
		Random random = new Random();
		int r = random.nextInt(80);
		++aint;
		if (r == 0){
			// 1/80的概率出现不合法的数据.
			return "";
		} else {
			// 返回一个合法的数据.
			String astring = "'" + String.valueOf(random.nextInt(99999)) + "'";
			String time = "'" + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(System.currentTimeMillis()) + "'";
			return aint + ", " + astring + ", "  + time;
		}
	}
}

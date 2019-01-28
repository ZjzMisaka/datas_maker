# DatasMaker
这是一个基于Java的向数据库批量上传数据的工具. 用户可以仅凭两行代码与一个制造数据的方法批量将数据上传至数据库. 目前支持有MySQL和Oracal数据库. 工程内含有示例, 含有注释. <br/>

----
### 本工具的特点:
* 支持向制定数据库的指定表分批上传指定数量的数据. <br/>
* 在上传过程中如果有某批次的数据出现错误, 将会发出提示并重新上传这一批次的数据. <br/>
* 上传总数不是单批总数的倍数也没关系. <br/>
* 当用户的代码报未知错误后自动停止程序. <br/>
### 待增加的特性: 
* 自动判断每批次的上传总数, 将效率最大化. <br/>
* 为了安全性在表名列名上添加反引号. <br/>
* 通过ssh连接数据库. <br/>
* 给制造数据方法增加一个参数: 是否完成一轮数据添加. <br/>
* 对查询语句进行封装, 因为一些用户可能需要基于其他表的查询结果添加数据. <br/>
### 使用: 
用户需要指定制造数据的方法与其所属的类, 工具会通过反射调用此方法, 方法的参数代表上一个批次的数据是否合法成功上传. <br/>
#### 构造方法: 
目前只有两种 <br/>
```Java
public DataMaker(DBType dbType, String ip, int port, String dataBaseName, String userName, String password)
```
*参数依次为: 数据库类型, 数据库地址, 数据库接口, 数据库名, 数据库账号, 数据库密码* <br/>
```Java
public DataMaker(DBType dbType, String ip, int port, String dataBaseName, String userName, String password, String tableName)
```
*参数依次为: 数据库类型, 数据库地址, 数据库接口, 数据库名, 数据库账号, 数据库密码, 数据库表名* <br/>
##### 构造方法简单示例: 
```Java
DataMaker dataMaker = new DataMaker(DBType.MySQL, "192.111.11.11", 3306, "database_name", "root", "root", "table_name");
```
#### 批量上传方法: 
```Java
public void makeDatas(int allDataTotalCount, int oneTurnDataTotalCount, String fields, String callerClassName, String methodName)
```
*参数依次为: 需要的数据总数, 一轮批次添加的数据总数, 需要传递的字段名列表, 用作制造数据的方法所属的类名, 用作制造数据的方法的名字.*
##### 批量上传方法简单示例:
```Java
dataMaker.makeDatas(7654321, 12345, "aint, astring, adate", "com.makedatas.sample.DataMakerTest", "makeData");
```
#### 制造数据方法: 
* 方法名任意. <br/>
* 方法参数为一个布尔值, 代表上一批次的数据是否合法成功上传. <br/>
* 每次调用制造一条数据, 以字符串形式作为返回值传递. 例如: "1024, 'String', '2012-03-15 10:13:56'" <br/>
* 当布尔值参数值为false, 代表上一批次中有数据不合法, 需要重新上传, 用户需要作出应对. <br/>
*例如: 每批次上传一万条数据至某表, 该表中有列字段为数字型, 按条递增. 这时候需要把这个变量减去一万, 以便保证表中这个字段的数值能连贯顺延不中断.* <br/>
* 该方法和它的所属类的访问修饰符必须为public. <br/>
* 调用完构造方法和其他初始化方法后, 应当调用: <br/>
```Java
public void makeDatas(int allDataTotalCount, int oneTurnDataTotalCount, String fields, String callerClassName, String methodName)
```
*参数依次为: 需要的数据总数, 一轮批次添加的数据总数, 需要传递的字段名列表, 用作制造数据的方法所属的类名, 用作制造数据的方法的名字.* <br/>
##### 制造数据方法简单示例: 
```Java
/*static int dataInt = 0;*/
/*DO SOMETHING*/
public String makeData(boolean hasSuccessedLastInvoke){
	if(!hasSuccessedLastInvoke){
		dataInt -= /*oneTurnDataTotalCount*/;
	}
	++dataInt;
	String dataStr = "'Hello World!'";
	return dataInt + ", " + dataStr;
}
```

完整的示例程序请看项目中[DataMakerTest.java](https://github.com/ZjzMisaka/datas_maker/blob/master/src/com/makedatas/sample/DatasMakerTest.java)

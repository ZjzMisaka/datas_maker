# DatasMaker
这是一个基于Java的向数据库批量上传数据的工具. 用户可以仅凭两行代码与一个制造数据的方法批量将数据上传至数据库. 目前支持有MySQL和Oracal数据库. 工程内含有示例, 含有注释. <br/>

----
## 本工具的特点:
* 支持向制定数据库的指定表分批上传指定数量的数据. <br/>
* 支持通过ssh连接数据库. <br/>
* 可自动判断每批次的上传条数, 将效率最大化. <br/>
* 在上传过程中如果有某批次的数据出现错误, 将会发出提示通知用户进行应对并重新上传这一批次的数据. <br/>
* 如果自填单批上传条数, 上传条数不是单批条数的倍数也没关系. <br/>
* 当用户的代码报未知错误后自动停止程序. <br/>
## 待增加的特性: 
* 增加对更多异常的捕获解释输出. <br/>
* 支持断线重连. <br/>
## 使用: 
用户需要指定制造数据的方法与其所属的类, 工具会通过反射调用此方法, 方法的参数代表上一个批次的数据是否合法成功上传. <br/>
### 构造方法: 
目前有四种 <br/>
**以下两种: 直接连接数据库**
```Java
public DatasMaker(DBType dbType, String ip, int port, String dataBaseName, String dbUserName, String dbPassword)
```
*参数依次为: 数据库类型, 数据库地址, 数据库接口, 数据库名, 数据库账号, 数据库密码* <br/>
```Java
public DatasMaker(DBType dbType, String ip, int port, String dataBaseName, String dbUserName, String dbPassword, String tableName)
```
*参数依次为: 数据库类型, 数据库地址, 数据库接口, 数据库名, 数据库账号, 数据库密码, 数据库表名* <br/>
**以下两种: 通过ssh连接数据库**
```Java
public DatasMaker(DBType dbType, String ip, int sshPort, int localPort, int dbPort, String sshUserName, String sshPassword, String dataBaseName, String dbUserName, String dbPassword)
```
*参数依次为: 数据库类型, 地址, ssh端口, 本地端口, 数据库端口, ssh用户名, ssh密码, 数据库用户名, 数据库密码* <br/>
```Java
public DatasMaker(DBType dbType, String ip, int sshPort, int localPort, int dbPort, String sshUserName, String sshPassword, String dataBaseName, String dbUserName, String dbPassword, String tableName)
```
*参数依次为: 数据库类型, 地址, ssh端口, 本地端口, 数据库端口, ssh用户名, ssh密码, 数据库用户名, 数据库密码, 表名* <br/>
#### 构造方法简单示例: 
```Java
DataMaker dataMaker = new DataMaker(DBType.MySQL, "192.111.11.11", 3306, "database_name", "root", "root", "table_name");
```
### 批量上传方法: 
**自动判断单批条数**
```Java
public void makeDatas(int allDataTotalCount, String fields, String callerClassName, String methodName)
```
*参数依次为: 需要的数据条数, 需要传递的字段名列表, 用作制造数据的方法所属的类名, 用作制造数据的方法的名字.* <br/>
**手动输入单批条数**
```Java
public void makeDatas(int allDataTotalCount, int oneTurnDataTotalCount, String fields, String callerClassName, String methodName)
```
*参数依次为: 需要的数据条数, 一轮批次添加的数据条数, 需要传递的字段名列表, 用作制造数据的方法所属的类名, 用作制造数据的方法的名字.* <br/>
#### 批量上传方法简单示例:
```Java
dataMaker.makeDatas(7654321, 12345, "aint, astring, adate", "com.makedatas.sample.DataMakerTest", "makeData");
```
### 制造数据方法: 
* 方法名任意. <br/>
* 方法参数为两个布尔值和一个int值, 第一个boolean代表一个批次的数据是否上传完成; 第二个boolean代表如果这一批次的数据上传完成, 这一批次的数据是否合法成功上传; int代表这一批次上传的条数. <br/>
* 每次调用制造一条数据, 以字符串形式作为返回值传递. 例如: "1024, 'String', '2012-03-15 10:13:56'" <br/>
* 当第二个布尔值参数值为false, 代表上一批次中有数据不合法, 需要重新上传, 用户需要作出应对. <br/>
*例如: 每批次上传一万条数据至某表, 该表中有列字段为数字型, 按条递增. 这时候需要把这个变量减去一万, 以便保证表中这个字段的数值能连贯顺延不中断.* <br/>
* 该方法和它的所属类的访问修饰符必须为public. <br/>
#### 制造数据方法简单示例: 
```Java
/*static int dataInt = 0;*/
/*DO SOMETHING*/
public String makeData(boolean hasDoneLastInvoke, boolean hasSuccessedLastInvoke, int lastTurnDataTotalCount){
	if(!hasSuccessedLastInvoke){
		dataInt -= lastTurnDataTotalCount;
	}
	++dataInt;
	String dataStr = "'Hello World!'";
	String dataTime = "'" + selectUtil.selectTimestamp("CREATE_TIME", "TABLE_NAME", "CUSTOMER_ID = " + dataInt) + "'";
	return dataInt + ", " + dataStr + ", " + dataTime;
}
```
### 查询方法: 
有时候用户添加的数据需要基于其他表的查询结果, 因此封装了查询的方法. [SelectUtil.java](https://github.com/ZjzMisaka/datas_maker/blob/master/src/com/datasmaker/utils/SelectUtil.java) <br/>
#### 连接数据库: 
**直接连接数据库**
```Java
public SelectUtil(DBType dbType, String ip, int port, String dataBaseName, String dbUserName, String dbPassword)
```
**通过ssh连接数据库**
```Java
public SelectUtil(DBType dbType, String ip, int sshPort, int localPort, int dbPort, String sshUserName, String sshPassword, String dataBaseName, String dbUserName, String dbPassword)
```
#### 进行查询
```Java
public String selectString(String resColumnName, String tableName)
public String selectString(String resColumnName, String tableName, String extra)
```
* 查询的方法以返回值类型区分为selectString, selectInt等. 第一个参数为需要得到的字段名, 第二个参数为需要查询的表名, 第三个为where后的判断条件. <br/>

完整的示例程序请看项目中[DatasMakerTest.java](https://github.com/ZjzMisaka/datas_maker/blob/master/src/com/datasmaker/sample/DatasMakerTest.java) <br/>

----
* jsch-0.1.55.jar <br/>
* mysql-connector-java-5.1.47.jar <br/>
* ojdbc6.jar <br/>

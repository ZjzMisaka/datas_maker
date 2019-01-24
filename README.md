这是一个基于Java的向数据库批量上传数据的工具. 用户可以仅凭两行代码与一个制造数据的方法批量将假数据上传至数据库. 目前支持有MySQL和Oracal数据库. 工程内含有示例, 含有注释.

本工具的特点:
1. 支持向制定数据库的指定表分批上传指定数量的数据.
2. 在上传过程中如果有某批次的数据出现错误, 将会发出提示并重新上传这一批次的数据.
3. 上传总数不是单批总数的倍数也没关系
4. 当用户的代码报未知错误后自动停止程序.

待增加的特性: 自动判断每批次的上传总数, 将效率最大化.

使用: 

用户需要指定制造数据的方法与其所属的类, 工具会通过反射调用此方法, 方法的参数代表上一个批次的数据是否合法成功上传. 
制造数据的方法: 

1. 方法参数为一个布尔值, 代表上一批次的数据是否合法成功上传. 
2. 每次调用制造一条数据, 以字符串形式作为返回值传递. 例如: "int1, 'String2', '2012-03-15 10:13:56'"
3. 当布尔值参数值为false, 代表上一批次中有数据不合法, 需要重新上传, 用户需要作出应对. 

	例如: 每批次上传一万条数据, 其中有条数据为数字型, 按条递增. 这时候需要把这个变量减去一万, 以便保证表中这个字段的数值能连贯顺延不中断. 
4. 该方法和它的所属类的访问修饰符必须为public.
5. 构造方法: 目前只有两种

	(1). public DataMaker(DBType dbType, String ip, int port, String dataBaseName, String userName, String password)
	
		参数依次为: 数据库类型, 数据库地址, 数据库接口, 数据库名, 数据库账号, 数据库密码
		
	(2). public DataMaker(DBType dbType, String ip, int port, String dataBaseName, String userName, String password, String tableName)
	
		参数依次为: 数据库类型, 数据库地址, 数据库接口, 数据库名, 数据库账号, 数据库密码, 数据库表名
		
6. 调用完构造方法和其他初始化方法后, 应当调用

	(1). public void makeDatas(int allDataTotalCount, int oneTurnDataTotalCount, String fields, String callerClassName, String methodName)
	
		参数依次为: 需要的数据总数, 一轮批次添加的数据总数, 需要传递的字段名列表, 用作制造数据的方法所属的类名, 用作制造数据的方法的名字.
7. 最后, 需要写出制造数据的方法. 

	例如: 
	
	public String makeData(boolean hasSuccessedLastInvoke){
	
		if(!hasSuccessedLastInvoke){
		
			// TODO
			
		}
		
		int dataInt = 1024;
		
		String dataStr = "'Hello World!'";
		
		return "int + ", " + dataStr"
		
	}
	

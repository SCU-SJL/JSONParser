# JSONParser
一个用于完成普通 Java Bean 和 JSON 字符串以及 JsonObject 之间相互转化的工具类  
  
      
## 原理分析
### 一. Java Bean -> Json String
* 1. 使用反射读取当前 bean 的字段，对每个字段进行解析处理
* 2. 如果当前字段为非 Object 类型，则直接提取字段值
* 3. 如果当前字段是 Object 类型，则对其进行递归处理，重复上述步骤  
### 二. Json String -> JsonObject  
* 1. 划分出以下几个 Token 分别处理：  
对象开始："{"  
对象结束："}"  
数组开始："["  
数组结束："]"  
逗号：","  
引号：":"
字符串："\""  
数字  
JSON结束  
布尔值  
null值  
* 2. 依据当前的 Token 进行解析，例如如果 Token 是数字，那么就读取一个数字放到 JsonObject  
如果当前的 Token 是对象开始 Token，就重复整个过程进行递归解析等等
* 3. 根据当前的 Token 判断下一个期望出现的 Token(可能有多个)
* 4. 重复上述步骤直到遍历完 Json String  
### 三. JsonObject -> Java Bean  
使用反射技术，将 JsonObject 中的值匹配到 bean 的每个字段并赋值  
注意此处需要 bean 拥有相应的构造器和 setter 函数
  
### 补充说明
该解析器能够解析各类一维数组以及嵌套的集合类，例如：List<List<Integer>>，Set<Pojo>等  
但对于 Map 的反序列化中，限制了 Map 的 Key 只能为包装类或 String  
以及对于反序列过程中，暂时只支持声明为接口类型的集合类，例如：private List<Integer> list，但不支持 private ArrayList<Integer> list等

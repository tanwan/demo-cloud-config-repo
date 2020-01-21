# logback [官方文档](http://logback.qos.ch/manual/index.html)

## 配置文件

文件名可以为logback.groovy logback-test.xml logback.xml 文件查找顺序就是按照以上的顺序查找，一个没找到就查找下一个 文件要放在classpath下 以下代码可以打印出logback使用的是哪个配置文件

```java
LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
   StatusPrinter.print(lc);
```

## 配置说明

### configuration
```xml
<configuration scan="true" scanPeriod="60 seconds" debug="false">
  <appender/>
  <logger/>
  <root/>
  <property/>
  <timestamp/>
</configuration>
```
1. scan: 当此属性设置为true时，配置文件如果发生改变，将会被重新加载，默认值为true。
2. scanPeriod: 设置监测配置文件是否有修改的时间间隔，如果没有给出时间单位，默认单位是毫秒。当scan为true时，此属性生效。默认的时间间隔为1分钟。
3. debug: 当此属性设置为true时，将打印出logback内部日志信息，实时查看logback运行状态。默认值为false。

### logger
用来设置某一个包或者具体的某一个类的日志打印级别、以及指定 <appender>  
如果<logger>没有指定<appender-ref>那么这个logger不打印log
```xml
<logger name="com.lzy" level="error" additivity="true">
    <appender-ref ref="STDOUT"/>
</logger>
```
1. name: 用来指定受此loger约束的某一个包或者具体的某一个类。
2. level: 用来设置打印级别，大小写无关：ALL,TRACE,DEBUG,INFO,WARN,ERROR和OFF，还有一个特殊值INHERITED或者同义词NULL，代表强制执行上级的级别。 如果未设置此属性，那么当前loger将会继承上级的级别。
3. addtivity: 是否向上级loger传递打印信息。默认是true。

### root
它是根logger
```xml
<root level="debug">
    <appender-ref ref="STDOUT" />
</root>
```

## appender

### ConsoleAppender
输出到控制台  
```xml
<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
        <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
     </encoder>
      <target>System.out</target>
    </appender>
```

1. target: System.out 或者 System.err ,默认 System.out

### FileAppender
输入到文件
```xml
<appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <file>testFile.log</file>
        <append>true</append>
        <encoder>
            <pattern>%-4relative [%thread] %-5level %logger{35} - %msg%n</pattern>
        </encoder>
        <prudent>false</prudent>
    </appender>
```
1. file:被写入的文件名，可以是相对目录，也可以是绝对目录，如果上级目录不存在会自动创建，没有默认值。
2. append:如果是 true，日志被追加到文件结尾，如果是 false，清空现存文件，默认是true
3. prudent:如果是 true，日志会被安全的写入文件，即使其他的FileAppender也在向此文件做写入操作，效率低，默认是 false

### RollingFileAppender
滚动记录文件，先将日志记录到指定文件，当符合某个条件时，将日志记录到其他文件
```xml
<appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
      <fileNamePattern>logFile.%d{yyyy-MM-dd}.log</fileNamePattern>
      <maxHistory>30</maxHistory>
    </rollingPolicy>
    <encoder>
      <pattern>%-4relative [%thread] %-5level %logger{35} - %msg%n</pattern>
    </encoder>
  </appender>
```
1. file：被写入的文件名，可以是相对目录，也可以是绝对目录，如果上级目录不存在会自动创建，没有默认值。
2. append：如果是 true，日志被追加到文件结尾，如果是 false，清空现存文件，默认是true。
3. encoder：对记录事件进行格式化
4. rollingPolicy:当发生滚动时，决定 RollingFileAppender 的行为，涉及文件移动和重命名。
5. triggeringPolicy: 告知 RollingFileAppender 合适激活滚动。
6. prudent：当为true时，不支持FixedWindowRollingPolicy。支持TimeBasedRollingPolicy，但是有两个限制，1不支持也不允许文件压缩，2不能设置file属性，必须留空。

#### rollingPolicy
##### TimeBasedRollingPolicy
最常用的滚动策略，它根据时间来制定滚动策略，既负责滚动也负责触发滚动  
```xml
<rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
  <fileNamePattern>logFile.%d{yyyy-MM-dd}.log</fileNamePattern>
  <maxHistory>30</maxHistory>
</rollingPolicy>
```
1. fileNamePattern:必要节点，包含文件名及"%d"转换符,"%d"可以包含一个java.text.SimpleDateFormat指定的时间格式，如：%d{yyyy-MM}。如果直接使用 %d，默认格式是yyyy-MM-dd
2. maxHistory:可选节点，控制保留的归档文件的最大数量，超出数量就删除旧文件。删除之前的旧文件。

##### FixedWindowRollingPolicy
根据固定窗口算法重命名文件的滚动策略  
```xml
<rollingPolicy class="ch.qos.logback.core.rolling.FixedWindowRollingPolicy">
    <fileNamePattern>tests.%i.log.zip</fileNamePattern>
    <minIndex>1</minIndex>
    <maxIndex>3</maxIndex>
</rollingPolicy>
```
1. minIndex:窗口索引最小值
2. maxIndex:窗口索引最大值，当用户指定的窗口过大时，会自动将窗口设置为12。
3. fileNamePattern:必须包含"%i"例如，假设最小值和最大值分别为1和2，命名模式为 mylog%i.log,会产生归档文件mylog1.log和mylog2.log

#### triggeringPolicy
##### SizeBasedTriggeringPolicy
查看当前活动文件的大小，如果超过指定大小会告知RollingFileAppender 触发当前活动文件滚动
```xml
  <triggeringPolicy class="ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy">
  <maxFileSize>5MB</maxFileSize>
  </triggeringPolicy>
```
1. maxFileSize:这是活动文件的大小，默认值是10MB。

### 不常用的Appender
SocketAppender、SMTPAppender、DBAppender、SyslogAppender、SiftingAppender 参考官方文档

### encoder [reference](https://logback.qos.ch/manual/layouts.html)
```xml
<encoder>
   <pattern>%-4relative [%thread] %-5level %logger{35} - %msg%n</pattern>
</encoder>
```
负责两件事，一是把日志信息转换成字节数组，二是把字节数组写入到输出流.目前PatternLayoutEncoder是唯一有用的且默认的encoder
<pattern>节点，用来设置日志的输入格式。使用"%"加"转换符"方式，如果要输出"%"，则必须用"\\"对"%"进行转义。
转换符:
- %logger{length} 输出logger名 length logger的长度 不加则全输出
- %date{pattern} 如:HH:mm:ss.SSS
- %msg 应用程序提供的信息
- %thread 输出产生日志的线程名
- %relative 输出从程序启动到创建日志记录的时间，单位是毫秒
- %level 输出日志级别
- %caller 输出触发打印日志的事件(很像堆栈)
- %line 输出打印日志的行号
- %method 输出打印日志的方法

修饰符: 可选的格式修饰符位于"%"和转换符之间
格式 min.max .前面表示min,后面表示max 如 %-20.30logger, %.30logger
- -：左对齐
- min:最小字符宽度,如果字符小于最小宽度，则左填充或右填充，默认是左填充（即右对齐），填充符为空格
- max:最大字符宽度,如果字符大于最大宽度，则从前面截断。点符号"."后面加减号"-"再加数字，表示从尾部截断

#### color
使得%color() 如: %blue(%logger{36})
- black boldblack
- red boldRed
- green boldGreen
- yellow boldYellow
- blue boldBlue
- magent boldMagent
- cyan boldCyan
- white boldWhite
- gray boldGray

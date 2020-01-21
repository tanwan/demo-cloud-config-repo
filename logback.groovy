//For more information on configuration files in Groovy
//please see http://logback.qos.ch/manual/groovy.html
//For assistance related to this tool or configuration files
//in general, please contact the logback user mailing list at
//http://qos.ch/mailman/listinfo/logback-user

import ch.qos.logback.classic.boolex.JaninoEventEvaluator
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.filter.LevelFilter
import ch.qos.logback.classic.filter.ThresholdFilter
import ch.qos.logback.core.filter.EvaluatorFilter

import static ch.qos.logback.classic.Level.*
import static ch.qos.logback.core.spi.FilterReply.*

appender("STDOUT", ConsoleAppender) {
    //See http://logback.qos.ch/manual/filters.html
    filter(EvaluatorFilter) {
        //需要添加janino的依赖
        evaluator(JaninoEventEvaluator) {
            expression = 'return logger.contains("AutoConfigurationReportLoggingInitializer");'
        }
        //不符合就执行下一个过滤器
        onMismatch = NEUTRAL
        //符合条件就应用
        onMatch = ACCEPT
    }
    //LevelFilter(只输出指定的级别) ThresholdFilter(输出以上的级别)
    filter(LevelFilter) {
        level=DEBUG
        onMismatch = NEUTRAL
        onMatch = ACCEPT
    }
    filter(LevelFilter) {
        level=INFO
        onMismatch = DENY
        onMatch = ACCEPT
    }
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} %green(%-5level)  %magenta([%thread])  %blue(%logger{36}#%method) - %boldWhite(%msg%n)"
    }
}
//只输出ERROR级别的日志
appender("STDERR", ConsoleAppender) {
    //See http://logback.qos.ch/manual/filters.html
    filter(LevelFilter){
        level = ERROR
        onMatch = ACCEPT
        onMismatch = DENY
    }
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} %red(%-5level)  %magenta([%thread])  %blue(%logger{36}#%method) - %boldRed(%msg%n)"
    }
}
//只输出WARN级别的日志
appender("STDWARN", ConsoleAppender) {
    //See http://logback.qos.ch/manual/filters.html
    filter(LevelFilter){
        level = WARN
        onMatch = ACCEPT
        onMismatch = DENY
    }
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} %yellow(%-5level)  %magenta([%thread])  %blue(%logger{36}#%method) - %boldYellow(%msg%n)"
    }
}
//输出到文件
appender("FILE", RollingFileAppender) {
    filter(ThresholdFilter){
        level = ERROR
    }
    file="./log/log.log"
    encoder(PatternLayoutEncoder) {
        pattern = "%d{HH:mm:ss.SSS} %-5level  [%thread]  %logger{36}#%method - %msg%n"
    }
    rollingPolicy(TimeBasedRollingPolicy) {
        FileNamePattern = "./log/log-%d{yyyy-MM-dd}.log"
        maxHistory=30
    }
}
logger("com.lzy", DEBUG,["STDOUT","STDERR","STDWARN","FILE"],false)
root(INFO, ["STDOUT","STDERR","STDWARN","FILE"])

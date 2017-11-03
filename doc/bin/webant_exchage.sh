#!/bin/bash

source ~/.bash_profile

export JAVA_HOME=jdk
export JRE_HOME=$JAVA_HOME/jre
export CLASSPATH=$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar:$JRE_HOME/lib:.:$CLASSPATH

JAVA=$JAVA_HOME/bin/java


JAVA_OPTS="$JAVA_OPTS -server -Xms256m -Xmx512m -Xmn150m -XX:MaxPermSize=128m"
#performance Options
JAVA_OPTS="$JAVA_OPTS -XX:+AggressiveOpts"
JAVA_OPTS="$JAVA_OPTS -XX:+UseBiasedLocking"
JAVA_OPTS="$JAVA_OPTS -XX:+UseFastAccessorMethods"
JAVA_OPTS="$JAVA_OPTS -XX:+DisableExplicitGC"
JAVA_OPTS="$JAVA_OPTS -XX:+UseParNewGC"
JAVA_OPTS="$JAVA_OPTS -XX:+UseConcMarkSweepGC"
JAVA_OPTS="$JAVA_OPTS -XX:+CMSParallelRemarkEnabled"
JAVA_OPTS="$JAVA_OPTS -XX:+UseCMSCompactAtFullCollection"
JAVA_OPTS="$JAVA_OPTS -XX:+UseCMSInitiatingOccupancyOnly"
JAVA_OPTS="$JAVA_OPTS -XX:CMSInitiatingOccupancyFraction=75"
JAVA_OPTS="$JAVE_OPTS -XX:+HeapDumpOnOutOfMemoryError"
JAVA_OPTS="$JAVA_OPTS -XX:HeapDumpPath=../dump_files/"

APP_DIR="."
CONF_DIR="$APP_DIR/conf"
CFG_NAME="$CONF_DIR/global.properties"
TIMEZONE="-Dfile.encoding=UTF8 -Duser.timezone=GMT+08"
LIB_DIR=$APP_DIR/lib
LIB_JARS=`ls -r $LIB_DIR|grep .jar|awk '{print "'$LIB_DIR'/"$0}'|tr "\n" ":"`
nohup $JAVA $JAVA_OPTS $TIMEZONE "-DLOG_FILE_DIR=logs/WorkenXpathServer_1" -cp "$CONF_DIR:$APP_DIR/lib/webant-xpath-0.0.1.jar:$LIB_JARS" cn.inveno.worken.WorkenXpathServer $CFG_NAME > /dev/null 2>&1  &
nohup $JAVA $JAVA_OPTS $TIMEZONE "-DLOG_FILE_DIR=logs/WorkenXpathServer_2" -cp "$CONF_DIR:$APP_DIR/lib/webant-xpath-0.0.1.jar:$LIB_JARS" cn.inveno.worken.WorkenXpathServer $CFG_NAME > /dev/null 2>&1  &
nohup $JAVA $JAVA_OPTS $TIMEZONE "-DLOG_FILE_DIR=logs/WorkenXpathServer_3" -cp "$CONF_DIR:$APP_DIR/lib/webant-xpath-0.0.1.jar:$LIB_JARS" cn.inveno.worken.WorkenXpathServer $CFG_NAME js > /dev/null 2>&1  &
#$JAVA $JAVA_OPTS -cp "$CONF_DIR:$APP_DIR/lib/webant-xpath-0.0.1.jar:$LIB_JARS" cn.inveno.worken.WorkenXpathServer $CFG_NAME


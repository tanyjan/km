#!/bin/bash

TASK_NUM=1
count=$(ps aux|grep WorkeDetailTest|grep -v grep |wc -l)
for ((i=1;i<=$(($TASK_NUM-$count));i++));do
    $(ps aux|grep '/data/webant-xpath/phantomjs-2.1.1-linux-x86_64/bin/phantomjs'|grep -v grep|awk '{print $2}'|xargs kill)
    echo $count
    echo "WorkeDetailTest"
    cd /data/webant-xpath
    sh bin/detail_test_task.sh
    echo $i
done

TASK_NUM=2
count=$(ps aux|grep WorkenListTestServer|grep -v grep |wc -l)
for ((i=1;i<=$(($TASK_NUM-$count));i++));do
    echo $count
    echo "WorkenListTestServer"
    cd /data/webant-xpath
    sh bin/webant_exchage_test.sh
    echo $i
done

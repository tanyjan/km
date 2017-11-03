#!/bin/bash
# Danger: 务必修改对应的Monitor! 不然可能会不断重启

DETAIL_TASK_NUM=2 # js + normal
LIST_TASK_NUM=3 # js + normal * 2

detail_count=$(ps aux|grep WorkeDetailXpathServer|grep -v grep|wc -l)
list_count=$(ps aux|grep WorkenXpathServer|grep -v grep |wc -l)

echo "list_count: $list_count detail_count: $detail_count"
if [ $detail_count -lt $DETAIL_TASK_NUM -o $list_count -lt $LIST_TASK_NUM ]; then
    $(ps aux|grep '/data/webant-xpath/phantomjs-2.1.1-linux-x86_64/bin/phantomjs'|grep -v grep|awk '{print $2}'|xargs kill)
    cd /data/webant-xpath
    sh bin/work_server_start.sh
fi


#count=$(ps aux|grep WorkeDetailXpathServer|grep -v grep|wc -l)
#if [ 0 == $count ]; then
#    $(ps aux|grep '/data/webant-xpath/phantomjs-2.1.1-linux-x86_64/bin/phantomjs'|grep -v grep|awk '{print $2}'|xargs kill)
#    echo $count
#    cd /data/webant-xpath
#    sh bin/detail_task.sh
#fi
#
#TASK_NUM=3
#count=$(ps aux|grep WorkenXpathServer|grep -v grep |wc -l)
#for ((i=1;i<=$(($TASK_NUM-$count));i++));do
#    echo $count
#    cd /data/webant-xpath
#    sh bin/webant_exchage.sh
#    echo $i
#done

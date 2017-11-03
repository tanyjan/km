#!/bin/bash

source ~/.bash_profile

cd /data/webant-xpath

ip=$(ifconfig|grep -v '127.0.0.1'|grep "inet addr"|cut -d: -f2|cut -d" " -f1)


$(ps aux|grep '/data/webant-xpath/phantomjs-2.1.1-linux-x86_64/bin/phantomjs'|grep -v grep|awk '{print $2}'|xargs kill)
if [ '172.31.22.64' == $ip ]
then
    $(ps axu|grep -E 'WorkeDetailTest|WorkenListTestServer'|grep -v grep|awk '{print $2}'|xargs kill)
    sh bin/webant_exchage_test.sh
    sh bin/detail_test_task.sh
else
    $(ps axu|grep -E 'WorkenXpathServer|WorkeDetailXpathServer'|grep -v grep|awk '{print $2}'|xargs kill)

    sh bin/webant_exchage.sh
    sh bin/detail_task.sh
fi

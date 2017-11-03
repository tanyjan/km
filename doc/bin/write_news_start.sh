#! /bin/sh

source ~/.bash_profile

sh_str="./bin/write_news.sh"
host_dir="/data/webant-xpath"
proc_name="WriteHotNewsServer"

pid=0

proc_num()
{
    num=$(ps -ef | grep $proc_name | grep -v 'grep'| wc -l)
    return $num
}

proc_id()
{
    pid=`ps -ef | grep $proc_name | grep -v grep | awk '{print $2}'`
}

proc_num
number=$?
if [ $number -eq 0 ]
then
    cd $host_dir; $sh_str
    proc_id
    echo ${pid}, `date`
else
    echo "proc exist."
    $(ps -ef | grep $proc_name | grep -v 'grep'|awk '{print $2}'|xargs kill)
    cd $host_dir; $sh_str
fi

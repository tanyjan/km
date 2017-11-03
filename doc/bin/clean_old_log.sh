#!/bin/bash

source ~/.bash_profile

find /data/webant-*/logs/ -mtime +3 -name "*.log.*" -exec rm -rf {} \;

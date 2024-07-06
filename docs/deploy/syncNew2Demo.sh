#!/bin/bash
######################################
####  从备份sql中恢复最新数据
######################################
script_dir=$(dirname "$(readlink -f "$0")")
echo $script_dir

SQL_FILE=ls -t ../databases/*.sql | head -1

ssh -NL 3307:localhost:3306 usa &
sleep 3
mysql -h 127.0.0.1 -P 3307 -u soho -p123456 < $SQL_FILE
echo "数据库同步到Demo环境成功"
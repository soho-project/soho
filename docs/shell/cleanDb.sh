
scriptDir=$(cd $(dirname $0);pwd)
cd $scriptDir
cd ../databases

for item in `ls ./*.sql`; do
  rm -f tmp.sql
  cat $item |grep -v 'INSERT INTO `pay_info`' \
   |grep -v 'INSERT INTO `pay_order`' \
   |grep -v 'INSERT INTO `chat_session_message`' \
   |grep -v 'INSERT INTO `chat_session_message_user`' \
   |grep -v 'INSERT INTO `admin_user_login_log`' \
   |grep -v 'INSERT INTO `admin_operation_log`' \
   > tmp.sql
  rm -f $item
  mv tmp.sql $item
done
rm -f tmp.sql


# cat soho-admin-2022_12_19_00_22_06.sql |grep -v 'INSERT INTO `pay_info`' > ./test.sql

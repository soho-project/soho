#!/bin/bash
######################################
####  从备份sql中恢复最新数据
######################################

oldDir=$(pwd)
script_dir=$(dirname "$(readlink -f "$0")")
echo "脚本目录: $script_dir"
cd $script_dir
echo "原始目录: $oldDir"

# 数据库连接信息
REMOTE_HOST="usa"
LOCAL_PORT=3307
REMOTE_PORT=3306
DB_USER="soho"
DB_PASSWORD="123456"

# 备份文件目录
BACKUP_DIR="../databases"

# 方式1：从配置文件读取数据库列表
CONFIG_FILE="$script_dir/databases.conf"

# 方式2：自动从备份文件名中提取数据库名
# 如果你的备份文件命名格式是"数据库名-日期时间.sql"
AUTO_DETECT_DBS=true

# 杀死指定端口的进程
kill_port() {
    local port="$1"

    if [ -z "$port" ]; then
        echo "错误：请指定端口号"
        echo "用法: $0 <端口号>"
        exit 1
    fi

    # 检查端口是否被占用
    local pid=$(lsof -ti:$port)

    if [ -z "$pid" ]; then
        echo "端口 $port 没有被任何进程占用"
        return 0
    fi

    echo "找到占用端口 $port 的进程: $pid"

    # 显示进程详细信息
    echo "进程详细信息:"
    ps -p $pid -o pid,ppid,user,%cpu,%mem,cmd

    # 询问是否杀死进程
    echo -n "是否杀死这些进程？ (y/n): "
    read -r confirm

    if [[ "$confirm" == "y" || "$confirm" == "Y" ]]; then
        echo "正在杀死进程 $pid..."
        kill -9 $pid 2>/dev/null

        # 检查是否杀死成功
        sleep 1
        if lsof -ti:$port >/dev/null 2>&1; then
            echo "警告：未能完全杀死进程，尝试强制终止..."
            kill -9 $(lsof -ti:$port) 2>/dev/null
        fi

        echo "端口 $port 已释放"
    else
        echo "操作取消"
    fi
}

# 建立SSH隧道
#echo "正在建立SSH隧道到 $REMOTE_HOST..."
kill_port $LOCAL_PORT
ssh -f -N -L ${LOCAL_PORT}:localhost:${REMOTE_PORT} $REMOTE_HOST
#TUNNEL_PID=$!
#sleep 10

# 检查隧道是否建立成功
#echo "pid: ${TUNNEL_PID}"
#if ! ps -p $TUNNEL_PID > /dev/null; then
#    echo "错误：SSH隧道建立失败"
#    exit 1
#fi

# 确保脚本退出时关闭隧道
cleanup() {
    echo "正在关闭SSH隧道..."
    kill $TUNNEL_PID 2>/dev/null
    cd "$oldDir"
}
trap cleanup EXIT

# 函数：恢复单个数据库
restore_database() {
    local db_name="$1"
    local backup_file="$2"

    echo "正在恢复数据库: $db_name"
    echo "使用备份文件: $backup_file"

    # 检查备份文件是否存在
    if [ ! -f "$backup_file" ]; then
        echo "错误：备份文件不存在: $backup_file"
        return 1
    fi

    # 检查文件是否被压缩
    local actual_file="$backup_file"
    local use_gzip=false

    if [[ "$backup_file" == *.gz ]]; then
        echo "检测到压缩文件，使用gzip解压..."
        use_gzip=true
    fi

    # 恢复数据库
    if [ "$use_gzip" = true ]; then
        # 解压并导入
        if gunzip -c "$backup_file" | mysql -h 127.0.0.1 -P $LOCAL_PORT -u $DB_USER -p$DB_PASSWORD $db_name; then
            echo "✓ 数据库 $db_name 恢复成功 (从压缩文件)"
            return 0
        else
            echo "✗ 数据库 $db_name 恢复失败"
            return 1
        fi
    else
        # 直接导入
        if mysql -h 127.0.0.1 -P $LOCAL_PORT -u $DB_USER -p$DB_PASSWORD $db_name < "$backup_file"; then
            echo "✓ 数据库 $db_name 恢复成功"
            return 0
        else
            echo "✗ 数据库 $db_name 恢复失败"
            return 1
        fi
    fi
}

# 函数：获取数据库的最新备份文件
get_latest_backup() {
    local db_name="$1"

    # 先查找压缩文件
    local compressed_file=$(ls -t ${BACKUP_DIR}/${db_name}-*.sql.gz 2>/dev/null | head -1)

    # 再查找未压缩文件
    local uncompressed_file=$(ls -t ${BACKUP_DIR}/${db_name}-*.sql 2>/dev/null | head -1)

    # 选择最新的文件（通过比较修改时间）
    if [ -n "$compressed_file" ] && [ -n "$uncompressed_file" ]; then
        # 两个文件都存在，比较哪个更新
        if [ "$compressed_file" -nt "$uncompressed_file" ]; then
            echo "$compressed_file"
        else
            echo "$uncompressed_file"
        fi
    elif [ -n "$compressed_file" ]; then
        echo "$compressed_file"
    elif [ -n "$uncompressed_file" ]; then
        echo "$uncompressed_file"
    else
        echo ""
    fi
}

# 主恢复逻辑
echo "================================"
echo "开始恢复数据库"

# 从配置文件读取数据库列表
if [ -f "$CONFIG_FILE" ]; then
    echo "从配置文件 $CONFIG_FILE 读取数据库列表"
    DATABASES=()
    while IFS= read -r line || [[ -n "$line" ]]; do
        # 跳过空行和注释行
        if [[ -n "$line" && ! "$line" =~ ^[[:space:]]*# ]]; then
            DATABASES+=("$line")
        fi
    done < "$CONFIG_FILE"

    echo "找到 ${#DATABASES[@]} 个数据库需要恢复"

    # 询问是否继续
    echo "是否要恢复以上所有数据库？ (y/n)"
    read -r confirm
    if [[ "$confirm" != "y" && "$confirm" != "Y" ]]; then
        echo "操作取消"
        exit 0
    fi

    # 恢复每个数据库
    success_count=0
    fail_count=0

    for DB_NAME in "${DATABASES[@]}"
    do
        echo "------------------------"
        LATEST_BACKUP=$(get_latest_backup "$DB_NAME")

        if [ -z "$LATEST_BACKUP" ]; then
            echo "警告：数据库 $DB_NAME 没有找到备份文件"
            fail_count=$((fail_count + 1))
            continue
        fi

        if restore_database "$DB_NAME" "$LATEST_BACKUP"; then
            success_count=$((success_count + 1))
        else
            fail_count=$((fail_count + 1))
        fi
    done

    echo "================================"
    echo "恢复完成！"
    echo "成功: $success_count, 失败: $fail_count"

elif [ "$AUTO_DETECT_DBS" = true ]; then
    # 自动检测备份文件中的数据库
    echo "自动检测备份目录中的数据库..."

    # 获取所有备份文件中的数据库名
    declare -A db_map

    # 查找所有备份文件
    for backup_file in ${BACKUP_DIR}/*.sql ${BACKUP_DIR}/*.sql.gz; do
        if [ -f "$backup_file" ]; then
            # 从文件名中提取数据库名（假设格式：数据库名-日期.sql）
            filename=$(basename "$backup_file")
            # 去除扩展名
            filename_no_ext="${filename%.*}"
            # 如果是.gz文件，再去除.gz
            filename_no_ext="${filename_no_ext%.*}"
            # 提取数据库名（最后一个-之前的部分）
            db_name="${filename_no_ext%-*}"

            if [ -n "$db_name" ]; then
                db_map["$db_name"]=1
            fi
        fi
    done

    if [ ${#db_map[@]} -eq 0 ]; then
        echo "错误：备份目录中没有找到备份文件"
        exit 1
    fi

    # 显示找到的数据库
    echo "找到以下数据库的备份文件:"
    for db in "${!db_map[@]}"; do
        echo "  - $db"
    done

    # 询问是否继续
    echo "是否要恢复以上所有数据库？ (y/n)"
    read -r confirm
    if [[ "$confirm" != "y" && "$confirm" != "Y" ]]; then
        echo "操作取消"
        exit 0
    fi

    # 恢复每个数据库
    success_count=0
    fail_count=0

    for DB_NAME in "${!db_map[@]}"; do
        echo "------------------------"
        LATEST_BACKUP=$(get_latest_backup "$DB_NAME")

        if [ -z "$LATEST_BACKUP" ]; then
            echo "警告：数据库 $DB_NAME 没有找到备份文件"
            fail_count=$((fail_count + 1))
            continue
        fi

        if restore_database "$DB_NAME" "$LATEST_BACKUP"; then
            success_count=$((success_count + 1))
        else
            fail_count=$((fail_count + 1))
        fi
    done

    echo "================================"
    echo "恢复完成！"
    echo "成功: $success_count, 失败: $fail_count"

else
    # 单个数据库恢复（兼容原脚本）
    echo "使用单个数据库恢复模式"
    SQL_FILE=$(ls -t ${BACKUP_DIR}/*.sql ${BACKUP_DIR}/*.sql.gz 2>/dev/null | head -1)

    if [ -z "$SQL_FILE" ]; then
        echo "错误：备份目录中没有找到备份文件"
        exit 1
    fi

    echo "找到最新备份文件: $SQL_FILE"

    # 从文件名中提取数据库名
    filename=$(basename "$SQL_FILE")
    filename_no_ext="${filename%.*}"
    filename_no_ext="${filename_no_ext%.*}"
    DB_NAME="${filename_no_ext%-*}"

    if [ -z "$DB_NAME" ]; then
        DB_NAME="soho"  # 默认数据库名
    fi

    echo "将恢复到数据库: $DB_NAME"

    # 询问是否继续
    echo "是否要恢复数据库 $DB_NAME？ (y/n)"
    read -r confirm
    if [[ "$confirm" != "y" && "$confirm" != "Y" ]]; then
        echo "操作取消"
        exit 0
    fi

    if restore_database "$DB_NAME" "$SQL_FILE"; then
        echo "================================"
        echo "数据库 $DB_NAME 恢复成功！"
    else
        echo "================================"
        echo "数据库 $DB_NAME 恢复失败！"
        exit 1
    fi
fi

echo "数据库同步到Demo环境成功"
@echo off
chcp 65001 >nul
setlocal EnableExtensions EnableDelayedExpansion

REM ====== 配置区（可用环境变量覆盖）======
if "%DB_HOST%"=="" set "DB_HOST=192.168.0.101"
if "%DB_USER%"=="" set "DB_USER=dev"
if "%DB_PASSWORD%"=="" set "DB_PASSWORD=dev"
if "%KEEP_COUNT%"=="" set "KEEP_COUNT=5"

REM script_dir = 当前 bat 所在目录（末尾不含 \）
set "SCRIPT_DIR=%~dp0"
if "%SCRIPT_DIR:~-1%"=="\" set "SCRIPT_DIR=%SCRIPT_DIR:~0,-1%"

REM BACKUP_DIR 默认：脚本目录上一级\databases
if "%BACKUP_DIR%"=="" set "BACKUP_DIR=%SCRIPT_DIR%\..\databases"

set "CONFIG_FILE=%SCRIPT_DIR%\databases.conf"

REM 创建备份目录
if not exist "%BACKUP_DIR%" mkdir "%BACKUP_DIR%"

REM 获取当前时间戳：YYYY_MM_DD_HH_MM_SS
call :GetTimestamp CURRENT_DATE

REM ====== 读取数据库列表 ======
set "DB_COUNT=0"
if exist "%CONFIG_FILE%" (
  echo 从配置文件读取数据库列表: "%CONFIG_FILE%"
  for /f "usebackq delims=" %%L in ("%CONFIG_FILE%") do (
    set "LINE=%%L"
    REM 去掉首尾空白（简单处理：先去前导空格；尾部空格一般不影响库名）
    call :LTrim LINE

    if not "!LINE!"=="" (
      REM 忽略注释行（#开头，允许前面有空格已被 LTrim 去掉）
      if not "!LINE:~0,1!"=="#" (
        set /a DB_COUNT+=1
        set "DB!DB_COUNT!=!LINE!"
      )
    )
  )
  echo 从配置文件读取到 !DB_COUNT! 个数据库
) else (
  echo 配置文件 "%CONFIG_FILE%" 不存在，使用默认数据库列表
  set "DB_COUNT=1"
  set "DB1=dev"
)

echo.

REM ====== 逐库备份 ======
for /L %%I in (1,1,!DB_COUNT!) do (
  set "DB_NAME=!DB%%I!"
  echo 正在备份数据库: !DB_NAME!

  set "BACKUP_FILE=%BACKUP_DIR%\!DB_NAME!-!CURRENT_DATE!.sql"

  mysqldump --set-gtid-purged=OFF --column-statistics=0 -h "%DB_HOST%" -u "%DB_USER%" -p"%DB_PASSWORD%" "!DB_NAME!" > "!BACKUP_FILE!"
  if !errorlevel! equ 0 (
    echo ✓ 数据库 !DB_NAME! 备份成功
    echo   文件: !BACKUP_FILE!
    call :FileSizeKB "!BACKUP_FILE!" SIZE_KB
    echo   大小: !SIZE_KB! KB
  ) else (
    echo ✗ 错误: 数据库 !DB_NAME! 备份失败!
    echo   正在检查数据库是否存在...
    mysql -h "%DB_HOST%" -u "%DB_USER%" -p"%DB_PASSWORD%" -e "USE !DB_NAME!" 1>nul 2>nul
    if !errorlevel! equ 0 (
      echo   数据库存在，但备份失败，请检查权限或磁盘空间
    ) else (
      echo   数据库不存在或无法连接
    )
    REM 如果 dump 失败，可能留下空文件/半截文件，这里不强制删除，按需可取消注释：
    REM del /q "!BACKUP_FILE!" 2>nul
  )

  REM 只保留最近 KEEP_COUNT 份（按文件名中时间戳倒序）
  call :CleanupPrefix "!DB_NAME!" "%KEEP_COUNT%"

  REM legacy_prefix：把 _ 替换成 -
  set "LEGACY=!DB_NAME:_=-!"
  if not "!LEGACY!"=="!DB_NAME!" (
    call :CleanupPrefix "!LEGACY!" "%KEEP_COUNT%"
  )

  echo.
)

echo 备份完成!
echo 备份文件保存在: %BACKUP_DIR%
endlocal
exit /b 0


REM ================== 子程序区 ==================

:GetTimestamp
for /f "usebackq delims=" %%t in (`
  powershell -NoProfile -Command "Get-Date -Format 'yyyy_MM_dd_HH_mm_ss'"
`) do (
  set "%~1=%%t"
  exit /b 0
)
exit /b 1

:_ts_ok
REM dt=YYYYMMDDhhmmss...
set "YYYY=!dt:~0,4!"
set "MM=!dt:~4,2!"
set "DD=!dt:~6,2!"
set "hh=!dt:~8,2!"
set "mm=!dt:~10,2!"
set "ss=!dt:~12,2!"
set "%~1=!YYYY!_!MM!_!DD!_!hh!_!mm!_!ss!"
exit /b 0

:LTrim
REM 去除变量 %1 的前导空格
set "s=!%~1!"
for /f "tokens=* delims= " %%a in ("!s!") do set "s=%%a"
set "%~1=!s!"
exit /b 0

:FileSizeKB
REM %1=文件路径  %2=输出变量名
set "f=%~1"
set /a kb=(%~z1 + 1023) / 1024
set "%~2=%kb%"
exit /b 0

:CleanupPrefix
REM %1=prefix %2=keep_count
set "prefix=%~1"
set "keep=%~2"

REM 列出符合 prefix-*.sql 的文件，按文件名倒序（时间戳在文件名里）
REM 然后从第 keep+1 个开始删除
set /a "skip=keep+1"
set "idx=0"

for /f "usebackq delims=" %%F in (`dir /b /o-n "%BACKUP_DIR%\%prefix%-*.sql" 2^>nul`) do (
  set /a idx+=1
  if !idx! geq !skip! (
    del /q "%BACKUP_DIR%\%%F" 2>nul
  )
)
exit /b 0

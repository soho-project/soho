@echo off
chcp 65001 >nul
setlocal

REM 获取脚本目录
set "SCRIPT_DIR=%~dp0"
if "%SCRIPT_DIR:~-1%"=="\" set "SCRIPT_DIR=%SCRIPT_DIR:~0,-1%"

REM 进入 databases 目录
cd /d "%SCRIPT_DIR%\..\databases" || exit /b

echo 当前目录：%cd%

REM 遍历 sql 文件
for %%F in (*.sql) do (

    echo 处理：%%F

    powershell -NoProfile -Command ^
    "$in='%%F';" ^
    "$out='tmp.sql';" ^
    "Get-Content -LiteralPath $in -Raw |" ^
    "Where-Object {$_ -notmatch 'INSERT INTO `pay_info`' } |" ^
    "Where-Object {$_ -notmatch 'INSERT INTO `pay_order`' } |" ^
    "Where-Object {$_ -notmatch 'INSERT INTO `chat_session_message`' } |" ^
    "Where-Object {$_ -notmatch 'INSERT INTO `chat_session_message_user`' } |" ^
    "Where-Object {$_ -notmatch 'INSERT INTO `admin_user_login_log`' } |" ^
    "Where-Object {$_ -notmatch 'INSERT INTO `admin_operation_log`' } |" ^
    "Set-Content -LiteralPath $out -Encoding utf8"

    move /Y tmp.sql "%%F" >nul
)

echo 完成！
pause

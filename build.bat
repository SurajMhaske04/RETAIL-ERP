@echo off
REM ============================================================
REM  RetailERP — Build & Run Script (Windows)
REM  Requires: JDK 11+, MySQL Connector/J .jar in lib/
REM ============================================================

SET PROJECT_DIR=%~dp0
SET SRC_DIR=%PROJECT_DIR%src
SET BIN_DIR=%PROJECT_DIR%bin
SET LIB_DIR=%PROJECT_DIR%lib

echo ==========================================
echo   RetailERP — Build Script
echo ==========================================

REM Create bin directory
if not exist "%BIN_DIR%" mkdir "%BIN_DIR%"

REM Check for MySQL connector
SET CLASSPATH=%BIN_DIR%
for %%f in (%LIB_DIR%\*.jar) do SET CLASSPATH=!CLASSPATH!;%%f

echo [1/2] Compiling sources...
javac -d "%BIN_DIR%" -cp "%CLASSPATH%" -sourcepath "%SRC_DIR%" "%SRC_DIR%\com\retailerp\Main.java"

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo  [ERROR] Compilation failed!
    pause
    exit /b 1
)

echo [2/2] Compilation successful!
echo.
echo ==========================================
echo   To RUN the application:
echo     java -cp "%BIN_DIR%;%LIB_DIR%\*" com.retailerp.Main
echo ==========================================
@echo off

REM Set fixed project directory
SET PROJECT_DIR=D:\RetailERP

SET SRC_DIR=%PROJECT_DIR%\src
SET BIN_DIR=%PROJECT_DIR%\bin
SET LIB_DIR=%PROJECT_DIR%\lib

echo ==========================================
echo   RetailERP — Build Script
echo ==========================================

if not exist "%BIN_DIR%" mkdir "%BIN_DIR%"

SETLOCAL EnableDelayedExpansion
SET CLASSPATH=%BIN_DIR%
for %%f in (%LIB_DIR%\*.jar) do SET CLASSPATH=!CLASSPATH!;%%f

echo [1/2] Compiling sources...
javac -d "%BIN_DIR%" -cp "%CLASSPATH%" -sourcepath "%SRC_DIR%" "%SRC_DIR%\com\retailerp\Main.java"

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo  [ERROR] Compilation failed!
    pause
    exit /b 1
)

echo [2/2] Compilation successful!
echo.
pause
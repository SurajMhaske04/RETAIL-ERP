@echo off
SETLOCAL EnableDelayedExpansion

REM Set fixed project directory
SET PROJECT_DIR=D:\RetailERP

SET BIN_DIR=%PROJECT_DIR%\bin
SET LIB_DIR=%PROJECT_DIR%\lib

SET CLASSPATH=%BIN_DIR%
for %%f in (%LIB_DIR%\*.jar) do SET CLASSPATH=!CLASSPATH!;%%f

echo Starting RetailERP...
cd /d %PROJECT_DIR%
java -cp "%CLASSPATH%" com.retailerp.Main

pause
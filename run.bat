@echo off
SETLOCAL EnableDelayedExpansion
REM ============================================================
REM  RetailERP — Run Script (Windows)
REM ============================================================

SET PROJECT_DIR=%~dp0
SET BIN_DIR=%PROJECT_DIR%bin
SET LIB_DIR=%PROJECT_DIR%lib

SET CLASSPATH=%BIN_DIR%
for %%f in (%LIB_DIR%\*.jar) do SET CLASSPATH=!CLASSPATH!;%%f

echo Starting RetailERP...
java -cp "%CLASSPATH%" com.retailerp.Main

pause

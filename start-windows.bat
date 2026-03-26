@echo off
REM Quick start script for WSMS Test Web Application on Windows

echo.
echo ========================================
echo WSMS Test Web Application - Quick Start
echo ========================================
echo.

REM Check if Maven is installed
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: Maven not found in PATH
    echo Please install Maven or add it to PATH
    pause
    exit /b 1
)

REM Check if Java is installed
where java >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: Java not found in PATH
    echo Please install Java 21 or higher
    pause
    exit /b 1
)

echo [1/3] Checking Java version...
java -version
echo.

echo [2/3] Building application...
call mvn clean package -DskipTests
if %errorlevel% neq 0 (
    echo ERROR: Build failed
    pause
    exit /b 1
)

echo.
echo [3/3] Starting application...
echo Application will run on: http://localhost:5173
echo.
echo Press Ctrl+C to stop the application
echo.

java -jar target/test-web-application-1.0.0.jar

pause

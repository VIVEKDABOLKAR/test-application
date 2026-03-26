@echo off
REM Combined start script - Runs both Test Web Application and Server Agent
REM Requires two terminal windows

color 0A
echo.
echo ========================================================
echo WSMS - Test Web Application + Server Agent Integration
echo ========================================================
echo.
echo This script will start both applications for monitoring.
echo.
echo IMPORTANT:
echo 1. This script opens the test web application
echo 2. You MUST open another terminal to run server-agent
echo.
echo Setup Instructions:
echo  1. Keep this window open (Test Web Application)
echo  2. Open new PowerShell/CMD window
echo  3. Navigate to: server_agent-main
echo  4. Run: java -jar target/server-agent-1.0.0.jar --configPath config.json
echo  5. Server should listen on port 4017
echo.
echo Application URLs:
echo  - Home:      http://localhost:5173
echo  - Dashboard: http://localhost:5173/dashboard
echo  - API:       http://localhost:5173/api/health
echo.
echo ========================================================
echo.

REM Build and run
cd test-web-application
echo Building test web application...
call mvn clean package -DskipTests -q

if %errorlevel% neq 0 (
    echo ERROR: Build failed
    pause
    exit /b 1
)

echo Launching test web application on port 5173...
echo.

java -jar target/test-web-application-1.0.0.jar

pause

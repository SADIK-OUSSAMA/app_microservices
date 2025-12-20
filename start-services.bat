@echo off
title Microservices Launcher
color 0A

echo ============================================
echo   Microservices Application Launcher
echo ============================================
echo.

:: Set the base directory
set BASE_DIR=%~dp0

echo [1/6] Starting Config Service...
start "Config Service" cmd /k "cd /d %BASE_DIR%config-service && mvnw spring-boot:run"
echo Waiting 20 seconds for Config Service to initialize...
timeout /t 20 /nobreak > nul

echo [2/6] Starting Discovery Service (Eureka)...
start "Discovery Service" cmd /k "cd /d %BASE_DIR%discovery-service && mvnw spring-boot:run"
echo Waiting 20 seconds for Discovery Service to initialize...
timeout /t 20 /nobreak > nul

echo [3/6] Starting Customer Service...
start "Customer Service" cmd /k "cd /d %BASE_DIR%customer-service && mvnw spring-boot:run"
echo Waiting 10 seconds...
timeout /t 10 /nobreak > nul

echo [4/6] Starting Inventory Service...
start "Inventory Service" cmd /k "cd /d %BASE_DIR%inventory-service && mvnw spring-boot:run"
echo Waiting 10 seconds...
timeout /t 10 /nobreak > nul

echo [5/6] Starting Billing Service...
start "Billing Service" cmd /k "cd /d %BASE_DIR%billing-service && mvnw spring-boot:run"
echo Waiting 10 seconds...
timeout /t 10 /nobreak > nul

echo [6/6] Starting Gateway Service...
start "Gateway Service" cmd /k "cd /d %BASE_DIR%gateway-service && mvnw spring-boot:run"

echo.
echo ============================================
echo   All services have been launched!
echo ============================================
echo.
echo Services started:
echo   - Config Service
echo   - Discovery Service (Eureka)
echo   - Customer Service
echo   - Inventory Service
echo   - Billing Service
echo   - Gateway Service
echo.
echo You can now start the frontend with:
echo   cd micro-services-app-frontend
echo   npm start
echo.
pause

@echo off
echo.
echo === Config Server and Item Service Authentication Test ===
echo.

REM Check if curl is available
where curl >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ curl is not available. Please install curl or use PowerShell script.
    pause
    exit /b 1
)

REM Check environment variables
echo === Environment Variable Check ===
echo.

if defined CONFIG_USERNAME (
    echo ✅ CONFIG_USERNAME: %CONFIG_USERNAME%
) else (
    echo ❌ CONFIG_USERNAME: Not set
)

if defined CONFIG_PASSWORD_HASH (
    echo ✅ CONFIG_PASSWORD_HASH: Set (%CONFIG_PASSWORD_HASH:~0,20%...)
) else (
    echo ❌ CONFIG_PASSWORD_HASH: Not set
)

echo.

if defined CONFIG_SERVER_USERNAME (
    echo ✅ CONFIG_SERVER_USERNAME: %CONFIG_SERVER_USERNAME%
) else (
    echo ❌ CONFIG_SERVER_USERNAME: Not set
)

if defined CONFIG_SERVER_PASSWORD (
    echo ✅ CONFIG_SERVER_PASSWORD: %CONFIG_SERVER_PASSWORD%
) else (
    echo ❌ CONFIG_SERVER_PASSWORD: Not set
)

echo.

REM Test config server authentication
echo === Testing Config Server Authentication ===
echo.

if defined CONFIG_SERVER_USERNAME if defined CONFIG_SERVER_PASSWORD (
    echo Testing with username: %CONFIG_SERVER_USERNAME%
    echo Testing with password: %CONFIG_SERVER_PASSWORD%
    echo.
    
    REM Test with curl
    curl -u "%CONFIG_SERVER_USERNAME%:%CONFIG_SERVER_PASSWORD%" -s -w "%%{http_code}" http://localhost:8888/config/item-service/dev > temp_response.txt
    
    set /p response=<temp_response.txt
    set statusCode=%response:~-3%
    
    if "%statusCode%"=="200" (
        echo ✅ Config Server Authentication SUCCESSFUL!
        echo Response: %response:~0,-3%
    ) else (
        echo ❌ Config Server Authentication FAILED!
        echo Status Code: %statusCode%
        echo Response: %response:~0,-3%
    )
    
    del temp_response.txt
) else (
    echo ❌ Cannot test authentication - missing environment variables
)

echo.
echo === Summary ===
echo.
echo If authentication is working:
echo    1. Start item service with environment variables set
echo    2. Check item service logs for successful config loading
echo.
echo If authentication is failing:
echo    1. Verify the password used on bcrypt-generator.com
echo    2. Ensure CONFIG_SERVER_PASSWORD matches that password
echo    3. Restart both applications with correct environment variables
echo.
pause
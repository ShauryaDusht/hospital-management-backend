@echo off
setlocal EnableDelayedExpansion

echo ========================================
echo IP-Based Rate Limiting Test
echo ========================================
echo.

set BASE_URL=http://localhost:8080

echo Test 1: Different users from same IP (IP limit triggers)
echo ----------------------------------------------------------
echo.

for /L %%i in (1,1,7) do (
    set USER=user%%i@test.com
    echo Attempt %%i ^(!USER!^):
    curl -X POST !BASE_URL!/auth/login ^
        -H "Content-Type: application/json" ^
        -d "{\"username\":\"!USER!\",\"password\":\"wrong\"}" ^
        -s -o nul -w "HTTP: %%{http_code}"
    echo.
    timeout /t 1 /nobreak >nul
)

echo.
echo ========================================
echo Expected:
echo - Attempts 1-5: HTTP 401
echo - Attempts 6-7: HTTP 429
echo ========================================
echo.

pause
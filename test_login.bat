@echo off
echo ========================================
echo Quick Rate Limit Test
echo ========================================
echo.

set BASE_URL=http://localhost:8080

echo Testing Login Rate Limit (6 attempts max)
echo ------------------------------------------
echo.

for /L %%i in (1,1,7) do (
    echo Attempt %%i:
    curl -X POST %BASE_URL%/auth/login ^
        -H "Content-Type: application/json" ^
        -d "{\"username\":\"test@test.com\",\"password\":\"wrong\"}" ^
        -s -w " | HTTP: %%{http_code}\n"
    echo.
    timeout /t 1 /nobreak >nul
)

echo.
echo ========================================
echo Expected Results:
echo - Attempts 1-5: HTTP 401 (Unauthorized)
echo - Attempts 6-7: HTTP 429 (Too Many Requests)
echo ========================================
echo.

pause
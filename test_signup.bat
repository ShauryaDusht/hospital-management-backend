@echo off
echo ========================================
echo Signup Rate Limit Test
echo ========================================
echo.

set BASE_URL=http://localhost:8080

echo Testing Signup Rate Limit (3 attempts max)
echo ------------------------------------------
echo.

for /L %%i in (1,1,5) do (
    echo Attempt %%i:
    curl -X POST %BASE_URL%/auth/signup ^
        -H "Content-Type: application/json" ^
        -d "{\"username\":\"user%%i@test.com\",\"password\":\"pass123\",\"name\":\"Test User\"}" ^
        -s -w " | HTTP: %%{http_code}\n"
    echo.
    timeout /t 1 /nobreak >nul
)

echo.
echo ========================================
echo Expected Results:
echo - Attempts 1-3: HTTP 200/201
echo - Attempts 4-5: HTTP 429 (Too Many Requests)
echo - Retry-After around 86400 seconds
echo ========================================
pause
@echo off
setlocal
cd /d "%~dp0\.."

python scripts\reset_database.py --force

if errorlevel 1 (
    echo.
    echo Reset bazy nie powiodl sie.
    pause
    exit /b 1
)

echo.
echo Baza danych zostala odtworzona.
pause

@echo off
setlocal
cd /d "%~dp0\.."

python scripts\reset_database.py --force --seed

if errorlevel 1 (
    echo.
    echo Reset lub seedowanie bazy nie powiodlo sie.
    pause
    exit /b 1
)

echo.
echo Baza danych zostala odtworzona i uzupelniona danymi testowymi.
pause

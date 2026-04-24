@echo off
REM scripts\check.bat - manual quality gate checks
setlocal enabledelayedexpansion

echo ========================================
echo  Quality Gate Check
echo ========================================

set "PASS=true"
set "PREFERRED_JAVA_HOME=%USERPROFILE%\.jdks\ms-21.0.10"

if exist "%PREFERRED_JAVA_HOME%\bin\java.exe" (
    set "JAVA_HOME=%PREFERRED_JAVA_HOME%"
    set "PATH=%JAVA_HOME%\bin;%PATH%"
    echo [OK] Using JDK: !JAVA_HOME!
) else (
    echo [WARN] Preferred JDK not found at %PREFERRED_JAVA_HOME%
)

echo.
echo ^>^>^> Java compile...
pushd backend
call mvn compile -q
if errorlevel 1 (
    echo [FAIL] Java compile failed
    set "PASS=false"
) else (
    echo [OK] Java compile passed
)

echo.
echo ^>^>^> Java tests...
call mvn clean test -q
if errorlevel 1 (
    echo [FAIL] Java tests failed
    set "PASS=false"
) else (
    echo [OK] Java tests passed
)

echo.
echo ^>^>^> Java checkstyle...
call mvn checkstyle:check
if errorlevel 1 (
    echo [FAIL] Java checkstyle failed
    set "PASS=false"
) else (
    echo [OK] Java checkstyle passed
)
popd

echo.
echo ^>^>^> TypeScript type check...
pushd frontend
if exist node_modules\.bin\vue-tsc.cmd (
    call npm run type-check
    if errorlevel 1 (
        echo [FAIL] TypeScript type check failed
        set "PASS=false"
    ) else (
        echo [OK] TypeScript type check passed
    )
) else (
    echo [SKIP] Frontend dependencies are not installed
)

echo.
echo ^>^>^> Frontend lint...
if exist node_modules\.bin\eslint.cmd (
    call npm run lint
    if errorlevel 1 (
        echo [FAIL] Frontend lint failed
        set "PASS=false"
    ) else (
        echo [OK] Frontend lint passed
    )
) else (
    echo [SKIP] Frontend dependencies are not installed
)
popd

echo.
echo ========================================
if "%PASS%"=="true" (
    echo  Quality gate passed
) else (
    echo  Quality gate failed
    echo  Hint: review docs/definition-of-done.md
    exit /b 1
)
echo ========================================

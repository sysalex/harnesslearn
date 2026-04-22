@echo off
REM scripts\check.bat - 手动触发质量门禁（与 Stop Hook 一致）
REM 用法: scripts\check.bat
setlocal enabledelayedexpansion

echo ========================================
echo  质量门禁检查
echo ========================================

set PASS=true

REM ── Java 编译检查 ──────────────────────────
echo.
echo ^>^>^> Java 编译检查...
cd backend
call mvn compile -q
if %errorlevel% neq 0 (
    echo [FAIL] Java 编译失败
    set PASS=false
) else (
    echo [OK] Java 编译成功
)

REM ── Java 测试 ─────────────────────────────
echo.
echo ^>^>^> 运行测试...
call mvn clean test -q
if %errorlevel% neq 0 (
    echo [FAIL] 测试失败
    set PASS=false
) else (
    echo [OK] 测试通过
)

REM ── Java 代码规范检查 ─────────────────────
echo.
echo ^>^>^> Java 代码规范检查 (checkstyle)...
call mvn checkstyle:check
if %errorlevel% neq 0 (
    echo [FAIL] 代码规范检查失败
    set PASS=false
) else (
    echo [OK] 代码规范通过
)

REM ── 前端 TypeScript 类型检查 ──────────────
echo.
echo ^>^>^> TypeScript 类型检查 (vue-tsc)...
cd ..\frontend
if exist node_modules\.bin\vue-tsc.cmd (
    call npm run type-check
    if %errorlevel% neq 0 (
        echo [FAIL] TypeScript 类型检查失败
        set PASS=false
    ) else (
        echo [OK] TypeScript 类型检查通过
    )
) else (
    echo [SKIP] 前端依赖未安装，运行: cd frontend ^&^& npm install
)

REM ── 前端 lint 检查 ─────────────────────────
echo.
echo ^>^>^> 前端 lint 检查 (eslint)...
if exist node_modules\.bin\eslint.cmd (
    call npm run lint
    if %errorlevel% neq 0 (
        echo [FAIL] ESLint 检查失败
        set PASS=false
    ) else (
        echo [OK] ESLint 检查通过
    )
) else (
    echo [SKIP] 前端依赖未安装
)

REM ── 汇总 ──────────────────────────────────────
echo.
echo ========================================
if "%PASS%"=="true" (
    echo  ✓ 质量门禁通过
) else (
    echo  ✗ 质量门禁未通过，请修复后再提交
    echo.
    echo  提示：查阅 docs/definition-of-done.md 核对完成标准
    exit /b 1
)
echo ========================================

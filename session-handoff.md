# 会话交接

**最后更新**: 2026-04-23  
**当前阶段**: Phase 1 - 基础设施搭建  
**当前任务**: 1.3.2 配置 Spring Security

## 当前状态
- `1.1.1` 最小 Spring Boot 后端骨架已完成。
- `1.1.2` Vue 3 前端骨架已完成，当前具备 Vite + Vue 3 + TypeScript + Vue Router + Pinia + Element Plus + 登录页占位。
- `1.2.1` 已完成：补充了初始化脚本结构校验测试，修复了 `department` 表前向引用 `user` 表的外键顺序问题，并在本地 MySQL 8.0.16 实例中真实执行通过。
- `1.2.2` 已完成：补齐了 MySQL 数据源配置、`@MapperScan("com.attendance.mapper")` 和 MySQL 分页拦截器，并通过配置测试验证。
- `1.3.1` 已完成：新增 `JwtTokenProvider`，支持 access/refresh token 生成、校验、刷新和 Claims 解析，并通过单元测试覆盖核心行为。
- 本地 MySQL 导入已确认：不要用 PowerShell 管道把 SQL 喂给 `mysql.exe`，否则中文会在进入原生进程前被转坏；应使用 `cmd` 文件重定向并配合 `--default-character-set=utf8mb4`。
- 仓库已启用“任务完成后自动 commit + push”的默认流程。

## 已完成
- 后端上下文启动测试通过。
- 前端默认路由登录页测试通过。
- 前端 `npm run lint` 通过。
- 前端 `npm run build` 通过。
- 前端开发服务器已验证可在 `http://127.0.0.1:5173/` 启动。

## 进行中
- 准备进入 `1.3.2 配置 Spring Security`。

## 阻塞项
- 命令行默认 Java 仍指向 JDK 8；后端 Maven 验证需要显式切到 IDEA 的 JDK 21。
- `1.1.3 配置 Docker 环境` 当前阻塞：本地未安装 Docker，暂不做容器化验证。
- 当前尚无实际 `com.attendance.mapper` 接口，因此 Spring Boot 启动时会出现一次 “No MyBatis mapper was found” 警告；在后续落实体和 Mapper 前属于预期现象。

## 下一步建议
1. 开始 `1.3.2 配置 Spring Security`，补齐 JWT 认证过滤器、权限校验和公开接口放行。
2. 后续在引入首个实体和 Mapper 时，顺手消掉当前 “No MyBatis mapper was found” 的预期警告。
3. 将本地 MySQL 连接方式整理进开发文档或脚本，避免后续重复摸索验证环境。
4. 待具备 Docker 环境后回补 `1.1.3`，并继续同步更新 `CHANGELOG.md`、`docs/task-list.md`、`session-handoff.md`。

## 验证结果
- `backend`: `mvn -Dtest=AttendanceSystemApplicationTests test`
- `backend`: `mvn checkstyle:check`
- `backend`: `mvn -Dtest=InitSqlScriptTest test`（使用 `%USERPROFILE%\\.jdks\\ms-21.0.10`）
- `backend`: `mvn -Dtest=MyBatisPlusConfigTest test`（使用 `%USERPROFILE%\\.jdks\\ms-21.0.10`）
- `backend`: `mvn -Dtest=JwtTokenProviderTest test`（使用 `%USERPROFILE%\\.jdks\\ms-21.0.10`）
- `backend`: `mvn -Dtest=AttendanceSystemApplicationTests,InitSqlScriptTest,MyBatisPlusConfigTest test`（使用 `%USERPROFILE%\\.jdks\\ms-21.0.10`）
- `backend`: `mvn -Dtest=AttendanceSystemApplicationTests,InitSqlScriptTest,MyBatisPlusConfigTest,JwtTokenProviderTest test`（使用 `%USERPROFILE%\\.jdks\\ms-21.0.10`）
- `mysql`: 使用 `cmd` 文件重定向执行 `sql/init.sql`，创建 `attendance_db` 并校验 6 张核心表、管理员账号和全局考勤规则
- `frontend`: `npm run test`
- `frontend`: `npm run lint`
- `frontend`: `npm run build`
- `frontend`: `npm run dev -- --host 127.0.0.1`

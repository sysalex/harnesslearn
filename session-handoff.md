# 会话交接

**最后更新**: 2026-04-24  
**当前阶段**: Phase 1 - 基础设施搭建  
**当前任务**: 1.3.3.2 后端分层架构纠偏（对齐 bh-im / seckill）

## 当前状态

- `1.3.3.2` 已完成：后端已纠偏为 `attendance-common` 与 `attendance-server` 聚合服务；`attendance-server` 内按 `starter / interfaces / application / domain / infrastructure` 五层拆分。
- 认证链路已迁入新结构，Java 包根统一为 `com.attendance.server.*`，公共基础组件统一为 `com.attendance.common.*`。
- 为了让仓库级质量门禁可直接运行，已补齐：
  - `scripts/check.bat`：执行前自动切换到 `%USERPROFILE%\.jdks\ms-21.0.10`
  - `frontend/package.json`：新增 `npm run type-check`

## 本次完成内容

- 移除 `attendance-server-shared` 模块命名，公共响应、异常和 JWT 基础组件迁入 `attendance-common`。
- 新增领域仓储契约 `UserRepository`，由基础设施层 `UserRepositoryImpl` 适配 `UserMapper`。
- 登录链路调整为 `AuthController -> AuthApplicationService -> UserService/UserRepository -> UserRepositoryImpl -> UserMapper`。
- `interfaces` 已移除对 `infrastructure` 的测试依赖，`application` 已移除对 `infrastructure` 的 POM 依赖和代码 import，由 `starter` 聚合运行时基础设施模块。
- 启动类组件扫描补齐 `com.attendance.common`，SQL 结构测试路径已适配新的模块层级。

## 已完成验证

- 已运行典型 mojibake 字符串扫描
  - 返回 exit 1，表示未匹配到已知乱码特征。
- `mvn clean test`（在 `attendance` 目录显式设置 `JAVA_HOME=%USERPROFILE%\.jdks\ms-21.0.10`）
  - 8 个 Maven 模块全部 SUCCESS
  - 37 个测试，0 failures / 0 errors / 0 skipped
- `scripts/check.bat`
  - 后端 `mvn compile`
  - 后端 `mvn clean test`
  - 后端 `mvn checkstyle:check`
  - 前端 `npm run type-check`
  - 前端 `npm run lint`
  - 结果：Quality gate passed

## 当前阻塞

- `1.1.3 Docker 环境` 仍阻塞：本地尚未安装 Docker，容器化相关任务暂未继续。
- 终端默认 `JAVA_HOME` 仍指向 JDK 8，但仓库质量门禁已通过脚本显式切换到 `%USERPROFILE%\.jdks\ms-21.0.10` 规避该问题。

## 下一步建议

1. 开始 `1.3.4`，在当前多模块结构上补齐 `GET /api/auth/profile`、`PUT /api/auth/profile`、`PUT /api/auth/password`。
2. 后续新增后端能力时，继续保持 `interfaces -> application -> domain.repository -> infrastructure.repository -> mapper` 的边界，不要让应用层直接依赖基础设施。
3. 若后续继续扩展认证链路，优先在所属模块内补测试，再做实现。

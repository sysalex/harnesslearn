# Learnings

## 2026-04-22 - Harness 规范补充

### 背景

当前仓库的 Harness 规范主体完整，但缺少若干运行载体，导致规范更多停留在文档原则层面。

### 学到的内容

- `docs/feedback-loop.md` 已经提到了会话交接和学习记录，但仓库中缺少实际文件，说明规范与落地产物存在断层。
- 任务状态只有“未开始/完成”时，无法清楚表达实际推进状态和阻塞原因。
- 可观测性约定必须进入项目主规范，而不是只零散存在于某个说明文件中。

### 后续建议

- 后续每次阶段结束都补充 `docs/retrospectives/` 文档。
- 新的工程规范变更优先通过“新增产物 + 轻量联动”方式落地，避免一次性重写大文档。

## 2026-04-22 - 前端脚手架与测试落地

### 背景

开始实现 `1.1.2 创建 Vue 3 项目结构`，目标是落地一个可启动、可构建、带登录页占位的前端骨架。

### 学到的内容

- 对“创建项目结构”这类任务，先生成最小脚手架，再对具体行为走 TDD 更稳妥；这次用“默认进入登录占位页”作为测试目标是有效的。
- PowerShell 下不能直接用 `&&`，需要拆成独立命令或串行执行脚本。
- 不要并行执行两个会改写同一个 `package.json` 的 `npm install`；依赖虽然可能装进 `node_modules`，但声明文件容易被覆盖。
- Element Plus 全量引入会立即带来较大的前端构建体积，早期能接受，但需要尽早登记为技术债。

### 后续建议

- 前端依赖安装命令保持串行，避免再次出现 `package.json` 与 `node_modules` 不一致。
- 后续如果继续扩展前端页面，优先引入按需加载或更细的路由拆分，提前控制包体增长。

## 2026-04-23 - 本仓库优先遵循 Harness Engineering 流程

### 背景

当前运行环境带有额外的通用流程技能，但本仓库已经通过 `AGENTS.md`、`docs/task-list.md`、`session-handoff.md` 等文件定义了完整的 Harness Engineering 工作方式。

### 学到的内容

- 在本仓库中，任务推进应优先遵循项目内的 Harness Engineering 规范，不应再额外叠加一套重复的设计/spec/plan 流程。
- 如果外部通用流程与仓库内既有流程重复，即使不直接冲突，也会引入额外文档和沟通成本，应避免。
- 本仓库的默认执行主线应保持为：查 `docs/task-list.md`、按 TDD 推进、完成必要验证、同步更新 `session-handoff.md` / `CHANGELOG.md`。

### 后续建议

- 后续在本仓库执行任务时，默认不再引入与 Harness 流程重复的 `superpowers` 过程型产物。
- 如果确实需要额外流程或文档，先确认是否为仓库规范所要求，再决定是否新增。

## 2026-04-23 - 初始化 SQL 先校验外键顺序

### 背景

推进 `1.2.1 创建数据库初始化脚本` 时，现有 `sql/init.sql` 在 `department` 建表阶段直接引用了尚未创建的 `user` 表。

### 学到的内容

- 初始化脚本里如果存在相互依赖的表，建表阶段只能保留对已存在表的外键；跨表依赖要延后到 `ALTER TABLE`。
- 即使暂时缺少可直接复用的 MySQL 客户端或连接信息，也应先补结构性测试，把“前向外键引用”这类确定性错误挡在提交前。
- 当前仓库在命令行环境下跑后端 Maven 校验时，仍需显式切到 `%USERPROFILE%\\.jdks\\ms-21.0.10`，否则会被 JDK 8 的 `--release` 不支持问题卡住。

### 后续建议

- 后续新增 SQL 初始化脚本或 migration 时，优先检查建表顺序和外键追加顺序。
- 如需在本机做真实 MySQL 执行验证，先把本地可复用的连接方式或客户端命令补进仓库文档，避免重复摸索环境。

## 2026-04-23 - Windows 下不要用 PowerShell 管道导入 MySQL 中文 SQL

### 背景

本地验证 `sql/init.sql` 时，第一次用 PowerShell 管道把脚本内容传给 `mysql.exe`，结果数据库里的中文注释和种子数据被写成了 `?`。

### 学到的内容

- 在 Windows / PowerShell 环境下，把含中文的 SQL 文本通过管道传给原生 `mysql.exe`，内容可能在进入进程前就被当前代码页转坏。
- 同一份 UTF-8 脚本使用 `cmd` 文件重定向配合 `mysql.exe --default-character-set=utf8mb4` 导入后，库内十六进制值正确，说明问题出在导入链路，不在脚本本身。
- 终端查看 MySQL 中文结果前，最好先切 `chcp 65001`，否则即使库里正确，也可能只是显示乱码。

### 后续建议

- 后续本机导入中文 SQL 默认用 `cmd /c "\"...\\mysql.exe\" ... < \"...sql\""`，不要再用 PowerShell 文本管道。
- 如果需要程序化验证中文是否正确，优先查 `HEX(column)` 或 `HEX(TABLE_COMMENT)`，不要只凭终端显示判断。

## 2026-04-23 - MyBatis-Plus 可先落配置，再接受无 Mapper 扫描警告

### 背景

推进 `1.2.2 配置 MyBatis-Plus` 时，仓库里还没有任何实际的 `Mapper` 接口，但任务要求已经需要先补齐数据源、Mapper 扫描和分页插件配置。

### 学到的内容

- 当前阶段可以先把 `application.yml` 数据源、`@MapperScan("com.attendance.mapper")` 和 `MybatisPlusInterceptor` 配好，并用配置测试锁住行为。
- 在还没有 `com.attendance.mapper` 下实际接口时，Spring Boot 启动会打印 “No MyBatis mapper was found” 警告；这是已知且可接受的阶段性现象，不代表配置失效。
- 测试 `@MapperScan` 时要注意单参数形式会落在注解的 `value()`，不能只断言 `basePackages()`。

### 后续建议

- 引入首个实体和 Mapper 时，同步把这条扫描警告消掉。
- 后续配置类优先用独立测试锁定 Bean 和属性，避免只靠人工目测 `application.yml`。

## 2026-04-23 - JWT 密钥读取要兼容普通字符串而不是只按 Base64

### 背景

实现 `JwtTokenProvider` 时，初版先尝试把密钥当 Base64 解码，结果测试里传入普通字符串密钥时被 `jjwt` 抛出 `DecodingException`。

### 学到的内容

- JWT 密钥来源既可能是 Base64，也可能是普通字符串；如果一上来强制按 Base64 解析，普通字符串场景会直接失败。
- 这类密钥解析问题应先靠单元测试锁住，再在实现里做兼容分支，不要等到 Spring Security 接入后再排查。
- `JwtTokenProviderTest` 这类纯工具测试适合直接 new 对象，不必先拉起 Spring 上下文。

### 后续建议

- 后续接 `1.3.2 Spring Security` 时，继续复用 `JwtTokenProvider`，不要在过滤器或配置类里重复写 token 解析逻辑。
- 如果后面要接外部环境中的 Base64 密钥，优先保留当前“Base64 优先，失败回退普通字符串”的兼容策略。

## 2026-04-23 - Spring Security 与 JWT 过滤器可以并行拆，但最终要回到独立文件

### 背景

推进 `1.3.2 配置 Spring Security` 时，我把“安全配置”和“JWT 过滤器”拆给了两个并行 agent。过程中一个分支先把过滤器临时塞进 `SecurityConfig.java`，另一个分支补了独立的过滤器测试。

### 学到的内容

- 这类任务可以并行拆，但职责边界要稳定：`SecurityConfig` 负责安全链和放行规则，`JwtAuthenticationFilter` 负责 token 到 `SecurityContext` 的恢复。
- 并行实现时即使临时产物能跑，也应该在主线集成阶段把类落回独立文件，否则很容易出现包名错位、同名类冲突或后续扩展困难。
- 对安全配置这类基础设施，`SecurityConfigTest` 和 `JwtAuthenticationFilterTest` 分开锁行为，比把所有断言塞进一个上下文测试更稳。

### 后续建议

- 后续继续多 agent 时，优先按“配置类 / 过滤器 / 控制器 / 服务 / Mapper”这种天然边界拆写入范围。
- 主线集成时先检查包名、Bean 依赖和文件归属，再跑完整测试，不要只看子 agent 的局部通过结果。

## 2026-04-24 - 引入首个 Mapper 后，测试切面要主动隔离数据库依赖

### 背景

推进 `1.3.3 实现用户登录接口` 时，仓库第一次真正落了 `UserMapper` 和 `AuthServiceImpl`。这会把此前只做配置验证的 `SpringBootTest` / `WebMvcTest` 一起带进 MyBatis 装配和安全 Bean 依赖。

### 学到的内容

- 引入首个 `Mapper` 之后，原来“没有真实 Mapper 也能过”的上下文测试会立刻受 `@MapperScan` 和数据源依赖影响，不能继续假设测试切片天然隔离。
- `WebMvcTest` 在导入 `SecurityConfig` 后，如果安全链里有 `JwtAuthenticationFilter`，就要显式补齐 `JwtTokenProvider` 的测试 bean；否则会在 Web 层测试里卡在安全装配，而不是接口行为。
- 对于只验证上下文是否能启动或安全放行规则的测试，用 `@MockBean UserMapper` 挡住数据库装配，比为了测试去引入一层临时 `SqlSessionFactory` 兜底更稳，也更接近真实运行边界。
- 登录接口的“错误处理完整”不应只停留在 401/403，还要补参数校验的 400 路径，并用单一输入场景锁定单一错误消息，避免一个测试同时命中多个字段校验导致断言不稳定。

### 后续建议

- 后续继续补 `profile` / `password` 能力时，默认同步检查现有 `SpringBootTest`、`WebMvcTest` 是否需要新的 `@MockBean` 隔离。
- 测试切面遇到上下文装配问题时，优先 mock 外部依赖或补最小真实 bean，不要留下只为测试存在、又可能混入生产主线的临时配置类。

## 2026-04-24 - MyBatis-Plus 服务层和自定义查询要保持同一套分层风格

### 背景

推进登录链路后复盘发现，虽然 `UserMapper` 已接入 MyBatis-Plus，但服务层仍直接依赖 `Mapper`，并且自定义用户名查询写成了注解 SQL，这和项目当前约定的分层方式不一致。

### 学到的内容

- 既然项目已经选择 MyBatis-Plus，领域服务层就应统一走 `IService<T>` / `ServiceImpl<M, T>` 这套继承结构，不要一部分走通用服务，一部分又回退到业务层直连 `Mapper`。
- 自定义查询如果不是极轻量且一次性的场景，优先放进 XML Mapper，避免注解 SQL 分散在接口定义里，后续扩展字段、结果映射或复用查询时更容易维护。
- 当分层风格需要调整时，测试不应只盯行为结果，也要补结构性测试，把“服务是否继承 `IService` / `ServiceImpl`”这种约束锁住，避免后面再次回退。

### 后续建议

- 后续新增领域服务时，默认先建 `Service` 接口和 `ServiceImpl`，再让业务服务依赖领域服务，而不是直接依赖 `Mapper`。
- 后续若继续新增 `UserMapper` 自定义 SQL，默认放到 `backend/src/main/resources/mapper/*.xml`，并同步维护 `resultMap`。

## 2026-04-24 - 单表查询优先走 MyBatis-Plus 通用能力，XML 留给多表和复杂映射

### 背景

登录链路第一次按“去掉 Mapper 注解 SQL、改成 XML”收口后，又复盘发现这对当前场景还是过重了：这里只是单表按用户名查一条用户记录，更适合直接使用 MyBatis-Plus 条件构造器。

### 学到的内容

- 单表查询如果只是等值条件、分页、排序、简单组合条件，优先走 `lambdaQuery()` / `LambdaQueryWrapper`，这样和 `IService` / `ServiceImpl` 的使用方式更一致。
- XML 更适合多表查询、复杂结果映射、聚合统计或明显超出通用条件构造器可读性的 SQL；不要为了“把 SQL 挪出去”就把简单单表查询也强行写进 XML。
- 公共字段一旦开始沉淀，就要尽早收口成基类，否则 `enabled`、`deleted`、`createdAt`、`updatedAt` 这类字段会在每个实体里反复散落，后面再统一成本更高。

### 后续建议

- 后续新增实体默认继承 `BaseEntity`，单表查询默认先尝试 MyBatis-Plus 通用能力。
- 只有出现多表、复杂字段映射或 SQL 可读性明显下降时，再新建 XML Mapper。

## 2026-04-24 - 公共基类字段命名一旦确定，代码和本地数据库要同轮同步

### 背景

公共基类最初已经落了一版字段，但字段名和你最终确认的 `createdByUserId`、`createdByUserName`、`createdTime`、`updatedByUserId`、`updatedByUserName`、`updatedTime`、`enabledFlag`、`deletedFlag` 不一致。如果只改 Java 实体，不改现有本地库，就会留下“代码和库结构对不上”的隐患。

### 学到的内容

- 这类基础字段命名一旦定版，不能只改实体类和初始化脚本；本地已存在的开发库也要同步迁移，否则后续联调时会出现列名找不到或旧字段遗留的问题。
- 这类调整适合同时保留两类产物：一份最新的 `init.sql` 代表全新初始化结果，一份 migration 代表对既有数据库的实际升级路径。
- “本地有数据库”这类前提不要再依赖 `mysql.exe` 是否在 PATH 里；当前项目本身已经有 JDBC 驱动和连接信息，用 JDBC 直接执行 migration 更稳，也更容易做列级校验。

### 后续建议

- 后续再出现基础字段或表结构命名调整时，默认同时更新 `sql/init.sql`、migration 文件和本地开发库。
- 执行完 migration 后，补一层 `information_schema.COLUMNS` 查询校验，不要只看“脚本执行成功”。

## 2026-04-24 - Java 字段 camelCase 不代表数据库列也该用 camelCase

### 背景

公共基类第一次收口时，把 Java 字段名直接拿去做数据库列名了，结果本地库里也被迁成了 camelCase。这和数据库表设计的常规约定不一致，后面你明确要求数据库列回到下划线风格。

### 学到的内容

- 领域对象字段名和数据库列名要分层看待：Java 代码保留可读性好的 camelCase，数据库层保留下划线命名，二者通过 `@TableField` 显式映射。
- 公共基类如果不用 Lombok，很容易在字段调整时产生大量机械 getter/setter 改动，信噪比很差；这类纯数据承载对象更适合统一用 `@Getter` / `@Setter`。
- 这类命名纠偏不能只改代码和脚本，还要把已经迁错的本地库再迁回来，否则下一步功能开发会在“代码对、库不对”的状态里继续积累问题。

### 后续建议

- 后续所有实体默认遵循同一原则：Java 字段 camelCase，数据库列 snake_case，MyBatis-Plus 注解负责映射。
- 公共基类和简单实体优先用 Lombok 消掉重复访问器代码，只在有特殊行为时手写方法。

## 2026-04-24 - 结构迁移除了字段名，还要补列注释

### 背景

公共字段从 camelCase 列名纠正回 snake_case 的过程中，本地库字段名已经对了，但通过临时 JDBC 改名时没有把列注释一并补上，导致第二次出现“结构看似对了，元数据没补齐”的问题。

### 学到的内容

- 数据库结构迁移不能只盯列是否存在、类型是否对，列注释同样属于表设计的一部分，尤其这类公共字段会在管理工具、巡检和交接里反复被看到。
- 临时 JDBC 修正脚本适合快速纠偏，但如果它改变了列定义，就要同步考虑 `COMMENT`，否则很容易留下“库里字段没注释、脚本里有注释”的分裂状态。
- `entity` 包下的简单持久化对象如果继续手写访问器，修改一轮基础字段时会额外制造很多低价值 diff；这类对象应该默认和 `BaseEntity` 保持同样的 Lombok 风格。

### 后续建议

- 后续所有结构迁移完成后，默认多做一层 `information_schema.COLUMNS` 校验，同时检查 `COLUMN_NAME` 和 `COLUMN_COMMENT`。
- `entity` 包下新增的简单实体默认直接用 Lombok，除非确实需要自定义访问逻辑。

## 2026-04-24 - 单模块拆成多模块时，先修依赖方向，再搬测试

### 背景

本次把 ackend 从单模块改成 ttendance-server-* 六模块 Reactor。初始主代码已经部分迁到新包结构，但测试、DTO 归属和模块依赖方向还停留在单模块思维。

### 学到的内容

- 先把父 POM 和子模块依赖方向立住，再搬代码，能更早暴露真实的模块边界问题。
- pplication 不应该反向依赖 interfaces 的 DTO；如果应用服务接口直接使用接口层 DTO，多模块一拆就会立即编译失败。
- 控制器契约测试在多模块下不一定适合继续用完整 Spring Boot 上下文。对于只验证 HTTP 结构、参数校验和异常映射的场景，MockMvcBuilders.standaloneSetup(...) 更稳，也能避开数据源和安全自动配置干扰。
- 从 PowerShell 批量改写 Java 文件时，默认 UTF-8 BOM 可能让 javac 直接报 非法字符: '\ufeff'；批量重写源码时应显式使用无 BOM UTF-8。

### 后续建议

- 后续如果继续扩展多模块，先判断“契约类”属于应用层还是接口层，再决定文件归属，不要搬完目录后再倒查依赖环。
- 需要局部测试控制器时，默认优先独立 MockMvc 测试；只有明确需要自动配置参与时再拉 Boot 上下文。
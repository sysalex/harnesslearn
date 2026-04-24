# Backend Multi-Module Refactor Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Convert `backend` into a six-module Maven reactor aligned with `attendance-server-*`, while preserving the current auth/login behavior and moving all code to `com.attendance.server.*`.

**Architecture:** Keep `backend/` as the parent reactor and split runtime code into `starter -> interfaces -> application -> infrastructure -> domain -> shared`. Move current auth functionality into those modules and remove the old single-module source tree.

**Tech Stack:** Spring Boot 3.2, Java 17, MyBatis-Plus, Spring Security, JWT, JUnit 5, Mockito

---

### Task 1: Lock The Target Structure With Failing Tests

**Files:**
- Modify: `backend/src/test/java/com/attendance/architecture/BackendLayeringStructureTest.java`
- Modify: `backend/src/test/java/com/attendance/AttendanceSystemApplicationTests.java`

- [ ] **Step 1: Rewrite the architecture test to assert the multi-module reactor target**

Key assertions to add:

```java
assertTrue(Files.exists(Path.of("attendance-server-starter", "pom.xml")));
assertTrue(Files.exists(Path.of("attendance-server-interfaces", "pom.xml")));
assertTrue(Files.exists(Path.of("attendance-server-application", "pom.xml")));
assertTrue(Files.exists(Path.of("attendance-server-domain", "pom.xml")));
assertTrue(Files.exists(Path.of("attendance-server-infrastructure", "pom.xml")));
assertTrue(Files.exists(Path.of("attendance-server-shared", "pom.xml")));
```

- [ ] **Step 2: Rewrite class-name assertions to the new package root**

Examples:

```java
Class.forName("com.attendance.server.starter.AttendanceServerApplication");
Class.forName("com.attendance.server.interfaces.rest.auth.AuthController");
Class.forName("com.attendance.server.application.auth.AuthApplicationService");
Class.forName("com.attendance.server.domain.user.entity.User");
```

- [ ] **Step 3: Run the focused test and confirm it fails for the expected reason**

Run:

```bash
set JAVA_HOME=%USERPROFILE%\\.jdks\\ms-21.0.10 && mvn -pl backend -Dtest=BackendLayeringStructureTest test
```

Expected: missing module paths and/or missing new package names.

### Task 2: Convert `backend/pom.xml` Into The Reactor Parent

**Files:**
- Modify: `backend/pom.xml`
- Create: `backend/attendance-server-shared/pom.xml`
- Create: `backend/attendance-server-domain/pom.xml`
- Create: `backend/attendance-server-infrastructure/pom.xml`
- Create: `backend/attendance-server-application/pom.xml`
- Create: `backend/attendance-server-interfaces/pom.xml`
- Create: `backend/attendance-server-starter/pom.xml`

- [ ] **Step 1: Change the current backend POM into a `pom` packaging parent**
- [ ] **Step 2: Move current dependency/plugin management up to the parent where appropriate**
- [ ] **Step 3: Add all six child modules**

Expected module list:

```xml
<modules>
    <module>attendance-server-shared</module>
    <module>attendance-server-domain</module>
    <module>attendance-server-infrastructure</module>
    <module>attendance-server-application</module>
    <module>attendance-server-interfaces</module>
    <module>attendance-server-starter</module>
</modules>
```

- [ ] **Step 4: Add child POM dependencies to match the designed dependency direction**

### Task 3: Move Shared And Domain Code

**Files:**
- Create/Move: `backend/attendance-server-shared/src/main/java/com/attendance/server/shared/...`
- Create/Move: `backend/attendance-server-domain/src/main/java/com/attendance/server/domain/...`

- [ ] **Step 1: Move `ApiResponse` and shared exception types into the shared module**
- [ ] **Step 2: Move `BaseEntity` and `User` into the domain module**
- [ ] **Step 3: Introduce the domain-facing user service contract under `domain.user.service`**

Example contract:

```java
public interface UserService extends IService<User> {
    User findByUsername(String username);
}
```

- [ ] **Step 4: Run the architecture test again and confirm module/class assertions get further**

### Task 4: Move Infrastructure And Application Code

**Files:**
- Create/Move: `backend/attendance-server-infrastructure/src/main/java/com/attendance/server/infrastructure/...`
- Create/Move: `backend/attendance-server-application/src/main/java/com/attendance/server/application/...`

- [ ] **Step 1: Move mapper, security, and config classes into infrastructure**
- [ ] **Step 2: Move the user service implementation into infrastructure or keep it in domain only if dependencies stay valid**
- [ ] **Step 3: Move login orchestration into the application module with updated imports**
- [ ] **Step 4: Ensure application depends only on what it actually needs after the split**

### Task 5: Move Interfaces And Starter Code

**Files:**
- Create/Move: `backend/attendance-server-interfaces/src/main/java/com/attendance/server/interfaces/...`
- Create/Move: `backend/attendance-server-starter/src/main/java/com/attendance/server/starter/...`
- Create/Move: `backend/attendance-server-starter/src/main/resources/application.yml`

- [ ] **Step 1: Move controller and auth DTOs into interfaces**
- [ ] **Step 2: Move the global HTTP exception handler to interfaces or keep it in shared only if startup wiring remains clear**
- [ ] **Step 3: Create the new `AttendanceServerApplication` in starter**
- [ ] **Step 4: Point `@MapperScan` at `com.attendance.server.infrastructure.persistence.mapper`**

### Task 6: Move And Fix Tests Module-By-Module

**Files:**
- Move tests into the module that owns the code under test

- [ ] **Step 1: Put starter architecture/context tests under `attendance-server-starter`**
- [ ] **Step 2: Put controller tests under `attendance-server-interfaces`**
- [ ] **Step 3: Put application service tests under `attendance-server-application`**
- [ ] **Step 4: Put security/config tests under `attendance-server-infrastructure`**
- [ ] **Step 5: Keep SQL script tests in the module that owns the SQL resource path or adapt paths explicitly**

### Task 7: Remove The Old Single-Module Source Tree

**Files:**
- Delete old source/test directories under `backend/src/...` after replacement exists

- [ ] **Step 1: Verify replacement files exist in new modules**
- [ ] **Step 2: Remove obsolete `backend/src/main/java/...` classes**
- [ ] **Step 3: Remove obsolete `backend/src/test/java/...` tests that were relocated**

### Task 8: Verify And Update Project Docs

**Files:**
- Modify: `docs/task-list.md`
- Modify: `CHANGELOG.md`
- Modify: `session-handoff.md`
- Modify: `docs/README.md` if needed
- Modify: `.harness/learnings.md` if new lessons are worth retaining

- [ ] **Step 1: Run reactor tests**

```bash
set JAVA_HOME=%USERPROFILE%\\.jdks\\ms-21.0.10 && mvn -f backend/pom.xml test
```

- [ ] **Step 2: Run reactor checkstyle**

```bash
set JAVA_HOME=%USERPROFILE%\\.jdks\\ms-21.0.10 && mvn -f backend/pom.xml checkstyle:check
```

- [ ] **Step 3: Update task/documentation status**
- [ ] **Step 4: Review `git status` before commit**

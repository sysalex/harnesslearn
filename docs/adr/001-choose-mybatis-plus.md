# ADR-001: 选择 MyBatis-Plus 而非 JPA

**日期**: 2026-04-22  
**状态**: 已接受  
**上下文**: 技术选型

---

## 背景

考勤系统需要选择合适的 ORM 框架。主要候选方案：
- JPA (Hibernate)
- MyBatis-Plus

## 决策

**选择 MyBatis-Plus**

## 理由

1. **复杂查询支持更好**
   - 考勤报表涉及多表关联、分组统计
   - MyBatis 的 SQL 控制更灵活
   - 支持 WHERE 条件下推、UNION 查询优化

2. **性能可控**
   - 可以直接编写优化后的 SQL
   - 避免 JPA 的 N+1 查询问题
   - 慢查询更容易定位和优化

3. **学习成本低**
   - 团队对 MyBatis 更熟悉
   - SQL 可见，调试方便
   - 符合开发者的 SQL 优化偏好

4. **生态成熟**
   - MyBatis-Plus 提供 CRUD 封装
   - 代码生成器减少样板代码
   - 分页插件、性能分析插件完善

## 后果

### 正面
- ✅ SQL 完全可控，便于优化
- ✅ 复杂查询实现简单
- ✅ 性能调优直观

### 负面
- ❌ 需要手写 SQL（相比 JPA 的注解方式）
- ❌ 数据库迁移需要手动管理（无自动 DDL）
- ❌ 切换数据库需要修改 SQL

## 参考

- [MyBatis-Plus 官方文档](https://baomidou.com/)
- [MyBatis vs JPA 对比](https://www.baeldung.com/mybatis-vs-jpa)

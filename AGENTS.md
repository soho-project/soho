# 项目信息

- 项目名称：soho 后端
- 技术栈：Spring Boot
- 数据库：MySQL
- 代码风格：Clean Code（高内聚、低耦合、可读性优先）
- 返回内容：统一中文说明
- 方法规范：所有方法必须添加中文注释（说明用途、参数、返回值）

---

# 前端项目路径

## React（主推版本）
/home/fang/work/html/soho-admin-v2/soho-admin-v2

## Vue 版本
/home/fang/work/html/soho-admin-v2/soho-admin-vue-v2

## AI 用户前端
/home/fang/work/html/soho/soho-ai-front

---

# 代码规范（必须遵守）

## 注释规范
- 类 / 接口 / Controller / Service / Mapper 必须中文注释
- 所有方法必须中文注释
- 关键逻辑必须写行内注释

## 返回规范
- 接口必须返回中文说明
- 错误信息必须清晰明确

## 代码风格
- 单一职责原则
- 避免重复代码
- 方法 ≤ 50 行
- 命名语义化（禁止拼音）

---

# 模块设计规范

## 核心原则：完全解耦

禁止业务模块互相直接依赖

### 标准结构
user-module
order-module
user-api
order-api
app（统一入口）

### 调用规则
❌ user-module → order-module  
✅ user-module → order-api

### 装配规则
所有模块统一在 app 入口装配

---

# SQL 规范

路径：
docs/sql/

命名：
YYYY-MM-DD-功能说明.sql

示例：
2026-04-19-user-table-add-column.sql

要求：
- 必须带注释
- 尽量幂等

---

# AI 开发约束

1. 优先可维护性
2. 禁止跨模块直接调用实现类
3. 必须符合模块规范
4. 必须生成 SQL（如涉及数据库）
5. 必须中文注释
6. 禁止低质量代码

---

# 推荐流程

1. 分析需求
2. 确定模块
3. 判断 API 模块
4. 设计 DTO/VO
5. 写 Service/Controller
6. 写 SQL
7. 补注释
8. 检查依赖
# SOHO 学习文档

本文档目录用于汇总项目中的开发说明、规范约束与通用能力说明，建议新同学先阅读“核心开发指南”，再按业务需要查阅专题文档。

## 入口导航

- [核心开发指南](#核心开发指南)
- [功能与基础能力](#功能与基础能力)
- [其他文档入口](#其他文档入口)

## 核心开发指南

- [项目架构](architecture.md)
- [项目规范](project-standard.md)
- [SOHO 项目开发规范](soho-standard.md)
- [鉴权与安全](security.md)
- [OAuth2（系统级说明）](oauth2.md)
- [版本管理](version.md)
- [单元测试](test.md)
- [Maven Archetype 工具](archetype.md)

## 功能与基础能力

### 配置与基础设施

- [系统配置服务](sys-config.md)
- [云服务](cloud.md)
- [分布式锁](lock.md)
- [限流器](ratelimiter.md)
- [延时队列](delayed-queue.md)

### 文件与上传

- [上传通用指南](upload.md)
- [文件上传服务](fileupload-service.md)
- [文件服务（soho-upload）](uploadFile.md)
- [Excel 操作](excel.md)

### 验证与消息能力

- [验证码](captcha.md)
- [短信发送](sms.md)
- [短信服务](sms-service.md)
- [邮件服务](email-service.md)

## 其他文档入口

- [数据库文档](../databases)
- [Docker 文档](../docker)

## 说明

- `main.md` 作为历史入口文件保留，内容与本页保持一致的导航职责。
- `upload.md` 偏通用接入说明，`uploadFile.md` 偏 `soho-upload` 模块实现说明。

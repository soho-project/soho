# soho-content-biz API 对接文档

本文档基于当前控制器源码整理，包含接口路径与请求/响应示例。

## 通用约定

- 统一响应：多数接口返回 `R<T>`，结构通常如下：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

- 分页参数：PageHelper 读取 `pageNum` / `pageSize`。
- 时间区间：`startTime` / `endTime`（Query 参数）。
- 权限范围：
- `/content/admin/**`：管理员权限。
- `/content/user/**`：登录用户权限。
- `/content/guest/**` 与 `/wp-json/wp/v2/**`：公开或弱鉴权接口。

---

## 一、管理员接口（/content/admin）

### 1. 内容（ContentInfo）

Base Path: `/content/admin/contentInfo`

- `GET /list`
- `GET /{id}`
- `POST /`
- `PUT /`
- `DELETE /{ids}`
- `GET /options`
- `GET /exportExcel`
- `POST /importExcel`

请求示例：

```http
GET /content/admin/contentInfo/list?pageNum=1&pageSize=10&title=Spring
```

响应示例（示意）：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 1,
    "rows": [
      { "id": 101, "title": "Spring 入门" }
    ]
  }
}
```

新增示例：

```http
POST /content/admin/contentInfo/
Content-Type: application/json

{
  "title": "新的文章",
  "body": "<p>正文</p>",
  "description": "摘要",
  "categoryId": 10,
  "status": 1
}
```

---

### 2. 内容分类（ContentCategory）

Base Path: `/content/admin/contentCategory`

- `GET /list`
- `GET /{id}`
- `POST /`
- `PUT /`
- `DELETE /{ids}`
- `GET /tree`
- `GET /exportExcel`
- `POST /importExcel`

树形示例：

```http
GET /content/admin/contentCategory/tree
```

---

### 3. 内容标签（ContentTag）

Base Path: `/content/admin/contentTag`

- `GET /list`
- `GET /{id}`
- `POST /`
- `PUT /`
- `DELETE /{ids}`
- `GET /exportExcel`
- `POST /importExcel`

请求示例：

```http
GET /content/admin/contentTag/list?pageNum=1&pageSize=10&name=Spring
```

响应示例（示意）：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 1,
    "rows": [
      { "id": 1, "name": "Spring", "slug": "spring" }
    ]
  }
}
```

---

### 4. 媒体资源（ContentMedia）

Base Path: `/content/admin/contentMedia`

- `GET /list`
- `GET /{id}`
- `POST /`
- `PUT /`
- `DELETE /{ids}`
- `GET /exportExcel`
- `POST /importExcel`

请求示例：

```http
GET /content/admin/contentMedia/list?pageNum=1&pageSize=10
```

响应示例（示意）：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 1,
    "rows": [
      { "id": 1, "title": "封面图", "url": "https://cdn.example.com/a.png" }
    ]
  }
}
```

---

### 5. 评论（ContentComment）

Base Path: `/content/admin/contentComment`

- `GET /list`
- `GET /{id}`
- `POST /`
- `PUT /`
- `DELETE /{ids}`
- `GET /exportExcel`
- `POST /importExcel`

请求示例：

```http
GET /content/admin/contentComment/list?pageNum=1&pageSize=10&contentId=101
```

响应示例（示意）：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 1,
    "rows": [
      { "id": 1, "contentId": 101, "authorName": "Alice", "content": "写得很好" }
    ]
  }
}
```

---

### 6. WordPress 导入导出（管理）

Base Path: `/content/admin/wordpress`

- `POST /import`
- `POST /import-wxr`
- `GET /export`
- `GET /export-wxr`

导入示例：

```http
POST /content/admin/wordpress/import
Content-Type: application/json

{
  "posts": true,
  "pages": true,
  "categories": true,
  "tags": true,
  "media": true,
  "users": false,
  "comments": true,
  "page": 1,
  "perPage": 50,
  "upsert": true,
  "wordpress": {
    "baseUrl": "https://your-wp-site.com",
    "username": "admin",
    "appPassword": "xxxx xxxx xxxx xxxx"
  }
}
```

---

## 二、用户接口（/content/user）

### 1. 内容（ContentInfo）

Base Path: `/content/user/contentInfo`

- `GET /list`
- `GET /{id}`
- `POST /`
- `PUT /`
- `DELETE /{ids}`
- `GET /options`

### 2. 标签（ContentTag）

Base Path: `/content/user/contentTag`

- `GET /list`
- `GET /{id}`
- `POST /`
- `PUT /`
- `DELETE /{ids}`

请求示例：

```http
GET /content/user/contentTag/list?pageNum=1&pageSize=10
```

响应示例（示意）：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 1,
    "rows": [
      { "id": 1, "name": "Spring", "slug": "spring" }
    ]
  }
}
```

### 3. 媒体（ContentMedia）

Base Path: `/content/user/contentMedia`

- `GET /list`
- `GET /{id}`
- `POST /`
- `PUT /`
- `DELETE /{ids}`

请求示例：

```http
GET /content/user/contentMedia/list?pageNum=1&pageSize=10
```

响应示例（示意）：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 1,
    "rows": [
      { "id": 1, "title": "封面图", "url": "https://cdn.example.com/a.png" }
    ]
  }
}
```

### 4. 评论（ContentComment）

Base Path: `/content/user/contentComment`

- `GET /list`
- `GET /{id}`
- `POST /`
- `PUT /`
- `DELETE /{ids}`

请求示例：

```http
GET /content/user/contentComment/list?pageNum=1&pageSize=10&contentId=101
```

响应示例（示意）：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 1,
    "rows": [
      { "id": 1, "contentId": 101, "authorName": "Alice", "content": "写得很好" }
    ]
  }
}
```

---

## 三、游客接口（/content/guest）

### 1. 内容（ContentInfo）

Base Path: `/content/guest/contentInfo`

- `GET /list`
- `GET /{id}`
- `GET /content`
- `POST /like`
- `POST /disLike`

内容详情示例：

```http
GET /content/guest/contentInfo/content?id=101
```

点赞示例：

```http
POST /content/guest/contentInfo/like
Content-Type: application/json

{ "id": 101 }
```

### 2. 分类（ContentCategory）

Base Path: `/content/guest/contentCategory`

- `GET /tree`
- `GET /category`
- `GET /nav`

### 3. 标签（ContentTag）

Base Path: `/content/guest/contentTag`

- `GET /list`
- `GET /{id}`

请求示例：

```http
GET /content/guest/contentTag/list?pageNum=1&pageSize=10
```

响应示例（示意）：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 1,
    "rows": [
      { "id": 1, "name": "Spring", "slug": "spring" }
    ]
  }
}
```

### 4. 媒体（ContentMedia）

Base Path: `/content/guest/contentMedia`

- `GET /list`
- `GET /{id}`

请求示例：

```http
GET /content/guest/contentMedia/list?pageNum=1&pageSize=10
```

响应示例（示意）：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 1,
    "rows": [
      { "id": 1, "title": "封面图", "url": "https://cdn.example.com/a.png" }
    ]
  }
}
```

### 5. 评论（ContentComment）

Base Path: `/content/guest/contentComment`

- `GET /list`
- `GET /{id}`
- `POST /`

请求示例：

```http
GET /content/guest/contentComment/list?pageNum=1&pageSize=10&contentId=101
```

响应示例（示意）：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 1,
    "rows": [
      { "id": 1, "contentId": 101, "authorName": "Alice", "content": "写得很好" }
    ]
  }
}
```

新增评论示例：

```http
POST /content/guest/contentComment/
Content-Type: application/json

{
  "contentId": 101,
  "parentId": 0,
  "authorName": "Alice",
  "authorEmail": "alice@example.com",
  "content": "写得很好",
  "status": "approved"
}
```

### 6. 游客-管理员内容（AdminContent）

Base Path: `/content/guest/adminContent`

- `GET /list`
- `GET /{id}`

请求示例：

```http
GET /content/guest/adminContent/list?pageNum=1&pageSize=10
```

响应示例（示意）：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 1,
    "rows": [
      { "id": 101, "title": "Spring 入门" }
    ]
  }
}
```

### 7. RSS Feed

Base Path: `/content/guest/feed`

- `GET /rss`（`application/rss+xml`）

请求示例：

```http
GET /content/guest/feed/rss
```

---

## 四、WordPress 兼容 REST（/wp-json/wp/v2）

用于兼容 WordPress REST API 的只读接口（分页参数：`page`、`per_page`）。

- `GET /posts`
- `GET /pages`
- `GET /categories`
- `GET /tags`
- `GET /media`
- `GET /users`
- `GET /comments`

请求示例：

```http
GET /wp-json/wp/v2/posts?page=1&per_page=10
```

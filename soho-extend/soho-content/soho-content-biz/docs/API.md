# soho-content-biz API 对接文档

本文件汇总 `soho-content-biz` 模块对外提供的主要 REST 接口，按访问角色分组。  
若接口返回为 `R<T>`，代表统一响应包装（`code/message/data`），具体字段以公共模块定义为准。

## 通用约定

- **分页参数**：使用 `PageUtils.startPage()`，通常由 PageHelper 读取 `pageNum` / `pageSize`（如需兼容前端请按实际约定传递）。
- **时间区间**：`BetweenCreatedTimeRequest` 支持 `startTime` / `endTime`（Query 参数）。
- **权限**：
  - `/content/admin/**`：需要管理员权限（有 `@Node` 标记）。
  - `/content/user/**`：需要登录用户权限。
  - `/content/guest/**` 与 `/wp-json/wp/v2/**`：公开或弱鉴权接口。

---

## 一、管理员接口（/content/admin）

### 1. 内容（ContentInfo）

Base Path: `/content/admin/contentInfo`

- `GET /list`：分页列表  
  Query：ContentInfo 字段 + `startTime/endTime`
- `GET /{id}`：详情  
- `POST /`：新增  
  Body：`ContentInfo`
- `PUT /`：更新  
  Body：`ContentInfo`
- `DELETE /{ids}`：删除（批量）  
  Path：逗号分隔 ID
- `GET /options`：下拉选项（value=id,label=title）
- `GET /exportExcel`：导出 Excel（同列表过滤条件）
- `POST /importExcel`：导入 Excel  
  Form：`file`

### 2. 内容分类（ContentCategory）

Base Path: `/content/admin/contentCategory`

- `GET /list`：分页列表  
  Query：ContentCategory 字段 + `startTime/endTime`
- `GET /{id}`：详情  
- `POST /`：新增  
  Body：`ContentCategory`
- `PUT /`：更新  
  Body：`ContentCategory`
- `DELETE /{ids}`：删除（批量，带子节点检查）
- `GET /tree`：分类树（TreeNodeVo）
- `GET /exportExcel`：导出 Excel
- `POST /importExcel`：导入 Excel  
  Form：`file`

### 3. 内容标签（ContentTag）

Base Path: `/content/admin/contentTag`

- `GET /list`
- `GET /{id}`
- `POST /`
- `PUT /`
- `DELETE /{ids}`
- `GET /exportExcel`
- `POST /importExcel`（Form：`file`）

### 4. 媒体资源（ContentMedia）

Base Path: `/content/admin/contentMedia`

- `GET /list`
- `GET /{id}`
- `POST /`
- `PUT /`
- `DELETE /{ids}`
- `GET /exportExcel`
- `POST /importExcel`（Form：`file`）

### 5. 评论（ContentComment）

Base Path: `/content/admin/contentComment`

- `GET /list`
- `GET /{id}`
- `POST /`
- `PUT /`
- `DELETE /{ids}`
- `GET /exportExcel`
- `POST /importExcel`（Form：`file`）

### 6. WordPress 导入导出（管理）

Base Path: `/content/admin/wordpress`

#### 6.1 导入（对接 WordPress REST API）

`POST /import`  
Body：`WordPressSyncRequest`

```json
{
  "posts": true,
  "pages": true,
  "categories": true,
  "tags": true,
  "media": true,
  "users": true,
  "comments": true,
  "page": 1,
  "perPage": 50,
  "upsert": true
}
```

响应：`WordPressSyncResult`

#### 6.2 导入（WXR 文件）

`POST /import-wxr`  
Form：`file`（WordPress 导出 XML）

响应：`WordPressWxrImportResult`

#### 6.3 导出

`GET /export`  
响应：`WordPressExportResult`

#### 6.4 导出（WXR XML）

`GET /export-wxr`  
返回：`application/xml`，用于 WordPress 后台“工具 -> 导入 -> WordPress”直接导入

---

## 二、用户接口（/content/user）

### 1. 内容

Base Path: `/content/user/contentInfo`

- `GET /list`
- `GET /{id}`
- `POST /`
- `PUT /`
- `DELETE /{ids}`
- `GET /options`

### 2. 标签

Base Path: `/content/user/contentTag`

- `GET /list`
- `GET /{id}`
- `POST /`
- `PUT /`
- `DELETE /{ids}`

### 3. 媒体

Base Path: `/content/user/contentMedia`

- `GET /list`
- `GET /{id}`
- `POST /`
- `PUT /`
- `DELETE /{ids}`

### 4. 评论

Base Path: `/content/user/contentComment`

- `GET /list`
- `GET /{id}`
- `POST /`
- `PUT /`
- `DELETE /{ids}`

---

## 三、游客接口（/content/guest）

### 1. 站点内容接口

Base Path: `/content/guest/api/content`

- `GET /hello`：健康检查
- `GET /list`：已发布内容列表
- `GET /category?id=...`：分类及导航
- `GET /nav`：全量导航树
- `GET /content?id=...`：内容详情
- `POST /like`：点赞  
  Body：`{ "id": 123 }`
- `POST /disLike`：点踩  
  Body：`{ "id": 123 }`

### 2. 游客-管理员内容

Base Path: `/content/guest/adminContent`

- `GET /list`
- `GET /{id}`

### 3. 游客-管理员分类树

Base Path: `/content/guest/adminContentCategory`

- `GET /tree`

### 4. 游客-内容/标签/媒体/评论

- `/content/guest/contentInfo`：`GET /list`、`GET /{id}`
- `/content/guest/contentTag`：`GET /list`、`GET /{id}`
- `/content/guest/contentMedia`：`GET /list`、`GET /{id}`
- `/content/guest/contentComment`：`GET /list`、`GET /{id}`

---

## 四、WordPress 兼容 REST（/wp-json/wp/v2）

用于兼容 WordPress REST API 的只读接口（分页参数：`page`、`per_page`）：

- `GET /posts`
- `GET /pages`
- `GET /categories`
- `GET /tags`
- `GET /media`
- `GET /users`
- `GET /comments`

---

## 五、文件上传示例（WXR 导入）

```bash
curl -X POST \\
  -H "Authorization: Bearer <token>" \\
  -F "file=@/path/WordPress.xml" \\
  http://<host>/content/admin/wordpress/import-wxr
```

## 六、导出 WXR 示例

```bash
curl -L -o wordpress-export.xml \\
  -H "Authorization: Bearer <token>" \\
  http://<host>/content/admin/wordpress/export-wxr
```

---

如需补充字段说明、示例响应或错误码，请告知具体接口。  

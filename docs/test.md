# 开放平台 API 接口文档

- 生成时间: 2026-01-13 22:37:14

## AdminConfigController

### 1. add

- 方法: `POST`
- 路径: `/admin/admin/adminConfig`
- 返回: `R<Boolean>`

#### Body(JSON)

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminConfig` | true | - | `JSON: AdminConfig` |

**Body 字段说明（adminConfig）**

- `id` (Long, 必填: false): ID
- `groupKey` (String, 必填: false): 配置文件分组名
- `key` (String, 必填: false): 配置信息唯一识别key
- `value` (String, 必填: false): 配置信息值
- `explain` (String, 必填: false): 说明
- `type` (Integer, 必填: false): 配置信息类型
- `updatedTime` (LocalDateTime, 必填: false): 更新时间
- `createdTime` (LocalDateTime, 必填: false): 创建时间


---

### 2. edit

- 方法: `PUT`
- 路径: `/admin/admin/adminConfig`
- 返回: `R<Boolean>`

#### Body(JSON)

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminConfig` | true | - | `JSON: AdminConfig` |

**Body 字段说明（adminConfig）**

- `id` (Long, 必填: false): ID
- `groupKey` (String, 必填: false): 配置文件分组名
- `key` (String, 必填: false): 配置信息唯一识别key
- `value` (String, 必填: false): 配置信息值
- `explain` (String, 必填: false): 说明
- `type` (Integer, 必填: false): 配置信息类型
- `updatedTime` (LocalDateTime, 必填: false): 更新时间
- `createdTime` (LocalDateTime, 必填: false): 创建时间


---

### 3. common

- 方法: `GET`
- 路径: `/admin/admin/adminConfig/common`
- 返回: `R<Map<String, Object>>`


---

### 4. list

- 方法: `GET`
- 路径: `/admin/admin/adminConfig/list`
- 返回: `R<PageSerializable<AdminConfig>>`

#### Query 参数（GET 参数）

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminConfig` | false | - | `AdminConfig` |


---

### 5. roleConfig

- 方法: `GET`
- 路径: `/admin/admin/adminConfig/roleConfig`
- 返回: `R<Map<String, Object>>`

#### Query 参数（GET 参数）

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `userDetails` | false | - | `SohoUserDetails` |
| `roleName` | false | - | `String` |


---

### 6. remove

- 方法: `DELETE`
- 路径: `/admin/admin/adminConfig/{ids}`
- 返回: `R<Boolean>`

#### Path 参数

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `ids` | true | - | `Long[]` |


---

### 7. getInfo

- 方法: `GET`
- 路径: `/admin/admin/adminConfig/{id}`
- 返回: `R<AdminConfig>`

#### Path 参数

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `id` | true | - | `Long` |


---

## AdminConfigGroupController

### 1. add

- 方法: `POST`
- 路径: `/admin/admin/adminConfigGroup`
- 返回: `R<Boolean>`

#### Body(JSON)

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminConfigGroup` | true | - | `JSON: AdminConfigGroup` |

**Body 字段说明（adminConfigGroup）**

- `id` (Long, 必填: false): ID
- `key` (String, 必填: false): 组识别键
- `name` (String, 必填: false): 组名
- `createdTime` (LocalDateTime, 必填: false): 创建时间


---

### 2. edit

- 方法: `PUT`
- 路径: `/admin/admin/adminConfigGroup`
- 返回: `R<Boolean>`

#### Body(JSON)

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminConfigGroup` | true | - | `JSON: AdminConfigGroup` |

**Body 字段说明（adminConfigGroup）**

- `id` (Long, 必填: false): ID
- `key` (String, 必填: false): 组识别键
- `name` (String, 必填: false): 组名
- `createdTime` (LocalDateTime, 必填: false): 创建时间


---

### 3. list

- 方法: `GET`
- 路径: `/admin/admin/adminConfigGroup/list`
- 返回: `R<Page<AdminConfigGroup>>`

#### Query 参数（GET 参数）

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminConfigGroup` | false | - | `AdminConfigGroup` |


---

### 4. options

- 方法: `GET`
- 路径: `/admin/admin/adminConfigGroup/options`
- 返回: `R<List<OptionVo<String, String>>>`


---

### 5. remove

- 方法: `DELETE`
- 路径: `/admin/admin/adminConfigGroup/{ids}`
- 返回: `R<Boolean>`

#### Path 参数

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `ids` | true | - | `Long[]` |


---

### 6. getInfo

- 方法: `GET`
- 路径: `/admin/admin/adminConfigGroup/{id}`
- 返回: `R<AdminConfigGroup>`

#### Path 参数

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `id` | true | - | `Long` |


---

## AdminDictController

### 1. add

- 方法: `POST`
- 路径: `/admin/admin/adminDict`
- 返回: `R<Boolean>`

#### Body(JSON)

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminDict` | true | - | `JSON: AdminDict` |

**Body 字段说明（adminDict）**

- `id` (Long, 必填: false): 主键
- `parentId` (Long, 必填: false): 父主键
- `code` (String, 必填: false): 字典码
- `dictKey` (Integer, 必填: false): 字典值
- `dictValue` (String, 必填: false): 字典名称
- `sort` (Integer, 必填: false): 排序
- `remark` (String, 必填: false): 备注
- `deleted` (Integer, 必填: false): 删除标志
- `updatedTime` (Date, 必填: false): 更新时间
- `createdTime` (Date, 必填: false): 创建时间


---

### 2. edit

- 方法: `PUT`
- 路径: `/admin/admin/adminDict`
- 返回: `R<Boolean>`

#### Body(JSON)

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminDict` | true | - | `JSON: AdminDict` |

**Body 字段说明（adminDict）**

- `id` (Long, 必填: false): 主键
- `parentId` (Long, 必填: false): 父主键
- `code` (String, 必填: false): 字典码
- `dictKey` (Integer, 必填: false): 字典值
- `dictValue` (String, 必填: false): 字典名称
- `sort` (Integer, 必填: false): 排序
- `remark` (String, 必填: false): 备注
- `deleted` (Integer, 必填: false): 删除标志
- `updatedTime` (Date, 必填: false): 更新时间
- `createdTime` (Date, 必填: false): 创建时间


---

### 3. list

- 方法: `GET`
- 路径: `/admin/admin/adminDict/list`
- 返回: `R<PageSerializable<AdminDict>>`

#### Query 参数（GET 参数）

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminDict` | false | - | `AdminDict` |


---

### 4. options

- 方法: `GET`
- 路径: `/admin/admin/adminDict/options`
- 返回: `R<Map<Integer, String>>`

#### Query 参数（GET 参数）

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `code` | true | - | `String` |


---

### 5. remove

- 方法: `DELETE`
- 路径: `/admin/admin/adminDict/{ids}`
- 返回: `R<Boolean>`

#### Path 参数

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `ids` | true | - | `Long[]` |


---

### 6. getInfo

- 方法: `GET`
- 路径: `/admin/admin/adminDict/{id}`
- 返回: `R<AdminDict>`

#### Path 参数

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `id` | true | - | `Long` |


---

## AdminEmailTemplateController

### 1. add

- 方法: `POST`
- 路径: `/admin/admin/adminEmailTemplate`
- 返回: `R<Boolean>`

#### Body(JSON)

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminEmailTemplate` | true | - | `JSON: AdminEmailTemplate` |

**Body 字段说明（adminEmailTemplate）**

- `id` (Integer, 必填: false): ID
- `name` (String, 必填: false): 模板名;;isFilter:true
- `title` (String, 必填: false): 邮件标题;;isFilter:true
- `body` (String, 必填: false): 邮件内容;;isFilter:true
- `updatedTime` (LocalDateTime, 必填: false): 更新时间
- `createdTime` (LocalDateTime, 必填: false): 创建时间


---

### 2. edit

- 方法: `PUT`
- 路径: `/admin/admin/adminEmailTemplate`
- 返回: `R<Boolean>`

#### Body(JSON)

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminEmailTemplate` | true | - | `JSON: AdminEmailTemplate` |

**Body 字段说明（adminEmailTemplate）**

- `id` (Integer, 必填: false): ID
- `name` (String, 必填: false): 模板名;;isFilter:true
- `title` (String, 必填: false): 邮件标题;;isFilter:true
- `body` (String, 必填: false): 邮件内容;;isFilter:true
- `updatedTime` (LocalDateTime, 必填: false): 更新时间
- `createdTime` (LocalDateTime, 必填: false): 创建时间


---

### 3. list

- 方法: `GET`
- 路径: `/admin/admin/adminEmailTemplate/list`
- 返回: `R<PageSerializable<AdminEmailTemplate>>`

#### Query 参数（GET 参数）

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminEmailTemplate` | false | - | `AdminEmailTemplate` |
| `betweenCreatedTimeRequest` | false | - | `BetweenCreatedTimeRequest` |


---

### 4. remove

- 方法: `DELETE`
- 路径: `/admin/admin/adminEmailTemplate/{ids}`
- 返回: `R<Boolean>`

#### Path 参数

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `ids` | true | - | `Long[]` |


---

### 5. getInfo

- 方法: `GET`
- 路径: `/admin/admin/adminEmailTemplate/{id}`
- 返回: `R<AdminEmailTemplate>`

#### Path 参数

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `id` | true | - | `Long` |


---

## AdminNotificationController

### 1. add

- 方法: `POST`
- 路径: `/admin/admin/adminNotification`
- 返回: `R<Boolean>`

#### Query 参数（GET 参数）

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `userDetails` | false | - | `SohoUserDetails` |

#### Body(JSON)

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminNotificationCreateRequest` | true | - | `JSON: AdminNotificationCreateRequest` |

**Body 字段说明（adminNotificationCreateRequest）**

- `title` (String, 必填: false): -
- `content` (String, 必填: false): -
- `adminUserId` (Long, 必填: false): -
- `adminUserIds` (ArrayList<Long>, 必填: false): -


---

### 2. edit

- 方法: `PUT`
- 路径: `/admin/admin/adminNotification`
- 返回: `R<Boolean>`

#### Body(JSON)

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminNotification` | true | - | `JSON: AdminNotification` |

**Body 字段说明（adminNotification）**

- `id` (Long, 必填: false): ID
- `adminUserId` (Long, 必填: false): 接收人
- `title` (String, 必填: false): 标题
- `createAdminUserId` (Long, 必填: false): 创建者 0 为系统发送
- `content` (String, 必填: false): 通知内容
- `createdTime` (LocalDateTime, 必填: false): 创建时间
- `isRead` (Integer, 必填: false): 是否已读 0 未读 1 已读


---

### 3. list

- 方法: `GET`
- 路径: `/admin/admin/adminNotification/list`
- 返回: `R<PageSerializable<AdminNotificationVo>>`

#### Query 参数（GET 参数）

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminNotification` | false | - | `AdminNotification` |


---

### 4. myNotification

- 方法: `GET`
- 路径: `/admin/admin/adminNotification/myNotification`
- 返回: `R<PageSerializable<AdminNotificationVo>>`

#### Query 参数（GET 参数）

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `userDetails` | false | - | `SohoUserDetails` |


---

### 5. 已读消息标记

- 方法: `GET`
- 路径: `/admin/admin/adminNotification/read/{ids}`
- 返回: `R<Boolean>`

#### Path 参数

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `ids` | true | - | `Long[]` |

#### Query 参数（GET 参数）

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `userDetails` | false | - | `SohoUserDetails` |


---

### 6. 已读消息标记

- 方法: `GET`
- 路径: `/admin/admin/adminNotification/readAll`
- 返回: `R<Boolean>`


---

### 7. remove

- 方法: `DELETE`
- 路径: `/admin/admin/adminNotification/{ids}`
- 返回: `R<Boolean>`

#### Path 参数

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `ids` | true | - | `Long[]` |


---

### 8. getInfo

- 方法: `GET`
- 路径: `/admin/admin/adminNotification/{id}`
- 返回: `R<AdminNotification>`

#### Path 参数

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `id` | true | - | `Long` |


---

## AdminOperationLogController

### 1. add

- 方法: `POST`
- 路径: `/admin/admin/adminOperationLog`
- 返回: `R<Boolean>`

#### Body(JSON)

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminOperationLog` | true | - | `JSON: AdminOperationLog` |

**Body 字段说明（adminOperationLog）**

- `id` (Integer, 必填: false): ID
- `adminUserId` (Long, 必填: false): 管理员;;frontType:select,foreign:admin_user.id~username,isFilter:true
- `method` (String, 必填: false): 请求方法;GET:GET,POST:POST,DELETE:DELETE,PUT:PUT;frontType:select,
- `path` (String, 必填: false): 请求路径
- `params` (String, 必填: false): 请求参数
- `content` (String, 必填: false): 操作内容;;
- `response` (String, 必填: false): 返回数据
- `updatedTime` (LocalDateTime, 必填: false): 更新时间
- `createdTime` (LocalDateTime, 必填: false): 创建时间


---

### 2. edit

- 方法: `PUT`
- 路径: `/admin/admin/adminOperationLog`
- 返回: `R<Boolean>`

#### Body(JSON)

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminOperationLog` | true | - | `JSON: AdminOperationLog` |

**Body 字段说明（adminOperationLog）**

- `id` (Integer, 必填: false): ID
- `adminUserId` (Long, 必填: false): 管理员;;frontType:select,foreign:admin_user.id~username,isFilter:true
- `method` (String, 必填: false): 请求方法;GET:GET,POST:POST,DELETE:DELETE,PUT:PUT;frontType:select,
- `path` (String, 必填: false): 请求路径
- `params` (String, 必填: false): 请求参数
- `content` (String, 必填: false): 操作内容;;
- `response` (String, 必填: false): 返回数据
- `updatedTime` (LocalDateTime, 必填: false): 更新时间
- `createdTime` (LocalDateTime, 必填: false): 创建时间


---

### 3. list

- 方法: `GET`
- 路径: `/admin/admin/adminOperationLog/list`
- 返回: `R<PageSerializable<AdminOperationLog>>`

#### Query 参数（GET 参数）

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminOperationLog` | false | - | `AdminOperationLog` |
| `betweenCreatedTimeRequest` | false | - | `BetweenCreatedTimeRequest` |


---

### 4. remove

- 方法: `DELETE`
- 路径: `/admin/admin/adminOperationLog/{ids}`
- 返回: `R<Boolean>`

#### Path 参数

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `ids` | true | - | `Long[]` |


---

### 5. getInfo

- 方法: `GET`
- 路径: `/admin/admin/adminOperationLog/{id}`
- 返回: `R<AdminOperationLog>`

#### Path 参数

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `id` | true | - | `Long` |


---

## AdminReleaseController

### 1. add

- 方法: `POST`
- 路径: `/admin/admin/adminRelease`
- 返回: `R<Boolean>`

#### Body(JSON)

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminRelease` | true | - | `JSON: AdminRelease` |

**Body 字段说明（adminRelease）**

- `id` (Integer, 必填: false): ID
- `version` (String, 必填: false): 版本号
- `notes` (String, 必填: false): 发版描述
- `createdTime` (LocalDateTime, 必填: false): 创建时间
- `updatedTime` (LocalDateTime, 必填: false): 更新时间
- `url` (String, 必填: false): 下载地址
- `platformType` (Integer, 必填: false): 平台类型;1:Windows,2:Linux;frontType:select,option:platform_typeEnum


---

### 2. edit

- 方法: `PUT`
- 路径: `/admin/admin/adminRelease`
- 返回: `R<Boolean>`

#### Body(JSON)

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminRelease` | true | - | `JSON: AdminRelease` |

**Body 字段说明（adminRelease）**

- `id` (Integer, 必填: false): ID
- `version` (String, 必填: false): 版本号
- `notes` (String, 必填: false): 发版描述
- `createdTime` (LocalDateTime, 必填: false): 创建时间
- `updatedTime` (LocalDateTime, 必填: false): 更新时间
- `url` (String, 必填: false): 下载地址
- `platformType` (Integer, 必填: false): 平台类型;1:Windows,2:Linux;frontType:select,option:platform_typeEnum


---

### 3. list

- 方法: `GET`
- 路径: `/admin/admin/adminRelease/list`
- 返回: `R<PageSerializable<AdminRelease>>`

#### Query 参数（GET 参数）

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminRelease` | false | - | `AdminRelease` |
| `betweenCreatedTimeRequest` | false | - | `BetweenCreatedTimeRequest` |


---

### 4. platformTypeEnumOption

- 方法: `GET`
- 路径: `/admin/admin/adminRelease/queryPlatformTypeEnumOption`
- 返回: `R<List<OptionVo<Integer, String>>>`


---

### 5. uploadAvatar

- 方法: `POST`
- 路径: `/admin/admin/adminRelease/upload-file`
- 返回: `Object`

#### Query 参数（GET 参数）

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `file` | true | - | `MultipartFile` |


---

### 6. remove

- 方法: `DELETE`
- 路径: `/admin/admin/adminRelease/{ids}`
- 返回: `R<Boolean>`

#### Path 参数

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `ids` | true | - | `Long[]` |


---

### 7. getInfo

- 方法: `GET`
- 路径: `/admin/admin/adminRelease/{id}`
- 返回: `R<AdminRelease>`

#### Path 参数

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `id` | true | - | `Long` |


---

## AdminResourceController

### 1. 创建资源

- 方法: `POST`
- 路径: `/admin/admin/adminResource`
- 返回: `R<AdminResource>`

#### Body(JSON)

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminResource` | true | - | `JSON: AdminResource` |

**Body 字段说明（adminResource）**

- `id` (Long, 必填: false): ID
- `name` (String, 必填: false): 英文名
- `route` (String, 必填: false): 路由
- `type` (Integer, 必填: false): 资源类型
- `remarks` (String, 必填: false): 备注
- `createdTime` (Date, 必填: false): 创建时间
- `visible` (Integer, 必填: false): 资源界面是否可见
- `sort` (Integer, 必填: false): 排序
- `breadcrumbParentId` (Long, 必填: false): 父ID
- `zhName` (String, 必填: false): 中文名
- `iconName` (String, 必填: false): 菜单图标


---

### 2. 删除指定资源

- 方法: `DELETE`
- 路径: `/admin/admin/adminResource`
- 返回: `R<Boolean>`

#### Body(JSON)

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminResource` | true | - | `JSON: AdminResource` |

**Body 字段说明（adminResource）**

- `id` (Long, 必填: false): ID
- `name` (String, 必填: false): 英文名
- `route` (String, 必填: false): 路由
- `type` (Integer, 必填: false): 资源类型
- `remarks` (String, 必填: false): 备注
- `createdTime` (Date, 必填: false): 创建时间
- `visible` (Integer, 必填: false): 资源界面是否可见
- `sort` (Integer, 必填: false): 排序
- `breadcrumbParentId` (Long, 必填: false): 父ID
- `zhName` (String, 必填: false): 中文名
- `iconName` (String, 必填: false): 菜单图标


---

### 3. 资源详情

- 方法: `GET`
- 路径: `/admin/admin/adminResource`
- 返回: `R<AdminResource>`

#### Query 参数（GET 参数）

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `id` | false | - | `Long` |


---

### 4. 更新资源

- 方法: `PUT`
- 路径: `/admin/admin/adminResource`
- 返回: `R<Boolean>`

#### Body(JSON)

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminResource` | true | - | `JSON: AdminResource` |

**Body 字段说明（adminResource）**

- `id` (Long, 必填: false): ID
- `name` (String, 必填: false): 英文名
- `route` (String, 必填: false): 路由
- `type` (Integer, 必填: false): 资源类型
- `remarks` (String, 必填: false): 备注
- `createdTime` (Date, 必填: false): 创建时间
- `visible` (Integer, 必填: false): 资源界面是否可见
- `sort` (Integer, 必填: false): 排序
- `breadcrumbParentId` (Long, 必填: false): 父ID
- `zhName` (String, 必填: false): 中文名
- `iconName` (String, 必填: false): 菜单图标


---

### 5. 同步菜单

- 方法: `GET`
- 路径: `/admin/admin/adminResource/admin-resource/sync`
- 返回: `R<Boolean>`


---

### 6. 资源列表

- 方法: `GET`
- 路径: `/admin/admin/adminResource/list`
- 返回: `R<PageSerializable<AdminResource>>`

#### Query 参数（GET 参数）

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminResource` | false | - | `AdminResource` |


---

### 7. routeVoList

- 方法: `GET`
- 路径: `/admin/admin/adminResource/routes`
- 返回: `List<RouteVo>`

#### Query 参数（GET 参数）

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `visible` | false | - | `Integer` |


---

### 8. getResourceTree

- 方法: `GET`
- 路径: `/admin/admin/adminResource/tree`
- 返回: `R<TreeResourceVo>`

#### Query 参数（GET 参数）

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `language` | false | - | `String` |


---

## AdminRoleController

### 1. 新增角色

- 方法: `POST`
- 路径: `/admin/admin/adminRole`
- 返回: `R<Boolean>`

#### Body(JSON)

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminRoleVo` | true | - | `JSON: AdminRoleVo` |

**Body 字段说明（adminRoleVo）**

- `id` (Long, 必填: false): -
- `name` (String, 必填: false): -
- `remarks` (String, 必填: false): -
- `enable` (Integer, 必填: false): -
- `createdTime` (Date, 必填: false): -
- `resourceIds` (List<Long>, 必填: false): -


---

### 2. 更新用户角色

- 方法: `PUT`
- 路径: `/admin/admin/adminRole`
- 返回: `R<Boolean>`

#### Body(JSON)

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminRoleVo` | true | - | `JSON: AdminRoleVo` |

**Body 字段说明（adminRoleVo）**

- `id` (Long, 必填: false): -
- `name` (String, 必填: false): -
- `remarks` (String, 必填: false): -
- `enable` (Integer, 必填: false): -
- `createdTime` (Date, 必填: false): -
- `resourceIds` (List<Long>, 必填: false): -


---

### 3. 角色列表

- 方法: `GET`
- 路径: `/admin/admin/adminRole/list`
- 返回: `R<PageSerializable<AdminRole>>`

#### Query 参数（GET 参数）

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `name` | false | - | `String` |


---

### 4. 角色选项

- 方法: `GET`
- 路径: `/admin/admin/adminRole/option-list`
- 返回: `R<List<OptionsRoleVo>>`

#### Query 参数（GET 参数）

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `name` | false | - | `String` |


---

### 5. 获取角色选中的资源

- 方法: `GET`
- 路径: `/admin/admin/adminRole/roleResources`
- 返回: `R<List<String>>`

#### Query 参数（GET 参数）

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `id` | false | - | `Long` |


---

### 6. 更新角色资源

- 方法: `DELETE`
- 路径: `/admin/admin/adminRole/{ids}`
- 返回: `R<Boolean>`

#### Path 参数

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `ids` | true | - | `Long[]` |


---

## AdminSmsTemplateController

### 1. add

- 方法: `POST`
- 路径: `/admin/admin/adminSmsTemplate`
- 返回: `R<Boolean>`

#### Body(JSON)

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminSmsTemplate` | true | - | `JSON: AdminSmsTemplate` |

**Body 字段说明（adminSmsTemplate）**

- `id` (Integer, 必填: false): ID
- `adapterName` (String, 必填: false): 设配器名称;
- `name` (String, 必填: false): 代码中使用名称;
- `title` (String, 必填: false): 标题;
- `templateCode` (String, 必填: false): 模板编号;
- `signName` (String, 必填: false): 签名;
- `createdTime` (LocalDateTime, 必填: false): 创建时间
- `updatedTime` (LocalDateTime, 必填: false): 更新时间


---

### 2. edit

- 方法: `PUT`
- 路径: `/admin/admin/adminSmsTemplate`
- 返回: `R<Boolean>`

#### Body(JSON)

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminSmsTemplate` | true | - | `JSON: AdminSmsTemplate` |

**Body 字段说明（adminSmsTemplate）**

- `id` (Integer, 必填: false): ID
- `adapterName` (String, 必填: false): 设配器名称;
- `name` (String, 必填: false): 代码中使用名称;
- `title` (String, 必填: false): 标题;
- `templateCode` (String, 必填: false): 模板编号;
- `signName` (String, 必填: false): 签名;
- `createdTime` (LocalDateTime, 必填: false): 创建时间
- `updatedTime` (LocalDateTime, 必填: false): 更新时间


---

### 3. list

- 方法: `GET`
- 路径: `/admin/admin/adminSmsTemplate/list`
- 返回: `R<PageSerializable<AdminSmsTemplate>>`

#### Query 参数（GET 参数）

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminSmsTemplate` | false | - | `AdminSmsTemplate` |
| `betweenCreatedTimeRequest` | false | - | `BetweenCreatedTimeRequest` |


---

### 4. remove

- 方法: `DELETE`
- 路径: `/admin/admin/adminSmsTemplate/{ids}`
- 返回: `R<Boolean>`

#### Path 参数

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `ids` | true | - | `Long[]` |


---

### 5. getInfo

- 方法: `GET`
- 路径: `/admin/admin/adminSmsTemplate/{id}`
- 返回: `R<AdminSmsTemplate>`

#### Path 参数

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `id` | true | - | `Long` |


---

## AdminUserController

### 1. create

- 方法: `POST`
- 路径: `/admin/admin/adminUser`
- 返回: `Object`

#### Body(JSON)

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminUserVo` | true | - | `JSON: AdminUserVo` |

**Body 字段说明（adminUserVo）**

- `id` (Long, 必填: false): -
- `username` (String, 必填: false): -
- `nickName` (String, 必填: false): -
- `avatar` (String, 必填: false): -
- `realName` (String, 必填: false): -
- `phone` (String, 必填: false): -
- `sex` (Integer, 必填: false): -
- `age` (Integer, 必填: false): -
- `email` (String, 必填: false): -
- `password` (String, 必填: false): -
- `updatedTime` (Date, 必填: false): -
- `createdTime` (Date, 必填: false): -
- `roleIds` (List<Long>, 必填: false): -


---

### 2. 用户详细信息

- 方法: `GET`
- 路径: `/admin/admin/adminUser`
- 返回: `R<AdminUserVo>`

#### Query 参数（GET 参数）

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `id` | false | - | `Long` |


---

### 3. update

- 方法: `PUT`
- 路径: `/admin/admin/adminUser`
- 返回: `Object`

#### Body(JSON)

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminUserVo` | true | - | `JSON: AdminUserVo` |

**Body 字段说明（adminUserVo）**

- `id` (Long, 必填: false): -
- `username` (String, 必填: false): -
- `nickName` (String, 必填: false): -
- `avatar` (String, 必填: false): -
- `realName` (String, 必填: false): -
- `phone` (String, 必填: false): -
- `sex` (Integer, 必填: false): -
- `age` (Integer, 必填: false): -
- `email` (String, 必填: false): -
- `password` (String, 必填: false): -
- `updatedTime` (Date, 必填: false): -
- `createdTime` (Date, 必填: false): -
- `roleIds` (List<Long>, 必填: false): -


---

### 4. list

- 方法: `GET`
- 路径: `/admin/admin/adminUser/list`
- 返回: `R<PageSerializable<AdminUserVo>>`

#### Query 参数（GET 参数）

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminUserVo` | false | - | `AdminUserVo` |
| `startDate` | false | - | `Date` |
| `endDate` | false | - | `Date` |


---

### 5. 管理员自己的信息

- 方法: `GET`
- 路径: `/admin/admin/adminUser/myself`
- 返回: `R<AdminUserVo>`


---

### 6. 更新自己信息

- 方法: `PUT`
- 路径: `/admin/admin/adminUser/myself`
- 返回: `Object`

#### Body(JSON)

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminUserVo` | true | - | `JSON: AdminUserVo` |

**Body 字段说明（adminUserVo）**

- `id` (Long, 必填: false): -
- `username` (String, 必填: false): -
- `nickName` (String, 必填: false): -
- `avatar` (String, 必填: false): -
- `realName` (String, 必填: false): -
- `phone` (String, 必填: false): -
- `sex` (Integer, 必填: false): -
- `age` (Integer, 必填: false): -
- `email` (String, 必填: false): -
- `password` (String, 必填: false): -
- `updatedTime` (Date, 必填: false): -
- `createdTime` (Date, 必填: false): -
- `roleIds` (List<Long>, 必填: false): -


---

### 7. 用户选项接口

- 方法: `GET`
- 路径: `/admin/admin/adminUser/options`
- 返回: `R<List<OptionVo<Long, String>>>`


---

### 8. 用户头像上传接口

- 方法: `POST`
- 路径: `/admin/admin/adminUser/upload-avatar`
- 返回: `Object`

#### Query 参数（GET 参数）

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `avatar` | true | - | `MultipartFile` |


---

### 9. 当前登录用户信息

- 方法: `GET`
- 路径: `/admin/admin/adminUser/user`
- 返回: `R<CurrentAdminUserVo>`


---

### 10. 用户选项接口

- 方法: `GET`
- 路径: `/admin/admin/adminUser/userOptions`
- 返回: `R<List<AdminUserOptionVo>>`


---

### 11. delete

- 方法: `DELETE`
- 路径: `/admin/admin/adminUser/{id}`
- 返回: `Object`

#### Path 参数

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `id` | true | - | `Long` |


---

## AdminUserLoginLogController

### 1. add

- 方法: `POST`
- 路径: `/admin/admin/adminUserLoginLog`
- 返回: `R<Boolean>`

#### Body(JSON)

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminUserLoginLog` | true | - | `JSON: AdminUserLoginLog` |

**Body 字段说明（adminUserLoginLog）**

- `id` (Integer, 必填: false): ID;;
- `adminUserId` (Long, 必填: false): 后台用户ID;admin_user.id~username;frontType:select,
- `clientIp` (String, 必填: false): 客户端IP地址考虑IPv6字段适当放宽
- `clientUserAgent` (String, 必填: false): 客户端软件信息
- `token` (String, 必填: false): 给用户发放的token
- `createdTime` (LocalDateTime, 必填: false): 创建时间


---

### 2. edit

- 方法: `PUT`
- 路径: `/admin/admin/adminUserLoginLog`
- 返回: `R<Boolean>`

#### Body(JSON)

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminUserLoginLog` | true | - | `JSON: AdminUserLoginLog` |

**Body 字段说明（adminUserLoginLog）**

- `id` (Integer, 必填: false): ID;;
- `adminUserId` (Long, 必填: false): 后台用户ID;admin_user.id~username;frontType:select,
- `clientIp` (String, 必填: false): 客户端IP地址考虑IPv6字段适当放宽
- `clientUserAgent` (String, 必填: false): 客户端软件信息
- `token` (String, 必填: false): 给用户发放的token
- `createdTime` (LocalDateTime, 必填: false): 创建时间


---

### 3. list

- 方法: `GET`
- 路径: `/admin/admin/adminUserLoginLog/list`
- 返回: `R<PageSerializable<AdminUserLoginLog>>`

#### Query 参数（GET 参数）

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `adminUserLoginLog` | false | - | `AdminUserLoginLog` |
| `betweenCreatedTimeRequest` | false | - | `BetweenCreatedTimeRequest` |


---

### 4. remove

- 方法: `DELETE`
- 路径: `/admin/admin/adminUserLoginLog/{ids}`
- 返回: `R<Boolean>`

#### Path 参数

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `ids` | true | - | `Long[]` |


---

### 5. getInfo

- 方法: `GET`
- 路径: `/admin/admin/adminUserLoginLog/{id}`
- 返回: `R<AdminUserLoginLog>`

#### Path 参数

| 参数 | 必填 | 描述 | 类型 |
|:-----|:-----|:-----|:-----|
| `id` | true | - | `Long` |

# SeekLight-Edu API 接口文档

## 文档说明

本文档描述了 SeekLight-Edu 项目的所有 REST API 接口。

**基础URL**: `http://localhost:8080`

**通用响应格式**:
```json
{
  "code": 200,
  "message": "OK",
  "data": {}
}
```

**响应码说明**:
- `200`: 请求成功
- `500`: 服务器错误
- 其他业务错误码根据具体业务场景定义

---

## 目录

1. [认证接口](#1-认证接口-authcontroller)
2. [聊天接口](#2-聊天接口-chatevecontroller)
3. [对话管理接口](#3-对话管理接口-dialoguecontroller)
4. [分组管理接口](#4-分组管理接口-groupcontroller)
5. [模型管理接口](#5-模型管理接口-modelcontroller)

---

## 1. 认证接口 (AuthController)

**基础路径**: `/`

### 1.1 用户登录

**接口描述**: 用户登录验证，成功后返回 JWT Token

**请求方式**: `POST`

**请求路径**: `/login`

**请求头**:
```
Content-Type: application/json
```

**请求参数** (Body):

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |

**请求示例**:
```json
{
  "username": "admin",
  "password": "123456"
}
```

**响应参数**:

| 参数名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码，200表示成功 |
| message | String | 响应消息 |
| data | String | JWT Token字符串 |

**响应示例**:
```json
{
  "code": 200,
  "message": "OK",
  "data": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

### 1.2 用户注册

**接口描述**: 新用户注册

**请求方式**: `POST`

**请求路径**: `/register`

**请求头**:
```
Content-Type: application/json
```

**请求参数** (Body):

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| username | String | 是 | 用户名 |
| password | String | 是 | 密码 |
| name | String | 是 | 用户真实姓名 |
| role | String | 否 | 用户角色，默认为 "USER" |

**请求示例**:
```json
{
  "username": "student001",
  "password": "123456",
  "name": "张三",
  "role": "USER"
}
```

**响应参数**:

| 参数名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码 |
| message | String | 响应消息 |
| data | String | 注册结果信息 |

**响应示例**:
```json
{
  "code": 200,
  "message": "OK",
  "data": "注册成功"
}
```

---

## 2. 聊天接口 (ChatEveController)

**基础路径**: `/chatEve`

### 2.1 流式聊天对话

**接口描述**: 使用 AI 模型进行流式对话，采用 SSE (Server-Sent Events) 技术实时返回响应

**请求方式**: `POST`

**请求路径**: `/chatEve/runs`

**请求头**:
```
Content-Type: application/json
Api-key: {apiKey} (可选)
```

**请求参数** (Body):

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| user | String | 是 | 用户标识 |
| dialogueId | Long | 否 | 对话ID，首次对话可为空 |
| model | String | 是 | 模型名称 |
| maxTokens | Integer | 否 | 最大生成token数 |
| temperature | Double | 否 | 温度参数，控制随机性，范围 0-2 |
| stream | Boolean | 否 | 是否流式输出，默认 true |
| messages | Array | 是 | 对话消息列表 |

**messages 对象说明**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| role | String | 是 | 角色，可选值: "user", "assistant", "system" |
| content | String | 是 | 消息内容 |

**请求示例**:
```json
{
  "user": "student001",
  "dialogueId": 123,
  "model": "gpt-4",
  "maxTokens": 2000,
  "temperature": 0.7,
  "stream": true,
  "messages": [
    {
      "role": "system",
      "content": "你是一个智能助教"
    },
    {
      "role": "user",
      "content": "请解释什么是机器学习"
    }
  ]
}
```

**响应**: SSE 流式响应

**响应格式**: `text/event-stream`

**响应示例**:
```
data: {"content": "机器学习是", "done": false}

data: {"content": "人工智能的", "done": false}

data: {"content": "一个分支...", "done": true}
```

---

## 3. 对话管理接口 (DialogueController)

**基础路径**: `/dialogue`

### 3.1 初始化对话

**接口描述**: 创建新的对话会话

**请求方式**: `POST`

**请求路径**: `/dialogue/init`

**请求头**:
```
Content-Type: application/json
Authorization: Bearer {token}
```

**请求参数**: 无

**响应参数**:

| 参数名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码 |
| message | String | 响应消息 |
| data | Long | 新创建的对话ID |

**响应示例**:
```json
{
  "code": 200,
  "message": "OK",
  "data": 456
}
```

---

### 3.2 获取对话历史列表

**接口描述**: 获取当前用户的所有对话历史

**请求方式**: `GET`

**请求路径**: `/dialogue/history`

**请求头**:
```
Authorization: Bearer {token}
```

**请求参数**: 无

**响应参数**:

| 参数名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码 |
| message | String | 响应消息 |
| data | Array | 对话列表 |

**data 对象说明 (TDialogue)**:

| 参数名 | 类型 | 说明 |
|--------|------|------|
| dialogueId | Long | 对话ID |
| userId | Long | 用户ID |
| modelId | Long | 模型ID |
| title | String | 对话标题 |
| isDeleted | Integer | 是否删除，0=未删除，1=已删除 |
| createdAt | DateTime | 创建时间 |
| updatedAt | DateTime | 更新时间 |

**响应示例**:
```json
{
  "code": 200,
  "message": "OK",
  "data": [
    {
      "dialogueId": 123,
      "userId": 1,
      "modelId": 5,
      "title": "机器学习讨论",
      "isDeleted": 0,
      "createdAt": "2026-02-14T10:30:00",
      "updatedAt": "2026-02-14T11:00:00"
    }
  ]
}
```

---

### 3.3 删除对话

**接口描述**: 删除指定的对话（软删除）

**请求方式**: `DELETE`

**请求路径**: `/dialogue/history`

**请求头**:
```
Authorization: Bearer {token}
```

**请求参数** (Query):

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| dialogueId | Long | 是 | 要删除的对话ID |

**请求示例**:
```
DELETE /dialogue/history?dialogueId=123
```

**响应参数**:

| 参数名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码 |
| message | String | 响应消息 |
| data | String | 操作结果 |

**响应示例**:
```json
{
  "code": 200,
  "message": "OK",
  "data": "删除成功"
}
```

---

### 3.4 获取对话消息历史

**接口描述**: 获取指定对话的所有消息记录

**请求方式**: `GET`

**请求路径**: `/dialogue/chatHistory`

**请求头**:
```
Authorization: Bearer {token}
```

**请求参数** (Query):

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| dialogueId | Long | 是 | 对话ID |

**请求示例**:
```
GET /dialogue/chatHistory?dialogueId=123
```

**响应参数**:

| 参数名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码 |
| message | String | 响应消息 |
| data | Array | 消息列表 |

**data 对象说明 (ChatMessage)**:

| 参数名 | 类型 | 说明 |
|--------|------|------|
| id | Long | 消息ID |
| dialogueId | Long | 对话ID |
| role | String | 角色: user/assistant/system |
| content | String | 消息内容 |
| createdAt | DateTime | 创建时间 |

**响应示例**:
```json
{
  "code": 200,
  "message": "OK",
  "data": [
    {
      "id": 1001,
      "dialogueId": 123,
      "role": "user",
      "content": "什么是机器学习？",
      "createdAt": "2026-02-14T10:30:00"
    },
    {
      "id": 1002,
      "dialogueId": 123,
      "role": "assistant",
      "content": "机器学习是人工智能的一个分支...",
      "createdAt": "2026-02-14T10:30:05"
    }
  ]
}
```

---

## 4. 分组管理接口 (GroupController)

**基础路径**: `/group`

### 4.1 创建分组

**接口描述**: 创建新的用户分组

**请求方式**: `POST`

**请求路径**: `/group`

**请求头**:
```
Content-Type: application/json
Authorization: Bearer {token}
```

**请求参数** (Body):

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| groupName | String | 是 | 分组名称 |
| groupApiKey | String | 是 | 分组API密钥 |
| description | String | 否 | 分组描述 |

**请求示例**:
```json
{
  "groupName": "计算机科学系",
  "groupApiKey": "sk-group-20260214-001",
  "description": "计算机科学系学生分组"
}
```

**响应参数**:

| 参数名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码 |
| message | String | 响应消息 |
| data | Object | 创建的分组信息 |

**data 对象说明 (TGroup)**:

| 参数名 | 类型 | 说明 |
|--------|------|------|
| groupId | Long | 分组ID |
| groupName | String | 分组名称 |
| groupApiKey | String | 分组API密钥 |
| description | String | 分组描述 |
| isDeleted | Integer | 是否删除，0=未删除，1=已删除 |
| createdAt | DateTime | 创建时间 |
| updatedAt | DateTime | 更新时间 |

**响应示例**:
```json
{
  "code": 200,
  "message": "OK",
  "data": {
    "groupId": 1,
    "groupName": "计算机科学系",
    "groupApiKey": "sk-group-20260214-001",
    "description": "计算机科学系学生分组",
    "isDeleted": 0,
    "createdAt": "2026-02-14T10:00:00",
    "updatedAt": "2026-02-14T10:00:00"
  }
}
```

---

### 4.2 查询分组列表

**接口描述**: 获取分组列表，支持按名称模糊搜索

**请求方式**: `GET`

**请求路径**: `/group`

**请求头**:
```
Authorization: Bearer {token}
```

**请求参数** (Query):

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| groupName | String | 否 | 分组名称，支持模糊查询 |

**请求示例**:
```
GET /group?groupName=计算机
```

**响应参数**:

| 参数名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码 |
| message | String | 响应消息 |
| data | Array | 分组列表 |

**响应示例**:
```json
{
  "code": 200,
  "message": "OK",
  "data": [
    {
      "groupId": 1,
      "groupName": "计算机科学系",
      "groupApiKey": "sk-group-20260214-001",
      "description": "计算机科学系学生分组",
      "isDeleted": 0,
      "createdAt": "2026-02-14T10:00:00",
      "updatedAt": "2026-02-14T10:00:00"
    }
  ]
}
```

---

### 4.3 更新分组

**接口描述**: 更新分组信息

**请求方式**: `PUT`

**请求路径**: `/group/{groupId}`

**请求头**:
```
Content-Type: application/json
Authorization: Bearer {token}
```

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| groupId | Long | 是 | 分组ID |

**请求参数** (Body):

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| groupName | String | 否 | 分组名称 |
| groupApiKey | String | 否 | 分组API密钥 |
| description | String | 否 | 分组描述 |

**请求示例**:
```json
{
  "groupName": "计算机科学系2024",
  "description": "更新后的描述"
}
```

**响应参数**: 同创建分组

---

### 4.4 删除分组

**接口描述**: 删除指定分组（软删除）

**请求方式**: `DELETE`

**请求路径**: `/group/{groupId}`

**请求头**:
```
Authorization: Bearer {token}
```

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| groupId | Long | 是 | 分组ID |

**请求示例**:
```
DELETE /group/1
```

**响应参数**:

| 参数名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码 |
| message | String | 响应消息 |
| data | String | 操作结果 |

**响应示例**:
```json
{
  "code": 200,
  "message": "OK",
  "data": "删除成功"
}
```

---

## 5. 模型管理接口 (ModelController)

**基础路径**: `/model`

### 5.1 创建模型

**接口描述**: 创建新的AI模型配置

**请求方式**: `POST`

**请求路径**: `/model`

**请求头**:
```
Content-Type: application/json
Authorization: Bearer {token}
```

**请求参数** (Body):

| 参数名 | 类型 | 必填 | 说明                   |
|--------|------|------|----------------------|
| modelName | String | 是 | 模型名称，如: gpt-4        |
| description | String | 否 | 模型描述                 |
| provider | String | 是 | 提供商，如: openai, azure |
| modelKey | String | 是 | 模型代号                 |
| status | Integer | 否 | 状态，1=上架，0=下架，默认1     |
| groupId | Long | 是 | 所属分组ID               |

**请求示例**:
```json
{
  "modelName": "gpt-4",
  "description": "OpenAI GPT-4 模型",
  "provider": "openai",
  "modelKey": "sk-model-xxx",
  "status": 1,
  "groupId": 1
}
```

**响应参数**:

| 参数名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码 |
| message | String | 响应消息 |
| data | Object | 创建的模型信息 |

**data 对象说明 (TModel)**:

| 参数名 | 类型 | 说明 |
|--------|------|------|
| modelId | Long | 模型ID |
| modelName | String | 模型名称 |
| description | String | 模型描述 |
| provider | String | 提供商 |
| modelKey | String | 模型密钥 |
| status | Integer | 状态，1=上架，0=下架 |
| groupId | Long | 所属分组ID |
| isDeleted | Integer | 是否删除，0=未删除，1=已删除 |
| createdAt | DateTime | 创建时间 |
| updatedAt | DateTime | 更新时间 |

**响应示例**:
```json
{
  "code": 200,
  "message": "OK",
  "data": {
    "modelId": 5,
    "modelName": "gpt-4",
    "description": "OpenAI GPT-4 模型",
    "provider": "openai",
    "modelKey": "sk-model-xxx",
    "status": 1,
    "groupId": 1,
    "isDeleted": 0,
    "createdAt": "2026-02-14T10:00:00",
    "updatedAt": "2026-02-14T10:00:00"
  }
}
```

---

### 5.2 查询模型列表

**接口描述**: 获取模型列表，支持多条件筛选

**请求方式**: `GET`

**请求路径**: `/model`

**请求头**:
```
Authorization: Bearer {token}
```

**请求参数** (Query):

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| modelName | String | 否 | 模型名称，支持模糊查询 |
| status | Integer | 否 | 状态筛选，1=上架，0=下架 |
| groupId | Long | 否 | 分组ID筛选 |

**请求示例**:
```
GET /model?status=1&groupId=1
```

**响应参数**:

| 参数名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码 |
| message | String | 响应消息 |
| data | Array | 模型列表 |

**响应示例**:
```json
{
  "code": 200,
  "message": "OK",
  "data": [
    {
      "modelId": 5,
      "modelName": "gpt-4",
      "description": "OpenAI GPT-4 模型",
      "provider": "openai",
      "modelKey": "sk-model-xxx",
      "status": 1,
      "groupId": 1,
      "isDeleted": 0,
      "createdAt": "2026-02-14T10:00:00",
      "updatedAt": "2026-02-14T10:00:00"
    }
  ]
}
```

---

### 5.3 查询可用模型列表

**接口描述**: 获取当前用户可使用的模型列表（仅返回上架状态的模型）

**请求方式**: `GET`

**请求路径**: `/model/available`

**请求头**:
```
Authorization: Bearer {token}
```

**请求参数** (Query):

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| modelName | String | 否 | 模型名称，支持模糊查询 |
| status | Integer | 否 | 状态筛选，默认为1（仅上架） |

**请求示例**:
```
GET /model/available?modelName=gpt
```

**响应参数**: 同查询模型列表

---

### 5.4 更新模型

**接口描述**: 更新模型信息

**请求方式**: `PUT`

**请求路径**: `/model/{modelId}`

**请求头**:
```
Content-Type: application/json
Authorization: Bearer {token}
```

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| modelId | Long | 是 | 模型ID |

**请求参数** (Body):

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| modelName | String | 否 | 模型名称 |
| description | String | 否 | 模型描述 |
| provider | String | 否 | 提供商 |
| modelKey | String | 否 | 模型密钥 |
| status | Integer | 否 | 状态，1=上架，0=下架 |
| groupId | Long | 否 | 所属分组ID |

**请求示例**:
```json
{
  "description": "更新后的描述",
  "status": 0
}
```

**响应参数**: 同创建模型

---

### 5.5 删除模型

**接口描述**: 删除指定模型（软删除）

**请求方式**: `DELETE`

**请求路径**: `/model/{modelId}`

**请求头**:
```
Authorization: Bearer {token}
```

**路径参数**:

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| modelId | Long | 是 | 模型ID |

**请求示例**:
```
DELETE /model/5
```

**响应参数**:

| 参数名 | 类型 | 说明 |
|--------|------|------|
| code | Integer | 状态码 |
| message | String | 响应消息 |
| data | String | 操作结果 |

**响应示例**:
```json
{
  "code": 200,
  "message": "OK",
  "data": "删除成功"
}
```

---

## 附录

### A. 认证说明

本项目使用 JWT (JSON Web Token) 进行身份认证：

1. 用户登录成功后，服务器返回 JWT Token
2. 后续请求需要在请求头中携带 Token：
   ```
   Authorization: Bearer {token}
   ```
3. Token 过期后需要重新登录获取新 Token

### B. 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误 |
| 401 | 未授权，需要登录 |
| 403 | 无权限访问 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### C. 状态字段说明

本项目中的状态字段统一使用：
- `0`: 未激活/下架/已删除
- `1`: 激活/上架/未删除

### D. 数据保留策略

- 对话和消息支持软删除，删除后不会物理删除数据
- 模型和分组的删除会标记 `isDeleted` 为 1
- 所有删除操作可逆，管理员可恢复数据

### E. Swagger 文档

本项目提供了 Swagger 在线文档，启动服务后可访问：
```
http://localhost:8080/swagger-ui.html
```

通过 Swagger 可以在线测试所有接口。

---

**文档版本**: v1.0
**最后更新**: 2026-02-14
**维护者**: SeekLight-Edu 开发团队

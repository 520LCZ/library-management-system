# 图书管理系统 — 前后端共享契约

> 前端 (frontend/) 与后端 (backend/) 必须严格遵守本契约，保证接口对齐。

## 1. 全局约定

- 后端基础路径: `http://127.0.0.1:8080`，所有接口前缀 `/api`
- 前端开发代理: Vite 把 `/api` 代理到 `http://127.0.0.1:8080`
- 统一返回结构:
  ```json
  { "code": 200, "message": "success", "data": <any> }
  ```
  - code=200 成功; 401 未登录; 403 无权限; 500 服务异常
- 分页请求参数: `page`(从1) , `size`(默认10), 可选 `keyword` 等
- 分页返回:
  ```json
  { "records": [...], "total": 100, "page": 1, "size": 10 }
  ```

## 2. 认证 (JWT)

- `POST /api/auth/login`  body: `{ "username":"admin", "password":"admin123" }`
  - 返回 data: `{ "token":"<jwt>", "userInfo": { "id":1, "username":"admin", "nickname":"管理员", "role":"admin", "avatar":"" } }`
- `GET /api/auth/info`  (需登录) 返回当前登录用户 userInfo
- 前端: 登录后把 token 存 localStorage(key=`library_token`)，axios 请求头加 `Authorization: Bearer <token>`
- 角色: `admin`(管理员/全部权限), `librarian`(图书管理员: 图书/借阅/读者/分类/统计), `reader`(读者: 仅查看+借阅)
- 预置账号: admin/admin123, librarian/lib123, reader/reader123

## 3. 数据模型 (字段以 snake_case 入库, Java 实体用 camelCase + MyBatis-Plus @TableField 映射)

### book 图书
id, title, author, isbn, category_id, publisher, publish_date(date), price(decimal), stock(int, 在馆可借数), total(int, 总数), cover(url), location, description, status(1上架 0下架), create_time, update_time

### category 分类
id, name, parent_id(0=顶级), sort, create_time

### reader 读者
id, name, gender(0男 1女), phone, email, id_card, address, register_date, status(1正常 0停用), create_time

### borrow 借阅
id, book_id, reader_id, borrow_date, due_date, return_date(nullable), status(1借出中 2已归还 3已逾期), create_time

### user 系统用户
id, username, password(BCrypt), nickname, role(admin/librarian/reader), avatar, email, phone, status(1启用 0禁用), create_time

## 4. 接口清单

### 4.1 驾驶舱 dashboard (首页)
- `GET /api/dashboard/stats` → data:
  ```json
  {
    "kpi": { "bookCount": 320, "readerCount": 156, "borrowingCount": 23, "overdueCount": 4 },
    "borrowTrend": [ {"month":"01","count":12}, ... {"month":"12","count":18} ],
    "categoryDist": [ {"name":"文学","value":45}, ... ],
    "topBooks": [ {"name":"三体","value":32}, ... ],   // 借阅次数 Top5
    "readerGrowth": [ {"month":"01","count":3}, ... ]  // 新增读者
  }
  ```

### 4.2 图书 book
- `GET /api/book/page?page=1&size=10&keyword=&categoryId=` → PageResult<Book>
- `GET /api/book/{id}` → Book (含 categoryName)
- `POST /api/book` body:Book → 新增
- `PUT /api/book` body:Book(id必填) → 修改
- `DELETE /api/book/{id}`
- `PUT /api/book/{id}/status?status=1` → 上下架

### 4.3 分类 category
- `GET /api/category/tree` → List<{id,name,parentId,sort,children:[]}>  (树形)
- `GET /api/category/list` → List<Category>
- `POST /api/category` / `PUT /api/category` / `DELETE /api/category/{id}`

### 4.4 读者 reader
- `GET /api/reader/page?page=&size=&keyword=` → PageResult<Reader>
- `GET /api/reader/{id}` → Reader (含 borrowHistory 列表)
- `POST /api/reader` / `PUT /api/reader` / `DELETE /api/reader/{id}`

### 4.5 借阅 borrow
- `GET /api/borrow/page?page=&size=&status=` → PageResult (含 bookTitle, readerName)
- `POST /api/borrow` body: `{ "bookId":1, "readerId":2, "days":30 }` → 借出(扣减 stock)
- `PUT /api/borrow/{id}/return` → 归还(恢复 stock, 设 returnDate, status=2)
- `GET /api/borrow/overdue` → 逾期列表

### 4.6 统计 stats
- `GET /api/stats/borrow-by-category` → [{name,value}]
- `GET /api/stats/borrow-by-month` → [{month,count}]
- `GET /api/stats/active-readers` → [{name,value}]  // 借阅最多 Top10
- `GET /api/stats/inventory-summary` → {totalBooks, totalStock, totalBorrowed, byCategory:[{name,value}]}

### 4.7 用户管理 user (系统设置中心)
- `GET /api/user/page?page=&size=&keyword=` → PageResult<User>(password 不返回)
- `POST /api/user` / `PUT /api/user` / `DELETE /api/user/{id}`
- `PUT /api/user/{id}/status?status=1`
- `GET /api/role/list` → [{value:"admin",label:"管理员"},{value:"librarian",label:"图书管理员"},{value:"reader",label:"读者"}]

## 5. 配色 (墨绿 + 浅色, 前后端预览一致)

- 主色 primary: `#2F5E4E`
- primary-dark: `#244A3D`  / primary-light: `#3E7A66`
- 背景底色 bg: `#F5F7F6`, 卡片 card: `#FFFFFF`
- 文字主 text-primary: `#1F2D27`, 次级 text-secondary: `#6B7B72`
- 边框 border: `#E3E8E4`
- 成功 `#3E7A66` 警告 `#C98A2B` 危险 `#B5482E` 信息 `#5A8F7B`
- 前端: Element Plus 主题色覆盖为 #2F5E4E; 侧边栏深墨绿 `#244A3D` 配白字

## 6. 运行

- 后端: `cd backend && mvn spring-boot:run`  (默认 H2 文件库, 零配置; 切 MySQL: `--spring.profiles.active=mysql` 并改 application-mysql.yml)
- 前端: `cd frontend && npm install && npm run dev`  → http://127.0.0.1:5173
- 登录: admin / admin123

# 移动互动课堂系统

一个用于期末大作业的**极简**课堂互动系统：老师建课、发题，学生签到、答题，大屏看实时统计，最后导出数据。

> 没有微服务、没有 Redis、没有 Vue、没有 Node.js、没有 JWT。
> 就是 Spring Boot + MySQL + 原生 HTML/CSS/JS，一个 jar 跑起来，代码简单到每个人都能看懂。

## 一、技术栈

| 技术 | 版本 | 用来干什么 |
| --- | --- | --- |
| JDK | 17 | 运行环境 |
| Spring Boot | 3.2.5 | 写后端接口、内置 Tomcat |
| MyBatis-Plus | 3.5.5 | 不写 SQL 就能增删改查 |
| MySQL | 8.0 | 存数据 |
| Lombok | 随 Spring Boot | `@Data` 少写 getter/setter |
| 原生 HTML/CSS/JS | - | 前端页面，放在 `static` 下，用 `fetch` 调接口 |

## 二、目录结构

```
移动互动课堂/
├── pom.xml
├── README.md
├── docs/
│   └── API_CONTRACT.md              # 接口契约（改字段前先看这个）
└── src/main/
    ├── java/com/example/classroom/
    │   ├── ClassroomApplication.java # 启动类
    │   ├── common/Result.java        # 统一返回格式
    │   ├── config/
    │   │   ├── AuthInterceptor.java  # 登录拦截器
    │   │   └── WebMvcConfig.java     # 拦截规则
    │   ├── controller/AuthController.java
    │   ├── entity/User.java
    │   └── mapper/UserMapper.java
    └── resources/
        ├── application.yml           # 端口、数据库配置
        ├── sql/V1__init.sql          # 建库建表 + 初始账号
        └── static/
            ├── login.html            # 登录页
            ├── register.html         # 注册页
            ├── index.html            # 登录后的中转页
            ├── js/common.js          # 全组公用 myFetch
            ├── teacher/              # M2：建课、发题
            ├── student/              # M1、M4：签到、答题
            ├── screen/               # M5：大屏展示
            └── report/               # M3：数据导出
```

`teacher/`、`student/`、`screen/`、`report/` 现在只有 `.gitkeep`，是给组员留的空目录。
**页面入口约定：每个目录的首页统一叫 `index.html`**，这样根目录 `index.html` 的菜单链接不用改。

## 三、怎么跑起来

### 0. 先装好环境

- JDK 17（命令行输入 `java -version` 能看到 17 就行）
- Maven 3.8+（IDEA 自带也可以）
- MySQL 8.0，并且记得 root 密码

### 1. 导入数据库

在项目根目录打开命令行（这台电脑的 MySQL 端口是 **3307**，所以多一个 `-P 3307`）：

```bash
mysql -u root -p -P 3307 < src/main/resources/sql/V1__init.sql
```

输入 MySQL 密码后回车，脚本会自动创建 `classroom` 库、5 张表和 4 个测试账号。

> 如果你的 MySQL 是默认端口 3306，把 `-P 3307` 去掉即可。
> 注意：脚本开头会 `DROP TABLE`，如果 `classroom` 库里已经有同名旧表，会先被删掉，导入前先确认没有要保留的数据。

不想用命令行的话，用 Navicat / DataGrip 打开 `V1__init.sql`，全选执行也可以。

### 2. 改数据库密码（如果和默认不一样）

打开 `src/main/resources/application.yml`，端口和账号密码都在这里：

```yaml
url: jdbc:mysql://localhost:3307/classroom?...
username: root
password: "123456"
```

- 密码不是 `123456` 就改 `password`。
- MySQL 端口不是 `3307` 就把 `url` 里的 `3307` 改成你的端口（默认是 `3306`）。

### 3. 启动项目

**方式一（IDEA）**：打开项目，等 Maven 下载完依赖，右键 `ClassroomApplication.java` → Run。

**方式二（命令行）**：

```bash
mvn spring-boot:run
```

看到 `启动成功！浏览器打开：http://localhost:8080/login.html` 就说明起来了。

### 4. 访问页面

浏览器打开：<http://localhost:8080/login.html>

### 测试账号

| 账号 | 密码 | 角色 | 姓名 |
| --- | --- | --- | --- |
| teacher | 123456 | 老师 | 王老师 |
| stu01 | 123456 | 学生 | 张三 |
| stu02 | 123456 | 学生 | 李四 |
| stu03 | 123456 | 学生 | 王五 |

## 四、6 人分工表

| 成员 | 角色 | 负责模块 | 主要代码位置 | 交付标准 |
| --- | --- | --- | --- | --- |
| **PM（组长）** | 框架 + 登录 | 项目骨架、数据库、登录注册、Token 拦截器、公共 JS、接口契约、Git 管理 | `pom.xml`、`application.yml`、`sql/`、`AuthController`、`AuthInterceptor`、`static/login.html`、`static/register.html`、`static/index.html`、`static/js/common.js` | 能注册、能登录、没带 Token 调接口返回 401 |
| **M1** | 签到 | 签到接口 + 学生签到页（`student/index.html`、`student/checkin.html`） | `controller/CheckinController.java`、`static/student/` | 输入课堂口令能签到；重复签到有提示 |
| **M2** | 建课 / 发题 | 课堂、题目接口 + 教师页 | `controller/ClassroomController.java`、`controller/ExerciseController.java`、`static/teacher/` | 老师能建课拿到口令、能发布带选项的题目 |
| **M3** | 报表 | 签到/答题统计 + CSV 导出 + 报表页 | `controller/ReportController.java`、`static/report/` | 能按课堂导出 CSV，Excel 能打开 |
| **M4** | 答题 | 答题接口 + 学生答题页（`student/answer.html`） | `controller/AnswerController.java`、`static/student/` | 学生能选选项提交，能知道自己对不对 |
| **M5** | 大屏 | 统计接口 + 大屏页 | `controller/ScreenController.java`、`static/screen/` | 大屏每 3 秒自动刷新签到数、答题分布 |

### 分工说明（很重要）

1. **M1 和 M4 共用 `student/` 目录**：M1 写 `checkin.html`，M4 写 `answer.html`，两人先商量好谁建 `student/index.html`（建议 M1 建，里面放两个链接），避免同时改同一个文件。
2. **每个人只改自己负责的文件**。要动别人的代码，先在群里说一声。
3. **接口字段以 `docs/API_CONTRACT.md` 为准**，谁想改字段名，先改文档再改代码，并且请全组喝奶茶。
4. 后端统一返回 `Result`（`code`、`msg`、`data`），前端统一用 `myFetch` 发请求，不要各写各的。
5. 新增 Controller 请放在 `controller` 包，类名用 `XxxController`，接口路径用 `/api/xxx`，这样会被登录拦截器自动保护。

## 五、接口约定

完整内容看 [docs/API_CONTRACT.md](docs/API_CONTRACT.md)，这里只记三条：

1. 后端永远返回 `{ "code": 200, "msg": "成功", "data": ... }`，前端只认 `code === 200`。
2. 登录后所有请求都要带请求头 `token: xxxxx`，`common.js` 的 `myFetch` 已经自动帮你带了。
3. JSON 字段一律**小驼峰**（`classroomId`、`realName`），数据库字段是下划线（`classroom_id`、`real_name`），MyBatis-Plus 会自动转。

## 六、Git 协作规则

```bash
# 每天开始写代码前
git pull

# 写完自己的部分
git add .
git commit -m "feat: 完成学生签到页面"
git push
```

- 提交信息格式：`feat: 新功能`、`fix: 修 bug`、`docs: 改文档`。
- **不要提交** `target/`、`.idea/`，`.gitignore` 已经帮你忽略了。
- 不要直接改 `application.yml` 里的数据库账号密码然后提交（本地密码自己记着就行）。
- 合并冲突拿不准就找 PM，不要强推（不要 `git push -f`）。

## 七、常见问题

**1. 8080 端口被占用**

```bash
netstat -ano | findstr 8080
taskkill /PID 占用端口的PID /F
```

或者改 `application.yml` 里的 `server.port`。

**2. 启动报 `Communications link failure` / `Access denied`**

- 确认 MySQL 服务启动了（Windows 服务里找 MySQL80）。
- 确认 `classroom` 库已经建好（执行过第 1 步的 SQL）。
- 确认 `application.yml` 里的端口、账号、密码和你本地一致。
- 本机 MySQL 是 **3307** 端口，不是默认的 3306，别漏了。

**3. 页面 404**

- 静态页面必须放在 `src/main/resources/static/` 下。
- 改完 HTML 不用重启，刷新浏览器即可；改 Java 代码需要重启。

**4. 接口返回 401 请先登录**

Token 没存上或者被清了，回登录页重新登录一次。

**5. 中文乱码**

- 所有文件保存为 UTF-8。
- 数据库已经是 `utf8mb4`，不要自己改成别的。

---

祝大家顺利做完，答辩不慌。

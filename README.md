# 移动互动课堂系统

一个用于期末大作业的极简课堂互动系统：老师建课、发题，学生签到、答题，大屏看统计，最后导出数据。

> 没有微服务、没有 Redis、没有 Vue、没有 Node.js、没有 JWT。
> 就是 Spring Boot + MySQL + 原生 HTML/CSS/JS，一个 jar 跑起来。

## 一、技术栈

| 技术 | 版本 | 用来干什么 |
| --- | --- | --- |
| JDK | 17 | 运行环境 |
| Spring Boot | 3.2.5 | 写后端接口、内置 Tomcat |
| MyBatis-Plus | 3.5.5 | 不写 SQL 就能增删改查 |
| MySQL | 8.0 | 存数据 |
| Lombok | 随 Spring Boot | `@Data` 少写 getter/setter |
| 原生 HTML/CSS/JS | - | 前端页面，放在 `static` 下，用 `fetch` 调接口 |

## 二、项目结构

```
src/main/
├── java/com/classroom/
│   ├── ClassroomApplication.java      # 启动类
│   ├── common/
│   │   └── Result.java                # 统一返回格式
│   ├── config/
│   │   ├── AuthInterceptor.java       # 登录拦截器
│   │   └── WebMvcConfig.java          # 拦截规则
│   ├── controller/
│   │   └── AuthController.java        # 登录、注册（PM 已写）
│   ├── entity/
│   │   └── User.java
│   └── mapper/
│       └── UserMapper.java
└── resources/
    ├── application.yml                # 端口、数据库配置
    ├── sql/
    │   └── V1__init.sql               # 建库建表 + 初始账号
    └── static/                        # 前端所有页面都放这里
        ├── css/
        │   └── common.css             # 公共样式（PM 管）
        ├── js/
        │   └── common.js              # 登录校验 + 请求封装（PM 写）
        ├── index.html                 # 登录页（PM 写）
        ├── register.html              # 注册页（PM 写）
        ├── teacher/                   # M2 专属
        │   ├── home.html              # 建课页
        │   └── publish.html           # 发题页
        ├── student/                   # M1、M4 专属
        │   ├── join.html              # M1 进课签到
        │   └── answer.html            # M4 答题
        ├── screen/                    # M5 专属
        │   └── index.html             # 大屏展示
        └── report/                    # M3 专属
            └── index.html             # 数据导出
```

**认领规则：只改自己名下的文件。**
`css/common.css`、`js/common.js`、`index.html`、`register.html`、`config/`、`AuthController.java` 是公共文件，由 PM 维护；要改先在群里说一声，别直接覆盖。

## 三、怎么跑起来

### 0. 环境

- JDK 17
- Maven 3.8+（IDEA 自带也可以）
- MySQL 8.0，root 密码 `123456`

### 1. 导入数据库

本机 MySQL 端口是 **3307**，所以命令多一个 `-P 3307`：

```bash
mysql -u root -p -P 3307 < src/main/resources/sql/V1__init.sql
```

> 如果你的 MySQL 是默认端口 3306，把 `-P 3307` 去掉。
> 脚本开头会 `DROP TABLE`，如果 `classroom` 库里已有旧表，会先被删掉，导入前先确认没有要保留的数据。

### 2. 检查数据库配置

打开 `src/main/resources/application.yml`：

```yaml
url: jdbc:mysql://localhost:3307/classroom?...
username: root
password: "123456"
```

端口和密码不一致就改这里。

### 3. 启动

IDEA：打开项目，运行 `com.classroom.ClassroomApplication`。

命令行：

```bash
mvn spring-boot:run
```

### 4. 访问

浏览器打开：<http://localhost:8080/index.html>

### 测试账号

| 账号 | 密码 | 角色 | 姓名 |
| --- | --- | --- | --- |
| teacher | 123456 | 老师 | 王老师 |
| stu01 | 123456 | 学生 | 张三 |
| stu02 | 123456 | 学生 | 李四 |
| stu03 | 123456 | 学生 | 王五 |

## 四、6 人分工（谁改哪个文件）

| 成员 | 负责模块 | 前端文件（你改这里） | 后端文件（你新建/改这里） | 完成标准 |
| --- | --- | --- | --- | --- |
| **PM（组长）** | 框架 + 登录注册 + 公共资源 | `static/index.html`、`register.html`、`css/common.css`、`js/common.js` | `config/`、`controller/AuthController.java`、`entity/User.java`、`mapper/UserMapper.java` | 能注册、能登录、没带 token 调接口返回 401 |
| **M1** | 进课签到 | `static/student/join.html` | `controller/CheckinController.java`、`entity/Checkin.java`、`mapper/CheckinMapper.java` | 输入课堂口令能签到；重复签到有提示 |
| **M2** | 建课 / 发题 | `static/teacher/home.html`、`static/teacher/publish.html` | `controller/ClassroomController.java`、`controller/ExerciseController.java`、`entity/Classroom.java`、`entity/Exercise.java`、对应的 Mapper | 老师能建课拿到口令、能发布带四个选项的题目 |
| **M3** | 数据导出 | `static/report/index.html` | `controller/ReportController.java` | 按课堂导出 CSV，Excel 打开中文不乱码 |
| **M4** | 答题 | `static/student/answer.html` | `controller/AnswerController.java`、`entity/Answer.java`、`mapper/AnswerMapper.java` | 学生能选选项提交，能提示答对/答错 |
| **M5** | 大屏展示 | `static/screen/index.html` | `controller/ScreenController.java` | 大屏每 3 秒刷新签到数、答题数和选项分布 |

### PM 已经完成的部分

- 项目骨架、`pom.xml`、`application.yml`、`.gitignore`；
- 数据库脚本 `sql/V1__init.sql`（5 张表 + 4 个测试账号）；
- 登录、注册、UUID Token、登录拦截器；
- 公共样式 `css/common.css`、公共 JS `js/common.js`（`myFetch`、`checkLogin`、`fillUser`、`logout`）；
- 登录页 `index.html`、注册页 `register.html`；
- 6 个组员页面的**界面骨架和 TODO 注释**（按钮目前点了会提示“接口还没写”，这是正常的）；
- 接口契约 `docs/API_CONTRACT.md`、README、Git 仓库。

### M1 要改哪里（签到）

- 前端：只改 `static/student/join.html` 里的 `joinClass()`，把 TODO 换成两次 `myFetch` 调用。
- 后端：新建 `CheckinController`、`Checkin`、`CheckinMapper`。
- 需要的接口：`GET /api/classroom/byCode?code=xxx`（找 M2 要）、`POST /api/checkin`。
- 签到成功后请执行 `localStorage.setItem('classroomId', 课堂id)`，M4 的答题页要用。

### M2 要改哪里（建课 / 发题）

- 前端：`static/teacher/home.html` 的 `createClassroom()`，`static/teacher/publish.html` 的 `publishExercise()`。
- 后端：新建 `ClassroomController`、`ExerciseController` 和对应实体、Mapper。
- 需要的接口：`POST /api/classroom`（建课，生成 6 位口令）、`GET /api/classroom/byCode`（给 M1 用）、`POST /api/exercise`（发题）。
- 建课返回的 `code` 就是课堂口令，格式建议 6 位大写字母/数字。

### M3 要改哪里（数据导出）

- 前端：只改 `static/report/index.html` 的 `exportReport()`，用 `fetch` + `blob` 下载，不要用 `<a href>`（带不了 token）。
- 后端：新建 `ReportController`，返回 CSV，不要返回 JSON。
- CSV 列顺序固定：`账号,姓名,签到时间,题目,选择,是否正确`，开头加 UTF-8 BOM。

### M4 要改哪里（答题）

- 前端：只改 `static/student/answer.html` 的 `loadExercise()` 和 `submitAnswer()`。
- 后端：新建 `AnswerController`、`Answer`、`AnswerMapper`。
- 需要的接口：`GET /api/exercise/list?classroomId=1`（找 M2 要）、`POST /api/answer`。
- 判对错必须由后端查 `exercise.answer` 判断，不能信前端。

### M5 要改哪里（大屏）

- 前端：只改 `static/screen/index.html` 的 `refreshStats()`，然后把文件末尾的 `setInterval` 注释打开。
- 后端：新建 `ScreenController`，写 `GET /api/screen/stats?classroomId=1`。
- 返回字段看 `docs/API_CONTRACT.md` 第 4.11 节，`optionCount` 的 A/B/C/D 四个键都要有，没人选就是 0。

### 页面入口

| 页面 | 路径 | 负责人 |
| --- | --- | --- |
| 登录 | `/index.html`（访问 `/` 也会到这个页面） | PM |
| 注册 | `/register.html` | PM |
| 老师建课 | `/teacher/home.html` | M2 |
| 老师发题 | `/teacher/publish.html` | M2 |
| 学生签到 | `/student/join.html` | M1 |
| 学生答题 | `/student/answer.html` | M4 |
| 课堂大屏 | `/screen/index.html` | M5 |
| 数据导出 | `/report/index.html` | M3 |

登录成功后的跳转：

- 老师 → `/teacher/home.html`
- 学生 → `/student/join.html`

## 五、组员写后端的模板

新建一个 Controller，照这个写就行（以签到为例）：

```java
package com.classroom.controller;

import com.classroom.common.Result;
import com.classroom.entity.Checkin;
import com.classroom.mapper.CheckinMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkin")
public class CheckinController {

    @Autowired
    private CheckinMapper checkinMapper;

    @PostMapping
    public Result add(@RequestBody Checkin checkin) {
        // 简单校验：同一节课不能重复签到
        // 这里先省略，M1 自己补
        checkinMapper.insert(checkin);
        return Result.ok("签到成功", checkin);
    }
}
```

实体（注意 `@TableName` 和 `@TableId`）：

```java
package com.classroom.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("checkin")
public class Checkin {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long classroomId;
    private Long studentId;
    private LocalDateTime createTime;   // 数据库是 datetime
}
```

Mapper：

```java
package com.classroom.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.classroom.entity.Checkin;

public interface CheckinMapper extends BaseMapper<Checkin> {
}
```

> 加了 `@MapperScan("com.classroom.mapper")`，Mapper 不用再写 `@Mapper` 注解。

## 六、接口约定（必须遵守）

完整内容看 [docs/API_CONTRACT.md](docs/API_CONTRACT.md)，这里记三条：

1. 后端永远返回 `{ "code": 200, "msg": "成功", "data": ... }`，前端只认 `code === 200`。
2. 登录后所有请求都要带请求头 `token: xxxxx`，用 `myFetch` 会自动带。
3. JSON 字段一律**小驼峰**（`classroomId`、`realName`），数据库字段是下划线（`classroom_id`、`real_name`）。

改字段名之前先改契约文档，并在群里说一声。**谁改字段名谁请全组喝奶茶。**

## 七、Git 协作

```bash
# 每天开始前
git pull

# 写完自己的部分
git add .
git commit -m "feat: 完成学生签到"
git push
```

- 提交信息格式：`feat: 新功能`、`fix: 修 bug`、`docs: 改文档`。
- 不要提交 `target/`、`.idea/`，`.gitignore` 已经忽略了。
- 不要 `git push -f`，冲突找 PM。

## 八、常见问题

**1. 8080 端口被占用**

```bash
netstat -ano | findstr 8080
taskkill /PID 占用端口的PID /F
```

或者改 `application.yml` 里的 `server.port`。

**2. 启动报 `Communications link failure` / `Access denied`**

- 确认 MySQL 服务启动了。
- 确认执行过 `V1__init.sql`。
- 本机 MySQL 是 **3307** 端口，不是默认的 3306，检查 `application.yml` 和导入命令。

**3. 页面 404**

- HTML 必须放在 `src/main/resources/static/` 下。
- 老师页面在 `static/teacher/`，学生页面在 `static/student/`，别放错。
- 静态页面改完刷新浏览器即可；Java 改完要重启。

**4. 接口返回 401 请先登录**

token 没存上或过期了，回 `/index.html` 重新登录。

**5. 中文乱码**

- 文件保存为 UTF-8。
- 数据库是 `utf8mb4`，不要改。

---

祝大家顺利做完，答辩不慌。

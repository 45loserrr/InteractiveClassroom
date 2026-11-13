# 移动互动课堂系统（Interactive Classroom）

> 面向高校/培训课堂的轻量级互动教学平台：教师建课生成 6 位课堂码，学生扫码/输码即可签到、答题、抢答，大屏实时呈现课堂数据。
> 本项目为**课程实训项目**，采用 6 人小组分工、五周迭代交付，全部本地原生环境运行（不使用 Docker）。

---

## 一、项目简介

| 项 | 说明 |
| :--- | :--- |
| 项目定位 | 局域网内可独立部署的课堂互动系统，零外部依赖（除 MySQL），断网也能用 |
| 使用场景 | 机房、多媒体教室；教师笔记本作为"服务器"，学生手机接入同一 WiFi |
| 终端形态 | 学生端（H5 / Android APK）、教师端（Web / Windows exe）、大屏端（Web / 全屏 exe） |
| 技术选型 | 后端 Spring Boot 3.2.5 + MyBatis-Plus 3.5.7 + MySQL 8.0；前端 Vue3 + Vite + TypeScript |
| 实时通道 | Spring WebSocket（STOMP 原生通道，签到/发题/抢答毫秒级广播） |
| 交付周期 | 5 周（详见 `docs/02-五周排期与详细分工.md`） |

**仓库结构**

```
移动互动课堂系统/
├─ .github/              # PR 模板、Issue 模板（提 PR 前照着填）
├─ .editorconfig         # 统一缩进/编码/换行（先装编辑器插件）
├─ .gitattributes        # 换行符与二进制文件的 Git 策略
├─ docs/                 # 项目管理与接口、设计文档
├─ backend/              # Spring Boot 后端 API + WebSocket 服务（含 mvnw 包装器）
├─ frontend/
│  ├─ teacher/           # 教师端（Vue3 + Vite + TS，可打 Electron exe）
│  ├─ student/           # 学生端（Vue3，可打 Capacitor APK）
│  └─ screen/            # 大屏端（Vue3，可打全屏 Electron exe）
├─ .gitignore
└─ README.md
```

> 各模块有自己的 README：`backend/README.md`（接口实现规范）、`frontend/README.md`（三端约定）、`frontend/teacher/README.md`（目录与接口调用方式）。

---

## 二、统一环境要求（全组必须一致）

| 组件 | 版本 | 说明 |
| :--- | :--- | :--- |
| JDK | **17**（LTS） | 后端编译与运行；`JAVA_HOME` 指向 JDK 17，禁止用 JDK 8/21 |
| Maven | 3.9.x | 依赖管理、`mvn -DskipTests package` 打 jar |
| MySQL | **8.0.x** | 字符集 `utf8mb4`，本地端口 3306 |
| Node.js | **20**（LTS） | 前端构建；`node -v` 应为 `v20.x` |
| npm | 随 Node 20 自带（≥10） | 前端依赖安装 |
| Git | ≥ 2.40 | 版本管理（Windows 请开启 `core.autocrlf=true`） |
| 浏览器 | Chrome / Edge 最新版 | 大屏与教师端演示 |

> 校验命令：`java -version`、`mvn -v`、`mysql --version`、`node -v`、`npm -v`、`git --version`
> 全部版本不一致时，按上表对齐后再开始开发，避免"我这能跑"的扯皮。

---

## 三、团队角色分工（6 人）

| 编号 | 角色 | 负责模块 | 主要产出 | 代码目录 |
| :--- | :--- | :--- | :--- | :--- |
| **PM** | 项目负责人 | 需求、排期、接口契约、集成与验收 | PRD、排期表、`docs/api.yaml`、冒烟脚本、答辩 PPT | `docs/`、根目录脚本 |
| **M1** | 学生端 | H5 登录/签到/答题/抢答、Capacitor 打 APK | 学生端页面、APK 安装包 | `frontend/student/` |
| **M2** | 教师端 | 教师登录、建课、选人、发题、抢答控制、学情页、Electron 打 exe | 教师端页面、Windows 安装包 | `frontend/teacher/` |
| **M3** | 后端 API | 表结构、REST 接口、鉴权、跨域、数据聚合、jar 打包 | `backend/` 全部 API、`schema.sql` | `backend/` |
| **M4** | 实时服务 | WebSocket 通道、消息协议、断线重连、广播性能 | 实时推送模块、消息类型文档 | `backend/.../websocket/` |
| **M5** | 大屏 + 测试 | 大屏可视化、全屏 exe、压测与测试报告 | 大屏页面、测试报告、演示视频素材 | `frontend/screen/`、`docs/测试报告` |

**协作原则**：物理隔离（只改自己的文件夹）→ 必须提 PR → PM 合并。详见 `docs/03-Git协作与防冲突规范.md`。

---

## 四、系统功能清单

### 4.1 基础教学

| 功能 | 说明 | 优先级 |
| :--- | :--- | :--- |
| 登录 | 教师/学生统一账号体系，登录返回 Token；学生可用学号+姓名免注册进入课堂 | P0 |
| 创建课堂 | 教师新建课堂，系统生成唯一 **6 位数字课堂码**，课堂可开始/结束 | P0 |

### 4.2 互动模块

| 功能 | 说明 | 优先级 |
| :--- | :--- | :--- |
| 签到 | 学生输入 6 位课堂码签到；重复签到拦截；大屏实时显示应到/实到与滚动名单 | P0 |
| 随机选人 | 教师点击"随机选人"，从已签到名单中抽取，带转盘/滚动动画 | P0 |
| 单选答题 | 教师发布单选题（A/B/C/D），学生作答，实时统计各选项分布与正确率 | P0 |
| 多选答题 | 题目支持多选，按"全对"判定正确；统计正确率与漏选分布 | P1 |
| 抢答 | 教师发起抢答并倒计时，学生毫秒级抢答，服务端按数据库时间戳判定首答 | P0 |

### 4.3 课堂氛围

| 功能 | 说明 | 优先级 |
| :--- | :--- | :--- |
| 弹幕 | 学生发送弹幕，大屏滚动展示；敏感词过滤与频率限制 | P1 |
| 分组 | 按人数随机/手动分组，支持小组积分与小组抢答 | P1 |

### 4.4 数据看板

| 功能 | 说明 | 优先级 |
| :--- | :--- | :--- |
| 签到率 | 按课堂/班级统计应到、实到、签到率，支持趋势图 | P1 |
| 正确率 | 单题正确率、整堂课平均正确率、题目难度分布 | P1 |
| 排行榜 | 答题正确数、抢答成功次数、发言/弹幕活跃度排行 | P1 |
| 学情报告 | 学生个人报告（历史签到、答题、错题）与班级报告（导出/打印） | P1 |

### 4.5 扩展功能

| 功能 | 说明 | 优先级 |
| :--- | :--- | :--- |
| 题库管理 | 题目增删改查、Excel 批量导入、按知识点/难度打标签 | P1 |
| 公告 | 教师发布课堂公告，学生端/大屏同步展示 | P1 |
| 导出 Excel | 签到表、答题明细、成绩排行榜导出为 `.xlsx` | P1 |

> P0 = 五周内必须跑通的演示闭环（签到 + 答题 + 抢答）；P1 = 迭代增强项，按周次排期落地。
> 详细需求描述与验收标准见 `docs/01-产品需求文档(PRD).md`。

---

## 五、本地启动步骤（原生环境，无 Docker）

### 第 0 步：克隆仓库

```bash
git clone git@github.com:<你的账号>/InteractiveClassroom.git
cd InteractiveClassroom
```

### 第 1 步：建库建表（MySQL 8.0）

**1）启动本地 MySQL 服务**（Windows 服务名通常为 `MySQL80`）：

```bash
# Windows（管理员 PowerShell）
net start MySQL80

# macOS
brew services start mysql
```

**2）用 root 登录并创建数据库**（字符集必须是 `utf8mb4`，否则中文题干/姓名会乱码）：

```sql
-- 登录：mysql -u root -p
CREATE DATABASE IF NOT EXISTS classroom
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_general_ci;

-- 可选：为项目单独建账号（推荐，避免各组员密码不同）
CREATE USER IF NOT EXISTS 'classroom'@'%' IDENTIFIED BY 'classroom123';
GRANT ALL PRIVILEGES ON classroom.* TO 'classroom'@'%';
FLUSH PRIVILEGES;
```

**3）导入表结构**：

```bash
mysql -u root -p classroom < backend/src/main/resources/db/schema.sql
```

> 表结构含 `user`、`classroom`、`checkin`、`exercise`、`answer`、`race` 六张基础表，均为空表，不含演示数据。
> 需要演示数据时，由 PM 在 `backend/src/main/resources/db/data-demo.sql` 中维护（当前尚未提供）。

**4）数据库连接配置**：默认读取环境变量，未设置时回退到占位默认值。

| 环境变量 | 默认值 | 说明 |
| :--- | :--- | :--- |
| `DB_USERNAME` | `root` | 数据库账号 |
| `DB_PASSWORD` | `123456` | 数据库密码 |

```bash
# Windows PowerShell（临时生效，示例）
$env:DB_USERNAME="classroom"; $env:DB_PASSWORD="classroom123"

# macOS / Linux
export DB_USERNAME=classroom DB_PASSWORD=classroom123
```

> **注意**：真实密码请写入 `backend/src/main/resources/application-local.yml`（已被 `.gitignore` 忽略），或只用环境变量，**严禁提交到仓库**。

### 第 2 步：启动后端（端口 8080）

```bash
cd backend
mvn -DskipTests package          # 首次会下载依赖，约 3~10 分钟
java -jar target/classroom-0.1.0.jar
```

- 接口文档（Swagger UI）：<http://localhost:8080/swagger-ui.html>
- 健康检查：<http://localhost:8080/api/auth/ping>
- WebSocket 端点：`ws://localhost:8080/ws`（由 M4 实现）
- 若本机未启动 MySQL，`application.yml` 中 `hikari.initialization-fail-timeout=-1` 保证服务仍可启动（接口会报数据库错误，属预期）。

### 第 3 步：启动教师端（端口 5173）

```bash
cd frontend/teacher
npm install
npm run dev        # 浏览器打开 http://localhost:5173
npm run build      # 产物在 dist/，用于 Electron 打包
```

### 第 4 步：学生端 / 大屏端（后续周次开放）

```bash
cd frontend/student && npm install && npm run dev    # 学生端（M1 负责）
cd frontend/screen  && npm install && npm run dev    # 大屏端（M5 负责）
```

### 第 5 步：局域网真机联调（第 5 周）

1. 电脑与手机连同一 WiFi，`ipconfig` 查到本机 IPv4（如 `192.168.1.10`）。
2. 前端 `vite.config.ts` 的 proxy 目标与后端 CORS 均指向该 IP。
3. 学生手机访问 `http://192.168.1.10:5174`，教师端 `http://192.168.1.10:5173`，大屏 `http://192.168.1.10:5175`。
4. Windows 防火墙需放行 8080 / 5173-5175 端口。

---

## 六、常用命令速查

| 场景 | 命令 |
| :--- | :--- |
| 后端编译 | `cd backend && mvn -q -DskipTests package` |
| 后端运行 | `java -jar backend/target/classroom-0.1.0.jar` |
| 后端单元测试 | `cd backend && mvn test` |
| 前端安装依赖 | `cd frontend/teacher && npm install` |
| 前端开发 | `cd frontend/teacher && npm run dev` |
| 前端构建 | `cd frontend/teacher && npm run build` |
| 拉取并合并主干 | `git pull origin main && git merge origin/main` |

---

## 七、文档索引

| 文档 | 内容 |
| :--- | :--- |
| `docs/01-产品需求文档(PRD).md` | 功能详述、P0/P1 划分、验收口径 |
| `docs/02-五周排期与详细分工.md` | 5 周排期表、每人每周任务与周五验收标准 |
| `docs/03-Git协作与防冲突规范.md` | 物理隔离、禁止直推、每日动作、冲突解决 |
| `docs/04-接口契约模板.md` | 接口先行原则与登录/建课/签到/发题/抢答模板 |
| `docs/05-系统设计文档.md` | 总体架构、后端分层、库表与并发设计、WebSocket 通道、关键时序、安全与 ADR |

---

## 八、免责与约定

- 本项目为教学实训用途，未做生产级安全加固（如 HTTPS、限流、审计日志）。
- 提交前请确认不含真实密码、Token 与个人信息；`.env`、`*.log`、`application-local.yml` 已被忽略。
- 遇到问题先查 `docs/03`，仍无法解决再在群里 @ 对应模块负责人。

# 三周安排与下周任务（每人共 3 个模块）

老师要求每人一共交 **3 个模块**，之前做完的也算。不是每人再新做 3 个。

- 5 个组员 × 3 个模块 = 15 个模块；
- 现在已经有 6 个：建课、发题、签到、答题、大屏、数据导出；
- 还需要新增 9 个；
- 本轮不给 PM 排新模块，PM 的框架、登录鉴权、公共 JS/CSS 不计入这 15 个。

距离 12 月 20 日还有三周：

- 第 1 周：11/30 – 12/06
- 第 2 周：12/07 – 12/13
- 第 3 周：12/14 – 12/20

## 一、模块盘点

| 成员 | 已有模块（计入 3 个） | 还需要新增 | 下周先做 |
| --- | --- | --- | --- |
| 陈嘉（M2） | 建课、发题 | 题库管理（1 个） | 题库管理 |
| 于文博（M1） | 签到 | 我的签到记录、签到统计（2 个） | 我的签到记录 |
| 刘子瑜（M4） | 答题 | 我的答题记录/成绩、错题本（2 个） | 我的答题记录/成绩 |
| 张宇轩（M5） | 大屏 | 排行榜、大屏显示设置（2 个） | 排行榜 |
| 郭曦（M3） | 数据导出 | 报表筛选/预览、统计图表（2 个） | 报表筛选/预览 |

目标：**12/13 之前每人 3 个模块全部完成并 push**。第 3 周只修 bug、联调、准备演示，不再加新功能。

## 二、下周（11/30 – 12/06）总任务

下周只做两件事：

1. 修之前代码里的 P0 bug；
2. 每人完成自己的**第一个新增模块**。

### 2.1 时间表

| 日期 | 任务 |
| --- | --- |
| 11/30 周一 | 所有人 `git pull`，按“建课→签到→发题→答题→大屏→报表”跑一遍，记录 bug；开始做新增模块 |
| 12/01 周二 | 修自己文件里的 bug；新增模块继续写 |
| 12/02 周三 | 新增模块主体完成，自己用浏览器点一遍 |
| 12/03 周四 | 新增模块自测；commit + push，群里说一声 |
| 12/04 周五 | 全组联调：老师建课、学生签到、老师发题、学生答题、大屏、导出 |
| 12/05 周六 | 修联调 bug；更新 `docs/API_CONTRACT.md` |
| 12/06 周日 | 全流程彩排，准备下周继续做第二个新增模块 |

### 2.2 下周必须修的 bug

| 问题 | 现象 | 负责 | 改哪里 |
| --- | --- | --- | --- |
| 大屏可能显示错课堂 | 建了两个课时，`/screen/` 只读 localStorage，可能还是上一节课 | 陈嘉 + 张宇轩 | 陈嘉让“打开大屏”带 `?classroomId=当前id`；张宇轩让大屏优先读 URL，并显示课堂名/口令 |
| 大屏只认加载时那一次课堂 id | 页面开着时切换课堂，大屏不会变 | 张宇轩 | `screen/index.html`：课堂 id 从 URL 读，没有 URL 再读 localStorage；页面上显示当前课堂 |
| 答题页题干直接拼 HTML | 题干里如果有 `<` 等字符，页面会乱，存在 XSS | 刘子瑜 | `student/answer.html`：用 `escapeHtml()` 或 `textContent`，不要直接拼 `q.title` |
| 答题按钮可重复点 | 网络慢时连点会发多次请求 | 刘子瑜 | `student/answer.html`：提交时禁用按钮，返回后恢复 |
| 发题页和后台选项校验不一致 | 前端强制 4 个选项，后端允许 2–4 个 | 陈嘉 | 统一成“必须 4 个选项”，前端后端都按 4 个校验 |
| 报表页初始化顺序乱 | 先读 localStorage 再加载课堂列表，偶尔选不中课堂 | 郭曦 | `report/index.html`：先 `loadClassrooms()`，再根据列表恢复上次课堂 |
| 报表无数据时提示不清 | 空课堂导出只有表头，页面没有说明 | 郭曦 | `report/index.html`：预览为空时显示“该课堂暂无签到/答题数据” |
| 签到页提示太素、重复签到不醒目 | 重复签到只有一行文字 | 于文博 | `student/join.html`：成功/失败用 `.notice`/`.notice-error`，重复签到明确提示 |

> 只修自己文件里的 bug。公共文件（`common.js`、`common.css`、`index.html`、`register.html`、`AuthController`、`WebMvcConfig`、`application.yml`、`V1__init.sql`、`pom.xml`）本轮冻结，不要动；确实要改先在群里说。

### 2.3 下周每人具体任务

| 成员 | 修 bug | 新增模块 | 下周交付 |
| --- | --- | --- | --- |
| 陈嘉 | 大屏链接带课堂 id；选项统一 4 个 | 题库管理 | 题库页能按课堂/关键词查题、复制题目到另一个课堂 |
| 于文博 | 签到成功/失败/重复提示 | 我的签到记录 | 学生能看自己签过哪些课、签到时间 |
| 刘子瑜 | 题干转义；提交防连点 | 我的答题记录/成绩 | 学生能看答过的题、对错、正确率 |
| 张宇轩 | 大屏优先读 URL；显示当前课堂 | 排行榜 | 大屏能看签到速度前 5、答题正确数前 5 |
| 郭曦 | 报表初始化顺序；空数据提示 | 报表筛选/预览 | 按课堂、账号/姓名、时间筛选，先预览再导出 |

## 三、下周新增模块的最小要求

### 陈嘉：题库管理

- 范围：老师所有课堂的题目，不只是当前课堂；
- 功能：
  - 按课堂筛选、按题干关键词搜索；
  - 勾选一道题，复制到另一个课堂；
  - 题目列表展示题干、选项、答案（老师端可以看答案）。
- 接口：
  - `GET /api/exercise/bank?teacherId=1&classroomId=&keyword=`
  - `POST /api/exercise/copy`，请求 `{ exerciseId, targetClassroomId }`
- 文件：`teacher/publish.html`（加“题库管理”区域）、`ExerciseController.java`、`ExerciseMapper.java`。
- 验收：A 课堂的题复制到 B 课堂，B 课堂能看到，答案不变。

### 于文博：我的签到记录

- 功能：学生查看自己签过哪些课、课堂名称、签到时间；按时间倒序。
- 接口：`GET /api/checkin/my?studentId=2`。
- 文件：`student/join.html`（加“我的签到记录”区域）、`CheckinController.java`、`CheckinMapper.java`；课堂名可以调用 M2 的 `ClassroomMapper`，不要改。
- 验收：stu01 签两节课，记录里两节都在，时间正确；重复签到不新增记录。

### 刘子瑜：我的答题记录/成绩

- 功能：学生查看答过的题、自己的选择、正确答案、对错、总正确率。
- 接口：`GET /api/answer/my?studentId=2`，返回题目、选项、选择、正确答案、是否正确。
- 文件：`student/answer.html`（加“我的记录”标签/区域）、`AnswerController.java`、`AnswerMapper.java`；题目信息读 M2 的 `ExerciseMapper`，不要改。
- 验收：答 3 题对 2 题，正确率显示 66.7%；答错的题能看到正确答案。

### 张宇轩：排行榜

- 功能：签到速度前 5 名（按 `create_time` 正序）、答题正确数前 5 名（按答对数倒序）。
- 接口：`GET /api/screen/rank?classroomId=1`，返回两个数组。
- 文件：`screen/index.html`（加排行榜区域/切换按钮）、`ScreenController.java`。
- 验收：3 个学生签到/答题后，两个榜排序正确；大屏 3 秒刷新不影响排行榜。

### 郭曦：报表筛选/预览

- 功能：按课堂、学生账号/姓名、签到时间范围筛选；页面表格先预览，再导出当前筛选结果。
- 接口：`GET /api/report/list?classroomId=1&keyword=&start=&end=`，返回 JSON 行。
- 文件：`report/index.html`、`ReportController.java`。
- 验收：筛选后预览行数和导出 CSV 行数一致；空结果有提示。

## 四、后两周安排

### 第 2 周（12/07 – 12/13）：补齐第二个新增模块

| 成员 | 第二个新增模块 |
| --- | --- |
| 于文博 | 签到统计（总次数、最近签到） |
| 刘子瑜 | 错题本 |
| 张宇轩 | 大屏显示设置（全屏、轮播、主题） |
| 郭曦 | 统计图表（签到率、正确率、选项分布） |
| 陈嘉 | 不新增模块，负责题库模块联调和全组回归测试 |

12/13 之前，每人 3 个模块必须全部 push。

### 第 3 周（12/14 – 12/20）：只修 bug、联调、答辩

- 12/14 – 12/16：全流程回归，修 P0/P1 bug；
- 12/17 – 12/18：补边界情况、空数据、错误提示；
- 12/19：演示彩排，准备 5 分钟讲稿；
- 12/20：提交/答辩。

第 3 周不再加新功能，避免越改越乱。

## 五、文件范围（只改自己的）

| 成员 | 前端可改 | 后端可改/新建 | 只读，不要改 |
| --- | --- | --- | --- |
| 陈嘉 | `teacher/home.html`、`teacher/publish.html` | `ClassroomController`、`ExerciseController`、`Classroom`、`Exercise`、`ClassroomMapper`、`ExerciseMapper` | `AnswerMapper`、`CheckinMapper`、`UserMapper` |
| 于文博 | `student/join.html` | `CheckinController`、`Checkin`、`CheckinMapper` | `ClassroomMapper`、`UserMapper` |
| 刘子瑜 | `student/answer.html` | `AnswerController`、`Answer`、`AnswerMapper` | `ExerciseMapper`、`UserMapper` |
| 张宇轩 | `screen/index.html` | `ScreenController` | `CheckinMapper`、`AnswerMapper`、`ExerciseMapper`、`ClassroomMapper`、`UserMapper` |
| 郭曦 | `report/index.html` | `ReportController` | 不要改别人的 Controller/Entity/Mapper，报表直接用 JdbcTemplate 查 |

公共文件本轮冻结：`common.js`、`common.css`、`index.html`、`register.html`、`AuthController`、`WebMvcConfig`、`application.yml`、`V1__init.sql`、`pom.xml`。

## 六、开发标准

1. **不新增表、不改表结构**。现有 `user`、`classroom`、`checkin`、`exercise`、`answer` 五张表够用。
2. 新接口统一返回 `{ code, msg, data }`，成功 `code = 200`；字段一律小驼峰。
3. 前端请求统一用 `myFetch`，不要自己另写一套 token 处理。
4. 时间统一 `yyyy-MM-dd HH:mm:ss`；Java 用 `LocalDateTime` 时加 `@JsonFormat`。
5. id 一律传数字，不要传字符串；学生题目列表不能返回 `answer`。
6. 页面必须有登录校验和导航，不能只写一个裸接口。
7. 题干、课程名、学生名展示前要转义，不能直接拼 HTML。
8. 每个模块单独 commit，提交前先 `git pull`，只 `git add` 自己的文件，不要 `git add .`。
9. commit 信息格式：`feat: M1 我的签到记录`、`fix: M4 答题页转义`。
10. 不引入 Vue、Node、Redis、WebSocket、ECharts CDN；保持原生 HTML/CSS/JS + Spring Boot。
11. 改接口字段先改 `docs/API_CONTRACT.md`，并在群里说，谁改字段谁请奶茶。
12. 每次提交前本地跑通：`mvn clean package`，老师/学生登录，走一遍自己的模块。

## 七、下周验收清单

- [ ] 现有主流程还能跑：建课 → 签到 → 发题 → 答题 → 大屏 → 导出。
- [ ] 大屏打开时能明确显示是哪节课，不再串课。
- [ ] 答题页题干不会把页面搞乱，提交按钮不能连点。
- [ ] 发题页和后台都按 4 个选项校验。
- [ ] 报表页先加载课堂列表，空数据有提示。
- [ ] 每人完成 1 个新增模块，并 push。
- [ ] 每人知道自己还差几个模块，12/13 前交满 3 个。
- [ ] 没有改公共文件，没有新增表。

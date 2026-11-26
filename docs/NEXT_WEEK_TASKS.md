# 下周任务分配（每人 3 个模块）

老师要求每个人负责 3 个模块。本轮不再给 PM 排新模块，PM 已有的框架、登录鉴权、公共 JS/CSS 不再重复计入。

下周目标：每人交 3 个**能演示的最小版本**。全部基于现有 5 张表，不新增表、不换技术栈、不引入 Vue/Node/Redis；现有主流程不能坏。

## 一、总表

| 成员 | 模块 1 | 模块 2 | 模块 3 |
| --- | --- | --- | --- |
| 陈嘉（M2） | 建课与课堂管理 | 发题与题目管理 | 题库复用 |
| 于文博（M1） | 进课签到 | 我的签到记录 | 签到统计 |
| 刘子瑜（M4） | 在线答题 | 我的答题记录与成绩 | 错题本 |
| 张宇轩（M5） | 实时统计大屏 | 课堂排行榜 | 大屏显示设置 |
| 郭曦（M3） | CSV 导出 | 报表筛选与预览 | 统计图表 |

## 二、每个人具体做什么

### 陈嘉（M2）：教师端 3 个模块

**模块 1：建课与课堂管理**

- 保留现有：建课、6 位口令、课堂列表、按口令查课。
- 新增：
  - 课堂列表按课程名/口令搜索；
  - 删除课堂（弹窗确认），删除时连同该课堂的题目、签到、答题一起清理；
  - 删除要用 `@Transactional`，避免只删一半。
- 接口：现有 `POST /api/classroom`、`GET /api/classroom/list`、`GET /api/classroom/byCode`；新增 `DELETE /api/classroom/{id}`。
- 文件：`teacher/home.html`、`ClassroomController.java`、`ClassroomMapper.java`；删除关联数据时可以调用 M1/M4 的 Mapper，但不要改他们的文件。
- 验收：建两个课，删掉其中一个，另一个的签到/题目/答题不受影响。

**模块 2：发题与题目管理**

- 保留现有：发布单选、题目列表（不返回 `answer`）。
- 新增：
  - 编辑题干、选项、正确答案；
  - 删除题目（同时删除这道题的答题记录）；
  - 题目列表按题干搜索。
- 接口：现有 `POST /api/exercise`、`GET /api/exercise/list`；新增 `PUT /api/exercise/{id}`、`DELETE /api/exercise/{id}`。
- 文件：`teacher/publish.html`、`ExerciseController.java`、`ExerciseMapper.java`。
- 验收：改一道题的答案后，学生重新答题能按新答案判分；删题后答题记录一起消失。

**模块 3：题库复用**

- 新增：把一道题复制到另一个课堂，选项和答案原样带过去。
- 接口：`POST /api/exercise/copy`，请求 `{ exerciseId, targetClassroomId }`，返回新题目 id。
- 文件：`ExerciseController.java`、`teacher/publish.html`。
- 验收：A 课堂的题复制到 B 课堂，B 课堂的题目列表能看到，答案不变。

### 于文博（M1）：学生签到 3 个模块

**模块 1：进课签到**

- 保留现有：输入口令查课、签到、重复签到拦截、保存 `classroomId`。
- 要求：不要破坏现有签到接口和页面。

**模块 2：我的签到记录**

- 新增：学生查看自己签过哪些课、签到时间。
- 接口：`GET /api/checkin/my?studentId=2`，返回课堂名称、课堂口令、签到时间。
- 页面：`student/join.html` 增加“我的签到记录”区域或标签页。
- 文件：`CheckinController.java`、`CheckinMapper.java`、`student/join.html`；课堂名可以调用 M2 的 `ClassroomMapper`，不要改。
- 验收：stu01 签两节课，记录里两节都能看到，时间正确。

**模块 3：签到统计**

- 新增：学生个人签到统计：总签到次数、最近一次签到时间、最近签到课堂。
- 接口：`GET /api/checkin/my/stats?studentId=2`。
- 页面：`student/join.html` 显示三个数字卡片。
- 文件：`CheckinController.java`、`CheckinMapper.java`、`student/join.html`。
- 验收：签到后数字加一；重复签到不重复计数。

### 刘子瑜（M4）：学生答题 3 个模块

**模块 1：在线答题**

- 保留现有：加载题目、选项、提交、后端判题、重复提交拦截。
- 要求：现有答题流程不能坏。

**模块 2：我的答题记录与成绩**

- 新增：学生查看答过的题、自己的选择、对错、正确率。
- 接口：`GET /api/answer/my?studentId=2`，返回题目、选项、选择、正确答案、是否正确。
- 页面：`student/answer.html` 增加“我的记录”标签页。
- 文件：`AnswerController.java`、`AnswerMapper.java`、`student/answer.html`；题目信息调用 M2 的 `ExerciseMapper`，不要改。
- 验收：答 3 题对 2 题，正确率显示 66.7%；答错的题能看到正确答案。

**模块 3：错题本**

- 新增：只显示答错的题，支持按课堂筛选，展示正确答案和选项。
- 接口：`GET /api/answer/wrong?studentId=2&classroomId=1`。
- 页面：`student/answer.html` 增加“错题本”标签页。
- 文件：`AnswerController.java`、`AnswerMapper.java`、`student/answer.html`。
- 验收：答错一题后错题本出现该题；答对后不出现。

### 张宇轩（M5）：大屏 3 个模块

**模块 1：实时统计大屏**

- 保留现有：课堂口令、签到数、答题数、A/B/C/D 分布、3 秒轮询。
- 要求：现有大屏不能坏。

**模块 2：课堂排行榜**

- 新增：签到速度前 5 名、答题正确数前 5 名（或正确率前 5 名）。
- 接口：`GET /api/screen/rank?classroomId=1`，返回两个排行榜数组。
- 页面：`screen/index.html` 增加排行榜区域，和统计区域轮播或按钮切换。
- 文件：`ScreenController.java`、`screen/index.html`。
- 验收：3 个学生签到/答题后，排行榜按时间和正确数排序正确。

**模块 3：大屏显示设置**

- 新增：全屏按钮、统计/排行榜/口令轮播（10 秒一换）、字号或深色/浅色切换；设置存 `localStorage`。
- 文件：`screen/index.html`，不新增后端接口。
- 验收：刷新页面后设置还在；轮播能自动切换。

### 郭曦（M3）：报表 3 个模块

**模块 1：CSV 导出**

- 保留现有：按课堂导出六列 CSV，UTF-8 BOM，签到未答题也留一行。
- 要求：现有导出不能坏。

**模块 2：报表筛选与预览**

- 新增：按课堂、学生账号/姓名、签到时间范围筛选；筛选结果先在页面表格预览，再导出。
- 接口：`GET /api/report/list?classroomId=1&keyword=张三&start=2026-11-30&end=2026-12-06`，返回 JSON。
- 页面：`report/index.html` 增加筛选表单和预览表格。
- 文件：`ReportController.java`、`report/index.html`。
- 验收：筛选后预览行数和导出 CSV 行数一致。

**模块 3：统计图表**

- 新增：签到率、答题正确率、A/B/C/D 分布，用纯 HTML/CSS 柱状图或进度条展示，不引入 ECharts/CDN。
- 接口：`GET /api/report/summary?classroomId=1`，返回 `studentCount`、`checkinCount`、`answerCount`、`correctCount`、`optionCount`。
- 页面：`report/index.html` 增加图表区域。
- 文件：`ReportController.java`、`report/index.html`。
- 验收：数据变化后图表跟着变；和数据库统计一致。

## 三、时间安排

| 日期 | 任务 |
| --- | --- |
| 11/30 周一 | 每人完成模块 1 最小版，自己跑通，commit + push |
| 12/01 周二 | 模块 1 联调修 bug，开始模块 2 |
| 12/02 周三 | 模块 2 完成并 push，群里说一声 |
| 12/03 周四 | 模块 2 联调，开始模块 3 |
| 12/04 周五 | 模块 3 完成并 push |
| 12/05 周六 | 每人自测 3 个模块，更新 `docs/API_CONTRACT.md` |
| 12/06 周日 | 全组集成，按“建课→签到→发题→答题→大屏→报表”走一遍，准备演示 |

## 四、共同规矩

1. 只改自己负责的文件，公共的 `common.js`、`common.css`、`index.html`、`register.html`、`AuthController` 不要动。
2. 不新增数据库表；现有 5 张表够用。确实要加字段，先在群里说，不要自己改 `V1__init.sql`。
3. 新接口统一返回 `Result`，字段小驼峰，前端用 `myFetch`，不要另写一套。
4. 每个模块单独 commit，提交前先 `git pull`，只 `git add` 自己的文件。
5. 三个模块都要有页面入口或按钮，不能只写接口。
6. 现有主流程必须保持可用：建课、签到、发题、答题、大屏、导出，不能因为加模块坏掉。
7. 12/06 集成时每人准备 2 分钟演示：自己的 3 个模块各点一下，说明数据从哪来。

## 五、验收清单

- [ ] 陈嘉：删除课堂会清理关联数据；题目可编辑/删除/复制。
- [ ] 于文博：我的签到记录、签到次数、最近签到时间正确。
- [ ] 刘子瑜：我的答题记录、正确率、错题本正确。
- [ ] 张宇轩：排行榜排序正确；大屏轮播/全屏可用。
- [ ] 郭曦：筛选预览和导出结果一致；图表数据正确。
- [ ] 全组：建课→签到→发题→答题→大屏→导出完整跑通。
- [ ] 每人正好 3 个模块，页面能演示，不超范围。

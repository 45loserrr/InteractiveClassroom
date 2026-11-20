# 模块联动说明（组员开发要求）

这份文档讲清楚模块之间怎么配合，以及每个人要写到什么程度，才能让整条流程跑通。
接口的字段名以 `API_CONTRACT.md` 为准，这里重点讲“谁把什么数据交给谁”。

## 一、整体流程

```
老师登录
  ↓
M2 建课：生成 classroomId 和课堂口令 code
  ↓
老师打开大屏 /screen/?classroomId=1 —— 这一步就是“发起签到”
  ↓
学生输入 code，M1 签到，写 checkin 表
  ↓
M5 大屏轮询统计，签到人数 +1
  ↓
M2 发题，写 exercise 表
  ↓
学生答题，M4 写 answer 表
  ↓
M5 大屏显示答题人数和选项分布
  ↓
M3 导出 CSV（checkin + answer + user + exercise）
```

### “发起签到”怎么算

这套极简版没有单独的“签到开关”表。老师点“开始签到”就是**打开大屏**，大屏把课堂口令显示出来，学生看到口令就能签。

如果后面想让老师点“开始/结束”才能签到，再给 `classroom` 表加一个 `status` 字段（0 未开始、1 签到中、2 已结束）。这是加分项，不是现在必须做的。

## 二、数据是怎么串起来的

| 表 | 谁写 | 谁读 | 关键字段 | 作用 |
| --- | --- | --- | --- | --- |
| `user` | PM | 所有人 | id、role、real_name | 老师/学生身份、姓名 |
| `classroom` | M2 | M1/M2/M4/M5/M3 | id、code、teacher_id | 课堂主键和口令 |
| `checkin` | M1 | M5/M3 | classroom_id、student_id | 签到记录 |
| `exercise` | M2 | M4/M5/M3 | id、classroom_id、answer | 题目和正确答案 |
| `answer` | M4 | M5/M3 | exercise_id、student_id、choose_option | 答题记录 |

核心就一句话：**`classroomId` 把所有表串起来**。

- `classroom.id` 是建课生成的；
- `checkin.classroom_id` 和 `exercise.classroom_id` 都用它；
- `answer.exercise_id` 用 `exercise.id`；
- `teacher_id`、`student_id` 都是 `user.id`。

数据库没加外键，所以代码里必须自己保证 id 是真的，不能随便传一个不存在的 id。

## 三、前端 localStorage 约定

| key | 谁写 | 谁读 | 说明 |
| --- | --- | --- | --- |
| `token` | PM 登录 | 所有接口 | `myFetch` 自动带 |
| `role` | PM 登录 | 页面校验 | teacher / student |
| `realName` | PM 登录 | 导航栏 | 显示姓名 |
| `userId` | PM 登录 | M1/M4 | 当前用户 id |
| `classroomId` | M2 建课 / M1 签到 | M2 发题、M4 答题、M5 大屏、M3 报表 | 当前课堂 id |
| `classroomCode` | M2 建课 / M1 签到 | M5 大屏 | 当前课堂口令 |
| `classroomName` | M2 建课 / M1 签到 | 页面显示 | 当前课堂名称 |

规则：

- 老师建完课，M2 要写 `classroomId`、`classroomCode`、`classroomName`；
- 学生签到成功，M1 也要写这三个；
- M4 答题页没有 `classroomId` 就提示“先去签到”；
- 退出登录时 `common.js` 已经把这几个一起清掉了。

## 四、每个人的开发要求

### M2：建课（第一个交）

**后端**

1. `POST /api/classroom`
   - 请求：`{ "name": "软件工程", "teacherId": 1 }`
   - 校验课程名不能为空；
   - 生成 6 位口令：大写字母 + 数字，循环查数据库直到不重复；
   - 插入 `classroom`，拿到自增 id；
   - 返回：`{ id, code, name, teacherId, teacherName }`；
   - **必须返回 `id` 和 `code`**，M1 和大屏都要用。

2. `GET /api/classroom/byCode?code=A1B2C3`
   - 按 `code` 查课堂；
   - 查不到返回 400「课堂口令不存在」；
   - 返回同上一个对象。M1 就靠这个接口找课堂。

3. `GET /api/classroom/list?teacherId=1`
   - 查这个老师建的课堂，返回数组；
   - 报表页选课堂时会用。

**前端 `teacher/home.html`**

- 建课成功后，页面上显示课堂口令和课堂 id；
- 写入 localStorage：`classroomId`、`classroomCode`、`classroomName`；
- 放一个“开始签到”按钮，点击打开：
  `/screen/?classroomId=当前课堂id`；
- 页面上能直接看到口令，方便投屏。

**验收**

- 建课后能看到 6 位口令和 id；
- 打开大屏能带上课 id；
- 把口令给 M1，M1 能查到课堂。

### M1：签到（第二个交）

**后端**

1. `POST /api/checkin`
   - 请求：`{ "classroomId": 1, "studentId": 2 }`
   - 先查课堂是否存在；
   - 再查这个学生是否已经签过：`classroom_id = ? AND student_id = ?`；
   - 重复签到返回 400「你已经签到过了」；
   - 插入签到，`createTime` 用 `LocalDateTime.now()`；
   - 返回：`{ id, classroomId, studentId, studentName, createTime }`；
   - `studentName` 用 PM 的 `UserMapper` 查，不要改 UserMapper。

2. `GET /api/checkin/list?classroomId=1`
   - 返回这个课堂的签到列表，带 `studentName`；
   - 大屏和报表会读，字段按契约来。

**前端 `student/join.html`**

- 输入口令，统一转大写；
- 先调 `GET /api/classroom/byCode?code=...`；
- 查到课堂后调 `POST /api/checkin`，传 `classroomId` 和 `userId`；
- 成功后写入 `classroomId`、`classroomCode`、`classroomName`；
- 页面上提示签到成功，给一个“去答题”的链接；
- 重复签到把后端的 msg 弹出来。

**验收**

- 用 M2 建课的口令能签到；
- 同一账号再签一次提示“你已经签到过了”；
- 大屏上的签到人数 +1；
- `localStorage.classroomId` 有值，M4 能读到。

### M2：发题（第三次交）

**后端**

1. `POST /api/exercise`
   - 请求：`{ classroomId, title, options, answer }`
   - `options` 是 `A.80|B.443|C.8080|D.3306` 这种格式；
   - `answer` 只能是 A/B/C/D；
   - 校验课堂存在；
   - 插入后返回 `{ "id": 题目id }`。

2. `GET /api/exercise/list?classroomId=1`
   - 返回：`[{ id, classroomId, title, options }]`；
   - **绝对不能返回 `answer`**，不然学生按 F12 就作弊了；
   - M4 答题页要靠这个接口拿题。

**前端 `teacher/publish.html`**

- 从 localStorage 读 `classroomId`；
- 没有就提示“先建课”；
- 页面上显示当前课堂名称/口令，避免老师填错课堂；
- 四个选项拼成 `A.xxx|B.xxx|C.xxx|D.xxx` 再提交；
- 发布成功显示题目 id。

**验收**

- 用建课的 classroomId 能发题；
- M4 用同一个 classroomId 能拿到题目列表；
- 列表里没有正确答案字段。

### M4：答题（第四次交）

**后端**

1. `POST /api/answer`
   - 请求：`{ exerciseId, studentId, chooseOption }`
   - 先查题目是否存在；
   - 再查这个学生是否答过这道题：`exercise_id = ? AND student_id = ?`；
   - 重复提交返回 400「你已经答过这题了」；
   - 用 M2 的 `ExerciseMapper` 查 `exercise.answer`，和 `chooseOption` 比较；
   - 插入 `answer`；
   - 返回：`{ correct: true/false, rightAnswer: "A" }`；
   - 对错必须后端判，不能信前端。

**前端 `student/answer.html`**

- 从 localStorage 读 `classroomId`，没有就提示“先去签到”；
- 调 `GET /api/exercise/list?classroomId=...` 拿题；
- `options.split('|')` 拆成四个选项；
- 选中后调 `POST /api/answer`；
- 页面上显示“答对了”或“答错了，正确答案是 X”；
- 同一题提交过后，再点提交要能看到后端的重复提示。

**验收**

- M2 发题、M1 签到后，学生能答题；
- 答完大屏的答题人数 +1，选项分布变化；
- 重复答同一题返回 400。

### M5：大屏（第五次交）

**后端**

`GET /api/screen/stats?classroomId=1`

返回：

```json
{
  "classroomName": "软件工程",
  "classroomCode": "A1B2C3",
  "studentCount": 3,
  "checkinCount": 2,
  "answerCount": 5,
  "optionCount": { "A": 3, "B": 1, "C": 1, "D": 0 }
}
```

实现思路：

- `classroomName`、`classroomCode` 用 M2 的 `ClassroomMapper` 查；
- `studentCount` 用 `UserMapper` 查 `role = 'student'` 的人数；
- `checkinCount` 用 M1 的 `CheckinMapper` 按课堂 count；
- 先查这个课堂的所有 `exercise`，拿到题目 id 列表；
- 再用 M4 的 `AnswerMapper` 查这些题目的答题记录；
- 统计 `answerCount` 和每个选项的数量；
- `optionCount` 的 A/B/C/D 四个键必须都有，没人选就是 0。

**前端 `screen/index.html`**

- 课堂 id 优先从网址 `?classroomId=1` 拿，没有再看 localStorage；
- 大屏上要显示课堂口令，学生就是看这个口令签到的；
- 每 3 秒调一次 stats，更新签到数、答题数、选项柱状图；
- 柱状图宽度按 `当前选项人数 / 最大选项人数 * 100%` 算。

**验收**

- 大屏保持打开；
- M1 签到后 3 秒内签到数 +1；
- M4 答题后 3 秒内答题数 +1，对应选项柱子变长；
- 大屏上能看到课堂口令。

### M3：报表（最后交）

**后端**

`GET /api/report/export?classroomId=1`

- 返回 CSV，不是 JSON；
- 文件开头加 UTF-8 BOM，不然 Excel 中文乱码；
- 列顺序固定：`账号,姓名,签到时间,题目,选择,是否正确`；
- 数据来源：M1 的签到、M2 的题目、M4 的答题、PM 的用户；
- 建议做法：先查签到列表，再查题目和答题，按学生和题目拼行；
- 签到了但没答题的学生，输出一行，题目/选择留空；
- 答了题但没签到的学生，也可以输出，签到时间留空。

**前端 `report/index.html`**

- 用 `GET /api/classroom/list?teacherId=...` 让老师选课堂，或者直接填课堂 id；
- 下载必须用 `fetch` + `blob`，因为 `<a href>` 带不了 token；
- 文件名例如 `classroom_1_report.csv`。

**验收**

- 签到和答题都有数据后导出；
- Excel 打开中文正常；
- 行数和数据库里的签到/答题对得上。

### PM：公共部分

- 提供 `token`、`Result`、`myFetch`、`checkLogin`、`UserMapper`；
- `logout` 会清掉 `classroomId`、`classroomCode`、`classroomName`；
- 谁要改接口字段，先改 `API_CONTRACT.md` 再改代码。

## 五、跨模块硬性规定

1. 所有 `/api/**`（除 `/api/auth/**`）都要带 `token`，前端统一用 `myFetch`。
2. 所有接口返回 `{ code, msg, data }`，成功 `code = 200`。
3. id 一律用数字，不要传字符串。
4. 时间统一 `yyyy-MM-dd HH:mm:ss`，Java 用 `LocalDateTime` 时加 `@JsonFormat`。
5. `options` 用竖线拼：`A.80|B.443|C.8080|D.3306`。
6. 学生拿到的题目列表不能有 `answer` 字段。
7. M4、M5、M3 可以调用别人的 Mapper 查数据，但不能改别人的文件。
8. 不要跨模块直接改数据库表结构，要加字段先找 PM。
9. 大屏每 3 秒刷新一次，不要用 WebSocket，大作业没必要。
10. 前端页面只改自己那个 html，公共 CSS/JS 不要动。

## 六、联调验收顺序

按这个顺序测，哪一步断了就找对应的人：

1. 老师登录，建课，拿到 id 和口令；
2. 打开 `/screen/?classroomId=1`，大屏显示口令和 0；
3. 学生另一个浏览器登录，输入口令签到；
4. 大屏 3 秒内签到数 +1；
5. 同一学生再签一次，提示“你已经签到过了”；
6. 老师发题，学生答题页能加载题目；
7. 学生提交答案，大屏答题数 +1、选项柱子变化；
8. 同一学生再答一次，提示“你已经答过这题了”；
9. 老师导出 CSV，Excel 打开中文正常；
10. 退出登录，再访问教师/学生页面，应该被赶回登录页。

## 七、常见联调问题

- **M4 说没题目**：先看 M2 的 `/api/exercise/list?classroomId=...` 有没有数据，再看 M4 读的 `classroomId` 是不是 M1 存的那个。
- **大屏不动**：先看 `/api/screen/stats` 返回的对不对，再看大屏有没有开 `setInterval`。
- **签到数不动**：看 `checkin.classroom_id` 是不是大屏的课堂 id，别把课堂 id 和题目 id 搞混。
- **报表是空的**：看导出的 classroomId 对不对，再看签到和答题是不是同一个课堂。
- **401**：token 没带或过期，重新登录；`myFetch` 会自动带，自己写 fetch 的要手动加 header。

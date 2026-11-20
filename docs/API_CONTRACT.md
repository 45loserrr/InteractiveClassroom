# 移动互动课堂 · 接口契约

> **因为大家各写各的，接口入参出参必须按照以下格式来，谁改字段名谁负责请全组喝奶茶。**
>
> 这份文档就是前后端之间的“合同”。写代码前先看，改代码前先改这里，改完在群里说一声。
>
> 模块之间怎么配合、每个人要写到什么程度，看 [MODULE_FLOW.md](MODULE_FLOW.md)。

## 0. 三句话记住全部规则

1. 后端永远返回 `{ code, msg, data }`，前端只认 `code === 200`。
2. 登录后每个请求都要带请求头 `token`，前端统一用 `myFetch`，它已经自动带了。
3. JSON 字段一律**小驼峰**（`classroomId`、`realName`），数据库字段是**下划线**（`classroom_id`、`real_name`），MyBatis-Plus 自动转换，不要手写映射。

---

## 1. 统一返回格式

所有 `/api/**` 接口（除了导出 CSV）都返回这个结构：

```json
{
  "code": 200,
  "msg": "成功",
  "data": {}
}
```

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| code | number | 200 成功；400 业务失败；401 没登录；500 服务器错误 |
| msg | string | 给用户看的提示，前端可以直接 alert |
| data | 任意 | 成功时的数据；可以是对象、数组，也可以为 null |

**成功（有数据）**

```json
{
  "code": 200,
  "msg": "成功",
  "data": { "id": 1, "name": "软件工程" }
}
```

**成功（没有数据）**

```json
{
  "code": 200,
  "msg": "注册成功",
  "data": null
}
```

**业务失败**

```json
{
  "code": 400,
  "msg": "用户名已存在，换一个吧",
  "data": null
}
```

> 注意：HTTP 状态码一律是 200，业务成功失败只看 `code`。只有拦截器发现没带 token 时返回 `{"code":401,"msg":"请先登录","data":null}`。

---

## 2. 登录状态怎么传

### 2.1 请求头

```
token: 3f2a9c1d8b7e4f6a9c0d1e2f3a4b5c6d
```

- Token 是登录接口返回的一串 UUID（去掉横杠，32 位）。
- 前端存在 `localStorage`，`js/common.js` 的 `myFetch` 自动读取并放进请求头。
- **不要**放 URL 参数里，也**不要**用 Cookie。

### 2.2 前端 localStorage 约定

| key | 值 | 什么时候写 |
| --- | --- | --- |
| token | 登录返回的 token | 登录成功后 |
| role | `teacher` / `student` | 登录成功后 |
| realName | 姓名 | 登录成功后 |
| userId | 用户 id（数字） | 登录成功后 |
| classroomId | 当前课堂 id（数字） | M2 建课后 / M1 签到后 |
| classroomCode | 当前课堂口令 | M2 建课后 / M1 签到后 |
| classroomName | 当前课堂名称 | M2 建课后 / M1 签到后 |

退出登录时 `common.js` 会把这些一起清掉。

### 2.3 没登录会怎样

任何 `/api/**`（除 `/api/auth/**`）没带 token，都会收到：

```json
{ "code": 401, "msg": "请先登录", "data": null }
```

`myFetch` 会自动清空 localStorage 并跳回 `/index.html`（登录页）。

### 2.4 页面入口

| 页面 | 路径 | 负责人 |
| --- | --- | --- |
| 登录 | `/index.html` | PM |
| 注册 | `/register.html` | PM |
| 教师端（建课） | `/teacher/` | M2 |
| 教师发题 | `/teacher/publish.html` | M2 |
| 学生端（签到） | `/student/` | M1 |
| 学生答题 | `/student/answer.html` | M4 |
| 课堂大屏 | `/screen/` | M5 |
| 数据导出 | `/report/` | M3 |

登录成功后：老师跳 `/teacher/`，学生跳 `/student/`。

---

## 3. 接口总表

| 模块 | 方法 | 路径 | 负责人 | 状态 |
| --- | --- | --- | --- | --- |
| 登录 | POST | `/api/auth/login` | PM | ✅ 已完成 |
| 注册 | POST | `/api/auth/register` | PM | ✅ 已完成 |
| 建课 | POST | `/api/classroom` | M2 | ⬜ 待开发 |
| 课堂列表 | GET | `/api/classroom/list` | M2 | ⬜ 待开发 |
| 按口令查课堂 | GET | `/api/classroom/byCode` | M2 | ⬜ 待开发 |
| 签到 | POST | `/api/checkin` | M1 | ⬜ 待开发 |
| 签到列表 | GET | `/api/checkin/list` | M1 | ⬜ 待开发 |
| 发布题目 | POST | `/api/exercise` | M2 | ⬜ 待开发 |
| 题目列表 | GET | `/api/exercise/list` | M2 | ⬜ 待开发 |
| 提交答案 | POST | `/api/answer` | M4 | ⬜ 待开发 |
| 大屏统计 | GET | `/api/screen/stats` | M5 | ⬜ 待开发 |
| 导出报表 | GET | `/api/report/export` | M3 | ⬜ 待开发 |

> 状态改成 ✅ 之前，请自己用浏览器或 Postman 调通，并把示例贴回本文档。

---

## 4. 每个接口的详细定义

### 4.1 登录（已完成）

`POST /api/auth/login`

**请求**

```json
{
  "username": "stu01",
  "password": "123456"
}
```

**成功返回**

```json
{
  "code": 200,
  "msg": "登录成功",
  "data": {
    "token": "3f2a9c1d8b7e4f6a9c0d1e2f3a4b5c6d",
    "id": 2,
    "username": "stu01",
    "role": "student",
    "realName": "张三"
  }
}
```

**失败返回**

```json
{ "code": 400, "msg": "账号或密码错误", "data": null }
```

---

### 4.2 注册（已完成）

`POST /api/auth/register`

**请求**

```json
{
  "username": "stu04",
  "password": "123456",
  "realName": "赵六",
  "role": "student"
}
```

- `role` 只能是 `teacher` 或 `student`，不传默认 `student`。
- `realName` 不传就用账号名。
- 账号重复会返回 400。

**成功返回**

```json
{ "code": 200, "msg": "注册成功", "data": null }
```

---

### 4.3 建课（M2）

`POST /api/classroom`

**请求**

```json
{
  "name": "软件工程",
  "teacherId": 1
}
```

- `teacherId` 从 localStorage 的 `userId` 拿，不要让学生手填。
- 后端生成一个 6 位课堂口令 `code`（大写字母 + 数字），学生凭它签到。

**成功返回**

```json
{
  "code": 200,
  "msg": "创建成功",
  "data": {
    "id": 1,
    "code": "A1B2C3",
    "name": "软件工程",
    "teacherId": 1,
    "teacherName": "王老师"
  }
}
```

> 返回的 `id` 和 `code` 是后面所有模块的入口：M1 用 code 找课堂，M4/M5/M3 用 id 查数据。

---

### 4.4 查课堂列表（M2）

`GET /api/classroom/list`

| 参数 | 位置 | 必填 | 说明 |
| --- | --- | --- | --- |
| teacherId | query | 否 | 传了就只查这个老师建的课；不传查全部 |

**返回**

```json
{
  "code": 200,
  "msg": "成功",
  "data": [
    {
      "id": 1,
      "code": "A1B2C3",
      "name": "软件工程",
      "teacherId": 1,
      "teacherName": "王老师"
    }
  ]
}
```

- `data` 直接是数组，不要再包一层 `{ "list": [...] }`。
- `teacherName` 是联表查出来的额外字段，数据库 `classroom` 表里没有，没关系。

---

### 4.5 按口令查课堂（M2）

`GET /api/classroom/byCode?code=A1B2C3`

学生签到前先用口令找到课堂。

**返回**：和 4.3 的单个对象一样，`data` 是一个对象；查不到返回 400。

> M1 的学生签到页先调这个接口，拿到课堂 id 再去签到。

```json
{
  "code": 200,
  "msg": "成功",
  "data": { "id": 1, "code": "A1B2C3", "name": "软件工程", "teacherId": 1, "teacherName": "王老师" }
}
```

---

### 4.6 签到（M1）

`POST /api/checkin`

**请求**

```json
{
  "classroomId": 1,
  "studentId": 2
}
```

- `studentId` 从 localStorage 的 `userId` 拿，不要让学生手填。
- 同一个人同一节课重复签到，返回 400「你已经签到过了」。

**成功返回**

```json
{
  "code": 200,
  "msg": "签到成功",
  "data": {
    "id": 10,
    "classroomId": 1,
    "studentId": 2,
    "studentName": "张三",
    "createTime": "2026-11-19 18:30:00"
  }
}
```

**重复签到**

```json
{ "code": 400, "msg": "你已经签到过了", "data": null }
```

> 签到成功后，M1 要把 `classroomId`、`classroomCode`、`classroomName` 存到 localStorage，M4 的答题页要用。

---

### 4.7 签到列表（M1）

`GET /api/checkin/list?classroomId=1`

老师看谁签到了，大屏和报表也会用。

**返回**

```json
{
  "code": 200,
  "msg": "成功",
  "data": [
    {
      "id": 10,
      "classroomId": 1,
      "studentId": 2,
      "studentName": "张三",
      "createTime": "2026-11-19 18:30:00"
    }
  ]
}
```

---

### 4.8 发布题目（M2）

`POST /api/exercise`

**请求**

```json
{
  "classroomId": 1,
  "title": "HTTP 协议默认端口是？",
  "options": "A.80|B.443|C.8080|D.3306",
  "answer": "A"
}
```

- `options` 就存一个字符串，选项之间用竖线 `|` 分隔，前端 `split('|')` 拆开。
- `answer` 只能是 `A`、`B`、`C`、`D` 之一。

**成功返回**

```json
{
  "code": 200,
  "msg": "发布成功",
  "data": { "id": 1 }
}
```

> 返回的题目 id 是 M4 提交答案时要用的。

---

### 4.9 题目列表（M2）

`GET /api/exercise/list?classroomId=1`

**返回**

```json
{
  "code": 200,
  "msg": "成功",
  "data": [
    {
      "id": 1,
      "classroomId": 1,
      "title": "HTTP 协议默认端口是？",
      "options": "A.80|B.443|C.8080|D.3306"
    }
  ]
}
```

> **注意：学生用的题目列表绝对不能返回 `answer` 字段**，不然打开 F12 就作弊了。
> 对错由 4.10 的答题接口在后台判断。
>
> M4 答题页用 `classroomId` 调这个接口，拿到题目 id 后再提交答案。

---

### 4.10 提交答案（M4）

`POST /api/answer`

**请求**

```json
{
  "exerciseId": 1,
  "studentId": 2,
  "chooseOption": "A"
}
```

- 同一题重复提交，返回 400「你已经答过这题了」。
- 对错由后端查 `exercise.answer` 判断，**不要信前端传来的“正确/错误”**。

**成功返回**

```json
{
  "code": 200,
  "msg": "提交成功",
  "data": {
    "correct": true,
    "rightAnswer": "A"
  }
}
```

**重复提交**

```json
{ "code": 400, "msg": "你已经答过这题了", "data": null }
```

---

### 4.11 大屏统计（M5）

`GET /api/screen/stats?classroomId=1`

**返回**

```json
{
  "code": 200,
  "msg": "成功",
  "data": {
    "classroomName": "软件工程",
    "classroomCode": "A1B2C3",
    "studentCount": 3,
    "checkinCount": 2,
    "answerCount": 5,
    "optionCount": { "A": 3, "B": 1, "C": 1, "D": 0 }
  }
}
```

| 字段 | 说明 |
| --- | --- |
| classroomName | 课堂名称 |
| classroomCode | 课堂口令，大屏上要显示出来，学生照着输 |
| studentCount | 学生总人数（user 表里 role = student 的人数） |
| checkinCount | 本课堂已签到人数 |
| answerCount | 本课堂已提交的答题总数 |
| optionCount | 每个选项被选了多少次，键就是选项字母 |

> `optionCount` 的 A/B/C/D 四个键**永远都要有**，没人选就是 0，不然大屏柱子会缺一根。

---

### 4.12 导出报表（M3）

`GET /api/report/export?classroomId=1`

**这是唯一一个不返回 JSON 的接口**，直接返回 CSV 文件：

- `Content-Type: text/csv;charset=UTF-8`
- `Content-Disposition: attachment; filename="classroom_1_report.csv"`
- 文件开头加 UTF-8 BOM（`\uFEFF`），不然 Excel 打开中文会乱码。

CSV 列顺序固定为：

```csv
账号,姓名,签到时间,题目,选择,是否正确
stu01,张三,2026-11-19 18:30:00,HTTP 协议默认端口是？,A,是
```

**前端下载示例**（因为 `<a href>` 带不了 token 请求头，要用 fetch + blob）：

```js
async function exportReport(classroomId) {
  const res = await fetch('/api/report/export?classroomId=' + classroomId, {
    headers: { token: localStorage.getItem('token') }
  });
  const blob = await res.blob();
  const a = document.createElement('a');
  a.href = URL.createObjectURL(blob);
  a.download = 'classroom_report.csv';
  a.click();
  URL.revokeObjectURL(a.href);
}
```

---

## 5. 字段对照表（数据库 → JSON）

> 后端实体用 Lombok `@Data`，字段名就是 JSON 字段名。数据库下划线由 MyBatis-Plus 自动映射。

### user（用户）

| 数据库 | JSON | 类型 | 说明 |
| --- | --- | --- | --- |
| id | id | number | 主键 |
| username | username | string | 登录账号 |
| password | password | string | 明文，**永远不要返回给前端** |
| role | role | string | `teacher` / `student` |
| real_name | realName | string | 姓名 |

### classroom（课堂）

| 数据库 | JSON | 类型 | 说明 |
| --- | --- | --- | --- |
| id | id | number | 主键 |
| code | code | string | 邀请码，例如 `A1B2C3` |
| name | name | string | 课程名 |
| teacher_id | teacherId | number | 老师 id |
| - | teacherName | string | 联表查出来的，数据库没有 |

### checkin（签到）

| 数据库 | JSON | 类型 | 说明 |
| --- | --- | --- | --- |
| id | id | number | 主键 |
| classroom_id | classroomId | number | 课堂 id |
| student_id | studentId | number | 学生 id |
| create_time | createTime | string | `yyyy-MM-dd HH:mm:ss` |
| - | studentName | string | 联表查出来的 |

### exercise（题目）

| 数据库 | JSON | 类型 | 说明 |
| --- | --- | --- | --- |
| id | id | number | 主键 |
| classroom_id | classroomId | number | 课堂 id |
| title | title | string | 题干 |
| options | options | string | 选项，用竖线分隔 |
| answer | answer | string | 正确答案；**学生端不返回** |

### answer（答题）

| 数据库 | JSON | 类型 | 说明 |
| --- | --- | --- | --- |
| id | id | number | 主键 |
| exercise_id | exerciseId | number | 题目 id |
| student_id | studentId | number | 学生 id |
| choose_option | chooseOption | string | 学生选的答案 |

---

## 6. 错误码和提示语

| code | 什么时候用 | msg 示例 |
| --- | --- | --- |
| 200 | 成功 | `成功` / `登录成功` / `签到成功` |
| 400 | 参数不对、业务不允许 | `账号或密码错误` / `你已经签到过了` / `用户名已存在，换一个吧` |
| 401 | 没带 token（拦截器统一返回） | `请先登录` |
| 500 | 代码抛异常 | `服务器开小差了` |

- 400 的 `msg` 要写成**人话**，能直接 alert 给用户看。
- 不要返回 `null` 当整个响应体，也不要把异常堆栈返回给前端。

---

## 7. 改字段的流程（奶茶条款）

1. 先改这份 `API_CONTRACT.md`。
2. 在群里 @ 所有人，说清楚：**改哪个接口、哪个字段、为什么改**。
3. 前端和后端同一天改完，不要只改一半。
4. 谁没按流程改，导致别人联调半小时以上，**谁请全组喝奶茶**（大杯，少冰）。

**禁止事项**

- 禁止把 `data` 从数组改成 `{ list: [] }`。
- 禁止把 `classroomId` 改成 `class_id`、`cid` 这种名字。
- 禁止自己另写一套返回格式（比如直接返回字符串或 `{ success: true }`）。
- 禁止在 JSON 里返回数据库下划线字段名。

---

## 8. 前端调用示例

`common.js` 已经提供了 `myFetch(url, method, data)`，**不要自己写 fetch**。

**GET**

```js
const res = await myFetch('/api/checkin/list?classroomId=1', 'GET');
if (res.code === 200) {
  console.log(res.data); // 数组
}
```

**POST**

```js
const res = await myFetch('/api/checkin', 'POST', {
  classroomId: 1,
  studentId: Number(localStorage.getItem('userId'))
});
if (res.code === 200) {
  alert('签到成功');
}
```

**渲染列表（原生 JS）**

```js
let html = '';
for (let i = 0; i < res.data.length; i++) {
  html += '<tr><td>' + res.data[i].studentName + '</td><td>' + res.data[i].createTime + '</td></tr>';
}
document.getElementById('tbody').innerHTML = html;
```

---

## 9. 联调前自查清单

- [ ] 请求头带了 `token`（打开 F12 → Network 看 Request Headers）。
- [ ] JSON 字段名是小驼峰，和本文档一字不差。
- [ ] id 是数字，不是字符串（`2` 不是 `"2"`）。
- [ ] 列表直接放 `data` 数组，没有多包一层。
- [ ] 时间统一 `yyyy-MM-dd HH:mm:ss`。
- [ ] 没登录调接口能看到 401，并且自动跳回登录页。
- [ ] 自己负责的接口状态在本文档第 3 节改成 ✅。

---

**最后再说一遍：谁改字段名谁负责请全组喝奶茶。**

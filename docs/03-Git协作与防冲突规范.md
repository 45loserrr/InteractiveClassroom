# Git 协作与防冲突规范

> 适用对象：本项目 6 人小组全体成员
> 生效时间：第 1 周仓库初始化后立即生效
> 违规后果：直推 `main` 的提交将被回滚，责任人需在群内说明并重做

---

## 一、分支模型

| 分支 | 用途 | 谁能写 |
| :--- | :--- | :--- |
| `main` | 随时可编译、可演示的稳定分支，答辩用 | **只有 PM**（通过 PR 合并） |
| `feat/<模块>-<功能>` | 功能开发，如 `feat/m1-checkin` | 对应模块负责人 |
| `fix/<模块>-<问题>` | Bug 修复，如 `fix/m3-cors` | 对应模块负责人 |
| `docs/<主题>` | 文档修改 | 任何人 |

**分支命名规则**：`类型/模块-简述`，全小写，用 `-` 连接，禁止中文与空格。
模块代号：`m1`（学生端）、`m2`（教师端）、`m3`（后端）、`m4`（实时）、`m5`（大屏测试）、`pm`（管理）。

---

## 二、四条铁律

### 2.1 物理隔离：只改自己的文件夹

**原则**：每个人只在自己的负责目录内新增/修改文件，从源头消灭冲突。

| 角色 | 只有权修改 | 只读（可看不可改） |
| :--- | :--- | :--- |
| PM | `README.md`、`docs/`、根目录脚本 | 其余全部 |
| M1 学生端 | `frontend/student/` | 其他前端、`backend/` |
| M2 教师端 | `frontend/teacher/` | 其他前端、`backend/` |
| M3 后端 API | `backend/src/main/java/com/classroom/{controller,service,mapper,entity,config,utils}/`、`backend/src/main/resources/{application.yml,db/}`、`backend/pom.xml` | `websocket/`（M4 领地）、前端 |
| M4 实时服务 | `backend/src/main/java/com/classroom/websocket/` | 其余后端 |
| M5 大屏+测试 | `frontend/screen/`、`docs/测试报告/` | 其余全部 |

**例外流程**：确需修改他人目录时，**先在群里 @ 负责人说明原因并取得同意**，由对方修改或对方在 PR 里 co-author，不得擅自提交。

### 2.2 禁止直推：必须提 PR

- **任何情况下**不得 `git push origin main`。
- 唯一例外：**仓库初始化提交**（PM 首次建立骨架并推送），此后 `main` 只接受 PR 合并。
- 所有变更经由：`feature 分支 push` → `Pull Request` → `PM Review` → `Squash merge`。
- PR 描述必须包含：**改了什么 / 怎么自测 / 影响哪些模块 / 截图或接口返回**。
- 合并前 CI/本地必须满足：后端 `mvn -q -DskipTests package` 通过；教师端 `npm run build` 通过。

**PR 检查清单（复制到每个 PR 描述里）**

```
- [ ] 只改动了我负责的目录
- [ ] 本地编译/构建通过
- [ ] 自测了主流程（说明步骤）
- [ ] 没有提交 .env / application-local.yml / *.log / 密码
- [ ] 接口变更已同步 docs/04-接口契约模板.md
```

### 2.3 每日动作：先 pull 后 merge

**每天开工第一件事**（10 秒，必做）：

```bash
git checkout main
git pull origin main
git checkout <你的分支>
git merge origin/main        # 把主干合进自己的分支，而不是反过来
```

**每天收工前**：`git add -A && git commit -m "..." && git push origin <你的分支>`，禁止把代码留在本地过夜。

**commit message 规范**（便于生成周报）：

| 类型 | 含义 | 示例 |
| :--- | :--- | :--- |
| `feat` | 新功能 | `feat(m1): 完成签到页重复签到提示` |
| `fix` | 修复 Bug | `fix(m3): 修复签到接口并发重复插入` |
| `docs` | 文档 | `docs(pm): 补充第 2 周接口契约` |
| `refactor` | 重构 | `refactor(m4): 抽出 WebSocket 消息封装` |
| `chore` | 杂项/构建 | `chore(pm): 初始化项目骨架` |
| `test` | 测试 | `test(m5): 新增 30 人并发抢答脚本` |

格式：`类型(模块): 一句话说清做了什么`，不超过 50 字，**禁止** `update`、`修改`、`aaa` 之类无信息提交。

### 2.4 冲突解决：谁后合并谁负责

- 冲突判定：`git merge` 报 `CONFLICT` 时，**后提交/后合并的一方**负责解决。
- 解决步骤：
  1. `git status` 看冲突文件，只改自己负责的文件；
  2. 他人文件的冲突，**不要自己拍板**，把冲突片段截图发群里，由该文件负责人决定保留哪边；
  3. 解决后 `git add <文件> && git commit`（保留 merge commit 信息）；
  4. 本地重新编译 + 自测，再 push。
- **严禁**用 `git push -f` 覆盖他人提交；`main` 分支已开启保护，强推无效。
- 如果冲突文件里出现"两边都要"的逻辑（如两套工具类），由 PM 拉 5 分钟小会决策，不允许多人同时改同一文件过夜。

---

## 三、提交前的三查

```bash
# 1. 我改了哪些文件？是否越界？
git status

# 2. 有没有把密码/日志带上？
git diff --cached | findstr /i "password token secret .env"    # Windows
git diff --cached | grep -iE "password|token|secret|\.env"     # macOS/Linux

# 3. 能不能编译？
cd backend && mvn -q -DskipTests package
cd frontend/teacher && npm run build
```

---

## 四、常见事故与处置

| 事故 | 处置 |
| :--- | :--- |
| 误提交了 `application-local.yml`（含密码） | 立即 `git rm --cached`，改密码，通知 PM 清理历史，重新提交 |
| 误 push 到 `main` | 立刻在群里报告，PM 回滚：`git revert <commit>` 后重新走 PR |
| 拉取后本地跑不起来 | 先 `git stash` → `git pull` → `git stash pop`；仍失败则删 `node_modules`/`target` 重装依赖 |
| 分支落后主干太多 | `git merge origin/main` 而非 `rebase`，避免历史错乱导致他人冲突 |
| 硬盘上的文件被同事覆盖 | 从 `origin/main` 或对方分支 checkout 单个文件：`git checkout origin/main -- 路径` |

---

## 五、周会检查项（PM 每周五执行）

1. `main` 是否可编译、可启动；
2. 每人本周 PR 是否都已合并，未合并原因是什么；
3. 是否有越过目录边界的提交；
4. 是否有明文密钥入库（`git log -p` 抽查）；
5. 下周分支是否已按命名规范创建。

## 改了什么

<!-- 一句话说明本 PR 的目标，例如：M1 完成签到页重复签到提示 -->

- 

## 涉及模块

- [ ] M1 学生端 `frontend/student/`
- [ ] M2 教师端 `frontend/teacher/`
- [ ] M3 后端 API `backend/.../{controller,service,mapper,entity}`
- [ ] M4 实时服务 `backend/.../websocket/`
- [ ] M5 大屏与测试 `frontend/screen/`、`docs/测试报告/`
- [ ] PM 文档 `README.md`、`docs/`

## 自测记录（必须写具体步骤）

| 步骤 | 操作 | 期望 | 实际 |
| :--- | :--- | :--- | :--- |
| 1 |  |  |  |
| 2 |  |  |  |

## 合并前检查清单（对不上就不要提 PR）

- [ ] **只改动了我负责的目录**（越界已征得负责人同意，并在描述中说明）
- [ ] 本地构建通过：后端 `mvn -q -DskipTests package`；前端 `npm run build`
- [ ] 接口有变更时，已同步更新 `docs/04-接口契约模板.md`
- [ ] 未提交 `.env`、`application-local.yml`、`*.log`、真实密码/Token
- [ ] 没有把 `node_modules/`、`target/`、`dist/` 提交进来
- [ ] 提交信息符合 `类型(模块): 描述` 规范（见 `docs/03`）

## 影响面与回滚

<!-- 是否影响他人模块、是否需要改数据库、如何回滚 -->

- 数据库变更：无 / 有（附 SQL）
- 回滚方式：`git revert <commit>` 即可 / 需手动处理（说明）

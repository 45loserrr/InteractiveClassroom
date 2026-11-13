# frontend · 三端前端说明

三端是**三个独立工程**（各自的 `package.json` / `node_modules` / 端口），互不引用源码，避免 6 人并行时互相踩文件。公共部分以"约定"的形式共享，而非抽 npm 包。

| 端 | 目录 | 端口 | 使用者 | 技术 | 负责人 | 当前状态 |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| 教师端 | `teacher/` | 5173 | 教师笔记本 | Vue3 + Vite + TS（可打 Electron exe） | M2 | ✅ 可运行骨架 |
| 学生端 | `student/` | 5174 | 学生手机 | Vue3 + Vite + TS（可打 Capacitor APK） | M1 | ⬜ 待 M1 初始化 |
| 大屏端 | `screen/` | 5175 | 教室投影 | Vue3 + Vite + TS（可打全屏 Electron） | M5 | ⬜ 待 M5 初始化 |

## 一、学生端 / 大屏端怎么从 0 开始（M1 / M5 照做）

```bash
# 以 student 为例（screen 同理，端口改 5175）
cd frontend/student
npm create vite@latest . -- --template vue-ts     # 在当前目录生成骨架
npm install
npm pkg set scripts.dev="vite --port 5174"
```

然后按 `teacher/` 的结构补齐：

1. 复制 `teacher/vite.config.ts` 并改端口与标题；
2. 复制 `teacher/src/env.d.ts`（环境变量类型声明）；
3. 复制 `teacher/src/api/http.ts` 与 `src/types/api.ts`（保证三端接口调用方式一致）；
4. 建立 `src/views/`，按 `docs/01` 的功能清单建页面占位。

> **不要**直接 `npm create vite` 覆盖 `teacher/`；三端各自独立提交，互不改动对方目录。

## 二、共同约定（三端都必须遵守）

| 项 | 约定 |
| :--- | :--- |
| 接口调用 | 统一 `api/http.ts`，默认相对路径 `/api`，不硬编码后端地址 |
| 类型定义 | 与 `docs/04-接口契约模板.md` 对齐，字段名用 camelCase |
| 报错展示 | 用后端 `message` 直接提示，不拼接技术细节 |
| 大屏脱敏 | 任何投影可见的姓名必须脱敏（张*明） |
| 移动端适配 | 学生端按 375px 宽设计，按钮触控区 ≥ 44px |
| 端口 | 教师 5173 / 学生 5174 / 大屏 5175，不得占用他人端口 |

## 三、局域网联调时的地址策略

```text
浏览器/手机 → http://<教师笔记本IP>:5173（或 5174/5175）
                    ↓ vite proxy
              http://localhost:8080/api
```

- 开发期：只暴露前端端口，后端由代理转发，**不需要**在手机上访问 8080；
- 交付期：由后端同源托管三端 `dist`（见 `docs/05` 第 2 节），前端代码不需要改。

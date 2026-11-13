# frontend/teacher · 教师端（M2 负责）

Vue 3 + Vite 5 + TypeScript。默认 5173 端口，开发期通过 `vite.config.ts` 把 `/api`、`/ws` 代理到后端 8080。

## 一、命令

```bash
npm install          # 首次
npm run dev          # http://localhost:5173
npm run build        # 类型检查 + 打包，产物在 dist/
npm run preview      # 本地预览 dist
```

`npm run build` 会先跑 `vue-tsc --noEmit`，**类型不通过就不出包**，这是提交前的硬门禁。

## 二、目录约定

```text
src/
├─ main.ts            # 入口：createApp + router
├─ App.vue            # 布局壳
├─ router/index.ts     # 路由表（/ → Login）
├─ views/             # 页面级组件：Login / Classroom / Checkin / Exercise / Race
├─ components/        # 可复用组件（M2 自行添加，文件名 PascalCase.vue）
├─ composables/       # 组合式函数，如 useWebSocket.ts / useCountdown.ts
├─ stores/            # 跨页状态（本期不引入 Pinia，先占位）
├─ api/http.ts        # 统一请求封装（拆包 / 带 Token / 超时）
├─ types/api.ts       # 与 docs/04 对齐的类型定义
├─ styles/base.css    # 全局样式与 CSS 变量
└─ utils/             # 纯函数工具
```

## 三、接口调用方式（统一走 `api/http.ts`）

```ts
import { http } from '@/api/http'
import type { ClassroomInfo } from '@/types/api'

// 成功时直接拿到 data；业务失败会抛 ApiError，页面里 catch 后 toast 即可
const room = await http.post<ClassroomInfo>('/api/classroom', { name: '软件工程导论' })
```

约定：

- **不要**在页面里直接 `fetch`，也不要硬编码 `http://localhost:8080`（换环境会挂）。
- 地址来自 `VITE_API_BASE`：留空 = 同源（推荐），真机联调时复制 `.env.example` 为 `.env.local` 再填 IP。
- 登录成功后 `setToken(token)`，退出时 `clearToken()`。

## 四、页面开发约定

| 约定 | 说明 |
| :--- | :--- |
| 视图懒加载 | `component: () => import('../views/Xxx.vue')` |
| 组件命名 | 文件 `PascalCase.vue`，`<script setup lang="ts">` |
| 样式隔离 | 页面样式用 `<style scoped>`，公共变量放 `styles/base.css` |
| 中文文案 | 面向教师，避免技术术语（"课堂码"而不是"classCode"） |
| 错误提示 | 用 `ApiError.code` 判断，`message` 直接展示给用户 |

## 五、提交前自检

```bash
npm run build      # 类型 + 打包必须通过
```

> 新增依赖前先在群里说一声：Electron / ECharts / qrcode 等属于"谁引入谁维护"，避免依赖冲突。

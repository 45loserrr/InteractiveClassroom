/// <reference types="vite/client" />

interface ImportMetaEnv {
  /** 接口基地址；留空表示同源（dev 由 vite proxy 转发，交付由后端同源托管） */
  readonly VITE_API_BASE?: string
  /** WebSocket 基地址，如 ws://192.168.1.10:8080；留空则由页面 location 推导 */
  readonly VITE_WS_BASE?: string
}

interface ImportMeta {
  readonly env: ImportMetaEnv
}

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<Record<string, never>, Record<string, never>, unknown>
  export default component
}

import type { ApiResult } from '../types/api'

/**
 * 极简 HTTP 客户端（不引入 axios，保持骨架轻量）。
 *
 * 设计要点：
 * 1. **地址同源优先**：默认 `VITE_API_BASE` 为空，请求走相对路径 `/api/...`，
 *    dev 环境由 vite proxy 转发，交付环境由后端同源托管，两边都不用改代码；
 * 2. **统一拆包**：只返回 `data`，业务失败抛 `ApiError`，页面写 `try/catch` 即可；
 * 3. **Token 自动附带**：登录后 `setToken`，后续请求自动带 `Authorization`；
 * 4. **超时保护**：默认 15s，避免课堂现场"转圈不动"。
 */

const BASE_URL: string = import.meta.env.VITE_API_BASE ?? ''
const TOKEN_KEY = 'classroom_token'

export function getToken(): string {
  return localStorage.getItem(TOKEN_KEY) ?? ''
}

export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token)
}

export function clearToken(): void {
  localStorage.removeItem(TOKEN_KEY)
}

/** 业务错误：`code` 对应 docs/04 §0.3 的错误码表 */
export class ApiError extends Error {
  readonly code: number

  constructor(code: number, message: string) {
    super(message)
    this.name = 'ApiError'
    this.code = code
  }
}

type HttpMethod = 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH'

interface RequestOptions {
  method?: HttpMethod
  body?: unknown
  headers?: Record<string, string>
  timeoutMs?: number
}

/**
 * 发起请求并解开统一响应体。
 *
 * @throws ApiError 业务失败（code != 0）、超时或网络异常（code = -1）
 */
export async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const { method = 'GET', body, headers = {}, timeoutMs = 15000 } = options
  const controller = new AbortController()
  const timer = window.setTimeout(() => controller.abort(), timeoutMs)

  try {
    const token = getToken()
    const response = await fetch(`${BASE_URL}${path}`, {
      method,
      headers: {
        'Content-Type': 'application/json',
        ...(token ? { Authorization: `Bearer ${token}` } : {}),
        ...headers,
      },
      body: body === undefined ? undefined : JSON.stringify(body),
      signal: controller.signal,
    })

    if (response.status === 401) {
      clearToken()
      throw new ApiError(401, '登录已失效，请重新登录')
    }

    const result = (await response.json()) as ApiResult<T>
    if (result.code !== 0) {
      throw new ApiError(result.code, result.message || '请求失败')
    }
    return result.data
  } catch (error) {
    if (error instanceof ApiError) {
      throw error
    }
    if (error instanceof DOMException && error.name === 'AbortError') {
      throw new ApiError(-1, '请求超时，请检查网络后重试')
    }
    throw new ApiError(-1, (error as Error).message || '网络异常，请稍后重试')
  } finally {
    window.clearTimeout(timer)
  }
}

export const http = {
  get: <T>(path: string, options?: Omit<RequestOptions, 'method' | 'body'>) =>
    request<T>(path, { ...options, method: 'GET' }),
  post: <T>(path: string, body?: unknown, options?: Omit<RequestOptions, 'method' | 'body'>) =>
    request<T>(path, { ...options, method: 'POST', body }),
  put: <T>(path: string, body?: unknown, options?: Omit<RequestOptions, 'method' | 'body'>) =>
    request<T>(path, { ...options, method: 'PUT', body }),
  del: <T>(path: string, options?: Omit<RequestOptions, 'method' | 'body'>) =>
    request<T>(path, { ...options, method: 'DELETE' }),
}

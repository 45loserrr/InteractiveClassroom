/**
 * 后端统一响应体（与 docs/04-接口契约模板.md §0.2 一致）
 */
export interface ApiResult<T = unknown> {
  /** 0 表示成功，非 0 为业务错误码 */
  code: number
  /** 可直接 toast 的中文提示 */
  message: string
  /** 业务数据，失败时为 null */
  data: T
}

/** MyBatis-Plus 分页结构 */
export interface PageResult<T> {
  records: T[]
  total: number
  current: number
  size: number
}

/** 登录 / 学生进课堂返回体 */
export interface LoginResult {
  token: string
  userId: number
  username: string
  realName: string
  role: 'teacher' | 'student'
  expireAt: string
}

/** 课堂基本信息 */
export interface ClassroomInfo {
  classroomId: number
  name: string
  className?: string
  classCode: string
  status: 'pending' | 'ongoing' | 'closed'
  createdAt?: string
}

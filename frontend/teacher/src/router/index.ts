import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

// 教师端路由表：/ 默认指向登录页。
// 新增页面时按「物理隔离」原则只改本文件与 src/views/。
const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'login',
    component: () => import('../views/Login.vue'),
    meta: { title: '教师登录' },
  },
  {
    path: '/classroom',
    name: 'classroom',
    component: () => import('../views/Classroom.vue'),
    meta: { title: '我的课堂' },
  },
  {
    path: '/checkin',
    name: 'checkin',
    component: () => import('../views/Checkin.vue'),
    meta: { title: '签到管理' },
  },
  {
    path: '/exercise',
    name: 'exercise',
    component: () => import('../views/Exercise.vue'),
    meta: { title: '随堂练习' },
  },
  {
    path: '/race',
    name: 'race',
    component: () => import('../views/Race.vue'),
    meta: { title: '抢答控制' },
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/',
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.afterEach((to) => {
  const title = (to.meta.title as string | undefined) ?? '教师端'
  document.title = `${title} · 移动互动课堂`
})

export default router

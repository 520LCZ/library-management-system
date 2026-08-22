import { createRouter, createWebHashHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

// 路由表: 登录页 + 主布局下 8 个业务模块
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', icon: 'User', roles: ['admin', 'librarian', 'reader'] }
  },
  {
    path: '/',
    component: () => import('@/layout/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '驾驶舱', icon: 'Odometer', roles: ['admin', 'librarian', 'reader'] }
      },
      {
        path: 'book',
        name: 'BookList',
        component: () => import('@/views/book/list.vue'),
        meta: { title: '图书管理', icon: 'Reading', roles: ['admin', 'librarian', 'reader'] }
      },
      {
        path: 'book/:id',
        name: 'BookDetail',
        component: () => import('@/views/book/detail.vue'),
        meta: { title: '图书详情', icon: 'Document', roles: ['admin', 'librarian', 'reader'], hidden: true }
      },
      {
        path: 'recommend',
        name: 'Recommend',
        component: () => import('@/views/recommend/index.vue'),
        meta: { title: '图书推荐', icon: 'Star', roles: ['admin', 'librarian', 'reader'] }
      },
      {
        path: 'comment',
        name: 'Comment',
        component: () => import('@/views/comment/list.vue'),
        meta: { title: '评论管理', icon: 'ChatDotRound', roles: ['admin', 'librarian'] }
      },
      {
        path: 'borrow',
        name: 'Borrow',
        component: () => import('@/views/borrow/index.vue'),
        meta: { title: '借阅管理', icon: 'Tickets', roles: ['admin', 'librarian', 'reader'] }
      },
      {
        path: 'reader',
        name: 'Reader',
        component: () => import('@/views/reader/index.vue'),
        meta: { title: '读者管理', icon: 'UserFilled', roles: ['admin', 'librarian'] }
      },
      {
        path: 'category',
        name: 'Category',
        component: () => import('@/views/category/index.vue'),
        meta: { title: '分类管理', icon: 'Files', roles: ['admin', 'librarian'] }
      },
      {
        path: 'stats',
        name: 'Stats',
        component: () => import('@/views/stats/index.vue'),
        meta: { title: '统计分析', icon: 'DataLine', roles: ['admin', 'librarian'] }
      },
      {
        path: 'settings/user',
        name: 'SettingsUser',
        component: () => import('@/views/settings/user.vue'),
        meta: { title: '系统用户', icon: 'Setting', roles: ['admin'] }
      }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 全局前置守卫: 无 token 跳登录; 有 token 但无 userInfo 拉取; 角色不符跳首页
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  const hasToken = !!userStore.token

  if (!hasToken) {
    if (to.path === '/login') {
      next()
      return
    }
    next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
    return
  }

  // 已登录访问登录页, 直接回首页
  if (to.path === '/login') {
    next('/')
    return
  }

  // 有 token 但无 userInfo, 先拉取用户信息
  if (!userStore.userInfo) {
    try {
      await userStore.fetchUserInfo()
    } catch (e) {
      // 拉取失败, 清 token 回登录
      userStore.logout()
      next(`/login?redirect=${encodeURIComponent(to.fullPath)}`)
      return
    }
  }

  // 角色校验
  const requiredRoles = to.meta && to.meta.roles
  if (requiredRoles && requiredRoles.length > 0) {
    const ok = requiredRoles.some((r) => userStore.hasRole(r))
    if (!ok) {
      // 角色不符, 回首页
      next('/')
      return
    }
  }
  next()
})

export default router

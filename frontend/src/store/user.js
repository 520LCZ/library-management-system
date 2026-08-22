import { defineStore } from 'pinia'
import request from '@/utils/request'

// 用户状态: token / userInfo / roles
export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('library_token') || '',
    userInfo: null,
    roles: []
  }),
  getters: {
    // 判断是否拥有某个角色
    hasRole: (state) => (role) => {
      if (!state.roles || state.roles.length === 0) return false
      if (state.roles.includes('admin')) return true // admin 拥有全部
      return state.roles.includes(role)
    }
  },
  actions: {
    // 登录: 调 /api/auth/login, 存 token 与 userInfo
    async login(payload) {
      const data = await request.post('/auth/login', payload)
      this.token = data.token
      this.userInfo = data.userInfo
      // 契约返回 userInfo.role 字段, 转为数组
      const role = data.userInfo && data.userInfo.role
      this.roles = role ? [role] : []
      localStorage.setItem('library_token', data.token)
      return data
    },
    // 拉取当前登录用户信息
    async fetchUserInfo() {
      const data = await request.get('/auth/info')
      this.userInfo = data
      const role = data && data.role
      this.roles = role ? [role] : []
      return data
    },
    // 退出登录: 清状态与 localStorage
    logout() {
      this.token = ''
      this.userInfo = null
      this.roles = []
      localStorage.removeItem('library_token')
    }
  }
})

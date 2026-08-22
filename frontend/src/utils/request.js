import axios from 'axios'
import { ElMessage } from 'element-plus'
import { handleMock } from './mock'

// 创建 axios 实例: baseURL '/api'
// - 开发模式(dev): 由 Vite 代理转发到后端 8080
// - 生产模式(prod/GitHub Pages): 替换 custom adapter 走内存 mock (公网无后端)
const service = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 生产环境: 自定义 adapter, 所有请求走 handleMock, 不发真 HTTP
if (import.meta.env.PROD) {
  service.defaults.adapter = (config) => {
    const method = (config.method || 'get').toUpperCase()
    // 去掉 baseURL 前缀 /api, 保留 query
    let path = (config.url || '')
    if (path.startsWith('/api')) path = path.slice(4)
    if (!path.startsWith('/')) path = '/' + path
    let queryString = ''
    if (config.params) {
      const sp = new URLSearchParams()
      Object.entries(config.params).forEach(([k, v]) => {
        if (v !== undefined && v !== null && v !== '') sp.append(k, String(v))
      })
      const s = sp.toString()
      if (s) queryString = (queryString ? '&' : '?') + s
    }
    const rawPath = path + queryString
    const token = (config.headers && config.headers['Authorization']) || ''
    const bearer = typeof token === 'string' && token.startsWith('Bearer ') ? token.slice(7) : ''
    const body = config.data ? (typeof config.data === 'string' ? JSON.parse(config.data) : config.data) : null
    const mock = handleMock(method, rawPath, body, bearer)
    const status = mock.status || 200
    return Promise.resolve({
      data: mock.json,
      status,
      statusText: 'OK',
      headers: { 'content-type': 'application/json' },
      config,
      request: {}
    })
  }
}

// 请求拦截器: 从 localStorage 取 token 加 Authorization 头
service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('library_token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器: 统一处理 code, 401 清 token 跳登录, 其它非 200 报错
service.interceptors.response.use(
  (response) => {
    const res = response.data
    // 非对象(如文件流)直接返回
    if (typeof res !== 'object' || res === null) {
      return res
    }
    if (res.code === 200) {
      return res.data
    }
    if (res.code === 401) {
      localStorage.removeItem('library_token')
      ElMessage.error('登录已失效, 请重新登录')
      // 跳登录 (避免循环依赖, 用 location)
      const redirect = encodeURIComponent(window.location.pathname + window.location.search)
      window.location.href = '#/login?redirect=' + redirect
      return Promise.reject(new Error('未登录'))
    }
    if (res.code === 403) {
      ElMessage.error('无权限访问')
      return Promise.reject(new Error('无权限'))
    }
    ElMessage.error(res.message || '请求失败')
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  (error) => {
    // HTTP 层错误
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('library_token')
      window.location.href = '#/login'
      return Promise.reject(error)
    }
    ElMessage.error(error.message || '网络异常')
    return Promise.reject(error)
  }
)

export default service

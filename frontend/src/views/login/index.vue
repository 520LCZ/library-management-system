<template>
  <div class="login-page">
    <div class="login-card g-card">
      <div class="login-header">
        <el-icon :size="40" color="#2F5E4E"><Reading /></el-icon>
        <h1>图书管理系统</h1>
        <p class="sub">Library Management System</p>
      </div>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @keyup.enter="onLogin">
        <el-form-item label="账号" prop="username">
          <el-input v-model="form.username" placeholder="请输入账号" :prefix-icon="User" clearable />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" :prefix-icon="Lock" clearable />
        </el-form-item>
        <el-button type="primary" class="login-btn" :loading="loading" @click="onLogin">登 录</el-button>
      </el-form>
      <div class="tips">
        <span>预置账号: <b>admin / admin123</b></span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const formRef = ref(null)
const loading = ref(false)
const form = reactive({ username: 'admin', password: 'admin123' })
const rules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const onLogin = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      await userStore.login({ username: form.username, password: form.password })
      ElMessage.success('登录成功')
      const redirect = route.query.redirect ? decodeURIComponent(route.query.redirect) : '/'
      router.push(redirect)
    } catch (e) {
      // request 已报错
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #244A3D 0%, #2F5E4E 50%, #3E7A66 100%);
}
.login-card {
  width: 380px;
  padding: 36px 32px 24px;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
}
.login-header {
  text-align: center;
  margin-bottom: 24px;
}
.login-header h1 {
  margin: 12px 0 4px;
  font-size: 22px;
  color: var(--brand-primary);
  letter-spacing: 2px;
}
.sub {
  margin: 0;
  color: var(--brand-text-secondary);
  font-size: 12px;
  letter-spacing: 1px;
}
.login-btn {
  width: 100%;
  margin-top: 8px;
  background: var(--brand-primary);
  border-color: var(--brand-primary);
}
.login-btn:hover, .login-btn:focus {
  background: var(--brand-primary-light);
  border-color: var(--brand-primary-light);
}
.tips {
  margin-top: 16px;
  text-align: center;
  font-size: 12px;
  color: var(--brand-text-secondary);
}
.tips b {
  color: var(--brand-primary);
}
</style>

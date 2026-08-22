<template>
  <el-container class="layout-root">
    <!-- 侧边栏: 深墨绿底白字 -->
    <el-aside :width="collapsed ? '64px' : '220px'" class="layout-aside">
      <div class="logo">
        <el-icon :size="22"><Reading /></el-icon>
        <span v-show="!collapsed" class="logo-text">图书管理系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="collapsed"
        :collapse-transition="false"
        router
        class="aside-menu"
        background-color="#244A3D"
        text-color="#E8F0EC"
        active-text-color="#FFFFFF"
      >
        <template v-for="route in menuRoutes" :key="route.path">
          <el-menu-item :index="'/' + route.path">
            <el-icon><component :is="route.meta.icon" /></el-icon>
            <template #title>{{ route.meta.title }}</template>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- 顶部 header: 白底, 标题/面包屑 + 用户头像 -->
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon class="collapse-btn" :size="20" @click="collapsed = !collapsed">
            <Fold v-if="!collapsed" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentTitle">{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="onCommand">
            <span class="user-trigger">
              <el-avatar :size="32" class="user-avatar">
                {{ avatarText }}
              </el-avatar>
              <span class="user-name">{{ userStore.userInfo?.nickname || userStore.userInfo?.username || '用户' }}</span>
              <el-icon><CaretBottom /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="password">
                  <el-icon><Lock /></el-icon>修改密码
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="layout-main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="pwdVisible" title="修改密码" width="420px">
      <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="90px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdVisible = false">取消</el-button>
        <el-button type="primary" @click="submitPwd">确认</el-button>
      </template>
    </el-dialog>
  </el-container>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import request from '@/utils/request'
import router from '@/router'

const route = useRoute()
const userStore = useUserStore()
const collapsed = ref(false)

// 按角色过滤菜单项 (router 配置中 children 含 meta.roles)
const menuRoutes = computed(() => {
  const layoutRoute = router.options.routes.find((r) => r.path === '/')
  const children = layoutRoute ? layoutRoute.children : []
  return children.filter((c) => {
    if (c.meta && c.meta.hidden) return false
    const roles = c.meta && c.meta.roles
    if (!roles || roles.length === 0) return true
    return roles.some((r) => userStore.hasRole(r))
  })
})

const activeMenu = computed(() => '/' + (route.path.split('/')[1] || 'dashboard'))
const currentTitle = computed(() => route.meta && route.meta.title)

const avatarText = computed(() => {
  const name = userStore.userInfo?.nickname || userStore.userInfo?.username || 'U'
  return name.charAt(0).toUpperCase()
})

// 下拉命令
const onCommand = (cmd) => {
  if (cmd === 'password') {
    pwdVisible.value = true
  } else if (cmd === 'logout') {
    ElMessageBox.confirm('确定退出登录?', '提示', { type: 'warning' })
      .then(() => {
        userStore.logout()
        router.push('/login')
      })
      .catch(() => {})
  }
}

// 修改密码
const pwdVisible = ref(false)
const pwdFormRef = ref(null)
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })
const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, message: '至少 6 位', trigger: 'blur' }],
  confirmPassword: [{ required: true, message: '请确认密码', trigger: 'blur' }, {
    validator: (rule, value, cb) => {
      if (value !== pwdForm.newPassword) cb(new Error('两次密码不一致'))
      else cb()
    }, trigger: 'blur'
  }]
}
const submitPwd = () => {
  pwdFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      await request.put('/auth/password', {
        oldPassword: pwdForm.oldPassword,
        newPassword: pwdForm.newPassword
      })
      ElMessage.success('修改成功, 请重新登录')
      pwdVisible.value = false
      userStore.logout()
      router.push('/login')
    } catch (e) {
      // request 已报错
    }
  })
}
</script>

<style scoped>
.layout-root {
  height: 100vh;
}

/* 侧边栏深墨绿 */
.layout-aside {
  background: #244A3D;
  transition: width 0.2s;
  overflow: hidden;
}
.logo {
  height: 56px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 18px;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  white-space: nowrap;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}
.logo-text {
  letter-spacing: 1px;
}
.aside-menu {
  border-right: none;
  height: calc(100vh - 56px);
}
/* 菜单项 hover 浅墨绿 */
.aside-menu :deep(.el-menu-item) {
  margin: 2px 8px;
  border-radius: 6px;
}
.aside-menu :deep(.el-menu-item:hover) {
  background: #2F5E4E !important;
  color: #fff !important;
}
.aside-menu :deep(.el-menu-item.is-active) {
  background: #2F5E4E !important;
  color: #fff !important;
}

/* 顶部 header */
.layout-header {
  background: #fff;
  border-bottom: 1px solid var(--brand-border);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  height: 56px;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.collapse-btn {
  cursor: pointer;
  color: var(--brand-text-primary);
}
.header-right {
  display: flex;
  align-items: center;
}
.user-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  outline: none;
}
.user-avatar {
  background: var(--brand-primary);
  color: #fff;
}
.user-name {
  font-size: 14px;
  color: var(--brand-text-primary);
}

.layout-main {
  padding: 0;
  background: var(--brand-bg);
  overflow-y: auto;
}

/* 路由切换动画 */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.15s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>

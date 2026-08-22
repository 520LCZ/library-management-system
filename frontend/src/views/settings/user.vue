<template>
  <div class="g-page">
    <div class="g-card" style="padding:16px">
      <div class="g-toolbar">
        <el-input v-model="query.keyword" placeholder="用户名/昵称" clearable style="width:220px" @keyup.enter="onSearch" />
        <el-button type="primary" @click="onSearch"><el-icon><Search /></el-icon>查询</el-button>
        <el-button @click="onReset"><el-icon><Refresh /></el-icon>重置</el-button>
        <el-button type="success" @click="openDialog(null)"><el-icon><Plus /></el-icon>新增用户</el-button>
      </div>

      <el-table :data="list" v-loading="loading" border stripe style="width:100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="用户名" width="140" />
        <el-table-column prop="nickname" label="昵称" width="140" />
        <el-table-column label="角色" width="140">
          <template #default="{ row }">
            <el-tag :type="roleTag(row.role)" size="small">{{ roleLabel(row.role) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
        <el-table-column prop="phone" label="手机" width="130" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button link :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button link type="danger" @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        class="pager"
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @size-change="loadList"
        @current-change="loadList"
      />
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="form.username" :disabled="!!form.id" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="昵称" prop="nickname">
              <el-input v-model="form.nickname" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item v-if="!form.id" label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="角色" prop="role">
              <el-select v-model="form.role" style="width:100%">
                <el-option v-for="r in roleList" :key="r.value" :label="r.label" :value="r.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio :label="1">启用</el-radio>
                <el-radio :label="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="邮箱">
              <el-input v-model="form.email" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机">
              <el-input v-model="form.phone" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="onSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'

const list = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, size: 10, keyword: '' })

const roleList = ref([])
const loadRoleList = async () => {
  try {
    roleList.value = await request.get('/role/list')
  } catch (e) {
    roleList.value = [
      { value: 'admin', label: '管理员' },
      { value: 'librarian', label: '图书管理员' },
      { value: 'reader', label: '读者' }
    ]
  }
}
const roleLabel = (v) => {
  const r = roleList.value.find((i) => i.value === v)
  return r ? r.label : v
}
const roleTag = (v) => ({ admin: 'danger', librarian: 'warning', reader: 'success' }[v] || 'info')

const loadList = async () => {
  loading.value = true
  try {
    const data = await request.get('/user/page', { params: { page: query.page, size: query.size, keyword: query.keyword } })
    list.value = data.records || []
    total.value = data.total || 0
  } catch (e) {
    list.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}
const onSearch = () => { query.page = 1; loadList() }
const onReset = () => { query.keyword = ''; query.page = 1; loadList() }

// 新增/编辑
const dialogVisible = ref(false)
const dialogTitle = ref('新增用户')
const submitting = ref(false)
const formRef = ref(null)
const defaultForm = () => ({ id: null, username: '', password: '', nickname: '', role: 'reader', email: '', phone: '', status: 1 })
const form = reactive(defaultForm())
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const openDialog = (row) => {
  if (row) {
    dialogTitle.value = '编辑用户'
    Object.assign(form, defaultForm(), row, { password: '' })
  } else {
    dialogTitle.value = '新增用户'
    Object.assign(form, defaultForm())
  }
  dialogVisible.value = true
}
const resetForm = () => { Object.assign(form, defaultForm()); formRef.value && formRef.value.clearValidate() }

const onSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      const payload = { ...form }
      if (form.id) {
        // 编辑时若密码为空则不传
        if (!payload.password) delete payload.password
        await request.put('/user', payload)
        ElMessage.success('修改成功')
      } else {
        await request.post('/user', payload)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadList()
    } catch (e) {
    } finally {
      submitting.value = false
    }
  })
}

const toggleStatus = async (row) => {
  try {
    await request.put(`/user/${row.id}/status?status=${row.status === 1 ? 0 : 1}`)
    ElMessage.success('操作成功')
    loadList()
  } catch (e) {}
}

const onDelete = (row) => {
  ElMessageBox.confirm(`确定删除用户 ${row.username}?`, '提示', { type: 'warning' })
    .then(async () => {
      try {
        await request.delete('/user/' + row.id)
        ElMessage.success('删除成功')
        loadList()
      } catch (e) {}
    })
    .catch(() => {})
}

onMounted(() => {
  loadRoleList()
  loadList()
})
</script>

<style scoped>
.pager { margin-top: 16px; justify-content: flex-end; }
</style>

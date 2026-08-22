<template>
  <div class="g-page">
    <div class="g-card" style="padding:16px">
      <div class="g-toolbar">
        <el-input v-model="query.keyword" placeholder="姓名/手机/邮箱" clearable style="width:220px" @keyup.enter="onSearch" />
        <el-button type="primary" @click="onSearch"><el-icon><Search /></el-icon>查询</el-button>
        <el-button @click="onReset"><el-icon><Refresh /></el-icon>重置</el-button>
        <el-button v-if="canEdit" type="success" @click="openDialog(null)"><el-icon><Plus /></el-icon>新增读者</el-button>
      </div>

      <el-table :data="list" v-loading="loading" border stripe style="width:100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column label="性别" width="80">
          <template #default="{ row }">{{ row.gender === 0 ? '男' : '女' }}</template>
        </el-table-column>
        <el-table-column prop="phone" label="手机" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="160" show-overflow-tooltip />
        <el-table-column prop="registerDate" label="注册日期" width="120" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '正常' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetail(row)">详情</el-button>
            <el-button v-if="canEdit" link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button v-if="canDelete" link type="danger" @click="onDelete(row)">删除</el-button>
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

    <!-- 新增/编辑 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="560px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="姓名" prop="name">
              <el-input v-model="form.name" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio :label="0">男</el-radio>
                <el-radio :label="1">女</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="手机" prop="phone">
              <el-input v-model="form.phone" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="身份证号">
          <el-input v-model="form.idCard" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" type="textarea" :rows="2" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="onSubmit">保存</el-button>
      </template>
    </el-dialog>

    <!-- 详情抽屉 -->
    <el-drawer v-model="detailVisible" title="读者详情" size="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="姓名">{{ detail.name }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ detail.gender === 0 ? '男' : '女' }}</el-descriptions-item>
        <el-descriptions-item label="手机">{{ detail.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ detail.email }}</el-descriptions-item>
        <el-descriptions-item label="身份证">{{ detail.idCard }}</el-descriptions-item>
        <el-descriptions-item label="注册日期">{{ detail.registerDate }}</el-descriptions-item>
        <el-descriptions-item label="地址" :span="2">{{ detail.address }}</el-descriptions-item>
      </el-descriptions>
      <div class="chart-title" style="margin-top:16px">借阅历史</div>
      <el-table :data="detail.borrowHistory || []" border size="small">
        <el-table-column prop="bookTitle" label="书名" min-width="140" show-overflow-tooltip />
        <el-table-column prop="borrowDate" label="借出日" width="110" />
        <el-table-column prop="dueDate" label="应还日" width="110" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-drawer>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const canEdit = computed(() => userStore.hasRole('admin') || userStore.hasRole('librarian'))
const canDelete = computed(() => userStore.hasRole('admin'))

const list = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, size: 10, keyword: '' })

const loadList = async () => {
  loading.value = true
  try {
    const data = await request.get('/reader/page', { params: { page: query.page, size: query.size, keyword: query.keyword } })
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

const statusText = (s) => ({ 1: '借出中', 2: '已归还', 3: '已逾期' }[s] || '未知')
const statusType = (s) => ({ 1: 'warning', 2: 'success', 3: 'danger' }[s] || 'info')

// 详情抽屉
const detailVisible = ref(false)
const detail = reactive({})
const openDetail = async (row) => {
  try {
    const data = await request.get('/reader/' + row.id)
    Object.assign(detail, data || {})
    detailVisible.value = true
  } catch (e) {}
}

// 新增/编辑
const dialogVisible = ref(false)
const dialogTitle = ref('新增读者')
const submitting = ref(false)
const formRef = ref(null)
const defaultForm = () => ({ id: null, name: '', gender: 0, phone: '', email: '', idCard: '', address: '', registerDate: '', status: 1 })
const form = reactive(defaultForm())
const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机', trigger: 'blur' }]
}

const openDialog = (row) => {
  if (row) {
    dialogTitle.value = '编辑读者'
    Object.assign(form, defaultForm(), row)
  } else {
    dialogTitle.value = '新增读者'
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
      if (form.id) {
        await request.put('/reader', form)
        ElMessage.success('修改成功')
      } else {
        await request.post('/reader', form)
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

const onDelete = (row) => {
  ElMessageBox.confirm(`确定删除读者 ${row.name}?`, '提示', { type: 'warning' })
    .then(async () => {
      try {
        await request.delete('/reader/' + row.id)
        ElMessage.success('删除成功')
        loadList()
      } catch (e) {}
    })
    .catch(() => {})
}

onMounted(() => loadList())
</script>

<style scoped>
.pager { margin-top: 16px; justify-content: flex-end; }
.chart-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--brand-text-primary);
  margin-bottom: 12px;
  padding-left: 8px;
  border-left: 3px solid var(--brand-primary);
}
</style>

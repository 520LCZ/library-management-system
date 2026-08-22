<template>
  <div class="g-page">
    <div class="g-card" style="padding:16px">
      <!-- 搜索栏 -->
      <div class="g-toolbar">
        <el-select v-model="query.status" placeholder="状态" clearable style="width:160px" @change="onSearch">
          <el-option label="借出中" :value="1" />
          <el-option label="已归还" :value="2" />
          <el-option label="已逾期" :value="3" />
        </el-select>
        <el-button type="primary" @click="onSearch"><el-icon><Search /></el-icon>查询</el-button>
        <el-button @click="onReset"><el-icon><Refresh /></el-icon>重置</el-button>
        <el-button v-if="canEdit" type="success" @click="openDialog"><el-icon><Plus /></el-icon>新建借阅</el-button>
      </div>

      <el-table :data="list" v-loading="loading" border stripe style="width:100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="bookTitle" label="书名" min-width="160" show-overflow-tooltip />
        <el-table-column prop="readerName" label="读者" width="120" show-overflow-tooltip />
        <el-table-column prop="borrowDate" label="借出日" width="120" />
        <el-table-column prop="dueDate" label="应还日" width="120" />
        <el-table-column label="归还日" width="120">
          <template #default="{ row }">{{ row.returnDate || '-' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="canEdit" label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 1 || row.status === 3" link type="primary" @click="onReturn(row)">归还</el-button>
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

    <!-- 新建借阅 -->
    <el-dialog v-model="dialogVisible" title="新建借阅" width="480px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="图书" prop="bookId">
          <el-select v-model="form.bookId" filterable placeholder="请选择图书" style="width:100%" @change="onBookChange">
            <el-option v-for="b in bookOptions" :key="b.id" :label="`${b.title} (库存:${b.stock})`" :value="b.id" :disabled="b.stock <= 0" />
          </el-select>
        </el-form-item>
        <el-form-item label="读者" prop="readerId">
          <el-select v-model="form.readerId" filterable placeholder="请选择读者" style="width:100%">
            <el-option v-for="r in readerOptions" :key="r.id" :label="r.name + (r.phone ? '(' + r.phone + ')' : '')" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="借阅天数" prop="days">
          <el-input-number v-model="form.days" :min="1" :max="90" style="width:100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="onSubmit">确认借出</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const canEdit = computed(() => userStore.hasRole('admin') || userStore.hasRole('librarian') || userStore.hasRole('reader'))

const list = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, size: 10, status: '' })

const loadList = async () => {
  loading.value = true
  try {
    const data = await request.get('/borrow/page', { params: { page: query.page, size: query.size, status: query.status } })
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
const onReset = () => { query.status = ''; query.page = 1; loadList() }

const statusText = (s) => ({ 1: '借出中', 2: '已归还', 3: '已逾期' }[s] || '未知')
const statusType = (s) => ({ 1: 'warning', 2: 'success', 3: 'danger' }[s] || 'info')

// 归还
const onReturn = (row) => {
  ElMessageBox.confirm(`确认归还《${row.bookTitle}》?`, '提示', { type: 'warning' })
    .then(async () => {
      try {
        await request.put(`/borrow/${row.id}/return`)
        ElMessage.success('归还成功')
        loadList()
      } catch (e) {}
    })
    .catch(() => {})
}

// 新建借阅
const dialogVisible = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const form = reactive({ bookId: '', readerId: '', days: 30 })
const rules = {
  bookId: [{ required: true, message: '请选择图书', trigger: 'change' }],
  readerId: [{ required: true, message: '请选择读者', trigger: 'change' }],
  days: [{ required: true, message: '请输入天数', trigger: 'blur' }]
}

const bookOptions = ref([])
const readerOptions = ref([])

const loadOptions = async () => {
  try {
    const [books, readers] = await Promise.all([
      request.get('/book/page', { params: { page: 1, size: 200, status: 1 } }),
      request.get('/reader/page', { params: { page: 1, size: 200 } })
    ])
    bookOptions.value = (books && books.records) || []
    readerOptions.value = (readers && readers.records) || []
  } catch (e) {
    bookOptions.value = []
    readerOptions.value = []
  }
}

const openDialog = async () => {
  await loadOptions()
  Object.assign(form, { bookId: '', readerId: '', days: 30 })
  dialogVisible.value = true
}

const onBookChange = () => {}

const onSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      await request.post('/borrow', { bookId: form.bookId, readerId: form.readerId, days: form.days })
      ElMessage.success('借出成功')
      dialogVisible.value = false
      loadList()
    } catch (e) {
    } finally {
      submitting.value = false
    }
  })
}

onMounted(() => {
  loadList()
})
</script>

<style scoped>
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>

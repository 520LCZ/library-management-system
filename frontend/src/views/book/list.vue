<template>
  <div class="g-page">
    <div class="g-card" style="padding:16px">
      <!-- 搜索栏 -->
      <div class="g-toolbar">
        <el-input v-model="query.keyword" placeholder="书名/作者/ISBN" clearable style="width:220px" @keyup.enter="onSearch" />
        <el-select v-model="query.categoryId" placeholder="分类" clearable filterable style="width:180px" @change="onSearch">
          <el-option v-for="c in categoryFlat" :key="c.id" :label="c.name" :value="c.id" />
        </el-select>
        <el-button type="primary" @click="onSearch"><el-icon><Search /></el-icon>查询</el-button>
        <el-button @click="onReset"><el-icon><Refresh /></el-icon>重置</el-button>
        <el-button v-if="canEdit" type="success" @click="openDialog(null)"><el-icon><Plus /></el-icon>新增图书</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="list" v-loading="loading" border stripe style="width:100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column label="封面" width="80">
          <template #default="{ row }">
            <el-image v-if="row.cover" :src="row.cover" style="width:40px;height:54px" fit="cover" :preview-src-list="[row.cover]" />
            <el-icon v-else><Picture /></el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="书名" min-width="140" show-overflow-tooltip />
        <el-table-column prop="author" label="作者" width="120" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="100" />
        <el-table-column label="库存" width="100">
          <template #default="{ row }">
            <span>{{ row.stock }} / {{ row.total }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="goDetail(row.id)">详情</el-button>
            <el-button v-if="canEdit" link type="primary" @click="openDialog(row)">编辑</el-button>
            <el-button v-if="canEdit" link :type="row.status === 1 ? 'warning' : 'success'" @click="toggleStatus(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-button v-if="canDelete" link type="danger" @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
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

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="680px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="书名" prop="title">
              <el-input v-model="form.title" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="作者" prop="author">
              <el-input v-model="form.author" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="ISBN" prop="isbn">
              <el-input v-model="form.isbn" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分类" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="请选择" filterable style="width:100%">
                <el-option v-for="c in categoryFlat" :key="c.id" :label="c.name" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="出版社">
              <el-input v-model="form.publisher" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出版日期">
              <el-date-picker v-model="form.publishDate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="价格">
              <el-input-number v-model="form.price" :precision="2" :min="0" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="库存" prop="stock">
              <el-input-number v-model="form.stock" :min="0" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="总数" prop="total">
              <el-input-number v-model="form.total" :min="0" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="封面URL">
          <el-input v-model="form.cover" placeholder="http://..." />
        </el-form-item>
        <el-form-item label="位置">
          <el-input v-model="form.location" />
        </el-form-item>
        <el-form-item label="简介">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">上架</el-radio>
            <el-radio :label="0">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="onSubmit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const canEdit = computed(() => userStore.hasRole('admin') || userStore.hasRole('librarian'))
const canDelete = computed(() => userStore.hasRole('admin'))

// 列表与查询
const list = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, size: 10, keyword: '', categoryId: '' })

// 分类扁平 (用于下拉)
const categoryFlat = ref([])

const loadCategoryFlat = async () => {
  try {
    const data = await request.get('/category/list')
    categoryFlat.value = data || []
  } catch (e) {
    categoryFlat.value = []
  }
}

const loadList = async () => {
  loading.value = true
  try {
    const data = await request.get('/book/page', { params: { page: query.page, size: query.size, keyword: query.keyword, categoryId: query.categoryId } })
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
const onReset = () => { query.keyword = ''; query.categoryId = ''; query.page = 1; loadList() }

const goDetail = (id) => router.push('/book/' + id)

// 新增/编辑表单
const dialogVisible = ref(false)
const dialogTitle = ref('新增图书')
const submitting = ref(false)
const formRef = ref(null)
const defaultForm = () => ({
  id: null, title: '', author: '', isbn: '', categoryId: '', publisher: '', publishDate: '',
  price: 0, stock: 1, total: 1, cover: '', location: '', description: '', status: 1
})
const form = reactive(defaultForm())
const rules = {
  title: [{ required: true, message: '请输入书名', trigger: 'blur' }],
  author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  stock: [{ required: true, message: '请输入库存', trigger: 'blur' }],
  total: [{ required: true, message: '请输入总数', trigger: 'blur' }]
}

const openDialog = (row) => {
  if (row) {
    dialogTitle.value = '编辑图书'
    Object.assign(form, defaultForm(), row)
  } else {
    dialogTitle.value = '新增图书'
    Object.assign(form, defaultForm())
  }
  dialogVisible.value = true
}

const resetForm = () => {
  Object.assign(form, defaultForm())
  formRef.value && formRef.value.clearValidate()
}

const onSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (form.id) {
        await request.put('/book', form)
        ElMessage.success('修改成功')
      } else {
        await request.post('/book', form)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadList()
    } catch (e) {
      // request 已报错
    } finally {
      submitting.value = false
    }
  })
}

// 上下架
const toggleStatus = async (row) => {
  try {
    await request.put(`/book/${row.id}/status?status=${row.status === 1 ? 0 : 1}`)
    ElMessage.success('操作成功')
    loadList()
  } catch (e) {}
}

// 删除
const onDelete = (row) => {
  ElMessageBox.confirm(`确定删除《${row.title}》?`, '提示', { type: 'warning' })
    .then(async () => {
      try {
        await request.delete('/book/' + row.id)
        ElMessage.success('删除成功')
        loadList()
      } catch (e) {}
    })
    .catch(() => {})
}

onMounted(() => {
  loadCategoryFlat()
  loadList()
})
</script>

<style scoped>
.pager {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>

<template>
  <div class="g-page">
    <div class="g-card" style="padding:16px">
      <div class="g-toolbar">
        <span class="title">分类树</span>
        <el-button v-if="canEdit" type="success" @click="openDialog(null, 0)"><el-icon><Plus /></el-icon>新增顶级分类</el-button>
        <el-button type="primary" @click="loadTree"><el-icon><Refresh /></el-icon>刷新</el-button>
      </div>

      <el-table :data="tree" v-loading="loading" row-key="id" border default-expand-all>
        <el-table-column prop="name" label="分类名称" min-width="220" />
        <el-table-column prop="sort" label="排序" width="100" />
        <el-table-column label="操作" width="240">
          <template #default="{ row }">
            <el-button v-if="canEdit" link type="primary" @click="openDialog(null, row.id)">新增子分类</el-button>
            <el-button v-if="canEdit" link type="primary" @click="openDialog(row, null)">编辑</el-button>
            <el-button v-if="canDelete" link type="danger" @click="onDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="420px" @close="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="父分类">
          <el-tree-select
            v-model="form.parentId"
            :data="parentOptions"
            :props="{ label: 'name', value: 'id', children: 'children' }"
            check-strictly
            clearable
            placeholder="不选为顶级"
            style="width:100%"
          />
        </el-form-item>
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" style="width:100%" />
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
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/utils/request'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const canEdit = computed(() => userStore.hasRole('admin') || userStore.hasRole('librarian'))
const canDelete = computed(() => userStore.hasRole('admin'))

const tree = ref([])
const loading = ref(false)

const loadTree = async () => {
  loading.value = true
  try {
    const data = await request.get('/category/tree')
    tree.value = data || []
  } catch (e) {
    tree.value = []
  } finally {
    loading.value = false
  }
}

const parentOptions = computed(() => {
  // 顶级 + 一个虚拟根用于新增子分类
  return [{ id: 0, name: '顶级分类', children: tree.value }]
})

// 新增/编辑
const dialogVisible = ref(false)
const dialogTitle = ref('新增分类')
const submitting = ref(false)
const formRef = ref(null)
const defaultForm = () => ({ id: null, name: '', parentId: 0, sort: 0 })
const form = reactive(defaultForm())
const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const openDialog = (row, parentId) => {
  if (row) {
    dialogTitle.value = '编辑分类'
    Object.assign(form, defaultForm(), row)
  } else {
    dialogTitle.value = '新增分类'
    Object.assign(form, defaultForm())
    if (parentId !== null && parentId !== undefined) {
      form.parentId = parentId
    }
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
      if (!payload.parentId) payload.parentId = 0
      if (form.id) {
        await request.put('/category', payload)
        ElMessage.success('修改成功')
      } else {
        await request.post('/category', payload)
        ElMessage.success('新增成功')
      }
      dialogVisible.value = false
      loadTree()
    } catch (e) {
    } finally {
      submitting.value = false
    }
  })
}

const onDelete = (row) => {
  ElMessageBox.confirm(`确定删除分类「${row.name}」?子分类会一并影响。`, '提示', { type: 'warning' })
    .then(async () => {
      try {
        await request.delete('/category/' + row.id)
        ElMessage.success('删除成功')
        loadTree()
      } catch (e) {}
    })
    .catch(() => {})
}

onMounted(() => loadTree())
</script>

<style scoped>
.title {
  font-size: 16px;
  font-weight: 600;
  color: var(--brand-primary);
  flex: 1;
}
</style>

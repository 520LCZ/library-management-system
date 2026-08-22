<template>
  <div class="g-page">
    <div class="g-card" style="padding:16px">
      <!-- 搜索栏 -->
      <div class="g-toolbar">
        <el-input v-model="query.keyword" placeholder="书名/评论内容" clearable style="width:240px" @keyup.enter="onSearch" />
        <el-select v-model="query.status" placeholder="审核状态" clearable style="width:160px" @change="onSearch">
          <el-option label="待审核" :value="0" />
          <el-option label="已通过" :value="1" />
          <el-option label="已驳回" :value="2" />
        </el-select>
        <el-button type="primary" @click="onSearch"><el-icon><Search /></el-icon>查询</el-button>
        <el-button @click="onReset"><el-icon><Refresh /></el-icon>重置</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="list" v-loading="loading" border stripe style="width:100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="bookTitle" label="书名" min-width="160" show-overflow-tooltip />
        <el-table-column prop="username" label="评论人" width="120" />
        <el-table-column label="评分" width="150">
          <template #default="{ row }">
            <el-rate v-model="row.rating" disabled :max="5" />
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评论内容" min-width="280" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="评论时间" width="170">
          <template #default="{ row }">{{ row.createTime }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button v-if="canAudit && row.status === 0" link type="success" @click="onAudit(row, 1)">通过</el-button>
            <el-button v-if="canAudit && row.status === 0" link type="warning" @click="onAudit(row, 2)">驳回</el-button>
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
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const canAudit = computed(() => userStore.hasRole('admin') || userStore.hasRole('librarian'))
const canDelete = computed(() => userStore.hasRole('admin'))

// 列表与查询
const list = ref([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, size: 10, keyword: '', status: '' })

const statusText = (s) => ({ 0: '待审核', 1: '已通过', 2: '已驳回' }[s] || '未知')
const statusTag = (s) => ({ 0: 'warning', 1: 'success', 2: 'info' }[s] || 'info')

const loadList = async () => {
  loading.value = true
  try {
    const data = await request.get('/comment/page', { params: { page: query.page, size: query.size, keyword: query.keyword, status: query.status === '' ? undefined : query.status } })
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
const onReset = () => { query.keyword = ''; query.status = ''; query.page = 1; loadList() }

// 审核
const onAudit = (row, status) => {
  const tip = status === 1 ? '通过' : '驳回'
  ElMessageBox.confirm(`确定${tip}这条评论?`, '提示', { type: 'warning' })
    .then(async () => {
      try {
        await request.put(`/comment/${row.id}/status?status=${status}`)
        ElMessage.success(`${tip}成功`)
        loadList()
      } catch (e) {}
    })
    .catch(() => {})
}

// 删除
const onDelete = (row) => {
  ElMessageBox.confirm(`确定删除这条评论?`, '提示', { type: 'warning' })
    .then(async () => {
      try {
        await request.delete('/comment/' + row.id)
        ElMessage.success('删除成功')
        loadList()
      } catch (e) {}
    })
    .catch(() => {})
}

onMounted(() => {
  loadList()
})
</script>

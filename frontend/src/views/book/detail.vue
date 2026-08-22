<template>
  <div class="g-page" v-loading="loading">
    <div class="g-card detail-card" style="padding:24px">
      <div class="detail-head">
        <el-button @click="goBack" :icon="ArrowLeft">返回</el-button>
        <span class="title">{{ book.title }}</span>
      </div>
      <el-divider />
      <el-row :gutter="32">
        <el-col :span="6">
          <div class="cover-wrap">
            <el-image v-if="book.cover" :src="book.cover" class="cover" fit="cover" :preview-src-list="[book.cover]" />
            <el-icon v-else class="cover-placeholder"><Picture /></el-icon>
          </div>
        </el-col>
        <el-col :span="18">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="作者">{{ book.author }}</el-descriptions-item>
            <el-descriptions-item label="ISBN">{{ book.isbn }}</el-descriptions-item>
            <el-descriptions-item label="分类">{{ book.categoryName }}</el-descriptions-item>
            <el-descriptions-item label="出版社">{{ book.publisher }}</el-descriptions-item>
            <el-descriptions-item label="出版日期">{{ book.publishDate }}</el-descriptions-item>
            <el-descriptions-item label="价格">¥{{ book.price }}</el-descriptions-item>
            <el-descriptions-item label="总数">{{ book.total }}</el-descriptions-item>
            <el-descriptions-item label="在馆库存">{{ book.stock }}</el-descriptions-item>
            <el-descriptions-item label="位置">{{ book.location }}</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="book.status === 1 ? 'success' : 'info'" size="small">
                {{ book.status === 1 ? '上架' : '下架' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="简介" :span="2">{{ book.description }}</el-descriptions-item>
          </el-descriptions>
        </el-col>
      </el-row>
    </div>

    <div class="g-card" style="padding:16px; margin-top:16px">
      <div class="chart-title">该书借阅记录</div>
      <el-table :data="borrowList" border stripe style="width:100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="readerName" label="读者" min-width="120" show-overflow-tooltip />
        <el-table-column prop="borrowDate" label="借出日" width="130" />
        <el-table-column prop="dueDate" label="应还日" width="130" />
        <el-table-column prop="returnDate" label="归还日" width="130">
          <template #default="{ row }">{{ row.returnDate || '-' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!loading && borrowList.length === 0" description="暂无借阅记录" />
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import request from '@/utils/request'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const book = reactive({})
const borrowList = ref([])

const loadBook = async () => {
  loading.value = true
  try {
    const data = await request.get('/book/' + route.params.id)
    Object.assign(book, data || {})
    // 详情接口可能返回该书借阅记录 (按契约 /api/book/{id} 含 categoryName, 借阅记录字段名兼容 borrowHistory)
    if (data && Array.isArray(data.borrowHistory)) {
      borrowList.value = data.borrowHistory
    } else {
      // 兜底: 走借阅分页按 bookId 过滤
      try {
        const r = await request.get('/borrow/page', { params: { page: 1, size: 50, bookId: route.params.id } })
        borrowList.value = (r && r.records) || []
      } catch (e) {
        borrowList.value = []
      }
    }
  } catch (e) {
    // 错误已报
  } finally {
    loading.value = false
  }
}

const goBack = () => router.push('/book')

const statusText = (s) => ({ 1: '借出中', 2: '已归还', 3: '已逾期' }[s] || '未知')
const statusType = (s) => ({ 1: 'warning', 2: 'success', 3: 'danger' }[s] || 'info')

onMounted(loadBook)
</script>

<style scoped>
.detail-head {
  display: flex;
  align-items: center;
  gap: 12px;
}
.detail-head .title {
  font-size: 20px;
  font-weight: 600;
  color: var(--brand-primary);
}
.cover-wrap {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 220px;
  background: #F5F7F6;
  border-radius: 8px;
}
.cover {
  width: 160px;
  height: 220px;
  border-radius: 6px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
.cover-placeholder {
  font-size: 48px;
  color: #c0c4cc;
}
.chart-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--brand-text-primary);
  margin-bottom: 12px;
  padding-left: 8px;
  border-left: 3px solid var(--brand-primary);
}
</style>

<template>
  <div class="g-page">
    <!-- KPI -->
    <div class="g-card" style="padding:16px;margin-bottom:16px">
      <el-row :gutter="16">
        <el-col :span="6">
          <el-statistic title="热门推荐" :value="hot.length" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="高分推荐" :value="rating.length" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="合计推荐" :value="hot.length + rating.length" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="图书总数" :value="totalBooks" />
        </el-col>
      </el-row>
    </div>

    <!-- 热门借阅 TOP -->
    <div class="g-card" style="padding:16px;margin-bottom:16px">
      <div class="block-title"><el-icon><TrendCharts /></el-icon> 热门借阅 TOP {{ hot.length }}</div>
      <el-row :gutter="16" v-loading="loading">
        <el-col v-for="(item, idx) in hot" :key="'h' + item.id" :xs="24" :sm="12" :md="8" :lg="6" style="margin-bottom:16px">
          <el-card shadow="hover" class="rec-card" @click="goDetail(item.id)">
            <div class="rec-rank">#{{ idx + 1 }}</div>
            <div class="rec-cover" :style="{ backgroundImage: item.cover ? `url(${item.cover})` : '' }"></div>
            <div class="rec-info">
              <div class="rec-title">{{ item.title }}</div>
              <div class="rec-meta">{{ item.author }} · {{ item.categoryName || '未分类' }}</div>
              <div class="rec-score">
                <el-icon><Reading /></el-icon> 借阅 {{ Math.round(item.score || 0) }} 次
              </div>
            </div>
          </el-card>
        </el-col>
        <el-empty v-if="!loading && hot.length === 0" description="暂无热门数据" />
      </el-row>
    </div>

    <!-- 高分评论 TOP -->
    <div class="g-card" style="padding:16px">
      <div class="block-title"><el-icon><Star /></el-icon> 高分评论 TOP {{ rating.length }}</div>
      <el-row :gutter="16" v-loading="loading">
        <el-col v-for="(item, idx) in rating" :key="'r' + item.id" :xs="24" :sm="12" :md="8" :lg="6" style="margin-bottom:16px">
          <el-card shadow="hover" class="rec-card" @click="goDetail(item.id)">
            <div class="rec-rank">#{{ idx + 1 }}</div>
            <div class="rec-cover" :style="{ backgroundImage: item.cover ? `url(${item.cover})` : '' }"></div>
            <div class="rec-info">
              <div class="rec-title">{{ item.title }}</div>
              <div class="rec-meta">{{ item.author }} · {{ item.categoryName || '未分类' }}</div>
              <div class="rec-score">
                <el-rate :model-value="item.score" disabled allow-half :max="5" />
              </div>
            </div>
          </el-card>
        </el-col>
        <el-empty v-if="!loading && rating.length === 0" description="暂无高分评论" />
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { TrendCharts, Star, Reading } from '@element-plus/icons-vue'
import request from '@/utils/request'

const router = useRouter()
const loading = ref(false)
const hot = ref([])
const rating = ref([])
const totalBooks = ref(0)

const loadRecommend = async () => {
  loading.value = true
  try {
    const data = await request.get('/recommend/list')
    hot.value = data.hot || []
    rating.value = data.rating || []
  } catch (e) {
    hot.value = []
    rating.value = []
  } finally {
    loading.value = false
  }
}

const loadTotalBooks = async () => {
  try {
    const data = await request.get('/book/page', { params: { page: 1, size: 1 } })
    totalBooks.value = data.total || 0
  } catch (e) {
    totalBooks.value = 0
  }
}

const goDetail = (id) => {
  router.push('/book/' + id)
}

onMounted(() => {
  loadRecommend()
  loadTotalBooks()
})
</script>

<style scoped>
.block-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
  display: flex;
  align-items: center;
  gap: 6px;
  color: #1d4e3b;
}
.rec-card {
  cursor: pointer;
  position: relative;
  transition: transform .2s;
}
.rec-card:hover {
  transform: translateY(-2px);
}
.rec-card :deep(.el-card__body) {
  padding: 0;
  display: flex;
  align-items: stretch;
}
.rec-rank {
  position: absolute;
  top: 6px;
  left: 6px;
  z-index: 2;
  background: rgba(29, 78, 59, 0.85);
  color: #fff;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 8px;
}
.rec-cover {
  width: 80px;
  height: 110px;
  background: #f0f0f0 center/cover no-repeat;
  flex-shrink: 0;
}
.rec-info {
  flex: 1;
  padding: 8px 12px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-width: 0;
}
.rec-title {
  font-weight: 600;
  font-size: 14px;
  color: #1d4e3b;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.rec-meta {
  font-size: 12px;
  color: #888;
  margin: 4px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.rec-score {
  font-size: 12px;
  color: #555;
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>

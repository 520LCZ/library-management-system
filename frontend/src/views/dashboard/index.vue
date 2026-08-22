<template>
  <div class="g-page dashboard">
    <!-- KPI 卡片 -->
    <el-row :gutter="16" class="kpi-row">
      <el-col :span="6" v-for="item in kpiList" :key="item.key">
        <div class="g-card kpi-card">
          <div class="kpi-inner">
            <div class="kpi-icon" :style="{ background: item.bg, color: item.color }">
              <el-icon :size="24"><component :is="item.icon" /></el-icon>
            </div>
            <div class="kpi-text">
              <div class="kpi-label">{{ item.label }}</div>
              <div class="kpi-value">{{ item.value }}</div>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- ECharts 图表 -->
    <el-row :gutter="16" class="chart-row">
      <el-col :span="12">
        <div class="g-card chart-card">
          <div class="chart-title">借阅趋势(近12月)</div>
          <div ref="trendRef" class="chart-box"></div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="g-card chart-card">
          <div class="chart-title">分类占比</div>
          <div ref="pieRef" class="chart-box"></div>
        </div>
      </el-col>
    </el-row>
    <el-row :gutter="16" class="chart-row">
      <el-col :span="12">
        <div class="g-card chart-card">
          <div class="chart-title">热门图书 Top5</div>
          <div ref="topRef" class="chart-box"></div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="g-card chart-card">
          <div class="chart-title">读者增长(近12月)</div>
          <div ref="growthRef" class="chart-box"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, reactive, ref, shallowRef } from 'vue'
import * as echarts from 'echarts'
import request from '@/utils/request'

// KPI 数据
const stats = reactive({
  kpi: { bookCount: 0, readerCount: 0, borrowingCount: 0, overdueCount: 0 },
  borrowTrend: [],
  categoryDist: [],
  topBooks: [],
  readerGrowth: []
})

const kpiList = computed(() => [
  { key: 'book', label: '总藏书', value: stats.kpi.bookCount, icon: 'Reading', color: '#2F5E4E', bg: '#E8F0EC' },
  { key: 'reader', label: '读者数', value: stats.kpi.readerCount, icon: 'UserFilled', color: '#3E7A66', bg: '#E8F0EC' },
  { key: 'borrowing', label: '借出中', value: stats.kpi.borrowingCount, icon: 'Tickets', color: '#C98A2B', bg: '#F6EBDA' },
  { key: 'overdue', label: '逾期', value: stats.kpi.overdueCount, icon: 'Warning', color: '#B5482E', bg: '#F3E3DE' }
])

// 图表实例 (shallowRef 避免 deep reactive 包裹)
const trendRef = ref(null)
const pieRef = ref(null)
const topRef = ref(null)
const growthRef = ref(null)
const trendChart = shallowRef(null)
const pieChart = shallowRef(null)
const topChart = shallowRef(null)
const growthChart = shallowRef(null)

const loadStats = async () => {
  try {
    const data = await request.get('/dashboard/stats')
    if (data) {
      stats.kpi = data.kpi || stats.kpi
      stats.borrowTrend = data.borrowTrend || []
      stats.categoryDist = data.categoryDist || []
      stats.topBooks = data.topBooks || []
      stats.readerGrowth = data.readerGrowth || []
    }
  } catch (e) {
    // 兜底示例结构
    stats.borrowTrend = Array.from({ length: 12 }, (_, i) => ({ month: String(i + 1).padStart(2, '0'), count: 10 + i }))
    stats.categoryDist = [{ name: '文学', value: 45 }, { name: '科技', value: 30 }, { name: '历史', value: 20 }, { name: '其它', value: 15 }]
    stats.topBooks = [{ name: '三体', value: 32 }, { name: '活着', value: 28 }, { name: '百年孤独', value: 22 }, { name: '围城', value: 18 }, { name: '小王子', value: 12 }]
    stats.readerGrowth = Array.from({ length: 12 }, (_, i) => ({ month: String(i + 1).padStart(2, '0'), count: 3 + (i % 5) }))
  }
  renderCharts()
}

const renderCharts = () => {
  // 借阅趋势折线
  if (trendRef.value) {
    if (!trendChart.value) trendChart.value = echarts.init(trendRef.value)
    trendChart.value.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: 40, right: 20, top: 30, bottom: 30 },
      xAxis: { type: 'category', data: stats.borrowTrend.map((i) => i.month), axisLine: { lineStyle: { color: '#E3E8E4' } } },
      yAxis: { type: 'value', axisLine: { show: false }, splitLine: { lineStyle: { color: '#F0F2F1' } } },
      series: [{ name: '借阅', type: 'line', smooth: true, data: stats.borrowTrend.map((i) => i.count), itemStyle: { color: '#2F5E4E' }, areaStyle: { color: 'rgba(47,94,78,0.15)' } }]
    })
  }
  // 分类占比饼
  if (pieRef.value) {
    if (!pieChart.value) pieChart.value = echarts.init(pieRef.value)
    pieChart.value.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      color: ['#2F5E4E', '#3E7A66', '#5A8F7B', '#82b1a2', '#C98A2B'],
      series: [{ type: 'pie', radius: ['40%', '65%'], data: stats.categoryDist, itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 }, label: { formatter: '{b}: {d}%' } }]
    })
  }
  // 热门图书横向柱
  if (topRef.value) {
    if (!topChart.value) topChart.value = echarts.init(topRef.value)
    const data = [...stats.topBooks].reverse()
    topChart.value.setOption({
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { left: 80, right: 20, top: 30, bottom: 30 },
      xAxis: { type: 'value', axisLine: { show: false }, splitLine: { lineStyle: { color: '#F0F2F1' } } },
      yAxis: { type: 'category', data: data.map((i) => i.name), axisLine: { lineStyle: { color: '#E3E8E4' } } },
      series: [{ type: 'bar', data: data.map((i) => i.value), itemStyle: { color: '#3E7A66', borderRadius: [0, 4, 4, 0] }, barWidth: 16 }]
    })
  }
  // 读者增长折线
  if (growthRef.value) {
    if (!growthChart.value) growthChart.value = echarts.init(growthRef.value)
    growthChart.value.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: 40, right: 20, top: 30, bottom: 30 },
      xAxis: { type: 'category', data: stats.readerGrowth.map((i) => i.month), axisLine: { lineStyle: { color: '#E3E8E4' } } },
      yAxis: { type: 'value', axisLine: { show: false }, splitLine: { lineStyle: { color: '#F0F2F1' } } },
      series: [{ name: '新增读者', type: 'line', smooth: true, data: stats.readerGrowth.map((i) => i.count), itemStyle: { color: '#C98A2B' }, areaStyle: { color: 'rgba(201,138,43,0.15)' } }]
    })
  }
}

const onResize = () => {
  trendChart.value && trendChart.value.resize()
  pieChart.value && pieChart.value.resize()
  topChart.value && topChart.value.resize()
  growthChart.value && growthChart.value.resize()
}

onMounted(async () => {
  await loadStats()
  window.addEventListener('resize', onResize)
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', onResize)
  trendChart.value && trendChart.value.dispose()
  pieChart.value && pieChart.value.dispose()
  topChart.value && topChart.value.dispose()
  growthChart.value && growthChart.value.dispose()
})
</script>

<style scoped>
.dashboard {
  height: 100%;
}
.kpi-row {
  margin-bottom: 16px;
}
.kpi-card {
  padding: 18px 20px;
}
.kpi-inner {
  display: flex;
  align-items: center;
  gap: 14px;
}
.kpi-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}
.kpi-label {
  font-size: 13px;
  color: var(--brand-text-secondary);
}
.kpi-value {
  font-size: 26px;
  font-weight: 600;
  color: var(--brand-text-primary);
  margin-top: 2px;
}
.chart-row {
  margin-bottom: 16px;
}
.chart-card {
  padding: 16px;
}
.chart-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--brand-text-primary);
  margin-bottom: 8px;
  padding-left: 8px;
  border-left: 3px solid var(--brand-primary);
}
.chart-box {
  height: 280px;
}
</style>

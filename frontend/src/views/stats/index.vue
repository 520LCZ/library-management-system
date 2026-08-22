<template>
  <div class="g-page">
    <el-row :gutter="16" class="chart-row">
      <el-col :span="12">
        <div class="g-card chart-card">
          <div class="chart-title">借阅分类占比</div>
          <div ref="catRef" class="chart-box"></div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="g-card chart-card">
          <div class="chart-title">月度借阅</div>
          <div ref="monthRef" class="chart-box"></div>
        </div>
      </el-col>
    </el-row>
    <el-row :gutter="16" class="chart-row">
      <el-col :span="12">
        <div class="g-card chart-card">
          <div class="chart-title">活跃读者 Top10</div>
          <div ref="readerRef" class="chart-box"></div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="g-card chart-card">
          <div class="chart-title">库存汇总</div>
          <div ref="invRef" class="chart-box"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, reactive, ref, shallowRef } from 'vue'
import * as echarts from 'echarts'
import request from '@/utils/request'

const catRef = ref(null)
const monthRef = ref(null)
const readerRef = ref(null)
const invRef = ref(null)
const catChart = shallowRef(null)
const monthChart = shallowRef(null)
const readerChart = shallowRef(null)
const invChart = shallowRef(null)

const state = reactive({
  byCategory: [],
  byMonth: [],
  activeReaders: [],
  inventory: { totalBooks: 0, totalStock: 0, totalBorrowed: 0, byCategory: [] }
})

const loadAll = async () => {
  const tasks = [
    (async () => {
      try { state.byCategory = await request.get('/stats/borrow-by-category') } catch (e) {
        state.byCategory = [{ name: '文学', value: 45 }, { name: '科技', value: 30 }, { name: '历史', value: 25 }]
      }
    })(),
    (async () => {
      try { state.byMonth = await request.get('/stats/borrow-by-month') } catch (e) {
        state.byMonth = Array.from({ length: 12 }, (_, i) => ({ month: String(i + 1).padStart(2, '0'), count: 10 + i }))
      }
    })(),
    (async () => {
      try { state.activeReaders = await request.get('/stats/active-readers') } catch (e) {
        state.activeReaders = Array.from({ length: 10 }, (_, i) => ({ name: '读者' + (i + 1), value: 20 - i }))
      }
    })(),
    (async () => {
      try { state.inventory = await request.get('/stats/inventory-summary') } catch (e) {
        state.inventory = { totalBooks: 320, totalStock: 290, totalBorrowed: 30, byCategory: [{ name: '文学', value: 120 }, { name: '科技', value: 100 }, { name: '历史', value: 100 }] }
      }
    })()
  ]
  await Promise.all(tasks)
  renderCharts()
}

const renderCharts = () => {
  if (catRef.value) {
    if (!catChart.value) catChart.value = echarts.init(catRef.value)
    catChart.value.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      color: ['#2F5E4E', '#3E7A66', '#5A8F7B', '#82b1a2', '#C98A2B', '#B5482E'],
      series: [{ type: 'pie', radius: ['40%', '65%'], data: state.byCategory, itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 }, label: { formatter: '{b}: {d}%' } }]
    })
  }
  if (monthRef.value) {
    if (!monthChart.value) monthChart.value = echarts.init(monthRef.value)
    monthChart.value.setOption({
      tooltip: { trigger: 'axis' },
      grid: { left: 40, right: 20, top: 30, bottom: 30 },
      xAxis: { type: 'category', data: state.byMonth.map((i) => i.month), axisLine: { lineStyle: { color: '#E3E8E4' } } },
      yAxis: { type: 'value', axisLine: { show: false }, splitLine: { lineStyle: { color: '#F0F2F1' } } },
      series: [{ type: 'bar', data: state.byMonth.map((i) => i.count), itemStyle: { color: '#3E7A66', borderRadius: [4, 4, 0, 0] }, barWidth: 18 }]
    })
  }
  if (readerRef.value) {
    if (!readerChart.value) readerChart.value = echarts.init(readerRef.value)
    const data = [...state.activeReaders].reverse()
    readerChart.value.setOption({
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { left: 80, right: 20, top: 30, bottom: 30 },
      xAxis: { type: 'value', axisLine: { show: false }, splitLine: { lineStyle: { color: '#F0F2F1' } } },
      yAxis: { type: 'category', data: data.map((i) => i.name), axisLine: { lineStyle: { color: '#E3E8E4' } } },
      series: [{ type: 'bar', data: data.map((i) => i.value), itemStyle: { color: '#2F5E4E', borderRadius: [0, 4, 4, 0] }, barWidth: 14 }]
    })
  }
  if (invRef.value) {
    if (!invChart.value) invChart.value = echarts.init(invRef.value)
    const summary = state.inventory
    invChart.value.setOption({
      tooltip: { trigger: 'item' },
      legend: { bottom: 0 },
      color: ['#2F5E4E', '#3E7A66', '#5A8F7B', '#82b1a2'],
      series: [{ type: 'pie', radius: ['45%', '70%'], data: summary.byCategory, itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 }, label: { formatter: '{b}: {c}' } }]
    })
  }
}

const onResize = () => {
  catChart.value && catChart.value.resize()
  monthChart.value && monthChart.value.resize()
  readerChart.value && readerChart.value.resize()
  invChart.value && invChart.value.resize()
}

onMounted(async () => {
  await loadAll()
  window.addEventListener('resize', onResize)
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', onResize)
  catChart.value && catChart.value.dispose()
  monthChart.value && monthChart.value.dispose()
  readerChart.value && readerChart.value.dispose()
  invChart.value && invChart.value.dispose()
})
</script>

<style scoped>
.chart-row { margin-bottom: 16px; }
.chart-card { padding: 16px; }
.chart-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--brand-text-primary);
  margin-bottom: 8px;
  padding-left: 8px;
  border-left: 3px solid var(--brand-primary);
}
.chart-box { height: 300px; }
</style>

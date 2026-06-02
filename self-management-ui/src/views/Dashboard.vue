<template>
  <div class="dashboard-container">
    <div class="header-section">
      <h2 class="title">📊 数据总览面板</h2>
      <p class="subtitle">把计划、课程、专注、财务和体重都放在一处，方便快速看状态。</p>
    </div>

    <el-row :gutter="20" class="kpi-row">
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="kpi-card glass-card">
          <div class="kpi-icon">🎯</div>
          <div class="kpi-info">
            <div class="kpi-label">计划总数</div>
            <div class="kpi-value">{{ overview.planTotal }} <span class="kpi-unit">个</span></div>
            <div class="kpi-sub">已完成 {{ overview.planCompleted }}，进行中 {{ overview.planProgress }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="kpi-card glass-card">
          <div class="kpi-icon">⏱️</div>
          <div class="kpi-info">
            <div class="kpi-label">本周专注</div>
            <div class="kpi-value">{{ totalFocusHours }} <span class="kpi-unit">小时</span></div>
            <div class="kpi-sub">平均每天 {{ focusAverage.toFixed(1) }} 分钟</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="kpi-card glass-card">
          <div class="kpi-icon">💰</div>
          <div class="kpi-info">
            <div class="kpi-label">本月结余</div>
            <div class="kpi-value">¥ {{ currentBalance.toFixed(2) }}</div>
            <div class="kpi-sub">收入 {{ currentIncome.toFixed(2) }} / 支出 {{ currentExpense.toFixed(2) }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <el-card shadow="hover" class="kpi-card glass-card">
          <div class="kpi-icon">📚</div>
          <div class="kpi-info">
            <div class="kpi-label">课程安排</div>
            <div class="kpi-value">{{ overview.courseTotal }} <span class="kpi-unit">节</span></div>
            <div class="kpi-sub">今天 {{ todayCourseCount }} 节课</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="16">
        <el-card shadow="hover" class="glass-card chart-card">
          <template #header>
            <div class="chart-header">🔥 近 7 天专注趋势</div>
          </template>
          <div ref="focusChartRef" class="chart-box"></div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="8">
        <el-card shadow="hover" class="glass-card chart-card">
          <template #header>
            <div class="chart-header">🧭 计划状态分布</div>
          </template>
          <div ref="planChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="24">
        <el-card shadow="hover" class="glass-card chart-card">
          <template #header>
            <div class="chart-header">💹 财务月度收支</div>
          </template>
          <div ref="financeChartRef" class="chart-box" style="height: 340px;"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="glass-card list-card">
          <template #header>
            <div class="chart-header">📌 最近计划</div>
          </template>
          <div class="list-grid">
            <div v-for="item in recentPlans" :key="item.id" class="list-item">
              <div>
                <div class="list-title">{{ item.title }}</div>
                <div class="list-meta">{{ item.deadline || '未设置截止日期' }} · {{ item.status }}</div>
              </div>
              <el-tag :type="planTagType(item.status)" effect="dark" size="small">{{ item.status }}</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="glass-card list-card">
          <template #header>
            <div class="chart-header">📚 课程预览</div>
          </template>
          <div class="list-grid">
            <div v-for="item in recentCourses" :key="item.id" class="list-item">
              <div>
                <div class="list-title">{{ item.courseName }}</div>
                <div class="list-meta">{{ dayText(item.dayOfWeek) }} · 第 {{ item.section }} 节 · {{ item.location || '未填写地点' }}</div>
              </div>
              <el-tag type="info" effect="plain" size="small">{{ item.teacher || '未填写老师' }}</el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, markRaw, ref } from 'vue'
import * as echarts from 'echarts'
import axios from 'axios'

const currentUser = ref(JSON.parse(localStorage.getItem('user') || '{}'))
const focusChartRef = ref(null)
const planChartRef = ref(null)
const financeChartRef = ref(null)
const overview = ref({ planTotal: 0, planCompleted: 0, planProgress: 0, courseTotal: 0 })
const recentPlans = ref([])
const recentCourses = ref([])
const totalFocusHours = ref(0)
const focusAverage = ref(0)
const currentBalance = ref(0)
const currentIncome = ref(0)
const currentExpense = ref(0)
const todayCourseCount = ref(0)

let focusChart = null
let planChart = null
let financeChart = null

const dayText = (dayOfWeek) => ['','周一','周二','周三','周四','周五','周六','周日'][dayOfWeek] || '未安排'
const planTagType = (status) => status === '已完成' ? 'success' : status === '进行中' ? 'warning' : 'info'

const readLocal = (key, fallback = []) => {
  try { return JSON.parse(localStorage.getItem(key) || JSON.stringify(fallback)) } catch { return fallback }
}

const loadOverview = () => {
  const userId = currentUser.value.id
  const plans = readLocal(`self-management-plans-${userId}`)
  const courses = readLocal(`self-management-courses-${userId}`)
  const finances = readLocal(`self-management-finances-${userId}`)
  const focus = readLocal(`self-management-focus-${userId}`)

  overview.value.planTotal = plans.length
  overview.value.planCompleted = plans.filter(item => item.status === '已完成').length
  overview.value.planProgress = plans.filter(item => item.status === '进行中').length
  overview.value.courseTotal = courses.length

  recentPlans.value = [...plans].sort((a, b) => String(a.deadline || '').localeCompare(String(b.deadline || ''))).slice(0, 5)
  recentCourses.value = [...courses].sort((a, b) => (a.dayOfWeek - b.dayOfWeek) || (a.section - b.section)).slice(0, 5)
  todayCourseCount.value = courses.filter(item => item.dayOfWeek === new Date().getDay() || (new Date().getDay() === 0 && item.dayOfWeek === 7)).length

  const totalMinutes = focus.reduce((sum, item) => sum + Number(item.total || 0), 0)
  totalFocusHours.value = Math.floor(totalMinutes / 60)
  focusAverage.value = focus.length ? totalMinutes / focus.length : 0

  const currentMonthStr = new Date().toISOString().slice(0, 7)
  currentIncome.value = finances.filter(item => item.type === '收入' && String(item.recordDate || '').slice(0, 7) === currentMonthStr).reduce((sum, item) => sum + Number(item.amount || 0), 0)
  currentExpense.value = finances.filter(item => item.type === '支出' && String(item.recordDate || '').slice(0, 7) === currentMonthStr).reduce((sum, item) => sum + Number(item.amount || 0), 0)
  currentBalance.value = currentIncome.value - currentExpense.value
}

const initPlanChart = async () => {
  planChart = markRaw(echarts.init(planChartRef.value))
  const userId = currentUser.value.id
  const localPlans = readLocal(`self-management-plans-${userId}`)
  let realData = []
  try {
    const res = await axios.get('/plan/stats', { params: { userId } })
    if (res.data.code === 200 && Array.isArray(res.data.data) && res.data.data.length) realData = res.data.data
  } catch {}
  if (!realData.length) {
    const counts = { '未开始': 0, '进行中': 0, '已完成': 0 }
    localPlans.forEach(item => { counts[item.status] = (counts[item.status] || 0) + 1 })
    realData = Object.entries(counts).map(([name, value]) => ({ name, value }))
  }
  const completedItem = realData.find(item => item.name === '已完成')
  if (completedItem) overview.value.planCompleted = completedItem.value
  planChart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: '0%', left: 'center', icon: 'circle' },
    color: ['#60a5fa', '#f59e0b', '#34d399'],
    series: [{ name: '计划状态', type: 'pie', radius: ['45%', '72%'], avoidLabelOverlap: false, itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 }, label: { show: true, formatter: '{b}\n{d}%' }, labelLine: { show: true }, data: realData }]
  })
}

const initFinanceChart = async () => {
  financeChart = markRaw(echarts.init(financeChartRef.value))
  const userId = currentUser.value.id
  const localFinances = readLocal(`self-management-finances-${userId}`)
  let rawData = []
  try {
    const res = await axios.get('/finance/stats', { params: { userId } })
    if (res.data.code === 200) rawData = res.data.data || []
  } catch {}
  if (!rawData.length && localFinances.length) {
    const monthMap = new Map()
    localFinances.forEach(item => {
      const month = String(item.recordDate || '').slice(0, 7)
      if (!month) return
      if (!monthMap.has(month)) monthMap.set(month, { month, type: '收入', total: 0 })
      if (!monthMap.has(`${month}-expense`)) monthMap.set(`${month}-expense`, { month, type: '支出', total: 0 })
      if (item.type === '收入') monthMap.get(month).total += Number(item.amount || 0)
      if (item.type === '支出') monthMap.get(`${month}-expense`).total += Number(item.amount || 0)
    })
    rawData = [...monthMap.values()]
  }
  const months = [...new Set(rawData.map(item => item.month))].sort()
  const incomes = months.map(month => rawData.find(item => item.month === month && item.type === '收入')?.total || 0)
  const expenses = months.map(month => rawData.find(item => item.month === month && item.type === '支出')?.total || 0)
  financeChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    legend: { data: ['收入', '支出'], top: '2%', right: '5%' },
    grid: { top: '15%', left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: months, axisLine: { lineStyle: { color: '#94a3b8' } } },
    yAxis: { type: 'value', splitLine: { lineStyle: { type: 'dashed', color: '#e2e8f0' } } },
    color: ['#22c55e', '#ef4444'],
    series: [
      { name: '收入', type: 'bar', barWidth: '22%', itemStyle: { borderRadius: [6, 6, 0, 0] }, data: incomes },
      { name: '支出', type: 'bar', barWidth: '22%', itemStyle: { borderRadius: [6, 6, 0, 0] }, data: expenses }
    ]
  })
}

const initFocusChart = async () => {
  focusChart = markRaw(echarts.init(focusChartRef.value))
  const userId = currentUser.value.id
  const localFocus = readLocal(`self-management-focus-${userId}`)
  let rawData = []
  try {
    const res = await axios.get('/focus/stats', { params: { userId } })
    if (res.data.code === 200) rawData = res.data.data || []
  } catch {}
  if (!rawData.length) rawData = localFocus.map(item => ({ date: item.date, total: item.total }))
  const totalMinutes = rawData.reduce((sum, item) => sum + Number(item.total || 0), 0)
  totalFocusHours.value = Math.floor(totalMinutes / 60)
  const last7Days = []
  const last7Values = []
  for (let i = 6; i >= 0; i--) {
    const d = new Date(); d.setDate(d.getDate() - i)
    const dateStr = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
    last7Days.push(['周日','周一','周二','周三','周四','周五','周六'][d.getDay()])
    last7Values.push(rawData.find(item => item.date === dateStr)?.total || 0)
  }
  focusChart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'cross' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', boundaryGap: false, data: last7Days, axisLine: { lineStyle: { color: '#94a3b8' } } },
    yAxis: { type: 'value', name: '分钟', splitLine: { lineStyle: { type: 'dashed', color: '#e2e8f0' } } },
    series: [{ name: '专注时长', type: 'line', smooth: true, symbol: 'circle', symbolSize: 8, lineStyle: { color: '#06b6d4', width: 4 }, areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(6, 182, 212, 0.45)' }, { offset: 1, color: 'rgba(6, 182, 212, 0.01)' }]) }, data: last7Values }]
  })
}

const handleResize = () => { focusChart?.resize(); planChart?.resize(); financeChart?.resize() }

onMounted(() => {
  loadOverview()
  setTimeout(() => { initFocusChart(); initPlanChart(); initFinanceChart(); window.addEventListener('resize', handleResize) }, 80)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  focusChart?.dispose(); planChart?.dispose(); financeChart?.dispose()
})
</script>

<style scoped>
.dashboard-container { padding: 20px; animation: fadeIn 0.6s ease-out; }
.header-section { margin-bottom: 25px; }
.title { font-size: 28px; font-weight: 900; color: #1e293b; margin: 0 0 5px 0; letter-spacing: 1px; }
.subtitle { color: #64748b; font-size: 15px; margin: 0; }
.glass-card { border-radius: 16px; background: rgba(255, 255, 255, 0.88); backdrop-filter: blur(12px); border: 1px solid rgba(255, 255, 255, 0.65); box-shadow: 0 10px 30px rgba(0, 0, 0, 0.04); transition: all 0.3s ease; }
.glass-card:hover { box-shadow: 0 15px 35px rgba(0, 0, 0, 0.08); transform: translateY(-2px); }
.kpi-row, .chart-row { margin-bottom: 20px; }
.kpi-card { display: flex; align-items: center; padding: 22px 18px; }
.kpi-icon { font-size: 40px; margin-right: 16px; padding: 14px; border-radius: 16px; background: #f8fafc; }
.kpi-info { display: flex; flex-direction: column; gap: 4px; }
.kpi-label { font-size: 14px; color: #64748b; font-weight: bold; }
.kpi-value { font-size: 30px; font-weight: 900; color: #0f172a; font-family: 'Courier New', Courier, monospace; }
.kpi-unit { font-size: 16px; color: #94a3b8; font-weight: normal; }
.kpi-sub { color: #64748b; font-size: 12px; }
.chart-card, .list-card { height: 100%; }
.chart-header { font-size: 16px; font-weight: 800; color: #334155; padding-bottom: 5px; border-bottom: 2px solid #f1f5f9; }
.chart-box { width: 100%; height: 320px; }
.list-grid { display: grid; gap: 12px; }
.list-item { display: flex; justify-content: space-between; gap: 12px; align-items: center; padding: 12px 14px; border-radius: 12px; background: rgba(248, 250, 252, 0.9); }
.list-title { color: #0f172a; font-weight: 800; margin-bottom: 4px; }
.list-meta { color: #64748b; font-size: 12px; }
:deep(.el-card__body) { padding: 15px; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(15px); } to { opacity: 1; transform: translateY(0); } }
@media (max-width: 900px) {
  .dashboard-container { padding: 8px 4px 20px; }
  .title { font-size: 22px; line-height: 1.3; }
  .subtitle { font-size: 13px; line-height: 1.6; }
  .kpi-card { padding: 18px 16px; }
  .kpi-icon { font-size: 30px; margin-right: 12px; padding: 10px; }
  .kpi-value { font-size: 24px; }
  .chart-header { font-size: 14px; }
  .chart-box { height: 260px; }
}
</style>

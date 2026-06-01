<template>
  <div class="dashboard-container">
    <div class="header-section">
      <h2 class="title">🌌 全局数据指挥中心</h2>
      <p class="subtitle">数据不会撒谎，它记录着你自律的每一次跳动。</p>
    </div>

    <el-row :gutter="20" class="kpi-row">
      <el-col :span="8">
        <el-card shadow="hover" class="kpi-card glass-card focus-card">
          <div class="kpi-icon">⏱️</div>
          <div class="kpi-info">
            <div class="kpi-label">累计专注时长</div>
            <div class="kpi-value">{{ totalFocusHours }} <span class="kpi-unit">小时</span></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="kpi-card glass-card plan-card">
          <div class="kpi-icon">🎯</div>
          <div class="kpi-info">
            <div class="kpi-label">计划完成总数</div>
            <div class="kpi-value">{{ completedPlanCount }} <span class="kpi-unit">个</span></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="hover" class="kpi-card glass-card finance-card">
          <div class="kpi-icon">💰</div>
          <div class="kpi-info">
            <div class="kpi-label">本月财务结余</div>
            <div class="kpi-value">¥ {{ currentBalance.toFixed(2) }} </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="16">
        <el-card shadow="hover" class="glass-card chart-card">
          <template #header>
            <div class="chart-header">🔥 近7日专注战斗力趋势</div>
          </template>
          <div ref="focusChartRef" class="chart-box"></div>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card shadow="hover" class="glass-card chart-card">
          <template #header>
            <div class="chart-header">📊 目标计划执行分布</div>
          </template>
          <div ref="planChartRef" class="chart-box"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="chart-row">
      <el-col :span="24">
        <el-card shadow="hover" class="glass-card chart-card">
          <template #header>
            <div class="chart-header">💵 历史财务收支复盘</div>
          </template>
          <div ref="financeChartRef" class="chart-box" style="height: 350px;"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, markRaw } from 'vue'
import * as echarts from 'echarts'
import axios from 'axios'

const currentUser = ref(JSON.parse(localStorage.getItem('user') || '{}'))

const focusChartRef = ref(null)
const planChartRef = ref(null)
const financeChartRef = ref(null)

// 🌟 KPI 卡片动态绑定的变量全家桶
const totalFocusHours = ref(0) // 动态专注小时数
const completedPlanCount = ref(0)
const currentBalance = ref(0) 

let focusChart = null
let planChart = null
let financeChart = null

// 🌟 1. 获取真实计划数据并渲染
const initPlanChart = async () => {
  planChart = markRaw(echarts.init(planChartRef.value))
  try {
    const res = await axios.get('/plan/stats', { params: { userId: currentUser.value.id } })
    if (res.data.code === 200) {
      const realData = res.data.data || []
      const completedItem = realData.find(item => item.name === '已完成')
      if (completedItem) completedPlanCount.value = completedItem.value

      const option = {
        tooltip: { trigger: 'item' },
        legend: { bottom: '0%', left: 'center', icon: 'circle' },
        color: ['#3b82f6', '#f59e0b', '#10b981'], 
        series: [{
          name: '计划状态', type: 'pie', radius: ['45%', '70%'], avoidLabelOverlap: false,
          itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
          label: { show: false, position: 'center' },
          emphasis: { label: { show: true, fontSize: '20', fontWeight: 'bold' } },
          labelLine: { show: false }, data: realData
        }]
      }
      planChart.setOption(option)
    }
  } catch (error) { console.error('获取饼图数据失败:', error) }
}

// 🌟 2. 获取真实财务数据，组装双塔柱状图
const initFinanceChart = async () => {
  financeChart = markRaw(echarts.init(financeChartRef.value))
  try {
    const res = await axios.get('/finance/stats', { params: { userId: currentUser.value.id } })
    if (res.data.code === 200) {
      const rawData = res.data.data || []
      
      const months = [...new Set(rawData.map(item => item.month))].sort()
      const incomes = months.map(m => {
        const found = rawData.find(item => item.month === m && item.type === '收入')
        return found ? found.total : 0
      })
      const expenses = months.map(m => {
        const found = rawData.find(item => item.month === m && item.type === '支出')
        return found ? found.total : 0
      })

      // 算出当前年月的收支结余
      const d = new Date()
      const currentMonthStr = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`
      const curIncome = rawData.find(item => item.month === currentMonthStr && item.type === '收入')?.total || 0
      const curExpense = rawData.find(item => item.month === currentMonthStr && item.type === '支出')?.total || 0
      currentBalance.value = curIncome - curExpense

      const option = {
        tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
        // 🌟 修复 1：把图例移到图表的右上角，避开底部的日期
        legend: { 
          data: ['总收入', '总支出'],
          top: '2%', 
          right: '5%' 
        },
        // 🌟 修复 2：调整网格间距，给顶部图例和底部日期都留出呼吸空间
        grid: { 
          top: '15%', 
          left: '3%', 
          right: '4%', 
          bottom: '3%', 
          containLabel: true 
        },
        xAxis: { type: 'category', data: months, axisLine: { lineStyle: { color: '#94a3b8' } } },
        yAxis: { type: 'value', splitLine: { lineStyle: { type: 'dashed', color: '#e2e8f0' } } },
        color: ['#34d399', '#f87171'], 
        series: [
          { name: '总收入', type: 'bar', barWidth: '20%', itemStyle: { borderRadius: [5, 5, 0, 0] }, data: incomes },
          { name: '总支出', type: 'bar', barWidth: '20%', itemStyle: { borderRadius: [5, 5, 0, 0] }, data: expenses }
        ]
      }
      financeChart.setOption(option)
    }
  } catch (error) { console.error('获取财务数据失败:', error) }
}

// 🌟 3. 获取真实专注数据，点亮近7日折线图
const initFocusChart = async () => {
  focusChart = markRaw(echarts.init(focusChartRef.value))
  try {
    const res = await axios.get('/focus/stats', { params: { userId: currentUser.value.id } })
    if (res.data.code === 200) {
      const rawData = res.data.data || []

      // 🎯 KPI魔法：换算历史总累计专注小时数
      const totalMinutes = rawData.reduce((sum, item) => sum + Number(item.total), 0)
      totalFocusHours.value = Math.floor(totalMinutes / 60)

      // 🧠 算法魔法：推算最近7天，并自动填补没有记录的日期空缺
      const last7Days = []
      const last7Values = []
      for (let i = 6; i >= 0; i--) {
        const d = new Date()
        d.setDate(d.getDate() - i)
        // 格式化为 YYYY-MM-DD
        const dateStr = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
        const dayName = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'][d.getDay()]
        
        last7Days.push(dayName)
        const found = rawData.find(item => item.date === dateStr)
        last7Values.push(found ? Number(found.total) : 0)
      }

      const option = {
        tooltip: { trigger: 'axis', axisPointer: { type: 'cross' } },
        grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
        xAxis: { type: 'category', boundaryGap: false, data: last7Days, axisLine: { lineStyle: { color: '#94a3b8' } } },
        yAxis: { type: 'value', name: '分钟', splitLine: { lineStyle: { type: 'dashed', color: '#e2e8f0' } } },
        series: [{ 
          name: '专注时长', type: 'line', smooth: true, symbol: 'none', 
          lineStyle: { color: '#10b981', width: 4 }, 
          areaStyle: { color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{ offset: 0, color: 'rgba(16, 185, 129, 0.5)' }, { offset: 1, color: 'rgba(16, 185, 129, 0.01)' }]) }, 
          data: last7Values 
        }]
      }
      focusChart.setOption(option)
    }
  } catch (error) { console.error('获取专注数据失败:', error) }
}

const handleResize = () => {
  focusChart?.resize(); planChart?.resize(); financeChart?.resize()
}

onMounted(() => {
  setTimeout(() => {
    initFocusChart()  // 🚀 全面激活真实接口
    initPlanChart() 
    initFinanceChart() 
    window.addEventListener('resize', handleResize)
  }, 100)
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
.glass-card { border-radius: 16px; background: rgba(255, 255, 255, 0.85); backdrop-filter: blur(12px); border: 1px solid rgba(255, 255, 255, 0.6); box-shadow: 0 10px 30px rgba(0, 0, 0, 0.04); transition: all 0.3s ease; }
.glass-card:hover { box-shadow: 0 15px 35px rgba(0, 0, 0, 0.08); transform: translateY(-2px); }
.kpi-row { margin-bottom: 20px; }
.kpi-card { display: flex; align-items: center; padding: 25px 20px; }
.kpi-icon { font-size: 45px; margin-right: 20px; padding: 15px; border-radius: 16px; background: #f8fafc; box-shadow: inset 0 2px 4px rgba(0,0,0,0.02); }
.kpi-info { display: flex; flex-direction: column; }
.kpi-label { font-size: 14px; color: #64748b; font-weight: bold; margin-bottom: 5px; }
.kpi-value { font-size: 32px; font-weight: 900; color: #0f172a; font-family: 'Courier New', Courier, monospace; }
.kpi-unit { font-size: 16px; color: #94a3b8; font-weight: normal; }
.chart-row { margin-bottom: 20px; }
.chart-card { height: 100%; }
.chart-header { font-size: 16px; font-weight: 800; color: #334155; padding-bottom: 5px; border-bottom: 2px solid #f1f5f9; }
.chart-box { width: 100%; height: 300px; }
:deep(.el-card__body) { padding: 15px; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(15px); } to { opacity: 1; transform: translateY(0); } }
</style>
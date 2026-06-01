
<template>
  <div class="focus-container">
    <el-card shadow="always" class="premium-glass-card" :class="{ 'is-focusing': isRunning }">
      
      <div class="header">
        <span class="icon">🍅</span>
        <h2 class="title">沉浸专注时刻</h2>
      </div>
      <p class="subtitle" v-if="!isRunning">拖动滑块，设定你的专属专注时长</p>
      <p class="subtitle focus-text" v-else>别切屏，世界正在为你静止...</p>

      <div class="timer-display-wrapper" :class="{ 'breathing': isRunning }">
        <el-progress 
          type="dashboard" 
          :percentage="percentage" 
          :color="progressColor"
          :width="280"
          :stroke-width="16"
        >
          <template #default>
            <div class="time-huge">{{ formatTime(timeLeft) }}</div>
            <div class="time-label">{{ isRunning ? 'FOCUSING' : 'READY' }}</div>
          </template>
        </el-progress>
      </div>

      <transition name="fade">
        <div class="settings-section" v-if="!isRunning">
          
          <div class="slider-wrapper">
            <span class="limit-text">1 分钟</span>
            <el-slider 
              v-model="customMinutes" 
              :min="1" 
              :max="120" 
              :step="1" 
              show-tooltip
              @input="updateTimeFromSlider"
              class="custom-slider"
            />
            <span class="limit-text">120 分钟</span>
          </div>

          <div class="quick-pills">
            <el-button round size="small" class="pill-btn" @click="setQuickTime(5)">☕ 5分</el-button>
            <el-button round size="small" class="pill-btn" @click="setQuickTime(25)">🍅 25分</el-button>
            <el-button round size="small" class="pill-btn" @click="setQuickTime(45)">📖 45分</el-button>
            <el-button round size="small" class="pill-btn" @click="setQuickTime(90)">🔥 90分</el-button>
          </div>
        </div>
      </transition>

      <div class="controls-section">
        <el-button 
          v-if="!isRunning" 
          type="primary" 
          size="large" 
          round 
          class="action-btn start-btn" 
          @click="startTimer"
        >
          🚀 开启沉浸
        </el-button>
        
        <el-button 
          v-if="isRunning" 
          type="warning" 
          size="large" 
          round 
          class="action-btn" 
          @click="pauseTimer"
        >
          ⏸️ 暂时中止
        </el-button>
        
        <el-button 
          type="info" 
          plain 
          size="large" 
          round 
          class="action-btn reset-btn" 
          @click="resetTimer"
        >
          🔄 放弃重置
        </el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElNotification } from 'element-plus'
import axios from 'axios'
// --- 🌟 状态与记忆核心 ---
const customMinutes = ref(25)   // 滑动条绑定的分钟数
const TOTAL_TIME = ref(25 * 60) // 总秒数
const timeLeft = ref(25 * 60)   // 剩余秒数
const isRunning = ref(false)    // 是否正在运行
let timerInterval = null

// 进度条倒着走
const percentage = computed(() => {
  return Number(((timeLeft.value / TOTAL_TIME.value) * 100).toFixed(1))
})

// 进度条颜色渐变
const progressColor = computed(() => {
  if (percentage.value > 50) return '#34d399' // 翠绿色
  if (percentage.value > 20) return '#fbbf24' // 琥珀色
  return '#f87171'                            // 珊瑚红
})

// 格式化时间 00:00
const formatTime = (seconds) => {
  const m = Math.floor(seconds / 60).toString().padStart(2, '0')
  const s = (seconds % 60).toString().padStart(2, '0')
  return `${m}:${s}`
}

// 🌟 响应滑动条拖动
const updateTimeFromSlider = (val) => {
  TOTAL_TIME.value = val * 60
  timeLeft.value = val * 60
}

// 响应快捷药丸点击
const setQuickTime = (val) => {
  customMinutes.value = val
  updateTimeFromSlider(val)
}

// --- 🌟 定时器逻辑 (带失忆恢复功能) ---
const startTimer = () => {
  if (timeLeft.value <= 0) return
  isRunning.value = true
  
  // 记录到本地保险箱，防止切页面丢失
  const expectedEndTime = Date.now() + timeLeft.value * 1000
  localStorage.setItem('tomato_end_time', expectedEndTime)
  localStorage.setItem('tomato_total_time', TOTAL_TIME.value)

  runInterval()
}

const runInterval = () => {
  clearInterval(timerInterval)
  timerInterval = setInterval(() => {
    const endTime = localStorage.getItem('tomato_end_time')
    if (endTime) {
      const remaining = Math.round((endTime - Date.now()) / 1000)
      if (remaining > 0) {
        timeLeft.value = remaining
      } else {
        handleFinished()
      }
    }
  }, 1000)
}

const pauseTimer = () => {
  isRunning.value = false
  clearInterval(timerInterval)
  localStorage.removeItem('tomato_end_time')
}

const resetTimer = () => {
  pauseTimer()
  timeLeft.value = TOTAL_TIME.value
}

const handleFinished = async () => {
  pauseTimer()
  // 计算刚刚专注了多少分钟
  const focusedMinutes = Math.round(TOTAL_TIME.value / 60)
  timeLeft.value = 0
  
  // 🌟 一步到位核心：倒计时结束瞬间，向后端数据库开火！
  try {
    await axios.post('/focus/add', {
      userId: JSON.parse(localStorage.getItem('user') || '{}').id,
      duration: focusedMinutes,
      focusDate: new Date().toISOString().split('T')[0] // 比如 2026-04-01
    })
  } catch (error) {
    console.error('专注记录保存到数据库失败', error)
  }
  
  if (Notification.permission === 'granted') {
    new Notification('🍅 专注达成！', { body: `太棒了，你深度专注了 ${focusedMinutes} 分钟！` })
  }
  ElNotification({
    title: '专注达成 🎉',
    message: `你成功完成了一次 ${focusedMinutes} 分钟的沉浸专注，已自动记入数据大屏！`,
    type: 'success',
    duration: 5000
  })
}

// 页面加载时的恢复机制
onMounted(() => {
  if ('Notification' in window && Notification.permission !== 'granted') {
    Notification.requestPermission()
  }

  const savedEndTime = localStorage.getItem('tomato_end_time')
  const savedTotalTime = localStorage.getItem('tomato_total_time')

  if (savedEndTime && savedTotalTime) {
    const remaining = Math.round((savedEndTime - Date.now()) / 1000)
    TOTAL_TIME.value = parseInt(savedTotalTime)
    customMinutes.value = Math.round(TOTAL_TIME.value / 60) // 恢复滑动条位置

    if (remaining > 0) {
      timeLeft.value = remaining
      isRunning.value = true
      runInterval()
    } else {
      handleFinished()
    }
  }
})

onUnmounted(() => {
  clearInterval(timerInterval)
})
</script>

<style scoped>
.focus-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 75vh;
  animation: fadeIn 0.8s ease-out;
}

/* 🌟 极致美感的毛玻璃卡片 */
.premium-glass-card {
  width: 480px;
  padding: 40px 20px;
  border-radius: 24px;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.05);
  text-align: center;
  transition: all 0.5s ease;
}

/* 专注状态下的高级呼吸光晕 */
.premium-glass-card.is-focusing {
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 0 40px rgba(52, 211, 153, 0.2), 0 20px 50px rgba(0, 0, 0, 0.05);
  transform: translateY(-5px);
}

.header {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin-bottom: 5px;
}

.icon {
  font-size: 28px;
  animation: bounce 2s infinite;
}

.title {
  color: #1f2937;
  font-size: 28px;
  font-weight: 900;
  letter-spacing: 1px;
  margin: 0;
}

.subtitle {
  color: #6b7280;
  font-size: 14px;
  margin-bottom: 30px;
  transition: color 0.3s;
}

.focus-text {
  color: #10b981;
  font-weight: bold;
  letter-spacing: 2px;
  animation: pulseText 2s infinite;
}

.timer-display-wrapper {
  margin: 20px 0 30px 0;
  transition: transform 0.5s ease;
}

.timer-display-wrapper.breathing {
  transform: scale(1.05);
}

.time-huge {
  font-size: 56px;
  font-weight: 900;
  color: #111827;
  font-family: 'Courier New', Courier, monospace;
  text-shadow: 2px 2px 4px rgba(0,0,0,0.05);
}

.time-label {
  font-size: 14px;
  color: #9ca3af;
  margin-top: 5px;
  letter-spacing: 3px;
  font-weight: bold;
}

/* 🌟 自定义滑动条区域 */
.settings-section {
  margin-bottom: 30px;
}

.slider-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
  padding: 0 20px;
  margin-bottom: 20px;
}

.limit-text {
  font-size: 12px;
  color: #9ca3af;
  font-weight: bold;
  white-space: nowrap;
}

.custom-slider {
  flex: 1;
}

/* 快捷药丸按钮 */
.quick-pills {
  display: flex;
  justify-content: center;
  gap: 12px;
}

.pill-btn {
  font-weight: bold;
  color: #4b5563;
  border-color: #e5e7eb;
  background: transparent;
  transition: all 0.3s;
}
.pill-btn:hover {
  background: #f3f4f6;
  border-color: #d1d5db;
  transform: translateY(-2px);
}

.controls-section {
  display: flex;
  justify-content: center;
  gap: 15px;
}

.action-btn {
  font-weight: bold;
  width: 140px;
  font-size: 16px;
  letter-spacing: 1px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.start-btn {
  background: linear-gradient(135deg, #34d399 0%, #10b981 100%);
  border: none;
  box-shadow: 0 10px 20px rgba(16, 185, 129, 0.3);
}

.start-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 15px 25px rgba(16, 185, 129, 0.4);
}

.reset-btn {
  width: 120px;
}

/* 🌟 Vue 隐藏动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.4s ease, transform 0.4s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-5px); }
}

@keyframes pulseText {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}
</style>
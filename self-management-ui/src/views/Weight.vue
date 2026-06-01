<template>
  <div class="weight-panel-container">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card shadow="hover" class="glass-box-card chart-card">
          <div class="card-header-flex">
            <h3 class="panel-title">📈 近一周体重追踪（7次记录）</h3>
            <el-button type="success" size="default" @click="openUploadDialog"> 录入今日数据 </el-button>
          </div>
          
          <div class="svg-chart-wrapper" v-if="tableData.length >= 2">
            <svg viewBox="0 0 400 200" class="svg-body">
              <line x1="30" y1="50" x2="370" y2="50" stroke="#ebeef5" stroke-dasharray="4" />
              <line x1="30" y1="110" x2="370" y2="110" stroke="#ebeef5" stroke-dasharray="4" />
              <line x1="30" y1="170" x2="370" y2="170" stroke="#ebeef5" stroke-dasharray="4" />
              
              <path :d="svgAreaPath" fill="url(#blue-gradient)" opacity="0.15" />
              <polyline :points="svgPoints" fill="none" stroke="#4FACFE" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" />
              
              <g v-for="(point, idx) in chartCoordinates" :key="idx">
                <circle :cx="point.x" :cy="point.y" r="5" fill="#ffffff" stroke="#4FACFE" stroke-width="2" />
                <text :x="point.x" :y="point.y - 10" text-anchor="middle" font-size="10" font-weight="bold" fill="#2b3674">
                  {{ point.weight }}kg
                </text>
                <text :x="point.x" :y="190" text-anchor="middle" font-size="9" fill="#a3aed1">
                  {{ point.date }}
                </text>
              </g>

              <defs>
                <linearGradient id="blue-gradient" x1="0%" y1="0%" x2="0%" y2="100%">
                  <stop offset="0%" stop-color="#4FACFE" />
                  <stop offset="100%" stop-color="#ffffff" stop-opacity="0" />
                </linearGradient>
              </defs>
            </svg>
          </div>
          <div class="empty-chart-holder" v-else>
            <el-empty description="连续上传至少2天的数据，即可完美生成动态变化趋势图哦！" :image-size="80" />
          </div>
        </el-card>

        <el-card shadow="hover" class="glass-box-card" style="margin-top: 20px;">
          <el-table :data="tableData" size="small" style="width: 100%;">
            <el-table-column prop="recordDate" label="记录日期" align="center" width="160" />
            <el-table-column prop="weight" label="体重数据 (kg)" align="center">
              <template #default="scope">
                <span class="weight-number-highlight">{{ scope.row.weight }} kg</span>
              </template>
            </el-table-column>
            <el-table-column label="状态判定" align="center" width="160">
              <template #default="scope">
                <el-tag :type="getBmiTagType(scope.row.weight)" size="small" effect="dark">
                  {{ getBmiText(scope.row.weight) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center" width="120">
              <template #default="scope">
                <el-button type="danger" size="small" link @click="handleDelete(scope.row.id)">撤销</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="8">
        <el-card shadow="hover" class="glass-box-card bmi-tool-card">
          <h3 class="panel-title">🧮 智能标准体重换算</h3>
          <p class="intro-desc">输入当前身高，自动推演契合中国居民膳食标准的健康底线：</p>
          
          <div class="input-inline-box">
            <el-input-number v-model="userHeight" :min="100" :max="250" size="default" style="width: 100%;" />
            <span class="unit-label">厘米 (cm)</span>
          </div>

          <div class="ideal-weight-result">
            <div class="result-row">
              <span class="res-lbl">标准完美体重：</span>
              <span class="res-val val-best">{{ ((userHeight - 100) * 0.9).toFixed(1) }} kg</span>
            </div>
            <div class="result-row">
              <span class="res-lbl">健康BMI范围：</span>
              <span class="res-val">{{ minHealthyWeight }}kg ~ {{ maxHealthyWeight }}kg</span>
            </div>
          </div>

          <div class="standard-reference-table">
            <div class="ref-title">📋 亚洲成人 BMI 参考评级标准</div>
            <div class="ref-row row-header">
              <span>分类评级</span><span>BMI 范围</span>
            </div>
            <div class="ref-row"><span>偏瘦 (消瘦型)</span><span>&lt; 18.5</span></div>
            <div class="ref-row active-normal"><span>正常 (健美标准)</span><span>18.5 ~ 23.9</span></div>
            <div class="ref-row"><span>超重 (偏重风险)</span><span>24.0 ~ 27.9</span></div>
            <div class="ref-row"><span>肥胖 (急需控重)</span><span>&gt;= 28.0</span></div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="dialogVisible" title="🏋️ 录入每日身体指标快照" width="380px" class="glass-dialog-box">
      <el-form :model="form" label-position="top">
        <el-form-item label="选择记录日期">
          <el-date-picker v-model="form.recordDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择日期" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="当前净重数据 (kg)">
          <el-input-number v-model="form.weight" :precision="2" :step="0.1" :min="30" :max="200" style="width: 100%;" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitWeight">确 认 提 交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const currentUser = ref(JSON.parse(localStorage.getItem('user') || '{}'))
const tableData = ref([])
const dialogVisible = ref(false)
const userHeight = ref(175) // 预设健康黄金基准身高

const form = ref({
  recordDate: new Date().toISOString().split('T')[0],
  weight: 65.0
})

// --- 🌟 智能健康指数计算核心 ---
const minHealthyWeight = computed(() => ((18.5 * Math.pow(userHeight.value / 100, 2))).toFixed(1))
const maxHealthyWeight = computed(() => ((23.9 * Math.pow(userHeight.value / 100, 2))).toFixed(1))

const getBmiText = (weight) => {
  const bmi = weight / Math.pow(userHeight.value / 100, 2)
  if (bmi < 18.5) return '身体偏瘦'
  if (bmi <= 23.9) return '指标正常'
  if (bmi <= 27.9) return '身体超重'
  return '中度肥胖'
}

const getBmiTagType = (weight) => {
  const bmi = weight / Math.pow(userHeight.value / 100, 2)
  if (bmi < 18.5) return 'info'
  if (bmi <= 23.9) return 'success'
  if (bmi <= 27.9) return 'warning'
  return 'danger'
}

// --- 🌟 原生 SVG 多维多点坐标缩放引擎 ---
const chartCoordinates = computed(() => {
  if (tableData.value.length === 0) return []
  const weights = tableData.value.map(d => d.weight)
  const minW = Math.min(...weights) - 1
  const maxW = Math.max(...weights) + 1
  const range = maxW - minW || 1

  return tableData.value.map((item, index) => {
    // 横坐标均匀平铺，纵坐标线性映射到 50 到 170 的 SVG 视窗高度内
    const x = tableData.value.length > 1 ? (index / (tableData.value.length - 1)) * 340 + 30 : 200
    const y = 170 - ((item.weight - minW) / range) * 120
    const mDate = item.recordDate.substring(5) // 只保留 MM-DD 格式
    return { x, y, weight: item.weight, date: mDate }
  })
})

const svgPoints = computed(() => {
  return chartCoordinates.value.map(p => `${p.x},${p.y}`).join(' ')
})

const svgAreaPath = computed(() => {
  if (chartCoordinates.value.length === 0) return ''
  const first = chartCoordinates.value[0]
  const last = chartCoordinates.value[chartCoordinates.value.length - 1]
  const polyPoints = chartCoordinates.value.map(p => `${p.x},${p.y}`).join(' ')
  return `M ${first.x} 170 L ${polyPoints} L ${last.x} 170 Z`
})

// --- 🌟 增删改查网络请求打通 ---
const loadWeightData = async () => {
  try {
    const res = await axios.get('/weight/list', { params: { userId: currentUser.value.id } })
    if (res.data.code === 200) tableData.value = res.data.data
  } catch (e) { ElMessage.error('体重数据加载失败') }
}

const openUploadDialog = () => {
  form.value = {
    recordDate: new Date().toISOString().split('T')[0],
    weight: tableData.value.length > 0 ? tableData.value[tableData.value.length - 1].weight : 65.0
  }
  dialogVisible.value = true
}

const submitWeight = async () => {
  if (!form.value.weight || !form.value.recordDate) return ElMessage.warning('信息未填全')
  form.value.userId = currentUser.value.id
  try {
    const res = await axios.post('/weight/upload', form.value)
    if (res.data.code === 200) {
      ElMessage.success('今日身体状态快照录入完成！')
      dialogVisible.value = false
      loadWeightData()
    }
  } catch (e) { ElMessage.error('上传失败') }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除本条体重打卡日志吗？', '确认撤销', { type: 'warning' }).then(async () => {
    const res = await axios.delete(`/weight/${id}`)
    if (res.data.code === 200) {
      ElMessage.success('已清空')
      loadWeightData()
    }
  }).catch(() => {})
}

onMounted(() => { loadWeightData() })
</script>

<style scoped>
.weight-panel-container { padding: 10px 20px; animation: slideUp 0.4s ease-out; }
.panel-title { font-size: 18px; color: #2b3674; font-weight: 800; margin: 0 0 15px 0; letter-spacing: 0.5px; }
.card-header-flex { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }

.glass-box-card {
  border-radius: 16px; background: rgba(255, 255, 255, 0.85); backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.6); box-shadow: 0 10px 30px rgba(112, 144, 176, 0.08);
}

.chart-card { min-height: 280px; }
.svg-chart-wrapper { width: 100%; padding: 10px 0; background: #fafcfe; border-radius: 12px; border: 1px solid #f0f4f8; }
.svg-body { width: 100%; height: auto; display: block; overflow: visible; }

.weight-number-highlight { font-weight: bold; font-size: 15px; color: #4FACFE; font-family: monospace; }
.intro-desc { font-size: 13px; color: #a3aed1; margin: 0 0 15px 0; line-height: 1.5; }

.input-inline-box { display: flex; align-items: center; gap: 10px; margin-bottom: 20px; }
.unit-label { font-size: 13px; color: #2b3674; font-weight: bold; white-space: nowrap; }

.ideal-weight-result { background: linear-gradient(135deg, #f4f7fe 0%, #eef2f7 100%); padding: 15px; border-radius: 12px; margin-bottom: 20px; }
.result-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; font-size: 14px; }
.result-row:last-child { margin-bottom: 0; }
.res-lbl { color: #576574; font-weight: 500; }
.res-val { color: #2b3674; font-weight: bold; }
.val-best { color: #2ed573; font-size: 16px; font-weight: 800; }

.standard-reference-table { border: 1px solid #e2e8f0; border-radius: 10px; overflow: hidden; font-size: 12px; }
.ref-title { background: #f4f7fe; padding: 10px; text-align: center; color: #2b3674; font-weight: bold; border-bottom: 1px solid #e2e8f0; }
.ref-row { display: flex; justify-content: space-between; padding: 8px 12px; color: #576574; border-bottom: 1px solid #f1f5f9; }
.ref-row:last-child { border-bottom: none; }
.row-header { font-weight: bold; color: #2b3674; background: #fafbfc; }
.active-normal { background: rgba(46, 213, 115, 0.1); color: #2ed573; font-weight: bold; }

@keyframes slideUp { from { opacity: 0; transform: translateY(12px); } to { opacity: 1; transform: translateY(0); } }
</style>
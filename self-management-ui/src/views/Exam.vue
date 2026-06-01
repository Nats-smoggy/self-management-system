<template>
  <div class="exam-container">
    <div class="countdown-section">
      <h3 class="section-title">🔥 官方大考倒计时</h3>
      <el-row :gutter="20">
        <el-col :span="12" v-for="(exam, index) in officialExams" :key="index">
          <el-card shadow="hover" class="glass-card countdown-card" :class="'theme-' + index">
            <div class="exam-icon">{{ exam.icon }}</div>
            <div class="exam-info">
              <h2 class="exam-name">{{ exam.name }}</h2>
              <p class="exam-date">预计开考日期：{{ exam.date }}</p>
            </div>
            <div class="days-box">
              <span class="days-num">{{ exam.daysLeft }}</span>
              <span class="days-text">天</span>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <el-card shadow="hover" class="glass-card table-section">
      <div class="header-actions">
        <h3 class="section-title" style="margin: 0;">📝 我的专属备考计划</h3>
        <el-button type="primary" size="large" @click="handleAdd"> + 新增考试 </el-button>
      </div>

      <el-table :data="tableData" style="width: 100%; border-radius: 10px; overflow: hidden;"
        :header-cell-style="{ background: '#f4f6f9', color: '#34495e', fontWeight: 'bold' }">
        <el-table-column prop="examName" label="考试/考证名称" width="220">
          <template #default="scope">
            <strong style="color: #2c3e50; font-size: 15px;">{{ scope.row.examName }}</strong>
          </template>
        </el-table-column>
        
        <el-table-column prop="examDate" label="考试日期" width="150" align="center">
          <template #default="scope">
            <el-tag type="warning" effect="dark" round style="font-weight: bold; font-family: monospace;">
              {{ scope.row.examDate }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="倒计时" width="120" align="center">
          <template #default="scope">
            <span :style="{ color: getDaysLeft(scope.row.examDate) <= 30 ? '#ff4757' : '#2ed573', fontWeight: '900', fontSize: '18px' }">
              {{ getDaysLeft(scope.row.examDate) }} 天
            </span>
          </template>
        </el-table-column>

        <el-table-column prop="remark" label="备考宣言 / 备注" min-width="200" />
        
        <el-table-column label="操作" width="180" align="center">
          <template #default="scope">
            <el-button size="small" type="primary" plain @click="handleEdit(scope.row)">调整</el-button>
            <el-button size="small" type="danger" plain @click="handleDelete(scope.row.id)">考完删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="450px" class="glass-dialog">
      <el-form :model="form" label-width="80px">
        <el-form-item label="考试名称">
          <el-input v-model="form.examName" placeholder="例如：计算机二级 / 期末考试" />
        </el-form-item>
        
        <el-form-item label="考试日期">
          <el-date-picker v-model="form.examDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择宣战日期" style="width: 100%;" />
        </el-form-item>
        
        <el-form-item label="备考备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="写一句鼓励自己的话吧！" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">怂了(取消)</el-button>
        <el-button type="primary" @click="saveExam">宣战(保存)</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const currentUser = ref(JSON.parse(localStorage.getItem('user') || '{}'))
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增考试计划')
const form = ref({})

// --- 🌟 1. 智能算天数魔法 ---
const getDaysLeft = (targetDateStr) => {
  if (!targetDateStr) return 0
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  const target = new Date(targetDateStr)
  const diffTime = target - today
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  return diffDays > 0 ? diffDays : 0
}

// 自动计算四六级和教资的下一个考试时间 (基于历年规律动态计算)
const calculateOfficialExams = () => {
  const today = new Date()
  const year = today.getFullYear()
  
  // 假定：四六级通常在 6月15日 和 12月15日 左右
  let cetDate = new Date(`${year}-06-15`)
  if (today > cetDate) cetDate = new Date(`${year}-12-15`)
  if (today > cetDate) cetDate = new Date(`${year + 1}-06-15`)

  // 假定：教资笔试通常在 3月10日 和 10月30日 左右
  let ntceDate = new Date(`${year}-03-10`)
  if (today > ntceDate) ntceDate = new Date(`${year}-10-30`)
  if (today > ntceDate) ntceDate = new Date(`${year + 1}-03-10`)

  return [
    { 
      name: '全国大学英语四、六级考试 (CET)', 
      date: cetDate.toISOString().split('T')[0], 
      daysLeft: getDaysLeft(cetDate.toISOString().split('T')[0]),
      icon: '🇬🇧'
    },
    { 
      name: '中小学教师资格考试 (NTCE笔试)', 
      date: ntceDate.toISOString().split('T')[0], 
      daysLeft: getDaysLeft(ntceDate.toISOString().split('T')[0]),
      icon: '👩‍🏫'
    }
  ]
}
const officialExams = ref(calculateOfficialExams())

// --- 🌟 2. 个人考试 CRUD ---
const loadData = async () => {
  try {
    const res = await axios.get('/exam/list', { params: { userId: currentUser.value.id } })
    if (res.data.code === 200) tableData.value = res.data.data
  } catch (error) { ElMessage.error('获取考试计划失败') }
}

const handleAdd = () => {
  dialogTitle.value = '新增考试计划'
  form.value = { examName: '', examDate: '', remark: '' }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '调整考试计划'
  form.value = { ...row }
  dialogVisible.value = true
}

const saveExam = async () => {
  if (!form.value.examName || !form.value.examDate) return ElMessage.warning('考试名称和日期必须填写！')
  form.value.userId = currentUser.value.id 
  try {
    const url = form.value.id ? '/exam/update' : '/exam/add'
    const method = form.value.id ? 'put' : 'post'
    const res = await axios[method](url, form.value)
    if (res.data.code === 200) {
      ElMessage.success('目标已锁定，全力备考吧！')
      dialogVisible.value = false
      loadData()
    }
  } catch (error) { ElMessage.error('保存失败') }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('考完了？确定要删除这条记录吗？', '确认', { type: 'warning' }).then(async () => {
    const res = await axios.delete(`/exam/${id}`)
    if (res.data.code === 200) {
      ElMessage.success('已删除，祝你逢考必过！')
      loadData()
    }
  }).catch(() => {})
}

onMounted(() => { loadData() })
</script>

<style scoped>
.exam-container { padding: 20px; animation: fadeIn 0.5s ease-out; }
.section-title { font-size: 20px; color: #2c3e50; font-weight: 800; margin-bottom: 15px; letter-spacing: 1px; }

.glass-card {
  border-radius: 16px; background: rgba(255, 255, 255, 0.7); backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.5); box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
}

.countdown-section { margin-bottom: 25px; }

.countdown-card {
  display: flex; align-items: center; justify-content: space-between; padding: 25px 30px; transition: transform 0.3s; cursor: default;
}
.countdown-card:hover { transform: translateY(-5px); }

/* 四六级专属渐变色 */
.theme-0 { background: linear-gradient(135deg, rgba(161, 196, 253, 0.8) 0%, rgba(194, 233, 251, 0.8) 100%); border-color: #a1c4fd; }
/* 教资专属渐变色 */
.theme-1 { background: linear-gradient(135deg, rgba(255, 201, 201, 0.8) 0%, rgba(255, 226, 226, 0.8) 100%); border-color: #ffc9c9; }

.exam-icon { font-size: 45px; margin-right: 20px; }
.exam-info { flex: 1; }
.exam-name { margin: 0 0 5px 0; font-size: 20px; color: #2c3e50; font-weight: 800; }
.exam-date { margin: 0; font-size: 13px; color: #576574; }

.days-box { text-align: center; background: rgba(255,255,255,0.6); padding: 10px 20px; border-radius: 12px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); }
.days-num { font-size: 36px; font-weight: 900; color: #ff4757; font-family: monospace; }
.days-text { font-size: 14px; color: #2c3e50; font-weight: bold; margin-left: 5px; }

.table-section { padding-bottom: 10px; }
.header-actions { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }

@keyframes fadeIn { from { opacity: 0; transform: translateY(15px); } to { opacity: 1; transform: translateY(0); } }
</style>
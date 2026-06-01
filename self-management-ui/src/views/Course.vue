<template>
  <div class="course-container">
    <el-card shadow="hover" class="glass-card">
      <div class="header-actions">
        <h3 class="title">📅 我的超级课程表</h3>
        
        <div class="button-group">
          <el-button type="success" size="large" plain @click="handleAddManual" style="margin-right: 15px;">
            ➕ 手动添加
          </el-button>

          <el-upload
            :action="uploadUrl"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
            :data="{ userId: currentUser.id }" 
            accept=".xlsx, .xls"
            style="display: inline-block; margin-right: 15px;"
          >
            <el-button type="primary" size="large" class="upload-btn" :loading="uploading">
              ☁️ 一键导入 Excel
            </el-button>
          </el-upload>

          <el-button type="danger" size="large" plain class="clear-btn" @click="handleClear">
            🗑️ 清空课表
          </el-button>
        </div>
      </div>

      <el-table 
        :data="tableData" 
        style="width: 100%; margin-top: 15px; border-radius: 10px; overflow: hidden;"
        :row-style="{ height: '105px' }"
        :header-cell-style="{ background: '#f4f6f9', color: '#34495e', fontWeight: 'bold', fontSize: '15px' }"
      >
        <el-table-column prop="period" label="节次 \ 星期" width="110" align="center" fixed="left">
          <template #default="scope">
            <div class="period-label">{{ scope.row.period }}</div>
          </template>
        </el-table-column>
        
        <el-table-column 
          v-for="(day, index) in daysConfig" 
          :key="index" 
          :prop="day.prop" 
          :label="day.label" 
          align="center" 
          min-width="140"
        >
          <template #default="scope">
            <div 
              v-if="scope.row[day.prop]" 
              class="course-card" 
              :style="{ backgroundColor: getCourseColor(scope.row[day.prop].courseName) }"
              @click="handleCardClick(scope.row[day.prop])"
            >
              <div class="course-name">{{ scope.row[day.prop].courseName }}</div>
              <div class="course-info">📍 {{ scope.row[day.prop].location || '未定' }}</div>
              <div class="course-info">👨‍🏫 {{ scope.row[day.prop].teacher || '暂无' }}</div>
            </div>
            <div v-else class="empty-slot"></div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="450px" class="glass-dialog">
      <el-form :model="courseForm" label-width="80px">
        <el-form-item label="课程名称">
          <el-input v-model="courseForm.courseName" placeholder="例如：高级软件工程" />
        </el-form-item>
        
        <el-form-item label="上课时间">
          <el-col :span="11">
            <el-select v-model="courseForm.dayOfWeek" placeholder="星期几" style="width: 100%;">
              <el-option label="星期一" :value="1" />
              <el-option label="星期二" :value="2" />
              <el-option label="星期三" :value="3" />
              <el-option label="星期四" :value="4" />
              <el-option label="星期五" :value="5" />
              <el-option label="星期六" :value="6" />
              <el-option label="星期日" :value="7" />
            </el-select>
          </el-col>
          <el-col :span="2" style="text-align: center; color: #999;">-</el-col>
          <el-col :span="11">
            <el-select v-model="courseForm.section" placeholder="第几节" style="width: 100%;">
              <el-option v-for="i in 10" :key="i" :label="`第 ${i} 节`" :value="i" />
            </el-select>
          </el-col>
        </el-form-item>
        
        <el-form-item label="上课地点">
          <el-input v-model="courseForm.location" placeholder="例如：文昌校区主楼 A101" />
        </el-form-item>
        
        <el-form-item label="任课教师">
          <el-input v-model="courseForm.teacher" placeholder="例如：张强教授" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <div style="display: flex; justify-content: space-between;">
          <el-button v-if="courseForm.id" type="danger" plain @click="deleteCourse(courseForm.id)">删 除</el-button>
          <div v-else></div> <div>
            <el-button @click="dialogVisible = false">取 消</el-button>
            <el-button type="primary" @click="saveCourse">保 存</el-button>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

// --- 🌟 1. 基础数据准备 ---
const currentUser = ref(JSON.parse(localStorage.getItem('user') || '{}'))
const uploadUrl = ref(axios.defaults.baseURL + '/course/import')
const tableData = ref([])
const uploading = ref(false)

// 弹窗控制状态
const dialogVisible = ref(false)
const dialogTitle = ref('手动添加课程')
const courseForm = ref({})

// 星期配置数组
const daysConfig = [
  { prop: 'mon', label: '星期一' }, { prop: 'tue', label: '星期二' },
  { prop: 'wed', label: '星期三' }, { prop: 'thu', label: '星期四' },
  { prop: 'fri', label: '星期五' }, { prop: 'sat', label: '星期六' },
  { prop: 'sun', label: '星期日' }
]

// 超级马卡龙色盘
const colorPalette = [
  '#E8F4FF', '#E6FFFB', '#F6FFED', '#FFF2E8', '#FFF0F6', 
  '#F0F5FF', '#F9F0FF', '#FCF0E3', '#E0F2F1', '#FFF9C4'
]

// 魔法取色器
const getCourseColor = (name) => {
  if (!name) return '#f0f0f0'
  let hash = 0
  for (let i = 0; i < name.length; i++) {
    hash = name.charCodeAt(i) + ((hash << 5) - hash)
  }
  const index = Math.abs(hash) % colorPalette.length
  return colorPalette[index]
}


// --- 🌟 2. 核心数据加载与网格渲染 ---
const loadData = async () => {
  try {
    const res = await axios.get('/course/list', { params: { userId: currentUser.value.id } })
    if (res.data.code === 200 && res.data.data.length > 0) {
      formatTableData(res.data.data) 
    } else {
      initEmptyTable()
    }
  } catch (error) {
    ElMessage.error('获取课表失败，请检查网络')
    initEmptyTable()
  }
}

// 扁平数据转 10x7 网格
const formatTableData = (rawCourses) => {
  const matrix = []
  for (let i = 1; i <= 10; i++) {
    matrix.push({ period: `第 ${i} 节`, mon: null, tue: null, wed: null, thu: null, fri: null, sat: null, sun: null })
  }
  const dayMap = { 1: 'mon', 2: 'tue', 3: 'wed', 4: 'thu', 5: 'fri', 6: 'sat', 7: 'sun' }
  rawCourses.forEach(course => {
    const rowIndex = course.section - 1 
    const colKey = dayMap[course.dayOfWeek] 
    if (rowIndex >= 0 && rowIndex < 10 && colKey) {
      matrix[rowIndex][colKey] = course // 把整个课程对象塞进格子里
    }
  })
  tableData.value = matrix
}

const initEmptyTable = () => {
  const emptyTable = []
  for (let i = 1; i <= 10; i++) {
    emptyTable.push({ period: `第 ${i} 节`, mon: null, tue: null, wed: null, thu: null, fri: null, sat: null, sun: null })
  }
  tableData.value = emptyTable
}


// --- 🌟 3. Excel 导入与清空 ---
const beforeUpload = (file) => {
  const isExcel = file.name.endsWith('.xlsx') || file.name.endsWith('.xls')
  if (!isExcel) {
    ElMessage.warning('只能上传 Excel 文件哦！')
    return false
  }
  uploading.value = true 
  return true
}

const handleUploadSuccess = (res) => {
  uploading.value = false
  if (res.code === 200) {
    ElMessage.success('🎉 课表导入成功！')
    loadData() 
  } else {
    ElMessage.error(res.msg || '导入失败，请检查 Excel 格式')
  }
}

const handleUploadError = () => {
  uploading.value = false
  ElMessage.error('糟糕，连接后端失败！')
}

const handleClear = () => {
  ElMessageBox.confirm('这将会清空你所有的课程安排，确定要删库跑路吗？', '高危操作提醒', {
    confirmButtonText: '确定清空',
    cancelButtonText: '手滑了',
    type: 'error',
  }).then(async () => {
    try {
      const res = await axios.delete(`/course/clear/${currentUser.value.id}`)
      if (res.data.code === 200) {
        ElMessage.success('课表已清空')
        initEmptyTable() 
      }
    } catch (error) {
      ElMessage.error('清空失败！')
    }
  }).catch(() => {})
}


// --- 🌟 4. 单独增删改查逻辑 ---
const handleAddManual = () => {
  dialogTitle.value = '➕ 手动添加课程'
  courseForm.value = { courseName: '', dayOfWeek: 1, section: 1, location: '', teacher: '' }
  dialogVisible.value = true
}

const handleCardClick = (course) => {
  dialogTitle.value = '✏️ 修改或删除课程'
  courseForm.value = JSON.parse(JSON.stringify(course)) // 深拷贝，避免双向绑定影响原视图
  dialogVisible.value = true
}

const saveCourse = async () => {
  if (!courseForm.value.courseName) return ElMessage.warning('课程名称不能为空！')
  courseForm.value.userId = currentUser.value.id 

  try {
    const url = courseForm.value.id ? '/course/update' : '/course/add'
    const method = courseForm.value.id ? 'put' : 'post'
    const res = await axios[method](url, courseForm.value)
    
    if (res.data.code === 200) {
      ElMessage.success('保存成功！')
      dialogVisible.value = false
      loadData() 
    }
  } catch (error) {
    ElMessage.error('保存失败，请检查网络！')
  }
}

const deleteCourse = (id) => {
  ElMessageBox.confirm('确定要删除这节课吗？', '提示', { type: 'warning' }).then(async () => {
    try {
      const res = await axios.delete(`/course/${id}`)
      if (res.data.code === 200) {
        ElMessage.success('删除成功！')
        dialogVisible.value = false
        loadData() 
      }
    } catch (error) {
      ElMessage.error('删除失败！')
    }
  }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.course-container {
  padding: 20px;
  animation: fadeIn 0.5s ease-out;
}

.glass-card {
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(12px);
  border: 1px solid rgba(255, 255, 255, 0.5);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05);
}

.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.title {
  font-size: 22px;
  color: #2c3e50;
  font-weight: 800;
  letter-spacing: 1px;
}

.upload-btn { border-radius: 8px; font-weight: bold; box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3); }
.clear-btn { border-radius: 8px; font-weight: bold; }

.period-label {
  font-weight: bold;
  color: #7f8c8d;
  font-size: 14px;
}

/* 🌟 核心：课程卡片美化 */
.course-card {
  padding: 8px;
  border-radius: 10px;
  text-align: left;
  line-height: 1.3;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.03);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  cursor: pointer;
  border: 1px solid rgba(255, 255, 255, 0.6);
  height: 90px; /* 固定高度确保网格整齐 */
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.course-card:hover {
  transform: translateY(-4px) scale(1.03);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  filter: brightness(0.95);
  z-index: 10;
}

.course-name {
  font-weight: 900;
  font-size: 13px;
  color: #2c3e50;
  margin-bottom: 4px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-info {
  font-size: 11px;
  color: #5a6a7a;
  margin-top: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-weight: 500;
}

.empty-slot {
  height: 100%;
  width: 100%;
  border-radius: 10px;
  transition: background 0.3s;
}
.empty-slot:hover {
  background-color: #fafbfc;
}

/* 去除 Element UI 默认的边框 */
:deep(.el-table--border) { border: none; }
:deep(.el-table td.el-table__cell, .el-table th.el-table__cell.is-leaf) {
  border-bottom: 1px dashed #ebeef5;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(15px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
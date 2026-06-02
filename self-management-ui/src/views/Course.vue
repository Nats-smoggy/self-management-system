<template>
  <div class="course-container">
    <el-card shadow="hover" class="glass-card">
      <div class="header-actions">
        <h3 class="title">📘 我的课程表</h3>

        <div class="button-group">
          <el-button type="success" size="large" plain @click="handleAddManual" style="margin-right: 15px;">
            ➕ 手动添加
          </el-button>

          <el-upload
            :show-file-list="false"
            :before-upload="beforeUpload"
            :http-request="handleLocalImport"
            accept=".xlsx,.xls,.csv"
            style="display: inline-block; margin-right: 15px;"
          >
            <el-button type="primary" size="large" class="upload-btn" :loading="uploading">
              📂 本地导入 Excel
            </el-button>
          </el-upload>

          <el-button type="danger" size="large" plain class="clear-btn" @click="handleClear">
            🧹 清空课表
          </el-button>
        </div>
      </div>

      <el-table
        :data="tableData"
        style="width: 100%; margin-top: 15px; border-radius: 10px; overflow: hidden;"
        :row-style="{ height: '105px' }"
        :header-cell-style="{ background: '#f4f6f9', color: '#34495e', fontWeight: 'bold', fontSize: '15px' }"
      >
        <el-table-column prop="period" label="节次 / 星期" width="110" align="center" fixed="left">
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
              <div class="course-info">👩‍🏫 {{ scope.row[day.prop].teacher || '暂无' }}</div>
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
          <el-input v-model="courseForm.teacher" placeholder="例如：张老师" />
        </el-form-item>
      </el-form>

      <template #footer>
        <div style="display: flex; justify-content: space-between;">
          <el-button v-if="courseForm.id" type="danger" plain @click="deleteCourse(courseForm.id)">删除</el-button>
          <div v-else></div>
          <div>
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="saveCourse">保存</el-button>
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
import * as XLSX from 'xlsx'

const currentUser = ref(JSON.parse(localStorage.getItem('user') || '{}'))
const tableData = ref([])
const uploading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('手动添加课程')
const courseForm = ref({})

const daysConfig = [
  { prop: 'mon', label: '星期一' }, { prop: 'tue', label: '星期二' },
  { prop: 'wed', label: '星期三' }, { prop: 'thu', label: '星期四' },
  { prop: 'fri', label: '星期五' }, { prop: 'sat', label: '星期六' },
  { prop: 'sun', label: '星期日' }
]

const colorPalette = ['#E8F4FF', '#E6FFFB', '#F6FFED', '#FFF2E8', '#FFF0F6', '#F0F5FF', '#F9F0FF', '#FCF0E3', '#E0F2F1', '#FFF9C4']

const getStoreKey = () => `self-management-courses-${currentUser.value.id || 'guest'}`
const readCourses = () => {
  try { return JSON.parse(localStorage.getItem(getStoreKey()) || '[]') } catch { return [] }
}
const writeCourses = (courses) => localStorage.setItem(getStoreKey(), JSON.stringify(courses))

const getCourseColor = (name) => {
  if (!name) return '#f0f0f0'
  let hash = 0
  for (let i = 0; i < name.length; i++) hash = name.charCodeAt(i) + ((hash << 5) - hash)
  return colorPalette[Math.abs(hash) % colorPalette.length]
}

const initEmptyTable = () => {
  tableData.value = Array.from({ length: 10 }, (_, index) => ({ period: `第 ${index + 1} 节`, mon: null, tue: null, wed: null, thu: null, fri: null, sat: null, sun: null }))
}

const formatTableData = (rawCourses) => {
  const matrix = Array.from({ length: 10 }, (_, index) => ({ period: `第 ${index + 1} 节`, mon: null, tue: null, wed: null, thu: null, fri: null, sat: null, sun: null }))
  const dayMap = { 1: 'mon', 2: 'tue', 3: 'wed', 4: 'thu', 5: 'fri', 6: 'sat', 7: 'sun' }
  rawCourses.forEach(course => {
    const rowIndex = Number(course.section) - 1
    const colKey = dayMap[Number(course.dayOfWeek)]
    if (rowIndex >= 0 && rowIndex < 10 && colKey) matrix[rowIndex][colKey] = course
  })
  tableData.value = matrix
}

const loadData = async () => {
  try {
    const res = await axios.get('/course/list', { params: { userId: currentUser.value.id } })
    if (res.data.code === 200) {
      const courses = res.data.data || []
      writeCourses(courses)
      formatTableData(courses)
      return
    }
    throw new Error(res.data.msg || 'load failed')
  } catch {
    const localCourses = readCourses()
    if (localCourses.length) formatTableData(localCourses)
    else initEmptyTable()
  }
}

const persistCourse = (course) => {
  const courses = readCourses()
  const normalized = { ...course, id: course.id ?? Date.now(), userId: currentUser.value.id }
  const index = courses.findIndex(item => Number(item.id) === Number(normalized.id))
  if (index >= 0) courses[index] = { ...courses[index], ...normalized }
  else courses.push(normalized)
  writeCourses(courses)
  formatTableData(courses)
}

const removeCourseLocal = (id) => {
  const courses = readCourses().filter(item => Number(item.id) !== Number(id))
  writeCourses(courses)
  formatTableData(courses)
}

const beforeUpload = (file) => {
  const fileName = file.name.toLowerCase()
  const isExcel = fileName.endsWith('.xlsx') || fileName.endsWith('.xls') || fileName.endsWith('.csv')
  if (!isExcel) {
    ElMessage.warning('只能上传 Excel 或 CSV 文件')
    return false
  }
  uploading.value = true
  return true
}

const parseWorkbook = (arrayBuffer, fileName) => {
  const workbook = XLSX.read(arrayBuffer, { type: 'array' })
  const sheet = workbook.Sheets[workbook.SheetNames[0]]
  const rows = XLSX.utils.sheet_to_json(sheet, { defval: '' })

  const dayAlias = {
    monday: 1, mon: 1, 星期一: 1, 周一: 1,
    tuesday: 2, tue: 2, 星期二: 2, 周二: 2,
    wednesday: 3, wed: 3, 星期三: 3, 周三: 3,
    thursday: 4, thu: 4, 星期四: 4, 周四: 4,
    friday: 5, fri: 5, 星期五: 5, 周五: 5,
    saturday: 6, sat: 6, 星期六: 6, 周六: 6,
    sunday: 7, sun: 7, 星期日: 7, 周日: 7
  }

  return rows.map((row, index) => {
    const dayRaw = row.dayOfWeek ?? row.day ?? row.weekday ?? row.星期 ?? row.星期几 ?? row['星期'] ?? row['dayOfWeek']
    const dayOfWeek = typeof dayRaw === 'number' ? dayRaw : dayAlias[String(dayRaw || '').trim().toLowerCase()] || 1
    const section = Number(row.section ?? row.period ?? row.节次 ?? row['节次'] ?? index + 1)
    return {
      id: row.id ? Number(row.id) : Date.now() + index,
      userId: currentUser.value.id,
      courseName: row.courseName ?? row.course ?? row.name ?? row['课程名称'] ?? row['课程'] ?? '',
      dayOfWeek,
      section,
      location: row.location ?? row.place ?? row['地点'] ?? '',
      teacher: row.teacher ?? row['教师'] ?? row['老师'] ?? ''
    }
  }).filter(item => item.courseName)
}

const handleLocalImport = async (options) => {
  try {
    const file = options.file
    const buffer = await file.arrayBuffer()
    const imported = parseWorkbook(buffer, file.name)

    if (!imported.length) {
      ElMessage.warning('表格里没有识别到可用课程，请检查列名')
      return
    }

    const existing = readCourses()
    const merged = [...existing]
    imported.forEach(course => {
      const index = merged.findIndex(item => item.dayOfWeek === course.dayOfWeek && item.section === course.section)
      if (index >= 0) merged[index] = { ...merged[index], ...course }
      else merged.push(course)
    })
    writeCourses(merged)
    formatTableData(merged)
    ElMessage.success(`导入成功，新增/更新 ${imported.length} 条课程`)
  } catch (error) {
    ElMessage.error('导入失败，请确认文件格式是否正确')
  } finally {
    uploading.value = false
  }
}

const handleClear = () => {
  ElMessageBox.confirm('这将清空你所有的课程安排，确定要继续吗？', '高危操作提醒', {
    confirmButtonText: '确定清空',
    cancelButtonText: '再想想',
    type: 'error'
  }).then(async () => {
    try {
      const res = await axios.delete(`/course/clear/${currentUser.value.id}`)
      if (res.data.code === 200) {
        writeCourses([])
        initEmptyTable()
        ElMessage.success('课表已清空')
        return
      }
      throw new Error(res.data.msg || 'clear failed')
    } catch {
      writeCourses([])
      initEmptyTable()
      ElMessage.success('已清空本地课表')
    }
  }).catch(() => {})
}

const handleAddManual = () => {
  dialogTitle.value = '手动添加课程'
  courseForm.value = { courseName: '', dayOfWeek: 1, section: 1, location: '', teacher: '' }
  dialogVisible.value = true
}

const handleCardClick = (course) => {
  dialogTitle.value = '修改课程'
  courseForm.value = JSON.parse(JSON.stringify(course))
  dialogVisible.value = true
}

const saveCourse = async () => {
  if (!courseForm.value.courseName) return ElMessage.warning('课程名称不能为空')
  courseForm.value.userId = currentUser.value.id
  try {
    const url = courseForm.value.id ? '/course/update' : '/course/add'
    const method = courseForm.value.id ? 'put' : 'post'
    const res = await axios[method](url, courseForm.value)
    if (res.data.code === 200) {
      writeCourses(readCourses().map(item => item.id === courseForm.value.id ? { ...item, ...courseForm.value } : item))
      dialogVisible.value = false
      loadData()
      ElMessage.success('保存成功')
      return
    }
    throw new Error(res.data.msg || 'save failed')
  } catch {
    persistCourse(courseForm.value)
    dialogVisible.value = false
    ElMessage.success('已保存到本地课表')
  }
}

const deleteCourse = (id) => {
  ElMessageBox.confirm('确定要删除这节课吗？', '提示', { type: 'warning' }).then(async () => {
    try {
      const res = await axios.delete(`/course/${id}`)
      if (res.data.code === 200) {
        removeCourseLocal(id)
        dialogVisible.value = false
        ElMessage.success('删除成功')
        return
      }
      throw new Error(res.data.msg || 'delete failed')
    } catch {
      removeCourseLocal(id)
      dialogVisible.value = false
      ElMessage.success('已从本地课表删除')
    }
  }).catch(() => {})
}

onMounted(() => { loadData() })
</script>

<style scoped>
.course-container { padding: 20px; animation: fadeIn 0.5s ease-out; }
.glass-card { border-radius: 20px; background: rgba(255, 255, 255, 0.85); backdrop-filter: blur(12px); border: 1px solid rgba(255, 255, 255, 0.5); box-shadow: 0 10px 30px rgba(0, 0, 0, 0.05); }
.header-actions { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
.title { font-size: 22px; color: #2c3e50; font-weight: 800; letter-spacing: 1px; }
.upload-btn, .clear-btn { border-radius: 8px; font-weight: bold; }
.period-label { font-weight: bold; color: #7f8c8d; font-size: 14px; }
.course-card { padding: 8px; border-radius: 10px; text-align: left; line-height: 1.3; box-shadow: 0 4px 10px rgba(0,0,0,0.03); transition: all 0.3s ease; cursor: pointer; border: 1px solid rgba(255,255,255,0.6); height: 90px; box-sizing: border-box; display: flex; flex-direction: column; justify-content: center; }
.course-card:hover { transform: translateY(-4px) scale(1.03); box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1); filter: brightness(0.95); z-index: 10; }
.course-name { font-weight: 900; font-size: 13px; color: #2c3e50; margin-bottom: 4px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.course-info { font-size: 11px; color: #5a6a7a; margin-top: 2px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; font-weight: 500; }
.empty-slot { height: 100%; width: 100%; border-radius: 10px; transition: background 0.3s; }
.empty-slot:hover { background-color: #fafbfc; }
:deep(.el-table--border) { border: none; }
:deep(.el-table td.el-table__cell, .el-table th.el-table__cell.is-leaf) { border-bottom: 1px dashed #ebeef5; }
@keyframes fadeIn { from { opacity: 0; transform: translateY(15px); } to { opacity: 1; transform: translateY(0); } }
</style>

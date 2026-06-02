<template>
  <div style="padding: 20px; animation: fadeIn 0.5s ease-out;">
    <el-card shadow="hover" style="border-radius: 15px; border: 1px solid rgba(255,255,255,0.5);">
      <div style="margin-bottom: 20px; display: flex; justify-content: space-between; align-items: center;">
        <h3 style="margin: 0; color: #2c3e50; font-weight: 800;">🎯 我的目标计划</h3>
        <el-button type="primary" size="large" @click="handleAdd" style="border-radius: 8px; font-weight: bold;">
          + 新建计划
        </el-button>
      </div>

      <el-table :data="tableData" border stripe style="width: 100%; border-radius: 8px; overflow: hidden;">
        <el-table-column type="index" label="序号" width="70" align="center" />

        <el-table-column prop="title" label="计划名称" width="200">
          <template #default="scope">
            <strong style="color: #34495e;">{{ scope.row.title }}</strong>
          </template>
        </el-table-column>

        <el-table-column prop="content" label="具体内容" min-width="200" />

        <el-table-column prop="deadline" label="截止日期" width="150" align="center">
          <template #default="scope">
            <span v-if="scope.row.deadline" style="color: #e67e22; font-weight: bold; font-family: monospace; font-size: 14px;">
              📮 {{ scope.row.deadline }}
            </span>
            <span v-else style="color: #bdc3c7; font-size: 13px;">未设置</span>
          </template>
        </el-table-column>

        <el-table-column prop="status" label="当前状态" width="120" align="center">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)" effect="dark" style="font-weight: bold;">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="240" align="center">
          <template #default="scope">
            <el-button
              size="small"
              type="warning"
              plain
              v-if="scope.row.status === '未开始'"
              @click="changeStatus(scope.row, '进行中')"
            >
              🚀 开始
            </el-button>

            <el-button
              size="small"
              type="success"
              plain
              v-if="scope.row.status === '进行中'"
              @click="changeStatus(scope.row, '已完成')"
            >
              ✔ 完成
            </el-button>

            <el-button size="small" type="primary" plain @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" plain @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="计划名称">
          <el-input v-model="form.title" placeholder="例如：今天背一百个单词" />
        </el-form-item>

        <el-form-item label="具体内容">
          <el-input v-model="form.content" type="textarea" :rows="3" placeholder="详细描述一下你的计划..." />
        </el-form-item>

        <el-form-item label="截止日期">
          <el-date-picker
            v-model="form.deadline"
            type="date"
            placeholder="请选择 Deadline"
            value-format="YYYY-MM-DD"
            style="width: 100%;"
          />
        </el-form-item>

        <el-form-item label="当前状态">
          <el-radio-group v-model="form.status">
            <el-radio-button value="未开始" label="未开始" />
            <el-radio-button value="进行中" label="进行中" />
            <el-radio-button value="已完成" label="已完成" />
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="savePlan">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import axios from 'axios'
import { ElMessage, ElMessageBox } from 'element-plus'

const currentUser = ref(JSON.parse(localStorage.getItem('user') || '{}'))
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新建计划')
const form = ref({})

const planStoreKey = computed(() => `self-management-plans-${currentUser.value.id || 'guest'}`)

const getLocalPlans = () => {
  try {
    return JSON.parse(localStorage.getItem(planStoreKey.value) || '[]')
  } catch {
    return []
  }
}

const setLocalPlans = (plans) => {
  localStorage.setItem(planStoreKey.value, JSON.stringify(plans))
}

const syncFromLocal = () => {
  tableData.value = getLocalPlans()
}

const persistPlan = (plan) => {
  const plans = getLocalPlans()
  const normalized = {
    ...plan,
    id: plan.id ?? Date.now(),
    userId: currentUser.value.id
  }
  const index = plans.findIndex(item => item.id === normalized.id)

  if (index >= 0) {
    plans[index] = { ...plans[index], ...normalized }
  } else {
    plans.unshift(normalized)
  }

  setLocalPlans(plans)
  tableData.value = plans
}

const removeLocalPlan = (id) => {
  const plans = getLocalPlans().filter(item => item.id !== id)
  setLocalPlans(plans)
  tableData.value = plans
}

const getStatusType = (status) => {
  if (status === '已完成') return 'success'
  if (status === '进行中') return 'warning'
  return 'info'
}

const getStatusText = (status) => {
  if (status === '已完成') return '✅ 已完成'
  if (status === '进行中') return '🔄 进行中'
  return '📘 未开始'
}

const loadData = async () => {
  try {
    const response = await axios.get('/plan/list', {
      params: { userId: currentUser.value.id }
    })
    if (response.data.code === 200) {
      tableData.value = response.data.data
      setLocalPlans(response.data.data || [])
      return
    }
    throw new Error(response.data.msg || 'load failed')
  } catch {
    syncFromLocal()
    ElMessage.warning('服务不可用，已切换到本地计划模式')
  }
}

const handleAdd = () => {
  dialogTitle.value = '新建计划'
  form.value = {
    title: '',
    content: '',
    status: '未开始',
    deadline: ''
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '修改计划'
  form.value = { ...row }
  dialogVisible.value = true
}

const changeStatus = async (row, newStatus) => {
  const updatedData = { ...row, status: newStatus }
  try {
    const response = await axios.put('/plan/update', updatedData)
    if (response.data.code === 200) {
      ElMessage.success(newStatus === '已完成' ? '计划已完成！' : '计划已开始，继续加油！')
      loadData()
      return
    }
    throw new Error(response.data.msg || 'update failed')
  } catch {
    persistPlan(updatedData)
    ElMessage.success('已更新本地状态')
  }
}

const savePlan = async () => {
  if (!form.value.title) {
    return ElMessage.warning('计划名称不能为空哦')
  }
  if (!form.value.deadline) {
    return ElMessage.warning('Deadline 不能为空')
  }

  form.value.userId = currentUser.value.id

  try {
    const url = form.value.id ? '/plan/update' : '/plan/add'
    const method = form.value.id ? 'put' : 'post'
    const response = await axios[method](url, form.value)

    if (response.data.code === 200) {
      ElMessage.success('计划保存成功')
      dialogVisible.value = false
      loadData()
      return
    }

    throw new Error(response.data.msg || 'save failed')
  } catch {
    persistPlan(form.value)
    dialogVisible.value = false
    ElMessage.success('已保存到本地，不依赖网络也能使用')
  }
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除这个计划吗？', '提示', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await axios.delete(`/plan/${id}`)
      if (response.data.code === 200) {
        ElMessage.success('计划已删除')
        loadData()
        return
      }
      throw new Error(response.data.msg || 'delete failed')
    } catch {
      removeLocalPlan(id)
      ElMessage.success('已从本地删除')
    }
  }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
:deep(.el-table--border) {
  border: none;
}

:deep(.el-table th.el-table__cell) {
  background-color: #f4f6f9 !important;
  color: #34495e;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>

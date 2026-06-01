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
              📅 {{ scope.row.deadline }}
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
              @click="changeStatus(scope.row, '进行中')">
              🚀 开始
            </el-button>
            
            <el-button 
              size="small" 
              type="success" 
              plain 
              v-if="scope.row.status === '进行中'" 
              @click="changeStatus(scope.row, '已完成')">
              ✅ 完成
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
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="savePlan">保 存</el-button>
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
const dialogTitle = ref('新建计划')
const form = ref({})

// 状态渲染辅助函数
const getStatusType = (status) => {
  if (status === '已完成') return 'success'
  if (status === '进行中') return 'warning'
  return 'info' 
}

const getStatusText = (status) => {
  if (status === '已完成') return '✅ 已完成'
  if (status === '进行中') return '⏳ 进行中'
  return '💤 未开始'
}

// 1. 获取计划列表 
const loadData = async () => {
  try {
    const response = await axios.get('/plan/list', {
      params: { userId: currentUser.value.id }
    })
    if (response.data.code === 200) {
      tableData.value = response.data.data
    }
  } catch (error) {
    ElMessage.error('获取计划列表失败！')
  }
}

// 🌟 2. 点击新建 (附带默认截止日期逻辑)
const handleAdd = () => {
  dialogTitle.value = '新建计划'
  form.value = {
    title: '',
    content: '',
    status: '未开始',
    deadline: '' // 初始化为空，等用户选择
  }
  dialogVisible.value = true
}

// 3. 点击编辑
const handleEdit = (row) => {
  dialogTitle.value = '修改计划'
  form.value = { ...row }
  dialogVisible.value = true
}

// 4. 快捷状态流转 
const changeStatus = async (row, newStatus) => {
  const updatedData = { ...row, status: newStatus }
  try {
    const response = await axios.put('/plan/update', updatedData)
    if (response.data.code === 200) {
      ElMessage.success(newStatus === '已完成' ? '太棒了！计划已完成！' : '计划已启动，加油！')
      loadData()
    }
  } catch (error) {
    ElMessage.error('操作失败！')
  }
}

// 🌟 5. 保存计划表单 (包含截止日期的校验)
const savePlan = async () => {
  if (!form.value.title) {
    return ElMessage.warning('计划名称不能为空哦！')
  }
  if (!form.value.deadline) {
    return ElMessage.warning('死线 (Deadline) 必须要设置！给自己点压力！')
  }
  
  form.value.userId = currentUser.value.id 

  try {
    const url = form.value.id ? '/plan/update' : '/plan/add'
    const method = form.value.id ? 'put' : 'post'
    
    const response = await axios[method](url, form.value)
    if (response.data.code === 200) {
      ElMessage.success('计划保存成功！')
      dialogVisible.value = false
      loadData()
    } else {
      ElMessage.error(response.data.msg || '保存失败')
    }
  } catch (error) {
    ElMessage.error('网络请求失败，请检查后端是否运行')
  }
}

// 6. 删除计划
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要放弃这个计划吗？', '灵魂拷问', {
    confirmButtonText: '狠心删除',
    cancelButtonText: '再想想',
    type: 'warning',
  }).then(async () => {
    try {
      const response = await axios.delete(`/plan/${id}`)
      if (response.data.code === 200) {
        ElMessage.success('计划已删除！')
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
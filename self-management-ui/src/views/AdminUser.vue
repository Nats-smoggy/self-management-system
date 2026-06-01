<template>
  <div class="admin-container">
    <el-card shadow="hover" class="glass-card">
      <div class="card-header">
        <h2 class="title">🛡️ 系统用户管理中心</h2>
        <el-tag type="danger" effect="dark" size="large">ADMIN 最高权限已激活</el-tag>
      </div>

      <el-table :data="tableData" style="width: 100%; background: transparent;">
        <el-table-column prop="id" label="ID" width="70" align="center" />
        
        <el-table-column label="头像" width="80" align="center">
          <template #default="scope">
            <el-avatar :size="40" :src="scope.row.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
          </template>
        </el-table-column>
        
        <el-table-column prop="username" label="登录账号" width="130" />
        <el-table-column prop="nickname" label="昵称" width="130" />
        
        <el-table-column prop="role" label="身份权限" width="130" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.role === 'ADMIN' ? 'danger' : 'success'" effect="plain" round>
              {{ scope.row.role === 'ADMIN' ? '👑 超级管理' : '👤 普通用户' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="当前状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 0 ? 'info' : 'primary'" effect="dark" size="small">
              {{ scope.row.status === 0 ? '🚫 已封禁' : '✅ 正常' }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column label="注册时间" min-width="170">
          <template #default="scope">
            <span class="time-text">
              {{ scope.row.createTime ? scope.row.createTime.replace('T', ' ').substring(0, 19) : '未知' }}
            </span>
          </template>
        </el-table-column>
        
        <el-table-column label="最高指令" width="220" align="center">
          <template #default="scope">
            <el-button 
              type="success" plain size="small" 
              :disabled="scope.row.role === 'ADMIN'" 
              @click="handlePromote(scope.row.id, scope.row.username)"
            >提权</el-button>

            <el-button 
              v-if="scope.row.status !== 0"
              type="danger" plain size="small" 
              :disabled="scope.row.role === 'ADMIN'" 
              @click="handleStatus(scope.row.id, scope.row.username, 0)"
            >封禁</el-button>

            <el-button 
              v-else
              type="warning" plain size="small" 
              @click="handleStatus(scope.row.id, scope.row.username, 1)"
            >解封</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios' // 🌟 确保 main.js 已经配置了 baseURL
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref([])

// 🌟 1. 获取所有用户列表 (使用相对路径)
const loadData = async () => {
  try {
    const res = await axios.get('/user/list')
    if (res.data.code === 200) {
      tableData.value = res.data.data
    }
  } catch (error) {
    ElMessage.error('获取用户列表失败，请检查公网隧道是否畅通')
  }
}

// 🌟 2. 提权操作
const handlePromote = (id, username) => {
  ElMessageBox.confirm(`确定要提拔【${username}】为超级管理员吗？`, '提权指令', {
    confirmButtonText: '确定提拔', cancelButtonText: '取消', type: 'warning',
  }).then(async () => {
    const res = await axios.put(`/user/promote/${id}`)
    if (res.data.code === 200) {
      ElMessage.success('提拔成功！')
      loadData() 
    }
  }).catch(() => {})
}

// 🌟 3. 逻辑封禁/解封
const handleStatus = (id, username, targetStatus) => {
  const actionName = targetStatus === 0 ? '封禁' : '解封'
  ElMessageBox.confirm(`确定要${actionName}用户【${username}】吗？`, '状态变更', {
    confirmButtonText: `确定${actionName}`, cancelButtonText: '点错了',
    type: targetStatus === 0 ? 'error' : 'warning',
  }).then(async () => {
    // 路径参数拼接
    const res = await axios.put(`/user/status/${id}/${targetStatus}`)
    if (res.data.code === 200) {
      ElMessage.success(`操作成功：已${actionName}`)
      loadData()
    }
  }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.admin-container {
  padding: 20px;
  animation: fadeIn 0.6s ease-out;
}

/* 🌟 核心：白浅蓝毛玻璃质感 */
.glass-card {
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.45) !important;
  backdrop-filter: blur(15px);
  -webkit-backdrop-filter: blur(15px);
  border: 1px solid rgba(255, 255, 255, 0.6) !important;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.05) !important;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  padding-bottom: 15px;
}

.title {
  margin: 0;
  font-weight: 800;
  background: linear-gradient(135deg, #2c3e50 0%, #4facfe 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.time-text {
  font-family: 'Courier New', Courier, monospace;
  font-size: 13px;
  color: #606266;
}

/* 简单的入场动画 */
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 覆盖表格背景，保持整体通透 */
:deep(.el-table), :deep(.el-table tr), :deep(.el-table th), :deep(.el-table td) {
  background-color: transparent !important;
}
</style>
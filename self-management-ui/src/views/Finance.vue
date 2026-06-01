<template>
  <div style="padding: 20px;">
    <el-card shadow="hover" style="border-radius: 12px;">
      <div style="margin-bottom: 20px; display: flex; justify-content: space-between;">
        <h3 style="margin: 0; color: #333;">💰 我的专属小账本</h3>
        <el-button type="success" size="large" @click="handleAdd"> + 记一笔账 </el-button>
      </div>

      <el-table :data="tableData" border stripe style="width: 100%">
        <el-table-column prop="recordDate" label="记录日期" width="150" sortable />
        
        <el-table-column prop="type" label="收支类型" width="120" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.type === '收入' ? 'success' : 'danger'" effect="dark">
              {{ scope.row.type }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="category" label="消费分类" width="150" />
        
        <el-table-column prop="amount" label="金额 (元)" width="150" align="right">
          <template #default="scope">
            <span :style="{ color: scope.row.type === '收入' ? '#67C23A' : '#F56C6C', fontWeight: 'bold', fontSize: '16px' }">
              {{ scope.row.type === '收入' ? '+' : '-' }} {{ scope.row.amount }}
            </span>
          </template>
        </el-table-column>
        
        <el-table-column prop="remark" label="备注说明" min-width="200" />
        
        <el-table-column label="操作" width="180" align="center">
          <template #default="scope">
            <el-button size="small" type="primary" plain @click="handleEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" plain @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="收支类型">
          <el-radio-group v-model="form.type">
            <el-radio-button value="支出" label="支出" />
            <el-radio-button value="收入" label="收入" />
          </el-radio-group>
        </el-form-item>
        
        <el-form-item label="金额">
          <el-input-number v-model="form.amount" :precision="2" :step="10" :min="0.01" style="width: 100%;" />
        </el-form-item>
        
        <el-form-item label="账单分类">
          <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%;">
            <el-option v-if="form.type === '支出'" label="餐饮美食" value="餐饮美食" />
            <el-option v-if="form.type === '支出'" label="学习提升" value="学习提升" />
            <el-option v-if="form.type === '支出'" label="休闲娱乐" value="休闲娱乐" />
            <el-option v-if="form.type === '支出'" label="交通出行" value="交通出行" />
            <el-option v-if="form.type === '支出'" label="其他支出" value="其他支出" />
            <el-option v-if="form.type === '收入'" label="生活费" value="生活费" />
            <el-option v-if="form.type === '收入'" label="兼职收入" value="兼职收入" />
            <el-option v-if="form.type === '收入'" label="奖学金" value="奖学金" />
            <el-option v-if="form.type === '收入'" label="其他收入" value="其他收入" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="日期">
          <el-date-picker v-model="form.recordDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width: 100%;" />
        </el-form-item>
        
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" placeholder="写点什么吧..." />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveFinance">确 定</el-button>
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
const dialogTitle = ref('记一笔账')
const form = ref({})

// 🌟 1. 获取账单列表 (相对路径，查 finance)
const loadData = async () => {
  try {
    const response = await axios.get('/finance/list', {
      params: { userId: currentUser.value.id }
    })
    if (response.data.code === 200) {
      tableData.value = response.data.data
    }
  } catch (error) {
    ElMessage.error('获取账单失败！')
  }
}

// 2. 点击新增按钮
const handleAdd = () => {
  dialogTitle.value = '记一笔账'
  form.value = {
    type: '支出', 
    amount: 0,
    category: '',
    recordDate: new Date().toISOString().split('T')[0], 
    remark: ''
  }
  dialogVisible.value = true
}

// 3. 点击编辑按钮
const handleEdit = (row) => {
  dialogTitle.value = '修改账单'
  form.value = { ...row }
  dialogVisible.value = true
}

// 🌟 4. 保存账单 (相对路径)
const saveFinance = async () => {
  if (!form.value.amount || !form.value.category || !form.value.recordDate) {
    return ElMessage.warning('金额、分类和日期都不能为空哦！')
  }
  
  form.value.userId = currentUser.value.id 

  try {
    const url = form.value.id ? '/finance/update' : '/finance/add'
    const method = form.value.id ? 'put' : 'post'
    
    const response = await axios[method](url, form.value)
    if (response.data.code === 200) {
      ElMessage.success('保存成功！')
      dialogVisible.value = false
      loadData()
    }
  } catch (error) {
    ElMessage.error('保存失败，请检查网络！')
  }
}

// 🌟 5. 删除账单 (相对路径)
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除这笔账单吗？删除后无法恢复！', '警告', {
    confirmButtonText: '确定删除',
    cancelButtonText: '点错了',
    type: 'warning',
  }).then(async () => {
    try {
      const response = await axios.delete(`/finance/${id}`)
      if (response.data.code === 200) {
        ElMessage.success('删除成功！')
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
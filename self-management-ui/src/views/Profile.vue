<template>
  <div class="profile-page">
    <el-card shadow="hover" class="profile-card">
      <div style="text-align: center; padding: 20px 0;">
        <el-upload class="avatar-uploader" action="#" :auto-upload="false" :show-file-list="false" @change="handleAvatarChange">
          <div class="avatar-container">
            <el-avatar :size="100" :src="currentUser.avatar || defaultAvatar" class="avatar-img" />
            <div class="edit-mask">更换头像</div>
          </div>
        </el-upload>

        <h2 style="margin: 15px 0 5px 0;">{{ currentUser.nickname || '未命名用户' }}</h2>
        <p style="color: #999; margin: 0;">系统专属账号：{{ currentUser.username }}</p>
      </div>

      <el-divider border-style="dashed" />

      <el-descriptions title="详细档案" :column="1" border>
        <el-descriptions-item label="用户数字 ID">{{ currentUser.id }}</el-descriptions-item>
        <el-descriptions-item label="当前系统角色">
          <el-tag :type="currentUser.role === 'ADMIN' ? 'danger' : 'success'" effect="dark">
            {{ currentUser.role === 'ADMIN' ? '超级管理员' : '普通用户' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="账号注册时间">{{ formatTime(currentUser.createTime) }}</el-descriptions-item>
      </el-descriptions>

      <div class="profile-actions">
        <el-button type="danger" plain @click="pwdDialogVisible = true">修改密码</el-button>
        <el-button type="warning" plain @click="openEditDialog">编辑资料</el-button>
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="编辑个人资料" :width="dialogWidth">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="登录账号"><el-input v-model="editForm.username" disabled /></el-form-item>
        <el-form-item label="用户昵称"><el-input v-model="editForm.nickname" placeholder="请输入新昵称" /></el-form-item>
        <el-form-item label="专业"><el-input v-model="editForm.major" placeholder="例如：计算机科学" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveProfile">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="pwdDialogVisible" title="修改系统密码" :width="dialogWidth">
      <el-form :model="pwdForm" label-width="90px">
        <el-form-item label="原密码"><el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入当前密码" /></el-form-item>
        <el-form-item label="新密码"><el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="请输入新密码" /></el-form-item>
        <el-form-item label="确认新密码"><el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="savePassword">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const defaultAvatar = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
const currentUser = ref(JSON.parse(localStorage.getItem('user') || '{}'))
const dialogWidth = computed(() => (window.innerWidth <= 900 ? '92%' : '30%'))

const usersKey = 'self-management-users'
const readUsers = () => {
  try { return JSON.parse(localStorage.getItem(usersKey) || '[]') } catch { return [] }
}
const writeUsers = (users) => localStorage.setItem(usersKey, JSON.stringify(users))

const formatTime = (value) => value ? value.replace('T', ' ').substring(0, 19) : '未知'

const handleAvatarChange = (file) => {
  if (file.raw.size / 1024 / 1024 > 2) {
    ElMessage.error('图片太大了，头像不能超过 2MB')
    return
  }

  const reader = new FileReader()
  reader.readAsDataURL(file.raw)
  reader.onload = () => {
    const base64Image = reader.result
    currentUser.value.avatar = base64Image
    localStorage.setItem('user', JSON.stringify(currentUser.value))

    const users = readUsers()
    const user = users.find(item => Number(item.id) === Number(currentUser.value.id))
    if (user) {
      user.avatar = base64Image
      writeUsers(users)
    }

    ElMessage.success('头像已更新')
    setTimeout(() => location.reload(), 300)
  }
}

const dialogVisible = ref(false)
const editForm = ref({})
const openEditDialog = () => {
  editForm.value = { ...currentUser.value }
  dialogVisible.value = true
}

const saveProfile = () => {
  if (!editForm.value.nickname) return ElMessage.warning('昵称不能为空')

  const users = readUsers()
  const index = users.findIndex(item => Number(item.id) === Number(currentUser.value.id))
  if (index >= 0) {
    users[index] = { ...users[index], ...editForm.value, updateTime: new Date().toISOString() }
    writeUsers(users)
  }

  currentUser.value = { ...currentUser.value, ...editForm.value }
  localStorage.setItem('user', JSON.stringify(currentUser.value))
  dialogVisible.value = false
  ElMessage.success('资料已保存')
  setTimeout(() => location.reload(), 300)
}

const pwdDialogVisible = ref(false)
const pwdForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })

const savePassword = () => {
  if (!pwdForm.value.oldPassword || !pwdForm.value.newPassword) return ElMessage.warning('密码不能为空')
  if (pwdForm.value.newPassword !== pwdForm.value.confirmPassword) return ElMessage.error('两次输入的新密码不一致')

  const users = readUsers()
  const index = users.findIndex(item => Number(item.id) === Number(currentUser.value.id))
  if (index < 0) return ElMessage.error('未找到当前用户')
  if (users[index].password !== pwdForm.value.oldPassword) return ElMessage.error('原密码错误')

  users[index].password = pwdForm.value.newPassword
  users[index].updateTime = new Date().toISOString()
  writeUsers(users)
  currentUser.value.password = pwdForm.value.newPassword
  localStorage.setItem('user', JSON.stringify(currentUser.value))
  pwdDialogVisible.value = false
  localStorage.removeItem('user')
  ElMessage.success('密码已修改，请重新登录')
  setTimeout(() => router.push('/login'), 500)
}
</script>

<style scoped>
.profile-page { padding: 20px; }
.profile-card { max-width: 500px; margin: 0 auto; border-radius: 15px; }
.profile-actions { text-align: center; margin-top: 30px; display: flex; justify-content: center; gap: 12px; flex-wrap: wrap; }
.avatar-container { position: relative; display: inline-block; cursor: pointer; border-radius: 50%; overflow: hidden; }
.avatar-img { display: block; transition: all 0.3s; }
.edit-mask { position: absolute; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.5); color: white; display: flex; justify-content: center; align-items: center; opacity: 0; transition: opacity 0.3s; font-size: 14px; }
.avatar-container:hover .edit-mask { opacity: 1; }
</style>

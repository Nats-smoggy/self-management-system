<template>
  <div style="padding: 20px;">
    <el-card shadow="hover" style="max-width: 500px; margin: 0 auto; border-radius: 15px;">
      
      <div style="text-align: center; padding: 20px 0;">
        <el-upload
          class="avatar-uploader"
          action="#"
          :auto-upload="false"
          :show-file-list="false"
          @change="handleAvatarChange"
        >
          <div class="avatar-container">
            <el-avatar :size="100" :src="currentUser.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" class="avatar-img" />
            <div class="edit-mask">更换头像</div>
          </div>
        </el-upload>
        
        <h2 style="margin: 15px 0 5px 0;">{{ currentUser.nickname }}</h2>
        <p style="color: #999; margin: 0;">系统专属账号：{{ currentUser.username }}</p>
      </div>
      
      <el-divider border-style="dashed" />
      
      <el-descriptions title="详细档案" :column="1" border>
        <el-descriptions-item label="用户数字 ID">{{ currentUser.id }}</el-descriptions-item>
        <el-descriptions-item label="当前系统角色">
          <el-tag :type="currentUser.role === 'ADMIN' ? 'danger' : 'success'" effect="dark">
            {{ currentUser.role === 'ADMIN' ? '👑 超级管理员' : '👤 普通用户' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="账号注册时间">{{ currentUser.createTime ? currentUser.createTime.replace('T', ' ').substring(0, 19) : '未知' }}</el-descriptions-item>
      </el-descriptions>
      
      <div style="text-align: center; margin-top: 30px;">
        <el-button type="danger" plain @click="pwdDialogVisible = true">修改密码</el-button>
        <el-button type="warning" plain @click="openEditDialog">编辑资料</el-button>
      </div>
    </el-card>

    <el-dialog v-model="dialogVisible" title="编辑个人资料" width="30%">
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="登录账号"><el-input v-model="editForm.username" disabled /></el-form-item>
        <el-form-item label="用户昵称"><el-input v-model="editForm.nickname" placeholder="请输入新昵称" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveProfile">确 定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="pwdDialogVisible" title="修改系统密码" width="30%">
      <el-form :model="pwdForm" label-width="90px">
        <el-form-item label="原密码">
          <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入现在的密码" />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认新密码">
          <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdDialogVisible = false">取 消</el-button>
        <el-button type="danger" @click="savePassword">确认修改</el-button>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()

// 从保险箱拿到当前用户
const currentUser = ref(JSON.parse(localStorage.getItem('user') || '{}'))

// ====== 模块一：修改头像 (Base64 魔法) ======
const handleAvatarChange = (file) => {
  // 限制图片大小不能超过 2MB
  if (file.raw.size / 1024 / 1024 > 2) {
    ElMessage.error('图片太大了！头像不能超过 2MB 哦！')
    return
  }

  // FileReader：把图片文件转换成长长的 Base64 字符串
  const reader = new FileReader()
  reader.readAsDataURL(file.raw)
  reader.onload = async () => {
    const base64Image = reader.result
    
    try {
      // 直接调用原有的更新资料接口，把头像字符串存进去
      const response = await axios.put('/user/update', {
        id: currentUser.value.id,
        avatar: base64Image
      })
      if (response.data.code === 200) {
        ElMessage.success('头像更换成功！')
        // 同步更新本地的保险箱和页面的显示
        currentUser.value.avatar = base64Image
        localStorage.setItem('user', JSON.stringify(currentUser.value))
        // 刷新一下右上角的头像
        setTimeout(() => location.reload(), 500)
      }
    } catch (error) {
      ElMessage.error('头像上传失败，连接后端异常！')
    }
  }
}

// ====== 模块二：编辑资料 (已完美) ======
const dialogVisible = ref(false)
const editForm = ref({})
const openEditDialog = () => {
  editForm.value = { ...currentUser.value }
  dialogVisible.value = true
}
const saveProfile = async () => {
  if (!editForm.value.nickname) return ElMessage.warning('昵称不能为空！')
  try {
    const response = await axios.put('/user/update', editForm.value)
    if (response.data.code === 200) {
      ElMessage.success('资料修改成功！')
      localStorage.setItem('user', JSON.stringify(editForm.value))
      dialogVisible.value = false 
      setTimeout(() => location.reload(), 500)
    }
  } catch (error) { ElMessage.error('保存失败！') }
}

// ====== 模块三：修改密码 (全新加入) ======
const pwdDialogVisible = ref(false)
const pwdForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })

const savePassword = async () => {
  if (!pwdForm.value.oldPassword || !pwdForm.value.newPassword) {
    return ElMessage.warning('密码不能为空哦！')
  }
  if (pwdForm.value.newPassword !== pwdForm.value.confirmPassword) {
    return ElMessage.error('两次输入的新密码不一致！')
  }

  try {
    const response = await axios.put('/user/password', {
      id: currentUser.value.id,
      oldPassword: pwdForm.value.oldPassword,
      newPassword: pwdForm.value.newPassword
    })
    
    if (response.data.code === 200) {
      ElMessage.success('密码修改成功！请重新登录。')
      pwdDialogVisible.value = false
      // 密码改了，把保险箱清空，踢回登录页
      localStorage.removeItem('user')
      setTimeout(() => router.push('/login'), 1000)
    } else {
      ElMessage.error(response.data.msg || '原密码错误！')
    }
  } catch (error) {
    ElMessage.error('修改失败，连接后端异常！')
  }
}
</script>

<style scoped>
/* 悬浮修改头像的高级特效 */
.avatar-container {
  position: relative;
  display: inline-block;
  cursor: pointer;
  border-radius: 50%;
  overflow: hidden;
}
.avatar-img {
  display: block;
  transition: all 0.3s;
}
.edit-mask {
  position: absolute;
  top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 14px;
  opacity: 0;
  transition: all 0.3s;
}
.avatar-container:hover .avatar-img {
  transform: scale(1.1);
  filter: blur(2px);
}
.avatar-container:hover .edit-mask {
  opacity: 1;
}
</style>

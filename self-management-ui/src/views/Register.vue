<template>
  <div class="register-outer-container">
    <div class="campus-carousel">
      <div v-for="(img, index) in campusImages" :key="index" class="carousel-item" :class="{ active: index === currentImageIndex }" :style="{ backgroundImage: 'url(' + img + ')' }"></div>
    </div>

    <div class="bg-overlay"></div>

    <div class="register-inner-box">
      <el-card class="modern-light-card">
        <div class="brand-section">
          <div class="logo-icon">
            <el-icon><EditPen /></el-icon>
          </div>
          <h2 class="main-title">创建新身份</h2>
          <div class="title-separator"></div>
          <p class="en-subtitle">JOIN SELF-MANAGEMENT SYSTEM</p>
        </div>

        <el-form ref="registerFormRef" :model="form" :rules="rules" status-icon class="modern-form">
          <el-form-item prop="username" class="modern-item">
            <el-input v-model="form.username" placeholder="建议学号或字母组合" class="modern-input">
              <template #prefix><el-icon class="input-icon"><User /></el-icon></template>
            </el-input>
          </el-form-item>

          <el-form-item prop="nickname" class="modern-item">
            <el-input v-model="form.nickname" placeholder="你希望大家怎么称呼你" class="modern-input">
              <template #prefix><el-icon class="input-icon"><Avatar /></el-icon></template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password" class="modern-item">
            <el-input v-model="form.password" type="password" placeholder="设置密码（字母+数字）" show-password class="modern-input">
              <template #prefix><el-icon class="input-icon"><Lock /></el-icon></template>
            </el-input>
          </el-form-item>

          <el-form-item prop="confirm" class="modern-item">
            <el-input v-model="form.confirm" type="password" placeholder="再次确认密码" show-password class="modern-input">
              <template #prefix><el-icon class="input-icon"><CircleCheck /></el-icon></template>
            </el-input>
          </el-form-item>

          <el-form-item>
            <button type="button" class="vitality-btn" @click="handleRegister" :disabled="loading">
              <span v-if="!loading">立即注册</span>
              <span v-else class="loading-text">
                <el-icon class="is-loading"><Loading /></el-icon> 正在创建本地账号...
              </span>
            </button>
          </el-form-item>
        </el-form>

        <div class="modern-footer">
          <span class="text-gray">已有账号？</span>
          <router-link to="/login" class="reg-link">返回登录</router-link>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Avatar, EditPen, CircleCheck, Loading } from '@element-plus/icons-vue'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)
const campusImages = ref([
  'https://www.cucas.cn/uploads/school/2017/0517/bc54a8f90fe47bfb4fd53474e2a076b0.jpg',
  'https://www.digiedupro.com/media/2019/04/China-University-of-Mining-and-Technology-Campus-5.jpg',
  'http://school.nseac.com/img/sch_img/10290.jpg'
])
const currentImageIndex = ref(0)
let carouselInterval = null

const startCarousel = () => {
  carouselInterval = setInterval(() => {
    currentImageIndex.value = (currentImageIndex.value + 1) % campusImages.value.length
  }, 7000)
}

onMounted(startCarousel)
onUnmounted(() => clearInterval(carouselInterval))

const form = reactive({ username: '', nickname: '', password: '', confirm: '' })
const usersKey = 'self-management-users'

const readUsers = () => {
  try { return JSON.parse(localStorage.getItem(usersKey) || '[]') } catch { return [] }
}

const writeUsers = (users) => localStorage.setItem(usersKey, JSON.stringify(users))

const validateConfirm = (rule, value, callback) => {
  if (!value) callback(new Error('请再次输入密码'))
  else if (value !== form.password) callback(new Error('两次输入密码不一致'))
  else callback()
}

const rules = reactive({
  username: [
    { required: true, message: '账号不能为空', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9]+$/, message: '账号只能包含字母和数字', trigger: 'change' }
  ],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  password: [
    { required: true, message: '密码不能为空', trigger: 'blur' },
    { pattern: /^(?=.*[0-9])(?=.*[a-zA-Z]).+$/, message: '密码必须包含字母和数字', trigger: 'change' }
  ],
  confirm: [{ validator: validateConfirm, trigger: 'blur' }]
})

const handleRegister = () => {
  registerFormRef.value.validate((valid) => {
    if (!valid) {
      ElMessage.warning('请先把红色提示项补完整')
      return
    }

    const username = form.username.trim()
    const nickname = form.nickname.trim()
    const users = readUsers()

    if (users.some(item => item.username === username)) {
      ElMessage.error('该账号已存在，请换一个用户名')
      return
    }

    const newUser = {
      id: users.reduce((max, item) => Math.max(max, Number(item.id) || 0), 0) + 1,
      username,
      nickname,
      password: form.password,
      avatar: '',
      major: '',
      createTime: new Date().toISOString(),
      updateTime: new Date().toISOString(),
      role: 'USER',
      status: 1
    }

    loading.value = true
    try {
      users.push(newUser)
      writeUsers(users)
      localStorage.setItem('user', JSON.stringify(newUser))
      ElMessage.success('注册成功，账号已经保存到本地')
      router.push('/login')
    } catch (error) {
      ElMessage.error(error?.message || '注册失败')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.register-outer-container { height: 100vh; width: 100vw; display: flex; justify-content: center; align-items: center; background-color: #eef2f7; overflow: hidden; position: relative; font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', Arial, sans-serif; }
.campus-carousel { position: absolute; top: 0; left: 0; width: 100%; height: 100%; z-index: 1; }
.carousel-item { position: absolute; top: 0; left: 0; width: 100%; height: 100%; background-size: cover; background-position: center; background-repeat: no-repeat; opacity: 0; transition: opacity 1.8s ease-in-out; }
.carousel-item.active { opacity: 1; }
.bg-overlay { position: absolute; top: 0; left: 0; width: 100%; height: 100%; background: rgba(255, 255, 255, 0.2); z-index: 2; }
.register-inner-box { position: relative; z-index: 10; }
.modern-light-card { width: 440px; background: rgba(255, 255, 255, 0.92) !important; border-radius: 24px; border: 1px solid rgba(255, 255, 255, 0.8); box-shadow: 0 15px 45px rgba(0, 0, 0, 0.15); }
:deep(.el-card__body) { padding: 40px !important; }
.brand-section { text-align: center; margin-bottom: 25px; }
.logo-icon { font-size: 42px; background: linear-gradient(135deg, #4FACFE 0%, #00F2FE 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; margin-bottom: 5px; display: inline-block; }
.main-title { color: #2b3674; font-size: 26px; font-weight: 800; margin: 0; letter-spacing: 1px; }
.title-separator { width: 40px; height: 4px; background: linear-gradient(90deg, #4FACFE, #00F2FE); margin: 10px auto; border-radius: 4px; }
.en-subtitle { color: #a3aed1; font-size: 11px; letter-spacing: 2px; margin: 0; font-weight: 500; }
.modern-form :deep(.el-form-item__content) { margin-left: 0 !important; }
.modern-item { margin-bottom: 20px; }
.modern-form :deep(.el-form-item__error) { color: #ff5b5b; padding-top: 2px; left: 10px; font-size: 12px; }
.modern-input :deep(.el-input__wrapper) { background-color: #f4f7fe !important; border-radius: 12px !important; border: 1px solid #e2e8f0 !important; box-shadow: none !important; padding: 0 16px !important; height: 48px !important; transition: all 0.3s ease; }
.modern-input :deep(.el-input__wrapper:hover), .modern-input :deep(.el-input__wrapper.is-focus) { border-color: #4FACFE !important; background-color: #ffffff !important; }
.input-icon { font-size: 18px; color: #a3aed1; }
.modern-input :deep(.el-input__inner) { color: #2b3674 !important; font-size: 14px !important; font-weight: 500; }
.vitality-btn { width: 100%; height: 50px; border: none; color: #ffffff; background: linear-gradient(135deg, #4FACFE 0%, #00F2FE 100%); cursor: pointer; border-radius: 12px; font-size: 16px; font-weight: 700; letter-spacing: 1px; transition: all 0.3s ease; display: flex; justify-content: center; align-items: center; box-shadow: 0 8px 20px rgba(79, 172, 254, 0.3); margin-top: 10px; }
.vitality-btn:hover { transform: translateY(-2px); box-shadow: 0 12px 25px rgba(79, 172, 254, 0.45); }
.vitality-btn:disabled { background: #cbd5e1; box-shadow: none; cursor: not-allowed; }
.loading-text { display: flex; align-items: center; gap: 8px; }
.modern-footer { margin-top: 20px; font-size: 14px; display: flex; justify-content: center; gap: 8px; }
.text-gray { color: #a3aed1; }
.reg-link { color: #4FACFE; text-decoration: none; font-weight: 700; }
.reg-link:hover { color: #00F2FE; text-decoration: underline; }
</style>

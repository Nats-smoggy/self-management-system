<template>
  <div class="login-outer-container">
    <div class="campus-carousel">
      <div v-for="(img, index) in campusImages" :key="index" class="carousel-item" :class="{ active: index === currentImageIndex }" :style="{ backgroundImage: 'url(' + img + ')' }"></div>
    </div>

    <div class="bg-overlay"></div>

    <div class="login-inner-box">
      <el-card class="modern-light-card">
        <div class="brand-section">
          <div class="logo-icon">
            <el-icon><Monitor /></el-icon>
          </div>
          <h2 class="main-title">大学生自我规划</h2>
          <div class="title-separator"></div>
          <p class="en-subtitle">SELF-MANAGEMENT SYSTEM</p>
        </div>

        <el-form ref="loginFormRef" :model="form" :rules="rules" class="modern-form">
          <el-form-item prop="username" class="modern-item">
            <el-input v-model="form.username" placeholder="请输入账号 / Username" clearable class="modern-input">
              <template #prefix><el-icon class="input-icon"><User /></el-icon></template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password" class="modern-item">
            <el-input v-model="form.password" type="password" placeholder="请输入密码 / Password" show-password @keyup.enter="handleLogin" class="modern-input">
              <template #prefix><el-icon class="input-icon"><Lock /></el-icon></template>
            </el-input>
          </el-form-item>

          <el-form-item>
            <button type="button" class="vitality-btn" @click="handleLogin" :disabled="loading">
              <span v-if="!loading">立即登录</span>
              <span v-else class="loading-text">
                <el-icon class="is-loading"><Loading /></el-icon> 正在进入系统...
              </span>
            </button>
          </el-form-item>
        </el-form>

        <div class="modern-footer">
          <span class="text-gray">新同学？</span>
          <router-link to="/register" class="reg-link">点击注册</router-link>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, Monitor, Loading } from '@element-plus/icons-vue'

const router = useRouter()
const loginFormRef = ref(null)
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

const form = reactive({ username: '', password: '' })
const usersKey = 'self-management-users'

const readUsers = () => {
  try { return JSON.parse(localStorage.getItem(usersKey) || '[]') } catch { return [] }
}

const rules = reactive({
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
})

const handleLogin = () => {
  loginFormRef.value.validate((valid) => {
    if (!valid) {
      ElMessage.warning('请先把账号和密码填完整')
      return
    }

    loading.value = true
    try {
      const users = readUsers()
      const user = users.find(item => item.username === form.username.trim() && item.password === form.password)

      if (!user) {
        ElMessage.error('账号或密码错误')
        return
      }

      if (Number(user.status) === 0) {
        ElMessage.error('该账号已被封禁')
        return
      }

      localStorage.setItem('user', JSON.stringify(user))
      const targetPath = user.role === 'ADMIN' ? '/home/admin' : '/home/dashboard'
      ElMessage.success('登录成功，欢迎回来')
      router.push(targetPath)
    } catch (error) {
      ElMessage.error(error?.message || '登录失败')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-outer-container { height: 100vh; width: 100vw; display: flex; justify-content: center; align-items: center; background-color: #eef2f7; overflow: hidden; position: relative; font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', Arial, sans-serif; }
.campus-carousel { position: absolute; top: 0; left: 0; width: 100%; height: 100%; z-index: 1; }
.carousel-item { position: absolute; top: 0; left: 0; width: 100%; height: 100%; background-size: cover; background-position: center; background-repeat: no-repeat; opacity: 0; transition: opacity 1.8s ease-in-out; }
.carousel-item.active { opacity: 1; }
.bg-overlay { position: absolute; top: 0; left: 0; width: 100%; height: 100%; background: rgba(255, 255, 255, 0.2); backdrop-filter: none !important; -webkit-backdrop-filter: none !important; z-index: 2; }
.login-inner-box { position: relative; z-index: 10; }
.modern-light-card { width: 420px; background: rgba(255, 255, 255, 0.92) !important; border-radius: 24px; border: 1px solid rgba(255, 255, 255, 0.8); box-shadow: 0 15px 45px rgba(0, 0, 0, 0.15); transition: transform 0.3s ease, box-shadow 0.3s ease; }
.modern-light-card:hover { transform: translateY(-5px); box-shadow: 0 25px 55px rgba(79, 172, 254, 0.3); }
:deep(.el-card__body) { padding: 45px 40px !important; }
.brand-section { text-align: center; margin-bottom: 35px; }
.logo-icon { font-size: 42px; background: linear-gradient(135deg, #4FACFE 0%, #00F2FE 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; margin-bottom: 8px; display: inline-block; }
.main-title { color: #2b3674; font-size: 26px; font-weight: 800; margin: 0; letter-spacing: 1px; }
.title-separator { width: 40px; height: 4px; background: linear-gradient(90deg, #4FACFE, #00F2FE); margin: 12px auto; border-radius: 4px; }
.en-subtitle { color: #a3aed1; font-size: 12px; letter-spacing: 2px; margin: 0; font-weight: 500; }
.modern-form :deep(.el-form-item__content) { margin-left: 0 !important; }
.modern-item { margin-bottom: 24px; }
.modern-form :deep(.el-form-item__error) { color: #ff5b5b; padding-top: 4px; left: 10px; }
.modern-input :deep(.el-input__wrapper) { background-color: #f4f7fe !important; border-radius: 14px !important; border: 1px solid #e2e8f0 !important; box-shadow: none !important; padding: 0 16px !important; height: 52px !important; transition: all 0.3s ease; }
.modern-input :deep(.el-input__wrapper):hover { border-color: #4FACFE !important; background-color: #ffffff !important; }
.modern-input :deep(.el-input__wrapper.is-focus) { border-color: #4FACFE !important; box-shadow: 0 0 0 3px rgba(79, 172, 254, 0.2) !important; background-color: #ffffff !important; }
.input-icon { font-size: 18px; color: #a3aed1; }
.modern-input :deep(.el-input__inner) { color: #2b3674 !important; font-size: 15px !important; font-weight: 500; }
.vitality-btn { width: 100%; height: 52px; border: none; outline: none; color: #ffffff; background: linear-gradient(135deg, #4FACFE 0%, #00F2FE 100%); cursor: pointer; border-radius: 14px; font-size: 16px; font-weight: 700; letter-spacing: 1px; transition: all 0.3s ease; display: flex; justify-content: center; align-items: center; box-shadow: 0 8px 20px rgba(79, 172, 254, 0.3); }
.vitality-btn:hover { transform: translateY(-2px); box-shadow: 0 12px 25px rgba(79, 172, 254, 0.45); }
.vitality-btn:disabled { background: #cbd5e1; box-shadow: none; cursor: not-allowed; }
.loading-text { display: flex; align-items: center; gap: 8px; }
.modern-footer { margin-top: 25px; font-size: 14px; display: flex; justify-content: center; gap: 8px; }
.text-gray { color: #a3aed1; }
.reg-link { color: #4FACFE; text-decoration: none; font-weight: 700; }
.reg-link:hover { color: #00F2FE; text-decoration: underline; }
</style>

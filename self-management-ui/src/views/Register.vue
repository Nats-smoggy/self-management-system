<template>
  <div class="register-outer-container">
    <div class="campus-carousel">
      <div 
        v-for="(img, index) in campusImages" 
        :key="index" 
        class="carousel-item" 
        :class="{ active: index === currentImageIndex }"
        :style="{ backgroundImage: 'url(' + img + ')' }"
      ></div>
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
            <el-input v-model="form.nickname" placeholder="你希望大家怎么称呼你？" class="modern-input">
              <template #prefix><el-icon class="input-icon"><Avatar /></el-icon></template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password" class="modern-item">
            <el-input v-model="form.password" type="password" placeholder="设置密码：须含字母和数字" show-password class="modern-input">
              <template #prefix><el-icon class="input-icon"><Lock /></el-icon></template>
            </el-input>
          </el-form-item>

          <el-form-item prop="confirm" class="modern-item">
            <el-input v-model="form.confirm" type="password" placeholder="请再次确认您的密码" show-password class="modern-input">
              <template #prefix><el-icon class="input-icon"><CircleCheck /></el-icon></template>
            </el-input>
          </el-form-item>

          <el-form-item>
            <button type="button" class="vitality-btn" @click="handleRegister" :disabled="loading">
              <span v-if="!loading">开启自律之旅</span>
              <span v-else class="loading-text">
                <el-icon class="is-loading"><Loading /></el-icon> 正在同步云端数据...
              </span>
            </button>
          </el-form-item>
        </el-form>

        <div class="modern-footer">
          <span class="text-gray">已有通行权限？</span>
          <router-link to="/login" class="reg-link">返回登录中心</router-link>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
/** 🌟 核心：逻辑完全继承原版，不触动任何功能 🌟 **/
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { User, Lock, Avatar, EditPen, CircleCheck, Loading } from '@element-plus/icons-vue'

const router = useRouter()
const registerFormRef = ref(null)
const loading = ref(false)

// ☀️ 校园高清图源（与登录页完全一致，确保视觉统一）
const campusImages = ref([
  'https://www.cucas.cn/uploads/school/2017/0517/bc54a8f90fe47bfb4fd53474e2a076b0.jpg',
  'https://www.digiedupro.com/media/2019/04/China-University-of-Mining-and-Technology-Campus-5.jpg',
  'http://school.nseac.com/img/sch_img/10290.jpg',
])
const currentImageIndex = ref(0)
let carouselInterval = null

const startCarousel = () => {
  carouselInterval = setInterval(() => {
    currentImageIndex.value = (currentImageIndex.value + 1) % campusImages.value.length
  }, 7000)
}

onMounted(() => { startCarousel() })
onUnmounted(() => { clearInterval(carouselInterval) })

const form = reactive({
  username: '',
  nickname: '',
  password: '',
  confirm: ''
})

// 🌟 原版逻辑：确认密码校验
const validateConfirm = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入密码不一致！'))
  } else {
    callback()
  }
}

// 🌟 原版逻辑：严格的正则校验规则
const rules = reactive({
  username: [
    { required: true, message: '账号不能为空', trigger: 'blur' },
    { min: 3, max: 15, message: '长度在 3 到 15 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9]+$/, message: '账号只能包含字母和数字', trigger: 'change' }
  ],
  nickname: [{ required: true, message: '起个好听的昵称吧', trigger: 'blur' }],
  password: [
    { required: true, message: '密码不能为空', trigger: 'blur' },
    { pattern: /^(?=.*[0-9])(?=.*[a-zA-Z]).+$/, message: '密码必须包含字母和数字', trigger: 'change' }
  ],
  confirm: [{ validator: validateConfirm, trigger: 'blur' }]
})

const handleRegister = () => {
  registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        // 核心接口：/user/register
        const res = await axios.post('/user/register', form)
        if (res.data.code === 200) {
          ElMessage.success('欢迎加入！身份注册已成功，请登录。')
          router.push('/login')
        } else {
          ElMessage.error(res.data.msg || '注册受阻')
        }
      } catch (error) {
        ElMessage.error('无法连接认证服务器，请确保 Java 后端正在运行。')
      } finally {
        loading.value = false
      }
    } else {
      ElMessage.warning('信息填写不规范，请核对红色提示哦~')
    }
  })
}
</script>

<style scoped>
/* 🌟 样式完全复刻登录页，确保一致性 🌟 */
.register-outer-container {
  height: 100vh;
  width: 100vw;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #eef2f7;
  overflow: hidden;
  position: relative;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', Arial, sans-serif;
}

.campus-carousel {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.carousel-item {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  opacity: 0;
  transition: opacity 1.8s ease-in-out;
}

.carousel-item.active { opacity: 1; }

.bg-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.2); 
  z-index: 2;
}

.register-inner-box { position: relative; z-index: 10; }

.modern-light-card {
  width: 440px; /* 注册表单项多，宽度微调大一点点 */
  background: rgba(255, 255, 255, 0.92) !important; 
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.8);
  box-shadow: 0 15px 45px rgba(0, 0, 0, 0.15); 
}

:deep(.el-card__body) { padding: 40px 40px !important; }

.brand-section { text-align: center; margin-bottom: 25px; }

.logo-icon {
  font-size: 42px;
  background: linear-gradient(135deg, #4FACFE 0%, #00F2FE 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin-bottom: 5px;
  display: inline-block;
}

.main-title {
  color: #2b3674;
  font-size: 26px;
  font-weight: 800;
  margin: 0;
  letter-spacing: 1px;
}

.title-separator {
  width: 40px;
  height: 4px;
  background: linear-gradient(90deg, #4FACFE, #00F2FE);
  margin: 10px auto;
  border-radius: 4px;
}

.en-subtitle {
  color: #a3aed1;
  font-size: 11px;
  letter-spacing: 2px;
  margin: 0;
  font-weight: 500;
}

.modern-form :deep(.el-form-item__content) { margin-left: 0 !important; }
.modern-item { margin-bottom: 20px; }

.modern-form :deep(.el-form-item__error) {
  color: #ff5b5b;
  padding-top: 2px;
  left: 10px;
  font-size: 12px;
}

.modern-input :deep(.el-input__wrapper) {
  background-color: #f4f7fe !important; 
  border-radius: 12px !important;
  border: 1px solid #e2e8f0 !important;
  box-shadow: none !important;
  padding: 0 16px !important;
  height: 48px !important;
  transition: all 0.3s ease;
}

.modern-input :deep(.el-input__wrapper:hover),
.modern-input :deep(.el-input__wrapper.is-focus) {
  border-color: #4FACFE !important;
  background-color: #ffffff !important;
}

.input-icon { font-size: 18px; color: #a3aed1; }
.modern-input :deep(.is-focus) .input-icon { color: #4FACFE; }

.modern-input :deep(.el-input__inner) {
  color: #2b3674 !important;
  font-size: 14px !important;
  font-weight: 500;
}

.vitality-btn {
  width: 100%;
  height: 50px;
  border: none;
  color: #ffffff;
  background: linear-gradient(135deg, #4FACFE 0%, #00F2FE 100%);
  cursor: pointer;
  border-radius: 12px;
  font-size: 16px;
  font-weight: 700;
  letter-spacing: 1px;
  transition: all 0.3s ease;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 8px 20px rgba(79, 172, 254, 0.3);
  margin-top: 10px;
}

.vitality-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 25px rgba(79, 172, 254, 0.45);
}

.vitality-btn:disabled {
  background: #cbd5e1;
  box-shadow: none;
  cursor: not-allowed;
}

.loading-text { display: flex; align-items: center; gap: 8px; }

.modern-footer {
  margin-top: 20px;
  font-size: 14px;
  display: flex;
  justify-content: center;
  gap: 8px;
}

.text-gray { color: #a3aed1; }
.reg-link { color: #4FACFE; text-decoration: none; font-weight: 700; }
.reg-link:hover { color: #00F2FE; text-decoration: underline; }
</style>
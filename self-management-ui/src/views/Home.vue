<template>
  <el-container class="home-layout">
    <el-aside v-if="!isMobile" width="220px" class="aside-menu">
      <div class="menu-shell">
        <div class="logo-box">
          <span class="logo-text">自我规划大本营</span>
        </div>
        <AppMenu :current-user="currentUser" :route-path="route.path" />
      </div>
    </el-aside>

    <el-container class="content-shell">
      <el-header class="top-header">
        <div class="header-left">
          <el-button
            v-if="isMobile"
            circle
            plain
            class="menu-trigger"
            @click="drawerVisible = true"
          >
            <el-icon><Expand /></el-icon>
          </el-button>

          <div class="welcome-copy">
            <div class="welcome-title">欢迎回来</div>
            <div class="welcome-subtitle">今天也是推进计划的一天</div>
          </div>
        </div>

        <el-dropdown @command="handleCommand">
          <span class="el-dropdown-link user-chip">
            <el-avatar
              :size="32"
              :src="currentUser.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'"
              class="user-avatar"
            />
            <span class="user-name">{{ currentUser.nickname || currentUser.username }}</span>
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </span>

          <template #dropdown>
            <el-dropdown-menu class="user-menu">
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout" class="danger-item">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <el-main class="page-main">
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>

  <el-drawer
    v-model="drawerVisible"
    direction="ltr"
    size="78%"
    :with-header="false"
    class="mobile-drawer"
  >
    <div class="menu-shell">
      <div class="logo-box">
        <span class="logo-text">自我规划大本营</span>
      </div>
      <AppMenu :current-user="currentUser" :route-path="route.path" @navigate="drawerVisible = false" />
    </div>
  </el-drawer>
</template>

<script setup>
import { computed, defineComponent, onMounted, onUnmounted, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import {
  ArrowDown,
  Calendar,
  CoffeeCup,
  DataLine,
  Document,
  Expand,
  Money,
  Reading,
  Setting,
  Timer,
  TrendCharts,
  Trophy,
  User
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute()
const currentUser = ref(JSON.parse(localStorage.getItem('user') || '{}'))
const drawerVisible = ref(false)
const viewportWidth = ref(typeof window === 'undefined' ? 1280 : window.innerWidth)

const isMobile = computed(() => viewportWidth.value <= 900)

const handleResize = () => {
  viewportWidth.value = window.innerWidth
  if (!isMobile.value) {
    drawerVisible.value = false
  }
}

onMounted(() => {
  handleResize()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出系统吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      localStorage.removeItem('user')
      router.push('/login')
      ElMessage.success('已退出登录')
    }).catch(() => {})
  } else if (command === 'profile') {
    router.push('/home/profile')
  }
}

const AppMenu = defineComponent({
  name: 'AppMenu',
  props: {
    currentUser: {
      type: Object,
      required: true
    },
    routePath: {
      type: String,
      required: true
    }
  },
  emits: ['navigate'],
  template: `
    <div class="menu-wrapper">
      <el-menu
        router
        :default-active="routePath"
        active-text-color="#2563eb"
        background-color="transparent"
        text-color="#475569"
        class="el-menu-vertical"
        @select="$emit('navigate')"
      >
        <el-menu-item index="/home/dashboard">
          <el-icon><DataLine /></el-icon>
          <span>数据看板</span>
        </el-menu-item>

        <el-sub-menu index="study-management">
          <template #title>
            <el-icon><Reading /></el-icon>
            <span class="menu-group-title">学习管理</span>
          </template>
          <el-menu-item index="/home/plan">
            <el-icon><Calendar /></el-icon>
            <span>目标计划</span>
          </el-menu-item>
          <el-menu-item index="/home/course">
            <el-icon><Document /></el-icon>
            <span>课程表</span>
          </el-menu-item>
          <el-menu-item index="/home/focus">
            <el-icon><Timer /></el-icon>
            <span>专注时刻</span>
          </el-menu-item>
          <el-menu-item index="/home/exam">
            <el-icon><Trophy /></el-icon>
            <span>考试计划</span>
          </el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="life-management">
          <template #title>
            <el-icon><CoffeeCup /></el-icon>
            <span class="menu-group-title">生活管理</span>
          </template>
          <el-menu-item index="/home/finance">
            <el-icon><Money /></el-icon>
            <span>财务账本</span>
          </el-menu-item>
          <el-menu-item index="/home/weight">
            <el-icon><TrendCharts /></el-icon>
            <span>体重管理</span>
          </el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/home/profile">
          <el-icon><User /></el-icon>
          <span>个人中心</span>
        </el-menu-item>

        <el-menu-item v-if="currentUser.role === 'ADMIN'" index="/home/admin" class="admin-entry">
          <el-icon><Setting /></el-icon>
          <span>管理员控制台</span>
        </el-menu-item>
      </el-menu>
    </div>
  `,
  components: {
    ArrowDown,
    Calendar,
    CoffeeCup,
    DataLine,
    Document,
    Money,
    Reading,
    Setting,
    Timer,
    TrendCharts,
    Trophy,
    User
  }
})
</script>

<style scoped>
.home-layout {
  min-height: 100vh;
  background:
    radial-gradient(circle at top left, rgba(59, 130, 246, 0.12), transparent 28%),
    radial-gradient(circle at bottom right, rgba(16, 185, 129, 0.12), transparent 26%),
    #eef4ff;
}

.aside-menu {
  background: rgba(255, 255, 255, 0.74);
  border-right: 1px solid rgba(148, 163, 184, 0.18);
  backdrop-filter: blur(14px);
}

.menu-shell {
  height: 100%;
}

.logo-box {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 72px;
  padding: 0 18px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.18);
}

.logo-text {
  color: #2563eb;
  font-weight: 800;
  font-size: 18px;
  letter-spacing: 0.04em;
}

.menu-wrapper {
  padding: 14px 10px 18px;
}

.content-shell {
  min-width: 0;
}

.top-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 20px;
  background: rgba(255, 255, 255, 0.52);
  border-bottom: 1px solid rgba(148, 163, 184, 0.16);
  backdrop-filter: blur(14px);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 14px;
  min-width: 0;
}

.welcome-copy {
  min-width: 0;
}

.welcome-title {
  color: #0f172a;
  font-size: 15px;
  font-weight: 800;
}

.welcome-subtitle {
  color: #64748b;
  font-size: 12px;
  margin-top: 2px;
}

.menu-trigger {
  border-color: rgba(37, 99, 235, 0.18);
}

.user-chip {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: #2563eb;
  cursor: pointer;
  font-weight: 700;
  min-width: 0;
}

.user-name {
  max-width: 140px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-avatar {
  border: 2px solid #fff;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.08);
}

.page-main {
  padding: 20px;
  overflow-y: auto;
  background: transparent;
}

:deep(.el-menu) {
  border-right: none !important;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  border-radius: 12px;
  margin-bottom: 6px;
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background-color: rgba(255, 255, 255, 0.56) !important;
  color: #2563eb !important;
}

:deep(.el-menu-item.is-active) {
  background-color: rgba(255, 255, 255, 0.84) !important;
  color: #2563eb !important;
  font-weight: 700;
}

:deep(.el-menu--inline) {
  background: transparent !important;
}

.menu-group-title {
  font-weight: 700;
}

:deep(.admin-entry),
:deep(.admin-entry span) {
  color: #dc2626 !important;
  font-weight: 700;
}

:deep(.mobile-drawer .el-drawer) {
  background: rgba(245, 248, 255, 0.96);
  backdrop-filter: blur(14px);
}

:deep(.mobile-drawer .el-drawer__body) {
  padding: 0;
}

:deep(.user-menu) {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
}

:deep(.danger-item) {
  color: #dc2626;
}

@media (max-width: 900px) {
  .top-header {
    padding: 12px 14px;
  }

  .welcome-subtitle {
    display: none;
  }

  .user-name {
    max-width: 86px;
  }

  .page-main {
    padding: 12px;
  }
}
</style>

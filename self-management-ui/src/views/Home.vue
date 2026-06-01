<template>
  <el-container style="height: 100vh; background-color: transparent;">
    
    <el-aside width="220px" class="aside-menu" style="background-color: transparent; border-right: 1px solid rgba(255,255,255,0.6);">
      <div class="logo-box" style="border-bottom: 1px solid rgba(255,255,255,0.5);">
        <span class="logo-text" style="color: #409eff; font-weight: 800; font-size: 18px; letter-spacing: 1px;">
          ☁️ 自我规划大本营
        </span>
      </div>
      
      <el-menu
        router 
        active-text-color="#409eff"
        background-color="transparent"
        text-color="#606266"
        :default-active="route.path"
        class="el-menu-vertical"
        style="border-right: none;"
      >
        <el-menu-item index="/home/dashboard">
          <el-icon><DataLine /></el-icon>
          <span>数据看板 (首页)</span>
        </el-menu-item>
        
        <el-sub-menu index="study-management">
          <template #title>
            <el-icon><Reading /></el-icon>
            <span style="font-weight: bold;">学习管理</span>
          </template>
          
          <el-menu-item index="/home/plan">
            <el-icon><Calendar /></el-icon>
            <span>我的目标计划</span>
          </el-menu-item>
          
          <el-menu-item index="/home/course">
            <el-icon><Document /></el-icon>
            <span>我的超级课表</span>
          </el-menu-item>

          <el-menu-item index="/home/focus">
            <el-icon><Timer /></el-icon>
            <span>番茄专注时刻</span>
          </el-menu-item>

          <el-menu-item index="/home/exam">
            <el-icon><Trophy /></el-icon>
            <span>我的考试计划</span>
          </el-menu-item>
        </el-sub-menu>

        <el-sub-menu index="life-management">
          <template #title>
            <el-icon><CoffeeCup /></el-icon>
            <span style="font-weight: bold;">生活管理</span>
          </template>
          
          <el-menu-item index="/home/finance">
            <el-icon><Money /></el-icon>
            <span>我的财务账本</span>
          </el-menu-item>
          <el-menu-item index="/home/weight">
            <el-icon><TrendCharts /></el-icon>
            <span>体重体形管理</span>
          </el-menu-item>
        </el-sub-menu>
        
        <el-menu-item index="/home/profile">
         <el-icon><User /></el-icon>
         <span>个人信息中心</span>
       </el-menu-item>

       <el-menu-item index="/home/admin" v-if="currentUser.role === 'ADMIN'">
         <el-icon><Setting /></el-icon>
         <span style="color: #f56c6c; font-weight: bold;">管理员控制台</span>
       </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header style="background: rgba(255,255,255,0.4); backdrop-filter: blur(12px); border-bottom: 1px solid rgba(255,255,255,0.6); display: flex; align-items: center; justify-content: space-between; padding: 0 20px;">
        
        <div style="color: #606266; font-size: 15px; font-weight: bold; letter-spacing: 1px;">
          ✨ 欢迎回来！今天是自律的一天
        </div>
        
        <el-dropdown @command="handleCommand">
          <span class="el-dropdown-link" style="color: #409eff; cursor: pointer; display: flex; align-items: center; font-weight: bold;">
            <el-avatar :size="32" :src="currentUser.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" style="margin-right: 10px; border: 2px solid #fff; box-shadow: 0 2px 6px rgba(0,0,0,0.1);" />
            {{ currentUser.nickname || currentUser.username }}
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </span>
          
          <template #dropdown>
            <el-dropdown-menu style="background: rgba(255,255,255,0.8); backdrop-filter: blur(10px);">
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="logout" style="color: #f56c6c;">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>

      <el-main style="background-color: transparent; padding: 20px; overflow-y: auto;">
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
// 🌟 核心修复：只保留这唯一的一行图标导入代码！
import { DataLine, Calendar, User, ArrowDown, Money, Timer, Document, Setting, Reading, CoffeeCup, Trophy, TrendCharts } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const route = useRoute() 
const currentUser = ref(JSON.parse(localStorage.getItem('user') || '{}'))

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出系统吗？', '提示', {
      confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning',
    }).then(() => {
      localStorage.removeItem('user') 
      router.push('/login')
      ElMessage.success('已退出')
    }).catch(() => {})
  } else if (command === 'profile') {
    router.push('/home/profile')
  }
}
</script>

<style scoped>
.logo-box { height: 60px; display: flex; align-items: center; justify-content: center; }
.el-menu { border-right: none !important; }

/* 左侧菜单悬停：极其柔和的白光底色 */
.el-menu-item:hover, :deep(.el-sub-menu__title:hover) {
  background-color: rgba(255, 255, 255, 0.5) !important;
  color: #409eff !important;
  border-radius: 8px;
  margin: 0 8px;
}
.el-menu-item.is-active {
  background-color: rgba(255, 255, 255, 0.8) !important;
  border-radius: 8px;
  margin: 0 8px;
  font-weight: bold;
}

/* 优雅的细滚动条 */
.el-main::-webkit-scrollbar { width: 6px; }
.el-main::-webkit-scrollbar-thumb { background-color: rgba(0, 0, 0, 0.1); border-radius: 3px; }
.el-main::-webkit-scrollbar-track { background: transparent; }

/* 修正子菜单展开后的背景色，使其保持通透感 */
:deep(.el-menu--inline) {
  background-color: transparent !important;
}
</style>
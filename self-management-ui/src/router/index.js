import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Home from '../views/Home.vue'
import Dashboard from '../views/Dashboard.vue'
import Plan from '../views/Plan.vue'
import Profile from '../views/Profile.vue'
import AdminUser from '../views/AdminUser.vue'
import Finance from '../views/Finance.vue' // 🌟 引入账本页面
import Focus from '../views/Focus.vue' // 🌟 引入番茄钟页面
import Course from '../views/Course.vue' // 🌟 引入超级课表页面
import Exam from '../views/Exam.vue' // 🌟 核心：引入新增的考试计划页面
import Weight from '../views/Weight.vue' // 🌟 引入新创建的体重管理组件

const routes = [
  { path: '/', redirect: '/login' },
  { path: '/login', component: Login },
  { path: '/register', component: Register },
  
  {
    path: '/home',
    component: Home,
    redirect: '/home/dashboard',
    children: [
      { path: 'dashboard', component: Dashboard },
      { path: 'plan', component: Plan },
      { path: 'profile', component: Profile },
      { path: 'finance', component: Finance },
      { path: 'focus', component: Focus },
      { path: 'course', component: Course },
      { path: 'exam', component: Exam }, // 🌟 核心：成功接入考试计划子路由
      { path: 'admin', component: AdminUser },
      { path: 'course', component: Course },
      { path: 'exam', component: Exam },
      { path: 'weight', component: Weight }, // 🌟 注入体重数据路由
      { path: 'admin', component: AdminUser }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 🌟🌟🌟 前端拦截器（路由守卫）🌟🌟🌟
router.beforeEach((to, from, next) => {
  const user = localStorage.getItem('user')

  if (to.path === '/login' || to.path === '/register') {
    return next()
  }

  if (!user) {
    return next('/login')
  }

  next()
})

export default router
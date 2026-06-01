import { createRouter, createWebHashHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import Home from '../views/Home.vue'
import Dashboard from '../views/Dashboard.vue'
import Plan from '../views/Plan.vue'
import Profile from '../views/Profile.vue'
import AdminUser from '../views/AdminUser.vue'
import Finance from '../views/Finance.vue'
import Focus from '../views/Focus.vue'
import Course from '../views/Course.vue'
import Exam from '../views/Exam.vue'
import Weight from '../views/Weight.vue'

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
      { path: 'exam', component: Exam },
      { path: 'admin', component: AdminUser },
      { path: 'weight', component: Weight }
    ]
  }
]

const router = createRouter({
  history: createWebHashHistory(import.meta.env.BASE_URL),
  routes
})

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

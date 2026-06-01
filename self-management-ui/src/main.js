import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import axios from 'axios'

const app = createApp(App)

// 🌟 自动导航仪：本地用 localhost，公网用 cpolar 的 8080 后端链接
axios.defaults.baseURL = window.location.hostname === 'localhost' 
  ? 'http://localhost:8080' 
  : 'https://2dd88073.r3.cpolar.cn'

app.use(ElementPlus)
app.use(router)
app.mount('#app')
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import axios from 'axios'

const app = createApp(App)

const apiBaseUrl =
  import.meta.env.VITE_API_BASE_URL ||
  (window.location.hostname === 'localhost'
    ? 'http://localhost:8080'
    : 'https://2dd88073.r3.cpolar.cn')

axios.defaults.baseURL = apiBaseUrl

app.use(ElementPlus)
app.use(router)
app.mount('#app')

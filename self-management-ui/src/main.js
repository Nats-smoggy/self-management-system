import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import axios from 'axios'

const app = createApp(App)

const resolveApiBaseUrl = () => {
  const runtimeHost = window.location.hostname

  if (runtimeHost === 'localhost' || runtimeHost === '127.0.0.1') {
    return 'http://localhost:8080'
  }

  if (runtimeHost.endsWith('github.io')) {
    return 'https://self-management-system-api.onrender.com'
  }

  return import.meta.env.VITE_API_BASE_URL || 'https://self-management-system-api.onrender.com'
}

const apiBaseUrl = resolveApiBaseUrl()

axios.defaults.baseURL = apiBaseUrl
axios.defaults.withCredentials = true

app.use(ElementPlus)
app.use(router)
app.mount('#app')

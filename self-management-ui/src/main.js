import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import router from './router'
import axios from 'axios'
import { apiBaseUrl } from './utils/api'
import { installLocalAxiosAdapter, seedIfNeeded } from './utils/localApi'

const app = createApp(App)

axios.defaults.baseURL = apiBaseUrl
axios.defaults.withCredentials = true
seedIfNeeded()
installLocalAxiosAdapter(axios)

app.use(ElementPlus)
app.use(router)
app.mount('#app')

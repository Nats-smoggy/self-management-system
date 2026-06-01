import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    // 允许局域网监听
    host: '0.0.0.0', 
    
    // 🌟 1. 允许的前端公网域名
    allowedHosts: [
      '12ed7c4d.r3.cpolar.cn'
    ],
    
    // 🌟 2. 配置热更新（HMR），必须和上面的域名一模一样
    hmr: {
      host: '12ed7c4d.r3.cpolar.cn',
      clientPort: 443,
      protocol: 'wss'
    }
  }
})
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  base: '/self-management-system/',
  plugins: [vue()],
  server: {
    host: '0.0.0.0',
    allowedHosts: ['12ed7c4d.r3.cpolar.cn'],
    hmr: {
      host: '12ed7c4d.r3.cpolar.cn',
      clientPort: 443,
      protocol: 'wss'
    }
  }
})

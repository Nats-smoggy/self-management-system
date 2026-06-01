import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, process.cwd(), '')

  return {
    base: env.VITE_PUBLIC_BASE || '/',
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
  }
})

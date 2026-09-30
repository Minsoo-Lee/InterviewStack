import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    // 홈 서버 이식 시 5173 포트가 이미 다른 서비스에서 쓰이고 있어서 5174로 고정.
    port: 5174,
  },
})

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// Vite 配置: 端口 5173, 代理 /api 到后端 8080
  // 生产部署到 GitHub Pages 子路径 https://520lcz.github.io/library-management-system/
export default defineConfig(({ mode }) => ({
  plugins: [vue()],
  base: mode === 'production' ? '/library-management-system/' : '/',
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5173,
    host: '127.0.0.1',
    proxy: {
      '/api': {
        target: 'http://127.0.0.1:8080',
        changeOrigin: true
        // rewrite 不改路径, 后端直接吃 /api/xxx
      }
    }
  },
  build: {
    target: 'es2015',
    cssTarget: 'chrome61',
    outDir: 'dist',
    chunkSizeWarningLimit: 1500
  }
}))

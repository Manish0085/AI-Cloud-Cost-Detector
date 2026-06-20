import path from "node:path"
import { defineConfig } from "vite"
import react from "@vitejs/plugin-react"
import tailwindcss from "@tailwindcss/vite"

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), tailwindcss()],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
  server: {
    port: 5173,
    proxy: {
      // Same-origin in dev → avoids CORS (backend has no CORS config).
      "/api": {
        target: "http://localhost:8081",
        changeOrigin: true,
      },
      // Spring Security OAuth2 endpoints (authorization + provider callback).
      "/oauth2": {
        target: "http://localhost:8081",
        changeOrigin: true,
      },
      "/login/oauth2": {
        target: "http://localhost:8081",
        changeOrigin: true,
      },
    },
  },
})

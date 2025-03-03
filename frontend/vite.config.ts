import path from "path";
import react from "@vitejs/plugin-react-swc";
import { defineConfig } from "vitest/config";

export default defineConfig({
  base: "/todos",
  server: {
    port: 8080,
  },
  plugins: [react()],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
  test: {
    globals: true, 
    environment: 'jsdom', 
    setupFiles: './src/tests/setup.ts', // conf file
  },
});

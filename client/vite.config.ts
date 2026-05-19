import path from "node:path";
import react from "@vitejs/plugin-react";
import { defineConfig } from "vite";

const cacheRoot = process.env.TEMP ?? process.env.TMP ?? ".";

export default defineConfig({
  cacheDir: path.resolve(cacheRoot, "magic-fridge-vite-cache"),
  plugins: [react()],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
});

import { fileURLToPath, URL } from "node:url";
import vue from "@vitejs/plugin-vue";
import { coverageConfigDefaults, defineConfig } from "vitest/config";

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
      "~": fileURLToPath(new URL("./", import.meta.url)),
    },
  },
  test: {
    fakeTimers: {
      toFake: ["setTimeout", "nextTick"],
    },
    globals: true,
    clearMocks: true,
    environment: "happy-dom",
    coverage: {
      provider: "v8",
      reporter: [["lcov", { projectRoot: "./src" }]],
      exclude: [
        ...coverageConfigDefaults.exclude,
        "utilities/**/*",
        "mocks/**/*",
        "dist/**/*",
        "public/**/*",
        "src/router/routes.ts",
        "src/router/index.ts",
        "src/config/*",
        "src/utilities/*",
        "**/__mocks__/**",
      ],
    },
    setupFiles: ["vitest.setup.ts", "dotenv/config"],
  },
});

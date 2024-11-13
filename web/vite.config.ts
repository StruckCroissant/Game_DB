import { fileURLToPath, URL } from "node:url";
import { defineConfig, loadEnv } from "vite";
import vue from "@vitejs/plugin-vue";
import istanbul from "vite-plugin-istanbul";
import type { UserConfig } from "vite";
import { stripAttribute } from "./app/sfc";
import { NodeTypes } from "./app/types";

const vuePlugin = () =>
  vue({
    template: {
      compilerOptions: {
        nodeTransforms: [
          (node) => {
            if (process.env.NODE_ENV !== "production") return;
            if (node.type === NodeTypes.ROOT) return;

            stripAttribute(node, "data-testid");
          },
        ],
      },
    },
  });

const defaultConfig: () => UserConfig = () => ({
  plugins: [vuePlugin()],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
      "~": fileURLToPath(new URL("./", import.meta.url)),
    },
  },
  server: {
    port: parseInt(String(process.env.VITE_APP_PORT ?? "5173")),
    strictPort: true,
  },
  preview: {
    port: parseInt(process.env.VITE_APP_PORT ?? "9191"),
    strictPort: true,
  },
  envDir: ".",
  envPrefix: "API",
});

export default async function config({ mode }): Promise<UserConfig> {
  process.env = { ...process.env, ...loadEnv(mode, process.cwd()) };
  const config = { ...defaultConfig() };

  if (process.env.INSTRUMENT_BUILD) {
    config.build = {
      ...(config?.build ?? {}),
      sourcemap: true,
    };
    config.plugins.push(
      istanbul({
        requireEnv: false,
        forceBuildInstrument: true,
        checkProd: false,
      })
    );
  }

  return defineConfig(config);
}

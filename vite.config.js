import { defineConfig } from 'vite';
import squint from '@w3t-ab/vite-plugin-squint';
import solid from 'vite-plugin-solid';

export default defineConfig({
  plugins: [squint({ scan: true }), solid()],
  publicDir: 'resources/public',
  build: { outDir: 'dist', target: 'esnext' },
  // `npm run dev` with the API: run `node server/index.mjs` on :8080 too.
  server: { proxy: { '/api': 'http://localhost:8080' } },
});

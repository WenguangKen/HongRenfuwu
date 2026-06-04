var __assign = (this && this.__assign) || function () {
    __assign = Object.assign || function(t) {
        for (var s, i = 1, n = arguments.length; i < n; i++) {
            s = arguments[i];
            for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p))
                t[p] = s[p];
        }
        return t;
    };
    return __assign.apply(this, arguments);
};
import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import path from 'path';
import Components from 'unplugin-vue-components/vite';
import { AntDesignVueResolver } from 'unplugin-vue-components/resolvers';
import AutoImport from 'unplugin-auto-import/vite';
import { visualizer } from 'rollup-plugin-visualizer';
// https://vite.dev/config/
export default defineConfig(function (_a) {
    var mode = _a.mode;
    var enableAnalyze = process.env.ANALYZE === 'true' || mode === 'analyze';
    var isProd = mode === 'production';
    return {
        plugins: [
            vue(),
            Components({
                resolvers: [
                    AntDesignVueResolver({
                        importStyle: false,
                        resolveIcons: true,
                    }),
                ],
                dts: true,
                exclude: [/[/\\]node_modules[/\\]/, /[/\\]\.git[/\\]/, /[/\\]\.nuxt[/\\]/],
            }),
            AutoImport({
                imports: ['vue', 'vue-router', 'pinia'],
                dts: 'src/auto-imports.d.ts',
            }),
            enableAnalyze &&
                visualizer({
                    filename: 'dist/stats.html',
                    gzipSize: true,
                    brotliSize: true,
                    open: false,
                }),
        ].filter(Boolean),
        resolve: {
            alias: {
                '@': path.resolve(__dirname, './src'),
            },
        },
        server: {
            host: '0.0.0.0',
            port: 5173,
            allowedHosts: true,
            proxy: {
                // 红人管理服务 - 分佣订单等 (端口 8082)
                '/api/influencer': {
                    target: 'http://127.0.0.1:8082',
                    changeOrigin: true,
                    rewrite: function (path) { return path.replace(/^\/api\/influencer/, '/influencer-api/influencer'); },
                },
                // 红人管理服务 (端口 8082)
                '/api/influencer-api': {
                    target: 'http://127.0.0.1:8082',
                    changeOrigin: true,
                    rewrite: function (path) { return path.replace(/^\/api/, ''); },
                },
                '/api/v1/influencer': {
                    target: 'http://127.0.0.1:8082',
                    changeOrigin: true,
                    rewrite: function (path) { return path.replace(/^\/api/, '/influencer-api'); },
                },
                '/influencer-api': {
                    target: 'http://127.0.0.1:8082',
                    changeOrigin: true,
                    ws: false,
                    secure: false,
                },
                // AI 智能体服务 (端口 8085，SSE 流式对话)
                '/ai-agent-api': {
                    target: 'http://127.0.0.1:8085',
                    changeOrigin: true,
                    ws: false,
                    secure: false,
                    configure: function (proxy) {
                        proxy.on('proxyReq', function (proxyReq, req) {
                            var auth = req.headers.authorization;
                            if (auth) {
                                proxyReq.setHeader('Authorization', auth);
                            }
                        });
                    },
                },
                // Eccang 集成服务 (端口 8081)
                '/api/eccang': {
                    target: 'http://127.0.0.1:8081',
                    changeOrigin: true,
                },
                // Webhook 服务 (端口 8083)
                '/api/webhook': {
                    target: 'http://127.0.0.1:8083',
                    changeOrigin: true,
                    rewrite: function (path) { return path.replace(/^\/api\/webhook/, ''); },
                },
                // 用户权限服务 (端口 8080) - 兜底
                '/api': {
                    target: 'http://127.0.0.1:8080',
                    changeOrigin: true,
                },
            },
        },
        build: __assign({ chunkSizeWarningLimit: 1200, 
            // 生产环境使用 esbuild 压缩（默认）
            minify: 'esbuild', rollupOptions: {
                output: {
                    manualChunks: function (id) {
                        if (id.includes('node_modules')) {
                            if (id.includes('ant-design-vue'))
                                return 'vendor-antdv';
                            if (id.includes('vue'))
                                return 'vendor-vue';
                            if (id.includes('@sentry'))
                                return 'vendor-sentry';
                            if (id.includes('exceljs'))
                                return 'vendor-exceljs';
                            if (id.includes('dayjs'))
                                return 'vendor-dayjs';
                            if (id.includes('axios'))
                                return 'vendor-axios';
                            return 'vendor';
                        }
                    },
                },
            } }, (isProd && {
            esbuildOptions: {
                drop: ['console', 'debugger'],
            },
        })),
        // 优化依赖预构建
        optimizeDeps: {
            include: ['vue', 'vue-router', 'pinia', 'axios', 'dayjs', 'ant-design-vue'],
        },
    };
});

/**
 * 环境变量类型定义
 */
interface ImportMetaEnv {
  readonly VITE_API_BASE_URL?: string;
  readonly VITE_SENTRY_DSN?: string;
  readonly VITE_PERM_BOOTSTRAP?: 'local' | 'remote';
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}


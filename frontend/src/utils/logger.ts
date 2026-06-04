/**
 * 统一日志管理工具
 * 替换console.log/error/warn，支持生产环境自动禁用
 */

type LogLevel = 'debug' | 'info' | 'warn' | 'error';

interface LogEntry {
  level: LogLevel;
  message: string;
  data?: unknown;
  timestamp: string;
}

class Logger {
  private isDev = import.meta.env.DEV;
  private isProduction = import.meta.env.PROD;
  private logHistory: LogEntry[] = [];
  private maxHistorySize = 100;

  private formatMessage(level: LogLevel, message: string, data?: unknown): string {
    const timestamp = new Date().toISOString();
    return `[${timestamp}] [${level.toUpperCase()}] ${message}`;
  }

  private addToHistory(level: LogLevel, message: string, data?: unknown): void {
    this.logHistory.push({
      level,
      message,
      data,
      timestamp: new Date().toISOString(),
    });

    // 限制历史记录大小
    if (this.logHistory.length > this.maxHistorySize) {
      this.logHistory.shift();
    }
  }

  /**
   * 调试日志（仅开发环境）
   */
  debug(message: string, ...args: unknown[]): void {
    if (!this.isDev) return;
    const formatted = this.formatMessage('debug', message);
    console.debug(formatted, ...args);
    this.addToHistory('debug', message, args.length > 0 ? args : undefined);
  }

  /**
   * 信息日志（开发环境 + 生产环境可选）
   */
  info(message: string, ...args: unknown[]): void {
    if (!this.isDev) return;
    const formatted = this.formatMessage('info', message);
    console.info(formatted, ...args);
    this.addToHistory('info', message, args.length > 0 ? args : undefined);
  }

  /**
   * 警告日志
   */
  warn(message: string, ...args: unknown[]): void {
    const formatted = this.formatMessage('warn', message);
    if (this.isDev) {
      console.warn(formatted, ...args);
    }
    this.addToHistory('warn', message, args.length > 0 ? args : undefined);

    // 生产环境可以选择上报警告
    if (this.isProduction) {
      // 可以集成错误监控服务（如Sentry）
      // this.reportToSentry('warn', message, args);
    }
  }

  /**
   * 错误日志
   */
  error(message: string, error?: Error | unknown, ...args: unknown[]): void {
    const formatted = this.formatMessage('error', message);
    
    if (this.isDev) {
      console.error(formatted, error, ...args);
    }
    
    this.addToHistory('error', message, error || args.length > 0 ? args : undefined);

    // 生产环境上报错误
    if (this.isProduction && error) {
      // 可以集成错误监控服务（如Sentry）
      // if (error instanceof Error) {
      //   Sentry.captureException(error);
      // } else {
      //   Sentry.captureMessage(message, 'error');
      // }
    }
  }

  /**
   * 获取日志历史（用于调试）
   */
  getHistory(): LogEntry[] {
    return [...this.logHistory];
  }

  /**
   * 清空日志历史
   */
  clearHistory(): void {
    this.logHistory = [];
  }

  /**
   * 导出日志历史（用于错误上报）
   */
  exportHistory(): string {
    return JSON.stringify(this.logHistory, null, 2);
  }
}

// 导出单例
export const logger = new Logger();

// 导出类型
export type { LogLevel, LogEntry };


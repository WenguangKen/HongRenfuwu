import { defineStore } from 'pinia';
import type { RouteLocationNormalizedLoaded } from 'vue-router';
import { routeNameToComponentName } from '@/utils/routeHelper';

export interface TabView {
  path: string;
  fullPath: string;
  title: string;
  name: string;
}

const STORAGE_KEY = 'tabs_visited_views';
const CACHED_KEY = 'tabs_cached_views';
const ACTIVE_KEY = 'tabs_active_view';

// 从 localStorage 加载数据
function loadFromStorage(): { visitedViews: TabView[]; cachedViews: string[]; activePath: string | null } {
  try {
    const visited = localStorage.getItem(STORAGE_KEY);
    const cached = localStorage.getItem(CACHED_KEY);
    const active = localStorage.getItem(ACTIVE_KEY);
    return {
      visitedViews: visited ? JSON.parse(visited) : [],
      cachedViews: cached ? JSON.parse(cached) : [],
      activePath: active || null
    };
  } catch {
    return { visitedViews: [], cachedViews: [], activePath: null };
  }
}

// 保存到 localStorage
function saveToStorage(visitedViews: TabView[], cachedViews: string[], activePath?: string | null) {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(visitedViews));
    localStorage.setItem(CACHED_KEY, JSON.stringify(cachedViews));
    if (activePath !== undefined) {
      if (activePath) {
        localStorage.setItem(ACTIVE_KEY, activePath);
      } else {
        localStorage.removeItem(ACTIVE_KEY);
      }
    }
  } catch (error) {
    // 静默处理错误
  }
}

export const useTabsStore = defineStore('tabs', {
  state: () => {
    const { visitedViews, cachedViews, activePath } = loadFromStorage();
    return {
      visitedViews: visitedViews as TabView[],
      cachedViews: cachedViews as string[],
      activePath: activePath as string | null
    };
  },
  actions: {
    addView(route: RouteLocationNormalizedLoaded) {
      const MAX_TABS = 15;
      const path = route.path;
      if (!path) return;

      const name = (route.name as string) || '';
      const title =
        (route.meta?.title as string) ||
        name ||
        path;

      const existing = this.visitedViews.find((v) => v.path === path);
      if (existing) {
        existing.fullPath = route.fullPath;
        existing.title = title;
        existing.name = name;
      } else {
        this.visitedViews.push({
          path,
          fullPath: route.fullPath,
          title,
          name
        });
      }

      // 将路由 name 转换为组件名（PascalCase）
      if (name) {
        const componentName = routeNameToComponentName(name);
        if (componentName && !this.cachedViews.includes(componentName)) {
          this.cachedViews.push(componentName);
        }
      }

      // LRU：超过上限时淘汰最久未使用的标签（队头）
      if (this.visitedViews.length > MAX_TABS) {
        const oldest = this.visitedViews[0];
        // 不淘汰当前刚加入的同一路径
        if (oldest && oldest.path !== path) {
          // 从 visitedViews 移除
          this.visitedViews.shift();
          // 同步移除对应的缓存组件
          if (oldest.name) {
            const oldestComp = routeNameToComponentName(oldest.name);
            if (oldestComp) {
              const ci = this.cachedViews.indexOf(oldestComp);
              if (ci !== -1) this.cachedViews.splice(ci, 1);
            }
          }
        } else {
          // 如果 oldest 就是当前，移除第二个元素
          if (this.visitedViews.length > 1) {
            const removed = this.visitedViews.splice(1, 1)[0];
            if (removed?.name) {
              const rc = routeNameToComponentName(removed.name);
              if (rc) {
                const idx = this.cachedViews.indexOf(rc);
                if (idx !== -1) this.cachedViews.splice(idx, 1);
              }
            }
          }
        }
      }

      // 更新最后活跃路径
      this.activePath = route.fullPath;

      // 持久化到 localStorage
      saveToStorage(this.visitedViews, this.cachedViews, this.activePath);
    },
    removeView(path: string): string | null {
      const index = this.visitedViews.findIndex((v) => v.path === path);
      if (index === -1) return null;

      const [removed] = this.visitedViews.splice(index, 1);

      if (removed?.name) {
        // 将路由 name 转换为组件名（PascalCase）
        const componentName = routeNameToComponentName(removed.name);
        if (componentName) {
          const ci = this.cachedViews.indexOf(componentName);
          if (ci !== -1) {
            this.cachedViews.splice(ci, 1);
          }
        }
      }

      const target =
        this.visitedViews[index - 1] ||
        this.visitedViews[index] ||
        null;

      this.activePath = target ? target.fullPath : '/dashboard';

      // 持久化到 localStorage
      saveToStorage(this.visitedViews, this.cachedViews, this.activePath);

      return this.activePath;
    },
    moveView(fromIndex: number, toIndex: number) {
      if (fromIndex === toIndex) return;

      // 确保索引在有效范围内
      fromIndex = Math.max(0, Math.min(fromIndex, this.visitedViews.length - 1));
      toIndex = Math.max(0, Math.min(toIndex, this.visitedViews.length));

      // 如果从前往后移动，目标位置需要调整
      if (fromIndex < toIndex) {
        toIndex = toIndex - 1;
      }

      if (fromIndex === toIndex) return;

      const [moved] = this.visitedViews.splice(fromIndex, 1);
      this.visitedViews.splice(toIndex, 0, moved!);

      // 持久化到 localStorage
      saveToStorage(this.visitedViews, this.cachedViews);
    },
    clearAll() {
      this.visitedViews = [];
      this.cachedViews = [];
      this.activePath = null;
      saveToStorage([], [], null);
    }
  }
});

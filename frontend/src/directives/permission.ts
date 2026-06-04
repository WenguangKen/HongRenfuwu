import type { Directive } from 'vue';
import { watch } from 'vue';
import { usePermissionStore } from '@/stores/permission';

const apply = (el: HTMLElement, allowed: boolean) => {
  if (!allowed) {
    el.style.display = 'none';
  } else {
    el.style.display = '';
  }
};

const permission: Directive<HTMLElement, string | string[]> = {
  mounted(el, binding) {
    const store = usePermissionStore();
    const value = binding.value;
    
    const checkPermission = () => {
      const allowed = Array.isArray(value) 
        ? value.some(k => store.isAllowedOp(k)) 
        : store.isAllowedOp(value);
      apply(el, allowed);
    };
    
    // 初始检查
    checkPermission();
    
    // 监听权限变化
    const stopWatcher = watch(
      () => store.operationPermissions,
      () => {
        checkPermission();
      },
      { deep: true }
    );
    
    // 将清理函数存储到元素上，以便在 unmounted 时清理
    (el as any).__permissionWatcher = stopWatcher;
  },
  updated(el, binding) {
    const store = usePermissionStore();
    const value = binding.value;
    const allowed = Array.isArray(value) ? value.some(k => store.isAllowedOp(k)) : store.isAllowedOp(value);
    apply(el, allowed);
  },
  unmounted(el) {
    // 清理 watch
    const stopWatcher = (el as any).__permissionWatcher;
    if (stopWatcher) {
      stopWatcher();
      delete (el as any).__permissionWatcher;
    }
  }
};

export default permission;

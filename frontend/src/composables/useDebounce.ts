import { ref, watch, onUnmounted } from 'vue';
import type { Ref } from 'vue';

export function useDebounce<T>(value: Ref<T>, delay = 300) {
  const debouncedValue = ref(value.value) as Ref<T>;
  let timer: number | null = null;

  watch(value, (newValue) => {
    if (timer) clearTimeout(timer);
    timer = window.setTimeout(() => {
      debouncedValue.value = newValue;
    }, delay);
  });

  onUnmounted(() => {
    if (timer) clearTimeout(timer);
  });

  return debouncedValue;
}

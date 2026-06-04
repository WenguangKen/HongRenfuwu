import { reactive, ref } from 'vue';

export function useTableFilter<T extends Record<string, any>>(initialFilter: T) {
  const filterForm = reactive({ ...initialFilter }) as T;
  const filterExpanded = ref(false);
  const initialSnapshot = JSON.parse(JSON.stringify(initialFilter));

  const handleFilter = (fn?: (form: T) => void) => {
    if (fn) fn(filterForm);
  };

  const handleResetFilter = (fn?: (form: T) => void) => {
    Object.assign(filterForm, initialSnapshot);
    if (fn) fn(filterForm);
  };

  return {
    filterForm,
    filterExpanded,
    handleFilter,
    handleResetFilter,
  };
}

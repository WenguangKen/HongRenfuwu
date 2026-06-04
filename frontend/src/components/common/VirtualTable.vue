<template>
  <div class="virtual-table" :style="{ maxHeight: scrollY + 'px' }">
    <div class="v-header" :style="headerGridStyle">
      <div v-if="withSelection" class="hcell sticky-left select">选择</div>
      <template v-for="col in columns" :key="col.key">
        <div
          class="hcell"
          :class="[
            col.fixed === 'left' ? 'sticky-left' : '',
            col.fixed === 'right' ? 'sticky-right' : ''
          ]"
        >
          <span class="title">{{ col.title }}</span>
          <span v-if="col.sorter" class="sort-icons">
            <a-space size="0">
              <a-button type="link" size="small" @click="emitSort(col.key, 'ascend')">▲</a-button>
              <a-button type="link" size="small" @click="emitSort(col.key, 'descend')">▼</a-button>
            </a-space>
          </span>
        </div>
      </template>
    </div>
    <VirtualList
      :data-key="'__rowkey__'"
      :data-sources="rowsWithKey"
      :keeps="keeps"
      :estimate-size="estimateSize"
      :data-component="VirtualTableRow"
      :extra-props="rowExtraProps"
      class="v-body"
    />
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import VirtualList from 'vue3-virtual-scroll-list';
import VirtualTableRow from './VirtualTableRow.vue';

type Align = 'left' | 'center' | 'right';
type VColumn = {
  title: string;
  key: string;
  dataIndex?: string;
  width?: number;
  fixed?: 'left' | 'right';
  align?: Align;
  sorter?: boolean;
  render?: any;
};

const props = defineProps<{
  columns: VColumn[];
  dataSources: any[];
  rowKey?: string | ((record: any) => string | number);
  keeps?: number;
  estimateSize?: number;
  rowSelection?: { selectedRowKeys: Array<string | number>; onChange: (keys: Array<string | number>) => void };
  scrollY?: number;
}>();

const emits = defineEmits<{
  (e: 'sort', payload: { key: string; order: 'ascend' | 'descend' }): void;
}>();

const withSelection = computed(() => !!props.rowSelection);
const keeps = computed(() => props.keeps ?? 20);
const estimateSize = computed(() => props.estimateSize ?? 64);
const scrollY = computed(() => props.scrollY ?? 600);

const colWidths = computed(() => props.columns.map(c => (c.width ? `${c.width}px` : 'auto')));
const headerGridStyle = computed(() => ({
  display: 'grid',
  gridTemplateColumns: `${withSelection.value ? '50px ' : ''}${colWidths.value.join(' ')}`,
  gap: '8px',
  padding: '10px 16px',
  borderBottom: '1px solid #f1f5f9',
  position: 'sticky',
  top: '0',
  background: '#f8fafc',
  zIndex: 5
}) as any);

const calcRowKey = (r: any, idx: number) => {
  if (typeof props.rowKey === 'function') return props.rowKey(r);
  if (typeof props.rowKey === 'string') return r[props.rowKey];
  return r.key ?? r.id ?? idx;
};

const rowsWithKey = computed(() =>
  props.dataSources.map((r, idx) => ({ ...r, __rowkey__: calcRowKey(r, idx) }))
);

const rowExtraProps = computed(() => {
  const selectedKeys = props.rowSelection?.selectedRowKeys ?? [];
  const onToggleSelect = (key: string | number) => {
    if (!props.rowSelection) return;
    const set = new Set(props.rowSelection.selectedRowKeys);
    if (set.has(key)) set.delete(key);
    else set.add(key);
    props.rowSelection.onChange(Array.from(set));
  };
  return {
    columns: props.columns,
    withSelection: withSelection.value,
    onToggleSelect,
    gridColumns: colWidths.value,
    selectedKeys,
    rowKeyValue: (row: any) => row.__rowkey__
  };
});

const emitSort = (key: string, order: 'ascend' | 'descend') => emits('sort', { key, order });
</script>

<script lang="ts">
export default {
  name: 'VirtualTable'
};
</script>

<style scoped>
.v-body { max-height: 600px; overflow-y: auto; }
.hcell { font-weight: 700; color: #334155; background: #f8fafc; }
.sticky-left { position: sticky; left: 0; z-index: 6; }
.sticky-right { position: sticky; right: 0; z-index: 6; }
.sort-icons { margin-left: 6px; }
</style>

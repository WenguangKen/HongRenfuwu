<template>
  <div class="v-row" :style="gridStyle">
    <div v-if="withSelection" class="cell sticky-left select">
      <a-checkbox :checked="checked" @change="toggle" />
    </div>
    <template v-for="col in columns" :key="col.key">
      <div
        class="cell"
        :class="[
          col.fixed === 'left' ? 'sticky-left' : '',
          col.fixed === 'right' ? 'sticky-right' : '',
          col.align ? `align-${col.align}` : ''
        ]"
      >
        <component :is="col.render ? col.render : DefaultCell" :record="record" :column="col" />
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { computed, defineComponent } from 'vue';

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
  record: any;
  columns: VColumn[];
  withSelection?: boolean;
  selectedKeys?: Array<string | number>;
  onToggleSelect?: (key: string | number) => void;
  rowKeyValue: string | number;
  gridColumns: string[];
}>();

const gridStyle = computed(() => ({
  display: 'grid',
  gridTemplateColumns: `${props.withSelection ? '50px ' : ''}${props.gridColumns.join(' ')}`,
  alignItems: 'center',
  gap: '8px',
  minHeight: '56px',
  padding: '8px 12px',
  borderBottom: '1px solid #f1f5f9',
  background: '#fff'
}));

const toggle = () => props.onToggleSelect?.(props.rowKeyValue);
const checked = computed(() => (props.selectedKeys ?? []).includes(props.rowKeyValue));

const DefaultCell = defineComponent({
  props: ['record', 'column'],
  setup(p: any) {
    return () => {
      const di = p.column?.dataIndex;
      const val = di ? p.record?.[di] : undefined;
      return val !== undefined && val !== null ? String(val) : '-';
    };
  }
});
</script>

<script lang="ts">
export default {
  name: 'VirtualTableRow'
};
</script>

<style scoped>
.cell { overflow: hidden; white-space: nowrap; text-overflow: ellipsis; background: #fff; }
.sticky-left { position: sticky; left: 0; z-index: 2; }
.sticky-right { position: sticky; right: 0; z-index: 2; }
.align-center { text-align: center; }
.align-right { text-align: right; }
</style>

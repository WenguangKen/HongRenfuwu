<template>
  <a-modal
    v-model:open="visible"
    title="选择产品系列"
    width="1200px"
    centered
    :footer="null"
    class="premium-selector-modal"
    destroyOnClose
  >
    <div class="selector-container">
      <!-- Clean Search & Filter Section -->
      <div class="search-section">
        <div class="search-inner">
          <div class="search-input-wrapper">
            <a-input 
              v-model:value="searchKeyword" 
              placeholder="搜索产品系列名称..." 
              allow-clear
              class="pro-search-input"
            >
              <template #prefix><search-outlined style="color: #637381" /></template>
            </a-input>
          </div>
        </div>
      </div>
      
      <!-- Grid Area -->
      <div class="grid-viewport">
        <div v-if="loading" class="grid-loading-state">
          <a-spin size="large" />
          <div class="loading-text">正在同步产品系列...</div>
        </div>
        
        <div v-else-if="filteredCollections.length > 0" class="collection-grid">
          <div 
            v-for="col in filteredCollections" 
            :key="col.id"
            class="pro-card"
            :class="{ 'is-selected': selectedKeys.includes(col.id) }"
            @click="toggleSelection(col.id)"
          >
            <div class="card-media">
              <div class="col-visual" :style="getColStyle(col.id)">
                <span class="col-initial">{{ col.title.charAt(0).toUpperCase() }}</span>
              </div>
              
              <div class="selection-check">
                <div class="check-dot">
                  <check-outlined v-if="selectedKeys.includes(col.id)" />
                </div>
              </div>
            </div>

            <div class="card-body">
              <div class="content-top">
                <a-tooltip placement="top">
                  <template #title>{{ col.title }}</template>
                  <div class="card-title">{{ col.title }}</div>
                </a-tooltip>
                <div class="id-tag">ID: {{ col.id }}</div>
              </div>
              
              <div class="product-footer">
                <div class="card-count">
                  <span class="num">{{ col.productsCount || 0 }}</span> 个商品
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-else class="empty-view">
          <a-empty description="未发现匹配的产品系列" />
        </div>
      </div>

      <!-- Footer Dock -->
      <div class="control-footer">
        <div class="footer-left">
           <div class="selection-info">
            已选择 <span class="num">{{ selectedKeys.length }}</span> 个系列
          </div>
        </div>
        <div class="footer-right">
          <a-button @click="handleCancel" class="cancel-btn">取 消</a-button>
          <a-button 
            type="primary" 
            class="action-btn"
            @click="handleOk" 
            :disabled="selectedKeys.length === 0"
          >
            确认选定
          </a-button>
        </div>
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { SearchOutlined, CheckOutlined } from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
export interface ShopifyCollection {
  id: string;
  title: string;
  productsCount?: number;
}
const getShopifyCollections = async (storeId: number): Promise<ShopifyCollection[]> => [];

const props = defineProps<{
  open: boolean;
  initialSelectedKeys?: (string|number)[];
  storeId?: number;
}>();

const emit = defineEmits(['update:open', 'select']);

const visible = computed({
  get: () => props.open,
  set: (val) => emit('update:open', val),
});

const searchKeyword = ref('');
const selectedKeys = ref<(string|number)[]>([]);
const loading = ref(false);
const collections = ref<ShopifyCollection[]>([]);

// Sync Visibility
watch(() => props.open, async (val) => {
  if (val) {
    selectedKeys.value = props.initialSelectedKeys || [];
    searchKeyword.value = '';
    await fetchCollections();
  }
});

const fetchCollections = async () => {
  if (!props.storeId) return;
  
  loading.value = true;
  try {
    const result = await getShopifyCollections(props.storeId);
    collections.value = result || [];
  } catch (e: any) {
    message.error('无法同步获取系列列表');
  } finally {
    loading.value = false;
  }
};

const filteredCollections = computed(() => {
  if (!searchKeyword.value) return collections.value;
  const kw = searchKeyword.value.toLowerCase();
  return collections.value.filter((c: any) => 
    c.title.toLowerCase().includes(kw)
  );
});

const toggleSelection = (id: string | number) => {
  const index = selectedKeys.value.indexOf(id);
  if (index > -1) {
    selectedKeys.value.splice(index, 1);
  } else {
    selectedKeys.value.push(id);
  }
};

const handleOk = () => {
  const selectedItems = collections.value.filter((c: any) => selectedKeys.value.includes(c.id));
  emit('select', selectedItems);
  visible.value = false;
};

const handleCancel = () => {
  visible.value = false;
};

// Style generator for collections without images
const COLORS = [
  'linear-gradient(135deg, #FF9A9E 0%, #FAD0C4 100%)',
  'linear-gradient(135deg, #A18CD1 0%, #FBC2EB 100%)',
  'linear-gradient(135deg, #84FAB0 0%, #8FD3F4 100%)',
  'linear-gradient(135deg, #F6D365 0%, #FDA085 100%)',
  'linear-gradient(135deg, #A1C4FD 0%, #C2E9FB 100%)',
  'linear-gradient(135deg, #667EEA 0%, #764BA2 100%)'
];

const getColStyle = (id: string | number) => {
  let numId = 0;
  if (typeof id === 'string') {
    numId = id.split('').reduce((acc, char) => acc + char.charCodeAt(0), 0);
  } else {
    numId = id;
  }
  const color = COLORS[numId % COLORS.length];
  return { background: color };
};
</script>

<style scoped lang="scss">
// Premium UI Variables (Shopify Professional Green)
$pro-primary: #008060;
$pro-primary-hover: #006e52;
$pro-text: #202223;
$pro-muted: #6d7175;
$pro-border: #dfe3e8;
$pro-bg: #f6f6f7;

.premium-selector-modal {
  :deep(.ant-modal-content) {
    padding: 0;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  }
  
  :deep(.ant-modal-header) {
    padding: 16px 24px;
    border-bottom: 1px solid $pro-border;
  }
  
  :deep(.ant-modal-title) {
    font-size: 18px;
    font-weight: 700;
    color: $pro-text;
  }
}

.selector-container {
  display: flex;
  flex-direction: column;
  background: #fff;
}

// Search
.search-section {
  padding: 16px 24px;
  background: #fff;
  border-bottom: 1px solid #f1f2f3;
  .search-inner { display: flex; align-items: center; }
}

.pro-search-input {
  width: 380px;
  :deep(.ant-input-affix-wrapper) {
    border-radius: 8px;
    border-color: $pro-border !important;
    padding: 8px 16px;
    background: #fff;
    transition: all 0.2s;
    &:hover, &.ant-input-affix-wrapper-focused { 
      border-color: $pro-primary !important; 
      box-shadow: 0 0 0 2px rgba($pro-primary, 0.1) !important; 
    }
  }
}

// Viewport
.grid-viewport {
  height: 620px;
  background: $pro-bg;
  padding: 20px 24px;
  overflow-y: auto;
  position: relative;
  display: flex;
  flex-direction: column;
}

.grid-loading-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  .loading-text { font-size: 14px; color: $pro-muted; font-weight: 500; }
  :deep(.ant-spin-dot-item) { background-color: $pro-primary; }
}

.collection-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

// Card Design
.pro-card {
  background: #fff;
  border: 1px solid $pro-border;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  height: 240px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  
  &:hover {
    border-color: $pro-primary;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
    transform: translateY(-2px);
  }
  
  &.is-selected {
    border-color: $pro-primary;
    box-shadow: 0 0 0 1px $pro-primary;
  }
}

.card-media {
  width: 100%;
  height: 140px;
  position: relative;
  overflow: hidden;

  .col-visual {
    width: 100%; height: 100%;
    display: flex; align-items: center; justify-content: center;
    .col-initial { font-size: 32px; font-weight: 800; color: #fff; text-shadow: 0 2px 4px rgba(0,0,0,0.1); }
  }

  .selection-check {
    position: absolute; top: 10px; right: 10px; z-index: 5;
    .check-dot {
      width: 20px; height: 20px; border-radius: 50%;
      border: 2px solid #dfe3e8; background: #fff;
      display: flex; align-items: center; justify-content: center;
      transition: all 0.2s;
      .anticon { font-size: 11px; color: #fff; }
    }
  }
}

.is-selected .check-dot {
  background: $pro-primary !important; border-color: $pro-primary !important;
}

.card-body {
  padding: 12px 14px;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  background: #fff;
}

.content-top {
  .card-title {
    font-size: 14px; font-weight: 700; color: $pro-text;
    overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
    margin-bottom: 4px;
  }
  .id-tag { font-size: 11px; color: $pro-muted; opacity: 0.8; }
}

.product-footer {
  margin-top: auto;
  .card-count {
    font-size: 12px; color: $pro-muted;
    .num { font-weight: 700; color: $pro-primary; }
  }
}

// Footer
.control-footer {
  display: flex; justify-content: space-between; align-items: center;
  padding: 18px 24px; border-top: 1px dotted $pro-border;
}

.footer-left {
  .selection-info {
    font-size: 14px; color: $pro-text;
    .num { font-weight: 700; color: $pro-primary; margin: 0 4px;}
  }
}

.footer-right { display: flex; gap: 12px; }

.cancel-btn {
  height: 40px !important;
  padding: 0 24px !important;
  border-radius: 8px !important;
  font-weight: 600 !important;
}

.action-btn {
  background: $pro-primary !important; border: none;
  height: 40px !important;
  padding: 0 32px !important;
  border-radius: 8px !important;
  font-weight: 700 !important;
  box-shadow: 0 2px 4px rgba($pro-primary, 0.2);
  &:hover:not(:disabled) { background: $pro-primary-hover !important; box-shadow: 0 4px 12px rgba($pro-primary, 0.3); }
  &:disabled { background: #babfc3 !important; box-shadow: none; }
}

.empty-view { padding: 80px 0; }
::-webkit-scrollbar { width: 4px; }
::-webkit-scrollbar-thumb { background: #c1c4c8; border-radius: 10px; }
</style>

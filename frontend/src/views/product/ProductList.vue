<template>
  <div class="product-list-page">
    <!-- 筛选区域 -->
    <a-card :bordered="false" class="filter-card glass-card" :body-style="{ padding: '16px 20px' }">
      <a-form :model="filterForm" layout="vertical" size="middle">
        <a-row :gutter="[20, 16]">
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="商品名称">
              <a-input 
                v-model:value="filterForm.name" 
                placeholder="搜索商品名称..." 
                allow-clear 
                class="premium-input-search"
                @dblclick="openBatchSearch"
              />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="所属店铺">
              <a-select v-model:value="filterForm.storeId" placeholder="选择店铺" show-search allow-clear class="premium-select">
                <a-select-option v-for="s in stores" :key="s.id" :value="s.id">{{ s.storeName }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="商品SKU">
              <a-input v-model:value="filterForm.sku" placeholder="输入SKU..." allow-clear class="premium-input" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="商品ASIN">
              <a-input v-model:value="filterForm.asin" placeholder="输入ASIN..." allow-clear class="premium-input" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="价格范围">
              <div class="premium-price-range-wrapper">
                <a-input-number
                  v-model:value="filterForm.priceMin"
                  placeholder="Min"
                  :min="0"
                  class="range-input"
                  :controls="false"
                >
                  <template #prefix>
                    <span class="currency-symbol">$</span>
                  </template>
                </a-input-number>
                <div class="range-separator">~</div>
                <a-input-number
                  v-model:value="filterForm.priceMax"
                  placeholder="Max"
                  :min="0"
                  class="range-input"
                :controls="false"
                >
                  <template #prefix>
                    <span class="currency-symbol">$</span>
                  </template>
                </a-input-number>
              </div>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="库存状态">
              <a-select v-model:value="filterForm.inventoryStatus" placeholder="选择库存状态" allow-clear class="premium-select">
                <a-select-option value="in_stock">有货</a-select-option>
                <a-select-option value="out_of_stock">缺货</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <template v-if="filterExpanded">
             <a-col :xs="24" :sm="12" :md="8" :lg="8">
              <a-form-item label="创建时间">
                <a-range-picker 
                  v-model:value="filterForm.timeRange" 
                  format="YYYY-MM-DD" 
                  value-format="YYYY-MM-DD" 
                  class="premium-datepicker" 
                  style="width: 100%"
                />
              </a-form-item>
            </a-col>
          </template>

          <a-col :span="24" style="display: flex; justify-content: flex-end; align-items: flex-end;">
            <div class="filter-footer" style="padding-top: 0; border-top: none; margin-top: 0;">
              <a-space size="middle">
                <a-button type="primary" @click="handleFilter" class="premium-btn primary-gradient" style="height: 32px; padding: 0 20px;">
                  查询
                </a-button>
                <a-button @click="handleResetFilter" class="premium-btn" style="height: 32px; padding: 0 20px;">重置</a-button>
                <a-button type="link" @click="filterExpanded = !filterExpanded" class="expand-btn" style="font-size: 13px;">
                  {{ filterExpanded ? '收起筛选' : '更多筛选' }} <component :is="filterExpanded ? UpOutlined : DownOutlined" />
                </a-button>
              </a-space>
            </div>
          </a-col>
        </a-row>
      </a-form>
    </a-card>

    <a-card :bordered="false" class="table-card glass-card" :body-style="{ padding: '0' }">
      <template #title>
        <div class="table-actions-toolbar">
          <div class="status-switcher-wrapper">
            <a-radio-group v-model:value="activeKey" button-style="solid" class="premium-segmented" @change="handleTabChange">
              <a-radio-button value="active">已上架</a-radio-button>
              <a-radio-button value="draft">草稿</a-radio-button>
              <a-radio-button value="inactive">已下架</a-radio-button>
              <a-radio-button value="out_of_stock">缺货</a-radio-button>
              <a-radio-button value="suspected_deleted">疑似删除</a-radio-button>
            </a-radio-group>
          </div>
          
          <a-space size="small" class="toolbar-btns">
            <a-button type="primary" @click="syncModalVisible = true" class="premium-btn-small primary-gradient">
              <template #icon><SyncOutlined /></template>同步易仓商品
            </a-button>
          </a-space>
        </div>
      </template>

      <a-table
        :columns="columns"
        :data-source="displayData"
        :row-key="(record: any) => record.id || record.key"
        :pagination="false"
        :loading="{ spinning: loading, indicator: LoadingIcon }"
        :scroll="displayData.length > 0 ? { x: 'max-content', y: filterExpanded ? 'calc(100vh - 460px)' : 'calc(100vh - 350px)' } : { x: 'max-content', y: filterExpanded ? 'calc(100vh - 460px)' : 'calc(100vh - 350px)' }"
        :sticky="{ offsetScroll: 0 }"
        size="middle"
        class="premium-table"
        @change="handleTableChange"
      >
        <template #emptyText>
          <div class="table-empty-state">
            <a-empty description="暂无商品" />
          </div>
        </template>
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'productInfo'">
            <div class="user-info-cell">
              <div class="avatar-wrapper">
                 <div style="position: relative; width: 48px; height: 48px; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 6px rgba(0,0,0,0.05);">
                  <img 
                    :src="record.image" 
                    alt="商品图片"
                    style="width: 100%; height: 100%; object-fit: cover; cursor: pointer;"
                    loading="lazy"
                    @click="previewImage(record.image)"
                  />
                </div>
              </div>
              <div class="info-content">
                <div class="user-name" :title="record.name">{{ record.name }}</div>
                <div class="meta-row" style="margin-top: 2px;">
                   <span class="spu-text" @click.stop="handleCopy(record.asin)" style="cursor: pointer; color: #94a3b8; font-size: 11px; display: flex; align-items: center; gap: 4px;">
                      ASIN: {{ record.asin }} <CopyOutlined />
                   </span>
                   <span class="spu-text" @click.stop="handleCopy(record.sku)" style="cursor: pointer; color: #94a3b8; font-size: 11px; display: flex; align-items: center; gap: 4px; margin-left: 8px;">
                      SKU: {{ record.sku }} <CopyOutlined />
                   </span>
                </div>
                <div class="meta-row" style="display: flex; align-items: center; gap: 8px; margin-top: 2px;">
                  <a-tag v-if="record.vendor" color="cyan" style="margin: 0; font-size: 10px; line-height: 18px;">{{ record.vendor }}</a-tag>
                  <a-tag v-if="record.status === 'archived'" color="orange" style="margin: 0; font-size: 10px; line-height: 18px;">归档</a-tag>
                  <a-tag v-if="record.status === 'unlisted'" color="default" style="margin: 0; font-size: 10px; line-height: 18px;">下架</a-tag>
                </div>
                  <span class="sku-count" style="font-size: 11px; color: #64748b;">
                    平台: {{ record.platform || 'Amazon' }}
                  </span>
              </div>
            </div>
          </template>
          
          <template v-else-if="column.key === 'shopName'">
             <a-tag class="status-tag tag-blue">{{ record.shopName || '-' }}</a-tag>
          </template>

          <template v-else-if="column.key === 'price'">
            <div style="display: flex; flex-direction: column; align-items: center; gap: 2px;">
              <span style="font-weight: 600; color: #1e293b; font-family: 'Inter', sans-serif;">{{ record.displayPrice }}</span>
            </div>
          </template>
          
          <template v-else-if="column.key === 'fbaInventory'">
             <span :style="{ color: record.fbaInventory < 10 ? '#ef4444' : (record.fbaInventory < 50 ? '#f59e0b' : '#10b981'), fontWeight: 600 }">
              <span style="font-size: 10px; color: #64748b; margin-right: 4px;">FBA:</span>
              {{ record.fbaInventory }}
            </span>
          </template>

          <template v-else-if="column.key === 'link'">
            <div class="homepage-link-cell">
               <a :href="record.link" target="_blank" class="action-link" style="font-size: 12px; color: #3b82f6;">
                 <LinkOutlined /> 查看
               </a>
            </div>
          </template>

          <template v-else-if="column.key === 'action'">
            <div class="action-btns-wrapper">
              <a-button type="primary" size="small" @click="openDetail(record)" class="detail-btn">
                详情
              </a-button>
            </div>
          </template>
        </template>
      </a-table>
      
      <!-- 自定义底部页脚 -->
      <div class="pagination-footer">
        <div class="footer-left">
          <span class="info-item">当前商品数量 <span class="count-value">{{ pagination.total }}</span></span>
        </div>
        <div class="footer-right">
          <a-pagination
            v-model:current="pagination.current"
            v-model:pageSize="pagination.pageSize"
            :total="pagination.total"
            :show-size-changer="true"
            :show-quick-jumper="true"
            @change="onPageChange"
          />
        </div>
      </div>
    </a-card>

    <ProductImagePreview
      v-model:open="imagePreviewVisible"
      :url="previewImageUrl"
    />
    <ProductDetailModal
      v-model:open="detailVisible"
      :product-data="currentProduct"
    />
    <ProductSyncModal
      v-model:open="syncModalVisible"
      @success="handleSyncSuccess"
    />

    <!-- 批量搜索商品弹窗 -->
    <a-modal v-model:open="batchSearchVisible" title="批量搜索商品名称" @ok="handleBatchSearchOk"
      @cancel="batchSearchVisible = false" :width="500" class="premium-modal">
      <a-textarea v-model:value="batchSearchValue"
        placeholder="请输入要搜索的商品名称列表，支持回车换行或逗号分隔。&#10;&#10;例如：&#10;T-Shirt&#10;Hoodie" :rows="8" class="premium-input custom-scrollbar" />
      <div style="margin-top: 10px; color: #8c8c8c; font-size: 13px;">
        将模糊匹配输入的名称列表
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch, h } from 'vue';
import { 
  PlusOutlined, DownOutlined, UpOutlined, ExportOutlined, LoadingOutlined, 
  SearchOutlined, CloseOutlined, UserOutlined, MailOutlined, LinkOutlined, 
  BarcodeOutlined, TagOutlined, SyncOutlined, CopyOutlined
} from '@ant-design/icons-vue';
import { Empty, message } from 'ant-design-vue';
import type { TableColumnsType } from 'ant-design-vue';
import dayjs from 'dayjs';

import ProductImagePreview from '@/components/product/ProductImagePreview.vue';
import ProductDetailModal from '@/components/product/ProductDetailModal.vue';
import ProductSyncModal from '@/components/product/ProductSyncModal.vue';
import { getEccangProducts, getEccangStores } from '@/services/eccangService';
import type { EccangStoreConfig } from '@/services/eccangService';
import type { ProductItem, ProductStatus } from '@/types/product';

import { useSseStore } from '@/stores/sse';

defineOptions({
  name: 'ProductList'
});

const sseStore = useSseStore();

// Watch for SSE events to auto-refresh
watch(() => sseStore.lastEvent, (event) => {
  if (event && (
    event.category === 'products' || 
    event.category === 'inventory' || 
    (event.category === 'system' && (event.data?.topic === 'reconnect' || event.data?.topic === 'fallback_poll'))
  )) {

    fetchProducts();
  }
});

// Loading Icon
const LoadingIcon = h(LoadingOutlined, { style: { fontSize: '24px' }, spin: true });

const activeKey = ref('active');
const loading = ref(false);
const filterExpanded = ref(false);
const selectedRowKeys = ref<any[]>([]);

// Modals
const imagePreviewVisible = ref(false);
const previewImageUrl = ref('');
const detailVisible = ref(false);
const currentProduct = ref<any>(null);
const syncModalVisible = ref(false);

const stores = ref<EccangStoreConfig[]>([]);

// Pagination
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
});

// Sorter State
const currentSorter = reactive({
  columnKey: '',
  order: '' as 'ascend' | 'descend' | '',
});

// Filter
const filterForm = reactive({
  name: '',
  sku: '',
  asin: '',
  storeId: undefined as number | undefined,
  priceMin: undefined as number | undefined,
  priceMax: undefined as number | undefined,
  inventoryStatus: undefined as string | undefined,
  timeRange: undefined as [string, string] | undefined,
});

// Data
const allData = ref<ProductItem[]>([]);
const displayData = ref<ProductItem[]>([]);

const columns: TableColumnsType = [
  {
    title: '商品信息',
    key: 'productInfo',
    width: 250,
    fixed: 'left',
    align: 'left',
  },
  {
    title: '所属店铺',
    key: 'shopName',
    width: 150,
    align: 'center',
  },
  {
    title: '商品单价',
    key: 'price',
    width: 120,
    align: 'center',
    sorter: (a: any, b: any) => parseFloat(a.price) - parseFloat(b.price),
  },
  {
    title: 'FBA库存',
    key: 'fbaInventory',
    width: 100,
    align: 'center',
    sorter: (a: any, b: any) => a.fbaInventory - b.fbaInventory,
  },
  {
    title: '链接',
    key: 'link',
    width: 100,
    align: 'center',
  },
  {
    title: '操作',
    key: 'action',
    width: 100,
    fixed: 'right',
    align: 'center',
  },
];



// Filter Logic
const handleFilter = () => {
  loading.value = true;
  // Client-side filtering simulation
  setTimeout(() => {
    let result = [...allData.value];
    
    // Status Filter (Tab)
    if (activeKey.value) {
      if (activeKey.value === 'inactive') {
        // Tab "已下架" includes unlisted and archived
        result = result.filter(item => ['inactive', 'archived', 'unlisted'].includes(item.status));
      } else if (activeKey.value === 'out_of_stock') {
        // Tab "缺货": explicit out_of_stock status OR active items with 0 inventory
        // Note: We exclude draft/archived/unlisted even if 0 inventory, as requested.
        result = result.filter(item => item.status === 'out_of_stock' || (item.status === 'active' && (item.fbaInventory || 0) <= 0));
      } else if (activeKey.value === 'active') {
        // Tab "已上架": active status AND inventory > 0
        result = result.filter(item => item.status === 'active' && (item.fbaInventory || 0) > 0);
      } else {
        result = result.filter(item => item.status === activeKey.value);
      }
    }
    
    // Form Filters
    if (filterForm.name) {
      result = result.filter(item => item.name.toLowerCase().includes(filterForm.name.toLowerCase()));
    }
    if (filterForm.sku) {
      const skuQuery = filterForm.sku.toLowerCase();
      // 优化 SKU 搜索：检查所有变体的 SKU
      result = result.filter(item => {
        // 检查主 SKU
        if (item.sku && item.sku.toLowerCase().includes(skuQuery)) return true;
        // 检查变体 SKU
        if (item.variants && item.variants.length > 0) {
          return item.variants.some(v => v.sku && v.sku.toLowerCase().includes(skuQuery));
        }
        return false;
      });
    }
    if (filterForm.asin) {
      const asinQuery = filterForm.asin.toLowerCase();
      result = result.filter(item => item.asin && item.asin.toLowerCase().includes(asinQuery));
    }
    if (filterForm.storeId) {
      result = result.filter(item => item.storeId === filterForm.storeId);
    }
    
    // 价格范围筛选
    if (filterForm.priceMin !== undefined || filterForm.priceMax !== undefined) {
      result = result.filter(item => {
        // 解析商品价格（取第一个变体或 minPrice）
        const price = parseFloat(item.price || '0');
        // 也可以考虑检查所有变体是否有落在区间内的，这里暂以主价格为准
        
        let matchMin = true;
        let matchMax = true;
        
        if (filterForm.priceMin !== undefined) {
          matchMin = price >= filterForm.priceMin;
        }
        if (filterForm.priceMax !== undefined) {
          matchMax = price <= filterForm.priceMax;
        }
        
        return matchMin && matchMax;
      });
    }

    if (filterForm.inventoryStatus) {
      if (filterForm.inventoryStatus === 'in_stock') {
        result = result.filter(item => (item.fbaInventory || 0) > 0);
      } else if (filterForm.inventoryStatus === 'out_of_stock') {
        result = result.filter(item => (item.fbaInventory || 0) === 0);
      }
    }

    // 时间范围筛选
    if (filterForm.timeRange && filterForm.timeRange.length === 2) {
      const startDate = dayjs(filterForm.timeRange[0]).startOf('day');
      const endDate = dayjs(filterForm.timeRange[1]).endOf('day');
      result = result.filter(item => {
        const itemDate = item.createdAt ? dayjs(item.createdAt as string) : null;
        if (!itemDate || !itemDate.isValid()) return true;
        return !itemDate.isBefore(startDate) && !itemDate.isAfter(endDate);
      });
    }
    
    // Sort logic
    if (currentSorter.columnKey && currentSorter.order) {
      const { columnKey, order } = currentSorter;
      result.sort((a: any, b: any) => {
        let valA = a[columnKey];
        let valB = b[columnKey];
        
        // Custom value extraction if needed
        if (columnKey === 'price') {
          valA = parseFloat(a.price || '0');
          valB = parseFloat(b.price || '0');
        }
        
        if (valA === valB) return 0;
        const compare = valA > valB ? 1 : -1;
        return order === 'ascend' ? compare : -compare;
      });
    }
    
    pagination.total = result.length;
    // Pagination slice
    // Safety check for pagination values
    const currentPage = Math.max(1, Number(pagination.current) || 1);
    const pageSize = Math.max(1, Number(pagination.pageSize) || 10);
    const start = (currentPage - 1) * pageSize;
    displayData.value = result.slice(start, start + pageSize);
    
    // Sync back standardized values to prevent NaN in UI
    pagination.current = currentPage;
    pagination.pageSize = pageSize;
    
    loading.value = false;

  }, 300);
};

const handleResetFilter = () => {
  filterForm.name = '';
  filterForm.sku = '';
  filterForm.asin = '';
  filterForm.storeId = undefined;
  filterForm.priceMin = undefined;
  filterForm.priceMax = undefined;
  filterForm.inventoryStatus = undefined;
  filterForm.timeRange = undefined;
  currentSorter.columnKey = '';
  currentSorter.order = '';
  handleFilter();
};

const handleTabChange = () => {
  pagination.current = 1;
  handleFilter();
};

const onPageChange = (page: number, pageSize: number) => {
  pagination.current = page;
  pagination.pageSize = pageSize;
  handleFilter();
};

const handleTableChange = (pag: any, filters: any, sorter: any) => {
  if (pag && pag.current) {
    pagination.current = pag.current;
  }
  if (pag && pag.pageSize) {
    pagination.pageSize = pag.pageSize;
  }
  
  if (sorter && sorter.columnKey) {
    currentSorter.columnKey = sorter.columnKey as string;
    currentSorter.order = sorter.order as 'ascend' | 'descend' | '';
  } else {
    currentSorter.columnKey = '';
    currentSorter.order = '';
  }
  
  handleFilter();
};

// Fetch Data
const fetchProducts = async () => {
  if (loading.value) return;
  loading.value = true;
  try {
    // Fix #1: Pass filter params to backend to reduce data transfer
    const apiParams: any = {};
    if (filterForm.name) apiParams.keyword = filterForm.name;
    if (filterForm.storeId) apiParams.storeId = filterForm.storeId;
    // Backend supports 'active'/'draft' status filter
    if (activeKey.value && activeKey.value !== 'out_of_stock' && activeKey.value !== 'suspected_deleted') {
      apiParams.status = activeKey.value;
    }
    const products = await getEccangProducts(apiParams);
    const mapped = products.map((p: any, idx: number): any => {
      // Find matching store for this product (used for admin links)
      const store = stores.value.find(s => s.id === p.storeId);

      return {
        key: p.id || idx,
        id: p.id || idx,
        name: p.name || p.title,
        image: p.mainImage || p.image || 'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="200" height="200" viewBox="0 0 200 200"%3E%3Crect fill="%23f1f5f9" width="200" height="200"/%3E%3Cpath fill="%2394a3b8" d="M80 60h40c6.6 0 12 5.4 12 12v56c0 6.6-5.4 12-12 12H80c-6.6 0-12-5.4-12-12V72c0-6.6 5.4-12 12-12zm20 10a14 14 0 100 28 14 14 0 000-28zm-24 70h48l-12-16-8 10.7L92 120l-16 20z"/%3E%3C/svg%3E',
        sku: p.sku || '-',
        asin: p.asin || '-',
        price: p.price?.toString() || '0.00',
        displayPrice: `$${parseFloat(p.price || '0').toFixed(2)}`,
        vendor: p.vendor || 'Amazon', 
        platform: store?.platform || p.platform || 'Amazon',
        link: p.link || `https://www.amazon.com/dp/${p.asin}`, 
        status: p.status || 'active',
        fbaInventory: p.fbaInventory || p.inventory || 0,
        createTime: p.createdAt,
        shopName: p.amazonShopName || store?.amazonShopName || store?.storeName || 'Store A',
        storeId: p.storeId || 1,
        variants: p.variants || [],
        logs: []
      };
    });
    allData.value = mapped;
    handleFilter();
  } catch (e) {
    if (e && (e as any).code === 'ERR_CANCELED') return;
    console.error(e);
    message.error('加载商品失败');
  } finally {
    loading.value = false;
  }
};

const handleSyncSuccess = () => {
  fetchProducts();
};

const previewImage = (url: string) => {
  previewImageUrl.value = url;
  imagePreviewVisible.value = true;
};

const openDetail = (record: any) => {
  currentProduct.value = record;
  detailVisible.value = true;
};

// Fix #12: Implement batch search for product names
const batchSearchVisible = ref(false);
const batchSearchValue = ref('');

const openBatchSearch = () => {
  batchSearchValue.value = filterForm.name?.replace(/,/g, '\n') || '';
  batchSearchVisible.value = true;
};

const handleBatchSearchOk = () => {
  const values = batchSearchValue.value.split(/[\n,]+/).map(v => v.trim()).filter(Boolean);
  filterForm.name = values.join(',');
  batchSearchVisible.value = false;
  handleFilter();
};

const handleCopy = (text: string) => {
  if (!text) return;
  navigator.clipboard.writeText(text).then(() => {
    message.success('已复制');
  });
};

onMounted(async () => {
  try {
     const storesRes = await getEccangStores();
     // @ts-ignore
     stores.value = storesRes.content || storesRes || [];
  } catch (e) {
    console.error(e);
  }
  fetchProducts(); // Fetch products AFTER stores are loaded (or concurrently if we used Promise.all, but sequential is safer for mapping)
});

</script>

<style lang="scss" scoped>
/* 
  Reusing InfluencerList Style System 
  1:1 Copy of the Premium UI Classes 
*/
.product-list-page {
  height: 100%;
  padding: 8px;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  gap: 8px;
  overflow: hidden;

  /* Refined Glass Card */
  .glass-card {
    background: #ffffff;
    border: 1px solid rgba(0, 0, 0, 0.04);
    border-radius: 12px;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.02);
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 8px 30px rgba(0, 0, 0, 0.05);
    }
  }

  .filter-card {
    margin-bottom: 8px !important;
    flex-shrink: 0;
    
    :deep(.ant-form-item) {
      margin-bottom: 0;
      .ant-form-item-label {
        padding: 0 !important;
        line-height: 1.2;
        > label {
          font-size: 12px;
          font-weight: 600;
          color: #64748b;
          height: 16px;
          margin-bottom: 4px;
        }
      }
      .ant-form-item-control-input {
        min-height: 32px;
      }
    }
    background: rgba(255, 255, 255, 0.6);
    backdrop-filter: blur(8px);
    border: 1px solid rgba(255, 255, 255, 0.8);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);
  }

  .table-card {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    min-height: 0;

    :deep(.ant-card-head) {
      border-bottom: 1px solid rgba(0, 0, 0, 0.04);
      padding: 0 16px;
      min-height: 48px;
      display: flex;
      align-items: center;
      .ant-card-head-title { padding: 0; }
    }

    :deep(.ant-card-body) {
      flex: 1;
      display: flex;
      flex-direction: column;
      overflow: hidden;
      padding: 0 !important;
    }
  }

  .table-actions-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;

    .status-switcher-wrapper {
      .premium-segmented {
        background: transparent;
        padding: 0;
        border-radius: 0;
        border: none;
        display: flex;
        gap: 4px;

        :deep(.ant-radio-button-wrapper) {
          border: 1px solid transparent;
          background: transparent;
          border-radius: 6px;
          height: 28px;
          line-height: 26px;
          font-weight: 500;
          color: #64748b;
          font-size: 13px;
          padding: 0 12px;
          transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

          &:before { display: none; }
          &:hover { color: #1890ff; background: #f1f5f9; }

          &.ant-radio-button-wrapper-checked {
            background: #eff6ff;
            color: #1890ff;
            border-color: #bfdbfe;
            box-shadow: none;
            font-weight: 600;
          }
        }
      }
    }
  }

  .premium-table {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    min-height: 500px;

    :deep(.ant-spin-nested-loading),
    :deep(.ant-spin-container),
    :deep(.ant-table),
    :deep(.ant-table-container) {
      display: flex;
      flex-direction: column;
      overflow: hidden;
      background: #ffffff;
    }

    :deep(.ant-table-content) {
      overflow: auto !important;
      &::-webkit-scrollbar { height: 8px; width: 8px; }
      &::-webkit-scrollbar-thumb { 
        background: #e2e8f0; 
        border-radius: 10px; 
        &:hover { background: #cbd5e1; }
      }
      &::-webkit-scrollbar-track { background: rgba(0, 0, 0, 0.02); border-radius: 10px; }
    }

    :deep(.ant-table) {
      background: transparent;
    }

    /* Prevent header from being squished in flex container */
    :deep(.ant-table-header) {
      flex-shrink: 0;
    }

    :deep(.ant-table-thead > tr > th) {
      background: rgba(248, 250, 252, 0.8) !important;
      backdrop-filter: blur(10px);
      color: #475569;
      font-weight: 700;
      font-size: 13px;
      border-bottom: 2px solid #f1f5f9;
      padding: 10px 8px !important;
      white-space: nowrap !important;
      height: auto !important;
      transition: all 0.3s ease;
      &.ant-table-selection-column {
        padding: 0 8px !important;
      }
    }

    /* Empty State Styling */
    .table-empty-state {
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
      height: v-bind('filterExpanded ? "calc(100vh - 520px)" : "calc(100vh - 410px)"');
      min-height: 200px;
      :deep(.ant-empty-description) {
        color: #94a3b8;
        font-weight: 600;
        font-size: 14px;
        margin-top: 16px;
      }
    }

    /* Fixed Column Background Fix - Corrected for Headers */
    :deep(.ant-table-thead > tr > th.ant-table-cell-fix-left),
    :deep(.ant-table-thead > tr > th.ant-table-cell-fix-right) {
      background: #f8fafc !important;
      z-index: 3;
    }

    /* Fixed Column Body Background */
    :deep(.ant-table-tbody > tr > td.ant-table-cell-fix-left),
    :deep(.ant-table-tbody > tr > td.ant-table-cell-fix-right) {
      background: #fff;
      z-index: 2;
    }

    /* Fix hover state for fixed columns */
    :deep(.ant-table-tbody > tr:hover > td.ant-table-cell-fix-left),
    :deep(.ant-table-tbody > tr:hover > td.ant-table-cell-fix-right) {
      background: #f0f7ff !important;
    }

    :deep(.ant-table-tbody > tr > td) {
      border-bottom: 1px solid #f8fafc;
      padding: 8px 8px;
      transition: all 0.2s;
    }

    :deep(.ant-table-tbody > tr:hover > td) {
      background: #f0f7ff !important;
    }
  }

  /* User Info Cell (Product Info) */
  .user-info-cell {
    display: flex;
    align-items: center;
    gap: 14px;
    
    
    .avatar-wrapper {
       /* Custom avatar styles for product image */
       display: flex;
       align-items: center;
       justify-content: center;
    }

    
    .info-content {
      overflow: hidden;
      min-width: 0;
      .user-name {
        font-weight: 600;
        color: #1e293b;
        font-size: 14px;
        margin-bottom: 2px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
      }
      .user-id {
        font-size: 11px;
        color: #94a3b8;
        font-family: 'JetBrains Mono', monospace;
      }
    }
  }

  /* Status Tags */
  .status-tag {
    border-radius: 20px;
    padding: 2px 12px;
    font-weight: 600;
    font-size: 11px;
    letter-spacing: 0.5px;
    border: none;
    &.tag-blue { background: #dbeafe; color: #1e40af; }
  }

  /* Action Buttons */
  .action-btns-wrapper {
    display: flex;
    gap: 4px;
    justify-content: center;
    .detail-btn {
      border-radius: 4px;
      font-weight: 500;
      height: 24px;
      padding: 0 8px;
    }
  }

  /* Compact Pagination Footer - EXACT COPY */
  .pagination-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 6px 16px;
    background: #ffffff;
    border-top: 1px solid rgba(0, 0, 0, 0.04);
    z-index: 10;
    
    .footer-left {
      display: flex;
      align-items: center;
      gap: 8px;
      color: #94a3b8;
      font-size: 12px;
      
      .count-value {
        font-weight: 600;
        color: #1e293b;
        background: #f1f5f9;
        padding: 1px 6px;
        border-radius: 4px;
        
        &.highlight {
          color: #1890ff;
          background: #e6f7ff;
        }
      }
    }
  }

  /* Premium Buttons & Inputs */
  .premium-btn {
    border-radius: 6px;
    height: 32px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    font-weight: 500;
    transition: all 0.3s;
    
    &.primary-gradient {
      background: linear-gradient(135deg, #1890ff 0%, #1d4ed8 100%);
      border: none;
      color: #fff;
      box-shadow: 0 2px 6px rgba(29, 78, 216, 0.15);
      
      &:hover {
        box-shadow: 0 4px 10px rgba(29, 78, 216, 0.2);
        transform: translateY(-1px);
      }
    }

    &.small {
      height: 28px;
      padding: 0 12px;
      font-size: 12px;
      border-radius: 6px;
    }
  }

  .premium-btn-small {
     height: 28px; 
     padding: 0 12px;
     font-size: 12px;
     border-radius: 6px;
     display: flex; align-items: center; gap: 4px;
     border: 1px solid #e2e8f0;
     background: #fff;
     color: #64748b;
     transition: all 0.3s;
     &:hover { color: #1890ff; border-color: #1890ff; background: #f0f7ff; }

     &.primary-gradient {
        background: linear-gradient(135deg, #1890ff 0%, #1d4ed8 100%);
        border: none;
        color: #fff;
        &:hover { opacity: 0.9; box-shadow: 0 2px 8px rgba(29, 78, 216, 0.2); }
     }
  }
  
  .transfer-btn {
      border-radius: 4px;
      color: #64748b;
      height: 28px;
      padding: 0 8px;
      display: flex;
      align-items: center;
      gap: 2px;
      font-size: 12px;
      border: 1px solid #e2e8f0;
      background: #fff;
      &:hover {
        color: #1890ff;
        border-color: #1890ff;
        background: #f0f7ff;
      }
  }



  .sku-tag {
    background: #f1f5f9;
    color: #475569;
    padding: 2px 6px;
    border-radius: 4px;
    font-size: 11px;
    font-family: 'JetBrains Mono', monospace;
    cursor: pointer;
    display: inline-flex;
    align-items: center;
    gap: 4px;
    transition: all 0.2s;
    
    &:hover {
      background: #e2e8f0;
      color: #3b82f6;
    }
    
    .copy-icon { opacity: 0.5; font-size: 10px; }
    &:hover .copy-icon { opacity: 1; }
  }
  .premium-datepicker,
  .premium-input-search {
    border-radius: 8px !important;
    border-color: #e2e8f0 !important;
    background: #fafbfc !important;
    transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
    height: 32px !important;
    padding: 0 11px !important;
    display: flex;
    align-items: center;

    &:hover {
      border-color: #cbd5e1 !important;
      background: #fff !important;
    }
  }

  .premium-range-group {
    display: flex;
    height: 40px;
    .premium-select :deep(.ant-select-selector) {
      border-top-right-radius: 0 !important;
      border-bottom-right-radius: 0 !important;
      border-right: none !important;
    }
    .premium-datepicker {
      border-top-left-radius: 0 !important;
      border-bottom-left-radius: 0 !important;
      flex: 1;
    }
  }
  
  .filter-footer {
      display: flex;
      justify-content: flex-end;
      margin-top: 8px;
      padding-top: 20px;
      border-top: 1px dashed #cbd5e1;
  }
  
  
  /* Premium Price Range */
  .premium-price-range-wrapper {
    display: flex;
    align-items: center;
    background: #fafbfc;
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    height: 32px;
    padding: 0 4px;
    transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
    width: 100%;
    
    &:hover, &:focus-within {
      border-color: #1890ff;
      background: #fff;
      box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
    }
    
    .range-input {
      flex: 1;
      width: 0;
      
      :deep(.ant-input-number), 
      :deep(.ant-input-number-affix-wrapper) {
        border: none !important;
        background: transparent !important;
        padding: 0 4px !important;
        box-shadow: none !important;
        width: 100%;
        
        &.ant-input-number-affix-wrapper-focused,
        &.ant-input-number-focused {
          box-shadow: none !important;
        }
      }
      
      :deep(.ant-input-number-input) {
        height: 30px;
        font-size: 13px;
        text-align: center;
        padding: 0 !important;
      }
      
      .currency-symbol {
        color: #94a3b8;
        font-size: 12px;
      }
    }
    
    .range-separator {
      color: #94a3b8;
      padding: 0 2px;
      font-size: 12px;
      user-select: none;
      font-weight: 500;
    }
  }

  :deep(.ant-dropdown-menu), .premium-menu {
      border-radius: 10px;
      padding: 6px;
      box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
      border: 1px solid rgba(0, 0, 0, 0.05);
      .ant-dropdown-menu-item {
          border-radius: 6px;
          margin-bottom: 2px;
          &:hover { background: #f1f5f9; color: #1890ff; }
      }
  }
}
</style>

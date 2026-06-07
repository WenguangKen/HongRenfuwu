<template>
  <div class="product-list-page">
    <div class="page-header">
      <div class="page-header-left">
        <h2 class="page-title">商品列表</h2>
        <span class="page-subtitle">易仓亚马逊商品 · 共 {{ pagination.total }} 件</span>
      </div>
      <a-space size="middle">
        <a-button type="primary" @click="openSyncModal('product')" class="premium-btn primary-gradient">
          <template #icon><SyncOutlined /></template>
          同步易仓商品
        </a-button>
        <a-button type="default" @click="openSyncModal('fba')" class="premium-btn">
          <template #icon><SyncOutlined /></template>
          同步 FBA 库存
        </a-button>
      </a-space>
    </div>

    <!-- 筛选区域 -->
    <a-card :bordered="false" class="filter-card glass-card" :body-style="{ padding: '16px 20px' }">
      <a-form :model="filterForm" layout="vertical" size="middle">
        <a-row :gutter="[20, 16]">
          <a-col :xs="24" :sm="12" :md="8" :lg="3">
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
          <a-col :xs="24" :sm="12" :md="8" :lg="3">
            <a-form-item label="所属店铺">
              <a-select v-model:value="filterForm.platformAccount" placeholder="选择店铺" show-search allow-clear option-filter-prop="children" class="premium-select">
                <a-select-option v-for="acc in platformAccounts" :key="acc" :value="acc">
                  {{ acc }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="3">
            <a-form-item label="站点">
              <a-select v-model:value="filterForm.site" placeholder="选择站点" show-search allow-clear option-filter-prop="children" class="premium-select">
                <a-select-option v-for="st in sites" :key="st" :value="st">
                  {{ st }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="3">
            <a-form-item label="Seller SKU">
              <a-input v-model:value="filterForm.sku" placeholder="输入Seller SKU..." allow-clear class="premium-input" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="3">
            <a-form-item label="父ASIN">
              <a-input v-model:value="filterForm.asin" placeholder="输入父ASIN..." allow-clear class="premium-input" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="5">
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
                <a-button type="primary" @click="() => { pagination.current = 1; fetchProducts(); }" class="premium-btn primary-gradient" style="height: 32px; padding: 0 20px;">
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

    <div class="status-tabs-bar">
      <a-radio-group v-model:value="activeKey" button-style="solid" class="premium-segmented" @change="handleTabChange">
        <a-radio-button value="all">全部 ({{ statistics.all || 0 }})</a-radio-button>
        <a-radio-button value="active">已上架 ({{ statistics.active || 0 }})</a-radio-button>
        <a-radio-button value="inactive">已下架 ({{ statistics.inactive || 0 }})</a-radio-button>
        <a-radio-button value="out_of_stock">缺货 ({{ statistics.out_of_stock || 0 }})</a-radio-button>
        <a-radio-button value="suspected_deleted">疑似删除 ({{ statistics.suspected_deleted || 0 }})</a-radio-button>
      </a-radio-group>
    </div>

    <a-card :bordered="false" class="table-card glass-card" :body-style="{ padding: '0' }">
      <a-table
        :columns="columns"
        :data-source="displayData"
        :row-key="(record: any) => record.id || record.key"
        :pagination="false"
        :loading="{ spinning: loading, indicator: LoadingIcon }"
        :scroll="displayData.length > 0 ? { x: 1170, y: filterExpanded ? 'calc(100vh - 460px)' : 'calc(100vh - 350px)' } : { x: 1170, y: filterExpanded ? 'calc(100vh - 460px)' : 'calc(100vh - 350px)' }"
        :sticky="{ offsetScroll: 0 }"
        size="middle"
        table-layout="fixed"
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
            <div class="product-info-cell">
              <div
                class="product-thumb"
                :class="{ 'is-placeholder': record.imageIsPlaceholder }"
                @click="previewImage(record.largeImage)"
              >
                <img
                  :src="record.image"
                  :alt="record.name"
                  loading="lazy"
                  referrerpolicy="no-referrer"
                  @load="onImageLoad($event, record)"
                  @error="onImageError($event, record)"
                />
                <span v-if="record.imageIsPlaceholder" class="thumb-badge">无图</span>
              </div>
              <div class="product-meta">
                <div class="product-title" :title="record.name" style="font-weight: 500; font-size: 13px; line-height: 1.4; color: #1e293b; max-height: 40px; overflow: hidden; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical;">{{ record.name }}</div>
                <div v-if="record.categoryPath" class="product-category" style="font-size: 11px; color: #0284c7; margin-top: 6px; display: flex; align-items: center; gap: 4px;">
                  <span style="background: #f0f9ff; border: 1px solid #e0f2fe; padding: 2px 6px; border-radius: 4px; font-weight: 500; display: inline-block; max-width: 280px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;" :title="record.categoryPath">
                    {{ record.categoryPath }}
                  </span>
                </div>
              </div>
            </div>
          </template>

          <template v-else-if="column.key === 'productCodes'">
            <div class="product-codes-cell" style="display: flex; flex-direction: column; gap: 4px; font-size: 12px;">
              <!-- 父ASIN -->
              <div style="display: flex; align-items: center; gap: 6px; flex-wrap: wrap;">
                <span style="font-weight: 600; color: #475569;">父ASIN:</span>
                <template v-if="record.hasMultipleAsins">
                  <a-tooltip :title="record.asinTooltip">
                    <span class="code-tag multiple" @click.stop="handleCopy(record.parentAsin)" title="点击复制父 ASIN" style="cursor: pointer; background: #eff6ff; color: #1d4ed8; padding: 1px 6px; border-radius: 4px; font-family: monospace; border: 1px dashed #60a5fa;">
                      {{ record.parentAsin || '-' }} <CopyOutlined style="font-size: 10px; color: #60a5fa;" />
                    </span>
                  </a-tooltip>
                </template>
                <template v-else>
                  <span v-if="record.parentAsin && record.parentAsin !== '-'" class="code-tag" @click.stop="handleCopy(record.parentAsin)" title="点击复制父 ASIN" style="cursor: pointer; background: #eff6ff; color: #1d4ed8; padding: 1px 6px; border-radius: 4px; font-family: monospace;">
                    {{ record.parentAsin }} <CopyOutlined style="font-size: 10px; color: #60a5fa;" />
                  </span>
                  <span v-else>-</span>
                </template>
              </div>
              <!-- SKU -->
              <div style="display: flex; align-items: center; gap: 6px; flex-wrap: wrap;">
                <span style="font-weight: 600; color: #475569;">Seller SKU:</span>
                <template v-if="record.hasMultipleSkus">
                  <a-tooltip :title="record.skuTooltip">
                    <span class="code-tag multiple" style="cursor: help; background: #fff7ed; color: #c2410c; padding: 1px 6px; border-radius: 4px; font-family: monospace; border: 1px dashed #fdba74;">
                      {{ record.displaySku }}
                    </span>
                  </a-tooltip>
                </template>
                <template v-else>
                  <span v-if="record.displaySku && record.displaySku !== '-'" class="code-tag" @click.stop="handleCopy(record.displaySku)" title="点击复制 Seller SKU" style="cursor: pointer; background: #fff7ed; color: #c2410c; padding: 1px 6px; border-radius: 4px; font-family: monospace;">
                    {{ record.displaySku }} <CopyOutlined style="font-size: 10px; color: #fdba74;" />
                  </span>
                  <span v-else>-</span>
                </template>
              </div>
            </div>
          </template>

          <template v-else-if="column.key === 'skuStats'">
            <div class="sku-stats-cell" style="display: flex; flex-direction: column; gap: 1px; font-size: 11px; width: 100%; text-align: center; line-height: 1.2;">
              <div><span style="color: #10b981; font-weight: 600;">上:{{ record.skuActive || 0 }}</span></div>
              <div><span style="color: #ef4444; font-weight: 600;">下:{{ record.skuInactive || 0 }}</span></div>
              <div style="border-top: 1px dashed #e2e8f0; margin-top: 1px; padding-top: 1px; color: #3b82f6; font-weight: 700;">总:{{ record.skuTotal || 0 }}</div>
            </div>
          </template>

          <template v-else-if="column.key === 'platform'">
            <a-tag color="blue" class="platform-tag" style="margin: 0; font-size: 11px; font-weight: 500; padding: 2px 8px; border-radius: 4px;">{{ record.platform || 'Amazon' }}</a-tag>
          </template>

          <template v-else-if="column.key === 'fulfillment'">
            <a-tag v-if="record.productType" color="purple" class="type-tag" style="margin: 0; font-size: 11px; font-weight: 500; padding: 2px 8px; border-radius: 4px;">{{ record.productType }}</a-tag>
            <span v-else>-</span>
          </template>

          <template v-else-if="column.key === 'siteCode'">
            <span v-if="record.siteCode" class="site-pill">{{ record.siteCode }}</span>
            <span v-else>-</span>
          </template>

          <template v-else-if="column.key === 'platformAccount'">
            <span v-if="record.platformAccount" class="account-pill">{{ record.platformAccount }}</span>
            <span v-else>-</span>
          </template>

          <template v-else-if="column.key === 'shopName'">
            <span class="shop-pill">{{ record.shopName || '-' }}</span>
          </template>

          <template v-else-if="column.key === 'price'">
            <span class="price-text">{{ record.displayPrice }}</span>
          </template>

          <template v-else-if="column.key === 'fbaInventory'">
            <span class="inventory-pill" :class="inventoryLevel(record.fbaInventory)">
              {{ record.fbaInventory }}
            </span>
          </template>

          <template v-else-if="column.key === 'action'">
            <div class="action-btns-wrapper">
              <a 
                class="action-link" 
                :href="record.link" 
                target="_blank" 
                :class="{ disabled: !record.asin || record.asin === '-' }"
                @click="(!record.asin || record.asin === '-') ? $event.preventDefault() : null"
              >
                链接
              </a>
              <span class="divider">|</span>
              <a class="action-link detail" @click="openDetail(record)">详情</a>
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
      :sync-type="currentSyncType"
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
import { getEccangProducts, getEccangStores, getEccangProductStatistics, getEccangPlatformAccounts, getEccangSites } from '@/services/eccangService';
import type { EccangStoreConfig } from '@/services/eccangService';
import type { ProductItem, ProductStatus } from '@/types/product';

defineOptions({
  name: 'ProductList'
});

// Loading Icon
const LoadingIcon = h(LoadingOutlined, { style: { fontSize: '24px' }, spin: true });

const PLACEHOLDER_IMAGE =
  'data:image/svg+xml,' + encodeURIComponent(
    '<svg xmlns="http://www.w3.org/2000/svg" width="120" height="120" viewBox="0 0 120 120"><rect fill="#f1f5f9" width="120" height="120" rx="12"/><path fill="#94a3b8" d="M44 36h32c4 0 7 3 7 7v34c0 4-3 7-7 7H44c-4 0-7-3-7-7V43c0-4 3-7 7-7zm16 6a10 10 0 100 20 10 10 0 000-20zm-18 42h36l-9-12-6 8-7-9-14 13z"/></svg>'
  );

const isPlaceholderImage = (url?: string | null) => {
  if (!url) return true;
  return url.includes('noimg.jpg') || url.includes('/images/base/noimg') || url.startsWith('data:image/svg+xml');
};

const amazonImageFromAsin = (asin?: string | null, size = 'SL150') => {
  if (!asin || asin === '-') return null;
  return `https://images-na.ssl-images-amazon.com/images/P/${asin}.01._${size}_.jpg`;
};

const resolveProductImage = (url?: string | null, asin?: string | null, size = 'SL150') => {
  if (!isPlaceholderImage(url)) {
    if (url && url.includes('images-na.ssl-images-amazon.com')) {
      return url.replace(/_(?:SL|UL|SX|SY)\d+_/, `_${size}_`).replace(/^http:\/\//i, 'https://');
    }
    return url!.replace(/^http:\/\//i, 'https://');
  }
  const amazonImage = amazonImageFromAsin(asin, size);
  return amazonImage || PLACEHOLDER_IMAGE;
};

const inventoryLevel = (qty: number) => {
  if (qty <= 0) return 'out';
  if (qty < 10) return 'low';
  if (qty < 50) return 'mid';
  return 'high';
};

const onImageError = (event: Event, record: any) => {
  const img = event.target as HTMLImageElement;
  const currentUrl = img.src;
  
  if (currentUrl === PLACEHOLDER_IMAGE) return;

  // 1. Try newer Amazon ad-system widget URL for this record's ASIN
  const match = currentUrl.match(/\/images\/P\/([A-Z0-9]{10})/i);
  if (match) {
    const asin = match[1];
    const adSystemUrl = `https://ws-na.amazon-adsystem.com/widgets/q?ASIN=${asin}&Format=_SL250_&ID=AsinImage&MarketPlace=US`;
    if (currentUrl !== adSystemUrl) {
      record.image = adSystemUrl;
      img.src = adSystemUrl;
      return;
    }
  }

  // 2. Try first variant's ASIN
  const firstVar = record.variants?.[0];
  if (firstVar) {
    if (firstVar.imageUrl && currentUrl !== firstVar.imageUrl) {
      record.image = firstVar.imageUrl;
      img.src = firstVar.imageUrl;
      return;
    }
    if (firstVar.asin && firstVar.asin !== '-') {
      const varAsinUrl = `https://images-na.ssl-images-amazon.com/images/P/${firstVar.asin}.01._SL75_.jpg`;
      if (currentUrl !== varAsinUrl) {
        record.image = varAsinUrl;
        img.src = varAsinUrl;
        return;
      }
      const varAdSystemUrl = `https://ws-na.amazon-adsystem.com/widgets/q?ASIN=${firstVar.asin}&Format=_SL250_&ID=AsinImage&MarketPlace=US`;
      if (currentUrl !== varAdSystemUrl) {
        record.image = varAdSystemUrl;
        img.src = varAdSystemUrl;
        return;
      }
    }
  }

  // 3. Fallback to placeholder
  record.image = PLACEHOLDER_IMAGE;
  record.imageIsPlaceholder = true;
  img.src = PLACEHOLDER_IMAGE;
};

const onImageLoad = (event: Event, record: any) => {
  const img = event.target as HTMLImageElement;
  if (img.naturalWidth === 1 && img.naturalHeight === 1) {
    onImageError(event, record);
  }
};

const activeKey = ref('all');

const statistics = reactive({
  all: 0,
  active: 0,
  inactive: 0,
  out_of_stock: 0,
  suspected_deleted: 0,
});

const loading = ref(false);
const filterExpanded = ref(false);
const selectedRowKeys = ref<any[]>([]);

// Modals
const imagePreviewVisible = ref(false);
const previewImageUrl = ref('');
const detailVisible = ref(false);
const currentProduct = ref<any>(null);
const syncModalVisible = ref(false);
const currentSyncType = ref<'product' | 'fba'>('product');

const openSyncModal = (type: 'product' | 'fba') => {
  currentSyncType.value = type;
  syncModalVisible.value = true;
};

const stores = ref<EccangStoreConfig[]>([]);
const platformAccounts = ref<string[]>([]);
const sites = ref<string[]>([]);

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
  platformAccount: undefined as string | undefined,
  site: undefined as string | undefined,
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
    width: 240,
    fixed: 'left',
    align: 'left',
  },
  {
    title: '商品编码',
    key: 'productCodes',
    width: 170,
    align: 'left',
  },
  {
    title: 'SKU统计',
    key: 'skuStats',
    width: 80,
    align: 'center',
  },
  {
    title: '平台',
    key: 'platform',
    width: 65,
    align: 'center',
  },
  {
    title: '配送',
    key: 'fulfillment',
    width: 65,
    align: 'center',
  },
  {
    title: '站点',
    key: 'siteCode',
    width: 65,
    align: 'center',
  },
  {
    title: '店铺',
    key: 'platformAccount',
    width: 130,
    align: 'center',
  },
  {
    title: '单价',
    key: 'price',
    width: 100,
    align: 'center',
    sorter: (a: any, b: any) => parseFloat(a.price) - parseFloat(b.price),
  },
  {
    title: 'FBA库存',
    key: 'fbaInventory',
    width: 90,
    align: 'center',
    sorter: (a: any, b: any) => a.fbaInventory - b.fbaInventory,
  },
  {
    title: '操作',
    key: 'action',
    width: 80,
    fixed: 'right',
    align: 'center',
  },
];

const useServerPagination = () =>
  filterForm.priceMin === undefined &&
  filterForm.priceMax === undefined &&
  !filterForm.inventoryStatus &&
  !filterForm.timeRange;



// Filter Logic
const handleFilter = () => {
  if (useServerPagination()) {
    pagination.current = 1;
    fetchProducts();
    return;
  }
  loading.value = true;
  // Client-side filtering simulation
  setTimeout(() => {
    let result = [...allData.value];
    
    // Status Filter (Tab)
    if (activeKey.value && activeKey.value !== 'all') {
      if (activeKey.value === 'inactive') {
        // Tab "已下架" includes unlisted and archived
        result = result.filter(item => ['inactive', 'archived', 'unlisted'].includes(item.status));
      } else if (activeKey.value === 'out_of_stock') {
        // Tab "缺货": explicit out_of_stock status OR active items with 0 inventory
        // Note: We exclude draft/archived/unlisted even if 0 inventory, as requested.
        result = result.filter(item => item.status === 'out_of_stock' || (item.status === 'active' && (item.fbaInventory || 0) <= 0));
      } else if (activeKey.value === 'active') {
        // Tab "已上架": 仅按状态筛选（易仓同步的 FBA 库存默认为 0，不能作为上架条件）
        result = result.filter(item => item.status === 'active');
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
      result = result.filter(item => {
        if (item.asin && item.asin.toLowerCase().includes(asinQuery)) return true;
        if (item.parentAsin && item.parentAsin.toLowerCase().includes(asinQuery)) return true;
        if (item.variants && item.variants.length > 0) {
          return item.variants.some(v => v.asin && v.asin.toLowerCase().includes(asinQuery));
        }
        return false;
      });
    }
    if (filterForm.platformAccount) {
      result = result.filter(item => item.platformAccount === filterForm.platformAccount);
    }
    if (filterForm.site) {
      result = result.filter(item => item.siteCode === filterForm.site);
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
  filterForm.platformAccount = undefined;
  filterForm.site = undefined;
  filterForm.priceMin = undefined;
  filterForm.priceMax = undefined;
  filterForm.inventoryStatus = undefined;
  filterForm.timeRange = undefined;
  currentSorter.columnKey = '';
  currentSorter.order = '';
  pagination.current = 1;
  fetchProducts();
};

const handleTabChange = () => {
  pagination.current = 1;
  fetchProducts();
};

const onPageChange = (page: number, pageSize: number) => {
  pagination.current = page;
  pagination.pageSize = pageSize;
  if (useServerPagination()) {
    fetchProducts();
  } else {
    handleFilter();
  }
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

const formatAttributesText = (raw?: string | null) => {
  if (!raw) return '';
  try {
    const attrs = typeof raw === 'string' ? JSON.parse(raw) : raw;
    const parts: string[] = [];
    if (attrs.colors?.length) parts.push(`颜色: ${attrs.colors.join('/')}`);
    else if (attrs.color) parts.push(`颜色: ${attrs.color}`);
    if (attrs.sizes?.length) parts.push(`尺码: ${attrs.sizes.join('/')}`);
    else if (attrs.size) parts.push(`尺码: ${attrs.size}`);
    if (attrs.fulfillmentType) parts.push(attrs.fulfillmentType);
    if (attrs.specs) parts.push(attrs.specs);
    return parts.join(' · ');
  } catch {
    return '';
  }
};

const mapProductRow = (p: any, idx: number) => {
  const store = stores.value.find(s => s.id === p.storeId);
  const asin = p.asin || p.parentAsin || p.spu || p.variants?.[0]?.asin || '-';
  const rawImage = p.smallImageUrl || p.mainImage || p.image || p.variants?.[0]?.smallImageUrl || p.variants?.[0]?.imageUrl || '';
  const image = resolveProductImage(rawImage, asin, 'SL75');
  const rawLargeImage = p.mainImage || p.image || p.variants?.[0]?.imageUrl || p.smallImageUrl || '';
  const largeImage = resolveProductImage(rawLargeImage, asin, 'SL500');
  // Parse min and max prices
  const minPrice = Number(p.minPrice ?? p.variants?.[0]?.price ?? p.price ?? 0);
  const maxPrice = Number(p.maxPrice ?? p.variants?.[0]?.price ?? p.price ?? 0);
  
  let displayPrice = `$${minPrice.toFixed(2)}`;
  if (maxPrice > minPrice) {
    displayPrice = `$${minPrice.toFixed(2)} - $${maxPrice.toFixed(2)}`;
  }
  const siteCode = p.siteCode || '';
  const platformAccount = p.platformAccount || p.amazonShopName || '';
  const attributesText = formatAttributesText(p.attributesJson || p.optionsJson);

  const variants = p.variants || [];
  const skuTotal = variants.length;
  const skuActive = variants.filter((v: any) => v.status === 'active' || v.status === '1').length;
  const skuInactive = skuTotal - skuActive;

  // Extract unique SKUs and ASINs from variants
  const uniqueSkus = [...new Set(variants.map((v: any) => v.sku).filter(Boolean))];
  const uniqueAsins = [...new Set(variants.map((v: any) => v.asin).filter(Boolean))];

  const hasMultipleSkus = uniqueSkus.length > 1;
  const hasMultipleAsins = uniqueAsins.length > 1;

  const displaySku = hasMultipleSkus 
    ? `多 SKU (共 ${uniqueSkus.length} 个)` 
    : (uniqueSkus[0] || p.sku || p.platformSku || '-');

  const parentAsinVal = p.parentAsin || p.spu || p.handle || '';

  const displayAsin = hasMultipleAsins 
    ? (parentAsinVal ? `${parentAsinVal} (共 ${uniqueAsins.length} 个)` : `多 ASIN (共 ${uniqueAsins.length} 个)`)
    : (uniqueAsins[0] || asin || '-');

  const skuTooltip = hasMultipleSkus
    ? (uniqueSkus.length > 5 ? `${uniqueSkus.slice(0, 5).join(', ')} 等共 ${uniqueSkus.length} 个 SKU` : uniqueSkus.join(', '))
    : displaySku;

  const asinTooltip = hasMultipleAsins
    ? (uniqueAsins.length > 5 ? `${uniqueAsins.slice(0, 5).join(', ')} 等共 ${uniqueAsins.length} 个 ASIN` : uniqueAsins.join(', '))
    : displayAsin;

  return {
    key: p.id || idx,
    id: p.id || idx,
    name: p.name || p.title,
    image,
    largeImage,
    imageIsPlaceholder: isPlaceholderImage(rawImage) && !amazonImageFromAsin(asin),
    sku: p.sku || p.platformSku || p.variants?.[0]?.sku || '-',
    asin,
    parentAsin: p.parentAsin || p.spu || p.asin || p.variants?.[0]?.asin || '-',
    spu: p.parentAsin || p.handle || p.spu || '',
    handle: p.parentAsin || p.handle || '',
    variantCount: p.variantCount ?? p.variants?.length ?? 0,
    price: String(minPrice),
    displayPrice,
    vendor: p.vendor || 'Amazon',
    platform: store?.platform || p.platform || 'Amazon',
    siteCode,
    platformAccount,
    link: asin && asin !== '-' ? `https://www.amazon.com/dp/${asin}` : '#',
    status: p.status || 'active',
    fbaInventory: p.fbaInventory ?? p.inventory ?? p.variants?.[0]?.fbaInventory ?? p.variants?.[0]?.inventoryQuantity ?? 0,
    createTime: p.createdAt,
    createdAt: p.createdAt,
    shopName: p.storeName || store?.storeName || '默认店铺',
    productType: p.productType || p.fulfillmentType || '',
    attributesText,
    optionsJson: p.attributesJson || p.optionsJson || '',
    categoryPath: p.categoryPath || '',
    storeId: p.storeId || 1,
    variants,
    skuTotal,
    skuActive,
    skuInactive,
    displaySku,
    displayAsin,
    skuTooltip,
    asinTooltip,
    hasMultipleSkus,
    hasMultipleAsins,
    logs: [],
  };
};

const fetchStatistics = async () => {
  try {
    const apiParams: Record<string, unknown> = {};
    if (filterForm.name) apiParams.keyword = filterForm.name;
    else if (filterForm.sku) apiParams.keyword = filterForm.sku;
    else if (filterForm.asin) apiParams.keyword = filterForm.asin;
    if (filterForm.platformAccount) apiParams.platformAccount = filterForm.platformAccount;
    if (filterForm.site) apiParams.site = filterForm.site;

    const statsRes = await getEccangProductStatistics(apiParams);
    if (statsRes) {
      Object.assign(statistics, statsRes);
    }
  } catch (e) {
    console.error('Failed to fetch statistics', e);
  }
};

// Fetch Data
const fetchProducts = async () => {
  if (loading.value) return;
  loading.value = true;
  try {
    fetchStatistics();

    const apiParams: Record<string, unknown> = {};
    if (filterForm.name) apiParams.keyword = filterForm.name;
    else if (filterForm.sku) apiParams.keyword = filterForm.sku;
    else if (filterForm.asin) apiParams.keyword = filterForm.asin;
    if (filterForm.platformAccount) apiParams.platformAccount = filterForm.platformAccount;
    if (filterForm.site) apiParams.site = filterForm.site;
    if (activeKey.value && activeKey.value !== 'all') {
      apiParams.status = activeKey.value;
    }
    if (useServerPagination()) {
      apiParams.page = pagination.current - 1;
      apiParams.size = pagination.pageSize;
    }

    const productsRes = await getEccangProducts(apiParams);
    const isPaged = !Array.isArray(productsRes) && !!productsRes?.content;
    const products = isPaged ? productsRes.content : (Array.isArray(productsRes) ? productsRes : []);
    const mapped = products.map(mapProductRow);

    allData.value = mapped;
    if (isPaged) {
      pagination.total = productsRes.totalElements ?? mapped.length;
      displayData.value = mapped;
    } else {
      handleFilter();
    }
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
  try {
     const accountsRes = await getEccangPlatformAccounts();
     platformAccounts.value = accountsRes || [];
  } catch (e) {
    console.error(e);
  }
  try {
     const sitesRes = await getEccangSites();
     sites.value = sitesRes || [];
  } catch (e) {
    console.error(e);
  }
  fetchProducts(); // Fetch products AFTER stores, accounts and sites are loaded
});

</script>

<style lang="scss" scoped>
/* 
  Reusing InfluencerList Style System 
  1:1 Copy of the Premium UI Classes 
*/
.product-list-page {
  height: 100%;
  padding: 12px 16px;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  gap: 10px;
  overflow: hidden;

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-shrink: 0;

    .page-header-left {
      display: flex;
      flex-direction: column;
      gap: 2px;
    }

    .page-title {
      margin: 0;
      font-size: 20px;
      font-weight: 700;
      color: #0f172a;
      line-height: 1.2;
    }

    .page-subtitle {
      font-size: 13px;
      color: #64748b;
    }
  }

  .status-tabs-bar {
    flex-shrink: 0;
    padding: 10px 14px;
    background: #fff;
    border: 1px solid rgba(0, 0, 0, 0.04);
    border-radius: 12px;
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.02);
  }

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
      padding: 10px 12px;
      transition: all 0.2s;
    }

    :deep(.ant-table-tbody > tr:hover > td) {
      background: #f0f7ff !important;
    }
  }

  .product-info-cell {
    display: flex;
    align-items: flex-start;
    gap: 12px;
    padding: 4px 0;
  }

  .product-thumb {
    position: relative;
    width: 56px;
    height: 56px;
    flex-shrink: 0;
    border-radius: 8px;
    overflow: hidden;
    border: 1px solid #e2e8f0;
    background: #f8fafc;
    cursor: pointer;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      display: block;
    }

    &.is-placeholder {
      background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
    }

    .thumb-badge {
      position: absolute;
      left: 4px;
      bottom: 4px;
      padding: 1px 5px;
      border-radius: 4px;
      background: rgba(15, 23, 42, 0.65);
      color: #fff;
      font-size: 10px;
      line-height: 1.4;
    }
  }

  .product-meta {
    min-width: 0;
    flex: 1;
  }

  .product-title {
    font-weight: 600;
    color: #0f172a;
    font-size: 13px;
    line-height: 1.4;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    margin-bottom: 4px;
    word-break: break-word;
    white-space: normal;
  }

  .product-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
    margin-bottom: 6px;
  }

  .code-tag {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    padding: 2px 8px;
    border-radius: 6px;
    background: #f8fafc;
    border: 1px solid #e2e8f0;
    color: #475569;
    font-size: 11px;
    font-family: 'JetBrains Mono', monospace;
    cursor: pointer;
    transition: all 0.2s;

    &:hover {
      background: #f0f7ff;
      border-color: #3b82f6;
      color: #2563eb;
    }
  }

  .product-sub {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 8px;
    row-gap: 4px;

    .platform-tag {
      margin: 0;
      font-size: 10px;
      line-height: 18px;
      border-radius: 4px;
    }

    .spu-text {
      font-size: 11px;
      color: #94a3b8;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  .site-account-cell {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
  }

  .site-pill {
    display: inline-block;
    padding: 2px 8px;
    border-radius: 12px;
    background: #f0fdf4;
    color: #15803d;
    font-size: 11px;
    font-weight: 700;
    letter-spacing: 0.5px;
  }

  .account-pill {
    display: inline-block;
    padding: 2px 8px;
    border-radius: 12px;
    background: #f8fafc;
    color: #475569;
    border: 1px solid #e2e8f0;
    font-size: 11px;
    font-weight: 500;
  }

  .account-sub {
    font-size: 11px;
    color: #94a3b8;
    line-height: 1.2;
  }

  .shop-pill {
    display: inline-block;
    padding: 4px 10px;
    border-radius: 20px;
    background: #eff6ff;
    color: #1d4ed8;
    font-size: 12px;
    font-weight: 600;
  }

  .account-text {
    font-size: 11px;
    color: #64748b;
    font-weight: 500;
  }

  .category-text {
    font-size: 11px;
    color: #7c3aed;
    font-weight: 500;
    max-width: 180px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .price-text {
    font-weight: 700;
    color: #0f172a;
    font-size: 14px;
    font-family: 'Inter', sans-serif;
  }

  .inventory-pill {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    min-width: 36px;
    padding: 2px 8px;
    border-radius: 6px;
    font-weight: 700;
    font-size: 13px;

    &.out { background: #fef2f2; color: #dc2626; }
    &.low { background: #fff7ed; color: #ea580c; }
    &.mid { background: #fefce8; color: #ca8a04; }
    &.high { background: #ecfdf5; color: #16a34a; }
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
    align-items: center;
    justify-content: center;
    gap: 8px;
    
    .action-link {
      color: #3b82f6;
      font-size: 13px;
      font-weight: 500;
      transition: all 0.2s;
      
      &:hover {
        color: #1d4ed8;
      }
      
      &.disabled {
        color: #94a3b8;
        cursor: not-allowed;
        pointer-events: none;
      }
      
      &.detail {
        color: #ef4444;
        &:hover { color: #b91c1c; }
      }
    }
    
    .divider {
      color: #cbd5e1;
      font-size: 12px;
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

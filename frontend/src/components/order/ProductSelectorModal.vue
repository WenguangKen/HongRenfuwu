<template>
  <a-modal
    v-model:open="visible"
    title="选择商品"
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
              placeholder="搜索商品名称 / SKU / SPU"
              allow-clear
              class="pro-search-input"
            >
              <template #prefix><search-outlined style="color: #637381" /></template>
            </a-input>
          </div>

          <div class="filter-wrapper">
            <a-select
              v-model:value="sourceFilter"
              class="pro-status-select"
              placeholder="商品分类"
            >
              <a-select-option value="all">全部商品</a-select-option>
              <a-select-option value="recommended">订单商品</a-select-option>
              <a-select-option value="other">非订单商品</a-select-option>
            </a-select>
          </div>
        </div>
      </div>

      <!-- Product Grid -->
      <div class="grid-viewport" :class="{ 'is-loading': loading }">
        <template v-if="loading">
          <div class="loading-state-centered">
            <a-spin size="large" tip="正在同步商品数据..." />
          </div>
        </template>
        <template v-else>
          <div v-if="displayProducts.length > 0" class="product-grid">
            <div
              v-for="product in displayProducts"
              :key="product.id"
              class="pro-card"
              :class="{
                'is-selected': selectedKeys.includes(product.id),
                'is-recommended': product.isRecommended
              }"
              @click="toggleSelection(product.id)"
            >
              <div class="card-media">
                <img
                  :src="optimizeShopifyImage(product.image)"
                  :alt="product.title"
                  loading="lazy"
                  @error="($event.target as HTMLImageElement).src = PLACEHOLDER_IMAGE"
                />
                
                <!-- Status tag: Top Left -->
                <div class="media-tag status-active" v-if="product.status === 'active' || product.status === 'ACTIVE'">
                  ACTIVE
                </div>

                <!-- Order Product tag: Top Right -->
                <div class="media-tag order-rec" v-if="product.isRecommended">
                  <shopping-outlined /> 订单商品
                </div>

                <!-- Selected Check: Bottom Right -->
                <div class="selected-check" v-if="selectedKeys.includes(product.id)">
                  <check-outlined />
                </div>
              </div>
              <div class="card-body">
                <a-tooltip :title="product.title">
                  <div class="card-title">{{ product.title }}</div>
                </a-tooltip>
                
                <div class="specs-box">
                  <div class="id-row">
                    <span class="id-label">SPU:</span>
                    <span class="id-val spu" title="点击复制 SPU" @click="handleCopy(product.spu, 'SPU')">{{ product.spu || '-' }}</span>
                  </div>
                  <div class="id-row">
                    <span class="id-label">SKU:</span>
                    <span class="id-val sku" title="点击复制 SKU" @click="handleCopy(product.sku, 'SKU')">{{ product.sku || '-' }}</span>
                  </div>
                  <div class="card-variant">
                    <span class="id-label">规格详情:</span>
                    <span class="id-val txt">{{ product.variant && product.variant !== '-' ? product.variant : '-' }}</span>
                  </div>
                </div>
              </div>
              <div class="product-footer">
                <div class="card-price">¥{{ product.price.toFixed(2) }}</div>
                <div class="card-stock" :class="{ 'warning': product.inventory <= 5 }">
                  {{ product.inventory }} <span style="font-size: 10px; opacity: 0.7">库存</span>
                </div>
              </div>
            </div>
          </div>
          <div v-else class="empty-view">
            <shopping-outlined style="font-size: 48px; color: #cbd5e1;" />
            <div style="color: #94a3b8; margin-top: 12px;">暂无匹配商品</div>
          </div>
        </template>
      </div>

      <!-- Footer -->
      <div class="selector-footer">
        <div class="footer-left">
          <a-tag v-if="sourceFilter === 'recommended'" color="blue">
            订单商品 (前端展示)
          </a-tag>
          <template v-else>
            <span style="color: #94a3b8; font-size: 13px;">
              共 {{ totalElements }} 款主商品，第 {{ currentPage }} / {{ totalPages || 1 }} 页
              <span style="margin-left:8px; opacity: 0.8; font-size: 12px;">(展示所有规格)</span>
            </span>
          </template>
          <span style="margin-left: 16px; color: #64748b; font-size: 13px; font-weight: 600;">
            已选: {{ selectedKeys.length }} 件
          </span>
        </div>
        <div class="footer-right">
          <a-space :size="12">
            <a-button @click="handleCancel" class="cancel-btn">取 消</a-button>
            <a-button
              type="primary"
              class="action-btn"
              @click="handleOk"
            >
              确认选定
            </a-button>
          </a-space>
        </div>
      </div>

      <!-- Backend Pagination Controls -->
      <div class="pagination-bar" v-if="sourceFilter !== 'recommended' && totalPages > 1">
        <a-pagination
          v-model:current="currentPage"
          :total="totalElements"
          :page-size="pageSize"
          show-quick-jumper
          :show-size-changer="false"
          @change="onPageChange"
        />
      </div>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { SearchOutlined, ShoppingOutlined, CheckOutlined } from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import { getEccangProducts } from '@/services/eccangService';

// Placeholder image for missing product images
const PLACEHOLDER_IMAGE = "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='100%25' height='100%25' viewBox='0 0 24 24' fill='none' stroke='%23cbd5e1' stroke-width='1.5' stroke-linecap='round' stroke-linejoin='round'%3E%3Crect x='3' y='3' width='18' height='18' rx='2' ry='2'%3E%3C/rect%3E%3Ccircle cx='8.5' cy='8.5' r='1.5'%3E%3C/circle%3E%3Cpolyline points='21 15 16 10 5 21'%3E%3C/polyline%3E%3C/svg%3E";

// Copy utility
const handleCopy = async (text: string, label: string) => {
  if (!text || text === '-') return;
  try {
    text = text.trim();
    await navigator.clipboard.writeText(text);
    message.success(`已复制 ${label}`);
  } catch (error) {
    message.error('复制失败');
  }
};

const optimizeShopifyImage = (url?: string, size = '400') => {
  if (!url) return PLACEHOLDER_IMAGE;
  if (!url.includes('cdn.shopify.com')) return url;
  try {
    const u = new URL(url);
    u.searchParams.delete('crop');
    u.searchParams.set('width', size);
    return u.toString();
  } catch (e) {
    return url;
  }
};

const generateFingerprint = (sku: string, variant: string, _spu?: any, vid?: any) => {
  if (vid) return `vid:${vid}`;
  const s = String(sku || '').toLowerCase().replace(/[^a-z0-9]/g, '');
  const v = String(variant || '').toLowerCase().replace(/[^a-z0-9]/g, '');
  return `${s}|${v}`;
};

interface ProductItem {
  id: number;
  title: string;
  variant: string;
  sku: string;
  spu: string;
  price: number;
  inventory: number;
  status: string;
  image?: string;
  shopifyVariantId?: number;
  shopifyProductId?: number;
  shopifyProductGid?: string;
  isRecommended?: boolean;
}

const props = withDefaults(defineProps<{
  open: boolean;
  storeId?: number;
  multiple?: boolean;
  initialSelectedKeys?: number[];
  initialSelectedSkus?: string[];
  recommendedSkus?: string[];
  orderProducts?: any[];
}>(), {
  multiple: true,
  initialSelectedKeys: () => [],
  initialSelectedSkus: () => [],
  recommendedSkus: () => [],
  orderProducts: () => [],
});

const emit = defineEmits(['update:open', 'select']);

const visible = computed({
  get: () => props.open,
  set: (val) => emit('update:open', val),
});

// Core state
const searchKeyword = ref('');
const sourceFilter = ref<string>('all');
const loading = ref(false);
const products = ref<ProductItem[]>([]);
const selectedKeys = ref<number[]>([]);
const stableSortWeights = ref<Map<number, number>>(new Map());
const selectedItemsMap = ref<Map<number, ProductItem>>(new Map());

// Backend pagination state
const currentPage = ref(1);
const pageSize = 20;
const totalElements = ref(0);
const totalPages = ref(0);

let searchTimer: ReturnType<typeof setTimeout> | null = null;
let isInitialFetchForSession = false;

// ========== Data Fetching ==========

const fetchProducts = async () => {
  if (!props.storeId) return;
  loading.value = true;
  try {
    const params: any = {
      storeId: props.storeId,
      page: currentPage.value - 1, // backend page is 0-based
      size: pageSize,
    };
    if (searchKeyword.value.trim()) {
      params.keyword = searchKeyword.value.trim();
    }

    const res = await getEccangProducts(params);

    // Backend returns Page object: { content, totalElements, totalPages, ... }
    const isPageResponse = res && typeof res === 'object' && 'content' in res;

    let rawProducts: any[];
    if (isPageResponse) {
      rawProducts = (res as any).content || [];
      totalElements.value = (res as any).totalElements || 0;
      totalPages.value = (res as any).totalPages || 0;
    } else {
      rawProducts = Array.isArray(res) ? res : [];
      totalElements.value = rawProducts.length;
      totalPages.value = 1;
    }

    // Flatten product + variants into ProductItem[]
    const items: ProductItem[] = [];
    const recommendedSet = new Set(props.recommendedSkus || []);

    for (const product of rawProducts) {
      const productStatus = product.status || 'active';
      const spuId = product.spu || String(product.shopifyProductId || '-');

      const getProductImage = (p: any) => {
        let raw = p.mainImage || p.image || p.imageUrl || (p.images && p.images[0]?.src);
        if (!raw && p.spuImages) {
          try {
            const arr = JSON.parse(p.spuImages);
            if (Array.isArray(arr) && arr.length > 0) {
              raw = arr[0].url || arr[0].src || arr[0];
            } else if (typeof arr === 'string') {
              raw = arr;
            }
          } catch(e) {
            const str = String(p.spuImages).trim();
            if (str.startsWith('http')) {
              raw = str.split(',')[0];
            }
          }
        }
        if (!raw || raw === 'null' || raw === 'undefined') return '';
        
        let url = String(raw).trim();
        url = url.replace(/^['"]+|['"]+$/g, '').replace(/^\["?|"?\]$/g, '');
        if (url.startsWith('//')) {
          url = `https:${url}`;
        } else if (!url.startsWith('http')) {
          return '';
        }
        return url;
      };

      const variants = product.variants || [];
      if (variants.length === 0) {
        const displaySku = product.sku || spuId;
        const matchKey = generateFingerprint(String(displaySku), '', product.shopifyProductId, product.shopifyVariantId);
        const isRec = recommendedSet.has(matchKey);

        items.push({
          id: product.id,
          title: product.title,
          variant: '-',
          sku: String(displaySku),
          spu: spuId,
          price: parseFloat(product.price) || 0,
          inventory: product.inventoryQuantity || product.totalInventory || 0,
          status: productStatus,
          image: getProductImage(product),
          shopifyProductId: product.shopifyProductId,
          shopifyProductGid: product.shopifyGid,
          isRecommended: isRec,
        });
      } else {
        for (const variant of variants) {
          const sanitize = (v: string | null | undefined) => {
            if (!v || v === 'null' || v === 'undefined' || v === 'Default Title') return '-';
            return v;
          };

          const displaySku = variant.sku && variant.sku !== 'null' && variant.sku !== 'undefined'
            ? variant.sku
            : (product.sku || spuId);

          const opVid = variant.shopifyVariantId;
          const variantTitle = sanitize(variant.title);
          const matchKey = generateFingerprint(
            String(displaySku),
            variantTitle === '-' ? '' : variantTitle,
            product.shopifyProductId,
            opVid
          );
          const isRec = recommendedSet.has(matchKey);
          
          let vImage = variant.imageUrl;
          if (vImage && vImage !== 'null' && vImage !== 'undefined') {
            vImage = String(vImage).trim().replace(/^['"]+|['"]+$/g, '').replace(/^\["?|"?\]$/g, '');
            vImage = vImage.startsWith('//') ? `https:${vImage}` : vImage;
            if (!vImage.startsWith('http')) {
              vImage = getProductImage(product);
            }
          } else {
            vImage = getProductImage(product);
          }

          items.push({
            id: variant.id || product.id,
            title: product.title,
            variant: variantTitle,
            sku: String(displaySku),
            spu: spuId,
            price: parseFloat(variant.price) || 0,
            inventory: variant.inventoryQuantity || 0,
            status: productStatus,
            image: vImage,
            shopifyVariantId: opVid ? Number(opVid) : undefined,
            shopifyProductId: product.shopifyProductId,
            shopifyProductGid: product.shopifyGid,
            isRecommended: isRec,
          });
        }
      }
    }

    products.value = items;
    calculateStableWeights();
  } catch (error) {
    console.error('[ProductSelectorModal] fetchProducts error:', error);
    products.value = [];
  } finally {
    loading.value = false;
  }
};

// ========== Order Products (in-memory) ==========

const buildOrderProducts = (): ProductItem[] => {
  if (!props.orderProducts || props.orderProducts.length === 0) return [];

  return props.orderProducts.map((op, idx) => {
    const sku = op.sku || '';
    const variant = op.variantTitle || op.variant_title || op.variant || '';
    let img = op.imageUrl || op.image || '';
    if (img && img.startsWith('//')) img = `https:${img}`;
    
    return {
      id: op.id || -(idx + 10000), // PRESERVE REAL ID to prevent cache miss!
      title: op.title || op.name || '订单商品',
      variant: variant || '-',
      sku: sku,
      spu: String(op.spu || op.shopifyProductId || '-'),
      price: parseFloat(op.price || '0') || 0,
      inventory: op.inventoryQuantity || op.quantity || 0,
      status: op.status || 'active',
      image: img,
      shopifyVariantId: op.shopifyVariantId ? Number(op.shopifyVariantId) : undefined,
      shopifyProductId: op.shopifyProductId ? Number(op.shopifyProductId) : undefined,
      isRecommended: true,
    };
  });
};

// ========== Sorting & Filtering ==========

const calculateStableWeights = () => {
  const weights = new Map<number, number>();
  const recommendedSet = new Set(props.recommendedSkus || []);
  const currentSelectedSet = new Set(selectedKeys.value);

  const allCandidates = new Map<number, ProductItem>();
  products.value.forEach(p => allCandidates.set(p.id, p));
  buildOrderProducts().forEach(p => allCandidates.set(p.id, p));
  selectedItemsMap.value.forEach(p => allCandidates.set(p.id, p));

  allCandidates.forEach(p => {
    const matchKey = generateFingerprint(p.sku, p.variant === '-' ? '' : p.variant, p.shopifyProductId, p.shopifyVariantId);
    const isRec = recommendedSet.has(matchKey) || p.isRecommended;
    const isSel = currentSelectedSet.has(p.id);
    let score = 0;
    if (isRec && isSel) score = 3;
    else if (isRec) score = 2;
    else if (isSel) score = 1;
    weights.set(p.id, score);
  });
  stableSortWeights.value = weights;
};

const displayProducts = computed(() => {
  if (sourceFilter.value === 'recommended') {
    // Show order products from memory, no backend request needed
    const orderItems = buildOrderProducts();
    const recFromFetched = products.value.filter(p => p.isRecommended);
    const merged = [...orderItems];
    for (const p of recFromFetched) {
      if (!merged.some(m => m.shopifyVariantId && m.shopifyVariantId === p.shopifyVariantId)) {
        merged.push(p);
      }
    }
    return merged;
  }

  let list = [...products.value];

  if (sourceFilter.value === 'other') {
    list = list.filter(p => !p.isRecommended);
  }

  if (sourceFilter.value === 'all') {
    const orderItems = buildOrderProducts();
    const selectedItems = Array.from(selectedItemsMap.value.values());
    const priorityItemsSet = new Map<number, ProductItem>();
    
    for (const item of orderItems) priorityItemsSet.set(item.id, item);
    for (const item of selectedItems) priorityItemsSet.set(item.id, item);
    
    const priorityItemsList = Array.from(priorityItemsSet.values());
    list = list.filter(p => !priorityItemsSet.has(p.id));
    list = [...priorityItemsList, ...list];
  }

  // Remove: list = list.filter(p => p.price > 0);

  // Stable weight sorting to prevent jumpiness on selection
  list = [...list].sort((a, b) => {
    const scoreA = stableSortWeights.value.get(a.id) || 0;
    const scoreB = stableSortWeights.value.get(b.id) || 0;
    if (scoreA !== scoreB) return scoreB - scoreA;
    return 0;
  });

  return list;
});

// ========== Interaction ==========

const toggleSelection = (id: number) => {
  const index = selectedKeys.value.indexOf(id);
  const product = products.value.find(p => p.id === id)
    || displayProducts.value.find(p => p.id === id);

  if (index > -1) {
    selectedKeys.value.splice(index, 1);
    selectedItemsMap.value.delete(id);
  } else {
    if (props.multiple) {
      selectedKeys.value.push(id);
      if (product) selectedItemsMap.value.set(id, product);
    } else {
      selectedKeys.value = [id];
      selectedItemsMap.value.clear();
      if (product) selectedItemsMap.value.set(id, product);
    }
  }
};

const handleOk = () => {
  const selectedItems = selectedKeys.value
    .map(id => selectedItemsMap.value.get(id))
    .filter(Boolean);
  emit('select', selectedItems);
  visible.value = false;
};

const handleCancel = () => {
  visible.value = false;
};

const onPageChange = (page: number) => {
  currentPage.value = page;
  fetchProducts();
};

// ========== Lifecycle & Watchers ==========

watch(() => props.open, async (val) => {
  if (!val) return;

  isInitialFetchForSession = true;

  // 1. Initialize selection state safely
  const initialIds = (props.initialSelectedKeys || []).map(id => Number(id));
  selectedKeys.value = [...initialIds];
  selectedItemsMap.value.clear();

  // Pick optimal view
  sourceFilter.value = (props.recommendedSkus && props.recommendedSkus.length > 0) ? 'recommended' : 'all';
  searchKeyword.value = '';
  currentPage.value = 1;

  // 2. Fetch the current page
  await fetchProducts();

  // 3. Populate memory cache correctly from order metadata so selections out-of-page are remembered
  const orderList = buildOrderProducts();
  const memoryItems = [...products.value, ...orderList];
  
  // Directly restore cache for keys already passed via initialSelectedKeys
  for (const pid of selectedKeys.value) {
    if (!selectedItemsMap.value.has(pid)) {
      const match = memoryItems.find(x => x.id === pid);
      if (match) selectedItemsMap.value.set(pid, match);
    }
  }

  // 4. Auto-match recommended SKUs to selections using advanced fingerprinting
  if (props.initialSelectedSkus && props.initialSelectedSkus.length > 0) {
    const remainingSkus = [...props.initialSelectedSkus];
    const newKeys: number[] = [...selectedKeys.value];

    // Merge fetched and order items to guarantee we find previously selected items even off-page
    const allCandidates = new Map<number, ProductItem>();
    memoryItems.forEach(i => allCandidates.set(i.id, i));
    
    // Convert to array and prefer recommended items naturally
    const sortedForMatching = Array.from(allCandidates.values()).sort((a, b) => {
      const aRec = a.isRecommended ? 1 : 0;
      const bRec = b.isRecommended ? 1 : 0;
      return bRec - aRec;
    });

    sortedForMatching.forEach(p => {
      const pid = Number(p.id);
      const s = String(p.sku || '').toLowerCase().replace(/[^a-z0-9]/g, '');
      const v = String(p.variant === '-' ? '' : p.variant).toLowerCase().replace(/[^a-z0-9]/g, '');
      const exactFinger = `${s}|${v}`;
      const shopifyVid = p.shopifyVariantId ? String(p.shopifyVariantId) : null;

      let skuIdx = -1;

      if (shopifyVid) {
        skuIdx = remainingSkus.findIndex(req => req === `vid:${shopifyVid}`);
      }
      if (skuIdx === -1) {
        skuIdx = remainingSkus.indexOf(exactFinger);
      }
      if (skuIdx === -1) {
        skuIdx = remainingSkus.findIndex(req => String(req).startsWith(`${s}|`));
      }
      if (skuIdx === -1) {
        skuIdx = remainingSkus.findIndex(req => String(req).toLowerCase() === s);
      }

      if (skuIdx > -1 && !newKeys.includes(pid)) {
        newKeys.push(pid);
        remainingSkus.splice(skuIdx, 1);
        selectedItemsMap.value.set(pid, p); // cache it!
      }
    });

    selectedKeys.value = [...newKeys];
  }

  // 5. Final fallback to ensure state is clean
  products.value.forEach(p => {
    const pid = Number(p.id);
    if (selectedKeys.value.includes(pid) && !selectedItemsMap.value.has(pid)) {
      selectedItemsMap.value.set(pid, p);
    }
  });

  calculateStableWeights();
  isInitialFetchForSession = false;
});

// Debounced search
watch(searchKeyword, () => {
  if (isInitialFetchForSession) return;
  if (sourceFilter.value === 'recommended' && searchKeyword.value) {
    sourceFilter.value = 'all';
  }
  if (searchTimer) clearTimeout(searchTimer);
  searchTimer = setTimeout(() => {
    if (isInitialFetchForSession) return;
    currentPage.value = 1;
    fetchProducts();
  }, 400);
});

// Source filter change
watch(sourceFilter, (val) => {
  if (isInitialFetchForSession) return;
  if (val === 'recommended') return;
  currentPage.value = 1;
  fetchProducts();
});
</script>

<style scoped lang="scss">
$pro-primary: #008060;
$pro-primary-hover: #006e52;
$pro-primary-light: #f0fdf4;
$pro-text: #202223;
$pro-muted: #6d7175;
$pro-border: #dfe3e8;
$pro-bg: #f6f6f7;
$pro-danger: #d72c0d;

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

.search-section {
  padding: 16px 24px;
  background: #fff;
  border-bottom: 1px solid #f1f2f3;
  .search-inner {
    display: flex;
    gap: 12px;
    align-items: center;
  }
}

.search-input-wrapper {
  :deep(.ant-input-affix-wrapper) {
    width: 320px;
    border-radius: 8px;
    border-color: $pro-border;
    padding: 4px 11px;
    height: 40px;
    display: flex;
    align-items: center;
    background: #fff;
    transition: all 0.2s;
    &:hover, &.ant-input-affix-wrapper-focused {
      border-color: $pro-primary;
      box-shadow: 0 0 0 2px rgba($pro-primary, 0.1);
    }
  }
}

.pro-search-input {
  width: 320px;
  height: 40px;
}

.pro-status-select {
  width: 180px;
  :deep(.ant-select-selector) {
    border-radius: 8px !important;
    height: 40px !important;
    display: flex;
    align-items: center;
    border-color: $pro-border !important;
    &:hover { border-color: $pro-primary !important; }
  }
}

.grid-viewport {
  height: 680px;
  background: $pro-bg;
  padding: 20px 24px;
  overflow-y: auto;
  position: relative;
  &.is-loading {
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

.loading-state-centered {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 100px;
  :deep(.ant-spin-text) {
    margin-top: 12px;
    font-weight: 600;
    color: #64748b;
  }
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.pro-card {
  background: #fff;
  border: 1px solid $pro-border;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  height: 350px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

  &:hover {
    border-color: #cbd5e1;
    box-shadow: 0 12px 24px rgba(0, 0, 0, 0.08);
    transform: translateY(-4px);
  }
  &.is-recommended {
    border: 1.5px solid #2563eb;
    background: #f0f7ff;
    box-shadow: 0 4px 12px rgba(37, 99, 235, 0.1);
  }
  &.is-selected {
    border: 2px solid $pro-primary !important;
    background: #f0fdf4 !important;
    box-shadow: 0 0 0 1px $pro-primary inset, 0 8px 20px rgba(0, 128, 96, 0.15) !important;
  }
}

.card-media {
  width: 100%;
  height: 200px;
  background: #f4f6f8;
  position: relative;
  overflow: hidden;
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;
  }
  &:hover img {
    transform: scale(1.04);
  }
}

.rec-tag-float {
  display: none;
}

.media-tag {
  position: absolute;
  padding: 4px 8px;
  font-size: 11px;
  font-weight: 800;
  line-height: 1;
  text-transform: uppercase;
  z-index: 2;
  
  &.status-active {
    top: 0;
    left: 0;
    background: #008060;
    color: #fff;
    border-bottom-right-radius: 8px;
  }
  
  &.order-rec {
    top: 0;
    right: 0;
    background: #2563eb;
    color: #fff;
    border-bottom-left-radius: 8px;
    display: flex;
    align-items: center;
    gap: 4px;
    font-weight: 600;
  }
}

.selected-check {
  position: absolute;
  bottom: 8px;
  right: 8px;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #008060;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.15);
  z-index: 2;
}

.card-body {
  flex: 1;
  padding: 12px 12px 0px;
  display: flex;
  flex-direction: column;
}

.card-title {
  font-size: 14px;
  font-weight: 700;
  color: $pro-text;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-bottom: 12px;
}

.specs-box {
  background: #fafafa;
  border-radius: 6px;
  padding: 8px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 12px;
}

.id-row, .card-variant {
  display: flex;
  align-items: center;
  font-size: 11px;
}

.id-label {
  color: #8c9196;
  width: 50px;
  flex-shrink: 0;
  font-weight: 600;
}

.id-val {
  padding: 2px 6px;
  border-radius: 4px;
  font-family: monospace;
  font-weight: 600;
  
  &.spu {
    background: #f4f6f8;
    color: #202223;
  }
  
  &.sku {
    background: #e3f1df;
    color: #008060;
  }
  
  &.txt {
    font-family: inherit;
    background: transparent;
    color: #6d7175;
    padding: 0;
    font-weight: 400;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  border-top: 1px solid #f1f2f3;
}

.card-price {
  font-size: 15px;
  font-weight: 700;
  color: $pro-primary;
}

.card-stock {
  font-size: 12px;
  color: $pro-muted;
  &.warning {
    color: $pro-danger;
    font-weight: 600;
  }
}

.empty-view {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
}

.selector-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 24px;
  border-top: 1px solid $pro-border;
  background: #fff;
}

.pagination-bar {
  display: flex;
  justify-content: center;
  padding: 8px 24px 16px;
  background: #fff;
}

.cancel-btn {
  border-radius: 8px;
  height: 36px;
  border-color: $pro-border;
  &:hover { border-color: $pro-primary; color: $pro-primary; }
}

.action-btn {
  border-radius: 8px;
  height: 36px;
  font-weight: 600;
  background: $pro-primary;
  border-color: $pro-primary;
  &:hover { background: $pro-primary-hover; border-color: $pro-primary-hover; }
}
</style>

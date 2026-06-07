<template>
  <a-modal
    v-model:open="visible"
    :title="null"
    width="1200px" 
    :footer="null"
    centered
    class="premium-refined-modal"
    wrapClassName="product-detail-modal-wrap"
    destroyOnClose
    :closable="false"
    :body-style="{ padding: '0px', height: '750px' }"
  >
    <!-- Custom Header (IC Style) -->
    <div class="ic-modal-header">
      <div class="ic-header-left">
        <div class="ic-header-icon">
          <ShoppingOutlined />
        </div>
        <div class="ic-header-text">
          <div class="ic-header-title">
            商品详情
            <span class="order-no-tags">
              <a-tag v-if="productData?.spu">SPU: {{ productData.spu }}</a-tag>
            </span>
          </div>
          <div class="ic-header-subtitle">
            查看商品基本信息及 SKU 详情
          </div>
        </div>
      </div>
      <div class="ic-header-right"></div>
      <a-button type="text" class="close-btn" @click="handleCancel">
        <CloseOutlined />
      </a-button>
    </div>

    <!-- Body Container -->
    <div class="modal-body-container">
      <div class="root-tabs-container">
        <a-tabs 
          v-model:activeKey="activeTab" 
          tab-position="left" 
          class="unified-height-tabs"
        >
          <!-- Tab 1: Info (Refined Grid Layout) -->
          <a-tab-pane key="info" class="unified-tab-pane">
             <template #tab>
               <span class="tab-label-box"><InfoCircleOutlined /> 商品信息</span>
             </template>
             <div class="unified-tab-content locked-height">
               <div class="unified-content-wrapper scrollable-y">
                 
                 <!-- Section 1: Header (Title & Status) -->
                 <div class="product-header-section" style="margin-bottom: 24px;">
                    <div class="ph-top-row" style="display: flex; justify-content: space-between; align-items: flex-start;">
                       <div class="ph-title" style="font-size: 18px; font-weight: 700; color: #1e293b; line-height: 1.4;">
                          {{ productData?.name }}
                       </div>
                       <div class="ph-status">
                          <a-tag v-if="productData?.status === 'active'" color="green" style="margin: 0; padding: 4px 10px; border-radius: 6px; font-weight: 600;">已上架</a-tag>
                          <a-tag v-else-if="productData?.status === 'draft'" color="default" style="margin: 0; padding: 4px 10px; border-radius: 6px; font-weight: 600;">草稿</a-tag>
                          <a-tag v-else-if="productData?.status === 'archived'" color="orange" style="margin: 0; padding: 4px 10px; border-radius: 6px; font-weight: 600;">归档</a-tag>
                          <a-tag v-else-if="productData?.status === 'out_of_stock'" color="red" style="margin: 0; padding: 4px 10px; border-radius: 6px; font-weight: 600;">缺货</a-tag>
                          <a-tag v-else-if="['inactive', 'unlisted'].includes(productData?.status)" color="default" style="margin: 0; padding: 4px 10px; border-radius: 6px; font-weight: 600;">已下架</a-tag>
                          <a-tag v-else color="default" style="margin: 0; padding: 4px 10px; border-radius: 6px; font-weight: 600;">{{ productData?.status || '未知' }}</a-tag>
                       </div>
                    </div>
                 </div>

                 <!-- Section 2: Image Gallery (Moved to Top) -->
                 <div class="gallery-section" style="margin-bottom: 24px;">
                    <div class="image-group-list">
                        <div class="image-item-wrapper" v-for="(img, idx) in resolvedImages" :key="idx">
                           <img :src="img" class="product-img" @load="handleImageLoad($event, idx)" @error="handleImageError(idx)" @click="previewImage(img)" />
                           <div class="img-tag" v-if="idx === 0">主图</div>
                        </div>
                    </div>
                 </div>

                 <!-- Section 3: Info Grid -->
                 <a-descriptions bordered size="small" :column="2" class="premium-descriptions">
                   <a-descriptions-item label="站点">
                      {{ productData?.siteCode || '-' }}
                   </a-descriptions-item>

                   <a-descriptions-item label="所属店铺">
                      {{ productData?.shopName || '-' }}
                   </a-descriptions-item>

                   <a-descriptions-item label="易仓账号">
                      {{ productData?.platformAccount || '-' }}
                   </a-descriptions-item>

                   <a-descriptions-item label="品牌">
                      {{ productData?.vendor || '-' }}
                   </a-descriptions-item>

                   <a-descriptions-item label="商品属性" :span="2">
                      {{ productData?.attributesText || '-' }}
                   </a-descriptions-item>
                   
                   <a-descriptions-item label="分类" :span="2">
                      {{ productData?.categoryPath || '-' }}
                   </a-descriptions-item>
                   
                   <a-descriptions-item label="父 ASIN">
                      <span style="font-family: 'JetBrains Mono'; color: #64748b;">{{ productData?.spu || '-' }}</span>
                   </a-descriptions-item>

                   <a-descriptions-item label="商品售价">
                      <span style="color: #1890ff; font-weight: 700; font-family: 'JetBrains Mono'; font-size: 14px;">
                        {{ productData?.displayPrice || ('$' + (productData?.price || 0)) }}
                      </span>
                   </a-descriptions-item>

                   <a-descriptions-item label="总库存">
                     <span :style="{ color: totalInventory < 10 ? '#ef4444' : '#10b981', fontWeight: 600 }">
                       {{ totalInventory }}
                     </span>
                   </a-descriptions-item>
                   
                   <a-descriptions-item label="商品链接" :span="2">
                     <a :href="productData?.link" target="_blank" style="word-break: break-all;">{{ productData?.link }}</a>
                   </a-descriptions-item>
                 </a-descriptions>
                 
                 <!-- Section 4: Product Description (Restored) -->
                 <div class="description-section" style="margin-top: 24px;">
                    <div class="section-title" style="font-size: 14px; font-weight: 700; color: #475569; margin-bottom: 12px; border-left: 3px solid #3b82f6; padding-left: 8px;">
                       商品介绍
                    </div>
                    <div class="html-content" v-html="productData?.bodyHtml || '<p style=\'color:#94a3b8\'>暂无介绍</p>'" style="font-size: 13px; color: #334155; line-height: 1.6;"></div>
                 </div>

               </div>
             </div>
          </a-tab-pane>

          <!-- Tab 2: Variants (Clean Table Style) -->
          <a-tab-pane key="variants" class="unified-tab-pane">
            <template #tab>
              <span class="tab-label-box"><AppstoreOutlined /> 变体详情</span>
            </template>
            <div class="unified-tab-content locked-height" style="display: flex; flex-direction: column;">
              <div class="variants-header-actions" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; flex-shrink: 0; flex-wrap: wrap; gap: 10px;">
                 <div class="filter-group" style="display: flex; align-items: center; gap: 8px; flex-wrap: wrap;">
                    <div class="section-tips" style="font-size: 13px; color: #94a3b8; white-space: nowrap;">
                       共 {{ filteredVariants.length }} 个变体
                    </div>
                    <a-select
                      v-if="uniqueOptions.option1.length > 1"
                      v-model:value="optionFilters.option1"
                      placeholder="规格1"
                      style="min-width: 100px;"
                      allowClear
                      size="small"
                    >
                      <a-select-option v-for="opt in uniqueOptions.option1" :key="opt" :value="opt">{{ opt }}</a-select-option>
                    </a-select>
                    <a-select
                      v-if="uniqueOptions.option2.length > 1"
                      v-model:value="optionFilters.option2"
                      placeholder="规格2"
                      style="min-width: 100px;"
                      allowClear
                      size="small"
                    >
                      <a-select-option v-for="opt in uniqueOptions.option2" :key="opt" :value="opt">{{ opt }}</a-select-option>
                    </a-select>
                    <a-select
                      v-if="uniqueOptions.option3.length > 1"
                      v-model:value="optionFilters.option3"
                      placeholder="规格3"
                      style="min-width: 100px;"
                      allowClear
                      size="small"
                    >
                      <a-select-option v-for="opt in uniqueOptions.option3" :key="opt" :value="opt">{{ opt }}</a-select-option>
                    </a-select>
                 </div>
                 <div class="search-box">
                    <a-input 
                      v-model:value="searchText" 
                      placeholder="搜索 SKU 或 ID" 
                      style="width: 200px; border-radius: 6px; font-size: 12px;"
                      allowClear
                    >
                       <template #prefix><SearchOutlined style="color: #cbd5e1" /></template>
                    </a-input>
                 </div>
              </div>
              
              <div class="variants-table-wrapper clean-style scrollable-table-container">
                <table class="premium-detail-table clean-table">
                  <thead>
                     <tr>
                       <th style="width: 260px;">SKU</th>
                       <th style="width: 140px;">ASIN</th>
                       <th style="text-align: center; width: 60px;">图片</th>
                       <th>变体属性</th>
                       <th style="text-align: right; width: 100px;">售价</th>
                       <th style="text-align: center; width: 100px;">库存状态</th>
                     </tr>
                  </thead>
                  <tbody>
                    <tr v-for="(variant, index) in filteredVariants" :key="variant.id || index">
                       <td>
                          <div class="sku-cell-content" style="display: flex; align-items: center; gap: 6px;">
                             <!-- Copyable Text -->
                             <div class="sku-text-only" @click.stop="handleCopy(variant.shopifyVariantId?.toString() || variant.sku)" title="点击复制">
                                <span v-if="variant.sku && variant.sku !== 'null'">{{ variant.sku }}</span>
                                <span v-else>{{ variant.shopifyVariantId || '-' }}</span>
                             </div>

                             <!-- Status Tag -->
                             <a-tag v-if="variant.status === 'active'" color="green" style="font-size: 11px; padding: 0 4px; line-height: 16px; margin: 0; font-weight: 600;">上架</a-tag>
                             <a-tag v-else-if="variant.status === 'draft'" color="default" style="font-size: 11px; padding: 0 4px; line-height: 16px; margin: 0; font-weight: 600;">草稿</a-tag>
                             <a-tag v-else color="red" style="font-size: 11px; padding: 0 4px; line-height: 16px; margin: 0; font-weight: 600;">下架</a-tag>

                             <!-- Shopify Admin Link -->
                             <a 
                               v-if="productData?.shopDomain && variant.shopifyVariantId" 
                               :href="`https://${productData.shopDomain}/admin/products/${productData.shopifyId}/variants/${variant.shopifyVariantId}`" 
                               target="_blank" 
                               class="admin-link-btn"
                               title="在 Shopify 后台查看"
                               @click.stop
                             >
                                <LinkOutlined />
                             </a>
                          </div>
                       </td>
                       <td>
                          <div class="asin-cell-content" style="display: flex; align-items: center; gap: 6px;">
                             <!-- Copyable Text -->
                             <div class="sku-text-only" @click.stop="handleCopy(variant.asin)" title="点击复制">
                                <span>{{ variant.asin || '-' }}</span>
                             </div>
                             <!-- Amazon Link -->
                             <a 
                               v-if="variant.asin && variant.asin !== '-'" 
                               :href="variant.asinUrl || `https://www.amazon.com/dp/${variant.asin}`" 
                               target="_blank" 
                               class="admin-link-btn"
                               title="在亚马逊查看"
                               @click.stop
                             >
                                <LinkOutlined />
                             </a>
                          </div>
                       </td>
                        <td style="text-align: center;">
                           <div class="variant-img-thumbnail small" v-if="resolveVariantImage(variant)" @click.stop="previewImage(resolveVariantImage(variant))">
                              <img :src="resolveVariantImage(variant)" @load="handleVariantImgLoad($event, variant)" @error="handleVariantImgError($event, variant)" />
                           </div>
                           <div v-else class="variant-img-placeholder small"><PictureOutlined /></div>
                        </td>
                       <td>
                          <div class="variant-options">
                             {{ [variant.option1, variant.option2, variant.option3].filter(val => val && val !== 'null').join(' / ') || '默认变体' }}
                          </div>
                       </td>
                       <td style="text-align: right;">
                          <span class="price-mono">${{ variant.price || 0 }}</span>
                       </td>
                       <td style="text-align: center;">
                          <span class="inventory-pill" :class="{ 'low': (variant.inventoryQuantity || 0) < 10, 'out': (variant.inventoryQuantity || 0) <= 0 }">
                             {{ (variant.inventoryQuantity || 0) > 0 ? ((variant.inventoryQuantity || 0) + ' 件') : '缺货' }}
                          </span>
                       </td>
                    </tr>
                    <tr v-if="filteredVariants.length === 0">
                       <td colspan="6" style="text-align: center; padding: 40px; color: #94a3b8;">
                          无匹配变体
                       </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </a-tab-pane>
        </a-tabs>
      </div>
    </div>
    
    <!-- Image Preview Modal -->
    <a-modal :open="previewVisible" :footer="null" @cancel="previewVisible = false" centered width="600px" style="z-index: 2000;">
      <img :src="previewUrl" style="width: 100%;" />
    </a-modal>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { SkinOutlined, CloseOutlined, UserOutlined, InfoCircleOutlined, HistoryOutlined, AppstoreOutlined, CopyOutlined, PictureOutlined, ShoppingOutlined, SearchOutlined } from '@ant-design/icons-vue';
import { Empty, message } from 'ant-design-vue';

const simpleImage = Empty.PRESENTED_IMAGE_SIMPLE;

const props = defineProps<{
  open: boolean;
  productData: any;
}>();

const emit = defineEmits<{
  (e: 'update:open', value: boolean): void;
}>();

const spuImageList = computed(() => {
  const imgs = props.productData?.spuImages;
  if (imgs && typeof imgs === 'string') {
    return imgs.split(',').filter(Boolean);
  }
  return props.productData?.image ? [props.productData.image] : [];
});

const resolvedImages = ref<string[]>([]);
watch(spuImageList, (newImgs) => {
  resolvedImages.value = [...newImgs];
}, { immediate: true });

const PLACEHOLDER_IMAGE =
  'data:image/svg+xml,' + encodeURIComponent(
    '<svg xmlns="http://www.w3.org/2000/svg" width="120" height="120" viewBox="0 0 120 120"><rect fill="#f1f5f9" width="120" height="120" rx="12"/><path fill="#94a3b8" d="M44 36h32c4 0 7 3 7 7v34c0 4-3 7-7 7H44c-4 0-7-3-7-7V43c0-4 3-7 7-7zm16 6a10 10 0 100 20 10 10 0 000-20zm-18 42h36l-9-12-6 8-7-9-14 13z"/></svg>'
  );

const isPlaceholderImage = (url?: string | null) => {
  if (!url) return true;
  return url.includes('noimg.jpg') || url.includes('/images/base/noimg') || url.startsWith('data:image/svg+xml');
};

const handleImageError = (idx: number) => {
  const currentUrl = resolvedImages.value[idx];
  if (!currentUrl) return;

  const match = currentUrl.match(/\/images\/P\/([A-Z0-9]{10})/i);
  if (match) {
    const asin = match[1];
    const adSystemUrl = `https://ws-na.amazon-adsystem.com/widgets/q?ASIN=${asin}&Format=_SL250_&ID=AsinImage&MarketPlace=US`;
    if (currentUrl !== adSystemUrl) {
      resolvedImages.value[idx] = adSystemUrl;
      return;
    }
  }

  const variants = props.productData?.variants || [];
  const firstVar = variants[0];
  if (firstVar) {
    if (firstVar.imageUrl && resolvedImages.value[idx] !== firstVar.imageUrl) {
      resolvedImages.value[idx] = firstVar.imageUrl;
      return;
    }
    if (firstVar.asin && firstVar.asin !== '-') {
      const varAsinUrl = `https://images-na.ssl-images-amazon.com/images/P/${firstVar.asin}.01._SL250_.jpg`;
      if (resolvedImages.value[idx] !== varAsinUrl) {
        resolvedImages.value[idx] = varAsinUrl;
        return;
      }
      const varAdSystemUrl = `https://ws-na.amazon-adsystem.com/widgets/q?ASIN=${firstVar.asin}&Format=_SL250_&ID=AsinImage&MarketPlace=US`;
      if (resolvedImages.value[idx] !== varAdSystemUrl) {
        resolvedImages.value[idx] = varAdSystemUrl;
        return;
      }
    }
  }

  resolvedImages.value[idx] = PLACEHOLDER_IMAGE;
};

const resolveVariantImage = (variant: any) => {
  if (variant.imageUrl && !isPlaceholderImage(variant.imageUrl)) {
    return variant.imageUrl;
  }
  if (variant.asin && variant.asin !== '-') {
    return `https://images-na.ssl-images-amazon.com/images/P/${variant.asin}.01._SL75_.jpg`;
  }
  return null;
};

const handleVariantImgError = (event: Event, variant: any) => {
  const img = event.target as HTMLImageElement;
  const currentUrl = img.src;
  
  if (variant.asin && variant.asin !== '-') {
    const adSystemUrl = `https://ws-na.amazon-adsystem.com/widgets/q?ASIN=${variant.asin}&Format=_SL250_&ID=AsinImage&MarketPlace=US`;
    if (currentUrl !== adSystemUrl) {
      img.src = adSystemUrl;
      return;
    }
  }
  
  img.src = PLACEHOLDER_IMAGE;
};

const handleImageLoad = (event: Event, idx: number) => {
  const img = event.target as HTMLImageElement;
  if (img.naturalWidth === 1 && img.naturalHeight === 1) {
    handleImageError(idx);
  }
};

const handleVariantImgLoad = (event: Event, variant: any) => {
  const img = event.target as HTMLImageElement;
  if (img.naturalWidth === 1 && img.naturalHeight === 1) {
    handleVariantImgError(event, variant);
  }
};

const visible = computed({
  get: () => props.open,
  set: (val: boolean) => emit('update:open', val),
});

const activeTab = ref('info');
const previewVisible = ref(false);
const previewUrl = ref('');

// --- Attribute Filters Logic ---
const searchText = ref('');
const optionFilters = ref<{ option1?: string; option2?: string; option3?: string }>({
  option1: undefined,
  option2: undefined,
  option3: undefined
});

// Extract unique option values for filters
const uniqueOptions = computed(() => {
  const variants = props.productData?.variants || [];
  const opt1Set = new Set<string>();
  const opt2Set = new Set<string>();
  const opt3Set = new Set<string>();
  
  variants.forEach((v: any) => {
    if (v.option1 && v.option1 !== 'null') opt1Set.add(v.option1);
    if (v.option2 && v.option2 !== 'null') opt2Set.add(v.option2);
    if (v.option3 && v.option3 !== 'null') opt3Set.add(v.option3);
  });
  
  return {
    option1: Array.from(opt1Set).sort(),
    option2: Array.from(opt2Set).sort(),
    option3: Array.from(opt3Set).sort()
  };
});

// Total Inventory Calculation
const totalInventory = computed(() => {
  if (!props.productData?.variants?.length) return props.productData?.inventory || 0;
  return props.productData.variants.reduce((sum: number, v: any) => sum + (v.inventoryQuantity || 0), 0);
});

// Filtered Variants for Search and Attribute Filters
const filteredVariants = computed(() => {
  let variants = props.productData?.variants || [];
  
  // Apply attribute filters
  if (optionFilters.value.option1) {
    variants = variants.filter((v: any) => v.option1 === optionFilters.value.option1);
  }
  if (optionFilters.value.option2) {
    variants = variants.filter((v: any) => v.option2 === optionFilters.value.option2);
  }
  if (optionFilters.value.option3) {
    variants = variants.filter((v: any) => v.option3 === optionFilters.value.option3);
  }
  
  // Apply text search
  if (searchText.value) {
    const lowerSearch = searchText.value.toLowerCase();
    variants = variants.filter((v: any) => {
      const sku = (v.sku || '').toLowerCase();
      const id = (v.shopifyVariantId || '').toString();
      const title = (v.title || '').toLowerCase();
      const asin = (v.asin || '').toLowerCase();
      return sku.includes(lowerSearch) || id.includes(lowerSearch) || title.includes(lowerSearch) || asin.includes(lowerSearch);
    });
  }
  
  return variants;
});
// -----------------------------

// Logs Pagination
const currentPage = ref(1);
const pageSize = ref(10);

watch(() => props.productData, () => { 
  currentPage.value = 1; 
  searchText.value = ''; // Reset search on new product
  optionFilters.value = { option1: undefined, option2: undefined, option3: undefined }; // Reset filters
}, { deep: true });

const paginatedLogs = computed(() => {
  const logs = props.productData?.logs || [];
  const start = (currentPage.value - 1) * pageSize.value;
  return logs.slice(start, start + pageSize.value);
});

const handleCopy = (text: string) => {
  if (!text) return;
  navigator.clipboard.writeText(text).then(() => {
    message.success('已复制');
  });
};

const getLogClass = (status: string) => {
  if (status === '已上架') return 'sample'; // Green
  if (status === '下架') return 'settlement'; // Purple
  if (status === '缺货') return 'commission'; // Orange
  if (status === '草稿') return 'created'; // Blue
  if (status === '疑似删除') return 'status_change'; // Red
  return 'basic'; // Indigo
};

const previewImage = (url: string) => {
  if (!url) return;
  previewUrl.value = url;
  previewVisible.value = true;
};

const handleCancel = () => {
  visible.value = false;
};
</script>

<!-- Global Styles Override matches Influencer Modal EXACTLY -->
<style lang="scss">
.product-detail-modal-wrap {
  display: flex;
  align-items: center;
  justify-content: center;

  .ant-modal {
    width: 1200px !important;
    height: 750px !important;
    max-height: 90vh !important;
    top: 0 !important;
    padding-bottom: 0 !important;
    margin: 0 !important;
    
    .ant-modal-content {
      height: 100% !important;
      padding: 0 !important;
      border-radius: 24px;
      overflow: hidden !important;
      display: flex;
      flex-direction: column;
      @supports (backdrop-filter: blur(20px)) {
        background: rgba(255, 255, 255, 0.85);
        backdrop-filter: blur(20px);
      }
    }

    .ant-modal-header {
      padding: 0 !important;
      margin: 0 !important;
      border-bottom: none;
      background: transparent;
      flex-shrink: 0;
    }

    /* REMOVED .ant-modal-body global override to match Influencer logic */

    .ant-modal-close {
      display: none;
    }
  }
}
</style>

<style lang="scss" scoped>
/* CSS Reset for Modal Header to match Influencer Modal */
.ic-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  background: linear-gradient(135deg, rgba(59, 130, 246, 0.05) 0%, rgba(37, 99, 235, 0.08) 100%);
  border-bottom: 1px solid rgba(226, 232, 240, 0.8);
  flex-shrink: 0;
}

.ic-header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.ic-header-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  box-shadow: 0 8px 16px rgba(37, 99, 235, 0.2);
}

.ic-header-title {
  font-size: 17px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 2px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.order-no-tags {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  
  .ant-tag {
    margin: 0;
    font-weight: normal;
    font-size: 12px;
    padding: 2px 7px;
    background: white;
    border: 1px solid #e2e8f0;
    color: #64748b;
  }
}

.ic-header-subtitle {
  font-size: 12px;
  color: #64748b;
}

.close-btn {
  border-radius: 10px;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #94a3b8;
  transition: all 0.2s;
  
  &:hover { 
    background: rgba(0, 0, 0, 0.05); 
    color: #ef4444; 
  }
}

.root-tabs-container {
  height: 100%;
  background: transparent;
}

/* --- PREMIUM VISUAL POLISH --- */

.unified-height-tabs {
  height: 100% !important;
  
  :deep(.ant-tabs-nav) {
    width: 160px !important;
    background: #f8fafc; /* Lighter background */
    border-right: 1px solid #f1f5f9;
    padding: 16px 8px !important;
    height: 100% !important;

    .ant-tabs-nav-list { gap: 4px; }

    .ant-tabs-tab {
      margin: 0 !important;
      padding: 10px 16px !important;
      border-radius: 10px !important;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      border: 1px solid transparent;

      .ant-tabs-tab-btn {
        width: 100%;
        .tab-label-box {
          display: flex;
          align-items: center;
          gap: 8px;
          font-size: 13px;
          font-weight: 600;
          color: #64748b;
          transition: all 0.2s;
          .anticon { font-size: 15px; opacity: 0.7; }
        }
      }

      &:hover {
        background: rgba(255, 255, 255, 0.8);
        transform: translateX(2px); /* Subtle movement */
      }

      &.ant-tabs-tab-active {
        background: #fff !important;
        border: 1px solid #e2e8f0;
        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 2px 4px -1px rgba(0, 0, 0, 0.03); /* Soft shadow */
        
        .ant-tabs-tab-btn .tab-label-box {
          color: #3b82f6; 
          .anticon { opacity: 1; color: #3b82f6; }
        }
      }
    }
    
    .ant-tabs-ink-bar { display: none !important; }
  }

  :deep(.ant-tabs-content-holder) {
    border-left: none;
    background: #fff;
    height: 100% !important;
  }
  
  :deep(.ant-tabs-content) { height: 100% !important; }
  
  :deep(.ant-tabs-tabpane) {
    height: 100% !important;
    padding: 0 !important;
    display: flex;
    flex-direction: column;
  }
}

.unified-tab-content {
  padding: 24px 32px 24px 24px !important;
  height: 100% !important;
  display: flex !important;
  flex-direction: column;
  position: relative;
  overflow: hidden;
  box-sizing: border-box;

  &.locked-height {
    height: 650px !important; 
  }
}

.unified-content-wrapper {
  flex: 1;
  height: 100%; 
  min-height: 0;
  display: flex;
  flex-direction: column;
  
  &.scrollable-y {
    overflow-y: auto;
    padding-right: 12px;
    &::-webkit-scrollbar { width: 6px; }
    &::-webkit-scrollbar-track { background: #f8fafc; border-radius: 10px; }
    &::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 10px; border: 1px solid #f8fafc; }
    &::-webkit-scrollbar-thumb:hover { background: #94a3b8; }
  }
}

.html-content {
  :deep(img) {
    max-width: 100% !important;
    height: auto !important;
    border-radius: 8px;
    margin: 8px 0;
  }
  
  :deep(ul), :deep(ol) {
    padding-left: 20px;
    margin-bottom: 12px;
  }
  
  :deep(p) {
    margin-bottom: 12px;
  }
}

/* Premium Grid Descriptions */
.premium-descriptions {
  :deep(.ant-descriptions-view) {
    border-radius: 12px; /* Softer corners */
    overflow: hidden;
    border: 1px solid #e2e8f0;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.02); /* Very subtle depth */
  }
  
  :deep(.ant-descriptions-row) {
    border-bottom: 1px solid #f1f5f9;
    transition: background 0.2s;
    &:hover { background: #fdfdfd; } /* Micro-interaction */
    &:last-child { border-bottom: none; }
  }
  
  :deep(.ant-descriptions-item-label) {
    background: linear-gradient(to right, #f8fafc, #fff) !important; /* Gradient fade */
    color: #64748b;
    font-size: 13px;
    font-weight: 500;
    padding: 12px 18px !important;
    width: 140px; 
    border-right: 1px solid #f1f5f9;
  }
  
  :deep(.ant-descriptions-item-content) {
    padding: 12px 18px !important;
    color: #334155;
    font-size: 13px;
    font-weight: 500;
  }
}

/* Variants Table - Clean & Compact Style */
.variants-table-wrapper.clean-style {
  border: 1px solid #f1f5f9;
  border-radius: 12px;
  overflow: hidden;
  background: #fff;
  margin-top: 0;
}

.premium-detail-table.clean-table {
  width: 100%;
  border-collapse: collapse;

  th {
    background: #f8fafc;
    color: #475569;
    font-size: 13px;
    font-weight: 700;
    text-transform: none;
    letter-spacing: normal;
    padding: 12px 16px;
    text-align: left;
    border-bottom: 1px solid #f1f5f9;
    
    /* Simulate Sample Tab table header exactly */
    display: table-cell; 
  }

  td {
    padding: 8px 16px; /* Compact padding */
    border-bottom: 1px solid #f8fafc;
    vertical-align: middle;
    font-size: 13px;
    color: #1e293b;
    background: white;
    transition: all 0.2s;
  }

  tr:hover td { 
    background: #fdfdfd; 
  }
  
  tr:last-child td { border-bottom: none; }
}

.sku-text-only {
   font-family: 'JetBrains Mono', monospace;
   font-size: 13px;
   color: #1e293b;
   font-weight: 600;
   cursor: pointer;
   transition: color 0.2s;
   
   &:hover { color: #3b82f6; }
}

.variant-img-thumbnail.small {
  width: 42px;
  height: 42px;
  border-radius: 6px;
  border: 1px solid #f1f5f9;
  
  img { width: 100%; height: 100%; object-fit: cover; }
}

.variant-img-placeholder.small {
  width: 42px;
  height: 42px;
  border-radius: 6px;
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8fafc;
  color: #cbd5e1;
}

.price-mono {
  font-family: 'JetBrains Mono', monospace; 
  font-weight: 700; 
  color: #1e293b;
}

.inventory-pill {
   display: inline-block;
   padding: 1px 8px;
   border-radius: 6px;
   font-size: 11px;
   font-weight: 700;
   background: #f0fdf4;
   color: #166534;
   
   &.low { background: #fff7ed; color: #c2410c; }
   &.out { background: #fef2f2; color: #b91c1c; }
}

/* 3. Refined Image Gallery */
.image-item-wrapper {
  position: relative;
  border-radius: 8px; /* Matching corners */
  overflow: hidden;
  border: 1px solid #e2e8f0;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05); /* Subtle lift */
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: white;
  
  .product-img {
    width: 80px;
    height: 80px;
    object-fit: contain; /* Better fit */
    padding: 4px;
    display: block;
  }

  .img-tag {
    position: absolute;
    bottom: 0; left: 0; right: 0;
    background: rgba(0, 0, 0, 0.6);
    color: #fff;
    font-size: 10px;
    text-align: center;
    padding: 2px 0;
    backdrop-filter: blur(2px);
  }

  &:hover {
    border-color: #3b82f6;
    transform: translateY(-2px);
    box-shadow: 0 10px 15px -3px rgba(59, 130, 246, 0.15);
  }
}

/* 4. Modern Search Box */
.search-box {
  :deep(.ant-input-affix-wrapper) {
    border-radius: 9999px !important; /* Full Pill Shape */
    border: 1px solid #e2e8f0;
    background: #fcfcfc;
    box-shadow: none;
    transition: all 0.2s;
    padding-left: 12px;
    
    &:hover, &:focus-within {
      background: #fff;
      border-color: #3b82f6;
      box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1); /* Focus ring */
    }
    
    input { background: transparent; font-size: 13px; }
  }
}

/* 5. Polished Table */
.variants-table-wrapper.clean-style {
  border: 1px solid #f1f5f9;
  border-radius: 12px;
  background: #fff;
  margin-top: 0;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.02); /* Lifted look */
  flex: 1;
  min-height: 0;
  overflow-y: auto; /* Enable vertical scrolling */
  max-height: calc(100% - 50px); /* Leave space for header actions */
  
  &::-webkit-scrollbar { width: 5px; }
  &::-webkit-scrollbar-track { background: transparent; }
  &::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 10px; }
  &::-webkit-scrollbar-thumb:hover { background: #cbd5e1; }
}

.premium-detail-table.clean-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  
  thead {
    position: sticky;
    top: 0;
    z-index: 10;
    
    th {
      background: rgba(248, 250, 252, 0.9); /* Translucent */
      backdrop-filter: blur(8px);
      color: #475569;
      font-size: 12px;
      font-weight: 700;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      padding: 14px 16px;
      border-bottom: 1px solid #e2e8f0;
      text-align: left;
    }
  }

  td {
    padding: 10px 16px; /* slightly more breathing room */
    border-bottom: 1px solid #f8fafc;
    font-size: 13px;
    vertical-align: middle;
    transition: all 0.15s;
  }

  tbody tr {
    transition: background 0.15s;
    &:hover {
      background: #f8fafc;
      td { background: transparent; } /* Ensure hover passes through */
    }
    &:last-child td { border-bottom: none; }
  }
}

/* 6. Typography Refinements */
.ph-title { 
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
  letter-spacing: -0.01em; 
}

.sku-text-only {
   font-family: 'JetBrains Mono', 'SF Mono', monospace;
   font-size: 12px;
   color: #334155;
   background: #f1f5f9; /* Subtle pill bg */
   padding: 2px 6px;
   border-radius: 4px;
   display: inline-block;
   font-weight: 500;
   cursor: pointer;
   transition: all 0.2s;
   
   &:hover { 
     color: #2563eb; 
     background: #eff6ff;
   }
}

.admin-link-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  border-radius: 6px;
  color: #94a3b8;
  background: white;
  border: 1px solid #e2e8f0;
  transition: all 0.2s;
  
  &:hover {
    color: #3b82f6;
    border-color: #3b82f6;
    background: #eff6ff;
    transform: translateY(-1px);
    box-shadow: 0 2px 4px rgba(59, 130, 246, 0.15);
  }
}

/* 7. Product Description Card */
.description-section {
  background: #fbfcff;
  border-radius: 12px;
  border: 1px solid #f1f5f9;
  padding: 16px;
}

/* Helpers */
.variant-img-thumbnail.small {
  width: 42px; height: 42px;
  border-radius: 6px;
  border: 1px solid #f1f5f9;
  img { width: 100%; height: 100%; object-fit: cover; }
}

.variant-img-placeholder.small {
  width: 42px; height: 42px;
  border-radius: 6px;
  font-size: 16px;
  display: flex; align-items: center; justify-content: center;
  background: #f8fafc; color: #cbd5e1;
}

.price-mono {
  font-family: 'JetBrains Mono', monospace; 
  font-weight: 600; 
  color: #1e293b;
}

.inventory-pill {
   display: inline-block;
   padding: 1px 8px;
   border-radius: 6px;
   font-size: 11px;
   font-weight: 700;
   background: #f0fdf4;
   color: #166534;
   
   &.low { background: #fff7ed; color: #c2410c; }
   &.out { background: #fef2f2; color: #b91c1c; }
}

.image-group-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.empty-state-wrapper {
   height: 100%;
   display: flex; justify-content: center; align-items: center;
   color: #94a3b8; font-size: 13px;
}
</style>

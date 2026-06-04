<template>
  <a-modal
    v-model:open="modalVisible"
    :title="null"
    :width="modalWidth"
    :footer="null"
    centered
    :draggable="false"
    :closable="false"
    wrap-class-name="refined-upload-wrapper"
    @cancel="handleCancel"
    class="refined-upload-modal"
    destroyOnClose
  >
    <div class="modal-layout-container" :class="{ 'with-left': showOrderDetail }">
      <!-- 0. 订单详情侧边栏 (左侧) -->
      <transition name="slide-left">
        <div v-if="showOrderDetail" class="insight-sidebar is-left">
          <div class="sidebar-head">
            <div class="head-title">
              <shopping-outlined />
              <span>关联订单参考</span>
            </div>
            <div class="sidebar-close" @click="showOrderDetail = false">
              <close-outlined />
            </div>
          </div>
          <div class="sidebar-scroll-content">
            <template v-if="loadingOrder">
              <div class="sidebar-loading">
                <loading-outlined spin />
                <span>查询详情中...</span>
              </div>
            </template>
            <template v-else-if="orderDetail">
              <!-- 人员信息 -->
              <div class="order-section-luxe">
                <div class="section-tag">人员信息</div>
                <div class="kv-list">
                  <div class="kv-item">
                    <span class="k">联络人</span>
                    <span class="v"><contacts-outlined /> {{ (taskData as any).contactPersonName || (orderDetail as any)?.contactPersonName || '-' }}</span>
                  </div>
                  <div class="kv-item">
                    <span class="k">负责人</span>
                    <span class="v"><user-switch-outlined /> {{ (taskData as any).ownerName || (orderDetail as any)?.ownerName || '-' }}</span>
                  </div>
                </div>
              </div>

              <!-- 订单概览 -->
              <div class="order-section-luxe">
                <div class="section-tag">订单概览</div>
                <div class="kv-list">
                  <div class="kv-item"><span class="k">订单号</span><span class="v">{{ orderDetail.orderNo }}</span></div>
                  <div class="kv-item"><span class="k">状态</span><a-tag color="processing" size="small">{{ orderDetail.financialStatus }}</a-tag></div>
                  <div class="kv-item"><span class="k">总额</span><span class="v-price">${{ orderDetail.totalAmount }}</span></div>
                </div>
              </div>

              <!-- 订单轨迹 -->
              <div class="order-section-luxe">
                <div class="section-tag">订单轨迹</div>
                <div class="timeline-box-luxe">
                  <a-timeline>
                    <a-timeline-item color="green" v-if="orderDetail.createdAt">
                      <template #dot><check-circle-outlined /></template>
                      订单创建: {{ formatDate(orderDetail.createdAt) }}
                    </a-timeline-item>
                    <a-timeline-item :color="orderDetail.paidAt ? 'green' : 'gray'">
                      付款确认: {{ orderDetail.paidAt ? formatDate(orderDetail.paidAt) : '待支付' }}
                    </a-timeline-item>
                    <a-timeline-item :color="orderDetail.shippedAt ? 'blue' : 'gray'">
                      包裹发出: {{ orderDetail.shippedAt ? formatDate(orderDetail.shippedAt) : '待发货' }}
                      <div v-if="orderDetail.trackingNumber" class="timeline-sub-v">单号: {{ orderDetail.trackingNumber }}</div>
                    </a-timeline-item>
                    <a-timeline-item :color="orderDetail.deliveredAt ? 'green' : 'gray'">
                      妥投成功: {{ orderDetail.deliveredAt ? formatDate(orderDetail.deliveredAt) : '运送中' }}
                    </a-timeline-item>
                  </a-timeline>
                </div>
              </div>

              <div class="order-section-luxe">
                <div class="section-tag">收货信息</div>
                <div class="kv-list">
                  <div class="kv-item"><span class="k">收件人</span><span class="v">{{ orderDetail.shippingName }}</span></div>
                  <div class="kv-item"><span class="k">目的地</span><span class="v">{{ orderDetail.shippingCountry || 'US' }}</span></div>
                  <div class="kv-item-stack">
                    <span class="k">详细地址</span>
                    <div class="v-address">{{ orderDetail.shippingAddress }}</div>
                  </div>
                </div>
              </div>

              <div class="order-section-luxe">
                <div class="section-tag">商品明细 ({{ orderDetail.products?.length || 0 }})</div>
                <div class="order-p-list">
                  <div v-for="(p, idx) in orderDetail.products" :key="idx" class="order-p-item">
                    <div class="p-img-wrapper">
                      <a-image :src="optimizeShopifyImage(p.image, '100x100')" class="p-img-mini" loading="lazy" />
                      <div class="p-sku-tag">
                        <span class="sku-main">{{ parseSkuToParts(p).main }}</span>
                        <span v-for="(spec, sIdx) in parseSkuToParts(p).specs" :key="sIdx"
                          class="sku-spec" :class="`spec-${Number(sIdx) % 3}`">
                          -{{ spec }}
                        </span>
                      </div>
                    </div>
                    <div class="p-info-mini">
                      <div class="p-name-mini">{{ p.name }}</div>
                      <div class="p-qty-mini">数量: {{ p.quantity }} | SPU: {{ (p as any).spu || (p as any).shopifyProductId || '-' }}</div>
                    </div>
                  </div>
                </div>
              </div>
            </template>
            <template v-else>
              <div class="sidebar-empty">
                <warning-outlined />
                <span>未找到订单，请核对订单号</span>
              </div>
            </template>
          </div>
        </div>
      </transition>

      <!-- 1. 主页面内容容器 -->
      <div class="refined-container">
        <!-- Header Area -->
        <div class="refined-header">
          <div class="header-left">
            <div class="brand-badge-luxe">
              <file-done-outlined />
            </div>
            <div class="header-text">
              <div class="header-main-info">
                <span class="meta-tag-blue">{{ taskData.taskId }}</span>
                <h1 class="influencer-name-refined">
                  {{ taskData.influencer }}
                </h1>
              </div>
              <div class="header-meta-info">
                <!-- Editable Order Section -->
                <div class="order-edit-inline" :class="{ 'is-editing': isEditingOrder }">
                  <template v-if="!isEditingOrder">
                    <span class="meta-item-luxe is-complex">
                      <shopping-outlined /> 
                      订单号: 
                      <span class="val link-trigger" @click.stop="toggleOrderSidebar">
                        <template v-if="taskData.orderNo">{{ taskData.orderNo }}</template>
                        <template v-else>待锁定订单</template>
                      </span>
                      <edit-outlined v-if="!readonly" class="edit-icon-trigger" @click.stop="startEditOrder" />
                    </span>
                  </template>
                  <template v-else>
                    <div class="inline-input-box">
                      <shopping-outlined class="prefix" />
                      <a-auto-complete
                        ref="orderInputRef" 
                        v-model:value="tempOrderNo" 
                        :options="orderSuggestions"
                        class="minimal-input" 
                        placeholder="输入新单号"
                        @blur="confirmEditOrder"
                        @select="confirmEditOrder"
                        @keyup.enter="confirmEditOrder"
                      />
                      <check-outlined class="confirm-btn" @click.stop="confirmEditOrder" />
                    </div>
                  </template>
                </div>

                <span v-if="taskData.productSku" class="meta-item-luxe">
                  <barcode-outlined /> SKU: 
                  <span class="val header-sku-highlight">
                    <span class="sku-main">{{ headerSkuParts.main }}</span>
                    <span v-for="(spec, sIdx) in headerSkuParts.specs" :key="sIdx"
                          class="sku-spec" :class="`spec-${Number(sIdx) % 3}`">
                      -{{ spec }}
                    </span>
                    <a-tag v-if="headerSkuParts.hasMore" color="blue" class="sku-tag-more" style="margin-left: 6px; border-radius: 4px; border: none; font-size: 11px; padding: 0 4px; height: 18px; line-height: 18px;">
                      +{{ String(taskData.productSku).split(/[,，]\s*/).filter(s => s.trim()).length - 1 }}
                    </a-tag>
                  </span>
                </span>
                <span class="meta-divider-soft">|</span>
                <span class="header-sub-text-luxe">红人内容管理工作台</span>
              </div>
            </div>
          </div>

          <div class="header-right">
            <div class="batch-stat-luxe">
              <span class="stat-count">{{ totalFiles }}</span>
              <span class="stat-label">待传资源</span>
            </div>
            <div class="close-icon-refined" @click="handleCancel">
              <close-outlined />
            </div>
          </div>
        </div>

        <!-- Body Area -->
        <div class="refined-body">
          <div class="action-bar-luxe">
            <a-button v-if="!readonly" type="primary" size="middle" @click="addContentGroup" class="add-content-btn">
              <template #icon><plus-outlined /></template>
              增加内容
            </a-button>
            <div class="bar-hint-luxe">
              <info-circle-outlined />
              <template v-if="readonly">查看模式 — 已完成内容仅供浏览</template>
              <template v-else>系统支持跨商品、跨平台批量同步。间距锁定 16px。</template>
            </div>
          </div>

          <div class="config-list-scroller">
            <transition-group name="card-float">
              <div v-for="(record, index) in uploadForm.contentGroups" :key="index" class="content-card-luxe">
                <div class="card-head-luxe">
                  <div class="content-id-tag">
                    <span class="num">#{{ String(index + 1).padStart(2, '0') }}</span>
                    <span class="label">项目项</span>
                  </div>
                  
                  <div style="margin-left: 16px; width: 180px;">
                    <a-date-picker 
                      v-model:value="record.publishDate" 
                      value-format="YYYY-MM-DD"
                      placeholder="发帖日期（必填）" 
                      class="luxe-datepicker full-width"
                      size="small"
                      :disabled="readonly"
                      :status="!record.publishDate ? 'warning' : undefined"
                    />
                  </div>
                  <div class="flex-spacer"></div>
                  <div class="head-options">
                    <div class="auth-toggle">
                      <span class="text">商用授权</span>
                      <a-switch v-model:checked="record.isCommercial" size="small" :disabled="readonly" />
                    </div>
                    <template v-if="!readonly">
                      <a-divider type="vertical" class="divider-soft" />
                      <a-button 
                        type="text" 
                        danger 
                        size="small" 
                        @click="removeContentGroup(index)"
                        :disabled="uploadForm.contentGroups.length === 1"
                        class="btn-remove-luxe"
                      >
                        <template #icon><delete-outlined /></template>
                        移除
                      </a-button>
                    </template>
                  </div>
                </div>

                <div class="card-grid-luxe">
                  <div class="control-panel-refined">
                    <div class="luxe-field">
                      <div class="l-label">关联商品</div>
                      <div class="l-input">
                        <div class="p-selector-luxe" @click="openProductSelector(index)">
                          <div v-if="record.skus && record.skus.length > 0" class="p-summary-box">
                            <a-tooltip 
                              placement="topLeft" 
                              arrow-point-at-center 
                              overlay-class-name="luxe-p-tooltip"
                              :overlay-style="{ width: '560px', minWidth: '560px', maxWidth: '560px' }"
                            >
                              <template #title>
                                <div class="tip-p-detail">
                                  <div class="tip-header">
                                    <shopping-outlined />
                                    <span>关联商品清单 ({{ record.skus.length }})</span>
                                  </div>
                                  <div v-for="(skuKey, sIdx) in record.skus" :key="skuKey" class="tip-p-row">
                                    <span class="p-idx">{{ sIdx + 1 }}</span>
                                    <div class="tip-p-content">
                                      <span class="tip-p-sku">{{ findProductBySkuKey(skuKey)?.sku || skuKey.split('_')[0] }}</span>
                                      <span class="tip-p-sep" v-if="extractSpecsFromKey(skuKey).length > 0">·</span>
                                      <span class="tip-p-spec" v-if="extractSpecsFromKey(skuKey).length > 0">
                                        {{ extractSpecsFromKey(skuKey).join(' / ') }}
                                      </span>
                                      <span class="tip-p-spu" v-if="findProductBySkuKey(skuKey)?.spu || findProductBySkuKey(skuKey)?.shopifyProductId">
                                        (SPU: {{ findProductBySkuKey(skuKey)?.spu || findProductBySkuKey(skuKey)?.shopifyProductId }})
                                      </span>
                                    </div>
                                  </div>
                                </div>
                              </template>
                              <div class="summary-content">
                                <shopping-outlined class="icon-main" />
                                <span class="summary-txt">已关联 <b>{{ record.skus.length }}</b> 件商品</span>
                              </div>
                            </a-tooltip>
                            <close-circle-filled class="p-clear-all" @click.stop="removeSkuFromGroup(record, '')" title="清空全部" />
                          </div>
                          <div v-else class="p-empty-luxe">
                            <barcode-outlined />
                            <span>点击选择商品...</span>
                          </div>
                        </div>
                      </div>
                    </div>

                    <div class="luxe-field">
                      <div class="l-label">发布终端</div>
                      <div class="l-input">
                        <a-select 
                          v-model:value="record.platform" 
                          placeholder="选择发布平台" 
                          class="luxe-select full-width" 
                          :disabled="readonly"
                          @change="(v: string) => onPlatformChange(index, v)"
                        >
                          <a-select-option v-for="p in platforms" :key="p" :value="p">{{ p }}</a-select-option>
                        </a-select>
                      </div>
                    </div>

                    <div class="luxe-field">
                      <div class="l-label">内容类型</div>
                      <div class="l-input">
                        <a-select 
                          v-model:value="record.contentType" 
                          placeholder="选择内容类型" 
                          class="luxe-select full-width" 
                          :disabled="readonly"
                        >
                          <template v-if="record.platform === 'TikTok'">
                            <a-select-option value="Tiktok video">Tiktok video</a-select-option>
                          </template>
                          <template v-else-if="record.platform === 'Instagram'">
                            <a-select-option value="IG-Reel">IG-Reel</a-select-option>
                            <a-select-option value="IG-feed post">IG-feed post</a-select-option>
                          </template>
                          <template v-else-if="record.platform === 'YouTube'">
                            <a-select-option value="Youtube short">Youtube short</a-select-option>
                            <a-select-option value="Youtube video">Youtube video</a-select-option>
                          </template>
                          <template v-else>
                            <a-select-option value="其它">其它</a-select-option>
                          </template>
                        </a-select>
                      </div>
                    </div>

                    <div class="luxe-field">
                      <div class="l-label">作品链接</div>
                      <div class="l-input">
                        <a-input v-model:value="record.link" placeholder="作品原文 URL" class="luxe-input full-width" :disabled="readonly" />
                      </div>
                    </div>
                    <div class="luxe-field">
                      <div class="l-label">素材标签</div>
                      <div class="l-input">
                        <a-select 
                          v-model:value="record.tags" 
                          mode="multiple" 
                          placeholder="选择标签" 
                          class="luxe-select full-width"
                          :options="tagOptions"
                          :disabled="readonly"
                        ></a-select>
                      </div>
                    </div>
                    <div class="luxe-field">
                      <div class="l-label">备注</div>
                      <div class="l-input">
                        <a-textarea v-model:value="record.remark" placeholder="请输入备注" class="luxe-input full-width" :auto-size="{ minRows: 2, maxRows: 3 }" :disabled="readonly" />
                      </div>
                    </div>
                  </div>

                  <div class="asset-panel-luxe">
                      <div class="asset-layout-luxe horizontal-mode">
                        <div class="asset-box-refined image-accent">
                          <div class="box-head">
                            <span class="title"><picture-outlined /> 图片素材</span>
                            <span class="count">{{ record.images.length }}/12</span>
                          </div>
                          <div class="box-body" v-lazy-images>
                            <a-upload
                              v-model:fileList="record.images"
                              list-type="picture-card"
                              :maxCount="12"
                              :multiple="true"
                              accept="image/*"
                              :before-upload="(file: any) => handleBeforeUpload(file, record, 'IMAGE')"
                              :remove="(file: any) => handleRemoveFile(file, record)"
                              @preview="handlePreview"
                              class="luxe-uploader"
                            >
                              <template #itemRender="{ originNode, actions }">
                                <div class="custom-upload-item">
                                  <component :is="originNode" />
                                  <span v-if="!readonly" class="delete-x-btn" @click.stop="actions.remove()">✕</span>
                                </div>
                              </template>
                              <div v-if="record.images.length < 12 && !readonly" class="luxe-trigger">
                                <plus-outlined />
                                <div class="txt">添加</div>
                              </div>
                            </a-upload>
                          </div>
                        </div>

                        <div class="asset-box-refined video-accent">
                          <div class="box-head">
                            <span class="title"><video-camera-outlined /> 视频素材</span>
                            <span class="count">{{ record.videos.length }}/6</span>
                          </div>
                          <div class="box-body" v-lazy-images>
                            <a-upload
                              v-model:fileList="record.videos"
                              list-type="picture-card"
                              :maxCount="6"
                              :multiple="true"
                              accept="video/*"
                              :before-upload="(file: any) => handleBeforeUpload(file, record, 'VIDEO')"
                              :remove="(file: any) => handleRemoveFile(file, record)"
                              @preview="handlePreview"
                              @change="(info: any) => handleVideoChange(info, record)"
                              class="luxe-uploader is-video"
                            >
                              <template #itemRender="{ originNode, actions }">
                                <div class="custom-upload-item">
                                  <component :is="originNode" />
                                  <span v-if="!readonly" class="delete-x-btn" @click.stop="actions.remove()">✕</span>
                                </div>
                              </template>
                              <div v-if="record.videos.length < 6 && !readonly" class="luxe-trigger">
                                <play-circle-outlined />
                                <div class="txt">添加</div>
                              </div>
                            </a-upload>
                          </div>
                        </div>
                      </div>
                  </div>
                </div>
              </div>
            </transition-group>
          </div>
        </div>

        <!-- Footer Area -->
        <div class="refined-footer-luxe">
          <div class="f-left-luxe">
            <a-button @click="handleCancel" class="btn-cancel-refined">{{ readonly ? '关闭' : '取消并关闭' }}</a-button>
          </div>
          <div class="f-right-luxe" v-if="!readonly">
            <div class="f-sync-status" v-if="hasUnsavedGroups">
              <loading-outlined v-if="savingDraft" />
              <cloud-upload-outlined v-else />
              <span>更改已暂存</span>
            </div>
            <a-space size="middle">
              <a-button @click="handleSaveDraft" :loading="savingDraft" class="btn-draft-refined">
                保存草稿
              </a-button>
              <a-button type="primary" @click="handleCompleteUpload" :loading="saving" class="btn-confirm-refined">
                确认并完成发布
                <template #icon><arrow-right-outlined /></template>
              </a-button>
            </a-space>
          </div>
        </div>
      </div>

    </div>

    <!-- Modals -->
    <a-modal :open="previewVisible" :title="null" :footer="null" @cancel="previewVisible = false" centered width="1000px">
      <img alt="preview" style="width: 100%; border-radius: 12px;" :src="previewImage" />
    </a-modal>

    <ProductSelectorModal
      v-model:open="productSelectorVisible"
      :store-id="currentStoreId"
      :multiple="true"
      :initial-selected-keys="(currentInitialSelectedKeys as any)"
      :initial-selected-skus="currentInitialSelectedSkus && currentInitialSelectedSkus.length > 0 ? currentInitialSelectedSkus : currentOrderSkus"
      :recommended-skus="currentOrderSkus"
      :order-products="orderDetail?.products || []"
      @select="handleProductSelect"
    />
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, watch, computed, onMounted, nextTick } from 'vue';
import { 
  PlusOutlined, CameraOutlined, VideoCameraOutlined, DeleteOutlined, CloseOutlined,
  ShoppingOutlined, FileDoneOutlined, CloseCircleFilled, PlayCircleOutlined,
  ArrowRightOutlined, CloudUploadOutlined, LoadingOutlined, BarcodeOutlined,
  InfoCircleOutlined, WarningOutlined, EditOutlined, CheckOutlined, UserOutlined,
  FileOutlined, CheckCircleOutlined, ContactsOutlined, UserSwitchOutlined
} from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import dayjs from 'dayjs';
import contentService from '@/services/contentService';
const getShopifyStores = async (...args: any[]): Promise<any> => [];
import { getSampleOrders, getSampleOrdersByInfluencer, getOrderDetails, type OrderDetailResponse } from '@/services/influencerOrderService';
import { influencerService } from '@/services/influencerService';
import { tagService } from '@/services/tagService';
import ProductSelectorModal from '@/components/order/ProductSelectorModal.vue';

const parseSkuToParts = (p: any) => {
  if (!p || !p.sku) return { main: '', specs: [] };
  
  // 使用短横线作为基础分隔，但要注意 variantTitle
  const sku = String(p.sku);
  const mainSku = (sku.includes(' / ') ? sku.split(' / ')[0] : (sku.includes('-') && !p.variant_title && !p.variantTitle ? sku.split('-')[0] : sku)) || '';
  
  let specs: string[] = [];
  const vTitle = p.variantTitle || p.variant_title || p.variant || '';
  if (vTitle && vTitle !== 'Default Title' && vTitle !== '-') {
    specs = String(vTitle).split('/').map((s: string) => s.trim().replace(/\s+/g, ''));
  } else if (sku.includes(' / ')) {
    specs = sku.split(' / ').slice(1).map((s: string) => s.trim().replace(/\s+/g, ''));
  } else if (sku.includes('-') && !p.variant_title) {
    const parts = sku.split('-');
    if (parts.length > 1) {
      specs = parts.slice(1).map((s: string) => s.trim().replace(/\s+/g, ''));
    }
  }
  
  return { main: mainSku.trim(), specs };
};

const generateFingerprint = (sku: string, variant: string, spu?: any, vid?: any) => {
  if (vid) return `vid:${vid}`;
  const s = String(sku || '').toLowerCase().replace(/[^a-z0-9]/g, '');
  const v = String(variant || '').toLowerCase().replace(/[^a-z0-9]/g, '');
  // 回归稳健策略：仅使用 SKU 和规格匹配，确保订单商品能够被识别并选中
  return `${s}|${v}`;
};

const formatSkuFull = (p: any) => {
  const { main, specs } = parseSkuToParts(p);
  return specs.length > 0 ? `${main}-${specs.join('-')}` : main;
};

// 优化：调整 Shopify 图片大小以提高性能
const optimizeShopifyImage = (url?: string, size: string = '200') => {
  if (!url || !url.includes('cdn.shopify.com')) return url;
  try {
    const u = new URL(url as string);
    u.searchParams.delete('crop');
    // 如果 URL 已经包含了强尺寸前缀，我们不直接修改 path，仅追加 query 参数让 CDN 自己处理
    const width = size ? (size.split('x')[0] || '200') : '200';
    u.searchParams.set('width', width);
    return u.toString();
  } catch (e) {
    return url;
  }
};

const getUniqueSkuKey = (p: any) => {
  if (!p) return '';
  // 核心唯一键，包含 VID (Shopify Variant ID) 或本地数据库 ID，用于精确区分同 SKU 的不同 SPU 商品
  const skuStr = p.sku || '';
  const vid = p.shopifyVariantId || '';
  const dbId = p.id || '';
  return `${skuStr}_${vid}_${dbId}`;
};

const headerSkuParts = computed(() => {
  if (!taskData.productSku) return { main: '', specs: [], hasMore: false };
  
  // 处理多个SKU的情况（以逗号或空格分隔）
  const skuList = String(taskData.productSku).split(/[,，]\s*/).filter(s => s.trim());
  const firstSku = skuList[0] || '';
  const hasMore = skuList.length > 1;

  // 尝试从 orderDetail 中查找实际商品，以获取更完整的规格信息
  if (orderDetail.value?.products) {
    const p = orderDetail.value.products.find((p: any) => p.sku === firstSku);
    if (p) return { ...parseSkuToParts(p), hasMore };
  }
  
  // 降级方案：直接从 taskData 中解析字符串（通常是 SKU-规格1-规格2）
  const main = firstSku.includes('-') ? firstSku.split('-')[0] : firstSku;
  const specs = firstSku.includes('-') ? firstSku.split('-').slice(1) : [];
  return { main, specs, hasMore };
});

const fullSkuFromOrder = computed(() => {
  const { main, specs } = headerSkuParts.value;
  return specs.length > 0 ? `${main}-${specs.join('-')}` : main;
});

interface SelectedProduct {
  id: number; title: string; sku: string; image?: string; variant?: string; price?: number;
}

interface ContentGroup {
  skus: string[]; // 支持多个唯一键
  platform: string | undefined; images: any[]; videos: any[];
  link: string; isCommercial: boolean; selectedProduct: SelectedProduct | null; uploadedPids: number[]; 
  shellPids?: number[];
  tags: string[];
  contentType?: string;
  publishDate?: string;
  remark?: string;
  groupIndex?: number;
  initialStatus?: string; // 加入状态追踪
}

const props = defineProps<{
  open: boolean;
  taskData: { 
    taskId: string; 
    influencer: string; 
    influencerId?: number; 
    key?: number; 
    orderNo?: string; 
    productSku?: string; 
    owner?: string;
    ownerName?: string;
    contactPersonName?: string;
  };
  platforms?: string[];
  readonly?: boolean;
}>();

const readonly = computed(() => props.readonly ?? false);

const emit = defineEmits<{ 
  (e: 'update:open', v: boolean): void; 
  (e: 'ok', d: ContentGroup[]): void; 
  (e: 'success'): void; 
  (e: 'change-order-no', val: string): void;
}>();

const modalVisible = ref(false);
const saving = ref(false);
const savingDraft = ref(false);
const loadingExisting = ref(false);
const platforms = props.platforms || ['TikTok', 'Instagram', 'YouTube', 'Facebook'];

const taskData = reactive({ 
  taskId: '', influencer: '', influencerId: 0, contentId: 0, orderNo: '', productSku: '', owner: '' 
});
const uploadForm = reactive({ contentGroups: [] as ContentGroup[] });

const currentStoreId = ref<number | undefined>(undefined);
const tagOptions = ref<{ value: string; label: string }[]>([]);

const loadTags = async () => {
  try {
    const res = await tagService.getTagsByCategory('CONTENT');
    tagOptions.value = (res || []).map((t: any) => ({ value: t.id, label: t.name }));
  } catch (e) { console.error(e); }
};

const currentEditingRowIndex = ref<number>(-1);

// 订单编辑
const isEditingOrder = ref(false);
const tempOrderNo = ref('');
const orderInputRef = ref<HTMLInputElement | null>(null);

const startEditOrder = async () => {
  tempOrderNo.value = taskData.orderNo;
  isEditingOrder.value = true;
  
  nextTick(() => {
    orderInputRef.value?.focus();
  });
};

const orderSuggestions = computed(() => {
  const allOrders = influencerOrders.value || [];
  return allOrders.map((o: any) => {
    const originalNo = o.orderNo || '';
    const formattedVal = originalNo.startsWith('#') ? originalNo : `#${originalNo}`;
    return {
      value: formattedVal,
      label: `单号: ${formattedVal} (${o.logisticsStatus || '处理中'})`
    };
  });
});

const confirmEditOrder = async () => {
  let newOrderNo = tempOrderNo.value.trim();
  // 格式化：如果用户输入时不带 #，则可以根据需要进行格式化。
  if (newOrderNo && !newOrderNo.startsWith('#')) {
    newOrderNo = `#${newOrderNo}`;
  }
  
  if (newOrderNo !== taskData.orderNo) {
    taskData.orderNo = newOrderNo;
    // 如果已有内容记录，则持久化到服务器
    if (taskData.contentId) {
      try {
        await contentService.updateContent(taskData.contentId, { orderNo: taskData.orderNo });
        message.success('已联动更新订单号');
        emit('change-order-no', taskData.orderNo);
      } catch (e) {
        message.error('保存订单号失败');
      }
    }
    if (showOrderDetail.value) await fetchOrderInfo();
  }
  isEditingOrder.value = false;
};

// 洞察侧边栏 (三栏布局)
const showOrderDetail = ref(false);
const loadingOrder = ref(false);
const orderDetail = ref<OrderDetailResponse | null>(null);
const influencerOrders = ref<any[]>([]);

const modalWidth = computed(() => {
  let w = 1140;
  if (showOrderDetail.value) w += 380;
  return `${w}px`;
});

const toggleOrderSidebar = async () => {
  showOrderDetail.value = !showOrderDetail.value;
  if (showOrderDetail.value && taskData.orderNo) await fetchOrderInfo();
};

const fetchOrderInfo = async () => {
  if (!taskData.orderNo) { orderDetail.value = null; return; }
  loadingOrder.value = true;
  try {
    const rawNo = taskData.orderNo.trim();
    // 强制把 # 去掉，统一用纯数字或其他符号去搜索
    const cleanNo = rawNo.replace(/^#+/, '').trim();
    const searchNo = `#${cleanNo}`;
    
    // 首先尝试使用纯字符串
    const orderList = await getSampleOrders(0, 5, { orderNo: cleanNo });
    let matchedOrder = orderList.content.find(o => {
      const dbNo = o.orderNo || '';
      const dbShopifyNo = String(o.shopifyOrderNumber || '');
      return dbNo === cleanNo || dbNo === searchNo || dbShopifyNo === cleanNo || dbShopifyNo === searchNo;
    });

    // 如果未找到，尝试加上 # 前缀显式搜索
    if (!matchedOrder && cleanNo.length > 0) {
      const orderListWithHash = await getSampleOrders(0, 5, { orderNo: searchNo });
      matchedOrder = orderListWithHash.content.find(o => {
        const dbNo = o.orderNo || '';
        const dbShopifyNo = String(o.shopifyOrderNumber || '');
        return dbNo === cleanNo || dbNo === searchNo || dbShopifyNo === cleanNo || dbShopifyNo === searchNo;
      });
    }

    if (matchedOrder?.id) {
      const detail = await getOrderDetails(matchedOrder.id, 'sample');
      if (detail && detail.products) {
        // 关键：为原生订单商品打上“真订单”标记
        detail.products = detail.products.map((p: any) => ({ ...p, _isOrderLine: true }));
      }
      orderDetail.value = detail;
    } else {
      orderDetail.value = null;
    }
  } catch (e) {
    orderDetail.value = null;
  } finally {
    loadingOrder.value = false;
  }
};

const formatDate = (date?: string) => date ? dayjs(date).format('YYYY-MM-DD') : '-';



const totalFiles = computed(() => {
  return uploadForm.contentGroups.reduce((sum, g) => sum + g.images.length + g.videos.length, 0);
});

const hasUnsavedGroups = computed(() => {
  return uploadForm.contentGroups.some(g => g.uploadedPids.length > 0);
});

const previewVisible = ref(false);
const previewImage = ref('');

const getBase64 = (file: File) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => resolve(reader.result);
    reader.onerror = error => reject(error);
  });
};

interface VideoMetadata {
  thumbnailDataUrl: string;
  thumbnailBlob: Blob | null;
  width: number;
  height: number;
  duration: number;
}

const extractVideoMetadata = (file: File): Promise<VideoMetadata> => {
  return new Promise((resolve, reject) => {
    const video = document.createElement('video');
    video.preload = 'metadata'; video.muted = true; video.playsInline = true;
    const url = URL.createObjectURL(file);
    video.src = url;
    
    // Add timeout to prevent hanging forever
    const timeoutId = setTimeout(() => {
        URL.revokeObjectURL(url);
        reject(new Error('Video metadata extraction timeout'));
    }, 5000);
    
    video.onloadedmetadata = () => {
      video.currentTime = Math.min(0.1, video.duration || 0);
    };
    
    video.onseeked = () => {
      clearTimeout(timeoutId);
      try {
          const canvas = document.createElement('canvas');
          canvas.width = video.videoWidth; canvas.height = video.videoHeight;
          const ctx = canvas.getContext('2d');
          if (ctx) {
            ctx.drawImage(video, 0, 0, canvas.width, canvas.height);
            const thumbnailDataUrl = canvas.toDataURL('image/jpeg', 0.8);
            canvas.toBlob((blob) => {
              URL.revokeObjectURL(url); 
              resolve({
                thumbnailDataUrl,
                thumbnailBlob: blob,
                width: video.videoWidth,
                height: video.videoHeight,
                duration: video.duration
              });
            }, 'image/jpeg', 0.8);
          } else { 
            URL.revokeObjectURL(url); 
            reject(new Error('Canvas ctx error')); 
          }
      } catch (e) {
          clearTimeout(timeoutId);
          URL.revokeObjectURL(url);
          reject(e);
      }
    };
    video.onerror = () => { 
      clearTimeout(timeoutId);
      URL.revokeObjectURL(url); 
      resolve({ thumbnailDataUrl: '', thumbnailBlob: null, width: 0, height: 0, duration: 0 });
    };
  });
};

const handleBeforeUpload = (file: any, record: ContentGroup, type: string) => {
  if (type === 'IMAGE') {
    const url = URL.createObjectURL(file);
    const img = new Image();
    img.onload = () => {
      const canvas = document.createElement('canvas');
      const MAX_PREVIEW = 400;
      let w = img.width, h = img.height;
      if (w > h) { h = Math.round(h * MAX_PREVIEW / w); w = MAX_PREVIEW; }
      else { w = Math.round(w * MAX_PREVIEW / h); h = MAX_PREVIEW; }
      canvas.width = w; canvas.height = h;
      const ctx = canvas.getContext('2d');
      if (ctx) {
        ctx.drawImage(img, 0, 0, w, h);
        const thumbDataUrl = canvas.toDataURL('image/jpeg', 0.8);
        canvas.toBlob((blob) => {
          const target = record.images.find((f: any) => f.uid === file.uid || f.originFileObj === file);
          if (target) { 
            target.thumbUrl = thumbDataUrl; 
            target.thumbnailBlob = blob;
            target.videoWidth = img.width;
            target.videoHeight = img.height;
          }
        }, 'image/jpeg', 0.8);
      }
      URL.revokeObjectURL(url);
    };
    img.src = url;
  }
  return false; // 保持手动上传模式
};

const handleVideoChange = async (info: any, record: ContentGroup) => {
  const { fileList } = info;
  record.videos = fileList;
  
  // 异步处理以防止 UI 冻结
  const processThumbnails = async () => {
    for (const file of fileList) {
      if (file.originFileObj && !file.thumbUrl && !file.thumbnail) {
        try {
          await new Promise(resolve => setTimeout(resolve, 0));
          const meta = await extractVideoMetadata(file.originFileObj);
          if (meta.thumbnailDataUrl) {
            file.thumbUrl = meta.thumbnailDataUrl;
            file.thumbnail = meta.thumbnailDataUrl;
          } else {
            // Fallback for when metadata load yields no image
            file.thumbUrl = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI0MiIgaGVpZ2h0PSI0MiIgdmlld0JveD0iMCAwIDI0IDI0IiBmaWxsPSJub25lIiBzdHJva2U9IiNhMGEwYTAiIHN0cm9rZS13aWR0aD0iMS41IiBzdHJva2UtbGluZWNhcD0icm91bmQiIHN0cm9rZS1saW5lam9pbj0icm91bmQiPjxwYXRoIGQ9Ik0yMyA3bC03IDUgNyA1Vjd6Ij48L3BhdGg+PHJlY3QgeD0iMSIgeT0iNSIgd2lkdGg9IjE1IiBoZWlnaHQ9IjE0IiByeD0iMiIgcnk9IjIiPjwvcmVjdD48L3N2Zz4=';
          }
          file.thumbnailBlob = meta.thumbnailBlob;
          file.videoWidth = meta.width;
          file.videoHeight = meta.height;
          file.duration = Math.round(meta.duration || 0);
        } catch (e) {
          console.warn('Thumbnail failed:', e);
          // Standard video placeholder icon if extraction fails completely
          file.thumbUrl = 'data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHdpZHRoPSI0MiIgaGVpZ2h0PSI0MiIgdmlld0JveD0iMCAwIDI0IDI0IiBmaWxsPSJub25lIiBzdHJva2U9IiNhMGEwYTAiIHN0cm9rZS13aWR0aD0iMS41IiBzdHJva2UtbGluZWNhcD0icm91bmQiIHN0cm9rZS1saW5lam9pbj0icm91bmQiPjxwYXRoIGQ9Ik0yMyA3bC03IDUgNyA1Vjd6Ij48L3BhdGg+PHJlY3QgeD0iMSIgeT0iNSIgd2lkdGg9IjE1IiBoZWlnaHQ9IjE0IiByeD0iMiIgcnk9IjIiPjwvcmVjdD48L3N2Zz4=';
        }
      }
    }
  };
  
  processThumbnails();
};

const handlePreview = async (file: any) => {
  if (!file.url && !file.preview) file.preview = (await getBase64(file.originFileObj)) as string;
  previewImage.value = file.url || file.preview;
  previewVisible.value = true;
};

const loadDefaultStore = async () => {
  try {
    const result = await getShopifyStores();
    const firstStore = result.content?.[0];
    if (firstStore?.id) currentStoreId.value = firstStore.id;
  } catch (e) {}
};

const handleProductChange = (index: number) => {
  const row = uploadForm.contentGroups[index];
  if (!row || !orderDetail.value?.products) return;
  
  // 不再只绑定一个 selectedProduct，但如果需要预览，可以保留第一个
  if (row.skus.length > 0) {
    const firstFullKey = row.skus[0];
    const product = orderDetail.value.products.find(p => getUniqueSkuKey(p) === firstFullKey);
    if (product) {
      row.selectedProduct = { 
        id: product.id, 
        title: product.name || '', 
        sku: product.sku, 
        image: product.image,
        variant: product.variantTitle && product.variantTitle !== 'Default Title' ? product.variantTitle : (product.sku.includes(' / ') ? product.sku.split(' / ').slice(1).join(' / ') : (product.sku.includes('-') ? product.sku.split('-').slice(1).join(' / ') : ''))
      };
    }
  } else {
    row.selectedProduct = null;
  }
};

const onPlatformChange = (index: number, platform: string) => {
  const row = uploadForm.contentGroups[index];
  if (!row) return;
  
  if (platform === 'TikTok') {
    row.contentType = 'Tiktok video';
  } else if (platform === 'Instagram') {
    row.contentType = 'IG-Reel';
  } else if (platform === 'YouTube') {
    row.contentType = 'Youtube video';
  } else {
    row.contentType = '其它';
  }
};

const clearProduct = (index: number) => {
  const row = uploadForm.contentGroups[index];
  if (row) { row.selectedProduct = null; row.skus = []; }
};

const createEmptyGroup = (): ContentGroup => ({
  skus: [], platform: undefined, images: [], videos: [], link: '', isCommercial: true, selectedProduct: null, 
  uploadedPids: [], shellPids: [], tags: [],
  contentType: '其它', publishDate: undefined, remark: '', groupIndex: undefined
});

const productSelectorVisible = ref(false);
const currentSelectorIndex = ref<number>(-1);

const currentInitialSelectedSkus = computed(() => {
  if (currentSelectorIndex.value === -1) return [];
  const group = uploadForm.contentGroups[currentSelectorIndex.value];
  if (!group || !group.skus) return [];
  // 核心修复：优先使用 vid:shopifyVariantId 格式，实现精准 ID 匹配
  return group.skus.map(s => {
    if (!s) return null;
    const parts = s.split('_');
    const shopifyVid = parts[1];
    // 优先用 vid 精准匹配（与 ProductSelectorModal 的 generateFingerprint 保持一致）
    if (shopifyVid && shopifyVid !== 'null' && shopifyVid !== '') {
      return `vid:${shopifyVid}`;
    }
    // 兜底：通过产品对象生成指纹
    const p = findProductBySkuKey(s);
    if (p) {
      const pAny = p as any;
      const vShopifyVid = pAny.shopifyVariantId;
      if (vShopifyVid) return `vid:${vShopifyVid}`;
      return generateFingerprint(pAny.sku || '', pAny.variantTitle || pAny.variant_title || pAny.variant || '');
    }
    // 最终兜底：如果是包含了连字符的格式（如 WYJWX0421-Chocolate-XS(0-2)），需要拆解出主 SKU 和规格以匹配 exactFinger
    const fallbackStr = parts[0] || '';
    if (fallbackStr.includes('-')) {
      const splitIndex = fallbackStr.indexOf('-');
      const mainSku = fallbackStr.slice(0, splitIndex).toLowerCase().replace(/[^a-z0-9]/g, '');
      const varStr = fallbackStr.slice(splitIndex + 1).toLowerCase().replace(/[^a-z0-9]/g, '');
      return `${mainSku}|${varStr}`;
    }
    // 如果没有连字符，直接作为 mainSku
    return fallbackStr.toLowerCase().replace(/[^a-z0-9]/g, '');
  }).filter(Boolean) as string[];
});

const currentInitialSelectedKeys = computed(() => {
  if (currentSelectorIndex.value === -1) return [];
  const group = uploadForm.contentGroups[currentSelectorIndex.value];
  if (!group || !group.skus) return [];
  
  // getUniqueSkuKey 格式：`${sku}_${shopifyVariantId}_${dbId}`
  // ProductSelectorModal 里商品的 id 是本地数据库 variant id，即 parts[2]
  return group.skus.map(s => {
    if (!s) return null;
    const parts = s.split('_');
    // parts[2] = 本地数据库 variant id（最精确）
    if (parts.length >= 3 && !isNaN(Number(parts[2])) && Number(parts[2]) > 0) {
      return Number(parts[2]);
    }
    // 兜底：parts[1] 可能是旧格式的 dbId
    if (parts.length >= 2 && !isNaN(Number(parts[1])) && Number(parts[1]) > 0) {
      return Number(parts[1]);
    }
    return null;
  }).filter(Boolean) as number[];
});

const currentOrderSkus = computed(() => {
  if (!orderDetail.value?.products) return [];
  // 核心：只推荐真正的订单商品给选品器展示蓝色标签
  return (orderDetail.value.products as any[])
    .filter((p: any) => p._isOrderLine === true)
    .map((p: any) => {
      const v = p.variant_title || p.variant || p.variantTitle || '';
      // 传入 shopifyVariantId 以实现精准 ID 匹配
      const f = generateFingerprint(p.sku || '', v, p.shopifyProductId || p.spu, p.shopifyVariantId);
      return f;
    }).filter(Boolean);
});

const findProductBySkuKey = (key: string) => {
  if (!key) return null;
  const parts = key.split('_');
  const skuString = parts[0] || '';
  const shopifyVid = parts[1] || '';
  const dbId = Number(parts[2] || parts[1]); // 兼容旧版格式 [sku, id]

  const products = orderDetail.value?.products || [];

  // 1. 优先通过 Shopify Variant ID 匹配 (最精确)
  if (shopifyVid && shopifyVid !== 'null') {
    const byVid = products.find((p: any) => String(p.shopifyVariantId) === String(shopifyVid));
    if (byVid) return byVid;
  }

  // 2. 通过本地 ID 匹配
  if (!isNaN(dbId)) {
    const byId = products.find((p: any) => Number(p.id) === dbId);
    if (byId) return byId;
  }

  // 2. 特殊修复：处理从数据库回显的带横杠复合 SKU（如 SKU-Color-Size）
  // 这种格式通常没有 _id 后缀
  if (skuString.includes('-')) {
    const searchFinger = skuString.toLowerCase().replace(/[^a-z0-9]/g, '');
    const byFinger = products.find((p: any) => {
      const pFinger = generateFingerprint(p.sku || '', p.variantTitle || p.variant_title || p.variant || '');
      return pFinger === searchFinger;
    });
    if (byFinger) return byFinger;
  }

  // 3. 兜底：通过纯 SKU 字符串匹配
  return products.find((p: any) => {
    const pSku = (p.sku || '').toLowerCase().replace(/[^a-z0-9]/g, '');
    const searchSku = skuString.toLowerCase().replace(/[^a-z0-9]/g, '');
    return pSku === searchSku || p.sku === skuString;
  });
};

const extractSpecsFromKey = (key: string) => {
  const p = findProductBySkuKey(key) as any;
  if (p) {
    // 优先读取各种可能的规格字段
    const vTitle = p.variantTitle || p.variant_title || p.variant || '';
    if (vTitle && vTitle !== '-') {
      // 按照常见的 / 、· 或 , 分隔符拆分规格
      return vTitle.split(/\s*[\/\·\,]\s*/).filter(Boolean);
    }
  }
  
  // 3. 特殊兜底：从 SKU 字符串中尝试提取规格
  const skuStr = key.split('_')[0] || '';
  if (skuStr.includes('-')) {
    const parts = skuStr.split('-');
    if (parts.length > 1) {
      return parts.slice(1).filter(Boolean);
    }
  }
  return [];
};

const removeSkuFromGroup = (group: ContentGroup, key: string) => {
  if (!key) {
    // Clear all if key is empty (case for clear all button)
    group.skus = [];
  } else {
    const idx = group.skus.indexOf(key);
    if (idx > -1) {
      group.skus.splice(idx, 1);
    }
  }
  handleProductChange(uploadForm.contentGroups.indexOf(group));
};

const openProductSelector = (index: number) => {
  if (props.readonly) return;
  currentSelectorIndex.value = index;
  productSelectorVisible.value = true;
};

const handleProductSelect = (selectedItems: any[]) => {
  const detail = orderDetail.value;
  if (!detail) {
    orderDetail.value = { products: [] } as any;
  } else if (!detail.products) {
    detail.products = [];
  }

  const group = uploadForm.contentGroups[currentSelectorIndex.value];
  if (!group) return;

  // 1. 同步所选商品到 orderDetail.products（确保元数据可用）
  selectedItems.forEach(item => {
    const detail = orderDetail.value;
    if (!detail) return;
    const productsList = detail.products || [];
    // 修复：用 shopifyVariantId 或 sku+variant 组合唯一比对，而非单纯 SKU
    const exists = productsList.some((p: any) => {
      if (item.shopifyVariantId && p.shopifyVariantId) {
        return String(p.shopifyVariantId) === String(item.shopifyVariantId);
      }
      // 兜底：sku + variantTitle 联合比对
      const pVariant = (p.variantTitle || p.variant_title || p.variant || '').toLowerCase();
      const iVariant = (item.variant === '-' ? '' : (item.variant || '')).toLowerCase();
      return (p.sku || '').toLowerCase() === (item.sku || '').toLowerCase() && pVariant === iVariant;
    });
    
    if (!exists) {
      if (!detail.products) detail.products = [];
      const newProduct = {
        id: item.id,
        name: item.title || item.name,
        sku: item.sku,
        image: item.image,
        variantTitle: item.variant !== '-' ? item.variant : '',
        quantity: 1,
        spu: item.spu,
        shopifyProductId: item.shopifyProductId,
        shopifyVariantId: item.shopifyVariantId, // 关键：必须传递 shopifyVariantId
        _isOrderLine: false // 手动选的商品标记为非原生订单项
      };
      (detail.products as any[]).push(newProduct);
    }
  });

  // 2. 直接使用 ProductSelectorModal 返回的完整 item 构建 skuKey，无需再回查 orderDetail
  group.skus = selectedItems.map(item => {
    return `${item.sku}_${item.shopifyVariantId || ''}_${item.id}`;
  });

  handleProductChange(currentSelectorIndex.value);
  productSelectorVisible.value = false;
};

const initForm = async () => {
  taskData.taskId = props.taskData.taskId;
  taskData.influencer = props.taskData.influencer;
  taskData.influencerId = props.taskData.influencerId || 0;
  taskData.contentId = props.taskData.key || 0;
  taskData.orderNo = props.taskData.orderNo || '';
  taskData.productSku = props.taskData.productSku || '';
  taskData.owner = props.taskData.owner || '';
  
  // 默认开启辅助信息侧边栏
  showOrderDetail.value = true;
  orderDetail.value = null;

  // 0. Load tags
  loadTags();

  // 1. 先尝试加载已有草稿内容
  await loadExistingContents();

  // 2. 如果没有任何已有内容，则初始化一个空组
  if (uploadForm.contentGroups.length === 0) {
    uploadForm.contentGroups = [createEmptyGroup()];
  }

  // Auto-fetch insights
  if (taskData.orderNo) fetchOrderInfo();
};

const loadExistingContents = async () => {
  let effectiveTaskGroupId = taskData.taskId;

  // 如果 taskId 是前端临时生成的 TASK-xxx，尝试通过 contentId 获取真实 taskGroupId
  if ((!effectiveTaskGroupId || effectiveTaskGroupId.startsWith('TASK-')) && taskData.contentId) {
    try {
      const detail = await contentService.getContent(taskData.contentId);
      if (detail.taskGroupId) {
        effectiveTaskGroupId = detail.taskGroupId;
        taskData.taskId = effectiveTaskGroupId; // 同步更新
      }
    } catch (e) { console.warn('Failed to resolve taskGroupId from contentId:', e); }
  }

  if (!effectiveTaskGroupId) return;
  loadingExisting.value = true;
  try {
    // 按 taskGroupId 查询所有已上传或草稿状态的内容
    const result = await contentService.getContents({
      taskGroupId: effectiveTaskGroupId,
      page: 0,
      size: 100 // 假设一个任务组不会超过 100 个文件
    });

    if (result.content && result.content.length > 0) {
      // 这里的逻辑是将散装的记录（每文件一条）按属性（平台+链接+SKU）重新聚合成 UI 中的 ContentGroup
      const groupsMap = new Map<string, ContentGroup>();
      
      result.content.forEach((item: any) => {
        // 创建聚合 Key：使用 contentGroupIndex 而不是属性拼接，确保相同属性的不同项目不会被合并
        const key = String(item.contentGroupIndex ?? 0);
        
        if (!groupsMap.has(key)) {
          groupsMap.set(key, {
            skus: item.productSku ? item.productSku.split(',').filter(Boolean) : [],
            platform: item.platform,
            images: [],
            videos: [],
            link: item.description || '',
            isCommercial: item.isCommercial ?? true,
            selectedProduct: item.productSku ? { id: 0, title: '', sku: item.productSku } : null,
            uploadedPids: [],
            tags: item.tagIds || [],
            contentType: item.contentType || '其它',
            publishDate: item.publishDate,
            remark: item.remark || '',
            groupIndex: item.contentGroupIndex ?? 0,
            initialStatus: item.status // 核心修复：记录此组的原始状态
          });
        }
        
        const group = groupsMap.get(key)!;
        group.uploadedPids.push(item.id);
        
        const fileObj = {
          uid: String(item.id),
          name: item.fileName || 'file',
          status: 'done',
          url: item.previewUrl,
          thumbUrl: item.thumbnailUrl || item.previewUrl,
          uploaded: true,
          contentId: item.id,
          type: item.postType === 'VIDEO' ? 'video/mp4' : 'image/jpeg'
        };

        // 关键修复: 只有当确实有文件路径或预览图时，才视为文件显示
        const hasFile = !!(item.filePath || item.previewUrl);

        if (hasFile) {
          if (item.postType === 'VIDEO') {
            group.videos.push(fileObj);
          } else {
            group.images.push(fileObj);
          }
        } else {
          // 如果没有文件，记录为 shellPid，后续可以复用
          group.shellPids = group.shellPids || [];
          group.shellPids.push(item.id);
        }
        
        // Ensure that link is set if empty but item has description
        if (!group.link && item.description) {
          group.link = item.description;
        }
      });

      uploadForm.contentGroups = Array.from(groupsMap.values());
      // 对于聚合出来的 product，如果是以 SKU 恢复的，尝试补充图片等信息（可选）
    } else {
      uploadForm.contentGroups = [];
    }
  } catch (e) {
    console.error('Failed to load existing contents:', e);
    uploadForm.contentGroups = [];
  } finally {
    loadingExisting.value = false;
  }
};

const addContentGroup = () => uploadForm.contentGroups.push(createEmptyGroup());

const removeContentGroup = async (index: number) => {
  const group = uploadForm.contentGroups[index];
  if (group && group.uploadedPids.length > 0) {
    const hide = message.loading('清理已存素材...', 0);
    try {
      let failCount = 0;
      for (const pid of [...group.uploadedPids]) {
        try {
          await contentService.deleteContent(pid);
        } catch (e: any) {
          failCount++;
          console.warn(`Failed to delete content ${pid}:`, e?.response?.data?.message || e.message);
        }
      }
      hide();
      if (failCount > 0) message.warning(`${failCount} 个素材无法删除（可能已被审核）`);
      else message.success('已清理');
    } catch (e) { hide(); message.warning('清理失败'); }
  }
  uploadForm.contentGroups.splice(index, 1);
};

const handleRemoveFile = async (file: any, group: ContentGroup) => {
  if (file.contentId) {
    try {
      await contentService.deleteContent(file.contentId);
      // 从已分配 ID 列表中移除
      group.uploadedPids = group.uploadedPids.filter(id => id !== file.contentId);
      if (group.shellPids) {
        group.shellPids = group.shellPids.filter(id => id !== file.contentId);
      }
      message.success('素材已移除');
      return true;
    } catch (e) {
      message.error('删除素材失败');
      return false;
    }
  }
  return true;
};

const handleCancel = () => { modalVisible.value = false; };

const formatPublishDate = (date: any) => {
  if (!date) return undefined;
  if (typeof date.format === 'function') return date.format('YYYY-MM-DDTHH:mm:ss');
  const dStr = String(date);
  if (dStr.length === 10) return dStr + 'T00:00:00';
  return dStr.replace(' ', 'T').substring(0, 19);
};

const processGroups = async (isDraft: boolean) => {
  let successCount = 0;
  let failCount = 0;
  
  for (let i = 0; i < uploadForm.contentGroups.length; i++) {
    const group = uploadForm.contentGroups[i];
    if (!group) continue;

    // 智能状态决策：
    // 1. 如果是点击“发布”（!isDraft），则至少要推进到 PENDING_REVIEW（待审核）
    // 2. 如果是点击“保存草稿”（isDraft），为了防止从“已完成”列表回退：
    //    - 如果它已经是 PENDING_REVIEW 或 PUBLISHED 或 READY，就保持现状。
    //    - 否则才设定为 PENDING_UPLOAD。
    let targetStatus = isDraft ? 'PENDING_UPLOAD' : 'PENDING_REVIEW';
    
    // 状态安全：绝不将已进入成熟阶段的任务反向回滚到 PENDING_UPLOAD
    const stableStatuses = ['PENDING_REVIEW', 'PUBLISHED', 'READY', 'COMPLETED'];
    if (isDraft && group.initialStatus && stableStatuses.includes(group.initialStatus)) {
      targetStatus = group.initialStatus;
    }
    
    // 如果确认发布时发现原本已经是 PUBLISHED，则保持 PUBLISHED (或根据需求决定。此处选择维持最高稳定性状态)
    if (!isDraft && group.initialStatus === 'PUBLISHED') {
      targetStatus = 'PUBLISHED';
    }
    
    // 将内部 skuKey（sku_vid_dbId 格式）转化为友好显示名称（如 wyjwx0422-Sage-XS(0-2)）
    // 保证列表页"关联商品"列显示一致的可读格式
    const skuValue = group.skus.map(skuKey => {
      const product = findProductBySkuKey(skuKey);
      const specs = extractSpecsFromKey(skuKey);
      
      // 如果找到了关联商品，则使用商品原始 SKU 作为 base
      // 如果没找到（回显数据），则从 skuKey 中拆解出可能的 SKU 部分
      let baseSku = product?.sku || skuKey.split('_')[0] || skuKey;
      
      // 安全修复：如果没找到商品对象（通常是数据库回显数据），且 baseSku 已经包含了检测到的规格，
      // 则先截断 baseSku 以获得纯正的 SKU 部分，防止重复追加。
      if (!product && baseSku.includes('-') && specs.length > 0) {
        const specStr = specs.join('-');
        if (baseSku.endsWith(`-${specStr}`)) {
          baseSku = baseSku.slice(0, -(specStr.length + 1));
        }
      }

      if (specs.length > 0) {
        return `${baseSku}-${specs.join('-')}`;
      }
      return baseSku;
    }).join(',');
    
    // 如果是第一个组，且还没有关联记录，尝试复用原始的 contentId (如果有)
    if (i === 0 && group.uploadedPids.length === 0 && taskData.contentId) {
      group.uploadedPids.push(taskData.contentId);
      // 同步添加到 shellPids，这样上传步骤可以复用该记录，避免重复创建
      group.shellPids = group.shellPids || [];
      group.shellPids.push(taskData.contentId);
    }

    // 1. 处理已有记录的元数据更新
    if (group.uploadedPids.length > 0) {
      // 找到这个组里面所有的记录，把它们都更新（包括 shell 记录）
      // 即使用户没有上传文件，但填了 link 或者 platform，shell 记录也应该跟随任务流转到 PENDING_REVIEW
      for (const pid of group.uploadedPids) {
        try {
          await contentService.updateContent(pid, {
            productSku: skuValue,
            orderNo: taskData.orderNo,
            status: targetStatus,
            tagIds: (group.tags || []).map((t: any) => Number(t)),
            isCommercial: group.isCommercial,
            owner: taskData.owner,
            description: group.link || '',
            platform: group.platform,
            contentType: group.contentType,
            publishDate: formatPublishDate(group.publishDate),
            remark: group.remark,
            contentGroupIndex: i
          });
          // 不要在这里计算 successCount，避免没有 file 的 shell 也计入文件成功数，或者看需求，不影响主流程就好
        } catch (e: any) {
          message.error(`更新前置信息失败: ${e.response?.data?.message || e.message}`);
          failCount++;
        }
      }
    } else if (skuValue || group.platform || group.link) {
      // 没有任何关联记录（连 shell 都没有），创建一个纯元数据草稿记录
      try {
        const newShell = await contentService.createContent({
          influencerId: taskData.influencerId,
          title: `${taskData.influencer} - ${group.platform || '素材'}`,
          description: group.link || '',
          platform: group.platform,
          productSku: skuValue,
          orderNo: taskData.orderNo,
          taskGroupId: taskData.taskId,
          owner: taskData.owner,
          tagIds: (group.tags || []).map((t: any) => Number(t)),
          isCommercial: group.isCommercial,
          contentType: group.contentType,
          publishDate: formatPublishDate(group.publishDate),
          postType: 'IMAGE', // 默认为图片，上传时会根据文件纠正
          remark: group.remark,
          contentGroupIndex: i
        } as any);
        await contentService.updateContent(newShell.id, { status: targetStatus } as any); 
        group.uploadedPids.push(newShell.id);
        group.shellPids = group.shellPids || [];
        group.shellPids.push(newShell.id);
        // 注意：不增加 successCount，因为只是背景草稿，没有实际内容
      } catch (e: any) {
        message.error(`创建记录失败: ${e.response?.data?.message || e.message}`);
        failCount++;
      }
    }

    // 2. 处理新上传的文件
    const filesToUpload = [
      ...group.images.filter((f: any) => !f.uploaded),
      ...group.videos.filter((f: any) => !f.uploaded)
    ];

    // 找到可以复用的 shell 记录（已经保存了元数据但没关联文件的记录）
    const availableShellPids = [...(group.shellPids || [])];
    let shellIdx = 0;

    // --- 并发上传控制 ---
    const MAX_CONCURRENT_UPLOADS = 3;
    let activeUploads = 0;
    let fileIdx = 0;
    
    const uploadNext = async (): Promise<void> => {
      if (fileIdx >= filesToUpload.length) return;
      
      const currentIdx = fileIdx++;
      const fileItem = filesToUpload[currentIdx];
      
      if (fileItem.originFileObj) {
        try {
          const type = fileItem.type?.startsWith('video') ? 'VIDEO' : 'IMAGE';
          let targetContentId: number;

          if (shellIdx < availableShellPids.length) {
            targetContentId = availableShellPids[shellIdx++] as number;
            // 该 shell 记录现在有了文件，从 shellPids 中正式移除
            if (group.shellPids) {
              group.shellPids = group.shellPids.filter(id => id !== targetContentId);
            }
          } else {
            // 没有多余的 shell 记录可以复用，创建新记录
            const newRes = await contentService.createContent({
              influencerId: taskData.influencerId,
              title: `${taskData.influencer} - ${group.platform || '素材'}`,
              description: group.link || '',
              platform: group.platform,
              productSku: skuValue,
              orderNo: taskData.orderNo,
              taskGroupId: taskData.taskId,
              owner: taskData.owner,
              tagIds: (group.tags || []).map((t: any) => Number(t)),
              isCommercial: group.isCommercial,
              contentType: group.contentType,
              publishDate: formatPublishDate(group.publishDate),
              postType: type,
              remark: group.remark,
              contentGroupIndex: i
            } as any);
            targetContentId = newRes.id;
            group.uploadedPids.push(targetContentId);
          }

          // 上传文件并同步更新最终状态
          // 传递进度回调给 contentService
          fileItem.status = 'uploading';
          fileItem.percent = 0;
          
          await contentService.uploadFile(
            targetContentId, 
            fileItem.originFileObj,
            fileItem.thumbnailBlob, // 传入由于压缩/截帧产生的 blob
            fileItem.videoWidth,
            fileItem.videoHeight,
            fileItem.duration,
            (progressEvent) => {
              if (progressEvent.total) {
                fileItem.percent = Math.round((progressEvent.loaded * 100) / progressEvent.total);
              }
            }
          );
          
          await contentService.updateContent(targetContentId, {
            status: targetStatus,
            postType: type
          } as any);

          fileItem.uploaded = true;
          fileItem.contentId = targetContentId;
          fileItem.status = 'done';
          fileItem.percent = 100;
          successCount++;
        } catch (e: any) {
          console.error('File upload failed:', e);
          fileItem.status = 'error';
          message.error(`文件上传失败: ${e.response?.data?.message || e.message}`);
          failCount++;
        }
      }
      
      // 递归触发下一个
      await uploadNext();
    };

    // 启动初始的并发任务池
    const promises = [];
    for (let i = 0; i < MAX_CONCURRENT_UPLOADS && i < filesToUpload.length; i++) {
        promises.push(uploadNext());
    }
    
    await Promise.all(promises);
  }
  return { successCount, failCount };
};

const handleSaveDraft = async () => {
  const hasContent = uploadForm.contentGroups.some(group => 
    group.images.length > 0 || group.videos.length > 0 || (group.link && group.link.trim())
  );
  if (!hasContent) { message.info('无上传内容（请添加图片或视频或链接）'); return; }
  savingDraft.value = true;
  try {
    const { successCount, failCount } = await processGroups(true);
    if (successCount > 0) message.success(`草稿保存成功 (${successCount})`);
    if (failCount > 0) message.error(`${failCount} 失败`);
    // 保存草稿后也通知父组件刷新列表
    emit('ok', uploadForm.contentGroups);
  } catch (e) {
    message.error('操作异常');
  } finally {
    savingDraft.value = false;
  }
};

const handleCompleteUpload = async () => {
  const hasContent = uploadForm.contentGroups.some(group => 
    group.images.length > 0 || group.videos.length > 0 || (group.link && group.link.trim())
  );
  if (!hasContent) { message.warning('请提供素材內容（图片或视频或链接至少一个）'); return; }
  // 校验发帖时间必填
  const missingDateIdx = uploadForm.contentGroups.findIndex(group => !group.publishDate);
  if (missingDateIdx >= 0) {
    message.warning(`第 ${missingDateIdx + 1} 项缺少发帖日期，请填写后再提交`);
    return;
  }
  saving.value = true;
  try {
    const { successCount, failCount } = await processGroups(false);
    if (failCount === 0) message.success('发布完成');
    else message.warning(`完成: ${successCount} 成功, ${failCount} 失败`);
    modalVisible.value = false;
    emit('ok', uploadForm.contentGroups); emit('success');
  } catch (e) { message.error('提交失败'); } finally { saving.value = false; }
};

// 自定义指令：为 .box-body 内所有 <img> 自动添加 loading="lazy"
const vLazyImages = {
  mounted(el: HTMLElement) {
    const setLazy = (img: HTMLImageElement) => {
      if (!img.getAttribute('loading')) {
        img.setAttribute('loading', 'lazy');
      }
    };
    // 处理已有的 img
    el.querySelectorAll('img').forEach((img) => setLazy(img as HTMLImageElement));
    // 监听后续动态添加的 img
    const observer = new MutationObserver((mutations) => {
      for (const mutation of mutations) {
        for (const node of mutation.addedNodes) {
          if (node instanceof HTMLImageElement) {
            setLazy(node);
          } else if (node instanceof HTMLElement) {
            node.querySelectorAll('img').forEach((img) => setLazy(img as HTMLImageElement));
          }
        }
      }
    });
    observer.observe(el, { childList: true, subtree: true });
    (el as any).__lazyObserver = observer;
  },
  unmounted(el: HTMLElement) {
    (el as any).__lazyObserver?.disconnect();
  }
};

onMounted(() => loadDefaultStore());
watch(() => props.open, (v) => { if (v) initForm(); modalVisible.value = v; });
watch(modalVisible, (v) => emit('update:open', v));

// 移除原来的 watch，改用 explicit onPlatformChange
</script>

<style lang="scss" scoped>
/* 核心弹窗美化 - 豪华三端布局 */
.refined-upload-wrapper {
  :deep(.ant-modal-content) { border-radius: 24px !important; background: transparent !important; padding: 0 !important; box-shadow: none !important; }
  :deep(.ant-modal-close) { display: none !important; }
  :deep(.ant-modal-wrap) { backdrop-filter: blur(8px); }
}

.modal-layout-container {
  display: flex; gap: 0; background: transparent; transition: all 0.45s cubic-bezier(0.19, 1, 0.22, 1);
  &.with-left { gap: 12px; .insight-sidebar.is-left { margin-left: 0; } }
  &.with-right { gap: 12px; .insight-sidebar.is-right { margin-right: 0; } }
}

/* 通用侧边栏样式 */
.insight-sidebar {
  width: 360px; flex-shrink: 0; background: rgba(255,255,255,0.97); backdrop-filter: blur(20px); border-radius: 20px; display: flex; flex-direction: column; overflow: hidden;
  box-shadow: 0 8px 32px rgba(0,0,0,0.08), 0 2px 8px rgba(0,0,0,0.04); height: min(840px, 94vh); border: 1px solid rgba(226,232,240,0.6);
  
  .sidebar-head {
    height: 54px; padding: 0 20px; background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%); border-bottom: 1px solid #e8edf3;
    display: flex; align-items: center; justify-content: space-between;
    .head-title { display: flex; align-items: center; gap: 8px; font-size: 13px; font-weight: 800; color: #1e293b; letter-spacing: -0.01em; }
    .sidebar-close { cursor: pointer; color: #94a3b8; font-size: 13px; width: 28px; height: 28px; border-radius: 8px; display: flex; align-items: center; justify-content: center; transition: all 0.2s; &:hover { color: #ef4444; background: #fef2f2; } }
  }
  .sidebar-scroll-content { flex: 1; overflow-y: auto; padding: 18px; &::-webkit-scrollbar { width: 3px; } &::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 6px; } }
  .sidebar-loading, .sidebar-empty { height: 160px; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 10px; color: #94a3b8; font-size: 12px; font-weight: 600; }
}

/* 订单部分视觉样式 */
.order-section-luxe {
  margin-bottom: 24px;
  .section-tag { font-size: 9px; font-weight: 800; color: #3b82f6; text-transform: uppercase; letter-spacing: 0.8px; margin-bottom: 12px; opacity: 0.8; }
  .kv-list { display: flex; flex-direction: column; gap: 8px; }
  .kv-item { display: flex; justify-content: space-between; align-items: center; .k { font-size: 11px; color: #64748b; font-weight: 600; } .v { font-size: 11px; color: #1e293b; font-weight: 700; font-family: 'JetBrains Mono'; } .v-price { font-size: 13px; color: #0f172a; font-weight: 800; } }
  .kv-item-stack { display: flex; flex-direction: column; gap: 6px; .k { font-size: 11px; color: #64748b; font-weight: 600; } .v-address { font-size: 11px; color: #1e293b; font-weight: 600; line-height: 1.5; background: #f8fafc; padding: 10px; border-radius: 8px; } }
}

.timeline-box-luxe {
  padding: 4px 4px 16px;
  :deep(.ant-timeline-item-content) { font-size: 11px; font-weight: 600; color: #475569; }
  .timeline-sub-v { font-size: 10px; color: #94a3b8; margin-top: 2px; font-family: 'JetBrains Mono'; }
}

.stats-panel-luxe {
  background: linear-gradient(135deg, #f0f4ff 0%, #f8fafc 100%); padding: 18px; border-radius: 16px; border: 1px solid #e0e7ff;
  .stats-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px; }
  .stat-bubble {
    background: white; padding: 14px 8px; border-radius: 14px; text-align: center; border: 1px solid #f1f5f9; box-shadow: 0 2px 8px rgba(0,0,0,0.03); transition: all 0.25s ease;
    &:hover { transform: translateY(-2px); box-shadow: 0 6px 16px rgba(0,0,0,0.06); }
    .s-val { font-size: 20px; font-weight: 900; color: #0f172a; line-height: 1; }
    .s-lab { font-size: 9px; font-weight: 700; color: #94a3b8; margin-top: 6px; text-transform: uppercase; letter-spacing: 0.5px; }
    &.accent { background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%); border-color: #bfdbfe; .s-val { color: #2563eb; } }
    &.highlight { background: linear-gradient(135deg, #fdf2f8 0%, #fce7f3 100%); border-color: #fbcfe8; .s-val { color: #db2777; } }
  }
}

.order-p-list { display: flex; flex-direction: column; gap: 10px; }
.order-p-item {
  display: flex; flex-direction: column; gap: 8px; background: #fff; border: 1px solid #f1f5f9; padding: 8px; border-radius: 12px;
  .p-img-wrapper {
    position: relative; width: 100%; border-radius: 8px; overflow: hidden; background: #f8fafc;
    .p-img-mini { width: 100%; height: auto; display: block; object-fit: cover; }
    .p-sku-tag {
      position: absolute; bottom: 0; left: 0; right: 0; background: rgba(255, 255, 255, 0.98);
      backdrop-filter: blur(4px); color: #334155; font-size: 10px; padding: 3px 6px;
      font-family: 'JetBrains Mono', monospace; text-align: center; font-weight: 700;
      white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
      box-shadow: 0 -2px 10px rgba(0,0,0,0.05);
      border-top: 1px solid #f1f5f9;
      
      .sku-main { color: #0369a1; font-weight: 800; }
      .sku-spec {
        font-weight: 700;
        &.spec-0 { color: #16a34a; }
        &.spec-1 { color: #ea580c; }
        &.spec-2 { color: #8b5cf6; }
      }
    }
  }
  .p-info-mini {
    padding: 0 4px;
    .p-name-mini { font-size: 11px; font-weight: 700; color: #1e293b; line-height: 1.4; display: -webkit-box; -webkit-line-clamp: 2; line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
    .p-qty-mini { font-size: 10px; color: #94a3b8; margin-top: 2px; font-weight: 600; }
  }
}

/* 红人画像详情 */
.influ-profile-card {
  background: linear-gradient(135deg, #eff6ff 0%, #faf5ff 50%, #fff 100%); padding: 18px; border-radius: 16px; border: 1px solid #dbeafe; margin-bottom: 24px; box-shadow: 0 2px 8px rgba(59,130,246,0.06);
  .influ-name-l { font-size: 18px; font-weight: 900; color: #0f172a; letter-spacing: -0.02em; }
  .influ-email-l { font-size: 11px; color: #64748b; font-weight: 600; margin-top: 4px; }
  .influ-tags-box { display: flex; flex-wrap: wrap; gap: 6px; margin-top: 14px; }
}

.mini-order-list { display: flex; flex-direction: column; gap: 10px; }
.mini-order-item {
  padding: 10px; background: #f8fafc; border-radius: 8px; .mo-row { display: flex; justify-content: space-between; align-items: center; .mo-no { font-size: 11px; font-weight: 800; font-family: 'JetBrains Mono'; } } .mo-date { font-size: 10px; color: #94a3b8; font-weight: 600; margin-top: 4px; }
}

.mini-content-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px; }
.mini-content-tile {
  aspect-ratio: 1; border-radius: 6px; overflow: hidden; border: 1px solid #f1f5f9; position: relative;
  .mc-img { width: 100%; height: 100%; object-fit: cover; }
  .mc-placeholder { 
    width: 100%; height: 100%; display: flex; align-items: center; justify-content: center; background: #f8fafc; color: #cbd5e1; font-size: 20px;
    &.shopify { background: #f0fdf4; color: #15803d; }
    &.facebook { background: #eff6ff; color: #1d4ed8; }
    &.tiktok { background: #000; color: #fff; }
    &.instagram { background: linear-gradient(45deg, #f09433 0%,#e6683c 25%,#dc2743 50%,#cc2366 75%,#bc1888 100%); color: #fff; }
  }
  .mc-platform { position: absolute; bottom: 0; width: 100%; background: rgba(0,0,0,0.6); backdrop-filter: blur(4px); color: white; font-size: 8px; text-align: center; padding: 2px 0; font-weight: 700; }
  .mc-link-overlay { 
    position: absolute; top: 4px; right: 4px; width: 20px; height: 20px; background: rgba(255,255,255,0.8); 
    border-radius: 4px; display: flex; align-items: center; justify-content: center; font-size: 10px; color: #3b82f6; 
    opacity: 0; transition: all 0.2s;
  }
  &:hover .mc-link-overlay { opacity: 1; }
}
.empty-hint-mini { font-size: 11px; color: #cbd5e1; text-align: center; padding: 10px; background: #fbfbfc; border-radius: 8px; }

/* 1. 主容器 (精细化 16px) */
.refined-container {
  flex: 1; min-width: 0; overflow: hidden; background: white; border-radius: 20px; display: flex; flex-direction: column; 
  height: min(840px, 94vh); box-shadow: 0 8px 40px rgba(0,0,0,0.1), 0 2px 10px rgba(0,0,0,0.04); border: 1px solid rgba(226,232,240,0.6);
}

.refined-header {
  height: 60px; padding: 0 24px; background: white; border-bottom: 1px solid #f1f5f9;
  display: flex; align-items: center; justify-content: space-between;
  
  .header-left {
    display: flex; align-items: center; gap: 16px;
    .brand-badge-luxe { width: 32px; height: 32px; background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%); color: #3b82f6; border-radius: 8px; display: flex; align-items: center; justify-content: center; font-size: 16px; }
    .header-text {
      .header-main-info { 
        display: flex; align-items: center; gap: 10px; 
        .meta-tag-blue { background: #3b82f6; color: #fff; font-size: 9px; font-weight: 800; padding: 1px 8px; border-radius: 4px; } 
        .influencer-name-refined { 
          margin: 0; font-size: 16px; font-weight: 800; color: #016; transition: all 0.2s; 
          &.clickable { cursor: pointer; &:hover { color: #3b82f6; } }
          .i-tip { font-size: 12px; opacity: 0.3; margin-left: 4px; }
        } 
      }
      .header-meta-info {
        display: flex; align-items: center; gap: 16px; margin-top: 2px;
        .order-edit-inline {
          .meta-item-luxe {
            font-size: 11px; color: #64748b; font-weight: 600; display: flex; align-items: center; gap: 6px; border: 1px solid transparent; padding: 2px 6px; border-radius: 6px;
            
            &.is-complex {
              .link-trigger { 
                color: #1e293b; font-family: 'JetBrains Mono'; font-weight: 800; border-bottom: 1.5px solid #e2e8f0; cursor: pointer; transition: all 0.2s;
                &:hover { color: #3b82f6; border-color: #3b82f6; background: #eff6ff; }
              }
              .edit-icon-trigger { 
                font-size: 12px; color: #94a3b8; cursor: pointer; padding: 4px; border-radius: 4px; transition: all 0.2s;
                &:hover { color: #3b82f6; background: #f1f5f9; }
              }
            }
          }
          .inline-input-box {
            display: flex; align-items: center; gap: 8px; background: #fff; border: 1px solid #3b82f6; border-radius: 6px; padding: 0 8px; height: 26px; box-shadow: 0 2px 8px rgba(59, 130, 246, 0.1);
            .prefix { font-size: 12px; color: #3b82f6; }
            .minimal-input { border: none; outline: none; font-size: 11px; font-weight: 800; font-family: 'JetBrains Mono'; width: 90px; }
            .confirm-btn { font-size: 12px; color: #10b981; cursor: pointer; &:hover { transform: scale(1.1); } }
          }
        }
        .meta-item-luxe { font-size: 11px; color: #64748b; font-weight: 600; display: flex; align-items: center; gap: 5px; .val { font-family: 'JetBrains Mono'; color: #1e293b; font-weight: 700; } }
        .meta-divider-soft { color: #e2e8f0; } 
        .header-sub-text-luxe { font-size: 9px; color: #94a3b8; font-weight: 600; text-transform: uppercase; letter-spacing: 0.5px; }
      }
    }
  }
  .header-sku-highlight {
    display: inline-flex;
    align-items: center;
    gap: 2px;
    background: #f1f5f9;
    padding: 2px 8px;
    border-radius: 6px;
    border: 1px solid #e2e8f0;
    
    .sku-main {
      color: #334155;
      font-weight: 700;
      font-size: 11px;
    }
    
    .sku-spec {
      font-weight: 600;
      font-size: 11px;
      &.spec-0 { color: #475569; }
      &.spec-1 { color: #64748b; }
      &.spec-2 { color: #94a3b8; }
    }
  }
  .header-right {
    display: flex; align-items: center; gap: 20px;
    .batch-stat-luxe { display: flex; align-items: baseline; gap: 6px; .stat-count { font-size: 18px; font-weight: 900; color: #016; } .stat-label { font-size: 10px; color: #94a3b8; font-weight: 700; } }
    .close-icon-refined { width: 28px; height: 28px; border-radius: 6px; background: #f8fafc; color: #94a3b8; display: flex; align-items: center; justify-content: center; cursor: pointer; &:hover { background: #fee2e2; color: #ef4444; } }
  }
}

/* 2. Body Area (Standard 16px) */
.refined-body { flex: 1; overflow: hidden; display: flex; flex-direction: column; padding: 18px 22px; background: linear-gradient(180deg, #fcfdfe 0%, #f8fafc 100%); }

.action-bar-luxe {
  display: flex; align-items: center; gap: 16px; margin-bottom: 16px;
  .add-content-btn { height: 36px; border-radius: 10px; font-weight: 700; font-size: 12px; background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%); border: none; box-shadow: 0 4px 12px rgba(59, 130, 246, 0.25); transition: all 0.2s; &:hover { transform: translateY(-1px); box-shadow: 0 6px 16px rgba(59, 130, 246, 0.35); } }
  .bar-hint-luxe { display: flex; align-items: center; gap: 6px; font-size: 11px; color: #94a3b8; font-weight: 500; }
}

.config-list-scroller { flex: 1; overflow-y: auto; padding-right: 8px; &::-webkit-scrollbar { width: 4px; } &::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 6px; } }

/* 3. Card Style (Compact 16px) */
.content-card-luxe {
  background: white; border-radius: 16px; border: 1px solid #e8edf3; margin-bottom: 16px; box-shadow: 0 2px 12px rgba(0,0,0,0.03); transition: all 0.25s ease;
  &:hover { box-shadow: 0 4px 20px rgba(0,0,0,0.06); border-color: #dbeafe; }
  .card-head-luxe {
    height: 40px; padding: 0 18px; background: linear-gradient(135deg, #fafbfc 0%, #f8fafc 100%); border-bottom: 1px solid #f1f5f9; display: flex; align-items: center; border-radius: 16px 16px 0 0;
    .content-id-tag { display: flex; align-items: center; gap: 8px; .num { font-size: 10px; font-weight: 900; color: #3b82f6; background: #eff6ff; padding: 2px 8px; border-radius: 6px; } .label { font-size: 11px; font-weight: 800; color: #475569; } }
    .head-options { display: flex; align-items: center; gap: 10px; .auth-toggle { display: flex; align-items: center; gap: 6px; .text { font-size: 10px; font-weight: 700; color: #64748b; } } .btn-remove-luxe { font-size: 10px; font-weight: 700; } }
  }
  .card-grid-luxe {
    padding: 20px; display: flex; gap: 24px;
    .control-panel-refined { width: 300px; display: flex; flex-direction: column; gap: 14px; }
    .asset-panel-luxe { flex: 1; min-width: 0; overflow: hidden; }
    .luxe-field { display: flex; align-items: center; gap: 12px; .l-label { width: 54px; font-size: 10px; font-weight: 800; color: #94a3b8; text-transform: uppercase; text-align: right; letter-spacing: 0.3px; } .l-input { flex: 1; } }
  }
}

.full-width { width: 100% !important; }

/* Controls Precision */
.p-selector-luxe {
  cursor: pointer; min-height: 40px; padding: 4px; border: 1.5px solid #e2e8f0; border-radius: 10px; background: #fff; transition: all 0.2s;
  &:hover { border-color: #3b82f6; background: #f8fbff; }
  .p-empty-luxe { height: 32px; display: flex; align-items: center; justify-content: center; gap: 8px; color: #94a3b8; font-size: 11px; font-weight: 700; }
  .p-summary-box { 
    height: 32px; display: flex; align-items: center; justify-content: space-between; padding: 0 10px;
    .summary-content { 
      display: flex; align-items: center; gap: 8px; font-size: 12px; color: #1e293b;
      .icon-main { color: #3b82f6; font-size: 14px; }
      b { color: #3b82f6; font-weight: 800; font-family: 'JetBrains Mono'; margin: 0 2px; }
      .summary-hint { font-size: 10px; color: #94a3b8; font-weight: 600; margin-left: 4px; opacity: 0.7; }
    }
    .p-clear-all { font-size: 14px; color: #cbd5e1; transition: all 0.2s; &:hover { color: #ef4444; transform: scale(1.1); } }
  }
}

:global(.luxe-p-tooltip) {
  width: 560px !important;
  max-width: 560px !important;
  min-width: 560px !important;
  opacity: 1 !important;
}

:global(.luxe-p-tooltip .ant-tooltip-inner) {
  width: 560px !important;
  max-width: 560px !important;
  min-width: 560px !important;
  background: #ffffff !important;
  border-radius: 12px !important;
  padding: 0 !important;
  border: 1px solid #e2e8f0 !important;
  box-shadow: 0 16px 48px rgba(0, 0, 0, 0.22) !important;
  overflow: hidden;
}

:global(.luxe-p-tooltip .tip-p-detail) {
  display: flex;
  flex-direction: column;
  gap: 0;
  filter: none !important;
}

:global(.luxe-p-tooltip .tip-header) {
  background: #f8fafc;
  padding: 12px 16px;
  border-bottom: 1px solid #f1f5f9;
  display: flex;
  align-items: center;
  gap: 8px;
}

:global(.luxe-p-tooltip .tip-header .anticon) { color: #3b82f6; font-size: 16px; }
:global(.luxe-p-tooltip .tip-header span) { color: #1e293b; font-size: 13px; font-weight: 800; }

:global(.luxe-p-tooltip .tip-p-row) {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 16px;
  border-bottom: 1px solid #f8fafc;
  transition: all 0.2s;
  min-width: 0;
}

:global(.luxe-p-tooltip .tip-p-row:last-child) { border-bottom: none; }
:global(.luxe-p-tooltip .tip-p-row:hover) { background: #f0f7ff; }

:global(.luxe-p-tooltip .p-idx) {
  font-size: 10px;
  font-weight: 900;
  color: #94a3b8;
  width: 20px;
  height: 20px;
  background: #f1f5f9;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

:global(.luxe-p-tooltip .tip-p-content) {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  min-width: 0;
  white-space: nowrap;
  overflow: hidden;
}

:global(.luxe-p-tooltip .tip-p-sku) {
  color: #2563eb;
  background: #eff6ff;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 13px;
  font-weight: 800;
  font-family: 'JetBrains Mono', monospace;
  flex-shrink: 0;
}

:global(.luxe-p-tooltip .tip-p-sep) {
  color: #cbd5e1;
  font-weight: 900;
}

:global(.luxe-p-tooltip .tip-p-spec) {
  color: #64748b;
  font-size: 12px;
  font-weight: 600;
  text-overflow: ellipsis;
  overflow: hidden;
}
:global(.luxe-p-tooltip .tip-p-spu) {
  color: #94a3b8;
  font-size: 11px;
  font-weight: 500;
  margin-left: 4px;
}
.luxe-select, .luxe-input, .luxe-datepicker { :deep(.ant-select-selector), :deep(.ant-input), :deep(.ant-picker) { height: 36px !important; border-radius: 8px !important; font-size: 13px !important; font-weight: 600 !important; border: 1.5px solid #e2e8f0 !important; background: #fff !important; } :deep(.ant-select-selection-item) { line-height: 34px !important; } :deep(.ant-select-selection-placeholder) { line-height: 34px !important; } :deep(.ant-picker-input > input) { font-size: 13px !important; font-weight: 600 !important; } }

.p-option {
  display: flex; align-items: flex-start; gap: 10px; padding: 4px 0;
  .p-option-thumb { width: 32px; height: 32px; border-radius: 6px; object-fit: cover; border: 1px solid #f1f5f9; flex-shrink: 0; }
  .p-option-content { display: flex; flex-direction: column; gap: 2px; overflow: hidden; }
  .p-option-top { display: flex; align-items: center; gap: 4px; white-space: nowrap; }
  .p-option-sku { font-family: 'JetBrains Mono'; font-weight: 800; color: #0369a1; font-size: 13px; }
  .p-option-spec { font-size: 11px; font-weight: 700; padding: 0 2px; &.spec-0 { color: #475569; } &.spec-1 { color: #64748b; } &.spec-2 { color: #94a3b8; } }
  .p-option-name { font-size: 11px; color: #64748b; font-weight: 500; line-height: 1.4; margin-top: 2px; }
}

/* Asset Layout */
.asset-layout-luxe { display: flex; gap: 16px; height: 100%; min-width: 0; }
.asset-box-refined {
  flex: 1; display: flex; flex-direction: column; gap: 10px; background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%); border-radius: 14px; border: 1px solid #e8edf3; padding: 16px; min-width: 0; overflow: hidden; transition: all 0.2s;
  &:hover { border-color: #dbeafe; }
  &.video-accent { background: linear-gradient(135deg, #fefefe 0%, #fafbfc 100%); }
  .box-head { display: flex; justify-content: space-between; align-items: center; .title { font-size: 11px; font-weight: 800; color: #475569; display: flex; align-items: center; gap: 8px; } .count { font-size: 10px; font-weight: 700; color: #94a3b8; background: white; padding: 2px 8px; border-radius: 6px; } }
}
.box-body {
  overflow-x: auto;
  overflow-y: hidden;
  padding: 4px 0 16px 0;
  width: 100%;
  
  /* 美化滚动条 */
  &::-webkit-scrollbar { height: 6px; }
  &::-webkit-scrollbar-thumb { background: #cbd5e1; border-radius: 6px; }
  &::-webkit-scrollbar-track { background: transparent; }
  &:hover::-webkit-scrollbar-thumb { background: #94a3b8; }
  
  .luxe-uploader {
    display: block;
    width: 100%;
    
    :deep(.ant-upload-list-picture-card) { 
      display: flex; 
      flex-wrap: nowrap; 
      gap: 14px; 
      width: max-content;
      min-width: 100%;
      padding: 2px;
      margin: 0;
    }
    :deep(.ant-upload-select), :deep(.ant-upload-list-item-container) { 
      width: 96px !important; 
      height: 128px !important; 
      margin: 0 !important; 
      border-radius: 10px !important; 
      flex-shrink: 0;
    }
    :deep(.ant-upload-list-item-thumbnail img) {
      object-fit: cover !important;
    }
    :deep(.ant-upload-list-item::before) {
      border-radius: 10px !important;
    }
    :deep(.ant-upload-list-item-actions .anticon-delete) {
      display: none !important; /* Hide default trash icon since we have a custom red X */
    }
    :deep(.ant-upload-list-item) {
      pointer-events: auto !important;
    }
    /* 自定义上传项容器 + 右上角红色 X 删除按钮 */
    .custom-upload-item {
      position: relative;
      width: 100%;
      height: 100%;
    }
    .delete-x-btn {
      position: absolute;
      top: 4px;
      right: 4px;
      z-index: 10;
      width: 20px;
      height: 20px;
      border-radius: 50%;
      background: #ef4444;
      border: 2px solid #fff;
      box-shadow: 0 2px 6px rgba(0,0,0,0.25);
      color: #fff;
      font-size: 10px;
      font-weight: 700;
      line-height: 16px;
      text-align: center;
      cursor: pointer;
      transition: all 0.15s ease;
      &:hover {
        background: #dc2626;
        transform: scale(1.2);
      }
    }
    .luxe-trigger { 
      width: 96px; 
      height: 128px; 
      display: flex; 
      flex-direction: column; 
      align-items: center; 
      justify-content: center; 
      color: #94a3b8; 
      background: rgba(248,250,252,0.8);
      border-radius: 12px;
      border: 1.5px dashed #cbd5e1;
      transition: all 0.3s cubic-bezier(0.19, 1, 0.22, 1);
      cursor: pointer;
      &:hover { border-color: #3b82f6; color: #3b82f6; background: #eff6ff; transform: scale(1.03); border-style: solid; }
      .txt { font-size: 11px; font-weight: 800; margin-top: 6px; } 
    }
  }
}

/* Footer Area */
.refined-footer-luxe {
  height: 60px; padding: 0 24px; background: rgba(255,255,255,0.95); backdrop-filter: blur(10px); border-top: 1px solid #e8edf3; display: flex; align-items: center; justify-content: space-between;
  .btn-cancel-refined { height: 36px; border: 1px solid #e2e8f0; font-weight: 700; color: #64748b; background: #fff; border-radius: 10px; transition: all 0.2s; &:hover { background: #f8fafc; border-color: #cbd5e1; color: #475569; } }
  .f-right-luxe { display: flex; align-items: center; gap: 16px; .f-sync-status { display: flex; align-items: center; gap: 6px; font-size: 10px; color: #3b82f6; font-weight: 700; animation: pulse-soft 2s infinite; } .btn-draft-refined { height: 38px; border-radius: 10px; font-weight: 700; border: 1.5px solid #dbeafe; color: #3b82f6; background: #fff; transition: all 0.2s; &:hover { background: #eff6ff; border-color: #93c5fd; } } .btn-confirm-refined { height: 38px; border-radius: 10px; font-weight: 800; background: linear-gradient(135deg, #0f172a 0%, #1e293b 100%); border: none; padding: 0 28px; color: white; box-shadow: 0 4px 12px rgba(15,23,42,0.2); transition: all 0.2s; &:hover { transform: translateY(-1px); box-shadow: 0 6px 20px rgba(15,23,42,0.3); } } }
}
@keyframes pulse-soft { 0%, 100% { opacity: 1; } 50% { opacity: 0.6; } }

/* Transitions */
.slide-left-enter-active, .slide-left-leave-active, .slide-right-enter-active, .slide-right-leave-active { transition: all 0.3s cubic-bezier(0.19, 1, 0.22, 1); }
.slide-left-enter-from, .slide-left-leave-to { opacity: 0; transform: translateX(-30px); margin-left: -380px; }
.slide-right-enter-from, .slide-right-leave-to { opacity: 0; transform: translateX(30px); margin-right: -380px; }

.flex-spacer { flex: 1; }
::-webkit-scrollbar { width: 4px; }
::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 10px; }
</style>

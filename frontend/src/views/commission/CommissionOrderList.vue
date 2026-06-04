<template>
  <div class="commission-order-page">
    <!-- 筛选区域 -->
    <a-card :bordered="false" class="filter-card glass-card" :body-style="{ padding: '16px 20px' }">
      <a-form :model="filterForm" layout="vertical" size="middle">
        <a-row :gutter="[20, 16]">
          <a-col :xs="24" :sm="12" :md="8" :lg="3">
            <a-form-item label="交易号">
              <a-input
                v-model:value="filterForm.orderNo"
                placeholder="双击批量搜索"
                allow-clear
                class="premium-input-search"
                @dblclick="openBatchSearchOrder"
              >
                <template #suffix>
                  <a-tooltip title="双击输入框或点击此处批量搜索">
                    <DatabaseOutlined style="color: #1890ff; cursor: pointer; font-size: 14px" @click="openBatchSearchOrder" />
                  </a-tooltip>
                </template>
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="3">
            <a-form-item label="订单号">
              <a-input
                v-model:value="filterForm.shopifyOrderId"
                placeholder="双击批量搜索"
                allow-clear
                class="premium-input-search"
                @dblclick="openBatchSearchShopifyId"
              >
                <template #suffix>
                  <a-tooltip title="双击输入框或点击此处批量搜索">
                    <DatabaseOutlined style="color: #10b981; cursor: pointer; font-size: 14px" @click="openBatchSearchShopifyId" />
                  </a-tooltip>
                </template>
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="关联红人">
              <a-input
                v-model:value="filterForm.influencer"
                placeholder="双击批量搜索"
                allow-clear
                class="premium-input-search"
                @dblclick="openBatchSearchInfluencer"
              >
                <template #suffix>
                  <a-tooltip title="双击输入框或点击此处批量搜索">
                    <DatabaseOutlined style="color: #1890ff; cursor: pointer; font-size: 14px" @click="openBatchSearchInfluencer" />
                  </a-tooltip>
                </template>
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="折扣码">
              <a-input
                v-model:value="filterForm.discountCode"
                placeholder="双击批量搜索"
                allow-clear
                class="premium-input-search"
                @dblclick="openBatchSearchDiscount"
              >
                <template #suffix>
                  <a-tooltip title="双击输入框或点击此处批量搜索">
                    <DatabaseOutlined style="color: #1890ff; cursor: pointer; font-size: 14px" @click="openBatchSearchDiscount" />
                  </a-tooltip>
                </template>
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="佣金率范围">
              <a-input-group compact class="premium-range-group">
                <a-input-number
                  v-model:value="filterForm.commissionRateMin"
                  placeholder="最小"
                  :min="0"
                  :max="100"
                  style="width: 50%; border-radius: 8px 0 0 8px !important;"
                  addon-after="%"
                  class="premium-input"
                />
                <a-input-number
                  v-model:value="filterForm.commissionRateMax"
                  placeholder="最大"
                  :min="0"
                  :max="100"
                  style="width: 50%; border-radius: 0 8px 8px 0 !important; border-left: 0;"
                  addon-after="%"
                  class="premium-input"
                />
              </a-input-group>
            </a-form-item>
          </a-col>

          <template v-if="filterExpanded">
            <a-col :xs="24" :sm="12" :md="8" :lg="4">
              <a-form-item label="SKU">
                <a-input v-model:value="filterForm.sku" placeholder="请输入SKU" class="premium-input" allow-clear />
              </a-form-item>
            </a-col>
            <a-col :xs="24" :sm="12" :md="8" :lg="8">
              <a-form-item label="时间">
                <a-input-group compact class="premium-range-group">
                  <a-select v-model:value="filterForm.timeType" style="width: 120px" placeholder="时间类型" class="premium-select">
                    <a-select-option value="createTime">创建时间</a-select-option>
                    <a-select-option value="paymentTime">付款时间</a-select-option>
                    <a-select-option value="deliveredTime">妥投时间</a-select-option>
                    <a-select-option value="distributeTime">分佣时间</a-select-option>
                  </a-select>
                  <a-range-picker
                    v-model:value="filterForm.timeRange"
                    style="width: calc(100% - 120px)"
                    :placeholder="['开始时间', '结束时间']"
                    format="YYYY-MM-DD"
                    :presets="timeRanges"
                    class="premium-datepicker"
                  />
                </a-input-group>
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

    <!-- 表格区域 -->
    <a-card :bordered="false" class="table-card glass-card" :body-style="{ padding: '0' }">
      <template #title>
        <div class="table-actions-toolbar">
          <div class="table-title">分佣订单列表</div>
          <div class="toolbar-btns">
            <a-button class="premium-btn-small" v-permission="'commission.order.export'" @click="handleExport">
              <template #icon><ExportOutlined /></template>
              导出
            </a-button>
          </div>
        </div>
      </template>

      <template v-if="displayData.length <= 500">
        <a-table
          :columns="columns"
          :data-source="displayData"
          :row-key="(record: any) => record.key ?? record.orderNo ?? record.longOrderNo"
          :pagination="false"
          :loading="{ spinning: loading, indicator: LoadingIcon }"
          :scroll="{ y: 'calc(100vh - 380px)', x: 'max-content' }"
          class="premium-table"
          @change="handleTableChange"
        >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'orderNo'">
            <div>
              <div style="font-weight: bold; display: flex; align-items: center; gap: 8px; flex-wrap: wrap; word-break: break-all; white-space: normal;">
                <span>{{ record.shortOrderNo }}</span>
                <a-tag v-if="record.isSplit" color="orange" size="small">拆单</a-tag>
              </div>
              <div style="font-size: 12px; color: #999; word-break: break-all; white-space: normal;">{{ record.longOrderNo }}</div>
            </div>
          </template>
          <template v-else-if="column.key === 'packageNo'">
            <div style="display: flex; align-items: center; gap: 8px; flex-wrap: wrap;">
              <a-tag v-if="record.packageNo" color="blue" size="small">{{ record.packageNo }}</a-tag>
              <span v-else style="color: #999;">-</span>
            </div>
          </template>
          <template v-else-if="column.key === 'influencer'">
            <div>
              <div style="font-weight: 500;">{{ record.influencerName }}</div>
              <div style="font-size: 12px; color: #999;">ID: {{ record.influencerId }}</div>
            </div>
          </template>
          <template v-else-if="column.key === 'products'">
            <div v-for="(product, idx) in record.products" :key="idx" style="margin-bottom: 4px;">
              <span>{{ product.sku }}</span>
              <span style="margin: 0 8px;">×{{ product.quantity }}</span>
              <span style="color: #666;">${{ product.price.toFixed(2) }}</span>
              <template v-if="product.status && product.status !== '正常'">
                <a-tag
                  v-if="/退/.test(product.status)"
                  color="red"
                  size="small"
                  style="margin-left: 8px;"
                >退</a-tag>
                <a-tag
                  v-else-if="/换/.test(product.status)"
                  color="green"
                  size="small"
                  style="margin-left: 8px;"
                >换</a-tag>
                <a-tag
                  v-else-if="/删|取消/.test(product.status)"
                  size="small"
                  style="margin-left: 8px;"
                >删</a-tag>
              <a-tag 
                  v-else
                size="small" 
                style="margin-left: 8px;"
                >{{ product.status }}</a-tag>
              </template>
            </div>
          </template>
          <template v-else-if="column.key === 'orderAmount'">
            <span style="font-weight: 500;">${{ record.orderAmount }}</span>
          </template>
          <template v-else-if="column.key === 'discountCode'">
            <span>{{ record.discountCode }}</span>
          </template>
          <template v-else-if="column.key === 'commissionType'">
            <a-tag :color="record.commissionType === 'discount' ? 'purple' : 'blue'">
              {{ record.commissionType === 'discount' ? '折扣' : '红人' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'commissionRate'">
            <span style="color: #1890ff; font-weight: 500;">{{ record.commissionRate }}%</span>
          </template>
          <template v-else-if="column.key === 'commissionAmount'">
            <span style="font-weight: 600; color: #52c41a;">${{ record.commissionAmount }}</span>
          </template>
          <template v-else-if="column.key === 'distributeTime'">
            <div v-if="record.timeInfo?.distributeTime" style="color: #52c41a; font-weight: 500;">
              {{ record.timeInfo.distributeTime }}
            </div>
            <span v-else style="color: #999;">-</span>
          </template>
          <template v-else-if="column.key === 'time'">
            <div style="font-size: 12px; line-height: 1.8;">
              <div v-if="record.timeInfo?.createTime">创建时间：{{ record.timeInfo.createTime }}</div>
              <div v-if="record.timeInfo?.paymentTime">付款时间：{{ record.timeInfo.paymentTime }}</div>
              <div v-if="record.timeInfo?.deliveredTime">妥投时间：{{ record.timeInfo.deliveredTime }}</div>
              <div v-if="record.timeInfo?.distributeTime" style="color: #52c41a; font-weight: 500;">分佣时间：{{ record.timeInfo.distributeTime }}</div>
            </div>
          </template>
          <template v-else-if="column.key === 'action'">
            <div class="action-btns-wrapper">
              <a-button type="link" size="small" @click="handleViewDetail(record)" class="action-link primary" v-permission="'commission.order.detail'">详情</a-button>
            </div>
          </template>
        </template>
        </a-table>
      </template>
      <template v-else>
        <div class="virtual-header">
          <div class="hcell">订单号</div>
          <div class="hcell">关联红人</div>
          <div class="hcell">订单金额</div>
          <div class="hcell">订单商品</div>
          <div class="hcell">订单折扣码</div>
          <div class="hcell">佣金类型</div>
          <div class="hcell">佣金率</div>
          <div class="hcell">已分佣金</div>
          <div class="hcell">时间</div>
        </div>
        <VirtualList
          :data-key="'key'"
          :data-sources="displayData"
          :keeps="20"
          :estimate-size="64"
          :data-component="VirtualCommissionOrderRow"
          :extra-props="{ onDetail: handleViewDetail }"
          class="virtual-list"
        />
      </template>

      <div class="pagination-footer">
        <div class="footer-left">
          <span class="info-item">当前内容数量 <span class="count-value">{{ pagination.total }}</span></span>
        </div>
        <div class="footer-right">
          <a-pagination
            v-model:current="pagination.current"
            v-model:pageSize="pagination.pageSize"
            :total="pagination.total"
            :show-size-changer="true"
            :show-quick-jumper="true"
            @change="handleTableChange"
          />
        </div>
      </div>
    </a-card>

    <!-- 订单详情Modal -->
    <OrderDetailModal
      v-model:open="detailModalVisible"
      :order-data="currentOrder"
      order-type="commission"
      @view-related-order="handleViewRelatedOrder"
    />

    <InfluencerExportModal
      v-model:open="exportModalVisible"
      :export-fields="orderExportFields"
      :selected-count="0"
      :current-user-id="String(userStore.userInfo?.id || '')"
      page-type="commission-order"
      @export="handleExportFromModal"
    />
    <!-- 批量搜索交易号弹窗 -->
    <a-modal
      v-model:open="batchSearchOrderVisible"
      :title="null"
      :footer="null"
      class="premium-modal influencer-create-modal"
      :closable="false"
      width="500px"
      centered
    >
      <div class="ic-modal-header glass-header">
        <div class="ic-header-left">
          <div class="ic-header-icon-wrapper" style="background: linear-gradient(135deg, #1890ff 0%, #3b82f6 100%);">
            <DatabaseOutlined />
          </div>
          <div class="ic-header-text">
            <div class="ic-header-title">批量搜索交易号</div>
            <div class="ic-header-subtitle">请输入交易号（如 #1001），多个请用换行分隔</div>
          </div>
        </div>
        <a-button type="text" class="close-btn" @click="batchSearchOrderVisible = false">
          <CloseOutlined style="font-size: 16px; color: #94a3b8" />
        </a-button>
      </div>
      <div class="ic-modal-body" style="background: #fff; padding: 24px;">
        <a-textarea
          v-model:value="batchSearchOrderValue"
          placeholder="例如：&#10;#1234&#10;#5678"
          :rows="10"
          class="premium-textarea"
          style="margin-bottom: 0;"
        />
      </div>
      <div class="ic-modal-footer">
        <a-space size="middle">
          <a-button @click="batchSearchOrderVisible = false" class="premium-btn">取消</a-button>
          <a-button type="primary" @click="handleBatchSearchOrder" class="premium-btn primary-gradient">开始搜索</a-button>
        </a-space>
      </div>
    </a-modal>

    <!-- 批量搜索订单号 (GID) 弹窗 -->
    <a-modal
      v-model:open="batchSearchShopifyIdVisible"
      :title="null"
      :footer="null"
      class="premium-modal influencer-create-modal"
      :closable="false"
      width="500px"
      centered
    >
      <div class="ic-modal-header glass-header">
        <div class="ic-header-left">
          <div class="ic-header-icon-wrapper" style="background: linear-gradient(135deg, #10b981 0%, #059669 100%);">
            <DatabaseOutlined />
          </div>
          <div class="ic-header-text">
            <div class="ic-header-title">批量搜索订单号</div>
            <div class="ic-header-subtitle">请输入订单号（GID），多个请用换行分隔</div>
          </div>
        </div>
        <a-button type="text" class="close-btn" @click="batchSearchShopifyIdVisible = false">
          <CloseOutlined style="font-size: 16px; color: #94a3b8" />
        </a-button>
      </div>
      <div class="ic-modal-body" style="background: #fff; padding: 24px;">
        <a-textarea
          v-model:value="batchSearchShopifyIdValue"
          placeholder="例如：&#10;6934080389369&#10;6934080389370"
          :rows="10"
          class="premium-textarea"
          style="margin-bottom: 0;"
        />
      </div>
      <div class="ic-modal-footer">
        <a-space size="middle">
          <a-button @click="batchSearchShopifyIdVisible = false" class="premium-btn">取消</a-button>
          <a-button type="primary" @click="handleBatchSearchShopifyId" class="premium-btn primary-gradient">开始搜索</a-button>
        </a-space>
      </div>
    </a-modal>

    <!-- 批量搜索折扣码弹窗 -->
    <a-modal
      v-model:open="batchSearchDiscountVisible"
      :title="null"
      :footer="null"
      class="premium-modal influencer-create-modal"
      :closable="false"
      width="500px"
      centered
    >
      <div class="ic-modal-header glass-header">
        <div class="ic-header-left">
          <div class="ic-header-icon-wrapper" style="background: linear-gradient(135deg, #1890ff 0%, #3b82f6 100%);">
            <DatabaseOutlined />
          </div>
          <div class="ic-header-text">
            <div class="ic-header-title">批量搜索折扣码</div>
            <div class="ic-header-subtitle">请输入折扣码，多个请用换行分隔</div>
          </div>
        </div>
        <a-button type="text" class="close-btn" @click="batchSearchDiscountVisible = false">
          <CloseOutlined style="font-size: 16px; color: #94a3b8" />
        </a-button>
      </div>
      <div class="ic-modal-body" style="background: #fff; padding: 24px;">
        <a-textarea
          v-model:value="batchSearchDiscountValue"
          placeholder="例如：&#10;SALE10&#10;SUMMER20"
          :rows="10"
          class="premium-textarea"
          style="margin-bottom: 0;"
        />
      </div>
      <div class="ic-modal-footer">
        <a-space size="middle">
          <a-button @click="batchSearchDiscountVisible = false" class="premium-btn">取消</a-button>
          <a-button type="primary" @click="handleBatchSearchDiscount" class="premium-btn primary-gradient">开始搜索</a-button>
        </a-space>
      </div>
    </a-modal>

    <!-- 批量搜索红人弹窗 -->
    <a-modal
      v-model:open="batchSearchInfluencerVisible"
      :title="null"
      :footer="null"
      class="premium-modal influencer-create-modal"
      :closable="false"
      width="500px"
      centered
    >
      <div class="ic-modal-header glass-header">
        <div class="ic-header-left">
          <div class="ic-header-icon-wrapper" style="background: linear-gradient(135deg, #1890ff 0%, #3b82f6 100%);">
            <DatabaseOutlined />
          </div>
          <div class="ic-header-text">
            <div class="ic-header-title">批量搜索关联红人</div>
            <div class="ic-header-subtitle">请输入红人名称，多个请用换行分隔</div>
          </div>
        </div>
        <a-button type="text" class="close-btn" @click="batchSearchInfluencerVisible = false">
          <CloseOutlined style="font-size: 16px; color: #94a3b8" />
        </a-button>
      </div>
      <div class="ic-modal-body" style="background: #fff; padding: 24px;">
        <a-textarea
          v-model:value="batchSearchInfluencerValue"
          placeholder="例如：&#10;Natalie Wilson&#10;Emma Lee"
          :rows="10"
          class="premium-textarea"
          style="margin-bottom: 0;"
        />
      </div>
      <div class="ic-modal-footer">
        <a-space size="middle">
          <a-button @click="batchSearchInfluencerVisible = false" class="premium-btn">取消</a-button>
          <a-button type="primary" @click="handleBatchSearchInfluencer" class="premium-btn primary-gradient">开始搜索</a-button>
        </a-space>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, h, onMounted } from 'vue';
import { DownOutlined, UpOutlined, LoadingOutlined, ExportOutlined, DatabaseOutlined, CloseOutlined } from '@ant-design/icons-vue';

defineOptions({
  name: 'CommissionOrderList'
});
import { message } from 'ant-design-vue';
import type { TableColumnsType } from 'ant-design-vue';
import dayjs from 'dayjs';
import OrderDetailModal from '@/components/order/OrderDetailModal.vue';
import VirtualList from 'vue3-virtual-scroll-list';
import VirtualCommissionOrderRow from '@/components/common/VirtualCommissionOrderRow.vue';
import type { CommissionOrderRow } from '@/types/order';
import { filterByTimeRange } from '@/composables/useTimeFilter';
import { influencerService } from '@/services/influencerService';
import { fetchAllPages } from '@/utils/dataHelper';
import { createExportTask } from '@/utils/exportTaskHelper';
import InfluencerExportModal from '@/components/influencer/InfluencerExportModal.vue';
import { useUserStore } from '@/stores/user';

const userStore = useUserStore();

// 使用统一的 influencerService (已含拦截器，自动解包 response.data)
const influencerHttp = influencerService;

const filterExpanded = ref(false);
const loading = ref(false);
const detailModalVisible = ref(false);
const exportModalVisible = ref(false);
const currentOrder = ref<any>(null);

// Custom loading indicator for table
const LoadingIcon = h(LoadingOutlined, { style: { fontSize: '24px' }, spin: true });

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 50,
  showSizeChanger: true,
  showQuickJumper: true,
});

const filterForm = reactive({
  orderNo: '',
  shopifyOrderId: '',
  influencer: '' as string,
  discountCode: '',
  sku: '',
  commissionRateMin: undefined as number | undefined,
  commissionRateMax: undefined as number | undefined,
  timeType: undefined as string | undefined,
  timeRange: undefined as [string, string] | undefined,
});

const influencers = ref<string[]>([]);

const timeRanges = [
  { label: '最近7天', value: [dayjs().subtract(6, 'day'), dayjs()] },
  { label: '最近14天', value: [dayjs().subtract(13, 'day'), dayjs()] },
  { label: '最近30天', value: [dayjs().subtract(29, 'day'), dayjs()] },
  { label: '最近90天', value: [dayjs().subtract(89, 'day'), dayjs()] },
];

const filterOption = (input: string, option: any) => {
  return option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

const columns: TableColumnsType = [
  {
    title: '交易号 / 订单号',
    key: 'orderNo',
    width: 160,
    fixed: 'left',
  },
  {
    title: '包裹号',
    key: 'packageNo',
    width: 120,
  },
  {
    title: '关联红人',
    key: 'influencer',
    width: 120,
  },
  {
    title: '订单商品',
    key: 'products',
    width: 180,
  },
  {
    title: '订单折扣码',
    key: 'discountCode',
    width: 100,
  },
  {
    title: '佣金类型',
    key: 'commissionType',
    width: 80,
  },
  {
    title: '订单金额',
    key: 'orderAmount',
    width: 90,
    sorter: (a: any, b: any) => parseFloat(a.orderAmount || '0') - parseFloat(b.orderAmount || '0'),
  },
  {
    title: '佣金率',
    key: 'commissionRate',
    width: 90,
    sorter: (a: any, b: any) => parseFloat(a.commissionRate || '0') - parseFloat(b.commissionRate || '0'),
  },
  {
    title: '已分佣金',
    key: 'commissionAmount',
    width: 90,
    sorter: (a: any, b: any) => parseFloat(a.commissionAmount || '0') - parseFloat(b.commissionAmount || '0'),
  },
  {
    title: '时间',
    key: 'time',
    width: 160,
  },
];

// 分佣订单数据 (从API获取)
const data = ref<CommissionOrderRow[]>([]);

const filteredData = computed<CommissionOrderRow[]>(() => {
  let result: CommissionOrderRow[] = [...data.value];
  
  if (filterForm.orderNo || filterForm.shopifyOrderId) {
    const combinedOrderNo = [filterForm.orderNo, filterForm.shopifyOrderId].filter(Boolean).join(',');
    const keywords = combinedOrderNo.split(',').map(s => s.trim().toLowerCase()).filter(Boolean);
    result = result.filter(item => 
      keywords.some(kw =>
        (item.shortOrderNo || '').toLowerCase().includes(kw) ||
        (item.longOrderNo || '').toLowerCase().includes(kw)
      )
    );
  }
  if (filterForm.influencer) {
    const keywords = filterForm.influencer.split(',').map((s: string) => s.trim().toLowerCase()).filter(Boolean);
    result = result.filter(item => 
      keywords.some(kw =>
        (item.influencerName || '').toLowerCase().includes(kw) ||
        (item.influencerId || '').toLowerCase().includes(kw)
      )
    );
  }
  if (filterForm.discountCode) {
    const keywords = filterForm.discountCode.split(',').map(s => s.trim().toLowerCase()).filter(Boolean);
    result = result.filter(item => 
      keywords.some(kw => (item.discountCode || '').toLowerCase().includes(kw))
    );
  }
  if (filterForm.sku) {
    result = result.filter(item => 
      item.products.some((p: any) => p.sku.toLowerCase().includes(filterForm.sku.toLowerCase()))
    );
  }
  if (filterForm.commissionRateMin !== undefined) {
    result = result.filter(item => parseFloat(item.commissionRate) >= filterForm.commissionRateMin!);
  }
  if (filterForm.commissionRateMax !== undefined) {
    result = result.filter(item => parseFloat(item.commissionRate) <= filterForm.commissionRateMax!);
  }
  
  if (filterForm.timeType && filterForm.timeRange && filterForm.timeRange.length === 2) {
    const getVal = (it: CommissionOrderRow) => {
      const info = it.timeInfo || {};
      switch (filterForm.timeType) {
        case 'createTime': return info.createTime;
        case 'paymentTime': return info.paymentTime;
        case 'deliveredTime': return info.deliveredTime;
        case 'distributeTime': return info.distributeTime;
        default: return undefined;
      }
    };
    result = filterByTimeRange(result, getVal, filterForm.timeRange as any);
  }
  
  return result;
});

const displayData = ref<CommissionOrderRow[]>([...data.value]);
const applyDisplayData = (src: CommissionOrderRow[]) => {
  if (src.length > 1000) {
    displayData.value = [];
    let i = 0;
    const batch = 200;
    const load = () => {
      displayData.value.push(...src.slice(i, i + batch));
      i += batch;
      if (i < src.length) {
        setTimeout(load, 0);
      }
    };
    load();
  } else {
    displayData.value = [...src];
  }
  // Don't override pagination.total here - it's set by the API response
};

watch(filteredData, (val: CommissionOrderRow[]) => {
  applyDisplayData(val);
}, { immediate: true });

const handleTableChange = (paginationOrPage?: any, filtersOrPageSize?: any, sorter?: any) => {
  if (typeof paginationOrPage === 'number') {
    pagination.current = paginationOrPage;
    pagination.pageSize = filtersOrPageSize || 10;
  } else if (paginationOrPage && typeof paginationOrPage === 'object') {
    pagination.current = paginationOrPage.current ?? pagination.current;
    pagination.pageSize = paginationOrPage.pageSize ?? pagination.pageSize;
  }
  fetchOrders();
};

const handleViewDetail = (record: CommissionOrderRow) => {
  currentOrder.value = record;
  detailModalVisible.value = true;
};

const handleViewRelatedOrder = (order: CommissionOrderRow) => {
  const foundOrder = data.value.find((item) => 
    item.orderNo === order.orderNo || 
    item.shortOrderNo === order.shortOrderNo ||
    item.shortOrderNo === order.orderNo
  );
  
  if (foundOrder) {
    currentOrder.value = foundOrder;
    detailModalVisible.value = true;
  } else {
    currentOrder.value = order;
    detailModalVisible.value = true;
  }
};

const handleResetFilter = () => {
  filterForm.orderNo = '';
  filterForm.shopifyOrderId = '';
  filterForm.influencer = '';
  filterForm.discountCode = '';
  filterForm.sku = '';
  filterForm.commissionRateMin = undefined;
  filterForm.commissionRateMax = undefined;
  filterForm.timeType = undefined;
  filterForm.timeRange = undefined;
  fetchOrders();
};

// 批量搜索订单号
const batchSearchOrderVisible = ref(false);
const batchSearchOrderValue = ref('');

const openBatchSearchOrder = () => {
  batchSearchOrderValue.value = filterForm.orderNo?.replace(/,/g, '\n') || '';
  batchSearchOrderVisible.value = true;
};

const handleBatchSearchOrder = () => {
  const values = batchSearchOrderValue.value.split(/[\n,]+/).map(v => v.trim()).filter(Boolean);
  filterForm.orderNo = values.join(',');
  batchSearchOrderVisible.value = false;
  handleFilter();
};

// 批量搜索订单号 (GID)
const batchSearchShopifyIdVisible = ref(false);
const batchSearchShopifyIdValue = ref('');

const openBatchSearchShopifyId = () => {
  batchSearchShopifyIdValue.value = filterForm.shopifyOrderId?.replace(/,/g, '\n') || '';
  batchSearchShopifyIdVisible.value = true;
};

const handleBatchSearchShopifyId = () => {
  const values = batchSearchShopifyIdValue.value.split(/[\n,]+/).map(v => v.trim()).filter(Boolean);
  filterForm.shopifyOrderId = values.join(',');
  batchSearchShopifyIdVisible.value = false;
  handleFilter();
};

// 批量搜索折扣码
const batchSearchDiscountVisible = ref(false);
const batchSearchDiscountValue = ref('');

const openBatchSearchDiscount = () => {
  batchSearchDiscountValue.value = filterForm.discountCode?.replace(/,/g, '\n') || '';
  batchSearchDiscountVisible.value = true;
};

const handleBatchSearchDiscount = () => {
  const values = batchSearchDiscountValue.value.split(/[\n,]+/).map(v => v.trim()).filter(Boolean);
  filterForm.discountCode = values.join(',');
  batchSearchDiscountVisible.value = false;
  handleFilter();
};

// 批量搜索红人
const batchSearchInfluencerVisible = ref(false);
const batchSearchInfluencerValue = ref('');

const openBatchSearchInfluencer = () => {
  batchSearchInfluencerValue.value = filterForm.influencer?.replace(/,/g, '\n') || '';
  batchSearchInfluencerVisible.value = true;
};

const handleBatchSearchInfluencer = () => {
  const values = batchSearchInfluencerValue.value.split(/[\n,]+/).map(v => v.trim()).filter(Boolean);
  filterForm.influencer = values.join(',');
  batchSearchInfluencerVisible.value = false;
  handleFilter();
};

const handleFilter = () => {
  pagination.current = 1;
  fetchOrders();
};

const orderExportFields = [
  { key: 'orderNo', title: '订单号' },
  { key: 'longOrderNo', title: '长订单号' },
  { key: 'packageNo', title: '包裹号' },
  { key: 'influencerName', title: '红人昵称' },
  { key: 'influencerId', title: '红人ID' },
  { key: 'products', title: '商品信息' },
  { key: 'discountCode', title: '折扣码' },
  { key: 'commissionType', title: '佣金类型' },
  { key: 'orderAmount', title: '订单金额' },
  { key: 'productAmount', title: '参与分佣金额' },
  { key: 'commissionRate', title: '佣金率(%)' },
  { key: 'commissionAmount', title: '佣金金额' },
  { key: 'createTime', title: '创建时间' },
  { key: 'paymentTime', title: '付款时间' },
  { key: 'deliveredTime', title: '妥投时间' },
  { key: 'distributeTime', title: '分佣时间' },
];

const handleExport = () => {
  exportModalVisible.value = true;
};

const handleExportFromModal = async (payload: { scope: 'all' | 'selected'; fields: string[]; columns: any[]; templateId?: string; templateName?: string }) => {
  const { scope, templateId, templateName } = payload;
  let exportData: any[] = [];

  if (scope === 'selected') {
    // Note: Commission Order doesn't have row selection currently, so this might always be empty unless we add selection.
    // Assuming 'selected' might be disabled or we fall back to displayed data if we implement selection later.
    // For now, if no selection, warn user.
    message.warning('当前列表不支持选中导出，请使用"导出全部"');
    return;
  } else {
    // 导出全部 - 跨页抓取
    const hideLoading = message.loading('正在获取全量导出数据...', 0);
    try {
      const params: Record<string, any> = {};
      if (filterForm.influencer) params.influencerName = filterForm.influencer;
      if (filterForm.orderNo) params.orderNo = filterForm.orderNo; // Assuming API supports this
      // Note: The backend API filtering seems to be limited based on fetchOrders implementation, 
      // but let's pass what we can. 
      // Actually, fetchOrders only passes 'influencerId'. 
      // FRONTEND filtering is used for other fields in filteredData computed property.
      // IF we want to export EXACTLY what is seen, we should export `filteredData` if it's small enough,
      // OR we need backend to support all filters.
      // Given the requirement "Implementing More Export Features" typically implies backend fetch,
      // but if frontend filtering is dominant, we might need a different approach.
      // However, for "Export All" across pages, we usually expect backend to handle it.
      // If backend doesn't support filter, "Export All" will export EVERYTHING for that influencer.
      
      const allRawData = await fetchAllPages(
        (p: any) => influencerHttp.getCommissionOrders({ ...params, page: p.page, size: p.size }).then((res: any) => ({
          data: res.data
        })),
        {}, 
        { pageSize: 100 }
      );

      // We need to apply the SAME frontend filtering if we want consistency, 
      // BUT `fetchAllPages` fetches raw data.
      // If the User has applied frontend filters (e.g. SKU, Time), the backend export might include ignored items.
      // To strictly follow "Export All (filtered)", we should ideally filter `allRawData` here.
      
      let processedData = allRawData.map((order: any, idx: number) => convertToRow(order, idx));
      
      // Apply frontend filters to the full dataset
       if (filterForm.orderNo) {
        const q = filterForm.orderNo.toLowerCase();
        processedData = processedData.filter(item => 
          (item.shortOrderNo || '').toLowerCase().includes(q) ||
          (item.longOrderNo || '').toLowerCase().includes(q)
        );
      }
      if (filterForm.discountCode) {
        const q = filterForm.discountCode.toLowerCase();
        processedData = processedData.filter(item => (item.discountCode || '').toLowerCase().includes(q));
      }
      if (filterForm.sku) {
        processedData = processedData.filter(item => 
          item.products.some((p: any) => p.sku.toLowerCase().includes(filterForm.sku.toLowerCase()))
        );
      }
      if (filterForm.commissionRateMin !== undefined) {
        processedData = processedData.filter(item => parseFloat(item.commissionRate) >= filterForm.commissionRateMin!);
      }
      if (filterForm.commissionRateMax !== undefined) {
        processedData = processedData.filter(item => parseFloat(item.commissionRate) <= filterForm.commissionRateMax!);
      }
      if (filterForm.timeType && filterForm.timeRange && filterForm.timeRange.length === 2) {
         const getVal = (it: CommissionOrderRow) => {
          const info = it.timeInfo || {};
          switch (filterForm.timeType) {
            case 'createTime': return info.createTime;
            case 'paymentTime': return info.paymentTime;
            case 'deliveredTime': return info.deliveredTime;
            case 'distributeTime': return info.distributeTime;
            default: return undefined;
          }
        };
        processedData = filterByTimeRange(processedData, getVal, filterForm.timeRange as any);
      }

      exportData = processedData.map(item => ({
        ...item, // include all fields
        products: item.products.map((p: any) => `${p.sku} x${p.quantity}`).join('\n'),
        createTime: item.timeInfo?.createTime,
        paymentTime: item.timeInfo?.paymentTime,
        deliveredTime: item.timeInfo?.deliveredTime,
        distributeTime: item.timeInfo?.distributeTime,
        commissionType: item.commissionType === 'discount' ? '折扣' : '红人',
      }));

    } catch (e) {
      console.error('导出获取数据失败:', e);
      message.error('获取全量数据失败');
      hideLoading();
      return;
    }
    hideLoading();
  }

  if (exportData.length === 0) {
    message.warning('无可供导出的数据');
    return;
  }

  try {
    await createExportTask({
      data: exportData,
      columns: orderExportFields,
      filename: `分佣订单_${dayjs().format('YYYYMMDD')}`,
      templateId,
      templateName,
      pageType: 'commission-order'
    });
    message.success('导出任务已创建');
    exportModalVisible.value = false;
  } catch (e) {
    message.error('导出任务创建失败');
  }
};

// 获取分佣订单列表
const fetchOrders = async () => {
  loading.value = true;
  try {
    const params: Record<string, any> = {
      page: pagination.current - 1,
      size: pagination.pageSize
    };
    if (filterForm.influencer) {
      params.influencerName = filterForm.influencer;
    }
    if (filterForm.orderNo) {
      params.orderNo = filterForm.orderNo;
    }
    if (filterForm.shopifyOrderId) {
      params.shopifyOrderId = filterForm.shopifyOrderId;
    }
    if (filterForm.discountCode) {
      params.discountCode = filterForm.discountCode;
    }
    if (filterForm.sku) {
      params.sku = filterForm.sku;
    }
    if (filterForm.commissionRateMin !== undefined) {
      params.commissionRateMin = filterForm.commissionRateMin;
    }
    if (filterForm.commissionRateMax !== undefined) {
      params.commissionRateMax = filterForm.commissionRateMax;
    }
    if (filterForm.timeType && filterForm.timeRange && filterForm.timeRange.length === 2) {
      params.timeType = filterForm.timeType;
      params.startTime = dayjs(filterForm.timeRange[0]).format('YYYY-MM-DD');
      params.endTime = dayjs(filterForm.timeRange[1]).format('YYYY-MM-DD');
    }
    
    const resData = await influencerHttp.getCommissionOrders(params);
    if (resData && resData.success && resData.data) {
      // 转换后端数据为前端格式
      const orders = resData.data.content || [];
      data.value = orders.map((order: any, idx: number) => convertToRow(order, idx));
      pagination.total = resData.data.totalElements || 0;

      // 提取红人名称用于筛选下拉
      if (influencers.value.length === 0) {
        const uniqueInfluencers = new Set<string>();
        orders.forEach((order: any) => {
          if (order.influencerName) {
            uniqueInfluencers.add(order.influencerName);
          }
        });
        influencers.value = Array.from(uniqueInfluencers).sort();
      }
    }
  } catch (error: any) {
    if (error?.code === 'ERR_CANCELED') return;
    console.error('获取分佣订单失败:', error);
    message.error('获取分佣订单失败');
  } finally {
    loading.value = false;
  }
};

// 转换后端数据为前端行格式
const convertToRow = (order: any, idx: number): CommissionOrderRow => {
  // 判断佣金类型: 默认使用红人佣金率，只有明确使用折扣码佣金率时才显示"折扣"
  let commissionType: 'influencer' | 'discount' = 'influencer';
  if (order.rateSource === 'discount' || order.useDiscountRate === true) {
    commissionType = 'discount';
  }
  
  // 解析商品信息
  let products: any[] = [];
  if (order.itemsSummary) {
    try {
      const items = JSON.parse(order.itemsSummary);
      if (Array.isArray(items)) {
        products = items.map((item: any) => ({
          sku: item.sku || item.variant_title || '',
          quantity: item.quantity || 1,
          price: item.price || 0,
          status: item.status || ''
        }));
      }
    } catch (e) {
      console.warn('解析商品信息失败:', e);
    }
  }
  
  return {
    key: order.id || idx,
    orderNo: order.orderName || '',
    shortOrderNo: order.orderName || '',
    longOrderNo: `CONVERSION-${order.conversionOrderId || ''}`,
    packageNo: order.trackingNumber || '',  // 包裹号
    influencerName: order.influencerName || '',
    influencerId: String(order.influencerId || ''),
    orderAmount: (order.orderTotal || 0).toFixed(2),
    productAmount: (order.commissionableAmount || 0).toFixed(2),
    shippingAmount: (order.shippingAmount || 0).toFixed(2),
    refundAmount: (order.refundAmount || 0).toFixed(2),
    products: products,
    discountCode: order.discountCode || '',
    commissionRate: (order.commissionRate || 0).toFixed(2),
    commissionType: commissionType,
    commissionAmount: (order.commissionAmount || 0).toFixed(2),
    timeInfo: {
      createTime: order.createdAt ? dayjs(order.createdAt).format('YYYY-MM-DD HH:mm:ss') : '',
      paymentTime: '',
      deliveredTime: order.deliveredAt ? dayjs(order.deliveredAt).format('YYYY-MM-DD HH:mm:ss') : '',
      distributeTime: order.settledAt ? dayjs(order.settledAt).format('YYYY-MM-DD HH:mm:ss') : ''
    },
    settlementStatus: order.settlementStatus || 'pending'
  };
};

onMounted(() => {
  fetchOrders();
});

</script>

<style lang="scss" scoped>
.commission-order-page {
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
    
    .table-title {
        font-size: 16px;
        font-weight: 700;
        color: #1e293b;
    }
  }
  
  /* Shared Table Styles */
  .premium-table {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    min-height: 500px;
    /* Styles shared with other components */
    :deep(.ant-table-thead > tr > th) {
        background: #f8fafc;
        color: #64748b;
        font-weight: 700;
        font-size: 13px;
        border-bottom: 2px solid #f1f5f9;
        padding: 10px 8px;
        &.ant-table-selection-column {
            padding: 0 8px !important;
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

  /* Compact Pagination Footer */
  .pagination-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 6px 16px;
    background: #ffffff;
    border-top: 1px solid rgba(0, 0, 0, 0.04);
    z-index: 10;
    flex-shrink: 0;
    
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
      }
    }
  }
  
  /* Premium Inputs */
  .premium-input, .premium-select :deep(.ant-select-selector), .premium-datepicker, .premium-input-search {
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
    
    &:focus, &.ant-select-focused .ant-select-selector {
        border-color: #1890ff !important;
        box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.06) !important;
        background: #ffffff !important;
    }
  }
  
  :deep(.ant-select-selection-item), :deep(.ant-select-placeholder) {
    line-height: 30px !important;
    font-size: 13px !important;
  }

  .premium-range-group {
      display: flex;
      height: 32px;
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
      :deep(.ant-input-number) {
          height: 32px; border-color: #e2e8f0;
          input { height: 32px; }
      }
  }
  
  /* Global Premium Scrollbar */
  ::-webkit-scrollbar {
    width: 6px;
    height: 6px;
  }
  ::-webkit-scrollbar-thumb {
    background: #e2e8f0;
    border-radius: 10px;
    transition: background 0.3s;
    &:hover { background: #cbd5e1; }
  }
  ::-webkit-scrollbar-track {
    background: rgba(0, 0, 0, 0.02);
    border-radius: 10px;
  }
  
  /* Premium Buttons */
  .premium-btn, .premium-btn-small {
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
  }

  .action-btns-wrapper {
    display: flex;
    align-items: center;
    gap: 8px;
    
    .action-link {
      font-weight: 600;
      font-size: 13px;
      color: #64748b;
      padding: 0 4px;
      
      &:hover { color: #2563eb; background: rgba(37, 99, 235, 0.05); border-radius: 4px; }
      &.primary { color: #2563eb; }
    }
  }

  .virtual-header {
      display: flex; background: #f8fafc; border-bottom: 2px solid #e2e8f0;
      padding: 0; min-height: 48px;
      .hcell {
          padding: 10px 8px; font-weight: 700; color: #64748b; font-size: 13px;
          flex: 1; display: flex; align-items: center;
          &:first-child { padding-left: 16px; min-width: 160px; }
          &:last-child { min-width: 80px; justify-content: center;}
      }
  }
  .virtual-list {
      flex: 1; overflow-y: auto;
  }
}
</style>

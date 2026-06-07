<template>
  <div class="conversion-order-page">
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
            <a-form-item label="订单状态">
              <a-select v-model:value="filterForm.status" placeholder="请选择状态" allow-clear class="premium-select">
                <a-select-option value="pending_payment">待付款</a-select-option>
                <a-select-option value="pending_ship">待揽收</a-select-option>
                <a-select-option value="shipped">已发货</a-select-option>
                <a-select-option value="delivered">已妥投</a-select-option>
                <a-select-option value="cancelling">取消中</a-select-option>
                <a-select-option value="cancelled">已取消</a-select-option>
                <a-select-option value="exception">异常</a-select-option>
              </a-select>
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
            <a-form-item label="佣金状态">
              <a-select v-model:value="filterForm.commissionStatus" placeholder="请选择佣金状态" allow-clear class="premium-select">
                <a-select-option value="unsettled">未结算</a-select-option>
                <a-select-option value="settled">已结算</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <template v-if="filterExpanded">
            <a-col :xs="24" :sm="12" :md="8" :lg="4">
              <a-form-item label="国家">
                <a-select
                  v-model:value="filterForm.country"
                  placeholder="多选国家"
                  mode="multiple"
                  allow-clear
                  class="premium-select"
                  :max-tag-count="1"
                >
                  <a-select-option value="US">美国 (US)</a-select-option>
                  <a-select-option value="GB">英国 (GB)</a-select-option>
                  <a-select-option value="DE">德国 (DE)</a-select-option>
                  <a-select-option value="FR">法国 (FR)</a-select-option>
                  <a-select-option value="CA">加拿大 (CA)</a-select-option>
                  <a-select-option value="AU">澳大利亚 (AU)</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :xs="24" :sm="12" :md="8" :lg="4">
              <a-form-item label="物流单号">
                <a-input
                  v-model:value="filterForm.trackingNo"
                  placeholder="双击批量搜索"
                  allow-clear
                  class="premium-input-search"
                  @dblclick="openBatchSearchTracking"
                >
                  <template #suffix>
                    <a-tooltip title="双击输入框或点击此处批量搜索">
                      <DatabaseOutlined style="color: #1890ff; cursor: pointer; font-size: 14px" @click="openBatchSearchTracking" />
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
              <a-form-item label="商品搜索 (SKU/SPU)">
                <a-input
                  v-model:value="filterForm.productSearchValue"
                  placeholder="双击批量搜索"
                  allow-clear
                  class="premium-input-search"
                  @dblclick="openBatchSearchProduct"
                >
                  <template #suffix>
                    <a-tooltip title="双击输入框或点击此处批量搜索">
                      <DatabaseOutlined style="color: #1890ff; cursor: pointer; font-size: 14px" @click="openBatchSearchProduct" />
                    </a-tooltip>
                  </template>
                </a-input>
              </a-form-item>
            </a-col>
            <a-col :xs="24" :sm="12" :md="8" :lg="4">
              <a-form-item label="邮箱搜索">
                <a-input
                  v-model:value="filterForm.customerEmail"
                  placeholder="双击批量搜索邮箱"
                  allow-clear
                  class="premium-input-search"
                  @dblclick="openBatchSearchEmail"
                >
                  <template #suffix>
                    <a-tooltip title="双击输入框或点击此处批量搜索">
                      <MailOutlined style="color: #1890ff; cursor: pointer; font-size: 14px" @click="openBatchSearchEmail" />
                    </a-tooltip>
                  </template>
                </a-input>
              </a-form-item>
            </a-col>
            <a-col :xs="24" :sm="24" :md="16" :lg="4">
              <a-form-item label="时间轴">
                <div class="integrated-time-selector">
                  <a-select v-model:value="filterForm.timeType" placeholder="分类" class="type-select" style="width: 85px">
                    <a-select-option value="createTime">创建</a-select-option>
                    <a-select-option value="paymentTime">付款</a-select-option>
                    <a-select-option value="shipTime">发货</a-select-option>
                    <a-select-option value="deliveredTime">妥投</a-select-option>
                  </a-select>
                  <a-range-picker
                    v-model:value="filterForm.timeRange"
                    :placeholder="['开始', '结束']"
                    format="YYYY-MM-DD"
                    :presets="timeRanges"
                    class="range-picker"
                    style="flex: 1"
                  />
                </div>
              </a-form-item>
            </a-col>
          </template>

          <!-- 独立的操作行：始终可见且居右对齐 -->
          <a-col :span="24">
            <div class="filter-footer-right">
              <a-space size="middle">
                <a-button type="primary" @click="handleFilter" class="premium-btn primary-gradient">
                  查询
                </a-button>
                <a-button @click="handleResetFilter" class="premium-btn">重置</a-button>
                <a-button type="link" @click="filterExpanded = !filterExpanded" class="expand-btn">
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
          <div class="status-switcher-wrapper">
            <a-radio-group v-model:value="activeKey" button-style="solid" class="premium-segmented" @change="handleTabChange">
              <a-radio-button value="all">全部 {{ totalOrderCount > 0 ? `(${totalOrderCount})` : '' }}</a-radio-button>
              <a-radio-button value="pending">待处理 {{ tabCounts.pending > 0 ? `(${tabCounts.pending})` : '' }}</a-radio-button>
              <a-radio-button value="pending_ship">待揽收 {{ tabCounts.pending_ship > 0 ? `(${tabCounts.pending_ship})` : '' }}</a-radio-button>
              <a-radio-button value="in_transit">运输中 {{ tabCounts.in_transit > 0 ? `(${tabCounts.in_transit})` : '' }}</a-radio-button>
              <a-radio-button value="delivered">已送达 {{ tabCounts.delivered > 0 ? `(${tabCounts.delivered})` : '' }}</a-radio-button>
              <a-radio-button value="cancelled">已取消 {{ tabCounts.cancelled > 0 ? `(${tabCounts.cancelled})` : '' }}</a-radio-button>
              <a-radio-button value="exception">异常 {{ tabCounts.exception > 0 ? `(${tabCounts.exception})` : '' }}</a-radio-button>
            </a-radio-group>
          </div>
          
          <a-space size="small" class="toolbar-btns">
            <div class="sync-btn-wrapper">
              <!-- Red Bird Style Sync Status Group -->
              <transition name="red-bird-slide">
                <div v-if="syncStatus.minimized && syncStatus.status === 'RUNNING'" class="red-bird-sync-group">
                  <div class="sync-details-box" @click="syncModalOpen = true">
                    <SyncOutlined spin class="rb-icon" />
                    <span class="rb-text">详情同步</span>
                    <span class="rb-percent">{{ syncStatus.progress }}%</span>
                  </div>
                  <div class="view-progress-box" @click="syncModalOpen = true">
                    <span>查看进展</span>
                  </div>
                </div>
              </transition>

              <a-button v-if="!(syncStatus.minimized && syncStatus.status === 'RUNNING')" type="primary" @click="syncModalOpen = true" :loading="syncLoading" class="premium-btn-small primary-gradient" v-permission="'order.conversion.sync'">
                <template #icon><sync-outlined /></template>
                同步订单
              </a-button>
            </div>
            <a-button class="premium-btn-small" v-permission="'order.conversion.export'" @click="handleExport">
              <template #icon><export-outlined /></template>导出
            </a-button>
            <a-button type="primary" ghost class="premium-btn-small" @click="openImportExcelModal" v-permission="'order.conversion.sync'">
              <template #icon><upload-outlined /></template>导入转化单
            </a-button>
          </a-space>
        </div>
      </template>

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

      <!-- 批量搜索邮箱弹窗 -->
      <a-modal
        v-model:open="batchSearchEmailVisible"
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
              <MailOutlined />
            </div>
            <div class="ic-header-text">
              <div class="ic-header-title">批量搜索邮箱</div>
              <div class="ic-header-subtitle">请输入邮箱地址，多个请用换行分隔</div>
            </div>
          </div>
          <a-button type="text" class="close-btn" @click="batchSearchEmailVisible = false">
            <CloseOutlined style="font-size: 16px; color: #94a3b8" />
          </a-button>
        </div>
        
        <div class="ic-modal-body" style="background: #fff; padding: 24px;">
          <a-textarea
            v-model:value="batchSearchEmailValue"
            placeholder="例如：&#10;user1@example.com&#10;user2@example.com"
            :rows="10"
            class="premium-textarea"
            style="margin-bottom: 0;"
          />
        </div>

        <div class="ic-modal-footer">
          <a-space size="middle">
            <a-button @click="batchSearchEmailVisible = false" class="premium-btn">取消</a-button>
            <a-button type="primary" @click="handleBatchSearchEmail" class="premium-btn primary-gradient">开始搜索</a-button>
          </a-space>
        </div>
      </a-modal>

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
              <UnorderedListOutlined />
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
              <UnorderedListOutlined />
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

      <!-- 批量搜索物流单号弹窗 -->
      <a-modal
        v-model:open="batchSearchTrackingVisible"
        :title="null"
        :footer="null"
        class="premium-modal influencer-create-modal"
        :closable="false"
        width="500px"
        centered
      >
        <div class="ic-modal-header glass-header">
          <div class="ic-header-left">
            <div class="ic-header-icon-wrapper" style="background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);">
              <DatabaseOutlined />
            </div>
            <div class="ic-header-text">
              <div class="ic-header-title">批量搜索物流单号</div>
              <div class="ic-header-subtitle">请输入物流单号，多个请用换行分隔</div>
            </div>
          </div>
          <a-button type="text" class="close-btn" @click="batchSearchTrackingVisible = false">
            <CloseOutlined style="font-size: 16px; color: #94a3b8" />
          </a-button>
        </div>
        
        <div class="ic-modal-body" style="background: #fff; padding: 24px;">
          <a-textarea
            v-model:value="batchSearchTrackingValue"
            placeholder="例如：&#10;1Z99999999999&#10;940010000000"
            :rows="10"
            class="premium-textarea"
            style="margin-bottom: 0;"
          />
        </div>

        <div class="ic-modal-footer">
          <a-space size="middle">
            <a-button @click="batchSearchTrackingVisible = false" class="premium-btn">取消</a-button>
            <a-button type="primary" @click="handleBatchSearchTracking" class="premium-btn primary-gradient">开始搜索</a-button>
          </a-space>
        </div>
      </a-modal>

      <template v-if="displayData.length <= 500 || initialLoading">
        <div v-if="initialLoading" style="padding: 24px;">
          <a-skeleton active :paragraph="{ rows: 8 }" :title="false" />
        </div>
        <a-table
          v-else
          :columns="columns"
          :data-source="displayData"
          :row-key="(record: any) => record.key ?? record.orderNo ?? record.longOrderNo"
          :pagination="false"
          :loading="loading"
          :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange, preserveSelectedRowKeys: true }"
          :scroll="{ y: filterExpanded ? 'calc(100vh - 460px)' : 'calc(100vh - 380px)', x: 'max-content' }"
          size="middle"
          class="premium-table"
          @change="handleTableChange"
        >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'orderNo'">
            <div>
              <div style="font-weight: bold; color: #1e293b;">
                {{ record.orderNo }}
                <!-- Tag only for split PARTS (must have F-suffix OR fulfillmentIds) -->
                <a-tag v-if="record.isSplit && record.fulfillmentIds" color="orange" size="small" style="margin-left: 8px;">拆</a-tag>
                <!-- Main Order Tag for remainder -->
                <a-tag v-else-if="record.isSplit && !record.fulfillmentIds" color="blue" size="small" style="margin-left: 8px;">主</a-tag>
              </div>
              <!-- Row 2: Long Shopify ID (Numeric only) -->
              <div style="font-size: 12px; color: #64748b; margin-top: 2px; word-break: break-all;">
                {{ record.longOrderNo ? record.longOrderNo.replace('gid://shopify/Order/', '') : '-' }}
              </div>
              <div v-if="record.fulfillmentIds" style="font-size: 12px; color: #8b5cf6; margin-top: 2px; word-break: break-all;">
                F-ID：{{ record.fulfillmentIds.split(',').map((id: string) => id.includes('/') ? id.split('/').pop() : id).join(',') }}
              </div>
            </div>
          </template>
          <template v-else-if="column.key === 'storeName'">
            <span style="color: #64748b; font-weight: 500;">{{ record.storeName || '-' }}</span>
          </template>
          <template v-else-if="column.key === 'products'">
            <div v-for="(product, idx) in record.products" :key="idx" style="display: flex; align-items: center; justify-content: space-between; flex-wrap: nowrap; gap: 4px; background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 6px; padding: 4px 8px; margin-bottom: 6px; width: 100%; box-sizing: border-box; overflow: hidden; white-space: nowrap;">
              <div style="display: flex; align-items: center; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; flex: 1; margin-right: 8px;">
                <span style="color: #0369a1; font-weight: 700; font-size: 13px; font-family: monospace;">{{ product.sku }}</span>
                <template v-if="product.variantTitle">
                  <span v-for="(spec, sIdx) in String(product.variantTitle).split('/')" :key="sIdx"
                        :style="{ 
                          color: ['#16a34a', '#ea580c', '#8b5cf6'][sIdx % 3], 
                          fontWeight: '600',
                          fontSize: '13px'
                        }">
                    -{{ spec.trim().replace(/\s+/g, '') }}
                  </span>
                </template>
              </div>
              
              <!-- Right side: quantity and status -->
              <div style="display: flex; align-items: center; flex-shrink: 0;">
                <span style="color: #ef4444; font-weight: 700; font-size: 14px; margin-right: 8px;">×{{ product.quantity }}</span>
                <span v-if="product.returnedQuantity > 0 && product.returnedQuantity < product.quantity" 
                  style="margin-left: 4px; color: #f59e0b; font-size: 12px;">
                  (待发{{ product.quantity - product.returnedQuantity }} 退{{ product.returnedQuantity }})
                </span>
                <a-tag v-else-if="product.status && product.status !== '待发货'" 
                  :color="product.status === '已发货' ? 'blue' : (product.status === '已退货' ? 'red' : 'orange')" 
                  size="small" 
                  style="margin-right: 0;">
                  {{ product.status }}
                </a-tag>
              </div>
            </div>
          </template>
          <template v-else-if="column.key === 'discountCode'">
            <div v-if="record.discountCode && record.discountCode !== '-'" style="display: inline-block; padding: 2px 8px; background: #fdf4ff; color: #a21caf; border: 1px dashed #f0abfc; border-radius: 4px; font-weight: 600; font-family: monospace; font-size: 13px;">
              {{ record.discountCode }}
            </div>
            <span v-else style="color: #cbd5e1;">-</span>
          </template>
          <template v-else-if="column.key === 'productAmount'">
            <span style="font-weight: 500;">${{ record.productAmount }}</span>
          </template>
          <template v-else-if="column.key === 'address'">
            <a-tooltip placement="topLeft" color="#ffffff" :overlay-inner-style="{ color: '#1e293b', borderRadius: '12px', padding: '12px', boxShadow: '0 10px 30px rgba(0,0,0,0.1)' }">
              <template #title>
                <div style="font-weight: 800; color: #3b82f6; margin-bottom: 4px; font-size: 10px; text-transform: uppercase; letter-spacing: 1px;">详细地址</div>
                <div style="line-height: 1.6; font-size: 13px;">{{ record.address }}</div>
              </template>
              <div class="premium-address-display">
                <environment-outlined class="addr-icon" />
                <span class="addr-text">{{ record.address || '-' }}</span>
              </div>
            </a-tooltip>
          </template>
          <template v-else-if="column.key === 'email'">
            <span style="color: #64748b;">{{ maskText(record.email, 'email') }}</span>
          </template>
          <template v-else-if="column.key === 'phone'">
            <span style="color: #64748b;">{{ maskText(record.phone, 'phone') }}</span>
          </template>
          <template v-else-if="column.key === 'receiverName'">
            <span style="color: #334155;">{{ record.receiverName || '-' }}</span>
          </template>
          <template v-else-if="column.key === 'totalAmount'">
            <span style="font-weight: 700; color: #3b82f6;">${{ record.totalAmount }}</span>
          </template>
          <template v-else-if="column.key === 'orderStatus'">
            <a-tag :class="['status-tag', getOrderStatusColor(record.orderStatus)]">
              {{ record.orderStatus }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'logisticsStatus'">
            <a-tag :class="['status-tag', getLogisticsStatusColor(record.logisticsStatus)]" v-if="record.logisticsStatus">
              {{ record.logisticsStatus }}
            </a-tag>
            <span v-else>-</span>
          </template>
          <template v-else-if="column.key === 'logisticsChannel'">
            <div class="logistics-info">
              <div v-if="record.logisticsName && record.logisticsName !== '-'">
                <div style="font-weight: 500; color: #334155;">{{ record.logisticsName }}</div>
              </div>
              <div v-if="record.trackingNo && record.trackingNo !== '-'" style="font-size: 12px; color: #64748b;">
                <a v-if="record.trackingUrl" :href="record.trackingUrl" target="_blank" class="tracking-link">
                  {{ record.trackingNo }}
                </a>
                <span v-else>{{ record.trackingNo }}</span>
              </div>
              <div v-if="record.estimatedDeliveryAt" class="estimated-delivery" style="margin-top: 2px; color: #10b981; font-size: 12px;">
                <span style="opacity: 0.85;">预计送达：</span>
                <span>{{ dayjs(record.estimatedDeliveryAt).format('YYYY-MM-DD') }}</span>
              </div>
              <span v-if="(!record.logisticsName || record.logisticsName === '-') && (!record.trackingNo || record.trackingNo === '-')">-</span>
            </div>
          </template>
          <template v-else-if="column.key === 'paymentTime'">
            <span style="color: #64748b;">{{ record.paymentTime }}</span>
          </template>
          <template v-else-if="column.key === 'time'">
            <div v-if="record.timeInfo" style="font-size: 13px; color: #64748b; line-height: 1.6;">
              <div>下单：{{ record.timeInfo.createTime || '-' }}</div>
              <div v-if="record.timeInfo.paymentTime">支付：{{ record.timeInfo.paymentTime }}</div>
              <div>发货：{{ record.timeInfo.shipTime || '-' }}</div>
              <div>到货：{{ record.timeInfo.deliveryTime || '-' }}</div>
            </div>
          </template>
          <template v-else-if="column.key === 'action'">
            <div class="action-btns-wrapper">
              <a-button type="primary" size="small" @click="handleViewDetail(record)" class="detail-btn">
                详情
              </a-button>
            </div>
          </template>
        </template>
        </a-table>
      </template>
      <template v-else>
        <div class="virtual-header">
          <div class="hcell" style="flex: 0 0 180px">订单号</div>
          <div class="hcell" style="flex: 0 0 120px">店铺</div>
          <div class="hcell" style="flex: 0 0 350px">订单商品</div>
          <div class="hcell" style="flex: 0 0 120px">商品金额</div>
          <div class="hcell" style="flex: 0 0 150px">折扣码</div>
          <div class="hcell" style="flex: 0 0 200px">收件地址</div>
          <div class="hcell" style="flex: 0 0 150px">收件人姓名</div>
          <div class="hcell" style="flex: 0 0 150px">实付金额</div>
          <div class="hcell" style="flex: 0 0 120px">订单状态</div>
          <div class="hcell" style="flex: 0 0 120px">物流状态</div>
          <div class="hcell" style="flex: 0 0 120px">物流渠道</div>
          <div class="hcell" style="flex: 0 0 280px">下单时间</div>
          <div class="hcell" style="flex: 0 0 100px">操作</div>
        </div>
        <VirtualList
          :data-key="'key'"
          :data-sources="displayData"
          :keeps="20"
          :estimate-size="64"
          :data-component="VirtualConversionOrderRow"
          :extra-props="{ onDetail: handleViewDetail }"
          class="virtual-list"
        />
      </template>
      <div class="pagination-footer">
        <div class="footer-left">
          <span class="info-item">当前记录数量 <span class="count-value">{{ pagination.total }}</span></span>
          <span class="info-divider">/</span>
          <span class="info-item">选中数量 <span class="count-value">{{ selectedRowKeys.length }}</span></span>
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

    <!-- 订单详情Modal -->
    <OrderDetailModal
      v-model:open="detailModalVisible"
      :order-data="currentOrder"
      order-type="conversion"
      @view-related-order="handleViewRelatedOrder"
    />
    
    <!-- Export Modal -->
    <InfluencerExportModal
      v-model:open="exportModalVisible"
      :selected-count="selectedRowKeys.length"
      :export-fields="orderExportFields"
      page-type="conversion-order"
      :current-user-id="currentUserId"
      @export="handleExportFromModal"
    />

    <!-- 订单同步组件 -->
    <OrderSyncModal
      v-model:open="syncModalOpen"
      type="conversion"
      @sync-finished="fetchOrders"
      @status-change="handleSyncStatusChange"
    />

    <!-- 批量搜索弹窗 -->
    <a-modal
      v-model:open="batchSearchProductVisible"
      :title="null"
      :footer="null"
      class="premium-modal influencer-create-modal"
      :closable="false"
      width="500px"
      centered
    >
      <div class="ic-modal-header glass-header">
        <div class="ic-header-left">
          <div class="ic-header-icon-wrapper" style="background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);">
            <UnorderedListOutlined />
          </div>
          <div class="ic-header-text">
            <div class="ic-header-title">批量搜索商品</div>
            <div class="ic-header-subtitle">请输入 SKU 或 SPU，多个请用换行分隔</div>
          </div>
        </div>
        <a-button type="text" class="close-btn" @click="batchSearchProductVisible = false">
          <CloseOutlined style="font-size: 16px; color: #94a3b8" />
        </a-button>
      </div>
      
      <div class="ic-modal-body" style="background: #fff; padding: 24px;">
        <a-select v-model:value="filterForm.productSearchType" style="width: 120px; margin-bottom: 12px;" class="premium-select">
          <a-select-option value="sku">按 SKU 搜索</a-select-option>
          <a-select-option value="spu">按 SPU 搜索</a-select-option>
        </a-select>
        <a-textarea
          v-model:value="batchSearchProductValue"
          placeholder="例如：&#10;WYJCK0339&#10;ABC123"
          :rows="10"
          class="premium-textarea"
          style="margin-bottom: 0;"
        />
      </div>

      <div class="ic-modal-footer">
        <a-space size="middle">
          <a-button @click="batchSearchProductVisible = false" class="premium-btn">取消</a-button>
          <a-button type="primary" @click="handleBatchSearchProduct" class="premium-btn primary-gradient">开始搜索</a-button>
        </a-space>
      </div>
    </a-modal>
    <!-- 批量搜索关联红人弹窗 -->
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
          <div class="ic-header-icon-wrapper" style="background: linear-gradient(135deg, #10b981 0%, #059669 100%);">
            <UserOutlined />
          </div>
          <div class="ic-header-text">
            <div class="ic-header-title">批量搜索关联红人</div>
            <div class="ic-header-subtitle">请输入红人名称，多个红人请用换行或逗号分隔</div>
          </div>
        </div>
        <a-button type="text" class="close-btn" @click="batchSearchInfluencerVisible = false">
          <CloseOutlined style="font-size: 16px; color: #94a3b8" />
        </a-button>
      </div>
      
      <div class="ic-modal-body" style="background: #fff; padding: 24px;">
        <a-textarea
          v-model:value="batchSearchInfluencerValue"
          placeholder="例如：&#10;Alice&#10;Bob, Charlie"
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

    <!-- 导入转化单弹窗 -->
    <a-modal
      v-model:open="importExcelModalVisible"
      title="导入转化订单"
      :footer="null"
      class="premium-modal influencer-create-modal"
      width="600px"
      centered
    >
      <div class="ic-modal-header glass-header" style="border-radius: 12px 12px 0 0;">
        <div class="ic-header-left">
          <div class="ic-header-icon-wrapper" style="background: linear-gradient(135deg, #10b981 0%, #059669 100%);">
            <UploadOutlined />
          </div>
          <div class="ic-header-text">
            <div class="ic-header-title">导入转化订单</div>
            <div class="ic-header-subtitle">支持 Excel (.xls, .xlsx) 和 CSV 文件格式</div>
          </div>
        </div>
      </div>
      
      <div class="ic-modal-body" style="background: #fff; padding: 24px;">
        <div style="margin-bottom: 20px; padding: 12px; background: #f0fdf4; border: 1px solid #bbf7d0; border-radius: 8px; color: #166534; font-size: 13px;">
          <div style="font-weight: 700; margin-bottom: 4px;">导入说明：</div>
          <div>1. 必须包含字段：订单ID/订单编号/Shopify Order Number、订单金额/总金额、分佣金额/佣金金额、红人Handle/红人社媒账号。</div>
          <div>2. 重复校验：如果导入的订单ID在样品单或转化单中已存在，则会自动跳过。</div>
          <div>3. 红人绑定：红人Handle必须非空且匹配系统中的有效红人，否则该行将导入失败。</div>
        </div>

        <a-upload-dragger
          name="file"
          :multiple="false"
          :before-upload="beforeUpload"
          :file-list="fileList"
          @remove="handleRemoveFile"
          accept=".xls,.xlsx,.csv"
          style="border-radius: 12px;"
        >
          <p class="ant-upload-drag-icon">
            <InboxOutlined style="color: #10b981; font-size: 48px;" />
          </p>
          <p class="ant-upload-text">点击或将文件拖拽到此处上传</p>
          <p class="ant-upload-hint">支持单个 Excel 或 CSV 文件，大小不超过 10MB</p>
        </a-upload-dragger>

        <!-- 导入结果展示 -->
        <div v-if="importResult" style="margin-top: 20px; border-top: 1px solid #f1f5f9; padding-top: 16px;">
          <div style="font-weight: 700; color: #1e293b; margin-bottom: 12px;">导入结果统计：</div>
          <a-row :gutter="16" style="margin-bottom: 16px;">
            <a-col :span="6">
              <a-statistic title="总行数" :value="importResult.totalCount" value-style="color: #1e293b" />
            </a-col>
            <a-col :span="6">
              <a-statistic title="成功导入" :value="importResult.successCount" value-style="color: #10b981" />
            </a-col>
            <a-col :span="6">
              <a-statistic title="跳过重复" :value="importResult.skippedCount" value-style="color: #f59e0b" />
            </a-col>
            <a-col :span="6">
              <a-statistic title="失败行数" :value="importResult.failedCount" value-style="color: #ef4444" />
            </a-col>
          </a-row>

          <div v-if="importResult.errors && importResult.errors.length > 0">
            <div style="font-weight: 700; color: #ef4444; margin-bottom: 8px; font-size: 13px;">失败/跳过详情：</div>
            <div style="max-height: 180px; overflow-y: auto; background: #fff5f5; border: 1px solid #fecaca; border-radius: 6px; padding: 8px;">
              <div v-for="(err, idx) in importResult.errors" :key="idx" style="font-size: 12px; color: #b91c1c; margin-bottom: 6px; display: flex; justify-content: space-between;">
                <span>第 {{ err.rowNum }} 行 (订单ID: {{ err.orderId || '-' }})</span>
                <span style="font-weight: 600;">{{ err.error }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="ic-modal-footer" style="padding: 16px 24px; text-align: right; border-top: 1px solid #e2e8f0;">
        <a-space size="middle">
          <a-button @click="closeImportModal" class="premium-btn">关闭</a-button>
          <a-button type="primary" :loading="importExcelLoading" :disabled="fileList.length === 0" @click="handleImportExcelSubmit" class="premium-btn primary-gradient">开始导入</a-button>
        </a-space>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
defineOptions({
  name: 'OrderConversion'
});

import { ref, reactive, computed, watch, onMounted, onUnmounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { 
  DownOutlined, 
  UpOutlined,
  ExportOutlined,
  SyncOutlined,
  CloseOutlined,
  DatabaseOutlined,
  UnorderedListOutlined,
  MailOutlined,
  UserOutlined,
  UploadOutlined,
  InboxOutlined
} from '@ant-design/icons-vue';
import { message, Modal } from 'ant-design-vue';
import type { TableColumnsType } from 'ant-design-vue';
import dayjs, { Dayjs } from 'dayjs';
import OrderDetailModal from '@/components/order/OrderDetailModal.vue';
import VirtualList from 'vue3-virtual-scroll-list';
import VirtualConversionOrderRow from '@/components/common/VirtualConversionOrderRow.vue';
import type { ConversionOrderRow } from '@/types/order';
import { 
  getConversionOrders, 
  getConversionTabCounts, 
  type ConversionOrderDto,
  importConversionOrdersExcel
} from '@/services/influencerOrderService';
import OrderSyncModal from '@/components/order/OrderSyncModal.vue';
import { useSseStore } from '@/stores/sse';
import { createExportTask } from '@/utils/exportTaskHelper';
import {
  applyConversionOrderQuery,
  notifyCopilotOrderSearchComplete,
} from '@/utils/applyCopilotOrderQuery';
import { onAiUiAction } from '@/utils/aiActionBus';
import { setCurrentUserIdGetter } from '@/utils/exportTemplate';
import InfluencerExportModal from '@/components/influencer/InfluencerExportModal.vue';

const route = useRoute();
const router = useRouter();

const filterExpanded = ref(false);
const activeKey = ref((route.query.tab as string) || 'pending');
const loading = ref(false);
const initialLoading = ref(true);

// 状态统计数据
const tabCounts = reactive({
  pending: 0,
  pending_ship: 0,
  in_transit: 0,
  delivered: 0,
  completed: 0,
  exception: 0,
  cancelled: 0
});

// 计算订单总量
const totalOrderCount = computed(() => {
  return Object.values(tabCounts).reduce((sum, count) => sum + (count || 0), 0);
});

const syncLoading = ref(false);
const detailModalVisible = ref(false);
const currentOrder = ref<any>(null);
const selectedRowKeys = ref<any[]>([]);
const exportModalVisible = ref(false);
const currentUserId = ref<string>('default_user');

// 设置 currentUserId getter
setCurrentUserIdGetter(() => {
  return currentUserId.value;
});

// 获取当前用户ID
const initCurrentUserId = () => {
  try {
    const userInfo = localStorage.getItem('userInfo');
    if (userInfo) {
      const userInfoObj = JSON.parse(userInfo);
      currentUserId.value = (userInfoObj.id || userInfoObj.userId || 'default_user') as string;
    }
  } catch (e) {
    console.error('获取当前用户ID失败', e);
  }
};

const pagination = reactive({
  current: parseInt((route.query.page as string) || '1', 10),
  pageSize: parseInt((route.query.pageSize as string) || '10', 10),
  total: 50,
  showSizeChanger: true,
  showQuickJumper: true,
});

// 同步相关状态
const syncModalOpen = ref(false);
const syncStatus = reactive({
  status: 'IDLE',
  progress: 0,
  minimized: false
});

const handleSyncStatusChange = (status: any) => {
  Object.assign(syncStatus, status);
};

const filterForm = reactive({
  orderNo: '',
  shopifyOrderId: '',
  influencer: '',
  discountCode: '',
  status: undefined as string | undefined,
  commissionStatus: undefined as string | undefined,
  trackingNo: '',
  country: [] as string[],
  productSearchType: 'sku' as 'sku' | 'spu',
  productSearchValue: '',
  influencerSocialSearch: '', // 添加批量搜索社媒账号字段
  customerEmail: '', // 增加邮箱筛选字段
  timeType: undefined as 'createTime' | 'paymentTime' | 'shipTime' | 'deliveredTime' | 'completedTime' | 'cancelApplyTime' | 'cancelTime' | 'exceptionTime' | undefined,
  timeRange: undefined as [Dayjs, Dayjs] | undefined,
});

// 批量搜索订单号相关
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

// 批量搜索订单号 (Shopify ID) 相关
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

// 批量搜索红人相关
const batchSearchInfluencerVisible = ref(false);
const batchSearchInfluencerValue = ref('');

const openBatchSearchInfluencer = () => {
  batchSearchInfluencerVisible.value = true;
  batchSearchInfluencerValue.value = filterForm.influencer || '';
};

const handleBatchSearchInfluencer = () => {
  const values = batchSearchInfluencerValue.value.split(/[\n,]+/).map(v => v.trim()).filter(Boolean);
  filterForm.influencer = values.join(',');
  batchSearchInfluencerVisible.value = false;
  handleFilter();
};

// 批量搜索物流单号相关
const batchSearchTrackingVisible = ref(false);
const batchSearchTrackingValue = ref('');

const openBatchSearchTracking = () => {
  batchSearchTrackingValue.value = filterForm.trackingNo?.replace(/,/g, '\n') || '';
  batchSearchTrackingVisible.value = true;
};

const handleBatchSearchTracking = () => {
  const values = batchSearchTrackingValue.value.split(/[\n,]+/).map(v => v.trim()).filter(Boolean);
  filterForm.trackingNo = values.join(',');
  batchSearchTrackingVisible.value = false;
  handleFilter();
};

// 批量搜索折扣码相关
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

// 批量搜索邮箱相关
const batchSearchEmailVisible = ref(false);
const batchSearchEmailValue = ref('');

const openBatchSearchEmail = () => {
  batchSearchEmailValue.value = filterForm.customerEmail?.replace(/,/g, '\n') || '';
  batchSearchEmailVisible.value = true;
};

const handleBatchSearchEmail = () => {
  const values = batchSearchEmailValue.value.split(/[\n,]+/).map(v => v.trim()).filter(Boolean);
  if (!values.length) {
    message.warning('请输入要搜索的邮箱');
    return;
  }
  filterForm.customerEmail = values.join(',');
  batchSearchEmailVisible.value = false;
  handleFilter();
};

// 批量搜索商品相关
const batchSearchProductVisible = ref(false);
const batchSearchProductValue = ref('');

const openBatchSearchProduct = () => {
  batchSearchProductValue.value = filterForm.productSearchValue?.replace(/,/g, '\n') || '';
  batchSearchProductVisible.value = true;
};

const handleBatchSearchProduct = () => {
  const values = batchSearchProductValue.value.split(/[\n,]+/).map(v => v.trim()).filter(Boolean);
  if (!values.length) {
    message.warning('请输入搜索内容');
    return;
  }
  filterForm.productSearchValue = values.join(',');
  batchSearchProductVisible.value = false;
  handleFilter();
};

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

// 加密显示函数
const maskText = (text: string, type: 'name' | 'email' | 'phone' | 'address' = 'name') => {
  if (!text || text === '-' || text === 'null null') return '-';
  // 明文显示逻辑：如果是姓名或地址，直接返回原值
  if (type === 'name' || type === 'address') return text;
  
  if (type === 'email') {
    const atIndex = text.indexOf('@');
    if (atIndex <= 1) return text;
    return text.charAt(0) + '*'.repeat(Math.max(atIndex - 1, 1)) + text.substring(atIndex);
  } else if (type === 'phone') {
    if (text.length <= 7) return text;
    return text.substring(0, 3) + '****' + text.substring(text.length - 4);
  }
  return text;
};

const columns: TableColumnsType = [
  {
    title: '交易号 / 订单号',
    key: 'orderNo',
    width: 180,
    fixed: 'left',
  },
  {
    title: '店铺',
    key: 'storeName',
    width: 120,
  },
  {
    title: '订单商品',
    key: 'products',
    width: 350,
  },
  {
    title: '商品金额',
    key: 'productAmount',
    width: 120,
    sorter: true,
  },
  {
    title: '折扣码',
    dataIndex: 'discountCode',
    key: 'discountCode',
    width: 150,
  },
  {
    title: '收件地址',
    key: 'address',
    width: 200,
  },
  {
    title: '收件人姓名',
    key: 'receiverName',
    width: 120,
  },
  {
    title: '订单总金额',
    key: 'totalAmount',
    width: 120,
    sorter: true,
  },
  {
    title: '订单状态',
    key: 'orderStatus',
    width: 120,
    align: 'center',
  },
  {
    title: '物流状态',
    key: 'logisticsStatus',
    width: 120,
    align: 'center',
  },
  {
    title: '物流渠道',
    key: 'logisticsChannel',
    width: 200,
  },
  {
    title: '支付时间',
    key: 'paymentTime',
    width: 160,
    sorter: true,
  },
  {
    title: '时间',
    key: 'time',
    width: 280,
  },
  {
    title: '操作',
    key: 'action',
    width: 100,
    fixed: 'right',
    align: 'center',
  },
];

// 订单数据 (从API获取)
const data = ref<ConversionOrderRow[]>([]);

// 将转化订单DTO转换为 ConversionOrderRow 格式
const convertToRow = (order: ConversionOrderDto, index: number): ConversionOrderRow => {
  // 解析订单状态
  const financialStatus = order.financialStatus?.toLowerCase() || 'pending';
  const fulfillmentDisplayStatus = (order.fulfillmentDisplayStatus || '').toLowerCase();
  
  let orderStatus = '待付款';
  let logisticsStatus = '待处理';
  
  // 0. 检查是否所有商品都已退货
  const products = order.products || [];
  let allReturned = false;
  
  if (products.length > 0) {
    if (order.isSplit && order.fulfillmentIds) {
      // 拆单包裹：检查当前包裹内的商品是否全部退货
      const packageFulfillmentIds = (order.fulfillmentIds || '').split(',');
      const packageProducts = products.filter((p: any) => 
        p.fulfillmentId && packageFulfillmentIds.includes(String(p.fulfillmentId))
      );
      allReturned = packageProducts.length > 0 && packageProducts.every((p: any) => p.isReturned === true);
    } else {
      // 非拆单：检查所有商品
      allReturned = products.every((p: any) => p.isReturned === true);
    }
  }

  // --- 状态判定逻辑 ---

  // 1. 优先处理：已取消 / 已退款 / 已关闭 / 全部退货
  if (order.cancelledAt || financialStatus === 'voided' || financialStatus === 'refunded' || allReturned) {
    orderStatus = financialStatus === 'refunded' ? '已退款' : '已取消';
    logisticsStatus = '已取消';
  } else if (order.closedAt) {
    // 订单已关闭 -> 已完成
    orderStatus = '已付款';
    logisticsStatus = '已完成';
  } else {
    // 2. 使用时间字段判定物流状态（优先级从高到低）
    if (order.deliveredAt || fulfillmentDisplayStatus === 'delivered') {
      // 只要是已送达，物流状态就显示为“已完成” (根据客户要求不再显示“已送达”)
      logisticsStatus = '已完成';
    } else if (['carrier_picked_up', 'in_transit', 'out_for_delivery', 'shipped', 'picked_up'].includes(fulfillmentDisplayStatus) || order.inTransitAt) {
      // 运输中：明确的运输中状态，或者已有运输中时间
      logisticsStatus = '运输中';
    } else if (['label_printed', 'ready_for_pickup', 'label_purchased', 'label_voided', 'confirmed'].includes(fulfillmentDisplayStatus) || 
               (['fulfilled', 'marked_as_fulfilled', 'success', 'partial'].includes(fulfillmentDisplayStatus) && !order.inTransitAt) ||
               order.fulfillmentCreatedAt) {
      // 待揽收：已发货(fulfilled)但没有运输时间，或者处于打单/准备中
      logisticsStatus = '待揽收'; 
    } else if (['failure', 'failed', 'not_delivered', 'exception', 'attempted_delivery', 'delayed'].includes(fulfillmentDisplayStatus)) {
      logisticsStatus = '异常';
    } else if (['restocked', 'returned', 'return_in_progress'].includes(fulfillmentDisplayStatus)) {
      logisticsStatus = fulfillmentDisplayStatus === 'return_in_progress' ? '退货中' : '已退货';
    } else if (['canceled', 'cancelled', 'label_voided'].includes(fulfillmentDisplayStatus)) {
      logisticsStatus = '已取消';
    } else {
      logisticsStatus = '待处理';
    }

    // 3. 判定订单状态
    if (financialStatus === 'pending') {
      orderStatus = '待付款';
    } else if (financialStatus === 'paid' || financialStatus === 'partially_paid') {
      if (logisticsStatus === '已完成' || logisticsStatus === '已送达') {
        orderStatus = '已完成';
      } else {
        orderStatus = '已付款';
      }
    }
 else if (financialStatus === 'partially_refunded') {
      orderStatus = '部分退款';
    }
  }
  
  const totalAmount = order.totalPrice || 0;
  
  // 构建时间信息
  const timeInfo: any = {};
  if (order.orderCreatedAt) timeInfo.createTime = dayjs(order.orderCreatedAt).format('YYYY-MM-DD HH:mm:ss');
  // 支付时间使用 processedAtShopify
  if (order.processedAtShopify) timeInfo.paymentTime = dayjs(order.processedAtShopify).format('YYYY-MM-DD HH:mm:ss');
  // 发货时间使用 fulfillmentCreatedAt
  if (order.fulfillmentCreatedAt) timeInfo.shipTime = dayjs(order.fulfillmentCreatedAt).format('YYYY-MM-DD HH:mm:ss');
  // 到货时间使用 deliveredAt
  if (order.deliveredAt) timeInfo.deliveryTime = dayjs(order.deliveredAt).format('YYYY-MM-DD HH:mm:ss');
  
  if (order.createdAt) timeInfo.recordTime = dayjs(order.createdAt).format('YYYY-MM-DD HH:mm:ss');
  
  return {
    id: order.id,
    key: order.id || index,
    orderNo: order.orderName || `#${order.shopifyOrderNumber}`,
    shortOrderNo: order.orderName || `#${order.shopifyOrderNumber}`,
    longOrderNo: order.shopifyOrderId || '',
    products: (order.products || []).map((p: any) => ({
      ...p,
      image: p.imageUrl,
      // 商品状态：已退货 > 已发货 > 待发货
      status: p.isReturned ? '已退货' : (p.fulfillmentStatus === 'fulfilled' ? '已发货' : '待发货')
    })),
    productAmount: totalAmount.toFixed(2),
    discountCode: order.discountCode || '',
    address: order.shippingAddress || order.recipientAddress || '',
    email: order.customerEmail || '',
    phone: order.shippingPhone || order.recipientPhone || '',
    receiverPhone: order.shippingPhone || order.recipientPhone || '',
    postalCode: '',
    receiverName: order.shippingName || order.recipientName || order.customerName || '',
    // Preserve raw fields for Detail Modal
    recipientName: order.recipientName,
    recipientPhone: order.recipientPhone,
    recipientAddress: order.recipientAddress,
    recipientCountry: order.recipientCountry,
    shippingName: order.shippingName,
    shippingPhone: order.shippingPhone,
    shippingAddress: order.shippingAddress,
    shippingCountry: order.shippingCountry,
    customerEmail: order.customerEmail,
    customerName: order.customerName,
    totalAmount: totalAmount.toFixed(2),
    orderStatus,
    logisticsStatus,
    logisticsName: order.trackingCompany || '-',
    trackingNo: order.trackingNumber || '-',
    trackingCompany: order.trackingCompany,
    trackingNumber: order.trackingNumber,
    trackingUrl: order.trackingUrl || '',
    fulfillmentIds: order.fulfillmentIds || '',
    packageNo: order.packageNo || '',
    isSplit: order.isSplit,
    storeName: order.storeName || '-',
    fulfillmentDisplayStatus: order.fulfillmentDisplayStatus || '',
    timeInfo,
    paymentTime: order.processedAtShopify ? dayjs(order.processedAtShopify).format('YYYY-MM-DD HH:mm:ss') : '-', // Add for column
    estimatedDeliveryAt: order.estimatedDeliveryAt,
    // 红人信息
    influencerName: order.influencerName,
    commissionRate: order.commissionRate,
    commissionAmount: order.commissionAmount,
    commissionStatus: order.commissionStatus,
  };
};

// 获取转化订单列表
const fetchOrders = async () => {
  loading.value = true;
  try {
    const combinedOrderNo = [filterForm.orderNo, filterForm.shopifyOrderId].filter(Boolean).join(',');
    const filters: any = {
      orderNo: combinedOrderNo,
      influencerName: filterForm.influencer ? filterForm.influencer : undefined,
      discountCode: filterForm.discountCode,
      status: filterForm.status,
      commissionStatus: filterForm.commissionStatus,
      trackingNo: filterForm.trackingNo,
      spu: filterForm.productSearchValue,
      customerEmail: filterForm.customerEmail,
      influencerSocialSearch: filterForm.influencerSocialSearch,
      recipientCountry: Array.isArray(filterForm.country) && filterForm.country.length > 0 ? filterForm.country.join(',') : undefined,
      tab: activeKey.value
    };

    // 更新 URL
    router.replace({
      query: {
        ...route.query,
        tab: activeKey.value,
        page: pagination.current.toString(),
        pageSize: pagination.pageSize.toString()
      }
    });
    
    // 添加时间筛选参数
    if (filterForm.timeType && filterForm.timeRange && filterForm.timeRange.length === 2) {
      filters.timeType = filterForm.timeType;
      filters.startTime = filterForm.timeRange[0].format('YYYY-MM-DD');
      filters.endTime = filterForm.timeRange[1].format('YYYY-MM-DD');
    }

    // 构建 Tab 计数筛选参数
    const countFilters: any = {
      influencerName: filterForm.influencer,
      orderNo: combinedOrderNo,
      discountCode: filterForm.discountCode,
      status: filterForm.status,
      commissionStatus: filterForm.commissionStatus,
      trackingNo: filterForm.trackingNo,
      spu: filterForm.productSearchValue,
      influencerSocialSearch: filterForm.influencerSocialSearch,
      customerEmail: filterForm.customerEmail,
      recipientCountry: Array.isArray(filterForm.country) && filterForm.country.length > 0 ? filterForm.country.join(',') : undefined
    };
    if (filterForm.timeType && filterForm.timeRange && filterForm.timeRange.length === 2) {
      countFilters.timeType = filterForm.timeType;
      countFilters.startTime = filterForm.timeRange[0].format('YYYY-MM-DD');
      countFilters.endTime = filterForm.timeRange[1].format('YYYY-MM-DD');
    }

    // 并行请求：数据 + Tab 计数同时发起
    const [response, counts] = await Promise.all([
      getConversionOrders(pagination.current - 1, pagination.pageSize, filters),
      getConversionTabCounts(countFilters)
    ]);

    let orders = response.content.map((order, index) => convertToRow(order, index));
    
    // 根据当前标签页强制覆盖物流状态
    orders = applyTabStatusOverride(orders, activeKey.value);
    
    // 后端已排序，无需前端重复排序
    data.value = orders;
    pagination.total = response.totalElements;
    Object.assign(tabCounts, counts);
    notifyCopilotOrderSearchComplete(route.query, pagination.total, filterForm, '/order/conversion');
  } catch (e: any) {
    if (e?.code === 'ERR_CANCELED') return;
    console.error('获取转化订单列表失败', e);
    message.error('获取转化订单列表失败');
  } finally {
    loading.value = false;
    initialLoading.value = false;
  }
};

// 提取物流状态覆盖逻辑（复用于导出）
const applyTabStatusOverride = (orders: ConversionOrderRow[], tab: string): ConversionOrderRow[] => {
  const statusMap: Record<string, string> = { shipped: '运输中', in_transit: '运输中', pending_ship: '待揽收' };
  const targetStatus = statusMap[tab];
  if (!targetStatus) return orders;
  return orders.map(order => ({ ...order, logisticsStatus: targetStatus }));
};

const displayData = computed<ConversionOrderRow[]>(() => [...data.value]);

const handleTabChange = () => {
  pagination.current = 1;
  // 切换 Tab 时清除筛选框的状态，避免冲突
  filterForm.status = undefined;
  fetchOrders();
};

const onPageChange = (page: number, pageSize: number) => {
  if (pagination.pageSize !== pageSize) {
    pagination.current = 1;
  } else {
    pagination.current = page;
  }
  pagination.pageSize = pageSize;
  fetchOrders();
};

const handleTableChange = (pag: any) => {
  if (pag && pag.current) {
    pagination.current = pag.current;
    pagination.pageSize = pag.pageSize;
    fetchOrders();
  }
};

const handleViewDetail = (record: ConversionOrderRow) => {
  currentOrder.value = record;
  detailModalVisible.value = true;
};

const handleViewRelatedOrder = (order: ConversionOrderRow) => {
  // 根据订单号查找对应的订单记录
  const foundOrder = data.value.find((item) => 
    item.orderNo === order.orderNo || 
    item.shortOrderNo === order.shortOrderNo ||
    item.shortOrderNo === order.orderNo
  );
  
  if (foundOrder) {
    currentOrder.value = foundOrder;
    detailModalVisible.value = true;
  } else {
    // 如果找不到，使用传入的订单数据
    currentOrder.value = order;
    detailModalVisible.value = true;
  }
};

const handleResetFilter = () => {
  filterForm.orderNo = '';
  filterForm.influencer = '';
  filterForm.discountCode = '';
  filterForm.status = undefined;
  filterForm.commissionStatus = undefined;
  filterForm.trackingNo = '';
  filterForm.country = [];
  filterForm.timeType = undefined;
  filterForm.timeRange = undefined;
  filterForm.productSearchValue = '';
  filterForm.productSearchType = 'sku';
  filterForm.customerEmail = '';
  handleFilter();
};

const handleFilter = () => {
  pagination.current = 1;
  fetchOrders();
};

const onSelectChange = (selectedKeys: any[]) => {
  selectedRowKeys.value = selectedKeys;
};

// 监听路由参数变化，同步内部状态
watch(
  () => route.query,
  (query) => {
    if (query.tab && query.tab !== activeKey.value) {
      activeKey.value = query.tab as string;
    }
    if (query.page) {
      const page = parseInt(query.page as string, 10);
      if (page !== pagination.current) {
        pagination.current = page;
      }
    }
    if (query.pageSize) {
      const size = parseInt(query.pageSize as string, 10);
      if (size !== pagination.pageSize) {
        pagination.pageSize = size;
      }
    }
    if (applyConversionOrderQuery(query, filterForm, activeKey, filterExpanded)) {
      pagination.current = 1;
      fetchOrders();
    }
  }
);

// Colors Helpers
const getOrderStatusColor = (status: string) => {
  switch (status) {
    case '待付款': return 'tag-orange';
    case '待揽收': return 'tag-blue';
    case '已发货': return 'tag-blue';
    case '已妥投': return 'tag-purple';
    case '已完成': return 'tag-success';
    case '取消中': return 'tag-orange';
    case '已取消': return 'tag-black';
    case '已退款': return 'tag-red';
    case '已退货': return 'tag-red';
    case '部分退款': return 'tag-orange';
    case '异常': return 'tag-red';
    default: return 'tag-black';
  }
};

const getLogisticsStatusColor = (status: string) => {
  switch (status) {
    case '待揽收': return 'tag-orange';
    case '运输中': return 'tag-blue';
    case '已发货': return 'tag-blue';
    case '已妥投': return 'tag-purple';
    case '已完成': return 'tag-success';
    case '已退款': return 'tag-red';
    case '已退货': return 'tag-red';
    case '退货中': return 'tag-orange';
    case '部分退款': return 'tag-orange';
    default: return 'tag-black';
  }
};

// 同步结束处理
const handleSyncFinished = () => {
  syncModalOpen.value = false;
};


// 定义订单导出字段
const orderExportFields = [
  { key: 'orderNo', title: '订单号', dataKey: 'orderNo' },
  { key: 'storeName', title: '店铺', dataKey: 'storeName' },
  { key: 'products', title: '商品', dataKey: 'products', formatter: (val: any[]) => val?.map(p => `${(p.variantTitle || '').replace(/\s*\/\s*/g, ' ')} ${p.sku || p.shopifyVariantId || '-'} x${p.quantity}`).join(',\n') || '-' },
  { key: 'productAmount', title: '商品金额', dataKey: 'productAmount' },
  { key: 'totalAmount', title: '订单总金额', dataKey: 'totalAmount' },
  { key: 'discountCode', title: '折扣码', dataKey: 'discountCode' },
  { key: 'receiverName', title: '收件人', dataKey: 'receiverName' },
  { key: 'address', title: '收件地址', dataKey: 'address' },
  { key: 'orderStatus', title: '订单状态', dataKey: 'orderStatus' },
  { key: 'logisticsStatus', title: '物流状态', dataKey: 'logisticsStatus' },
  { key: 'logisticsName', title: '物流公司', dataKey: 'logisticsName' },
  { key: 'trackingNo', title: '物流单号', dataKey: 'trackingNo' },
  { key: 'createTime', title: '创建时间', dataKey: 'timeInfo', formatter: (val: any) => val?.createTime || '-' },
];

// 打开导出弹窗
const handleExport = () => {
  exportModalVisible.value = true;
};

// 确认导出 (来自组件事件)
const handleExportFromModal = async (payload: { scope: string, fields: string[], columns: any[], templateId?: string, templateName?: string }) => {
  try {
    const { scope, columns, templateId, templateName } = payload;
    
    // 根据导出范围获取数据
    let exportData: any[];
    if (scope === 'selected') {
      exportData = displayData.value.filter((item: ConversionOrderRow) => selectedRowKeys.value.includes(item.key ?? item.orderNo));
    } else {
      message.loading({ content: '正在获取全部数据...', key: 'exportAllMsg' });
      const filters: any = {
        orderNo: filterForm.orderNo,
        influencerName: filterForm.influencer ? filterForm.influencer : undefined,
        discountCode: filterForm.discountCode,
        status: filterForm.status,
        commissionStatus: filterForm.commissionStatus,
        trackingNo: filterForm.trackingNo,
        spu: filterForm.productSearchValue,
        customerEmail: filterForm.customerEmail,
        influencerSocialSearch: filterForm.influencerSocialSearch,
        tab: activeKey.value
      };
      if (filterForm.timeType && filterForm.timeRange && filterForm.timeRange.length === 2) {
        filters.timeType = filterForm.timeType;
        filters.startTime = filterForm.timeRange[0].format('YYYY-MM-DD');
        filters.endTime = filterForm.timeRange[1].format('YYYY-MM-DD');
      }

      const response = await getConversionOrders(0, pagination.total > 0 ? pagination.total : 10000, filters);
      let orders = response.content.map((order, index) => convertToRow(order, index));
      
      orders = applyTabStatusOverride(orders, activeKey.value);
      exportData = orders;
      message.success({ content: '获取数据成功，开始导出', key: 'exportAllMsg' });
    }
    
    if (exportData.length === 0) {
      message.warning('没有可导出的数据');
      return;
    }
    
    await createExportTask({
      data: exportData,
      columns: columns,
      filename: `转化订单${scope === 'selected' ? '_选中' : ''}`,
      pageType: 'conversion-order',
      templateId: templateId,
      templateName: templateName,
    });
    
    exportModalVisible.value = false;
  } catch (error) {
    console.error('导出失败:', error);
    message.error('导出失败');
  }
};

// SSE 订阅 - 订单更新时自动刷新（带防抖）
const sseStore = useSseStore();
let sseWatchStop: (() => void) | null = null;
let sseDebounceTimer: any = null;

const debouncedFetch = () => {
  if (sseDebounceTimer) clearTimeout(sseDebounceTimer);
  sseDebounceTimer = setTimeout(() => {
    fetchOrders();
  }, 500);
};

let unsubscribeAiAction: (() => void) | undefined;

onMounted(() => {
  initCurrentUserId();
  if (applyConversionOrderQuery(route.query, filterForm, activeKey, filterExpanded)) {
    pagination.current = 1;
  }
  fetchOrders();

  unsubscribeAiAction = onAiUiAction((action) => {
    if (action.name === 'OpenExportModal') {
      exportModalVisible.value = true;
    }
  });
  
  // 监听 SSE 事件，订单更新时自动刷新（带 500ms 防抖）
  sseWatchStop = watch(() => sseStore.lastEvent, (newEvent) => {
    if (newEvent && (newEvent.category === 'orders' || newEvent.data?.topic?.startsWith('orders/'))) {
      debouncedFetch();
    }
  });
});

onUnmounted(() => {
  unsubscribeAiAction?.();
  if (sseWatchStop) sseWatchStop();
  if (sseDebounceTimer) clearTimeout(sseDebounceTimer);
});

const importExcelModalVisible = ref(false);
const importExcelLoading = ref(false);
const fileList = ref<any[]>([]);
const importResult = ref<any>(null);

const openImportExcelModal = () => {
  importExcelModalVisible.value = true;
  fileList.value = [];
  importResult.value = null;
};

const closeImportModal = () => {
  importExcelModalVisible.value = false;
  fileList.value = [];
  importResult.value = null;
};

const beforeUpload = (file: any) => {
  fileList.value = [file];
  return false; // prevent auto upload
};

const handleRemoveFile = () => {
  fileList.value = [];
};

const handleImportExcelSubmit = async () => {
  if (fileList.value.length === 0) {
    message.warning('请选择要导入的文件');
    return;
  }
  importExcelLoading.value = true;
  importResult.value = null;
  try {
    const file = fileList.value[0];
    const opId = isNaN(Number(currentUserId.value)) ? undefined : Number(currentUserId.value);
    const res = await importConversionOrdersExcel(file, opId);
    importResult.value = res;
    if (res.success) {
      message.success(`导入完成：成功 ${res.successCount}，跳过 ${res.skippedCount}，失败 ${res.failedCount}`);
      fetchOrders();
    } else {
      message.error(res.errors && res.errors.length > 0 ? '导入包含部分或全部失败，请查看详情' : '导入失败');
    }
  } catch (err: any) {
    message.error(err.response?.data?.message || err.message || '导入出错');
  } finally {
    importExcelLoading.value = false;
  }
};
</script>

<style lang="scss" scoped>
.conversion-order-page {
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

  .premium-address-display {
    display: flex;
    align-items: flex-start;
    gap: 6px;
    padding: 4px 8px;
    margin-left: -8px;
    border-radius: 6px;
    transition: all 0.3s;
    cursor: help;
    max-width: 100%;

    .addr-icon {
      color: #94a3b8;
      font-size: 11px;
      flex-shrink: 0;
      margin-top: 3px;
    }

    .addr-text {
      font-size: 12px;
      color: #64748b;
      font-weight: 500;
      white-space: normal;
      word-break: break-word;
      line-height: 1.4;
      flex: 1;
    }

    &:hover {
      background: #f1f5f9;
      .addr-icon { color: #3b82f6; }
      .addr-text { color: #1e293b; }
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
    background: #ffffff;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
    border: 1px solid #f1f5f9;
  }

  .filter-footer-right {
    display: flex;
    justify-content: flex-end;
    margin-top: 4px;
    padding-top: 4px;
  }

  .integrated-time-selector {
    display: flex;
    width: 100%;
    border-radius: 8px;
    overflow: hidden;
    border: 1px solid #e2e8f0;
    background: #fafbfc;
    
    :deep(.ant-select-selector) {
      border: none !important;
      background: transparent !important;
      box-shadow: none !important;
      border-right: 1px solid #e2e8f0 !important;
      border-radius: 0 !important;
    }
    
    :deep(.ant-picker) {
      border: none !important;
      background: transparent !important;
      box-shadow: none !important;
      padding: 0 11px !important;
    }
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

    .toolbar-btns {
       .premium-btn-small {
        height: 28px;
        padding: 0 12px;
        border-radius: 6px;
        font-size: 12px;
        font-weight: 500;
        display: flex;
        align-items: center;
        gap: 6px;
        border: 1px solid #e2e8f0;
        background: #fff;
        color: #64748b;
        transition: all 0.3s;

        &:hover {
          border-color: #1890ff;
          color: #1890ff;
          background: #f0f7ff;
        }

        &.primary-gradient {
          background: linear-gradient(135deg, #1890ff 0%, #1d4ed8 100%);
          border: none;
          color: #fff;
          
          &:hover {
            box-shadow: 0 4px 10px rgba(24, 144, 255, 0.2);
            transform: translateY(-1px);
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
    
    :deep(.ant-table-thead > tr > th) {
      background: #f8fafc;
      color: #64748b;
      font-weight: 700;
      font-size: 13px;
      border-bottom: 2px solid #f1f5f9;
      padding: 10px 8px; /* Compact Header */
      /* Fix selection column padding */
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
      padding: 8px 8px; /* Compact Body */
      transition: all 0.2s;
    }

    :deep(.ant-table-tbody > tr:hover > td) {
      background: #f0f7ff !important;
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
    &.tag-black { background: #0f172a; color: #fff; }
    &.tag-orange { background: #fff7ed; color: #c2410c; }
    &.tag-blue { background: #eff6ff; color: #1e40af; }
    &.tag-purple { background: #faf5ff; color: #7e22ce; }
    &.tag-success { background: #f0fdf4; color: #15803d; }
    &.tag-red { background: #fef2f2; color: #b91c1c; }
  }

  /* Action Buttons */
  .action-btns-wrapper {
    display: flex;
    gap: 4px;
    align-items: center;
    justify-content: center;
    
    .detail-btn {
      border-radius: 4px;
      font-weight: 500;
      height: 24px;
      padding: 0 8px;
    }
  }

  .virtual-list {
    flex: 1;
    overflow-y: auto;
    &.flex-1 { flex: 1; }
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
  
  .premium-range-group {
      .premium-select :deep(.ant-select-selector) {
          border-top-right-radius: 0 !important;
          border-bottom-right-radius: 0 !important;
          border-right: none !important;
      }
      .premium-datepicker {
          border-top-left-radius: 0 !important;
          border-bottom-left-radius: 0 !important;
      }
  }
  
  :deep(.ant-select-selection-item), :deep(.ant-select-placeholder) {
    line-height: 30px !important;
    font-size: 13px !important;
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
  }
  
  .table-empty-state {
      padding: 80px 0;
      :deep(.ant-empty-description) {
          color: #94a3b8; font-weight: 600; font-size: 14px;
      }
  }
  
  .virtual-header {
      display: flex; background: #f8fafc; border-bottom: 2px solid #f1f5f9; padding-right: 6px; /* scrollbar comp */
      .hcell {
          padding: 10px 8px; font-weight: 700; color: #64748b; font-size: 13px; flex: 1; display: flex; align-items: center; justify-content: center;
      }
  }

  /* Red Bird Sync Group Style */
  .red-bird-sync-group {
    display: flex;
    align-items: center;
    gap: 0;
    height: 30px;
    border-radius: 6px;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    white-space: nowrap;
    flex-shrink: 0;
    width: fit-content;

    &:hover {
      transform: translateY(-1px);
      box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
    }

    .sync-details-box {
      background: #1e293b;
      color: #fff;
      display: flex;
      align-items: center;
      padding: 0 12px;
      height: 100%;
      font-size: 12px;
      gap: 8px;
      border-right: 1px solid rgba(255, 255, 255, 0.1);
      flex-shrink: 0;

      .rb-icon {
        color: #60a5fa;
        font-size: 13px;
      }
      .rb-text {
        font-weight: 500;
        letter-spacing: 0.02em;
      }
      .rb-percent {
        font-weight: 700;
        color: #60a5fa;
        font-family: "JetBrains Mono", monospace;
        min-width: 35px;
        text-align: right;
      }
    }

    .view-progress-box {
      background: #2563eb;
      color: #fff;
      display: flex;
      align-items: center;
      padding: 0 12px;
      height: 100%;
      font-size: 12px;
      font-weight: 600;
      transition: background 0.2s;
      flex-shrink: 0;

      &:hover {
        background: #1d4ed8;
      }
    }
  }

  /* Animation for red-bird */
  .red-bird-slide-enter-active, .red-bird-slide-leave-active {
    transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.1);
  }
  .red-bird-slide-enter-from { 
    opacity: 0; 
    transform: translateX(20px); 
  }
  .red-bird-slide-leave-to { 
    opacity: 0; 
    transform: translateX(20px); 
  }

  @keyframes liquidFlow {
    from { transform: translateX(-100%); }
    to { transform: translateX(100%); }
  }

  /* 同步按钮包装器 */
  .sync-btn-wrapper {
    position: relative;
    display: inline-block;
  }

  /* Premium Config Modal */
  .premium-modal {
    .ant-modal-content {
      border-radius: 16px;
      overflow: hidden;
      padding: 0;
    }
    .ant-modal-header {
      padding: 20px 24px;
      margin-bottom: 0;
      border-bottom: 1px solid #f1f5f9;
      .ant-modal-title {
        font-weight: 700;
        color: #1e293b;
      }
    }
    .ant-modal-body {
      padding: 24px;
    }

    .sync-config-hint {
      display: flex;
      background: #eff6ff;
      border: 1px solid #bfdbfe;
      border-radius: 12px;
      padding: 12px 16px;
      margin-bottom: 24px;
      
      .hint-icon {
        color: #2563eb;
        font-size: 18px;
        margin-right: 12px;
        display: flex;
        align-items: center;
      }
      .hint-text {
        font-size: 13px;
        color: #1e40af;
        line-height: 1.5;
        font-weight: 500;
      }
    }

    .ant-form-item-label {
      font-weight: 600;
      color: #64748b;
      padding-bottom: 8px;
    }

    .ant-picker {
      border-radius: 10px;
      border-color: #e2e8f0;
      height: 44px;
      &:hover, &:focus {
        border-color: #2563eb;
      }
    }
  }
}

/* 美化同步进度弹窗 - 全局样式 */
.sync-progress-modal {
  :deep(.ant-modal-content) {
    border-radius: 20px;
    overflow: hidden;
    box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  }
  
  :deep(.ant-modal-header) {
    display: none;
  }
  
  :deep(.ant-modal-body) {
    padding: 0;
  }
  
  .sync-progress-content {
    padding: 40px 32px;
    text-align: center;
    background: linear-gradient(180deg, #f0f5ff 0%, #ffffff 40%);
  }
  
  .sync-header {
    margin-bottom: 28px;
    
    .sync-icon {
      width: 56px;
      height: 56px;
      border-radius: 16px;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      font-size: 24px;
      margin-bottom: 16px;
      transition: all 0.3s ease;
      
      &.running {
        background: linear-gradient(135deg, #1890ff 0%, #722ed1 100%);
        color: white;
        box-shadow: 0 8px 24px rgba(24, 144, 255, 0.35);
        animation: pulse 2s infinite;
      }
      
      &.success {
        background: linear-gradient(135deg, #52c41a 0%, #389e0d 100%);
        color: white;
        box-shadow: 0 8px 24px rgba(82, 196, 26, 0.35);
      }
      
      &.error {
        background: linear-gradient(135deg, #ff4d4f 0%, #cf1322 100%);
        color: white;
        box-shadow: 0 8px 24px rgba(255, 77, 79, 0.35);
      }
      
      &:not(.running):not(.success):not(.error) {
        background: #f1f5f9;
        color: #64748b;
      }
    }
    
    .sync-title {
      margin: 0;
      font-size: 20px;
      font-weight: 700;
      color: #1e293b;
    }
  }
  
  .sync-progress-ring {
    margin-bottom: 24px;
  }
  
  .sync-message {
    font-size: 15px;
    font-weight: 500;
    color: #475569;
    margin-bottom: 20px;
    min-height: 24px;
  }
  
  .sync-stats {
    display: inline-flex;
    align-items: center;
    gap: 16px;
    padding: 16px 24px;
    background: #f8fafc;
    border-radius: 12px;
    margin-bottom: 28px;
    
    .stat-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      
      .stat-value {
        font-size: 20px;
        font-weight: 700;
        color: #1e293b;
        line-height: 1.2;
      }
      
      .stat-label {
        font-size: 12px;
        color: #94a3b8;
        margin-top: 2px;
      }
      
      &.success .stat-value {
        color: #52c41a;
      }
      
      &.error .stat-value {
        color: #ff4d4f;
      }
    }
    
    .stat-divider {
      color: #cbd5e1;
      font-weight: 300;
      font-size: 18px;
    }
  }
  
  .sync-estimated-time {
    font-size: 13px;
    color: #94a3b8;
    margin-bottom: 16px;
    padding: 8px 16px;
    background: linear-gradient(135deg, #f1f5f9 0%, #e2e8f0 100%);
    border-radius: 8px;
    display: inline-block;
  }
  
  .sync-actions {
    display: flex;
    gap: 12px;
    justify-content: center;
    
    .background-btn {
      height: 40px;
      padding: 0 24px;
      border-radius: 10px;
      font-weight: 500;
      display: inline-flex;
      align-items: center;
      gap: 8px;
      transition: all 0.2s;
      
      &:hover {
        background: #f1f5f9;
        border-color: #e2e8f0;
      }
    }
    
    .cancel-btn {
      height: 40px;
      padding: 0 24px;
      border-radius: 10px;
      font-weight: 500;
    }
    
    .confirm-btn {
      height: 40px;
      padding: 0 32px;
      border-radius: 10px;
      font-weight: 600;
      background: linear-gradient(135deg, #1890ff 0%, #1d4ed8 100%);
      border: none;
      box-shadow: 0 4px 14px rgba(24, 144, 255, 0.35);
      transition: all 0.2s;
      
      &:hover {
        box-shadow: 0 6px 20px rgba(24, 144, 255, 0.45);
        transform: translateY(-1px);
      }
    }
  }
}

/* Premium Icons & Inputs */
.premium-input, .premium-select, .premium-input-search {
  &.ant-input-affix-wrapper,
  &.ant-picker,
  :deep(.ant-input-affix-wrapper),
  :deep(.ant-select-selector) {
    border-radius: 8px !important;
    border-color: #e2e8f0 !important;
    background: #fafbfc !important;
    height: 32px !important;
    transition: all 0.2s;
    
    &:hover { border-color: #cbd5e1 !important; background: #fff !important; }
    
    &.ant-input-affix-wrapper-focused, &.ant-select-focused .ant-select-selector { 
      border-color: #1890ff !important; 
      box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.06) !important;
      background: #ffffff !important;
    }
  }
}

/* Integrated Time Selector Styles */
.integrated-time-selector {
  display: inline-flex !important;
  align-items: center;
  background: #ffffff;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  height: 32px;
  padding: 0;
  overflow: hidden;
  width: 100%;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.02);

  &:hover {
    border-color: #2563eb;
  }

  &:focus-within {
    border-color: #2563eb;
    box-shadow: 0 0 0 2px rgba(37, 99, 235, 0.1);
  }

  .type-select {
    width: 130px;
    flex-shrink: 0;
    :deep(.ant-select-selector) {
      border: none !important;
      box-shadow: none !important;
      height: 30px !important;
      background: transparent !important;
      padding: 0 4px 0 12px !important;
      
      .ant-select-selection-item {
        line-height: 30px !important;
        color: #1e293b !important;
        font-weight: 500 !important;
        font-size: 13px !important;
        text-align: left !important;
      }
    }
    
    :deep(.ant-select-arrow) {
      font-size: 11px;
      right: 8px;
      color: #94a3b8;
    }
    
    position: relative;
    &::after {
      content: "";
      position: absolute;
      right: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 1px;
      height: 16px;
      background: #f1f5f9;
      display: block;
    }
  }

  .range-picker {
    flex: 1;
    border: none !important;
    box-shadow: none !important;
    height: 30px !important;
    background: transparent !important;
    padding: 0 8px !important;
    
    :deep(.ant-picker-input > input) {
      font-size: 12px !important;
      font-weight: 400 !important;
    }
    
    :deep(.ant-picker-separator) {
      color: #94a3b8 !important;
      font-size: 12px !important;
    }

    :deep(.ant-picker-suffix) {
      color: #94a3b8 !important;
      font-size: 12px !important;
    }
  }
}

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
}

@keyframes pulse {
  0%, 100% {
    box-shadow: 0 8px 24px rgba(24, 144, 255, 0.35);
  }
  50% {
    box-shadow: 0 8px 32px rgba(114, 46, 209, 0.45);
  }
}
</style>

<!-- 弹窗全局样式修复：解决 Teleport 导致的 Scoped CSS 失效 -->
<style lang="scss">
.influencer-create-modal {
  .ant-modal-content {
    padding: 0 !important;
    overflow: hidden;
    border-radius: 16px;
    box-shadow: 0 20px 50px rgba(0, 0, 0, 0.15);
  }

  .ic-modal-header {
    display: flex !important;
    align-items: center !important;
    justify-content: space-between !important;
    padding: 16px 24px !important;
    background: #ffffff !important;
    border-bottom: 1px solid #f1f5f9 !important;
    
    &.glass-header {
       background: rgba(255, 255, 255, 0.95) !important;
    }
    
    .ic-header-left {
      display: flex !important;
      align-items: center !important;
      gap: 16px !important;
    }
    
    .ic-header-icon-wrapper {
      flex-shrink: 0 !important;
      width: 40px !important;
      height: 40px !important;
      border-radius: 10px !important;
      background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%) !important;
      color: #ffffff !important;
      display: flex !important;
      align-items: center !important;
      justify-content: center !important;
      font-size: 18px !important;
      box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2) !important;
    }
    
    .ic-header-text {
      flex: 1 !important;
      .ic-header-title {
        font-size: 18px !important;
        font-weight: 700 !important;
        color: #1e293b !important;
        line-height: 1.2 !important;
        margin: 0 !important;
      }
      .ic-header-subtitle {
        font-size: 12px !important;
        color: #94a3b8 !important;
        margin-top: 2px !important;
      }
    }
    
    .close-btn {
      flex-shrink: 0 !important;
      color: #94a3b8 !important;
      &:hover {
        color: #64748b !important;
        background: #f1f5f9 !important;
      }
    }
  }

  .ic-modal-body {
    padding: 24px !important;
    background: #ffffff !important;
    
    .premium-textarea {
      border-radius: 8px !important;
      border-color: #e2e8f0 !important;
      font-size: 14px !important;
      
      &:focus {
        border-color: #1890ff !important;
        box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1) !important;
      }
    }
  }

  .ic-modal-footer {
    padding: 16px 24px !important;
    background: #f8fafc !important;
    border-top: 1px solid #f1f5f9 !important;
    text-align: right !important;
    
    .premium-btn {
      height: 36px !important;
      padding: 0 24px !important;
      border-radius: 8px !important;
      font-weight: 600 !important;
    }
  }
}
</style>

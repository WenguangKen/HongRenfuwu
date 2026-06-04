<template>
  <div class="content-library-page" :class="{ 'is-card-layout': viewMode === 'card' }">
    <!-- 筛选区域 -->
    <a-card :bordered="false" class="filter-card glass-card" :body-style="{ padding: '16px 20px' }">
      <a-form :model="filterForm" layout="vertical" size="middle">
        <a-row :gutter="[20, 16]">
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="任务ID">
              <a-input 
                v-model:value="filterForm.taskId" 
                placeholder="搜索任务ID (双击批量)" 
                allow-clear 
                class="premium-input-search"
                @dblclick="openBatchSearch('taskId')"
              >
                <template #prefix><SearchOutlined style="color: #94a3b8" /></template>
                <template #suffix>
                  <a-tooltip title="点击进行批量搜索">
                    <DatabaseOutlined style="color: #1890ff; cursor: pointer; font-size: 14px" @click="openBatchSearch('taskId')" />
                  </a-tooltip>
                </template>
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="素材链接">
              <a-input v-model:value="filterForm.content" placeholder="请输入素材内容" allow-clear class="premium-input" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="社媒Handle">
              <a-input 
                v-model:value="filterForm.defaultHandle" 
                placeholder="搜索Handle (双击批量)" 
                allow-clear 
                class="premium-input-search"
                @dblclick="openBatchSearch('defaultHandle')"
              >
                <template #prefix><ShareAltOutlined style="color: #94a3b8" /></template>
                <template #suffix>
                  <a-tooltip title="点击进行批量搜索">
                    <DatabaseOutlined style="color: #1890ff; cursor: pointer; font-size: 14px" @click="openBatchSearch('defaultHandle')" />
                  </a-tooltip>
                </template>
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="红人邮箱">
              <a-input 
                v-model:value="filterForm.influencerEmail" 
                placeholder="搜索红人邮箱 (双击批量)" 
                allow-clear 
                class="premium-input-search"
                @dblclick="openBatchSearch('influencerEmail')"
              >
                <template #prefix><MailOutlined style="color: #94a3b8" /></template>
                <template #suffix>
                  <a-tooltip title="点击进行批量搜索">
                    <DatabaseOutlined style="color: #1890ff; cursor: pointer; font-size: 14px" @click="openBatchSearch('influencerEmail')" />
                  </a-tooltip>
                </template>
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="关联红人">
              <a-select
                v-model:value="filterForm.influencer"
                placeholder="搜索红人名称"
                mode="multiple"
                show-search
                :filter-option="false"
                allow-clear
                class="premium-select"
                :max-tag-count="1"
                @search="handleInfluencerRemoteSearch"
              >
                <a-select-option v-for="inf in influencerList" :key="inf.id" :value="inf.nickName || inf.realName">
                  {{ inf.nickName || inf.realName }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="素材标签">
              <a-select
                v-model:value="filterForm.tags"
                placeholder="请选择标签"
                show-search
                :filter-option="filterOption"
                mode="multiple"
                allow-clear
                class="premium-select"
                :options="tagsList"
                :field-names="{ label: 'name', value: 'id' }"
              >
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="商品搜索 (SKU/SPU)">
              <a-input 
                v-model:value="filterForm.spuSku" 
                placeholder="搜索商品SKU (双击批量)" 
                allow-clear 
                class="premium-input-search"
                @dblclick="openBatchSearch('spuSku')"
              >
                <template #prefix><BarcodeOutlined style="color: #94a3b8" /></template>
                <template #suffix>
                  <a-tooltip title="点击进行批量搜索">
                    <DatabaseOutlined style="color: #1890ff; cursor: pointer; font-size: 14px" @click="openBatchSearch('spuSku')" />
                  </a-tooltip>
                </template>
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="关联订单">
              <a-input 
                v-model:value="filterForm.orderNo" 
                placeholder="搜索订单号 (双击批量)" 
                allow-clear 
                class="premium-input-search"
                @dblclick="openBatchSearch('orderNo')"
              >
                <template #prefix><ShoppingCartOutlined style="color: #94a3b8" /></template>
                <template #suffix>
                  <a-tooltip title="点击进行批量搜索">
                    <DatabaseOutlined style="color: #1890ff; cursor: pointer; font-size: 14px" @click="openBatchSearch('orderNo')" />
                  </a-tooltip>
                </template>
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="内容类型">
              <a-select v-model:value="filterForm.contentType" placeholder="全部类型" allow-clear class="premium-select">
                <a-select-option value="IG-Reel">IG-Reel</a-select-option>
                <a-select-option value="IG-feed post">IG-feed post</a-select-option>
                <a-select-option value="Tiktok video">Tiktok video</a-select-option>
                <a-select-option value="Youtube short">Youtube short</a-select-option>
                <a-select-option value="Youtube video">Youtube video</a-select-option>
                <a-select-option value="其它">其它</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="商用状态">
              <a-select v-model:value="filterForm.isCommercial" placeholder="全部状态" allow-clear class="premium-select">
                <a-select-option :value="true">可商用</a-select-option>
                <a-select-option :value="false">不可商用</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="负责人">
              <a-select
                v-model:value="filterForm.uploader"
                placeholder="请选择负责人 (多选)"
                mode="multiple"
                show-search
                :filter-option="filterOption"
                allow-clear
                class="premium-select"
                :max-tag-count="1"
              >
                <a-select-option v-for="u in ownerUsers" :key="u.id" :value="u.name">{{ u.name }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="联络员">
              <a-select
                v-model:value="filterForm.contactPerson"
                placeholder="请选择联络员 (多选)"
                mode="multiple"
                show-search
                :filter-option="filterOption"
                allow-clear
                class="premium-select"
                :max-tag-count="1"
              >
                <a-select-option v-for="name in contactPersonOptions" :key="name" :value="name">{{ name }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <template v-if="filterExpanded">
            <a-col :xs="24" :sm="12" :md="8" :lg="8">
              <a-form-item label="时间筛选">
                <div style="display: flex; gap: 8px; align-items: center;">
                  <a-select v-model:value="filterForm.timeType" style="width: 110px; flex-shrink: 0;">
                    <a-select-option value="upload">上传时间</a-select-option>
                    <a-select-option value="publish">发帖时间</a-select-option>
                  </a-select>
                  <a-range-picker
                    v-model:value="filterForm.timeRange"
                    style="flex: 1;"
                    :placeholder="['开始时间', '结束时间']"
                    format="YYYY-MM-DD"
                    value-format="YYYY-MM-DD"
                    :presets="timeRanges"
                    class="premium-datepicker"
                  />
                </div>
              </a-form-item>
            </a-col>
            <a-col :xs="24" :sm="12" :md="8" :lg="4">
              <a-form-item label="观看数">
                <div style="display: flex; gap: 4px; align-items: center;">
                  <a-input-number v-model:value="filterForm.viewsMin" :min="0" placeholder="最小" style="flex:1" />
                  <span style="color:#94a3b8">~</span>
                  <a-input-number v-model:value="filterForm.viewsMax" :min="0" placeholder="最大" style="flex:1" />
                </div>
              </a-form-item>
            </a-col>
            <a-col :xs="24" :sm="12" :md="8" :lg="4">
              <a-form-item label="点赞数">
                <div style="display: flex; gap: 4px; align-items: center;">
                  <a-input-number v-model:value="filterForm.likesMin" :min="0" placeholder="最小" style="flex:1" />
                  <span style="color:#94a3b8">~</span>
                  <a-input-number v-model:value="filterForm.likesMax" :min="0" placeholder="最大" style="flex:1" />
                </div>
              </a-form-item>
            </a-col>
            <a-col :xs="24" :sm="12" :md="8" :lg="4">
              <a-form-item label="评论数">
                <div style="display: flex; gap: 4px; align-items: center;">
                  <a-input-number v-model:value="filterForm.commentsMin" :min="0" placeholder="最小" style="flex:1" />
                  <span style="color:#94a3b8">~</span>
                  <a-input-number v-model:value="filterForm.commentsMax" :min="0" placeholder="最大" style="flex:1" />
                </div>
              </a-form-item>
            </a-col>
            <a-col :xs="24" :sm="12" :md="8" :lg="4">
              <a-form-item label="收藏数">
                <div style="display: flex; gap: 4px; align-items: center;">
                  <a-input-number v-model:value="filterForm.savesMin" :min="0" placeholder="最小" style="flex:1" />
                  <span style="color:#94a3b8">~</span>
                  <a-input-number v-model:value="filterForm.savesMax" :min="0" placeholder="最大" style="flex:1" />
                </div>
              </a-form-item>
            </a-col>
            <a-col :xs="24" :sm="12" :md="8" :lg="4">
              <a-form-item label="分享数">
                <div style="display: flex; gap: 4px; align-items: center;">
                  <a-input-number v-model:value="filterForm.sharesMin" :min="0" placeholder="最小" style="flex:1" />
                  <span style="color:#94a3b8">~</span>
                  <a-input-number v-model:value="filterForm.sharesMax" :min="0" placeholder="最大" style="flex:1" />
                </div>
              </a-form-item>
            </a-col>
            <a-col :xs="24" :sm="12" :md="8" :lg="4">
              <a-form-item label="ER(%)">
                <div style="display: flex; gap: 4px; align-items: center;">
                  <a-input-number v-model:value="filterForm.erMin" :min="0" :step="0.1" placeholder="最小" style="flex:1" />
                  <span style="color:#94a3b8">~</span>
                  <a-input-number v-model:value="filterForm.erMax" :min="0" :step="0.1" placeholder="最大" style="flex:1" />
                </div>
              </a-form-item>
            </a-col>
          </template>

          <a-col :xs="24" :sm="24" :md="24" :lg="24">
            <div class="filter-footer" style="padding-top: 0; border-top: none; margin-top: 0;">
              <a-space size="middle">
                <a-button type="primary" @click="handleFilter" class="premium-btn primary-gradient" style="height: 32px; padding: 0 20px;">
                  查询
                </a-button>
                <a-button @click="handleResetFilter" class="premium-btn" style="height: 32px; padding: 0 20px;">重置</a-button>
                <a-button type="link" @click="filterExpanded = !filterExpanded" class="expand-btn" style="font-size: 13px;">
                  {{ filterExpanded ? '收起筛选' : '更多筛选' }} <component :is="filterExpanded ? 'up-outlined' : 'down-outlined'" />
                </a-button>
              </a-space>
            </div>
          </a-col>
        </a-row>
      </a-form>
    </a-card>

    <!-- Export Modal -->
    <InfluencerExportModal
      v-model:open="exportModalVisible"
      :selected-count="selectedRowKeys.length"
      :export-fields="contentExportFields"
      page-type="content-library"
      :current-user-id="currentUserId"
      @export="handleExportFromModal"
    />

    <!-- 表格区域 -->
    <a-card :bordered="false" class="table-card glass-card" :class="{ 'is-card-view': viewMode === 'card' }" :body-style="{ padding: '0' }">
      <template #title>
        <div class="table-actions-toolbar">
          <div class="status-switcher-wrapper">
             <span style="font-weight: 700; font-size: 16px; color: #1e293b;">内容列表</span>
             <span v-if="viewMode === 'card'" class="card-total-hint">共 {{ pagination.total }} 个素材</span>
          </div>
          
          <a-space size="small" class="toolbar-btns">
            <a-radio-group v-model:value="viewMode" button-style="solid" size="small" class="view-mode-toggle">
              <a-radio-button value="table">
                <unordered-list-outlined /> 列表
              </a-radio-button>
              <a-radio-button value="card">
                <appstore-outlined /> 卡片
              </a-radio-button>
            </a-radio-group>
            <a-button class="premium-btn-small" v-permission="'content.library.export'" @click="handleExport">
              <template #icon><export-outlined /></template>导出
            </a-button>
            <a-button 
              type="primary" 
              class="premium-btn-small primary-gradient" 
              :disabled="selectedRowKeys.length === 0"
              :loading="batchDownloading"
              @click="handleBatchDownload"
            >
              <template #icon><download-outlined /></template>批量下载
            </a-button>
            <a-button 
              type="primary" 
              class="premium-btn-small primary-gradient" 
              :disabled="selectedRowKeys.length === 0"
              @click="batchTagModalVisible = true"
            >
              <template #icon><tag-outlined /></template>批量打标签
            </a-button>
          </a-space>
        </div>
      </template>

      <div v-if="selectedRowKeys.length > 0" class="batch-selection-bar">
        <a-space>
          <span class="selection-info">已选择 <span class="count">{{ selectedRowKeys.length }}</span> 项内容</span>
          <a-button type="link" size="small" @click="selectedRowKeys = []">取消选择</a-button>
          <a-divider type="vertical" />
          <a-button type="link" size="small" :loading="batchDownloading" @click="handleBatchDownload">
            <template #icon><download-outlined /></template>批量下载
          </a-button>
          <a-divider type="vertical" />
          <a-popconfirm
            title="确定删除选中内容？将同时删除待上传任务和内容库中的所有关联内容及文件。"
            ok-text="确定删除"
            cancel-text="取消"
            @confirm="handleBatchDelete"
            placement="bottomLeft"
            :ok-button-props="{ danger: true }"
          >
            <a-button type="link" size="small" danger class="action-link danger" v-permission="'content.library.delete'">
              <template #icon><delete-outlined /></template>批量删除
            </a-button>
          </a-popconfirm>
        </a-space>
      </div>

      <a-table
        v-if="viewMode === 'table'"
        :columns="columns"
        :data-source="data"
        :row-key="(record: any) => record.key"
        :pagination="false"
        :loading="{ spinning: loading, indicator: LoadingIcon }"
        :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
        :scroll="{ x: 2740 }"
        :sticky="{ offsetHeader: 0 }"
        class="premium-table"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'taskId'">
            <div style="font-weight: 700; color: #1e293b;">{{ record.taskId }}</div>
          </template>
          <template v-else-if="column.key === 'order'">
            <div v-if="record.order">
              <span style="color: #64748b; font-size: 12px; font-family: 'JetBrains Mono', monospace;">{{ record.order.orderNo }}</span>
            </div>
            <span v-else style="color: #cbd5e1;">-</span>
          </template>
          <template v-else-if="column.key === 'sku'">
            <div v-if="record.skus && record.skus.length > 0" style="display: flex; flex-direction: column; gap: 6px; width: 100%; min-width: 160px; padding: 4px 0;">
              <div v-for="(sku, idx) in record.skus" :key="idx" 
                   style="display: flex; align-items: center; background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 6px; padding: 2px 8px; width: fit-content; max-width: 100%; overflow: hidden; white-space: nowrap;">
                <span style="color: #0369a1; font-weight: 700; font-size: 12px; font-family: monospace;">{{ extractMainSkuCode(sku) }}</span>
                <template v-if="sku.includes('-')">
                  <span v-for="(spec, sIdx) in dedupeSkuSubString(sku.slice(extractMainSkuCode(sku).length + 1)).split('-')" :key="sIdx"
                        :style="{ 
                          color: ['#16a34a', '#ea580c', '#8b5cf6'][sIdx % 3], 
                          fontWeight: '600',
                          fontSize: '12px'
                        }">
                    -{{ spec.trim() }}
                  </span>
                </template>
              </div>
            </div>
            <span v-else style="color: #cbd5e1;">-</span>
          </template>
          <template v-else-if="column.key === 'media'">
            <div class="media-scroll-container" style="display: flex; gap: 8px; flex-wrap: nowrap; overflow-x: auto; max-width: 280px; padding-bottom: 6px;">
              <template v-if="record.media && record.media.length > 0">
                <div 
                  v-for="(item, index) in record.media" 
                  :key="index"
                  style="position: relative; cursor: pointer; flex-shrink: 0;" 
                  class="media-item" 
                  @click="previewMedia(record.media, index)"
                >
                  <img 
                    v-if="item.type === 'image'" 
                    :src="item.thumbnailUrl ? optimizeShopifyImage(item.thumbnailUrl, '200x200') : optimizeShopifyImage(item.url, '200x200')" 
                    alt="素材图片"
                    class="media-preview"
                    loading="lazy"
                    decoding="async"
                    style="width: 60px; height: 60px; object-fit: cover;"
                    @error="handleImgError($event, item)"
                  />
                  <div 
                    v-else-if="item.type === 'video'"
                    class="media-preview video-preview"
                    style="width: 60px; height: 60px;"
                  >
                    <img 
                      v-if="item.thumbnailUrl" 
                      :src="item.thumbnailUrl" 
                      alt="视频缩略图"
                      style="width: 60px; height: 60px; object-fit: cover; border-radius: 6px;"
                      loading="lazy"
                      decoding="async"
                      @error="(e: Event) => { const img = e.target as HTMLImageElement; img.style.display = 'none'; }"
                    />
                    <VideoFrameThumb 
                      v-else 
                      :src="item.url" 
                      :width="60" 
                      :height="60" 
                    />
                    <play-circle-outlined style="position: absolute; font-size: 24px; color: #fff; filter: drop-shadow(0 2px 4px rgba(0,0,0,0.3));" />
                    <div class="video-duration">{{ item.duration || 'Video' }}</div>
                  </div>
                </div>
              </template>
              <span v-else style="color: #cbd5e1;">-</span>
            </div>
          </template>
          <template v-else-if="column.key === 'link'">
            <div v-if="record.link">
              <a-tooltip :title="record.link" placement="topLeft">
                <a :href="record.link" target="_blank" style="color: #3b82f6; font-size: 13px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; display: block; max-width: 180px; margin-bottom: 4px;">
                  {{ record.link }}
                </a>
              </a-tooltip>
              <a-button size="small" type="link" style="padding: 0; height: auto; font-size: 12px; color: #64748b;" @click="copyLink(record.link)">
                <copy-outlined /> 复制链接
              </a-button>
            </div>
            <span v-else style="color: #cbd5e1;">-</span>
          </template>
          <template v-else-if="column.key === 'tags'">
            <div class="pill-tags-wrapper">
              <a-select
                mode="multiple"
                :value="record.tagIds"
                placeholder="选择标签"
                style="width: 100%"
                :bordered="false"
                class="inline-tag-select"
                @change="(val: number[]) => handleUpdateTag(record, val)"
              >
                <a-select-option v-for="tag in tagsList" :key="tag.id" :value="tag.id">
                  {{ tag.name }}
                </a-select-option>
              </a-select>
            </div>
          </template>
          <template v-else-if="column.key === 'socialData'">
            <div class="social-data-grid">
              <div class="social-item"><span class="label">观看</span><span class="value view">{{ record.socialData.views || 0 }}</span></div>
              <div class="social-item"><span class="label">点赞</span><span class="value like">{{ record.socialData.likes || 0 }}</span></div>
              <div class="social-item"><span class="label">评论</span><span class="value comment">{{ record.socialData.comments || 0 }}</span></div>
              <div class="social-item"><span class="label">收藏</span><span class="value save" style="color:#d97706">{{ record.socialData.saves || 0 }}</span></div>
              <div class="social-item"><span class="label">分享</span><span class="value share">{{ record.socialData.shares || 0 }}</span></div>
            </div>
          </template>
          <template v-else-if="column.key === 'engagementRate'">
            <a-tooltip v-if="record.engagementRate != null && record.engagementRate !== '-'">
              <template #title>
                <div style="font-size: 12px; line-height: 1.6;">
                  <div v-if="record.erFormula === 'video'">视频类 ER = (点赞+评论+分享) / 观看量 × 100%</div>
                  <div v-else>图文类 ER = (点赞+评论+收藏) / 粉丝数 × 100%</div>
                </div>
              </template>
              <a-tag :color="Number(record.engagementRate) >= 5 ? 'green' : Number(record.engagementRate) >= 2 ? 'blue' : 'default'" style="font-weight: 600; font-size: 13px;">
                {{ record.engagementRate }}%
              </a-tag>
            </a-tooltip>
            <span v-else style="color: #cbd5e1;">-</span>
          </template>
          <template v-else-if="column.key === 'isCommercial'">
            <a-tag :color="record.isCommercial ? 'success' : 'warning'" class="pill-tag">
              {{ record.isCommercial ? '可商用' : '不可商用' }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'socialDataUpdatedAt'">
            <div style="color: #475569; font-weight: 600; font-size: 13px;">
              {{ record.socialDataUpdatedAt ? dayjs(record.socialDataUpdatedAt).format('YYYY-MM-DD') : '-' }}
            </div>
          </template>
          <template v-else-if="column.key === 'uploadTime'">
            <div style="color: #475569; font-weight: 600; font-size: 13px;">
              {{ record.uploadTime ? dayjs(record.uploadTime).format('YYYY-MM-DD') : '-' }}
            </div>
          </template>
          <template v-else-if="column.key === 'publishDate'">
            <div style="color: #475569; font-weight: 600; font-size: 13px;">
              {{ record.publishDate ? dayjs(record.publishDate).format('YYYY-MM-DD') : '-' }}
            </div>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space :size="8">
              <a-button type="link" size="small" @click="openSocialModal(record)" class="action-link info">
                社媒数据
              </a-button>
              <a-divider type="vertical" />
              <a-popconfirm
                title="确定删除该素材？将同时删除待上传任务和内容库中的所有关联内容。"
                ok-text="确定删除"
                cancel-text="取消"
                @confirm="handleDelete(record)"
                placement="topRight"
                :ok-button-props="{ danger: true }"
              >
                <a-button type="link" size="small" class="action-link danger" v-permission="'content.library.delete'">
                  删除
                </a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>

      <a-spin v-else :spinning="loading" wrapper-class-name="content-card-view">
        <div v-if="data.length === 0 && !loading" class="card-empty">
          <a-empty description="暂无内容数据" />
        </div>
        <div v-else class="content-card-grid">
          <div
            v-for="record in data"
            :key="record.key"
            class="content-card content-card-ref"
            :class="{ 'is-selected': selectedRowKeys.includes(record.key) }"
          >
            <div
              class="card-cover"
              :class="cardMediaGridClass(record.media?.length || 0)"
            >
              <div class="card-check" @click.stop>
                <a-checkbox
                  :checked="selectedRowKeys.includes(record.key)"
                  @change="(e: any) => setCardSelected(record.key, e.target.checked)"
                />
              </div>

              <template v-if="record.media && record.media.length > 0">
                <div
                  v-for="tile in getCardMediaTiles(record.media)"
                  :key="`${record.key}-${tile.type === 'more' ? 'more-grid' : tile.index}`"
                  class="card-cover-tile"
                  :class="{ 'is-more-grid': tile.type === 'more' }"
                  @click.stop="tile.type !== 'more' && previewMedia(record.media, tile.index ?? 0)"
                >
                  <template v-if="tile.type === 'more' && tile.moreVisible?.length">
                    <div
                      class="more-thumb-grid"
                      :class="moreThumbGridClass(tile.moreVisible.length, (tile.moreOverflow ?? 0) > 0)"
                    >
                      <div
                        v-for="(moreItem, mi) in tile.moreVisible"
                        :key="mi"
                        class="more-thumb-cell"
                        @click.stop="previewMedia(record.media, 3 + mi)"
                      >
                        <img
                          v-if="moreItem.type === 'image'"
                          :src="moreItem.thumbnailUrl ? optimizeShopifyImage(moreItem.thumbnailUrl, '100x100') : optimizeShopifyImage(moreItem.url, '100x100')"
                          alt="素材"
                          class="cover-img"
                          loading="lazy"
                          @error="handleImgError($event, moreItem)"
                        />
                        <div v-else class="cover-video more-thumb-video">
                          <img
                            v-if="moreItem.thumbnailUrl"
                            :src="moreItem.thumbnailUrl"
                            alt="视频"
                            class="cover-img"
                            loading="lazy"
                          />
                          <VideoFrameThumb v-else :src="moreItem.url" :width="48" :height="48" />
                          <play-circle-outlined class="cover-play cover-play-sm" />
                        </div>
                      </div>
                      <div
                        v-if="(tile.moreOverflow ?? 0) > 0"
                        class="more-thumb-cell is-overflow"
                        @click.stop="previewMedia(record.media, 3 + tile.moreVisible!.length)"
                      >
                        <span class="more-count-sm">+{{ tile.moreOverflow }}</span>
                      </div>
                    </div>
                  </template>
                  <template v-else-if="tile.item">
                    <img
                      v-if="tile.item.type === 'image'"
                      :src="tile.item.thumbnailUrl ? optimizeShopifyImage(tile.item.thumbnailUrl, '300x300') : optimizeShopifyImage(tile.item.url, '300x300')"
                      alt="素材"
                      class="cover-img"
                      loading="lazy"
                      @error="handleImgError($event, tile.item)"
                    />
                    <div v-else class="cover-video">
                      <img
                        v-if="tile.item.thumbnailUrl"
                        :src="tile.item.thumbnailUrl"
                        alt="视频"
                        class="cover-img"
                        loading="lazy"
                      />
                      <VideoFrameThumb v-else :src="tile.item.url" :width="180" :height="320" />
                      <play-circle-outlined class="cover-play" />
                    </div>
                  </template>
                </div>
              </template>
              <div v-else class="card-cover-empty">暂无预览</div>

              <span class="card-platform-tag">{{ record.contentType || record.platform }}</span>

              <a-tooltip v-if="record.media?.length" title="下载素材">
                <button type="button" class="card-download-btn" @click.stop="handleDownload(record)">
                  <download-outlined />
                </button>
              </a-tooltip>

              <a-dropdown :trigger="['click']" placement="bottomRight" @click.stop>
                <button type="button" class="card-more-btn" @click.stop>
                  <ellipsis-outlined />
                </button>
                <template #overlay>
                  <a-menu>
                    <a-menu-item v-if="record.link" @click="openExternalLink(record.link)">
                      <link-outlined /> 打开链接
                    </a-menu-item>
                    <a-menu-item @click="openSocialModal(record)">社媒数据</a-menu-item>
                    <a-menu-item v-if="record.link" @click="copyLink(record.link)">复制链接</a-menu-item>
                    <a-menu-item v-permission="'content.library.delete'" danger @click="confirmDeleteCard(record)">
                      删除
                    </a-menu-item>
                  </a-menu>
                </template>
              </a-dropdown>
            </div>

            <div class="card-ref-footer">
              <div class="card-influencer-name" :title="record.influencer">{{ record.influencer || '-' }}</div>
              <div v-if="record.skuDisplayMain || record.skus?.length" class="card-sku-line">
                SKU: {{ record.skuDisplayMain || record.skus[0] }}
              </div>
              <div class="card-date-line">
                发布于 {{ record.publishDate ? dayjs(record.publishDate).format('YYYY-MM-DD') : '-' }}
              </div>
              <div class="card-social-grid social-data-grid" @click.stop="openSocialModal(record)" title="点击编辑社媒数据">
                <div class="social-item">
                  <span class="label">观看</span>
                  <span class="value view">{{ record.socialData?.views ?? 0 }}</span>
                </div>
                <div class="social-item">
                  <span class="label">点赞</span>
                  <span class="value like">{{ record.socialData?.likes ?? 0 }}</span>
                </div>
                <div class="social-item">
                  <span class="label">评论</span>
                  <span class="value comment">{{ record.socialData?.comments ?? 0 }}</span>
                </div>
                <div class="social-item">
                  <span class="label">收藏</span>
                  <span class="value save">{{ record.socialData?.saves ?? 0 }}</span>
                </div>
                <div class="social-item">
                  <span class="label">分享</span>
                  <span class="value share">{{ record.socialData?.shares ?? 0 }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </a-spin>

      <div class="pagination-footer">
        <div class="footer-left">
          <span class="info-item">当前任务数量 <span class="count-value">{{ pagination.total }}</span></span>
        </div>
        <div class="footer-right">
          <a-pagination
            v-model:current="pagination.current"
            v-model:pageSize="pagination.pageSize"
            :total="pagination.total"
            :show-size-changer="true"
            :show-quick-jumper="true"
            @change="handlePaginationChange"
            @showSizeChange="handlePaginationSizeChange"
          />
        </div>
      </div>
    </a-card>

    <PreviewImageModal
      v-model:open="imagePreviewVisible"
      :data="previewData"
      :data-list="previewMediaList"
      :initial-index="previewInitialIndex"
    />

    <!-- 编辑社媒数据弹窗 -->
    <a-modal
      v-model:open="socialModalVisible"
      title="编辑社交媒体数据"
      @ok="handleUpdateSocialMetrics"
      @cancel="socialModalVisible = false"
      :confirmLoading="socialModalLoading"
      class="premium-modal"
    >
      <div style="padding: 24px;">
        <a-form :model="socialForm" layout="vertical">
          <a-row :gutter="24">
            <a-col :span="12">
              <a-form-item label="视频观看/播放数">
                <a-input-number v-model:value="socialForm.views" :min="0" style="width: 100%" class="premium-input-number" />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="点赞数">
                <a-input-number v-model:value="socialForm.likes" :min="0" style="width: 100%" class="premium-input-number" />
              </a-form-item>
            </a-col>
          </a-row>
          <a-row :gutter="24" style="margin-top: 16px;">
            <a-col :span="12">
              <a-form-item label="评论数">
                <a-input-number v-model:value="socialForm.comments" :min="0" style="width: 100%" class="premium-input-number" />
              </a-form-item>
            </a-col>
            <a-col :span="12">
              <a-form-item label="分享/转发数">
                <a-input-number v-model:value="socialForm.shares" :min="0" style="width: 100%" class="premium-input-number" />
              </a-form-item>
            </a-col>
          </a-row>
          <a-row :gutter="24" style="margin-top: 16px;">
            <a-col :span="12">
              <a-form-item label="收藏数">
                <a-input-number v-model:value="socialForm.saves" :min="0" style="width: 100%" class="premium-input-number" />
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
      </div>
    </a-modal>

    <!-- 批量搜索弹窗 -->
    <a-modal
      v-model:open="batchSearchVisible"
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
            <SearchOutlined />
          </div>
          <div class="ic-header-text">
            <div class="ic-header-title">{{ batchSearchTitles[batchSearchType]?.title }}</div>
            <div class="ic-header-subtitle">{{ batchSearchTitles[batchSearchType]?.subtitle }}</div>
          </div>
        </div>
        <a-button type="text" class="close-btn" @click="batchSearchVisible = false">
          <CloseOutlined style="font-size: 16px; color: #94a3b8" />
        </a-button>
      </div>
      
      <div class="ic-modal-body" style="background: #fff; padding: 24px;">
        <a-textarea
          v-model:value="batchSearchValue"
          :placeholder="batchSearchPlaceholder"
          :rows="10"
          class="premium-textarea"
          style="margin-bottom: 0;"
        />
      </div>

      <div class="ic-modal-footer">
        <a-space size="middle">
          <a-button @click="batchSearchVisible = false" class="premium-btn">取消</a-button>
          <a-button type="primary" @click="handleBatchSearch" class="premium-btn primary-gradient">开始搜索</a-button>
        </a-space>
      </div>
    </a-modal>

    <!-- 批量打标签弹窗 -->
    <a-modal
      v-model:open="batchTagModalVisible"
      title="批量打标签"
      @ok="handleBatchTag"
      @cancel="batchTagModalVisible = false"
      :confirmLoading="batchTagLoading"
      class="premium-modal"
    >
      <div style="padding: 20px 0;">
        <p style="color: #64748b; margin-bottom: 12px;">将为选中的 {{ selectedRowKeys.length }} 项内容应用以下标签：</p>
        <a-select
          v-model:value="batchTagIds"
          mode="multiple"
          placeholder="请选择标签"
          style="width: 100%"
          class="premium-select"
        >
          <a-select-option v-for="tag in tagsList" :key="tag.id" :value="tag.id">
            {{ tag.name }}
          </a-select-option>
        </a-select>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, h, onMounted, watch } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { message, Modal } from 'ant-design-vue';
import type { TableColumnsType } from 'ant-design-vue';
import { createExportTask } from '@/utils/exportTaskHelper';
import { fetchAllPages } from '@/utils/dataHelper';
import { downloadContentRecordAsZip, delay } from '@/utils/contentDownload';
import InfluencerExportModal from '@/components/influencer/InfluencerExportModal.vue';
import dayjs from 'dayjs';
import PreviewImageModal from '@/components/content/PreviewImageModal.vue';
import VideoFrameThumb from '@/components/content/VideoFrameThumb.vue';
import contentService, { type ContentDto, deleteContent, batchDeleteContents } from '@/services/contentService';
import influencerService from '@/services/influencerService';
import { useCommonDataStore } from '@/stores/commonData';
import { storeToRefs } from 'pinia';
import { 
  DownOutlined, UpOutlined, LoadingOutlined, ExportOutlined,
  SearchOutlined, CloseOutlined, DatabaseOutlined, PlayCircleOutlined,
  ExclamationCircleFilled, TagOutlined, MailOutlined, ShareAltOutlined,
  BarcodeOutlined, ShoppingCartOutlined, CopyOutlined, LinkOutlined,
  AppstoreOutlined, UnorderedListOutlined, DownloadOutlined, EllipsisOutlined
} from '@ant-design/icons-vue';

type ViewMode = 'table' | 'card';
const VIEW_MODE_STORAGE_KEY = 'content-library-view-mode';

const viewMode = ref<ViewMode>(
  (localStorage.getItem(VIEW_MODE_STORAGE_KEY) as ViewMode) === 'card' ? 'card' : 'table'
);

watch(viewMode, (mode) => {
  localStorage.setItem(VIEW_MODE_STORAGE_KEY, mode);
});

// 格式化文件大小
const formatFileSize = (bytes: number): string => {
  if (bytes < 1024) return bytes + ' B';
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
  if (bytes < 1024 * 1024 * 1024) return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
  return (bytes / (1024 * 1024 * 1024)).toFixed(2) + ' GB';
};

const dedupeTrimStrings = (parts: string[]): string[] => {
  const seen = new Set<string>();
  const out: string[] = [];
  for (const p of parts) {
    const t = p.trim();
    if (!t || seen.has(t)) continue;
    seen.add(t);
    out.push(t);
  }
  return out;
};

/** 单条 SKU 文本中的主编码（与表格旧逻辑一致：带大数字段时用 _ 前缀，否则取首个 - 前） */
const extractMainSkuCode = (rawSku: string): string => {
  const first = rawSku.trim();
  if (!first) return '';
  if (first.includes('_') && first.split('_').some((seg: string) => /^\d{5,}$/.test(seg))) {
    return first.split('_')[0] ?? '';
  }
  const dash = first.indexOf('-');
  if (dash > 0) return first.slice(0, dash);
  return first;
};

const truncateSkuSub = (s: string, max = 120): string => {
  if (s.length <= max) return s;
  return `${s.slice(0, max - 1)}…`;
};

const dedupeSkuSubString = (s: string): string => {
  if (!s || !s.includes('-')) return s;
  const parts = s.split('-').map(p => p.trim()).filter(Boolean);
  if (parts.length <= 1) return s;
  
  // 检查是否存在重复的段落（例如 A-B-A-B 或 A-A）
  // 稳健策略：提取唯一段落并按原顺序重新组合
  const seen = new Set<string>();
  const uniqueParts: string[] = [];
  for (const p of parts) {
    if (!seen.has(p)) {
      seen.add(p);
      uniqueParts.push(p);
    }
  }
  return uniqueParts.join('-');
};

const applySkuDisplayFields = (row: any) => {
  const skus: string[] = row.skus || [];
  if (skus.length === 0) {
    row.skuDisplayMain = '';
    row.skuDisplaySub = '';
    return;
  }
  if (skus.length > 1) {
    // 提取去重后的主编码列表
    const mainCodes = dedupeTrimStrings(skus.map(extractMainSkuCode).filter(Boolean));
    const shown = mainCodes.slice(0, 3);
    row.skuDisplayMain = shown.join(' · ') + (mainCodes.length > 3 ? ` +${mainCodes.length - 3}` : '');
    row.skuDisplaySub = '';
    return;
  }
  const firstSku = skus[0]!;
  const code = extractMainSkuCode(firstSku);
  let sub = String(row.variantTitle || '').trim();
  if (!sub && firstSku.length > code.length + 1) {
    sub = firstSku.slice(code.length + 1).replace(/^-+/, '').trim();
  }
  
  // 处理重复规格的显示修正
  if (sub) sub = dedupeSkuSubString(sub);

  row.skuDisplayMain = code;
  row.skuDisplaySub = sub ? truncateSkuSub(sub) : '';
};

const skuTooltipText = (record: any): string => {
  if (!record?.skus?.length) return '';
  // 按主编码分组展示，避免重复
  const grouped = new Map<string, string[]>();
  for (const sku of record.skus) {
    const main = extractMainSkuCode(sku);
    let sub = sku.length > main.length + 1 ? sku.slice(main.length + 1).replace(/^-+/, '').trim() : '';
    
    // 修正重复规格
    if (sub) sub = dedupeSkuSubString(sub);

    if (!grouped.has(main)) grouped.set(main, []);
    if (sub && !grouped.get(main)!.includes(sub)) grouped.get(main)!.push(sub);
  }
  const lines: string[] = [];
  for (const [main, subs] of grouped) {
    if (subs.length === 0) {
      lines.push(main);
    } else if (subs.length <= 3) {
      lines.push(`${main}: ${subs.join(', ')}`);
    } else {
      lines.push(`${main}: ${subs.slice(0, 3).join(', ')} +${subs.length - 3}`);
    }
  }
  return lines.join('\n');
};

/** 与列表一致：同一 taskId + contentGroupIndex 合并多文件行，SKU 去重 */
const mergeGroupedContentRows = (tempData: any[]): any[] => {
  const groupedMap = new Map<string, any>();
  tempData.forEach((item: any) => {
    // 关键修复：确保即使没有 taskId，也能独立成行而不被错误合并
    const gid = item.taskId && item.taskId !== '-' ? item.taskId : `RAW-${item.id}`;
    const groupKey = `${gid}_${item.contentGroupIndex ?? 0}`;
    
    if (!groupedMap.has(groupKey)) {
      const merged = { ...item, media: [...(item.media || [])], skus: [...(item.skus || [])] };
      applySkuDisplayFields(merged);
      groupedMap.set(groupKey, merged);
    } else {
      const existing = groupedMap.get(groupKey);
      if (item.media?.length) existing.media.push(...item.media);
      if (item.skus?.length) {
        existing.skus = dedupeTrimStrings([...(existing.skus || []), ...item.skus]);
      }
      if (!String(existing.variantTitle || '').trim() && String(item.variantTitle || '').trim()) {
        existing.variantTitle = item.variantTitle;
      }
      applySkuDisplayFields(existing);
    }
  });
  return Array.from(groupedMap.values());
};

type CardMediaTile = {
  type: 'image' | 'video' | 'more';
  item?: any;
  index?: number;
  moreItems?: any[];
  moreVisible?: any[];
  moreOverflow?: number;
  moreCount?: number;
};

/** 第 4 格迷你网格：最多展示 9 张，超出在最后格显示 +N */
const getMoreThumbDisplay = (items: any[]) => {
  if (items.length <= 9) {
    return { visible: items, overflow: 0 };
  }
  return { visible: items.slice(0, 8), overflow: items.length - 8 };
};

const moreThumbGridClass = (count: number, hasOverflow: boolean) => {
  const total = hasOverflow ? count + 1 : count;
  if (total <= 1) return 'cells-1';
  if (total === 2) return 'cells-2';
  if (total === 3) return 'cells-3';
  if (total === 4) return 'cells-4';
  if (total <= 6) return 'cells-6';
  return 'cells-9';
};

/** 卡片封面：1 张全屏，2~3 张并排/网格，4 张 2×2，5+ 前三格大图 + 第四格缩略图网格 */
const getCardMediaTiles = (media: any[]): CardMediaTile[] => {
  if (!media?.length) return [];
  if (media.length === 1) {
    return [{ type: media[0].type === 'video' ? 'video' : 'image', item: media[0], index: 0 }];
  }
  if (media.length === 2) {
    return media.map((item, index) => ({
      type: item.type === 'video' ? 'video' : 'image',
      item,
      index,
    }));
  }
  if (media.length === 3) {
    return media.map((item, index) => ({
      type: item.type === 'video' ? 'video' : 'image',
      item,
      index,
    }));
  }
  if (media.length === 4) {
    return media.map((item, index) => ({
      type: item.type === 'video' ? 'video' : 'image',
      item,
      index,
    }));
  }
  const tiles: CardMediaTile[] = media.slice(0, 3).map((item, index) => ({
    type: item.type === 'video' ? 'video' : 'image',
    item,
    index,
  }));
  const remaining = media.slice(3);
  const { visible, overflow } = getMoreThumbDisplay(remaining);
  tiles.push({
    type: 'more',
    moreItems: remaining,
    moreVisible: visible,
    moreOverflow: overflow,
    moreCount: remaining.length,
    index: 3,
  });
  return tiles;
};

const cardMediaGridClass = (count: number) => {
  if (count <= 1) return 'tiles-1';
  if (count === 2) return 'tiles-2';
  if (count === 3) return 'tiles-3';
  return 'tiles-4';
};

const copyLink = async (link: string) => {
  try {
    await navigator.clipboard.writeText(link);
    message.success('链接已复制');
  } catch {
    message.error('复制失败');
  }
};

const openExternalLink = (link: string) => {
  window.open(link, '_blank', 'noopener,noreferrer');
};

const router = useRouter();
const route = useRoute();
const filterExpanded = ref(false);
const previewMediaList = ref<any[]>([]);
const previewInitialIndex = ref(0);
const exportModalVisible = ref(false);
const currentUserId = ref<string>('default_user');
const socialModalVisible = ref(false);
const socialModalLoading = ref(false);
// Optimization: Resize Shopify images to improve performance
const optimizeShopifyImage = (url?: string, size = '200x200') => {
  if (!url || !url.includes('cdn.shopify.com')) return url;
  try {
    const parts = url.split('.');
    const ext = parts.pop()?.split('?')[0];
    const query = url.split('?')[1] || '';
    if (!ext) return url;
    const base = url.split(`.${ext}`)[0];
    return `${base}_${size}.${ext}${query ? '?' + query : ''}`;
  } catch (e) {
    return url;
  }
};

// 图片加载失败处理：先尝试原始URL，再回退到占位图
const handleImgError = (e: Event, item: any) => {
  const img = e.target as HTMLImageElement;
  if (item.url && !img.dataset.fallback) {
    img.dataset.fallback = '1';
    img.src = item.url;
  } else {
    // 原始图和缩略图都失败，显示占位符
    img.style.display = 'none';
    const placeholder = document.createElement('div');
    placeholder.className = 'media-preview media-placeholder';
    placeholder.style.cssText = 'width:60px;height:60px;display:flex;align-items:center;justify-content:center;background:#f1f5f9;border-radius:6px;border:1px dashed #cbd5e1;';
    placeholder.innerHTML = '<svg viewBox="64 64 896 896" width="20" height="20" fill="#94a3b8"><path d="M928 160H96c-17.7 0-32 14.3-32 32v640c0 17.7 14.3 32 32 32h832c17.7 0 32-14.3 32-32V192c0-17.7-14.3-32-32-32zm-40 632H136v-39.9l138.5-164.3 150.1 178L658.1 489 888 761.6V792zm0-129.8L664.2 396.8c-3.2-3.8-9-3.8-12.2 0L424.6 666.4l-144-170.7c-3.2-3.8-9-3.8-12.2 0L136 652.7V232h752v390.2z"/><path d="M304 456a88 88 0 100-176 88 88 0 000 176zm0-116c15.5 0 28 12.5 28 28s-12.5 28-28 28-28-12.5-28-28 12.5-28 28-28z"/></svg>';
    img.parentElement?.appendChild(placeholder);
  }
};

const socialForm = reactive({
  id: null as number | null,
  views: 0,
  likes: 0,
  comments: 0,
  shares: 0,
  saves: 0
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
const imagePreviewVisible = ref(false);
const previewData = ref<any>(null);

const selectedRowKeys = ref<number[]>([]);
const batchTagModalVisible = ref(false);
const batchTagLoading = ref(false);
const batchTagIds = ref<number[]>([]);

// Custom loading indicator for table
const LoadingIcon = h(LoadingOutlined, { style: { fontSize: '24px' }, spin: true });

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
});

const filterForm = reactive({
  taskId: '',
  content: '',
  influencer: [] as string[],
  influencerEmail: '',
  defaultHandle: '',
  contactPerson: [] as string[],
  tags: [] as string[],
  spuSku: '',
  uploader: [] as string[],
  orderNo: '',
  contentType: undefined as string | undefined,
  isCommercial: undefined as boolean | undefined,
  timeRange: undefined as [string, string] | undefined,
  timeType: 'upload' as 'upload' | 'publish',
  publishDateRange: undefined as [string, string] | undefined,
  viewsMin: undefined as number | undefined,
  viewsMax: undefined as number | undefined,
  likesMin: undefined as number | undefined,
  likesMax: undefined as number | undefined,
  commentsMin: undefined as number | undefined,
  commentsMax: undefined as number | undefined,
  savesMin: undefined as number | undefined,
  savesMax: undefined as number | undefined,
  sharesMin: undefined as number | undefined,
  sharesMax: undefined as number | undefined,
  erMin: undefined as number | undefined,
  erMax: undefined as number | undefined,
});

const commonStore = useCommonDataStore();
const { allUsers, ownerUsers, contentTags, liaisonTagOptions } = storeToRefs(commonStore);
const influencerList = ref<any[]>([]);
const userList = computed(() => allUsers.value.map(u => ({ id: u.id, username: u.name })));
const tagsList = computed(() => contentTags.value);
const contactPersonOptions = computed(() => liaisonTagOptions.value.map(t => t.name).sort());
const platforms = ref<string[]>(['TikTok', 'Instagram', 'YouTube', 'Facebook']);

// --- Batch Search State ---
const batchSearchVisible = ref(false);
const batchSearchValue = ref('');
const batchSearchType = ref<string>('');
const batchSearchTitles: Record<string, { title: string; subtitle: string }> = {
  taskId: { title: '批量搜索任务ID', subtitle: '请输入任务ID，多个ID请用换行或逗号分隔' },
  influencerEmail: { title: '批量搜索红人邮箱', subtitle: '请输入红人邮箱，多个邮箱请用换行或逗号分隔' },
  orderNo: { title: '批量搜索关联订单', subtitle: '请输入订单号，多个订单号请用换行或逗号分隔' },
  spuSku: { title: '批量搜索 SPU/SKU', subtitle: '请输入 SKU/SPU，多个串请用换行或逗号分隔' },
  influencer: { title: '批量搜索红人名称', subtitle: '请输入红人名称，多个名称请用换行或逗号分隔' },
  defaultHandle: { title: '批量搜索社媒Handle', subtitle: '请输入 Handle，多个 Handle 请用换行或逗号分隔' },
};

const batchSearchPlaceholder = computed(() => {
  return batchSearchTitles[batchSearchType.value]?.subtitle || '请输入搜索内容...';
});

const openBatchSearch = (type: string) => {
  batchSearchType.value = type;
  const currentVal = (filterForm as any)[type];
  if (Array.isArray(currentVal)) {
    batchSearchValue.value = currentVal.join('\n');
  } else if (currentVal) {
    batchSearchValue.value = currentVal.split(',').join('\n');
  } else {
    batchSearchValue.value = '';
  }
  batchSearchVisible.value = true;
};

const handleBatchSearch = () => {
  const values = batchSearchValue.value
    .split(/[\n\s,;，；\t]+/)
    .filter(v => v.trim());
  
  if (Array.isArray((filterForm as any)[batchSearchType.value])) {
    (filterForm as any)[batchSearchType.value] = values;
  } else {
    (filterForm as any)[batchSearchType.value] = values.join(',');
  }
  
  batchSearchVisible.value = false;
  handleFilter();
};

const onSelectChange = (keys: number[]) => {
  selectedRowKeys.value = keys;
};

const setCardSelected = (key: number, checked: boolean) => {
  if (checked) {
    if (!selectedRowKeys.value.includes(key)) {
      selectedRowKeys.value = [...selectedRowKeys.value, key];
    }
  } else {
    selectedRowKeys.value = selectedRowKeys.value.filter((k) => k !== key);
  }
};

const handleBatchTag = async () => {
  if (selectedRowKeys.value.length === 0) return;
  
  batchTagLoading.value = true;
  try {
    await contentService.batchUpdateTags(selectedRowKeys.value, batchTagIds.value);
    message.success('已批量更新标签');
    batchTagModalVisible.value = false;
    batchTagIds.value = [];
    selectedRowKeys.value = [];
    loadData();
  } catch (e) {
    message.error('操作失败');
  } finally {
    batchTagLoading.value = false;
  }
};

const handleUpdateTag = async (record: any, tagIds: number[]) => {
  try {
    await contentService.updateContent(record.id || record.key, { 
      tagIds,
      isCommercial: record.isCommercial,
      owner: record.uploader,
      orderNo: record.order?.orderNo,
      productSku: record.skus?.[0]
    });
    message.success('标签更新成功');
    // 局部更新，不重新拉取全量
    record.tagIds = tagIds;
    const item = allGroupedData.value.find(d => d.key === record.key);
    if (item) item.tagIds = tagIds;
  } catch (e) {
    message.error('更新失败');
  }
};

const loadInfluencers = async (keyword: string = '') => {
  try {
    const result = await influencerService.getList({ page: 1, size: 50, searchKey: keyword || undefined });
    influencerList.value = (result.content || []).filter(inf => inf.nickName || inf.realName);
  } catch (e) { console.error(e); }
};

// 红人远程搜索防抖
let influencerSearchTimer: any = null;
const handleInfluencerRemoteSearch = (value: string) => {
  if (influencerSearchTimer) clearTimeout(influencerSearchTimer);
  influencerSearchTimer = setTimeout(() => {
    loadInfluencers(value);
  }, 300);
};


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
  { title: '任务ID', dataIndex: 'taskId', key: 'taskId', width: 160, minWidth: 160, fixed: 'left' },
  { title: '关联订单', key: 'order', width: 130, minWidth: 120 },
  { title: '关联红人', dataIndex: 'influencer', key: 'influencer', width: 170, minWidth: 150 },
  { title: '关联商品', key: 'sku', width: 280, minWidth: 220 },
  { title: '发布平台', dataIndex: 'platform', key: 'platform', width: 110, minWidth: 100 },
  { title: '素材预览', key: 'media', width: 340, minWidth: 300 },
  { title: '内容类型', dataIndex: 'contentType', key: 'contentType', width: 150, minWidth: 130 },
  { title: '素材链接', key: 'link', width: 220, minWidth: 200 },
  { title: '社媒Handle', dataIndex: 'defaultHandle', key: 'defaultHandle', width: 160, minWidth: 140, ellipsis: true },
  { title: '红人邮箱', dataIndex: 'influencerEmail', key: 'influencerEmail', width: 220, minWidth: 200, ellipsis: true },
  { title: '联络员', dataIndex: 'contactPersonName', key: 'contactPersonName', width: 130, minWidth: 110 },
  { title: '负责人', dataIndex: 'ownerName', key: 'ownerName', width: 130, minWidth: 110 },
  { title: '素材标签', key: 'tags', width: 200, minWidth: 180 },
  { title: '社媒数据', key: 'socialData', width: 240, minWidth: 220 },
  { title: 'ER', key: 'engagementRate', width: 100, minWidth: 90, align: 'center' },
  { title: '社媒更新时间', dataIndex: 'socialDataUpdatedAt', key: 'socialDataUpdatedAt', width: 170, minWidth: 150 },
  { title: '上传时间', dataIndex: 'uploadTime', key: 'uploadTime', width: 170, minWidth: 150 },
  { title: '发帖时间', key: 'publishDate', width: 130, minWidth: 120 },
  { title: '备注', dataIndex: 'remark', key: 'remark', width: 160, minWidth: 140, ellipsis: true },
  { title: '商用授权', key: 'isCommercial', width: 110, minWidth: 100, align: 'center' },
  { title: '操作', key: 'action', width: 200, minWidth: 200, fixed: 'right', align: 'center' },
];

// 真实数据
const data = ref<any[]>([]);
const allGroupedData = ref<any[]>([]);
const loading = ref(false);
const batchDownloading = ref(false);

// 提取公共转换函数：ContentDto -> 表格行
const transformContentToRow = (item: ContentDto, _index: number): any => {
  const isVideo = item.fileType?.startsWith('video');
  const mediaItem = item.previewUrl ? {
    type: isVideo ? 'video' : 'image',
    url: item.previewUrl,
    // 视频只用真正的缩略图，不要 fallback 到 previewUrl（视频文件URL 做 img src 会裂）
    thumbnailUrl: isVideo ? ((item as any).thumbnailUrl || '') : ((item as any).thumbnailUrl || item.previewUrl),
    name: item.fileName || '未命名文件',
    size: item.fileSize ? formatFileSize(item.fileSize) : '-',
    format: item.fileType?.split('/')[1]?.toUpperCase() || '-',
    resolution: item.width && item.height ? `${item.width} x ${item.height}` : '-',
  } : null;

  const skuParts = item.productSku
    ? dedupeTrimStrings(item.productSku.split(',').map((s: string) => s.trim()).filter(Boolean))
    : [];

  const row: any = {
    key: item.id,
    taskId: item.taskGroupId || `TASK-${item.id}`,
    contentGroupIndex: item.contentGroupIndex ?? 0,
    content: item.title || '素材内容',
    platform: item.platform || 'TikTok',
    contentType: item.contentType || (item.platform === 'TikTok' ? 'Tiktok video' : '其它'),
    order: item.orderNo ? { orderNo: item.orderNo } : null,
    spu: null,
    skus: skuParts,
    media: mediaItem ? [mediaItem] : [],
    link: item.postUrl || (item.description && /^https?:\/\//.test(item.description) ? item.description : ''),
    influencer: item.influencerName || `Influencer ${item.influencerId}`,
    influencerEmail: item.influencerEmail,
    influencerHandle: (item as any).influencerHandle || (item as any).defaultHandle || '-',
    defaultHandle: (item as any).defaultHandle || '-',
    contactPersonName: (item as any).contactPersonName || '-',
    ownerName: (item as any).ownerName || '-',
    tags: item.tags || [],
    tagIds: item.tagIds || [],
    socialData: {
      likes: item.likes || 0,
      comments: item.comments || 0,
      shares: item.shares || 0,
      views: item.views || 0,
    },
    publishDate: item.publishDate,
    uploadTime: item.updatedAt || item.createdAt,
    uploader: item.owner || '-',
    isCommercial: item.isCommercial ?? true,
    remark: item.remark || '-',
    socialDataUpdatedAt: (item as any).socialDataUpdatedAt,
    engagementRate: (item as any).engagementRate != null ? Number((item as any).engagementRate).toFixed(2) : '-',
    erFormula: ['IG-Reel', 'Tiktok video', 'Youtube short', 'Youtube video'].includes(item.contentType || '') ? 'video' : 'image',
    id: item.id,
    variantTitle: item.variantTitle || '',
    previewUrl: item.previewUrl,
    thumbnailUrl: (item as any).thumbnailUrl,
  };
  applySkuDisplayFields(row);
  return row;
};

// 加载内容库列表（后端按任务ID分组分页）
const loadData = async () => {
  loading.value = true;
  try {
    // 构建后端分页参数，grouped=true 启用按任务ID分组分页
    const params: any = {
      status: '!PENDING_UPLOAD',
      influencerEmail: filterForm.influencerEmail || undefined,
      taskGroupId: filterForm.taskId || undefined,
      orderNo: filterForm.orderNo || undefined,
      productSku: filterForm.spuSku || undefined,
      contentType: filterForm.contentType || undefined,
      isCommercial: filterForm.isCommercial,
      defaultHandle: filterForm.defaultHandle || undefined,
      contactPersonName: filterForm.contactPerson?.length > 0 ? filterForm.contactPerson.join(',') : undefined,
      owner: filterForm.uploader?.length > 0 ? filterForm.uploader.join(',') : undefined,
      influencerName: filterForm.influencer?.length > 0 ? filterForm.influencer.join(',') : undefined,
      tagIds: filterForm.tags?.length > 0 ? filterForm.tags.join(',') : undefined,
      page: pagination.current - 1,
      size: pagination.pageSize,
      grouped: true,  // 按任务ID分组分页
    };

    // 时间筛选尽量下推到后端
    if (filterForm.timeRange && filterForm.timeRange.length === 2) {
      const [start, end] = filterForm.timeRange;
      if (filterForm.timeType === 'upload') {
        params.startTime = start;
        params.endTime = end;
      } else {
        params.publishStartDate = start;
        params.publishEndDate = end;
      }
    }

    // 社媒数据范围筛选
    if (filterForm.viewsMin !== undefined) params.viewsMin = filterForm.viewsMin;
    if (filterForm.viewsMax !== undefined) params.viewsMax = filterForm.viewsMax;
    if (filterForm.likesMin !== undefined) params.likesMin = filterForm.likesMin;
    if (filterForm.likesMax !== undefined) params.likesMax = filterForm.likesMax;
    if (filterForm.commentsMin !== undefined) params.commentsMin = filterForm.commentsMin;
    if (filterForm.commentsMax !== undefined) params.commentsMax = filterForm.commentsMax;
    if (filterForm.savesMin !== undefined) params.savesMin = filterForm.savesMin;
    if (filterForm.savesMax !== undefined) params.savesMax = filterForm.savesMax;
    if (filterForm.sharesMin !== undefined) params.sharesMin = filterForm.sharesMin;
    if (filterForm.sharesMax !== undefined) params.sharesMax = filterForm.sharesMax;
    if (filterForm.erMin !== undefined) params.erMin = filterForm.erMin;
    if (filterForm.erMax !== undefined) params.erMax = filterForm.erMax;

    const result = await contentService.getContents(params);

    const tempData = (result.content || []).map(transformContentToRow);
    allGroupedData.value = mergeGroupedContentRows(tempData);
    data.value = allGroupedData.value;
    // totalElements 是后端按 (taskGroupId, contentGroupIndex) 分组后的总数
    const backendTotal = result.totalElements || 0;
    // 当前页前端合并后的实际行数
    const frontendPageRows = allGroupedData.value.length;
    // 如果当前页未满（行数 < pageSize），说明这是最后一页或唯一一页
    // 此时可以精确计算真实总数 = 前面页的数量 + 本页实际行数
    if (frontendPageRows < pagination.pageSize) {
      const realTotal = (pagination.current - 1) * pagination.pageSize + frontendPageRows;
      if (realTotal < backendTotal) {
        console.warn(`[ContentLibrary] 后端 totalGroups=${backendTotal}, 实际计算总数=${realTotal}，已修正`);
        pagination.total = realTotal;
      } else {
        pagination.total = backendTotal;
      }
    } else {
      pagination.total = backendTotal;
    }
  } catch (e: any) {
    console.error('加载内容库失败:', e);
    message.error('加载内容库失败');
  } finally {
    loading.value = false;
  }
};

onMounted(async () => {
  initCurrentUserId();
  restoreStateFromUrl();
  await Promise.all([
    loadInfluencers(),
    commonStore.loadUsers(),
    commonStore.loadContentTags(),
    commonStore.loadLiaisonTags(),
  ]);
  loadData();
});

// 筛选已在后端完成，filteredData 直接返回当页数据（保留以兼容导出逻辑引用）
const filteredData = computed(() => allGroupedData.value);

const handleTableChange = () => {
  syncStateToUrl();
  loadData();
};

const handlePaginationChange = (page: number, pageSize: number) => {
  pagination.current = page;
  pagination.pageSize = pageSize;
  syncStateToUrl();
  loadData();
};

const handlePaginationSizeChange = (_page: number, size: number) => {
  pagination.current = 1;
  pagination.pageSize = size;
  syncStateToUrl();
  loadData();
};

const handleResetFilter = () => {
  filterForm.taskId = '';
  filterForm.content = '';
  filterForm.influencer = [];
  filterForm.influencerEmail = '';
  filterForm.defaultHandle = '';
  filterForm.contactPerson = [];
  filterForm.tags = [];
  filterForm.spuSku = '';
  filterForm.uploader = [];
  filterForm.orderNo = '';
  filterForm.contentType = undefined;
  filterForm.isCommercial = undefined;
  filterForm.timeRange = undefined;
  filterForm.timeType = 'upload';
  filterForm.publishDateRange = undefined;
  filterForm.viewsMin = undefined;
  filterForm.viewsMax = undefined;
  filterForm.likesMin = undefined;
  filterForm.likesMax = undefined;
  filterForm.commentsMin = undefined;
  filterForm.commentsMax = undefined;
  filterForm.savesMin = undefined;
  filterForm.savesMax = undefined;
  filterForm.sharesMin = undefined;
  filterForm.sharesMax = undefined;
  filterForm.erMin = undefined;
  filterForm.erMax = undefined;
  handleFilter();
};

const handleFilter = () => {
  pagination.current = 1;
  syncStateToUrl();
  loadData();
};

// ── URL 状态持久化 ──
const syncStateToUrl = () => {
  const params = new URLSearchParams();
  if (pagination.current > 1) params.set('page', String(pagination.current));
  if (pagination.pageSize !== 10) params.set('size', String(pagination.pageSize));
  if (filterForm.taskId) params.set('taskId', filterForm.taskId);
  if (filterForm.content) params.set('content', filterForm.content);
  if (filterForm.influencerEmail) params.set('email', filterForm.influencerEmail);
  if (filterForm.orderNo) params.set('orderNo', filterForm.orderNo);
  if (filterForm.spuSku) params.set('sku', filterForm.spuSku);
  if (filterForm.contentType) params.set('type', filterForm.contentType);
  if (filterForm.defaultHandle) params.set('handle', filterForm.defaultHandle);
  const qs = params.toString();
  const newUrl = qs ? `${route.path}?${qs}` : route.path;
  router.replace(newUrl);
};

const restoreStateFromUrl = () => {
  const q = route.query;
  if (q.page) pagination.current = Number(q.page) || 1;
  if (q.size) pagination.pageSize = Number(q.size) || 10;
  if (q.taskId) filterForm.taskId = String(q.taskId);
  if (q.content) filterForm.content = String(q.content);
  if (q.email) filterForm.influencerEmail = String(q.email);
  if (q.orderNo) filterForm.orderNo = String(q.orderNo);
  if (q.sku) filterForm.spuSku = String(q.sku);
  if (q.type) filterForm.contentType = String(q.type);
  if (q.handle) filterForm.defaultHandle = String(q.handle);
};

const handleDelete = async (record: any) => {
  const hide = message.loading('正在删除...', 0);
  try {
    await deleteContent(record.key);
    message.success('已删除素材');
    loadData();
  } catch (e: any) {
    message.error('删除失败: ' + (e.response?.data?.message || e.message));
  } finally {
    hide();
  }
};

const confirmDeleteCard = (record: any) => {
  Modal.confirm({
    title: '确定删除该素材？',
    content: '将同时删除待上传任务和内容库中的所有关联内容。',
    okText: '确定删除',
    cancelText: '取消',
    okButtonProps: { danger: true },
    onOk: () => handleDelete(record),
  });
};

const handleBatchDelete = async () => {
  if (selectedRowKeys.value.length === 0) return;
  
  const hide = message.loading('正在批量删除...', 0);
  try {
    await batchDeleteContents(selectedRowKeys.value);
    hide();
    message.success('批量删除成功');
    selectedRowKeys.value = [];
    loadData();
  } catch (e: any) {
    message.error('批量删除失败: ' + (e.response?.data?.message || e.message));
  }
};

const confirmNonCommercialDownload = (onOk: () => void) => {
  Modal.confirm({
    title: 'Confirm',
    icon: null,
    content: h('div', { class: 'custom-confirm-body' }, [
      h(ExclamationCircleFilled, { class: 'body-icon' }),
      h('div', { class: 'body-text' }, [
        h('p', { class: 'text-main' }, '当前素材未获得商业授权，直接使用可能产生法律纠纷。请确保仅用于非商业用途或已在线下获得授权。'),
      ])
    ]),
    okText: '继续下载',
    cancelText: '取消',
    centered: false,
    mask: false,
    width: 650,
    okButtonProps: { type: 'primary', danger: true },
    wrapClassName: 'red-header-confirm-modal custom-top-modal',
    onOk,
  });
};

const handleDownload = (record: any) => {
  if (record.isCommercial === false) {
    confirmNonCommercialDownload(() => performDownload(record));
  } else {
    performDownload(record);
  }
};

const performDownload = async (record: any) => {
  try {
    message.loading({ content: '正在准备下载...', key: 'downloading', duration: 0 });
    const result = await downloadContentRecordAsZip(record);
    if (result.ok) {
      message.success({ content: '已开始下载', key: 'downloading' });
    } else {
      message.warning({ content: result.message || '下载失败', key: 'downloading' });
    }
  } catch (error) {
    console.error('Download failed:', error);
    message.error({ content: '下载失败', key: 'downloading' });
  }
};

const runBatchDownload = async (records: any[]) => {
  if (!records.length) {
    message.warning('请先选择要下载的内容');
    return;
  }

  batchDownloading.value = true;
  let success = 0;
  try {
    message.loading({ content: `正在打包 0/${records.length}`, key: 'batch-download', duration: 0 });
    for (let i = 0; i < records.length; i++) {
      const record = records[i];
      message.loading({ content: `正在打包 ${i + 1}/${records.length}：${record.influencer || record.taskId}`, key: 'batch-download', duration: 0 });
      const result = await downloadContentRecordAsZip(record, { forceZip: true });
      if (result.ok) {
        success += 1;
      } else {
        message.warning(`${record.influencer || record.taskId}：${result.message || '下载失败'}`);
      }
      if (i < records.length - 1) {
        await delay(400);
      }
    }
    if (success > 0) {
      message.success({ content: `已开始下载 ${success} 个压缩包`, key: 'batch-download' });
    } else {
      message.error({ content: '没有可下载的素材', key: 'batch-download' });
    }
  } finally {
    batchDownloading.value = false;
  }
};

const handleBatchDownload = () => {
  const records = data.value.filter((row) => selectedRowKeys.value.includes(row.key));
  if (!records.length) {
    message.warning('请先选择要下载的内容');
    return;
  }

  const hasNonCommercial = records.some((row) => row.isCommercial === false);
  if (hasNonCommercial) {
    confirmNonCommercialDownload(() => runBatchDownload(records));
  } else {
    runBatchDownload(records);
  }
};

const previewMedia = (mediaList: any[], index: number = 0) => {
  if (!mediaList || mediaList.length === 0) return;
  previewMediaList.value = mediaList;
  previewInitialIndex.value = index;
  previewData.value = {
    url: mediaList[index].url,
    name: mediaList[index].name,
    size: mediaList[index].size,
    format: mediaList[index].format,
    resolution: mediaList[index].resolution,
    type: mediaList[index].type,
  };
  imagePreviewVisible.value = true;
};

// 导出相关
const contentExportFields: any[] = [
  { key: 'taskId', title: '任务ID' },
  { key: 'influencer', title: '红人' },
  { key: 'influencerEmail', title: '红人邮箱' },
  { key: 'ownerName', title: '负责人' },
  { key: 'contactPersonName', title: '联络员' },
  { key: 'platform', title: '发布平台' },
  { key: 'contentType', title: '内容类型' },
  { key: 'productSku', title: '商品SKU' },
  { key: 'orderNo', title: '关联订单' },
  { key: 'previewImage', title: '素材预览' },
  { key: 'mediaUrl', title: '素材下载链接' },
  { key: 'link', title: '素材发布链接' },
  { key: 'publishDate', title: '发帖时间' },
  { key: 'isCommercial', title: '商用状态' },
  { key: 'tags', title: '标签' },
  { key: 'views', title: '阅读/播放数' },
  { key: 'likes', title: '点赞数' },
  { key: 'comments', title: '评论数' },
  { key: 'shares', title: '分享数' },
  { key: 'saves', title: '收藏数' },
  { key: 'engagementRate', title: 'ER(%)' },
  { key: 'uploader', title: '上传人' },
  { key: 'uploadTime', title: '上传时间' },
  { key: 'socialDataUpdatedAt', title: '社媒更新时间' },
];

const openSocialModal = (record: any) => {
  socialForm.id = record.key;
  socialForm.views = record.socialData.views;
  socialForm.likes = record.socialData.likes;
  socialForm.comments = record.socialData.comments;
  socialForm.shares = record.socialData.shares;
  socialForm.saves = record.socialData.saves;
  socialModalVisible.value = true;
};

const handleUpdateSocialMetrics = async () => {
  if (!socialForm.id) return;
  socialModalLoading.value = true;
  try {
    await contentService.updateSocialMetrics(socialForm.id, {
      views: socialForm.views,
      likes: socialForm.likes,
      comments: socialForm.comments,
      shares: socialForm.shares,
      saves: socialForm.saves
    });
    message.success('社媒数据更新成功');
    socialModalVisible.value = false;
    loadData();
  } catch (e) {
    message.error('更新失败');
  } finally {
    socialModalLoading.value = false;
  }
};

const handleExport = () => {
  exportModalVisible.value = true;
};

const handleExportFromModal = async (payload: { scope: 'all' | 'selected', fields: string[], columns: any[], templateId?: string, templateName?: string }) => {
  const { templateId, templateName, scope, columns } = payload;
  try {
    const params = {
      ...filterForm,
    };
    
    let exportRawData = [];
    
    if (scope === 'selected' && selectedRowKeys.value.length > 0) {
      // Use currently selected data
      exportRawData = allGroupedData.value.filter(item => selectedRowKeys.value.includes(item.key));
    } else {
      // 构建与 loadData 一致的完整后端筛选参数，避免前端二次过滤
      const exportParams: any = {
        status: '!PENDING_UPLOAD',
        influencerEmail: filterForm.influencerEmail || undefined,
        taskGroupId: filterForm.taskId || undefined,
        orderNo: filterForm.orderNo || undefined,
        productSku: filterForm.spuSku || undefined,
        contentType: filterForm.contentType || undefined,
        isCommercial: filterForm.isCommercial,
        defaultHandle: filterForm.defaultHandle || undefined,
        contactPersonName: filterForm.contactPerson?.length > 0 ? filterForm.contactPerson.join(',') : undefined,
        owner: filterForm.uploader?.length > 0 ? filterForm.uploader.join(',') : undefined,
        influencerName: filterForm.influencer?.length > 0 ? filterForm.influencer.join(',') : undefined,
        tagIds: filterForm.tags?.length > 0 ? filterForm.tags.join(',') : undefined,
      };

      // 时间筛选下推到后端
      if (filterForm.timeRange && filterForm.timeRange.length === 2) {
        const [start, end] = filterForm.timeRange;
        if (filterForm.timeType === 'upload') {
          exportParams.startTime = start;
          exportParams.endTime = end;
        } else {
          exportParams.publishStartDate = start;
          exportParams.publishEndDate = end;
        }
      }

      // 社媒数据范围筛选
      if (filterForm.viewsMin !== undefined) exportParams.viewsMin = filterForm.viewsMin;
      if (filterForm.viewsMax !== undefined) exportParams.viewsMax = filterForm.viewsMax;
      if (filterForm.likesMin !== undefined) exportParams.likesMin = filterForm.likesMin;
      if (filterForm.likesMax !== undefined) exportParams.likesMax = filterForm.likesMax;
      if (filterForm.commentsMin !== undefined) exportParams.commentsMin = filterForm.commentsMin;
      if (filterForm.commentsMax !== undefined) exportParams.commentsMax = filterForm.commentsMax;
      if (filterForm.savesMin !== undefined) exportParams.savesMin = filterForm.savesMin;
      if (filterForm.savesMax !== undefined) exportParams.savesMax = filterForm.savesMax;
      if (filterForm.sharesMin !== undefined) exportParams.sharesMin = filterForm.sharesMin;
      if (filterForm.sharesMax !== undefined) exportParams.sharesMax = filterForm.sharesMax;
      if (filterForm.erMin !== undefined) exportParams.erMin = filterForm.erMin;
      if (filterForm.erMax !== undefined) exportParams.erMax = filterForm.erMax;

      // Fetch all records across all pages
      const allData = await fetchAllPages<ContentDto>(
        (p) => contentService.getContents({
          ...p,
          sortBy: 'createdAt',
          sortDir: 'desc',
          grouped: false,
        }),
        exportParams,
        {
          pageSize: 500,
          onProgress: (p: number) => {

          }
        }
      );

      const tempExport = allData.map((item: ContentDto, i: number) => transformContentToRow(item, i));
      exportRawData = mergeGroupedContentRows(tempExport);
    }

    // Format tags and mapping
    const exportData = exportRawData.map((item: any) => {
      const tagIds = item.tagIds || [];
      const tagNames = tagIds.map((id: number) => {
        const tag = tagsList.value.find(t => t.id === id);
        return tag ? tag.name : id;
      }).join(', ');
      
      return {
        ...item,
        taskId: item.taskId || item.taskGroupId || `TASK-${item.id}`,
        influencer: item.influencerName || item.influencer,
        mediaUrl: item.previewUrl || (item.media?.[0]?.url) || '',
        link: item.link || item.postUrl || (item.description && /^https?:\/\//.test(item.description) ? item.description : '') || '',
        productSku: item.productSku || (item.skus ? item.skus.join(', ') : ''),
        orderNo: item.orderNo || item.order?.orderNo || '',
        publishDate: item.publishDate ? dayjs(item.publishDate).format('YYYY-MM-DD HH:mm:ss') : '',
        contentType: item.contentType,
        isCommercial: item.isCommercial ? '是' : '否',
        tags: tagNames,
        uploader: item.owner || item.uploader || '-',
        uploadTime: item.uploadTime ? dayjs(item.uploadTime).format('YYYY-MM-DD HH:mm:ss') : (item.createdAt ? dayjs(item.createdAt).format('YYYY-MM-DD HH:mm:ss') : ''),
        previewImage: item.thumbnailUrl || item.previewUrl || (item.media?.[0]?.url) || '',
        views: item.socialData?.views || item.views || 0,
        likes: item.socialData?.likes || item.likes || 0,
        comments: item.socialData?.comments || item.comments || 0,
        shares: item.socialData?.shares || item.shares || 0,
        saves: item.socialData?.saves || item.saves || 0,
        engagementRate: item.engagementRate || '-',
        socialDataUpdatedAt: item.socialDataUpdatedAt ? dayjs(item.socialDataUpdatedAt).format('YYYY-MM-DD HH:mm:ss') : '',
      };
    });

    await createExportTask({
      data: exportData,
      columns: columns,
      filename: `内容库导出`,
      templateId,
      templateName,
      pageType: 'content-library'
    });
    
    exportModalVisible.value = false;
    message.success(`导出任务已创建${templateName ? ': ' + templateName : ''}`);
  } catch (error) {
    console.error('导出失败:', error);
    message.error('导出失败');
  }
};

</script>

<style lang="scss" scoped>
.content-library-page {
  height: 100%;
  padding: 8px;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  gap: 8px;
  overflow-x: hidden;
  overflow-y: auto;

  &.is-card-layout {
    overflow: hidden;
  }
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
    overflow: visible;
    
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
    
    :deep(.ant-card-body) {
      overflow: visible !important;
    }
  }

  .table-card {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    min-height: 0;

    &.is-card-view {
      min-height: 0;
      overflow: visible;

      :deep(.ant-card-body) {
        display: flex;
        flex-direction: column;
        min-height: 0;
        overflow: visible;
      }

      .batch-selection-bar {
        flex-shrink: 0;
      }

      .content-card-view {
        flex: 1;
        min-height: 0;
        overflow-x: hidden;
        overflow-y: auto;
        position: relative;
        z-index: 1;

        :deep(.ant-spin-container) {
          height: auto;
          min-height: 0;
        }
      }

      .pagination-footer {
        position: relative;
        z-index: 30;
        flex-shrink: 0;
        overflow: visible;
        background: #fff;

        :deep(.ant-select-dropdown) {
          z-index: 2000;
        }
      }
    }

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
    :deep(.ant-spin-container) {
      display: flex;
      flex-direction: column;
      overflow: hidden;
      background: #ffffff;
    }

    :deep(.ant-table) {
      display: flex;
      flex-direction: column;
      overflow: hidden;
      background: #ffffff;
    }

    :deep(.ant-table-container) {
      overflow: auto;
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
      padding: 14px 12px !important;
      min-height: 48px !important;
      line-height: 1.6 !important;
      white-space: nowrap; /* Prevent header text wrapping */
      text-overflow: clip;
      overflow: visible;
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
      padding: 12px 12px !important;
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

  /* 素材预览横向滚动条美化 */
  .media-scroll-container {
    scrollbar-width: thin;
    scrollbar-color: rgba(99, 102, 241, 0.25) transparent;

    &::-webkit-scrollbar {
      height: 4px;
    }
    &::-webkit-scrollbar-track {
      background: transparent;
      margin: 0 4px;
    }
    &::-webkit-scrollbar-thumb {
      background: linear-gradient(90deg, rgba(99, 102, 241, 0.2), rgba(139, 92, 246, 0.3));
      border-radius: 10px;
      transition: background 0.3s;
      &:hover {
        background: linear-gradient(90deg, rgba(99, 102, 241, 0.4), rgba(139, 92, 246, 0.5));
      }
    }
  }

  /* 视频缩略图容器统一尺寸 */
  .video-preview {
    width: 60px;
    height: 60px;
    border-radius: 6px;
    overflow: hidden;
    background: #f1f5f9;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    flex-shrink: 0;
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

  /* Specific Content Library Styles */
  .media-item {
     border-radius: 6px; overflow: hidden; cursor: pointer; transition: all 0.3s;
     &:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,0.1); }
  }
  .media-preview {
     width: 48px; height: 48px; object-fit: cover; border-radius: 6px;
  }
  .video-preview { 
      background: #000; position: relative; display: flex; align-items: center; justify-content: center;
      .video-duration {
          position: absolute; bottom: 2px; right: 2px; background: rgba(0,0,0,0.6); color: #fff; 
          font-size: 9px; padding: 0 2px; border-radius: 2px;
      }
  }
  .pill-tags-wrapper {
      display: flex; gap: 4px; flex-wrap: wrap;
      .pill-tag { border-radius: 4px; font-size: 11px; margin: 0; background: #f1f5f9; border: 1px solid #e2e8f0; color: #64748b; }
  }
  .social-data-grid {
      display: flex; gap: 8px; font-size: 11px;
      .social-item { display: flex; flex-direction: column; align-items: center; justify-content: center; }
      .label { color: #94a3b8; font-size: 10px; }
      .value { font-weight: 700; font-family: 'JetBrains Mono', monospace;
          &.like { color: #f43f5e; }
          &.comment { color: #3b82f6; }
          &.share { color: #f59e0b; }
      }
  }
  .link-text {
      color: #3b82f6; font-size: 12px; max-width: 150px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; display: block;
      &:hover { text-decoration: underline; }
  }

  .media-count-badge {
    position: absolute;
    bottom: -4px;
    right: -4px;
    background: #3b82f6;
    color: #fff;
    font-size: 10px;
    font-weight: 700;
    padding: 1px 4px;
    border-radius: 4px;
    border: 2px solid #fff;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    pointer-events: none;
    z-index: 5;
  }

  .view-mode-toggle {
    :deep(.ant-radio-button-wrapper) {
      height: 32px;
      line-height: 30px;
      font-size: 12px;
      border-color: #e2e8f0;
      &:first-child { border-radius: 6px 0 0 6px; }
      &:last-child { border-radius: 0 6px 6px 0; }
    }
    :deep(.ant-radio-button-wrapper-checked) {
      background: #eff6ff;
      border-color: #93c5fd;
      color: #1d4ed8;
      font-weight: 600;
    }
  }

  .card-total-hint {
    margin-left: 12px;
    font-size: 13px;
    font-weight: 500;
    color: #64748b;
  }

  .status-switcher-wrapper {
    display: flex;
    align-items: center;
  }

  .content-card-view {
    flex: 1;
    min-height: 0;
    padding: 10px 12px 12px;
    overflow-x: hidden;
    overflow-y: auto;
  }

  .card-empty {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 48px 0;
  }

  .content-card-grid {
    display: grid;
    grid-template-columns: repeat(5, minmax(0, 1fr));
    gap: 12px;
    align-content: start;

    @media (max-width: 1600px) {
      grid-template-columns: repeat(4, minmax(0, 1fr));
    }
    @media (max-width: 1280px) {
      grid-template-columns: repeat(3, minmax(0, 1fr));
    }
    @media (max-width: 992px) {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }
    @media (max-width: 576px) {
      grid-template-columns: 1fr;
    }
  }

  .content-card-ref {
    position: relative;
    display: flex;
    flex-direction: column;
    background: #fff;
    border: 1px solid #eef2f6;
    border-radius: 10px;
    overflow: hidden;
    transition: box-shadow 0.2s;

    .card-cover {
      flex: none;
      width: 100%;
      aspect-ratio: 9 / 16;
    }

    &:hover {
      box-shadow: 0 10px 28px rgba(15, 23, 42, 0.1);

      .card-check { opacity: 1; }
      .card-more-btn { opacity: 1; }
    }

    &.is-selected {
      border-color: #3b82f6;
      box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.12);
    }
  }

  .card-check {
    position: absolute;
    top: 8px;
    left: 8px;
    z-index: 8;
    opacity: 0;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 6px;
    padding: 2px 4px;
    transition: opacity 0.2s;
  }

  .content-card-ref.is-selected .card-check {
    opacity: 1;
  }

  .card-cover {
    position: relative;
    display: grid;
    gap: 2px;
    width: 100%;
    background: #f1f5f9;
    overflow: hidden;

    &.tiles-1 {
      grid-template-columns: 1fr;
      grid-template-rows: 1fr;
    }
    &.tiles-2 {
      grid-template-columns: 1fr 1fr;
      grid-template-rows: 1fr;
    }
    &.tiles-3 {
      grid-template-columns: 1fr 1fr 1fr;
      grid-template-rows: 1fr;
    }
    &.tiles-4 {
      grid-template-columns: 1fr 1fr;
      grid-template-rows: 1fr 1fr;
    }

    &.tiles-1 .cover-play {
      font-size: 44px;
    }
  }

  .card-cover-tile {
    position: relative;
    overflow: hidden;
    cursor: pointer;
    background: #e2e8f0;

    &:hover .cover-img {
      transform: scale(1.03);
    }

    &.is-more-grid {
      padding: 0;
      background: #f1f5f9;
    }
  }

  .more-thumb-grid {
    display: grid;
    width: 100%;
    height: 100%;
    gap: 1px;
    background: #e2e8f0;

    &.cells-1 {
      grid-template-columns: 1fr;
      grid-template-rows: 1fr;
    }
    &.cells-2 {
      grid-template-columns: 1fr 1fr;
      grid-template-rows: 1fr;
    }
    &.cells-3 {
      grid-template-columns: 1fr 1fr;
      grid-template-rows: 1fr 1fr;
    }
    &.cells-4 {
      grid-template-columns: 1fr 1fr;
      grid-template-rows: 1fr 1fr;
    }
    &.cells-6 {
      grid-template-columns: repeat(3, 1fr);
      grid-template-rows: 1fr 1fr;
    }
    &.cells-9 {
      grid-template-columns: repeat(3, 1fr);
      grid-template-rows: repeat(3, 1fr);
    }
  }

  .more-thumb-cell {
    position: relative;
    overflow: hidden;
    min-width: 0;
    min-height: 0;
    background: #e2e8f0;
    cursor: pointer;

    &:hover .cover-img {
      transform: scale(1.05);
    }

    &.is-overflow {
      display: flex;
      align-items: center;
      justify-content: center;
      background: rgba(15, 23, 42, 0.65);
      color: #fff;

      .more-count-sm {
        font-size: 14px;
        font-weight: 700;
        line-height: 1;
      }
    }
  }

  .more-thumb-video {
    width: 100%;
    height: 100%;

    .cover-play-sm {
      font-size: 16px;
    }
  }

  .cover-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
    transition: transform 0.25s ease;
  }

  .cover-video {
    width: 100%;
    height: 100%;
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #0f172a;
    overflow: hidden;

    :deep(.vft-wrapper),
    :deep(.vft-loading),
    :deep(img) {
      width: 100% !important;
      height: 100% !important;
      border-radius: 0 !important;
      object-fit: cover;
    }
  }

  .cover-play {
    position: absolute;
    font-size: 28px;
    color: #fff;
    filter: drop-shadow(0 2px 6px rgba(0, 0, 0, 0.4));
    pointer-events: none;
  }

  .card-cover-empty {
    grid-column: 1 / -1;
    display: flex;
    align-items: center;
    justify-content: center;
    color: #94a3b8;
    font-size: 13px;
  }

  .card-platform-tag {
    position: absolute;
    left: 8px;
    bottom: 8px;
    z-index: 5;
    max-width: calc(100% - 56px);
    padding: 2px 8px;
    border-radius: 4px;
    background: rgba(255, 255, 255, 0.92);
    color: #334155;
    font-size: 11px;
    font-weight: 600;
    line-height: 18px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  }

  .card-download-btn,
  .card-more-btn {
    position: absolute;
    z-index: 6;
    display: flex;
    align-items: center;
    justify-content: center;
    border: none;
    cursor: pointer;
    transition: background 0.2s, opacity 0.2s;
  }

  .card-download-btn {
    right: 8px;
    bottom: 8px;
    width: 28px;
    height: 28px;
    border-radius: 6px;
    background: rgba(255, 255, 255, 0.95);
    color: #2563eb;
    font-size: 14px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.1);

    &:hover {
      background: #eff6ff;
      color: #1d4ed8;
    }
  }

  .card-more-btn {
    top: 8px;
    right: 8px;
    width: 26px;
    height: 26px;
    border-radius: 6px;
    background: rgba(255, 255, 255, 0.92);
    color: #64748b;
    font-size: 16px;
    opacity: 0;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);

    &:hover {
      background: #fff;
      color: #334155;
    }
  }

  .card-ref-footer {
    flex-shrink: 0;
    width: 100%;
    min-height: 88px;
    padding: 6px 8px 8px;
    background: #fff;
    text-align: center;
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    gap: 2px;
  }

  .card-influencer-name {
    font-size: 13px;
    font-weight: 600;
    color: #1e293b;
    line-height: 1.3;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .card-sku-line {
    font-size: 11px;
    color: #64748b;
    font-family: 'JetBrains Mono', monospace;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .card-date-line {
    font-size: 11px;
    color: #94a3b8;
    line-height: 1.2;
  }

  .card-social-grid {
    margin-top: 4px;
    padding-top: 4px;
    border-top: 1px solid #f1f5f9;
    justify-content: space-between;
    cursor: pointer;

    .social-item {
      flex: 1;
      min-width: 0;
    }

    .label {
      font-size: 9px;
    }

    .value {
      font-size: 11px;

      &.view { color: #6366f1; }
      &.save { color: #d97706; }
    }

    &:hover {
      background: #f8fafc;
      border-radius: 4px;
    }
  }


  /* Premium Modal Styles */
  :deep(.influencer-create-modal) {
    .ant-modal-content {
      padding: 0 !important;
      overflow: hidden;
      border-radius: 16px;
      box-shadow: 0 20px 50px rgba(0, 0, 0, 0.15);
    }

    .ic-modal-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 16px 24px;
      background: #ffffff;
      border-bottom: 1px solid #f1f5f9;
      
      &.glass-header {
        background: rgba(255, 255, 255, 0.95);
      }
      
      .ic-header-left {
        display: flex;
        align-items: center;
        gap: 16px;
      }
      
      .ic-header-icon-wrapper {
        width: 40px;
        height: 40px;
        border-radius: 10px;
        background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
        color: #ffffff;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 18px;
        box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2);
      }
      
      .ic-header-text {
        .ic-header-title {
          font-size: 18px;
          font-weight: 700;
          color: #1e293b;
        }
        .ic-header-subtitle {
          font-size: 12px;
          color: #94a3b8;
        }
      }
      
      .close-btn {
        color: #94a3b8;
        &:hover {
          color: #64748b;
          background: #f1f5f9;
        }
      }
    }

    .ic-modal-body {
      padding: 20px 24px;
      background: #ffffff;
      
      .premium-textarea {
        border-radius: 8px;
        border-color: #e2e8f0;
        font-size: 14px;
        
        &:focus {
          border-color: #3b82f6;
          box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
        }
      }
    }

    .ic-modal-footer {
      padding: 16px 24px;
      background: #ffffff;
      border-top: 1px solid #f1f5f9;
      text-align: right;
      
      .premium-btn {
        height: 36px;
        padding: 0 24px;
        border-radius: 8px;
        font-weight: 600;
        
        &.primary-gradient {
          background: linear-gradient(135deg, #10b981 0%, #059669 100%);
          border: none;
          color: #ffffff;
          
          &:hover {
            transform: translateY(-1px);
            box-shadow: 0 6px 15px rgba(16, 185, 129, 0.3);
          }
        }
      }
    }
  }
}
.batch-selection-bar {
  padding: 8px 16px;
  background: #eff6ff;
  border-bottom: 1px solid #dbeafe;
  display: flex;
  align-items: center;
  .selection-info {
    font-size: 13px;
    color: #1e40af;
    .count { font-weight: 800; margin: 0 4px; }
  }
}

.inline-tag-select {
  :deep(.ant-select-selector) {
    background: transparent !important;
    padding: 0 !important;
  }
  :deep(.ant-select-selection-item) {
    height: 22px !important;
    line-height: 20px !important;
    background: #f1f5f9 !important;
    border: 1px solid #e2e8f0 !important;
    font-size: 11px !important;
    border-radius: 4px !important;
  }
}
</style>

<style lang="scss">
/* Global styles for portal-ed modals - Matching Reference Image */
.red-header-confirm-modal {
  &.custom-top-modal {
    top: 15% !important;
  }
  
  .ant-modal-content {
    border-radius: 4px;
    padding: 0 !important;
    overflow: hidden;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  }
  
  .ant-modal-confirm-body-wrapper {
    padding: 0;
  }

  /* Header Simulation */
  .ant-modal-confirm-body {
    padding: 0;
    
    .ant-modal-confirm-title {
      background: #f87171;
      color: #ffffff;
      padding: 10px 16px;
      font-size: 14px;
      font-weight: 600;
      display: block;
      margin-bottom: 0;
      position: relative;
      
      &::after {
        content: '×';
        position: absolute;
        right: 16px;
        top: 8px;
        font-size: 20px;
        font-weight: 300;
        cursor: pointer;
        color: rgba(255, 255, 255, 0.8);
      }
    }
    
    .ant-modal-confirm-content {
      padding: 24px 20px;
      margin: 0;
    }
  }

  /* Body Content */
  .custom-confirm-body {
    display: flex;
    align-items: center; /* Center icon with text vertically */
    gap: 16px;
    
    &.no-wrap-content {
      white-space: nowrap;
    }

    .body-icon {
      font-size: 32px;
      color: #f87171;
      flex-shrink: 0;
    }
    
    .body-text {
      .text-main {
        color: #4b5563;
        font-size: 14px;
        margin-bottom: 0;
        line-height: 1.4;
      }
    }
  }

  /* Buttons */
  .ant-modal-confirm-btns {
    padding: 0 16px 16px;
    margin-top: 0;
    display: flex;
    justify-content: flex-end;
    gap: 8px;
    
    .ant-btn {
      height: 32px;
      min-width: 80px;
      border-radius: 2px;
      font-size: 12px;
      font-weight: 600;
      text-transform: uppercase;
      
      /* NO button style */
      &:not(.ant-btn-primary) {
        background: #ffffff;
        border: 1px solid #d1d5db;
        color: #9ca3af;
        transition: all 0.2s;
        &:hover { border-color: #9ca3af; color: #4b5563; }
      }
      
      /* YES button style */
      &.ant-btn-primary {
        background: #f87171;
        border: none;
        color: #ffffff;
        box-shadow: none;
        transition: opacity 0.2s;
        &:hover { background: #ef4444; opacity: 0.9; }
      }
    }
  }

  /* 关联商品列：紧凑展示，避免按每个 "-" 切片导致刷屏 */
  .sku-cell-compact {
    max-width: 260px;
    .sku-cell-inner {
      cursor: default;
    }
    .sku-cell-main {
      color: #0369a1;
      font-weight: 700;
      font-size: 13px;
      font-family: 'JetBrains Mono', monospace;
      word-break: break-all;
    }
    .sku-cell-sub {
      color: #64748b;
      font-size: 12px;
      margin-top: 4px;
      line-height: 1.35;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      word-break: break-word;
    }
  }

  /* SKU List Display Styles */
  .p-sku-tag-list {
    display: flex;
    flex-wrap: nowrap;
    align-items: center;
    gap: 2px;
    font-family: 'JetBrains Mono', monospace;
    font-size: 13px;
    white-space: nowrap;
    
    .sku-main {
      color: #0369a1;
      font-weight: 700;
    }
    
    .sku-spec {
      font-weight: 600;
      &.spec-0 { color: #16a34a; }
      &.spec-1 { color: #ea580c; }
      &.spec-2 { color: #8b5cf6; }
    }
  }

  /* 媒体横向滚动容器的滚动条美化 */
  .custom-scrollbar {
    &::-webkit-scrollbar {
      height: 6px;
    }
    &::-webkit-scrollbar-track {
      background: transparent;
    }
    &::-webkit-scrollbar-thumb {
      background-color: #cbd5e1;
      border-radius: 6px;
    }
    &:hover::-webkit-scrollbar-thumb {
      background-color: #94a3b8;
    }
  }
}
</style>
<style lang="scss">
/* Global Premium Modal Styles for Portals */
.influencer-create-modal {
  .ant-modal-content {
    padding: 0 !important;
    overflow: hidden;
    border-radius: 16px;
    box-shadow: 0 20px 50px rgba(0, 0, 0, 0.15);
  }

  .ic-modal-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px 24px;
    background: #ffffff;
    border-bottom: 1px solid #f1f5f9;
    
    &.glass-header {
      background: rgba(255, 255, 255, 0.95);
    }
    
    .ic-header-left {
      display: flex;
      align-items: center;
      gap: 16px;
    }
    
    .ic-header-icon-wrapper {
      width: 40px;
      height: 40px;
      border-radius: 10px;
      background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
      color: #ffffff;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 18px;
      box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2);
    }
    
    .ic-header-text {
      .ic-header-title {
        font-size: 18px;
        font-weight: 700;
        color: #1e293b;
      }
      .ic-header-subtitle {
        font-size: 12px;
        color: #94a3b8;
      }
    }
    
    .close-btn {
      color: #94a3b8;
      &:hover {
        color: #64748b;
        background: #f1f5f9;
      }
    }
  }

  .ic-modal-body {
    padding: 20px 24px;
    background: #ffffff;
    
    .premium-textarea {
      border-radius: 8px;
      border-color: #e2e8f0;
      font-size: 14px;
      
      &:focus {
        border-color: #3b82f6;
        box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
      }
    }
  }

  .ic-modal-footer {
    padding: 16px 24px;
    background: #ffffff;
    border-top: 1px solid #f1f5f9;
    text-align: right;
    
    .premium-btn {
      height: 36px;
      padding: 0 24px;
      border-radius: 8px;
      font-weight: 600;
      
      &.primary-gradient {
        background: linear-gradient(135deg, #10b981 0%, #059669 100%);
        border: none;
        color: #ffffff;
        
        &:hover {
          transform: translateY(-1px);
          box-shadow: 0 6px 15px rgba(16, 185, 129, 0.3);
        }
      }
    }
  }
}
</style>

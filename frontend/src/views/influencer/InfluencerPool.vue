<template>
  <div class="influencer-pool-page">
    <!-- 筛选区域 -->
    <a-card :bordered="false" class="filter-card glass-card" :body-style="{ padding: '16px 20px' }">
      <a-form :model="filterForm" layout="vertical" size="middle">
        <a-row :gutter="[20, 16]">
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="红人名称">
              <a-input 
                v-model:value.trim="filterForm.name" 
                placeholder="搜索红人姓名..." 
                allow-clear 
                class="premium-input-search"
                :disabled="filterForm.blankFields?.includes('name')"
                @dblclick="openBatchSearch"
              >
                <template #prefix><UserOutlined style="color: #94a3b8" /></template>
                <template #suffix>
                  <a-tooltip title="筛选空白">
                    <span 
                      class="blank-toggle-btn" 
                      :class="{ active: filterForm.blankFields?.includes('name') }" 
                      @click="toggleBlankField('name')"
                    >空白</span>
                  </a-tooltip>
                  <a-tooltip title="点击进行批量搜索">
                    <DatabaseOutlined style="color: #1890ff; cursor: pointer; font-size: 14px" @click="openBatchSearch" />
                  </a-tooltip>
                </template>
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="社媒账号名">
              <a-input 
                v-model:value.trim="filterForm.socialHandle" 
                placeholder="搜索社媒账号..." 
                allow-clear 
                class="premium-input-search"
                :disabled="filterForm.blankFields?.includes('socialHandle')"
                @dblclick="openBatchSearchSocialHandle"
              >
                <template #prefix><ShareAltOutlined style="color: #94a3b8" /></template>
                <template #suffix>
                  <a-tooltip title="筛选空白">
                    <span 
                      class="blank-toggle-btn" 
                      :class="{ active: filterForm.blankFields?.includes('socialHandle') }" 
                      @click="toggleBlankField('socialHandle')"
                    >空白</span>
                  </a-tooltip>
                  <a-tooltip title="点击进行批量搜索">
                    <DatabaseOutlined style="color: #1890ff; cursor: pointer; font-size: 14px" @click="openBatchSearchSocialHandle" />
                  </a-tooltip>
                </template>
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="红人邮箱 (Email)">
              <a-input v-model:value.trim="filterForm.email" placeholder="输入邮件地址" allow-clear class="premium-input" :disabled="filterForm.blankFields?.includes('email')" @dblclick="openBatchSearchEmail">
                 <template #prefix><MailOutlined style="color: #94a3b8" /></template>
                 <template #suffix>
                  <a-tooltip title="筛选空白">
                    <span 
                      class="blank-toggle-btn" 
                      :class="{ active: filterForm.blankFields?.includes('email') }" 
                      @click="toggleBlankField('email')"
                    >空白</span>
                  </a-tooltip>
                  <a-tooltip title="点击进行批量搜索">
                    <DatabaseOutlined style="color: #1890ff; cursor: pointer; font-size: 14px" @click="openBatchSearchEmail" />
                  </a-tooltip>
                </template>
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="红人来源">
              <a-select v-model:value="filterForm.source" placeholder="选择来源" mode="multiple" :max-tag-count="1" show-search :filter-option="filterOption" allow-clear class="premium-select">
                <a-select-option v-for="s in sources" :key="s" :value="s">{{ s }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="负责人">
              <a-select v-model:value="filterForm.owner" placeholder="负责人" mode="multiple" :max-tag-count="1" show-search :filter-option="(input: string, option: any) => (option?.label ?? '').toLowerCase().includes(input.toLowerCase())" allow-clear class="premium-select" @change="(val: any) => onSelectBlankChange(val, 'owner')">
                <a-select-option value="__BLANK__" :label="'（空白）'" style="color: #f59e0b; font-weight: 600;">（空白）</a-select-option>
                <a-select-option v-for="u in ownerUsers" :key="u.id" :value="u.id" :label="u.name">{{ u.name }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="联络人">
              <a-select v-model:value="filterForm.liaisonTagIds" placeholder="选择联络人" mode="multiple" :max-tag-count="1" show-search :filter-option="(input: string, option: any) => (option?.label ?? '').toLowerCase().includes(input.toLowerCase())" allow-clear class="premium-select" @change="(val: any) => onSelectBlankChange(val, 'contactPerson')">
                <a-select-option value="__BLANK__" :label="'（空白）'" style="color: #f59e0b; font-weight: 600;">（空白）</a-select-option>
                <a-select-option v-for="t in liaisonTagOptions" :key="t.id" :value="t.id" :label="t.name">{{ t.name }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>

          <template v-if="filterExpanded">
            <a-col :xs="24" :sm="12" :md="8" :lg="4">
              <a-form-item label="建联平台">
                <a-select v-model:value="filterForm.platform" placeholder="选择平台" mode="multiple" :max-tag-count="1" show-search :filter-option="filterOption" allow-clear class="premium-select" @change="(val: any) => onSelectBlankChange(val, 'platform')">
                  <a-select-option value="__BLANK__" style="color: #f59e0b; font-weight: 600;">（空白）</a-select-option>
                  <a-select-option v-for="p in platforms" :key="p" :value="p">{{ p }}</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :xs="24" :sm="12" :md="8" :lg="4">
              <a-form-item label="红人国家">
                <a-select v-model:value="filterForm.country" placeholder="选择国家" mode="multiple" :max-tag-count="1" show-search :filter-option="filterOption" allow-clear class="premium-select" @change="(val: any) => onSelectBlankChange(val, 'country')">
                  <a-select-option value="__BLANK__" style="color: #f59e0b; font-weight: 600;">（空白）</a-select-option>
                  <a-select-option v-for="c in countries" :key="c" :value="c">{{ c }}</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :xs="24" :sm="12" :md="8" :lg="4">
              <a-form-item label="是否付费">
                <a-select v-model:value="filterForm.isPaid" placeholder="全部状态" allow-clear class="premium-select">
                  <a-select-option :value="true">是</a-select-option>
                  <a-select-option :value="false">否</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :xs="24" :sm="12" :md="8" :lg="4">
              <a-form-item label="主页链接">
                <a-input v-model:value.trim="filterForm.link" placeholder="输入主页链接" allow-clear class="premium-input" @dblclick="openBatchSearchLink">
                  <template #prefix><LinkOutlined style="color: #94a3b8" /></template>
                  <template #suffix>
                    <a-tooltip title="点击进行批量搜索">
                      <DatabaseOutlined style="color: #1890ff; cursor: pointer; font-size: 14px" @click="openBatchSearchLink" />
                    </a-tooltip>
                  </template>
                </a-input>
              </a-form-item>
            </a-col>
            <a-col :xs="24" :sm="12" :md="8" :lg="4">
              <a-form-item label="红人类型">
                <a-select v-model:value="filterForm.influencerType" placeholder="选择类型" allow-clear class="premium-select" @change="(val: any) => onSelectBlankChange(val ? [val] : [], 'influencerType')">
                  <a-select-option value="__BLANK__" style="color: #f59e0b; font-weight: 600;">（空白）</a-select-option>
                  <a-select-option v-for="t in influencerTypes" :key="t" :value="t">{{ t }}</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :xs="24" :sm="12" :md="8" :lg="4">
              <a-form-item label="红人标签">
                <a-select 
                  v-model:value="filterForm.tagIds" 
                  placeholder="选择标签" 
                  mode="multiple" 
                  :max-tag-count="1" 
                  show-search 
                  :filter-option="filterOption" 
                  allow-clear 
                  class="premium-select"
                  @dblclick="openBatchSearchTags"
                >
                  <template #suffixIcon>
                    <UnorderedListOutlined @click.stop="openBatchSearchTags" />
                  </template>
                  <a-select-option v-for="tag in availableTags" :key="tag.id" :value="tag.id">
                    {{ tag.name }}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :xs="24" :sm="12" :md="8" :lg="4">
              <a-form-item label="人种/肤色">
                <a-select v-model:value="filterForm.race" placeholder="选择人种" allow-clear class="premium-select" @change="(val: any) => onSelectBlankChange(val ? [val] : [], 'race')">
                  <a-select-option value="__BLANK__" style="color: #f59e0b; font-weight: 600;">（空白）</a-select-option>
                  <a-select-option v-for="r in races" :key="r" :value="r">{{ r }}</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :xs="24" :sm="12" :md="8" :lg="4">
              <a-form-item label="粉丝量范围">
                <a-select v-model:value="filterForm.fansRange" placeholder="选择区间" allow-clear class="premium-select">
                  <a-select-option value="0-10k">0-10K</a-select-option>
                  <a-select-option value="10k-50k">10K-50K</a-select-option>
                  <a-select-option value="50k-100k">50K-100K</a-select-option>
                  <a-select-option value="100k+">100K+</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :xs="24" :sm="12" :md="8" :lg="4">
              <a-form-item label="时间范围">
                <div class="premium-time-filter-group">
                  <a-radio-group v-model:value="filterForm.timeType" button-style="solid" class="time-type-radio-group">
                    <a-radio-button value="created">创建</a-radio-button>
                    <a-radio-button value="updated">更新</a-radio-button>
                  </a-radio-group>
                  <a-range-picker 
                    v-model:value="filterForm.timeRange" 
                    format="YYYY-MM-DD" 
                    value-format="YYYY-MM-DD" 
                    :presets="timeRanges" 
                    class="premium-datepicker-integrated" 
                  />
                </div>
              </a-form-item>
            </a-col>
          </template>

        </a-row>

        <!-- 筛选按钮区域：持续居右且间距均衡 -->
        <div class="filter-footer-actions">
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
      </a-form>
    </a-card>

    <a-card :bordered="false" class="table-card glass-card" :body-style="{ padding: '0' }">
      <template #title>
        <div class="table-actions-toolbar">
          <div class="status-switcher-wrapper">
               <a-radio-group v-model:value="activeKey" button-style="solid" class="premium-segmented" @change="handleTabChange">
              <a-radio-button value="to-filter">待筛选 ({{ statusCounts.UNSCREENED || 0 }})</a-radio-button>
              <a-radio-button value="rejected">暂不合适 ({{ statusCounts.REJECTED || 0 }})</a-radio-button>
            </a-radio-group>
          </div>
          <a-space size="small" class="toolbar-btns">
            <a-dropdown :disabled="!selectedRowKeys.length" v-permission="'influencer.pool.batch_transfer'">
              <a-button class="transfer-btn" :disabled="!selectedRowKeys.length">
                批量操作 <DownOutlined style="font-size: 10px;" />
              </a-button>
              <template #overlay>
                <a-menu class="premium-menu">
                  <template v-if="activeKey === 'to-filter'">
                    <a-menu-item v-for="opt in getPoolTransferOptions('to-filter')" :key="opt.key" @click="batchMove(opt.key)" v-permission="'influencer.pool.transfer'">{{ opt.label }}</a-menu-item>
                  </template>
                  <template v-else-if="activeKey === 'rejected'">
                    <a-menu-item v-for="opt in getPoolTransferOptions('rejected')" :key="opt.key" @click="batchMove(opt.key)" v-permission="'influencer.pool.transfer'">{{ opt.label }}</a-menu-item>
                  </template>
                </a-menu>
              </template>
            </a-dropdown>
            <a-button @click="openExportModal" class="premium-btn-small" v-permission="'influencer.pool.export'">
              <template #icon><ExportOutlined /></template>导出
            </a-button>
            <a-button @click="openImportModal" class="premium-btn-small" v-permission="'influencer.pool.import'">
              <template #icon><UploadOutlined /></template>导入表格
            </a-button>
            <a-button type="primary" @click="openCreateInfluencer" class="premium-btn-small primary-gradient" v-permission="'influencer.pool.create'">
              <template #icon><PlusOutlined /></template>新建红人
            </a-button>
          </a-space>
        </div>
      </template>

      <!-- VirtualList Disabled -->
        <a-table
          :key="activeKey"
          :columns="columns"
          :data-source="displayData"
          :row-key="(record: any) => record.key ?? record.id"
          :pagination="false"
          :loading="loading"
          :scroll="{ y: filterExpanded ? 'calc(100vh - 460px)' : 'calc(100vh - 350px)', x: 'max-content' }"
          :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
          size="middle"
          class="premium-table"
           :row-class-name="getRowClassName"
        >
        <template #emptyText>
          <div class="table-empty-state">
            <a-empty description="暂无内容" />
          </div>
        </template>
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'userInfo'">
            <div class="user-info-cell">
              <div class="avatar-wrapper">
                <a-avatar :src="record.avatar" :size="44" class="premium-avatar" :style="{ background: record.avatar ? 'transparent' : getAvatarColor(record.name) + ' !important' }">{{ (record.name || '?').charAt(0).toUpperCase() }}</a-avatar>
              </div>
              <div class="info-content">
                <div class="user-name">{{ record.name }}</div>
                <div class="user-id">ID: {{ record.id }}</div>
              </div>
            </div>
          </template>

          <template v-else-if="column.key === 'socialMedia'">
            <div class="social-media-cell">
              <template v-if="record.socialMedias?.length || record.socialMediaList?.length">
                <div v-for="(sm, idx) in (record.socialMedias || record.socialMediaList)" :key="idx" class="social-item">
                  <div class="sm-detail-inline">
                    <span class="sm-platform-tag" :class="`platform-${getPlatformAbbr(sm.platform)}`">{{ getPlatformAbbr(sm.platform) }}</span>
                    <span class="sm-handle">{{ extractHandle(sm.handle || sm.url || '') }}</span>
                    <a-tooltip title="复制账号">
                      <CopyOutlined class="copy-btn-icon" @click.stop="copyText(extractHandle(sm.handle || sm.url || ''))" />
                    </a-tooltip>
                    <a-tooltip :title="sm.url || sm.handle">
                      <a v-if="sm.url || sm.handle" :href="formatUrlForHref(sm.url || sm.handle)" target="_blank" class="sm-link-icon" @click.stop>
                        <LinkOutlined />
                      </a>
                    </a-tooltip>
                  </div>
                </div>
              </template>
              <template v-else-if="record.defaultHandle || record.profileLink">
                <div class="social-item">
                  <div class="sm-detail-inline">
                    <span class="sm-platform-tag" :class="`platform-${getPlatformAbbr(record.platformName || record.defaultPlatform || 'Instagram')}`">{{ getPlatformAbbr(record.platformName || record.defaultPlatform || 'Instagram') }}</span>
                    <span class="sm-handle">{{ extractHandle(record.defaultHandle || record.profileLink || '') }}</span>
                    <a-tooltip title="复制账号">
                      <CopyOutlined class="copy-btn-icon" @click.stop="copyText(extractHandle(record.defaultHandle || record.profileLink || ''))" />
                    </a-tooltip>
                    <a-tooltip :title="record.profileLink || record.defaultHandle">
                      <a v-if="record.profileLink || record.defaultHandle" :href="formatUrlForHref(record.profileLink || record.defaultHandle)" target="_blank" class="sm-link-icon" @click.stop>
                        <LinkOutlined />
                      </a>
                    </a-tooltip>
                  </div>
                </div>
              </template>
              <div v-else style="color: #cbd5e1;">-</div>
            </div>
          </template>

          <template v-else-if="column.key === 'contact'">
            <div class="contact-cell">
              <div v-if="record.email" class="contact-item" style="display: flex; align-items: center; gap: 6px;">
                <MailOutlined style="color: #94a3b8;" />
                <span :title="record.email" style="font-family: 'JetBrains Mono', monospace; font-size: 12px; color: #475569;">{{ record.email }}</span>
              </div>
              <div v-if="record.phone" class="contact-item" style="display: flex; align-items: center; gap: 6px; margin-top: 2px;">
                <span style="color: #94a3b8; font-size: 12px;">📱</span>
                <span style="font-size: 12px; color: #475569;">{{ record.phone }}</span>
              </div>
              <div v-if="!record.email && !record.phone" style="color: #cbd5e1;">-</div>
            </div>
          </template>

          <template v-if="column.key === 'platform'">
            <div class="platform-tags centered-tags">
              <a-tag v-if="record.displayPlatform" :class="['premium-platform-tag', `platform-${getPlatformAbbr(record.displayPlatform)}`]">
                <template #icon>
                  <component :is="getPlatformIcon(record.displayPlatform)" />
                </template>
                {{ getPlatformAbbr(record.displayPlatform) }}
              </a-tag>
              <span v-else style="color: #cbd5e1;">-</span>
            </div>
          </template>

          <template v-else-if="column.key === 'contactPerson'">
            <span style="color: #475569; font-size: 13px;">{{ record.contactPersonName || '-' }}</span>
          </template>
          <template v-else-if="column.key === 'owner'">
            <span style="color: #475569; font-size: 13px;">{{ record.ownerName || (record.ownerId ? `ID:${record.ownerId}` : '-') }}</span>
          </template>

          <template v-else-if="column.key === 'isPaid'">
              <span :class="['status-icon-badge', record.isPaid ? 'yes' : 'no']">
                {{ record.isPaid ? '是' : '否' }}
              </span>
          </template>
          <template v-else-if="column.key === 'source'">
             <div v-if="record.source">
                <a-tag :bordered="false" class="premium-source-tag">{{ record.source }}</a-tag>
             </div>
             <span v-else>-</span>
          </template>
           <template v-else-if="column.key === 'influencerType'">
             <a-tag v-if="record.influencerType" class="status-tag tag-purple">{{ record.influencerType }}</a-tag>
             <span v-else>-</span>
          </template>
          <template v-else-if="column.key === 'tags'">
             <div class="pill-tags-wrapper">
              <a-tag v-for="(tag, index) in record.tags" :key="`${tag}-${index}`" class="pill-tag">{{ typeof tag === 'object' ? tag.name : (tag || '-') }}</a-tag>
            </div>
          </template>
          <template v-else-if="column.key === 'totalFans'">
             <span>{{ record.totalFans?.toLocaleString() || 0 }}</span>
          </template>
          <template v-else-if="column.key === 'updatedAt'">
             <span style="color: #64748b; font-size: 12px;">{{ record.updatedAt || '-' }}</span>
          </template>
          <template v-else-if="column.key === 'action'">
            <div class="action-btns-grid">
              <a-button type="primary" size="small" @click="handleViewDetail(record)" class="grid-btn detail-btn" v-permission="'influencer.pool.view'">详情</a-button>

              <a-dropdown placement="bottomRight" trigger="click">
                <a-button class="grid-btn more-btn" size="small">
                  更多 <DownOutlined style="font-size: 10px;"/>
                </a-button>
                <template #overlay>
                  <a-menu class="premium-menu">
                    <template v-if="activeKey === 'to-filter' || activeKey === 'rejected'">
                      <a-sub-menu key="transfer" v-permission="'influencer.pool.transfer'">
                        <template #title>
                          <span><SwapOutlined /> 状态流转</span>
                        </template>
                        <a-menu-item v-for="opt in getPoolTransferOptions(activeKey)" :key="opt.key" @click="(e: any) => handleTransfer(e, record)">
                          {{ opt.label }}
                        </a-menu-item>
                      </a-sub-menu>
                      <a-menu-divider />
                    </template>
                    
                    <a-menu-item key="delete" style="color: #ef4444;" @click="handleDelete(record.id)" v-permission="'influencer.pool.delete'">
                      <DeleteOutlined /> 删除记录
                    </a-menu-item>
                  </a-menu>
                </template>
              </a-dropdown>
            </div>
          </template>
          <template v-else>
            {{ record[column.dataIndex] ?? record[column.key] ?? '-' }}
          </template>
        </template>
        </a-table>


      <div class="pagination-footer">
        <div class="footer-left">
           <span class="info-item">当前内容数量 <span class="count-value">{{ pagination.total }}</span></span>
           <span class="info-divider">/</span>
           <span class="info-item">选中数量 <span class="count-value highlight">{{ selectedRowKeys.length }}</span></span>
        </div>
        <div class="footer-right">
             <a-pagination
            v-model:current="pagination.current"
            v-model:pageSize="pagination.pageSize"
            :total="pagination.total"
            :show-size-changer="true"
            :show-quick-jumper="true"
            size="small"
            @change="handlePageChange"
          />
        </div>
      </div>
    </a-card>

    <!-- 红人详情弹窗（资源池：隐藏打款信息 / 样品单信息等财务类 Tab） -->
    <InfluencerDetailModal
      v-model:open="detailModalVisible"
      :influencer-data="currentInfluencer"
      :show-finance-tabs="false"
    />
    
    <!-- 导出字段选择弹窗 -->
    <InfluencerExportModal
      v-model:open="exportModalVisible"
      :selected-count="selectedRowKeys.length"
      :export-fields="exportFields"
      page-type="influencer-pool"
      :current-user-id="currentUserId"
      @export="handleExportFromModal"
    />
    
    <!-- 导入表格弹窗 -->
    <InfluencerImportModal
      v-model:open="importModalVisible"
      :user-list="allUsers"
      stage="POOL"
      @success="fetchData"
    />
    
    <!-- 原因输入弹窗 -->
    <InfluencerTransferModal
      v-model:open="reasonModalVisible"
      :title="reasonModalTitle"
      @confirm="handleConfirmTransfer"
    />
    
    <!-- 批量操作原因输入弹窗 -->
    <InfluencerBatchTransferModal
      v-model:open="batchReasonModalVisible"
      :title="batchReasonModalTitle"
      :items="batchReasonItems"
      @confirm="handleConfirmBatchTransfer"
    />

    <!-- 沟通记录弹窗 -->
    <CommunicationLogModal
      v-model:open="commLogVisible"
      :influencer-id="commLogInfluencerId"
      :influencer-name="commLogInfluencerName"
    />
    
    <!-- 新建红人弹窗 -->
    <InfluencerCreateModal
      v-model:open="createModalVisible"
      initial-stage="POOL"
      initial-status="UNSCREENED"
      @created="handleCreatedInfluencer"
    />
    
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
            <UnorderedListOutlined />
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, watch, computed, onMounted } from 'vue';
import { storeToRefs } from 'pinia';
import { 
  PlusOutlined, DownOutlined, UpOutlined, ExportOutlined,
  CloseOutlined, 
  UserOutlined, MailOutlined, LinkOutlined,
  ShareAltOutlined,
  UnorderedListOutlined, DatabaseOutlined, UploadOutlined,
  CopyOutlined, SwapOutlined, DeleteOutlined, MoreOutlined
} from '@ant-design/icons-vue';
import type { TableColumnsType } from 'ant-design-vue';
import { message } from 'ant-design-vue';
import dayjs from 'dayjs';
import type { MenuInfo } from 'ant-design-vue/es/menu/src/interface';
import { PLATFORMS, COUNTRIES, SOURCES, RACES } from '@/config/constants';
const races = RACES;
import { filterInfluencers, formatFilterForApi } from '@/composables/useInfluencerFilter';
import { influencerService } from '@/services/influencerService';
import InfluencerDetailModal from '@/components/influencer/InfluencerDetailModal.vue';
import InfluencerExportModal from '@/components/influencer/InfluencerExportModal.vue';
import InfluencerImportModal from '@/components/influencer/InfluencerImportModal.vue';
import InfluencerTransferModal from '@/components/influencer/InfluencerTransferModal.vue';
import InfluencerBatchTransferModal from '@/components/influencer/InfluencerBatchTransferModal.vue';
import InfluencerCreateModal from '@/components/influencer/InfluencerCreateModal.vue';
import CommunicationLogModal from '@/components/influencer/CommunicationLogModal.vue';
import { createExportTask } from '@/utils/exportTaskHelper';
import { useUserStore } from '@/stores/user';
import { useCommonDataStore } from '@/stores/commonData';
import { useInfluencerSSE } from '@/composables/useInfluencerSSE';
import { getPlatformIcon, getPlatformAbbr, formatUrlForHref, extractHandle } from '@/utils/platform';
import {
  setCurrentUserIdGetter
} from '@/utils/exportTemplate';
import { STATUS_LABEL } from '@/types/enums';
import VirtualInfluencerRow from '@/components/common/VirtualInfluencerRow.vue';

const getAvatarColor = (name: string) => {
  if (!name) return '#cbd5e1';
  const colors = ['#8b5cf6', '#3b82f6', '#10b981', '#f59e0b', '#ec4899', '#6366f1', '#14b8a6', '#f43f5e'];
  let hash = 0;
  for (let i = 0; i < name.length; i++) {
    hash = name.charCodeAt(i) + ((hash << 5) - hash);
  }
  return colors[Math.abs(hash) % colors.length];
};

const activeKey = ref('to-filter');
const loading = ref(false);
const selectedRowKeys = ref<(string | number)[]>([]);
const filterExpanded = ref(false);
const detailModalVisible = ref(false);
const currentInfluencer = ref<any>(null);
const commonStore = useCommonDataStore();
const { allUsers, ownerUsers, userNameMap, availableTags, liaisonTagOptions, platforms, dynamicInfluencerTypes, countries } = storeToRefs(commonStore);
const statusCounts = ref<Record<string, number>>({});

// 初始化用户ID（从localStorage或user store获取）
const userStore = useUserStore();
const currentUserId = ref<string>('default_user');

// 设置用户ID获取器
setCurrentUserIdGetter(() => {
  const raw = localStorage.getItem('auth_token');
  if (raw) {
    try {
      const obj = JSON.parse(raw);
      const expiresAt = Number(obj?.expiresAt) || 0;
      if (!expiresAt || Date.now() <= expiresAt) {
        const t = String(obj?.token || '');
        if (t) return `user_${t.substring(0, 8)}`;
      }
    } catch {}
  }
  return 'default_user';
});

watch(() => userStore.userInfo, (userInfo) => {
  if (userInfo && typeof userInfo === 'object') {
    const userInfoObj = userInfo as Record<string, unknown>;
    currentUserId.value = (userInfoObj.id || userInfoObj.userId || 'default_user') as string;
  }
}, { immediate: true });

// 流转相关
const reasonModalVisible = ref(false);
const reasonModalTitle = ref('');

const pendingTransfer = ref<{
  record: any;
  status: string;
  statusText: string;
  targetStage?: string;
} | null>(null);

// 批量操作相关
const batchReasonModalVisible = ref(false);
const batchReasonModalTitle = ref('');
const batchReasonItems = ref<Array<{
  id: string;
  name: string;
}>>([]);
const pendingBatchTransfer = ref<{
  status: string;
  statusText: string;
  targetStage?: string;
} | null>(null);

// 新建红人弹窗相关
const createModalVisible = ref(false);

// 打开新建红人弹窗
const openCreateInfluencer = () => {
  createModalVisible.value = true;
};

// 创建红人成功回调
const handleCreatedInfluencer = (_payload: any) => {
  message.success('红人已创建并添加到待筛选列表');
  createModalVisible.value = false;
  // 刷新列表
  handleFilter();
};

// 状态映射
const statusMap: Record<string, string> = {
  pending: '待联系',
  rejected: '暂不合适',
};

// 需要输入原因的状态
const statusRequireReason = ['rejected'];

// 沟通记录弹窗
const commLogVisible = ref(false);
const commLogInfluencerId = ref<number>(0);
const commLogInfluencerName = ref('');

const openCommLog = (record: any) => {
  commLogInfluencerId.value = record.id;
  commLogInfluencerName.value = record.name || record.realName || record.nickName || '';
  commLogVisible.value = true;
};

const handleDelete = async (id: number) => {
  try {
    await influencerService.delete(id);
    message.success('删除成功');
    fetchData();
  } catch (e) {
    message.error('删除失败');
  }
};

const copyText = (text: string | null | undefined) => {
  if (!text) {
    message.warn('无内容可复制');
    return;
  }
  const str = String(text);
  if (navigator.clipboard && navigator.clipboard.writeText) {
    navigator.clipboard.writeText(str).then(() => {
      message.success('已复制到剪贴板');
    }).catch(err => {
      fallbackCopyText(str);
    });
  } else {
    fallbackCopyText(str);
  }
};

const fallbackCopyText = (text: string) => {
  const textArea = document.createElement('textarea');
  textArea.value = text;
  document.body.appendChild(textArea);
  textArea.select();
  try {
    document.execCommand('copy');
    message.success('已复制到剪贴板');
  } catch (err) {
    message.error('复制失败');
  }
  document.body.removeChild(textArea);
};

// 导入相关
const importModalVisible = ref(false);

// 批量搜索相关
type BatchSearchType = 'name' | 'email' | 'link' | 'socialHandle' | 'tags';
const batchSearchVisible = ref(false);
const batchSearchValue = ref('');
const batchSearchType = ref<BatchSearchType>('name');
const batchSearchTitles: Record<BatchSearchType, { title: string; subtitle: string }> = {
  name: { title: '批量搜索红人', subtitle: '请输入红人名称，多个名称请用换行或逗号分隔' },
  email: { title: '批量搜索邮箱', subtitle: '请输入红人邮箱，多个邮箱请用换行或逗号分隔' },
  link: { title: '批量搜索主页', subtitle: '请输入主页链接，多个链接请用换行或逗号分隔' },
  socialHandle: { title: '批量搜索社媒账号', subtitle: '请输入社媒账号名称，多个账号请用换行或逗号分隔' },
  tags: { title: '批量搜索标签', subtitle: '请输入标签名称，多个标签请用换行或逗号分隔' }
};

const timeRanges = [
  { label: '今天', value: [dayjs().startOf('day'), dayjs().endOf('day')] },
  { label: '最近7天', value: [dayjs().subtract(7, 'd'), dayjs()] },
  { label: '最近30天', value: [dayjs().subtract(30, 'd'), dayjs()] },
];

const batchSearchPlaceholder = computed(() => {
  if (batchSearchType.value === 'tags') {
    return '例如：\nBeauty\nFashion, Sports';
  }
  return '例如：\nInfluencer A\nInfluencer B, Influencer C';
});

// Batch Search — 统一入口
const openBatchSearchByType = (type: BatchSearchType = 'name') => {
  batchSearchType.value = type;
  if (type === 'tags') {
    if (filterForm.tagIds && filterForm.tagIds.length > 0) {
      const tags = Array.isArray(availableTags.value) ? availableTags.value : [];
      batchSearchValue.value = filterForm.tagIds
        .map(id => tags.find(t => t.id === id)?.name || '')
        .filter(Boolean).join('\n');
    } else {
      batchSearchValue.value = '';
    }
  } else {
    const fieldMap: Record<string, string | undefined> = {
      name: filterForm.name,
      email: filterForm.email,
      link: filterForm.link,
      socialHandle: filterForm.socialHandle,
    };
    const val = fieldMap[type] || '';
    batchSearchValue.value = val ? val.split(',').join('\n') : '';
  }
  batchSearchVisible.value = true;
};

const openBatchSearch = () => openBatchSearchByType('name');
const openBatchSearchEmail = () => openBatchSearchByType('email');
const openBatchSearchLink = () => openBatchSearchByType('link');
const openBatchSearchSocialHandle = () => openBatchSearchByType('socialHandle');
const openBatchSearchTags = () => openBatchSearchByType('tags');

const handleBatchSearch = () => {
  const values = batchSearchValue.value.split(/[\n,;，；\t]+/).map(v => v.trim()).filter(Boolean);
  if (!values.length) {
    message.warning('请输入搜索内容');
    return;
  }
  // 根据类型设置不同的筛选字段
  if (batchSearchType.value === 'name') {
    filterForm.name = values.join(',');
  } else if (batchSearchType.value === 'email') {
    filterForm.email = values.join(',');
  } else if (batchSearchType.value === 'link') {
    filterForm.link = values.join(',');
  } else if (batchSearchType.value === 'socialHandle') {
    filterForm.socialHandle = values.join(',');
  } else if (batchSearchType.value === 'tags') {
    // Resolve Tag IDs
    const matchedIds: number[] = [];
    const notFound: string[] = [];
    
    values.forEach(tagName => {
      const tag = availableTags.value.find(t => t.name.toLowerCase() === tagName.toLowerCase());
      if (tag) {
        matchedIds.push(tag.id);
      } else {
        notFound.push(tagName);
      }
    });

    if (notFound.length > 0) {
      message.warn(`未找到以下标签: ${notFound.join(', ')}`);
    }

    if (matchedIds.length > 0) {
      filterForm.tagIds = [...(filterForm.tagIds || []), ...matchedIds];
      // remove duplicates
      filterForm.tagIds = [...new Set(filterForm.tagIds)];
    }
  }
  batchSearchVisible.value = false;
  handleFilter();
};

// 定义所有可导出的字段
const exportFields = [
  { key: 'id', title: '红人ID', dataKey: 'id' },
  { key: 'name', title: '红人全名', dataKey: 'name' },
  { key: 'email', title: '红人邮箱', dataKey: 'email' },
  { key: 'backupEmail', title: '红人备用邮箱', dataKey: 'backupEmail' },
  { key: 'country', title: '国家', dataKey: 'country' },
  { key: 'race', title: '人种', dataKey: 'race' },
  { key: 'igHandle', title: 'IG账号名', dataKey: 'igHandle' },
  { key: 'igLink', title: 'IG link', dataKey: 'igLink' },
  { key: 'igFans', title: 'IG粉丝量', dataKey: 'igFans' },
  { key: 'ttHandle', title: 'TT账号名', dataKey: 'ttHandle' },
  { key: 'ttLink', title: 'TT link', dataKey: 'ttLink' },
  { key: 'ttFans', title: 'TT粉丝量', dataKey: 'ttFans' },
  { key: 'ytHandle', title: 'YT账号名', dataKey: 'ytHandle' },
  { key: 'ytLink', title: 'YT link', dataKey: 'ytLink' },
  { key: 'ytFans', title: 'YT粉丝量', dataKey: 'ytFans' },
  { key: 'ownerName', title: '负责人', dataKey: 'ownerName' },
  { key: 'contactPersonName', title: '联络员', dataKey: 'contactPersonName' },
  { key: 'isPaid', title: '是否付费', dataKey: 'isPaidText' },
  { key: 'influencerType', title: '红人类型', dataKey: 'influencerType' },
  { key: 'tags', title: '红人标签', dataKey: 'tagsText' },
  { key: 'source', title: '来源', dataKey: 'source' },
  { key: 'sourceValue', title: '来源hashtag', dataKey: 'sourceValue' },
  { key: 'createdAt', title: '创建时间', dataKey: 'createdAt' },
  { key: 'updatedAt', title: '更新时间', dataKey: 'updatedAt' },
  { key: 'status', title: '状态', dataKey: 'statusText' },
  { key: 'description', title: '备注', dataKey: 'descriptionText' }
];

// 导出相关
const exportModalVisible = ref(false);

// 打开导出弹窗
const openExportModal = () => {
  exportModalVisible.value = true;
};

// 确认导出 (来自组件事件)
const handleExportFromModal = async (payload: { scope: string, fields: string[], columns: any[], templateId?: string, templateName?: string }) => {
  try {
    const { scope, columns, templateId, templateName } = payload;
    
    // 根据导出范围获取数据
    let exportData: any[] = [];
    if (scope === 'selected') {
      // 导出选中数据
      exportData = displayData.value.filter((item: any) => 
        selectedRowKeys.value.includes(item.id || item.key)
      );
    } else {
      message.loading({ content: '正在获取全部数据...', key: 'exportAllMsg' });
      // 导出全部数据
      const stageStatus = poolStatusMap[activeKey.value] ?? poolStatusMap['to-filter'];
      const apiFilter = formatFilterForApi({
        ...filterForm,
        page: 1,
        size: pagination.total > 0 ? pagination.total : 10000,
      }, activeKey.value);
      apiFilter.stage = stageStatus?.stage || 'POOL';
      apiFilter.status = stageStatus?.status || 'UNSCREENED';
      
      const res = await influencerService.getList(apiFilter);
      exportData = res.content || [];
      message.success({ content: '获取数据成功，开始导出', key: 'exportAllMsg' });
    }
    
    // 格式化导出数据
    const formattedData = exportData.map((item: any) => {
      const socialMedias = item.socialMedias || item.socialMediaList || [];
      const getSM = (platform: string) => socialMedias.find((s: any) => s.platform?.toUpperCase() === platform.toUpperCase());
      
      const ig = getSM('INSTAGRAM') || getSM('IG');
      const tt = getSM('TIKTOK') || getSM('TT');
      const yt = getSM('YOUTUBE') || getSM('YT');

      return {
        ...item,
        name: item.name || item.realName || item.nickName || (item.email ? item.email.split('@')[0] : '未命名'),
        isPaidText: item.isPaid ? '是' : '否',
        statusText: STATUS_LABEL[item.status as keyof typeof STATUS_LABEL] || item.status || '-',
        ownerName: item.ownerId ? (userNameMap.value[item.ownerId] || `ID:${item.ownerId}`) : '-',
        contactPersonName: item.contactPersonId ? (userNameMap.value[item.contactPersonId] || `ID:${item.contactPersonId}`) : '-',
        igHandle: ig?.handle || '-',
        igLink: ig?.url || '-',
        igFans: ig?.followerCount || 0,
        ttHandle: tt?.handle || '-',
        ttLink: tt?.url || '-',
        ttFans: tt?.followerCount || 0,
        ytHandle: yt?.handle || '-',
        ytLink: yt?.url || '-',
        ytFans: yt?.followerCount || 0,
        tagsText: Array.isArray(item.tags) ? item.tags.map((t: any) => typeof t === 'object' ? t.name : t).join(', ') : '',
        createdAt: item.createdAt ? dayjs(item.createdAt).format('YYYY-MM-DD HH:mm') : '-',
        updatedAt: item.updatedAt ? dayjs(item.updatedAt).format('YYYY-MM-DD HH:mm') : '-',
        descriptionText: (item.description || '').replace(/Import Cost: .*/g, '').trim() || '-'
      };
    });

    if (formattedData.length === 0) {
      message.warning('没有可导出的数据');
      return;
    }
    
    // 使用统一的导出任务助手
    await createExportTask({
      data: formattedData,
      columns: columns,
      filename: `资源池_${activeKey.value}${scope === 'selected' ? '_选中' : ''}`,
      pageType: 'influencer-pool',
      templateId: templateId,
      templateName: templateName,
    });
    
    exportModalVisible.value = false;
  } catch (error) {
    console.error('导出失败:', error);
  }
};

// 打开导入弹窗
const openImportModal = () => {
  importModalVisible.value = true;
};


const filterForm = reactive({
  name: '',
  socialHandle: '', // 社媒账号名
  platform: undefined,
  country: undefined,
  source: undefined,
  email: '',
  link: '',
  isPaid: undefined,
  sourceDetail: '',
  influencerType: undefined,
  race: undefined, // 人种/肤色
  tagIds: undefined as number[] | undefined,
  liaisonTagIds: undefined as number[] | undefined,
  fansRange: undefined,
  owner: undefined,
  contactPerson: undefined,
  timeType: 'created',
  timeRange: undefined as [string, string] | undefined,
  blankFields: [] as string[],
});


const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 0, // 初始化为0，会在数据加载时更新
  showSizeChanger: true,
  showQuickJumper: true,
});

// 基础列定义
const columns: TableColumnsType = [
  { title: '红人信息', key: 'userInfo', width: 280, fixed: 'left' },
  { title: '社媒信息', key: 'socialMedia', width: 280 },
  { title: '联系方式', key: 'contact', width: 200 },
  { title: '建联平台', key: 'platform', width: 140 },
  { title: '红人国家', dataIndex: 'country', key: 'country', width: 120 },
  { title: '人种/肤色', dataIndex: 'race', key: 'race', width: 120 },
  { title: '联络人', key: 'contactPerson', width: 120 },
  { title: '负责人', key: 'owner', width: 120 },
  { title: '来源', key: 'source', width: 120 },
  { title: '红人标签', key: 'tags', width: 200 },
  { title: '是否付费', key: 'isPaid', width: 100 },
  { title: '粉丝总量', key: 'totalFans', width: 120 },
  { title: '红人类型', dataIndex: 'influencerType', key: 'influencerType', width: 140 },
  { title: '最近更新', key: 'updatedAt', width: 150 },
  { title: '操作', key: 'action', width: 180, fixed: 'right' },
];

// 标签数据 (从 API 加载)
const influencerTypes = computed(() => dynamicInfluencerTypes.value);
// platforms, countries 均由 commonStore 提供
const sources = [...SOURCES];

// 资源池状态映射
const poolStatusMap: Record<string, { stage: string; status: string }> = {
  'to-filter': { stage: 'POOL', status: 'UNSCREENED' },
  'rejected': { stage: 'POOL', status: 'REJECTED' },
};

// 不再使用 mockData，改用 API 获取数据
const displayData = ref<any[]>([]);

// 获取数据 (API 调用)
const fetchData = async () => {
  commonStore.loadPlatforms(); // 确保平台标签已加载
  commonStore.loadInfluencerTypes(); // 确保红人类型已加载
  loading.value = true;
  try {
    const stageStatus = poolStatusMap[activeKey.value] ?? poolStatusMap['to-filter'];
    const apiFilter = formatFilterForApi({
      ...filterForm,
      page: pagination.current,
      size: pagination.pageSize,
    }, activeKey.value);
    // 资源池使用 POOL stage
    apiFilter.stage = stageStatus?.stage || 'POOL';
    apiFilter.status = stageStatus?.status || 'UNSCREENED';
    
    const res = await influencerService.getList(apiFilter);
    // 同时获取各状态数量
    try {
      const poolParams = { stage: 'POOL' } as any;
      const counts = await influencerService.getStatusCounts(poolParams);
      statusCounts.value = counts || {};
    } catch (e) { console.error('Failed to fetch pool status counts:', e); }
    const cleanContent = (res.content || []).filter((item: any) => item && item.id).map((item: any) => {
      // 逻辑：优先寻找 isDefault/isPrimary 的社媒平台
      let displayPlatform = item.defaultPlatform || item.platformName;
      
      const smList = item.socialMedias || item.socialMediaList || [];
      const defaultSm = smList.find((sm: any) => sm.isDefault || sm.isPrimary);
      
      if (defaultSm) {
        displayPlatform = defaultSm.platform;
      } else if (smList.length > 0 && !displayPlatform) {
        displayPlatform = smList[0].platform;
      }

      return {
        ...item,
        displayPlatform,
        tags: Array.isArray(item.tags) ? item.tags : [],
        ownerName: item.ownerId ? (userNameMap.value[item.ownerId] || `ID:${item.ownerId}`) : '-',
        contactPersonName: item.contactPersonId ? (userNameMap.value[item.contactPersonId] || `ID:${item.contactPersonId}`) : '-',
      };
    });
    displayData.value = cleanContent;
    pagination.total = res.totalElements || 0;
  } catch (error: any) {
    if (error.name === 'CanceledError' || error.code === 'ERR_CANCELED') return;
    console.error('Fetch data failed', error);
  } finally {
    loading.value = false;
  }
};



const handlePageChange = () => {
  fetchData();
};

const handleTabChange = (_key: string) => {
  pagination.current = 1;
  selectedRowKeys.value = [];
  fetchData();
};

const filterOption = (input: string, option: any) => {
  return option.children.toLowerCase().indexOf(input.toLowerCase()) >= 0;
};

const handleFilter = () => {
  pagination.current = 1;
  fetchData();
};

const openLink = (url: string) => {
  if (url) window.open(url, '_blank');
};

const copyLink = (url: string) => {
  if (!url) return;
  navigator.clipboard.writeText(url).then(() => {
    message.success('链接已复制');
  }).catch(() => {
    message.error('复制失败');
  });
};

// ==================== 空白筛选逻辑 ====================
const blankFieldToFilterKey: Record<string, string> = {
  socialPlatform: 'socialPlatform',
  platform: 'platform',
  country: 'country',
  influencerType: 'influencerType',
  race: 'race',
  owner: 'owner',
  contactPerson: 'contactPerson',
};

/** 文本输入框的「空白」开关 */
const toggleBlankField = (field: string) => {
  const idx = filterForm.blankFields.indexOf(field);
  if (idx >= 0) {
    filterForm.blankFields.splice(idx, 1);
  } else {
    filterForm.blankFields.push(field);
    if (field === 'name') filterForm.name = '';
    if (field === 'socialHandle') filterForm.socialHandle = '';
    if (field === 'email') filterForm.email = '';
  }
};

/** 下拉选择的「（空白）」互斥处理 */
const onSelectBlankChange = (val: any, blankFieldName: string) => {
  const filterKey = blankFieldToFilterKey[blankFieldName];
  if (!filterKey) return;

  if (Array.isArray(val) && val.includes('__BLANK__')) {
    (filterForm as any)[filterKey] = ['__BLANK__'];
    if (!filterForm.blankFields.includes(blankFieldName)) {
      filterForm.blankFields.push(blankFieldName);
    }
  } else {
    const idx = filterForm.blankFields.indexOf(blankFieldName);
    if (idx >= 0) filterForm.blankFields.splice(idx, 1);
  }
};

const handleResetFilter = () => {
  filterForm.name = '';
  filterForm.socialHandle = '';
  filterForm.platform = undefined;
  filterForm.country = undefined;
  filterForm.source = undefined;
  filterForm.email = '';
  filterForm.link = '';
  filterForm.isPaid = undefined;
  filterForm.sourceDetail = '';
  filterForm.influencerType = undefined;
  filterForm.race = undefined;
  filterForm.tagIds = undefined;
  filterForm.liaisonTagIds = undefined;
  filterForm.fansRange = undefined;
  filterForm.owner = undefined;
  filterForm.contactPerson = undefined;
  filterForm.timeType = 'created';
  filterForm.timeRange = undefined;
  filterForm.blankFields = [];
  handleFilter();
};

const onSelectChange = (keys: (string | number)[]) => {
  selectedRowKeys.value = keys;
};
const onToggleSelectVirtual = (key: string | number) => {
  const set = new Set(selectedRowKeys.value);
  if (set.has(key)) set.delete(key);
  else set.add(key);
  selectedRowKeys.value = Array.from(set);
};

const handleViewDetail = (record: any) => {
  currentInfluencer.value = { ...record };
  detailModalVisible.value = true;
};

// 解析流转目标状态和阶段
const parseTransferTarget = (key: string): { targetStatus: string; targetStage?: string; label: string } => {
  const targets: Record<string, { targetStatus: string; targetStage?: string; label: string }> = {
    'PENDING_ONBOARDED': { targetStatus: 'PENDING', targetStage: 'ONBOARDED', label: '红人列表-待联系' },
    'CONTACTED_ONBOARDED': { targetStatus: 'CONTACTED', targetStage: 'ONBOARDED', label: '红人列表-已联系' },
    'COMMUNICATING_ONBOARDED': { targetStatus: 'COMMUNICATING', targetStage: 'ONBOARDED', label: '红人列表-沟通中' },
    'COOPERATING_ONBOARDED': { targetStatus: 'COOPERATING', targetStage: 'ONBOARDED', label: '红人列表-合作中' },
    'DORMANT_ONBOARDED': { targetStatus: 'DORMANT', targetStage: 'ONBOARDED', label: '红人列表-休眠中' },
    'PAUSED_ONBOARDED': { targetStatus: 'PAUSED', targetStage: 'ONBOARDED', label: '红人列表-暂不合作' },
    'BLACKLIST_ONBOARDED': { targetStatus: 'BLACKLIST', targetStage: 'ONBOARDED', label: '红人列表-黑名单' },
    'TERMINATED_ONBOARDED': { targetStatus: 'TERMINATED', targetStage: 'ONBOARDED', label: '红人列表-不再合作' },
    'REJECTED': { targetStatus: 'REJECTED', label: '暂不合适' },
    'UNSCREENED': { targetStatus: 'UNSCREENED', label: '待筛选' },
  };
  return targets[key] || { targetStatus: key, label: key };
};

// 资源池流转选项：完全放开，可流转到除当前状态外的任意状态
const getPoolTransferOptions = (currentKey: string): {key: string, label: string}[] => {
  const allOptions: {key: string, label: string}[] = [
    { key: 'UNSCREENED', label: '待筛选' },
    { key: 'REJECTED', label: '暂不合适' },
    { key: 'PENDING_ONBOARDED', label: '红人列表-待联系' },
    { key: 'CONTACTED_ONBOARDED', label: '红人列表-已联系' },
    { key: 'COMMUNICATING_ONBOARDED', label: '红人列表-沟通中' },
    { key: 'COOPERATING_ONBOARDED', label: '红人列表-合作中' },
    { key: 'DORMANT_ONBOARDED', label: '红人列表-休眠中' },
    { key: 'PAUSED_ONBOARDED', label: '红人列表-暂不合作' },
    { key: 'BLACKLIST_ONBOARDED', label: '红人列表-黑名单' },
    { key: 'TERMINATED_ONBOARDED', label: '红人列表-不再合作' },
  ];
  // 过滤掉当前 tab 对应的选项
  const currentStatusMap: Record<string, string> = {
    'to-filter': 'UNSCREENED',
    'rejected': 'REJECTED',
  };
  const currentStatus = currentStatusMap[currentKey];
  return allOptions.filter(opt => opt.key !== currentStatus);
};


// 处理流转操作
const handleTransfer = (menuInfo: MenuInfo, record: any) => {
  const key = menuInfo.key as string;
  const { targetStatus, targetStage, label } = parseTransferTarget(key);
  
  // 暂不合适需要输入原因
  if (statusRequireReason.includes(targetStatus.toLowerCase())) {
    pendingTransfer.value = { record, status: targetStatus, statusText: label, targetStage };
    reasonModalTitle.value = `流转至${label}`;
    reasonModalVisible.value = true;
  } else {
    executeTransfer(record, targetStatus, label, undefined, targetStage);
  }
};

// 确认流转
const handleConfirmTransfer = (payload: { reasonType: string; reason: string }) => {
  if (!pendingTransfer.value) return;
  
  const { record, status, statusText, targetStage } = pendingTransfer.value as any;
  const { reasonType, reason } = payload;
  
  // 组合最终原因
  let finalReason = reasonType || '';
  if (reason && reason.trim()) {
    if (finalReason && finalReason !== '其它') {
      finalReason += ` - ${reason.trim()}`;
    } else {
      finalReason = reason.trim();
    }
  }
  
  executeTransfer(record, status, statusText, finalReason, targetStage);
  pendingTransfer.value = null;
};

const transferringIds = ref<number[]>([]);

const getRowClassName = (record: any) => {
  return transferringIds.value.includes(record.id) ? 'transfer-row-leave' : '';
};

// 执行流转操作 - 调用真实API
const executeTransfer = async (record: any, status: string, statusText: string, reason?: string, targetStage?: string) => {
  loading.value = true;
  
  // 1. 标记正在流转，触发动画
  if (!transferringIds.value.includes(record.id)) {
      transferringIds.value.push(record.id);
  }

  try {
    // 2. 同时执行API请求和动画等待
    const apiPromise = influencerService.batchChangeStatus({
      ids: [Number(record.id)],
      targetStatus: status.toUpperCase(),
      targetStage: targetStage?.toUpperCase() as any,
      reason
    });
    
    const animationPromise = new Promise(resolve => setTimeout(resolve, 500));
    
    const [result] = await Promise.all([apiPromise, animationPromise]);
    
    if (result && result.failedItems && result.failedItems.length > 0) {
      const failMsg = result.failedItems.map((item: any) => item.errorMessage || item.message || '未知原因').join(', ');
      message.error(`部分操作失败: ${failMsg}`);
    } else {
      message.success(`已将 ${record.realName || record.name} 移入 ${statusText}`);
    }
    fetchData(); // 刷新列表
  } catch (e: any) {
    console.error('流转失败:', e);
    message.error(e.response?.data?.message || e.message || '流转请求失败');
  } finally {
    loading.value = false;
    // 清理动画标记 (使用 Number 确保匹配)
    transferringIds.value = transferringIds.value.filter(tid => Number(tid) !== Number(record.id));
  }
};

// 批量操作
const batchMove = async (targetStatus: string) => {
  if (!selectedRowKeys.value.length) return;
  
  const { targetStatus: apiStatus, targetStage: apiStage, label: statusText } = parseTransferTarget(targetStatus);
  
  // 如果需要输入原因
  if (statusRequireReason.includes(apiStatus.toLowerCase())) {
    const selectedItems = displayData.value
      .filter((item: any) => selectedRowKeys.value.includes(item.id))
      .map((item: any) => ({ id: String(item.id), name: item.realName || item.name || 'Unknown' }));
      
    pendingBatchTransfer.value = { status: apiStatus, statusText, targetStage: apiStage };
    batchReasonItems.value = selectedItems;
    batchReasonModalTitle.value = `批量流转至${statusText}`;
    batchReasonModalVisible.value = true;
    return;
  }
  
  // 否则直接执行
  loading.value = true;
  try {
    const result = await influencerService.batchChangeStatus({
      ids: selectedRowKeys.value.map(k => Number(k)),
      targetStatus: apiStatus.toUpperCase(),
      targetStage: apiStage?.toUpperCase() as any,
      reason: 'Batch move from Pool'
    });
    
    if (result && result.failedItems && result.failedItems.length > 0) {
      const failMsg = result.failedItems.map((item: any) => item.errorMessage || item.message || '未知原因').join(', ');
      message.error(`部分操作失败: ${failMsg}`);
    } else {
      message.success(`批量移入 ${statusText} 成功`);
    }
    
    selectedRowKeys.value = [];
    handleFilter();
  } catch (e: any) {
    console.error('Batch transfer failed:', e);
    message.error(e.response?.data?.message || e.message || '批量流转失败');
  } finally {
    loading.value = false;
  }
};

// 确认批量转移
const handleConfirmBatchTransfer = async (payload: { reasonType: string; reason: string }) => {
  if (!pendingBatchTransfer.value) return;
  
  const { status, statusText, targetStage } = pendingBatchTransfer.value as any;
  const { reasonType, reason } = payload;
  
  // 组合最终原因
  let finalReason = reasonType || '';
  if (reason && reason.trim()) {
    if (finalReason && finalReason !== '其它') {
      finalReason += ` - ${reason.trim()}`;
    } else {
      finalReason = reason.trim();
    }
  }
  
  loading.value = true;
  try {
    const result = await influencerService.batchChangeStatus({
      ids: selectedRowKeys.value.map(k => Number(k)),
      targetStatus: status.toUpperCase(),
      targetStage: targetStage?.toUpperCase() as any,
      reason: finalReason
    });
    
    if (result && result.failedItems && result.failedItems.length > 0) {
      const failMsg = result.failedItems.map((item: any) => item.errorMessage || item.message || '未知原因').join(', ');
      message.error(`部分流转失败: ${failMsg}`);
    } else {
      message.success(`批量移入 ${statusText} 成功`);
    }
    
    selectedRowKeys.value = [];
    handleFilter();
  } catch (e: any) {
    console.error('Batch transfer failed:', e);
    message.error(e.response?.data?.message || e.message || '批量流转失败');
  } finally {
    loading.value = false;
  }
  
  pendingBatchTransfer.value = null;
};

// SSE — 使用 composable
const { connect: connectSSE } = useInfluencerSSE(() => fetchData());

// 页面加载
onMounted(async () => {
    // 1. 立即加载列表数据（不等辅助数据）
    fetchData();

    // 2. 辅助数据从 Store 加载（带缓存）
    commonStore.loadAll().then(() => {
        // 用户名加载完成后更新显示映射
        if (displayData.value.length > 0 && Object.keys(userNameMap.value).length > 0) {
            displayData.value = displayData.value.map((item: any) => ({
                ...item,
                ownerName: item.ownerId ? userNameMap.value[item.ownerId] || item.ownerName : '-',
            }));
        }
    });

    // 3. SSE
    connectSSE();
});
</script>

<style lang="scss" scoped>
.influencer-pool-page {
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
    border: none;
    border-radius: 16px;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
    }
  }

  .filter-card {
    margin-bottom: 0 !important;
    flex-shrink: 0;

    :deep(.ant-card-body) {
      padding: 16px 20px !important;
    }

    :deep(.ant-form-item) {
      margin-bottom: 8px; /* 增加表单项垂直距离 */
      .ant-form-item-label {
        padding: 0 0 4px 4px !important; /* 调整标签内边距 */
        line-height: 1.2;
        > label {
          font-size: 12px;
          font-weight: 700; /* 加粗标签 */
          color: #475569;
          height: 18px;
          margin-bottom: 2px;
        }
      }
      .ant-form-item-control-input {
        min-height: 32px;
      }
    }
  }

  .filter-footer-actions {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px; /* 与栅格垂直间距 [20, 16] 保持一致 */
    padding: 0 4px;

    .expand-btn {
      font-size: 13px;
      color: #94a3b8;
      &:hover { color: #1890ff; }
    }
  }

  .table-card {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    min-height: 0;
    margin-bottom: 0 !important; /* Ensure table card uses full remaining height */

    :deep(.ant-card-head) {
      border-bottom: 1px solid #f1f5f9;
      padding: 0 16px;
      min-height: 40px;
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
    padding: 8px 0;
    background: #ffffff;
    border-bottom: 1px solid #f1f5f9;

    .status-switcher-wrapper {
      .premium-segmented {
        background: #eef1f5;
        padding: 3px;
        border-radius: 10px;
        border: none;
        display: flex;
        gap: 2px;
        box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.06);

        :deep(.ant-radio-button-wrapper) {
          border: none !important;
          background: transparent;
          border-radius: 7px;
          height: 28px;
          line-height: 28px;
          font-weight: 500;
          color: #64748b;
          font-size: 12px;
          padding: 0 12px;
          transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
          box-shadow: none !important;
          letter-spacing: 0.01em;

          &:before { display: none !important; }
          &:hover { color: #334155; }

          &.ant-radio-button-wrapper-checked {
            background: #ffffff !important;
            color: #1e293b !important;
            font-weight: 600;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1), 0 1px 2px rgba(0, 0, 0, 0.06) !important;
          }
        }
      }
    }

    .toolbar-btns {
      display: flex;
      align-items: center;

      :deep(.ant-btn), :deep(.premium-btn-small) {
        border-radius: 6px;
        height: 28px;
        font-weight: 500;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        gap: 4px;
        border: 1px solid #e2e8f0;
        color: #475569;
        box-shadow: none;
        transition: all 0.2s ease;
        background: #fff;
        padding: 0 12px;
        font-size: 12px;
        line-height: 1;

        &:not(:disabled):hover {
          color: #1890ff;
          border-color: #91caff;
          background: #f0f7ff;
        }

        &:disabled {
          background: #fafbfc;
          border-color: #f1f5f9;
          color: #cbd5e1;
        }

        .anticon {
          font-size: 12px;
        }
      }

      :deep(.primary-gradient) {
        background: linear-gradient(135deg, #ec4899 0%, #8b5cf6 100%) !important;
        border: none !important;
        color: #ffffff !important;
        box-shadow: 0 2px 8px rgba(236, 72, 153, 0.35) !important;
        font-weight: 600 !important;
        letter-spacing: 0.02em;

        .anticon { font-size: 13px; }
        
        &:not(:disabled):hover {
          background: linear-gradient(135deg, #f472b6 0%, #a78bfa 100%) !important;
          box-shadow: 0 4px 14px rgba(236, 72, 153, 0.45) !important;
        }
      }

      /* Vertical divider after batch dropdown */
      :deep(.ant-space-item:first-child)::after {
        content: '';
        display: inline-block;
        width: 1px;
        height: 16px;
        background: #e2e8f0;
        margin-left: 6px;
        vertical-align: middle;
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

    /* Prevent header from being squished in flex container */
    :deep(.ant-table-header) {
      flex-shrink: 0;
    }

    /* Fix for table content overflow and scrollbar styling */
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

    :deep(.ant-table-thead > tr > th) {
      background: #f8fafc !important;
      color: #64748b;
      font-weight: 800;
      font-size: 13px;
      text-transform: uppercase;
      letter-spacing: 1px;
      border-bottom: none !important;
      padding: 8px 16px !important;
      white-space: nowrap !important;
      height: auto !important;
      transition: all 0.3s ease;
      
      &.ant-table-selection-column {
        padding: 0 16px !important;
      }
    }

    :deep(.ant-table-thead > tr > th.ant-table-cell-fix-left),
    :deep(.ant-table-thead > tr > th.ant-table-cell-fix-right) {
      background: #f8fafc !important;
      z-index: 3;
    }

    /* Remove ugly vertical shadows from fixed columns */
    :deep(.ant-table-cell-fix-left-last::after),
    :deep(.ant-table-cell-fix-right-first::after) {
      box-shadow: none !important;
    }
    
    /* True floating strips for rows */
    :deep(.ant-table) {
      border-collapse: separate;
      border-spacing: 0 12px;
      background: transparent !important;
    }

    :deep(.ant-table-tbody > tr.ant-table-measure-row td) {
      padding: 0 !important;
      border: 0 !important;
      height: 0 !important;
    }

    :deep(.ant-table-tbody > tr:not(.ant-table-measure-row)) {
      background: #ffffff;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
      border-radius: 12px;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    }

    :deep(.ant-table-tbody > tr:not(.ant-table-measure-row) > td.ant-table-cell-fix-left),
    :deep(.ant-table-tbody > tr:not(.ant-table-measure-row) > td.ant-table-cell-fix-right) {
      background: inherit;
      z-index: 2;
    }

    /* Handle hover by background color change and individual td transform/shadow to prevent sticky clipping. */
    :deep(.ant-table-tbody > tr:not(.ant-table-measure-row):hover > td.ant-table-cell-fix-left),
    :deep(.ant-table-tbody > tr:not(.ant-table-measure-row):hover > td.ant-table-cell-fix-right),
    :deep(.ant-table-tbody > tr:not(.ant-table-measure-row):hover > td) {
      background: #eff6ff !important;
      transform: translateY(-2px);
      box-shadow: 0 6px 12px -4px rgba(0, 100, 255, 0.1);
      z-index: 3;
    }

    :deep(.ant-table-tbody > tr:not(.ant-table-measure-row) > td) {
      padding: 14px 16px !important;
      border-bottom: none !important;
      border-top: none !important;
      color: #1e293b;
      font-size: 14px;
      transition: all 0.3s ease;
      
      &:first-child {
        border-top-left-radius: 12px;
        border-bottom-left-radius: 12px;
      }
      &:last-child {
        border-top-right-radius: 12px;
        border-bottom-right-radius: 12px;
      }
    }
  }

  /* Premium Platform Tags */
  .premium-platform-tag {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    padding: 2px 10px;
    border-radius: 999px; /* Pill shape */
    font-weight: 700;
    font-size: 11px;
    letter-spacing: 0.5px;
    text-transform: uppercase;
    border: none;
    transition: all 0.2s ease;
    cursor: default;

    &.platform-TT {
      background: #f1f5f9;
      color: #0f172a;
    }
    &.platform-IG {
      background: #fdf2f8;
      color: #be185d;
    }
    &.platform-YT {
      background: #fef2f2;
      color: #b91c1c;
    }
    &.platform-FB {
      background: #eff6ff;
      color: #1d4ed8;
    }
    &.platform-X {
      background: #f1f5f9;
      color: #0f172a;
    }
  }

  .premium-source-tag {
    margin: 0;
    font-size: 11px;
    font-weight: 700;
    background: #f1f5f9;
    color: #475569;
    padding: 2px 10px;
    border-radius: 999px;
    letter-spacing: 0.5px;
  }

  .centered-tags {
    display: flex;
    justify-content: center;
    flex-wrap: wrap;
    gap: 4px;
  }

  /* User Info Cell */
  .user-info-cell {
    display: flex;
    align-items: center;
    gap: 14px;
    
    .avatar-wrapper {
      .premium-avatar {
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        border: none;
        color: #fff !important;
        font-weight: 800;
        font-size: 20px;
        transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
        
        &:hover {
          transform: scale(1.1) translateY(-2px);
          box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
        }
      }
    }
    
    .info-content {
      .user-name {
        font-weight: 800;
        color: #0f172a;
        font-size: 16px;
        margin-bottom: 4px;
      }
      .user-id {
        color: #64748b;
        font-size: 13px;
        color: #94a3b8;
        font-family: 'JetBrains Mono', monospace;
      }
    }
  }

  .pill-tags-wrapper {
    display: flex;
    flex-wrap: wrap;
    gap: 4px;
    
    .pill-tag {
      border-radius: 6px;
      font-size: 12px;
      padding: 1px 8px;
      background: #f1f5f9;
      color: #64748b;
      border: 1px solid #e2e8f0;
      transition: all 0.2s;
      
      &:hover {
        background: #e2e8f0;
        color: #475569;
      }
    }
  }


  .action-btns-wrapper {
    display: flex;
    align-items: center;
    gap: 8px;
    
    .detail-btn {
      border-radius: 6px;
      font-weight: 600;
      height: 28px;
      padding: 0 12px;
      font-size: 12px;
      background: linear-gradient(135deg, #60a5fa 0%, #2563eb 100%);
      border: none;
      box-shadow: 0 2px 4px rgba(37, 99, 235, 0.2);
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      color: #fff;
      
      &:hover {
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(37, 99, 235, 0.4);
        background: linear-gradient(135deg, #93c5fd 0%, #3b82f6 100%);
      }

      &:active {
        transform: translateY(0);
      }
    }
    
    .transfer-btn {
      border-radius: 6px;
      color: #475569;
      height: 28px;
      padding: 0 10px;
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 12px;
      font-weight: 500;
      border: 1px solid #e2e8f0;
      background: #f8fafc;
      transition: all 0.2s;
      
      &:hover {
        color: #3b82f6;
        border-color: #3b82f6;
        background: #fff;
        box-shadow: 0 2px 6px rgba(59, 130, 246, 0.1);
      }
    }

    .action-btn-danger {
      border-radius: 6px;
      font-weight: 600;
      height: 28px;
      padding: 0 12px;
      font-size: 12px;
      background: linear-gradient(135deg, #f87171 0%, #dc2626 100%);
      border: none !important;
      box-shadow: 0 2px 4px rgba(220, 38, 38, 0.2);
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      color: #fff !important;
      line-height: 28px;
      display: flex;
      align-items: center;
      justify-content: center;
      
      &:hover {
        transform: translateY(-1px);
        box-shadow: 0 4px 12px rgba(220, 38, 38, 0.4);
        background: linear-gradient(135deg, #fb923c 0%, #ef4444 100%);
      }

      &:active {
        transform: translateY(0);
      }
    }
  }  
      .micro-action-btn {
        height: 24px;
        padding: 0 10px;
        font-size: 12px;
        border-radius: 6px;
        
        &.go-btn {
          background: #eff6ff;
          color: #1890ff;
          border-color: #bfdbfe;
          &:hover { background: #1890ff; color: #fff; }
        }
        &.copy-btn {
          color: #64748b;
          &:hover { border-color: #1890ff; color: #1890ff; }
        }
      }

  .pagination-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 6px 16px;
    background: #ffffff;
    border-top: 1px solid rgba(0, 0, 0, 0.04);
    z-index: 10;
    position: sticky;
    bottom: 0;
    
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

  /* Premium Time Filter Group */
  .premium-time-filter-group {
    display: flex;
    align-items: center;
    background: #ffffff;
    border: 1px solid #d1d5db;
    border-radius: 8px;
    padding: 2px 4px;
    height: 32px;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);

    &:hover {
      border-color: #94a3b8;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.08);
    }

    &:focus-within {
      border-color: #1890ff;
      box-shadow: 0 0 0 3px rgba(24, 144, 255, 0.12);
      background: #fff;
    }

    .time-type-radio-group {
      flex-shrink: 0;
      display: flex;
      padding: 0 2px;
      
      :deep(.ant-radio-button-wrapper) {
        height: 24px;
        line-height: 22px;
        padding: 0 10px;
        font-size: 12px;
        background: transparent;
        border: none;
        color: #64748b;
        border-radius: 6px;
        margin-right: 2px;
        
        &:before { display: none; }
        
        &.ant-radio-button-wrapper-checked {
          background: #fff;
          color: #1890ff;
          box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
          font-weight: 600;
        }
      }
    }

    .premium-datepicker-integrated {
      flex: 1;
      border: none !important;
      background: transparent !important;
      box-shadow: none !important;
      height: 26px !important;
      padding: 0 8px !important;
      
      :deep(.ant-picker-input > input) {
        font-size: 13px !important;
      }
    }
  }

  /* Selection column visibility improve */
  :deep(.ant-table-selection-column) {
    .ant-checkbox-inner {
      border-radius: 4px;
      border-color: #cbd5e1;
    }
    .ant-checkbox-checked .ant-checkbox-inner {
      background-color: #1890ff;
      border-color: #1890ff;
    }
  }

  /* Contact Cell Premium Styling */
  .contact-card-cell {
    display: flex;
    flex-direction: column;
    gap: 4px;
    padding: 4px 0;

    .contact-line {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 2px 6px;
      border-radius: 6px;
      transition: all 0.2s;
      cursor: default;

      &:hover {
        background: #f1f5f9;
        .contact-copy-icon { opacity: 1; }
      }
    }

    .contact-icon-box {
      width: 22px;
      height: 22px;
      border-radius: 4px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 12px;
      flex-shrink: 0;

      &.mail { background: #eff6ff; color: #3b82f6; }
      &.phone { background: #f0fdf4; color: #16a34a; }
    }

    .contact-text {
      color: #334155;
      font-size: 13px;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      max-width: 140px;
      text-align: left;
    }

    .contact-copy-icon {
      font-size: 12px;
      color: #94a3b8;
      cursor: pointer;
      opacity: 0;
      transition: all 0.2s;
      
      &:hover { color: #3b82f6; }
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
  }

/* Icons & Buttons Color Consistency */
.transfer-btn {
  color: #64748b;
  &:hover { color: #1890ff; border-color: #1890ff; }
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

/* Premium Modal Styles */
:deep(.influencer-create-modal) {
  .ant-modal-content {
    padding: 0 !important;
    overflow: hidden;
    border-radius: 16px;
    box-shadow: 0 20px 50px rgba(0, 0, 0, 0.15);
  }
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
  padding: 24px;
  background: #ffffff;
  
  .premium-textarea {
    border-radius: 8px;
    border-color: #e2e8f0;
    font-size: 14px;
    
    &:focus {
      border-color: #1890ff;
      box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
    }
  }
}

.ic-modal-footer {
  padding: 16px 24px;
  background: #f8fafc;
  border-top: 1px solid #f1f5f9;
  text-align: right;
  
  .premium-btn {
    height: 36px;
    padding: 0 24px;
    border-radius: 8px;
    font-weight: 600;
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
/* 空白筛选开关按钮 */
.blank-toggle-btn {
  display: inline-block;
  padding: 1px 6px;
  font-size: 11px;
  line-height: 18px;
  border-radius: 4px;
  cursor: pointer;
  color: #94a3b8;
  background: #f1f5f9;
  border: 1px solid #e2e8f0;
  margin-right: 4px;
  transition: all 0.2s;
  user-select: none;
  font-weight: 600;
  &:hover {
    color: #3b82f6;
    border-color: #93c5fd;
  }
  &.active {
    color: #fff;
    background: linear-gradient(135deg, #3b82f6, #2563eb);
    border-color: #2563eb;
    box-shadow: 0 1px 4px rgba(37, 99, 235, 0.3);
  }
}

.social-media-cell {
  display: flex;
  flex-direction: column;
  gap: 8px;
    .social-item {
      display: flex;
      flex-direction: column;
      gap: 2px;
      padding: 4px 0;
      border: none;
      background: transparent;
    }

    .sm-platform-tag {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      padding: 0 6px;
      height: 20px;
      border-radius: 4px;
      font-size: 10px;
      font-weight: 800;
      letter-spacing: 0.5px;
      text-transform: uppercase;
      
      &.platform-TT { background: #f1f5f9; color: #0f172a; }
      &.platform-IG { background: #fdf2f8; color: #be185d; }
      &.platform-YT { background: #fef2f2; color: #b91c1c; }
      &.platform-FB { background: #eff6ff; color: #1d4ed8; }
      &.platform-X { background: #f1f5f9; color: #0f172a; }
    }

  .sm-detail-inline {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 13px;
    line-height: 1.4;
    width: 100%;
    min-width: 0;

    .sm-platform-tag {
      font-weight: 700;
      font-size: 11px;
      padding: 2px 6px;
      border-radius: 4px;
      flex-shrink: 0;
      
      &.platform-IG { background: #fee2e2; color: #e11d48; }
      &.platform-TT { background: #f1f5f9; color: #0f172a; }
      &.platform-YT { background: #fee2e2; color: #dc2626; }
      &.platform-FB { background: #e0e7ff; color: #4338ca; }
      &.platform-X { background: #e2e8f0; color: #0f172a; }
    }

    .sm-handle {
      color: #1e293b;
      font-weight: 600;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      flex: 1;
      min-width: 0;
    }

    .copy-btn-icon {
      color: #94a3b8;
      cursor: pointer;
      flex-shrink: 0;
      transition: color 0.2s;
      &:hover { color: #3b82f6; }
    }

    .sm-link-icon {
      color: #94a3b8;
      display: flex;
      align-items: center;
      justify-content: center;
      width: 24px;
      height: 24px;
      border-radius: 4px;
      transition: all 0.2s;
      flex-shrink: 0;

      &:hover {
        background: #e2e8f0;
        color: #3b82f6;
      }
    }
  }
}

.premium-btn-small {
  height: 32px !important;
  padding: 0 16px !important;
  font-size: 13px !important;
  border-radius: 6px !important;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-weight: 500;
  gap: 4px; /* Icon and text gap */
}
</style>
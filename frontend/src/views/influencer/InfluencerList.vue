<template>
  <div class="influencer-list-page">
    <!-- 筛选区域 -->
    <a-card :bordered="false" class="filter-card glass-card" :body-style="{ padding: '16px 20px' }">
      <a-form :model="filterForm" layout="vertical" size="middle">
        <a-row :gutter="[20, 16]" :class="{ 'collapsed-filter-row': !filterExpanded }">
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="红人名称">
              <a-input 
                v-model:value.trim="filterForm.name" 
                placeholder="搜索真实姓名..." 
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
            <a-form-item label="红人邮箱">
              <a-input 
                v-model:value.trim="filterForm.email" 
                placeholder="输入邮件地址" 
                allow-clear 
                class="premium-input" 
                :disabled="filterForm.blankFields?.includes('email')"
                @dblclick="openBatchSearchEmail"
              >
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
          <a-col v-if="activeKey === 'all'" :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="红人状态">
              <a-select
                v-model:value="filterForm.listStatus"
                placeholder="全部状态"
                allow-clear
                class="premium-select"
              >
                <a-select-option
                  v-for="opt in LIST_STATUS_FILTER_OPTIONS"
                  :key="opt.value"
                  :value="opt.value"
                >{{ opt.label }}</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="负责人">
                <a-select v-model:value="filterForm.owner" placeholder="选择负责人" mode="multiple" :max-tag-count="1" show-search :filter-option="filterOption" allow-clear class="premium-select" @change="(val: any) => onSelectBlankChange(val, 'owner')">
                  <a-select-option value="__BLANK__" style="color: #f59e0b; font-weight: 600;">（空白）</a-select-option>
                  <a-select-option v-for="u in ownerUsers" :key="u.id" :value="u.id">{{ u.name }}</a-select-option>
                </a-select>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="4">
            <a-form-item label="联络人">
                <a-select v-model:value="filterForm.liaisonTagIds" placeholder="选择联络人" mode="multiple" :max-tag-count="1" show-search :filter-option="filterOption" allow-clear class="premium-select" @change="(val: any) => onSelectBlankChange(val, 'contactPerson')">
                  <a-select-option value="__BLANK__" style="color: #f59e0b; font-weight: 600;">（空白）</a-select-option>
                  <a-select-option v-for="t in liaisonTagOptions" :key="t.id" :value="t.id">{{ t.name }}</a-select-option>
                </a-select>
            </a-form-item>
          </a-col>
          <template v-if="filterExpanded">
            <a-col :xs="24" :sm="12" :md="8" :lg="4">
              <a-form-item label="社媒平台">
                <a-select v-model:value="filterForm.socialPlatform" placeholder="选择社媒平台" mode="multiple" :max-tag-count="1" show-search :filter-option="filterOption" allow-clear class="premium-select" @change="(val: any) => onSelectBlankChange(val, 'socialPlatform')">
                  <a-select-option value="__BLANK__" style="color: #f59e0b; font-weight: 600;">（空白）</a-select-option>
                  <a-select-option v-for="p in platforms" :key="p" :value="p">{{ p }}</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
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
                <a-select 
                  v-model:value="filterForm.country" 
                  placeholder="选择国家" 
                  mode="multiple" 
                  :max-tag-count="1" 
                  show-search 
                  :filter-option="countryFilterOption" 
                  :filter-sort="countryFilterSort"
                  @search="onCountrySearch"
                  option-label-prop="label"
                  allow-clear 
                  class="premium-select"
                  @change="(val: any) => onSelectBlankChange(val, 'country')"
                >
                  <a-select-option value="__BLANK__" label="（空白）" style="color: #f59e0b; font-weight: 600;">（空白）</a-select-option>
                  <a-select-option v-for="c in countries" :key="c.code" :value="c.code" :label="c.code">
                    <div style="display: flex; justify-content: space-between;">
                      <span>{{ c.code }}</span>
                      <span style="color: #94a3b8; font-size: 13px;">{{ c.nameCn }}</span>
                    </div>
                  </a-select-option>
                </a-select>
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
                </a-input>
              </a-form-item>
            </a-col>
            <a-col :xs="24" :sm="12" :md="8" :lg="4">
              <a-form-item label="红人类型">
                <a-select v-model:value="filterForm.influencerType" placeholder="选择类型" mode="multiple" :max-tag-count="1" show-search :filter-option="filterOption" allow-clear class="premium-select" @change="(val: any) => onSelectBlankChange(val, 'influencerType')">
                  <a-select-option value="__BLANK__" style="color: #f59e0b; font-weight: 600;">（空白）</a-select-option>
                  <a-select-option v-for="t in dynamicInfluencerTypes" :key="t" :value="t">{{ t }}</a-select-option>
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
                <a-select v-model:value="filterForm.race" placeholder="选择人种" mode="multiple" :max-tag-count="1" allow-clear class="premium-select" @change="(val: any) => onSelectBlankChange(val, 'race')">
                  <a-select-option value="__BLANK__" style="color: #f59e0b; font-weight: 600;">（空白）</a-select-option>
                  <a-select-option v-for="r in races" :key="r" :value="r">{{ r }}</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :xs="24" :sm="12" :md="8" :lg="4">
              <a-form-item label="粉丝区间">
                <a-input-group compact class="fans-range-group">
                  <a-input-number 
                    v-model:value="filterForm.fansMin" 
                    placeholder="最小" 
                    :min="0"
                    style="width: 45%"
                    class="premium-input-number"
                  />
                  <a-input 
                    placeholder="~" 
                    disabled 
                    style="width: 10%; text-align: center; border-left: 0; border-right: 0; pointer-events: none; background: #fafafa;"
                  />
                  <a-input-number 
                    v-model:value="filterForm.fansMax" 
                    placeholder="最大" 
                    :min="0"
                    style="width: 45%"
                    class="premium-input-number"
                  />
                </a-input-group>
              </a-form-item>
            </a-col>
            <a-col :xs="24" :sm="12" :md="8" :lg="4">
              <a-form-item label="合作次数">
                <a-input-group compact class="fans-range-group">
                  <a-input-number 
                    v-model:value="filterForm.minSampleCount" 
                    placeholder="最小" 
                    :min="0"
                    style="width: 45%"
                    class="premium-input-number"
                  />
                  <a-input 
                    placeholder="~" 
                    disabled 
                    style="width: 10%; text-align: center; border-left: 0; border-right: 0; pointer-events: none; background: #fafafa;"
                  />
                  <a-input-number 
                    v-model:value="filterForm.maxSampleCount" 
                    placeholder="最大" 
                    :min="0"
                    style="width: 45%"
                    class="premium-input-number"
                  />
                </a-input-group>
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
            <a-col :xs="24" :sm="12" :md="8" :lg="4">
              <a-form-item label="是否有输出内容">
                <a-select v-model:value="filterForm.hasOutputContent" placeholder="全部" allow-clear class="premium-select">
                  <a-select-option value="true">是</a-select-option>
                  <a-select-option value="false">否</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </template>
        </a-row>

        <!-- 筛选按钮区域：同步对齐与间距 -->
        <div class="filter-footer-actions">
          <a-space size="middle">
            <a-button type="primary" @click="handleFilter" class="premium-btn primary-gradient" :loading="loading">
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
              <a-radio-button value="all">全部 ({{ statusCounts.ALL || 0 }})</a-radio-button>
              <a-radio-button value="pending">待联系 ({{ statusCounts.PENDING || 0 }})</a-radio-button>
              <a-radio-button value="contacted">已联系 ({{ statusCounts.CONTACTED || 0 }})</a-radio-button>
              <a-radio-button value="communicating">沟通中 ({{ statusCounts.COMMUNICATING || 0 }})</a-radio-button>
              <a-radio-button value="cooperating">合作中 ({{ statusCounts.COOPERATING || 0 }})</a-radio-button>
              <a-radio-button value="dormant">休眠中 ({{ statusCounts.DORMANT || 0 }})</a-radio-button>
              <a-radio-button value="paused">暂不合作 ({{ statusCounts.PAUSED || 0 }})</a-radio-button>
              <a-radio-button value="blacklist">黑名单 ({{ statusCounts.BLACKLIST || 0 }})</a-radio-button>
              <a-radio-button value="terminated">不再合作 ({{ statusCounts.TERMINATED || 0 }})</a-radio-button>
            </a-radio-group>
          </div>
          
          <a-space size="small" class="toolbar-btns">
            <a-dropdown :disabled="!selectedRowKeys.length" v-permission="'influencer.list.batch_transfer'">
              <a-button class="transfer-btn" :disabled="!selectedRowKeys.length">
                批量操作 <DownOutlined style="font-size: 10px" />
              </a-button>
              <template #overlay>
                <a-menu class="premium-menu">
                  <a-sub-menu key="transfer" title="批量流转">
                    <a-menu-item v-for="opt in getTransferOptions(activeKey)" :key="opt.key" @click="batchMove(opt.key)">
                      移入{{ opt.label }}
                    </a-menu-item>
                  </a-sub-menu>
                  <a-menu-divider />
                  <a-menu-item key="assignOwner" @click="openBatchAssignOwner">
                    <UserOutlined style="margin-right: 6px" />分配负责人
                  </a-menu-item>
                  <a-menu-item key="assignLiaison" @click="openBatchAssignLiaison">
                    <TeamOutlined style="margin-right: 6px" />分配联络员
                  </a-menu-item>
                  <a-menu-item key="assignTags" @click="openBatchAssignTags">
                    <TagsOutlined style="margin-right: 6px" />批量打标签
                  </a-menu-item>
                  <a-menu-divider />
                  <a-menu-item key="batchMail" @click="goBatchMail">
                    <MailOutlined style="margin-right: 6px" />批量发邮件
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
            <a-button @click="openExportModal" class="premium-btn-small" v-permission="'influencer.list.export'">
              <template #icon><ExportOutlined /></template>导出
            </a-button>
            <a-button @click="openImportModal" class="premium-btn-small" v-permission="'influencer.list.import'">
              <template #icon><ImportOutlined /></template>导入
            </a-button>
            <a-button type="primary" @click="openCreateInfluencer" class="premium-btn-small primary-gradient" v-permission="'influencer.list.create'">
              <template #icon><PlusOutlined /></template>新增红人
            </a-button>
          </a-space>
        </div>
      </template>

      <a-alert
        v-if="aiTopListBanner"
        type="info"
        show-icon
        banner
        class="ai-top-list-banner"
      >
        <template #message>
          <span>
            已找出
            <template v-if="aiTopListBanner.sortLabel">{{ aiTopListBanner.sortLabel }}的 </template>
            <b>{{ aiTopListBanner.shown }}</b> 位红人
          </span>
          <span class="ai-top-list-email">
            · 有主邮箱 <b>{{ aiTopListBanner.withEmail }}</b> 人
            · 无邮箱 <b>{{ aiTopListBanner.withoutEmail }}</b> 人
          </span>
        </template>
      </a-alert>

        <a-table
          :columns="columns"
          :data-source="displayData"
          :row-key="(record: any) => record.key ?? record.id"
          :pagination="false"
          :loading="{ spinning: loading, indicator: LoadingIcon }"
          :row-selection="{ selectedRowKeys: selectedRowKeys, onChange: onSelectChange }"
          :scroll="{ x: 'max-content', y: filterExpanded ? 'calc(100vh - 460px)' : 'calc(100vh - 350px)' }"
          :sticky="{ offsetScroll: 0 }"
          size="middle"
          class="premium-table"
          :row-class-name="getRowClassName"
        >
          <template #emptyText>
            <div class="table-empty-state">
              <a-empty :image="Empty.PRESENTED_IMAGE_SIMPLE" description="暂无内容" />
            </div>
          </template>
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'userInfo'">
            <div class="user-info-cell">
              <div class="avatar-wrapper">
                <a-avatar :src="record.avatar" :size="44" class="premium-avatar" :style="{ background: record.avatar ? 'transparent' : getAvatarColor(record.realName || record.nickName) + ' !important' }">{{ (record.realName || record.nickName || '?').charAt(0).toUpperCase() }}</a-avatar>
              </div>
              <div class="info-content">
                <div class="user-name">
                  {{ record.realName || record.nickName }}
                  <CopyOutlined class="copy-btn-icon" title="复制名称" @click.stop="handleCopy(record.realName || record.nickName)" />
                </div>
                <div class="user-id">#{{ record.id }}</div>
              </div>
            </div>
          </template>

          <template v-else-if="column.key === 'contact'">
            <div class="contact-card-cell">
              <div v-if="record.email" class="contact-line">
                <div class="contact-icon-box mail"><MailOutlined /></div>
                <span class="contact-text" :title="record.email">{{ record.email }}</span>
                <CopyOutlined class="contact-copy-icon" @click.stop="copyText(record.email)" />
              </div>
              <div v-if="record.phone" class="contact-line">
                <div class="contact-icon-box phone"><PhoneOutlined /></div>
                <span class="contact-text">{{ record.phone }}</span>
                <CopyOutlined class="contact-copy-icon" @click.stop="copyText(record.phone)" />
              </div>
              <div v-if="!record.email && !record.phone" style="color: #cbd5e1;">-</div>
            </div>
          </template>

          <template v-else-if="column.key === 'contactPerson'">
             <span style="color: #475569; font-size: 13px;">{{ getLiaisonName(record) }}</span>
          </template>

          <template v-else-if="column.key === 'country'">
            <div class="country-cell">
               <span v-if="record.country">{{ record.country }}</span>
               <span v-else style="color: #cbd5e1;">-</span>
            </div>
          </template>

          <template v-else-if="column.key === 'race'">
            <div class="race-cell">
               <span v-if="record.race">{{ record.race }}</span>
               <span v-else style="color: #cbd5e1;">-</span>
            </div>
          </template>

          <template v-else-if="column.key === 'owner'">
             <span style="color: #475569; font-size: 13px;">{{ record.ownerName || (record.ownerId ? `ID:${record.ownerId}` : '-') }}</span>
          </template>

          <template v-else-if="column.key === 'source'">
             <div v-if="record.source">
                <a-tag :bordered="false" class="premium-source-tag">{{ record.source }}</a-tag>
             </div>
             <span v-else>-</span>
          </template>

          <template v-else-if="column.key === 'updatedAt'">
             <div class="date-cell" style="color: #64748b; font-size: 13px;">{{ record.updatedAt ? dayjs(record.updatedAt).format('YYYY-MM-DD') : '-' }}</div>
          </template>
          
          <template v-else-if="column.key === 'platform'">
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
          
          <template v-else-if="column.key === 'tags'">
            <div class="pill-tags-wrapper">
              <template v-if="getFilteredTags(record)?.length">
                <a-tag 
                  v-for="tag in getFilteredTags(record)" 
                  :key="typeof tag === 'object' ? tag.id : tag"
                  class="pill-tag"
                  :style="typeof tag === 'object' ? {
                    backgroundColor: tag.backgroundColor || '#e6f7ff',
                    borderColor: tag.borderColor || '#91d5ff',
                    color: tag.textColor || '#1890ff'
                  } : {}"
                >
                  {{ typeof tag === 'object' ? tag.name : (tag || '-') }}
                </a-tag>
              </template>
              <span v-else>-</span>
            </div>
          </template>

          <template v-else-if="column.key === 'socialMedia'">
            <div class="social-media-cell">
              <template v-if="record.socialMedias?.length || record.socialMediaList?.length">
                <div v-for="(sm, idx) in (record.socialMedias || record.socialMediaList)" :key="idx" class="social-item">
                  <div class="sm-detail-inline">
                    <span class="sm-platform-tag" :class="`platform-${getPlatformAbbr(sm.platform)}`">{{ getPlatformAbbr(sm.platform) }}</span>
                    <span class="sm-handle">{{ extractHandle(sm.handle || sm.url || '') }}</span>
                    <CopyOutlined class="copy-btn-icon" title="复制账号" @click.stop="handleCopy(extractHandle(sm.handle || sm.url || ''))" />
                    <a v-if="sm.url || sm.handle" :href="formatUrlForHref(sm.url || sm.handle)" target="_blank" class="sm-link-icon" :title="sm.url || sm.handle" @click.stop>
                      <LinkOutlined />
                    </a>
                  </div>
                </div>
              </template>
              <template v-else-if="record.defaultHandle || record.profileLink">
                <div class="social-item">
                  <div class="sm-detail-inline">
                    <span class="sm-platform-tag" :class="`platform-${getPlatformAbbr(record.platformName || 'TikTok')}`">{{ getPlatformAbbr(record.platformName || 'TikTok') }}</span>
                    <span class="sm-handle">{{ extractHandle(record.defaultHandle || record.profileLink || '') }}</span>
                    <CopyOutlined class="copy-btn-icon" title="复制账号" @click.stop="handleCopy(extractHandle(record.defaultHandle || record.profileLink || ''))" />
                    <a v-if="record.profileLink || record.defaultHandle" :href="formatUrlForHref(record.profileLink || record.defaultHandle)" target="_blank" class="sm-link-icon" :title="record.profileLink || record.defaultHandle" @click.stop>
                      <LinkOutlined />
                    </a>
                  </div>
                </div>
              </template>
              <div v-else style="color: #cbd5e1;">-</div>
            </div>
          </template>

          <template v-else-if="column.key === 'isPaid'">
              <span :class="['status-icon-badge', record.isPaid ? 'yes' : 'no']">
                {{ record.isPaid ? '是' : '否' }}
              </span>
          </template>
          
          <template v-else-if="column.key === 'totalFans'">
             <span>{{ record.totalFans?.toLocaleString() || 0 }}</span>
          </template>

          <template v-else-if="column.key === 'sampleCount'">
             <span>{{ record.sampleCount || 0 }}</span>
          </template>

          <template v-else-if="column.key === 'hasContent'">
            <a-tag :color="record.hasContent ? 'success' : 'default'" class="status-tag">
              {{ record.hasContent ? '有' : '无' }}
            </a-tag>
          </template>

          <template v-else-if="column.key === 'action'">
            <div class="action-btns-grid">
              <a-button type="primary" size="small" @click="handleEdit(record)" class="grid-btn detail-btn">详情</a-button>

              <a-dropdown placement="bottomRight" trigger="click">
                <a-button class="grid-btn more-btn" size="small">
                  更多 <DownOutlined style="font-size: 10px;"/>
                </a-button>
                <template #overlay>
                  <a-menu class="premium-menu">
                    <!-- 发送邮件 -->
                    <a-menu-item key="mail" :disabled="!record.email && !record.backupEmail" @click="openInviteMail(record)">
                      <MailOutlined /> 发送邮件
                    </a-menu-item>
                    
                    <!-- 状态流转 -->
                    <a-sub-menu key="transfer" v-if="getTransferOptions(activeKey).length > 0">
                      <template #title>
                        <span><SwapOutlined /> 状态流转</span>
                      </template>
                      <a-menu-item v-for="opt in getTransferOptions(activeKey)" :key="opt.key" @click="(e: any) => handleTransfer(e, record)">
                        {{ opt.label }}
                      </a-menu-item>
                    </a-sub-menu>

                    <a-menu-divider />
                    
                    <!-- 删除 -->
                    <a-menu-item key="delete" @click="confirmDelete(record)" style="color: #ef4444;">
                      <DeleteOutlined /> 删除红人
                    </a-menu-item>
                  </a-menu>
                </template>
              </a-dropdown>
            </div>
          </template>
        </template>
        </a-table>
      
      <!-- 自定义底部页脚 -->
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
            :page-size-options="pagination.pageSizeOptions"
            @change="handleTableChange"
          />
        </div>
      </div>
    </a-card>

    <!-- 红人详情弹窗 -->
    <InfluencerDetailModal
      v-model:open="detailModalVisible"
      :initial-tab="detailModalInitialTab"
      :influencer-data="currentInfluencer"
      @refresh="fetchData"
    />
    
    <!-- 新建红人弹窗 -->
    <InfluencerCreateModal
      v-model:open="createModalVisible"
      initial-stage="ONBOARDED"
      initial-status="PENDING"
      @created="handleCreatedInfluencer"
    />
    
    <!-- 导入红人弹窗 -->
    <InfluencerImportModal
      v-model:open="importModalVisible"
      @success="handleImportSuccess"
    />
    
    <!-- 关联内容详情弹窗 -->
    <ContentDetailModal
      v-model:open="contentDetailVisible"
      :influencer-id="String(currentInfluencerId)"
      :influencer-name="currentInfluencerName"
    />
    
    <!-- 关联样品详情弹窗 -->
    <SampleDetailModal
      v-model:open="sampleDetailVisible"
      :influencer-id="String(currentInfluencerId)"
      :influencer-name="currentInfluencerName"
    />
    
    <!-- 转化订单详情弹窗 -->
    <ConversionDetailModal
      v-model:open="conversionDetailVisible"
      :influencer-id="String(currentInfluencerId)"
      :influencer-name="currentInfluencerName"
    />

    <!-- 内嵌浏览器弹窗 -->
    <BrowserPreviewModal
      v-model:open="browserModalVisible"
      :url="previewUrl"
    />
    
    <!-- 单个原因输入弹窗 -->
    <InfluencerTransferModal
      v-model:open="reasonModalVisible"
      :title="reasonModalTitle"
      @confirm="handleConfirmTransfer"
    />
    
    <!-- 导出字段选择弹窗 -->
    <InfluencerExportModal
      v-model:open="exportModalVisible"
      :selected-count="selectedRowKeys.length"
      :export-fields="exportFields"
      :page-type="PAGE_TYPE"
      :current-user-id="String(currentUserId)"
      :all-users="allUsersForExport"
      :initial-scope="exportInitialScope"
      :initial-template-name="exportInitialTemplateName"
      @export="handleExportFromModal"
    />
    
    <!-- 批量操作原因输入弹窗 -->
    <InfluencerBatchTransferModal
      v-model:open="batchReasonModalVisible"
      :title="batchReasonModalTitle"
      :items="batchReasonItems"
      @confirm="handleConfirmBatchTransfer"
    />

    <!-- 批量分配负责人弹窗 -->
    <a-modal
      v-model:open="batchAssignOwnerVisible"
      title="批量分配负责人"
      class="premium-modal"
      :ok-button-props="{ class: 'primary-gradient' }"
      centered
      @ok="handleBatchAssignOwner"
    >
      <div style="padding: 16px 0">
        <p style="margin-bottom: 12px; color: #64748b;">已选中 <b>{{ selectedRowKeys.length }}</b> 个红人，将统一分配负责人：</p>
        <a-select
          v-model:value="batchAssignOwnerValue"
          placeholder="请选择负责人"
          show-search
          :filter-option="filterOption"
          allow-clear
          style="width: 100%"
          class="premium-select"
          size="large"
        >
          <a-select-option v-for="u in ownerUsers" :key="u.id" :value="u.id">{{ u.name }}</a-select-option>
        </a-select>
      </div>
    </a-modal>

    <!-- 批量分配联络员弹窗 -->
    <a-modal
      v-model:open="batchAssignLiaisonVisible"
      title="批量分配联络员"
      class="premium-modal"
      :ok-button-props="{ class: 'primary-gradient' }"
      centered
      @ok="handleBatchAssignLiaison"
    >
      <div style="padding: 16px 0">
        <p style="margin-bottom: 12px; color: #64748b;">已选中 <b>{{ selectedRowKeys.length }}</b> 个红人，将分配联络员标签：</p>
        <a-select
          v-model:value="batchAssignLiaisonValue"
          placeholder="请选择联络员"
          show-search
          :filter-option="filterOption"
          allow-clear
          style="width: 100%"
          class="premium-select"
          size="large"
        >
          <a-select-option v-for="t in liaisonTagOptions" :key="t.id" :value="t.id">{{ t.name }}</a-select-option>
        </a-select>
      </div>
    </a-modal>

    <!-- 批量打标签弹窗 -->
    <a-modal
      v-model:open="batchAssignTagsVisible"
      title="批量打标签"
      class="premium-modal"
      :ok-button-props="{ class: 'primary-gradient' }"
      centered
      @ok="handleBatchAssignTags"
    >
      <div style="padding: 16px 0">
        <p style="margin-bottom: 12px; color: #64748b;">已选中 <b>{{ selectedRowKeys.length }}</b> 个红人，将追加以下标签：</p>
        <a-select
          v-model:value="batchAssignTagsValue"
          placeholder="请选择标签（可多选）"
          mode="multiple"
          show-search
          :filter-option="filterOption"
          allow-clear
          style="width: 100%"
          class="premium-select"
          size="large"
        >
          <a-select-option v-for="t in availableTags" :key="t.id" :value="t.id">{{ t.name }}</a-select-option>
        </a-select>
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
  </div>
</template>

<script setup lang="ts">
defineOptions({
  name: 'InfluencerList'
});
import { ref, reactive, onMounted, onUnmounted, watch, h, computed } from 'vue';
import { storeToRefs } from 'pinia';
import { 
  PlusOutlined, DownOutlined, UpOutlined, ExportOutlined, ImportOutlined,
  LoadingOutlined, SearchOutlined, CloseOutlined, 
  UserOutlined, MailOutlined, LinkOutlined, PhoneOutlined,
  GlobalOutlined, EnvironmentOutlined, WalletOutlined, ShareAltOutlined,
  TeamOutlined, TagsOutlined, UnorderedListOutlined, DatabaseOutlined, CopyOutlined, DeleteOutlined, MoreOutlined, SwapOutlined,
  InstagramOutlined, YoutubeOutlined, FacebookOutlined, TwitterOutlined,
  SyncOutlined, CheckCircleOutlined, CheckCircleFilled
} from '@ant-design/icons-vue';
import { message, Empty, Modal } from 'ant-design-vue';
import type { MenuInfo } from 'ant-design-vue/es/menu/src/interface';
import { useRouter, useRoute } from 'vue-router';
import dayjs from 'dayjs';

import { influencerService } from '@/services/influencerService';
import type { Influencer, InfluencerFilterDto, Address, PaymentRecord } from '@/types/influencer';
import { createInfluencerInitialFilter, formatFilterForApi } from '@/composables/useInfluencerFilter';
import { useUserStore } from '@/stores/user';
import { useCommonDataStore } from '@/stores/commonData';
import { useInfluencerSSE } from '@/composables/useInfluencerSSE';
import { getPlatformIcon, getPlatformAbbr, formatUrlForHref, extractHandle } from '@/utils/platform';
import { STATUS_LABEL, REASON_REQUIRED_STATUSES, ALL_TRANSFER_OPTIONS } from '@/types/enums';

import InfluencerDetailModal from '@/components/influencer/InfluencerDetailModal.vue';
import InfluencerCreateModal from '@/components/influencer/InfluencerCreateModal.vue';
import InfluencerImportModal from '@/components/influencer/InfluencerImportModal.vue';
import InfluencerExportModal from '@/components/influencer/InfluencerExportModal.vue';
import InfluencerTransferModal from '@/components/influencer/InfluencerTransferModal.vue';
import InfluencerBatchTransferModal from '@/components/influencer/InfluencerBatchTransferModal.vue';

import { createExportTask } from '@/utils/exportTaskHelper';
import BrowserPreviewModal from '@/components/common/BrowserPreviewModal.vue'; 
import ContentDetailModal from '@/components/influencer/ContentDetailModal.vue';
import SampleDetailModal from '@/components/influencer/SampleDetailModal.vue';
import ConversionDetailModal from '@/components/influencer/ConversionDetailModal.vue';
import { emitAiFilterComplete, onAiUiAction, type AiUiAction } from '@/utils/aiActionBus';
import { tabLabel } from '@/utils/aiInfluencerBridge';


// --- Constants ---
const PAGE_TYPE = 'influencer-list';
// --- Utils ---
const getAvatarColor = (name: string) => {
  if (!name) return '#cbd5e1';
  const colors = ['#8b5cf6', '#3b82f6', '#10b981', '#f59e0b', '#ec4899', '#6366f1', '#14b8a6', '#f43f5e'];
  let hash = 0;
  for (let i = 0; i < name.length; i++) {
    hash = name.charCodeAt(i) + ((hash << 5) - hash);
  }
  return colors[Math.abs(hash) % colors.length];
};

const BLANK_VALUE = '__BLANK__';
const SSE_DEBOUNCE_MS = 2000;
const USERS_PAGE_SIZE = 1000;
const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

// --- Loading ---
const loading = ref(false);
const initialLoading = ref(true);
const LoadingIcon = h(LoadingOutlined, { style: { fontSize: '24px' }, spin: true });

// --- State ---
const activeKey = ref('all');
const statusCounts = ref<Record<string, number>>({});
const selectedRowKeys = ref<(number | string)[]>([]);
const displayData = ref<Influencer[]>([]);
const renderBatchId = ref(0);
const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 0,
  showSizeChanger: true,
  pageSizeOptions: ['20', '50', '100', '500', '1000']
});

// --- Filter State ---
const filterExpanded = ref(false);
const filterForm = reactive(createInfluencerInitialFilter());

// --- Dictionaries (from Store) ---
const commonStore = useCommonDataStore();
const { userNameMap, allUsers, ownerUsers, availableTags, liaisonTagOptions, platforms, dynamicInfluencerTypes, countries } = storeToRefs(commonStore);
const sources = ['Manual', 'Crawler', 'Import'];
const influencerTypes = ['Fashion', 'Beauty', 'Tech', 'Lifestyle'];
const races = ['White', 'Black / African descent', 'Asian', 'Hispanic / Latino', 'Middle Eastern', 'Mixed', 'Other / Unclear'];

/** 仅「全部」Tab 可用的红人状态筛选项 */
const LIST_STATUS_FILTER_OPTIONS = [
  { value: 'PENDING', label: STATUS_LABEL.PENDING },
  { value: 'CONTACTED', label: STATUS_LABEL.CONTACTED },
  { value: 'COMMUNICATING', label: STATUS_LABEL.COMMUNICATING },
  { value: 'COOPERATING', label: STATUS_LABEL.COOPERATING },
  { value: 'DORMANT', label: STATUS_LABEL.DORMANT },
  { value: 'PAUSED', label: STATUS_LABEL.PAUSED },
  { value: 'BLACKLIST', label: STATUS_LABEL.BLACKLIST },
  { value: 'TERMINATED', label: STATUS_LABEL.TERMINATED },
];

const aiResultLimit = ref<number | undefined>();
const aiResultSort = ref<'fansDesc' | 'fansAsc' | undefined>();
const aiMatchedTotal = ref<number | undefined>();

function clearAiResultPrefs() {
  aiResultLimit.value = undefined;
  aiResultSort.value = undefined;
  aiMatchedTotal.value = undefined;
}

function syncAiResultPrefsFromRoute() {
  if (route.query.fromAi !== '1') return;
  aiResultLimit.value = route.query.limit ? Number(route.query.limit) : undefined;
  const sort = route.query.sortBy ? String(route.query.sortBy) : '';
  aiResultSort.value =
    sort === 'fansDesc' || sort === 'fansAsc' ? (sort as 'fansDesc' | 'fansAsc') : undefined;
}

function getItemFollowerCount(item: Record<string, unknown>): number {
  const direct = Number(item.followerCount ?? item.totalFans ?? 0);
  if (direct > 0) return direct;
  const sm = (item.socialMedias || item.socialMediaList || []) as Array<{ followerCount?: number }>;
  return sm.reduce((max, s) => Math.max(max, Number(s?.followerCount ?? 0)), 0);
}

function sliceRowsForAiTopList(
  rows: Influencer[],
  sortOverride?: 'fansDesc' | 'fansAsc',
  limitOverride?: number
): Influencer[] {
  let list = [...rows];
  const sort = sortOverride ?? aiResultSort.value;
  if (sort === 'fansDesc') {
    list.sort(
      (a, b) =>
        getItemFollowerCount(b as unknown as Record<string, unknown>) -
        getItemFollowerCount(a as unknown as Record<string, unknown>)
    );
  } else if (sort === 'fansAsc') {
    list.sort(
      (a, b) =>
        getItemFollowerCount(a as unknown as Record<string, unknown>) -
        getItemFollowerCount(b as unknown as Record<string, unknown>)
    );
  }
  const limit = limitOverride ?? aiResultLimit.value;
  if (limit != null && limit > 0) {
    list = list.slice(0, limit);
  }
  pagination.total = list.length;
  return list;
}

/** AI 截顶列表：区分「命中总数」与「当前页展示/邮箱统计」 */
const aiTopListBanner = computed(() => {
  if (route.query.fromAi !== '1') return null;
  const querySortBy = String(route.query.sortBy ?? '');
  const limit =
    aiResultLimit.value ?? (route.query.limit ? Number(route.query.limit) : undefined);
  const sort =
    aiResultSort.value ??
    (querySortBy === 'fansDesc' || querySortBy === 'fansAsc' ? querySortBy : undefined);
  if (limit == null && sort == null) return null;
  const shown = displayData.value.length;
  if (!shown) return null;
  let withEmail = 0;
  let withoutEmail = 0;
  for (const row of displayData.value) {
    if (row.email || row.backupEmail) withEmail += 1;
    else withoutEmail += 1;
  }
  return {
    matched: aiMatchedTotal.value,
    shown,
    withEmail,
    withoutEmail,
    sortLabel:
      sort === 'fansDesc' ? '粉丝最高' : sort === 'fansAsc' ? '粉丝最低' : '',
  };
});

// --- Transfer Modal State ---
const reasonModalVisible = ref(false);
const reasonModalTitle = ref('');
const pendingTransfer = ref<{ record: Influencer, status: string, statusText: string, targetStage?: string } | null>(null);
const batchReasonModalVisible = ref(false);
const batchReasonModalTitle = ref('');
const batchReasonItems = ref<{id: string, name: string}[]>([]);
const pendingBatchTransfer = ref<{ status: string; statusText: string; targetStage?: string } | null>(null);
const transferringIds = ref<number[]>([]);

// 为ExportModal提供string id的用户列表
const allUsersForExport = computed(() => allUsers.value.map(u => ({ id: String(u.id), name: u.name })));
const timeRanges = [
  { label: '今天', value: [dayjs().startOf('day'), dayjs().endOf('day')] },
  { label: '最近7天', value: [dayjs().subtract(7, 'd'), dayjs()] },
  { label: '最近30天', value: [dayjs().subtract(30, 'd'), dayjs()] },
];

// --- Modals State ---
const detailModalVisible = ref(false);
const detailModalInitialTab = ref('basic');
const currentInfluencer = ref<Partial<Influencer> | null>(null);
const createModalVisible = ref(false);
const contentDetailVisible = ref(false);
const sampleDetailVisible = ref(false);
const conversionDetailVisible = ref(false);
const currentInfluencerId = ref<number | string>('');
const currentInfluencerName = ref('');
const browserModalVisible = ref(false);
const previewUrl = ref('');
const exportModalVisible = ref(false);
const exportInitialScope = ref<'all' | 'selected' | undefined>();
const exportInitialTemplateName = ref<string | undefined>();
const importModalVisible = ref(false);
const batchSearchVisible = ref(false);
const batchSearchValue = ref('');
type BatchSearchType = 'name' | 'email' | 'link' | 'socialHandle' | 'tags';
const batchSearchType = ref<BatchSearchType>('name');



// --- Batch Assign State ---
const batchAssignOwnerVisible = ref(false);
const batchAssignOwnerValue = ref<number | undefined>(undefined);
const batchAssignLiaisonVisible = ref(false);
const batchAssignLiaisonValue = ref<number | undefined>(undefined);
const batchAssignTagsVisible = ref(false);
const batchAssignTagsValue = ref<number[]>([]);

const batchSearchTitles: Record<BatchSearchType, { title: string; subtitle: string }> = {
  name: { title: '批量搜索红人', subtitle: '请输入红人名称，多个名称请用换行或逗号分隔' },
  email: { title: '批量搜索邮箱', subtitle: '请输入红人邮箱，多个邮箱请用换行或逗号分隔' },
  link: { title: '批量搜索主页', subtitle: '请输入主页链接，多个链接请用换行或逗号分隔' },
  socialHandle: { title: '批量搜索社媒账号', subtitle: '请输入社媒账号名称，多个账号请用换行或逗号分隔' },
  tags: { title: '批量搜索标签', subtitle: '请输入标签名称，多个标签请用换行或逗号分隔' }
};

const batchSearchPlaceholder = computed(() => {
  if (batchSearchType.value === 'tags') {
    return '例如：\nBeauty\nFashion, Sports';
  }
  return '例如：\nInfluencer A\nInfluencer B, Influencer C';
});

// --- Computeds ---
const currentUserId = computed(() => userStore.userInfo?.id || 0);

const baseColumns = [
  { title: '红人信息', key: 'userInfo', width: 280, fixed: 'left' },
  { title: '社媒信息', key: 'socialMedia', width: 280 },
  { title: '联系方式', key: 'contact', width: 200 },
  { title: '建联平台', key: 'platform', width: 140 },
  { title: '红人国家', dataIndex: 'country', key: 'country', width: 120 },
  { title: '人种/肤色', dataIndex: 'race', key: 'race', width: 120 },
  { title: '联络员', key: 'contactPerson', width: 120 },
  { title: '负责人', key: 'owner', width: 120 },
  { title: '来源', key: 'source', width: 120 },
  { title: '红人标签', key: 'tags', width: 200 },
  { title: '是否付费', key: 'isPaid', width: 100 },
  { title: '粉丝总量', key: 'totalFans', width: 120 },
  { title: '合作次数', key: 'sampleCount', width: 120 },
  { title: '是否绑定内容', key: 'hasContent', width: 120 },
  { title: '最近更新', key: 'updatedAt', width: 150 },
  { title: '操作', key: 'action', width: 180, fixed: 'right' }
];

const columns = computed(() => {
    // Dynamic columns based on activeKey if needed
    return baseColumns;
});

const exportFields = [
  { key: 'id', title: '红人ID', dataKey: 'id' },
  { key: 'name', title: '红人全名', dataKey: 'name' }, // realName || nickName
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
  { key: 'sampleCount', title: '合作次数', dataKey: 'sampleCount' },
  { key: 'isPaid', title: '是否付费', dataKey: 'isPaidText' },
  { key: 'influencerType', title: '红人类型', dataKey: 'influencerType' },
  { key: 'tags', title: '红人标签', dataKey: 'tagsText' },
  { key: 'source', title: '来源', dataKey: 'source' },
  { key: 'sourceValue', title: '来源hashtag', dataKey: 'sourceValue' },
  { key: 'createdAt', title: '创建时间', dataKey: 'createdAt' },
  { key: 'latestOrderTime', title: '最新下单时间', dataKey: 'latestOrderTime' },
  { key: 'auditorName', title: '审核人', dataKey: 'auditorName' },
  { key: 'auditTime', title: '审核时间', dataKey: 'auditTime' },
  { key: 'paymentAmount', title: '付费金额', dataKey: 'paymentAmount' },
  { key: 'status', title: '状态', dataKey: 'statusText' },
  { key: 'description', title: '备注', dataKey: 'descriptionText' }
];

// --- Data Loading Methods ---

const fetchData = async (fetchCounts = true) => {
    loading.value = true;
    try {
        // 防御性同步：避免 onMounted / watch 重入时 aiResultSort 还没刷新就跑到这里
        syncAiResultPrefsFromRoute();
        const fromAi = route.query.fromAi === '1';
        const querySortBy = String(route.query.sortBy ?? '');
        const effectiveSort: 'fansDesc' | 'fansAsc' | undefined =
          aiResultSort.value
            ?? (querySortBy === 'fansDesc' || querySortBy === 'fansAsc'
              ? (querySortBy as 'fansDesc' | 'fansAsc')
              : undefined);
        const effectiveLimit: number | undefined =
          aiResultLimit.value ?? (route.query.limit ? Number(route.query.limit) : undefined);
        const needAiTopList = fromAi && (effectiveLimit != null || effectiveSort != null);
        let fetchPage = pagination.current;
        let fetchSize = pagination.pageSize;
        if (needAiTopList) {
          fetchPage = 1;
          const lim = effectiveLimit ?? 20;
          // 有排序需求时多拉一些用于前端再排序（后端 search 接口固定按 createdAt 倒序）
          fetchSize = effectiveSort
            ? Math.min(2000, Math.max(lim * 10, 200))
            : Math.min(500, lim);
        }
        const apiFilter = formatFilterForApi({
            ...filterForm,
            page: fetchPage,
            size: fetchSize
        }, activeKey.value);
        
        let res;
        
        if (fetchCounts) {
            // 使用合并查询接口，减少一次 HTTP 请求
            const result = await influencerService.getListWithCounts(apiFilter);
            res = result.list;
            if (result.statusCounts && typeof result.statusCounts === 'object') {
                statusCounts.value = result.statusCounts;
            }
        } else {
            // 仅翻页或切换 Tab 时，跳过 statusCounts 统计，减少数据库和网络开销
            res = await influencerService.getList(apiFilter);
        }
        
        aiMatchedTotal.value = res.totalElements || 0;
        let content = (res.content || []).map((item: any) => {
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
            ownerName: item.ownerId ? userNameMap.value[item.ownerId] || `ID:${item.ownerId}` : '-',
            contactPersonName: item.contactPersonId ? userNameMap.value[item.contactPersonId] || `ID:${item.contactPersonId}` : '-'
          };
        });
        if (needAiTopList) {
          content = sliceRowsForAiTopList(content, effectiveSort, effectiveLimit);
        } else {
          pagination.total = aiMatchedTotal.value;
        }
        renderBatchId.value++;
        const currentBatchId = renderBatchId.value;
        const CHUNK_SIZE = 40;
        
        if (content.length <= CHUNK_SIZE) {
            displayData.value = content;
        } else {
            // 首屏瞬时加载部分
            displayData.value = content.slice(0, CHUNK_SIZE);
            let currentIndex = CHUNK_SIZE;
            
            const renderNextChunk = () => {
                if (currentBatchId !== renderBatchId.value) return;
                if (currentIndex < content.length) {
                    const nextChunk = content.slice(currentIndex, Math.min(currentIndex + CHUNK_SIZE, content.length));
                    // 使用 push 增量更新，避免重新解构覆盖导致 Vue 每次全量 diff 数组
                    displayData.value.push(...nextChunk);
                    currentIndex += CHUNK_SIZE;
                    // 使用 setTimeout 而不是 rAF，确保 Vue 渲染后浏览器有充足时间进行重绘和垃圾回收，防止线程假死
                    setTimeout(renderNextChunk, 80);
                }
            };
            setTimeout(renderNextChunk, 80);
        }
        
        if (!needAiTopList) {
          pagination.total = res.totalElements || 0;
        }
    } catch (e) {
        if (e && (e as any).code === 'ERR_CANCELED') return;
        console.error('[fetchData] error:', e);
        message.warn('加载数据失败，请检查网络');
    } finally {
        loading.value = false;
        initialLoading.value = false;
    }
};



const fetchStatusCounts = async () => {
    try {
        const params = formatFilterForApi(filterForm, activeKey.value);
        const res = await influencerService.getStatusCounts(params);
        statusCounts.value = res || {};
    } catch (e) {
        console.error('Failed to fetch status counts:', e);
    }
};

// SSE — 使用 composable
const { connect: connectSSE } = useInfluencerSSE(() => fetchData());

// --- Methods ---

const handleFilter = () => {
    clearAiResultPrefs();
    pagination.current = 1;
    fetchData();
};





const openImportModal = () => {
    importModalVisible.value = true;
};

const handleImportSuccess = () => {
    fetchData();
};

// ==================== 空白筛选逻辑 ====================
// 映射：下拉选择 blankField name → filterForm 中对应的属性名
const blankFieldToFilterKey: Record<string, string> = {
  socialPlatform: 'socialPlatform',
  platform: 'platform',
  country: 'country',
  influencerType: 'influencerType',
  race: 'race',
  owner: 'owner',
  contactPerson: 'liaisonTagIds',
};

/** 文本输入框的「空白」开关 */
const toggleBlankField = (field: string) => {
  if (!filterForm.blankFields) filterForm.blankFields = [];
  const idx = filterForm.blankFields.indexOf(field);
  if (idx >= 0) {
    filterForm.blankFields.splice(idx, 1);
  } else {
    filterForm.blankFields.push(field);
    // 清空对应的文本输入
    if (field === 'name') filterForm.name = '';
    if (field === 'socialHandle') filterForm.socialHandle = '';
    if (field === 'email') filterForm.email = '';
  }
};

/** 下拉选择的「（空白）」互斥处理 */
const onSelectBlankChange = (val: any, blankFieldName: string) => {
  if (!filterForm.blankFields) filterForm.blankFields = [];
  const filterKey = blankFieldToFilterKey[blankFieldName];
  if (!filterKey) return;

  if (Array.isArray(val) && val.includes(BLANK_VALUE)) {
    // 选了「空白」，清除其他选项，只保留空白标记
    (filterForm as any)[filterKey] = [BLANK_VALUE];
    if (!filterForm.blankFields.includes(blankFieldName)) {
      filterForm.blankFields.push(blankFieldName);
    }
  } else {
    // 选了具体值，移除空白标记
    const idx = filterForm.blankFields.indexOf(blankFieldName);
    if (idx >= 0) filterForm.blankFields.splice(idx, 1);
  }
};

const handleResetFilter = () => {
    Object.assign(filterForm, createInfluencerInitialFilter());
    clearAiResultPrefs();
    handleFilter();
};

const handleTableChange = (page: number, pageSize: number) => {
    pagination.current = page;
    pagination.pageSize = pageSize;
    fetchData(false);
};

const handleTabChange = () => {
    pagination.current = 1;
    selectedRowKeys.value = [];
    if (activeKey.value !== 'all') {
      filterForm.listStatus = undefined;
    }
    clearAiResultPrefs();

    // 更新 URL 查询参数以支持刷新持久化
    const q: Record<string, string> = { ...route.query, status: activeKey.value };
    delete q.listStatus;
    delete q.limit;
    delete q.sortBy;
    delete q.fromAi;
    router.replace({ query: q });

    fetchData(false);
};

const onSelectChange = (keys: (number | string)[]) => {
    selectedRowKeys.value = keys;
};

// Actions
const handleEdit = (record: Influencer) => {
    currentInfluencer.value = record;
    detailModalInitialTab.value = 'basic';
    detailModalVisible.value = true;
};

const openCreateInfluencer = () => {
    createModalVisible.value = true;
};

const handleCreatedInfluencer = () => {
    message.success('创建成功');
    createModalVisible.value = false;
    fetchData();
};

const handleGoLink = (link?: string) => {
    if (!link) return;
    window.open(link.startsWith('http') ? link : `https://${link}`, '_blank');
};

const handleCopyLink = (link?: string) => {
    if (!link) return;
    navigator.clipboard.writeText(link);
    message.success('已复制');
};

const showContentDetail = (record: Influencer) => {
    currentInfluencerId.value = record.id;
    currentInfluencerName.value = record.realName || record.nickName || '';
    contentDetailVisible.value = true;
};
const showSampleDetail = (record: Influencer) => {
    currentInfluencerId.value = record.id;
    currentInfluencerName.value = record.realName || record.nickName || '';
    sampleDetailVisible.value = true;
};
const showConversionDetail = (record: Influencer) => {
    currentInfluencerId.value = record.id;
    currentInfluencerName.value = record.realName || record.nickName || '';
    conversionDetailVisible.value = true;
};

// --- 小A 页面联动 ---
let unsubscribeAiAction: (() => void) | undefined;

const TAB_STATUS_COUNT_KEY: Record<string, string> = {
  all: 'ALL',
  pending: 'PENDING',
  contacted: 'CONTACTED',
  communicating: 'COMMUNICATING',
  cooperating: 'COOPERATING',
  dormant: 'DORMANT',
  paused: 'PAUSED',
  blacklist: 'BLACKLIST',
  terminated: 'TERMINATED',
};

async function openInfluencerById(id: number) {
  try {
    const detail = await influencerService.getDetail(id);
    currentInfluencer.value = detail;
    detailModalInitialTab.value = 'basic';
    detailModalVisible.value = true;
  } catch {
    message.error('打开红人详情失败');
  }
}

function resetFilterFormForAi() {
  const initial = createInfluencerInitialFilter();
  Object.assign(filterForm, initial);
  filterForm.socialHandle = '';
  filterForm.name = '';
  filterForm.listStatus = undefined;
  filterExpanded.value = false;
  clearAiResultPrefs();
}

function applyCopilotDataFields(data: Record<string, unknown>) {
  if (data.searchKey) filterForm.name = String(data.searchKey);
  if (data.socialHandle) {
    filterForm.socialHandle = String(data.socialHandle).trim();
    filterExpanded.value = true;
  }
  if (data.email) {
    filterForm.email = String(data.email).trim();
    filterExpanded.value = true;
  }
  if (data.ownerName) {
    const key = String(data.ownerName).trim().toLowerCase();
    const u = ownerUsers.value.find((o) => o.name?.toLowerCase().includes(key));
    if (u) filterForm.owner = [u.id];
    filterExpanded.value = true;
  }
  if (data.contactPersonName) {
    const key = String(data.contactPersonName).trim().toLowerCase();
    const t = liaisonTagOptions.value.find((o) => o.name?.toLowerCase().includes(key));
    if (t) filterForm.liaisonTagIds = [t.id];
    filterExpanded.value = true;
  }
  if (data.platform) {
    filterForm.platform = Array.isArray(data.platform) ? data.platform.map(String) : [String(data.platform)];
    filterExpanded.value = true;
  }
  if (data.socialPlatform) {
    const sp = data.socialPlatform;
    filterForm.socialPlatform = Array.isArray(sp) ? sp.map(String) : [String(sp)];
    filterExpanded.value = true;
  }
  if (data.country) {
    filterForm.country = String(data.country);
    filterExpanded.value = true;
  }
  if (data.source) {
    filterForm.source = String(data.source);
    filterExpanded.value = true;
  }
  if (data.link) {
    filterForm.link = String(data.link).trim();
    filterExpanded.value = true;
  }
  if (data.brand) {
    filterForm.brand = String(data.brand);
    filterExpanded.value = true;
  }
  if (data.influencerType) {
    filterForm.influencerType = String(data.influencerType);
    filterExpanded.value = true;
  }
  if (data.race) {
    const r = data.race;
    filterForm.race = Array.isArray(r) ? r.map(String) : [String(r)];
    filterExpanded.value = true;
  }
  if (data.tagNames) {
    const names = Array.isArray(data.tagNames)
      ? data.tagNames.map(String)
      : String(data.tagNames).split('|').filter(Boolean);
    const ids = names
      .map((n) => availableTags.value.find((t) => t.name?.toLowerCase().includes(n.toLowerCase()))?.id)
      .filter((id): id is number => typeof id === 'number');
    if (ids.length) {
      filterForm.tagIds = ids as number[];
      filterExpanded.value = true;
    }
  }
  if (data.isPaid === true || data.isPaid === false) {
    filterForm.isPaid = Boolean(data.isPaid);
    filterExpanded.value = true;
  }
  if (data.hasOutputContent === true || data.hasOutputContent === false) {
    filterForm.hasOutputContent = data.hasOutputContent ? 'true' : 'false';
    filterExpanded.value = true;
  }
  if (data.timeType && data.timeStart && data.timeEnd) {
    filterForm.timeType = data.timeType === 'updated' ? 'updated' : 'created';
    filterForm.timeRange = [String(data.timeStart), String(data.timeEnd)];
    filterExpanded.value = true;
  }
  if (data.fansMin != null || data.fansMax != null) {
    filterForm.fansMin = undefined;
    filterForm.fansMax = undefined;
    if (data.fansMin != null && data.fansMin !== '') filterForm.fansMin = Number(data.fansMin);
    if (data.fansMax != null && data.fansMax !== '') filterForm.fansMax = Number(data.fansMax);
    filterExpanded.value = true;
  }
  if (data.minSampleCount != null && data.minSampleCount !== '') {
    filterForm.minSampleCount = Number(data.minSampleCount);
    filterExpanded.value = true;
  }
  if (data.maxSampleCount != null && data.maxSampleCount !== '') {
    filterForm.maxSampleCount = Number(data.maxSampleCount);
    filterExpanded.value = true;
  }
}

function routeQueryToCopilotData(): Record<string, unknown> {
  const q = route.query;
  const data: Record<string, unknown> = {};
  if (q.q) data.searchKey = String(q.q);
  if (q.handle) data.socialHandle = String(q.handle);
  if (q.email) data.email = String(q.email);
  if (q.ownerName) data.ownerName = String(q.ownerName);
  if (q.contactPersonName) data.contactPersonName = String(q.contactPersonName);
  if (q.link) data.link = String(q.link);
  if (q.source) data.source = String(q.source);
  if (q.platform) data.platform = String(q.platform);
  if (q.socialPlatform) data.socialPlatform = String(q.socialPlatform);
  if (q.country) data.country = String(q.country);
  if (q.race) data.race = String(q.race);
  if (q.influencerType) data.influencerType = String(q.influencerType);
  if (q.tagNames) data.tagNames = String(q.tagNames).split('|').filter(Boolean);
  if (q.fansMax) data.fansMax = Number(q.fansMax);
  if (q.fansMin) data.fansMin = Number(q.fansMin);
  if (q.minSampleCount) data.minSampleCount = Number(q.minSampleCount);
  if (q.maxSampleCount) data.maxSampleCount = Number(q.maxSampleCount);
  if (q.isPaid === '0') data.isPaid = false;
  if (q.isPaid === '1') data.isPaid = true;
  if (q.hasOutputContent === '0') data.hasOutputContent = false;
  if (q.hasOutputContent === '1') data.hasOutputContent = true;
  if (q.timeType) data.timeType = String(q.timeType);
  if (q.timeStart) data.timeStart = String(q.timeStart);
  if (q.timeEnd) data.timeEnd = String(q.timeEnd);
  return data;
}

function applyAiFilterPayload(action: AiUiAction) {
  if (action.data.resetFilters === true) {
    resetFilterFormForAi();
  }
  const tab = String(action.data.statusTab || 'all');
  activeKey.value = tab;
  applyCopilotDataFields(action.data);
  if (tab === 'all' && action.data.listStatus) {
    filterForm.listStatus = String(action.data.listStatus);
  }
  if (action.data.listLimit != null) {
    aiResultLimit.value = Number(action.data.listLimit);
  }
  if (action.data.sortBy === 'fansDesc' || action.data.sortBy === 'fansAsc') {
    aiResultSort.value = action.data.sortBy;
  }
  pagination.current = 1;
}

type AiFilterSnapshot = {
  activeKey: string;
  filterForm: typeof filterForm;
  filterExpanded: boolean;
  paginationCurrent: number;
};

function snapshotFilterState(): AiFilterSnapshot {
  return {
    activeKey: activeKey.value,
    filterForm: { ...filterForm },
    filterExpanded: filterExpanded.value,
    paginationCurrent: pagination.current,
  };
}

function restoreFilterState(snap: AiFilterSnapshot) {
  activeKey.value = snap.activeKey;
  Object.assign(filterForm, snap.filterForm);
  filterExpanded.value = snap.filterExpanded;
  pagination.current = snap.paginationCurrent;
}

function reportAiFilterTotal(extra?: {
  handle?: string;
  searchName?: string;
  fansMax?: number;
  fansMin?: number;
  applied?: boolean;
}) {
  // 关键修复：区分两种场景
  //  1) AI 截顶模式（带 limit/sort）→ 当前展示的就是"匹配的全部"，shown = displayData.length
  //  2) 普通筛选模式 → 当前展示的只是首页 pageSize 行，真实命中数应取 aiMatchedTotal（=totalElements）
  const fromAi = route.query.fromAi === '1';
  const querySortBy = String(route.query.sortBy ?? '');
  const limit =
    aiResultLimit.value ?? (route.query.limit ? Number(route.query.limit) : undefined);
  const sort =
    aiResultSort.value ?? (querySortBy === 'fansDesc' || querySortBy === 'fansAsc' ? querySortBy : undefined);
  const isTopList = fromAi && (limit != null || sort != null);
  const matched = aiMatchedTotal.value ?? pagination.total ?? displayData.value.length;
  const shown = isTopList
    ? displayData.value.length
    : matched;
  const singleId =
    shown === 1 && displayData.value[0]?.id != null
      ? Number(displayData.value[0].id)
      : undefined;
  emitAiFilterComplete({
    total: shown,
    matchedTotal: matched,
    listLimit: aiResultLimit.value,
    sortBy: aiResultSort.value,
    applied: extra?.applied ?? shown > 0,
    influencerId: singleId,
    ...extra,
  });
}

function runAiFilterWithRollback(
  action: AiUiAction,
  extra?: { handle?: string; searchName?: string; fansMax?: number; fansMin?: number }
) {
  const snap = snapshotFilterState();
  applyAiFilterPayload(action);
  return fetchData().then(() => {
    const total = pagination.total;
    if (total === 0) {
      restoreFilterState(snap);
      return fetchData().then(() => {
        reportAiFilterTotal({ ...extra, applied: false });
      });
    }
    reportAiFilterTotal({ ...extra, applied: true });
  });
}

function closeInfluencerDetailModal() {
  detailModalVisible.value = false;
  currentInfluencer.value = null;
  if (route.query.openId != null) {
    const q = { ...route.query };
    delete q.openId;
    void router.replace({ path: route.path, query: q });
  }
}

function handleAiUiAction(action: AiUiAction) {
  if (action.name === 'CloseInfluencerDetail') {
    closeInfluencerDetailModal();
    return;
  }
  if (action.name === 'OpenExportModal') {
    const scope = action.data.scope != null ? String(action.data.scope) : undefined;
    exportInitialScope.value =
      scope === 'selected' || scope === 'all' ? scope : undefined;
    exportInitialTemplateName.value = action.data.templateName
      ? String(action.data.templateName)
      : undefined;
    exportModalVisible.value = true;
    return;
  }
  if (action.name === 'CopilotBulkAction') {
    const maxCount = action.data.maxCount != null ? Number(action.data.maxCount) : undefined;
    void handleCopilotBulkAction(String(action.data.mode ?? ''), maxCount);
    return;
  }
  if (action.name === 'OpenInfluencerDetail') {
    const forceOpen = action.data.forceOpen === true;
    const id = action.data.influencerId != null ? Number(action.data.influencerId) : null;
    const handle = action.data.socialHandle ? String(action.data.socialHandle) : undefined;

    const openById = (rowId: number) => {
      void openInfluencerById(rowId);
      const q = { ...route.query, fromAi: '1', openId: String(rowId) };
      if (handle) (q as Record<string, string>).handle = handle;
      void router.replace({ path: route.path, query: q });
    };

    if (forceOpen && id != null) {
      openById(id);
    }

    if (handle) {
      void runAiFilterWithRollback(
        { name: 'ApplyInfluencerFilter', data: { statusTab: 'all', socialHandle: handle } },
        { handle }
      ).then(() => {
        if (!forceOpen) return;
        if (pagination.total > 1) return;
        if (pagination.total === 1 && displayData.value[0]?.id != null) {
          openById(Number(displayData.value[0].id));
        }
      });
    } else if (forceOpen && id != null) {
      fetchData();
    }
    return;
  }
  if (action.name !== 'ApplyInfluencerFilter') return;
  void runAiFilterWithRollback(action, {
    handle: action.data.socialHandle ? String(action.data.socialHandle) : undefined,
    searchName: action.data.searchKey ? String(action.data.searchKey) : undefined,
    fansMax: action.data.fansMax != null ? Number(action.data.fansMax) : undefined,
    fansMin: action.data.fansMin != null ? Number(action.data.fansMin) : undefined,
  });
}

// --- Lifecycle ---
const STATE_KEY = 'influencerListState';

onMounted(async () => {
    // 从 SessionStorage 恢复状态
    try {
        const savedState = sessionStorage.getItem(STATE_KEY);
        if (savedState) {
            const parsed = JSON.parse(savedState);
            if (parsed.activeKey) activeKey.value = parsed.activeKey;
            if (parsed.pagination) Object.assign(pagination, parsed.pagination);
            if (parsed.filterForm) Object.assign(filterForm, parsed.filterForm);
            if (parsed.filterExpanded !== undefined) filterExpanded.value = parsed.filterExpanded;
        }
    } catch (e) {
        console.error('Failed to load saved state', e);
    }

    // 从 URL 查询参数恢复（小A 跳转，优先级高于 SessionStorage）
    if (route.query.fromAi === '1') {
      if (route.query.status) activeKey.value = String(route.query.status);
      if (route.query.listStatus && activeKey.value === 'all') {
        filterForm.listStatus = String(route.query.listStatus);
      }
      applyCopilotDataFields(routeQueryToCopilotData());
      syncAiResultPrefsFromRoute();
    }

    unsubscribeAiAction = onAiUiAction(handleAiUiAction);

    // 1. 立即加载核心列表数据
    fetchData();

    // 2. 辅助数据从 Store 加载（带缓存，不重复请求）
    commonStore.loadAll().then(() => {
        if (route.query.fromAi === '1') {
          applyCopilotDataFields(routeQueryToCopilotData());
          fetchData();
        }
        // 用户名加载完成后更新显示映射
        if (displayData.value.length > 0 && Object.keys(userNameMap.value).length > 0) {
            displayData.value = displayData.value.map((item: any) => ({
                ...item,
                ownerName: item.ownerId ? userNameMap.value[item.ownerId] || item.ownerName : '-',
                contactPersonName: item.contactPersonId ? userNameMap.value[item.contactPersonId] || item.contactPersonName : '-'
            }));
        }
    });

    // 3. SSE
    connectSSE();

    if (route.query.openId) {
      void openInfluencerById(Number(route.query.openId));
    }
});

onUnmounted(() => {
  unsubscribeAiAction?.();
});

watch(
  () => [
    route.query.status,
    route.query.fromAi,
    route.query.resetAi,
    route.query.q,
    route.query.fansMax,
    route.query.fansMin,
    route.query.isPaid,
    route.query.handle,
    route.query.openId,
    route.query.listStatus,
    route.query.limit,
    route.query.sortBy,
    route.query.socialPlatform,
    route.query.platform,
    route.query.race,
    route.query.email,
    route.query.ownerName,
    route.query.contactPersonName,
    route.query.link,
    route.query.source,
    route.query.influencerType,
    route.query.tagNames,
    route.query.hasOutputContent,
    route.query.timeType,
    route.query.timeStart,
    route.query.timeEnd,
    route.query.minSampleCount,
    route.query.maxSampleCount,
  ],
  () => {
    if (route.query.fromAi !== '1') return;
    if (route.query.resetAi === '1') {
      resetFilterFormForAi();
    }
    if (route.query.status) {
      activeKey.value = String(route.query.status);
    }
    if (route.query.listStatus && activeKey.value === 'all') {
      filterForm.listStatus = String(route.query.listStatus);
    } else if (route.query.resetAi === '1') {
      filterForm.listStatus = undefined;
    }
    applyCopilotDataFields(routeQueryToCopilotData());
    syncAiResultPrefsFromRoute();
    if (route.query.openId) {
      void openInfluencerById(Number(route.query.openId));
    }
    pagination.current = 1;
    fetchData().then(() => {
      reportAiFilterTotal({
        handle: route.query.handle ? String(route.query.handle) : undefined,
        fansMax: route.query.fansMax ? Number(route.query.fansMax) : undefined,
        fansMin: route.query.fansMin ? Number(route.query.fansMin) : undefined,
      });
    });
  }
);

watch([() => pagination.current, () => pagination.pageSize, filterForm, activeKey, filterExpanded], () => {
    sessionStorage.setItem(STATE_KEY, JSON.stringify({
        pagination: { current: pagination.current, pageSize: pagination.pageSize },
        filterForm,
        activeKey: activeKey.value,
        filterExpanded: filterExpanded.value
    }));
}, { deep: true });





const getLiaisonName = (record: Influencer) => {
  // 1. Try to find LIAISON tag in tag list (tags 可能是名称字符串或对象)
  if (record.tags && Array.isArray(record.tags)) {
    const opts = Array.isArray(liaisonTagOptions.value) ? liaisonTagOptions.value : [];
    const liaisonNames = opts.map(o => o.name);
    const liaisonIds = opts.map(o => o.id);
    const liaisonTag = record.tags.find((t: any) => {
      if (typeof t === 'object') {
        return liaisonIds.includes(Number(t.id));
      }
      // t 是字符串，可能是名称或 ID
      if (liaisonNames.includes(t)) return true;
      return liaisonIds.includes(Number(t));
    });
    if (liaisonTag) {
      if (typeof liaisonTag === 'object') return liaisonTag.name;
      // 如果是名称字符串，直接返回
      if (liaisonNames.includes(liaisonTag)) return liaisonTag;
      // 如果是 ID，查找名称
      const opt = liaisonTagOptions.value.find(o => o.id === Number(liaisonTag));
      return opt ? opt.name : liaisonTag;
    }
  }
  
  // 2. Fallback to old field
  return record.contactPersonName || (record.contactPersonId ? `ID:${record.contactPersonId}` : '-');
};

// 过滤掉 LIAISON 等其它标签，只显示红人标签 (INFLUENCER 类别)
const getFilteredTags = (record: Influencer) => {
  if (!record.tags || !Array.isArray(record.tags)) return [];
  
  // 如果 tags 里是完整的对象（包含 category）
  if (record.tags.length > 0 && typeof record.tags[0] === 'object' && record.tags[0].category) {
    return record.tags.filter((t: any) => t.category === 'INFLUENCER');
  }

  // 否则根据 availableTags (已加载的 INFLUENCER 标签) 来过滤
  const tags = Array.isArray(availableTags.value) ? availableTags.value : [];
  const influencerTagNames = tags.map(t => t.name);
  const influencerTagIds = tags.map(t => t.id);

  return record.tags.filter((t: any) => {
    if (typeof t === 'object') {
      return influencerTagIds.includes(Number(t.id));
    }
    // t 是字符串：排除名称和 ID 匹配
    return influencerTagNames.includes(t) || influencerTagIds.includes(Number(t));
  });
};

// Status Transfer
const statusMap: Record<string, string> = {
  pending: '待联系',
  contacted: '已联系',
  communicating: '沟通中',
  cooperating: '合作中',
  dormant: '休眠',
  paused: '暂停',
  blacklist: '黑名单',
  terminated: '终止'
};
const statusRequireReason = ['paused', 'blacklist', 'terminated'];

const getTransferOptions = (currentKey: string): {key: string, label: string, targetStage?: string}[] => {
    const allOptions: {key: string, label: string, targetStage?: string}[] = [
        { key: 'UNSCREENED', label: '资源池-待筛选', targetStage: 'POOL' },
        { key: 'REJECTED', label: '资源池-暂不合适', targetStage: 'POOL' },
        { key: 'PENDING', label: '待联系', targetStage: 'ONBOARDED' },
        { key: 'CONTACTED', label: '已联系', targetStage: 'ONBOARDED' },
        { key: 'COMMUNICATING', label: '沟通中', targetStage: 'ONBOARDED' },
        { key: 'COOPERATING', label: '合作中', targetStage: 'ONBOARDED' },
        { key: 'DORMANT', label: '休眠中', targetStage: 'ONBOARDED' },
        { key: 'PAUSED', label: '暂不合作', targetStage: 'ONBOARDED' },
        { key: 'BLACKLIST', label: '黑名单', targetStage: 'ONBOARDED' },
        { key: 'TERMINATED', label: '不再合作', targetStage: 'ONBOARDED' },
    ];
    return allOptions.filter(opt => opt.key.toLowerCase() !== currentKey.toLowerCase());
};

const getRowClassName = (record: Influencer) => {
  return transferringIds.value.includes(record.id) ? 'transfer-row-leave' : '';
};

const handleTransfer = async (e: MenuInfo, record: Influencer) => {
    const targetStatus = e.key as string;
    const options = getTransferOptions(activeKey.value);
    const selectedOption = options.find(opt => opt.key === targetStatus);
    const targetStage = selectedOption?.targetStage;
    const label = selectedOption?.label || statusMap[targetStatus.toLowerCase()] || targetStatus;
    
    // 休眠规则检查
    if (targetStatus === 'DORMANT') {
        try {
            message.loading({ content: '检查休眠规则...', key: 'checkDormancy' });
            const result = await influencerService.checkDormancy(record.id);
            message.destroy('checkDormancy');
            
            if (!result.eligible) {
                Modal.confirm({
                    title: '不符合休眠规则',
                    content: `该红人目前不符合自动休眠条件 (${result.message})。是否确认强制移入休眠？`,
                    okText: '强制移入',
                    cancelText: '取消',
                    onOk: () => {
                        executeTransfer(record.id, targetStatus, `Manual Force: ${result.message}`, targetStage);
                    }
                });
                return;
            }
        } catch (err) {
            message.destroy('checkDormancy');
            console.error('Check dormancy failed', err);
             Modal.confirm({
                title: '规则检查失败',
                content: '无法验证休眠规则。是否强制移入？',
                okText: '强制移入',
                cancelText: '取消',
                onOk: () => executeTransfer(record.id, targetStatus, 'Manual Force: Check Failed', targetStage)
            });
            return;
        }
    }

    // 需要输入原因的状态流转
    if (statusRequireReason.includes(targetStatus.toLowerCase())) {
        pendingTransfer.value = { record, status: targetStatus, statusText: label, targetStage };
        reasonModalTitle.value = `流转至 ${label}`;
        reasonModalVisible.value = true;
    } else {
         executeTransfer(record.id, targetStatus, undefined, targetStage);
    }
};

const handleConfirmTransfer = (reason: string) => {
    if (pendingTransfer.value) {
        const { record, status, targetStage } = pendingTransfer.value as any;
        executeTransfer(record.id, status, reason, targetStage);
        reasonModalVisible.value = false;
        pendingTransfer.value = null;
    }
};

const executeTransfer = async (id: number, status: string, reason?: string, targetStage?: string) => {
    if (!transferringIds.value.includes(id)) {
        transferringIds.value.push(id);
    }

    try {
        const apiPromise = influencerService.batchChangeStatus({
            ids: [Number(id)],
            targetStatus: status.toUpperCase(), 
            targetStage: targetStage?.toUpperCase() as any,
            reason
        });
        const animationPromise = new Promise(resolve => setTimeout(resolve, 500));
        const [result] = await Promise.all([apiPromise, animationPromise]);
        
        if (result && result.failedItems && result.failedItems.length > 0) {
             const failMsg = result.failedItems.map((item: any) => item.errorMessage || item.message || '未知原因').join(', ');
             message.error(`部分流转失败: ${failMsg}`);
        } else {
             message.success('流转成功');
        }
        fetchData();
    } catch(e: any) { 
        console.error('Transfer failed:', e);
        message.error(e.response?.data?.message || e.message || '网络请求失败，请稍后重试');
    } finally {
        transferringIds.value = transferringIds.value.filter(tid => Number(tid) !== Number(id));
    }
};

const confirmDelete = (record: Influencer) => {
    Modal.confirm({
        title: '确定要移除该红人吗？',
        content: `移除红人 "${record.realName || record.nickName || '无名'}"，移除后不可恢复。`,
        okText: '确定',
        cancelText: '取消',
        okType: 'danger',
        onOk: () => handleDelete(record.id)
    });
};

// Batch
const batchMove = (status: string) => {
    if (!selectedRowKeys.value.length) return message.warn('请选择红人');
    
    const label = statusMap[status] || status;
    const options = getTransferOptions(activeKey.value);
    const selectedOption = options.find(opt => opt.key === status);
    const targetStage = selectedOption?.targetStage;

    if (statusRequireReason.includes(status)) {
        pendingBatchTransfer.value = { status, statusText: label, targetStage };
        batchReasonModalTitle.value = `批量流转至 ${label}`;
        batchReasonItems.value = displayData.value
            .filter(d => selectedRowKeys.value.includes(d.id))
            .map(d => ({ id: String(d.id), name: d.realName || d.nickName || '' }));
        batchReasonModalVisible.value = true;
    } else {
        executeBatchTransfer(status, undefined, targetStage);
    }
};

const handleConfirmBatchTransfer = () => {
    if (pendingBatchTransfer.value) {
        const { status, targetStage } = pendingBatchTransfer.value as any;
        executeBatchTransfer(status, 'Batch Operation', targetStage);
        batchReasonModalVisible.value = false;
    }
};

const executeBatchTransfer = async (status: string, reason?: string, targetStage?: string) => {
     try {
        const result = await influencerService.batchChangeStatus({
            ids: selectedRowKeys.value.map(k => Number(k)),
            targetStatus: status.toUpperCase(),
            targetStage: targetStage?.toUpperCase() as any,
            reason
        });
        
        if (result && result.failedItems && result.failedItems.length > 0) {
             const failMsg = result.failedItems.map((item: any) => item.errorMessage || item.message || '未知原因').join(', ');
             message.error(`部分操作失败: ${failMsg}`);
        } else {
             message.success('批量操作成功');
        }
        
        selectedRowKeys.value = [];
        fetchData();
    } catch(e: any) { 
        console.error('Batch transfer failed:', e);
        message.error(e.response?.data?.message || e.message || '批量流转失败');
    }
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

// --- Batch Assign Operations ---
const openBatchAssignOwner = () => {
    if (!selectedRowKeys.value.length) return message.warn('请选择红人');
    batchAssignOwnerValue.value = undefined;
    batchAssignOwnerVisible.value = true;
};

const handleBatchAssignOwner = async () => {
    if (!batchAssignOwnerValue.value) return message.warn('请选择负责人');
    try {
        await influencerService.batchUpdate({
            ids: selectedRowKeys.value.map(k => Number(k)),
            field: 'ownerId',
            value: batchAssignOwnerValue.value
        });
        message.success('批量分配负责人成功');
        batchAssignOwnerVisible.value = false;
        selectedRowKeys.value = [];
        fetchData();
    } catch (e) {
        message.error('操作失败');
    }
};

const openBatchAssignLiaison = () => {
    if (!selectedRowKeys.value.length) return message.warn('请选择红人');
    batchAssignLiaisonValue.value = undefined;
    batchAssignLiaisonVisible.value = true;
};

const handleBatchAssignLiaison = async () => {
    if (!batchAssignLiaisonValue.value) return message.warn('请选择联络员');
    try {
        // 联络员是覆盖操作：先移除所有联络员标签，再添加选中的
        const allLiaisonIds = liaisonTagOptions.value.map(t => t.id);
        await influencerService.batchUpdate({
            ids: selectedRowKeys.value.map(k => Number(k)),
            field: 'replaceTags',
            value: [batchAssignLiaisonValue.value],
            removeTagIds: allLiaisonIds
        });
        message.success('批量分配联络员成功');
        batchAssignLiaisonVisible.value = false;
        selectedRowKeys.value = [];
        fetchData();
    } catch (e) {
        message.error('操作失败');
    }
};

const openBatchAssignTags = () => {
    if (!selectedRowKeys.value.length) return message.warn('请选择红人');
    batchAssignTagsValue.value = [];
    batchAssignTagsVisible.value = true;
};

const handleBatchAssignTags = async () => {
    if (!batchAssignTagsValue.value.length) return message.warn('请选择标签');
    try {
        await influencerService.batchUpdate({
            ids: selectedRowKeys.value.map(k => Number(k)),
            field: 'appendTags',
            value: batchAssignTagsValue.value
        });
        message.success('批量打标签成功');
        batchAssignTagsVisible.value = false;
        selectedRowKeys.value = [];
        fetchData();
    } catch (e) {
        message.error('操作失败');
    }
};

const goBatchMail = () => {
    if (!selectedRowKeys.value.length) {
        message.warn('请先勾选红人');
        return;
    }
    const ids = selectedRowKeys.value.map((k) => Number(k)).filter((n) => !Number.isNaN(n) && n > 0);
    router.push({ path: '/mail/campaigns/create', query: { ids: ids.join(',') } });
};

/** 单红人 AI 邀约邮件：跳转到批量发信页（已支持 1 人 + AI 个性化） */
function openInviteMail(record: Influencer) {
  if (!record?.id) return;
  if (!record.email && !record.backupEmail) {
    message.warning('该红人未配置邮箱，无法发送邀约邮件');
    return;
  }
  router.push({
    path: '/mail/campaigns/create',
    query: {
      ids: String(record.id),
      ai: '1',
      from: 'invite',
    },
  });
}

/** 小A 快捷操作：勾选当前筛选条件下的全部红人 */
async function selectAllFilteredInfluencerIds(maxCount?: number): Promise<number[]> {
  syncAiResultPrefsFromRoute();
  const fromAi = route.query.fromAi === '1';
  const querySortBy = String(route.query.sortBy ?? '');
  const effectiveMax = maxCount ?? aiResultLimit.value;
  const effectiveSort: 'fansDesc' | 'fansAsc' | undefined =
    aiResultSort.value ??
    (querySortBy === 'fansDesc' || querySortBy === 'fansAsc'
      ? (querySortBy as 'fansDesc' | 'fansAsc')
      : undefined);
  const isTopList = fromAi && (effectiveMax != null || effectiveSort != null);

  // 列表已是 Top-N 且与展示一致时，直接勾选当前页，避免误勾全量命中池
  if (isTopList && effectiveMax != null && displayData.value.length > 0) {
    const ids = displayData.value
      .map((item) => item.id)
      .filter((id): id is number => id != null)
      .slice(0, effectiveMax);
    if (ids.length > 0) {
      message.success({ content: `已勾选 ${ids.length} 位红人`, key: 'copilotSelectAll' });
      return ids;
    }
  }

  // 必须用 aiMatchedTotal（= res.totalElements，真实命中数）兜底，否则只能拉到 10 条
  // by-createdAt 的池子，前端再按粉丝排序也是错的命中池，会导致勾选位移、与左侧列表不一致。
  const matched = (aiMatchedTotal.value ?? pagination.total) || 0;
  const total = matched;
  if (!total) {
    message.warning('当前列表没有可勾选的红人');
    return [];
  }
  const hardCap = 2000;
  const want =
    effectiveMax && effectiveMax > 0
      ? Math.min(effectiveMax, total, hardCap)
      : Math.min(total, hardCap);
  if (!effectiveMax && total > hardCap) {
    message.warning(`当前匹配 ${total} 人，已自动勾选前 ${want} 人（单次上限 ${hardCap}）`);
  }
  message.loading({ content: `正在勾选 ${want} 位红人…`, key: 'copilotSelectAll' });
  try {
    const needSort = !!effectiveSort;
    const fetchSize = needSort
      ? Math.min(total, hardCap)
      : Math.max(want, Math.min(total, hardCap));
    const apiFilter = formatFilterForApi(
      { ...filterForm, page: 1, size: fetchSize },
      activeKey.value
    );
    apiFilter.stage = 'ONBOARDED';
    const res = await influencerService.getList(apiFilter);
    let rows = res.content || [];
    if (effectiveSort === 'fansDesc') {
      rows = [...rows].sort(
        (a, b) =>
          getItemFollowerCount(b as unknown as Record<string, unknown>) -
          getItemFollowerCount(a as unknown as Record<string, unknown>)
      );
    } else if (effectiveSort === 'fansAsc') {
      rows = [...rows].sort(
        (a, b) =>
          getItemFollowerCount(a as unknown as Record<string, unknown>) -
          getItemFollowerCount(b as unknown as Record<string, unknown>)
      );
    }
    const ids = rows
      .map((item: { id?: number }) => item.id)
      .filter((id): id is number => id != null)
      .slice(0, want);
    message.success({ content: `已勾选 ${ids.length} 位红人`, key: 'copilotSelectAll' });
    return ids;
  } catch (e) {
    message.error({ content: '勾选失败，请稍后重试', key: 'copilotSelectAll' });
    return [];
  }
}

async function handleCopilotBulkAction(mode: string, maxCount?: number) {
  const ids = await selectAllFilteredInfluencerIds(maxCount);
  if (!ids.length) return;
  selectedRowKeys.value = ids;
  if (mode === 'mail') {
    router.push({ path: '/mail/campaigns/create', query: { ids: ids.join(',') } });
    return;
  }
  if (mode === 'tag') {
    batchAssignTagsVisible.value = true;
  }
}

// --- Copy Helper ---
const copyText = (text: string | undefined | null) => {
  if (!text) {
    message.warn('无内容可复制');
    return;
  }
  
  const str = String(text);
  
  // Try Clipboard API first
  if (navigator.clipboard && navigator.clipboard.writeText) {
    navigator.clipboard.writeText(str).then(() => {
      message.success('已复制到剪贴板');
    }).catch(err => {
      console.error('Clipboard API copy failed:', err);
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
    console.error('Fallback copy failed:', err);
    message.error('复制失败');
  }
  document.body.removeChild(textArea);
};

const handleCopy = copyText;

// Export
const openExportModal = () => exportModalVisible.value = true;
const handleExportFromModal = async (payload: { scope: string, fields: string[], columns: any[], templateId?: string, templateName?: string }) => {
  try {
    const { scope, columns, templateId, templateName } = payload;
    let exportData: any[] = [];
    
    if (scope === 'selected') {
      exportData = displayData.value.filter(item => selectedRowKeys.value.includes(item.id));
    } else {
      message.loading({ content: '正在获取全部数据...', key: 'exportAllMsg' });
      const apiFilter = formatFilterForApi({
          ...filterForm,
          page: 1,
          size: pagination.total > 0 ? pagination.total : 10000
      }, activeKey.value);
      apiFilter.stage = 'ONBOARDED';
      
      const res = await influencerService.getList(apiFilter);
      exportData = res.content || [];
      message.success({ content: '获取数据成功，开始导出', key: 'exportAllMsg' });
    }
    
    // 格式化导出数据
    const formattedData = exportData.map(item => {
      const socialMedias = item.socialMedias || item.socialMediaList || [];
      const getSM = (platform: string) => socialMedias.find((s: any) => s.platform?.toUpperCase() === platform.toUpperCase());
      
      const ig = getSM('INSTAGRAM') || getSM('IG');
      const tt = getSM('TIKTOK') || getSM('TT');
      const yt = getSM('YOUTUBE') || getSM('YT');

      return {
        ...item,
        name: item.realName || item.nickName || '-',
        isPaidText: item.isPaid ? '是' : '否',
        statusText: STATUS_LABEL[item.status as keyof typeof STATUS_LABEL] || item.status || '-',
        ownerName: item.ownerId ? userNameMap.value[item.ownerId] || `ID:${item.ownerId}` : '-',
        contactPersonName: item.contactPersonId ? userNameMap.value[item.contactPersonId] || `ID:${item.contactPersonId}` : '-',
        igHandle: ig?.handle || '-',
        igLink: ig?.url || '-',
        igFans: ig?.followerCount || 0,
        ttHandle: tt?.handle || '-',
        ttLink: tt?.url || '-',
        ttFans: tt?.followerCount || 0,
        ytHandle: yt?.handle || '-',
        ytLink: yt?.url || '-',
        ytFans: yt?.followerCount || 0,
        tagsText: getFilteredTags(item).map((t: any) => typeof t === 'object' ? t.name : t).join(', '),
        createdAt: item.createdAt ? dayjs(item.createdAt).format('YYYY-MM-DD HH:mm') : '-',
        updatedAt: item.updatedAt ? dayjs(item.updatedAt).format('YYYY-MM-DD HH:mm') : '-',
        auditTime: item.auditTime ? dayjs(item.auditTime).format('YYYY-MM-DD HH:mm') : '-',
        latestOrderTime: item.lastSampleAt ? dayjs(item.lastSampleAt).format('YYYY-MM-DD HH:mm') : '-',
        paymentAmount: item.paymentAmount || 0,
        descriptionText: (item.description || '').replace(/Import Cost: .*/g, '').trim() || '-'
      };
    });

    if (formattedData.length === 0) {
      message.warning('没有可导出的数据');
      return;
    }

    await createExportTask({
      data: formattedData,
      columns: columns,
      filename: `红人分析报告${scope === 'selected' ? '_选中' : ''}`,
      pageType: 'influencer-list',
      templateId,
      templateName,
    });
    
    exportModalVisible.value = false;
  } catch (error) {
    console.error('导出失败:', error);
    message.error('导出失败');
  }
};

// Batch Search — 统一入口

const openBatchSearchByType = (type: BatchSearchType = 'name') => {
  batchSearchType.value = type;

  // tags 类型特殊处理：ID → 名称
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
    // 通用文本类型：从 filterForm 中取值并转换逗号为换行
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

// 保留原名导出以兼容模板中已有的调用
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

// Helpers
const getPlatformColor = (p: string) => {
    if (!p) return 'blue';
    const lower = p.toLowerCase();
    if (lower.includes('youtube')) return 'red';
    if (lower.includes('tiktok')) return 'black';
    if (lower.includes('instagram')) return 'magenta';
    return 'blue';
};

const filterOption = (input: string, option: any) => {
  return option.children ? option.children[0]?.children?.toLowerCase().indexOf(input.toLowerCase()) >= 0 : false;
};

const countryFilterOption = (input: string, option: any) => {
  const country = countries.value.find(c => c.code === option.value);
  if (!country) return false;
  return (
    country.code.toLowerCase().includes(input.toLowerCase()) || 
    (country.nameCn && country.nameCn.toLowerCase().includes(input.toLowerCase())) || 
    (country.nameEn && country.nameEn.toLowerCase().includes(input.toLowerCase()))
  );
};

const countrySearchText = ref('');
const onCountrySearch = (val: string) => {
  countrySearchText.value = val;
};

const countryFilterSort = (optionA: any, optionB: any) => {
  const keyword = countrySearchText.value.toLowerCase();
  if (!keyword) return 0;
  
  const codeA = String(optionA.value || '').toLowerCase();
  const codeB = String(optionB.value || '').toLowerCase();
  
  // Exact match
  const isExactA = codeA === keyword ? 1 : 0;
  const isExactB = codeB === keyword ? 1 : 0;
  if (isExactA !== isExactB) return isExactB - isExactA;
  
  // Prefix match
  const isPrefixA = codeA.startsWith(keyword) ? 1 : 0;
  const isPrefixB = codeB.startsWith(keyword) ? 1 : 0;
  if (isPrefixA !== isPrefixB) return isPrefixB - isPrefixA;
  
  // Name match
  const countryA = countries.value.find(c => c.code === optionA.value);
  const countryB = countries.value.find(c => c.code === optionB.value);
  
  const nameExactA = (countryA?.nameCn === keyword || countryA?.nameEn?.toLowerCase() === keyword) ? 1 : 0;
  const nameExactB = (countryB?.nameCn === keyword || countryB?.nameEn?.toLowerCase() === keyword) ? 1 : 0;
  if (nameExactA !== nameExactB) return nameExactB - nameExactA;

  return 0;
};

// --- Watchers ---
let debounceTimer: any = null;
watch(
  () => [filterForm.name, filterForm.platform, filterForm.country, filterForm.tagIds, filterForm.socialPlatform, filterForm.socialHandle, filterForm.email],
  () => {
    if (debounceTimer) clearTimeout(debounceTimer);
    debounceTimer = setTimeout(() => {
      handleFilter();
    }, 800);
  },
  { deep: true }
);



</script>

<style lang="scss" scoped>
.influencer-list-page {
  height: 100%;
  padding: 8px;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  gap: 8px;
  overflow: hidden;

  .status-pending {
  background: #fffbe6;
  color: #faad14;
  border: 1px solid #ffe58f;
}

.status-icon-badge {
  display: inline-block;
  min-width: 28px;
  padding: 2px 6px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;
  line-height: 18px;
  text-align: center;
}
.status-icon-badge.yes {
  background-color: #10b981;
  color: #ffffff;
  box-shadow: 0 2px 4px rgba(16, 185, 129, 0.2);
}
.status-icon-badge.no {
  background-color: #f1f5f9;
  color: #94a3b8;
}
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
    flex-shrink: 0;
    
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
    background: #ffffff;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.03);
    border: 1px solid #f1f5f9;
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

  .centered-tags {
    display: flex;
    justify-content: center;
    flex-wrap: wrap;
    gap: 4px;
  }

  .filter-footer-actions {
    display: flex;
    justify-content: flex-end;
    margin-top: 16px;
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

  .ai-top-list-banner {
    margin: 0;
    border-radius: 0;
    :deep(.ant-alert-message) {
      font-size: 13px;
      color: #334155;
    }
    .ai-top-list-email {
      margin-left: 4px;
      color: #64748b;
    }
  }

  .table-actions-toolbar {
    display: flex;
    justify-content: space-between;
    align-items: center;
    width: 100%;
    padding: 8px 0;

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
          border: none;
          background: transparent;
          border-radius: 7px;
          height: 28px;
          line-height: 28px;
          font-weight: 500;
          color: #64748b;
          font-size: 12px;
          padding: 0 12px;
          transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
          letter-spacing: 0.01em;

          &:before { display: none; }
          &:hover { color: #334155; }

          &.ant-radio-button-wrapper-checked {
            background: #ffffff;
            color: #1e293b;
            border: none;
            box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1), 0 1px 2px rgba(0, 0, 0, 0.06);
            font-weight: 600;
          }
        }
      }
    }

    .toolbar-btns {
      display: flex;
      align-items: center;
      gap: 6px;

      :deep(.ant-space-item) {
        line-height: 1;
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

    /* Fixed Column Background Fix */
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

  /* User Info Cell */
  .user-info-cell {
    display: flex;
    align-items: center;
    gap: 12px;
    min-width: 0; 
    
    .avatar-wrapper {
      position: relative;
      flex-shrink: 0;
      
      .premium-avatar {
        border: none;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        font-size: 20px;
        font-weight: 800;
        transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
        
        &:hover {
          transform: scale(1.1) translateY(-2px);
          box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
        }
      }
    }
    
    .info-content {
      min-width: 0;
      flex: 1;
      .user-name {
        font-weight: 800;
        color: #0f172a;
        font-size: 16px;
        margin-bottom: 4px;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        line-height: 1.4;
      }
      .user-id {
        color: #64748b;
        font-size: 13px;
        font-family: 'JetBrains Mono', 'Courier New', Courier, monospace;
      }
    }
  }

  /* Social Media Cell */
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

  /* Platform Tags */
  .platform-tags {
    .status-tag {
      border-radius: 20px;
      padding: 2px 12px;
      font-weight: 600;
      font-size: 11px;
      letter-spacing: 0.5px;
      border: none;
      
      &.tag-black { background: #0f172a; color: #fff; }
      &.tag-blue { background: #dbeafe; color: #1e40af; }
      &.tag-red { background: #fee2e2; color: #b91c1c; }
      &.tag-pink { background: #fdf2f8; color: #9d174d; }
    }
  }

  /* Home Page Link Optimization */
  .homepage-link-cell {
    .link-actions-compact {
      display: flex;
      gap: 4px;
      justify-content: center;
      
      .micro-action-btn {
        height: 22px;
        padding: 0 8px;
        font-size: 11px;
        border-radius: 4px;
        
        &.go-btn {
          background: #f0f7ff;
          color: #1890ff;
          border-color: #91d5ff;
          &:hover { background: #1890ff; color: #fff; }
        }
        &.copy-btn {
          color: #64748b;
          background: #f8fafc;
          &:hover { border-color: #1890ff; color: #1890ff; }
        }
      }
    }
  }

  /* Action Buttons - 2x2 grid layout */
  .action-btns-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    grid-template-rows: auto auto;
    gap: 6px 8px;
    width: 132px;
    .grid-btn {
      width: 100%;
      height: 28px;
      padding: 0 6px;
      font-size: 12px;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      border-radius: 6px;
    }
    .placeholder-btn {
      height: 28px;
      display: block;
    }
    .delete-btn {
      font-weight: 600;
      background: linear-gradient(135deg, #f87171 0%, #dc2626 100%);
      border: none !important;
      color: #fff !important;
      box-shadow: 0 2px 4px rgba(220, 38, 38, 0.2);
      &:hover {
        background: linear-gradient(135deg, #fb923c 0%, #ef4444 100%);
        color: #fff !important;
      }
    }
    .detail-btn {
      font-weight: 600;
      background: linear-gradient(135deg, #60a5fa 0%, #2563eb 100%);
      border: none;
      color: #fff;
      box-shadow: 0 2px 4px rgba(37, 99, 235, 0.2);
      &:hover {
        background: linear-gradient(135deg, #93c5fd 0%, #3b82f6 100%);
      }
    }
    .transfer-btn {
      color: #475569;
      font-weight: 500;
      border: 1px solid #e2e8f0;
      background: #f8fafc;
      gap: 4px;
      &:hover {
        color: #3b82f6;
        border-color: #3b82f6;
        background: #fff;
      }
    }
    .invite-btn {
      font-weight: 500;
      color: #0f766e;
      background: #ecfdf5;
      border: 1px solid #a7f3d0;
      &:hover {
        color: #fff;
        background: #0f766e;
        border-color: #0f766e;
      }
      &:disabled {
        color: #94a3b8;
        background: #f1f5f9;
        border-color: #e2e8f0;
        cursor: not-allowed;
      }
    }
  }

  /* Action Buttons (legacy wrapper kept for compatibility) */
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

    .invite-btn {
      border-radius: 6px;
      height: 28px;
      padding: 0 10px;
      font-size: 12px;
      font-weight: 500;
      color: #0f766e;
      background: #ecfdf5;
      border: 1px solid #a7f3d0;
      transition: all 0.2s;
      &:hover {
        color: #fff;
        background: #0f766e;
        border-color: #0f766e;
        box-shadow: 0 2px 8px rgba(15, 118, 110, 0.25);
      }
      &:disabled {
        color: #94a3b8;
        background: #f1f5f9;
        border-color: #e2e8f0;
        cursor: not-allowed;
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

  /* Batch Search Modal Styles */
  .batch-search-tip {
    font-size: 12px;
    color: #94a3b8;
    margin-bottom: 12px;
    padding: 8px 12px;
    background: #f8fafc;
    border-radius: 6px;
    border-left: 3px solid #1890ff;
  }

  .premium-textarea {
    border-radius: 12px;
    border: 1px solid #e2e8f0;
    font-family: 'Inter', system-ui, -apple-system, sans-serif;
    font-size: 14px;
    padding: 12px;
    background: #fafbfc;
    transition: all 0.3s;
    &:hover { border-color: #1890ff; background: #fff; }
    &:focus {
      border-color: #1890ff;
      background: #fff;
      box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.1);
    }
  }

  /* Premium Menu Styles for Dropdowns */
  :deep(.ant-dropdown-menu),
  .premium-menu {
    border-radius: 10px;
    padding: 6px;
    box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
    border: 1px solid rgba(0, 0, 0, 0.05);

    .ant-dropdown-menu-item {
      border-radius: 6px;
      margin-bottom: 2px;
      color: #475569;
      font-weight: 500;
      padding: 8px 12px;
      transition: all 0.2s;

      &:last-child { margin-bottom: 0; }

      &:hover {
        background: #f1f5f9;
        color: #1890ff;
      }

      &.ant-dropdown-menu-item-active {
        background: #e6f7ff;
        color: #1890ff;
      }
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
  /* Premium Time Filter Group */
  .premium-time-filter-group {
    display: flex;
    align-items: center;
    background: #ffffff; /* 改为纯白，与背景形成对比 */
    border: 1px solid #d1d5db; /* 稍深一点的边框 */
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
        &::placeholder { color: #94a3b8; }
      }
      
      :deep(.ant-picker-range-separator) {
        padding: 0 4px;
        color: #cbd5e1;
      }
    }
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

/* Action Buttons Grid */
.action-btns-grid {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: nowrap;
}

.grid-btn {
  border-radius: 8px;
  font-weight: 600;
  font-size: 12px;
  height: 30px;
  padding: 0 12px;
  border: none;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.detail-btn {
  background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
  color: #fff;
  &:hover {
    transform: translateY(-1px);
    box-shadow: 0 4px 10px rgba(37, 99, 235, 0.3);
  }
}

.more-btn {
  background: #f1f5f9;
  color: #475569;
  &:hover {
    background: #e2e8f0;
    color: #1e293b;
    transform: translateY(-1px);
    box-shadow: 0 4px 8px rgba(0,0,0,0.05);
  }
}

/* Premium Compact Buttons */
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
  height: 28px !important;
  padding: 0 12px !important;
  font-size: 12px !important;
  border-radius: 6px !important;
  display: inline-flex !important;
  align-items: center;
  justify-content: center;
  font-weight: 500;
  gap: 4px;
  line-height: 1;
  border: 1px solid #e2e8f0 !important;
  color: #475569 !important;
  background: #ffffff !important;
  transition: all 0.2s ease !important;
  box-shadow: none !important;

  .anticon { font-size: 12px; }

  &:hover {
    color: #1890ff !important;
    border-color: #91caff !important;
    background: #f0f7ff !important;
  }
}

.transfer-btn {
  height: 28px !important;
  padding: 0 12px !important;
  font-size: 12px !important;
  border-radius: 6px !important;
  display: inline-flex !important;
  align-items: center;
  justify-content: center;
  font-weight: 500;
  gap: 4px;
  line-height: 1;
  border: 1px solid #e2e8f0 !important;
  color: #475569 !important;
  background: #ffffff !important;
  transition: all 0.2s ease !important;
  box-shadow: none !important;

  &:hover {
    color: #1890ff !important;
    border-color: #91caff !important;
    background: #f0f7ff !important;
  }
  &:disabled {
    border-color: #f1f5f9 !important;
    color: #cbd5e1 !important;
    background: #fafbfc !important;
    &:hover {
      border-color: #f1f5f9 !important;
      color: #cbd5e1 !important;
      background: #fafbfc !important;
    }
  }
}

.primary-gradient {
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%) !important;
  border: none !important;
  color: #ffffff !important;
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.35) !important;
  font-weight: 600 !important;
  letter-spacing: 0.02em;

  .anticon { font-size: 13px; }

  &:hover {
    background: linear-gradient(135deg, #40a9ff 0%, #1890ff 100%) !important;
    box-shadow: 0 4px 14px rgba(24, 144, 255, 0.45) !important;
    color: #ffffff !important;
    border-color: transparent !important;
  }
}

.premium-input, .premium-select, .premium-input-search, .premium-select-left, .premium-datepicker-right {
  /* Inner input cleanup to prevent double borders */
  &.ant-input-affix-wrapper,
  &.ant-picker,
  :deep(.ant-input-affix-wrapper),
  :deep(.ant-picker) {
    border-radius: 8px !important;
    border-color: #e2e8f0 !important;
    height: 32px !important;
    padding: 0 12px !important;
    background: #fafbfc !important;
    transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
    display: flex;
    align-items: center;
    
    &:hover { border-color: #cbd5e1 !important; background: #fff !important; }
    
    &.ant-input-affix-wrapper-focused, &.ant-picker-focused { 
      border-color: #1890ff !important; 
      box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.06) !important;
      background: #ffffff !important;
    }
    
    .ant-input, .ant-picker-input > input { 
      font-size: 13px !important; 
      background: transparent !important; 
      border: none !important;
      box-shadow: none !important;
      height: 28px !important;
      &:focus { border: none !important; box-shadow: none !important; }
    }
  }

  &.ant-input,
  &:not(.ant-input-affix-wrapper):not(.ant-picker) :deep(.ant-input), 
  :deep(.ant-select-selector) {
    border-radius: 8px !important;
    border-color: #e2e8f0 !important;
    height: 32px !important;
    display: flex;
    align-items: center;
    font-size: 13px !important;
    padding: 0 12px !important;
    background: #fafbfc !important;
    transition: all 0.2s;
    
    &:hover { border-color: #cbd5e1 !important; background: #fff !important; }
    
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
}

.premium-input-search, .premium-input {
  :deep(.ant-input-prefix) {
    margin-right: 8px;
    color: #94a3b8;
  }
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


/* Avatar Styling */
.premium-avatar {
  border: 2px solid #fff;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);
  font-weight: 800;
  color: #fff !important;
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

.premium-datepicker-right {
  border-radius: 0 8px 8px 0 !important;
  border-left: none !important;
}
.premium-select-left {
  :deep(.ant-select-selector) {
    border-radius: 8px 0 0 8px !important;
    border-right: none !important;
  }
}

:deep(.transfer-row-leave) {
  transform: translateX(100px);
  opacity: 0;
  transition: all 0.5s ease-in-out;
  pointer-events: none;
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

.fans-range-group {
  display: flex !important;
  flex-wrap: nowrap !important;
  align-items: center;
  width: 100%;
  
  :deep(.ant-input-number), :deep(.ant-input) {
    min-width: 0; /* Prevent flex items from overflowing */
  }
}

.collapsed-filter-row {
  flex-wrap: nowrap !important;
  overflow: hidden;
}
/* Clean up redundant action-btn-danger styles */
.action-btn-danger-text {
  padding: 4px 10px;
  border-radius: 6px;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
  display: inline-flex;
  align-items: center;
  font-weight: 500;
  color: #94a3b8 !important;
  font-size: 12px;
}

/* 性能优化：为海量数据的表格行启动浏览器级虚拟挂载 */
:deep(.ant-table-tbody > tr.ant-table-row) {
  content-visibility: auto;
  contain-intrinsic-size: auto 80px; /* 约等于一行的高度，保证滚动条平滑 */
}
</style>

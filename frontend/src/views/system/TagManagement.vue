<template>
  <div class="tag-management-page">
    <!-- 筛选区域 -->
    <a-card :bordered="false" class="filter-card glass-card" :body-style="{ padding: '16px 20px' }">
      <a-form :model="filterForm" layout="vertical">
        <a-row :gutter="[16, 16]">
          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <a-form-item label="标签名称">
              <a-input v-model:value="filterForm.name" placeholder="请输入标签名称" allow-clear />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <a-form-item label="创建时间">
              <a-range-picker v-model:value="filterForm.timeRange" format="YYYY-MM-DD" style="width: 100%" />
            </a-form-item>
          </a-col>
          <a-col :xs="24" :sm="12" :md="8" :lg="6">
            <div class="filter-actions">
              <a-space>
                <a-button type="primary" @click="handleFilter" class="primary-gradient">查询</a-button>
                <a-button @click="handleResetFilter">重置</a-button>
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
              <a-radio-button value="influencer">红人标签</a-radio-button>
              <a-radio-button value="content">内容标签</a-radio-button>
              <a-radio-button value="reason">原因标签</a-radio-button>
              <a-radio-button value="platform">平台管理</a-radio-button>
              <a-radio-button value="type">红人类型</a-radio-button>
              <a-radio-button value="country">国家管理</a-radio-button>
              <a-radio-button value="province">省份维护</a-radio-button>
              <a-radio-button value="liaison">联络员管理</a-radio-button>
              <a-radio-button value="payment_type">付费类型</a-radio-button>
            </a-radio-group>
          </div>
          <div class="toolbar-btns">

            <a-button 
              v-if="activeKey === 'influencer' || activeKey === 'type'"
              type="default" 
              @click="handleRepairTags" 
              :loading="repairLoading"
              style="margin-right: 8px"
            >
              <template #icon><sync-outlined :spin="repairLoading" /></template>
              补全缺失标签
            </a-button>
            <a-button 
              v-if="activeKey !== 'province'"
              type="primary" 
              @click="showModal()" 
              class="primary-gradient" 
              v-permission="getAddPermission()"
            >
              <template #icon><plus-outlined /></template>
              新增标签
            </a-button>
          </div>
        </div>
      </template>

      <a-table
        :columns="columns"
        :data-source="pagedData"
        :row-key="(record: any) => record.key ?? record.id"
        :pagination="false"
        :loading="loading || provinceLoading"
        :scroll="tableScroll"
        class="premium-table"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'name'">
            <div class="tag-preview-cell">
               <a-tag 
                 class="custom-tag"
                 :style="{ 
                   backgroundColor: record.backgroundColor, 
                   borderColor: record.borderColor, 
                   color: record.textColor 
                 }"
               >
                 {{ record.name }}
               </a-tag>
               <span class="tag-desc" v-if="record.desc">{{ record.desc }}</span>
            </div>
          </template>
          
          <template v-else-if="column.key === 'creator'">
            <div class="user-info-cell">
              <a-avatar size="small" class="mini-avatar" :style="{ backgroundColor: getCreatorColor(getUserName(record.creator)) }">{{ getUserName(record.creator).charAt(0) }}</a-avatar>
              <div class="info-right">
                 <span class="name">{{ getUserName(record.creator) }}</span>
                 <span class="time">{{ record.createTime }}</span>
              </div>
            </div>
          </template>

          <template v-else-if="column.key === 'count'">
             <div class="count-badge">
                <span class="count-num">{{ record.count }}</span>
                <span class="count-label">关联</span>
             </div>
          </template>

          <template v-else-if="column.key === 'countryNameCombined'">
             <div class="country-cell">
               <div style="font-weight: 500; font-size: 13px;">{{ record.nameCn }}</div>
               <div style="font-size: 11px; color: #94a3b8;">{{ record.nameEn }}</div>
             </div>
          </template>

          <template v-else-if="column.key === 'taxInfo'">
             <div class="tax-info-cell">
                <div v-if="record.taxName">{{ record.taxName }}</div>
                <div v-if="record.taxPercentage !== null" class="tax-tag">{{ record.taxPercentage }}%</div>
             </div>
          </template>

          <template v-else-if="column.key === 'status'">
             <a-tag :color="record.enabled ? 'success' : 'default'">
                {{ record.enabled ? '已启用' : '已禁用' }}
             </a-tag>
          </template>

          <template v-else-if="column.key === 'action'">
            <a-space :size="8">
              <a-button type="text" size="small" @click="showModal(record)" class="action-btn edit" v-permission="getEditPermission()">
                编辑
              </a-button>
              <a-button v-if="activeKey !== 'province'" type="text" size="small" @click="handleDelete(record)" class="action-btn delete" v-permission="getDeletePermission()">
                删除
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
      
      <!-- 自定义底部页脚 -->
      <div class="pagination-footer">
        <div class="footer-left">
          <span class="info-item">当前内容数量 <span class="count-value">{{ filteredData.length }}</span></span>
        </div>
        <div class="footer-right">
          <a-pagination
            v-model:current="pagination.current"
            v-model:pageSize="pagination.pageSize"
            :total="filteredData.length"
            :show-size-changer="true"
            :show-quick-jumper="true"
            @change="onPageChange"
          />
        </div>
      </div>
    </a-card>

    <!-- 弹窗 -->
    <TagEditModal
      v-if="visible"
      v-model:open="visible"
      :record="currentRecord"
      @ok="handleOk"
    />

    <!-- Delete Confirmation Modal (Premium Heavy Operation Style) -->
    <a-modal
      v-model:open="deleteModalOpen"
      :title="null"
      :footer="null"
      :closable="false"
      width="480px"
      centered
      class="premium-delete-modal"
    >
      <div class="delete-modal-content">
        <!-- Header -->
        <div class="dm-header">
           <div class="dm-icon-wrapper">
             <ExclamationCircleFilled />
           </div>
           <div class="dm-title">删除标签确认</div>
           <div class="dm-subtitle">
             此操作为高风险操作，请谨慎处理
           </div>
        </div>

        <!-- Body -->
        <div class="dm-body">
           <div class="warning-text">
             您确定要永久删除标签 <span class="highlight-name">{{ deleteTarget?.name }}</span> 吗？
           </div>
           <div class="sub-warning">
             该标签当前已关联 <span class="count">{{ deleteTarget?.count || 0 }}</span> 条数据。<br>
             删除后，相关数据将<span class="danger-text">立即失去</span>该标签关联<br>且操作<span class="danger-text">无法恢复</span>。
           </div>
        </div>

        <!-- Footer -->
        <div class="dm-footer">
           <a-button class="cancel-btn" @click="deleteModalOpen = false">取消</a-button>
           <a-button 
             type="primary" 
             class="delete-btn" 
             :loading="deleteLoading" 
             @click="handleDeleteConfirm"
           >
             确认永久删除
           </a-button>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, watch } from 'vue';
import { PlusOutlined, ExclamationCircleFilled } from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import type { TableColumnsType } from 'ant-design-vue';
import dayjs from 'dayjs';
import TagEditModal from '@/components/system/TagEditModal.vue';
import { getAllCountries, createCountry, updateCountry, deleteCountry as deleteCountryApi, getAllProvinces, type Country, type Province } from '@/services/countryService';

import { SyncOutlined } from '@ant-design/icons-vue';
import tagService, { type SystemTag } from '@/services/tagService';
import { getUserList, type User } from '@/services/userService';

// 基础定义
const activeKey = ref('influencer');
const visible = ref(false);
const loading = ref(false);
const currentRecord = ref<any>(null);
const repairLoading = ref(false);

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 20,
});

const filterForm = reactive({
  name: '',
  timeRange: undefined as [any, any] | undefined,
});

const columns = computed<TableColumnsType>(() => {
  if (activeKey.value === 'province') {
    return [
      { title: '省份代码', key: 'code', dataIndex: 'code', width: 120, fixed: 'left' },
      { title: '所属国家', key: 'countryInfo', dataIndex: 'countryInfo', width: 180 },
      { title: '省份名称', key: 'name', dataIndex: 'name', width: 200 },
      { title: '状态', key: 'status', width: 100, align: 'center' },
      { title: '操作', key: 'action', width: 150, fixed: 'right', align: 'center' },
    ];
  } else if (activeKey.value === 'country') {
      return [
      { title: '国家代码', key: 'code', dataIndex: 'code', width: 100, fixed: 'left' },
      { title: '国家名称 (中/英)', key: 'countryNameCombined', width: 220 },
      { title: '电话代码', key: 'phonePrefix', dataIndex: 'phonePrefix', width: 120 },
      { title: '时差', key: 'timezone', dataIndex: 'timezone', width: 100 },
      { title: '状态', key: 'status', width: 100, align: 'center' },
      { title: '操作', key: 'action', width: 150, fixed: 'right', align: 'center' },
    ];
  }
  return [
    { title: '标签信息', key: 'name', width: 280, fixed: 'left' },
    { title: '创建人 / 时间', key: 'creator', width: 220 },
    { title: '关联统计', key: 'count', width: 140, align: 'center' },
    { title: '操作', key: 'action', width: 150, fixed: 'right', align: 'center' },
  ];
});

const tableScroll = computed(() => ({
  y: 'calc(100vh - 300px)',
  x: 'max-content'
}));

// 标签数据 (从 API 加载)
const influencerTags = ref<any[]>([]);
const contentTags = ref<any[]>([]);
const platforms = ref<any[]>([]);
const influencerTypes = ref<any[]>([]);
const reasonTags = ref<any[]>([]);
const liaisonTags = ref<any[]>([]);
const paymentTypeTags = ref<any[]>([]);

// 类别映射
const CATEGORY_MAP: Record<string, string> = {
  'influencer': 'INFLUENCER',
  'content': 'CONTENT',
  'reason': 'REASON',
  'platform': 'PLATFORM',
  'type': 'INFLUENCER_TYPE',
  'liaison': 'LIAISON',
  'payment_type': 'PAYMENT_TYPE',
  'country': 'COUNTRY', // Add country if needed, though strictly not used by loadTags but good to have
};

// 加载标签数据
const loadTags = async (tabKey: string) => {
  const category = CATEGORY_MAP[tabKey];
  if (!category) return;
  
  loading.value = true;
  try {
    const tags = await tagService.getTagsByCategory(category);
    const formattedTags = tags.map((tag: SystemTag) => ({
      key: tag.id,
      id: tag.id,
      name: tag.name,
      backgroundColor: tag.backgroundColor,
      borderColor: tag.borderColor,
      textColor: tag.textColor,
      desc: tag.description || '',
      count: tag.usageCount ?? tag.count ?? 0,
      creator: tag.createdBy, // Store raw ID
      createTime: tag.createdAt || dayjs().format('YYYY-MM-DD HH:mm:ss'),
      enabled: tag.enabled,
      sortOrder: tag.sortOrder,
    }));
    
    // 根据当前 tab 更新对应数据
    switch (tabKey) {
      case 'influencer': influencerTags.value = formattedTags; break;
      case 'content': contentTags.value = formattedTags; break;
      case 'platform': platforms.value = formattedTags; break;
      case 'type': influencerTypes.value = formattedTags; break;
      case 'reason': reasonTags.value = formattedTags; break;
      case 'liaison': liaisonTags.value = formattedTags; break;
      case 'payment_type': paymentTypeTags.value = formattedTags; break;
    }
  } catch (e: any) {
    console.error('Failed to load tags:', e);
    message.error('加载标签失败');
  } finally {
    loading.value = false;
  }
};

// 国家数据 (从 API 加载)
const countryTags = ref<any[]>([]);
const countryLoading = ref(false);

// 加载国家数据
const loadCountries = async () => {
  countryLoading.value = true;
  try {
    const countries = await getAllCountries();
    countryTags.value = countries.map((c: Country, index: number) => ({
      key: c.id,
      id: c.id,
      name: `${c.nameCn} (${c.code})`,
      countryNameCombined: `${c.nameCn} / ${c.nameEn}`,
      code: c.code,
      nameCn: c.nameCn,
      nameEn: c.nameEn,
      phonePrefix: c.phonePrefix,
      timezone: c.timezone,
      sortOrder: c.sortOrder,
      enabled: c.enabled,
      backgroundColor: c.enabled ? '#e6f7ff' : '#f5f5f5',
      borderColor: c.enabled ? '#91d5ff' : '#d9d9d9',
      textColor: c.enabled ? '#1890ff' : '#999999',
      desc: c.nameEn || '',
      count: 0,
      creator: 'System',
      createTime: dayjs().format('YYYY-MM-DD HH:mm:ss'),
    }));
  } catch (error) {
    console.error('Failed to load countries:', error);
    message.error('加载国家列表失败');
  } finally {
    countryLoading.value = false;
  }
};

// 省份数据
const provinceTags = ref<any[]>([]);
const provinceLoading = ref(false);
const syncLoading = ref(false);

const loadProvinces = async () => {
  provinceLoading.value = true;
  try {
    const provinces = await getAllProvinces();
    provinceTags.value = provinces.map((p: any) => ({
      key: p.id,
      id: p.id,
      name: p.provinceName,
      code: p.provinceCode,
      countryName: p.countryName,
      countryCode: p.countryCode,
      countryInfo: `${p.countryName} (${p.countryCode})`,
      taxName: p.taxName,
      taxPercentage: p.taxPercentage,
      enabled: p.enabled,
      // Style
      backgroundColor: p.enabled ? '#fff7e6' : '#f5f5f5',
      borderColor: p.enabled ? '#ffd591' : '#d9d9d9',
      textColor: p.enabled ? '#fa8c16' : '#999999',
    }));
  } catch (err) {
    console.error(err);
    message.error('Load provinces failed');
  } finally {
    provinceLoading.value = false;
  }
};

// 用户数据（从公共 Store 获取，避免重复请求）
import { useCommonDataStore } from '@/stores/commonData';
const commonStore = useCommonDataStore();
const userMap = computed(() => commonStore.userNameMap);

const loadUsers = async () => {
  await commonStore.loadUsers();
};


const getUserName = (id: string | number) => {
  if (!id) return 'System';
  return userMap.value[Number(id)] || (id === 'System' ? 'System' : `User:${id}`);
};



const currentData = computed(() => {
  switch (activeKey.value) {
    case 'influencer': return influencerTags.value;
    case 'content': return contentTags.value;
    case 'platform': return platforms.value;
    case 'type': return influencerTypes.value;
    case 'reason': return reasonTags.value;
    case 'liaison': return liaisonTags.value;
    case 'payment_type': return paymentTypeTags.value;
    case 'country': return countryTags.value;
    case 'province': return provinceTags.value;
    default: return influencerTags.value;
  }
});

const filteredData = computed(() => {
  let res = [...currentData.value];
  if (filterForm.name) {
    res = res.filter(i => (i.name || '').toLowerCase().includes(filterForm.name.toLowerCase()));
  }
  // 时间范围筛选
  if (filterForm.timeRange && filterForm.timeRange.length === 2) {
    const startDate = dayjs(filterForm.timeRange[0]).startOf('day');
    const endDate = dayjs(filterForm.timeRange[1]).endOf('day');
    res = res.filter(i => {
      const itemDate = i.createdAt ? dayjs(i.createdAt) : null;
      if (!itemDate || !itemDate.isValid()) return true;
      return !itemDate.isBefore(startDate) && !itemDate.isAfter(endDate);
    });
  }
  return res;
});

// 分页数据
const pagedData = computed(() => {
  const start = (pagination.current - 1) * pagination.pageSize;
  const end = start + pagination.pageSize;
  return filteredData.value.slice(start, end);
});

// 方法
const handleTabChange = () => {
  pagination.current = 1;
  if (activeKey.value === 'country') {
    loadCountries();
  } else if (activeKey.value === 'province') {
    loadProvinces();
  } else {
    // 加载标签数据
    loadTags(activeKey.value);
  }
};

// 获取当前用户名称
const getCurrentUserName = () => {
  const userInfoRaw = localStorage.getItem('user_info');
  const userInfo = JSON.parse(userInfoRaw || '{}');
  return userInfo.username || userInfo.realName || 'System';
};

const handleFilter = () => {
  pagination.current = 1;
  handleTabChange();
};

const handleResetFilter = () => {
  filterForm.name = '';
  filterForm.timeRange = undefined;
  handleFilter();
};

const onPageChange = (page: number, pageSize: number) => {
  pagination.current = page;
  pagination.pageSize = pageSize;
};

const handleTableChange = (pag: any) => {
  pagination.current = pag.current;
  pagination.pageSize = pag.pageSize;
};

const showModal = (record?: any) => {
  currentRecord.value = record || null;
  visible.value = true;
};

const handleOk = async (formData: any) => {
  visible.value = false;
  
  if (activeKey.value === 'country') {
    try {
      const countryData = {
        nameCn: formData.name,
        code: formData.desc || formData.name,
        ...formData
      };
      if (formData.isEdit && formData.id) {
        await updateCountry(formData.id, countryData);
        message.success('更新成功');
      } else {
        await createCountry(countryData);
        message.success('创建成功');
      }
      loadCountries();
      return;
    } catch (e: any) {
      message.error((formData.isEdit ? '更新' : '创建') + '失败: ' + (e.response?.data?.message || e.message || '未知错误'));
      return;
    }
  }
  
  if (activeKey.value === 'province') {
    message.warning('省份编辑暂未支持 API 调用');
    return;
  }
  
  // 标签管理调用真实 API
  const category = CATEGORY_MAP[activeKey.value];
  if (!category) return;
  
  try {
    if (formData.isEdit && formData.id) {
      // 更新标签 — 检查是否与其他标签同名
      const duplicate = currentData.value.find(
        (t: any) => t.name === formData.name && t.id !== formData.id
      );
      if (duplicate) {
        message.warning(`当前类别下已存在同名标签「${formData.name}」，请更换名称`);
        return;
      }

      const currentUserName = getCurrentUserName();
      await tagService.updateTag(formData.id, {
        category,
        name: formData.name,
        description: formData.desc,
        backgroundColor: formData.backgroundColor,
        borderColor: formData.borderColor,
        textColor: formData.textColor,
        updatedBy: currentUserName,
      });
      message.success('更新成功');
    } else {
      // 创建标签 — 前端去重检查
      const duplicate = currentData.value.find(
        (t: any) => t.name === formData.name
      );
      if (duplicate) {
        message.warning(`当前类别下已存在同名标签「${formData.name}」，请勿重复创建`);
        return;
      }

      const currentUserName = getCurrentUserName();
      await tagService.createTag({
        category,
        name: formData.name,
        description: formData.desc,
        backgroundColor: formData.backgroundColor,
        borderColor: formData.borderColor,
        textColor: formData.textColor,
        createdBy: currentUserName,
      });
      message.success('创建成功');
    }
    // 刷新数据
    await loadTags(activeKey.value);
  } catch (e: any) {
    message.error((formData.isEdit ? '更新' : '创建') + '失败: ' + (e.response?.data?.message || e.message || '未知错误'));
  }
};

const handleRepairTags = async () => {
  repairLoading.value = true;
  try {
    await tagService.repairTags();
    message.success('标签补全成功');
    loadTags(activeKey.value);
  } catch (e: any) {
    message.error('补全失败: ' + (e.response?.data?.message || e.message || '未知错误'));
  } finally {
    repairLoading.value = false;
  }
};

const getCreatorColor = (name: string) => {
  const colors = ['#3b82f6', '#8b5cf6', '#ec4899', '#f43f5e', '#f59e0b', '#10b981'];
  if(!name) return colors[0];
  let hash = 0;
  for (let i = 0; i < name.length; i++) hash = name.charCodeAt(i) + ((hash << 5) - hash);
  return colors[Math.abs(hash) % colors.length];
};

// --- Delete Logic ---
const deleteModalOpen = ref(false);
const deleteTarget = ref<any>(null);
const deleteLoading = ref(false);

const handleDelete = (record: any) => {
  deleteTarget.value = record;
  deleteModalOpen.value = true;
};

const handleDeleteConfirm = async () => {
  if (!deleteTarget.value?.id) return;
  
  deleteLoading.value = true;
  
  // 如果是国家管理
  if (activeKey.value === 'country') {
    try {
      await deleteCountryApi(deleteTarget.value.id);
      message.success('删除成功');
      deleteModalOpen.value = false;
      loadCountries();
    } catch (e: any) {
      message.error('删除失败: ' + (e.response?.data?.message || e.message || '未知错误'));
    } finally {
      deleteLoading.value = false;
    }
    return;
  }

  if (activeKey.value === 'province') {
    deleteLoading.value = false;
    deleteModalOpen.value = false;
    message.warning('省份暂不支持通过此界面删除');
    return;
  }
  
  try {
    await tagService.deleteTag(deleteTarget.value.id);
    message.success('删除成功');
    deleteModalOpen.value = false;
    // 刷新数据
    loadTags(activeKey.value);
  } catch (e: any) {
    message.error('删除失败: ' + (e.response?.data?.message || e.message || '未知错误'));
  } finally {
    deleteLoading.value = false;
  }
};

// 权限逻辑
const getAddPermission = () => {
  const map: any = { 'influencer': 'system.tag.influencer.add', 'content': 'system.tag.content.add', 'reason': 'system.tag.reason.add', 'platform': 'system.tag.platform.add', 'type': 'system.tag.influencer_type.add', 'country': 'system.tag.country.add', 'liaison': 'system.tag.liaison.add' };
  return map[activeKey.value] || 'system.tag.influencer.add';
};
const getEditPermission = () => {
  const map: any = { 'influencer': 'system.tag.influencer.edit', 'content': 'system.tag.content.edit', 'reason': 'system.tag.reason.edit', 'platform': 'system.tag.platform.edit', 'type': 'system.tag.influencer_type.edit', 'country': 'system.tag.country.edit', 'liaison': 'system.tag.liaison.edit' };
  return map[activeKey.value] || 'system.tag.influencer.edit';
};
const getDeletePermission = () => {
  const map: any = { 
    'influencer': 'system.tag.influencer.delete', 
    'content': 'system.tag.content.delete', 
    'reason': 'system.tag.reason.delete', 
    'platform': 'system.tag.platform.delete', 
    'type': 'system.tag.influencer_type.delete', 
    'country': 'system.tag.country.delete',
    'liaison': 'system.tag.liaison.delete',
    'payment_type': 'system.tag.payment_type.delete',
    'province': 'system.tag.province.delete' 
  };
  return map[activeKey.value] || 'system.tag.influencer.delete';
};

onMounted(() => {
  loadUsers();
  // 加载初始标签数据
  loadTags(activeKey.value);
});
</script>

<style lang="scss" scoped>
.tag-management-page {
  height: 100%;
  padding: 8px;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  gap: 8px;

  .glass-card {
    background: rgba(255, 255, 255, 0.6);
    backdrop-filter: blur(8px);
    border: 1px solid rgba(255, 255, 255, 0.8);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);
    border-radius: 12px;
    transition: all 0.3s ease;
    &:hover { box-shadow: 0 8px 30px rgba(0, 0, 0, 0.05); }
  }

  .filter-card {
    flex-shrink: 0;
    :deep(.ant-form-item) {
      margin-bottom: 0;
      .ant-form-item-label {
        padding: 0 !important;
        line-height: 1.2;
        > label { font-size: 12px; font-weight: 600; color: #64748b; height: 16px; margin-bottom: 4px; }
      }
    }
    .filter-actions { height: 100%; display: flex; align-items: flex-end; padding-bottom: 2px; }
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
      .ant-card-head-title { padding: 0; width: 100%; }
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
    .premium-segmented {
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
        transition: all 0.3s;
        &:before { display: none; }
        &:hover { color: #1890ff; background: #f1f5f9; }
        &.ant-radio-button-wrapper-checked {
          background: #eff6ff;
          color: #1890ff;
          border-color: #bfdbfe;
          font-weight: 600;
        }
      }
    }
  }

  .premium-table {
    flex: 1;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    :deep(.ant-spin-nested-loading), :deep(.ant-spin-container), :deep(.ant-table), :deep(.ant-table-container) {
      flex: 1; display: flex; flex-direction: column; overflow: hidden;
    }
    :deep(.ant-table-content) { overflow: auto !important; }
    :deep(.ant-table) { background: transparent; border-collapse: collapse !important; border-spacing: 0 !important; }
    :deep(.ant-table-thead > tr > th) {
      background: #f8fafc !important; padding: 10px 8px !important; font-weight: 700; color: #64748b; font-size: 13px; border-bottom: 2px solid #f1f5f9;
      z-index: 20 !important;
    }
    :deep(.ant-table-tbody > tr > td) { padding: 8px 8px !important; border-bottom: 1px solid #f8fafc; }
    :deep(.ant-table-tbody > tr:hover > td) { background: #f0f7ff !important; }
    /* Force override for measure row */
    :deep(.ant-table-tbody > tr.ant-table-measure-row > td) {
      padding: 0 !important;
      border: none !important;
      height: 0 !important;
      line-height: 0 !important;
      overflow: hidden !important;
      font-size: 0 !important;
    }
  }

  .color-config-cell {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    .color-preview {
      width: 20px; height: 20px; border-radius: 4px; border: 1px solid #e2e8f0;
      &.bg { background: transparent; }
      &.border { border-width: 2px; background: transparent; }
      &.text { color: #fff; display: flex; align-items: center; justify-content: center; font-size: 11px; font-weight: 700; }
    }
  }

  .creator-cell {
    display: flex; align-items: center; gap: 8px;
    .mini-avatar { background: #3b82f6; font-size: 12px; }
    .name { font-size: 13px; color: #475569; }
  }

  .time-cell-stacked {
    display: flex; flex-direction: column; gap: 2px;
    .time-row {
      display: flex; gap: 6px; font-size: 12px;
      .label { color: #94a3b8; flex-shrink: 0; }
      .value { color: #64748b; }
    }
  }

  .count-cell {
    .count-value { font-weight: 600; color: #1e293b; background: #f1f5f9; padding: 2px 8px; border-radius: 4px; font-size: 13px; }
  }

  /* NEW: Tag Preview Styles */
  .tag-preview-cell {
    display: flex; flex-direction: column; gap: 4px; justify-content: flex-start;
    .custom-tag {
       margin: 0; border-width: 1px; border-style: solid; align-self: flex-start;
       padding: 0 8px; border-radius: 4px; font-weight: 500; font-size: 12px; line-height: 20px;
    }
    .tag-desc { font-size: 12px; color: #94a3b8; line-height: 1.4; }
  }

  /* NEW: Merged User Info Cell */
  .user-info-cell {
    display: flex; align-items: center; gap: 10px;
    .mini-avatar { flex-shrink: 0; }
    .info-right {
       display: flex; flex-direction: column; line-height: 1.4;
       .name { font-weight: 500; color: #334155; font-size: 13px; }
       .time { font-size: 11px; color: #94a3b8; }
    }
  }

  /* NEW: Count Badge */
  .count-badge {
    display: inline-flex; align-items: center; gap: 4px;
    background: #f1f5f9; padding: 2px 8px; border-radius: 999px;
    .count-num { font-weight: 700; color: #3b82f6; font-size: 13px; }
    .count-label { font-size: 11px; color: #64748b; }
  }

  .primary-gradient {
    background: linear-gradient(135deg, #1890ff 0%, #36cfc9 100%);
    border: none; color: #fff; box-shadow: 0 4px 12px rgba(24, 144, 255, 0.2);
    &:hover { box-shadow: 0 6px 16px rgba(24, 144, 255, 0.3); transform: translateY(-1px); }
  }

  .action-btn {
    font-weight: 600;
    &.edit { color: #1890ff; &:hover { background: #eff6ff; } }
    &.delete { color: #ff4d4f; &:hover { background: #fff1f0; } }
  }

  .pagination-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 16px;
    background: #ffffff;
    border-top: 1px solid rgba(0, 0, 0, 0.04);
    .footer-left {
      color: #94a3b8; font-size: 12px;
      .count-value { font-weight: 600; color: #1e293b; background: #f1f5f9; padding: 2px 6px; border-radius: 4px; }
    }
  }
}
</style>

<!-- Non-scoped styles for Modal (since it teleports to body) -->
<style lang="scss">
.premium-delete-modal {
  .ant-modal-content {
    border-radius: 16px;
    overflow: hidden;
    padding: 0;
    box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  }
  
  .delete-modal-content {
    display: flex; flex-direction: column;
    
    .dm-header {
       padding: 24px;
       display: flex; flex-direction: column; align-items: center; justify-content: center;
       background: linear-gradient(135deg, #fee2e2 0%, #ffffff 100%);
       border-bottom: 1px solid #fecaca;
       
       .dm-icon-wrapper {
         width: 64px; height: 64px;
         background: linear-gradient(135deg, #ef4444 0%, #b91c1c 100%);
         border-radius: 50%;
         display: flex; align-items: center; justify-content: center;
         color: #fff; font-size: 32px;
         box-shadow: 0 10px 15px -3px rgba(239, 68, 68, 0.3);
         margin-bottom: 16px;
         animation: bounce 1s infinite;
       }
       
       .dm-title { font-size: 20px; font-weight: 800; color: #7f1d1d; margin-bottom: 4px; }
       .dm-subtitle { font-size: 13px; color: #991b1b; opacity: 0.8; }
    }
    
    .dm-body {
       padding: 32px 24px;
       text-align: center;
       
       .warning-text { font-size: 16px; color: #374151; margin-bottom: 12px; }
       .highlight-name { font-weight: 800; color: #111827; background: #f3f4f6; padding: 2px 8px; border-radius: 4px; }
       
       .sub-warning { 
          font-size: 13px; color: #6b7280; line-height: 1.6; background: #fef2f2; padding: 12px; border-radius: 8px; border: 1px solid #fee2e2;
          .count { font-weight: 700; color: #000; }
          .danger-text { color: #dc2626; font-weight: 700; }
       }
    }
    
    .dm-footer {
       padding: 16px 24px;
       background: #f9fafb;
       border-top: 1px solid #f3f4f6;
       display: flex; justify-content: flex-end; gap: 12px;
       
       .cancel-btn { height: 40px; border-radius: 8px; border: 1px solid #d1d5db; font-weight: 500; font-size: 14px; padding: 0 24px; }
       .delete-btn {
          height: 40px; border-radius: 8px; font-weight: 600; font-size: 14px; padding: 0 24px;
          background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%); border: none;
          box-shadow: 0 4px 6px -1px rgba(220, 38, 38, 0.3);
          &:hover { box-shadow: 0 10px 15px -3px rgba(220, 38, 38, 0.4); transform: translateY(-1px); }
       }
    }
  }
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-3px); }
}
</style>

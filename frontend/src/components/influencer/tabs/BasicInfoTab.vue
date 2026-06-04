<template>
  <div class="basic-info-tab">
    <!-- View Mode: Profile Header -->
    <div class="profile-header">
      <div class="ph-left">
        <a-avatar :size="56" class="profile-avatar" :style="{ backgroundColor: getAvatarColor(displayData.realName || displayData.name) }">
          {{ (displayData.realName || displayData.name)?.[0]?.toUpperCase() || 'U' }}
        </a-avatar>
        <div class="ph-info">
          <div class="ph-name-row">
            <span class="ph-name">{{ displayData.realName || displayData.name || '未命名红人' }}</span>
            <a-tag color="blue" class="ph-tag">{{ displayData.influencerType || '未分类' }}</a-tag>
            <span class="ph-id">ID: {{ displayData.id || '-' }}</span>
          </div>
          <div class="ph-meta-row">
            <span class="ph-meta"><GlobalOutlined /> {{ displayData.country || '未知地区' }}</span>
            <span class="ph-divider">|</span>
            <span class="ph-meta"><UserOutlined /> {{ displayData.race || '未知人种' }}</span>
          </div>
        </div>
      </div>
      <div class="ph-actions">
        <a-button @click="openEditModal" class="premium-edit-btn">
          <template #icon><EditOutlined /></template> 编辑档案
        </a-button>
      </div>
    </div>



    <!-- View Mode: Modular supplementary info -->
    <div class="info-row-modular">
      <div class="modular-grid">
        <div class="modular-item">
          <span class="lbl">联络人</span>
          <a-tag v-if="liaisonTag" color="blue" class="val-tag">{{ typeof liaisonTag === 'object' ? liaisonTag.name : liaisonTag }}</a-tag>
          <span v-else class="val">{{ displayData.contactPersonName || '-' }}</span>
        </div>
        <div class="modular-item">
          <span class="lbl">负责人</span>
          <span class="val">{{ displayData.ownerName || displayData.owner || '-' }}</span>
        </div>
        <div class="modular-item">
          <span class="lbl">合作状态</span>
          <a-tag :color="displayData.isPaid ? 'success' : 'default'" class="val-tag">{{ displayData.isPaid ? '已付费' : '未付费' }}</a-tag>
        </div>
        <div class="modular-item">
          <span class="lbl">来源</span>
          <span class="val">{{ displayData.source || '-' }}</span>
        </div>
        <div class="modular-item">
          <span class="lbl">更新时间</span>
          <span class="val time-val">{{ displayData.updatedAt || '-' }}</span>
        </div>
      </div>
      <div class="modular-tags-row">
        <span class="lbl">标签</span>
        <div class="tags-container">
          <template v-if="filteredDisplayTags?.length">
            <a-tag 
              v-for="tag in filteredDisplayTags" 
              :key="typeof tag === 'object' ? tag.id : tag"
              :style="typeof tag === 'object' ? {
                backgroundColor: tag.backgroundColor || '#e6f7ff',
                borderColor: tag.borderColor || '#91d5ff',
                color: tag.textColor || '#1890ff'
              } : {}"
              class="val-tag tag-pill"
            >{{ typeof tag === 'object' ? tag.name : tag }}</a-tag>
          </template>
          <span v-else class="empty-val">-</span>
        </div>
      </div>
    </div>

    <!-- Edit Modal (Comprehensive) -->
    <a-modal
      v-model:open="editing"
      :title="null"
      :footer="null"
      :closable="false"
      class="influencer-create-modal"
      width="880px"
      centered
      :mask-closable="false"
    >
      <!-- Custom Header -->
      <div class="ic-modal-header glass-header">
        <div class="ic-header-left">
          <div class="ic-header-icon-wrapper">
            <EditOutlined />
          </div>
          <div class="ic-header-text">
            <div class="ic-header-title">编辑红人档案</div>
            <div class="ic-header-subtitle">Edit influencer profile details (Full Edit Mode)</div>
          </div>
        </div>
        <a-button type="text" class="close-btn" @click="editing = false">
          <CloseOutlined />
        </a-button>
      </div>

      <!-- Modal Body -->
      <div class="ic-modal-body">
        <!-- Readonly Meta Banner -->
        <div class="readonly-meta-banner">
          <div class="meta-item">
            <span class="meta-label">红人 ID</span>
            <span class="meta-value">#{{ formState.id }}</span>
          </div>
          <div class="meta-divider"></div>
          <div class="meta-item">
            <span class="meta-label">红人名称</span>
            <span class="meta-value">{{ computedFullName }}</span>
          </div>
        </div>

         <!-- Section 1: Basic Info -->
        <div class="ic-section">
          <div class="ic-section-header">
            <span class="ic-section-title">基础信息 (Basic Info)</span>
          </div>
          <div class="ic-section-body">
            <a-form
              :model="formState"
              layout="horizontal"
              :colon="true"
              :label-col="{ style: { width: '76px', flex: '0 0 76px' } }"
              :wrapper-col="{ style: { flex: '1 1 0', minWidth: 0 } }"
              class="ic-form premium-form-v2 ic-form-inline"
            >
              <div class="ic-form-row">
                <a-form-item label="名字" required>
                  <a-input v-model:value="formState.firstName" placeholder="请输入名字" @blur="formState.firstName = formState.firstName.trim()" />
                </a-form-item>
                <a-form-item label="姓氏" required>
                  <a-input v-model:value="formState.lastName" placeholder="请输入姓氏" @blur="formState.lastName = formState.lastName.trim()" />
                </a-form-item>
              </div>
              <div class="ic-form-row">
                <a-form-item label="电子邮箱">
                  <a-input v-model:value="formState.email" placeholder="Email" />
                </a-form-item>
                <a-form-item label="备用邮箱">
                  <a-select
                    v-model:value="formState.auxiliaryEmails"
                    mode="tags"
                    placeholder="输入邮箱后按回车添加"
                    :token-separators="[',', ' ']"
                    style="width: 100%"
                  />
                </a-form-item>
              </div>
              <div class="ic-form-row">
                <a-form-item label="手机号码">
                  <a-input v-model:value="formState.phone" placeholder="请输入主要联系电话" />
                </a-form-item>
                <a-form-item label="红人来源">
                   <a-select v-model:value="formState.source" disabled style="width: 100%">
                     <a-select-option v-for="s in sources" :key="s" :value="s">{{ s }}</a-select-option>
                   </a-select>
                </a-form-item>
              </div>
              <div class="ic-form-row">
                <a-form-item label="负责人">
                  <a-select 
                    v-model:value="formState.ownerId" 
                    placeholder="请选择负责人"
                    allow-clear
                    show-search
                    :filter-option="(input: string, option: any) => option.label?.toLowerCase().includes(input.toLowerCase())"
                    style="width: 100%"
                  >
                    <a-select-option 
                      v-for="u in ownerUsers" 
                      :key="u.id" 
                      :value="u.id"
                      :label="u.name"
                    >
                      {{ u.name }}
                    </a-select-option>
                  </a-select>
                </a-form-item>
                <a-form-item label="联络人">
                  <a-select 
                    v-model:value="formState.contactPersonTagId" 
                    placeholder="请选择联络人"
                    allow-clear
                    show-search
                    :filter-option="(input: string, option: any) => option.label?.toLowerCase().includes(input.toLowerCase())"
                    style="width: 100%"
                  >
                    <a-select-option 
                      v-for="t in liaisonTagOptions" 
                      :key="t.id" 
                      :value="t.id"
                      :label="t.name"
                    >
                      {{ t.name }}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </div>
              <div class="ic-form-row">
                <a-form-item label="红人类型">
                   <a-select v-model:value="formState.influencerType" placeholder="请选择红人类型" style="width: 100%">
                      <a-select-option v-for="t in dynamicInfluencerTypes" :key="t" :value="t">
                        {{ t }}
                      </a-select-option>
                   </a-select>
                </a-form-item>
                <a-form-item label="人种">
                  <a-select v-model:value="formState.race" allow-clear placeholder="请选择人种" style="width: 100%">
                     <a-select-option value="White">白人 (White)</a-select-option>
                     <a-select-option value="Black / African descent">黑人 (Black / African descent)</a-select-option>
                     <a-select-option value="Asian">亚裔 (Asian)</a-select-option>
                     <a-select-option value="Hispanic / Latino">拉丁裔 (Hispanic / Latino)</a-select-option>
                     <a-select-option value="Middle Eastern">中东裔 (Middle Eastern)</a-select-option>
                     <a-select-option value="Mixed">混血 (Mixed)</a-select-option>
                     <a-select-option value="Other / Unclear">其他 (Other / Unclear)</a-select-option>
                  </a-select>
                </a-form-item>
              </div>
            </a-form>
          </div>
        </div>

        <!-- Section 3: Others -->
         <div class="ic-section">
          <div class="ic-section-header">
            <span class="ic-section-title">标签与备注 (Others)</span>
          </div>
          <div class="ic-section-body">
              <a-form
                :model="formState"
                layout="horizontal"
                :colon="true"
                :label-col="{ style: { width: '76px', flex: '0 0 76px' } }"
                :wrapper-col="{ style: { flex: '1 1 0', minWidth: 0 } }"
                class="ic-form premium-form-v2 ic-form-inline"
              >
                <div class="ic-form-row ic-form-row-single">
                  <a-form-item label="红人标签">
                    <a-select 
                      v-model:value="formState.tags" 
                      mode="multiple" 
                      placeholder="请选择标签"
                      style="width: 100%"
                    >
                      <a-select-option v-for="t in availableTags" :key="t.id" :value="t.id">{{ t.name }}</a-select-option>
                    </a-select>
                  </a-form-item>
                </div>
             </a-form>
          </div>
         </div>

      </div>

      <!-- Footer -->
       <div class="ic-modal-footer">
        <a-space size="middle">
          <a-button @click="editing = false" class="premium-btn">取消</a-button>
          <a-button type="primary" @click="handleSave" class="premium-btn primary-gradient">保存修改</a-button>
        </a-space>
      </div>

    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { 
  EditOutlined, UserOutlined, GlobalOutlined, 
  ContactsOutlined, ShopOutlined, SafetyCertificateOutlined,
   TagsOutlined, EnvironmentOutlined, CloseOutlined,
   ShareAltOutlined, UsergroupAddOutlined, ArrowRightOutlined, PlusOutlined,
   YoutubeFilled, FacebookFilled, InstagramFilled, TwitterOutlined
} from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import { SOURCES } from '@/config/constants';
import { influencerService } from '@/services/influencerService';
import { tagService, type SystemTag } from '@/services/tagService';
import { getEnabledCountries, type Country } from '@/services/countryService';
import { ref, computed, reactive, onMounted, watch } from 'vue';
import { storeToRefs } from 'pinia';
import { useCommonDataStore } from '@/stores/commonData';

// Mock TikTok Icon for now or use a generic one if not available in icon set
import { VideoCameraFilled } from '@ant-design/icons-vue'; 

const props = defineProps<{
  influencerData: any;
  defaultAddress?: {
    recipientName?: string;
    phone?: string;
    email?: string;
    address?: string;
    postalCode?: string;
    isDefault?: boolean;
  } | null;
}>();

const emit = defineEmits<{
  (e: 'refresh'): void;
}>();

const editing = ref(false);
const saving = ref(false);
const sources = [...SOURCES];
const commonStore = useCommonDataStore();
const { ownerUsers, availableTags, liaisonTagOptions, dynamicInfluencerTypes, countries } = storeToRefs(commonStore);

// Reactive Data for Display
const displayData = computed(() => props.influencerData || {});

// Find the LIAISON tag in display data tags
const liaisonTag = computed(() => {
  if (!displayData.value.tags || !Array.isArray(displayData.value.tags)) return null;
  // If tags are objects with category, we find it. 
  // But often they might just be names or IDs. 
  // For now, if we have liaisonTagOptions loaded, we can check against it.
  const liaisonIds = liaisonTagOptions.value.map(o => Number(o.id));
  return displayData.value.tags.find((t: any) => {
    const id = typeof t === 'object' ? Number(t.id) : Number(t);
    return liaisonIds.includes(id);
  });
});

// 过滤掉 LIAISON 标签，只显示红人标签
const filteredDisplayTags = computed(() => {
  if (!displayData.value.tags || !Array.isArray(displayData.value.tags)) return [];
  const liaisonIds = liaisonTagOptions.value.map(o => Number(o.id));
  return displayData.value.tags.filter((t: any) => {
    const id = typeof t === 'object' ? Number(t.id) : Number(t);
    return !liaisonIds.includes(id);
  });
});

const formState = reactive({
  id: '',
  firstName: '', // 名字
  lastName: '', // 姓氏
  realName: '', // 用于向后兼容
  email: '',
  phone: '',
  influencerType: '',
  race: '',
  source: '',
  country: '',
  address: {
    street1: '', street2: '', city: '', state: '', zip: ''
  },
  tags: [] as number[],
  ownerId: undefined as number | undefined,
  contactPersonTagId: undefined as number | undefined,
  auxiliaryEmails: [] as string[],
});

// 由 firstName + lastName 自动生成
const computedFullName = computed(() => {
  return `${formState.firstName} ${formState.lastName}`.replace(/\s+/g, ' ').trim() || '-';
});

// Sync Logic
const syncFromProps = () => {
  if (props.influencerData) {
    const d = props.influencerData;
    formState.id = d.id || '';
    // 拆分 realName 为 firstName 和 lastName
    const realNameParts = (d.realName || '').trim().split(/\s+/);
    if (realNameParts.length >= 2) {
      formState.firstName = realNameParts[0];
      formState.lastName = realNameParts.slice(1).join(' ');
    } else {
      formState.firstName = d.firstName || realNameParts[0] || '';
      formState.lastName = d.lastName || '';
    }
    formState.realName = d.realName || '';
    formState.email = d.email || '';
    formState.phone = d.phone || d.mobile || d.tel || '';
    formState.influencerType = d.influencerType || '';
    formState.race = d.race || '';
    formState.source = d.source || '';
    formState.country = d.country || '';
    // Find liaison tag among existing tags
    const liaisonIds = liaisonTagOptions.value.map(o => Number(o.id));
    const existingLiaison = d.tagIds?.find((id: any) => liaisonIds.includes(Number(id)));
    formState.contactPersonTagId = existingLiaison ? Number(existingLiaison) : undefined;
    
    // 过滤掉 LIAISON 标签，只保留红人标签在 tags 里
    formState.tags = d.tagIds
      ? d.tagIds.filter((id: any) => !liaisonIds.includes(Number(id))).map(Number)
      : [];
    formState.ownerId = d.ownerId || undefined;
    
    // 备用邮箱 - 从 influencer 实体的 backupEmail 字段读取
    const rawEmails = d.backupEmail || d.auxiliaryEmails;
    if (rawEmails) {
      try {
        const parsed = typeof rawEmails === 'string' ? JSON.parse(rawEmails) : rawEmails;
        formState.auxiliaryEmails = Array.isArray(parsed) ? parsed : [];
      } catch {
        formState.auxiliaryEmails = [];
      }
    } else {
      formState.auxiliaryEmails = [];
    }
    
    // Address (Handle possibly nested or flat)
    const addr = d.address || {};
    formState.address.street1 = addr.street1 || '';
    formState.address.street2 = addr.street2 || '';
    formState.address.city = addr.city || '';
    formState.address.state = addr.state || '';
    formState.address.zip = addr.zip || '';
  }
};

const openEditModal = () => {
  syncFromProps();
  editing.value = true;
};

const handleSave = async () => {
  if (!formState.id) {
    message.error('红人ID不存在');
    return;
  }
  saving.value = true;
  try {
    // 合并 firstName + lastName 为 realName
    const combinedRealName = `${formState.firstName.trim()} ${formState.lastName.trim()}`.replace(/\s+/g, ' ').trim();
    // Handle tag merging: 红人标签 + 联络人标签合并
    const liaisonIds = liaisonTagOptions.value.map(o => Number(o.id));
    // 先过滤掉所有旧的 LIAISON 标签，再添加新选择的
    const finalTagIds = formState.tags
      .map(t => Number(t))
      .filter(id => !liaisonIds.includes(id));
    if (formState.contactPersonTagId) {
      finalTagIds.push(Number(formState.contactPersonTagId));
    }
    
    // Final payload construction
    const updatePayload: any = {
      id: Number(formState.id),
      realName: combinedRealName,
      nickName: combinedRealName, // 与 realName 保持一致
      email: formState.email,
      phone: formState.phone,
      country: formState.country,
      source: formState.source,
      influencerType: formState.influencerType,
      race: formState.race,
      tagIds: finalTagIds,
      ownerId: formState.ownerId || null,
      auxiliaryEmails: formState.auxiliaryEmails.length > 0 ? JSON.stringify(formState.auxiliaryEmails) : null,
    };

    await influencerService.update(updatePayload);
    message.success('红人档案已保存');
    editing.value = false;
    emit('refresh'); // Notify parent to refresh data
  } catch (error) {
    console.error('保存失败:', error);
    message.error('保存失败，请重试');
  } finally {
    saving.value = false;
  }
};

// 加载逻辑已移至 commonStore

const countryOptions = ref<Country[]>([]);
const loadCountryOptions = async () => {
  try {
    const res = await getEnabledCountries();
    countryOptions.value = res;
  } catch (e) {
    console.error('Failed to load countries:', e);
  }
};

// Load tags and users on component mount
onMounted(() => {
  commonStore.loadAll();
});

// Utils
const getPlatformColor = (p: string) => {
  const colorMap: Record<string, string> = {
    tiktok: 'black', facebook: 'blue', youtube: 'red', ins: 'pink', instagram: 'purple'
  };
  return colorMap[p?.toLowerCase()] || 'blue';
};

const getPlatformIcon = (p: string) => {
  const map: Record<string, any> = {
    'tiktok': VideoCameraFilled, // Placeholder for TikTok
    'facebook': FacebookFilled,
    'youtube': YoutubeFilled,
    'instagram': InstagramFilled,
    'ins': InstagramFilled,
    'twitter': TwitterOutlined
  };
  return map[p?.toLowerCase()] || GlobalOutlined;
}

const socialAccounts = computed(() => {
  // Prefer socialMedias list from backend (support both field naming conventions)
  const list = displayData.value.socialMediaList || displayData.value.socialMedias;
  if (list && list.length > 0) {
    return list.map((sm: any) => ({
      platform: sm.platform || sm.platformName || 'Unknown',
      name: sm.handle || sm.name || sm.userName || '',
      handle: sm.url || sm.link || sm.profileLink || '', // Use URL/Link as handle for display or actual handle
      fans: sm.followerCount || sm.fansCount || sm.fans || '0'
    }));
  }
  
  // Fallback to platformData if available (from create DTO style)
  if (displayData.value.platformData && displayData.value.platformData.length > 0) {
    return displayData.value.platformData;
  }
  
  // Fallback to single platform properties if list missing
  if (displayData.value.platform) {
      return [{
          platform: displayData.value.platform,
          name: displayData.value.nickName || displayData.value.name,
          handle: displayData.value.profileLink,
          fans: displayData.value.totalFans
      }]
  }

  return [];
});

const getPlatformStyle = (p: string) => {
  const key = p?.toLowerCase();
  if (key === 'tiktok') {
    return { bg: '#f9fafb', border: '#e2e8f0', text: '#0f172a', iconBg: '#000000' };
  }
  if (key === 'youtube') {
    return { bg: '#fef2f2', border: '#fecaca', text: '#ef4444', iconBg: '#ff0000' };
  }
  if (key === 'instagram' || key === 'ins') {
    return { bg: '#fdf2f8', border: '#fbcfe8', text: '#db2777', iconBg: 'linear-gradient(45deg, #f09433, #e6683c, #dc2743, #cc2366, #bc1888, #8a3ab9)' };
  }
  if (key === 'facebook') {
    return { bg: '#eff6ff', border: '#bfdbfe', text: '#3b82f6', iconBg: '#1877f2' };
  }
  if (key === 'twitter') {
    return { bg: '#f0f9ff', border: '#bae6fd', text: '#0ea5e9', iconBg: '#1da1f2' };
  }
  return { bg: '#ffffff', border: '#e2e8f0', text: '#64748b', iconBg: '#94a3b8' };
};

// Avatar color helper for profile header
const getAvatarColor = (name: string) => {
  const colors = ['#f56a00', '#7265e6', '#ffbf00', '#00a2ae', '#87d068', '#1890ff'];
  if (!name) return colors[0];
  return colors[name.charCodeAt(0) % colors.length];
};
</script>

<style lang="scss" scoped>
.basic-info-tab {
  padding: 0 4px;
}

.profile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4px 8px;
  margin-bottom: 4px;
  
  .ph-left {
    display: flex;
    align-items: center;
    gap: 14px;
  }
  
  .ph-info { display: flex; flex-direction: column; gap: 2px; }
  
  .ph-name-row {
    display: flex; align-items: center; gap: 10px;
    .ph-name { font-size: 17px; font-weight: 700; color: #1e293b; }
    .ph-tag { border-radius: 6px; font-weight: 600; font-size: 12px; }
    .ph-id { font-size: 12px; color: #94a3b8; font-family: monospace; }
  }
  
  .ph-meta-row {
     display: flex; align-items: center; gap: 10px; font-size: 12px; color: #64748b;
     .ph-divider { color: #e2e8f0; }
  }
}

.premium-edit-btn {
  border-radius: 8px; height: 28px; font-weight: 600; font-size: 12px;
  border: 1px solid #e2e8f0; color: #475569; transition: all 0.3s;
  &:hover { color: #4f46e5; border-color: #4f46e5; transform: translateY(-1px); }
  &:active { transform: translateY(0); }
}

.info-row-modular {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 14px 16px;
  background: #f8fafc;
  border-radius: 12px;
  margin: 0 4px 8px;
  border: 1px solid #f1f5f9;

  .modular-grid {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
  }

  .modular-item {
    flex: 1;
    min-width: 160px;
    display: flex;
    align-items: center;
    justify-content: center;
    white-space: nowrap;
    gap: 8px;
    background: #ffffff;
    padding: 6px 14px;
    border-radius: 8px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 1px 2px rgba(0,0,0,0.02);
    transition: all 0.2s;
    &:hover { border-color: #cbd5e1; box-shadow: 0 2px 4px rgba(0,0,0,0.04); }
  }

  .modular-tags-row {
    display: flex;
    align-items: flex-start;
    gap: 8px;
    background: #ffffff;
    padding: 10px 14px;
    border-radius: 8px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 1px 2px rgba(0,0,0,0.02);
    
    .lbl { padding-top: 4px; }
    .tags-container { 
      display: flex; flex-wrap: wrap; gap: 6px; flex: 1;
      .empty-val { color: #94a3b8; font-size: 13px; font-weight: 500; margin-top: 3px; }
    }
  }

  .lbl {
    color: #64748b;
    font-size: 12px;
    font-weight: 600;
  }

  .val {
    color: #1e293b;
    font-weight: 700;
    font-size: 13px;
  }

  .time-val {
    font-family: 'JetBrains Mono', monospace;
    font-weight: 500;
  }
  
  .val-tag {
    margin: 0;
    font-size: 12px;
    border-radius: 6px;
  }
  
  .tag-pill {
    padding: 2px 10px;
    border-radius: 12px;
    font-weight: 600;
    box-shadow: 0 1px 2px rgba(0,0,0,0.05);
  }
}

.platform-list { display: flex; flex-wrap: wrap; gap: 6px; }
.platform-tag { border: none; font-weight: 600; padding: 0 6px; border-radius: 4px; font-size: 12px; }
.tag-youtube { background: #fee2e2; color: #dc2626; }
.tag-instagram { background: #f3e8ff; color: #9333ea; }
.tag-tiktok { background: #eee; color: #000; }
.tag-facebook { background: #e0f2fe; color: #0284c7; }

/* Social Matrix Section */
.social-matrix-section {
  margin: 0 8px 16px 8px;
  background: #ffffff;
  border: 1px solid #f1f5f9;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.01);

  /* Ensure no vertical scroll on page for social section */
  .social-scroll-container {
    display: flex;
    overflow-x: auto;
    padding-bottom: 4px; 
    gap: 12px;
    
    &::-webkit-scrollbar {
      height: 4px;
    }
    &::-webkit-scrollbar-thumb {
      background: #e2e8f0; border-radius: 2px;
    }
    &::-webkit-scrollbar-track {
      background: transparent;
    }
  }

  .social-grid {
    display: none; 
  }
}

.social-card {
  min-width: 240px;
  border: 1px solid transparent; 
  border-radius: 10px;
  padding: 12px;
  display: flex; align-items: center; gap: 12px;
  transition: all 0.3s;
  cursor: pointer;
  
  &:hover {
    box-shadow: 0 4px 12px rgba(0,0,0,0.05);
    transform: translateY(-2px);
    .visit-btn { opacity: 1; transform: translateX(0); }
  }

  .sc-icon {
    width: 40px; height: 40px; border-radius: 50%;
    display: flex; align-items: center; justify-content: center;
    font-size: 20px; color: #fff;
    flex-shrink: 0;
  }

  .sc-info {
    flex: 1;
    display: flex; flex-direction: column; gap: 2px;
    min-width: 0;
    .sc-platform { font-size: 10px; font-weight: 800; text-transform: uppercase; letter-spacing: 0.5px; } 
    .sc-handle { font-size: 13px; font-weight: 600; color: #1e293b; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
    .sc-fans { font-size: 11px; color: #64748b; display: flex; align-items: center; gap: 4px; }
  }

  .visit-btn {
    opacity: 0; transform: translateX(-5px); transition: all 0.3s; padding: 0; font-size: 12px;
  }
}

/* --- Keep Existing Modal Styles --- */

:deep(.influencer-create-modal) {
  .ant-modal {
    width: 880px !important;
    max-width: calc(100vw - 32px);
  }

  .ant-modal-content {
    padding: 0; overflow: hidden; border-radius: 16px; box-shadow: 0 20px 50px rgba(0, 0, 0, 0.15);
  }
}

.ic-modal-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 16px 24px; background: #ffffff; border-bottom: 1px solid #f1f5f9;
  
  &.glass-header { background: rgba(255, 255, 255, 0.95); }
  
  .ic-header-left { display: flex; align-items: center; gap: 16px; }
  .ic-header-icon-wrapper {
    width: 40px; height: 40px; border-radius: 10px;
    background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%); /* Blue for Edit */
    color: #ffffff; display: flex; align-items: center; justify-content: center; font-size: 18px;
    box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2);
  }
  .ic-header-title { font-size: 18px; font-weight: 700; color: #1e293b; }
  .ic-header-subtitle { font-size: 12px; color: #94a3b8; }
  .close-btn { color: #94a3b8; &:hover { color: #64748b; background: #f1f5f9; } }
}

.ic-modal-body {
  padding: 20px 24px; background: #f8fafc; max-height: 65vh; overflow-y: auto;
}

/* Premium Form Redesign V2 */
.readonly-meta-banner {
  display: flex;
  align-items: center;
  gap: 24px;
  background: linear-gradient(135deg, #eff6ff 0%, #dbeafe 100%);
  border: 1px solid #bfdbfe;
  border-radius: 12px;
  padding: 12px 20px;
  margin-bottom: 20px;
  
  .meta-item {
    display: flex;
    flex-direction: column;
    gap: 2px;
    
    .meta-label {
      font-size: 11px;
      font-weight: 700;
      color: #1e3a8a;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }
    
    .meta-value {
      font-size: 15px;
      font-weight: 700;
      color: #1e40af;
    }
  }
  
  .meta-divider {
    width: 1px;
    height: 28px;
    background: #bfdbfe;
  }
}

.ic-section {
  background: #ffffff;
  border-radius: 14px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 2px 4px -1px rgba(0, 0, 0, 0.03);
  margin-bottom: 20px;
  overflow: hidden;
  
  .ic-section-header {
    padding: 14px 20px;
    border-bottom: 1px solid #f1f5f9;
    background: #fafbfc;
    
    .ic-section-title {
      font-size: 14px;
      font-weight: 700;
      color: #1e293b;
      text-transform: uppercase;
      letter-spacing: 0.5px;
      display: flex;
      align-items: center;
      gap: 8px;
      
      &:before {
        content: "";
        display: block;
        width: 3.5px;
        height: 15px;
        background: #3b82f6;
        border-radius: 4px;
      }
    }
  }
  
  .ic-section-body {
    padding: 20px 24px;
  }
}

/* 横向：标签 + 输入框同一行，一行两个字段 */
.ic-form.premium-form-v2.ic-form-inline {
  .ic-form-row {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 14px 28px;
    margin-bottom: 16px;

    &:last-child {
      margin-bottom: 0;
    }

    &.ic-form-row-single {
      grid-template-columns: 1fr;
    }
  }

  :deep(.ant-form-item) {
    margin-bottom: 0;
    width: 100%;
    min-width: 0;

    .ant-form-item-row {
      flex-wrap: nowrap !important;
      width: 100%;
    }

    &:has(textarea),
    &:has(.ant-input-textarea) {
      align-items: flex-start;

      .ant-form-item-label {
        padding-top: 8px;
      }
    }

    .ant-form-item-label {
      flex: 0 0 76px !important;
      max-width: 76px !important;
      text-align: right;

      > label {
        font-size: 13px;
        font-weight: 600;
        color: #475569;
        height: auto;
        white-space: nowrap;
      }
    }

    .ant-form-item-control {
      flex: 1 1 0 !important;
      min-width: 0 !important;
      max-width: none !important;
    }

    .ant-form-item-control-input,
    .ant-form-item-control-input-content {
      width: 100%;
      min-width: 0;
    }

    .ant-input,
    .ant-input-affix-wrapper,
    .ant-input-number,
    .ant-select,
    .ant-select.ant-select-in-form-item {
      width: 100% !important;
      max-width: 100% !important;
      box-sizing: border-box;
    }

    .ant-input,
    .ant-select-selector,
    .ant-input-number,
    .ant-input-affix-wrapper {
      border-radius: 8px !important;
      border-color: #e2e8f0 !important;
      background: #fff !important;

      &:hover {
        border-color: #94a3b8 !important;
      }
    }

    .ant-input-affix-wrapper-focused,
    .ant-input:focus,
    .ant-select-focused .ant-select-selector {
      border-color: #3b82f6 !important;
      box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.12) !important;
    }

    .ant-input:not(textarea),
    .ant-input-number,
    .ant-input-affix-wrapper {
      height: 36px !important;
    }

    .ant-select .ant-select-selector {
      min-height: 36px !important;
      padding: 0 11px !important;
    }

    .ant-select:not(.ant-select-multiple):not(.ant-select-customize-input) .ant-select-selector {
      height: 36px !important;
    }

    .ant-select-multiple .ant-select-selector {
      min-height: 36px !important;
      padding: 2px 8px !important;
    }

    .ant-select-disabled .ant-select-selector,
    .ant-input-disabled,
    .read-only-input {
      background: #f1f5f9 !important;
      color: #64748b !important;
      border-color: #e2e8f0 !important;
      cursor: not-allowed;
    }
  }
}

.ic-modal-footer {
  padding: 16px 24px; background: #ffffff; border-top: 1px solid #f1f5f9; text-align: right;
  .premium-btn {
    height: 36px; padding: 0 24px; border-radius: 8px; font-weight: 600;
    &.primary-gradient {
      background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%); border: none; color: #ffffff;
      &:hover { transform: translateY(-1px); box-shadow: 0 6px 15px rgba(37, 99, 235, 0.3); }
    }
  }
}

.read-only-input {
  background: #f1f5f9 !important; color: #94a3b8 !important; cursor: not-allowed;
}
</style>

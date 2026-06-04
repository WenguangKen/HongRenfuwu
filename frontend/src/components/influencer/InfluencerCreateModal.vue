.
<template>
  <a-modal
    v-model:open="visible"
    :title="null"
    width="880px"
    :footer="null"
    centered
    class="influencer-create-modal"
    :mask-closable="false"
    destroy-on-close
    :closable="false"
  >
    <!-- Custom Header -->
    <div class="ic-modal-header glass-header">
      <div class="ic-header-left">
        <div class="ic-header-icon-wrapper">
          <UserAddOutlined />
        </div>
        <div class="ic-header-text">
          <div class="ic-header-title">新建红人档案</div>
          <div class="ic-header-subtitle">Create new influencer profile with basic info and social media details</div>
        </div>
      </div>
      <a-button type="text" class="close-btn" @click="handleCancel">
        <CloseOutlined />
      </a-button>
    </div>

    <!-- 内容主体：灰色背景 -->
    <div class="ic-modal-body">

      <!-- 区域 1: 基本信息 -->
      <div class="ic-section">
        <div class="ic-section-header">
          <span class="ic-section-title">基础信息 (Basic Info)</span>
        </div>
        <div class="ic-section-body">
          <a-form
            ref="formRef"
            :model="formState"
            :rules="formRules"
            layout="horizontal"
            :colon="true"
            :label-col="{ style: { width: '76px', flex: '0 0 76px' } }"
            :wrapper-col="{ style: { flex: '1 1 0', minWidth: 0 } }"
            class="ic-form premium-form-v2 ic-form-inline"
            autocomplete="off"
          >
            <div class="ic-form-row">
              <a-form-item label="名字" name="firstName" :rules="[{ required: true, message: '请输入名字' }]">
                <a-input v-model:value="formState.firstName" placeholder="请输入名字" allow-clear autocomplete="off" @blur="formState.firstName = formState.firstName.trim()" />
              </a-form-item>
              <a-form-item label="姓氏" name="lastName" :rules="[{ required: true, message: '请输入姓氏' }]">
                <a-input v-model:value="formState.lastName" placeholder="请输入姓氏" allow-clear autocomplete="off" @blur="formState.lastName = formState.lastName.trim()" />
              </a-form-item>
            </div>
            <div class="ic-form-row">
              <a-form-item label="电子邮箱" name="email" :rules="[{ required: true, message: '请输入邮箱地址' }, { type: 'email', message: '请输入有效的邮箱地址' }]">
                <a-input v-model:value="formState.email" placeholder="请输入邮箱地址" allow-clear autocomplete="off" />
              </a-form-item>
              <a-form-item label="备用邮箱">
                <a-select v-model:value="formState.auxiliaryEmails" mode="tags" placeholder="输入邮箱后按回车添加" :token-separators="[',', ' ']" style="width: 100%" />
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
                  <a-select-option v-for="u in ownerUsers" :key="u.id" :value="u.id" :label="u.name">{{ u.name }}</a-select-option>
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
                  <a-select-option v-for="t in liaisonTagOptions" :key="t.id" :value="t.id" :label="t.name">{{ t.name }}</a-select-option>
                </a-select>
              </a-form-item>
            </div>
            <div class="ic-form-row">
              <a-form-item label="红人标签">
                <a-select v-model:value="formState.tags" mode="multiple" placeholder="请选择标签" allow-clear style="width: 100%">
                  <a-select-option v-for="t in availableTags" :key="t.id" :value="t.id">{{ t.name }}</a-select-option>
                </a-select>
              </a-form-item>
              <a-form-item label="红人类型">
                <a-select v-model:value="formState.influencerType" placeholder="请选择红人类型" allow-clear style="width: 100%">
                  <a-select-option v-for="t in dynamicInfluencerTypes" :key="t" :value="t">{{ t }}</a-select-option>
                </a-select>
              </a-form-item>
            </div>
            <div class="ic-form-row">
              <a-form-item label="人种">
                <a-select v-model:value="formState.race" placeholder="请选择人种" allow-clear style="width: 100%">
                  <a-select-option value="White">白人 (White)</a-select-option>
                  <a-select-option value="Black / African descent">黑人 (Black / African descent)</a-select-option>
                  <a-select-option value="Asian">亚裔 (Asian)</a-select-option>
                  <a-select-option value="Hispanic / Latino">拉丁裔 (Hispanic / Latino)</a-select-option>
                  <a-select-option value="Middle Eastern">中东裔 (Middle Eastern)</a-select-option>
                  <a-select-option value="Mixed">混血 (Mixed)</a-select-option>
                  <a-select-option value="Other / Unclear">其他 (Other / Unclear)</a-select-option>
                </a-select>
              </a-form-item>
              <a-form-item label="语言">
                <a-select v-model:value="formState.language" placeholder="请选择主要语言" allow-clear style="width: 100%">
                  <a-select-option value="English">English</a-select-option>
                  <a-select-option value="Spanish">Spanish</a-select-option>
                  <a-select-option value="French">French</a-select-option>
                  <a-select-option value="Portuguese">Portuguese</a-select-option>
                  <a-select-option value="German">German</a-select-option>
                  <a-select-option value="Other">Other</a-select-option>
                </a-select>
              </a-form-item>
            </div>
            <div class="ic-form-row ic-form-row-single">
              <a-form-item label="描述/备注">
                <a-textarea v-model:value="formState.description" placeholder="请输入红人描述或备注信息" :rows="3" />
              </a-form-item>
            </div>
          </a-form>
        </div>
      </div>

      <!-- 建联平台 -->
      <div class="ic-section">
        <div class="ic-section-header">
          <span class="ic-section-title">建联平台 (Connection Platform)</span>
        </div>
        <div class="ic-section-body social-media-body">
          <div class="social-table-wrap">
            <table class="social-table">
              <thead>
                <tr>
                  <th class="col-default">默认</th>
                  <th class="col-platform">合作平台</th>
                  <th class="col-name">社媒账号名</th>
                  <th class="col-link">主页链接</th>
                  <th class="col-fans">粉丝总量</th>
                  <th class="col-actions">管理操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(item, index) in formState.socialMediaList" :key="index">
                  <td class="col-default">
                    <a-radio
                      :checked="item.isPrimary"
                      @click="setPrimarySocial(index)"
                    />
                  </td>
                  <td class="col-platform">
                    <a-select v-model:value="item.platform" placeholder="选择平台" style="width: 100%">
                      <a-select-option v-for="p in platforms" :key="p" :value="p">{{ p }}</a-select-option>
                    </a-select>
                  </td>
                  <td class="col-name">
                    <a-input v-model:value="item.name" placeholder="名称" autocomplete="off" />
                  </td>
                  <td class="col-link">
                    <a-input
                      v-model:value="item.link"
                      placeholder="链接"
                      @blur="handleLinkChange(index, item.link)"
                    />
                  </td>
                  <td class="col-fans">
                    <a-input-number
                      v-model:value="item.fans"
                      placeholder="0"
                      style="width: 100%"
                      :formatter="(value: string) => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                      :parser="(value: string) => value.replace(/\$\s?|(,*)/g, '')"
                      :controls="false"
                    />
                  </td>
                  <td class="col-actions">
                    <a-button
                      type="link"
                      danger
                      size="small"
                      :disabled="formState.socialMediaList.length === 1"
                      @click="removeSocialMedia(index)"
                    >
                      移除
                    </a-button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <a-button type="dashed" block @click="addSocialMedia" class="add-social-btn">
            <PlusOutlined /> 添加更多社交账号
          </a-button>
        </div>
      </div>

      <!-- 区域 3: 寄样信息 -->
      <div class="ic-section">
        <div class="ic-section-header">
          <span class="ic-section-title">寄样信息 (Shipping Info)</span>
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
              <a-form-item label="街道地址">
                <a-input v-model:value="formState.street1" placeholder="街道地址 (Line 1)" autocomplete="off" />
              </a-form-item>
              <a-form-item label="公寓/单元">
                <a-input v-model:value="formState.street2" placeholder="公寓、单元号等 (Optional)" autocomplete="off" />
              </a-form-item>
            </div>
            <div class="ic-form-row">
              <a-form-item label="城市">
                <a-input v-model:value="formState.city" placeholder="城市" autocomplete="off" />
              </a-form-item>
              <a-form-item label="州/省">
                <a-input v-model:value="formState.state" placeholder="州/省" autocomplete="off" />
              </a-form-item>
            </div>
            <div class="ic-form-row">
              <a-form-item label="邮政编码">
                <a-input v-model:value="formState.zip" placeholder="邮政编码" autocomplete="off" />
              </a-form-item>
              <a-form-item label="手机号码">
                <a-input v-model:value="formState.phone" placeholder="收件人联系电话" autocomplete="off" />
              </a-form-item>
            </div>
            <div class="ic-form-row ic-form-row-single">
              <a-form-item label="国家/地区">
                <a-input v-model:value="formState.country" placeholder="国家/地区" autocomplete="off" />
              </a-form-item>
            </div>
          </a-form>
        </div>
      </div>



    </div>

    <!-- 底部按钮 -->
    <div class="ic-modal-footer">
      <a-space size="middle">
        <a-button @click="handleCancel" class="premium-btn">取消</a-button>
        <a-button type="primary" @click="handleOk" :loading="loading" class="premium-btn primary-gradient">确认创建</a-button>
      </a-space>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { reactive, watch, ref, onMounted } from 'vue';
import { storeToRefs } from 'pinia';
import { message } from 'ant-design-vue';
import { 
  UserAddOutlined,
  PlusOutlined,
  CloseOutlined
} from '@ant-design/icons-vue';
import { SOURCES } from '@/config/constants';
import { useUserStore } from '@/stores/user';
import { useCommonDataStore } from '@/stores/commonData';
import { influencerService } from '@/services/influencerService';
import { getEnabledCountries, type Country } from '@/services/countryService';
import { tagService, type SystemTag } from '@/services/tagService';
import type { InfluencerCreateDto } from '@/types/influencer';

const props = withDefaults(defineProps<{
  open: boolean;
  initialStage?: 'POOL' | 'ONBOARDED';  // 初始阶段: POOL=资源池, ONBOARDED=红人列表
  initialStatus?: string;  // 初始状态: UNSCREENED=待筛选, PENDING=待联系
}>(), {
  open: false,
  initialStage: 'ONBOARDED',  // 默认红人列表
  initialStatus: 'PENDING',    // 默认待联系
});

const emit = defineEmits<{
  (e: 'update:open', value: boolean): void;
  (e: 'created', value: any): void;
}>();

const visible = ref(false);
const loading = ref(false);
const formRef = ref<any>(null);

// Form validation rules
const formRules = {
  firstName: [{ required: true, message: '请输入名字', trigger: 'blur' }],
  lastName: [{ required: true, message: '请输入姓氏', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱地址', trigger: 'blur' },
    { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
  ]
};
const avatarFileList = ref<any[]>([]);
const sources = [...SOURCES];
const userStore = useUserStore();
const commonStore = useCommonDataStore();
const { ownerUsers, platforms, availableTags, liaisonTagOptions, dynamicInfluencerTypes, countries } = storeToRefs(commonStore);

// 从链接自动识别平台
const detectPlatformFromUrl = (url: string): string => {
  if (!url) return '';
  const lowerUrl = url.toLowerCase();
  
  // 查找动态平台名匹配 (优先)
  for (const p of platforms.value) {
    const pLower = p.toLowerCase();
    if (lowerUrl.includes(pLower)) return p;
    // 缩写识别逻辑
    if (pLower === 'instagram' && lowerUrl.includes('ins')) return p;
    if (pLower === 'ins' && lowerUrl.includes('instagram')) return p;
  }

  if (lowerUrl.includes('tiktok.com') || lowerUrl.includes('tiktok')) return 'TIKTOK';
  if (lowerUrl.includes('instagram.com') || lowerUrl.includes('instagram') || lowerUrl.includes('ins')) return 'INS';
  if (lowerUrl.includes('youtube.com') || lowerUrl.includes('youtu.be')) return 'YOUTUBE';
  if (lowerUrl.includes('facebook.com') || lowerUrl.includes('fb.com')) return 'FACEBOOK';
  return '';
};

// 监听社交媒体链接变化，自动识别平台
const handleLinkChange = (index: number, newLink: string) => {
  const item = formState.socialMediaList[index];
  if (item && newLink) {
    const detectedPlatform = detectPlatformFromUrl(newLink);
    if (detectedPlatform && !item.platform) {
      item.platform = detectedPlatform;
    }
  }
};

// 这些加载逻辑现在都通过 store 管理，本组件只需在打开时调用 store.loadAll() 或对应方法

// 平台标签已移至 commonStore

const dataLoaded = ref(false);

onMounted(() => {
  // Removed immediate loading
});

watch(
  () => props.open,
  (val) => {
    visible.value = val;
    if (val) {
      resetForm();
      if (!dataLoaded.value) {
        commonStore.loadAll(); // 一次性加载所有公共数据
        dataLoaded.value = true;
      }
    }
  },
);

watch(visible, (val) => emit('update:open', val));

interface SocialMediaItem {
  platform: string;
  name: string;
  link: string;
  fans: number | null;
  isPrimary?: boolean;
}

const formState = reactive({
  firstName: '',
  lastName: '',
  email: '',
  phone: '',
  influencerType: '',
  race: 'White',  // 默认白人
  gender: 2 as number | undefined,  // 默认女
  language: 'English',  // 默认英语
  description: '',
  avatar: '',
  source: '手动添加',  // 默认手动添加
  ownerId: undefined as number | undefined,  // 负责人ID
  contactPersonTagId: undefined as number | undefined,  // 联络人标签ID
  
  // Address
  street1: '',
  street2: '',
  city: '',
  state: '',
  zip: '',
  country: 'US',
  
  isPaid: false,
  commissionRate: 18 as number | undefined,
  paymentMethod: '',
  paymentAccount: '',
  
  // Social Media - 默认平台为空，根据链接自动识别
  socialMediaList: [{ platform: '', name: '', link: '', fans: null, isPrimary: true }] as SocialMediaItem[],
  
  // Other
  tags: [] as number[],
  auxiliaryEmails: [] as string[],
});

const resetForm = () => {
  formState.firstName = '';
  formState.lastName = '';
  formState.email = '';
  formState.phone = '';
  formState.influencerType = '';
  formState.race = 'White';  // 默认白人
  formState.gender = 2;  // 默认女
  formState.language = 'English';  // 默认英语
  formState.description = '';
  formState.avatar = '';
  formState.source = '手动添加';  // 默认手动添加
  formState.ownerId = userStore.userInfo?.id || undefined;  // 默认当前用户
  formState.contactPersonTagId = undefined;
  avatarFileList.value = [];
  
  formState.street1 = '';
  formState.street2 = '';
  formState.city = '';
  formState.state = '';
  formState.zip = '';
  formState.country = 'US';
  formState.isPaid = false;
  formState.commissionRate = 18;
  formState.paymentMethod = '';
  formState.paymentAccount = '';

  formState.socialMediaList = [{ platform: '', name: '', link: '', fans: null, isPrimary: true }];  // 平台默认为空
  
  formState.tags = [];
  formState.auxiliaryEmails = [];
  loading.value = false;
};

const addSocialMedia = () => {
  formState.socialMediaList.push({
    platform: 'TIKTOK',
    name: '',
    link: '',
    fans: null,
    isPrimary: false
  });
};

const removeSocialMedia = (index: number) => {
  if (formState.socialMediaList.length > 1) {
    const item = formState.socialMediaList[index];
    const wasPrimary = item?.isPrimary;
    formState.socialMediaList.splice(index, 1);
    if (wasPrimary && formState.socialMediaList.length > 0) {
      const firstItem = formState.socialMediaList[0];
      if (firstItem) {
        firstItem.isPrimary = true;
      }
    }
  }
};

const setPrimarySocial = (index: number) => {
  formState.socialMediaList.forEach((item, i) => {
    item.isPrimary = i === index;
  });
};

const onAvatarChange = ({ fileList }: any) => {
  avatarFileList.value = fileList.slice(0, 1);
  const file = avatarFileList.value[0];
  if (!file) {
    formState.avatar = '';
    return;
  }
  const raw = file.originFileObj || file;
  const reader = new FileReader();
  reader.onload = (e: any) => {
    formState.avatar = e.target.result as string;
  };
  reader.readAsDataURL(raw);
};

const handleCancel = () => {
  visible.value = false;
};

const tagOptions = ref([
  { value: 'Fashion', label: '时尚 (Fashion)' },
  { value: 'Beauty', label: '美妆 (Beauty)' },
  { value: 'Tech', label: '数码 (Tech)' },
  { value: 'Lifestyle', label: '生活 (Lifestyle)' },
  { value: 'Mother & Baby', label: '毋婴 (Mother & Baby)' },
  { value: 'Gaming', label: '游戏 (Gaming)' },
]); // 备用，优先使用 API 获取的 influencerTagOptions

const handleOk = async () => {
  // Validate form using Ant Design form validation
  try {
    await formRef.value?.validateFields();
  } catch (error) {
    message.warning('请填写必填字段');
    return;
  }
  
  // 检查社媒信息完善度
  const validSocial = formState.socialMediaList.filter(item => item.platform && item.name);
  if (validSocial.length === 0) {
    message.warning('请完善社媒信息 (至少填写一个平台和账号名称)');
    return;
  }
  
  loading.value = true;
  try {
    // 提取主平台数据
    const primarySocial = formState.socialMediaList.find(item => item.isPrimary) || formState.socialMediaList[0];
    
    // 构建完整地址字符串
    const fullAddress = [formState.street1, formState.street2, formState.city, formState.state, formState.country]
      .filter(Boolean)
      .join(', ');
    
    // 构造 DTO - 与后端 InfluencerCreateDto 字段对应
    const payload: InfluencerCreateDto = {
      // === 必填字段 ===
      realName: `${formState.firstName.trim()} ${formState.lastName.trim()}`.replace(/\s+/g, ' ').trim(),
      email: formState.email,
      
      // === 基本信息 ===
      nickName: `${formState.firstName.trim()} ${formState.lastName.trim()}`.replace(/\s+/g, ' ').trim(), // 使用真实姓名作为昵称
      phone: formState.phone || undefined,
      country: formState.country || undefined,
      language: formState.language || undefined,
      race: formState.race || undefined,
      gender: formState.gender,
      description: formState.description || undefined,
      influencerType: formState.influencerType || undefined,
      
      // === 业务属性 ===
      source: formState.source || 'Manual',
      ownerId: formState.ownerId,
      tagIds: [...formState.tags], // 标签ID数组
      
      // Liaison logic: Add to tagIds if selected
      
      // === 财务信息 ===
      commissionRate: formState.commissionRate !== undefined ? Number(formState.commissionRate) : undefined,
      paymentMethod: formState.paymentMethod || undefined,
      paymentAccount: formState.paymentAccount || undefined,
      
      // === 默认社交媒体 ===
      defaultPlatform: primarySocial?.platform || 'TIKTOK',
      defaultHandle: primarySocial?.name || '',
      defaultProfileUrl: primarySocial?.link || '',
      defaultFollowerCount: primarySocial?.fans ? Number(primarySocial.fans) : undefined,
      
      // === 地址信息 ===
      address: fullAddress || undefined,
      city: formState.city || undefined,
      state: formState.state || undefined,
      street1: formState.street1 || undefined,
      street2: formState.street2 || undefined,
      postalCode: formState.zip || undefined,

      // === 社交媒体列表 ===
      socialMediaList: formState.socialMediaList.filter(item => item.platform && item.name).map(item => ({
        platform: item.platform,
        handle: item.name, // Handle
        url: item.link || '', // URL
        followerCount: item.fans ? Number(item.fans) : 0,
        isDefault: item.isPrimary || false
      })),
      
      // === 初始状态（根据来源页面设置） ===
      initialStage: props.initialStage,
      initialStatus: props.initialStatus,
    };

    if (formState.contactPersonTagId) {
       payload.tagIds = [...(payload.tagIds || []), Number(formState.contactPersonTagId)];
    }
    // 辅助邮箱以 JSON 字符串方式传递
    if (formState.auxiliaryEmails.length > 0) {
      (payload as any).auxiliaryEmails = JSON.stringify(formState.auxiliaryEmails);
    }
    
    await influencerService.create(payload);
    
    message.success('创建成功');
    emit('created', payload);
    visible.value = false;
  } catch (error) {
    console.error(error);
    // Error handled by interceptor ideally
  } finally {
    loading.value = false;
  }
};
</script>

<style scoped lang="scss">
:deep(.influencer-create-modal) {
  .ant-modal {
    width: 880px !important;
    max-width: calc(100vw - 32px);
  }

  .ant-modal-content {
    padding: 0;
    overflow: hidden;
    border-radius: 16px;
    box-shadow: 0 20px 50px rgba(0, 0, 0, 0.15);
  }
}

/* Redesigned Header */
.ic-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  background: #ffffff;
  border-bottom: 1px solid #f1f5f9;
  position: relative;

  &.glass-header {
    background: rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(10px);
  }

  .ic-header-left {
    display: flex;
    align-items: center;
    gap: 16px;
  }

  .ic-header-icon-wrapper {
    width: 44px;
    height: 44px;
    border-radius: 12px;
    background: linear-gradient(135deg, #10b981 0%, #059669 100%);
    color: #ffffff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;
    box-shadow: 0 4px 12px rgba(16, 185, 129, 0.2);
  }

  .ic-header-title {
    font-size: 18px;
    font-weight: 700;
    color: #1e293b;
    line-height: 1.2;
  }

  .ic-header-subtitle {
    font-size: 12px;
    color: #94a3b8;
    margin-top: 2px;
  }

  .close-btn {
    color: #94a3b8;
    &:hover { color: #64748b; background: #f1f5f9; }
  }
}

/* Modal Body */
.ic-modal-body {
  padding: 20px 24px;
  background: #f8fafc;
  max-height: 70vh;
  overflow-y: auto;

  &::-webkit-scrollbar { width: 6px; }
  &::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 6px; }
}

.ic-section {
  background: #ffffff;
  border-radius: 14px;
  border: 1px solid #e2e8f0;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05), 0 2px 4px -1px rgba(0, 0, 0, 0.03);
  margin-bottom: 20px;
  overflow: hidden;

  &:last-child { margin-bottom: 0; }
  
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
        background: #10b981;
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

    &.ant-form-item-has-error {
      .ant-form-item-label > label {
        margin-top: 0;
      }
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
      border-color: #10b981 !important;
      box-shadow: 0 0 0 2px rgba(16, 185, 129, 0.12) !important;
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
  }
}

/* Social Media Table */
.social-table-wrap {
  overflow-x: auto;
  margin-bottom: 12px;
}

.social-table {
  width: 100%;
  table-layout: fixed;
  border-collapse: collapse;
  font-size: 13px;

  th {
    padding: 10px 8px;
    text-align: left;
    font-weight: 600;
    color: #475569;
    background: #f8fafc;
    border-bottom: 1px solid #e2e8f0;
    white-space: nowrap;
  }

  td {
    padding: 8px 6px;
    vertical-align: middle;
    border-bottom: 1px solid #f1f5f9;
    overflow: hidden;
  }

  tbody tr:hover {
    background: #fafbfc;
  }

  .col-default { width: 48px; text-align: center; }
  .col-platform { width: 14%; }
  .col-name { width: 16%; }
  .col-link { width: 36%; }
  .col-fans { width: 14%; }
  .col-actions { width: 64px; text-align: center; white-space: nowrap; }

  :deep(.ant-select),
  :deep(.ant-input),
  :deep(.ant-input-number) {
    width: 100% !important;
    max-width: 100% !important;
  }

  :deep(.ant-select-selector),
  :deep(.ant-input),
  :deep(.ant-input-number) {
    height: 36px !important;
    border-radius: 8px !important;
    border-color: #e2e8f0 !important;
    background: #fff !important;
  }

  :deep(.ant-input-number-input) {
    height: 34px !important;
  }
}

.add-social-btn {
  border-style: dashed;
  border-radius: 10px;
  height: 40px;
  background: #f8fafc;
  color: #64748b;
  font-weight: 600;
  margin-top: 8px;
  transition: all 0.3s;
  &:hover { color: #10b981; border-color: #10b981; background: #ecfdf5; }
}

/* Footer Section */
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
      box-shadow: 0 4px 12px rgba(16, 185, 129, 0.2);
      &:hover { transform: translateY(-1px); box-shadow: 0 6px 15px rgba(16, 185, 129, 0.3); }
    }
  }
}
</style>

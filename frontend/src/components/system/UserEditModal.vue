<template>
  <a-modal
    v-model:open="visible"
    :title="null"
    width="800px"
    :footer="null"
    centered
    class="user-modal"
    :mask-closable="false"
  >
    <!-- 自定义头部，参考角色编辑弹窗 -->
    <div class="um-modal-header">
      <div class="um-header-left">
        <div class="um-header-icon">U</div>
        <div class="um-header-text">
          <div class="um-header-title">
            {{ isEdit ? `编辑用户 (ID: ${record?.key ?? record?.id})` : '新增用户' }}
          </div>
          <div class="um-header-subtitle">配置用户的基础信息与账号状态</div>
        </div>
      </div>
    </div>

    <div class="um-modal-body">
      <div class="um-section">
        <div class="um-section-header">
          <span class="um-section-title">基本信息</span>
        </div>
        <div class="um-section-body">
          <a-form :model="formState" layout="vertical" class="um-form">
            <div class="um-form-row">
              <a-form-item label="用户名" required>
                <a-input v-model:value="formState.username" placeholder="请输入用户名" />
              </a-form-item>
              <a-form-item label="角色名" required>
                <a-select v-model:value="formState.roleId" placeholder="请选择角色">
                  <a-select-option v-for="role in availableRoles" :key="role.id" :value="role.id">
                    {{ role.name }}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </div>

            <div class="um-form-row">
              <a-form-item label="电话">
                <a-input v-model:value="formState.phone" placeholder="请输入电话" />
              </a-form-item>
              <a-form-item label="邮箱" required>
                <a-input v-model:value="formState.email" placeholder="请输入邮箱" />
              </a-form-item>
            </div>

            <div class="um-form-row">
              <a-form-item label="密码">
                <div style="display: flex; gap: 8px;">
                  <a-input-password 
                    v-model:value="formState.password" 
                    placeholder="请输入密码" 
                    :disabled="isEdit"
                  />
                  <a-button v-if="isEdit" type="primary" ghost @click="handleOpenPasswordModal">修改</a-button>
                </div>
              </a-form-item>
              <a-form-item label="状态">
                <a-switch
                  v-model:checked="formState.active"
                  checked-children="启用"
                  un-checked-children="停用"
                />
              </a-form-item>
            </div>

            <div v-if="isEdit" class="um-form-row">
              <a-form-item label="创建时间">
                <a-input v-model:value="formState.createTime" disabled />
              </a-form-item>
              <a-form-item label="最近登录时间">
                <a-input v-model:value="formState.lastLogin" disabled />
              </a-form-item>
            </div>

            <div v-if="isEdit" class="um-form-row">
              <a-form-item label="最近登录ID">
                <a-input v-model:value="formState.lastLoginId" disabled />
              </a-form-item>
            </div>
          </a-form>
        </div>
      </div>

      <!-- 店铺分配板块 -->
      <div class="um-section" style="margin-top: 20px;">
        <div class="um-section-header">
          <span class="um-section-title">店铺权限分配</span>
        </div>
        <div class="um-section-body">
          <div v-if="loadingStores" style="padding: 20px 0; text-align: center;">
            <a-spin />
          </div>
          <div v-else-if="allStoresList.length === 0" style="color: #94a3b8; padding: 10px 0; text-align: center; font-size: 13px;">
            暂无已同步的店铺账号，请先在易仓集成中同步店铺。
          </div>
          <div v-else>
            <!-- 头部筛选和全选控制 -->
            <div style="margin-bottom: 16px; display: flex; justify-content: space-between; align-items: center; gap: 16px; flex-wrap: wrap;">
              <span style="color: #64748b; font-size: 13px; font-weight: 500;">选择可查看的店铺账号（支持筛选）：</span>
              <div style="display: flex; gap: 10px; align-items: center; flex-wrap: wrap;">
                <a-input 
                  v-model:value="searchFilters.account" 
                  placeholder="搜索店铺账号" 
                  allow-clear
                  style="width: 170px;"
                />
                <a-select
                  v-model:value="searchFilters.site"
                  placeholder="筛选国家/站点"
                  allow-clear
                  style="width: 140px;"
                >
                  <a-select-option v-for="site in uniqueSites" :key="site" :value="site">
                    {{ site }}
                  </a-select-option>
                </a-select>
                <a-checkbox
                  v-model:checked="selectAllStores"
                  style="margin-left: 8px;"
                >
                  全选 / 全不选
                </a-checkbox>
              </div>
            </div>

            <div class="stores-grid">
              <a-checkbox-group v-model:value="formState.allocatedStores" style="width: 100%">
                <a-row :gutter="[12, 12]">
                  <a-col v-for="store in filteredStoresList" :key="store.value" :span="12">
                    <div class="store-item-card" :class="{ 'is-selected': formState.allocatedStores.includes(store.value) }" @click.stop="toggleStoreSelection(store.value)">
                      <a-checkbox :value="store.value" @click.prevent>
                        <span class="store-account-name">{{ store.account }}</span>
                        <span class="store-site-tag">{{ store.site }}</span>
                      </a-checkbox>
                    </div>
                  </a-col>
                </a-row>
              </a-checkbox-group>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="um-modal-footer">
      <a-space>
        <a-button @click="visible = false">取消</a-button>
        <a-button type="primary" @click="handleOk">确定</a-button>
      </a-space>
    </div>

    <!-- 修改密码Modal -->
    <UserPasswordModal
      v-if="passwordModalVisible"
      v-model:open="passwordModalVisible"
      :user="record"
      @success="handlePasswordSuccess"
    />
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch, onMounted } from 'vue';
import { message } from 'ant-design-vue';
import { PlusOutlined, LoadingOutlined } from '@ant-design/icons-vue';
import type { UploadChangeParam, UploadProps } from 'ant-design-vue';
import dayjs from 'dayjs';
import { getRoleList, type Role } from '@/services/roleService';
import { getEccangStores } from '@/services/eccangService';

import UserPasswordModal from './UserPasswordModal.vue';

const props = defineProps<{
  open: boolean;
  record: any;
}>();

const emit = defineEmits(['update:open', 'ok']);

const visible = computed({
  get: () => props.open,
  set: (val) => emit('update:open', val),
});

const isEdit = ref(false);
const passwordModalVisible = ref(false);
const loading = ref(false);
const originalEmail = ref(''); // 记录编辑模式下原始邮箱（可能是加密值）
const fileList = ref<UploadProps['fileList']>([]);
const availableRoles = ref<Role[]>([]);

const formState = reactive({
  username: '',
  roleId: undefined as number | undefined,
  phone: '',
  email: '',
  password: '',
  avatar: '',
  createTime: '',
  lastLogin: '',
  lastLoginId: '',
  active: true,
  allocatedStores: [] as string[],
});

const loadingStores = ref(false);
interface StoreOption {
  value: string;
  account: string;
  site: string;
}
const allStoresList = ref<StoreOption[]>([]);

const searchFilters = reactive({
  account: '',
  site: undefined as string | undefined,
});

const uniqueSites = computed(() => {
  const sites = allStoresList.value.map(s => s.site).filter(Boolean);
  return Array.from(new Set(sites)).sort();
});

const filteredStoresList = computed(() => {
  return allStoresList.value.filter(s => {
    const matchAccount = !searchFilters.account || s.account.toLowerCase().includes(searchFilters.account.toLowerCase());
    const matchSite = !searchFilters.site || s.site.toLowerCase() === searchFilters.site.toLowerCase();
    return matchAccount && matchSite;
  });
});

const isEncryptedData = (value: string | undefined): boolean => {
  if (!value) return false;
  return value.length > 40 && /^[A-Za-z0-9+/=]+$/.test(value) && !value.includes('@');
};

const selectAllStores = computed({
  get: () => {
    const filtered = filteredStoresList.value;
    if (filtered.length === 0) return false;
    return filtered.every(s => formState.allocatedStores.includes(s.value));
  },
  set: (checked) => {
    const filtered = filteredStoresList.value;
    const currentAllocated = new Set(formState.allocatedStores);
    if (checked) {
      filtered.forEach(s => currentAllocated.add(s.value));
    } else {
      filtered.forEach(s => currentAllocated.delete(s.value));
    }
    formState.allocatedStores = Array.from(currentAllocated);
  }
});

const loadStores = async () => {
  loadingStores.value = true;
  try {
    const stores = await getEccangStores();
    const list: StoreOption[] = [];
    const seen = new Set<string>();
    stores.forEach(s => {
      if (s.accountCounts && Array.isArray(s.accountCounts)) {
        s.accountCounts.forEach(ac => {
          const key = `${ac.site}|${ac.userAccount}`;
          if (!seen.has(key)) {
            seen.add(key);
            list.push({
              value: key,
              account: ac.userAccount,
              site: ac.site,
            });
          }
        });
      }
    });
    allStoresList.value = list;
  } catch (error) {
    console.error('Failed to load stores for allocation', error);
  } finally {
    loadingStores.value = false;
  }
};

const toggleStoreSelection = (value: string) => {
  const index = formState.allocatedStores.indexOf(value);
  if (index > -1) {
    formState.allocatedStores.splice(index, 1);
  } else {
    formState.allocatedStores.push(value);
  }
};

const fetchRoles = async () => {
  try {
    const res = await getRoleList({ page: 0, size: 100 });
    availableRoles.value = res.content;
  } catch (error) {
    console.error('Failed to fetch roles', error);
  }
};

const getMaskedPassword = (pwd: string) => {
  if (!pwd || pwd.length < 6) return '******';
  return pwd.substring(0, 3) + '******' + pwd.substring(pwd.length - 3);
};

const getBase64 = (img: Blob, callback: (base64Url: string) => void) => {
  const reader = new FileReader();
  reader.addEventListener('load', () => callback(reader.result as string));
  reader.readAsDataURL(img);
};

const beforeUpload = (file: File) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
  if (!isJpgOrPng) {
    message.error('请上传 JPG/PNG 格式图片!');
    return false;
  }
  const isLt2M = file.size / 1024 / 1024 < 2;
  if (!isLt2M) {
    message.error('图片大小不能超过 2MB!');
    return false;
  }
  
  loading.value = true;
  getBase64(file, (base64Url: string) => {
    formState.avatar = base64Url;
    loading.value = false;
  });
  
  return false;
};

const handleChange = (info: UploadChangeParam) => {
  if (info.file.status === 'uploading') {
    loading.value = true;
    return;
  }
  if (info.file.status === 'done') {
    loading.value = false;
  }
};

const handleOpenPasswordModal = () => {
  passwordModalVisible.value = true;
};

const handlePasswordSuccess = () => {
  // 可以在这里提示用户，或者更新当前表单的密码显示
  // 实际上因为是 mock 数据，这里不需要额外操作，但生产环境可能需要刷新数据
};

watch(() => props.open, (val) => {
  if (val) {
    // Reset search filters when opening modal
    searchFilters.account = '';
    searchFilters.site = undefined;

    if (props.record) {
      isEdit.value = true;
      const record = props.record;
      formState.username = record.username;
      // Map role to roleId
      formState.roleId = record.roles && record.roles.length > 0 ? record.roles[0].id : undefined;
      formState.phone = record.phone;
      formState.email = record.email;
      originalEmail.value = record.email || ''; // 保存原始邮箱值
      // 编辑模式下显示加密密码：前后三位 + 中间加密
      formState.password = '******'; // Password is not returned by API usually
      formState.createTime = record.createdAt;
      formState.active = record.status === 'active';
      formState.lastLogin = record.lastLoginTime;
      formState.lastLoginId = `${record.lastLoginIp || '-'} ${record.lastLoginLocation ? `(${record.lastLoginLocation})` : ''}`;
      formState.allocatedStores = record.allocatedStores || [];
    } else {
      isEdit.value = false;
      formState.username = '';
      formState.roleId = undefined;
      formState.phone = '';
      formState.email = '';
      formState.password = '';
      formState.avatar = '';
      const now = dayjs().format('YYYY-MM-DD HH:mm:ss');
      formState.createTime = now;
      formState.lastLogin = '';
      formState.lastLoginId = '';
      formState.active = true;
      formState.allocatedStores = [];
    }
    loadStores();
    // Ensure roles are loaded
    if (availableRoles.value.length === 0) {
      fetchRoles();
    }
  }
}, { immediate: true });

const handleOk = () => {
  if (!formState.username) {
    message.warning('请填写用户名');
    return;
  }
  if (!formState.roleId) {
    message.warning('请选择角色');
    return;
  }
  if (!formState.email) {
    message.warning('请填写邮箱');
    return;
  }
  // 新增模式下密码必填
  if (!isEdit.value && !formState.password) {
    message.warning('请填写密码');
    return;
  }
  
  const submitData = {
    ...formState,
    roleIds: [formState.roleId],
    isEdit: isEdit.value,
    id: props.record?.id
  };

  // 如果编辑模式下密码未修改（仍为占位符），则不提交密码字段
  if (isEdit.value && formState.password.includes('******')) {
    delete (submitData as any).password;
  }

  // 如果编辑模式下邮箱未修改（仍是加密值），则不提交email字段，避免后端@Email验证失败
  if (isEdit.value && formState.email === originalEmail.value && isEncryptedData(formState.email)) {
    delete (submitData as any).email;
  }

  emit('ok', submitData);
  // visible.value = false; // Let parent close it
};

onMounted(() => {
  fetchRoles();
});
</script>

<style lang="scss" scoped>
/* 用户弹窗样式，参考角色编辑弹窗 */
:deep(.user-modal) .ant-modal-content {
  padding: 0;
  overflow: hidden;
  border-radius: 20px;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.15);
}

.um-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  background: linear-gradient(135deg, rgba(251, 113, 133, 0.08) 0%, rgba(251, 191, 36, 0.05) 100%);
  border-bottom: 1px solid rgba(251, 113, 133, 0.1);
}

.um-header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.um-header-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: linear-gradient(135deg, #fb7185 0%, #f43f5e 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 800;
  font-size: 22px;
  box-shadow: 0 6px 16px rgba(251, 113, 133, 0.3);
}

.um-header-title {
  font-size: 17px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 2px;
}

.um-header-subtitle {
  font-size: 13px;
  color: #64748b;
}

.um-modal-body {
  padding: 24px;
  background: #fff;
}

.um-section {
  background: #fff;
  border-radius: 16px;
  border: 1px solid #f1f5f9;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);
  transition: all 0.3s ease;
  
  &:hover {
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.04);
    border-color: rgba(251, 113, 133, 0.2);
  }
}

.um-section-header {
  padding: 12px 16px;
  background: rgba(248, 250, 252, 0.6);
  border-bottom: 1px solid #f1f5f9;
}

.um-section-title {
  font-size: 14px;
  font-weight: 700;
  color: #334155;
  display: flex;
  align-items: center;
  gap: 6px;
  
  &::before {
    content: '';
    display: block;
    width: 4px;
    height: 14px;
    background: #fb7185;
    border-radius: 2px;
  }
}

.um-section-body {
  padding: 20px;
}

.um-form-row {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.um-form-row:last-child {
  grid-template-columns: minmax(0, 1fr);
  margin-bottom: 0;
}

.um-modal-footer {
  padding: 16px 24px;
  border-top: 1px solid #f1f5f9;
  background: #f8fafc;
  text-align: right;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.stores-grid {
  max-height: 360px;
  overflow-y: auto;
  padding: 12px;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  background: #f8fafc;
}

.store-item-card {
  padding: 10px 14px;
  border-radius: 8px;
  border: 1px solid #e2e8f0;
  background: #fff;
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  display: flex;
  align-items: center;
  cursor: pointer;
  user-select: none;

  &:hover {
    border-color: #fb7185;
    box-shadow: 0 4px 12px rgba(251, 113, 133, 0.08);
  }

  &.is-selected {
    border-color: #fb7185;
    background: linear-gradient(135deg, rgba(251, 113, 133, 0.04) 0%, rgba(251, 113, 133, 0.08) 100%);
    box-shadow: 0 4px 12px rgba(251, 113, 133, 0.08);
  }
}

.store-account-name {
  font-weight: 500;
  color: #334155;
  margin-right: 8px;
  font-size: 13px;
}

.store-site-tag {
  font-size: 10px;
  background: #e2e8f0;
  color: #475569;
  padding: 2px 6px;
  border-radius: 4px;
  text-transform: uppercase;
  font-weight: 700;
  letter-spacing: 0.5px;
}
</style>

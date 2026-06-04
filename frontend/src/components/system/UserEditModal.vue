<template>
  <a-modal
    v-model:open="visible"
    :title="null"
    width="720px"
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
});

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
    if (props.record) {
      isEdit.value = true;
      const record = props.record;
      formState.username = record.username;
      // Map role to roleId
      formState.roleId = record.roles && record.roles.length > 0 ? record.roles[0].id : undefined;
      formState.phone = record.phone;
      formState.email = record.email;
      // 编辑模式下显示加密密码：前后三位 + 中间加密
      formState.password = '******'; // Password is not returned by API usually
      formState.createTime = record.createdAt;
      formState.active = record.status === 'active';
      formState.lastLogin = record.lastLoginTime;
      formState.lastLoginId = `${record.lastLoginIp || '-'} ${record.lastLoginLocation ? `(${record.lastLoginLocation})` : ''}`;
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
    }
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
</style>

<template>
  <div class="address-tab">
    <div class="section-header">
      <div class="header-tips">共录入 {{ list.length }} 条收发货地址</div>
      <a-button @click="handleAdd" class="premium-add-btn">
        <template #icon><PlusOutlined /></template>
        新增地址档案
      </a-button>
    </div>
    
    <div class="table-container">
      <a-table
        :columns="columns"
        :data-source="list"
        :pagination="false"
        :row-key="(_record: Address, index: number) => index"
        class="premium-table"
        size="middle"
        :scroll="{ y: 460 }"
      >
        <template #bodyCell="{ column, record, index }: { column: any; record: Address; index: number }">
          
          <!-- New Default Column -->
          <template v-if="column.key === 'isDefault'">
            <div class="default-cell">
               <a-radio 
                  :checked="!!record.isDefault" 
                  @click="handleSetDefault(index)"
                  class="custom-radio"
               />

            </div>
          </template>

          <template v-else-if="column.key === 'recipientName'">
            <a-input v-if="record.editing" v-model:value="record.recipientName" placeholder="姓名" class="table-input" />
            <span v-else class="text-main">{{ record.recipientName || '-' }}</span>
          </template>

          <template v-else-if="column.key === 'phone'">
            <a-input v-if="record.editing" v-model:value="record.phone" placeholder="电话" class="table-input" />
            <span v-else class="text-sub">{{ record.phone || '-' }}</span>
          </template>

          <template v-else-if="column.key === 'email'">
            <a-input v-if="record.editing" v-model:value="record.email" placeholder="邮箱" class="table-input" />
            <span v-else class="text-sub">{{ record.email || '-' }}</span>
          </template>

          <template v-else-if="column.key === 'postalCode'">
            <a-input v-if="record.editing" v-model:value="record.postalCode" placeholder="邮编" class="table-input" />
            <span v-else class="mono-text">{{ record.postalCode || '-' }}</span>
          </template>

          <template v-else-if="column.key === 'address'">
            <a-textarea v-if="record.editing" v-model:value="record.address" placeholder="详细地址" class="table-textarea" :rows="1" auto-size />
            <span v-else class="address-text">{{ record.address || '-' }}</span>
          </template>

          <template v-else-if="column.key === 'action'">
            <div class="action-btns-wrapper">
              <template v-if="record.editing">
                <a-button type="primary" size="small" @click="handleSave(index)" class="detail-btn">保存</a-button>
                <a-button size="small" @click="handleCancel(index)" class="transfer-btn">取消</a-button>
              </template>
              <template v-else>
                <a-button type="primary" size="small" @click="handleEdit(index)" class="detail-btn">修改</a-button>
                <a-button size="small" danger class="transfer-btn" @click="confirmDelete(index)">移除</a-button>
              </template>
            </div>
          </template>
        </template>
      </a-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, createVNode } from 'vue';
import { PlusOutlined } from '@ant-design/icons-vue';
import { message, Modal } from 'ant-design-vue';
import { showDeleteConfirm } from '@/utils/modal';
import { influencerService } from '@/services/influencerService';

interface Address {
  id?: number;
  recipientName: string;
  phone: string;
  email: string;
  postalCode: string;
  address: string;
  isDefault?: boolean;
  editing: boolean;
}

const props = defineProps<{
  data: Address[];
  influencerId?: number;
}>();

const emit = defineEmits<{
  (e: 'update:data', value: Address[]): void;
  (e: 'default-changed'): void;
}>();

const list = ref<Address[]>([]);
// 防止双向绑定无限循环
let skipNextWatch = false;

const columns = [
  { title: '默认', dataIndex: 'isDefault', key: 'isDefault', width: 60, align: 'center' as const },
  { title: '收件人姓名', dataIndex: 'recipientName', key: 'recipientName', width: 100, ellipsis: true },
  { title: '联系电话', dataIndex: 'phone', key: 'phone', width: 120, ellipsis: true },
  { title: '电子邮箱', dataIndex: 'email', key: 'email', width: 220, ellipsis: true },
  { title: '邮政编码', dataIndex: 'postalCode', key: 'postalCode', width: 90 },
  { title: '详细收货地址', dataIndex: 'address', key: 'address', ellipsis: true }, // Flexible width
  { title: '操作', dataIndex: 'action', key: 'action', width: 110, align: 'right' as const },
];

watch(() => props.data, (val) => {
  // Simple initialization logic
  const validData = (val || []).map(item => ({...item, editing: false, isDefault: !!item.isDefault}));
  list.value = validData;
}, { immediate: true });

// Clear and reload when switching influencers to prevent showing stale data
watch(() => props.influencerId, async (newId, oldId) => {
  if (newId && newId !== oldId) {
    list.value = [];
    await refreshList();
  }
});

// Removed watch(list) and emit loop to prevent freeze.
// Data persistence is now handled by handleSave/handleDelete API calls.

const handleAdd = () => {
  list.value.unshift({ recipientName: '', phone: '', email: '', postalCode: '', address: '', editing: true, isDefault: false });
};

const handleEdit = (index: number) => { 
  if (list.value[index]) list.value[index].editing = true; 
};

// Set Default Address Logic - only update the selected item, backend clears others automatically
const handleSetDefault = async (index: number) => {
  if (!props.influencerId) return;
  
  const item = list.value[index];
  if (!item || !item.id) {
    message.warning('请先保存地址后再设置默认');
    return;
  }
  
  try {
    // Only update this one item with isDefault=true
    // Backend's clearDefault will automatically set other addresses to false
    await influencerService.updateAddress(props.influencerId, item.id, {
      recipientName: item.recipientName,
      phone: item.phone,
      email: item.email,
      address: item.address,
      postalCode: item.postalCode,
      isDefault: true,
    });
    
    // Refresh list to get updated state from backend
    await refreshList();
    emit('default-changed');
    message.success('已设置默认地址');
  } catch (e) {
    message.error('设置默认地址失败');
    await refreshList();
  }
};

const handleSave = async (index: number) => {
  const item = list.value[index];
  if (!item) return;
  if (!item.recipientName || !item.address) return message.warning('请补充收件人和地址');
  
  if (props.influencerId) {
    try {
      const payload = {
        recipientName: item.recipientName,
        phone: item.phone,
        email: item.email,
        address: item.address,
        postalCode: item.postalCode,
        isDefault: item.isDefault,
      };
      if (item.id) {
        await influencerService.updateAddress(props.influencerId, item.id, payload);
      } else {
        await influencerService.addAddress(props.influencerId, payload);
      }
      // Refresh list to get the new id from backend
      await refreshList();
      message.success('地址已保存');
    } catch (e) {
      message.error('保存失败');
      return;
    }
  }
  item.editing = false;
};

const refreshList = async () => {
  if (!props.influencerId) return;
  try {
    const addresses = await influencerService.getAddresses(props.influencerId) as unknown as Address[];
    list.value = (addresses || []).map((item: any) => ({ ...item, editing: false, isDefault: !!item.isDefault }));
  } catch (e) {
    // ignore
  }
};

const handleCancel = (index: number) => {
  const item = list.value[index];
  if (!item) return;
  if (!item.recipientName && !item.address) list.value.splice(index, 1);
  else item.editing = false;
};

const confirmDelete = (index: number) => {
  showDeleteConfirm({
    title: '确定要移除此地址吗？',
    content: '移除后该地址信息将无法直接查看。',
    onOk: () => handleDelete(index),
  });
};

const handleDelete = async (index: number) => {
  const item = list.value[index];
  if (props.influencerId && item?.id) {
    try {
      await influencerService.deleteAddress(props.influencerId, item.id);
    } catch (e) {
      message.error('删除失败');
      return;
    }
  }
  list.value.splice(index, 1);
  message.success('已从档案中移除');
};
</script>

<style lang="scss" scoped>
.address-tab {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  .header-tips { font-size: 14px; color: #94a3b8; font-weight: 500; }
}

.premium-add-btn {
  height: 36px;
  background: #1e293b;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  font-size: 13px;
  padding: 0 20px;
  transition: all 0.3s;
  display: flex; align-items: center; gap: 6px;
  &:hover { background: #334155; transform: translateY(-1px); box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); }
}

.table-container { 
  flex: 1; 
  border-radius: 12px; 
  border: 1px solid #f1f5f9; 
  overflow: hidden; 
  min-height: 0; 
  background: #fff; 
  box-shadow: 0 4px 20px rgba(0,0,0,0.02); 
}

.premium-table {
  :deep(.ant-table) { background: transparent; }
  :deep(.ant-table-thead > tr > th) {
    background: #f8fafc !important;
    color: #475569 !important;
    font-weight: 700 !important;
    font-size: 13px !important;
    border-bottom: 1px solid #f1f5f9 !important;
    padding: 12px 16px !important;
  }
  :deep(.ant-table-tbody > tr > td) {
    padding: 10px 16px !important;
    border-bottom: 1px solid #f8fafc !important;
    transition: all 0.2s;
    vertical-align: middle;
  }
  :deep(.ant-table-tbody > tr:hover > td) { background: #fcfdfe !important; }
}

.default-cell {
  display: flex; justify-content: center;
  .custom-radio {
    display: flex; align-items: center;
    :deep(.ant-radio-inner) { border-color: #cbd5e1; }
    :deep(.ant-radio-checked .ant-radio-inner) { border-color: #3b82f6; background-color: #3b82f6; }
  }
  .default-tag {
    font-size: 11px; color: #3b82f6; font-weight: 600; background: #eff6ff; padding: 1px 6px; border-radius: 4px; margin-left: 4px;
  }
}

.text-main { font-weight: 700; color: #1e293b; font-size: 13px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; display: block; }
.text-sub { font-weight: 500; color: #64748b; font-size: 12px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; display: block; }
.mono-text { font-family: 'JetBrains Mono', monospace; color: #64748b; font-size: 12px; }
.address-text { color: #334155; font-size: 13px; font-weight: 500; line-height: 1.5; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; display: block; }

.table-input, .table-textarea {
  width: 100%;
  :deep(.ant-input) {
    border-radius: 6px !important;
    border: 1px solid #e2e8f0 !important;
    background: #fff !important;
    min-height: 28px !important;
    padding: 2px 8px !important;
    font-size: 12px;
    &:focus { border-color: #3b82f6 !important; box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1) !important; }
  }
}

.action-btns-wrapper {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 8px;

  .detail-btn {
    background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
    border: none;
    box-shadow: 0 2px 4px rgba(59, 130, 246, 0.15);
    font-weight: 600;
    font-size: 12px;
    height: 28px;
    padding: 0 12px;
    border-radius: 6px;
    &:hover { transform: translateY(-1px); box-shadow: 0 4px 10px rgba(59, 130, 246, 0.25); }
  }

  .transfer-btn {
    border: 1px solid #e2e8f0;
    color: #475569;
    font-weight: 600;
    font-size: 12px;
    background: #fff;
    height: 28px;
    padding: 0 12px;
    border-radius: 6px;
    &:hover { color: #3b82f6; border-color: #3b82f6; background: #f8fafc; }
    &.ant-btn-dangerous {
      color: #ef4444;
      &:hover { border-color: #ef4444; background: #fef2f2; color: #ef4444; }
    }
  }
}
</style>

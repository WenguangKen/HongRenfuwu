<template>
  <div class="eccang-setting-page">
    <a-card :bordered="false" class="header-card glass-card" :body-style="{ padding: '20px 24px' }">
      <div class="page-header">
        <div class="header-left">
          <div class="icon-wrapper">
            <CloudServerOutlined />
          </div>
          <div class="header-text">
            <h1 class="page-title">易仓 (Eccang) ERP 配置</h1>
            <p class="page-desc">当前使用易仓「wms应用」凭证，请补充 service_id 后即可同步商品</p>
          </div>
        </div>
      </div>
    </a-card>

    <a-card :bordered="false" class="config-card glass-card" :body-style="{ padding: '20px 24px' }">
      <template #title>
        <div class="card-title">
          <SettingOutlined />
          <span>API 授权配置</span>
        </div>
      </template>
      <a-form layout="vertical">
        <a-row :gutter="16">
          <a-col :xs="24" :md="12">
            <a-form-item label="App Key">
              <a-input 
                v-model:value="configForm.appKey" 
                placeholder="请输入易仓 App Key"
                class="premium-input"
              >
                <template #prefix><KeyOutlined /></template>
              </a-input>
            </a-form-item>
          </a-col>
          <a-col :xs="24" :md="12">
            <a-form-item label="App Token / Secret">
              <a-input-password 
                v-model:value="configForm.appToken" 
                placeholder="请输入易仓 App Token"
                class="premium-input"
              >
                <template #prefix><SafetyCertificateOutlined /></template>
              </a-input-password>
            </a-form-item>
          </a-col>
          <a-col :xs="24">
            <div style="display: flex; justify-content: flex-end;">
              <a-button type="primary" @click="saveConfig" class="primary-gradient" :loading="saving">
                保存授权配置
              </a-button>
            </div>
          </a-col>
        </a-row>
      </a-form>
    </a-card>

    <a-card :bordered="false" class="endpoints-card glass-card" :body-style="{ padding: 0 }">
      <template #title>
        <div class="card-title">
          <ShopOutlined />
          <span>已授权的亚马逊店铺</span>
          <a-button type="link" @click="handleSyncStores" :loading="syncingStores" size="small">
            <SyncOutlined /> 从易仓同步店铺
          </a-button>
        </div>
      </template>
      <div class="store-list-container" style="padding: 16px;">
        <a-table 
          :columns="columns" 
          :data-source="stores" 
          :row-key="(record: any) => record.id"
          :pagination="false"
          size="middle"
          table-layout="fixed"
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'platform'">
              <a-tag color="orange">Amazon</a-tag>
            </template>
            <template v-else-if="column.key === 'platformAccounts'">
              <div class="accounts-grid-wrapper">
                <template v-if="record.accountCounts && record.accountCounts.length > 0">
                  <div class="accounts-grid-table">
                    <div class="grid-header">
                      <div class="col-site">站点</div>
                      <div class="col-account">账号名称</div>
                      <div class="col-active">在售</div>
                      <div class="col-inactive">下架</div>
                      <div class="col-total">总数</div>
                    </div>
                    <div class="grid-body">
                      <div 
                        v-for="item in getVisibleAccountCounts(record)" 
                        :key="item.site + '|' + item.userAccount" 
                        class="grid-row"
                      >
                        <div class="col-site">
                          <span v-if="item.site" class="site-badge">
                            {{ item.site }}
                          </span>
                          <span v-else>-</span>
                        </div>
                        <div class="col-account account-name-text">
                          {{ item.userAccount }}
                        </div>
                        <div class="col-active status-active">
                          <div class="count-item-row"><span class="label">SPU:</span> <span class="val">{{ item.activeProductCount }}</span></div>
                          <div class="count-item-row sku-count"><span class="label">SKU:</span> <span class="val">{{ item.activeSkuCount ?? 0 }}</span></div>
                        </div>
                        <div class="col-inactive status-inactive">
                          <div class="count-item-row"><span class="label">SPU:</span> <span class="val">{{ item.inactiveProductCount }}</span></div>
                          <div class="count-item-row sku-count"><span class="label">SKU:</span> <span class="val">{{ item.inactiveSkuCount ?? 0 }}</span></div>
                        </div>
                        <div class="col-total status-total">
                          <div class="count-item-row"><span class="label">SPU:</span> <span class="val">{{ item.totalProductCount }}</span></div>
                          <div class="count-item-row sku-count"><span class="label">SKU:</span> <span class="val">{{ item.totalSkuCount ?? 0 }}</span></div>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div 
                    v-if="record.accountCounts.length > 8" 
                    class="expand-btn-wrapper"
                  >
                    <span 
                      class="expand-btn"
                      @click="toggleExpand(record.id!)"
                    >
                      {{ isExpanded(record.id!) ? '收起' : `+ 展开更多 (${record.accountCounts.length - 8})` }}
                    </span>
                  </div>
                </template>
                <template v-else>
                  <div class="accounts-grid-table fallback-grid">
                    <div class="grid-header">
                      <div class="col-site">站点</div>
                      <div class="col-account">账号名称</div>
                    </div>
                    <div class="grid-body">
                      <div 
                        v-for="account in getVisibleAccounts(record)" 
                        :key="account" 
                        class="grid-row"
                      >
                        <div class="col-site">
                          <span v-if="parseAccount(account).site" class="site-badge">
                            {{ parseAccount(account).site }}
                          </span>
                          <span v-else>-</span>
                        </div>
                        <div class="col-account account-name-text">
                          {{ parseAccount(account).name }}
                        </div>
                      </div>
                    </div>
                  </div>
                  <div 
                    v-if="record.platformAccounts && record.platformAccounts.length > 8" 
                    class="expand-btn-wrapper"
                  >
                    <span 
                      class="expand-btn"
                      @click="toggleExpand(record.id!)"
                    >
                      {{ isExpanded(record.id!) ? '收起' : `+ 展开更多 (${record.platformAccounts.length - 8})` }}
                    </span>
                  </div>
                  <span v-if="!record.platformAccounts || record.platformAccounts.length === 0" class="no-data-tip">
                    暂无同步的平台店铺账号，请在「商品列表」中同步商品
                  </span>
                </template>
              </div>
            </template>
            <template v-else-if="column.key === 'activeProductCount'">
              <div class="main-column-counts">
                <div class="status-active font-semibold"><span class="label">SPU:</span> <span class="val">{{ record.activeProductCount ?? 0 }}</span></div>
                <div class="sku-count-sub"><span class="label">SKU:</span> <span class="val">{{ record.activeSkuCount ?? 0 }}</span></div>
              </div>
            </template>
            <template v-else-if="column.key === 'inactiveProductCount'">
              <div class="main-column-counts">
                <div class="status-inactive font-semibold"><span class="label">SPU:</span> <span class="val">{{ record.inactiveProductCount ?? 0 }}</span></div>
                <div class="sku-count-sub"><span class="label">SKU:</span> <span class="val">{{ record.inactiveSkuCount ?? 0 }}</span></div>
              </div>
            </template>
            <template v-else-if="column.key === 'totalProductCount'">
              <div class="main-column-counts">
                <div class="status-total font-semibold"><span class="label">SPU:</span> <span class="val">{{ record.totalProductCount ?? 0 }}</span></div>
                <div class="sku-count-sub"><span class="label">SKU:</span> <span class="val">{{ record.totalSkuCount ?? 0 }}</span></div>
              </div>
            </template>
            <template v-else-if="column.key === 'action'">
              <a-button type="link" size="small" @click="openEditStore(record)">
                编辑
              </a-button>
            </template>
          </template>
          <template #emptyText>
            <a-empty description="暂无绑定的店铺，请先配置授权并同步" />
          </template>
        </a-table>
      </div>
    </a-card>

    <!-- 编辑店铺 Modal -->
    <a-modal
      v-model:open="editModalOpen"
      @ok="handleSaveStore"
      :confirm-loading="savingStore"
      ok-text="保存"
      cancel-text="取消"
      class="premium-edit-modal"
      :width="520"
    >
      <template #title>
        <div class="modal-header-custom">
          <div class="icon-box">
            <ShopOutlined />
          </div>
          <div class="title-text">
            <span class="main-title">编辑店铺配置</span>
            <span class="sub-title">管理对接易仓的店铺关联信息</span>
          </div>
        </div>
      </template>

      <div class="modal-body-custom">
        <a-form layout="vertical">
          <a-form-item class="premium-form-item">
            <template #label>
              <span class="form-label-custom">
                易仓店铺代码 <span class="label-en">(Store Code)</span>
              </span>
            </template>
            <a-input 
              v-model:value="editingStore.eccangStoreCode" 
              placeholder="请输入易仓系统的 Store Code，如: DEFAULT" 
              class="premium-input-custom"
            >
              <template #prefix>
                <CloudServerOutlined style="color: #94a3b8;" />
              </template>
            </a-input>
            <div class="form-item-tip">易仓系统内配置的店铺代号，用于商品与订单的匹配对接</div>
          </a-form-item>

          <a-form-item class="premium-form-item">
            <template #label>
              <span class="form-label-custom">
                自定义显示名称 <span class="label-en">(Custom Name)</span>
              </span>
            </template>
            <a-input 
              v-model:value="editingStore.storeName" 
              placeholder="请输入方便识别的显示名称，如: 默认亚马逊店铺" 
              class="premium-input-custom"
            >
              <template #prefix>
                <SettingOutlined style="color: #94a3b8;" />
              </template>
            </a-input>
            <div class="form-item-tip">在系统列表中显示的友好名称，便于多店铺间区分识别</div>
          </a-form-item>
        </a-form>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { 
  CloudServerOutlined, 
  SettingOutlined, 
  KeyOutlined, 
  SafetyCertificateOutlined,
  ShopOutlined,
  SyncOutlined
} from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import { getEccangStores, getEccangConfig, saveEccangConfig, updateEccangStore } from '@/services/eccangService';
import type { EccangStoreConfig } from '@/services/eccangService';

const configForm = ref({
  appKey: '',
  appToken: ''
});

const saving = ref(false);
const syncingStores = ref(false);
const stores = ref<EccangStoreConfig[]>([]);

const expandedStores = ref<Record<number, boolean>>({});

const isExpanded = (storeId: number) => {
  return !!expandedStores.value[storeId];
};

const toggleExpand = (storeId: number) => {
  expandedStores.value[storeId] = !expandedStores.value[storeId];
};

const getVisibleAccounts = (record: EccangStoreConfig) => {
  if (!record.platformAccounts) return [];
  if (record.id === undefined || isExpanded(record.id)) return record.platformAccounts;
  return record.platformAccounts.slice(0, 8);
};

const getVisibleAccountCounts = (record: EccangStoreConfig) => {
  if (!record.accountCounts) return [];
  if (record.id === undefined || isExpanded(record.id)) return record.accountCounts;
  return record.accountCounts.slice(0, 8);
};

const parseAccount = (accountStr: string) => {
  const match = accountStr.match(/^\[(.*?)\]\s*(.*)$/);
  if (match) {
    return { site: match[1], name: match[2] };
  }
  return { site: '', name: accountStr };
};

const editModalOpen = ref(false);
const savingStore = ref(false);
const editingStore = ref<{ id?: number; storeName: string; eccangStoreCode: string }>({
  storeName: '',
  eccangStoreCode: ''
});

const openEditStore = (record: EccangStoreConfig) => {
  editingStore.value = {
    id: record.id,
    storeName: record.storeName || '',
    eccangStoreCode: record.eccangStoreCode || ''
  };
  editModalOpen.value = true;
};

const handleSaveStore = async () => {
  if (!editingStore.value.id) return;
  if (!editingStore.value.storeName.trim()) {
    message.warning('自定义店铺名称不能为空');
    return;
  }
  if (!editingStore.value.eccangStoreCode.trim()) {
    message.warning('易仓店铺名称 (Store Code) 不能为空');
    return;
  }
  savingStore.value = true;
  try {
    await updateEccangStore(editingStore.value.id, {
      storeName: editingStore.value.storeName.trim(),
      eccangStoreCode: editingStore.value.eccangStoreCode.trim()
    });
    message.success('保存店铺信息成功');
    editModalOpen.value = false;
    await loadStores();
  } catch (e) {
    message.error('保存店铺信息失败');
  } finally {
    savingStore.value = false;
  }
};

const columns = [
  { title: '平台', dataIndex: 'platform', key: 'platform', width: 90 },
  { title: '易仓店铺名称 (Store Code)', dataIndex: 'eccangStoreCode', key: 'eccangStoreCode', width: 220 },
  { title: '自定义店铺名称', dataIndex: 'storeName', key: 'storeName', width: 220 },
  { title: '平台账号 (已同步店铺)', dataIndex: 'platformAccounts', key: 'platformAccounts', width: 580 },
  { title: '在售商品', dataIndex: 'activeProductCount', key: 'activeProductCount', width: 120, align: 'center' },
  { title: '下架商品', dataIndex: 'inactiveProductCount', key: 'inactiveProductCount', width: 120, align: 'center' },
  { title: '总商品数', dataIndex: 'totalProductCount', key: 'totalProductCount', width: 120, align: 'center' },
  { title: '操作', key: 'action', width: 90, align: 'center' }
];

const loadConfig = async () => {
  try {
    const config = await getEccangConfig();
    if (config) {
      configForm.value.appKey = config.appKey || '';
      // We don't get the secret/token back from the server, but we know it's configured
      if (config.isConfigured) {
        configForm.value.appToken = '********'; // Masked placeholder
      }
    }
  } catch (e) {
    // Ignore
  }
};

const saveConfig = async () => {
  if (!configForm.value.appKey) {
    message.warning('请填写App Key');
    return;
  }
  saving.value = true;
  try {
    await saveEccangConfig({ 
      appKey: configForm.value.appKey, 
      appSecret: configForm.value.appToken === '********' ? undefined : configForm.value.appToken,
      accessToken: configForm.value.appToken === '********' ? undefined : configForm.value.appToken 
    });
    message.success('易仓授权配置已保存');
    await loadConfig();
  } catch (e) {
    message.error('保存失败');
  } finally {
    saving.value = false;
  }
};

const loadStores = async () => {
  stores.value = await getEccangStores();
};

const handleSyncStores = async () => {
  message.info('请先在易仓后台确认店铺，再在本系统「商品列表」选择店铺同步商品');
};

onMounted(() => {
  loadConfig();
  loadStores();
});
</script>

<style lang="scss" scoped>
.eccang-setting-page {
  height: auto;
  min-height: 100%;
  padding: 16px;
  padding-bottom: 40px;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  gap: 16px;

  .glass-card {
    background: rgba(255, 255, 255, 0.85);
    backdrop-filter: blur(8px);
    border: 1px solid rgba(255, 255, 255, 0.9);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);
    border-radius: 12px;
  }

  .card-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 15px;
    font-weight: 700;
    color: #1e293b;
  }

  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-left {
      display: flex;
      align-items: center;
      gap: 16px;

      .icon-wrapper {
        width: 56px;
        height: 56px;
        background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
        border-radius: 14px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        font-size: 28px;
        box-shadow: 0 8px 16px rgba(245, 158, 11, 0.3);
      }

      .header-text {
        .page-title {
          font-size: 22px;
          font-weight: 800;
          color: #1e293b;
          margin: 0 0 4px 0;
        }
        .page-desc {
          font-size: 13px;
          color: #64748b;
          margin: 0;
        }
      }
    }
  }

  .primary-gradient {
    background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
    border: none;
    color: #fff;
    box-shadow: 0 4px 12px rgba(245, 158, 11, 0.3);
    
    &:hover {
      box-shadow: 0 6px 16px rgba(245, 158, 11, 0.4);
      transform: translateY(-1px);
    }
  }

  .accounts-grid-wrapper {
    display: flex;
    flex-direction: column;
    gap: 8px;
    width: 100%;
    max-width: 650px;
  }

  .accounts-grid-table {
    display: flex;
    flex-direction: column;
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    overflow: hidden;
    background-color: #ffffff;
    font-size: 13px;
    width: 100%;

    .grid-header {
      display: grid;
      grid-template-columns: 70px 160px 100px 100px 100px;
      background-color: #f8fafc;
      border-bottom: 1px solid #e2e8f0;
      font-weight: 700;
      color: #475569;
      
      & > div {
        padding: 10px 12px;
      }
    }

    &.fallback-grid {
      .grid-header {
        grid-template-columns: 80px 1fr;
      }
      .grid-row {
        grid-template-columns: 80px 1fr;
      }
    }

    .grid-body {
      display: flex;
      flex-direction: column;
    }

    .grid-row {
      display: grid;
      grid-template-columns: 70px 160px 100px 100px 100px;
      border-bottom: 1px solid #f1f5f9;
      color: #334155;
      align-items: center;
      transition: background-color 0.15s ease;
      
      &:last-child {
        border-bottom: none;
      }
      
      &:hover {
        background-color: #f8fafc;
      }

      & > div {
        padding: 10px 12px;
      }
    }

    .site-badge {
      display: inline-block;
      background: linear-gradient(135deg, #3b82f6 0%, #1d4ed8 100%);
      color: #ffffff;
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 10px;
      font-weight: 700;
      text-transform: uppercase;
      text-align: center;
      line-height: 1.2;
      min-width: 32px;
    }

    .account-name-text {
      font-family: monospace;
      font-weight: 600;
      color: #1e293b;
      word-break: break-all;
    }

    .status-active {
      color: #10b981;
    }

    .status-inactive {
      color: #ef4444;
    }

    .status-total {
      color: #3b82f6;
    }

    .count-item-row {
      display: flex;
      justify-content: flex-start;
      align-items: center;
      line-height: 1.4;
      font-size: 11px;
      
      .label {
        color: #64748b;
        font-weight: 500;
        margin-right: 4px;
        min-width: 30px;
      }
      
      .val {
        font-weight: 700;
      }
    }
    
    .sku-count {
      margin-top: 2px;
      border-top: 1px dashed #f1f5f9;
      padding-top: 2px;
      
      .label {
        color: #94a3b8;
      }
      
      .val {
        color: #64748b;
      }
    }
  }

  .expand-btn-wrapper {
    display: flex;
    justify-content: flex-start;
    padding-top: 4px;
  }

  .expand-btn {
    display: inline-flex;
    align-items: center;
    padding: 3px 10px;
    border-radius: 12px;
    font-size: 11px;
    font-weight: 600;
    background-color: #f8fafc;
    color: #475569;
    border: 1px dashed #cbd5e1;
    transition: all 0.2s ease;
    cursor: pointer;
    user-select: none;
    
    &:hover {
      background-color: #f1f5f9;
      color: #1e293b;
      border-color: #94a3b8;
      transform: scale(1.03);
    }
  }

  .main-column-counts {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
    font-size: 12px;
    
    .status-active, .status-inactive, .status-total {
      display: flex;
      align-items: center;
      
      .label {
        color: #64748b;
        font-weight: 500;
        margin-right: 4px;
      }
      .val {
        font-weight: 700;
      }
    }

    .status-active {
      color: #10b981;
    }

    .status-inactive {
      color: #ef4444;
    }

    .status-total {
      color: #3b82f6;
    }
    
    .sku-count-sub {
      font-size: 11px;
      background-color: #f8fafc;
      padding: 1px 6px;
      border-radius: 4px;
      border: 1px solid #e2e8f0;
      display: inline-flex;
      align-items: center;
      
      .label {
        color: #94a3b8;
        font-weight: 500;
        margin-right: 4px;
      }
      
      .val {
        color: #64748b;
        font-weight: 600;
      }
    }
  }
}
</style>

<style lang="scss">
.premium-edit-modal {
  .ant-modal-content {
    border-radius: 16px;
    padding: 0;
    overflow: hidden;
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.12);
  }

  .ant-modal-header {
    background: #f8fafc;
    border-bottom: 1px solid #f1f5f9;
    padding: 20px 24px;
    margin: 0;
  }

  .ant-modal-body {
    padding: 24px;
  }

  .ant-modal-footer {
    background: #f8fafc;
    border-top: 1px solid #f1f5f9;
    padding: 16px 24px;
    margin: 0;
  }

  .modal-header-custom {
    display: flex;
    align-items: center;
    gap: 12px;

    .icon-box {
      width: 40px;
      height: 40px;
      border-radius: 10px;
      background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      color: #ffffff;
      font-size: 20px;
      box-shadow: 0 4px 10px rgba(245, 158, 11, 0.2);
    }

    .title-text {
      display: flex;
      flex-direction: column;

      .main-title {
        font-size: 16px;
        font-weight: 700;
        color: #1e293b;
        line-height: 1.4;
      }

      .sub-title {
        font-size: 12px;
        font-weight: 400;
        color: #64748b;
        line-height: 1.2;
        margin-top: 2px;
      }
    }
  }

  .modal-body-custom {
    .premium-form-item {
      margin-bottom: 20px;

      &:last-child {
        margin-bottom: 0;
      }
    }

    .form-label-custom {
      font-size: 14px;
      font-weight: 600;
      color: #334155;

      .label-en {
        font-size: 11px;
        color: #94a3b8;
        font-weight: 400;
      }
    }

    .premium-input-custom {
      height: 40px;
      border-radius: 8px;
      border: 1px solid #cbd5e1;
      font-size: 14px;
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.02);
      transition: all 0.2s ease;

      &:hover {
        border-color: #f59e0b;
      }

      &:focus, &.ant-input-focused {
        border-color: #d97706;
        box-shadow: 0 0 0 2px rgba(217, 119, 6, 0.15);
      }
      
      .ant-input {
        font-size: 14px;
      }
    }

    .form-item-tip {
      font-size: 12px;
      color: #94a3b8;
      margin-top: 6px;
      line-height: 1.4;
    }
  }
}
</style>

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
            <p class="page-desc">配置易仓 API 授权，绑定亚马逊店铺并同步商品与 FBA 库存</p>
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
        >
          <template #bodyCell="{ column, record }">
            <template v-if="column.key === 'platform'">
              <a-tag color="orange">Amazon</a-tag>
            </template>
          </template>
          <template #emptyText>
            <a-empty description="暂无绑定的店铺，请先配置授权并同步" />
          </template>
        </a-table>
      </div>
    </a-card>
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
import { getEccangStores, getEccangConfig, saveEccangConfig } from '@/services/eccangService';
import type { EccangStoreConfig } from '@/services/eccangService';

const configForm = ref({
  appKey: '',
  appToken: ''
});

const saving = ref(false);
const syncingStores = ref(false);
const stores = ref<EccangStoreConfig[]>([]);

const columns = [
  { title: '平台', dataIndex: 'platform', key: 'platform', width: 100 },
  { title: '易仓店铺名称 (Store Code)', dataIndex: 'eccangStoreCode', key: 'eccangStoreCode' },
  { title: '自定义店铺名称', dataIndex: 'storeName', key: 'storeName' }
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
  message.info('因没有易仓API文档，同步店铺暂时通过 mock 进行');
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
}
</style>

<template>
  <div class="storage-setting-page">
    <!-- 页面标题 -->
    <a-card :bordered="false" class="header-card glass-card" :body-style="{ padding: '20px 24px' }">
      <div class="page-header">
        <div class="header-left">
          <div class="icon-wrapper">
            <CloudServerOutlined />
          </div>
          <div class="header-text">
            <h1 class="page-title">存储配置</h1>
            <p class="page-desc">配置内容素材的存储方式，支持本地存储和云存储</p>
          </div>
        </div>
      </div>
    </a-card>

    <!-- 配置表单 -->
    <a-card :bordered="false" class="config-card glass-card" :body-style="{ padding: '24px' }">
      <a-spin :spinning="loading">
        <a-form
          :model="formData"
          :label-col="{ span: 6 }"
          :wrapper-col="{ span: 14 }"
          @finish="handleSave"
        >
          <!-- 存储类型 -->
          <a-form-item label="存储类型" name="storageType" :rules="[{ required: true }]">
            <a-radio-group v-model:value="formData.storageType" @change="handleTypeChange">
              <a-radio-button value="LOCAL">
                <FolderOutlined /> 本地存储
              </a-radio-button>
              <a-radio-button value="MINIO">
                <CloudOutlined /> MinIO 对象存储
              </a-radio-button>
            </a-radio-group>
          </a-form-item>

          <!-- 本地存储配置 -->
          <template v-if="formData.storageType === 'LOCAL'">
            <a-divider orientation="left">本地存储配置</a-divider>
            
            <a-form-item label="存储路径" name="basePath" :rules="[{ required: true, message: '请输入存储路径' }]">
              <a-input v-model:value="formData.basePath" placeholder="如: D:/content-files 或 /data/content-files">
                <template #prefix><FolderOpenOutlined /></template>
              </a-input>
              <div class="form-hint">
                <InfoCircleOutlined /> 
                <span>
                  服务器上用于存储文件的绝对路径。
                  <a-tooltip title="出于浏览器安全限制，无法直接通过网页选择服务器文件夹，请手动复制路径">
                    <span style="color: #64748b; cursor: help; border-bottom: 1px dashed #64748b">
                      为什么不能选择文件夹？
                    </span>
                  </a-tooltip>
                </span>
              </div>
            </a-form-item>

            <a-form-item label="公开访问 URL" name="publicUrlPrefix">
              <a-input v-model:value="formData.publicUrlPrefix" placeholder="如: http://localhost:8082/influencer-api/v1/files">
                <template #prefix><LinkOutlined /></template>
              </a-input>
              <div class="form-hint">
                <InfoCircleOutlined /> 文件对外访问的 URL 前缀
              </div>
            </a-form-item>
          </template>

          <!-- MinIO 配置 -->
          <template v-if="formData.storageType === 'MINIO'">
            <a-divider orientation="left">MinIO 对象存储配置</a-divider>

            <a-form-item label="Bucket 名称" name="basePath" :rules="[{ required: true, message: '请输入 Bucket 名称' }]">
              <a-input v-model:value="formData.basePath" placeholder="influencer-storage">
                <template #prefix><DatabaseOutlined /></template>
              </a-input>
              <div class="form-hint">
                <InfoCircleOutlined /> MinIO 中的存储桶名称，如不存在会在测试连接时自动创建
              </div>
            </a-form-item>

            <a-form-item label="Endpoint" name="endpoint" :rules="[{ required: true, message: '请输入 MinIO 服务地址' }]">
              <a-input v-model:value="formData.endpoint" placeholder="http://localhost:9000">
                <template #prefix><GlobalOutlined /></template>
              </a-input>
              <div class="form-hint">
                <InfoCircleOutlined /> MinIO 服务 API 地址（非控制台地址）
              </div>
            </a-form-item>

            <a-form-item label="Access Key" name="accessKey" :rules="[{ required: true, message: '请输入 Access Key' }]">
              <a-input v-model:value="formData.accessKey" placeholder="minioadmin">
                <template #prefix><KeyOutlined /></template>
              </a-input>
            </a-form-item>

            <a-form-item label="Secret Key" name="secretKey" :rules="[{ required: true, message: '请输入 Secret Key' }]">
              <a-input-password v-model:value="formData.secretKey" placeholder="请输入 MinIO Secret Key" />
            </a-form-item>

            <a-form-item label="Region" name="region">
              <a-input v-model:value="formData.region" placeholder="us-east-1（默认值，通常无需修改）" />
              <div class="form-hint">
                <InfoCircleOutlined /> MinIO 默认使用 us-east-1，一般无需修改
              </div>
            </a-form-item>

            <a-form-item label="CDN 域名 (可选)" name="cdnDomain">
              <a-input v-model:value="formData.cdnDomain" placeholder="https://cdn.example.com">
                <template #prefix><LinkOutlined /></template>
              </a-input>
              <div class="form-hint">
                <InfoCircleOutlined /> 如果通过 CDN 或 Nginx 代理访问 MinIO，请填写外部访问域名
              </div>
            </a-form-item>
          </template>

          <!-- 通用配置 -->
          <a-divider orientation="left">文件限制</a-divider>

          <a-form-item label="最大文件大小">
            <a-input-number 
              v-model:value="maxFileSizeMB" 
              :min="1" 
              :max="1024" 
              addon-after="MB"
              style="width: 200px;"
            />
          </a-form-item>

          <a-form-item label="允许的文件类型">
            <a-checkbox-group v-model:value="allowedTypesList">
              <a-checkbox value="image/*">图片 (image/*)</a-checkbox>
              <a-checkbox value="video/*">视频 (video/*)</a-checkbox>
              <a-checkbox value="application/pdf">PDF</a-checkbox>
            </a-checkbox-group>
          </a-form-item>

          <!-- 操作按钮 -->
          <a-form-item :wrapper-col="{ offset: 6, span: 14 }">
            <a-space>
              <a-button type="primary" html-type="submit" :loading="saving" class="primary-gradient">
                <SaveOutlined /> 保存配置
              </a-button>
              <a-button @click="handleTest" :loading="testing">
                <ApiOutlined /> 测试连接
              </a-button>
            </a-space>
          </a-form-item>
        </a-form>
      </a-spin>
    </a-card>

    <!-- 使用说明 -->
    <a-card :bordered="false" class="help-card glass-card" :body-style="{ padding: '20px 24px' }">
      <template #title>
        <div class="card-title">
          <QuestionCircleOutlined />
          <span>配置说明</span>
        </div>
      </template>
      <a-row :gutter="24">
        <a-col :xs="24" :md="12">
          <h4>本地存储</h4>
          <ul>
            <li>存储路径必须是服务器可写的目录</li>
            <li>建议使用绝对路径（如 <code>/opt/influencer/media</code>）</li>
            <li>公开 URL 前缀用于生成文件访问链接</li>
          </ul>
        </a-col>
        <a-col :xs="24" :md="12">
          <h4>MinIO 对象存储</h4>
          <ul>
            <li>MinIO 控制台地址: <code>http://服务器IP:9001</code></li>
            <li>API 地址 (Endpoint): <code>http://服务器IP:9000</code></li>
            <li>Bucket 不存在时会在「测试连接」时自动创建</li>
            <li>建议通过 Nginx 反向代理对外提供文件访问</li>
          </ul>
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import {
  CloudServerOutlined,
  FolderOutlined,
  FolderOpenOutlined,
  CloudOutlined,
  LinkOutlined,
  InfoCircleOutlined,
  SaveOutlined,
  ApiOutlined,
  QuestionCircleOutlined,
  DatabaseOutlined,
  GlobalOutlined,
  KeyOutlined
} from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import { influencerApiHttp as influencerHttp } from '@/services/influencerService';

interface StorageConfig {
  id?: number;
  storageType: string;
  basePath: string;
  publicUrlPrefix?: string;
  accessKey?: string;
  secretKey?: string;
  region?: string;
  endpoint?: string;
  cdnDomain?: string;
  maxFileSize?: number;
  allowedTypes?: string;
  isActive?: boolean;
}

const loading = ref(false);
const saving = ref(false);
const testing = ref(false);

const formData = ref<StorageConfig>({
  storageType: 'LOCAL',
  basePath: '',
  publicUrlPrefix: '',
  maxFileSize: 104857600,
  allowedTypes: 'image/*,video/*,application/pdf'
});

// 计算属性：MB 和字节转换
const maxFileSizeMB = computed({
  get: () => Math.round((formData.value.maxFileSize || 104857600) / (1024 * 1024)),
  set: (val) => { formData.value.maxFileSize = val * 1024 * 1024; }
});

// 计算属性：允许类型列表
const allowedTypesList = computed({
  get: () => (formData.value.allowedTypes || '').split(',').filter(Boolean),
  set: (val) => { formData.value.allowedTypes = val.join(','); }
});

// 加载配置
const fetchConfig = async () => {
  loading.value = true;
  try {
    const res = await influencerHttp.get('/v1/storage/config');
    if (res.data) {
      formData.value = res.data;
    }
  } catch (e) {
    // 可能是首次配置，使用默认值

  } finally {
    loading.value = false;
  }
};

// 保存配置
const handleSave = async () => {
  saving.value = true;
  try {
    await influencerHttp.put('/v1/storage/config', formData.value);
    message.success('配置保存成功');
  } catch (e: any) {
    const errorMsg = e.response?.data?.message || e.message || '保存失败';
    message.error(`保存失败: ${errorMsg}`);
  } finally {
    saving.value = false;
  }
};

// 测试连接
const handleTest = async () => {
  testing.value = true;
  try {
    const res = await influencerHttp.post('/v1/storage/test', formData.value);
    if (res.data?.success) {
      message.success('连接测试成功');
    } else {
      message.error(res.data?.message || '连接测试失败');
    }
  } catch (e: any) {
    const errorMsg = e.response?.data?.message || e.message || '测试失败';
    message.error(`测试失败: ${errorMsg}`);
  } finally {
    testing.value = false;
  }
};

// 切换存储类型时重置部分字段
const handleTypeChange = () => {
  if (formData.value.storageType === 'LOCAL') {
    formData.value.basePath = '';
    formData.value.publicUrlPrefix = '';
  } else if (formData.value.storageType === 'MINIO') {
    formData.value.basePath = '';
    formData.value.publicUrlPrefix = '';
    formData.value.endpoint = '';
    formData.value.region = 'us-east-1';
  }
};

onMounted(() => {
  fetchConfig();
});
</script>

<style lang="scss" scoped>
.storage-setting-page {
  height: 100%;
  padding: 8px;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  gap: 12px;
  overflow: auto;

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
        background: linear-gradient(135deg, #10b981 0%, #047857 100%);
        border-radius: 14px;
        display: flex;
        align-items: center;
        justify-content: center;
        color: #fff;
        font-size: 28px;
        box-shadow: 0 8px 16px rgba(16, 185, 129, 0.3);
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

  .form-hint {
    margin-top: 8px;
    font-size: 12px;
    color: #94a3b8;
    display: flex;
    align-items: center;
    gap: 4px;
  }

  .primary-gradient {
    background: linear-gradient(135deg, #10b981 0%, #047857 100%);
    border: none;
    color: #fff;
    box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
    
    &:hover {
      box-shadow: 0 6px 16px rgba(16, 185, 129, 0.4);
      transform: translateY(-1px);
    }
  }

  .help-card {
    h4 {
      font-size: 14px;
      font-weight: 700;
      color: #1e293b;
      margin-bottom: 12px;
    }

    ul {
      padding-left: 20px;
      margin: 0;
      
      li {
        font-size: 13px;
        color: #475569;
        line-height: 1.8;
        
        code {
          background: #f1f5f9;
          padding: 2px 6px;
          border-radius: 4px;
          font-size: 12px;
        }
      }
    }
  }
}
</style>

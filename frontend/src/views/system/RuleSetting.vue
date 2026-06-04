<template>
  <div class="rule-setting-page">
    <a-card :bordered="false" class="table-card" :body-style="{ padding: '8px' }">
      <template #title>
        <div style="display: flex; justify-content: space-between; align-items: center; gap: 16px;">
          <a-tabs v-model:activeKey="activeKey" @change="handleTabChange" style="margin: 0; flex: 1; min-width: 0;">
            <a-tab-pane key="dormant" tab="休眠规则" />
            <a-tab-pane key="commission" tab="分佣规则" />
          </a-tabs>
        </div>
      </template>
      
      <div class="rule-container">
        <div class="rule-main-content">
          <!-- 休眠规则 -->
          <div v-if="activeKey === 'dormant'" class="rule-content">
            <div class="rule-header">
              <span class="rule-header-label">规则开关</span>
              <a-switch v-model:checked="dormantRuleEnabled" @change="handleDormantRuleToggle" />
            </div>
            
            <a-form :model="dormantForm" layout="vertical" :disabled="!dormantRuleEnabled" class="rule-form">
          <a-row :gutter="24">
            <a-col :span="12">
              <a-form-item label="当前没有进行中的样品单">
                <a-checkbox v-model:checked="dormantForm.noOngoingSampleOrder" :disabled="!dormantRuleEnabled">
                  启用此条件
                </a-checkbox>
              </a-form-item>
            </a-col>
            
            <a-col :span="12">
              <a-form-item label="当前没有进行中的内容上传任务">
                <a-checkbox v-model:checked="dormantForm.noOngoingUploadTask" :disabled="!dormantRuleEnabled">
                  启用此条件
                </a-checkbox>
              </a-form-item>
            </a-col>
          </a-row>
          
          <a-row :gutter="24">
            <a-col :span="12">
              <a-form-item label="红人最近一次样品单时间">
                <div class="form-item-row">
                  <a-checkbox v-model:checked="dormantForm.sampleOrderTimeEnabled" :disabled="!dormantRuleEnabled">
                    大于
                  </a-checkbox>
                  <a-input-number 
                    v-model:value="dormantForm.sampleOrderDays" 
                    :min="1" 
                    :max="9999"
                    :disabled="!dormantRuleEnabled || !dormantForm.sampleOrderTimeEnabled"
                    class="number-input"
                  />
                  <span class="form-item-desc">天（取最后一旦妥投时间）</span>
                </div>
              </a-form-item>
            </a-col>
            
            <a-col :span="12">
              <a-form-item label="红人最近一次转化单时间">
                <div class="form-item-row">
                  <a-checkbox v-model:checked="dormantForm.conversionOrderTimeEnabled" :disabled="!dormantRuleEnabled">
                    大于
                  </a-checkbox>
                  <a-input-number 
                    v-model:value="dormantForm.conversionOrderDays" 
                    :min="1" 
                    :max="9999"
                    :disabled="!dormantRuleEnabled || !dormantForm.conversionOrderTimeEnabled"
                    class="number-input"
                  />
                  <span class="form-item-desc">天（取最新的付款时间）</span>
                </div>
              </a-form-item>
            </a-col>
          </a-row>
          
          <a-row :gutter="24">
            <a-col :span="12">
              <a-form-item label="红人最近一次上传内容">
                <div class="form-item-row">
                  <a-checkbox v-model:checked="dormantForm.uploadContentTimeEnabled" :disabled="!dormantRuleEnabled">
                    大于
                  </a-checkbox>
                  <a-input-number 
                    v-model:value="dormantForm.uploadContentDays" 
                    :min="1" 
                    :max="9999"
                    :disabled="!dormantRuleEnabled || !dormantForm.uploadContentTimeEnabled"
                    class="number-input"
                  />
                  <span class="form-item-desc">天（取最新的内容上传时间）</span>
                </div>
              </a-form-item>
            </a-col>
            
            <a-col :span="12">
              <a-form-item label="手动移除休眠库">
                <div class="form-item-row">
                  <a-checkbox v-model:checked="dormantForm.manualRemoveEnabled" :disabled="!dormantRuleEnabled">
                    大于
                  </a-checkbox>
                  <a-input-number 
                    v-model:value="dormantForm.manualRemoveDays" 
                    :min="1" 
                    :max="9999"
                    :disabled="!dormantRuleEnabled || !dormantForm.manualRemoveEnabled"
                    class="number-input"
                  />
                  <span class="form-item-desc">天</span>
                </div>
              </a-form-item>
            </a-col>
          </a-row>
          
          <a-form-item class="form-actions">
              <a-button type="primary" @click="handleDormantSave" :disabled="!dormantRuleEnabled">保存</a-button>
            </a-form-item>
            </a-form>
          </div>

          <!-- 分佣规则 -->
          <div v-if="activeKey === 'commission'" class="rule-content">
            <div class="rule-header">
              <span class="rule-header-label">规则开关</span>
              <a-switch v-model:checked="commissionRuleEnabled" @change="handleCommissionRuleToggle" />
            </div>
            
            <a-alert 
              v-if="!commissionRuleEnabled"
              message="分佣规则已关闭，系统将暂停分佣"
              type="warning"
              show-icon
              class="rule-alert"
            />
            
            <a-form :model="commissionForm" layout="vertical" :disabled="!commissionRuleEnabled" class="rule-form">
              <a-row :gutter="24">
                <a-col :span="12">
                  <a-form-item label="订单状态为">
                    <div class="form-item-row">
                      <a-select 
                        v-model:value="commissionForm.orderStatus" 
                        placeholder="请选择订单状态"
                        class="select-input"
                        :disabled="!commissionRuleEnabled"
                      >
                        <a-select-option value="paid">已付款</a-select-option>
                        <a-select-option value="shipped">已发货</a-select-option>
                        <a-select-option value="delivered">已妥投</a-select-option>
                        <a-select-option value="completed">已完成</a-select-option>
                      </a-select>
                      <a-input-number 
                        v-model:value="commissionForm.statusDays" 
                        :min="1" 
                        :max="9999"
                        :disabled="!commissionRuleEnabled"
                        class="number-input"
                      />
                      <span class="form-item-desc">天后</span>
                    </div>
                  </a-form-item>
                </a-col>
                
                <a-col :span="12">
                  <a-form-item label="订单状态恢复正常后">
                    <div class="form-item-row">
                      <a-input-number 
                        v-model:value="commissionForm.recoveryDays" 
                        :min="1" 
                        :max="9999"
                        :disabled="!commissionRuleEnabled"
                        class="number-input"
                      />
                      <span class="form-item-desc">天后进行分佣</span>
                    </div>
                  </a-form-item>
                </a-col>
              </a-row>
              
              <a-row :gutter="24">
                <a-col :span="12">
                  <a-form-item label="订单处于未分佣的状态">
                    <a-checkbox v-model:checked="commissionForm.uncommissionedOnly" :disabled="!commissionRuleEnabled">
                      仅处理未分佣的订单
                    </a-checkbox>
                  </a-form-item>
                </a-col>
              </a-row>
              
              <a-form-item class="form-actions">
                  <a-button type="primary" @click="handleCommissionSave" :disabled="!commissionRuleEnabled">保存</a-button>
              </a-form-item>
            </a-form>
          </div>
        </div>
        
        <!-- 右侧装饰区域 -->
        <div class="rule-sidebar">
          <div class="sidebar-card">
            <div class="sidebar-icon">⚙️</div>
            <h3>规则说明</h3>
            <div class="sidebar-content" v-if="activeKey === 'dormant'">
              <p>休眠规则用于自动识别长时间未活跃的红人，系统会根据设定的条件自动将符合条件的红人标记为休眠状态。</p>
              <ul>
                <li>所有启用的条件需要同时满足</li>
                <li>时间条件以最后一次相关操作为准</li>
                <li>关闭规则后，系统将停止自动识别</li>
              </ul>
            </div>
            <div class="sidebar-content" v-else>
              <p>分佣规则用于控制订单分佣的时机和条件，系统会根据设定的规则自动进行分佣操作。</p>
              <ul>
                <li>订单取消中、已取消状态均不分佣</li>
                <li>退货退款订单不参与分佣</li>
                <li>部分退款：完成部分退款后分佣，只分剩余金额（扣除退款金额）。如果整单退款或取消，则不分佣</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { message, Modal } from 'ant-design-vue';
import { influencerApiHttp as influencerHttp } from '@/services/influencerService';

const activeKey = ref('dormant');

// 休眠红人规则
const dormantRuleEnabled = ref(true);
const dormantForm = reactive({
  noOngoingSampleOrder: true,
  noOngoingUploadTask: true,
  sampleOrderTimeEnabled: true,
  sampleOrderDays: 30,
  conversionOrderTimeEnabled: true,
  conversionOrderDays: 30,
  uploadContentTimeEnabled: true,
  uploadContentDays: 30,
  manualRemoveEnabled: true,
  manualRemoveDays: 60,
});

// 分佣规则
const commissionRuleEnabled = ref(true);
const commissionForm = reactive({
  orderStatus: 'delivered',
  statusDays: 7,
  uncommissionedOnly: true,
  recoveryDays: 3,
});

// 加载休眠规则配置
const loadDormancyRules = async () => {
  try {
    const response = await influencerHttp.get('/v1/influencer/settings/dormancy_rules');
    if (response.data && response.data.settingValue) {
      const rules = JSON.parse(response.data.settingValue);
      dormantRuleEnabled.value = rules.enabled !== false;
      dormantForm.noOngoingSampleOrder = rules.noPendingSample !== false;
      dormantForm.noOngoingUploadTask = rules.noPendingContent !== false;
      dormantForm.sampleOrderDays = rules.lastSampleDays || 30;
      dormantForm.conversionOrderDays = rules.lastConversionDays || 30;
      dormantForm.uploadContentDays = rules.lastContentDays || 30;
      dormantForm.manualRemoveDays = rules.manualRestoreGraceDays || 60;
      dormantForm.sampleOrderTimeEnabled = rules.lastSampleDays > 0;
      dormantForm.conversionOrderTimeEnabled = rules.lastConversionDays > 0;
      dormantForm.uploadContentTimeEnabled = rules.lastContentDays > 0;
      dormantForm.manualRemoveEnabled = rules.manualRestoreGraceDays > 0;
    }
  } catch (error) {
    console.warn('Failed to load dormancy rules', error);
  }
};

// 保存休眠规则到后端
const saveDormancyRules = async () => {
  const rules = {
    enabled: dormantRuleEnabled.value,
    noPendingSample: dormantForm.noOngoingSampleOrder,
    noPendingContent: dormantForm.noOngoingUploadTask,
    lastSampleDays: dormantForm.sampleOrderTimeEnabled ? dormantForm.sampleOrderDays : 0,
    lastConversionDays: dormantForm.conversionOrderTimeEnabled ? dormantForm.conversionOrderDays : 0,
    lastContentDays: dormantForm.uploadContentTimeEnabled ? dormantForm.uploadContentDays : 0,
    manualRestoreGraceDays: dormantForm.manualRemoveEnabled ? dormantForm.manualRemoveDays : 0,
  };
  
  await influencerHttp.put('/v1/influencer/settings/dormancy_rules', {
    settingKey: 'dormancy_rules',
    settingValue: JSON.stringify(rules),
  });
};

// 加载分佣规则配置
const loadCommissionRules = async () => {
  try {
    const response = await influencerHttp.get('/v1/influencer/settings/commission_rules');
    if (response.data && response.data.settingValue) {
      const rules = JSON.parse(response.data.settingValue);
      commissionRuleEnabled.value = rules.enabled !== false;
      commissionForm.orderStatus = rules.orderStatus || 'delivered';
      commissionForm.statusDays = rules.statusDays || 7;
      commissionForm.recoveryDays = rules.recoveryDays || 3;
      commissionForm.uncommissionedOnly = rules.uncommissionedOnly !== false;
    }
  } catch (error) {
    console.warn('Failed to load commission rules', error);
  }
};

// 保存分佣规则到后端
const saveCommissionRules = async () => {
  const rules = {
    enabled: commissionRuleEnabled.value,
    orderStatus: commissionForm.orderStatus,
    statusDays: commissionForm.statusDays,
    recoveryDays: commissionForm.recoveryDays,
    uncommissionedOnly: commissionForm.uncommissionedOnly,
  };
  
  await influencerHttp.put('/v1/influencer/settings/commission_rules', {
    settingKey: 'commission_rules',
    settingValue: JSON.stringify(rules),
  });
};

onMounted(() => {
  loadDormancyRules();
  loadCommissionRules();
});

const handleDormantRuleToggle = (checked: boolean) => {
  if (!checked) {
    Modal.confirm({
      title: '确认关闭',
      content: '关闭休眠规则后，系统将不再自动识别休眠红人，确定要关闭吗？',
      onOk: async () => {
        dormantRuleEnabled.value = false;
        await saveDormancyRules();
        message.info('休眠规则已关闭');
      },
      onCancel: () => {
        dormantRuleEnabled.value = true;
      },
    });
  } else {
    dormantRuleEnabled.value = true;
    saveDormancyRules();
    message.success('休眠规则已开启');
  }
};

const handleCommissionRuleToggle = (checked: boolean) => {
  if (!checked) {
    Modal.confirm({
      title: '确认关闭',
      content: '关闭分佣规则后，系统将暂停分佣，确定要关闭吗？',
      onOk: async () => {
        commissionRuleEnabled.value = false;
        await saveCommissionRules();
        message.warning('分佣规则已关闭，系统将暂停分佣');
      },
      onCancel: () => {
        commissionRuleEnabled.value = true;
      },
    });
  } else {
    commissionRuleEnabled.value = true;
    saveCommissionRules();
    message.success('分佣规则已开启');
  }
};

const handleDormantSave = async () => {
  // 验证必填项
  if (dormantForm.sampleOrderTimeEnabled && !dormantForm.sampleOrderDays) {
    message.warning('请填写样品单时间天数');
    return;
  }
  if (dormantForm.conversionOrderTimeEnabled && !dormantForm.conversionOrderDays) {
    message.warning('请填写转化单时间天数');
    return;
  }
  if (dormantForm.uploadContentTimeEnabled && !dormantForm.uploadContentDays) {
    message.warning('请填写上传内容时间天数');
    return;
  }
  if (dormantForm.manualRemoveEnabled && !dormantForm.manualRemoveDays) {
    message.warning('请填写手动移除休眠库天数');
    return;
  }
  
  try {
    await saveDormancyRules();
    message.success('保存成功');
  } catch (error) {
    message.error('保存失败');
  }
};

const handleDormantConfirm = () => {
  Modal.confirm({
    title: '确认规则',
    content: '确认应用当前休眠规则吗？',
    onOk: async () => {
      await handleDormantSave();
      message.success('规则已确认并保存');
    },
  });
};

const handleCommissionSave = async () => {
  // 验证必填项
  if (!commissionForm.orderStatus) {
    message.warning('请选择订单状态');
    return;
  }
  if (!commissionForm.statusDays) {
    message.warning('请填写状态天数');
    return;
  }
  if (!commissionForm.recoveryDays) {
    message.warning('请填写恢复正常后天数');
    return;
  }
  
  try {
    await saveCommissionRules();
    message.success('保存成功');
  } catch (error) {
    message.error('保存失败');
  }
};

const handleCommissionConfirm = () => {
  Modal.confirm({
    title: '确认规则',
    content: '确认应用当前分佣规则吗？',
    onOk: () => {
      handleCommissionSave();
      message.success('规则已确认并保存');
    },
  });
};

const handleTabChange = () => {
  // 切换标签页时的处理
};
</script>

<style lang="scss" scoped>
.rule-setting-page {
  .table-card {
    :deep(.ant-card-head) {
      min-height: 40px;
      padding: 4px 16px;
    }
    
    :deep(.ant-card-head-title) {
      padding: 4px 0;
    }
    
    :deep(.ant-tabs) {
      .ant-tabs-tab {
        padding: 8px 16px;
      }
    }
  }
  
  .rule-container {
    display: flex !important;
    gap: 24px;
    padding: 16px;
    align-items: stretch;
  }
  
  .rule-main-content {
    flex: 1;
    min-width: 0;
    display: flex;
    flex-direction: column;
  }
  
  .rule-content {
    padding: 24px;
    background: #fff;
    border-radius: 8px;
    border: 1px solid #f0f0f0;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    flex: 1;
    display: flex;
    flex-direction: column;
    min-height: 100%;
  }
  
  .rule-sidebar {
    width: 320px;
    flex-shrink: 0;
    display: flex;
    flex-direction: column;
  }
  
  .sidebar-card {
    padding: 24px;
    background: #f0f9ff;
    border-radius: 8px;
    border: 1px solid #bae6fd;
    box-shadow: none;
    color: #334155;
    flex: 1;
    display: flex;
    flex-direction: column;
    height: 100%;
    
    .sidebar-icon {
      font-size: 48px;
      text-align: center;
      margin-bottom: 16px;
      color: #0ea5e9;
    }
    
    h3 {
      font-size: 18px;
      font-weight: 600;
      color: #0c4a6e;
      margin-bottom: 16px;
      text-align: center;
    }
    
    .sidebar-content {
      color: #334155;
      font-size: 14px;
      line-height: 1.8;
      
      p {
        margin-bottom: 16px;
        color: #0c4a6e;
        font-weight: 500;
      }
      
      ul {
        margin: 0;
        padding-left: 20px;
        
        li {
          margin-bottom: 8px;
          color: #334155;
          
          &:last-child {
            margin-bottom: 0;
          }
        }
      }
    }
  }
  
  .rule-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid #f0f0f0;
    
    .rule-header-label {
      font-size: 14px;
      color: #666;
      font-weight: 500;
    }
  }
  
  .rule-alert {
    margin-bottom: 24px;
  }
  
  .rule-form {
    flex: 1;
    display: flex;
    flex-direction: column;
    
    :deep(.ant-form-item) {
      margin-bottom: 20px;
    }
    
    :deep(.ant-form-item-label) {
      padding-bottom: 8px;
      label {
        font-weight: 500;
        color: #333;
      }
    }
    
    :deep(.ant-row) {
      margin-bottom: 0;
    }
    
    .form-item-row {
      display: flex;
      align-items: center;
      gap: 12px;
      flex-wrap: wrap;
    }
    
    .number-input {
      width: 120px;
    }
    
    .select-input {
      width: 200px;
    }
    
    .form-item-desc {
      color: #666;
      font-size: 14px;
      white-space: nowrap;
    }
    
    .form-actions {
      margin-top: auto;
      padding-top: 24px;
      border-top: 1px solid #f0f0f0;
      margin-bottom: 0;
    }
  }
  
  .sidebar-content {
    flex: 1;
    display: flex;
    flex-direction: column;
  }
}
</style>


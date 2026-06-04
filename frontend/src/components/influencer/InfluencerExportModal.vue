<template>
  <a-modal
    v-model:open="visible"
    :title="null"
    width="820px"
    :footer="null"
    centered
    class="influencer-export-modal"
    :mask-closable="false"
    :closable="false"
    destroy-on-close
  >
    <!-- Header -->
    <div class="ic-modal-header glass-header">
      <div class="ic-header-left">
        <div class="ic-header-icon-wrapper">
          <ExportOutlined />
        </div>
        <div class="ic-header-text">
          <div class="ic-header-title">自定义字段导出</div>
          <div class="ic-header-subtitle">选择要导出的字段并调整顺序</div>
        </div>
      </div>
      <a-button type="text" class="close-btn" @click="handleCancel">
        <CloseOutlined />
      </a-button>
    </div>

    <!-- 内容 -->
    <div class="ic-modal-body">
      <div class="export-modal-content">
        <!-- 导出范围选择区域 -->
        <div class="ic-form-section">
          <div class="ic-form-section-title">导出范围</div>
          <div class="ic-section-body">
            <div class="export-scope-cards">
              <div 
                class="export-scope-card" 
                :class="{ 'export-scope-card-selected': exportScope === 'selected' }"
                @click="exportScope = 'selected'"
              >
                <div class="export-scope-card-content">
                  <div class="export-scope-card-title">导出选中数据</div>
                  <div class="export-scope-card-info">
                    已选择 <span :class="{ 'text-warning': selectedCount === 0 }">{{ selectedCount }}</span> 条数据
                  </div>
                </div>
              </div>
              <div 
                class="export-scope-card" 
                :class="{ 'export-scope-card-selected': exportScope === 'all' }"
                @click="exportScope = 'all'"
              >
                <div class="export-scope-card-content">
                  <div class="export-scope-card-title">导出全部数据</div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 模板选择区域 -->
        <div class="ic-form-section">
          <div class="ic-form-section-title">模板管理</div>
          <div class="ic-section-body">
            <div class="export-template-row">
              <a-select
                v-model:value="currentTemplateId"
                placeholder="选择导出模板"
                allow-clear
                @change="handleTemplateChange"
                class="export-template-select"
              >
                <template v-if="myTemplates.length > 0">
                  <a-select-opt-group label="我的模板">
                    <a-select-option v-for="template in myTemplates" :key="template.id" :value="template.id">
                      <span>{{ template.name }}</span>
                      <a-tag v-if="template.isPublic" :bordered="false" color="success" style="margin-left: 8px; font-size: 10px; line-height: 16px;">全员公开</a-tag>
                      <a-tag v-else-if="template.sharedUserIds?.length" :bordered="false" color="processing" style="margin-left: 8px; font-size: 10px; line-height: 16px;">已分享</a-tag>
                    </a-select-option>
                  </a-select-opt-group>
                </template>
                <template v-if="sharedDropdownTemplates.length > 0">
                  <a-select-opt-group label="分享给我的模板">
                    <a-select-option v-for="template in sharedDropdownTemplates" :key="template.id" :value="template.id">
                      <span style="font-weight: 500;">{{ template.name }}</span>
                      <a-tag :bordered="false" color="purple" style="margin-left: 8px; font-size: 10px; line-height: 16px;">
                        来自: {{ template.userName || '未知' }}
                      </a-tag>
                    </a-select-option>
                  </a-select-opt-group>
                </template>
                <template v-if="myTemplates.length === 0 && sharedTemplates.length === 0">
                  <a-select-option value="" disabled>暂无模板</a-select-option>
                </template>
              </a-select>
              <a-button @click="openTemplateManageModal" class="export-template-btn">模板管理</a-button>
              <a-button type="primary" @click="openSaveTemplateModal" class="export-template-btn">新增模板</a-button>
            </div>
          </div>
        </div>
        
        <!-- 字段选择区域 -->
        <div class="ic-form-section">
          <div class="ic-section-header-row">
            <div class="ic-form-section-title">字段选择</div>
            <div class="ic-section-extra">
              <a-checkbox
                :checked="allFieldsSelected"
                :indeterminate="hasIndeterminateSelection"
                @change="handleSelectAll"
                :disabled="!isMyTemplate"
              >
                全选
              </a-checkbox>
              <a-button 
                type="link" 
                size="small" 
                @click="handleResetExportFields" 
                :disabled="!isMyTemplate"
                style="padding: 0; height: auto; margin-left: 12px; font-size: 12px;"
              >
                重置为默认
              </a-button>
            </div>
          </div>
          <div class="field-selection-container">
            <a-checkbox-group v-model:value="selectedExportFields" :disabled="!isMyTemplate" style="width: 100%">
              <a-row :gutter="[8, 8]">
                <a-col :span="6" v-for="field in exportFields" :key="field.key">
                  <div class="field-checkbox-item" :class="{ 'checked': selectedExportFields.includes(field.key) }">
                    <a-checkbox :value="field.key" :disabled="!isMyTemplate">{{ field.title }}</a-checkbox>
                  </div>
                </a-col>
              </a-row>
            </a-checkbox-group>
            <div v-if="!isMyTemplate" class="export-tip-text">
              <info-circle-outlined /> 使用他人模板时不可修改字段和顺序
            </div>
          </div>
        </div>
        
        <!-- 已选字段顺序配置（横向拖拽） -->
        <div class="ic-form-section order-section" v-if="selectedExportFields.length > 0">
          <div class="ic-form-section-title">已选字段顺序 (左右拖拽调整)</div>
          <div class="horizontal-drag-container">
            <div class="horizontal-drag-list">
              <div
                v-for="(fieldKey, index) in orderedSelectedFields"
                :key="fieldKey"
                class="field-chip"
                :class="{ 
                  'dragging': dragIndex === index, 
                  'drag-over': dragOverIndex === index,
                  'disabled': !isMyTemplate
                }"
                :draggable="isMyTemplate"
                @dragstart="isMyTemplate ? handleDragStart($event, index) : null"
                @dragover.prevent="isMyTemplate ? handleDragOver($event, index) : null"
                @drop="isMyTemplate ? handleDrop($event, index) : null"
                @dragend="isMyTemplate ? handleDragEnd() : null"
              >
                <div class="chip-content">
                  <span v-if="isMyTemplate" class="chip-handle">⣿</span>
                  <span class="chip-label">{{ getFieldTitle(fieldKey) }}</span>
                  <CloseOutlined 
                    v-if="isMyTemplate"
                    class="chip-remove" 
                    @click.stop="handleRemoveField(fieldKey)"
                  />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Footer -->
    <div class="ic-modal-footer">
      <a-space size="middle">
        <a-button @click="handleCancel" class="premium-btn">取消</a-button>
        <a-button 
          v-if="isMyTemplate && currentTemplateId" 
          type="default" 
          @click="openSaveTemplateModal"
          class="premium-btn"
        >
          {{ isEditingTemplate ? '更新模板' : '保存模板' }}
        </a-button>
        <a-button type="primary" @click="handleConfirmExport" class="premium-btn primary-gradient-blue">导出数据</a-button>
      </a-space>
    </div>
  </a-modal>

  <!-- 保存/编辑模板弹窗 -->
  <a-modal
    v-model:open="saveTemplateModalVisible"
    :title="null"
    :footer="null"
    width="500px"
    centered
    class="premium-sub-modal"
    :closable="false"
    :body-style="{ padding: '0' }"
    :z-index="1050"
  >
    <div class="sub-modal-header">
      <div class="sub-header-title">
        <copy-outlined v-if="!isEditingTemplate" />
        <edit-outlined v-else />
        {{ isEditingTemplate ? '编辑导出模板' : '保存导出模板' }}
      </div>
      <a-button type="text" class="sub-close-btn" @click="handleCancelSaveTemplate">
        <close-outlined />
      </a-button>
    </div>
    
    <div class="sub-modal-body">
      <a-form :model="templateForm" layout="vertical" class="premium-form">
        <a-form-item label="模板名称" required>
          <a-input v-model:value="templateForm.name" placeholder="请输入模板名称" class="premium-input-field" />
        </a-form-item>
        
        <div class="share-settings-box">
          <a-form-item class="compact-form-item">
            <a-checkbox v-model:checked="templateForm.isPublic" class="premium-checkbox">
              <span class="share-label">公开分享 <span class="share-hint">(所有人可见)</span></span>
            </a-checkbox>
          </a-form-item>
          
          <a-form-item label="分享给指定用户" v-if="!templateForm.isPublic">
            <a-select
              v-model:value="templateForm.sharedUserIds"
              mode="multiple"
              placeholder="选择要分享的用户 (可选)"
              :options="allUsers.map((u: { id: string; name: string }) => ({ label: u.name, value: u.id }))"
              class="premium-select-field"
            />
          </a-form-item>
        </div>
      </a-form>
    </div>

    <div class="sub-modal-footer">
      <a-space size="middle">
        <a-button @click="handleCancelSaveTemplate" class="cancel-btn">取消</a-button>
        <a-button type="primary" @click="handleSaveTemplate" class="confirm-btn">
          {{ isEditingTemplate ? '更新模板' : '立即保存' }}
        </a-button>
      </a-space>
    </div>
  </a-modal>

  <!-- 模板管理弹窗 -->
  <a-modal
    v-model:open="templateManageModalVisible"
    :title="null"
    :footer="null"
    width="800px"
    centered
    class="template-manage-modal premium-sub-modal"
    :closable="false"
    :body-style="{ padding: '0' }"
    :z-index="1050"
  >
    <div class="sub-modal-header">
      <div class="sub-header-title">
        <appstore-outlined /> 模板列表管理
      </div>
      <a-button type="text" class="sub-close-btn" @click="templateManageModalVisible = false">
        <close-outlined />
      </a-button>
    </div>

    <div class="sub-modal-body manage-body" style="padding: 10px 24px 24px;">
      <a-tabs>
        <a-tab-pane key="my-templates" tab="我的模板">
          <div class="template-list-scroller">
            <div
              v-for="template in myTemplates"
              :key="template.id"
              class="manage-template-item"
              :class="{ 'active': currentTemplateId === template.id }"
            >
              <div class="item-main">
                <div class="item-header-row">
                  <div class="item-title-group">
                    <span class="item-name">{{ template.name }}</span>
                    <a-tag v-if="template.isPublic" color="green" class="small-tag">公开</a-tag>
                    <a-tag v-else-if="template.sharedUserIds?.length" color="blue" class="small-tag">已分享</a-tag>
                  </div>
                  <div class="item-actions">
                    <a-button size="small" type="primary" :ghost="currentTemplateId !== template.id" @click="selectTemplate(template.id)" :disabled="currentTemplateId === template.id">
                      {{ currentTemplateId === template.id ? '当前使用' : '使用' }}
                    </a-button>
                    <a-button size="small" type="text" danger @click="confirmDeleteTemplate(template.id)">删除</a-button>
                  </div>
                </div>
                
                <div class="item-meta-row">
                  <span class="meta-badge"></span>
                  <span class="meta-text">{{ template.fields.length }} 个字段</span>
                  <span class="meta-divider">|</span>
                  <span class="meta-text">更新于 {{ dayjs(template.updatedAt).format('YYYY-MM-DD HH:mm') }}</span>
                </div>

                <div class="item-fields-wrap">
                  <span v-for="fieldKey in template.fields" :key="fieldKey" class="field-tag">
                    {{ getFieldTitle(fieldKey) }}
                  </span>
                </div>
              </div>
            </div>
            <a-empty v-if="myTemplates.length === 0" description="暂无自定义模板" />
          </div>
        </a-tab-pane>
        
        <a-tab-pane key="shared-templates" tab="分享给我的">
          <div class="template-list-scroller">
            <div
              v-for="template in sharedTemplates"
              :key="template.id"
              class="manage-template-item shared"
              :class="{ 'active': currentTemplateId === template.id }"
            >
              <div class="item-main">
                <div class="item-header-row">
                  <div class="item-title-group">
                    <span class="item-name">{{ template.name }}</span>
                    <a-tag color="purple" class="small-tag">来自: {{ template.userName || '未知' }}</a-tag>
                  </div>
                  <div class="item-actions">
                    <a-button size="small" type="primary" :ghost="currentTemplateId !== template.id" @click="selectTemplate(template.id)" :disabled="currentTemplateId === template.id || isTemplateHidden(template)">
                      {{ currentTemplateId === template.id ? '当前使用' : '使用' }}
                    </a-button>
                    <a-button v-if="!isTemplateHidden(template)" size="small" type="text" style="color: #8c8c8c" @click="confirmHideTemplate(template.id)">隐藏</a-button>
                    <a-button v-else size="small" type="text" style="color: #1890ff" @click="handleUnhideTemplate(template.id)">恢复显示</a-button>
                  </div>
                </div>
                
                <div class="item-meta-row">
                  <span class="meta-badge shared"></span>
                  <span class="meta-text">{{ template.fields.length }} 个字段</span>
                  <span class="meta-divider">|</span>
                  <span class="meta-text">更新于 {{ dayjs(template.updatedAt).format('YYYY-MM-DD HH:mm') }}</span>
                </div>

                <div class="item-fields-wrap">
                  <span v-for="fieldKey in template.fields" :key="fieldKey" class="field-tag">
                    {{ getFieldTitle(fieldKey) }}
                  </span>
                </div>
              </div>
            </div>
            <a-empty v-if="sharedTemplates.length === 0" description="暂无他人分享" />
          </div>
        </a-tab-pane>
      </a-tabs>
    </div>
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, computed, watch } from 'vue';
import { message } from 'ant-design-vue';
import { showDeleteConfirm } from '@/utils/modal';
import { CloseOutlined, ExportOutlined, InfoCircleOutlined, CopyOutlined, EditOutlined, AppstoreOutlined, DeleteOutlined, EyeInvisibleOutlined } from '@ant-design/icons-vue';
import dayjs from 'dayjs';
import { 
  createTemplate,
  updateTemplate,
  deleteTemplate,
  hideTemplate,
  unhideTemplate,
  getSortedTemplates,
  recordTemplateUsage,
  checkTemplateNameDuplicate,
  type ExportTemplate 
} from '@/utils/exportTemplate';

const props = withDefaults(defineProps<{
  open: boolean;
  selectedCount: number;
  exportFields: Array<{ key: string; title: string; [key: string]: any }>;
  pageType: string;
  currentUserId: string;
  allUsers?: Array<{ id: string; name: string }>;
}>(), {
  open: false,
  selectedCount: 0,
  allUsers: () => [],
});

const emit = defineEmits<{
  (e: 'update:open', value: boolean): void;
  (e: 'export', payload: { scope: 'all' | 'selected'; fields: string[]; columns: any[]; templateId?: string; templateName?: string }): void;
}>();

const visible = ref(false);
const exportScope = ref<'all' | 'selected'>('selected');
const selectedExportFields = ref<string[]>([]);
const orderedExportFields = ref<typeof props.exportFields>([]);
const currentTemplateId = ref<string | null>(null);
const availableTemplates = ref<ExportTemplate[]>([]);
const dragIndex = ref<number | null>(null);
const dragOverIndex = ref<number | null>(null);

// 模板相关
const saveTemplateModalVisible = ref(false);
const templateManageModalVisible = ref(false);
const isEditingTemplate = ref(false);
const templateForm = reactive({
  name: '',
  isPublic: false,
  sharedUserIds: [] as string[],
});

watch(
  () => props.open,
  (val) => {
    visible.value = val;
    if (val) {
      refreshTemplates();
      initOrderedFields();
      
      // Auto-select last used template
      if (!currentTemplateId.value && availableTemplates.value.length > 0) {
        const sortedByUserUsage = [...availableTemplates.value].sort((a, b) => {
          const usageA = a.userUsage?.find(u => String(u.userId) === String(props.currentUserId));
          const usageB = b.userUsage?.find(u => String(u.userId) === String(props.currentUserId));
          const timeA = usageA ? new Date(usageA.lastUsedAt).getTime() : 0;
          const timeB = usageB ? new Date(usageB.lastUsedAt).getTime() : 0;
          return timeB - timeA;
        });
        
        const lastUsed = sortedByUserUsage.find(t => t.userUsage?.some(u => String(u.userId) === String(props.currentUserId)));
        if (lastUsed) {
          handleTemplateChange(lastUsed.id);
          currentTemplateId.value = lastUsed.id;
        } else if (selectedExportFields.value.length === 0) {
          selectedExportFields.value = props.exportFields.map(f => f.key);
        }
      } else if (!currentTemplateId.value && selectedExportFields.value.length === 0) {
        selectedExportFields.value = props.exportFields.map(f => f.key);
      }
      
      // 如果有选中条目，默认选"导出选中"，否则选"导出全部"
      exportScope.value = props.selectedCount > 0 ? 'selected' : 'all';
    }
  },
);

watch(visible, (val) => emit('update:open', val));

const sortedTemplates = ref<ExportTemplate[]>([]);

const refreshTemplates = () => {
  // 传 true 让管理列表能够看到被隐藏的模板，以便支持"恢复"
  sortedTemplates.value = getSortedTemplates(props.pageType, true);
  availableTemplates.value = sortedTemplates.value;
  
  // 如果没有模板，初始化一些示例模板 (逻辑保持不变)
  if (sortedTemplates.value.length === 0) {
    // 这里可以添加默认模板初始化逻辑，或者让父组件处理
  }
};

const myTemplates = computed(() => {
  return sortedTemplates.value.filter(t => String(t.userId) === String(props.currentUserId));
});

const sharedTemplates = computed(() => {
  return sortedTemplates.value.filter(t => 
    String(t.userId) !== String(props.currentUserId) && 
    (t.isPublic || (t.sharedUserIds && t.sharedUserIds.some(id => String(id) === String(props.currentUserId))))
  );
});

// 下拉列表专用的分享模板（过滤掉已隐藏的）
const sharedDropdownTemplates = computed(() => {
  return sharedTemplates.value.filter(t => !isTemplateHidden(t));
});

const isTemplateHidden = (template: ExportTemplate) => {
  return template.hiddenByUserIds?.some(id => String(id) === String(props.currentUserId));
};

const defaultExportFields = computed(() => props.exportFields.map(f => f.key));

const allFieldsSelected = computed(() => {
  return selectedExportFields.value.length === props.exportFields.length && props.exportFields.length > 0;
});

const hasIndeterminateSelection = computed(() => {
  return selectedExportFields.value.length > 0 && selectedExportFields.value.length < props.exportFields.length;
});

const orderedSelectedFields = computed(() => {
  return orderedExportFields.value
    .filter(f => selectedExportFields.value.includes(f.key))
    .map(f => f.key);
});

const currentTemplate = computed(() => {
  if (!currentTemplateId.value) return null;
  return availableTemplates.value.find(t => t.id === currentTemplateId.value) || null;
});

const isMyTemplate = computed(() => {
  if (!currentTemplate.value) return true;
  return String(currentTemplate.value.userId) === String(props.currentUserId);
});

const getFieldTitle = (fieldKey: string) => {
  const field = props.exportFields.find(f => f.key === fieldKey);
  return field ? field.title : fieldKey;
};

const initOrderedFields = () => {
  if (orderedExportFields.value.length === 0) {
    orderedExportFields.value = [...props.exportFields];
  }
};

const handleTemplateChange = (templateId: string | null) => {
  if (!templateId) {
    selectedExportFields.value = [...defaultExportFields.value];
    orderedExportFields.value = [...props.exportFields];
    isEditingTemplate.value = false;
    return;
  }
  
  recordTemplateUsage(props.pageType, templateId);
  refreshTemplates();
  
  const template = availableTemplates.value.find(t => t.id === templateId);
  if (template) {
    const templateFieldKeys = template.fields;
    const ordered = templateFieldKeys
      .map(key => props.exportFields.find(f => f.key === key))
      .filter((f): f is typeof props.exportFields[0] => f !== undefined);
    
    const missingFields = props.exportFields.filter(f => !templateFieldKeys.includes(f.key));
    orderedExportFields.value = [...ordered, ...missingFields];
    
    selectedExportFields.value = templateFieldKeys.filter(key => 
      props.exportFields.some(f => f.key === key)
    );
    
    if (template.userId !== props.currentUserId) {
      isEditingTemplate.value = false;
    }
  }
};

const handleSelectAll = (e: any) => {
  if (!isMyTemplate.value) return;
  if (e.target.checked) {
    selectedExportFields.value = props.exportFields.map(f => f.key);
  } else {
    selectedExportFields.value = [];
  }
};

const handleResetExportFields = () => {
  if (!isMyTemplate.value) return;
  selectedExportFields.value = [...defaultExportFields.value];
  orderedExportFields.value = [...props.exportFields];
  currentTemplateId.value = null;
};

// 拖拽逻辑
const handleDragStart = (event: DragEvent, index: number) => {
  if (!isMyTemplate.value) {
    event.preventDefault();
    return;
  }
  dragIndex.value = index;
  if (event.dataTransfer) {
    event.dataTransfer.effectAllowed = 'move';
    event.dataTransfer.setData('text/html', index.toString());
  }
};

const handleDragOver = (event: DragEvent, index: number) => {
  event.preventDefault();
  if (dragIndex.value !== null && dragIndex.value !== index) {
    dragOverIndex.value = index;
  }
};

const handleDrop = (event: DragEvent, dropIndex: number) => {
  event.preventDefault();
  if (dragIndex.value === null || dragIndex.value === dropIndex) return;
  
  const currentDragIndex = dragIndex.value;
  if (currentDragIndex === null) return;
  
  const currentOrder = [...orderedSelectedFields.value];
  const draggedKey = currentOrder[currentDragIndex];
  
  if (draggedKey) {
    currentOrder.splice(currentDragIndex, 1);
    currentOrder.splice(dropIndex, 0, draggedKey);
    
    const otherFields = orderedExportFields.value.filter(f => !selectedExportFields.value.includes(f.key));
    const selectedFields = currentOrder.map(key => props.exportFields.find(f => f.key === key)).filter((f): f is typeof props.exportFields[0] => f !== undefined);
    orderedExportFields.value = [...selectedFields, ...otherFields];
  }
  
  dragIndex.value = null;
  dragOverIndex.value = null;
};

const handleDragEnd = () => {
  dragIndex.value = null;
  dragOverIndex.value = null;
};

const handleRemoveField = (fieldKey: string) => {
  if (!isMyTemplate.value) return;
  selectedExportFields.value = selectedExportFields.value.filter(k => k !== fieldKey);
};

// 模板管理
const openSaveTemplateModal = () => {
  if (selectedExportFields.value.length === 0) {
    message.warning('请先选择要导出的字段');
    return;
  }
  
  if (currentTemplateId.value && isMyTemplate.value) {
     // 如果当前选中的是自己的模板，默认为更新
     templateForm.name = currentTemplate.value?.name || '';
     templateForm.isPublic = currentTemplate.value?.isPublic || false;
     templateForm.sharedUserIds = currentTemplate.value?.sharedUserIds ? [...currentTemplate.value.sharedUserIds] : [];
     isEditingTemplate.value = true;
  } else {
     // 否则为新增
     templateForm.name = '';
     templateForm.isPublic = false;
     templateForm.sharedUserIds = [];
     isEditingTemplate.value = false;
  }
  
  saveTemplateModalVisible.value = true;
};

const openTemplateManageModal = () => {
  templateManageModalVisible.value = true;
};

const handleCancelSaveTemplate = () => {
  saveTemplateModalVisible.value = false;
};

const handleSaveTemplate = () => {
  if (!templateForm.name.trim()) {
    message.warning('请输入模板名称');
    return;
  }
  
  try {
    if (isEditingTemplate.value && currentTemplateId.value) {
      if (checkTemplateNameDuplicate(props.pageType, templateForm.name.trim(), currentTemplateId.value)) {
        message.error('模板名称已存在，请使用其他名称');
        return;
      }
    } else {
      if (checkTemplateNameDuplicate(props.pageType, templateForm.name.trim())) {
        message.error('模板名称已存在，请使用其他名称');
        return;
      }
    }
  } catch (error) {
    // ignore
  }
  
  const fieldKeys = orderedSelectedFields.value;
  
  if (isEditingTemplate.value && currentTemplateId.value) {
    const success = updateTemplate(
      props.pageType,
      currentTemplateId.value,
      {
        name: templateForm.name.trim(),
        fields: fieldKeys,
        isPublic: templateForm.isPublic,
        sharedUserIds: templateForm.isPublic ? [] : templateForm.sharedUserIds
      }
    );
    
    if (success) {
      refreshTemplates();
      saveTemplateModalVisible.value = false;
      message.success('模板更新成功');
    } else {
      message.error('模板更新失败');
    }
  } else {
    const template = createTemplate(
      props.pageType,
      templateForm.name.trim(),
      fieldKeys,
      templateForm.isPublic,
      templateForm.isPublic ? [] : templateForm.sharedUserIds
    );
    
    refreshTemplates();
    currentTemplateId.value = template.id;
    saveTemplateModalVisible.value = false;
    message.success('模板保存成功');
  }
};

const confirmDeleteTemplate = (id: string) => {
  showDeleteConfirm({
    title: '确定删除该导出模板？',
    content: '删除后无法恢复，确定要继续吗？',
    onOk: () => deleteTemplateFromManage(id)
  });
};

const deleteTemplateFromManage = (id: string) => {
  const success = deleteTemplate(props.pageType, id);
  if (success) {
    refreshTemplates();
    if (currentTemplateId.value === id) {
      currentTemplateId.value = null;
      handleResetExportFields();
    }
    message.success('删除成功');
  } else {
    message.error('删除失败');
  }
};

const confirmHideTemplate = (id: string) => {
  showDeleteConfirm({
    title: '确定在下拉框隐藏该模板吗？',
    content: '隐藏后，该模板将不再出现在导出列表的下拉选择中。您可以随时在这里恢复显示。',
    onOk: () => hideTemplateFromManage(id)
  });
};

const handleUnhideTemplate = (id: string) => {
  if (unhideTemplate(props.pageType, id)) {
    message.success('已恢复显示该模板');
    refreshTemplates();
    
    // 如果当前没有选中模板且是可见的，可以考虑自动选中
    // 这里保持简单，只刷新列表
  }
};

const hideTemplateFromManage = (id: string) => {
  const success = hideTemplate(props.pageType, id);
  if (success) {
    refreshTemplates();
    if (currentTemplateId.value === id) {
      currentTemplateId.value = null;
      handleResetExportFields();
    }
    message.success('已隐藏');
  } else {
    message.error('隐藏失败');
  }
};

const selectTemplate = (id: string) => {
  currentTemplateId.value = id;
  handleTemplateChange(id);
  templateManageModalVisible.value = false;
};

const formatTemplateTime = (time: string) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm');
};

const handleCancel = () => {
  visible.value = false;
};

const handleConfirmExport = () => {
  if (selectedExportFields.value.length === 0) {
    message.warning('请至少选择一个字段');
    return;
  }
  
  if (exportScope.value === 'selected' && props.selectedCount === 0) {
    message.warning('请先选择需要导出的数据');
    return;
  }
  
  // Construct columns based on orderedSelectedFields
  const selectedColumns = orderedSelectedFields.value
    .map(key => props.exportFields.find(f => f.key === key))
    .filter((f): f is typeof props.exportFields[0] => f !== undefined);

  emit('export', {
    scope: exportScope.value,
    fields: orderedSelectedFields.value,
    columns: selectedColumns,
    templateId: currentTemplateId.value || undefined,
    templateName: currentTemplate.value?.name || undefined
  });
  
  visible.value = false;
};
</script>

<style lang="scss">
.influencer-export-modal .ant-modal-content {
  padding: 0 !important;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.15);
}

/* Premium Sub-Modal Styling */
.premium-sub-modal {
  :deep(.ant-modal-content) {
    padding: 0 !important;
    border-radius: 16px;
    overflow: hidden;
    box-shadow: 0 15px 40px rgba(0, 0, 0, 0.1);
  }

  .sub-modal-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 16px 20px;
    background: #ffffff;
    border-bottom: 1px solid #f1f5f9;

    .sub-header-title {
      font-size: 16px;
      font-weight: 700;
      color: #1e293b;
      display: flex;
      align-items: center;
      gap: 10px;
      .anticon { color: #3b82f6; }
    }
    
    .sub-close-btn {
      color: #94a3b8;
      &:hover { color: #64748b; background: #f8fafc; }
    }
  }

  .sub-modal-body {
    padding: 24px;
    background: #f8fafc;
    
    &.manage-body { padding: 0; }
  }

  .sub-modal-footer {
    padding: 16px 24px;
    background: #ffffff;
    border-top: 1px solid #f1f5f9;
    text-align: right;
  }
}

/* Form Polish */
.premium-form {
  .premium-input-field {
    border-radius: 8px !important;
    border-color: #e2e8f0 !important;
    padding: 8px 12px;
    background: #ffffff !important;
    &:focus { border-color: #3b82f6 !important; box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1) !important; }
  }
  
  .share-settings-box {
    margin-top: 16px;
    padding: 16px;
    background: #ffffff;
    border-radius: 12px;
    border: 1px solid #f1f5f9;

    .share-label {
      font-weight: 600;
      color: #475569;
      .share-hint { font-weight: 400; color: #94a3b8; font-size: 12px; }
    }
  }
}

/* Tabs & Logic */
.premium-tabs {
  :deep(.ant-tabs-nav) {
    padding: 0 24px;
    margin-bottom: 0;
    background: #fff;
    border-bottom: 1px solid #f1f5f9;
  }
  :deep(.ant-tabs-tab-btn) { font-weight: 600; color: #94a3b8; font-size: 13px; }
  :deep(.ant-tabs-tab-active .ant-tabs-tab-btn) { color: #3b82f6; }
  :deep(.ant-tabs-ink-bar) { height: 3px; border-radius: 3px 3px 0 0; }
}

.template-list-scroller {
  padding: 16px 24px;
  max-height: 480px;
  overflow-y: auto;
  background: #f8fafc;
  
  &::-webkit-scrollbar { width: 6px; }
  &::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 6px; }
}

.manage-template-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.04);
  margin-bottom: 12px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  background: #fff;
  
  &:hover {
    border-color: #3b82f6;
    box-shadow: 0 4px 12px rgba(59, 130, 246, 0.08);
    transform: translateY(-1px);
  }
  
  &.active { border-color: #3b82f6; background: #f0f7ff; }
  
  .item-name { font-weight: 700; color: #1e293b; font-size: 14px; }
  .item-meta { font-size: 11px; color: #94a3b8; margin-top: 4px; font-family: 'Inter', sans-serif; }
  
  .item-fields-preview-scroller {
    margin-top: 12px;
    display: flex;
    flex-wrap: wrap;
    gap: 6px;
    padding-bottom: 2px;
    
    .field-preview-tag {
      flex-shrink: 0;
      font-size: 10px;
      font-weight: 600;
      color: #64748b;
      background: #f1f5f9;
      padding: 2px 8px;
      border-radius: 4px;
      white-space: nowrap;
    }
  }

  .item-actions {
    display: flex;
    gap: 8px;
    .use-btn { border-radius: 6px; font-weight: 600; font-size: 12px; }
    .del-btn { color: #94a3b8; &:hover { color: #ef4444; background: #fee2e2; } }
  }
}

.premium-tag {
  border-radius: 4px;
  font-weight: 700;
  font-size: 10px;
  text-transform: uppercase;
  padding: 0 4px;
  &.success { background: #ecfdf5; color: #10b981; border: none; }
  &.blue { background: #eff6ff; color: #3b82f6; border: none; }
  &.grey { background: #f8fafc; color: #94a3b8; border: 1px solid #e2e8f0; }
}

/* Scoped Styles */
</style>

<style scoped lang="scss">
.influencer-export-modal {
  :deep(.ant-modal-content) {
    padding: 0 !important;
    border-radius: 16px;
    overflow: hidden;
    background: #ffffff;
  }
}

/* Premium Header */
.ic-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
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
    width: 38px;
    height: 38px;
    border-radius: 12px;
    background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
    color: #ffffff;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 20px;
    box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
  }

  .ic-header-title {
    font-size: 16px;
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

.ic-modal-body {
  padding: 12px 16px;
  background: #f8fafc;
  max-height: 70vh;
  overflow-y: auto;

  &::-webkit-scrollbar { width: 6px; }
  &::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 6px; }
}

.ic-form-section {
  background: #ffffff;
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.04);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.01);
  margin-bottom: 8px;
  padding: 12px;

  &:last-child { margin-bottom: 0; }

  .ic-form-section-title {
    font-size: 13px;
    font-weight: 700;
    color: #475569;
    margin-bottom: 10px;
    display: flex;
    align-items: center;
    gap: 8px;
    text-transform: uppercase;
    letter-spacing: 0.3px;
    
    &::before {
      content: '';
      width: 3px;
      height: 14px;
      background: #3b82f6;
      border-radius: 2px;
    }
  }

  .ic-section-header-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 4px;
    
    .ic-form-section-title { margin-bottom: 0; }
  }
}

/* Scope Selection Polish */
.export-scope-cards {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.export-scope-card {
  padding: 8px 12px;
  border-radius: 12px;
  border: 2px solid #f1f5f9;
  background: #ffffff;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;

  &:hover {
    border-color: rgba(59, 130, 246, 0.2);
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.02);
  }

  &.export-scope-card-selected {
    border-color: #3b82f6;
    background: #f0f7ff;
    &::after {
      content: '✓';
      position: absolute;
      top: 8px;
      right: 12px;
      color: #3b82f6;
      font-weight: 900;
    }
    
    .export-scope-card-title { color: #1e40af; }
    .export-scope-card-info { color: #3b82f6; }
  }

  .export-scope-card-title {
    font-size: 15px;
    font-weight: 700;
    color: #334155;
    margin-bottom: 4px;
  }

  .export-scope-card-info {
    font-size: 12px;
    color: #94a3b8;
    font-weight: 500;
    .text-warning { color: #f59e0b; }
  }
}

/* Template Select */
.export-template-row {
  display: flex;
  gap: 12px;
  
  :deep(.export-template-select) {
    flex: 1;
    .ant-select-selector {
      border-radius: 10px !important;
      border-color: #e2e8f0 !important;
      height: 36px !important;
      display: flex;
      align-items: center;
    }
  }

  .ant-btn {
    border-radius: 10px;
    height: 36px;
    padding: 0 16px;
    font-weight: 600;
    font-size: 13px;
    
    &.ant-btn-primary {
      background: #3b82f6;
      &:hover { background: #2563eb; }
    }
  }
}

/* Field Grid Polish */
.field-selection-container {
  background: #f8fafc;
  border-radius: 12px;
  padding: 12px;
  border: 1px solid #f1f5f9;

  .field-checkbox-item {
    padding: 4px 10px;
    border-radius: 8px;
    background: #ffffff;
    border: 1px solid transparent;
    transition: all 0.2s;
    margin: 1px 0;
    
    &:hover { border-color: rgba(59, 130, 246, 0.2); }
    &.checked { 
      border-color: rgba(59, 130, 246, 0.1);
      background: #ffffff;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.03);
    }
    
    :deep(.ant-checkbox-wrapper) {
      width: 100%;
      font-size: 13px;
      font-weight: 600;
      color: #475569;
      
      .ant-checkbox-checked + span { color: #3b82f6; }
    }
  }
}

/* Order Section Scroller */
.horizontal-drag-container {
  background: #f8fafc;
  border-radius: 12px;
  padding: 10px 12px;
  min-height: 48px;
  display: block; 
  overflow-x: auto;
  
  &::-webkit-scrollbar { height: 6px; }
  &::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 6px; }

  .horizontal-drag-list {
    display: flex;
    flex-wrap: nowrap;
    gap: 10px;
    align-items: center;
    width: max-content;
    padding-bottom: 4px; /* for scrollbar spacing */
  }
}

.field-chip {
  flex-shrink: 0;
  background: #ffffff;
  border: 1px solid rgba(0, 0, 0, 0.05);
  border-radius: 10px;
  padding: 4px 8px;
  border-radius: 6px;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  
  &:hover:not(.disabled) {
    transform: translateY(-3px) scale(1.02);
    box-shadow: 0 10px 20px rgba(59, 130, 246, 0.12);
    border-color: #3b82f6;
  }

  &.dragging { opacity: 0.4; border-style: dashed; }
  &.drag-over { border-left: 4px solid #3b82f6; padding-left: 18px; }

  .chip-content {
    display: flex;
    align-items: center;
    gap: 10px;
    
    .chip-handle { color: #cbd5e1; font-size: 14px; flex-shrink: 0; }
    .chip-label { 
      font-size: 13px; 
      font-weight: 700; 
      color: #1e293b; 
      white-space: nowrap; /* Prevent vertical text */
    }
    .chip-remove { 
      font-size: 11px; 
      color: #94a3b8; 
      &:hover { color: #ef4444; }
    }
  }
}

/* Modal Footer Polish */
.ic-modal-footer {
  padding: 10px 24px;
  background: #ffffff;
  border-top: 1px solid #f1f5f9;
  text-align: right;
  
  .premium-btn {
    height: 32px;
    padding: 0 20px;
    border-radius: 8px;
    font-weight: 700;
    
    &.primary-gradient-blue {
      background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
      border: none;
      color: #ffffff;
      box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
      &:hover { transform: translateY(-1px); box-shadow: 0 6px 15px rgba(37, 99, 235, 0.3); }
    }
  }
}

.template-list-scroller {
  height: 480px;
  overflow-y: auto;
  padding-right: 8px;
  
  &::-webkit-scrollbar { width: 6px; }
  &::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 6px; }
}

/* Manage Template List UI Redesign */
.manage-template-item {
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  padding: 16px 20px;
  margin-bottom: 12px;
  background: #fff;
  transition: all 0.3s;

  &:hover {
    border-color: #d9d9d9;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }

  &.active {
    border-color: var(--ant-primary-color, #1890ff);
    background: #fafafa;
  }

  .item-main {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .item-header-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .item-title-group {
    display: flex;
    align-items: center;
    gap: 8px;

    .item-name {
      font-size: 16px;
      font-weight: 500;
      color: rgba(0, 0, 0, 0.85);
    }
    
    .small-tag {
      margin: 0;
      font-size: 11px;
      line-height: 18px;
    }
  }

  .item-actions {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .item-meta-row {
    font-size: 12px;
    color: rgba(0, 0, 0, 0.45);
    display: flex;
    align-items: center;
    gap: 8px;

    .meta-badge {
       display: inline-block;
       width: 6px;
       height: 6px;
       border-radius: 50%;
       background: #52c41a;
       
       &.shared {
         background: #a855f7;
       }
    }

    .meta-divider {
      color: #e8e8e8;
    }
  }

  .item-fields-wrap {
    display: flex;
    flex-wrap: wrap;
    gap: 6px;

    .field-tag {
      background: #fafafa;
      border: 1px solid #d9d9d9;
      border-radius: 4px;
      color: rgba(0, 0, 0, 0.65);
      font-size: 12px;
      padding: 2px 8px;
      line-height: 20px;
    }
  }
}
</style>

<style>
/* 强制提升 Message 组件的 z-index，避免被 1050 的 Modal 遮挡导致文字暗淡 */
.ant-message {
  z-index: 9999 !important;
}
</style>
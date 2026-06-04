<template>
  <div class="template-manage-page">
    <!-- 筛选区域 -->
    <a-card :bordered="false" class="filter-card glass-card" :body-style="{ padding: '16px 20px' }">
      <div class="filter-row">
        <div class="filter-left">
          <a-input
            v-model:value="filterName"
            placeholder="搜索模板名称..."
            allow-clear
            class="premium-input-search"
            style="width: 260px"
            @pressEnter="loadTemplates"
          >
            <template #prefix><SearchOutlined style="color: #94a3b8" /></template>
          </a-input>
          <a-select
            v-model:value="filterCategory"
            placeholder="全部分类"
            allow-clear
            style="width: 160px"
            @change="loadTemplates"
          >
            <a-select-option v-for="cat in categoryOptions" :key="cat.value" :value="cat.value">
              {{ cat.label }}
            </a-select-option>
          </a-select>
        </div>
        <div class="filter-right">
          <a-button type="primary" @click="openTemplateModal()" class="primary-gradient">
            <template #icon><PlusOutlined /></template>
            新建模板
          </a-button>
        </div>
      </div>
    </a-card>

    <!-- 模板卡片网格 -->
    <div class="template-grid" v-if="templates.length > 0">
      <!-- 新建模板卡片 -->
      <div class="template-card new-card" @click="openTemplateModal()">
        <div class="new-card-content">
          <div class="new-icon">
            <PlusOutlined />
          </div>
          <div class="new-text">新建模板</div>
        </div>
      </div>

      <div
        v-for="tpl in templates"
        :key="tpl.id"
        class="template-card"
        @click="openTemplateModal(tpl)"
      >
        <div class="card-header">
          <div class="card-icon-wrapper" :style="{ background: getCategoryGradient(tpl.category) }">
            <FileTextOutlined />
          </div>
          <a-dropdown :trigger="['click']" @click.stop>
            <MoreOutlined class="card-more-btn" />
            <template #overlay>
              <a-menu @click="({ key }: { key: string }) => handleCardAction(key, tpl)">
                <a-menu-item key="edit"><EditOutlined /> 编辑</a-menu-item>
                <a-menu-item key="copy"><CopyOutlined /> 复制</a-menu-item>
                <a-menu-divider />
                <a-menu-item key="delete" class="danger-item"><DeleteOutlined /> 删除</a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>

        <div class="card-title">{{ tpl.name }}</div>
        <div class="card-preview">{{ stripHtml(tpl.bodyHtml) }}</div>

        <div class="card-footer">
          <a-tag :color="getCategoryColor(tpl.category)" class="category-tag">
            {{ getCategoryLabel(tpl.category) }}
          </a-tag>
          <span class="usage-count">
            <ThunderboltOutlined />
            使用 {{ tpl.usageCount }} 次
          </span>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <a-card v-else :bordered="false" class="empty-card glass-card" :body-style="{ padding: '80px 0' }">
      <a-empty description="还没有邮件模板">
        <template #image>
          <div class="empty-icon-wrapper">
            <FileTextOutlined />
          </div>
        </template>
        <a-button type="primary" @click="openTemplateModal()" class="primary-gradient" style="margin-top: 16px">
          <template #icon><PlusOutlined /></template>
          创建第一个模板
        </a-button>
      </a-empty>
    </a-card>

    <!-- 模板编辑弹窗 -->
    <a-modal
      v-model:open="templateModalOpen"
      :title="null"
      :footer="null"
      :closable="true"
      width="780px"
      centered
      class="premium-refined-modal"
      :bodyStyle="{ padding: 0 }"
    >
      <div class="glass-header-compact">
        <div class="header-main">
          <div class="logo-box">
            <span class="logo-text">📝</span>
          </div>
          <div class="title-meta">
            <h3 class="main-title">{{ editingTemplate ? '编辑模板' : '新建模板' }}</h3>
            <div class="simple-subtitle">创建邮件模板，支持变量自动替换</div>
          </div>
        </div>
      </div>

      <div class="modal-body-container" style="max-height: calc(100vh - 280px); overflow-y: auto; padding: 24px;">
        <a-form :model="templateForm" layout="vertical">
          <a-row :gutter="16">
            <a-col :span="14">
              <a-form-item label="模板名称" :rules="[{ required: true, message: '请输入' }]">
                <a-input v-model:value="templateForm.name" placeholder="如：标准合作邀约" class="premium-input" />
              </a-form-item>
            </a-col>
            <a-col :span="10">
              <a-form-item label="模板分类">
                <a-select v-model:value="templateForm.category" placeholder="选择分类">
                  <a-select-option v-for="cat in categoryOptions" :key="cat.value" :value="cat.value">
                    {{ cat.label }}
                  </a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
          </a-row>

          <a-form-item label="邮件主题" :rules="[{ required: true, message: '请输入' }]">
            <a-input v-model:value="templateForm.subject" placeholder="Hi {{name}}, Exciting Collaboration Opportunity!" class="premium-input" />
          </a-form-item>

          <a-form-item label="邮件正文" :rules="[{ required: true, message: '请输入' }]">
            <a-textarea
              v-model:value="templateForm.bodyHtml"
              :placeholder="defaultPlaceholder"
              :auto-size="{ minRows: 12, maxRows: 20 }"
              class="premium-input"
            />
          </a-form-item>

          <!-- 可用变量 -->
          <div class="variable-section">
            <div class="variable-title">
              <CodeOutlined /> 可用变量（点击插入）
            </div>
            <div class="variable-tags">
              <a-tag
                v-for="v in templateVariables"
                :key="v.key"
                class="variable-tag"
                @click="insertVariable(v.key)"
              >
                <span class="var-key" v-text="'{{' + v.key + '}}'"></span>
                <span class="var-label">{{ v.label }}</span>
              </a-tag>
            </div>
          </div>
        </a-form>
      </div>

      <div class="modal-fixed-footer">
        <a-button class="btn-cancel" @click="templateModalOpen = false">取消</a-button>
        <a-button type="primary" class="btn-submit" @click="handleSaveTemplate" :loading="saveTemplateLoading">
          {{ editingTemplate ? '保存修改' : '创建模板' }}
        </a-button>
      </div>
    </a-modal>

    <!-- 删除确认 -->
    <a-modal
      v-model:open="deleteModalOpen"
      :title="null"
      :footer="null"
      :closable="false"
      width="420px"
      centered
      class="premium-delete-modal"
    >
      <div class="delete-modal-content">
        <div class="dm-header">
          <div class="dm-icon-wrapper">
            <ExclamationCircleFilled />
          </div>
          <div class="dm-title">删除模板确认</div>
          <div class="dm-subtitle">此操作不可恢复</div>
        </div>
        <div class="dm-body">
          <div class="warning-text">
            确定要删除模板 <span class="highlight-name">{{ deleteTarget?.name }}</span> 吗？
          </div>
          <div class="sub-warning">
            该模板已被使用 <span class="count">{{ deleteTarget?.usageCount || 0 }}</span> 次，删除后相关邮件不受影响。
          </div>
        </div>
        <div class="dm-footer">
          <a-button class="cancel-btn" @click="deleteModalOpen = false">取消</a-button>
          <a-button type="primary" class="delete-btn" :loading="deleteLoading" @click="handleDeleteConfirm">
            确认删除
          </a-button>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import {
  SearchOutlined, PlusOutlined, FileTextOutlined, MoreOutlined,
  EditOutlined, CopyOutlined, DeleteOutlined, ThunderboltOutlined,
  CodeOutlined, ExclamationCircleFilled,
} from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import type { OutreachTemplate, TemplateCategory } from '@/types/outreach';
import { TEMPLATE_CATEGORY_MAP, TEMPLATE_VARIABLES } from '@/types/outreach';
import { getTemplateList, createTemplate, updateTemplate, copyTemplate, deleteTemplate } from '@/services/outreachService';

const filterName = ref('');
const filterCategory = ref<TemplateCategory | undefined>(undefined);
const templates = ref<OutreachTemplate[]>([]);
const loading = ref(false);

const categoryOptions = Object.entries(TEMPLATE_CATEGORY_MAP).map(([value, info]) => ({
  value, label: info.label,
}));

const templateVariables = TEMPLATE_VARIABLES;

const defaultPlaceholder = `Dear {{name}},

I'm reaching out from our brand. We've been following your content on {{platform}} and would love to explore a collaboration opportunity...

Best regards,
{{brand_name}}`;

const loadTemplates = async () => {
  loading.value = true;
  try {
    const data = await getTemplateList({
      name: filterName.value || undefined,
      category: filterCategory.value,
      status: 'active',
    });
    templates.value = data.content;
  } catch {
    // API not ready
    templates.value = [];
  } finally {
    loading.value = false;
  }
};

// --- Template modal ---
const templateModalOpen = ref(false);
const editingTemplate = ref<OutreachTemplate | null>(null);
const saveTemplateLoading = ref(false);

const templateForm = reactive({
  name: '',
  category: 'invitation' as TemplateCategory,
  subject: '',
  bodyHtml: '',
});

const openTemplateModal = (tpl?: OutreachTemplate) => {
  if (tpl) {
    editingTemplate.value = tpl;
    templateForm.name = tpl.name;
    templateForm.category = tpl.category;
    templateForm.subject = tpl.subject;
    templateForm.bodyHtml = tpl.bodyHtml;
  } else {
    editingTemplate.value = null;
    templateForm.name = '';
    templateForm.category = 'invitation';
    templateForm.subject = '';
    templateForm.bodyHtml = '';
  }
  templateModalOpen.value = true;
};

const handleSaveTemplate = async () => {
  if (!templateForm.name || !templateForm.subject || !templateForm.bodyHtml) {
    message.warning('请填写完整信息');
    return;
  }
  saveTemplateLoading.value = true;
  try {
    const dto = {
      name: templateForm.name,
      category: templateForm.category,
      subject: templateForm.subject,
      bodyHtml: templateForm.bodyHtml,
    };
    if (editingTemplate.value) {
      await updateTemplate(editingTemplate.value.id, dto);
      message.success('模板已更新');
    } else {
      await createTemplate(dto);
      message.success('模板已创建');
    }
    templateModalOpen.value = false;
    await loadTemplates();
  } catch {
    message.error('操作失败');
  } finally {
    saveTemplateLoading.value = false;
  }
};

const insertVariable = (key: string) => {
  templateForm.bodyHtml += `{{${key}}}`;
};

// --- Card actions ---
const handleCardAction = (action: string, tpl: OutreachTemplate) => {
  if (action === 'edit') {
    openTemplateModal(tpl);
  } else if (action === 'copy') {
    handleCopyTemplate(tpl);
  } else if (action === 'delete') {
    deleteTarget.value = tpl;
    deleteModalOpen.value = true;
  }
};

const handleCopyTemplate = async (tpl: OutreachTemplate) => {
  try {
    await copyTemplate(tpl.id);
    message.success('模板已复制');
    await loadTemplates();
  } catch {
    message.error('复制失败');
  }
};

// --- Delete ---
const deleteModalOpen = ref(false);
const deleteTarget = ref<OutreachTemplate | null>(null);
const deleteLoading = ref(false);

const handleDeleteConfirm = async () => {
  if (!deleteTarget.value) return;
  deleteLoading.value = true;
  try {
    await deleteTemplate(deleteTarget.value.id);
    message.success('删除成功');
    deleteModalOpen.value = false;
    await loadTemplates();
  } catch {
    message.error('删除失败');
  } finally {
    deleteLoading.value = false;
  }
};

// --- Helpers ---
const getCategoryLabel = (cat: string) => TEMPLATE_CATEGORY_MAP[cat as TemplateCategory]?.label || cat;
const getCategoryColor = (cat: string) => TEMPLATE_CATEGORY_MAP[cat as TemplateCategory]?.color || '#64748b';
const getCategoryGradient = (cat: string) => {
  const gradients: Record<string, string> = {
    invitation: 'linear-gradient(135deg, #3b82f6, #1d4ed8)',
    premium: 'linear-gradient(135deg, #8b5cf6, #6d28d9)',
    ambassador: 'linear-gradient(135deg, #f59e0b, #d97706)',
    followup: 'linear-gradient(135deg, #10b981, #059669)',
    custom: 'linear-gradient(135deg, #64748b, #475569)',
  };
  return gradients[cat] || gradients.custom;
};

const stripHtml = (html: string) => {
  if (!html) return '';
  const text = html.replace(/<[^>]+>/g, '').replace(/&nbsp;/g, ' ').trim();
  return text.length > 120 ? text.slice(0, 120) + '...' : text;
};

onMounted(() => {
  loadTemplates();
});
</script>

<style lang="scss" scoped>
.template-manage-page {
  height: 100%;
  padding: 8px;
  background: #f8fafc;
  display: flex;
  flex-direction: column;
  gap: 8px;
  overflow-y: auto;

  .glass-card {
    background: rgba(255, 255, 255, 0.6);
    backdrop-filter: blur(8px);
    border: 1px solid rgba(255, 255, 255, 0.8);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.02);
    border-radius: 12px;
  }
}

.filter-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.filter-left {
  display: flex;
  gap: 12px;
  align-items: center;
}

.premium-input-search {
  border-radius: 8px;
  height: 36px;
}

.primary-gradient {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  border: none;
  color: #fff;
  box-shadow: 0 4px 12px rgba(99, 102, 241, 0.2);
  height: 36px;
  border-radius: 8px;
  font-weight: 600;
  &:hover { box-shadow: 0 6px 16px rgba(99, 102, 241, 0.3); transform: translateY(-1px); }
}

/* Template grid */
.template-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.template-card {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(8px);
  border: 1px solid rgba(255, 255, 255, 0.9);
  border-radius: 14px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  min-height: 200px;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 12px 32px rgba(0, 0, 0, 0.08);
    border-color: #c7d2fe;
  }

  &.new-card {
    border: 2px dashed #cbd5e1;
    background: rgba(248, 250, 252, 0.6);
    display: flex;
    align-items: center;
    justify-content: center;

    &:hover {
      border-color: #8b5cf6;
      background: rgba(245, 243, 255, 0.6);
    }
  }
}

.new-card-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;

  .new-icon {
    width: 48px;
    height: 48px;
    background: #f1f5f9;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 22px;
    color: #94a3b8;
    transition: all 0.3s;
  }

  .new-text {
    font-size: 14px;
    font-weight: 600;
    color: #94a3b8;
  }

  .new-card:hover & {
    .new-icon { background: #ede9fe; color: #8b5cf6; }
    .new-text { color: #8b5cf6; }
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.card-icon-wrapper {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 16px;
}

.card-more-btn {
  font-size: 18px;
  color: #94a3b8;
  padding: 4px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s;
  &:hover { background: #f1f5f9; color: #64748b; }
}

.card-title {
  font-size: 15px;
  font-weight: 700;
  color: #1e293b;
  margin-bottom: 8px;
  line-height: 1.3;
}

.card-preview {
  font-size: 12px;
  color: #94a3b8;
  line-height: 1.6;
  flex: 1;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px solid #f1f5f9;
}

.category-tag {
  font-size: 11px;
  font-weight: 700;
  border-radius: 4px;
  border: none;
}

.usage-count {
  font-size: 11px;
  color: #94a3b8;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* Variable section */
.variable-section {
  background: #f8fafc;
  border: 1px solid #f1f5f9;
  border-radius: 10px;
  padding: 14px 16px;
}

.variable-title {
  font-size: 12px;
  font-weight: 700;
  color: #64748b;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.variable-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.variable-tag {
  cursor: pointer;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  padding: 4px 10px;
  background: #fff;
  transition: all 0.2s;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;

  &:hover {
    border-color: #8b5cf6;
    background: #faf5ff;
    transform: translateY(-1px);
  }

  .var-key {
    font-family: 'SF Mono', monospace;
    font-weight: 700;
    color: #8b5cf6;
    font-size: 11px;
  }

  .var-label {
    color: #94a3b8;
    font-size: 11px;
  }
}

.premium-input {
  border-radius: 8px;
}

/* Empty */
.empty-card {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.empty-icon-wrapper {
  width: 64px;
  height: 64px;
  background: #f1f5f9;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 16px;
  font-size: 28px;
  color: #94a3b8;
}

/* Danger dropdown item */
:global(.ant-dropdown-menu-item.danger-item) {
  color: #ef4444 !important;
}
</style>

<template>
  <div class="social-media-tab">
    <div class="section-header">
      <div class="header-tips">共管理 {{ list.length }} 个社媒账号</div>
      <a-button @click="handleAdd" class="premium-add-btn">
        <template #icon><PlusOutlined /></template>
        新增社媒档案
      </a-button>
    </div>
    
    <div class="table-container">
      <a-table
        :columns="columns"
        :data-source="list"
        :pagination="false"
        :row-key="(_record: SocialMedia, index: number) => index"
        class="premium-table"
        size="middle"
        :scroll="{ y: 460 }"
      >
        <template #bodyCell="{ column, record, index }: { column: any; record: SocialMedia; index: number }">
          <template v-if="column.key === 'isDefault'">
            <div class="default-radio-cell" @click="handleSetDefault(index)">
               <div class="custom-radio" :class="{ 'checked': record.isDefault }">
                 <div class="radio-dot"></div>
               </div>
            </div>
          </template>

          <template v-else-if="column.key === 'platform'">
            <div v-if="record.editing" class="edit-cell">
              <a-select v-model:value="record.platform" placeholder="选择平台" class="table-select">
                <a-select-option v-for="p in platforms" :key="p" :value="p">{{ p }}</a-select-option>
              </a-select>
            </div>
            <div v-else class="platform-display">
              <div :class="['platform-indicator', getPlatformColor(record.platform)]"></div>
              <span class="platform-name">{{ record.platform || '-' }}</span>
            </div>
          </template>

          <template v-else-if="column.key === 'handle'">
            <a-input 
              v-if="record.editing" 
              v-model:value="record.handle" 
              placeholder="名称" 
              class="table-input" 
              @blur="onHandleBlur(record)"
            />
            <span v-else class="text-main">{{ record.handle || '-' }}</span>
          </template>

          <template v-else-if="column.key === 'url'">
            <a-input 
              v-if="record.editing" 
              v-model:value="record.url" 
              placeholder="链接" 
              class="table-input" 
              @blur="onUrlBlur(record)"
              @change="onUrlChange(record)"
            />
            <a-tooltip v-else-if="record.url" :title="record.url" placement="bottomLeft">
              <div class="homepage-link-cell">
                <div class="link-actions-default">
                  <a-button type="primary" size="small" class="action-btn go-btn" @click="handleGoLink(record.url)">
                    前往
                  </a-button>
                  <a-button size="small" class="action-btn copy-btn" @click="handleCopyLink(record.url)">
                    复制
                  </a-button>
                </div>
              </div>
            </a-tooltip>
            <span v-else>-</span>
          </template>

          <template v-else-if="column.key === 'followerCount'">
            <a-input-number v-if="record.editing" v-model:value="record.followerCount" :min="0" class="table-input-number" />
            <span v-else class="fans-count">{{ formatNumber(record.followerCount) }}</span>
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
import { ref, watch, createVNode, onMounted } from 'vue';
import { PlusOutlined, CloseOutlined } from '@ant-design/icons-vue';
import { message, Modal } from 'ant-design-vue';
import { storeToRefs } from 'pinia';
import { useCommonDataStore } from '@/stores/commonData';
import type { Dayjs } from 'dayjs';
import { showDeleteConfirm } from '@/utils/modal';
import { influencerService } from '@/services/influencerService';
import { tagService } from '@/services/tagService';

interface SocialMedia {
  platform: string;
  handle: string;
  url: string;
  followerCount: number | undefined;
  id?: number; 
  levelTime: Dayjs | undefined;
  editing: boolean;
  isDefault?: boolean;
}

const props = defineProps<{
  data: SocialMedia[];
  influencerId?: number;
}>();

const emit = defineEmits<{
  (e: 'update:data', value: SocialMedia[]): void;
  (e: 'default-changed'): void;
}>();

// 防止双向绑定无限循环
let skipNextWatch = false;

// 动态从标签管理获取社媒平台列表 (移至 commonStore)
const commonStore = useCommonDataStore();
const { platforms } = storeToRefs(commonStore);
const list = ref<SocialMedia[]>([]);

// 加载平台列表
onMounted(async () => {
    commonStore.loadPlatforms();
});

const columns = [
  { title: '默认', dataIndex: 'isDefault', key: 'isDefault', width: '60px', align: 'center' as const },
  { title: '合作平台', dataIndex: 'platform', key: 'platform', width: 180 },
  { title: '社媒账号名', dataIndex: 'handle', key: 'handle', width: 180 },
  { title: '主页链接', dataIndex: 'url', key: 'url', width: 220 },
  { title: '粉丝总量', dataIndex: 'followerCount', key: 'followerCount', width: 100 },
  { title: '管理操作', dataIndex: 'action', key: 'action', width: 140, align: 'right' as const },
];

watch(() => props.data, (val) => {
  // 简单策略：当 props.data 变化时，重新加载 list
  const validData = (val || []).map(i => ({...i, editing: false}));
  list.value = validData;
  
  // 确保默认值逻辑
  if (list.value.length > 0 && !list.value.some(i => i.isDefault)) {
     const firstItem = list.value[0];
     if (firstItem) firstItem.isDefault = true;
  }
}, { immediate: true });

// Clear and reload when switching influencers to prevent showing stale data
watch(() => props.influencerId, async (newId, oldId) => {
  if (newId && newId !== oldId) {
    // Immediately clear to prevent showing old data
    list.value = [];
    // Load fresh data from API
    await refreshList();
  }
});

// Removed watch(list) and emit loop to prevent freeze.
// Data persistence is now handled by handleSave/handleDelete API calls.

const handleAdd = () => {
  const isFirst = list.value.length === 0;
  list.value.unshift({ 
    platform: '', 
    handle: '', 
    url: '', 
    followerCount: undefined, 
    levelTime: undefined, 
    editing: true,
    isDefault: isFirst
  });
};

const handleSetDefault = async (index: number) => {
  if (!props.influencerId) return;
  
  const item = list.value[index];
  if (!item || !item.id) {
    message.warning('请先保存社媒后再设置默认');
    return;
  }
  
  try {
    // Only update this one item with isDefault=true
    // Backend's clearDefault will automatically set other social medias to false
    await influencerService.updateSocialMedia(item.id, {
      platform: item.platform,
      handle: item.handle,
      url: item.url,
      followerCount: item.followerCount,
      isDefault: true,
    });
    
    // Refresh list to get updated state from backend
    await refreshList();
    emit('default-changed');
    message.success('默认社媒已更新');
  } catch (e) {
    message.error('更新默认社媒失败');
    await refreshList();
  }
};

const handleEdit = (index: number) => { 
  const item = list.value[index];
  if (item) item.editing = true; 
};

const handleSave = async (index: number) => {
  const item = list.value[index];
  if (!item) return;
  if (!item.platform || !item.handle) return message.warning('请补充完整平台和名称');
  
  if (item.handle && (item.handle.startsWith('http://') || item.handle.startsWith('https://') || item.handle.includes('.com'))) {
    return message.warning('社媒账号名不能是链接格式，请填入纯文本，主页链接请填在后面');
  }
  
  if (props.influencerId) {
    try {
      const payload = {
        platform: item.platform,
        handle: item.handle,
        url: item.url,
        followerCount: item.followerCount,
        isDefault: item.isDefault,
      };
      if (item.id) {
        await influencerService.updateSocialMedia(item.id, payload);
      } else {
        await influencerService.addSocialMedia(props.influencerId, payload);
      }
      // Refresh list to get the new id from backend
      await refreshList();
      message.success('社媒信息已保存');
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
    const medias = await influencerService.getSocialMedias(props.influencerId) as unknown as SocialMedia[];
    list.value = (medias || []).map((m: any) => ({ ...m, editing: false }));
  } catch (e) {
    // ignore
  }
};

const onHandleBlur = (record: SocialMedia) => {
  if (record.handle) {
    let val = record.handle.trim();
    if (val.startsWith('http://') || val.startsWith('https://') || val.includes('.com/') || val.includes('.com')) {
      // If the user pastes a URL into the handle field, try to move it to url and extract handle
      if (!record.url) {
        record.url = val;
      }
      try {
        const urlStr = val.startsWith('http') ? val : 'https://' + val;
        const parsedUrl = new URL(urlStr);
        const parts = parsedUrl.pathname.split('/').filter(p => p);
        if (parts.length > 0) {
          const last = parts[parts.length - 1];
          if (last && !last.includes('watch') && !last.includes('search')) {
            val = last.startsWith('@') ? last : last;
          } else {
            val = '';
          }
        } else {
          val = ''; // No path, just domain
        }
      } catch (e) {
        val = ''; // Failed to parse
      }

      // If it still looks like a URL after extraction, clear it completely
      if (val.startsWith('http://') || val.startsWith('https://') || val.includes('.com')) {
        record.handle = '';
      } else {
        record.handle = val;
      }

      // Try to detect platform based on the new url
      if (record.url && !record.platform) {
        detectPlatform(record);
      }
    }
  }
};

const onUrlChange = (record: SocialMedia) => {
    // Real-time detection could go here, but blur is safer to avoid jitter
    if (!record.platform && record.url) {
        detectPlatform(record);
    }
};

const onUrlBlur = (record: SocialMedia) => {
    if (record.url) {
        detectPlatform(record);
    }
};

const detectPlatform = (record: SocialMedia) => {
    const url = record.url.toLowerCase();
    if (url.includes('facebook.com') || url.includes('fb.com')) {
        record.platform = 'FACEBOOK';
    } else if (url.includes('tiktok.com')) {
        record.platform = 'TIKTOK';
    } else if (url.includes('youtube.com') || url.includes('youtu.be')) {
        record.platform = 'YOUTUBE';
    } else if (url.includes('instagram.com') || url.includes('instagr.am')) {
        record.platform = 'INS';
    } else if (url.includes('twitter.com') || url.includes('x.com')) {
        record.platform = 'TWITTER';
    } else if (url.includes('pinterest.com')) {
        record.platform = 'PINTEREST';
    }
    
    // Attempt to extract handle if empty
    if (!record.handle && record.platform) {
        try {
            const parts = record.url.split('/').filter(p => p);
            const last = parts[parts.length - 1];
            // Simple heuristic: last part is handle (works for FB, INS, TikTok often)
            if (last && !last.includes('watch') && !last.includes('search')) {
               record.handle = last.startsWith('@') ? last : last; 
            }
        } catch (e) {
            // ignore
        }
    }
};

const handleCancel = (index: number) => {
  const item = list.value[index];
  if (!item) return;
  if (!item) return;
  if (!item.platform && !item.handle) handleDelete(index);
  else item.editing = false;
};

const confirmDelete = (index: number) => {
  showDeleteConfirm({
    title: '确定要移除此社媒账号吗？',
    content: '移除后该账号的相关数据将无法直接查看。',
    onOk: () => handleDelete(index),
  });
};

const handleDelete = async (index: number) => {
  const item = list.value[index];
  if (!item) return;
  
  if (props.influencerId && item.id) {
    try {
      await influencerService.deleteSocialMedia(item.id);
    } catch (e) {
      message.error('删除失败');
      return;
    }
  }
  
  const wasDefault = item.isDefault;
  list.value.splice(index, 1);
  // 如果删掉的是默认项，且还有剩余项，则把第一项设为默认
  if (wasDefault && list.value.length > 0) {
    const firstItem = list.value[0];
    if (firstItem) firstItem.isDefault = true;
  }
  message.success('已从档案中移除');
};

const formatNumber = (num: any) => {
  if (num === undefined || num === null) return '-';
  if (num >= 1000000) return (num / 1000000).toFixed(1) + 'M';
  if (num >= 1000) return (num / 1000).toFixed(1) + 'K';
  return num.toString();
};

const getPlatformColor = (p: string) => {
  const map: Record<string, string> = { TIKTOK: 'black', FACEBOOK: 'blue', YOUTUBE: 'red', INS: 'pink' };
  return map[p] || 'default';
};

const handleGoLink = (url: string) => {
  if (!url) return;
  // 确保 URL 有正确的协议前缀
  const targetUrl = url.startsWith('http://') || url.startsWith('https://') 
    ? url 
    : `https://${url}`;
  window.open(targetUrl, '_blank');
};

const handleCopyLink = (url: string) => {
  if (!url) return;
  navigator.clipboard.writeText(url).then(() => {
    message.success('链接已复制到剪贴板');
  }).catch(() => {
    message.error('复制失败');
  });
};
</script>

<style lang="scss" scoped>
.social-media-tab {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px; /* 压缩头部间距 */
  .header-tips { font-size: 14px; color: #94a3b8; font-weight: 500; }
}

.premium-add-btn {
  height: 32px;
  background: #1e293b;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  font-size: 12px;
  padding: 0 14px;
  transition: all 0.3s;
  &:hover { background: #334155; transform: translateY(-1px); box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); }
}

.table-container { 
  flex: 1; 
  border-radius: 12px; 
  border: 1px solid #f1f5f9; 
  background: #fff;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.premium-table {
  flex: 1;
  overflow: hidden;
  :deep(.ant-table-wrapper), :deep(.ant-table), :deep(.ant-table-container) {
    height: 100% !important;
  }
  :deep(.ant-table-body) {
    overflow-y: auto !important;
  }
  :deep(.ant-table-thead > tr > th) {
    background: #f8fafc !important;
    color: #475569 !important;
    font-weight: 700 !important;
    font-size: 13px !important;
    border-bottom: 1px solid #f1f5f9 !important;
    padding: 16px !important;
  }
  :deep(.ant-table-tbody > tr > td) {
    padding: 8px 16px !important; /* 统一为与打款信息一致的间距 */
    border-bottom: 1px solid #f8fafc !important;
    transition: all 0.2s;
  }
  
  /* 针对主页链接和操作列，强制零间距以保持极致紧凑 */
  :deep(.ant-table-tbody > tr > td[data-index="link"]),
  :deep(.ant-table-tbody > tr > td[data-key="link"]),
  :deep(.ant-table-tbody > tr > td[data-index="action"]),
  :deep(.ant-table-tbody > tr > td[data-key="action"]) {
    padding: 0 16px !important;
  }
  :deep(.ant-table-tbody > tr:hover > td) { background: #fcfdfe !important; }
}

.platform-display {
  display: flex; align-items: center; gap: 10px;
  .platform-indicator {
    width: 6px; height: 6px; border-radius: 50%;
    &.black { background: #000; box-shadow: 0 0 6px rgba(0,0,0,0.2); }
    &.blue { background: #2563eb; }
    &.red { background: #ef4444; }
    &.pink { background: #db2777; }
    &.default { background: #94a3b8; }
  }
  .platform-name { font-weight: 700; color: #334155; font-size: 13px; }
}

.text-main { font-weight: 600; color: #1e293b; font-size: 13px; }
.fans-count { font-family: 'JetBrains Mono', monospace; font-weight: 700; color: #64748b; font-size: 13px; }

.action-btns-wrapper {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 8px;

  .detail-btn {
    background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
    border: none;
    box-shadow: 0 2px 4px rgba(59, 130, 246, 0.2);
    font-weight: 600;
    font-size: 11px;
    height: 24px;
    padding: 0 10px;
    &:hover { transform: translateY(-1px); box-shadow: 0 3px 8px rgba(59, 130, 246, 0.3); }
  }

  .transfer-btn {
    border: 1px solid #e2e8f0;
    color: #475569;
    font-weight: 600;
    font-size: 11px;
    background: #fff;
    height: 24px;
    padding: 0 10px;
    &:hover { color: #3b82f6; border-color: #3b82f6; background: #f0f7ff; }
    &.ant-btn-dangerous {
      color: #ef4444;
      &:hover { border-color: #ef4444; background: #fef2f2; color: #ef4444; }
    }
  }
}

.homepage-link-cell {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: flex-start;
  min-height: 40px; /* 压缩 min-height 解决视觉空跳 */

  .link-actions-default {
    display: flex;
    gap: 4px;
    opacity: 1;
    transition: all 0.2s;
  }


  .action-btn {
    border-radius: 6px;
    font-size: 11px;
    font-weight: 600;
    height: 24px;
    padding: 0 8px;
    
    &.go-btn {
      background: linear-gradient(135deg, #6366f1 0%, #3b82f6 100%);
      border: none;
      box-shadow: 0 2px 8px rgba(59, 130, 246, 0.25);
      &:hover { transform: translateY(-1px); box-shadow: 0 4px 12px rgba(59, 130, 246, 0.35); }
    }

    &.copy-btn {
      border: 1px solid #e2e8f0;
      color: #64748b;
      &:hover { border-color: #3b82f6; color: #3b82f6; background: #eff6ff; }
    }
  }
}

.table-input, .table-input-number, .table-select {
  width: 100% !important;
  min-width: 180px !important;
  :deep(.ant-input), :deep(.ant-input-number), :deep(.ant-select-selector) {
    border-radius: 8px !important;
    border: 1.5px solid #e2e8f0 !important;
    background: #fff !important;
    height: 36px !important;
    display: flex; align-items: center;
  }
}

.default-radio-cell {
  display: flex;
  justify-content: center;
  cursor: pointer;
  padding: 4px;

  .custom-radio {
    width: 18px;
    height: 18px;
    border: 2px solid #cbd5e1;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.2s;

    .radio-dot {
      width: 8px;
      height: 8px;
      background: #3b82f6;
      border-radius: 50%;
      opacity: 0;
      transform: scale(0.5);
      transition: all 0.2s;
    }

    &.checked {
      border-color: #3b82f6;
      .radio-dot { opacity: 1; transform: scale(1); }
    }

    &:hover:not(.checked) {
      border-color: #94a3b8;
    }
  }
}
</style>


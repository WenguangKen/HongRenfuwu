<template>
  <div class="vin-row-container" :class="{ 'selected': isSelected }" @click="toggleSelect">
    <div class="vcell user-col">
       <div class="user-main">
          <a-avatar :src="data.avatar" :size="32" class="vin-avatar">
            <template #icon><UserOutlined /></template>
          </a-avatar>
          <div class="user-info">
             <div class="user-name" @click.stop="onDetail(data)">{{ data.realName || data.nickName || '未命名' }}</div>
             <div class="user-sub">ID: {{ data.id }}</div>
          </div>
       </div>
    </div>

    <div class="vcell social-col">
       <div class="social-summary">
          <div v-if="data.socialMedias?.length || data.socialMediaList?.length" class="sm-list-minimal">
            <div v-for="(sm, idx) in (data.socialMedias || data.socialMediaList).slice(0, 2)" :key="idx" class="sm-item-min">
               <span class="sm-platform-dot" :class="`platform-${getPlatformAbbr(sm.platform)}`" />
               <span class="sm-handle-min">{{ extractHandle(sm.handle || sm.url || '') }}</span>
            </div>
            <div v-if="(data.socialMedias || data.socialMediaList).length > 2" class="sm-more">...</div>
          </div>
          <div v-else class="empty-text">-</div>
       </div>
    </div>

    <div class="vcell platform-col">
       <div class="platform-tags-container">
          <a-tag v-if="displayPlatform" :class="['premium-platform-tag', `platform-${getPlatformAbbr(displayPlatform)}`]">
            <template #icon>
              <component :is="getPlatformIcon(displayPlatform)" />
            </template>
            {{ getPlatformAbbr(displayPlatform) }}
          </a-tag>
          <span v-else class="empty-text">-</span>
       </div>
    </div>

    <div class="vcell country-col">
       <div class="country-info">
          <span v-if="data.country" class="country-code">{{ data.country }}</span>
          <span v-else class="empty-text">-</span>
       </div>
    </div>

    <div class="vcell email-col">
       <div class="email-info truncate-text" :title="data.email">
          {{ data.email || '-' }}
       </div>
    </div>

    <div class="vcell paid-col">
       <a-tag :color="data.isPaid ? 'success' : 'default'" class="mini-tag">
          {{ data.isPaid ? '已付费' : '未付费' }}
       </a-tag>
    </div>

    <div class="vcell date-col">
       <div class="date-text">{{ data.updatedAt ? dayjs(data.updatedAt).format('MM-DD HH:mm') : '-' }}</div>
    </div>

    <div class="vcell action-col">
       <div class="action-btns-wrapper">
         <a-button type="primary" size="small" class="detail-btn" @click.stop="onDetail(data)">
            详情
         </a-button>
         <a-dropdown @click.stop>
            <a-button class="transfer-btn" size="small">
              流转 <DownOutlined style="font-size: 10px;"/>
            </a-button>
            <template #overlay>
              <a-menu class="premium-menu">
                <a-menu-item key="transfer">暂无快速流转</a-menu-item>
              </a-menu>
            </template>
         </a-dropdown>
       </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { 
  UserOutlined, InstagramOutlined, YoutubeOutlined, 
  FacebookOutlined, TwitterOutlined, DownOutlined,
  DeleteOutlined
} from '@ant-design/icons-vue';
import dayjs from 'dayjs';

const props = defineProps<{
  source: any;
  index?: number;
  selectedKeys: (string | number)[];
  rowKey: string;
  onToggleSelect: (id: string | number) => void;
  onDetail: (record: any) => void;
}>();

const data = computed(() => props.source || {});

const isSelected = computed(() => {
  return props.selectedKeys.includes(data.value[props.rowKey]);
});

const toggleSelect = () => {
  props.onToggleSelect(data.value[props.rowKey]);
};

// 提取默认平台逻辑
const displayPlatform = computed(() => {
  const item = data.value;
  let platform = item.defaultPlatform || item.platformName;
  
  const smList = item.socialMedias || item.socialMediaList || [];
  const defaultSm = smList.find((sm: any) => sm.isDefault || sm.isPrimary);
  
  if (defaultSm) {
    platform = defaultSm.platform;
  } else if (smList.length > 0 && !platform) {
    platform = smList[0].platform;
  }
  return platform;
});

const TikTokIcon = {
  template: `<svg viewBox="0 0 448 512" width="1em" height="1em" fill="currentColor">
    <path d="M448,209.91a210.06,210.06,0,0,1-122.77-39.25V349.38A162.55,162.55,0,1,1,185,188.31V278.2a74.62,74.62,0,1,0,52.23,71.18V0l88,0a121.18,121.18,0,0,0,1.86,22.17h0A122.18,122.18,0,0,0,381,102.39a121.43,121.43,0,0,0,67,20.14Z"/>
  </svg>`
};

const getPlatformIcon = (platform: string) => {
  if (!platform) return TikTokIcon;
  const p = platform.toUpperCase();
  if (p.includes('INSTAGRAM') || p === 'INS' || p === 'IG') return InstagramOutlined;
  if (p.includes('TIKTOK') || p === 'TT') return TikTokIcon;
  if (p.includes('YOUTUBE') || p === 'YT') return YoutubeOutlined;
  if (p.includes('FACEBOOK') || p === 'FB') return FacebookOutlined;
  if (p.includes('TWITTER') || p === 'X') return TwitterOutlined;
  return TikTokIcon;
};

const getPlatformAbbr = (platform: string) => {
  if (!platform) return 'TT';
  const p = platform.toUpperCase();
  if (p.includes('INSTAGRAM') || p === 'INS' || p === 'IG') return 'IG';
  if (p.includes('TIKTOK') || p === 'TT') return 'TT';
  if (p.includes('YOUTUBE') || p === 'YT') return 'YT';
  if (p.includes('FACEBOOK') || p === 'FB') return 'FB';
  if (p.includes('TWITTER') || p === 'X') return 'X';
  return platform;
};

const extractHandle = (text: string | undefined): string => {
  if (!text) return '';
  let clean: string = text.trim();
  if (clean.includes('/') || clean.includes('.com')) {
    const parts = clean.split('/');
    let h = parts.pop() || '';
    if (h.includes('?')) h = h.split('?')[0] || '';
    return h.startsWith('@') ? h : '@' + h;
  }
  return clean.startsWith('@') ? clean : '@' + clean;
};
</script>

<style scoped lang="scss">
.vin-row-container {
  display: flex;
  align-items: center;
  padding: 8px 16px;
  border-bottom: 1px solid #f1f5f9;
  cursor: pointer;
  transition: all 0.2s ease;
  min-width: 1000px;
  background: #fff;

  &:hover {
    background: #f8fafc;
  }

  &.selected {
    background: #eff6ff;
  }

  .vcell {
    flex: 1;
    padding: 0 10px;
    overflow: hidden;
    display: flex;
    align-items: center;
  }

  .user-col { flex: 2; }
  .social-col { flex: 2; }
  .platform-col { flex: 1.2; justify-content: center; }
  .country-col { flex: 1; justify-content: center; }
  .email-col { flex: 1.5; }
  .paid-col { flex: 1; justify-content: center; }
  .date-col { flex: 1.2; justify-content: center; }
  .action-col { flex: 1.5; justify-content: center; }

  /* Action Buttons */
  .action-btns-wrapper {
    display: flex;
    align-items: center;
    gap: 6px;
    
    .detail-btn {
      border-radius: 6px;
      font-weight: 600;
      height: 26px;
      padding: 0 10px;
      font-size: 11px;
      background: linear-gradient(135deg, #fb7185 0%, #e11d48 100%);
      border: none;
      box-shadow: 0 2px 4px rgba(225, 29, 72, 0.15);
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      color: #fff;
      
      &:hover {
        transform: translateY(-1px);
        box-shadow: 0 4px 10px rgba(225, 29, 72, 0.25);
      }
    }
    
    .transfer-btn {
      border-radius: 6px;
      color: #64748b;
      height: 26px;
      padding: 0 6px;
      display: flex;
      align-items: center;
      gap: 2px;
      font-size: 11px;
      border: 1px solid #e2e8f0;
      background: #f8fafc;
      &:hover {
        color: #3b82f6;
        border-color: #3b82f6;
        background: #fff;
      }
    }
  }

  /* Premium Platform Tags */
  .premium-platform-tag {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    padding: 2px 10px;
    border-radius: 6px;
    font-weight: 700;
    font-size: 11px;
    letter-spacing: 0.5px;
    text-transform: uppercase;
    border: none;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
    transition: all 0.3s ease;
    cursor: default;

    &.platform-TT { background: linear-gradient(135deg, #000 0%, #333 100%); color: #fff; }
    &.platform-IG { background: linear-gradient(135deg, #833ab4 0%, #fd1d1d 50%, #fcb045 100%); color: #fff; }
    &.platform-YT { background: linear-gradient(135deg, #FF0000 0%, #cc0000 100%); color: #fff; }
    &.platform-FB { background: linear-gradient(135deg, #1877F2 0%, #0c5dc7 100%); color: #fff; }
    &.platform-X { background: #000; color: #fff; }
  }

  .user-main {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .user-info {
    .user-name {
      font-weight: 600;
      color: #1e293b;
      font-size: 13px;
      &:hover { color: #3b82f6; text-decoration: underline; }
    }
    .user-sub { font-size: 11px; color: #94a3b8; }
  }

  .sm-item-min {
    display: flex;
    align-items: center;
    gap: 5px;
    margin-bottom: 2px;
  }

  .sm-platform-dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    &.platform-TT { background: #000; }
    &.platform-IG { background: #e1306c; }
    &.platform-YT { background: #ff0000; }
    &.platform-FB { background: #1877f2; }
  }

  .sm-handle-min { font-size: 12px; color: #475569; }
  .empty-text { color: #cbd5e1; font-size: 12px; }
  .truncate-text { white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
  .date-text { font-size: 12px; color: #64748b; }
}
</style>

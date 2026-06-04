/**
 * 红人管理 — 状态与平台枚举
 */

/** 红人状态 */
export enum InfluencerStatus {
  UNSCREENED = 'UNSCREENED',
  REJECTED = 'REJECTED',
  PENDING = 'PENDING',
  CONTACTED = 'CONTACTED',
  COMMUNICATING = 'COMMUNICATING',
  COOPERATING = 'COOPERATING',
  DORMANT = 'DORMANT',
  PAUSED = 'PAUSED',
  BLACKLIST = 'BLACKLIST',
  TERMINATED = 'TERMINATED',
}

/** 红人阶段 */
export enum InfluencerStage {
  POOL = 'POOL',
  LIST = 'LIST',
}

/** 状态中文标签 */
export const STATUS_LABEL: Record<InfluencerStatus, string> = {
  [InfluencerStatus.UNSCREENED]: '待筛选',
  [InfluencerStatus.REJECTED]: '暂不合适',
  [InfluencerStatus.PENDING]: '待联系',
  [InfluencerStatus.CONTACTED]: '已联系',
  [InfluencerStatus.COMMUNICATING]: '沟通中',
  [InfluencerStatus.COOPERATING]: '合作中',
  [InfluencerStatus.DORMANT]: '休眠中',
  [InfluencerStatus.PAUSED]: '暂不合作',
  [InfluencerStatus.BLACKLIST]: '黑名单',
  [InfluencerStatus.TERMINATED]: '不再合作',
};

/** 需要填写原因的目标状态 */
export const REASON_REQUIRED_STATUSES: InfluencerStatus[] = [
  InfluencerStatus.PAUSED,
  InfluencerStatus.BLACKLIST,
  InfluencerStatus.TERMINATED,
];

/** 全部流转选项 */
export const ALL_TRANSFER_OPTIONS: { key: string; label: string; targetStage?: string }[] = [
  { key: InfluencerStatus.UNSCREENED, label: '资源池-待筛选', targetStage: InfluencerStage.POOL },
  { key: InfluencerStatus.REJECTED, label: '资源池-暂不合适', targetStage: InfluencerStage.POOL },
  { key: InfluencerStatus.PENDING, label: '待联系' },
  { key: InfluencerStatus.CONTACTED, label: '已联系' },
  { key: InfluencerStatus.COMMUNICATING, label: '沟通中' },
  { key: InfluencerStatus.COOPERATING, label: '合作中' },
  { key: InfluencerStatus.DORMANT, label: '休眠中' },
  { key: InfluencerStatus.PAUSED, label: '暂不合作' },
  { key: InfluencerStatus.BLACKLIST, label: '黑名单' },
  { key: InfluencerStatus.TERMINATED, label: '不再合作' },
];

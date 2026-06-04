/**
 * 红人筛选组合式函数
 *
 * 提供红人列表/资源池的筛选功能：
 * - createInfluencerInitialFilter()：生成初始筛选表单状态
 * - formatFilterForApi()：将前端筛选表单转换为后端 API 参数
 * - filterInfluencers()：本地内存过滤（用于前端即时筛选）
 *
 * @module useInfluencerFilter
 */
import type { InfluencerFilterDto } from '@/types/influencer';
import dayjs from 'dayjs';

/**
 * 创建初始筛选表单状态
 * 用于 reactive(createInfluencerInitialFilter()) 初始化筛选表单
 *
 * @returns 包含所有筛选字段默认值的对象
 */
export function createInfluencerInitialFilter(): InfluencerFilterDto {
  return {
    /* --- API 字段 --- */
    searchKey: '',              // 搜索关键词（映射到 name）
    stage: 'POOL',              // 阶段：POOL=资源池, ONBOARDED=红人列表
    status: undefined,          // 状态：PENDING/COOPERATING/DORMANT 等
    listStatus: undefined,      // 「全部」Tab 下的红人状态筛选（API status）
    country: undefined,         // 国家筛选
    platform: undefined,        // 建联平台筛选
    socialPlatform: undefined,  // 社媒平台筛选
    brand: undefined,           // 品牌筛选

    /* --- UI 辅助字段（前端状态，不直接传后端） --- */
    name: '',                   // 红人名称输入
    email: '',                  // 红人邮箱输入
    source: undefined,          // 红人来源
    link: '',                   // 主页链接
    influencerType: undefined,  // 红人类型
    race: undefined,            // 人种/肤色
    fansRange: undefined,       // 粉丝区间（下拉选择）
    timeType: 'created',        // 时间类型：created=创建时间, updated=更新时间
    timeRange: [],              // 时间范围 [开始, 结束]
    owner: undefined,           // 负责人筛选
    contactPerson: undefined,   // 联络人筛选
    socialHandle: '',           // 社媒账号名搜索
    fansMin: undefined,         // 粉丝数最小值（手动输入）
    fansMax: undefined,         // 粉丝数最大值（手动输入）

    /* --- 业务筛选 --- */
    isPaid: undefined,          // 是否付费合作
    tagIds: undefined,          // 红人标签 ID 列表
    liaisonTagIds: undefined,   // 联络员标签 ID 列表
    ownerIds: undefined,         // 负责人 ID 列表

    /* --- 内容 --- */
    hasOutputContent: undefined, // 是否有输出内容

    /* --- 合作次数 --- */
    minSampleCount: undefined,  // 最小合作次数
    maxSampleCount: undefined,  // 最大合作次数

    /* --- 空白筛选 --- */
    blankFields: [],            // 空白字段筛选列表

    /* --- 分页 --- */
    page: 1,                    // 当前页码（1-based）
    size: 20                    // 每页大小
  };
}

/**
 * 将前端筛选表单转换为后端 API 参数
 *
 * 处理逻辑：
 * 1. 根据当前 Tab 映射 Stage 和 Status
 * 2. 将 UI 字段映射到 API 字段（如 name → searchKey, email → email/emails）
 * 3. 处理批量搜索（逗号分隔 → 数组）
 * 4. 处理粉丝区间、时间范围等复合筛选条件
 *
 * @param filter - 前端筛选表单状态
 * @param activeTabKey - 当前激活的 Tab key（pending/cooperating 等）
 * @returns 格式化后的 API 请求参数
 */
export function formatFilterForApi(filter: any, activeTabKey: string): InfluencerFilterDto {
  const dto: any = {
    page: filter.page || 1,
    size: filter.size || 20,
    blankFields: filter.blankFields ? [...filter.blankFields] : []
  };

  /* --- 红人名称 / 搜索关键词 --- */
  if (filter.name) {
    const delimiters = /[,，;；\n\t]+/;
    if (delimiters.test(filter.name)) {
      // 批量搜索模式：多分隔符支持
      const nameList = filter.name.split(delimiters).map((n: string) => n.trim()).filter(Boolean);
      if (nameList.length > 1) {
        dto.names = nameList;
      } else if (nameList.length === 1) {
        dto.searchKey = nameList[0];
      }
    } else {
      dto.searchKey = filter.name;
    }
  }

  /* --- 阶段/状态映射（根据 Tab） --- */
  const poolTabs = ['to-filter', 'rejected'];
  const onboardedTabs = ['all', 'pending', 'contacted', 'communicating', 'cooperating', 'dormant', 'paused', 'blacklist', 'terminated'];

  if (poolTabs.includes(activeTabKey)) {
    dto.stage = 'POOL';
  } else if (onboardedTabs.includes(activeTabKey)) {
    dto.stage = 'ONBOARDED';
  } else if (filter.stage) {
    dto.stage = filter.stage;
  }

  // Tab → Status 映射
  switch (activeTabKey) {
    case 'pending':       dto.status = 'PENDING'; break;
    case 'contacted':     dto.status = 'CONTACTED'; break;
    case 'communicating': dto.status = 'COMMUNICATING'; break;
    case 'cooperating':   dto.status = 'COOPERATING'; break;
    case 'dormant':       dto.status = 'DORMANT'; break;
    case 'paused':        dto.status = 'PAUSED'; break;
    case 'blacklist':     dto.status = 'BLACKLIST'; break;
    case 'terminated':    dto.status = 'TERMINATED'; break;
    case 'rejected':      dto.status = 'REJECTED'; break;
    case 'to-filter':     dto.status = 'UNSCREENED'; break;
    case 'all':
      if (filter.listStatus) dto.status = filter.listStatus;
      break;
    default: break;
  }

  /* --- 基本筛选条件（过滤掉 __BLANK__ 标记值） --- */
  const checkBlank = (arr: any[] | undefined, fieldName: string) => {
    if (arr?.includes('__BLANK__')) {
      if (!dto.blankFields) dto.blankFields = [];
      if (!dto.blankFields.includes(fieldName)) dto.blankFields.push(fieldName);
    }
  };
  const stripBlank = (arr: any[] | undefined) => arr?.filter((v: any) => v !== '__BLANK__');
  if (filter.country) {
    const arr = Array.isArray(filter.country) ? filter.country : [filter.country];
    checkBlank(arr, 'country');
    const cleaned = stripBlank(arr);
    if (cleaned && cleaned.length > 0) dto.country = cleaned.join(',');
  }
  if (filter.platform) {
    const arr = Array.isArray(filter.platform) ? filter.platform : [filter.platform];
    checkBlank(arr, 'platform');
    const cleaned = stripBlank(arr);
    if (cleaned && cleaned.length > 0) dto.platform = cleaned as any;
  }
  if (filter.socialPlatform) {
    const arr = Array.isArray(filter.socialPlatform) ? filter.socialPlatform : [filter.socialPlatform];
    checkBlank(arr, 'socialPlatform');
    const cleaned = stripBlank(arr);
    if (cleaned && cleaned.length > 0) dto.socialPlatform = cleaned as any;
  }
  if (filter.isPaid !== undefined) dto.isPaid = filter.isPaid;

  // 负责人 / 联络人（过滤 __BLANK__并收集为空字段）
  if (filter.owner) {
    const arr = Array.isArray(filter.owner) ? filter.owner : [filter.owner];
    checkBlank(arr, 'owner');
    const cleaned = stripBlank(arr);
    if (cleaned && cleaned.length > 0) dto.ownerIds = cleaned;
  }
  if (filter.contactPerson) {
    const arr = Array.isArray(filter.contactPerson) ? filter.contactPerson : [filter.contactPerson];
    checkBlank(arr, 'contactPerson');
    const cleaned = stripBlank(arr);
    if (cleaned && cleaned.length > 0) dto.contactPersonIds = cleaned;
  }
  // 联络员标签（liaisonTagIds）也可能包含 __BLANK__，后面会处理

  /* --- 邮箱筛选（支持批量搜索） --- */
  if (filter.email) {
    if (filter.email.includes(',')) {
      // 批量搜索模式：逗号分隔 → emails 数组（后端使用 IN 查询）
      const emailList = filter.email.split(',').map((e: string) => e.trim()).filter(Boolean);
      if (emailList.length > 1) {
        dto.emails = emailList;
      } else if (emailList.length === 1) {
        dto.email = emailList[0];
      }
    } else {
      // 单个邮箱模糊搜索
      dto.email = filter.email;
    }
  }

  /* --- 其他文本筛选（过滤 __BLANK__并收集为空字段） --- */
  if (filter.source) dto.origin = filter.source.toUpperCase();    // 来源 → Origin 枚举
  if (filter.influencerType) {
    const arr = Array.isArray(filter.influencerType) ? filter.influencerType : [filter.influencerType];
    checkBlank(arr, 'influencerType');
    const cleaned = stripBlank(arr);
    if (cleaned && cleaned.length > 0) dto.influencerType = cleaned as any;
  }
  if (filter.link) dto.profileLink = filter.link;                  // 主页链接
  if (filter.race) {
    const arr = Array.isArray(filter.race) ? filter.race : [filter.race];
    checkBlank(arr, 'race');
    const cleaned = stripBlank(arr);
    if (cleaned && cleaned.length > 0) dto.race = cleaned as any;
  }

  /* --- 时间范围筛选 --- */
  if (filter.timeType && filter.timeRange && filter.timeRange.length === 2) {
    dto.timeType = filter.timeType;
    // 确保 timeStart/timeEnd 是字符串而非 dayjs 对象，避免后端反序列化错误
    const start = filter.timeRange[0];
    const end = filter.timeRange[1];
    dto.timeStart = typeof start === 'string' ? start : dayjs(start).format('YYYY-MM-DD');
    dto.timeEnd = typeof end === 'string' ? end : dayjs(end).format('YYYY-MM-DD');
  }

  /* --- 粉丝区间筛选 --- */
  // 优先使用手动输入的精确值
  if (filter.fansMin !== undefined && filter.fansMin !== null) {
    dto.fanRangeMin = filter.fansMin;
  }
  if (filter.fansMax !== undefined && filter.fansMax !== null) {
    dto.fanRangeMax = filter.fansMax;
  }
  // 若未手动输入，则使用下拉选择的预设区间
  if (!dto.fanRangeMin && !dto.fanRangeMax && filter.fansRange) {
    const range = filter.fansRange;
    if (range === '0-10k')     { dto.fanRangeMin = 0;      dto.fanRangeMax = 10000; }
    else if (range === '10k-50k')  { dto.fanRangeMin = 10000;  dto.fanRangeMax = 50000; }
    else if (range === '50k-100k') { dto.fanRangeMin = 50000;  dto.fanRangeMax = 100000; }
    else if (range === '100k+')    { dto.fanRangeMin = 100000; }
  }

  /* --- 社媒账号名搜索 --- */
  if (filter.socialHandle) {
    (dto as any).socialHandle = filter.socialHandle;
  }

  /* --- 标签筛选 --- */
  if (filter.tagIds && filter.tagIds.length > 0) {
    dto.tagIds = filter.tagIds;
  }

  /* --- 内容筛选 --- */
  if (filter.hasOutputContent !== undefined && filter.hasOutputContent !== null && filter.hasOutputContent !== '') {
    dto.hasOutputContent = filter.hasOutputContent === 'true' || filter.hasOutputContent === true;
  }

  /* --- 合作次数筛选 --- */
  if (filter.minSampleCount !== undefined) {
    dto.minSampleCount = filter.minSampleCount;
  }
  if (filter.maxSampleCount !== undefined) {
    dto.maxSampleCount = filter.maxSampleCount;
  }

  /* --- 联络员标签筛选（过滤 __BLANK__） --- */
  if (filter.liaisonTagIds && filter.liaisonTagIds.length > 0) {
    const cleaned = filter.liaisonTagIds.filter((v: any) => v !== '__BLANK__');
    if (cleaned.length > 0) dto.liaisonTagIds = cleaned;
  }

  /* --- 空白字段筛选处理完毕，移除为空记录 --- */
  if (dto.blankFields && dto.blankFields.length === 0) {
    delete dto.blankFields;
  }

  return dto;
}

/**
 * 本地内存过滤红人列表
 *
 * 用于不经过后端 API 的前端即时筛选（如已加载全部数据时）
 * 注意：此方法仅用于简单场景，复杂筛选应通过 API 进行
 *
 * @param data - 待筛选的红人数据数组
 * @param filter - 筛选条件
 * @param options - 可选配置（如是否启用时间筛选）
 * @returns 筛选后的红人数据数组
 */
export function filterInfluencers(
  data: any[],
  filter: any,
  options: { enableTime?: boolean } = {}
): any[] {
  return data.filter(item => {
    // 名称模糊匹配
    if (filter.name && !item.name?.toLowerCase().includes(filter.name.toLowerCase())) {
      return false;
    }

    // 平台精确匹配
    if (filter.platform && (!item.platforms || !item.platforms.includes(filter.platform))) {
      return false;
    }

    // 国家精确匹配
    if (filter.country && item.country !== filter.country) {
      return false;
    }

    // 来源精确匹配
    if (filter.source && item.source !== filter.source) {
      return false;
    }

    // 邮箱模糊匹配
    if (filter.email && !item.email?.toLowerCase().includes(filter.email.toLowerCase())) {
      return false;
    }

    // 主页链接模糊匹配
    if (filter.link && !item.link?.toLowerCase().includes(filter.link.toLowerCase())) {
      return false;
    }

    // 是否付费精确匹配
    if (filter.isPaid !== undefined && item.isPaid !== filter.isPaid) {
      return false;
    }

    // 红人类型精确匹配
    if (filter.influencerType && item.influencerType !== filter.influencerType) {
      return false;
    }

    // 时间范围筛选（可选启用）
    if (options.enableTime && filter.timeType && filter.timeRange?.length === 2) {
      const [start, end] = filter.timeRange;
      const dateField = filter.timeType === 'created' ? item.createdAt : item.updatedAt;
      if (dateField) {
        const itemDate = dayjs(dateField);
        if (itemDate.isBefore(dayjs(start)) || itemDate.isAfter(dayjs(end).endOf('day'))) {
          return false;
        }
      }
    }

    return true;
  });
}

/**
 * 导出模板管理工具
 */

// 用户使用统计
export interface UserUsage {
  userId: string; // 用户ID
  useCount: number; // 使用次数
  lastUsedAt: string; // 最后使用时间
}

export interface ExportTemplate {
  id: string;
  name: string; // 模板名称
  userId: string; // 创建者ID
  userName?: string; // 创建者名称
  isPublic: boolean; // 是否公开（开放给所有人）
  sharedUserIds?: string[]; // 分享给特定用户的ID列表
  hiddenByUserIds?: string[]; // 隐藏该模板的用户ID列表
  fields: string[]; // 字段key列表（按顺序）
  pageType: string; // 页面类型：'influencer-list' | 'influencer-pool'
  createdAt: string; // 创建时间
  updatedAt: string; // 更新时间
  userUsage?: UserUsage[]; // 用户使用统计（按用户记录）
}

const STORAGE_KEY_PREFIX = 'export_template_';

/**
 * 获取当前用户ID的函数（可以通过setCurrentUserIdGetter设置自定义获取器）
 */
let currentUserIdGetter: (() => string) | null = null;

/**
 * 设置用户ID获取器
 */
export function setCurrentUserIdGetter(getter: () => string) {
  currentUserIdGetter = getter;
}

/**
 * 获取当前用户ID
 */
function getCurrentUserId(): string {
  if (currentUserIdGetter) {
    return String(currentUserIdGetter());
  }
  
  try {
    // 尝试从localStorage获取用户ID（如果用户store有保存）
    const userInfo = localStorage.getItem('user_info');
    if (userInfo) {
      const parsed = JSON.parse(userInfo);
      if (parsed.id) return String(parsed.id);
      if (parsed.userId) return String(parsed.userId);
    }
    // 如果没有，生成一个基于token的ID或使用默认值
    const raw = localStorage.getItem('auth_token');
    if (raw) {
      try {
        const obj = JSON.parse(raw);
        const expiresAt = Number(obj?.expiresAt) || 0;
        if (!expiresAt || Date.now() <= expiresAt) {
          const t = String(obj?.token || '');
          if (t) return `user_${t.substring(0, 8)}`;
        }
      } catch {}
    }
  } catch {
    // 忽略错误
  }
  return 'default_user';
}

/**
 * 获取当前用户名
 */
function getCurrentUserName(): string {
  try {
    const userInfo = localStorage.getItem('user_info');
    if (userInfo) {
      const parsed = JSON.parse(userInfo);
      return parsed.username || parsed.name || parsed.email || '系统用户';
    }
  } catch {}
  return '系统用户';
}

/**
 * 获取存储key
 */
function getStorageKey(pageType: string): string {
  return `${STORAGE_KEY_PREFIX}${pageType}`;
}

/**
 * 加载模板列表（导出供外部使用）
 */
export function loadTemplates(pageType: string): ExportTemplate[] {
  try {
    const key = getStorageKey(pageType);
    const data = localStorage.getItem(key);
    return data ? JSON.parse(data) : [];
  } catch {
    return [];
  }
}

/**
 * 保存模板列表
 */
function saveTemplates(pageType: string, templates: ExportTemplate[]): void {
  try {
    const key = getStorageKey(pageType);
    localStorage.setItem(key, JSON.stringify(templates));
  } catch (error) {
    console.error('保存模板失败:', error);
  }
}

/**
 * 检查模板名称是否重复
 */
export function checkTemplateNameDuplicate(pageType: string, name: string, excludeId?: string): boolean {
  const templates = loadTemplates(pageType);
  const userId = getCurrentUserId();
  
  // 检查自己创建的模板中是否有重名
  return templates.some(t => 
    String(t.userId) === String(userId) && 
    t.name === name && 
    t.id !== excludeId
  );
}

/**
 * 创建新模板
 */
export function createTemplate(
  pageType: string,
  name: string,
  fields: string[],
  isPublic: boolean = false,
  sharedUserIds: string[] = []
): ExportTemplate {
  const templates = loadTemplates(pageType);
  const userId = getCurrentUserId();
  
  // 检查名称是否重复
  if (checkTemplateNameDuplicate(pageType, name)) {
    throw new Error('模板名称已存在，请使用其他名称');
  }
  
  const now = new Date().toISOString();
  
  const template: ExportTemplate = {
    id: `template_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`,
    name,
    userId,
    userName: getCurrentUserName(),
    isPublic,
    sharedUserIds: sharedUserIds.length > 0 ? [...sharedUserIds] : undefined,
    fields: [...fields],
    pageType,
    createdAt: now,
    updatedAt: now,
  };
  
  templates.push(template);
  saveTemplates(pageType, templates);
  
  return template;
}

/**
 * 更新模板
 */
export function updateTemplate(
  pageType: string,
  templateId: string,
  updates: Partial<Pick<ExportTemplate, 'name' | 'fields' | 'isPublic' | 'sharedUserIds'>>
): boolean {
  const templates = loadTemplates(pageType);
  const index = templates.findIndex(t => t.id === templateId);
  
  if (index === -1) {
    return false;
  }
  
  const template = templates[index];
  if (!template) {
    return false;
  }
  
  // 如果更新名称，检查是否重复
  if (updates.name && updates.name !== template.name) {
    if (checkTemplateNameDuplicate(pageType, updates.name, templateId)) {
      throw new Error('模板名称已存在，请使用其他名称');
    }
  }
  
  templates[index] = {
    ...template,
    ...updates,
    updatedAt: new Date().toISOString(),
    hiddenByUserIds: undefined, // 每次更新/再次分享时清除隐藏状态
  } as ExportTemplate;
  
  // 如果sharedUserIds是空数组，设置为undefined
  if ('sharedUserIds' in updates && Array.isArray(updates.sharedUserIds) && updates.sharedUserIds.length === 0) {
    templates[index].sharedUserIds = undefined;
  }
  
  saveTemplates(pageType, templates);
  return true;
}

/**
 * 删除模板
 */
export function deleteTemplate(pageType: string, templateId: string): boolean {
  const templates = loadTemplates(pageType);
  const userId = getCurrentUserId();
  
  // 只能删除自己创建的模板
  const index = templates.findIndex(t => t.id === templateId && String(t.userId) === String(userId));
  
  if (index === -1) {
    return false;
  }
  
  templates.splice(index, 1);
  saveTemplates(pageType, templates);
  return true;
}

/**
 * 隐藏模板（对于别人分享给我的模板）
 */
export function hideTemplate(pageType: string, templateId: string): boolean {
  const templates = loadTemplates(pageType);
  const userId = getCurrentUserId();
  
  const index = templates.findIndex(t => t.id === templateId);
  if (index === -1) {
    return false;
  }
  
  const template = templates[index];
  if (!template) {
    return false;
  }
  
  const hiddenByUserIds = template.hiddenByUserIds || [];
  
  if (!hiddenByUserIds.includes(String(userId))) {
    hiddenByUserIds.push(String(userId));
    template.hiddenByUserIds = hiddenByUserIds;
    saveTemplates(pageType, templates);
  }
  
  return true;
}

/**
 * 取消隐藏模板
 */
export function unhideTemplate(pageType: string, templateId: string): boolean {
  const templates = loadTemplates(pageType);
  const userId = getCurrentUserId();
  
  const index = templates.findIndex(t => t.id === templateId);
  if (index === -1) return false;
  
  const template = templates[index];
  if (!template) return false;
  
  if (template.hiddenByUserIds) {
    template.hiddenByUserIds = template.hiddenByUserIds.filter(id => String(id) !== String(userId));
    saveTemplates(pageType, templates);
  }
  return true;
}

/**
 * 获取用户可用的模板（自己的 + 公开的 + 分享给自己的）
 */
export function getAvailableTemplates(pageType: string, includeHidden = false): ExportTemplate[] {
  const templates = loadTemplates(pageType);
  const userId = getCurrentUserId();
  
  return templates.filter(t => {
    // 自己的模板不隐藏
    if (String(t.userId) === String(userId)) return true;
    
    // 如果被当前用户隐藏了，则不可见 (除非请求包含隐藏)
    if (!includeHidden && t.hiddenByUserIds && t.hiddenByUserIds.some(id => String(id) === String(userId))) {
      return false;
    }
    
    return t.isPublic || (t.sharedUserIds && t.sharedUserIds.some(id => String(id) === String(userId)));
  });
}

/**
 * 获取用户自己创建的模板
 */
export function getUserTemplates(pageType: string): ExportTemplate[] {
  const templates = loadTemplates(pageType);
  const userId = getCurrentUserId();
  
  return templates.filter(t => String(t.userId) === String(userId));
}

/**
 * 记录模板使用（增加使用次数，更新最后使用时间）
 */
export function recordTemplateUsage(pageType: string, templateId: string): void {
  const templates = loadTemplates(pageType);
  const userId = getCurrentUserId();
  const template = templates.find(t => t.id === templateId);
  
  if (!template) {
    return;
  }
  
  // 初始化 userUsage 数组
  if (!template.userUsage) {
    template.userUsage = [];
  }
  
  // 查找当前用户的使用记录
  const userUsageIndex = template.userUsage.findIndex(u => String(u.userId) === String(userId));
  const now = new Date().toISOString();
  
  if (userUsageIndex >= 0) {
    // 更新现有记录
    const userUsage = template.userUsage[userUsageIndex];
    if (userUsage) {
      userUsage.useCount += 1;
      userUsage.lastUsedAt = now;
    }
  } else {
    // 创建新记录
    template.userUsage.push({
      userId,
      useCount: 1,
      lastUsedAt: now,
    });
  }
  
  saveTemplates(pageType, templates);
}

/**
 * 获取用户的使用统计
 */
function getUserUsage(template: ExportTemplate): UserUsage | undefined {
  const userId = getCurrentUserId();
  if (!template.userUsage) {
    return undefined;
  }
  return template.userUsage.find(u => String(u.userId) === String(userId));
  // userId 用于查找，虽然未直接使用但逻辑上需要
}

/**
 * 获取排序后的模板列表（按使用次数和使用时间排序，用于下拉选择）
 */
export function getSortedTemplates(pageType: string, includeHidden = false): ExportTemplate[] {
  const templates = getAvailableTemplates(pageType, includeHidden);
  
  return templates.sort((a, b) => {
    const usageA = getUserUsage(a);
    const usageB = getUserUsage(b);
    
    // 获取使用次数（如果没有记录，默认为0）
    const countA = usageA?.useCount || 0;
    const countB = usageB?.useCount || 0;
    
    // 优先按使用次数排序（降序）
    if (countA !== countB) {
      return countB - countA;
    }
    
    // 如果使用次数相同，按最后使用时间排序（降序，最近使用的在前）
    const timeA = usageA?.lastUsedAt ? new Date(usageA.lastUsedAt).getTime() : 0;
    const timeB = usageB?.lastUsedAt ? new Date(usageB.lastUsedAt).getTime() : 0;
    
    if (timeA !== timeB) {
      return timeB - timeA;
    }
    
    // 如果都没有使用记录，按更新时间排序（降序）
    return new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime();
  });
}

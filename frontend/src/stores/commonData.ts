/**
 * 公共数据 Pinia Store
 *
 * 管理跨页面共享的数据：用户列表、标签、国家、平台等
 * 带缓存机制 — 已加载的数据不会重复请求
 */
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';
import { getUserList, type User } from '@/services/userService';
import { getEnabledCountries, type Country } from '@/services/countryService';
import { tagService } from '@/services/tagService';

export const useCommonDataStore = defineStore('commonData', () => {
  // --- State ---
  const allUsers = ref<{ id: number; name: string, roles?: any[] }[]>([]);
  const ownerUsers = computed(() => {
    return allUsers.value.filter(u => {
      if (!u.roles) return false;
      return u.roles.some(r => 
        r.name === '红人专员' || 
        r.name === '超级管理员' || 
        r.name === '红人实习生/Agency'
      );
    });
  });
  const userNameMap = ref<Record<number, string>>({});
  const countries = ref<Country[]>([]);
  const availableTags = ref<{ id: number; name: string }[]>([]);
  const liaisonTagOptions = ref<{ id: number; name: string }[]>([]);
  const contentTags = ref<{ id: number; name: string }[]>([]);
  const paymentTypeTags = ref<{ id: number; name: string }[]>([]);
  const platforms = ref<string[]>(['Instagram', 'YouTube', 'TikTok']);
  const dynamicInfluencerTypes = ref<string[]>([]);

  // --- Loaded flags ---
  const loaded = ref({
    users: false, countries: false, tags: false,
    liaison: false, types: false, platforms: false,
    content: false, paymentType: false,
  });

  // --- Helper: safely extract array from response ---
  const toArray = (res: unknown): any[] => {
    if (Array.isArray(res)) return res;
    if (res && typeof res === 'object' && Array.isArray((res as any).data)) return (res as any).data;
    return [];
  };

  // --- Actions ---
  const loadUsers = async (force = false) => {
    if (loaded.value.users && !force) return;
    try {
      const res = await getUserList({ status: 'active', size: 1000 });
      const users = res.content || [];
      allUsers.value = users.map((u: User) => ({ id: u.id, name: u.username, roles: u.roles }));
      const map: Record<number, string> = {};
      users.forEach((u: User) => { map[u.id] = u.username; });
      userNameMap.value = map;
      loaded.value.users = true;
    } catch (e: any) {
      if (e?.name === 'CanceledError') return;
      console.error('[CommonDataStore] Failed to load users:', e);
    }
  };

  const loadCountries = async (force = false) => {
    if (loaded.value.countries && !force) return;
    try {
      const res = await getEnabledCountries();
      countries.value = Array.isArray(res) ? res : [];
      loaded.value.countries = true;
    } catch {
      countries.value = [];
    }
  };

  const loadTags = async (force = false) => {
    if (loaded.value.tags && !force) return;
    try {
      const res = await tagService.getTagsByCategory('INFLUENCER');
      availableTags.value = toArray(res).map((t: any) => ({ id: t.id, name: t.name }));
      loaded.value.tags = true;
    } catch (e) {
      console.error('[CommonDataStore] Failed to load tags:', e);
    }
  };

  const loadLiaisonTags = async (force = false) => {
    if (loaded.value.liaison && !force) return;
    try {
      const res = await tagService.getTagsByCategory('LIAISON');
      liaisonTagOptions.value = toArray(res).map((t: any) => ({ id: t.id, name: t.name }));
      loaded.value.liaison = true;
    } catch (e) {
      console.error('[CommonDataStore] Failed to load liaison tags:', e);
    }
  };

  const loadInfluencerTypes = async (force = false) => {
    if (loaded.value.types && !force) return;
    try {
      const res = await tagService.getTagsByCategory('INFLUENCER_TYPE');
      dynamicInfluencerTypes.value = toArray(res).map((t: any) => t.name);
      loaded.value.types = true;
    } catch (e) {
      console.error('[CommonDataStore] Failed to load influencer types:', e);
    }
  };

  const loadPlatforms = async (force = false) => {
    if (loaded.value.platforms && !force) return;
    try {
      const res = await tagService.getTagsByCategory('PLATFORM');
      const arr = toArray(res);
      if (arr.length > 0) platforms.value = arr.map((t: any) => t.name);
      loaded.value.platforms = true;
    } catch (e) {
      console.error('[CommonDataStore] Failed to load platforms:', e);
    }
  };

  const loadContentTags = async (force = false) => {
    if (loaded.value.content && !force) return;
    try {
      const res = await tagService.getTagsByCategory('CONTENT');
      contentTags.value = toArray(res).map((t: any) => ({ id: t.id, name: t.name }));
      loaded.value.content = true;
    } catch (e) {
      console.error('[CommonDataStore] Failed to load content tags:', e);
    }
  };

  const loadPaymentTypeTags = async (force = false) => {
    if (loaded.value.paymentType && !force) return;
    try {
      const res = await tagService.getTagsByCategory('PAYMENT_TYPE');
      paymentTypeTags.value = toArray(res).map((t: any) => ({ id: t.id, name: t.name }));
      loaded.value.paymentType = true;
    } catch (e) {
      console.error('[CommonDataStore] Failed to load payment type tags:', e);
    }
  };

  /** 一次性加载全部公共数据 */
  const loadAll = (force = false) => Promise.allSettled([
    loadUsers(force), loadCountries(force), loadTags(force),
    loadLiaisonTags(force), loadInfluencerTypes(force), loadPlatforms(force),
    loadContentTags(force), loadPaymentTypeTags(force),
  ]);

  return {
    allUsers, ownerUsers, userNameMap, countries, availableTags,
    liaisonTagOptions, contentTags, paymentTypeTags,
    platforms, dynamicInfluencerTypes,
    loaded, loadAll, loadUsers, loadCountries, loadTags,
    loadLiaisonTags, loadInfluencerTypes, loadPlatforms,
    loadContentTags, loadPaymentTypeTags,
  };
});

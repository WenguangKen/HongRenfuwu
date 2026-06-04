/**
 * 红人列表数据加载 Composable
 *
 * 职责：
 * - 加载红人列表数据 (fetchData)
 * - 加载辅助字典数据 (用户、标签、平台等)
 * - 管理 loading/pagination/displayData 状态
 *
 * @module useInfluencerListData
 */
import { ref, reactive, type Ref } from 'vue';
import { message } from 'ant-design-vue';
import { influencerService } from '@/services/influencerService';
import { tagService } from '@/services/tagService';
import { getUserList, type User } from '@/services/userService';
import { getEnabledCountries, type Country } from '@/services/countryService';
import { formatFilterForApi } from '@/composables/useInfluencerFilter';
import type { Influencer } from '@/types/influencer';
import { useUserStore } from '@/stores/user';

/**
 * 红人列表数据加载 composable
 *
 * @param filterForm - 筛选表单 reactive 对象（由主组件持有）
 * @param activeKey - 当前激活的 Tab key ref
 */
export function useInfluencerListData(
  filterForm: any,
  activeKey: Ref<string>
) {
  const userStore = useUserStore();

  // --- Loading State ---
  const loading = ref(false);
  const initialLoading = ref(true);
  const renderBatchId = ref(0);

  // --- Data State ---
  const displayData = ref<Influencer[]>([]);
  const statusCounts = ref<Record<string, number>>({});
  const pagination = reactive({
    current: 1,
    pageSize: 20,
    total: 0,
    showSizeChanger: true,
    pageSizeOptions: ['20', '50', '100', '500', '1000']
  });

  // --- Dictionary State ---
  const platforms = ref<string[]>(['Instagram', 'YouTube', 'TikTok']);
  const countries = ref<Country[]>([]);
  const dynamicInfluencerTypes = ref<string[]>([]);
  const availableTags = ref<{id: number, name: string}[]>([]);
  const liaisonTagOptions = ref<{id: number, name: string}[]>([]);
  const allUsers = ref<{id: number, name: string}[]>([
    { id: userStore.userInfo?.id || 0, name: userStore.userInfo?.username || 'Me' }
  ]);
  const userNameMap = ref<Record<number, string>>({});

  // --- 核心数据加载 ---

  /** 获取红人列表 + 状态统计 */
  const fetchData = async (fetchCounts = true) => {
    loading.value = true;
    try {
      const apiFilter = formatFilterForApi({
        ...filterForm,
        page: pagination.current,
        size: pagination.pageSize
      }, activeKey.value);

      let res;
      if (fetchCounts) {
          const result = await influencerService.getListWithCounts(apiFilter);
          res = result.list;
          if (result.statusCounts && typeof result.statusCounts === 'object') {
              statusCounts.value = result.statusCounts;
          }
      } else {
          res = await influencerService.getList(apiFilter);
      }

      // 映射用户名及平台
      const content = (res.content || []).map((item: any) => {
        let displayPlatform = item.defaultPlatform || item.platformName;
        const smList = item.socialMedias || item.socialMediaList || [];
        const defaultSm = smList.find((sm: any) => sm.isDefault || sm.isPrimary);
        if (defaultSm) {
          displayPlatform = defaultSm.platform;
        } else if (smList.length > 0 && !displayPlatform) {
          displayPlatform = smList[0].platform;
        }
        return {
          ...item,
          displayPlatform,
          ownerName: item.ownerId ? userNameMap.value[item.ownerId] || `ID:${item.ownerId}` : '-',
          contactPersonName: item.contactPersonId ? userNameMap.value[item.contactPersonId] || `ID:${item.contactPersonId}` : '-'
        };
      });
      // Time slicing rendering to prevent UI freeze on large data (500/1000 items)
      renderBatchId.value++;
      const currentBatchId = renderBatchId.value;
      const CHUNK_SIZE = 50;
      
      if (content.length <= CHUNK_SIZE) {
        displayData.value = content;
      } else {
        // Initial render: show first chunk immediately
        displayData.value = content.slice(0, CHUNK_SIZE);
        let currentIndex = CHUNK_SIZE;
        
        const renderNextChunk = () => {
          // If a new fetch was triggered, abort this render queue
          if (currentBatchId !== renderBatchId.value) return;
          
          if (currentIndex < content.length) {
            const nextChunk = content.slice(currentIndex, Math.min(currentIndex + CHUNK_SIZE, content.length));
            displayData.value = [...displayData.value, ...nextChunk];
            currentIndex += CHUNK_SIZE;
            requestAnimationFrame(renderNextChunk);
          }
        };
        requestAnimationFrame(renderNextChunk);
      }
      
      pagination.total = res.totalElements || 0;
    } catch (e) {
      if (e && (e as any).code === 'ERR_CANCELED') return;
      console.error('[fetchData] error:', e);
      message.warn('加载数据失败，请检查网络');
    } finally {
      loading.value = false;
      initialLoading.value = false;
    }
  };

  /** 单独获取状态统计 */
  const fetchStatusCounts = async () => {
    try {
      const params = formatFilterForApi(filterForm, activeKey.value);
      const res = await influencerService.getStatusCounts(params);
      statusCounts.value = res || {};
    } catch (e) {
      console.error('Failed to fetch status counts:', e);
    }
  };

  // --- 辅助数据加载 ---

  const loadUsers = async () => {
    try {
      const res = await getUserList({ status: 'active', size: 1000 });
      const users = res.content || [];
      allUsers.value = users.map((u: User) => ({ id: u.id, name: u.username }));
      const map: Record<number, string> = {};
      users.forEach((u: User) => { map[u.id] = u.username; });
      userNameMap.value = map;
    } catch (e: any) {
      if (e && (e.name === 'CanceledError' || e.code === 'ERR_CANCELED')) return;
      console.error('Failed to load users:', e);
    }
  };

  const loadTags = async () => {
    try {
      const res = await influencerService.getTags('INFLUENCER');
      availableTags.value = res || [];
    } catch (e) {
      console.error('Failed to load tags', e);
    }
  };

  const loadLiaisonTags = async () => {
    try {
      const res = await tagService.getTagsByCategory('LIAISON');
      liaisonTagOptions.value = (res || []).map((t: any) => ({ id: t.id, name: t.name }));
    } catch (e) {
      console.error('Failed to load liaison tags', e);
    }
  };

  const loadInfluencerTypes = async () => {
    try {
      const res = await tagService.getTagsByCategory('INFLUENCER_TYPE');
      dynamicInfluencerTypes.value = (res || []).map((t: any) => t.name);
    } catch (e) {
      console.error('Failed to load influencer types', e);
    }
  };

  const loadPlatforms = async () => {
    try {
      const res = await tagService.getTagsByCategory('PLATFORM');
      if (res && res.length > 0) {
        platforms.value = res.map((t: any) => t.name);
      }
    } catch (e) {
      console.error('Failed to load platform tags', e);
    }
  };

  const loadCountries = () => {
    getEnabledCountries().then(res => {
      countries.value = Array.isArray(res) ? res : [];
    }).catch(() => { countries.value = []; });
  };

  /** 并行加载所有辅助数据 */
  const loadAllAuxData = () => {
    return Promise.all([
      loadUsers(),
      loadTags(),
      loadLiaisonTags(),
      loadInfluencerTypes(),
      loadPlatforms()
    ]);
  };

  return {
    // State
    loading,
    initialLoading,
    displayData,
    statusCounts,
    pagination,
    platforms,
    countries,
    dynamicInfluencerTypes,
    availableTags,
    liaisonTagOptions,
    allUsers,
    userNameMap,
    // Methods
    fetchData,
    fetchStatusCounts,
    loadUsers,
    loadTags,
    loadLiaisonTags,
    loadInfluencerTypes,
    loadPlatforms,
    loadCountries,
    loadAllAuxData
  };
}

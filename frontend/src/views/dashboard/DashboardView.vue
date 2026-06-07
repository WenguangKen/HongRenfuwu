<template>
  <div class="dashboard-page">
    <a-spin :spinning="initialLoading">
      <!-- 核心指标 -->
      <a-row :gutter="[16, 16]" class="section-row">
        <a-col v-for="kpi in kpiCards" :key="kpi.key" :xs="24" :sm="12" :xl="6">
          <a-card :bordered="false" class="stat-card">
            <div class="stat-card-inner">
              <div class="stat-header">
                <div class="stat-title">
                  <div class="stat-label">{{ kpi.label }}</div>
                  <div class="stat-value">{{ kpi.value }}</div>
                </div>
                <div class="stat-icon" :class="kpi.iconClass">
                  <component :is="kpi.icon" />
                </div>
              </div>
              <div class="stat-footer">
                <span v-if="!stats.dataInsufficient" :class="['trend-tag', kpi.trend >= 0 ? 'up' : 'down']">
                  <component :is="kpi.trend >= 0 ? ArrowUpOutlined : ArrowDownOutlined" />
                  {{ Math.abs(kpi.trend).toFixed(1) }}%
                </span>
                <span v-else class="trend-tag neutral">--</span>
                <span class="trend-desc">较昨日</span>
              </div>
            </div>
          </a-card>
        </a-col>
      </a-row>

      <!-- 趋势图 -->
      <a-card :bordered="false" class="section-card section-row" title="红人运营趋势">
        <template #extra>
          <a-range-picker
            v-model:value="selectedDateRange"
            size="middle"
            :ranges="ranges"
            :allow-clear="false"
            @change="handleDateRangeChange"
          />
        </template>
        <div class="chart-wrap">
          <div ref="chartContainer" class="chart-canvas"></div>
          <div v-if="chartIsEmpty" class="chart-empty-mask">所选时间段暂无业绩数据</div>
        </div>
      </a-card>

      <!-- 排行榜与漏斗 -->
      <a-row :gutter="[16, 16]" type="flex" align="stretch" class="section-row bottom-row" style="margin-bottom: 16px;">
        <a-col :xs="24" :lg="12" class="panel-col">
          <a-card :bordered="false" class="section-card panel-card" title="近30日 GMV 贡献榜">
            <div class="panel-body">
              <div v-if="gmvTop10.length === 0" class="panel-empty">
                <a-empty :image-style="{ height: '64px' }" description="近30日暂无 GMV 数据" />
              </div>
              <div v-else class="rank-list scrollable">
                <div v-for="(item, index) in gmvTop10" :key="item.id ?? index" class="rank-item">
                  <span class="rank-no" :class="{ top: index < 3 }">{{ index + 1 }}</span>
                  <a-avatar class="rank-avatar" :src="item.avatar" :size="36" :style="{ backgroundColor: getAvatarColor(item.name) }">
                    {{ item.name ? item.name.charAt(0).toUpperCase() : '?' }}
                  </a-avatar>
                  <div class="rank-meta">
                    <div class="rank-info">
                      <span class="rank-name" :title="item.name">{{ item.name }}</span>
                      <span class="rank-amount">{{ formatCurrency(item.gmv) }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </a-card>
        </a-col>

        <a-col :xs="24" :lg="12" class="panel-col">
          <a-card :bordered="false" class="section-card panel-card" title="红人阶段分布">
            <div class="panel-body">
              <div v-if="pieChartData.length === 0" class="panel-empty">
                <a-empty :image-style="{ height: '64px' }" description="暂无红人阶段数据" />
              </div>
              <div v-else class="stage-list scrollable">
                <div v-for="(item, index) in pieChartData" :key="item.label" class="rank-item">
                  <span class="rank-no" :class="{ top: index < 3 }">{{ index + 1 }}</span>
                  <div class="rank-meta" style="flex: 1;">
                    <div class="rank-info">
                      <span class="rank-name" :title="item.label">{{ item.label }}</span>
                      <span class="rank-amount">{{ item.value }} 人 · {{ stagePercent(item.value) }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </a-card>
        </a-col>
      </a-row>
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted, nextTick, watch } from 'vue';
import * as echarts from 'echarts';
import axios from 'axios';
import dayjs, { Dayjs } from 'dayjs';
import {
  UserOutlined,
  TeamOutlined,
  VideoCameraOutlined,
  SkinOutlined,
  ArrowUpOutlined,
  ArrowDownOutlined,
} from '@ant-design/icons-vue';

const avatarColors = ['#f59e0b', '#10b981', '#3b82f6', '#8b5cf6', '#ec4899', '#f43f5e'];
const getAvatarColor = (name: string) => {
  if (!name) return '#94a3b8';
  let hash = 0;
  for (let i = 0; i < name.length; i++) hash = name.charCodeAt(i) + ((hash << 5) - hash);
  return avatarColors[Math.abs(hash) % avatarColors.length];
};

// Interfaces
interface DashboardStats {
  activeInfluencers: number;
  conversionOrders: number;
  orderGMV: number;
  commissionAmount: number;
  activeInfluencersTrend: number;
  conversionOrdersTrend: number;
  orderGMVTrend: number;
  commissionTrend: number;
  dataInsufficient?: boolean;
}

interface DashboardTrend {
  dates: string[];
  orderCounts: number[];
  gmvData: number[];
  commissionData: number[];
}

interface TopGMVItem {
  id: number;
  name: string;
  avatar: string;
  gmv: number;
}

interface TopOrderItem {
  id: number;
  name: string;
  avatar: string;
  orderCount: number;
}

interface StageDistributionItem {
  stage: string;
  count: number;
}

// Format Currency
const formatCurrency = (value: number | undefined) => {
  if (value === undefined || value === null) return 'US$0.00';
  return `US$${value.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
};

// Date Range Picker
const selectedDateRange = ref<[Dayjs, Dayjs]>([dayjs().subtract(30, 'day'), dayjs()]);
const ranges = {
  '最近7天': [dayjs().subtract(7, 'days'), dayjs()] as [Dayjs, Dayjs],
  '最近30天': [dayjs().subtract(30, 'days'), dayjs()] as [Dayjs, Dayjs],
  '本月': [dayjs().startOf('month'), dayjs().endOf('month')] as [Dayjs, Dayjs],
};

const handleDateRangeChange = async (dates: any) => {
  if (dates && dates.length === 2) {
    const days = dates[1].diff(dates[0], 'day');
    await fetchTrendData(days, dates[0].format('YYYY-MM-DD'), dates[1].format('YYYY-MM-DD'));
  }
};

// State
const stats = reactive<DashboardStats>({
  activeInfluencers: 0,
  conversionOrders: 0,
  orderGMV: 0,
  commissionAmount: 0,
  activeInfluencersTrend: 0,
  conversionOrdersTrend: 0,
  orderGMVTrend: 0,
  commissionTrend: 0,
  dataInsufficient: false,
});

const loading = ref(false);
const initialLoading = ref(true);

const trendData = ref<DashboardTrend>({
  dates: [],
  orderCounts: [],
  gmvData: [],
  commissionData: []
});

const gmvTop10 = ref<TopGMVItem[]>([]);
const stageTotal = ref(0);
const pieChartData = ref<{label: string, value: number, color: string}[]>([]);

const cooperatingCount = computed(() =>
  pieChartData.value.find((item) => item.label.includes('合作'))?.value ?? 0
);
const poolCount = computed(() =>
  pieChartData.value
    .filter((item) => item.label.includes('资源池'))
    .reduce((sum, item) => sum + item.value, 0)
);

const formatShortTime = (raw?: string) => {
  if (!raw) return '-';
  const d = dayjs(raw);
  if (!d.isValid()) return '-';
  return d.isSame(dayjs(), 'year') ? d.format('MM-DD HH:mm') : d.format('YYYY-MM-DD');
};

const chartIsEmpty = computed(() => {
  const gmv = trendData.value.gmvData || [];
  const orders = trendData.value.orderCounts || [];
  const commission = trendData.value.commissionData || [];
  const hasGmv = gmv.some((v) => Number(v) > 0);
  const hasOrders = orders.some((v) => Number(v) > 0);
  const hasCommission = commission.some((v) => Number(v) > 0);
  return !hasGmv && !hasOrders && !hasCommission;
});

const kpiCards = computed(() => [
  {
    key: 'influencers',
    label: '系统活跃红人',
    value: `${stats.activeInfluencers} 位`,
    trend: stats.activeInfluencersTrend,
    icon: UserOutlined,
    iconClass: 'icon-cyan',
  },
  {
    key: 'total',
    label: '红人档案总数',
    value: `${stageTotal.value} 位`,
    trend: 0,
    icon: TeamOutlined,
    iconClass: 'icon-blue',
  },
  {
    key: 'cooperating',
    label: '合作中红人',
    value: `${cooperatingCount.value} 位`,
    trend: 0,
    icon: VideoCameraOutlined,
    iconClass: 'icon-pink',
  },
  {
    key: 'pool',
    label: '资源池红人',
    value: `${poolCount.value} 位`,
    trend: 0,
    icon: SkinOutlined,
    iconClass: 'icon-purple',
  },
]);

const formatStageTotal = (total: number) => {
  const n = Number(total);
  return Number.isFinite(n) ? n.toLocaleString() : '0';
};

const stagePercent = (value: number) => {
  const total = Number(stageTotal.value) || 0;
  const v = Number(value) || 0;
  if (total <= 0) return 0;
  const pct = Math.round((v / total) * 100);
  return v > 0 && pct === 0 ? 1 : pct;
};

const stageColors: Record<string, string> = {
  '资源池-待筛选': '#f59e0b',
  '已联系': '#ec4899',
  '沟通中': '#f43f5e',
  '合作中': '#8b5cf6',
  '资源池-暂不合适': '#9ca3af',
  '待联系': '#ef4444',
  '暂不合作': '#fde047',
  '休眠中': '#34d399',
  '不再合作': '#64748b'
};

// API Methods
const fetchDashboardStats = async () => {
  loading.value = true;
  try {
    const response = await axios.get<DashboardStats>('/influencer-api/v1/dashboard/stats');
    const data = response.data;
    if (data) {
      Object.assign(stats, data);
    }
  } catch (error) {
    console.error('获取仪表盘数据失败:', error);
  } finally {
    loading.value = false;
  }
};

const fetchTrendData = async (days: number = 30, startDate?: string, endDate?: string) => {
  try {
    const params: any = { days };
    if (startDate && endDate) {
      params.startDate = startDate;
      params.endDate = endDate;
    }
    const response = await axios.get<DashboardTrend>('/influencer-api/v1/dashboard/trend', { params });
    const data = response.data;
    if (data) {
      trendData.value = {
        dates: data.dates || [],
        orderCounts: data.orderCounts || [],
        gmvData: data.gmvData || [],
        commissionData: data.commissionData || []
      };
      if (lineChartInstance) {
        updateLineChart();
      }
    }
  } catch (error) {
    console.error('获取趋势数据失败:', error);
  }
};

const fetchTopGMV = async () => {
  try {
    const response = await axios.get<TopGMVItem[]>('/influencer-api/v1/dashboard/top-gmv?limit=10');
    if (response.data && Array.isArray(response.data)) {
      gmvTop10.value = response.data;
    }
  } catch (error) {
    console.error('获取GMV排行失败:', error);
  }
};

const fetchStageDistribution = async () => {
  try {
    const response = await axios.get<StageDistributionItem[]>('/influencer-api/v1/dashboard/stage-distribution');
    if (response.data && Array.isArray(response.data)) {
      const items = response.data.map((item) => ({
        stage: String(item.stage ?? ''),
        count: Number(item.count) || 0,
      }));
      stageTotal.value = items.reduce((sum, item) => sum + item.count, 0);
      pieChartData.value = items
        .filter((item) => item.count > 0)
        .sort((a, b) => b.count - a.count)
        .map((item) => ({
          label: item.stage,
          value: item.count,
          color: stageColors[item.stage] || '#cbd5e1',
        }));
    } else {
      stageTotal.value = 0;
      pieChartData.value = [];
    }
  } catch (error) {
    console.error('获取阶段分布失败:', error);
  }
};

// Chart Logic
const chartContainer = ref<HTMLElement | null>(null);
let lineChartInstance: ReturnType<typeof echarts.init> | null = null;

const initLineChart = () => {
  if (!chartContainer.value) return;
  const days = trendData.value.dates.length > 0 ? trendData.value.dates : Array.from({length: 30}, (_, i) => dayjs().subtract(29 - i, 'day').format('MM/DD'));
  const orderData = trendData.value.orderCounts.length > 0 ? trendData.value.orderCounts : Array(30).fill(0);
  const gmvData = trendData.value.gmvData.length > 0 ? trendData.value.gmvData : Array(30).fill(0);
  const commissionData = trendData.value.commissionData.length > 0 ? trendData.value.commissionData : Array(30).fill(0);

  lineChartInstance = echarts.init(chartContainer.value);

  const maxMoney = Math.max(...gmvData, ...commissionData, 0);
  const maxOrders = Math.max(...orderData, 0);
  const isEmptyChart = maxMoney <= 0 && maxOrders <= 0;

  const option: any = {
    tooltip: {
      trigger: 'axis',
      backgroundColor: '#ffffff',
      padding: [8, 16],
      borderWidth: 0,
      textStyle: { color: '#1d1d1f', fontSize: 13, fontWeight: 'bold' },
      extraCssText: 'box-shadow: 0 8px 24px rgba(139, 92, 246, 0.2); border-radius: 12px;',
      axisPointer: { type: 'line', lineStyle: { color: 'rgba(139, 92, 246, 0.3)', width: 1, type: 'dashed' } },
      formatter: (params: any) => {
        let result = `<div style="font-weight: 600; font-size: 13px; color: #64748b; margin-bottom: 8px;">${params[0].axisValue}</div>`;
        params.forEach((item: any) => {
          let value;
          if (item.seriesName.includes('GMV') || item.seriesName.includes('分佣')) {
            value = `$${item.value.toLocaleString('en-US', { minimumFractionDigits: 2 })}`;
          } else {
            value = `${item.value} 单`;
          }
          
          const color = item.color.colorStops ? item.color.colorStops[0].color : item.color;
          const marker = `<span style="display:inline-block;margin-right:4px;border-radius:2px;width:8px;height:8px;background-color:${color};"></span>`;
          
          result += `
            <div style="display: flex; align-items: center; justify-content: space-between; margin: 4px 0; gap: 16px;">
              <span style="color: #475569; font-size: 13px; display: flex; align-items: center;">${marker} ${item.seriesName}</span>
              <span style="font-weight: 700; color: #1e293b; font-size: 14px;">${value}</span>
            </div>
          `;
        });
        return result;
      }
    },
    grid: { left: 10, right: 10, bottom: 0, top: 20, containLabel: true },
    legend: {
      show: true, icon: 'circle', itemWidth: 8, itemHeight: 8, itemGap: 24,
      textStyle: { fontWeight: 600, color: '#64748b', fontSize: 12 },
      top: 0, right: 0 
    },
    xAxis: {
      type: 'category', boundaryGap: true, data: days,
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#94a3b8', fontSize: 11, margin: 16, fontWeight: 500, interval: 'auto' }
    },
    yAxis: [
      {
        type: 'value', position: 'left', min: 0,
        max: isEmptyChart ? 100 : undefined,
        splitNumber: isEmptyChart ? 4 : undefined,
        splitLine: { show: true, lineStyle: { type: 'dashed', color: '#f1f5f9', width: 1 } },
        axisLabel: {
          color: '#94a3b8', fontSize: 11, fontWeight: 500, margin: 16,
          formatter: (val: number) => {
            if (isEmptyChart && val <= 0) return '$0';
            return val >= 1000 ? `$${(val / 1000).toFixed(0)}k` : `$${val}`;
          },
        },
        axisLine: { show: false }, axisTick: { show: false },
      },
      {
        type: 'value', position: 'right', min: 0,
        max: isEmptyChart ? 10 : undefined,
        splitLine: { show: false }, 
        axisLabel: { 
          show: true, color: '#94a3b8', fontSize: 11, fontWeight: 500, margin: 16,
          formatter: '{value} 单'
        }, 
        axisLine: { show: false }, axisTick: { show: false },
      },
    ],
    series: [
      {
        name: 'GMV', type: 'line', yAxisIndex: 0, smooth: 0.5, showSymbol: false, symbol: 'circle', symbolSize: 6,
        lineStyle: { width: 4, color: '#8b5cf6', shadowColor: 'rgba(139, 92, 246, 0.5)', shadowBlur: 15, shadowOffsetY: 6 }, 
        itemStyle: { color: '#8b5cf6', borderColor: '#fff', borderWidth: 2 },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(139, 92, 246, 0.2)' },
            { offset: 1, color: 'rgba(139, 92, 246, 0.01)' }
          ])
        },
        data: gmvData, z: 3
      },
      {
        name: '预估分佣', type: 'line', yAxisIndex: 0, smooth: 0.5, showSymbol: false, symbol: 'circle', symbolSize: 6,
        lineStyle: { width: 4, color: '#f43f5e', shadowColor: 'rgba(244, 63, 94, 0.5)', shadowBlur: 15, shadowOffsetY: 6 }, 
        itemStyle: { color: '#f43f5e', borderColor: '#fff', borderWidth: 2 },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(244, 63, 94, 0.15)' },
            { offset: 1, color: 'rgba(244, 63, 94, 0.01)' }
          ])
        },
        data: commissionData, z: 3
      },
      {
        name: '当日订单量', type: 'line', yAxisIndex: 1, smooth: 0.5, showSymbol: false, symbol: 'circle', symbolSize: 6,
        lineStyle: { width: 4, color: '#3b82f6', shadowColor: 'rgba(59, 130, 246, 0.5)', shadowBlur: 15, shadowOffsetY: 6 }, 
        itemStyle: { color: '#3b82f6', borderColor: '#fff', borderWidth: 2 },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(59, 130, 246, 0.15)' },
            { offset: 1, color: 'rgba(59, 130, 246, 0.01)' }
          ])
        },
        data: orderData, z: 2
      }
    ],
    animationDuration: 1500,
    animationEasing: 'cubicOut'
  };
  
  lineChartInstance.setOption(option);
};

const handleResize = () => lineChartInstance?.resize();

const updateLineChart = () => {
  if (!lineChartInstance) return;
  const gmvData = trendData.value.gmvData || [];
  const commissionData = trendData.value.commissionData || [];
  const orderData = trendData.value.orderCounts || [];
  const maxMoney = Math.max(...gmvData, ...commissionData, 0);
  const maxOrders = Math.max(...orderData, 0);
  const isEmptyChart = maxMoney <= 0 && maxOrders <= 0;

  lineChartInstance.setOption({
    xAxis: { data: trendData.value.dates },
    yAxis: [
      { max: isEmptyChart ? 100 : undefined, splitNumber: isEmptyChart ? 4 : undefined },
      { max: isEmptyChart ? 10 : undefined },
    ],
    series: [
      { name: 'GMV', data: gmvData },
      { name: '预估分佣', data: commissionData },
      { name: '当日订单量', data: orderData }
    ],
  });
};

// --- 平台分布假数据 (Platform Distribution Mock Data) ---
const platformData = ref([
  { platform: 'TikTok', count: 450, color: '#000000' },
  { platform: 'Instagram', count: 320, color: '#E1306C' },
  { platform: 'YouTube', count: 280, color: '#FF0000' },
  { platform: 'Twitter (X)', count: 150, color: '#1DA1F2' },
  { platform: 'Facebook', count: 90, color: '#1877F2' }
]);

const platformChartContainer = ref<HTMLElement | null>(null);
let platformChartInstance: ReturnType<typeof echarts.init> | null = null;

const initPlatformChart = () => {
  if (!platformChartContainer.value) return;
  
  if (!platformChartInstance) {
    platformChartInstance = echarts.init(platformChartContainer.value);
  }

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      backgroundColor: '#ffffff',
      padding: [8, 16],
      borderWidth: 0,
      extraCssText: 'box-shadow: 0 8px 24px rgba(0,0,0,0.1); border-radius: 12px;',
      formatter: (params: any[]) => {
        const item = params[0];
        return `
          <div style="font-weight: 600; font-size: 14px; color: #1e293b; margin-bottom: 4px;">${item.name}</div>
          <div style="display: flex; align-items: center; justify-content: space-between; gap: 16px;">
            <span style="color: #64748b; font-size: 13px;">合作红人</span>
            <span style="font-weight: 700; color: ${item.color}; font-size: 15px;">${item.value} 人</span>
          </div>
        `;
      }
    },
    grid: {
      left: '3%',
      right: '6%',
      bottom: '3%',
      top: '5%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      splitLine: { lineStyle: { type: 'dashed', color: '#f1f5f9' } },
      axisLabel: { color: '#94a3b8' }
    },
    yAxis: {
      type: 'category',
      data: platformData.value.map(d => d.platform).reverse(),
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#475569', fontWeight: 500, fontSize: 13, margin: 12 }
    },
    series: [
      {
        name: '合作红人',
        type: 'bar',
        barWidth: 16,
        itemStyle: {
          borderRadius: [0, 8, 8, 0],
          color: (params: any) => {
            const data = [...platformData.value].reverse();
            return data[params.dataIndex]?.color || '#3b82f6';
          }
        },
        label: {
          show: true,
          position: 'right',
          color: '#1e293b',
          fontWeight: 600,
          formatter: '{c}'
        },
        data: platformData.value.map(d => d.count).reverse()
      }
    ]
  };
  platformChartInstance.setOption(option);
};


onMounted(async () => {
  await Promise.all([
    fetchDashboardStats(),
    fetchTrendData(30),
    fetchTopGMV(),
    fetchStageDistribution(),
  ]);
  initialLoading.value = false;
  nextTick(() => {
    initLineChart();
    initPlatformChart();
  });
  window.addEventListener('resize', () => {
    handleResize();
    platformChartInstance?.resize();
  });
});

const refreshDashboard = async () => {
  await Promise.all([
    fetchDashboardStats(),
    fetchTrendData(30),
    fetchTopGMV(),
    fetchStageDistribution(),
  ]);
  updateLineChart();
};

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
  if (lineChartInstance) lineChartInstance.dispose();
});
</script>

<style lang="scss" scoped>
.dashboard-page {
  padding: 8px;
  min-height: 100%;
}

.section-row {
  margin-bottom: 8px;
}

.bottom-row {
  margin-bottom: 0;
}

.panel-col {
  display: flex;
}

/* SaaS style cards */
.stat-card,
.section-card {
  border-radius: 12px;
  background: #ffffff;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid #e5e7eb;
}

.panel-card {
  flex: 1;
  width: 100%;
  display: flex;
  flex-direction: column;
}

.stat-card-inner {
  display: flex;
  flex-direction: column;
  padding: 4px;
}

.stat-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.stat-title {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.stat-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  flex-shrink: 0;

  &.icon-purple { background: #f3e8ff; color: #7e22ce; }
  &.icon-pink { background: #ffe4e6; color: #e11d48; }
  &.icon-blue { background: #e0f2fe; color: #0284c7; }
  &.icon-cyan { background: #ccfbf1; color: #0d9488; }
}

.stat-label {
  font-size: 14px;
  font-weight: 500;
  color: #64748b;
}

.stat-value {
  font-size: 26px;
  font-weight: 700;
  color: #0f172a;
  line-height: 1.1;
  word-break: break-all;
  letter-spacing: -0.01em;
}

.stat-footer {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 16px;
  padding-top: 12px;
  border-top: 1px dashed #e2e8f0;
}

.trend-desc {
  font-size: 12px;
  color: #94a3b8;
}

.trend-tag {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  padding: 3px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;

  &.up { background: rgba(52, 199, 89, 0.1); color: #34c759; }
  &.down { background: rgba(255, 59, 48, 0.1); color: #ff3b30; }
  &.neutral { background: rgba(142, 142, 147, 0.1); color: #8e8e93; }
}

.chart-wrap {
  position: relative;
}

.chart-canvas {
  width: 100%;
  height: 340px;
}

.chart-empty-mask {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  color: #86868b;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(4px);
  pointer-events: none;
  border-radius: 8px;
}

.panel-body {
  min-height: 280px;
  display: flex;
  flex-direction: column;
}

.panel-empty {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 240px;
}

.header-badge {
  font-size: 13px;
  font-weight: 500;
  color: #1d1d1f;
  background: rgba(0, 0, 0, 0.04);
  padding: 4px 12px;
  border-radius: 12px;
}

.header-link {
  font-size: 13px;
  font-weight: 500;
  color: #0066cc;
  text-decoration: none;
  transition: color 0.2s;

  &:hover {
    color: #004499;
  }
}

.recent-row {
  margin-bottom: 0;
}

.feed-list {
  display: flex;
  flex-direction: column;
  gap: 12px;

  &.scrollable {
    max-height: 340px;
    overflow-y: auto;
    padding-right: 4px;

    &::-webkit-scrollbar {
      width: 4px;
    }
    &::-webkit-scrollbar-thumb {
      background: rgba(0, 0, 0, 0.15);
      border-radius: 4px;
    }
  }
}

.feed-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px;
  border-radius: 12px;
  background: #fff;
  border: 1px solid #f1f5f9;
  transition: all 0.2s ease;
  box-shadow: 0 1px 3px rgba(0,0,0,0.02);

  &:hover {
    box-shadow: 0 4px 12px rgba(0,0,0,0.05);
    border-color: #e2e8f0;
    transform: translateY(-1px);
  }
}

.feed-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;

  &.bg-green {
    background: #dcfce7;
    color: #22c55e;
  }
  &.bg-blue {
    background: #e0f2fe;
    color: #0ea5e9;
  }
}

.feed-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.feed-title {
  font-size: 13px;
  color: #475569;
  line-height: 1.4;
  
  strong {
    color: #1e293b;
    font-weight: 600;
  }
  span {
    color: #3b82f6;
    font-weight: 500;
  }
}

.feed-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.feed-time {
  font-size: 12px;
  color: #94a3b8;
}

.feed-tag {
  font-size: 11px;
  font-weight: 500;
  padding: 2px 8px;
  border-radius: 12px;
  background: #f1f5f9;
  color: #64748b;

  &.blue {
    background: #e0f2fe;
    color: #0ea5e9;
  }
}

.feed-amount {
  font-size: 15px;
  font-weight: 700;
  color: #0f172a;
  flex-shrink: 0;
  text-align: right;

  &.positive {
    color: #10b981;
  }
}

.rank-list,
.stage-list {
  display: flex;
  flex-direction: column;
  gap: 12px;

  &.scrollable {
    max-height: 280px;
    overflow-y: auto;
    padding-right: 4px;

    &::-webkit-scrollbar {
      width: 4px;
    }
    &::-webkit-scrollbar-thumb {
      background: rgba(0, 0, 0, 0.15);
      border-radius: 4px;
    }
  }
}

.rank-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 12px;
  border-radius: 8px;
  transition: all 0.2s ease;

  &:hover {
    background: #f8fafc;
  }
}

.rank-no {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  background: #f1f5f9;
  color: #64748b;
  font-size: 14px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: inset 0 0 0 1px rgba(0,0,0,0.02);

  &.top {
    background: linear-gradient(135deg, #8b5cf6 0%, #6366f1 100%);
    color: #ffffff;
    box-shadow: 0 4px 10px rgba(99, 102, 241, 0.3);
  }
}

.rank-avatar {
  flex-shrink: 0;
  font-weight: 600;
  font-size: 14px;
  color: #fff;
  box-shadow: 0 2px 6px rgba(0,0,0,0.06);
}

.rank-meta {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;

  .rank-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .rank-name {
    font-size: 13px;
    font-weight: 500;
    color: #1e293b;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .rank-amount {
    font-size: 14px;
    font-weight: 600;
    color: #0f172a;
    flex-shrink: 0;
  }
  
  :deep(.ant-progress-bg) {
    background-color: #0066cc !important;
  }
}

.platform-chart-wrap {
  width: 100%;
  height: 240px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.platform-chart-canvas {
  width: 100%;
  height: 100%;
}

:deep(.section-card) {
  .ant-card-head {
    min-height: 48px;
    border-bottom: 1px solid rgba(0, 0, 0, 0.06);
    flex-shrink: 0;
  }

  .ant-card-head-title {
    font-size: 15px;
    font-weight: 600;
    color: #1d1d1f;
  }

  .ant-card-body {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding: 20px;
  }
}

:deep(.stat-card .ant-card-body) {
  padding: 20px;
}

:deep(.panel-empty .ant-empty-description) {
  color: #86868b;
  font-size: 13px;
}
</style>

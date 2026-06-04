const fs = require('fs');

const filePath = 'C:\\\\Users\\\\Administrator\\\\Desktop\\\\红人服务系统源码\\\\frontend\\\\src\\\\views\\\\dashboard\\\\DashboardView.vue';
let content = fs.readFileSync(filePath, 'utf8');

const scriptStart = content.indexOf('<script setup lang="ts">');
const scriptEnd = content.indexOf('</script>', scriptStart) + 9;
let scriptContent = content.substring(scriptStart, scriptEnd);

// Add missing icon imports
scriptContent = scriptContent.replace(
  /import \{([\s\S]*?)\} from '@ant-design\/icons-vue';/,
  import {\  RocketOutlined, BarChartOutlined, BulbOutlined, SyncOutlined, RiseOutlined, FieldTimeOutlined, TrophyOutlined\n} from '@ant-design/icons-vue';
);

const templateContent = 
<template>
  <div class="dashboard-workspace ultra-premium-dashboard">
    <template v-if="initialLoading">
      <a-row :gutter="[24, 24]">
        <a-col :span="24">
          <a-skeleton active :paragraph="{ rows: 8 }" />
        </a-col>
      </a-row>
    </template>

    <template v-else>
      <!-- Top Command Header -->
      <div class="command-header mb-24">
        <div class="header-bg-decoration"></div>
        <div class="header-content">
          <div class="user-greeting">
            <h1 class="greeting-title">
              <span class="gradient-text">早上好，Admin</span> 👋
            </h1>
            <p class="greeting-subtitle">您的 AI 红人管家已就绪。今天系统为您捕捉到了 <strong>3</strong> 个高价值业务机会。</p>
          </div>
          <div class="header-actions">
            <a-button type="primary" class="action-btn glass-btn" size="large">
              <template #icon><component :is="RocketOutlined" /></template>
              启动智能开发
            </a-button>
            <a-button class="action-btn secondary-btn" size="large">
              <template #icon><component :is="BarChartOutlined" /></template>
              生成数据简报
            </a-button>
          </div>
        </div>
      </div>

      <!-- KPI Matrix -->
      <div class="kpi-matrix mb-24">
        <a-row :gutter="[24, 24]">
          <a-col :xs="24" :sm="12" :xl="6">
            <div class="kpi-card">
              <div class="kpi-icon-wrap purple-glow">
                <component :is="DollarOutlined" class="kpi-icon" />
              </div>
              <div class="kpi-info">
                <span class="kpi-label">订单总 GMV</span>
                <span class="kpi-value">{{ formatCurrency(stats.orderGMV) }}</span>
              </div>
              <div class="kpi-trend">
                <span v-if="!stats.dataInsufficient" :class="['trend-pill', stats.orderGMVTrend >= 0 ? 'up' : 'down']">
                  <component :is="stats.orderGMVTrend >= 0 ? ArrowUpOutlined : ArrowDownOutlined" />
                  {{ Math.abs(stats.orderGMVTrend).toFixed(1) }}%
                </span>
                <span v-else class="trend-pill neutral">--</span>
              </div>
            </div>
          </a-col>
          
          <a-col :xs="24" :sm="12" :xl="6">
            <div class="kpi-card">
              <div class="kpi-icon-wrap pink-glow">
                <component :is="PayCircleOutlined" class="kpi-icon" />
              </div>
              <div class="kpi-info">
                <span class="kpi-label">预估总分佣</span>
                <span class="kpi-value">{{ formatCurrency(stats.commissionAmount) }}</span>
              </div>
              <div class="kpi-trend">
                <span v-if="!stats.dataInsufficient" :class="['trend-pill', stats.commissionTrend >= 0 ? 'up' : 'down']">
                  <component :is="stats.commissionTrend >= 0 ? ArrowUpOutlined : ArrowDownOutlined" />
                  {{ Math.abs(stats.commissionTrend).toFixed(1) }}%
                </span>
                <span v-else class="trend-pill neutral">--</span>
              </div>
            </div>
          </a-col>

          <a-col :xs="24" :sm="12" :xl="6">
            <div class="kpi-card">
              <div class="kpi-icon-wrap blue-glow">
                <component :is="ShoppingOutlined" class="kpi-icon" />
              </div>
              <div class="kpi-info">
                <span class="kpi-label">累计转化订单</span>
                <span class="kpi-value">{{ stats.conversionOrders }} <span class="kpi-unit">单</span></span>
              </div>
              <div class="kpi-trend">
                <span v-if="!stats.dataInsufficient" :class="['trend-pill', stats.conversionOrdersTrend >= 0 ? 'up' : 'down']">
                  <component :is="stats.conversionOrdersTrend >= 0 ? ArrowUpOutlined : ArrowDownOutlined" />
                  {{ Math.abs(stats.conversionOrdersTrend).toFixed(1) }}%
                </span>
                <span v-else class="trend-pill neutral">--</span>
              </div>
            </div>
          </a-col>

          <a-col :xs="24" :sm="12" :xl="6">
            <div class="kpi-card">
              <div class="kpi-icon-wrap cyan-glow">
                <component :is="UserOutlined" class="kpi-icon" />
              </div>
              <div class="kpi-info">
                <span class="kpi-label">系统活跃红人</span>
                <span class="kpi-value">{{ stats.activeInfluencers }} <span class="kpi-unit">位</span></span>
              </div>
              <div class="kpi-trend">
                <span v-if="!stats.dataInsufficient" :class="['trend-pill', stats.activeInfluencersTrend >= 0 ? 'up' : 'down']">
                  <component :is="stats.activeInfluencersTrend >= 0 ? ArrowUpOutlined : ArrowDownOutlined" />
                  {{ Math.abs(stats.activeInfluencersTrend).toFixed(1) }}%
                </span>
                <span v-else class="trend-pill neutral">--</span>
              </div>
            </div>
          </a-col>
        </a-row>
      </div>

      <!-- Main Arena (Chart & AI Insights) -->
      <a-row :gutter="[24, 24]" class="mb-24">
        <!-- Main Chart -->
        <a-col :xs="24" :xl="16">
          <div class="ultra-card h-full chart-arena">
            <div class="card-header">
              <div class="header-title-group">
                <h3 class="card-title">业绩增长全景图</h3>
                <span class="card-subtitle">AI 实时监控您的业绩波动</span>
              </div>
              <div class="header-actions">
                <a-range-picker 
                  v-model:value="selectedDateRange"
                  class="glass-date-picker" 
                  size="middle" 
                  :ranges="ranges"
                  :allowClear="false"
                  @change="handleDateRangeChange"
                />
              </div>
            </div>
            <div class="card-body">
              <div ref="chartContainer" class="chart-canvas"></div>
            </div>
          </div>
        </a-col>

        <!-- AI Insights Module -->
        <a-col :xs="24" :xl="8">
          <div class="ultra-card h-full ai-insights-arena">
            <div class="card-header border-bottom">
              <div class="header-title-group">
                <h3 class="card-title ai-gradient-title">
                  <component :is="BulbOutlined" class="ai-icon" /> AI 智能业务洞察
                </h3>
              </div>
              <a-button type="link" class="refresh-btn">
                <component :is="SyncOutlined" />
              </a-button>
            </div>
            <div class="card-body">
              <div class="insights-list">
                <div class="insight-item critical">
                  <div class="insight-icon"><component :is="RiseOutlined" /></div>
                  <div class="insight-content">
                    <p class="insight-text">发现 <strong>3 名</strong> 美妆区高质量潜力红人，预计转化率可达 12%。</p>
                    <a-button size="small" type="primary" class="insight-action-btn glass">一键开发</a-button>
                  </div>
                </div>
                <div class="insight-item warning">
                  <div class="insight-icon"><component :is="FieldTimeOutlined" /></div>
                  <div class="insight-content">
                    <p class="insight-text">红人 <strong>@fashion_nova</strong> 的样品已签收 3 天，但尚未产出任何内容。</p>
                    <a-button size="small" class="insight-action-btn outline">智能催更</a-button>
                  </div>
                </div>
                <div class="insight-item success">
                  <div class="insight-icon"><component :is="TrophyOutlined" /></div>
                  <div class="insight-content">
                    <p class="insight-text">本周 GMV 环比暴增 <strong>301%</strong>，主要由 TikTok 渠道的短视频带货贡献。</p>
                    <a-button size="small" class="insight-action-btn outline">查看详情</a-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </a-col>
      </a-row>

      <!-- Leaderboards & Funnel -->
      <a-row :gutter="[24, 24]" class="pb-24">
        <!-- GMV Leaderboard -->
        <a-col :xs="24" :lg="8">
          <div class="ultra-card h-full list-arena">
            <div class="card-header border-bottom">
              <h3 class="card-title">👑 近30日 GMV 贡献榜</h3>
            </div>
            <div class="card-body rank-body">
              <div v-if="gmvTop10.length === 0" class="empty-state">
                <a-empty description="暂无数据" />
              </div>
              <div v-else class="modern-rank-list">
                <div v-for="(item, index) in gmvTop10" :key="index" class="rank-row">
                  <div class="rank-avatar" :class="index < 3 ? 'top-' + (index + 1) : ''">
                    <span class="rank-num">{{ index + 1 }}</span>
                  </div>
                  <div class="rank-info">
                    <span class="rank-name">{{ item.name }}</span>
                    <div class="rank-progress-bg">
                      <div class="rank-progress-fill purple-gradient" :style="{ width: (gmvTop10[0] && gmvTop10[0].gmv > 0 ? item.gmv / gmvTop10[0].gmv * 100 : 0) + '%' }"></div>
                    </div>
                  </div>
                  <div class="rank-value highlight">{{ formatCurrency(item.gmv) }}</div>
                </div>
              </div>
            </div>
          </div>
        </a-col>

        <!-- Orders Leaderboard -->
        <a-col :xs="24" :lg="8">
          <div class="ultra-card h-full list-arena">
            <div class="card-header border-bottom">
              <h3 class="card-title">🔥 近30日 带货单量榜</h3>
            </div>
            <div class="card-body rank-body">
              <div v-if="orderTop10.length === 0" class="empty-state">
                <a-empty description="暂无数据" />
              </div>
              <div v-else class="modern-rank-list">
                <div v-for="(item, index) in orderTop10" :key="index" class="rank-row">
                  <div class="rank-avatar alt" :class="index < 3 ? 'top-' + (index + 1) : ''">
                    <span class="rank-num">{{ index + 1 }}</span>
                  </div>
                  <div class="rank-info">
                    <span class="rank-name">{{ item.name }}</span>
                    <div class="rank-progress-bg">
                      <div class="rank-progress-fill blue-gradient" :style="{ width: (orderTop10[0] && orderTop10[0].orderCount > 0 ? item.orderCount / orderTop10[0].orderCount * 100 : 0) + '%' }"></div>
                    </div>
                  </div>
                  <div class="rank-value">{{ item.orderCount }} <span class="unit">单</span></div>
                </div>
              </div>
            </div>
          </div>
        </a-col>

        <!-- Stage Distribution -->
        <a-col :xs="24" :lg="8">
          <div class="ultra-card h-full funnel-arena">
            <div class="card-header border-bottom">
              <h3 class="card-title">🎯 红人开发漏斗</h3>
            </div>
            <div class="card-body funnel-body">
              <div class="funnel-total-box">
                <span class="funnel-total-num">{{ stageTotal }}</span>
                <span class="funnel-total-label">全网建联红人</span>
              </div>
              <div class="funnel-list">
                <div v-for="item in pieChartData" :key="item.label" class="funnel-row">
                  <div class="funnel-label">
                    <span class="dot" :style="{ background: item.color }"></span>
                    <span class="name">{{ item.label }}</span>
                  </div>
                  <div class="funnel-bar-area">
                    <div class="funnel-bar" :style="{ width: (stageTotal > 0 ? item.value / stageTotal * 100 : 0) + '%', background: \linear-gradient(90deg, \, \dd)\ }"></div>
                  </div>
                  <div class="funnel-stats">
                    <span class="count">{{ item.value }}</span>
                    <span class="percent">{{ stageTotal > 0 ? (item.value > 0 && Math.round(item.value / stageTotal * 100) === 0 ? '<1' : Math.round(item.value / stageTotal * 100)) : 0 }}%</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </a-col>
      </a-row>

    </template>
  </div>
</template>
;

const styleContent = 
<style lang="scss" scoped>
.ultra-premium-dashboard {
  position: relative;
  padding: 24px;
  min-height: 100%;
  overflow-y: auto;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  background-color: #f4f7fe;
  
  /* Rich, high-end SaaS background meshes */
  background-image: 
    radial-gradient(circle at 15% 10%, rgba(139, 92, 246, 0.05) 0%, transparent 40%),
    radial-gradient(circle at 85% 20%, rgba(59, 130, 246, 0.06) 0%, transparent 40%),
    radial-gradient(circle at 50% 80%, rgba(6, 182, 212, 0.04) 0%, transparent 50%);
  background-attachment: fixed;
  
  @media (max-width: 768px) {
    padding: 16px;
  }
}

.mb-24 { margin-bottom: 24px; }
.mt-24 { margin-top: 24px; }
.pb-24 { padding-bottom: 24px; }
.h-full { height: 100%; }

/* --- Global Ultra Card Style --- */
.ultra-card {
  background: #ffffff;
  border-radius: 20px;
  box-shadow: 0 10px 40px -10px rgba(0,0,0,0.04);
  border: 1px solid rgba(226, 232, 240, 0.8);
  display: flex;
  flex-direction: column;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
  
  &:hover {
    box-shadow: 0 20px 40px -15px rgba(0,0,0,0.08);
    transform: translateY(-2px);
  }

  .card-header {
    padding: 20px 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    &.border-bottom {
      border-bottom: 1px solid #f1f5f9;
    }

    .header-title-group {
      display: flex;
      flex-direction: column;
      gap: 4px;
      
      .card-title {
        margin: 0;
        font-size: 16px;
        font-weight: 800;
        color: #0f172a;
        letter-spacing: -0.2px;
      }
      
      .card-subtitle {
        font-size: 12px;
        color: #64748b;
        font-weight: 500;
      }
    }
  }

  .card-body {
    padding: 24px;
    flex: 1;
    display: flex;
    flex-direction: column;
    
    &.no-padding {
      padding: 0;
    }
  }
}

/* --- Command Header --- */
.command-header {
  position: relative;
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
  border-radius: 20px;
  padding: 32px;
  overflow: hidden;
  box-shadow: 0 15px 35px -10px rgba(15, 23, 42, 0.3);
  
  .header-bg-decoration {
    position: absolute;
    top: -50%; left: -10%; right: -10%; bottom: -50%;
    background: 
      radial-gradient(circle at 20% 50%, rgba(139, 92, 246, 0.25) 0%, transparent 40%),
      radial-gradient(circle at 80% 80%, rgba(59, 130, 246, 0.25) 0%, transparent 40%);
    pointer-events: none;
    z-index: 0;
  }
  
  .header-content {
    position: relative;
    z-index: 1;
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    @media (max-width: 768px) {
      flex-direction: column;
      align-items: flex-start;
      gap: 24px;
    }
    
    .user-greeting {
      .greeting-title {
        font-size: 32px;
        font-weight: 800;
        color: #ffffff;
        margin-bottom: 12px;
        letter-spacing: -0.5px;
        
        .gradient-text {
          background: linear-gradient(90deg, #a78bfa, #60a5fa, #34d399);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
        }
      }
      
      .greeting-subtitle {
        font-size: 15px;
        color: #94a3b8;
        margin: 0;
        
        strong {
          color: #38bdf8;
          font-weight: 700;
          font-size: 16px;
        }
      }
    }
    
    .header-actions {
      display: flex;
      gap: 16px;
      
      .action-btn {
        border-radius: 12px;
        font-weight: 600;
        border: none;
        box-shadow: none;
        
        &.glass-btn {
          background: rgba(139, 92, 246, 0.8);
          backdrop-filter: blur(10px);
          color: #fff;
          &:hover { background: rgba(139, 92, 246, 1); }
        }
        
        &.secondary-btn {
          background: rgba(255, 255, 255, 0.1);
          color: #fff;
          backdrop-filter: blur(10px);
          &:hover { background: rgba(255, 255, 255, 0.2); }
        }
      }
    }
  }
}

/* --- KPI Matrix --- */
.kpi-matrix {
  .kpi-card {
    background: #ffffff;
    border-radius: 20px;
    padding: 24px;
    display: flex;
    flex-direction: column;
    box-shadow: 0 10px 40px -10px rgba(0,0,0,0.03);
    border: 1px solid rgba(226, 232, 240, 0.8);
    position: relative;
    overflow: hidden;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    
    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 20px 40px -15px rgba(0,0,0,0.08);
      border-color: rgba(226, 232, 240, 1);
    }
    
    .kpi-icon-wrap {
      width: 48px;
      height: 48px;
      border-radius: 14px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 22px;
      margin-bottom: 16px;
      
      &.purple-glow { background: rgba(139, 92, 246, 0.1); color: #8b5cf6; }
      &.pink-glow { background: rgba(236, 72, 153, 0.1); color: #ec4899; }
      &.blue-glow { background: rgba(59, 130, 246, 0.1); color: #3b82f6; }
      &.cyan-glow { background: rgba(6, 182, 212, 0.1); color: #06b6d4; }
    }
    
    .kpi-info {
      display: flex;
      flex-direction: column;
      gap: 4px;
      
      .kpi-label {
        font-size: 14px;
        color: #64748b;
        font-weight: 600;
      }
      
      .kpi-value {
        font-size: 28px;
        font-weight: 800;
        color: #0f172a;
        letter-spacing: -0.5px;
        line-height: 1.2;
        
        .kpi-unit {
          font-size: 14px;
          font-weight: 600;
          color: #94a3b8;
          margin-left: 2px;
        }
      }
    }
    
    .kpi-trend {
      position: absolute;
      top: 24px;
      right: 24px;
      
      .trend-pill {
        display: inline-flex;
        align-items: center;
        gap: 4px;
        padding: 4px 10px;
        border-radius: 20px;
        font-size: 13px;
        font-weight: 700;
        
        &.up { background: #ecfdf5; color: #059669; }
        &.down { background: #fff1f2; color: #e11d48; }
        &.neutral { background: #f1f5f9; color: #64748b; }
      }
    }
  }
}

/* --- Main Arena & AI Insights --- */
.chart-arena {
  .chart-canvas {
    width: 100%;
    height: 380px;
  }
  
  .glass-date-picker {
    border-radius: 8px;
    border-color: #e2e8f0;
    &:hover, &.ant-picker-focused { border-color: #8b5cf6; }
  }
}

.ai-insights-arena {
  .ai-gradient-title {
    background: linear-gradient(90deg, #8b5cf6, #ec4899);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    display: flex;
    align-items: center;
    gap: 8px;
    
    .ai-icon {
      color: #8b5cf6;
      -webkit-text-fill-color: initial;
    }
  }
  
  .refresh-btn {
    color: #94a3b8;
    &:hover { color: #8b5cf6; }
  }
  
  .insights-list {
    display: flex;
    flex-direction: column;
    padding: 16px;
    gap: 16px;
    
    .insight-item {
      display: flex;
      gap: 16px;
      padding: 16px;
      border-radius: 16px;
      background: #f8fafc;
      border: 1px solid transparent;
      transition: all 0.3s;
      
      &:hover {
        background: #ffffff;
        border-color: #e2e8f0;
        box-shadow: 0 4px 12px rgba(0,0,0,0.03);
      }
      
      .insight-icon {
        width: 32px;
        height: 32px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 16px;
        flex-shrink: 0;
      }
      
      &.critical .insight-icon { background: rgba(139, 92, 246, 0.1); color: #8b5cf6; }
      &.warning .insight-icon { background: rgba(245, 158, 11, 0.1); color: #f59e0b; }
      &.success .insight-icon { background: rgba(16, 185, 129, 0.1); color: #10b981; }
      
      .insight-content {
        display: flex;
        flex-direction: column;
        align-items: flex-start;
        gap: 12px;
        
        .insight-text {
          margin: 0;
          font-size: 14px;
          color: #334155;
          line-height: 1.5;
          
          strong {
            color: #0f172a;
            font-weight: 700;
          }
        }
        
        .insight-action-btn {
          border-radius: 8px;
          font-weight: 600;
          
          &.glass { background: linear-gradient(135deg, #a78bfa, #8b5cf6); border: none; }
          &.outline { background: #fff; border: 1px solid #cbd5e1; color: #475569; }
        }
      }
    }
  }
}

/* --- Leaderboards & Funnel --- */
.list-arena {
  .rank-body {
    padding: 16px 24px;
  }
  
  .modern-rank-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
    
    .rank-row {
      display: flex;
      align-items: center;
      gap: 16px;
      
      .rank-avatar {
        width: 32px;
        height: 32px;
        border-radius: 50%;
        background: #f1f5f9;
        color: #64748b;
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: 800;
        font-size: 13px;
        flex-shrink: 0;
        
        &.top-1 { background: linear-gradient(135deg, #fbbf24, #f59e0b); color: #fff; box-shadow: 0 4px 10px rgba(245, 158, 11, 0.3); }
        &.top-2 { background: linear-gradient(135deg, #94a3b8, #64748b); color: #fff; box-shadow: 0 4px 10px rgba(100, 116, 139, 0.3); }
        &.top-3 { background: linear-gradient(135deg, #fca5a5, #ef4444); color: #fff; box-shadow: 0 4px 10px rgba(239, 68, 68, 0.3); }
        
        &.alt.top-1 { background: linear-gradient(135deg, #60a5fa, #3b82f6); box-shadow: 0 4px 10px rgba(59, 130, 246, 0.3); }
        &.alt.top-2 { background: linear-gradient(135deg, #38bdf8, #0ea5e9); box-shadow: 0 4px 10px rgba(14, 165, 233, 0.3); }
        &.alt.top-3 { background: linear-gradient(135deg, #818cf8, #6366f1); box-shadow: 0 4px 10px rgba(99, 102, 241, 0.3); }
      }
      
      .rank-info {
        flex: 1;
        display: flex;
        flex-direction: column;
        gap: 6px;
        
        .rank-name {
          font-size: 14px;
          font-weight: 700;
          color: #1e293b;
        }
        
        .rank-progress-bg {
          height: 6px;
          background: #f1f5f9;
          border-radius: 3px;
          overflow: hidden;
          
          .rank-progress-fill {
            height: 100%;
            border-radius: 3px;
            
            &.purple-gradient { background: linear-gradient(90deg, #a78bfa, #8b5cf6); }
            &.blue-gradient { background: linear-gradient(90deg, #60a5fa, #3b82f6); }
          }
        }
      }
      
      .rank-value {
        font-size: 14px;
        font-weight: 800;
        color: #334155;
        
        &.highlight { color: #8b5cf6; }
        .unit { font-size: 12px; color: #94a3b8; font-weight: 600; margin-left: 2px; }
      }
    }
  }
}

.funnel-arena {
  .funnel-body {
    display: flex;
    flex-direction: column;
    padding: 24px;
  }
  
  .funnel-total-box {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 24px;
    background: #f8fafc;
    border-radius: 16px;
    margin-bottom: 24px;
    
    .funnel-total-num {
      font-size: 36px;
      font-weight: 900;
      color: #0f172a;
      letter-spacing: -1px;
    }
    
    .funnel-total-label {
      font-size: 14px;
      color: #64748b;
      font-weight: 600;
      margin-top: 4px;
    }
  }
  
  .funnel-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
    
    .funnel-row {
      display: flex;
      align-items: center;
      gap: 12px;
      
      .funnel-label {
        width: 100px;
        display: flex;
        align-items: center;
        gap: 8px;
        
        .dot { width: 8px; height: 8px; border-radius: 4px; }
        .name { font-size: 13px; font-weight: 600; color: #475569; }
      }
      
      .funnel-bar-area {
        flex: 1;
        height: 8px;
        background: #f1f5f9;
        border-radius: 4px;
        overflow: hidden;
        
        .funnel-bar {
          height: 100%;
          border-radius: 4px;
        }
      }
      
      .funnel-stats {
        width: 70px;
        display: flex;
        justify-content: flex-end;
        align-items: baseline;
        gap: 6px;
        
        .count { font-size: 14px; font-weight: 800; color: #1e293b; }
        .percent { font-size: 12px; font-weight: 600; color: #94a3b8; }
      }
    }
  }
}
</style>
;

fs.writeFileSync(filePath, templateContent + scriptContent + styleContent, 'utf8');
console.log("Complete rewrite successful!");

import re
import sys

with open('C:\\Users\\Administrator\\Desktop\\红人服务系统源码\\frontend\\src\\views\\dashboard\\DashboardView.vue', 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Replace template
template_pattern = re.compile(r'<!-- Hero Section & To-Dos -->.*?(?=</template>\s*<script)', re.DOTALL)
new_template = '''      <!-- V2 Command Center Layout -->
      <a-row :gutter="[24, 24]">
        
        <!-- LEFT MAIN AREA (Trends & Leaderboards) -->
        <a-col :xs="24" :lg="16" :xl="17">
          <div class="left-main-area">
            <!-- 1. Main Trend Chart -->
            <a-card :bordered="false" class="premium-modern-card chart-card mb-24">
              <template #title>
                <div class="chart-header-enhanced">
                  <div class="header-left">
                    <div class="title-text">业绩趋势看板</div>
                    <div class="header-stats hidden-on-mobile">
                      <div class="stat-chip blue">
                        <span class="stat-label">总单</span>
                        <span class="stat-value">{{ aggregateStats.totalOrders }}</span>
                      </div>
                      <div class="stat-chip purple">
                        <span class="stat-label">总GMV</span>
                        <span class="stat-value">{{ formatCurrency(aggregateStats.totalGMV) }}</span>
                      </div>
                      <div class="stat-chip pink">
                        <span class="stat-label">佣金</span>
                        <span class="stat-value">{{ formatCurrency(aggregateStats.totalCommission) }}</span>
                      </div>
                    </div>
                  </div>
                  <div class="header-right">
                    <a-range-picker 
                      v-model:value="selectedDateRange"
                      class="premium-date-picker" 
                      size="middle" 
                      :ranges="ranges"
                      :allowClear="false"
                      @change="handleDateRangeChange"
                    />
                  </div>
                </div>
              </template>
              <div class="chart-main-body-compact">
                <div ref="chartContainer" class="chart-canvas-view" style="height: 420px;"></div>
              </div>
            </a-card>

            <!-- 2. Dual Leaderboards -->
            <a-row :gutter="[24, 24]">
              <!-- GMV Leaderboard -->
              <a-col :xs="24" :md="12">
                <a-card :bordered="false" title="近30日 GMV 排行" class="premium-modern-card list-card">
                  <div v-if="gmvTop10.length === 0" class="empty-state">
                    <a-empty description="暂无数据" :image-style="{ height: '60px' }" />
                  </div>
                  <div v-else class="rank-list">
                    <div v-for="(item, index) in gmvTop10" :key="index" class="rank-row">
                      <div class="rank-badge" :class="\pos-\\">{{ index + 1 }}</div>
                      <div class="rank-content">
                        <div class="rank-info-header">
                          <div class="rank-name">{{ item.name }}</div>
                          <div class="rank-value highlight">{{ formatCurrency(item.gmv) }}</div>
                        </div>
                        <div class="rank-bar-wrap">
                          <div class="rank-bar purple-gradient" :style="{ width: (gmvTop10[0] && gmvTop10[0].gmv > 0 ? item.gmv / gmvTop10[0].gmv * 100 : 0) + '%' }"></div>
                        </div>
                      </div>
                    </div>
                  </div>
                </a-card>
              </a-col>

              <!-- Orders Leaderboard -->
              <a-col :xs="24" :md="12">
                <a-card :bordered="false" title="近30日 转化单量排行" class="premium-modern-card list-card">
                  <div v-if="orderTop10.length === 0" class="empty-state">
                    <a-empty description="暂无数据" :image-style="{ height: '60px' }" />
                  </div>
                  <div v-else class="rank-list">
                    <div v-for="(item, index) in orderTop10" :key="index" class="rank-row">
                      <div class="rank-badge alt" :class="\pos-\\">{{ index + 1 }}</div>
                      <div class="rank-content">
                        <div class="rank-info-header">
                          <div class="rank-name">{{ item.name }}</div>
                          <div class="rank-value">{{ item.orderCount }} 单</div>
                        </div>
                        <div class="rank-bar-wrap">
                          <div class="rank-bar blue-gradient" :style="{ width: (orderTop10[0] && orderTop10[0].orderCount > 0 ? item.orderCount / orderTop10[0].orderCount * 100 : 0) + '%' }"></div>
                        </div>
                      </div>
                    </div>
                  </div>
                </a-card>
              </a-col>
            </a-row>
          </div>
        </a-col>

        <!-- RIGHT SIDEBAR (Financial Overview) -->
        <a-col :xs="24" :lg="8" :xl="7">
          <div class="right-sidebar">
            
            <!-- 1. Vertical Stats Panel -->
            <a-card :bordered="false" class="premium-modern-card vertical-stats-panel mb-24">
              <template #title>
                <div class="panel-header">
                  <span class="title-text">核心资产总览</span>
                  <span class="subtitle-text">实时表现</span>
                </div>
              </template>
              
              <div class="vertical-stats-list">
                <!-- Stat 1: GMV -->
                <div class="v-stat-item">
                  <div class="v-stat-icon purple"><dollar-outlined /></div>
                  <div class="v-stat-content">
                    <div class="v-stat-label">订单 GMV</div>
                    <div class="v-stat-value">{{ formatCurrency(stats.orderGMV) }}</div>
                  </div>
                  <div class="v-stat-trend">
                    <span v-if="!stats.dataInsufficient" :class="['trend-badge', stats.orderGMVTrend >= 0 ? 'up' : 'down']">
                      <component :is="stats.orderGMVTrend >= 0 ? ArrowUpOutlined : ArrowDownOutlined" />
                      {{ Math.abs(stats.orderGMVTrend).toFixed(1) }}%
                    </span>
                    <span v-else class="trend-badge neutral">--</span>
                  </div>
                </div>
                
                <!-- Stat 2: Commission -->
                <div class="v-stat-item">
                  <div class="v-stat-icon pink"><pay-circle-outlined /></div>
                  <div class="v-stat-content">
                    <div class="v-stat-label">预估分佣</div>
                    <div class="v-stat-value">{{ formatCurrency(stats.commissionAmount) }}</div>
                  </div>
                  <div class="v-stat-trend">
                    <span v-if="!stats.dataInsufficient" :class="['trend-badge', stats.commissionTrend >= 0 ? 'up' : 'down']">
                      <component :is="stats.commissionTrend >= 0 ? ArrowUpOutlined : ArrowDownOutlined" />
                      {{ Math.abs(stats.commissionTrend).toFixed(1) }}%
                    </span>
                    <span v-else class="trend-badge neutral">--</span>
                  </div>
                </div>

                <!-- Stat 3: Orders -->
                <div class="v-stat-item">
                  <div class="v-stat-icon blue"><shopping-outlined /></div>
                  <div class="v-stat-content">
                    <div class="v-stat-label">转化订单</div>
                    <div class="v-stat-value">{{ stats.conversionOrders }} 单</div>
                  </div>
                  <div class="v-stat-trend">
                    <span v-if="!stats.dataInsufficient" :class="['trend-badge', stats.conversionOrdersTrend >= 0 ? 'up' : 'down']">
                      <component :is="stats.conversionOrdersTrend >= 0 ? ArrowUpOutlined : ArrowDownOutlined" />
                      {{ Math.abs(stats.conversionOrdersTrend).toFixed(1) }}%
                    </span>
                    <span v-else class="trend-badge neutral">--</span>
                  </div>
                </div>

                <!-- Stat 4: Influencers -->
                <div class="v-stat-item">
                  <div class="v-stat-icon cyan"><user-outlined /></div>
                  <div class="v-stat-content">
                    <div class="v-stat-label">活跃红人</div>
                    <div class="v-stat-value">{{ stats.activeInfluencers }} 位</div>
                  </div>
                  <div class="v-stat-trend">
                    <span v-if="!stats.dataInsufficient" :class="['trend-badge', stats.activeInfluencersTrend >= 0 ? 'up' : 'down']">
                      <component :is="stats.activeInfluencersTrend >= 0 ? ArrowUpOutlined : ArrowDownOutlined" />
                      {{ Math.abs(stats.activeInfluencersTrend).toFixed(1) }}%
                    </span>
                    <span v-else class="trend-badge neutral">--</span>
                  </div>
                </div>
              </div>
            </a-card>

            <!-- 2. Stage Distribution -->
            <a-card :bordered="false" title="开发阶段分布" class="premium-modern-card stage-card">
              <div class="stage-distribution">
                <div class="stage-total">
                  <span class="total-value">{{ stageTotal }}</span>
                  <span class="total-label">位红人</span>
                </div>
                <div class="stage-bars">
                  <div v-for="item in pieChartData" :key="item.label" class="stage-bar-row">
                    <div class="stage-bar-header">
                      <div class="stage-bar-label">
                        <span class="stage-dot" :style="{ background: item.color }"></span>
                        <span class="stage-name">{{ item.label }}</span>
                      </div>
                      <div class="stage-bar-values">
                        <span class="stage-count">{{ item.value }} 位</span>
                        <span class="stage-percent">{{ stageTotal > 0 ? (item.value > 0 && Math.round(item.value / stageTotal * 100) === 0 ? '<1' : Math.round(item.value / stageTotal * 100)) : 0 }}%</span>
                      </div>
                    </div>
                    <div class="stage-bar-track">
                      <div class="stage-bar-fill" :style="{ width: (stageTotal > 0 ? item.value / stageTotal * 100 : 0) + '%', background: \linear-gradient(90deg, \, \cc)\ }"></div>
                    </div>
                  </div>
                </div>
              </div>
            </a-card>

          </div>
        </a-col>
      </a-row>
    </template>
'''
content = template_pattern.sub(new_template, content)

# 2. Remove script mock data
script_pattern = re.compile(r'// Mock To-Do List.*?\]\);', re.DOTALL)
content = script_pattern.sub('// No mock data needed for V2 Layout', content)

# 3. Replace CSS from /* --- Hero Section & Welcome Banner --- */ up to /* --- Main Chart Card & Bottom Cards --- */
css_pattern = re.compile(r'/\* --- Hero Section & Welcome Banner --- \*/.*?/\* --- Main Chart Card & Bottom Cards --- \*/', re.DOTALL)
new_css = '''/* --- V2 Command Center Layout CSS --- */
.mb-24 { margin-bottom: 24px; }

.vertical-stats-panel {
  .panel-header {
    display: flex;
    justify-content: space-between;
    align-items: baseline;
    
    .title-text {
      font-size: 16px;
      font-weight: 800;
      color: #0f172a;
    }
    
    .subtitle-text {
      font-size: 13px;
      font-weight: 600;
      color: #94a3b8;
    }
  }

  .vertical-stats-list {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  .v-stat-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 16px;
    border-radius: 12px;
    background: #f8fafc;
    border: 1px solid transparent;
    transition: all 0.3s ease;

    &:hover {
      background: #ffffff;
      border-color: #e2e8f0;
      box-shadow: 0 10px 15px -3px rgba(0,0,0,0.05);
      transform: translateY(-2px);
    }

    .v-stat-icon {
      width: 44px;
      height: 44px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 20px;
      background: #ffffff;
      border: 1px solid #e2e8f0;
      box-shadow: 0 2px 4px rgba(0,0,0,0.02);
      flex-shrink: 0;

      &.cyan { color: #06b6d4; box-shadow: inset 0 0 0 1px rgba(6, 182, 212, 0.1); }
      &.blue { color: #3b82f6; box-shadow: inset 0 0 0 1px rgba(59, 130, 246, 0.1); }
      &.purple { color: #8b5cf6; box-shadow: inset 0 0 0 1px rgba(139, 92, 246, 0.1); }
      &.pink { color: #ec4899; box-shadow: inset 0 0 0 1px rgba(236, 72, 153, 0.1); }
    }

    .v-stat-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 4px;

      .v-stat-label {
        font-size: 13px;
        font-weight: 600;
        color: #64748b;
      }

      .v-stat-value {
        font-size: 20px;
        font-weight: 800;
        color: #0f172a;
        line-height: 1.2;
        letter-spacing: -0.5px;
      }
    }

    .v-stat-trend {
      .trend-badge {
        display: inline-flex;
        align-items: center;
        gap: 4px;
        padding: 4px 8px;
        border-radius: 6px;
        font-size: 12px;
        font-weight: 700;

        &.up { background: #ecfdf5; color: #059669; border: 1px solid #d1fae5; }
        &.down { background: #fff1f2; color: #e11d48; border: 1px solid #ffe4e6; }
        &.neutral { background: #f1f5f9; color: #64748b; border: 1px solid #e2e8f0; }
      }
    }
  }
}

/* --- Main Chart Card & Bottom Cards --- */'''
content = css_pattern.sub(new_css, content)

with open('C:\\Users\\Administrator\\Desktop\\红人服务系统源码\\frontend\\src\\views\\dashboard\\DashboardView.vue', 'w', encoding='utf-8') as f:
    f.write(content)

print("Replacement Complete")

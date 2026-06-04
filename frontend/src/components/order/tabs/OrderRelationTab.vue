<template>
  <div class="order-relation-tab">
    <div class="relation-header">
      <div class="header-info">
        <div class="header-title-row">
          <span class="header-icon">📦</span>
          <span class="header-title">订单关系图谱</span>
          <a-tag color="orange" class="split-tag">拆单订单</a-tag>
        </div>
      </div>
    </div>

    <!-- 订单-包裹关系图 -->
    <div class="relation-content">
      <div class="relation-section">
        <div class="section-title">
          <span>订单包裹关系</span>
        </div>
        
        <!-- 主订单信息 -->
        <div class="order-card main-order compact">
          <div class="order-body">
            <!-- 新布局：三行结构 -->
            <div class="order-grid-rows">
              <!-- 第一行：长单号、短单号、包裹号 -->
              <div class="grid-row top-row">
                <div class="info-group">
                  <span class="label">长单号</span>
                  <span class="value order-no">{{ orderData?.orderNo }}</span>
                </div>
                <div class="info-group">
                  <span class="label">短单号</span>
                  <span class="value order-no">{{ orderData?.shortOrderNo || '-' }}</span>
                </div>
                <div class="info-group">
                  <span class="label">当前包裹</span>
                  <span class="value package-no">{{ orderData?.packageNo || '-' }}</span>
                </div>
              </div>

              <!-- 第二行：物流状态、订单状态、关联红人 -->
              <div class="grid-row middle-row">
                <div class="info-group">
                  <span class="label">物流状态</span>
                  <a-tag :color="getLogisticsColor(orderData?.logisticsStatus)" class="status-tag-rounded" v-if="orderData?.logisticsStatus">
                    {{ orderData.logisticsStatus }}
                  </a-tag>
                  <span v-else>-</span>
                </div>
                <div class="info-group">
                  <span class="label">订单状态</span>
                  <a-tag :color="getStatusColor(orderData?.orderStatus)" class="status-tag-rounded">
                    {{ orderData?.orderStatus || '-' }}
                  </a-tag>
                </div>
                <div class="info-group">
                  <span class="label">关联红人</span>
                  <span class="value highlight-text">{{ orderData?.influencerName || '-' }}</span>
                </div>
              </div>

              <!-- 第三行：发货时间、妥投时间、分佣时间 -->
              <div class="grid-row bottom-row">
                <div class="info-group">
                  <span class="label">发货时间</span>
                  <span class="value time-text">{{ orderData?.timeInfo?.shipTime || '-' }}</span>
                </div>
                <div class="info-group">
                  <span class="label">妥投时间</span>
                  <span class="value time-text">{{ orderData?.timeInfo?.deliveredTime || '-' }}</span>
                </div>
                <div class="info-group">
                  <span class="label">分佣时间</span>
                  <span class="value time-text">{{ orderData?.timeInfo?.commissionTime || '-' }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 包裹列表 (卡片式展示) -->
        <div class="packages-section" v-if="packageList && packageList.length > 0">
          <div class="section-subtitle">
            <span class="icon">🧩</span> 拆单包裹 (Split Packages)
          </div>
          
          <div class="package-list">
            <div 
              v-for="(pkg, index) in packageTableData" 
              :key="pkg.packageNo"
              class="package-card"
            >
              <!-- 头部：包裹号与状态 -->
              <div class="pkg-header">
                <div class="pkg-left">
                  <div class="pkg-icon-wrapper">
                    <span class="pkg-icon">📦</span>
                  </div>
                  <div class="pkg-info-stack">
                    <span class="pkg-no">{{ pkg.packageNo }}</span>
                    <span class="pkg-index">Package #{{ pkg.index }}</span>
                  </div>
                </div>
                <div class="pkg-right">
                  <a-tag :color="getStatusColor(orderData?.orderStatus)" class="status-tag-modern">
                    {{ orderData?.orderStatus }}
                  </a-tag>
                  <a-tag 
                    v-if="pkg.status" 
                    :color="getLogisticsColor(pkg.status)" 
                    class="status-tag-modern"
                  >
                    {{ pkg.status }}
                  </a-tag>
                  <a-tag 
                    v-if="pkg.commissionStatus" 
                    :color="getCommissionStatusColor(pkg.commissionStatus)"
                    class="status-tag-modern"
                  >
                    {{ getCommissionStatusText(pkg.commissionStatus) }}
                  </a-tag>
                </div>
              </div>
              
              <!-- 中部：时间与关联信息 (水平布局) -->
              <div class="pkg-body horizontal-layout">
                <div class="info-item-row">
                  <span class="label">物流信息:</span>
                  <span class="value highlight-text">{{ pkg.logisticsNo || pkg.trackingNo || orderData?.logisticsNo || 'SF1334567890' }} ({{ pkg.logisticsName || orderData?.logisticsName || '顺丰速运' }})</span>
                </div>
                <div class="info-item-row">
                  <span class="label">发货时间:</span>
                  <span class="value time-text">{{ pkg.shipTime !== '-' ? pkg.shipTime : '2023-12-01 12:00:00' }}</span>
                </div>
                <div class="info-item-row">
                  <span class="label">妥投时间:</span>
                  <span class="value time-text">{{ pkg.deliveredTime !== '-' ? pkg.deliveredTime : '2023-12-03 14:30:00' }}</span>
                </div>
                <div class="info-item-row">
                  <span class="label">分佣时间:</span>
                  <span class="value time-text">{{ pkg.commissionTime !== '-' ? pkg.commissionTime : '2023-12-05 10:00:00' }}</span>
                </div>
              </div>
              
              <!-- 底部：商品列表 -->
              <div class="pkg-footer">
                <div class="products-preview">
                  <div 
                    v-for="(p, idx) in pkg.products" 
                    :key="idx" 
                    class="mini-product-item"
                  >
                    <div class="product-thumb-wrapper">
                      <img :src="p.image || placeholderImage" @error="handleImageError" class="product-thumb" />
                    </div>
                    <div class="product-details-layout">
                      <div class="row-top">
                        <span class="sku">{{ p.sku }}</span>
                        <span class="qty">x{{ p.quantity }}</span>
                      </div>
                      <div class="row-bottom">
                        <span class="price">{{ p.price ? `$${p.price}` : '-' }}</span>
                        <div class="status-badge" v-if="p.status && p.status !== '正常'">
                          {{ p.status }}
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <div class="orders-note">
            <span class="note-icon">ℹ️</span> 一个订单可以有多个包裹，包裹以订单为维度进行管理
          </div>
        </div>

        <!-- 换货信息 -->
        <div class="exchange-section" v-if="exchangeList && exchangeList.length > 0">
          <div class="section-subtitle">
            <span class="icon">🔄</span> 换货记录
          </div>
          <div class="exchange-list">
            <div 
              v-for="(exchange, index) in exchangeList" 
              :key="index"
              class="exchange-card"
            >
              <div class="exchange-header">
                <span class="exchange-label">换货记录 #{{ Number(index) + 1 }}</span>
                <span class="exchange-time" v-if="exchange.time">{{ exchange.time }}</span>
              </div>
              <div class="exchange-body">
                <div class="exchange-items">
                  <div class="exchange-item old">
                    <div class="item-header">
                      <span class="badge red">退回</span>
                      <span class="item-qty">x{{ exchange.oldProduct.quantity }}</span>
                    </div>
                    <div class="item-sku">{{ exchange.oldProduct.sku }}</div>
                  </div>
                  <div class="exchange-arrow">
                    <span class="arrow-icon">➔</span>
                  </div>
                  <div class="exchange-item new">
                    <div class="item-header">
                      <span class="badge green">换新</span>
                      <span class="item-qty">x{{ exchange.newProduct.quantity }}</span>
                    </div>
                    <div class="item-sku">{{ exchange.newProduct.sku }}</div>
                  </div>
                </div>
                <div class="exchange-reason" v-if="exchange.reason">
                  <span class="label">换货原因：</span>
                  <span>{{ exchange.reason }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';

const props = defineProps<{
  orderData: any;
}>();

const placeholderImage = 'https://via.placeholder.com/60x60?text=NoImg';

// 主订单商品（拆单后剩余的商品）
const mainOrderProducts = computed(() => {
  if (!props.orderData?.products) return [];
  
  // 如果有拆单，主订单的商品应该是原始商品减去被拆出去的商品
  if (props.orderData.isSplit && props.orderData.splitProducts) {
    const splitSkuSet = new Set(props.orderData.splitProducts.map((p: any) => p.sku));
    return props.orderData.products
      .filter((p: any) => !splitSkuSet.has(p.sku))
      .map((p: any) => ({
        ...p,
        image: p.image || `https://via.placeholder.com/60x60?text=${p.sku}`,
      }));
  }
  
  return props.orderData.products.map((p: any) => ({
    ...p,
    image: p.image || `https://via.placeholder.com/60x60?text=${p.sku}`,
  }));
});

// 包裹列表（从订单数据中获取，一个订单可以有多个包裹）
const packageList = computed(() => {
  if (!props.orderData?.packageRelations) {
    // 如果没有提供关系数据，根据packageNo生成模拟数据
    if (props.orderData?.packageNo) {
      // 主单包裹（包含主单商品）
      const mainPackage = {
        packageNo: props.orderData.packageNo,
        status: props.orderData.logisticsStatus || '已发货',
        commissionStatus: props.orderData.commissionStatus || 'pending',
        products: mainOrderProducts.value,
      };
      
      // 如果有拆单商品，添加拆单包裹
      if (props.orderData.splitProducts && props.orderData.splitProducts.length > 0) {
        return [
          mainPackage,
          {
            packageNo: `${props.orderData.packageNo}-SPLIT`,
            status: props.orderData.logisticsStatus || '已发货',
            commissionStatus: 'pending',
            products: props.orderData.splitProducts.map((p: any) => ({
              ...p,
              image: p.image || `https://via.placeholder.com/60x60?text=${p.sku}`,
            })),
          }
        ];
      }
      
      return [mainPackage];
    }
    return [];
  }
  
  // 直接返回包裹关系数据（一个订单可以有多个包裹）
  return props.orderData.packageRelations;
});

// 换货列表
const exchangeList = computed(() => {
  return props.orderData?.exchanges || [];
});

const getStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    '待处理': 'orange',
    '待发货': 'processing',
    '已发货': 'blue',
    '已妥投': 'cyan',
    '已完成': 'success',
    '取消中': 'warning',
    '已取消': 'default',
    '待付款': 'orange',
    '异常': 'error',
  };
  return colorMap[status] || 'default';
};

const getLogisticsColor = (status: string) => {
  const colorMap: Record<string, string> = {
    '已发货': 'blue',
    '已妥投': 'cyan',
    '已完成': 'success',
  };
  return colorMap[status] || 'default';
};

const getCommissionStatusColor = (status: string) => {
  const colorMap: Record<string, string> = {
    '待分佣': 'orange',
    '分佣中': 'processing',
    '已分佣': 'success',
    '分佣失败': 'error',
    '无需分佣': 'default',
  };
  return colorMap[status] || 'default';
};

const getCommissionStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    'pending': '待分佣',
    'distributing': '分佣中',
    'distributed': '已分佣',
    'failed': '分佣失败',
    'no_commission': '无需分佣',
  };
  return textMap[status] || status;
};

// 表格列定义（包裹列表）
const packageColumns = [
  { title: '包裹号', dataIndex: 'packageNo', key: 'packageNo', width: 150 },
  { title: '关联订单', dataIndex: 'order', key: 'order', width: 180 },
  { title: '物流状态', dataIndex: 'status', key: 'status', width: 110 },
  { title: '分佣状态', dataIndex: 'commissionStatus', key: 'commissionStatus', width: 110 },
  { title: '时间', dataIndex: 'time', key: 'time', width: 210 },
  { title: '商品', dataIndex: 'products', key: 'products' },
];

// 表格数据
const packageTableData = computed(() => {
  return packageList.value.map((pkg: any, idx: number) => ({
    ...pkg,
    index: idx + 1,
    shipTime: pkg.shipTime || pkg.sendTime || props.orderData?.timeInfo?.shipTime || '-',
    deliveredTime: pkg.deliveredTime || props.orderData?.timeInfo?.deliveredTime || '-',
    splitTime: pkg.splitTime || props.orderData?.timeInfo?.createTime || '-',
    commissionTime: pkg.commissionTime || props.orderData?.timeInfo?.distributeTime || '-',
  }));
});
const handleImageError = (e: any) => {
  const img = e.target as HTMLImageElement;
  const wrapper = img?.parentElement;
  if (!wrapper) return;
  img.style.display = 'none';
  if (wrapper.querySelector('.image-placeholder')) return;
  const placeholder = document.createElement('div');
  placeholder.className = 'image-placeholder';
  const icon = document.createElement('span');
  icon.textContent = '🖼️';
  icon.setAttribute('aria-label', 'image');
  icon.style.fontSize = '18px';
  placeholder.style.backgroundColor = '#f5f5f5';
  placeholder.style.color = '#d9d9d9';
  placeholder.style.display = 'flex';
  placeholder.style.alignItems = 'center';
  placeholder.style.justifyContent = 'center';
  placeholder.style.width = '100%';
  placeholder.style.height = '100%';
  placeholder.appendChild(icon);
  wrapper.appendChild(placeholder);
};
</script>

<style lang="scss" scoped>
.order-relation-tab {
  padding: 12px;
  background: #f8fafc;
  border-radius: 12px;
  width: 100%;
  display: flex;
  flex-direction: column;
}

.relation-header {
  margin-bottom: 8px;
  padding-bottom: 8px;
  border-bottom: 1px solid #e2e8f0;

  .header-info {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 12px;

    .header-title-row {
      display: flex;
      align-items: center;
      gap: 10px;

      .header-icon {
        font-size: 20px;
        background: #fff;
        width: 32px;
        height: 32px;
        display: flex;
        align-items: center;
        justify-content: center;
        border-radius: 8px;
        box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
      }

      .header-title {
        font-size: 16px;
        font-weight: 700;
        color: #1e293b;
      }

      .split-tag {
        border-radius: 6px;
        font-weight: 600;
        padding: 0 8px;
        font-size: 12px;
      }
    }

    .header-details {
      display: flex;
      gap: 12px;
      
      .info-text {
        font-size: 13px;
        color: #64748b;
        background: #fff;
        padding: 4px 10px;
        border-radius: 6px;
        border: 1px solid #f1f5f9;
        
        .highlight {
          color: #3b82f6;
          font-family: 'JetBrains Mono', monospace;
          font-weight: 600;
        }
      }
    }
  }
}

.relation-content {
  display: flex;
  flex-direction: column;
  width: 100%;

  .relation-section {
    display: flex;
    flex-direction: column;
    width: 100%;
    
    .section-title {
      font-size: 14px;
      font-weight: 700;
      margin-bottom: 8px;
      color: #334155;
      padding-left: 10px;
      border-left: 3px solid #3b82f6;
    }

    .section-subtitle {
      font-size: 13px;
      font-weight: 600;
      margin: 8px 0 6px 0;
      color: #475569;
      display: flex;
      align-items: center;
      gap: 6px;
      
      .icon { font-size: 14px; }
    }
  }
}

.order-card {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 12px;
  margin-bottom: 6px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.02);

  &.main-order {
    border: none;
    background: linear-gradient(145deg, #ffffff 0%, #f8fafc 100%);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
    position: relative;
    overflow: hidden;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 4px;
      height: 100%;
      background: #3b82f6;
    }
  }

  &.compact {
    padding: 16px 20px;
  }

    .order-grid-rows {
      display: flex;
      flex-direction: column;
      gap: 8px;
      
      .grid-row {
        display: grid;
        grid-template-columns: repeat(3, 1fr);
        gap: 12px;
        align-items: center;
        padding-bottom: 8px;
        border-bottom: 1px dashed #f1f5f9;
        
        &:last-child {
          padding-bottom: 0;
          border-bottom: none;
        }
      }
    }

    .info-group {
      display: flex;
      flex-direction: column;
      gap: 2px;
      
      .label {
        font-size: 12px;
        color: #94a3b8;
        font-weight: 500;
        text-transform: uppercase;
        letter-spacing: 0.5px;
      }

      .value {
        font-size: 13px;
        color: #334155;
        font-weight: 600;
        word-break: break-all;
        white-space: normal;
        line-height: 1.4;
        
        &.highlight-text {
          color: #1e293b;
        }
        
        &.time-text {
          font-family: 'JetBrains Mono', monospace;
          font-size: 12px;
          color: #64748b;
        }
      }
      
      .order-no {
        color: #3b82f6;
        font-weight: 700;
        font-family: 'JetBrains Mono', monospace;
        font-size: 14px;
      }
      
      .package-no {
        font-family: 'JetBrains Mono', monospace;
        color: #334155;
        font-weight: 600;
        font-size: 13px;
      }
      
      .status-tag-rounded {
        width: fit-content;
        border-radius: 20px;
        padding: 0 8px;
        font-weight: 600;
        border: none;
        font-size: 12px;
      }
    }
  }

/* 包裹列表样式 */
.packages-section {
  display: flex;
  flex-direction: column;

  .package-list {
    display: flex;
    flex-direction: column;
    gap: 8px;
    /* 只有包裹列表部分可以滚动 */
    max-height: 450px;
    overflow-y: auto;
    overflow-x: hidden;
    padding-right: 8px;
    margin-top: 6px;
    
    /* 滚动条样式 */
    &::-webkit-scrollbar {
      width: 6px;
    }
    &::-webkit-scrollbar-track {
      background: #f1f5f9;
      border-radius: 3px;
    }
    &::-webkit-scrollbar-thumb {
      background: #cbd5e1;
      border-radius: 3px;
    }
    &::-webkit-scrollbar-thumb:hover {
      background: #94a3b8;
    }
  }

  .package-card {
    background: #fff;
    border: 1px solid #f1f5f9;
    border-radius: 12px;
    padding: 0;
    transition: all 0.3s ease;
    overflow: hidden;

    &:hover {
      box-shadow: 0 10px 30px -5px rgba(0, 0, 0, 0.08);
      transform: translateY(-2px);
      border-color: #e2e8f0;
    }

    .pkg-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px 16px;
      background: #f8fafc;
      border-bottom: 1px solid #f1f5f9;

      .pkg-left {
        display: flex;
        align-items: center;
        gap: 12px;
        
        .pkg-icon-wrapper {
          width: 32px;
          height: 32px;
          background: #fff;
          border-radius: 8px;
          display: flex;
          align-items: center;
          justify-content: center;
          box-shadow: 0 2px 4px rgba(0,0,0,0.03);
          font-size: 16px;
        }

        .pkg-info-stack {
          display: flex;
          flex-direction: column;
          
          .pkg-no {
            font-size: 14px;
            font-weight: 700;
            color: #1e293b;
            font-family: 'JetBrains Mono', monospace;
          }

          .pkg-index {
            font-size: 10px;
            color: #94a3b8;
            font-weight: 600;
            text-transform: uppercase;
          }
        }
      }

      .pkg-right {
        display: flex;
        gap: 6px;
        
        .status-tag-modern {
          border: none;
          border-radius: 6px;
          padding: 2px 8px;
          font-weight: 600;
          font-size: 11px;
        }
      }
    }

    .pkg-body {
      padding: 12px 16px;
      display: flex;
      flex-direction: column;
      gap: 12px;

      &.horizontal-layout {
        flex-direction: row;
        flex-wrap: nowrap;
        align-items: center;
        justify-content: space-between;
        gap: 20px;
        
        .info-item-row {
          display: flex;
          align-items: center;
          gap: 8px;
          
          .label {
            color: #94a3b8;
            font-size: 12px;
            font-weight: 500;
            white-space: nowrap;
          }
          
          .value {
            color: #334155;
            font-weight: 500;
            font-size: 13px;
            
            &.highlight-text {
              color: #0f172a;
              font-weight: 600;
            }
            
            &.time-text {
              color: #64748b;
              font-size: 12px;
              font-family: 'JetBrains Mono', monospace;
            }
          }
        }
      }

      .info-row {
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 16px;
        
        .info-item {
          display: flex;
          flex-direction: column;
          gap: 4px;

          .label {
            color: #94a3b8;
            font-size: 12px;
            font-weight: 500;
          }

          .value {
            color: #334155;
            font-weight: 500;
            font-size: 13px;
            
            &.link {
              color: #3b82f6;
              font-family: 'JetBrains Mono', monospace;
              cursor: pointer;
              &:hover { text-decoration: underline; }
            }
            
            &.highlight-text {
              color: #0f172a;
              font-weight: 600;
            }
            
            &.time-text {
              color: #64748b;
              font-size: 12px;
            }
          }
          
          .value-stack {
            display: flex;
            flex-direction: column;
            gap: 2px;
          }
        }
      }
    }

    .pkg-footer {
      background: #fff;
      padding: 0 16px 16px 16px;

      .products-preview {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
        gap: 10px;
      }

      .mini-product-item {
        display: flex;
        align-items: center;
        gap: 10px;
        background: #f8fafc;
        padding: 8px;
        border-radius: 8px;
        border: 1px solid #f1f5f9;
        transition: all 0.2s;
        
        &:hover {
          background: #fff;
          border-color: #e2e8f0;
          box-shadow: 0 4px 12px rgba(0,0,0,0.03);
        }

        .product-thumb-wrapper {
          width: 40px;
          height: 40px;
          border-radius: 6px;
          overflow: hidden;
          flex-shrink: 0;
          border: 1px solid #e2e8f0;
          background: #fff;
          display: flex;
          align-items: center;
          justify-content: center;
        }

        .product-thumb {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }

        .product-details-layout {
          flex: 1;
          min-width: 0;
          display: flex;
          flex-direction: column;
          justify-content: center;
          gap: 2px;
          
          .row-top {
            display: flex;
            justify-content: space-between;
            align-items: center;

            .sku {
              font-size: 12px;
              font-weight: 600;
              color: #334155;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }
            
            .qty {
              font-size: 11px;
              color: #64748b;
              font-weight: 700;
              background: #e2e8f0;
              padding: 1px 5px;
              border-radius: 4px;
            }
          }

          .row-bottom {
            display: flex;
            justify-content: space-between;
            align-items: center;

            .price {
              font-size: 12px;
              font-weight: 700;
              color: #0f172a;
            }

            .status-badge {
              font-size: 10px;
              padding: 1px 5px;
              border-radius: 4px;
              background: #fee2e2;
              color: #ef4444;
              font-weight: 600;
            }
          }
        }
      }
    }
  }

  .orders-note {
    margin-top: 6px;
    font-size: 12px;
    color: #94a3b8;
    text-align: center;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 6px;
    
    .note-icon { font-size: 14px; }
  }
}

.exchange-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.exchange-card {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 0;
  overflow: hidden;

  .exchange-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 10px 16px;
    background: #f8fafc;
    border-bottom: 1px solid #f1f5f9;

    .exchange-label {
      font-weight: 700;
      color: #334155;
      font-size: 13px;
    }

    .exchange-time {
      font-size: 12px;
      color: #94a3b8;
      font-family: 'JetBrains Mono', monospace;
    }
  }

  .exchange-body {
    padding: 16px;

    .exchange-items {
      display: flex;
      align-items: center;
      gap: 16px;
      margin-bottom: 12px;

      .exchange-item {
        flex: 1;
        padding: 12px;
        background: #fff;
        border-radius: 8px;
        border: 1px solid #f1f5f9;
        box-shadow: 0 2px 4px rgba(0,0,0,0.02);

        &.old { border-left: 3px solid #ef4444; }
        &.new { border-left: 3px solid #22c55e; }

        .item-header {
          display: flex;
          justify-content: space-between;
          margin-bottom: 6px;
          
          .badge {
            font-size: 10px;
            font-weight: 700;
            padding: 2px 6px;
            border-radius: 4px;
            
            &.red { background: #fee2e2; color: #ef4444; }
            &.green { background: #dcfce7; color: #16a34a; }
          }
          
          .item-qty {
            font-weight: 600;
            color: #64748b;
            font-size: 11px;
          }
        }

        .item-sku {
          font-weight: 600;
          color: #1e293b;
          font-size: 13px;
        }
      }

      .exchange-arrow {
        font-size: 20px;
        color: #cbd5e1;
        display: flex;
        align-items: center;
        justify-content: center;
      }
    }

    .exchange-reason {
      font-size: 12px;
      color: #475569;
      padding: 8px 12px;
      background: #f1f5f9;
      border-radius: 6px;
      display: flex;
      align-items: center;

      .label {
        font-weight: 600;
        color: #64748b;
        margin-right: 6px;
      }
    }
  }
}
</style>

<template>
  <div class="payment-sample-section">
    <a-card title="打款信息" size="small" :body-style="{ padding: '12px' }">
      <a-table
        :columns="paymentColumns"
        :data-source="payments"
        :pagination="false"
        size="small"
        :row-key="(record: PaymentRecord, index: number) => record.paymentId || index"
      />
    </a-card>

    <a-card title="样品信息" size="small" :body-style="{ padding: '12px' }">
      <a-table
        :columns="sampleColumns"
        :data-source="sampleOrders"
        :pagination="false"
        size="small"
        :row-key="(record: SampleOrder, index: number) => `${record.spu}-${record.sku}-${index}`"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'spuSku'">
            <div>
              <div v-if="record.spu" style="margin-bottom: 2px;">
                <span style="color: #666; font-size: 12px;">SPU:</span>
                <span style="margin-left: 4px;">{{ record.spu }}</span>
              </div>
              <div v-if="record.sku">
                <span style="color: #666; font-size: 12px;">SKU:</span>
                <span style="margin-left: 4px;">{{ record.sku }}</span>
              </div>
            </div>
          </template>
          <template v-else-if="column.key === 'image'">
            <a-image
              :src="record.image"
              :width="60"
              :height="60"
              :preview="false"
              style="object-fit: cover; border-radius: 4px;"
              :fallback="'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PHJlY3Qgd2lkdGg9IjYwIiBoZWlnaHQ9IjYwIiBmaWxsPSIjZjBmMGYwIi8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtc2l6ZT0iMTIiIGZpbGw9IiM5OTkiIHRleHQtYW5jaG9yPSJtaWRkbGUiIGR5PSIuM2VtIj7lm77niYfliqDovb3lpLHotKU8L3RleHQ+PC9zdmc+'"
            />
          </template>
          <template v-else-if="column.key === 'price'">
            <span>¥{{ record.price?.toFixed(2) || '0.00' }}</span>
          </template>
          <template v-else-if="column.key === 'orderPackage'">
            <div>
              <div v-if="record.orderNo" style="margin-bottom: 4px;">
                <span style="color: #666; font-size: 12px;">订单：</span>
                <span>{{ record.orderNo }}</span>
              </div>
              <div v-if="record.packageNo">
                <span style="color: #666; font-size: 12px;">包裹：</span>
                <span>{{ record.packageNo }}</span>
              </div>
            </div>
          </template>
          <template v-else-if="column.key === 'time'">
            <div style="font-size: 12px; line-height: 1.6;">
              <div v-if="record.orderTime">
                <span style="color: #666;">下单：</span>{{ record.orderTime }}
              </div>
              <div v-if="record.shipTime">
                <span style="color: #666;">发货：</span>{{ record.shipTime }}
              </div>
              <div v-if="record.deliveryTime">
                <span style="color: #666;">妥投：</span>{{ record.deliveryTime }}
              </div>
            </div>
          </template>
          <template v-else-if="column.key === 'creator'">
            <span>{{ record.creator || 'SYS' }}</span>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
interface PaymentRecord {
  paymentId: string;
  amount: string;
  status: string;
  time: string;
}

interface SampleOrder {
  spu: string;
  sku: string;
  image: string;
  quantity: number;
  price: number;
  orderNo?: string;
  packageNo?: string;
  status: string;
  orderTime?: string;
  shipTime?: string;
  deliveryTime?: string;
  creator?: string;
}

const props = defineProps<{
  payments: PaymentRecord[];
  sampleOrders: SampleOrder[];
}>();

const paymentColumns = [
  { title: '打款ID', dataIndex: 'paymentId', key: 'paymentId', width: 140 },
  { title: '金额', dataIndex: 'amount', key: 'amount', width: 120 },
  { title: '状态', dataIndex: 'status', key: 'status', width: 120 },
  { title: '时间', dataIndex: 'time', key: 'time', width: 180 },
];

const sampleColumns = [
  { 
    title: 'SPU/SKU', 
    key: 'spuSku', 
    width: 180
  },
  { 
    title: '商品图片', 
    key: 'image', 
    width: 100,
    align: 'center'
  },
  { 
    title: '数量', 
    dataIndex: 'quantity', 
    key: 'quantity', 
    width: 80,
    align: 'center'
  },
  { 
    title: '价格', 
    key: 'price', 
    width: 100,
    align: 'right'
  },
  { 
    title: '关联订单/包裹单号', 
    key: 'orderPackage', 
    width: 200
  },
  { 
    title: '状态', 
    dataIndex: 'status', 
    key: 'status', 
    width: 120
  },
  { 
    title: '时间', 
    key: 'time', 
    width: 220
  },
  { 
    title: '下单人', 
    key: 'creator', 
    width: 100
  },
];
</script>

<style lang="scss" scoped>
.payment-sample-section {
  display: flex;
  flex-direction: column;
  gap: 12px;

  :deep(.ant-card) {
    margin: 0;
  }
}
</style>


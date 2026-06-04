<template>
  <a-modal
    v-model:open="visible"
    :title="null"
    width="880px"
    :footer="null"
    centered
    class="sample-create-modal"
    destroy-on-close
  >
    <div class="sc-modal-header">
      <div class="sc-header-left">
        <div class="sc-header-icon">O</div>
        <div class="sc-header-text">
          <div class="sc-header-title">{{ editData ? '编辑草稿订单' : '创建样品订单' }}</div>
          <div class="sc-header-subtitle">
            {{ editData ? '您可以修改草稿订单的所有信息，更新后仍为草稿状态' : '选择商品并填写收件信息，提交后系统将生成对应样品单' }}
          </div>
        </div>
      </div>
    </div>
    <div class="sc-modal-body">
      <a-form :model="createForm" layout="vertical" class="sc-form">
        <div class="sc-section">
          <div class="sc-section-header">
            <span class="sc-section-title">订单信息</span>
          </div>
          <div class="sc-section-body">
            <a-row :gutter="16">
              <a-col :span="8">
                <a-form-item label="目标店铺 (Store)" required>
                  <a-select 
                    v-model:value="createForm.storeId" 
                    placeholder="选择店铺" 
                    :loading="storesLoading"
                  >
                    <a-select-option v-for="store in stores" :key="store.id" :value="store.id">
                      {{ store.storeName }}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item label="关联红人 (Influencer)" required>
                <a-select 
                  v-model:value="createForm.influencerId" 
                  placeholder="输入红人名称搜索" 
                  show-search 
                  :filter-option="false" 
                  :loading="influencerLoading"
                  :options="influencerOptions"
                  @search="handleSearchInfluencer"
                  @change="handleInfluencerChange"
                >
                  <template #option="{ label, status }">
                    <div class="influencer-option" style="display: flex; align-items: center; justify-content: space-between; width: 100%;">
                      <span class="inf-name">{{ label }}</span>
                      <a-tag :color="getStatusColor(status)" size="small">
                        {{ getStatusLabel(status) }}
                      </a-tag>
                    </div>
                  </template>
                </a-select>
                  

                  <a-alert 
                    v-if="duplicateWarning" 
                    type="warning" 
                    show-icon 
                    style="margin-top: 8px; font-size: 12px;"
                  >
                    <template #message>
                      {{ duplicateWarning }}
                    </template>
                  </a-alert>
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item label="订单标签 (Tags)">
                  <a-select v-model:value="createForm.tags" mode="tags" placeholder="输入标签 (回车添加)" />
                </a-form-item>
              </a-col>
              
              <a-divider style="margin: 8px 0 24px; color: #94a3b8; font-size: 12px;">收件信息 (Shipping Address)</a-divider>



              <!-- Address Quick Select Cards -->
              <a-col :span="24" class="address-quick-select-col">
                <div class="address-quick-select-header">
                  <span class="label">选择已有地址 (Quick Select Saved Address):</span>
                  <a-tag v-if="addressLoading" color="processing">加载中...</a-tag>
                </div>
                <div class="address-cards-container" :class="{ empty: influencerAddresses.length === 0 }">
                  <div v-if="addressLoading" class="address-loading-state">
                    <a-spin tip="加载中..." />
                  </div>
                  <template v-else-if="influencerAddresses.length > 0">
                    <div 
                      v-for="addr in influencerAddresses" 
                      :key="addr.id" 
                      class="address-mini-card"
                      :class="{ active: selectedAddressId === addr.id }"
                      @click="handleAddressSelect(addr.id)"
                    >
                      <div class="card-header">
                        <span class="name text-ellipsis">{{ addr.recipientName }}</span>
                        <a-tag v-if="addr.isDefault" color="blue" size="small">默认</a-tag>
                      </div>
                      <div class="card-body">
                        <div class="addr-text">{{ addr.address }}</div>
                      </div>
                      <div class="card-check" v-if="selectedAddressId === addr.id">
                        <div class="check-icon">✓</div>
                      </div>
                    </div>
                  </template>
                  <div v-else class="address-empty-state">
                    {{ createForm.influencerId ? '该红人暂无保存的地址' : '请先选择红人以查看已存地址' }}
                  </div>
                </div>
              </a-col>

              <!-- 1. 国家 (Country) - 放最前面 -->
              <a-col :span="24">
                <a-form-item label="国家/地区 (Country)" required>
                  <a-select 
                    v-model:value="createForm.countryCode" 
                    placeholder="选择国家" 
                    show-search 
                    :filter-option="filterCountryOption"
                    :dropdownMatchSelectWidth="false"
                  >
                    <a-select-option v-for="c in countryOptions" :key="c.code" :value="c.code">
                      {{ c.name }} ({{ c.code }})
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>

              <!-- 2. 名字 + 姓氏 (First Name + Last Name) - 分开 -->
              <a-col :span="12">
                <a-form-item label="名字 (First Name)" required>
                  <a-input v-model:value="createForm.firstName" placeholder="名字" @blur="createForm.firstName = createForm.firstName.trim()" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="姓氏 (Last Name)" required>
                  <a-input v-model:value="createForm.lastName" placeholder="姓氏" @blur="createForm.lastName = createForm.lastName.trim()" />
                </a-form-item>
              </a-col>

              <!-- 3. 公司 (Company) -->
              <a-col :span="24">
                <a-form-item label="公司 (Company)">
                  <a-input v-model:value="createForm.company" placeholder="公司名称（选填）" />
                </a-form-item>
              </a-col>

              <!-- Address Smart Parser (Moved here) -->
              <a-col :span="24">
                <a-form-item label="地址智能解析 (Smart Address Parser)" class="address-parser-item">
                  <div class="address-parser-wrapper">
                    <a-input 
                      v-model:value="rawAddressInput" 
                      placeholder="粘贴完整地址，如: 145 w 67th st. Apt 9B NYC 10023" 
                      allow-clear
                      @pressEnter="parseAddress"
                    >
                      <template #prefix>
                        <environment-outlined style="color: #94a3b8" />
                      </template>
                      <template #suffix>
                        <a-button type="primary" size="small" @click="parseAddress" style="margin: -4px -7px -4px 0; border-radius: 4px;">
                          解析
                        </a-button>
                      </template>
                    </a-input>
                    <div class="parser-hint" v-if="!rawAddressInput">支持格式: 街道地址 + 公寓号 + 城市 + 州 + 邮编（自动识别美国地址）</div>
                  </div>
                </a-form-item>
              </a-col>

              <!-- 4. 地址 (Address Line 1) -->
              <a-col :span="24">
                <a-form-item label="地址 (Address)" required>
                  <a-input v-model:value="createForm.address" placeholder="街道地址" />
                </a-form-item>
              </a-col>

              <!-- 5. 公寓/房间号 (Address Line 2) -->
              <a-col :span="24">
                <a-form-item label="公寓、房间号等 (Apartment/Suite)">
                  <a-input v-model:value="createForm.address2" placeholder="公寓、房间号等（选填）" />
                </a-form-item>
              </a-col>

              <!-- 6. 城市 + 省份/州 -->
              <a-col :span="12">
                <a-form-item label="城市 (City)">
                  <a-input v-model:value="createForm.city" placeholder="城市" />
                </a-form-item>
              </a-col>
              <a-col :span="12" v-if="provinceOptions.length > 0">
                <a-form-item :label="provinceLabel" :required="provinceOptions.length > 0">
                  <a-select 
                    v-model:value="createForm.provinceCode" 
                    :placeholder="'选择' + provinceLabel" 
                    :loading="provinceLoading"
                    show-search 
                    option-filter-prop="label"
                  >
                    <a-select-option 
                      v-for="p in provinceOptions" 
                      :key="p.provinceCode" 
                      :value="p.provinceCode" 
                      :label="p.provinceName"
                    >
                      {{ p.provinceName }}
                    </a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>

              <!-- 7. 邮编 + 电话 -->
              <a-col :span="12">
                <a-form-item label="邮政编码 (Zip Code)">
                  <a-input v-model:value="createForm.zip" placeholder="邮政编码" />
                </a-form-item>
              </a-col>
              <a-col :span="12">
                <a-form-item label="电话号码 (Phone)" required>
                  <a-input-group compact>
                    <a-select
                      v-model:value="createForm.phonePrefix" 
                      show-search
                      :filter-option="filterCountryOption"
                      placeholder="+86"
                      style="width: 150px; text-align: center; background-color: #f8fafc;"
                      :dropdownMatchSelectWidth="300"
                    >
                      <a-select-option v-for="c in countryOptions" :key="c.code" :value="'+' + (c.phonePrefix || '').replace(/^\+/, '')">
                         {{ c.name }} {{ c.code }} +{{ c.phonePrefix ? c.phonePrefix.replace('+', '') : '' }}
                      </a-select-option>
                    </a-select>
                    <a-input 
                      v-model:value="createForm.phone" 
                      placeholder="电话号码" 
                      style="width: calc(100% - 150px)"
                    />
                  </a-input-group>
                </a-form-item>
              </a-col>

              <!-- 8. 电子邮箱 -->
              <a-col :span="12">
                <a-form-item label="电子邮箱 (Email)">
                  <a-input v-model:value="createForm.email" placeholder="电子邮箱" />
                </a-form-item>
              </a-col>
              
              <!-- 9. 订单备注 -->
              <a-col :span="24">
                <a-form-item label="订单备注 (Note)">
                  <a-textarea v-model:value="createForm.note" placeholder="输入订单备注信息" :rows="2" />
                </a-form-item>
              </a-col>
            </a-row>
          </div>
        </div>
        <div class="sc-section">
            <div class="sc-section-header">
            <span class="sc-section-title">商品信息 (Products)</span>
            <a-button type="primary" size="small" @click="openProductSelector">
              <plus-outlined /> 选择商品 (Select Product)
            </a-button>
          </div>
          <div class="sc-section-body">
            <a-table 
              :data-source="createForm.products" 
              :pagination="false" 
              row-key="id"
              size="small"
            >
              <a-table-column title="商品 (Product)" width="300px">
                <template #default="{ record }">
                  <div style="display: flex; align-items: center; gap: 12px;">
                    <img 
                      v-if="record.image" 
                      :src="record.image" 
                      style="width: 40px; height: 40px; border-radius: 4px; object-fit: cover;" 
                    />
                    <div 
                      v-else 
                      style="width: 40px; height: 40px; border-radius: 4px; background: #f1f5f9; display: flex; align-items: center; justify-content: center;"
                    >
                      <file-image-outlined style="color: #cbd5e1; font-size: 18px;" />
                    </div>
                    <div>
                      <div style="font-weight: 500; font-size: 13px;">{{ record.title }}</div>
                      <div style="font-size: 12px; color: #64748b;">{{ record.variant }}</div>
                    </div>
                  </div>
                </template>
              </a-table-column>
              <a-table-column title="SKU" data-index="sku" />
              <a-table-column title="价格 (Price)">
                <template #default="{ record }">
                  ${{ record.price.toFixed(2) }}
                </template>
              </a-table-column>
              <a-table-column title="数量 (Qty)" width="120px">
                <template #default="{ record }">
                  <a-input-number v-model:value="record.quantity" :min="1" size="small" />
                </template>
              </a-table-column>
              <a-table-column title="操作" width="60px" align="center">
                <template #default="{ index }">
                  <a-button type="text" danger size="small" @click="removeProduct(index)">
                    <delete-outlined />
                  </a-button>
                </template>
              </a-table-column>
            </a-table>
            
            <div v-if="createForm.products.length === 0" style="text-align: center; padding: 20px; color: #94a3b8;">
              暂无商品，请点击上方按钮添加
            </div>
          </div>
        </div>
      </a-form>
    </div>
    <div class="sc-modal-footer">
      <a-button @click="visible = false" style="margin-right: 12px;" :disabled="submitting">取消</a-button>
      <a-button @click="handleSubmitCreate(true)" :loading="submitting" style="margin-right: 12px;">保存为草稿</a-button>
      <a-button type="primary" @click="handleSubmitCreate(false)" :loading="submitting">确认创建</a-button>
    </div>

    <!-- Product Selector Modal -->
    <ProductSelectorModal
      v-model:open="productSelectorVisible"
      :initial-selected-keys="selectedProductKeys"
      :store-id="createForm.storeId"
      @select="handleProductSelected"
    />
  </a-modal>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue';
import axios from 'axios';
import { PlusOutlined, DeleteOutlined, FileImageOutlined, EnvironmentOutlined } from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import { showDeleteConfirm } from '@/utils/modal';
import ProductSelectorModal from './ProductSelectorModal.vue';
export interface ShopifyStoreConfig { id?: number; storeName: string; }
export interface OrderCreateRequest { storeId: number; [key: string]: any; }
const createInfluencerOrder = async (...args: any[]): Promise<any> => {};
const updateShopifyOrder = async (...args: any[]): Promise<any> => {};
const getShopifyStores = async (...args: any[]): Promise<any> => [];
import { 
  getEnabledCountries, 
  getProvinces, 
  type Country,
  type Province 
} from '@/services/countryService';
import { checkDuplicateSampleOrder, type DuplicateCheckResult } from '@/services/influencerOrderService';
import influencerService from '@/services/influencerService';
import type { Influencer, Address as InfluencerAddress } from '@/types/influencer';
import { STATUS_LABEL, InfluencerStatus } from '@/types/enums';

const props = defineProps<{
  open: boolean;
  influencers: string[];
  editData?: any;
}>();

const emit = defineEmits(['update:open', 'success']);

const visible = computed({
  get: () => props.open,
  set: (val) => emit('update:open', val),
});

const productSelectorVisible = ref(false);
const selectedProductKeys = ref<number[]>([]);
const submitting = ref(false);
const stores = ref<ShopifyStoreConfig[]>([]);
const storesLoading = ref(false);

const getStatusColor = (status?: string) => {
  switch (status) {
    case 'COOPERATING': return 'green';
    case 'CONTACTED': return 'cyan';
    case 'COMMUNICATING': return 'blue';
    case 'PENDING': return 'blue';
    case 'UNSCREENED': return 'default';
    case 'DORMANT': return 'orange';
    case 'PAUSED': return 'orange';
    case 'REJECTED': return 'orange';
    case 'BLACKLIST': return 'red';
    case 'TERMINATED': return 'red';
    default: return 'default';
  }
};

const getStatusLabel = (status?: string) => {
  if (status && status in STATUS_LABEL) {
    return STATUS_LABEL[status as InfluencerStatus];
  }
  return status || '未知';
};

// 新增：红人搜索与地址逻辑
const influencerLoading = ref(false);
const searchedInfluencers = ref<Influencer[]>([]);
const influencerOptions = computed(() => {
  // Filter out influencers with empty names
  const validInfluencers = searchedInfluencers.value.filter(inf => inf.realName && inf.realName.trim() !== '');
  
  const options: any[] = validInfluencers.map(inf => ({
    value: inf.id,
    label: inf.realName,
    status: inf.status
  }));
  
  // Fallback for prop-passed influencers
  if (props.influencers && props.influencers.length > 0) {
    props.influencers.forEach(name => {
      if (!options.find(o => o.label === name)) {
        options.push({ value: name, label: name, status: undefined });
      }
    });
  }
  return options;
});
const influencerAddresses = ref<InfluencerAddress[]>([]);
const addressLoading = ref(false);
const selectedAddressId = ref<number | undefined>(undefined);
const duplicateWarning = ref<string | null>(null);
const duplicateCheckResult = ref<DuplicateCheckResult | null>(null);
const rawAddressInput = ref('');

// ====== 地址智能解析 ======
const US_STATE_MAP: Record<string, string> = {
  'alabama': 'AL', 'alaska': 'AK', 'arizona': 'AZ', 'arkansas': 'AR', 'california': 'CA',
  'colorado': 'CO', 'connecticut': 'CT', 'delaware': 'DE', 'florida': 'FL', 'georgia': 'GA',
  'hawaii': 'HI', 'idaho': 'ID', 'illinois': 'IL', 'indiana': 'IN', 'iowa': 'IA',
  'kansas': 'KS', 'kentucky': 'KY', 'louisiana': 'LA', 'maine': 'ME', 'maryland': 'MD',
  'massachusetts': 'MA', 'michigan': 'MI', 'minnesota': 'MN', 'mississippi': 'MS', 'missouri': 'MO',
  'montana': 'MT', 'nebraska': 'NE', 'nevada': 'NV', 'new hampshire': 'NH', 'new jersey': 'NJ',
  'new mexico': 'NM', 'new york': 'NY', 'north carolina': 'NC', 'north dakota': 'ND', 'ohio': 'OH',
  'oklahoma': 'OK', 'oregon': 'OR', 'pennsylvania': 'PA', 'rhode island': 'RI', 'south carolina': 'SC',
  'south dakota': 'SD', 'tennessee': 'TN', 'texas': 'TX', 'utah': 'UT', 'vermont': 'VT',
  'virginia': 'VA', 'washington': 'WA', 'west virginia': 'WV', 'wisconsin': 'WI', 'wyoming': 'WY',
  'district of columbia': 'DC',
};
const US_STATE_CODES = new Set(Object.values(US_STATE_MAP));

const CITY_ALIASES: Record<string, { city: string; state: string }> = {
  'nyc': { city: 'New York', state: 'NY' },
  'la': { city: 'Los Angeles', state: 'CA' },
  'sf': { city: 'San Francisco', state: 'CA' },
  'chi': { city: 'Chicago', state: 'IL' },
  'philly': { city: 'Philadelphia', state: 'PA' },
  'atl': { city: 'Atlanta', state: 'GA' },
  'dc': { city: 'Washington', state: 'DC' },
  'bk': { city: 'Brooklyn', state: 'NY' },
  'bklyn': { city: 'Brooklyn', state: 'NY' },
};

const parseAddress = () => {
  const raw = rawAddressInput.value.trim();
  if (!raw) {
    message.warning('请输入需要解析的地址');
    return;
  }

  let address = '';
  let address2 = '';
  let city = '';
  let stateCode = '';
  let zip = '';
  let name = '';
  let phone = '';
  let email = '';
  let remaining = raw;

  // 0. 提取邮箱
  const emailMatch = remaining.match(/\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}\b/i);
  if (emailMatch) {
    email = emailMatch[0].trim();
    remaining = remaining.replace(emailMatch[0], ' ').trim();
  }

  // 1. 提取邮编 (5位数字 或 5+4 格式)
  const zipMatch = remaining.match(/\b(\d{5})(?:[-\s]?(\d{4}))?\b/);
  if (zipMatch) {
    zip = zipMatch[2] ? `${zipMatch[1] || ''}-${zipMatch[2]}` : (zipMatch[1] || '');
    remaining = remaining.replace(zipMatch[0] || '', ' ').trim();
  }

  // 2. 提取公寓号 (Apt, Suite, Unit, # 等)
  const aptPatterns = [
    /(?:,\s*)?(?:apt\.?|apartment|suite|ste\.?|unit|#)\s*([\w-]+)/i,
    /(?:,\s*)?#\s*([\w-]+)/i,
  ];
  for (const pattern of aptPatterns) {
    const aptMatch = remaining.match(pattern);
    if (aptMatch && aptMatch[0]) {
      address2 = aptMatch[0].replace(/^[,\s]+/, '').trim();
      remaining = remaining.replace(aptMatch[0], ' ').trim();
      break;
    }
  }

  // 2.5 提取电话号码 (在去除邮编等信息后更安全)
  const phoneMatch = remaining.match(/(?:tel[:\s]*|phone[:\s]*|P[:\s]*)?(\+?\d{1,3}[\s-]?\d{6,14}\b|\b\d{3}[-.\s]?\d{3}[-.\s]?\d{4}\b|\(\d{3}\)\s*\d{3}[-.\s]?\d{4}\b)/i);
  if (phoneMatch) {
    phone = phoneMatch[0].trim();
    remaining = remaining.replace(phoneMatch[0], ' ').trim();
  }

  // 2.6 提取姓名 (假设名字在街道号之前)
  const streetMatch = remaining.match(/\b(?:P\.O\.\s*Box\s*\d+|\d{1,6}\s+[a-zA-Z])/i);
  if (streetMatch && streetMatch.index !== undefined && streetMatch.index > 0) {
    name = remaining.substring(0, streetMatch.index).trim();
    name = name.replace(/[,/]+$/, '').trim(); // 移除尾部逗号等
    remaining = remaining.substring(streetMatch.index).trim();
  } else {
    // 尝试如果没有街道号，可能是以标点分隔的
    const nameMatch = remaining.match(/^([A-Za-z\s]+)[,/]/);
    if (nameMatch) {
      name = (nameMatch[1] || '').trim();
      remaining = remaining.replace(nameMatch[0] || '', '').trim();
    }
  }

  // 3. 按逗号分割剩余部分
  const parts = remaining.split(/[,]+/).map(p => p.trim()).filter(Boolean);

  if (parts.length >= 3) {
    // 格式: "street, city, state" 或 "street, city, state zip"
    address = parts[0] || '';
    city = parts[1] || '';
    const lastPart = parts[parts.length - 1] || '';
    // 最后部分可能是州或 "州 邮编"
    const stateFromLast = extractState(lastPart);
    if (stateFromLast) {
      stateCode = stateFromLast;
    } else {
      city = parts.slice(1).join(', ');
    }
  } else if (parts.length === 2) {
    address = parts[0] || '';
    // 第二部分可能是 "city state" 或纯城市
    const tokens = (parts[1] || '').split(/\s+/);
    const lastToken = tokens[tokens.length - 1] || '';
    const stateFromToken = extractState(lastToken);
    if (stateFromToken) {
      stateCode = stateFromToken;
      city = tokens.slice(0, -1).join(' ');
    } else {
      city = parts[1] || '';
    }
  } else if (parts.length === 1) {
    // 无逗号 — 尝试从尾部识别州和城市
    const tokens = (parts[0] || '').split(/\s+/);
    let stateIdx = -1;
    // 从后往前找州缩写
    for (let i = tokens.length - 1; i >= 1; i--) {
      const st = extractState(tokens[i] || '');
      if (st) {
        stateCode = st;
        stateIdx = i;
        break;
      }
    }
    if (stateIdx > 0) {
      // 州前面那个词是城市，再之前是地址
      const cityAlias = (tokens[stateIdx - 1] || '').toLowerCase();
      const aliasInfo = CITY_ALIASES[cityAlias];
      if (aliasInfo) {
        city = aliasInfo.city;
        if (!stateCode) stateCode = aliasInfo.state;
        address = tokens.slice(0, stateIdx - 1).join(' ');
      } else {
        city = tokens[stateIdx - 1] || '';
        address = tokens.slice(0, stateIdx - 1).join(' ');
      }
    } else {
      // 没找到州，尝试城市别名
      for (let i = tokens.length - 1; i >= 1; i--) {
        const alias = (tokens[i] || '').toLowerCase();
        const aliasInfo = CITY_ALIASES[alias];
        if (aliasInfo) {
          city = aliasInfo.city;
          stateCode = aliasInfo.state;
          address = tokens.slice(0, i).join(' ');
          break;
        }
      }
      if (!city) {
        address = parts[0] || '';
      }
    }
  }

  // 4. 填充表单
  createForm.countryCode = 'US';
  if (address) createForm.address = address;
  if (address2) createForm.address2 = address2;
  if (city) createForm.city = city;
  if (zip) createForm.zip = zip;
  if (email) createForm.email = email;
  
  if (name) {
    const nameParts = name.split(/\s+/);
    if (nameParts.length > 1) {
      createForm.firstName = nameParts[0] || '';
      createForm.lastName = nameParts.slice(1).join(' ');
    } else {
      createForm.firstName = name;
      createForm.lastName = '';
    }
  }

  if (phone) {
    // 移除前缀标签
    let cleanPhone = phone.replace(/^(?:tel|phone|ph|P)[:\s]*/i, '').trim();
    // 尝试分离国家代码 (+1 等)
    const prefixMatch = cleanPhone.match(/^(\+\d{1,3})\s*(.*)/);
    if (prefixMatch) {
      createForm.phonePrefix = prefixMatch[1] || '';
      createForm.phone = prefixMatch[2] || '';
    } else {
      createForm.phone = cleanPhone;
    }
  }

  // 州匹配到 provinceCode
  if (stateCode) {
    // 需要在省份加载后匹配
    const trySetProvince = () => {
      const match = provinceOptions.value.find(
        p => p.provinceCode === stateCode || p.provinceName?.toUpperCase() === stateCode
      );
      if (match) {
        createForm.provinceCode = match.provinceCode;
      }
    };
    if (provinceOptions.value.length > 0) {
      trySetProvince();
    } else {
      // 等省份加载完成后设置
      const stopWatch = watch(provinceOptions, (val) => {
        if (val.length > 0) {
          trySetProvince();
          stopWatch();
        }
      });
    }
  }

  message.success('地址解析完成');
};

const extractState = (text: string): string => {
  if (!text) return '';
  const upper = text.toUpperCase().replace(/[.,]+$/, '');
  if (US_STATE_CODES.has(upper)) return upper;
  const lower = text.toLowerCase().replace(/[.,]+$/, '');
  if (US_STATE_MAP[lower]) return US_STATE_MAP[lower];
  return '';
};

const createForm = reactive({
  storeId: undefined as number | undefined,
  influencer: undefined as string | undefined,
  influencerId: undefined as number | undefined,
  // 地址字段 - 和 Shopify 保持一致
  firstName: '',
  lastName: '',
  company: '',
  address: '',
  address2: '',
  city: '',
  provinceCode: '' as string, // 省份代码 (如 'GD', 'CA')
  countryCode: 'US' as string, // 国家代码
  zip: '',
  phone: '',
  email: '',
  note: '',
  tags: [] as string[],
  products: [] as any[],
  phonePrefix: '+1' // Default prefix
});

// 国家代码选项列表
const countryOptions = ref<Array<{ code: string; name: string; phonePrefix?: string }>>([]);
const countryLoading = ref(false);

// 省份选项列表 (从后端动态加载)
const provinceOptions = ref<Province[]>([]);
const provinceLoading = ref(false);

const provinceLabel = computed(() => {
  const code = createForm.countryCode;
  if (code === 'CN') return '省 (Province)';
  if (code === 'US' || code === 'CA' || code === 'AU') return '州 (State)';
  if (code === 'JP') return '都道府县';
  return '省/州 (Province/State)';
});


// 监听国家变化自动更新电话前缀
watch(() => createForm.countryCode, (newCode) => {
  const country = countryOptions.value.find(c => c.code === newCode);
  if (country && country.phonePrefix) {
    createForm.phonePrefix = '+' + country.phonePrefix.replace(/^\+/, '');
  }
});

// 获取选中的省份名称
const selectedProvinceName = computed(() => {
  if (!createForm.provinceCode) return undefined;
  const found = provinceOptions.value.find(p => p.provinceCode === createForm.provinceCode);
  return found?.provinceName;
});

const selectedCountryName = computed(() => {
  if (!createForm.countryCode) return undefined;
  const found = countryOptions.value.find(c => c.code === createForm.countryCode);
  return found?.name;
});

// 监听国家变化加载省份
watch(() => createForm.countryCode, async (newCode) => {
  createForm.provinceCode = ''; // 清空已选省份
  provinceOptions.value = [];
  
  if (newCode) {
    provinceLoading.value = true;
    try {
      const list = await getProvinces(newCode);
      provinceOptions.value = list;
    } catch (e) {
      console.error('Failed to load provinces:', e);
    } finally {
      provinceLoading.value = false;
    }
  }
}, { immediate: true });

// 根据 IP 检测国家
const detectCountryByIP = async () => {
  try {
    // 使用免费的 IP 地理位置 API
    const response = await fetch('https://ipapi.co/json/');
    if (response.ok) {
      const data = await response.json();
      const detectedCode = data.country_code;
      // 检查检测到的国家是否在可选列表中
      if (detectedCode && countryOptions.value.some(c => c.code === detectedCode)) {
        createForm.countryCode = detectedCode;
        console.log('Auto-detected country:', detectedCode);
      }
    }
  } catch (error) {
    console.warn('Failed to detect country by IP, using default:', error);
  }
};

// 加载国家列表
const loadCountries = async () => {
  countryLoading.value = true;
  try {
    const countries = await getEnabledCountries();
    countryOptions.value = countries.map(c => ({ 
      code: c.code, 
      name: c.nameCn,
      phonePrefix: c.phonePrefix 
    }));
    // 加载完国家列表后，尝试根据 IP 设置默认国家
    detectCountryByIP();
  } catch (error) {
    console.error('Failed to load countries:', error);
    // 回退到默认常用国家
    countryOptions.value = [
      { code: 'US', name: '美国', phonePrefix: '1' },
      { code: 'CN', name: '中国', phonePrefix: '86' },
      { code: 'GB', name: '英国', phonePrefix: '44' },
    ];
  } finally {
    countryLoading.value = false;
  }
};

// 国家下拉搜索过滤函数
const filterCountryOption = (input: string, option: any) => {
  const searchText = input.toLowerCase();
  const code = option.value?.toLowerCase() || '';
  const name = countryOptions.value.find(c => c.code === option.value)?.name?.toLowerCase() || '';
  return code.includes(searchText) || name.includes(searchText);
};

// Load stores and countries on mount
onMounted(async () => {
  // 并行加载国家列表和红人列表
  loadCountries();
  handleSearchInfluencer('');
  
  // 先加载店铺，完成后再回填（确保 a-select 能找到对应 label）
  storesLoading.value = true;
  try {
    console.log('SampleCreateModal: Starting to load stores...');
    const response = await getShopifyStores({}) as any;
    console.log('SampleCreateModal: getShopifyStores response:', response);
    
    if (response && response.content) {
      stores.value = response.content;
      console.log(`SampleCreateModal: Loaded ${stores.value.length} stores from response.content`);
    } else if (Array.isArray(response)) {
      stores.value = response;
      console.log(`SampleCreateModal: Loaded ${stores.value.length} stores from array response`);
    } else {
      console.warn('SampleCreateModal: Unexpected response structure:', response);
      stores.value = [];
    }
  } catch (e: any) {
    if (axios.isCancel(e)) {
      console.log('SampleCreateModal: Store load request canceled (expected behavior)');
      return;
    }
    console.error('SampleCreateModal: Failed to load stores!', e);
    const errorMsg = e.response?.data?.message || e.message || '未知错误';
    message.error(`无法加载店铺列表: ${errorMsg}`);
  } finally {
    if (storesLoading.value) {
      storesLoading.value = false;
    }
  }

  // 店铺加载完成后再回填（editData 模式）或自动选择单店铺
  if (props.editData) {
    await populateForm(props.editData);
  } else if (stores.value.length === 1 && stores.value[0]) {
    createForm.storeId = stores.value[0].id;
  }
});

// 抽取回填逻辑为独立函数
const populateForm = async (newVal: any) => {
  if (newVal) {
    // 店铺：直接设置（调用方确保 stores 已加载完毕）
    createForm.storeId = newVal.storeId;

    // 红人：先搜索加载到 options，再设置 influencerId
    if (newVal.influencerId && newVal.influencerName) {
      // 确保 influencerOptions 中有该红人
      const exists = searchedInfluencers.value.find(i => i.id === newVal.influencerId);
      if (!exists) {
        // 搜索该红人名称，加载到 options
        influencerLoading.value = true;
        try {
          const response = await influencerService.getList({
            searchKey: newVal.influencerName,
            page: 0,
            size: 20
          } as any);
          searchedInfluencers.value = response.content || [];
        } catch (e) {
          console.error('Failed to search influencer', e);
        } finally {
          influencerLoading.value = false;
        }
      }
      createForm.influencerId = newVal.influencerId;
      createForm.influencer = newVal.influencerName;

      // 加载该红人的地址列表
      addressLoading.value = true;
      influencerService.getAddresses(newVal.influencerId).then((addresses: any) => {
        influencerAddresses.value = (addresses || []) as InfluencerAddress[];
      }).catch((e: any) => {
        console.error('Failed to load influencer addresses', e);
      }).finally(() => {
        addressLoading.value = false;
      });
    }

    createForm.email = (newVal.customerEmail && newVal.customerEmail !== '-') ? newVal.customerEmail : '';
    
    // Address mapping
    if (newVal.recipientName) {
      const parts = newVal.recipientName.split(' ');
      createForm.firstName = parts[0] || '';
      createForm.lastName = parts.slice(1).join(' ') || '';
    }
    
    createForm.address = (newVal.recipientAddress && newVal.recipientAddress !== '-') ? newVal.recipientAddress : '';
    createForm.countryCode = (newVal.recipientCountry && newVal.recipientCountry !== '-') ? newVal.recipientCountry : 'US';
    
    if (newVal.recipientPhone && newVal.recipientPhone !== '-') {
      if (newVal.recipientPhone.startsWith('+')) {
        const match = newVal.recipientPhone.match(/^\+(\d+)\s*(.*)/);
        if (match) {
          createForm.phonePrefix = '+' + match[1];
          createForm.phone = match[2] || '';
        } else {
          createForm.phone = newVal.recipientPhone;
        }
      } else {
        createForm.phone = newVal.recipientPhone;
      }
    }
    
    createForm.note = newVal.note || '';
    
    if (newVal.tags) {
      createForm.tags = newVal.tags.split(',').filter(Boolean);
    } else {
      createForm.tags = [];
    }

    // Products mapping
    if (newVal.products && newVal.products.length > 0) {
      createForm.products = newVal.products.map((p: any) => ({
        id: p.shopifyVariantId || p.variantId,
        variantId: p.variantId,
        shopifyVariantId: p.shopifyVariantId,
        title: p.title,
        variant: p.variantTitle,
        sku: p.sku,
        price: parseFloat(p.price || '0'),
        quantity: p.quantity,
        image: p.imageUrl || p.image
      }));
    } else {
      createForm.products = [];
    }
  } else {
    // Reset form for create mode
    Object.assign(createForm, {
      storeId: (stores.value && stores.value.length === 1 && stores.value[0]) ? stores.value[0].id : undefined,
      influencer: undefined,
      influencerId: undefined,
      firstName: '',
      lastName: '',
      company: '',
      address: '',
      address2: '',
      city: '',
      provinceCode: '',
      countryCode: 'US',
      zip: '',
      phone: '',
      email: '',
      note: '',
      tags: [],
      products: [],
      phonePrefix: '+1'
    });
    influencerAddresses.value = [];
    selectedAddressId.value = undefined;
    duplicateWarning.value = null;
  }
};

// Watch editData 变化（弹窗已打开时切换编辑数据，不用 immediate 因为 onMounted 已处理首次回填）
watch(() => props.editData, (newVal) => {
  if (props.open) {
    populateForm(newVal);
  }
});

// 红人选择变化时检测重复订单
const handleInfluencerChange = async (val: any) => {
  duplicateWarning.value = null;
  duplicateCheckResult.value = null;
  influencerAddresses.value = [];
  selectedAddressId.value = undefined;

  if (!val) {
    createForm.influencer = undefined;
    createForm.influencerId = undefined;
    return;
  }

  // Find influencer in searched list or fallback to prop
  const inf = searchedInfluencers.value.find(i => i.id === val || i.realName === val);
  if (inf) {
    createForm.influencer = inf.realName;
    createForm.influencerId = inf.id;
    
    // Fetch addresses
    addressLoading.value = true;
    try {
      const addresses = await influencerService.getAddresses(inf.id) as any;
      influencerAddresses.value = (addresses || []) as InfluencerAddress[];
      // Auto-select default address if exists
      const defaultAddr = (influencerAddresses.value).find(a => a.isDefault);
      if (defaultAddr) {
        handleAddressSelect(defaultAddr.id);
      }
    } catch (e) {
      console.error('Failed to load influencer addresses', e);
    } finally {
      addressLoading.value = false;
    }

    // Check duplicates
    try {
      const result = await checkDuplicateSampleOrder(inf.id);
      duplicateCheckResult.value = result;
      if (result.hasDuplicate && result.lastOrderDate) {
        const date = new Date(result.lastOrderDate);
        const dateStr = `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日`;
        duplicateWarning.value = `${dateStr} 已为该红人创建过订单，是否确认为其再次下单？`;
      }
    } catch (e) {
      console.error('Failed to check duplicate', e);
    }
  } else {
    // Fallback for simple string input from props
    createForm.influencer = val;
    createForm.influencerId = undefined;
  }
};

const handleSearchInfluencer = async (query: string) => {
  influencerLoading.value = true;
  try {
    const response = await influencerService.getList({ 
      searchKey: query || '',
      stage: 'ONBOARDED', // Show onboarded influencers by default
      page: 0,
      size: 50 
    } as any);
    searchedInfluencers.value = response.content || [];
  } catch (e) {
    console.error('Search influencer failed', e);
  } finally {
    influencerLoading.value = false;
  }
};

const handleAddressSelect = (addressId: number) => {
  const addr = influencerAddresses.value.find(a => a.id === addressId);
  if (addr) {
    selectedAddressId.value = addressId;
    
    // Split name if possible
    const nameParts = (addr.recipientName || '').split(' ');
    createForm.firstName = (nameParts[0] || '').toString();
    createForm.lastName = (nameParts.slice(1).join(' ') || '').toString();
    
    createForm.address = addr.address;
    createForm.zip = addr.postalCode || '';
    createForm.email = addr.email || '';
    
    // Phone processing
    if (addr.phone) {
      // Remove prefix if already combined in addr.phone (assuming + prefix)
      if (addr.phone.startsWith('+')) {
        const match = addr.phone.match(/^\+(\d+)\s*(.*)/);
        if (match) {
          createForm.phonePrefix = '+' + match[1];
          createForm.phone = match[2] || '';
        } else {
          createForm.phone = addr.phone;
        }
      } else {
        createForm.phone = addr.phone;
      }
    }

    if (addr.country) {
      // Try to find matching country code (this might need a map or backend support)
      // For now, if it's 2 chars, assume it's the code
      if (addr.country.length === 2) {
        createForm.countryCode = addr.country.toUpperCase();
      }
    }
  }
};

const openProductSelector = () => {
  // Pre-select existing products
  selectedProductKeys.value = createForm.products.map(p => p.id);
  productSelectorVisible.value = true;
};

const handleProductSelected = (selectedProducts: any[]) => {
  // Merge with existing to preserve quantities
  const mergedProducts = selectedProducts.map(p => {
    const existing = createForm.products.find(ep => ep.id === p.id);
    return existing ? existing : { ...p, quantity: 1 };
  });
  
  createForm.products = mergedProducts;
};

const removeProduct = (index: number) => {
  showDeleteConfirm({
    title: '确定要移除该商品吗？',
    content: '移除后需要重新选择该商品。',
    onOk: () => {
      createForm.products.splice(index, 1);
      message.success('已移除商品');
    },
  });
};

const handleSubmitCreate = async (isDraft: boolean = false) => {
  // Validation
  if (!createForm.storeId) {
    message.warning('请选择目标店铺');
    return;
  }
  if (!createForm.influencer) {
    message.warning('请选择关联红人');
    return;
  }
  if (!createForm.firstName || !createForm.lastName) {
    message.warning('请输入收件人姓名');
    return;
  }
  if (!createForm.address) {
    message.warning('请输入收货地址');
    return;
  }
  if (createForm.products.length === 0) {
    message.warning('请至少选择一个商品');
    return;
  }

  // Inventory Check (Skip for drafts or edits)
  if (!isDraft && !props.editData) {
    const insufficientProducts = createForm.products.filter(p => p.quantity > (p.inventory || 0));
    if (insufficientProducts.length > 0) {
      const names = insufficientProducts.map(p => p.title).join(', ');
      message.error(`部分商品库存不足: ${names}`);
      return;
    }
  }
  
  // Build request
  const request: any = {
    storeId: createForm.storeId,
    influencerId: createForm.influencerId || undefined,
    customerEmail: createForm.email || undefined,
    customerPhone: (createForm.phonePrefix || '') + (createForm.phone || ''),
    financialStatus: isDraft ? 'PENDING' : 'PAID',
    fulfillmentStatus: 'UNFULFILLED',
    isDraft: isDraft,
    lineItems: createForm.products.map(p => ({
      variantId: p.variantId || p.id,
      shopifyVariantId: p.shopifyVariantId || p.variantId,
      quantity: p.quantity || 1,
      requiresShipping: true,
    })),
    shippingAddress: {
      firstName: createForm.firstName,
      lastName: createForm.lastName,
      company: createForm.company || undefined,
      address1: createForm.address,
      address2: createForm.address2 || undefined,
      city: createForm.city || undefined,
      province: selectedProvinceName.value || undefined,
      provinceCode: createForm.provinceCode || undefined,
      country: selectedCountryName.value || undefined,
      countryCode: createForm.countryCode || 'US',
      zip: createForm.zip || undefined,
      phone: (createForm.phonePrefix || '') + (createForm.phone || ''),
    },
    note: createForm.note || undefined,
    tags: createForm.tags.join(','),
    orderSource: 'influencer'
  };
  
  submitting.value = true;
  try {
    let response;
    if (props.editData) {
      // Editing existing draft
      response = await updateShopifyOrder(props.editData.orderId, request, { showSuccess: false } as any);
    } else {
      // Creating new order/draft
      response = await createInfluencerOrder(request, { showSuccess: false } as any);
    }
    
    if (response.success) {
      // 若操作成功，且红人有关联，将新地址保存至红人地址本（仅当不重复时）
      if (createForm.influencerId && createForm.address) {
        const addrName = `${createForm.firstName} ${createForm.lastName}`.trim();
        const isDuplicate = influencerAddresses.value.some(addr => {
          // 这里可以进行模糊匹配或者精准匹配
          return addr.address === createForm.address && 
                 (addr.recipientName || '').trim() === addrName;
        });

        if (!isDuplicate) {
          try {
            await influencerService.addAddress(createForm.influencerId, {
              recipientName: addrName,
              address: createForm.address,
              postalCode: createForm.zip || undefined,
              phone: (createForm.phonePrefix || '') + (createForm.phone || ''),
              email: createForm.email || undefined,
              country: createForm.countryCode
            } as any);
          } catch (e) {
            console.warn('Failed to auto-save address to influencer address book', e);
          }
        }
      }

      message.success(props.editData ? '草稿已更新' : (isDraft ? '草稿已保存' : '订单已创建'));
      emit('success', response);
      visible.value = false;
    } else {
      message.error(`操作失败: ${response.message}`);
    }
  } catch (error: any) {
    message.error(`操作出现错误: ${error.message || '未知错误'}`);
  } finally {
    submitting.value = false;
  }
};
</script>

<style lang="scss" scoped>
// Sample Create Modal Styles
.sample-create-modal {
  :deep(.ant-modal-content) { padding: 0; border-radius: 20px; overflow: hidden; }
  
  .sc-modal-header {
    padding: 32px 24px;
    background: linear-gradient(135deg, #1e293b 0%, #334155 100%);
    color: white;
    
    .sc-header-left {
      display: flex;
      align-items: center;
      gap: 20px;
      
      .sc-header-icon {
        width: 60px; height: 60px;
        border-radius: 16px;
        background: rgba(255, 255, 255, 0.1);
        backdrop-filter: blur(10px);
        display: flex; align-items: center; justify-content: center;
        font-size: 32px; font-weight: bold;
      }
      
      .sc-header-title { font-size: 22px; font-weight: 800; margin-bottom: 4px; }
      .sc-header-subtitle { opacity: 0.7; font-size: 14px; }
    }
  }
  
  .sc-modal-body {
    padding: 24px;
    background: #f8fafc;
    max-height: 60vh;
    overflow-y: auto;
    
    .sc-section {
      background: #fff;
      border-radius: 16px;
      padding: 20px;
      margin-bottom: 16px;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.02);
      
      .sc-section-header {
        display: flex; justify-content: space-between; align-items: center;
        margin-bottom: 16px;
        .sc-section-title { font-size: 16px; font-weight: 700; color: #1e293b; border-left: 4px solid #3b82f6; padding-left: 12px; }
      }
      
      .product-row {
        padding: 16px; background: #f8fafc; border-radius: 12px; margin-bottom: 12px; border: 1px solid #e2e8f0;
      }
    }
  }
  
  .sc-modal-footer {
    padding: 16px 24px; background: #fff; border-top: 1px solid #e2e8f0; text-align: right;
  }

  // Address Quick Select Styles
  .address-quick-select-col {
    margin-bottom: 24px;
    
    .address-quick-select-header {
      display: flex; align-items: center; gap: 8px; margin-bottom: 12px;
      .label { font-size: 13px; font-weight: 600; color: #64748b; }
    }
    
    .address-cards-container {
      display: flex; gap: 12px; overflow-x: auto; padding-bottom: 8px;
      scrollbar-width: thin;
      height: 120px; /* Fixed height to prevent layout shift */
      align-items: center;
      
      &::-webkit-scrollbar { height: 4px; }
      &::-webkit-scrollbar-thumb { background: #e2e8f0; border-radius: 4px; }
      
      .address-loading-state {
        width: 100%; text-align: center;
      }

      .address-mini-card {
        flex: 0 0 240px; border: 2px solid #e2e8f0; border-radius: 12px;
        height: 96px; /* Fixed height for card */
        padding: 12px; background: #fff; cursor: pointer; position: relative;
        transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
        display: flex; flex-direction: column; justify-content: space-between;
        
        &:hover { border-color: #cbd5e1; transform: translateY(-2px); box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05); }
        &.active { border-color: #3b82f6; background: #f0f7ff;
          .card-header .name { color: #2563eb; }
        }
        
        .card-header {
          display: flex; justify-content: space-between; align-items: center; margin-bottom: 4px;
          .name { font-weight: 700; color: #1e293b; font-size: 14px; }
          .text-ellipsis { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 140px; }
        }
        
        .card-body {
          .addr-text { font-size: 11px; color: #64748b; display: -webkit-box; -webkit-line-clamp: 2; line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
        }
        
        .card-check {
          position: absolute; top: 8px; right: 8px; width: 18px; height: 18px;
          background: #3b82f6; color: white; border-radius: 50%; display: flex;
          align-items: center; justify-content: center; font-size: 10px; font-weight: bold;
          box-shadow: 0 2px 4px rgba(59, 130, 246, 0.2);
          z-index: 10;
          border: 1.5px solid #fff;
        }
      }

      .address-empty-state {
        width: 100%; height: 96px; padding: 16px; background: #f1f5f9; border: 1px dashed #cbd5e1;
        border-radius: 12px; color: #94a3b8; font-size: 12px; text-align: center;
        display: flex; align-items: center; justify-content: center;
      }
    }
  }

  // Address Parser Styles
  .address-parser-item {
    margin-bottom: 8px !important;
    
    .address-parser-wrapper {
      .parser-hint {
        margin-top: 4px;
        font-size: 11px;
        color: #94a3b8;
        line-height: 1.4;
      }
    }
  }
}
</style>

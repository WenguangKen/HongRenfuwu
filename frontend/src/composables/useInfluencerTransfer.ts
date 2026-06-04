/**
 * 红人列表流转操作 Composable
 *
 * 职责：
 * - 单个/批量状态流转 (handleTransfer / batchMove)
 * - 流转选项映射 (getTransferOptions)
 * - 休眠规则校验
 * - 流转动画状态管理
 *
 * @module useInfluencerTransfer
 */
import { ref, type Ref } from 'vue';
import { message, Modal } from 'ant-design-vue';
import { influencerService } from '@/services/influencerService';
import type { Influencer } from '@/types/influencer';
import type { MenuInfo } from 'ant-design-vue/es/menu/src/interface';

/** 状态显示名称映射 */
const statusMap: Record<string, string> = {
  pending: '待联系',
  contacted: '已联系',
  communicating: '沟通中',
  cooperating: '合作中',
  dormant: '休眠',
  paused: '暂停',
  blacklist: '黑名单',
  terminated: '终止'
};

/** 需要填写原因的状态 */
const statusRequireReason = ['paused', 'blacklist', 'terminated'];

export function useInfluencerTransfer(
  activeKey: Ref<string>,
  selectedRowKeys: Ref<(number | string)[]>,
  displayData: Ref<Influencer[]>,
  fetchData: () => Promise<void>
) {
  // --- Transfer State ---
  const reasonModalVisible = ref(false);
  const reasonModalTitle = ref('');
  const pendingTransfer = ref<{ record: Influencer, status: string, statusText: string, targetStage?: string } | null>(null);
  const batchReasonModalVisible = ref(false);
  const batchReasonModalTitle = ref('');
  const batchReasonItems = ref<{id: string, name: string}[]>([]);
  const pendingBatchTransfer = ref<{ status: string; statusText: string; targetStage?: string } | null>(null);
  const transferringIds = ref<number[]>([]);

  /** 行 CSS 类名（流转动画） */
  const getRowClassName = (record: any) => {
    return transferringIds.value.includes(record.id) ? 'transfer-row-leave' : '';
  };

  /** 获取流转目标选项（排除当前状态） */
  const getTransferOptions = (currentKey: string): {key: string, label: string, targetStage?: string}[] => {
    const allOptions: {key: string, label: string, targetStage?: string}[] = [
      { key: 'UNSCREENED', label: '资源池-待筛选', targetStage: 'POOL' },
      { key: 'REJECTED', label: '资源池-暂不合适', targetStage: 'POOL' },
      { key: 'PENDING', label: '待联系' },
      { key: 'CONTACTED', label: '已联系' },
      { key: 'COMMUNICATING', label: '沟通中' },
      { key: 'COOPERATING', label: '合作中' },
      { key: 'DORMANT', label: '休眠中' },
      { key: 'PAUSED', label: '暂不合作' },
      { key: 'BLACKLIST', label: '黑名单' },
      { key: 'TERMINATED', label: '不再合作' },
    ];
    return allOptions.filter(opt => opt.key.toLowerCase() !== currentKey.toLowerCase());
  };

  /** 执行单个流转 */
  const executeTransfer = async (id: number, status: string, reason?: string, targetStage?: string) => {
    if (!transferringIds.value.includes(id)) {
      transferringIds.value.push(id);
    }
    try {
      const apiPromise = influencerService.batchChangeStatus({
        ids: [Number(id)],
        targetStatus: status.toUpperCase(),
        targetStage: targetStage?.toUpperCase() as any,
        reason
      });
      const animationPromise = new Promise(resolve => setTimeout(resolve, 500));
      const [result] = await Promise.all([apiPromise, animationPromise]);

      if (result && result.failedItems && result.failedItems.length > 0) {
        const failMsg = result.failedItems.map((item: any) => item.errorMessage || item.message || '未知原因').join(', ');
        message.error(`部分流转失败: ${failMsg}`);
      } else {
        message.success('流转成功');
      }
      fetchData();
    } catch (e: any) {
      console.error('Transfer failed:', e);
      message.error(e.response?.data?.message || e.message || '网络请求失败，请稍后重试');
    } finally {
      transferringIds.value = transferringIds.value.filter(tid => Number(tid) !== Number(id));
    }
  };

  /** 处理流转菜单点击 */
  const handleTransfer = async (e: MenuInfo, record: Influencer) => {
    const targetStatus = e.key as string;
    const options = getTransferOptions(activeKey.value);
    const selectedOption = options.find(opt => opt.key === targetStatus);
    const targetStage = selectedOption?.targetStage;
    const label = selectedOption?.label || statusMap[targetStatus.toLowerCase()] || targetStatus;

    // 休眠规则检查
    if (targetStatus === 'DORMANT') {
      try {
        message.loading({ content: '检查休眠规则...', key: 'checkDormancy' });
        const result = await influencerService.checkDormancy(record.id);
        message.destroy('checkDormancy');
        if (!result.eligible) {
          Modal.confirm({
            title: '不符合休眠规则',
            content: `该红人目前不符合自动休眠条件 (${result.message})。是否确认强制移入休眠？`,
            okText: '强制移入',
            cancelText: '取消',
            onOk: () => executeTransfer(record.id, targetStatus, `Manual Force: ${result.message}`, targetStage)
          });
          return;
        }
      } catch (err) {
        message.destroy('checkDormancy');
        Modal.confirm({
          title: '规则检查失败',
          content: '无法验证休眠规则。是否强制移入？',
          okText: '强制移入',
          cancelText: '取消',
          onOk: () => executeTransfer(record.id, targetStatus, 'Manual Force: Check Failed', targetStage)
        });
        return;
      }
    }

    // 需要输入原因的状态流转
    if (statusRequireReason.includes(targetStatus.toLowerCase())) {
      pendingTransfer.value = { record, status: targetStatus, statusText: label, targetStage };
      reasonModalTitle.value = `流转至 ${label}`;
      reasonModalVisible.value = true;
    } else {
      executeTransfer(record.id, targetStatus, undefined, targetStage);
    }
  };

  /** 确认流转（带原因） */
  const handleConfirmTransfer = (reason: string) => {
    if (pendingTransfer.value) {
      const { record, status, targetStage } = pendingTransfer.value as any;
      executeTransfer(record.id, status, reason, targetStage);
      reasonModalVisible.value = false;
      pendingTransfer.value = null;
    }
  };

  /** 执行批量流转 */
  const executeBatchTransfer = async (status: string, reason?: string, targetStage?: string) => {
    try {
      const result = await influencerService.batchChangeStatus({
        ids: selectedRowKeys.value.map(k => Number(k)),
        targetStatus: status.toUpperCase(),
        targetStage: targetStage?.toUpperCase() as any,
        reason
      });
      if (result && result.failedItems && result.failedItems.length > 0) {
        const failMsg = result.failedItems.map((item: any) => item.errorMessage || item.message || '未知原因').join(', ');
        message.error(`部分操作失败: ${failMsg}`);
      } else {
        message.success('批量操作成功');
      }
      selectedRowKeys.value = [];
      fetchData();
    } catch (e: any) {
      console.error('Batch transfer failed:', e);
      message.error(e.response?.data?.message || e.message || '批量流转失败');
    }
  };

  /** 批量流转入口 */
  const batchMove = (status: string) => {
    if (!selectedRowKeys.value.length) return message.warn('请选择红人');
    const label = statusMap[status] || status;
    const options = getTransferOptions(activeKey.value);
    const selectedOption = options.find(opt => opt.key === status);
    const targetStage = selectedOption?.targetStage;

    if (statusRequireReason.includes(status)) {
      pendingBatchTransfer.value = { status, statusText: label, targetStage };
      batchReasonModalTitle.value = `批量流转至 ${label}`;
      batchReasonItems.value = displayData.value
        .filter(d => selectedRowKeys.value.includes(d.id))
        .map(d => ({ id: String(d.id), name: d.realName || d.nickName || '' }));
      batchReasonModalVisible.value = true;
    } else {
      executeBatchTransfer(status, undefined, targetStage);
    }
  };

  /** 确认批量流转 */
  const handleConfirmBatchTransfer = () => {
    if (pendingBatchTransfer.value) {
      const { status, targetStage } = pendingBatchTransfer.value as any;
      executeBatchTransfer(status, 'Batch Operation', targetStage);
      batchReasonModalVisible.value = false;
    }
  };

  return {
    // State
    reasonModalVisible,
    reasonModalTitle,
    pendingTransfer,
    batchReasonModalVisible,
    batchReasonModalTitle,
    batchReasonItems,
    pendingBatchTransfer,
    transferringIds,
    statusMap,
    statusRequireReason,
    // Methods
    getRowClassName,
    getTransferOptions,
    handleTransfer,
    handleConfirmTransfer,
    executeTransfer,
    batchMove,
    handleConfirmBatchTransfer,
    executeBatchTransfer
  };
}

import { createVNode } from 'vue';
import { Modal } from 'ant-design-vue';
import { ExclamationCircleFilled } from '@ant-design/icons-vue'; // 使用实心图标更有质感

export const showDeleteConfirm = (options: {
  title?: string;
  content?: string;
  zIndex?: number;
  onOk?: () => void | Promise<void>;
  onCancel?: () => void;
}) => {
  const { title = '确定要删除吗？', content = '删除后数据将无法恢复，请谨慎操作。', zIndex = 2000, onOk, onCancel } = options;

  Modal.confirm({
    title: title,
    // 不再直接设置 style color，而是通过 class 控制，或者保留基础颜色
    icon: createVNode(ExclamationCircleFilled), 
    content: content,
    okText: '确认删除',
    cancelText: '取消',
    okType: 'danger',
    centered: true,
    maskClosable: true,
    class: 'refined-confirm-modal', // 添加样式类名
    width: 420, // 稍微宽一点，大气
    zIndex: zIndex, // 确保弹窗在所有层级之上
    onOk,
    onCancel,
  });
};

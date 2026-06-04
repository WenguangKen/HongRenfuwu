<template>
  <div class="tabs-wrapper" v-if="tabsStore.visitedViews.length">
    <div class="tabs-container" ref="scrollContainerRef">
      <div class="tabs-scroll" ref="scrollRef">
        <div
          v-for="(tab, index) in tabsStore.visitedViews"
          :key="tab.path"
          class="tab-item"
          :class="{ 
            active: tab.path === route.path,
            dragging: draggedIndex === index,
            'drag-over': dragOverIndex === index && draggedIndex !== index,
            'drag-over-before': dragOverIndex === index && draggedIndex !== index && dragInsertBefore,
            'drag-over-after': dragOverIndex === index && draggedIndex !== index && !dragInsertBefore,
            closing: closingTabPath === tab.path
          }"
          draggable="true"
          @click="handleClick(tab, $event)"
          @dragstart="handleDragStart(index, $event)"
          @dragenter.prevent="handleDragEnter(index, $event)"
          @dragover.prevent="handleDragOver(index, $event)"
          @dragleave="handleDragLeave"
          @drop="handleDrop(index, $event)"
          @dragend="handleDragEnd"
        >
          <span class="tab-title">{{ getDisplayTitle(tab) }}</span>
          <span 
            class="tab-close" 
            @click.stop="handleClose(tab)"
            @mousedown.stop
            draggable="false"
          >
            <close-outlined />
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, nextTick, onMounted, onUnmounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { CloseOutlined } from '@ant-design/icons-vue';
import { useTabsStore } from '@/stores/tabs';

const route = useRoute();
const router = useRouter();
const tabsStore = useTabsStore();
const scrollRef = ref<HTMLElement | null>(null);
const scrollContainerRef = ref<HTMLElement | null>(null);
const draggedIndex = ref<number | null>(null);
const dragOverIndex = ref<number | null>(null);
const dragInsertBefore = ref<boolean>(true);
const dragStartX = ref<number>(0);
const dragStartY = ref<number>(0);
const isDragging = ref(false);
const animationFrameId = ref<number | null>(null);
const autoScrollInterval = ref<number | null>(null);
const closingTabPath = ref<string | null>(null);

// 获取显示的标题（确保只显示标题，不显示路径）
const getDisplayTitle = (tab: (typeof tabsStore.visitedViews)[number]): string => {
  let title: string = tab.title || '';
  
  // 如果标题包含了路径信息（比如 "/influencer/"），清理掉
  // 只保留中文标题或纯文本标题
  if (title.includes('/')) {
    // 如果包含路径和中文，提取中文部分
    const chineseMatch = title.match(/[\u4e00-\u9fa5]+/g);
    if (chineseMatch && chineseMatch.length > 0) {
      title = chineseMatch.join('') || title;
    } else {
      // 如果没有中文，移除路径部分，只保留最后一个路径段的名称
      const pathParts = title.split('/').filter((p): p is string => !!p);
      if (pathParts.length > 0) {
        title = pathParts[pathParts.length - 1] || title;
      }
    }
  }
  
  return title;
};

// 路由变化时自动记录标签 & 缓存
watch(
  () => route.fullPath,
  () => {
    if (route.path !== '/user/login') {
      tabsStore.addView(route);
      nextTick(() => {
        scrollActiveIntoView();
      });
    }
  },
  { immediate: true }
);

const handleClick = (tab: (typeof tabsStore.visitedViews)[number], _event: MouseEvent) => {
  // 如果正在拖拽，不执行点击
  if (isDragging.value) {
    return;
  }
  if (tab.path !== route.path) {
    router.push(tab.fullPath);
  }
};

const handleClose = (tab: (typeof tabsStore.visitedViews)[number]) => {
  // 找到要关闭的标签索引
  const closingIndex = tabsStore.visitedViews.findIndex(v => v.path === tab.path);
  if (closingIndex === -1) return;
  
  // 判断关闭的是否是当前激活的页面
  const isClosingActiveTab = tab.path === route.path;
  
  // 设置关闭状态，触发动画
  closingTabPath.value = tab.path;
  
  // 使用 nextTick 确保 DOM 已更新
  nextTick(() => {
    // 获取要关闭的标签元素
    const tabs = scrollRef.value?.children;
    const closingTab = tabs ? tabs[closingIndex] as HTMLElement : null;
    
    if (closingTab) {
      // 获取标签宽度，用于计算后续标签的移动距离
      const tabWidth = closingTab.offsetWidth + 4; // 4px 是 gap
      
      // 让后续标签平滑向左移动
      if (tabs) {
        Array.from(tabs).forEach((t, index) => {
          const tabEl = t as HTMLElement;
          if (index > closingIndex) {
            // 后面的标签向左移动，填补空隙
            tabEl.style.transform = `translateX(-${tabWidth}px)`;
            tabEl.style.transition = 'transform 0.25s cubic-bezier(0.4, 0, 0.2, 1)';
            tabEl.style.willChange = 'transform';
          }
        });
      }
    }
    
    // 等待动画完成后再真正移除
    setTimeout(() => {
      const next = tabsStore.removeView(tab.path);
      
      // 清除关闭状态
      closingTabPath.value = null;
      
      // 等待 Vue 重新渲染完成后再重置样式，避免闪烁
      nextTick(() => {
        requestAnimationFrame(() => {
          // 重置所有标签的 transform，让 Vue 的自然布局生效
          const updatedTabs = scrollRef.value?.children;
          if (updatedTabs) {
            Array.from(updatedTabs).forEach((t) => {
              const tabEl = t as HTMLElement;
              tabEl.style.transform = '';
              tabEl.style.transition = '';
              tabEl.style.willChange = 'auto';
            });
          }
        });
      });
      
      // 只有当关闭的是当前激活的页面时，才跳转到下一个页面
      // 如果关闭的不是当前激活的页面，保持当前页面不变
      if (isClosingActiveTab && next && next !== route.fullPath) {
        router.push(next);
      }
    }, 250); // 与 CSS 动画时长一致
  });
};

// 滚动到当前激活的标签
const scrollActiveIntoView = () => {
  const container = scrollContainerRef.value;
  const scroll = scrollRef.value;
  if (!container || !scroll) return;
  
  const active = scroll.querySelector<HTMLElement>('.tab-item.active');
  if (!active) return;
  
  const activeLeft = active.offsetLeft;
  const activeRight = activeLeft + active.offsetWidth;
  const visibleLeft = container.scrollLeft;
  const visibleRight = visibleLeft + container.clientWidth;
  
  if (activeLeft < visibleLeft) {
    container.scrollLeft = activeLeft - 8;
  } else if (activeRight > visibleRight) {
    container.scrollLeft = activeRight - container.clientWidth + 8;
  }
};

// 拖拽相关处理
const handleDragStart = (index: number, event: DragEvent) => {
  draggedIndex.value = index;
  isDragging.value = false;
  dragStartX.value = event.clientX;
  dragStartY.value = event.clientY;
  
  if (event.dataTransfer) {
    event.dataTransfer.effectAllowed = 'move';
    event.dataTransfer.setData('text/html', index.toString());
  }
  
  // 延迟设置拖拽状态，避免与点击冲突
  setTimeout(() => {
    if (draggedIndex.value === index) {
      isDragging.value = true;
    }
  }, 100);
};

// 更新拖拽插入位置
const updateDragInsertPosition = (event: DragEvent) => {
  const target = event.currentTarget as HTMLElement;
  if (!target) return;
  
  const rect = target.getBoundingClientRect();
  const mouseX = event.clientX;
  
  // 判断鼠标在左半部还是右半部
  const centerX = rect.left + rect.width / 2;
  dragInsertBefore.value = mouseX < centerX;
};

const handleDragEnter = (index: number, event: DragEvent) => {
  if (draggedIndex.value !== null && draggedIndex.value !== index) {
    dragOverIndex.value = index;
    updateDragInsertPosition(event);
  }
};

// 自动滚动功能：当拖动到容器边缘时自动滚动
const autoScrollOnDrag = (event: DragEvent) => {
  const container = scrollContainerRef.value;
  if (!container) return;
  
  const containerRect = container.getBoundingClientRect();
  const mouseX = event.clientX;
  const scrollThreshold = 50; // 距离边缘50px时开始滚动
  const scrollSpeed = 10; // 滚动速度
  
  // 清除之前的自动滚动
  if (autoScrollInterval.value !== null) {
    clearInterval(autoScrollInterval.value);
    autoScrollInterval.value = null;
  }
  
  // 检查是否需要向左滚动
  if (mouseX < containerRect.left + scrollThreshold && container.scrollLeft > 0) {
    autoScrollInterval.value = window.setInterval(() => {
      if (container.scrollLeft > 0) {
        container.scrollLeft -= scrollSpeed;
      } else {
        if (autoScrollInterval.value !== null) {
          clearInterval(autoScrollInterval.value);
          autoScrollInterval.value = null;
        }
      }
    }, 16); // 约60fps
  } 
  // 检查是否需要向右滚动
  else if (mouseX > containerRect.right - scrollThreshold && 
           container.scrollLeft < container.scrollWidth - container.clientWidth) {
    autoScrollInterval.value = window.setInterval(() => {
      if (container.scrollLeft < container.scrollWidth - container.clientWidth) {
        container.scrollLeft += scrollSpeed;
      } else {
        if (autoScrollInterval.value !== null) {
          clearInterval(autoScrollInterval.value);
          autoScrollInterval.value = null;
        }
      }
    }, 16); // 约60fps
  }
};

const handleDragOver = (index: number, event: DragEvent) => {
  event.preventDefault();
  if (event.dataTransfer) {
    event.dataTransfer.dropEffect = 'move';
  }
  if (draggedIndex.value !== null && draggedIndex.value !== index) {
    dragOverIndex.value = index;
    updateDragInsertPosition(event);
    
    // 自动滚动：当拖动到容器边缘时自动滚动
    autoScrollOnDrag(event);
    
    // 使用 requestAnimationFrame 优化性能，让动画更流畅
    if (animationFrameId.value !== null) {
      cancelAnimationFrame(animationFrameId.value);
    }
    animationFrameId.value = requestAnimationFrame(() => {
      updateTabPositions();
    });
  }
};

// 更新标签位置，实现插入效果
const updateTabPositions = () => {
  if (draggedIndex.value === null || dragOverIndex.value === null) return;
  
  const tabs = scrollRef.value?.children;
  if (!tabs) return;
  
  const fromIndex = draggedIndex.value;
  const overIndex = dragOverIndex.value;
  
  // 计算插入位置
  const insertIndex = dragInsertBefore.value ? overIndex : overIndex + 1;
  
  // 计算每个标签应该移动的距离（基于实际标签宽度）
  const getTabWidth = (index: number) => {
    const tab = tabs[index] as HTMLElement;
    return tab ? tab.offsetWidth + 4 : 0; // 4px 是 gap
  };
  
  const draggedTabWidth = getTabWidth(fromIndex);
  
  // 为每个标签添加过渡效果，使用更标准的平滑缓动
  Array.from(tabs).forEach((tab, index) => {
    const tabEl = tab as HTMLElement;
    
    if (index === fromIndex) {
      // 被拖拽的标签
      tabEl.style.transform = 'scale(0.96) translateY(-2px)';
      tabEl.style.opacity = '0.3';
      tabEl.style.transition = 'all 0.25s ease-out';
      tabEl.style.zIndex = '1000';
      tabEl.style.willChange = 'transform, opacity';
    } else if (fromIndex < insertIndex) {
      // 从前往后拖拽
      if (index > fromIndex && index < insertIndex) {
        // 中间的标签向左移动
        tabEl.style.transform = `translateX(-${draggedTabWidth}px)`;
        tabEl.style.transition = 'transform 0.2s cubic-bezier(0.4, 0, 0.2, 1)';
      } else {
        tabEl.style.transform = '';
        tabEl.style.transition = 'transform 0.2s ease-out';
      }
    } else {
      // 从后往前拖拽
      if (index >= insertIndex && index < fromIndex) {
        // 中间的标签向右移动
        tabEl.style.transform = `translateX(${draggedTabWidth}px)`;
        tabEl.style.transition = 'transform 0.2s cubic-bezier(0.4, 0, 0.2, 1)';
      } else {
        tabEl.style.transform = '';
        tabEl.style.transition = 'transform 0.2s ease-out';
      }
    }
  });
};

const handleDragLeave = (event: DragEvent) => {
  // 只有当鼠标真正离开标签区域时才清除
  const target = event.currentTarget as HTMLElement;
  const relatedTarget = event.relatedTarget as HTMLElement;
  
  if (!target.contains(relatedTarget)) {
    dragOverIndex.value = null;
    
    // 取消动画帧
    if (animationFrameId.value !== null) {
      cancelAnimationFrame(animationFrameId.value);
      animationFrameId.value = null;
    }
    
    // 停止自动滚动
    if (autoScrollInterval.value !== null) {
      clearInterval(autoScrollInterval.value);
      autoScrollInterval.value = null;
    }
    
    // 重置标签位置
    const tabs = scrollRef.value?.children;
    if (tabs) {
      Array.from(tabs).forEach((tab) => {
        const tabEl = tab as HTMLElement;
        if (tabEl !== target) {
          tabEl.style.transform = '';
          tabEl.style.transition = 'transform 0.12s cubic-bezier(0.25, 0.46, 0.45, 0.94)';
          tabEl.style.willChange = 'auto';
        }
      });
    }
  }
};

const handleDrop = (index: number, event: DragEvent) => {
  event.preventDefault();
  event.stopPropagation();
  
  if (draggedIndex.value === null) {
    handleDragEnd();
    return;
  }
  
  const fromIndex = draggedIndex.value;
  
  // 如果拖拽到自己的位置，不做任何操作
  if (fromIndex === index) {
    handleDragEnd();
    return;
  }
  
  // 根据插入方向计算目标位置
  let targetIndex = index;
  if (!dragInsertBefore.value) {
    // 插入到后面
    targetIndex = index + 1;
  }
  
  // 确保目标位置在有效范围内
  targetIndex = Math.max(0, Math.min(targetIndex, tabsStore.visitedViews.length));
  
  // 如果位置没有变化，不做任何操作
  if (fromIndex === targetIndex) {
    handleDragEnd();
    return;
  }
  
  // 调用 store 方法移动标签（moveView 内部会处理索引调整）
  try {
    tabsStore.moveView(fromIndex, targetIndex);
  } catch (error) {
    // 静默处理错误
  }
  
  // 重置拖拽状态
  handleDragEnd();
};

const handleDragEnd = () => {
  // 取消动画帧
  if (animationFrameId.value !== null) {
    cancelAnimationFrame(animationFrameId.value);
    animationFrameId.value = null;
  }
  
  // 停止自动滚动
  if (autoScrollInterval.value !== null) {
    clearInterval(autoScrollInterval.value);
    autoScrollInterval.value = null;
  }
  
  // 重置所有标签的样式
  const tabs = scrollRef.value?.children;
  if (tabs) {
    Array.from(tabs).forEach((tab) => {
      const tabEl = tab as HTMLElement;
      tabEl.style.transform = '';
      tabEl.style.opacity = '';
      tabEl.style.transition = 'all 0.12s cubic-bezier(0.25, 0.46, 0.45, 0.94)';
      tabEl.style.willChange = 'auto';
      tabEl.style.zIndex = '';
    });
  }
  
  draggedIndex.value = null;
  dragOverIndex.value = null;
  isDragging.value = false;
};

onMounted(() => {
  nextTick(scrollActiveIntoView);
});

onUnmounted(() => {
  if (animationFrameId.value !== null) {
    cancelAnimationFrame(animationFrameId.value);
    animationFrameId.value = null;
  }
  if (autoScrollInterval.value !== null) {
    clearInterval(autoScrollInterval.value);
    autoScrollInterval.value = null;
  }
});
</script>

<style scoped lang="scss">
.tabs-wrapper {
  width: 100%;
  max-width: 900px;
}

.tabs-container {
  width: 100%;
  overflow-x: auto;
  overflow-y: hidden;
  scrollbar-width: none;
  -ms-overflow-style: none;
  
  &::-webkit-scrollbar {
    display: none;
  }
}

.tabs-scroll {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 0;
  background: transparent;
  height: 100%;
}

.tab-item {
  display: inline-flex;
  align-items: center;
  height: 32px;
  padding: 0 16px;
  font-size: 14px;
  font-weight: 500;
  border-radius: 16px;
  cursor: pointer;
  color: #64748b;
  background: transparent;
  transition: all 0.2s ease;
  white-space: nowrap;
  flex-shrink: 0;
  user-select: none;
  position: relative;

  &:hover {
    color: #1e293b;
    background: #f1f5f9;
    .tab-close { opacity: 1; }
  }

  &.active {
    color: #8b5cf6;
    background: #f3e8ff;
    font-weight: 600;

    .tab-close { opacity: 1; color: #8b5cf6; }
  }

  &.dragging {
    opacity: 0.1 !important;
    transform: scale(0.9);
  }

  &.drag-over-before::before {
    content: '';
    position: absolute;
    left: -4px;
    top: 50%;
    transform: translateY(-50%);
    width: 2px;
    height: 60%;
    background: #0066cc;
    border-radius: 2px;
    z-index: 10;
  }

  &.drag-over-after::after {
    content: '';
    position: absolute;
    right: -4px;
    top: 50%;
    transform: translateY(-50%);
    width: 2px;
    height: 60%;
    background: #0066cc;
    border-radius: 2px;
    z-index: 10;
  }

  &.closing {
    opacity: 0;
    transform: scale(0.8) translateY(10px);
    width: 0;
    padding: 0;
    margin: 0;
    overflow: hidden;
  }
}

.tab-title {
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-family: -apple-system, BlinkMacSystemFont, "SF Pro Text", "Segoe UI", sans-serif;
}

.tab-close {
  margin-left: 6px;
  font-size: 10px;
  color: #86868b;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  opacity: 0;
  padding: 2px;
  border-radius: 4px;
  
  &:hover {
    background: rgba(0, 0, 0, 0.06);
    color: #1d1d1f !important;
  }
}
</style>


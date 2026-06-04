/**
 * 路由切换顶部进度条 — 平滑、低干扰，避免宽度脉冲造成的「跳动」感
 */
let activeRequests = 0;
let barEl: HTMLDivElement | null = null;
let showTimer: ReturnType<typeof setTimeout> | null = null;
let hideTimer: ReturnType<typeof setTimeout> | null = null;
let trickleTimer: ReturnType<typeof setInterval> | null = null;
let progress = 0;
let shownAt = 0;

/** 延迟显示：极短跳转不闪一下 */
const SHOW_DELAY_MS = 150;
/** 至少展示一小段时间，避免一闪而过 */
const MIN_VISIBLE_MS = 350;

const ensureBar = () => {
  if (barEl || typeof document === 'undefined') return;
  barEl = document.createElement('div');
  barEl.className = 'route-progress-bar';
  document.body.appendChild(barEl);
};

const applyProgress = () => {
  if (!barEl) return;
  barEl.style.setProperty('--progress', String(Math.min(1, Math.max(0, progress))));
};

const stopTrickle = () => {
  if (trickleTimer) {
    clearInterval(trickleTimer);
    trickleTimer = null;
  }
};

const startTrickle = () => {
  stopTrickle();
  trickleTimer = setInterval(() => {
    if (progress >= 0.92) return;
    const delta = (0.92 - progress) * 0.08 + 0.01;
    progress = Math.min(0.92, progress + delta);
    applyProgress();
  }, 280);
};

const clearShowTimer = () => {
  if (showTimer) {
    clearTimeout(showTimer);
    showTimer = null;
  }
};

const clearHideTimer = () => {
  if (hideTimer) {
    clearTimeout(hideTimer);
    hideTimer = null;
  }
};

const showBar = () => {
  ensureBar();
  if (!barEl) return;
  shownAt = Date.now();
  progress = 0.12;
  applyProgress();
  barEl.classList.remove('is-done');
  barEl.classList.add('is-active');
  startTrickle();
};

const hideBar = () => {
  if (!barEl) return;
  stopTrickle();
  progress = 1;
  applyProgress();
  barEl.classList.remove('is-active');
  barEl.classList.add('is-done');
  clearHideTimer();
  hideTimer = setTimeout(() => {
    if (activeRequests > 0) return;
    barEl?.classList.remove('is-done');
    progress = 0;
    applyProgress();
  }, 400);
};

export const startRouteProgress = () => {
  activeRequests += 1;
  clearHideTimer();

  if (barEl?.classList.contains('is-active')) return;

  if (!showTimer) {
    showTimer = setTimeout(() => {
      showTimer = null;
      if (activeRequests > 0) showBar();
    }, SHOW_DELAY_MS);
  }
};

export const finishRouteProgress = () => {
  activeRequests = Math.max(0, activeRequests - 1);
  if (activeRequests > 0) return;

  clearShowTimer();
  if (!barEl?.classList.contains('is-active')) {
    progress = 0;
    applyProgress();
    return;
  }

  const elapsed = Date.now() - shownAt;
  const wait = Math.max(0, MIN_VISIBLE_MS - elapsed);
  setTimeout(hideBar, wait);
};

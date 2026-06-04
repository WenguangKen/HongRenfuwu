import JSZip from 'jszip';

export interface ContentMediaItem {
  url: string;
  name?: string;
  type?: 'image' | 'video' | string;
}

export interface ContentDownloadRecord {
  key?: string | number;
  taskId?: string;
  influencer?: string;
  media?: ContentMediaItem[];
}

function sanitizeFileName(name: string): string {
  return name.replace(/[<>:"/\\|?*\x00-\x1f]/g, '_').replace(/\s+/g, '_').slice(0, 100) || 'content';
}

function getMediaFileName(item: ContentMediaItem, index: number): string {
  const raw = item.name?.trim();
  if (raw && raw.includes('.')) {
    return sanitizeFileName(raw);
  }
  const ext = item.type === 'video' ? 'mp4' : 'jpg';
  const base = raw ? sanitizeFileName(raw) : `file_${index + 1}`;
  return base.includes('.') ? base : `${base}.${ext}`;
}

function uniqueFileName(baseName: string, index: number, used: Set<string>): string {
  let fileName = baseName;
  let suffix = 1;
  while (used.has(fileName)) {
    const dot = baseName.lastIndexOf('.');
    fileName = dot > 0
      ? `${baseName.slice(0, dot)}_${suffix}${baseName.slice(dot)}`
      : `${baseName}_${suffix}`;
    suffix += 1;
  }
  used.add(fileName);
  return fileName;
}

export function buildZipFileName(record: ContentDownloadRecord): string {
  const taskPart = sanitizeFileName(String(record.taskId || record.key || 'material'));
  const namePart = sanitizeFileName(String(record.influencer || 'content'));
  return `${taskPart}_${namePart}.zip`;
}

async function fetchMediaBlob(url: string): Promise<Blob | null> {
  try {
    const response = await fetch(url);
    if (!response.ok) return null;
    return await response.blob();
  } catch {
    return null;
  }
}

function triggerBlobDownload(blob: Blob, fileName: string) {
  const url = window.URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.href = url;
  link.download = fileName;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  window.URL.revokeObjectURL(url);
}

function triggerLinkDownload(url: string, fileName: string) {
  const link = document.createElement('a');
  link.href = url;
  link.target = '_blank';
  link.download = fileName;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}

export async function downloadContentRecordAsZip(
  record: ContentDownloadRecord,
  options?: { forceZip?: boolean }
): Promise<{ ok: boolean; message?: string }> {
  const media = (record.media || []).filter((item) => item?.url);
  if (media.length === 0) {
    return { ok: false, message: '无可下载的素材文件' };
  }

  const forceZip = options?.forceZip ?? media.length > 1;

  if (!forceZip && media.length === 1) {
    const item = media[0]!;
    const fileName = getMediaFileName(item, 0);
    const blob = await fetchMediaBlob(item.url);
    if (blob) {
      triggerBlobDownload(blob, fileName);
      return { ok: true };
    }
    triggerLinkDownload(item.url, fileName);
    return { ok: true };
  }

  const zip = new JSZip();
  const usedNames = new Set<string>();
  let added = 0;

  for (let i = 0; i < media.length; i++) {
    const item = media[i]!;
    const baseName = getMediaFileName(item, i);
    const fileName = uniqueFileName(baseName, i, usedNames);
    const blob = await fetchMediaBlob(item.url);
    if (blob) {
      zip.file(fileName, blob);
      added += 1;
    }
  }

  if (added === 0) {
    return { ok: false, message: '素材下载失败，请检查网络或文件权限' };
  }

  const zipBlob = await zip.generateAsync({ type: 'blob' });
  triggerBlobDownload(zipBlob, buildZipFileName(record));
  return { ok: true };
}

export function delay(ms: number): Promise<void> {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

/**
 * 平台相关工具函数
 */
import {
  InstagramOutlined, YoutubeOutlined,
  FacebookOutlined, TwitterOutlined
} from '@ant-design/icons-vue';

export const TikTokIcon = {
  template: `<svg viewBox="0 0 448 512" width="1em" height="1em" fill="currentColor">
    <path d="M448,209.91a210.06,210.06,0,0,1-122.77-39.25V349.38A162.55,162.55,0,1,1,185,188.31V278.2a74.62,74.62,0,1,0,52.23,71.18V0l88,0a121.18,121.18,0,0,0,1.86,22.17h0A122.18,122.18,0,0,0,381,102.39a121.43,121.43,0,0,0,67,20.14Z"/>
  </svg>`
};

export const getPlatformIcon = (platform: string) => {
  if (!platform) return TikTokIcon;
  const p = platform.toUpperCase();
  if (p.includes('INSTAGRAM') || p === 'INS' || p === 'IG') return InstagramOutlined;
  if (p.includes('TIKTOK') || p === 'TT') return TikTokIcon;
  if (p.includes('YOUTUBE') || p === 'YT') return YoutubeOutlined;
  if (p.includes('FACEBOOK') || p === 'FB') return FacebookOutlined;
  if (p.includes('TWITTER') || p === 'X') return TwitterOutlined;
  return TikTokIcon;
};

export const getPlatformAbbr = (platform: string) => {
  if (!platform) return 'TT';
  const p = platform.toUpperCase();
  if (p.includes('INSTAGRAM') || p === 'INS' || p === 'IG') return 'INS';
  if (p.includes('TIKTOK') || p === 'TT') return 'TT';
  if (p.includes('YOUTUBE') || p === 'YT') return 'YT';
  if (p.includes('FACEBOOK') || p === 'FB') return 'FB';
  if (p.includes('TWITTER') || p === 'X') return 'X';
  if (p.includes('XIAOHONGSHU') || p.includes('RED') || p === 'XHS') return 'XHS';
  return platform.substring(0, 3).toUpperCase();
};

export const formatUrlForHref = (raw: string | undefined | null): string => {
  if (!raw) return '#';
  const s = raw.trim();
  if (/^https?:\/\//i.test(s)) return s;
  if (s.startsWith('@')) return '#';
  return `https://${s}`;
};

export const extractHandle = (raw: string | undefined | null): string => {
  if (!raw) return '';
  const s = raw.trim();
  try {
    const url = new URL(s.startsWith('http') ? s : `https://${s}`);
    const parts = url.pathname.split('/').filter(Boolean);
    return parts.length > 0 ? `@${(parts[parts.length - 1] ?? '').replace(/^@/, '')}` : s;
  } catch {
    return s.startsWith('@') ? s : `@${s}`;
  }
};

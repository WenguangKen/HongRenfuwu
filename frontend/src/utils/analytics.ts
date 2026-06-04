import axios from 'axios';
const STORAGE_KEY = 'analytics_events';

const read = () => {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) : [];
  } catch {
    return [];
  }
};

const write = (events: any[]) => {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(events));
  } catch {
  }
};

export const trackEvent = (name: string, payload?: Record<string, any>) => {
  const events = read();
  events.push({ name, payload, ts: Date.now() });
  write(events);
};

export const trackPageView = (path: string) => {
  trackEvent('page_view', { path });
};

export const getEvents = () => read();

export const clearEvents = () => write([]);
export const fetchIPInfo = async () => {
  try {
    const resp = await axios.get('https://ipapi.co/json/');
    const d = resp.data || {};
    return {
      ip: String(d.ip || ''),
      city: d.city ? String(d.city) : undefined,
      region: d.region ? String(d.region) : undefined,
      country: d.country ? String(d.country) : undefined,
      latitude: typeof d.latitude === 'number' ? d.latitude : undefined,
      longitude: typeof d.longitude === 'number' ? d.longitude : undefined,
      org: d.org ? String(d.org) : undefined
    };
  } catch {
    try {
      const resp = await axios.get('https://api.ipify.org?format=json');
      const d = resp.data || {};
      return { ip: String(d.ip || '') };
    } catch {
      return { ip: '' };
    }
  }
};
export const trackLoginIP = async () => {
  const info = await fetchIPInfo();
  trackEvent('login_ip', info);
};

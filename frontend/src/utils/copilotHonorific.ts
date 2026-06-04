/** 小A 对当前登录用户的称呼：男 小哥 / 女 公主 */

export type CopilotUserGender = 1 | 2;

const GENDER_KEY_PREFIX = 'ai_copilot_user_gender_';

export function genderStorageKey(userKey: string | number): string {
  return `${GENDER_KEY_PREFIX}${String(userKey)}`;
}

export function loadUserGender(userKey: string | number | null | undefined): CopilotUserGender | null {
  if (userKey == null || userKey === '') return null;
  try {
    const raw = localStorage.getItem(genderStorageKey(userKey));
    if (raw === '1') return 1;
    if (raw === '2') return 2;
  } catch {
    /* ignore */
  }
  return null;
}

export function saveUserGender(userKey: string | number, gender: CopilotUserGender): void {
  localStorage.setItem(genderStorageKey(userKey), String(gender));
}

/** 1=男→小哥，2=女→公主；未设置时用「您」 */
export function resolveHonorific(gender: CopilotUserGender | null | undefined): string {
  if (gender === 1) return '小哥';
  if (gender === 2) return '公主';
  return '您';
}

export function honorificGreeting(gender: CopilotUserGender | null | undefined): string {
  const h = resolveHonorific(gender);
  if (h === '您') return '您好，我是小A';
  return `${h}好，我是小A`;
}

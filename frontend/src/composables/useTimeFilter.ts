import dayjs, { Dayjs } from 'dayjs';

export function filterByTimeRange<T>(
  list: T[],
  getTime: (item: T) => string | undefined,
  range?: [Dayjs, Dayjs]
): T[] {
  if (!range || range.length !== 2) return list;
  const [startTime, endTime] = range;
  const start = dayjs(startTime);
  const end = dayjs(endTime).endOf('day');
  return list.filter((item) => {
    const value = getTime(item);
    if (!value) return false;
    const t = dayjs(value);
    return t.isAfter(start) && t.isBefore(end) || t.isSame(start) || t.isSame(end);
  });
}

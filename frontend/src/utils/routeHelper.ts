/**
 * 路由辅助函数
 */

/**
 * 将路由 name 转换为组件名（PascalCase）
 * 例如: 'discount-list' -> 'DiscountList', 'order-sample' -> 'OrderSample'
 */
export function routeNameToComponentName(routeName: string | undefined | null): string {
  if (!routeName) return '';
  return routeName
    .split('-')
    .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
    .join('');
}


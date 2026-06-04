export interface InfluencerFilter {
  name?: string;
  platform?: string;
  country?: string;
  source?: string;
  email?: string;
  link?: string;
  isPaid?: boolean;
  influencerType?: string;
  tags?: string[];
  fansRange?: string;
  discountCode?: string;
  owner?: string;
  timeType?: string;
  timeRange?: [string, string];
}

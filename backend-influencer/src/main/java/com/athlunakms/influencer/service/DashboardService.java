package com.athlunakms.influencer.service;

import com.athlunakms.influencer.dto.DashboardStatsDto;
import com.athlunakms.influencer.dto.DashboardTrendDto;
import com.athlunakms.influencer.dto.InfluencerRankDto;
import com.athlunakms.influencer.dto.StageDistributionDto;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);
    private static final boolean ORDER_STATS_ENABLED = false;
    private final JdbcTemplate jdbcTemplate;

    public DashboardStatsDto getStats() {
        DashboardStatsDto stats = new DashboardStatsDto();
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Shanghai"));
        LocalDateTime monthStart = today.withDayOfMonth(1).atStartOfDay();
        LocalDateTime nextMonthStart = today.plusMonths(1L).withDayOfMonth(1).atStartOfDay();
        Map<String, Object> monthlyStats = this.getConversionOrderStats(monthStart, nextMonthStart);
        stats.setConversionOrders(this.getLongValue(monthlyStats, "order_count").longValue());
        stats.setOrderGMV(this.getBigDecimalValue(monthlyStats, "total_gmv"));
        stats.setCommissionAmount(this.getBigDecimalValue(monthlyStats, "total_commission"));
        long currentActiveInfluencers = this.getLongValue(monthlyStats, "influencer_count");
        stats.setActiveInfluencers(currentActiveInfluencers);
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime tomorrowStart = today.plusDays(1L).atStartOfDay();
        Map<String, Object> todayStats = this.getConversionOrderStats(todayStart, tomorrowStart);
        stats.setTodayOrders(this.getLongValue(todayStats, "order_count").longValue());
        stats.setTodayGMV(this.getBigDecimalValue(todayStats, "total_gmv"));
        stats.setTodaySettledCommission(this.getBigDecimalValue(todayStats, "total_commission"));
        LocalDateTime prevMonthStart = today.minusMonths(1L).withDayOfMonth(1).atStartOfDay();
        stats.setCurrentMonthLabel(today.getMonthValue() + "\u6708");
        stats.setPreviousMonthLabel(today.minusMonths(1L).getMonthValue() + "\u6708");
        LocalDateTime firstOrderDate = this.getFirstOrderDate();
        boolean dataInsufficient = firstOrderDate == null || firstOrderDate.isAfter(prevMonthStart);
        stats.setDataInsufficient(dataInsufficient);
        if (!dataInsufficient) {
            long currentOrders = this.getLongValue(monthlyStats, "order_count");
            BigDecimal currentGMV = this.getBigDecimalValue(monthlyStats, "total_gmv");
            BigDecimal currentCommission = this.getBigDecimalValue(monthlyStats, "total_commission");
            Map<String, Object> prevMonthStats = this.getConversionOrderStats(prevMonthStart, monthStart);
            long previousOrders = this.getLongValue(prevMonthStats, "order_count");
            BigDecimal previousGMV = this.getBigDecimalValue(prevMonthStats, "total_gmv");
            BigDecimal previousCommission = this.getBigDecimalValue(prevMonthStats, "total_commission");
            long previousActiveInfluencers = this.getLongValue(prevMonthStats, "influencer_count");
            stats.setPreviousConversionOrders(previousOrders);
            stats.setPreviousOrderGMV(previousGMV);
            stats.setPreviousCommissionAmount(previousCommission);
            stats.setPreviousActiveInfluencers(previousActiveInfluencers);

            // 月度预估：当月累计 ÷ 已过天数 × 当月总天数
            int elapsedDays = today.getDayOfMonth(); // 当月已过天数（含今天）
            int totalDaysInMonth = today.lengthOfMonth(); // 当月总天数
            double projectionFactor = (elapsedDays > 0) ? (double) totalDaysInMonth / elapsedDays : 1.0;

            if (previousOrders > 0L) {
                double projectedOrders = currentOrders * projectionFactor;
                stats.setConversionOrdersTrend((projectedOrders - (double)previousOrders) / (double)previousOrders * 100.0);
            }
            if (previousGMV.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal projectedGMV = currentGMV.multiply(BigDecimal.valueOf(projectionFactor));
                double gmvTrend = projectedGMV.subtract(previousGMV).divide(previousGMV, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100L)).doubleValue();
                stats.setOrderGMVTrend(gmvTrend);
            }
            if (previousCommission.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal projectedCommission = currentCommission.multiply(BigDecimal.valueOf(projectionFactor));
                double commissionTrend = projectedCommission.subtract(previousCommission).divide(previousCommission, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100L)).doubleValue();
                stats.setCommissionTrend(commissionTrend);
            }
            if (previousActiveInfluencers > 0L) {
                // 活跃红人也用预估：按当前月进度推算全月活跃红人数
                double projectedInfluencers = currentActiveInfluencers * projectionFactor;
                stats.setActiveInfluencersTrend((projectedInfluencers - (double)previousActiveInfluencers) / (double)previousActiveInfluencers * 100.0);
            }
        }
        return stats;
    }

    private LocalDateTime getFirstOrderDate() {
        if (!ORDER_STATS_ENABLED) {
            return null;
        }
        try {
            String sql = "SELECT MIN(order_created_at) FROM influencer_conversion_order";
            Timestamp result = (Timestamp)this.jdbcTemplate.queryForObject(sql, Timestamp.class);
            return result != null ? result.toLocalDateTime() : null;
        }
        catch (Exception e) {
            log.warn("\u83b7\u53d6\u7b2c\u4e00\u6761\u8ba2\u5355\u65f6\u95f4\u5931\u8d25", (Throwable)e);
            return null;
        }
    }

    public DashboardTrendDto getTrend(int days, String startDateStr, String endDateStr) {
        LocalDate end;
        LocalDate start;
        DashboardTrendDto trend = new DashboardTrendDto();
        ArrayList<String> dates = new ArrayList<String>();
        ArrayList<Long> orderCounts = new ArrayList<Long>();
        ArrayList<BigDecimal> gmvData = new ArrayList<BigDecimal>();
        ArrayList<BigDecimal> commissionData = new ArrayList<BigDecimal>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter parseFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Shanghai"));
        if (startDateStr != null && endDateStr != null) {
            try {
                start = LocalDate.parse(startDateStr, parseFormatter);
                end = LocalDate.parse(endDateStr, parseFormatter);
            }
            catch (Exception e) {
                log.warn("Parsing date range failed, falling back to days: {}", (Object)e.getMessage());
                end = today;
                start = today.minusDays(days - 1);
            }
        } else {
            end = today;
            start = today.minusDays(days - 1);
        }
        LocalDateTime rangeStart = start.atStartOfDay();
        LocalDateTime rangeEnd = end.plusDays(1L).atStartOfDay();
        HashMap<String, Map<String, Object>> dayStatsMap = new HashMap<>();
        if (ORDER_STATS_ENABLED) {
        String sql = "SELECT DATE(order_created_at) AS day,\n       COUNT(*) AS order_count,\n       COALESCE(SUM(total_price), 0) AS total_gmv,\n       COALESCE(SUM(commission_amount), 0) AS total_commission\nFROM influencer_conversion_order\nWHERE order_created_at >= ? AND order_created_at < ?\nGROUP BY DATE(order_created_at)\nORDER BY day\n";
        try {
            List<Map<String, Object>> results = this.jdbcTemplate.queryForList(sql, rangeStart, rangeEnd);
            DateTimeFormatter dayKeyFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (Map<String, Object> row : results) {
                Object dayObj = row.get("day");
                String dayKey = dayObj instanceof Date ? ((Date)dayObj).toLocalDate().format(dayKeyFormatter) : (dayObj instanceof LocalDate ? ((LocalDate)dayObj).format(dayKeyFormatter) : dayObj.toString());
                dayStatsMap.put(dayKey, row);
            }
        }
        catch (Exception e) {
            log.error("\u83b7\u53d6\u8d8b\u52bf\u805a\u5408\u6570\u636e\u5931\u8d25", (Throwable)e);
        }
        }
        DateTimeFormatter dayKeyFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate date = start;
        while (!date.isAfter(end)) {
            dates.add(date.format(formatter));
            String dayKey = date.format(dayKeyFmt);
            Map<String, Object> dayStats = dayStatsMap.get(dayKey);
            if (dayStats != null) {
                orderCounts.add(this.getLongValue(dayStats, "order_count"));
                gmvData.add(this.getBigDecimalValue(dayStats, "total_gmv"));
                commissionData.add(this.getBigDecimalValue(dayStats, "total_commission"));
            } else {
                orderCounts.add(0L);
                gmvData.add(BigDecimal.ZERO);
                commissionData.add(BigDecimal.ZERO);
            }
            date = date.plusDays(1L);
        }
        trend.setDates(dates);
        trend.setOrderCounts(orderCounts);
        trend.setGmvData(gmvData);
        trend.setCommissionData(commissionData);
        return trend;
    }

    public List<InfluencerRankDto> getTopGMV(int days) {
        if (!ORDER_STATS_ENABLED) {
            return Collections.emptyList();
        }
        LocalDateTime startDate = LocalDate.now(ZoneId.of("Asia/Shanghai")).minusDays(days).atStartOfDay();
        String sql = "SELECT ico.influencer_id, ico.influencer_name,\n       SUM(ico.total_price) as total_gmv,\n       COUNT(*) as order_count\nFROM influencer_conversion_order ico\nWHERE ico.order_created_at >= ?\nGROUP BY ico.influencer_id, ico.influencer_name\nORDER BY total_gmv DESC\nLIMIT 10\n";
        try {
            return this.jdbcTemplate.query(sql, (rs, rowNum) -> {
                InfluencerRankDto dto = new InfluencerRankDto();
                dto.setInfluencerId(Long.valueOf(rs.getLong("influencer_id")));
                dto.setName(rs.getString("influencer_name"));
                dto.setGmv(rs.getBigDecimal("total_gmv"));
                dto.setOrderCount(Long.valueOf(rs.getLong("order_count")));
                return dto;
            }, new Object[]{startDate});
        }
        catch (Exception e) {
            log.error("\u83b7\u53d6GMV\u6392\u884c\u699c\u5931\u8d25", (Throwable)e);
            return Collections.emptyList();
        }
    }

    public List<InfluencerRankDto> getTopOrders(int days) {
        if (!ORDER_STATS_ENABLED) {
            return Collections.emptyList();
        }
        LocalDateTime startDate = LocalDate.now(ZoneId.of("Asia/Shanghai")).minusDays(days).atStartOfDay();
        String sql = "SELECT ico.influencer_id, ico.influencer_name,\n       SUM(ico.total_price) as total_gmv,\n       COUNT(*) as order_count\nFROM influencer_conversion_order ico\nWHERE ico.order_created_at >= ?\nGROUP BY ico.influencer_id, ico.influencer_name\nORDER BY order_count DESC\nLIMIT 10\n";
        try {
            return this.jdbcTemplate.query(sql, (rs, rowNum) -> {
                InfluencerRankDto dto = new InfluencerRankDto();
                dto.setInfluencerId(Long.valueOf(rs.getLong("influencer_id")));
                dto.setName(rs.getString("influencer_name"));
                dto.setGmv(rs.getBigDecimal("total_gmv"));
                dto.setOrderCount(Long.valueOf(rs.getLong("order_count")));
                return dto;
            }, new Object[]{startDate});
        }
        catch (Exception e) {
            log.error("\u83b7\u53d6\u8ba2\u5355\u91cf\u6392\u884c\u699c\u5931\u8d25", (Throwable)e);
            return Collections.emptyList();
        }
    }

    public List<StageDistributionDto> getStageDistribution() {
        // stage+status composite key -> label/color mapping
        Map<String, String> compositeLabels = new HashMap<>();
        compositeLabels.put("ONBOARDED_PENDING", "\u5f85\u8054\u7cfb");
        compositeLabels.put("ONBOARDED_CONTACTED", "\u5df2\u8054\u7cfb");
        compositeLabels.put("ONBOARDED_COMMUNICATING", "\u6c9f\u901a\u4e2d");
        compositeLabels.put("ONBOARDED_COOPERATING", "\u5408\u4f5c\u4e2d");
        compositeLabels.put("ONBOARDED_DORMANT", "\u4f11\u7720\u4e2d");
        compositeLabels.put("ONBOARDED_PAUSED", "\u6682\u4e0d\u5408\u4f5c");
        compositeLabels.put("ONBOARDED_TERMINATED", "\u4e0d\u518d\u5408\u4f5c");
        compositeLabels.put("ONBOARDED_BLACKLIST", "\u9ed1\u540d\u5355");
        compositeLabels.put("POOL_UNSCREENED", "\u8d44\u6e90\u6c60-\u5f85\u7b5b\u9009");
        compositeLabels.put("POOL_REJECTED", "\u8d44\u6e90\u6c60-\u6682\u4e0d\u5408\u9002");

        Map<String, String> compositeColors = new HashMap<>();
        compositeColors.put("ONBOARDED_PENDING", "#fb7185");
        compositeColors.put("ONBOARDED_CONTACTED", "#f472b6");
        compositeColors.put("ONBOARDED_COMMUNICATING", "#fda4af");
        compositeColors.put("ONBOARDED_COOPERATING", "#8b5cf6");
        compositeColors.put("ONBOARDED_DORMANT", "#a7f3d0");
        compositeColors.put("ONBOARDED_PAUSED", "#fbbf24");
        compositeColors.put("ONBOARDED_TERMINATED", "#6b7280");
        compositeColors.put("ONBOARDED_BLACKLIST", "#cbd5e1");
        compositeColors.put("POOL_UNSCREENED", "#f59e0b");
        compositeColors.put("POOL_REJECTED", "#94a3b8");

        String sql = "SELECT stage, status, COUNT(*) as count\n" +
                "FROM influencer\n" +
                "WHERE (stage = 'ONBOARDED' AND status IN ('PENDING', 'CONTACTED', 'COMMUNICATING', 'COOPERATING', 'DORMANT', 'PAUSED', 'TERMINATED', 'BLACKLIST'))\n" +
                "   OR (stage = 'POOL' AND status IN ('UNSCREENED', 'REJECTED'))\n" +
                "GROUP BY stage, status\n" +
                "ORDER BY count DESC\n";
        try {
            return this.jdbcTemplate.query(sql, (rs, rowNum) -> {
                String stage = rs.getString("stage");
                String status = rs.getString("status");
                String key = stage + "_" + status;
                return new StageDistributionDto(
                        compositeLabels.getOrDefault(key, status),
                        Long.valueOf(rs.getLong("count")),
                        compositeColors.getOrDefault(key, "#94a3b8"));
            });
        }
        catch (Exception e) {
            log.error("\u83b7\u53d6\u9636\u6bb5\u5206\u5e03\u5931\u8d25", (Throwable)e);
            return Collections.emptyList();
        }
    }


    private Map<String, Object> getConversionOrderStats(LocalDateTime start, LocalDateTime end) {
        if (!ORDER_STATS_ENABLED) {
            HashMap<String, Object> empty = new HashMap<>();
            empty.put("order_count", 0L);
            empty.put("total_gmv", BigDecimal.ZERO);
            empty.put("total_commission", BigDecimal.ZERO);
            empty.put("influencer_count", 0L);
            return empty;
        }
        log.debug("[DashboardService] Querying orders from {} to {}", (Object)start, (Object)end);
        String sql = "SELECT COUNT(*) as order_count,\n       COALESCE(SUM(total_price), 0) as total_gmv,\n       COALESCE(SUM(commission_amount), 0) as total_commission,\n       COUNT(DISTINCT influencer_id) as influencer_count\nFROM influencer_conversion_order\nWHERE order_created_at >= ? AND order_created_at < ?\n";
        try {
            Map<String, Object> result = this.jdbcTemplate.queryForMap(sql, start, end);
            log.debug("[DashboardService] Query result: count={}, gmv={}, commission={}, influencers={}", result.get("order_count"), result.get("total_gmv"), result.get("total_commission"), result.get("influencer_count"));
            return result;
        }
        catch (Exception e) {
            log.error("\u83b7\u53d6\u8f6c\u5316\u8ba2\u5355\u7edf\u8ba1\u5931\u8d25", (Throwable)e);
            HashMap<String, Object> empty = new HashMap<String, Object>();
            empty.put("order_count", 0L);
            empty.put("total_gmv", BigDecimal.ZERO);
            empty.put("total_commission", BigDecimal.ZERO);
            empty.put("influencer_count", 0L);
            return empty;
        }
    }

    private Long getLongValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return 0L;
        }
        if (value instanceof Long) {
            return (Long)value;
        }
        if (value instanceof Number) {
            return ((Number)value).longValue();
        }
        return 0L;
    }

    private BigDecimal getBigDecimalValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal)value;
        }
        if (value instanceof Number) {
            return BigDecimal.valueOf(((Number)value).doubleValue());
        }
        return BigDecimal.ZERO;
    }

    public DashboardService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}


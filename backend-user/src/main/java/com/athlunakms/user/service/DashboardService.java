package com.athlunakms.user.service;

import com.athlunakms.user.dto.DashboardStatsDto;
import com.athlunakms.user.dto.DashboardTrendDto;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {
    private static final Logger log = LoggerFactory.getLogger(DashboardService.class);
    private final JdbcTemplate jdbcTemplate;

    public DashboardStatsDto getStats() {
        DashboardStatsDto stats = new DashboardStatsDto();
        try {
            Long activeInfluencers = this.queryActiveInfluencers();
            stats.setActiveInfluencers(activeInfluencers);
            Long conversionOrders = this.queryConversionOrders30Days();
            stats.setConversionOrders(conversionOrders);
            BigDecimal orderGMV = this.queryOrderGMV30Days();
            stats.setOrderGMV(orderGMV);
            BigDecimal commission = this.queryCommission30Days();
            stats.setCommissionAmount(commission);
            stats.setActiveInfluencersTrend(new BigDecimal("12.0"));
            stats.setConversionOrdersTrend(new BigDecimal("8.4"));
            stats.setOrderGMVTrend(new BigDecimal("-2.1"));
            stats.setCommissionTrend(new BigDecimal("15.2"));
            Long todayOrders = this.queryTodayOrders();
            stats.setTodayOrders(todayOrders);
            BigDecimal todayGMV = this.queryTodayGMV();
            stats.setTodayGMV(todayGMV);
            BigDecimal todaySettledCommission = this.queryTodaySettledCommission();
            stats.setTodaySettledCommission(todaySettledCommission);
        }
        catch (Exception e) {
            log.error("\u83b7\u53d6\u4eea\u8868\u76d8\u7edf\u8ba1\u6570\u636e\u5931\u8d25", e);
            stats.setActiveInfluencers(Long.valueOf(0L));
            stats.setConversionOrders(Long.valueOf(0L));
            stats.setOrderGMV(BigDecimal.ZERO);
            stats.setCommissionAmount(BigDecimal.ZERO);
            stats.setTodayOrders(Long.valueOf(0L));
            stats.setTodayGMV(BigDecimal.ZERO);
            stats.setTodaySettledCommission(BigDecimal.ZERO);
        }
        return stats;
    }

    private Long queryActiveInfluencers() {
        try {
            String sql = "    SELECT COUNT(*)\n    FROM influencer\n    WHERE status IN ('COMMUNICATING', 'COOPERATING')\n       OR stage IN ('ONBOARDED')\n";
            Long count = (Long)this.jdbcTemplate.queryForObject(sql, Long.class);
            return count != null ? count : 0L;
        }
        catch (Exception e) {
            log.warn("\u67e5\u8be2\u6d3b\u8dc3\u7ea2\u4eba\u6570\u91cf\u5931\u8d25: {}", (Object)e.getMessage());
            return 0L;
        }
    }

    private Long queryConversionOrders30Days() {
        try {
            String sql = "    SELECT COUNT(*)\n    FROM influencer_conversion_order\n    WHERE created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)\n";
            Long count = (Long)this.jdbcTemplate.queryForObject(sql, Long.class);
            return count != null ? count : 0L;
        }
        catch (Exception e) {
            log.warn("\u67e5\u8be2\u8f6c\u5316\u8ba2\u5355\u6570\u91cf\u5931\u8d25: {}", (Object)e.getMessage());
            return 0L;
        }
    }

    private BigDecimal queryOrderGMV30Days() {
        try {
            String sql = "    SELECT COALESCE(SUM(total_price), 0)\n    FROM influencer_conversion_order\n    WHERE created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)\n";
            BigDecimal gmv = (BigDecimal)this.jdbcTemplate.queryForObject(sql, BigDecimal.class);
            return gmv != null ? gmv.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        }
        catch (Exception e) {
            log.warn("\u67e5\u8be2\u8ba2\u5355GMV\u5931\u8d25: {}", (Object)e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal queryCommission30Days() {
        try {
            String sql = "    SELECT COALESCE(SUM(\n        CASE\n            WHEN commission_amount IS NOT NULL THEN commission_amount\n            ELSE total_price * COALESCE(commission_rate, 15) / 100\n        END\n    ), 0)\n    FROM influencer_conversion_order\n    WHERE created_at >= DATE_SUB(NOW(), INTERVAL 30 DAY)\n";
            BigDecimal commission = (BigDecimal)this.jdbcTemplate.queryForObject(sql, BigDecimal.class);
            return commission != null ? commission.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        }
        catch (Exception e) {
            log.warn("\u67e5\u8be2\u5206\u4f63\u91d1\u989d\u5931\u8d25: {}", (Object)e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    private Long queryTodayOrders() {
        try {
            String sql = "    SELECT COUNT(*)\n    FROM influencer_conversion_order\n    WHERE financial_status = 'paid'\n      AND DATE(order_created_at) = CURDATE()\n";
            Long count = (Long)this.jdbcTemplate.queryForObject(sql, Long.class);
            return count != null ? count : 0L;
        }
        catch (Exception e) {
            log.warn("\u67e5\u8be2\u5f53\u65e5\u51fa\u5355\u6570\u91cf\u5931\u8d25: {}", (Object)e.getMessage());
            return 0L;
        }
    }

    private BigDecimal queryTodayGMV() {
        try {
            String sql = "    SELECT COALESCE(SUM(total_price), 0)\n    FROM influencer_conversion_order\n    WHERE financial_status = 'paid'\n      AND DATE(order_created_at) = CURDATE()\n";
            BigDecimal gmv = (BigDecimal)this.jdbcTemplate.queryForObject(sql, BigDecimal.class);
            return gmv != null ? gmv.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        }
        catch (Exception e) {
            log.warn("\u67e5\u8be2\u5f53\u65e5GMV\u5931\u8d25: {}", (Object)e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal queryTodaySettledCommission() {
        try {
            String sql = "    SELECT COALESCE(SUM(commission_amount), 0)\n    FROM influencer_conversion_order\n    WHERE commission_status = 'settled'\n      AND DATE(updated_at) = CURDATE()\n";
            BigDecimal commission = (BigDecimal)this.jdbcTemplate.queryForObject(sql, BigDecimal.class);
            return commission != null ? commission.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        }
        catch (Exception e) {
            log.warn("\u67e5\u8be2\u5f53\u65e5\u5df2\u7ed3\u7b97\u5206\u4f63\u5931\u8d25: {}", (Object)e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    public DashboardTrendDto getTrend(int days) {
        if (days <= 0 || days > 30) {
            days = 15;
        }
        DashboardTrendDto trend = new DashboardTrendDto();
        ArrayList<String> dates = new ArrayList<String>();
        ArrayList<Long> orderCounts = new ArrayList<Long>();
        ArrayList<BigDecimal> gmvData = new ArrayList<BigDecimal>();
        ArrayList<BigDecimal> commissionData = new ArrayList<BigDecimal>();
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
            String sql = "    SELECT\n        DATE(order_created_at) as order_date,\n        COUNT(*) as order_count,\n        COALESCE(SUM(total_price), 0) as gmv,\n        COALESCE(SUM(commission_amount), 0) as commission\n    FROM influencer_conversion_order\n    WHERE order_created_at >= DATE_SUB(CURDATE(), INTERVAL ? DAY)\n      AND order_created_at < DATE_ADD(CURDATE(), INTERVAL 1 DAY)\n    GROUP BY DATE(order_created_at)\n    ORDER BY order_date ASC\n";
            List<Map<String, Object>> results = this.jdbcTemplate.queryForList(sql, days);
            LocalDate today = LocalDate.now();
            for (int i = days - 1; i >= 0; --i) {
                LocalDate date = today.minusDays(i);
                String dateStr = date.format(formatter);
                dates.add(dateStr);
                BigDecimal dayGmv = BigDecimal.ZERO;
                BigDecimal dayCommission = BigDecimal.ZERO;
                long dayCount = 0L;
                for (Map<String, Object> row : results) {
                    LocalDate rowDate;
                    Object orderDateObj = row.get("order_date");
                    if (orderDateObj instanceof Date) {
                        rowDate = ((Date)orderDateObj).toLocalDate();
                    } else {
                        if (!(orderDateObj instanceof LocalDate)) continue;
                        rowDate = (LocalDate)orderDateObj;
                    }
                    if (!rowDate.equals(date)) continue;
                    Object countObj = row.get("order_count");
                    dayCount = countObj != null ? ((Number)countObj).longValue() : 0L;
                    Object gmvObj = row.get("gmv");
                    dayGmv = gmvObj != null ? new BigDecimal(gmvObj.toString()) : BigDecimal.ZERO;
                    Object commObj = row.get("commission");
                    dayCommission = commObj != null ? new BigDecimal(commObj.toString()) : BigDecimal.ZERO;
                    break;
                }
                orderCounts.add(dayCount);
                gmvData.add(dayGmv.setScale(2, RoundingMode.HALF_UP));
                commissionData.add(dayCommission.setScale(2, RoundingMode.HALF_UP));
            }
        }
        catch (Exception e) {
            log.error("\u83b7\u53d6\u4eea\u8868\u76d8\u8d8b\u52bf\u6570\u636e\u5931\u8d25", e);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
            LocalDate today = LocalDate.now();
            for (int i = days - 1; i >= 0; --i) {
                dates.add(today.minusDays(i).format(formatter));
                orderCounts.add(0L);
                gmvData.add(BigDecimal.ZERO);
                commissionData.add(BigDecimal.ZERO);
            }
        }
        trend.setDates(dates);
        trend.setOrderCounts(orderCounts);
        trend.setGmvData(gmvData);
        trend.setCommissionData(commissionData);
        return trend;
    }

    public DashboardService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}


package com.athlunakms.ai.sync;

import com.athlunakms.ai.util.PgVectorUtils;
import dev.langchain4j.model.embedding.EmbeddingModel;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Component
@RequiredArgsConstructor
public class CrossServiceVectorSync {

    private final NamedParameterJdbcTemplate pgJdbcTemplate;
    private final EmbeddingModel embeddingModel;
    private final RestTemplate restTemplate;
    private final RateLimiterRegistry rateLimiterRegistry;

    @Value("${services.backend-influencer.url}")
    private String influencerServiceUrl;

    @Scheduled(fixedDelay = 3000)
    public void syncVectorsFromInfluencerModule() {
        try {
            String pullUrl = influencerServiceUrl + "/internal/api/influencers/pending-sync?limit=10";
            List<Map<String, Object>> pendingData = restTemplate.getForObject(pullUrl, List.class);

            if (pendingData == null || pendingData.isEmpty()) {
                return;
            }

            RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter("vectorSyncLimiter");

            for (Map<String, Object> inf : pendingData) {
                Long id = Long.valueOf(inf.get("id").toString());
                try {
                    RateLimiter.waitForPermission(rateLimiter);

                    String platform = stringVal(inf.get("platform"));
                    if (!StringUtils.hasText(platform)) {
                        platform = stringVal(inf.get("default_platform"));
                    }
                    String summary = stringVal(inf.get("embed_text"));
                    if (!StringUtils.hasText(summary)) {
                        summary = String.format("名字:%s 平台:%s 粉丝:%s 介绍:%s",
                                inf.get("real_name"), platform, inf.get("total_fans"), inf.get("description"));
                    }

                    float[] vector = embeddingModel.embed(summary).content().vector();
                    String vectorLiteral = PgVectorUtils.toVectorLiteral(vector);

                    boolean isCooperated = Boolean.TRUE.equals(inf.get("is_cooperated"))
                            || "COOPERATING".equalsIgnoreCase(stringVal(inf.get("status")));
                    String status = stringVal(inf.get("status"));
                    String country = stringVal(inf.get("country"));

                    String pgSql = "INSERT INTO ai_influencer_index (influencer_id, platform, is_cooperated, status, country, embedding) " +
                            "VALUES (:id, :platform, :isCooperated, :status, :country, cast(:vector as vector)) " +
                            "ON CONFLICT (influencer_id) DO UPDATE SET " +
                            "platform = EXCLUDED.platform, is_cooperated = EXCLUDED.is_cooperated, " +
                            "status = EXCLUDED.status, country = EXCLUDED.country, embedding = EXCLUDED.embedding";

                    MapSqlParameterSource params = new MapSqlParameterSource()
                            .addValue("id", id)
                            .addValue("platform", platform)
                            .addValue("isCooperated", isCooperated)
                            .addValue("status", StringUtils.hasText(status) ? status : null)
                            .addValue("country", StringUtils.hasText(country) ? country : null)
                            .addValue("vector", vectorLiteral);

                    pgJdbcTemplate.update(pgSql, params);
                    notifyStatus(id, "SUCCESS", null);
                    log.info("红人ID {} 向量同步成功", id);

                } catch (RequestNotPermitted e) {
                    log.warn("向量同步触发限流，红人ID {} 保持待同步，稍后重试", id);
                    break;
                } catch (Exception e) {
                    log.error("红人ID {} 向量同步失败", id, e);
                    notifyStatus(id, "FAILED", e.getMessage());
                }
            }
        } catch (Exception e) {
            log.debug("跨微服务拉取待同步向量队列: {}", e.getMessage());
        }
    }

    private void notifyStatus(Long id, String status, String errorMsg) {
        String notifyUrl = UriComponentsBuilder.fromHttpUrl(influencerServiceUrl + "/internal/api/influencers/sync-status")
                .queryParam("influencerId", id)
                .queryParam("status", status)
                .queryParam("errorMsg", StringUtils.hasText(errorMsg) ? errorMsg : "")
                .toUriString();
        restTemplate.postForObject(notifyUrl, null, Void.class);
    }

    private static String stringVal(Object value) {
        return value == null ? "" : value.toString().trim();
    }
}

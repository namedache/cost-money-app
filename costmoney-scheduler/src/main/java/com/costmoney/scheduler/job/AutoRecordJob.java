package com.costmoney.scheduler.job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class AutoRecordJob {
    private static final Logger log = LoggerFactory.getLogger(AutoRecordJob.class);
    private final RestClient referenceClient;
    private final RestClient ledgerClient;

    public AutoRecordJob() {
        this.referenceClient = RestClient.builder().baseUrl("http://localhost:8083").build();
        this.ledgerClient = RestClient.builder().baseUrl("http://localhost:8082").build();
    }

    @Scheduled(cron = "0 0 8 * * ?")
    public void processAutoRules() {
        log.info("Processing auto-record rules...");
        try {
            List<Map> rules = referenceClient.get().uri("/internal/auto-rules/active").retrieve().body(List.class);
            if (rules == null) return;

            LocalDate today = LocalDate.now();
            for (Map rule : rules) {
                if (!shouldFire(rule, today)) continue;

                try {
                    ledgerClient.post().uri("/api/records")
                            .header("X-User-Id", String.valueOf(rule.get("userId")))
                            .body(Map.of(
                                    "amount", rule.get("amount"),
                                    "type", "expense",
                                    "categoryId", rule.get("categoryId"),
                                    "accountId", rule.get("accountId"),
                                    "note", "自动记账: " + rule.get("name"),
                                    "date", today.toString()
                            )).retrieve().toEntity(Map.class);
                    log.info("Auto-record created for rule: {}", rule.get("name"));
                } catch (Exception e) {
                    log.error("Failed to create auto-record for rule {}: {}", rule.get("name"), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Failed to process auto-record rules: {}", e.getMessage());
        }
    }

    private boolean shouldFire(Map rule, LocalDate today) {
        String period = (String) rule.get("period");
        if (period == null) return true;
        return switch (period) {
            case "daily" -> true;
            case "weekly" -> today.getDayOfWeek() == DayOfWeek.MONDAY;
            case "monthly" -> today.getDayOfMonth() == 1;
            default -> true;
        };
    }
}

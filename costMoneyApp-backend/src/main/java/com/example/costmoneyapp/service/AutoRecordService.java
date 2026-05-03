package com.example.costmoneyapp.service;

import com.example.costmoneyapp.entity.AutoRule;
import com.example.costmoneyapp.entity.Record;
import com.example.costmoneyapp.repository.AutoRuleRepository;
import com.example.costmoneyapp.repository.RecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class AutoRecordService {

    private static final Logger log = LoggerFactory.getLogger(AutoRecordService.class);

    @Autowired
    private AutoRuleRepository autoRuleRepository;

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private AccountService accountService;

    @Scheduled(cron = "0 0 8 * * ?")
    @Transactional
    public void processAutoRules() {
        log.info("Processing auto record rules...");

        List<AutoRule> rules = autoRuleRepository.findByIsActiveTrue();
        LocalDate today = LocalDate.now();

        for (AutoRule rule : rules) {
            try {
                if (shouldExecute(rule, today)) {
                    executeRule(rule);
                    log.info("Auto rule executed: {} for date: {}", rule.getName(), today);
                }
            } catch (Exception e) {
                log.error("Failed to execute auto rule: {}", rule.getName(), e);
            }
        }
    }

    private boolean shouldExecute(AutoRule rule, LocalDate today) {
        int dayOfMonth = today.getDayOfMonth();
        int dayOfWeek = today.getDayOfWeek().getValue();

        return switch (rule.getPeriod()) {
            case "daily" -> true;
            case "weekly" -> dayOfWeek == 1;
            case "monthly" -> dayOfMonth == 1;
            case "custom" -> dayOfMonth % getIntervalDays(rule) == 1;
            default -> false;
        };
    }

    private int getIntervalDays(AutoRule rule) {
        return rule.getIntervalDays() != null && rule.getIntervalDays() > 0 ? rule.getIntervalDays() : 1;
    }

    private void executeRule(AutoRule rule) {
        Record record = new Record();
        record.setUserId(rule.getUserId());
        record.setAmount(rule.getAmount());
        record.setType("expense");
        record.setCategoryId(rule.getCategoryId());
        record.setAccountId(rule.getAccountId());
        record.setNote("自动记账: " + rule.getName());
        record.setDate(LocalDate.now());

        Record saved = recordRepository.save(record);

        BigDecimal balanceChange = rule.getAmount().negate();
        accountService.updateBalance(rule.getAccountId(), rule.getUserId(), balanceChange);

        log.info("Created auto record: id={}, amount={}, rule={}",
                saved.getId(), rule.getAmount(), rule.getName());
    }
}
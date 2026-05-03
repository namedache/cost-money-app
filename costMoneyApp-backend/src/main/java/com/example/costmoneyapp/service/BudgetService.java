package com.example.costmoneyapp.service;

import com.example.costmoneyapp.entity.Budget;
import com.example.costmoneyapp.repository.BudgetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class BudgetService {

    private static final Logger log = LoggerFactory.getLogger(BudgetService.class);

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private RecordService recordService;

    public List<Budget> getList(Long userId) {
        log.debug("Getting budget list for userId: {}", userId);
        return budgetRepository.findByUserIdAndIsActiveTrue(userId);
    }

    @Transactional
    public Budget create(Long userId, Budget budget) {
        log.info("Creating budget for userId: {}, categoryId: {}, amount: {}",
                userId, budget.getCategoryId(), budget.getAmount());
        budget.setUserId(userId);
        budget.setIsActive(true);
        return budgetRepository.save(budget);
    }

    @Transactional
    public Budget update(Long id, Long userId, Budget budget) {
        log.info("Updating budget id: {} for userId: {}", id, userId);
        Budget existing = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("预算不存在"));
        if (!existing.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改此预算");
        }
        existing.setCategoryId(budget.getCategoryId());
        existing.setPeriod(budget.getPeriod());
        existing.setAmount(budget.getAmount());
        existing.setAlertThreshold(budget.getAlertThreshold());
        return budgetRepository.save(existing);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        log.info("Deleting budget id: {} for userId: {}", id, userId);
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("预算不存在"));
        if (!budget.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此预算");
        }
        budgetRepository.delete(budget);
    }

    public List<Map<String, Object>> getBudgetsWithSpent(Long userId) {
        List<Budget> budgets = getList(userId);
        LocalDate today = LocalDate.now();
        List<Map<String, Object>> result = new ArrayList<>();

        for (Budget budget : budgets) {
            LocalDate[] dateRange = getDateRange(budget.getPeriod(), today);
            List<?> records = recordService.getListByDateRange(userId, dateRange[0], dateRange[1]);

            BigDecimal spent = BigDecimal.ZERO;
            for (Object r : records) {
                if (r instanceof com.example.costmoneyapp.entity.Record record) {
                    if ("expense".equals(record.getType())) {
                        if (budget.getCategoryId() == null ||
                            budget.getCategoryId().equals(record.getCategoryId())) {
                            spent = spent.add(record.getAmount());
                        }
                    }
                }
            }

            BigDecimal budgetAmount = budget.getAmount();
            int progress = budgetAmount.compareTo(BigDecimal.ZERO) > 0
                    ? spent.multiply(new BigDecimal("100")).divide(budgetAmount, 0, RoundingMode.HALF_UP).intValue()
                    : 0;

            Map<String, Object> item = new HashMap<>();
            item.put("id", budget.getId());
            item.put("categoryId", budget.getCategoryId());
            item.put("type", budget.getType());
            item.put("period", budget.getPeriod());
            item.put("amount", budgetAmount);
            item.put("spent", spent);
            item.put("progress", Math.min(progress, 999));
            item.put("alertThreshold", budget.getAlertThreshold());
            item.put("isActive", budget.getIsActive());
            item.put("createdAt", budget.getCreatedAt());
            result.add(item);
        }

        return result;
    }

    private LocalDate[] getDateRange(String period, LocalDate date) {
        if ("weekly".equals(period)) {
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            LocalDate startOfWeek = date.with(weekFields.dayOfWeek(), 1);
            LocalDate endOfWeek = startOfWeek.plusDays(6);
            return new LocalDate[]{startOfWeek, endOfWeek};
        } else {
            LocalDate startOfMonth = date.withDayOfMonth(1);
            LocalDate endOfMonth = date.withDayOfMonth(date.lengthOfMonth());
            return new LocalDate[]{startOfMonth, endOfMonth};
        }
    }
}

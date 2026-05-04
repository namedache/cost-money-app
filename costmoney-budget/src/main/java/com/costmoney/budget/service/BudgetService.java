package com.costmoney.budget.service;
import com.costmoney.budget.entity.Budget;
import com.costmoney.budget.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BudgetService {
    @Autowired private BudgetRepository budgetRepository;

    private final RestClient restClient;

    public BudgetService() {
        this.restClient = RestClient.builder().baseUrl("http://localhost:8082").build();
    }

    public List<Map<String, Object>> getBudgetsWithSpent(Long userId) {
        List<Budget> budgets = budgetRepository.findByUserIdAndIsActiveTrue(userId);
        return budgets.stream().map(b -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", b.getId());
            map.put("categoryId", b.getCategoryId());
            map.put("type", b.getType());
            map.put("period", b.getPeriod());
            map.put("amount", b.getAmount());
            map.put("alertThreshold", b.getAlertThreshold());
            map.put("isActive", b.isActive());

            LocalDate now = LocalDate.now();
            LocalDate start = now.withDayOfMonth(1);
            LocalDate end = now.withDayOfMonth(now.lengthOfMonth());

            try {
                List<Map> records = restClient.get()
                        .uri("/internal/records/by-date-range?userId={userId}&startDate={start}&endDate={end}", userId, start, end)
                        .retrieve().body(List.class);
                double spent = records == null ? 0 : records.stream()
                        .filter(r -> "expense".equals(r.get("type")))
                        .mapToDouble(r -> ((Number) r.get("amount")).doubleValue())
                        .sum();
                map.put("spent", spent);
                map.put("progress", b.getAmount().doubleValue() > 0 ? (int)(spent / b.getAmount().doubleValue() * 100) : 0);
            } catch (Exception e) {
                map.put("spent", 0);
                map.put("progress", 0);
            }
            return map;
        }).collect(Collectors.toList());
    }

    public List<Budget> getList(Long userId) { return budgetRepository.findByUserIdAndIsActiveTrue(userId); }

    @Transactional
    public Budget create(Long userId, Budget budget) { budget.setUserId(userId); return budgetRepository.save(budget); }

    @Transactional
    public void delete(Long id, Long userId) {
        Budget b = budgetRepository.findById(id).orElseThrow(() -> new RuntimeException("预算不存在"));
        if (!b.getUserId().equals(userId)) throw new RuntimeException("无权删除");
        budgetRepository.delete(b);
    }
}

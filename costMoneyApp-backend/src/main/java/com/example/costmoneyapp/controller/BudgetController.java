package com.example.costmoneyapp.controller;

import com.example.costmoneyapp.config.CurrentUser;
import com.example.costmoneyapp.entity.Budget;
import com.example.costmoneyapp.service.BudgetService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {

    private static final Logger log = LoggerFactory.getLogger(BudgetController.class);

    @Autowired
    private BudgetService budgetService;

    @GetMapping
    public ResponseEntity<List<Budget>> list() {
        Long userId = CurrentUser.getId();
        log.info("GET /api/budgets - userId: {}", userId);
        return ResponseEntity.ok(budgetService.getList(userId));
    }

    @GetMapping("/with-spent")
    public ResponseEntity<List<Map<String, Object>>> listWithSpent() {
        Long userId = CurrentUser.getId();
        log.info("GET /api/budgets/with-spent - userId: {}", userId);
        return ResponseEntity.ok(budgetService.getBudgetsWithSpent(userId));
    }

    @PostMapping
    public ResponseEntity<Budget> create(@RequestBody Budget budget) {
        Long userId = CurrentUser.getId();
        log.info("POST /api/budgets - userId: {}, amount: {}", userId, budget.getAmount());
        Budget created = budgetService.create(userId, budget);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Budget> update(@PathVariable Long id, @RequestBody Budget budget) {
        Long userId = CurrentUser.getId();
        log.info("PUT /api/budgets/{} - userId: {}", id, userId);
        Budget updated = budgetService.update(id, userId, budget);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        Long userId = CurrentUser.getId();
        log.info("DELETE /api/budgets/{} - userId: {}", id, userId);
        budgetService.delete(id, userId);
        return ResponseEntity.ok(Map.of("message", "删除成功"));
    }
}

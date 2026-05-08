package com.costmoney.budget.controller;
import com.costmoney.budget.entity.Budget;
import com.costmoney.budget.service.BudgetService;
import com.costmoney.common.config.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {
    @Autowired private BudgetService service;

    @GetMapping public List<Budget> list() { return service.getList(UserContext.getUserId()); }
    @GetMapping("/with-spent") public List<Map<String, Object>> withSpent() { return service.getBudgetsWithSpent(UserContext.getUserId()); }
    @PostMapping public Budget create(@RequestBody Budget budget) { return service.create(UserContext.getUserId(), budget); }
    @DeleteMapping("/{id}") public Map<String, String> delete(@PathVariable Long id) { service.delete(id, UserContext.getUserId()); return Map.of("message", "删除成功"); }
}

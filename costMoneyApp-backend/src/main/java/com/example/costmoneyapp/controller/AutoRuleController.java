package com.example.costmoneyapp.controller;

import com.example.costmoneyapp.config.CurrentUser;
import com.example.costmoneyapp.entity.AutoRule;
import com.example.costmoneyapp.service.AutoRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auto-rules")
public class AutoRuleController {

    private static final Logger log = LoggerFactory.getLogger(AutoRuleController.class);

    @Autowired
    private AutoRuleService autoRuleService;

    @GetMapping
    public ResponseEntity<List<AutoRule>> list() {
        Long userId = CurrentUser.getId();
        log.info("GET /api/auto-rules - userId: {}", userId);
        return ResponseEntity.ok(autoRuleService.getList(userId));
    }

    @PostMapping
    public ResponseEntity<AutoRule> create(@RequestBody AutoRule rule) {
        Long userId = CurrentUser.getId();
        log.info("POST /api/auto-rules - userId: {}, name: {}", userId, rule.getName());
        AutoRule created = autoRuleService.create(userId, rule);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AutoRule> update(@PathVariable Long id, @RequestBody AutoRule rule) {
        Long userId = CurrentUser.getId();
        log.info("PUT /api/auto-rules/{} - userId: {}", id, userId);
        AutoRule updated = autoRuleService.update(id, userId, rule);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        Long userId = CurrentUser.getId();
        log.info("DELETE /api/auto-rules/{} - userId: {}", id, userId);
        autoRuleService.delete(id, userId);
        return ResponseEntity.ok(Map.of("message", "删除成功"));
    }
}

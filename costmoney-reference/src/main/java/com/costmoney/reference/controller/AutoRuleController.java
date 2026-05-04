package com.costmoney.reference.controller;
import com.costmoney.common.config.UserContext;
import com.costmoney.reference.entity.AutoRule;
import com.costmoney.reference.service.AutoRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auto-rules")
public class AutoRuleController {
    @Autowired private AutoRuleService service;

    @GetMapping public List<AutoRule> list() { return service.getList(UserContext.getUserId()); }
    @PostMapping public AutoRule create(@RequestBody AutoRule rule) { return service.create(UserContext.getUserId(), rule); }
    @PutMapping("/{id}") public AutoRule update(@PathVariable Long id, @RequestBody AutoRule rule) { return service.update(id, UserContext.getUserId(), rule); }
    @DeleteMapping("/{id}") public Map<String, String> delete(@PathVariable Long id) { service.delete(id, UserContext.getUserId()); return Map.of("message", "删除成功"); }
}

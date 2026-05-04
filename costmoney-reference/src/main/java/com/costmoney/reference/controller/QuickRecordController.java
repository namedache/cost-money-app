package com.costmoney.reference.controller;
import com.costmoney.common.config.UserContext;
import com.costmoney.reference.entity.QuickRecord;
import com.costmoney.reference.service.QuickRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quick-records")
public class QuickRecordController {
    @Autowired private QuickRecordService service;

    @GetMapping public List<QuickRecord> list() { return service.getList(UserContext.getUserId()); }
    @PostMapping public QuickRecord create(@RequestBody QuickRecord qr) { return service.create(UserContext.getUserId(), qr); }
    @PutMapping("/{id}") public QuickRecord update(@PathVariable Long id, @RequestBody QuickRecord qr) { return service.update(id, UserContext.getUserId(), qr); }
    @DeleteMapping("/{id}") public Map<String, String> delete(@PathVariable Long id) { service.delete(id, UserContext.getUserId()); return Map.of("message", "删除成功"); }
}

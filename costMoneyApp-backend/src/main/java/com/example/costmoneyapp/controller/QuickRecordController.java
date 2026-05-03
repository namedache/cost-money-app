package com.example.costmoneyapp.controller;

import com.example.costmoneyapp.config.CurrentUser;
import com.example.costmoneyapp.entity.QuickRecord;
import com.example.costmoneyapp.service.QuickRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quick-records")
public class QuickRecordController {

    private static final Logger log = LoggerFactory.getLogger(QuickRecordController.class);

    @Autowired
    private QuickRecordService quickRecordService;

    @GetMapping
    public ResponseEntity<List<QuickRecord>> list() {
        Long userId = CurrentUser.getId();
        log.info("GET /api/quick-records - userId: {}", userId);
        return ResponseEntity.ok(quickRecordService.getList(userId));
    }

    @PostMapping
    public ResponseEntity<QuickRecord> create(@RequestBody QuickRecord quickRecord) {
        Long userId = CurrentUser.getId();
        log.info("POST /api/quick-records - userId: {}, name: {}", userId, quickRecord.getName());
        QuickRecord created = quickRecordService.create(userId, quickRecord);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuickRecord> update(@PathVariable Long id, @RequestBody QuickRecord quickRecord) {
        Long userId = CurrentUser.getId();
        log.info("PUT /api/quick-records/{} - userId: {}", id, userId);
        QuickRecord updated = quickRecordService.update(id, userId, quickRecord);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        Long userId = CurrentUser.getId();
        log.info("DELETE /api/quick-records/{} - userId: {}", id, userId);
        quickRecordService.delete(id, userId);
        return ResponseEntity.ok(Map.of("message", "删除成功"));
    }
}

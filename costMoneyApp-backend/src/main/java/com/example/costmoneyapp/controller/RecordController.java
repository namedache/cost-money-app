package com.example.costmoneyapp.controller;

import com.example.costmoneyapp.config.CurrentUser;
import com.example.costmoneyapp.entity.Record;
import com.example.costmoneyapp.service.RecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/records")
public class RecordController {

    private static final Logger log = LoggerFactory.getLogger(RecordController.class);

    @Autowired
    private RecordService recordService;

    @GetMapping
    public ResponseEntity<List<Record>> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = CurrentUser.getId();
        log.info("GET /api/records - userId: {}, startDate: {}, endDate: {}", userId, startDate, endDate);
        List<Record> records;
        if (startDate != null && endDate != null) {
            records = recordService.getListByDateRange(userId, startDate, endDate);
        } else {
            records = recordService.getList(userId);
        }
        return ResponseEntity.ok(records);
    }

    @PostMapping
    public ResponseEntity<Record> create(@RequestBody Record record) {
        Long userId = CurrentUser.getId();
        log.info("POST /api/records - userId: {}, amount: {}, type: {}", userId, record.getAmount(), record.getType());
        Record created = recordService.create(userId, record);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Record> update(@PathVariable Long id, @RequestBody Record record) {
        Long userId = CurrentUser.getId();
        log.info("PUT /api/records/{} - userId: {}", id, userId);
        Record updated = recordService.update(id, userId, record);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        Long userId = CurrentUser.getId();
        log.info("DELETE /api/records/{} - userId: {}", id, userId);
        recordService.delete(id, userId);
        return ResponseEntity.ok(Map.of("message", "删除成功"));
    }
}

package com.costmoney.ledger.controller;

import com.costmoney.common.config.UserContext;
import com.costmoney.ledger.entity.Record;
import com.costmoney.ledger.service.RecordService;
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

    @Autowired
    private RecordService recordService;

    @GetMapping
    public ResponseEntity<List<Record>> list(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = UserContext.getUserId();
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
        Long userId = UserContext.getUserId();
        return ResponseEntity.ok(recordService.create(userId, record));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Record> update(@PathVariable Long id, @RequestBody Record record) {
        Long userId = UserContext.getUserId();
        return ResponseEntity.ok(recordService.update(id, userId, record));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        recordService.delete(id, userId);
        return ResponseEntity.ok(Map.of("message", "删除成功"));
    }
}

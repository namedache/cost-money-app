package com.costmoney.ledger.controller;

import com.costmoney.common.config.UserContext;
import com.costmoney.ledger.entity.Record;
import com.costmoney.ledger.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    @Autowired
    private RecordRepository recordRepository;

    @GetMapping("/csv")
    public ResponseEntity<byte[]> exportCsv(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = UserContext.getUserId();
        List<Record> records = recordRepository.findByUserIdAndDateBetween(userId, startDate, endDate);

        StringBuilder sb = new StringBuilder("﻿"); // BOM for Excel
        sb.append("日期,类型,金额,分类ID,账户ID,备注\n");
        for (Record r : records) {
            sb.append(r.getDate()).append(",")
              .append(r.getType()).append(",")
              .append(r.getAmount()).append(",")
              .append(r.getCategoryId() != null ? r.getCategoryId() : "").append(",")
              .append(r.getAccountId() != null ? r.getAccountId() : "").append(",")
              .append(r.getNote() != null ? "\"" + r.getNote().replace("\"", "\"\"") + "\"" : "").append("\n");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=records.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> summary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        Long userId = UserContext.getUserId();
        List<Record> records = recordRepository.findByUserIdAndDateBetween(userId, startDate, endDate);

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        Map<Long, BigDecimal> categoryStats = new HashMap<>();
        Map<LocalDate, BigDecimal> dailyStats = new HashMap<>();

        for (Record r : records) {
            if ("income".equals(r.getType())) totalIncome = totalIncome.add(r.getAmount());
            else totalExpense = totalExpense.add(r.getAmount());

            if (r.getCategoryId() != null) {
                categoryStats.merge(r.getCategoryId(), r.getAmount(), BigDecimal::add);
            }
            dailyStats.merge(r.getDate(), r.getAmount(), BigDecimal::add);
        }

        return ResponseEntity.ok(Map.of(
                "totalIncome", totalIncome,
                "totalExpense", totalExpense,
                "balance", totalIncome.subtract(totalExpense),
                "recordCount", records.size(),
                "categoryStats", categoryStats,
                "dailyStats", dailyStats
        ));
    }
}

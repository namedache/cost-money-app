package com.costmoney.ledger.controller;

import com.costmoney.ledger.entity.Record;
import com.costmoney.ledger.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/internal/records")
public class InternalRecordController {

    @Autowired
    private RecordRepository recordRepository;

    @GetMapping("/by-date-range")
    public List<Record> getByDateRange(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return recordRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
    }

    @GetMapping("/count")
    public long countByUserId(@RequestParam Long userId) {
        return recordRepository.findByUserIdOrderByDateDesc(userId).size();
    }
}

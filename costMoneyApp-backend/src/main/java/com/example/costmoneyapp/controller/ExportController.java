package com.example.costmoneyapp.controller;

import com.example.costmoneyapp.config.CurrentUser;
import com.example.costmoneyapp.entity.Record;
import com.example.costmoneyapp.repository.RecordRepository;
import com.example.costmoneyapp.repository.CategoryRepository;
import com.example.costmoneyapp.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    private static final Logger log = LoggerFactory.getLogger(ExportController.class);

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/csv")
    public ResponseEntity<String> exportCsv(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Long userId = CurrentUser.getId();
        log.info("Export CSV for userId: {}, dateRange: {} to {}", userId, startDate, endDate);

        List<Record> records = recordRepository.findByUserIdAndDateBetween(userId, startDate, endDate);

        var categoryMap = new HashMap<Long, String>();
        var accountMap = new HashMap<Long, String>();

        categoryRepository.findByUserIdOrUserIdIsNull(userId)
                .forEach(c -> categoryMap.put(c.getId(), c.getName()));
        accountRepository.findByUserIdOrderBySortOrder(userId)
                .forEach(a -> accountMap.put(a.getId(), a.getName()));

        StringBuilder csv = new StringBuilder();
        csv.append("日期,类型,分类,账户,金额,备注\n");

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        for (Record r : records) {
            String type = "expense".equals(r.getType()) ? "支出" : "收入";
            String category = categoryMap.getOrDefault(r.getCategoryId(), "");
            String account = accountMap.getOrDefault(r.getAccountId(), "");
            String amount = r.getAmount().toString();
            String note = r.getNote() != null
                    ? r.getNote().replace(",", "，").replace("\"", "\"\"").replace("\n", " ")
                    : "";

            csv.append(String.format("%s,%s,%s,%s,%s,\"%s\"\n",
                    r.getDate().format(fmt), type, category, account, amount, note));
        }

        String filename = "records_" + startDate + "_" + endDate + ".csv";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv;charset=UTF-8"));
        // BOM for Excel compatibility
        byte[] bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        String csvContent = new String(bom, java.nio.charset.StandardCharsets.UTF_8) + csv;
        headers.setContentDispositionFormData("attachment", filename);

        return new ResponseEntity<>(csvContent, headers, HttpStatus.OK);
    }

    @GetMapping("/summary")
    public Map<String, Object> exportSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        Long userId = CurrentUser.getId();
        log.info("Export summary for userId: {}, dateRange: {} to {}", userId, startDate, endDate);

        List<Record> records = recordRepository.findByUserIdAndDateBetween(userId, startDate, endDate);

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;
        Map<String, BigDecimal> categoryStats = new HashMap<>();
        Map<String, BigDecimal> dailyStats = new HashMap<>();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (Record r : records) {
            if ("income".equals(r.getType())) {
                totalIncome = totalIncome.add(r.getAmount());
            } else {
                totalExpense = totalExpense.add(r.getAmount());
                String catName = categoryRepository.findById(r.getCategoryId())
                        .map(c -> c.getName())
                        .orElse("其他");
                categoryStats.merge(catName, r.getAmount(), BigDecimal::add);
            }
            String day = r.getDate().format(fmt);
            BigDecimal dailyChange = "income".equals(r.getType()) ? r.getAmount() : r.getAmount().negate();
            dailyStats.merge(day, dailyChange, BigDecimal::add);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        result.put("totalIncome", totalIncome);
        result.put("totalExpense", totalExpense);
        result.put("balance", totalIncome.subtract(totalExpense));
        result.put("recordCount", records.size());
        result.put("categoryStats", categoryStats);
        result.put("dailyStats", dailyStats);

        return result;
    }
}

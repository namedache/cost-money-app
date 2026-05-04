package com.costmoney.ledger.service;

import com.costmoney.ledger.entity.Record;
import com.costmoney.ledger.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class RecordService {

    private static final Set<String> VALID_TYPES = Set.of("expense", "income");

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private AccountService accountService;

    private void validateRecord(Record record) {
        if (record.getAmount() == null || record.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("金额必须大于0");
        }
        if (record.getType() == null || !VALID_TYPES.contains(record.getType())) {
            throw new RuntimeException("类型必须为 expense 或 income");
        }
        if (record.getDate() == null) {
            throw new RuntimeException("日期不能为空");
        }
        if (record.getDate().isAfter(LocalDate.now())) {
            throw new RuntimeException("日期不能是未来");
        }
    }

    public List<Record> getList(Long userId) {
        return recordRepository.findByUserIdOrderByDateDesc(userId);
    }

    public List<Record> getListByDateRange(Long userId, LocalDate startDate, LocalDate endDate) {
        return recordRepository.findByUserIdAndDateBetween(userId, startDate, endDate);
    }

    @Transactional
    public Record create(Long userId, Record record) {
        validateRecord(record);
        record.setUserId(userId);
        BigDecimal balanceChange = record.getType().equals("expense")
                ? record.getAmount().negate() : record.getAmount();
        Record saved = recordRepository.save(record);
        if (record.getAccountId() != null) {
            accountService.updateBalance(record.getAccountId(), userId, balanceChange);
        }
        return saved;
    }

    @Transactional
    public Record update(Long id, Long userId, Record record) {
        validateRecord(record);
        Record existing = recordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("记录不存在"));
        if (!existing.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改此记录");
        }
        BigDecimal oldChange = existing.getType().equals("expense")
                ? existing.getAmount().negate() : existing.getAmount();
        BigDecimal newChange = record.getType().equals("expense")
                ? record.getAmount().negate() : record.getAmount();
        if (existing.getAccountId() != null) {
            accountService.updateBalance(existing.getAccountId(), userId, oldChange.negate());
        }
        existing.setAmount(record.getAmount());
        existing.setType(record.getType());
        existing.setCategoryId(record.getCategoryId());
        existing.setAccountId(record.getAccountId());
        existing.setNote(record.getNote());
        existing.setDate(record.getDate());
        Record updated = recordRepository.save(existing);
        if (record.getAccountId() != null) {
            accountService.updateBalance(record.getAccountId(), userId, newChange);
        }
        return updated;
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Record record = recordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("记录不存在"));
        if (!record.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此记录");
        }
        BigDecimal balanceChange = record.getType().equals("expense")
                ? record.getAmount().negate() : record.getAmount();
        if (record.getAccountId() != null) {
            accountService.updateBalance(record.getAccountId(), userId, balanceChange.negate());
        }
        recordRepository.delete(record);
    }
}

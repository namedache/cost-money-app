package com.example.costmoneyapp.service;

import com.example.costmoneyapp.entity.QuickRecord;
import com.example.costmoneyapp.repository.QuickRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class QuickRecordService {

    private static final Logger log = LoggerFactory.getLogger(QuickRecordService.class);

    @Autowired
    private QuickRecordRepository quickRecordRepository;

    public List<QuickRecord> getList(Long userId) {
        log.debug("Getting quick record list for userId: {}", userId);
        return quickRecordRepository.findByUserIdOrderBySortOrder(userId);
    }

    @Transactional
    public QuickRecord create(Long userId, QuickRecord quickRecord) {
        log.info("Creating quick record: {} for userId: {}", quickRecord.getName(), userId);
        quickRecord.setUserId(userId);
        return quickRecordRepository.save(quickRecord);
    }

    @Transactional
    public QuickRecord update(Long id, Long userId, QuickRecord quickRecord) {
        log.info("Updating quick record id: {} for userId: {}", id, userId);
        QuickRecord existing = quickRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("快捷记账不存在"));
        if (!existing.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改此快捷记账");
        }
        existing.setName(quickRecord.getName());
        existing.setIcon(quickRecord.getIcon());
        existing.setColor(quickRecord.getColor());
        existing.setAmount(quickRecord.getAmount());
        existing.setCategoryId(quickRecord.getCategoryId());
        existing.setAccountId(quickRecord.getAccountId());
        existing.setSortOrder(quickRecord.getSortOrder());
        return quickRecordRepository.save(existing);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        log.info("Deleting quick record id: {} for userId: {}", id, userId);
        QuickRecord quickRecord = quickRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("快捷记账不存在"));
        if (!quickRecord.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此快捷记账");
        }
        quickRecordRepository.delete(quickRecord);
    }
}
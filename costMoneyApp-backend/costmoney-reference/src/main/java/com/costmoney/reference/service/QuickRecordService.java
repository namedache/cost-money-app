package com.costmoney.reference.service;
import com.costmoney.reference.entity.QuickRecord;
import com.costmoney.reference.repository.QuickRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class QuickRecordService {
    @Autowired private QuickRecordRepository repository;

    public List<QuickRecord> getList(Long userId) { return repository.findByUserIdOrderBySortOrder(userId); }
    @Transactional
    public QuickRecord create(Long userId, QuickRecord qr) { qr.setUserId(userId); return repository.save(qr); }
    @Transactional
    public QuickRecord update(Long id, Long userId, QuickRecord qr) {
        QuickRecord existing = repository.findById(id).orElseThrow(() -> new RuntimeException("快捷方式不存在"));
        if (!existing.getUserId().equals(userId)) throw new RuntimeException("无权修改");
        existing.setName(qr.getName()); existing.setIcon(qr.getIcon()); existing.setColor(qr.getColor());
        existing.setAmount(qr.getAmount()); existing.setCategoryId(qr.getCategoryId());
        existing.setAccountId(qr.getAccountId()); existing.setSortOrder(qr.getSortOrder());
        return repository.save(existing);
    }
    @Transactional
    public void delete(Long id, Long userId) {
        QuickRecord qr = repository.findById(id).orElseThrow(() -> new RuntimeException("快捷方式不存在"));
        if (!qr.getUserId().equals(userId)) throw new RuntimeException("无权删除");
        repository.delete(qr);
    }
}

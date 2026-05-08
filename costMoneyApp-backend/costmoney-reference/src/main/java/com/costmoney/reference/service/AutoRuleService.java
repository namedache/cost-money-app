package com.costmoney.reference.service;
import com.costmoney.reference.entity.AutoRule;
import com.costmoney.reference.repository.AutoRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AutoRuleService {
    @Autowired private AutoRuleRepository repository;

    public List<AutoRule> getList(Long userId) { return repository.findByUserIdAndIsActiveTrue(userId); }
    public List<AutoRule> getAllActive() { return repository.findByIsActiveTrue(); }
    @Transactional
    public AutoRule create(Long userId, AutoRule rule) { rule.setUserId(userId); return repository.save(rule); }
    @Transactional
    public AutoRule update(Long id, Long userId, AutoRule rule) {
        AutoRule existing = repository.findById(id).orElseThrow(() -> new RuntimeException("规则不存在"));
        if (!existing.getUserId().equals(userId)) throw new RuntimeException("无权修改");
        existing.setName(rule.getName()); existing.setKeyword(rule.getKeyword());
        existing.setAmount(rule.getAmount()); existing.setCategoryId(rule.getCategoryId());
        existing.setAccountId(rule.getAccountId()); existing.setPeriod(rule.getPeriod());
        existing.setIntervalDays(rule.getIntervalDays()); existing.setActive(rule.isActive());
        return repository.save(existing);
    }
    @Transactional
    public void delete(Long id, Long userId) {
        AutoRule rule = repository.findById(id).orElseThrow(() -> new RuntimeException("规则不存在"));
        if (!rule.getUserId().equals(userId)) throw new RuntimeException("无权删除");
        repository.delete(rule);
    }
}

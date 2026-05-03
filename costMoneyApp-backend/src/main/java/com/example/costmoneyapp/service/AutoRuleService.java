package com.example.costmoneyapp.service;

import com.example.costmoneyapp.entity.AutoRule;
import com.example.costmoneyapp.repository.AutoRuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AutoRuleService {

    private static final Logger log = LoggerFactory.getLogger(AutoRuleService.class);

    @Autowired
    private AutoRuleRepository autoRuleRepository;

    public List<AutoRule> getList(Long userId) {
        log.debug("Getting auto rule list for userId: {}", userId);
        return autoRuleRepository.findByUserIdAndIsActiveTrue(userId);
    }

    @Transactional
    public AutoRule create(Long userId, AutoRule rule) {
        log.info("Creating auto rule: {} for userId: {}", rule.getName(), userId);
        rule.setUserId(userId);
        rule.setIsActive(true);
        return autoRuleRepository.save(rule);
    }

    @Transactional
    public AutoRule update(Long id, Long userId, AutoRule rule) {
        log.info("Updating auto rule id: {} for userId: {}", id, userId);
        AutoRule existing = autoRuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("自动规则不存在"));
        if (!existing.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改此规则");
        }
        existing.setName(rule.getName());
        existing.setKeyword(rule.getKeyword());
        existing.setCategoryId(rule.getCategoryId());
        existing.setAccountId(rule.getAccountId());
        existing.setAmount(rule.getAmount());
        existing.setIsActive(rule.getIsActive());
        return autoRuleRepository.save(existing);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        log.info("Deleting auto rule id: {} for userId: {}", id, userId);
        AutoRule rule = autoRuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("自动规则不存在"));
        if (!rule.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此规则");
        }
        autoRuleRepository.delete(rule);
    }
}
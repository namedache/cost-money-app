package com.example.costmoneyapp.service;

import com.example.costmoneyapp.entity.Account;
import com.example.costmoneyapp.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private static final Logger log = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private AccountRepository accountRepository;

    public List<Account> getList(Long userId) {
        log.debug("Getting account list for userId: {}", userId);
        return accountRepository.findByUserIdOrderBySortOrder(userId);
    }

    @Transactional
    public Account create(Long userId, Account account) {
        if (account.getName() == null || account.getName().isBlank()) {
            throw new RuntimeException("账户名称不能为空");
        }
        log.info("Creating account: {} for userId: {}", account.getName(), userId);
        account.setUserId(userId);
        if (account.getBalance() == null) {
            account.setBalance(BigDecimal.ZERO);
        }
        return accountRepository.save(account);
    }

    @Transactional
    public Account update(Long id, Long userId, Account account) {
        log.info("Updating account id: {} for userId: {}", id, userId);
        Account existing = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("账户不存在"));
        if (!existing.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改此账户");
        }
        existing.setName(account.getName());
        existing.setIcon(account.getIcon());
        existing.setColor(account.getColor());
        existing.setBalance(account.getBalance());
        existing.setSortOrder(account.getSortOrder());
        existing.setIsActive(account.getIsActive());
        return accountRepository.save(existing);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        log.info("Deleting account id: {} for userId: {}", id, userId);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("账户不存在"));
        if (!account.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此账户");
        }
        accountRepository.delete(account);
    }

    @Transactional
    public void updateBalance(Long accountId, Long userId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("账户不存在"));
        if (!account.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此账户");
        }
        account.setBalance(account.getBalance().add(amount));
        accountRepository.save(account);
    }
}
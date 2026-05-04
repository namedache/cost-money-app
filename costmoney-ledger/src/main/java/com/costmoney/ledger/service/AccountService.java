package com.costmoney.ledger.service;

import com.costmoney.ledger.entity.Account;
import com.costmoney.ledger.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public List<Account> getList(Long userId) {
        return accountRepository.findByUserIdOrderBySortOrder(userId);
    }

    @Transactional
    public Account create(Long userId, Account account) {
        account.setUserId(userId);
        return accountRepository.save(account);
    }

    @Transactional
    public Account update(Long id, Long userId, Account account) {
        Account existing = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("账户不存在"));
        if (!existing.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改此账户");
        }
        existing.setName(account.getName());
        existing.setIcon(account.getIcon());
        existing.setColor(account.getColor());
        existing.setSortOrder(account.getSortOrder());
        existing.setActive(account.isActive());
        return accountRepository.save(existing);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("账户不存在"));
        if (!account.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此账户");
        }
        accountRepository.delete(account);
    }

    @Transactional
    public void updateBalance(Long accountId, Long userId, BigDecimal change) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("账户不存在"));
        if (!account.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此账户");
        }
        account.setBalance(account.getBalance().add(change));
        accountRepository.save(account);
    }
}

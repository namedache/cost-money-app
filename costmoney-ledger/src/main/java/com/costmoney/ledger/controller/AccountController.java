package com.costmoney.ledger.controller;

import com.costmoney.common.config.UserContext;
import com.costmoney.ledger.entity.Account;
import com.costmoney.ledger.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    public ResponseEntity<List<Account>> list() {
        Long userId = UserContext.getUserId();
        return ResponseEntity.ok(accountService.getList(userId));
    }

    @PostMapping
    public ResponseEntity<Account> create(@RequestBody Account account) {
        Long userId = UserContext.getUserId();
        return ResponseEntity.ok(accountService.create(userId, account));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Account> update(@PathVariable Long id, @RequestBody Account account) {
        Long userId = UserContext.getUserId();
        return ResponseEntity.ok(accountService.update(id, userId, account));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        accountService.delete(id, userId);
        return ResponseEntity.ok(Map.of("message", "删除成功"));
    }
}

package com.example.costmoneyapp.controller;

import com.example.costmoneyapp.config.CurrentUser;
import com.example.costmoneyapp.entity.Account;
import com.example.costmoneyapp.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private static final Logger log = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    private AccountService accountService;

    @GetMapping
    public ResponseEntity<List<Account>> list() {
        Long userId = CurrentUser.getId();
        log.info("GET /api/accounts - userId: {}", userId);
        return ResponseEntity.ok(accountService.getList(userId));
    }

    @PostMapping
    public ResponseEntity<Account> create(@RequestBody Account account) {
        Long userId = CurrentUser.getId();
        log.info("POST /api/accounts - userId: {}, account: {}", userId, account.getName());
        Account created = accountService.create(userId, account);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Account> update(@PathVariable Long id, @RequestBody Account account) {
        Long userId = CurrentUser.getId();
        log.info("PUT /api/accounts/{} - userId: {}", id, userId);
        Account updated = accountService.update(id, userId, account);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        Long userId = CurrentUser.getId();
        log.info("DELETE /api/accounts/{} - userId: {}", id, userId);
        accountService.delete(id, userId);
        return ResponseEntity.ok(Map.of("message", "删除成功"));
    }
}

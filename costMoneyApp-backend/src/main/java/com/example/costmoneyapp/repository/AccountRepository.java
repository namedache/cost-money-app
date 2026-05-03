package com.example.costmoneyapp.repository;

import com.example.costmoneyapp.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUserIdOrderBySortOrder(Long userId);
}
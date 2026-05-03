package com.example.costmoneyapp.repository;

import com.example.costmoneyapp.entity.AutoRule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AutoRuleRepository extends JpaRepository<AutoRule, Long> {
    List<AutoRule> findByUserIdAndIsActiveTrue(Long userId);
    List<AutoRule> findByIsActiveTrue();
}
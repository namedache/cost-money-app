package com.costmoney.budget.repository;
import com.costmoney.budget.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUserIdAndIsActiveTrue(Long userId);
}

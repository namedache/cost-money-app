package com.costmoney.ledger.repository;

import com.costmoney.ledger.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findByUserIdOrderByDateDesc(Long userId);

    @Query("SELECT r FROM Record r WHERE r.userId = :userId AND r.date >= :startDate AND r.date <= :endDate ORDER BY r.date DESC")
    List<Record> findByUserIdAndDateBetween(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<Record> findByUserIdAndAccountId(Long userId, Long accountId);
}

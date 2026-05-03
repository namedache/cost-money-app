package com.example.costmoneyapp.repository;

import com.example.costmoneyapp.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDate;
import java.util.List;

public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findByUserIdOrderByDateDesc(Long userId);

    @Query("SELECT r FROM Record r WHERE r.userId = ?1 AND r.date >= ?2 AND r.date <= ?3 ORDER BY r.date DESC")
    List<Record> findByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    List<Record> findByUserIdAndAccountId(Long userId, Long accountId);
}
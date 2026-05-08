package com.costmoney.reference.repository;
import com.costmoney.reference.entity.QuickRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuickRecordRepository extends JpaRepository<QuickRecord, Long> {
    List<QuickRecord> findByUserIdOrderBySortOrder(Long userId);
}

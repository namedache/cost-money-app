package com.costmoney.reference.repository;
import com.costmoney.reference.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserIdOrUserIdIsNullOrderBySortOrder(Long userId);
    List<Category> findByUserIdAndParentIdIsNull(Long userId);
    List<Category> findByParentId(Long parentId);
    List<Category> findByUserIdOrUserIdIsNull(Long userId);
}

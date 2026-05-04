package com.costmoney.reference.service;
import com.costmoney.reference.entity.Category;
import com.costmoney.reference.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CategoryService {
    @Autowired private CategoryRepository categoryRepository;

    public List<Category> getList(Long userId) {
        return categoryRepository.findByUserIdOrUserIdIsNullOrderBySortOrder(userId);
    }
    public List<Category> getParents(Long userId, String type) {
        return categoryRepository.findByUserIdAndParentIdIsNull(userId);
    }
    public List<Category> getChildren(Long parentId) {
        return categoryRepository.findByParentId(parentId);
    }
    @Transactional
    public Category create(Long userId, Category cat) {
        cat.setUserId(userId);
        cat.setSystem(false);
        return categoryRepository.save(cat);
    }
    @Transactional
    public Category update(Long id, Long userId, Category cat) {
        Category existing = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("分类不存在"));
        if (existing.isSystem()) throw new RuntimeException("系统分类不能修改");
        if (!existing.getUserId().equals(userId)) throw new RuntimeException("无权修改此分类");
        existing.setName(cat.getName());
        existing.setIcon(cat.getIcon());
        existing.setColor(cat.getColor());
        return categoryRepository.save(existing);
    }
    @Transactional
    public void delete(Long id, Long userId) {
        Category cat = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("分类不存在"));
        if (cat.isSystem()) throw new RuntimeException("系统分类不能删除");
        if (!cat.getUserId().equals(userId)) throw new RuntimeException("无权删除此分类");
        categoryRepository.delete(cat);
    }
}

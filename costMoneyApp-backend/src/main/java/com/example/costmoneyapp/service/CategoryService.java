package com.example.costmoneyapp.service;

import com.example.costmoneyapp.entity.Category;
import com.example.costmoneyapp.repository.CategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getList(Long userId) {
        log.debug("Getting category list for userId: {}", userId);
        return categoryRepository.findByUserIdOrUserIdIsNullOrderBySortOrder(userId);
    }

    public List<Category> getParentCategories(Long userId, String type) {
        log.debug("Getting parent categories for userId: {}, type: {}", userId, type);
        List<Category> parents = categoryRepository.findByUserIdAndParentIdIsNull(userId);
        List<Category> systemParents = categoryRepository.findByParentId(null);
        parents.addAll(systemParents);
        return parents;
    }

    public List<Category> getChildren(Long parentId) {
        return categoryRepository.findByParentId(parentId);
    }

    @Transactional
    public Category create(Long userId, Category category) {
        log.info("Creating category: {} for userId: {}", category.getName(), userId);
        category.setUserId(userId);
        category.setIsSystem(false);
        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(Long id, Long userId, Category category) {
        log.info("Updating category id: {} for userId: {}", id, userId);
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        if (existing.getIsSystem()) {
            throw new RuntimeException("系统分类不能修改");
        }
        if (!existing.getUserId().equals(userId)) {
            throw new RuntimeException("无权修改此分类");
        }
        existing.setName(category.getName());
        existing.setIcon(category.getIcon());
        existing.setColor(category.getColor());
        return categoryRepository.save(existing);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        log.info("Deleting category id: {} for userId: {}", id, userId);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("分类不存在"));
        if (category.getIsSystem()) {
            throw new RuntimeException("系统分类不能删除");
        }
        if (!category.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此分类");
        }
        categoryRepository.delete(category);
    }
}
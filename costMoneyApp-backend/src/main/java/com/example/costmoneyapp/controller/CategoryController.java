package com.example.costmoneyapp.controller;

import com.example.costmoneyapp.config.CurrentUser;
import com.example.costmoneyapp.entity.Category;
import com.example.costmoneyapp.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private static final Logger log = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> list(@RequestParam(required = false) String type) {
        Long userId = CurrentUser.getId();
        log.info("GET /api/categories - userId: {}, type: {}", userId, type);
        List<Category> categories = categoryService.getList(userId);
        if (type != null) {
            categories = categories.stream()
                    .filter(c -> type.equals(c.getType()))
                    .toList();
        }
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/parents")
    public ResponseEntity<List<Category>> parentList(@RequestParam(required = false) String type) {
        Long userId = CurrentUser.getId();
        log.info("GET /api/categories/parents - userId: {}, type: {}", userId, type);
        List<Category> parents = categoryService.getParentCategories(userId, type);
        if (type != null) {
            parents = parents.stream()
                    .filter(c -> type.equals(c.getType()))
                    .toList();
        }
        return ResponseEntity.ok(parents);
    }

    @GetMapping("/{parentId}/children")
    public ResponseEntity<List<Category>> children(@PathVariable Long parentId) {
        log.info("GET /api/categories/{}/children", parentId);
        return ResponseEntity.ok(categoryService.getChildren(parentId));
    }

    @PostMapping
    public ResponseEntity<Category> create(@RequestBody Category category) {
        Long userId = CurrentUser.getId();
        log.info("POST /api/categories - userId: {}, category: {}", userId, category.getName());
        Category created = categoryService.create(userId, category);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> update(@PathVariable Long id, @RequestBody Category category) {
        Long userId = CurrentUser.getId();
        log.info("PUT /api/categories/{} - userId: {}", id, userId);
        Category updated = categoryService.update(id, userId, category);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id) {
        Long userId = CurrentUser.getId();
        log.info("DELETE /api/categories/{} - userId: {}", id, userId);
        categoryService.delete(id, userId);
        return ResponseEntity.ok(Map.of("message", "删除成功"));
    }
}

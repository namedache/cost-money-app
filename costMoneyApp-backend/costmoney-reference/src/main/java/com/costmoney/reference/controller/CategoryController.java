package com.costmoney.reference.controller;
import com.costmoney.common.config.UserContext;
import com.costmoney.reference.entity.Category;
import com.costmoney.reference.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
    @Autowired private CategoryService service;

    @GetMapping public List<Category> list(@RequestParam(required = false) String type) { return service.getList(UserContext.getUserId()); }
    @GetMapping("/parents") public List<Category> parents(@RequestParam(required = false) String type) { return service.getParents(UserContext.getUserId(), type); }
    @GetMapping("/{parentId}/children") public List<Category> children(@PathVariable Long parentId) { return service.getChildren(parentId); }
    @PostMapping public Category create(@RequestBody Category cat) { return service.create(UserContext.getUserId(), cat); }
    @PutMapping("/{id}") public Category update(@PathVariable Long id, @RequestBody Category cat) { return service.update(id, UserContext.getUserId(), cat); }
    @DeleteMapping("/{id}") public Map<String, String> delete(@PathVariable Long id) { service.delete(id, UserContext.getUserId()); return Map.of("message", "删除成功"); }
}

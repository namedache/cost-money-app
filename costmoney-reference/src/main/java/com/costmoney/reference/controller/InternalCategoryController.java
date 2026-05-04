package com.costmoney.reference.controller;
import com.costmoney.reference.entity.Category;
import com.costmoney.reference.entity.AutoRule;
import com.costmoney.reference.repository.CategoryRepository;
import com.costmoney.reference.repository.AutoRuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/internal")
public class InternalCategoryController {
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private AutoRuleRepository autoRuleRepository;

    @GetMapping("/categories") public List<Category> getByIds(@RequestParam List<Long> ids) { return categoryRepository.findAllById(ids); }
    @GetMapping("/auto-rules/active") public List<AutoRule> getActiveRules() { return autoRuleRepository.findByIsActiveTrue(); }
}

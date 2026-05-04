package com.costmoney.reference.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "quick_records")
public class QuickRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonIgnore @Column(nullable = false) private Long userId;
    @Column(nullable = false) private String name;
    private String icon;
    private String color;
    @Column(nullable = false) private BigDecimal amount;
    @Column(name = "category_id") private Long categoryId;
    @Column(name = "account_id") private Long accountId;
    @Column(name = "sort_order") private int sortOrder = 0;
    @Column(name = "created_at") private LocalDateTime createdAt;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public String getIcon() { return icon; } public void setIcon(String icon) { this.icon = icon; }
    public String getColor() { return color; } public void setColor(String color) { this.color = color; }
    public BigDecimal getAmount() { return amount; } public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Long getCategoryId() { return categoryId; } public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public Long getAccountId() { return accountId; } public void setAccountId(Long accountId) { this.accountId = accountId; }
    public int getSortOrder() { return sortOrder; } public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}

package com.costmoney.budget.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "budgets")
public class Budget {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonIgnore @Column(nullable = false) private Long userId;
    @Column(name = "category_id") private Long categoryId;
    @Column(nullable = false) private String type;
    @Column(nullable = false) private String period;
    @Column(nullable = false) private BigDecimal amount;
    @Column(name = "alert_threshold") private int alertThreshold = 80;
    @Column(name = "is_active") private boolean isActive = true;
    @Column(name = "created_at") private LocalDateTime createdAt;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public Long getCategoryId() { return categoryId; } public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getType() { return type; } public void setType(String type) { this.type = type; }
    public String getPeriod() { return period; } public void setPeriod(String period) { this.period = period; }
    public BigDecimal getAmount() { return amount; } public void setAmount(BigDecimal amount) { this.amount = amount; }
    public int getAlertThreshold() { return alertThreshold; } public void setAlertThreshold(int alertThreshold) { this.alertThreshold = alertThreshold; }
    public boolean isActive() { return isActive; } public void setActive(boolean active) { isActive = active; }
}

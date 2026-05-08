package com.costmoney.reference.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "categories")
public class Category {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JsonIgnore @Column private Long userId;
    @Column(name = "parent_id") private Long parentId;
    @Column(nullable = false) private String name;
    private String icon;
    private String color;
    @Column(nullable = false) private String type;
    @Column(name = "is_system") private boolean isSystem = false;
    @Column(name = "sort_order") private int sortOrder = 0;
    @Column(name = "created_at") private LocalDateTime createdAt;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
    public Long getId() { return id; } public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; } public void setUserId(Long userId) { this.userId = userId; }
    public Long getParentId() { return parentId; } public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public String getIcon() { return icon; } public void setIcon(String icon) { this.icon = icon; }
    public String getColor() { return color; } public void setColor(String color) { this.color = color; }
    public String getType() { return type; } public void setType(String type) { this.type = type; }
    public boolean isSystem() { return isSystem; } public void setSystem(boolean system) { isSystem = system; }
    public int getSortOrder() { return sortOrder; } public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

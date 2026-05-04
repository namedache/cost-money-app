CREATE TABLE IF NOT EXISTS budgets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category_id BIGINT,
    type VARCHAR(255) NOT NULL,
    period VARCHAR(255) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    alert_threshold INT DEFAULT 80,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
CREATE INDEX idx_budgets_user ON budgets(user_id);

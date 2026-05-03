-- Users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Accounts table
CREATE TABLE IF NOT EXISTS accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    icon VARCHAR(255),
    color VARCHAR(255),
    balance DECIMAL(19,2) DEFAULT 0,
    sort_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_account_user FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Categories table
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    parent_id BIGINT,
    name VARCHAR(255) NOT NULL,
    icon VARCHAR(255),
    color VARCHAR(255),
    type VARCHAR(255) NOT NULL,
    is_system BOOLEAN DEFAULT FALSE,
    sort_order INT DEFAULT 0,
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_category_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES categories(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Records table
CREATE TABLE IF NOT EXISTS records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    type VARCHAR(255) NOT NULL,
    category_id BIGINT,
    account_id BIGINT,
    note VARCHAR(255),
    date DATE NOT NULL,
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_record_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_record_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_record_account FOREIGN KEY (account_id) REFERENCES accounts(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Budgets table
CREATE TABLE IF NOT EXISTS budgets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category_id BIGINT,
    type VARCHAR(255) NOT NULL,
    period VARCHAR(255) NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    alert_threshold INT DEFAULT 80,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_budget_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_budget_category FOREIGN KEY (category_id) REFERENCES categories(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Auto rules table
CREATE TABLE IF NOT EXISTS auto_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    keyword VARCHAR(255),
    category_id BIGINT,
    account_id BIGINT,
    amount DECIMAL(19,2),
    period VARCHAR(255) DEFAULT 'daily',
    interval_days INT DEFAULT 1,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_auto_rule_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_auto_rule_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_auto_rule_account FOREIGN KEY (account_id) REFERENCES accounts(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Quick records table
CREATE TABLE IF NOT EXISTS quick_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    icon VARCHAR(255),
    color VARCHAR(255),
    amount DECIMAL(19,2) NOT NULL,
    category_id BIGINT,
    account_id BIGINT,
    sort_order INT DEFAULT 0,
    created_at DATETIME(6) DEFAULT CURRENT_TIMESTAMP(6),
    CONSTRAINT fk_quick_record_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_quick_record_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT fk_quick_record_account FOREIGN KEY (account_id) REFERENCES accounts(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Indexes for common query patterns
CREATE INDEX idx_records_user_date ON records(user_id, date);
CREATE INDEX idx_records_user_type ON records(user_id, type);
CREATE INDEX idx_categories_user_type ON categories(user_id, type);
CREATE INDEX idx_accounts_user ON accounts(user_id);
CREATE INDEX idx_budgets_user ON budgets(user_id);
CREATE INDEX idx_auto_rules_user ON auto_rules(user_id);
CREATE INDEX idx_quick_records_user ON quick_records(user_id);

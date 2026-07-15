-- ============================================
-- SEARCH & FILTERING MIGRATION
-- ============================================

CREATE TABLE IF NOT EXISTS recent_searches (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    query VARCHAR(255) NOT NULL,
    user_id BIGINT,
    search_count INT NOT NULL DEFAULT 1,
    last_searched_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS search_synonyms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    keyword VARCHAR(255) NOT NULL UNIQUE,
    synonyms VARCHAR(512) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed some test synonyms
INSERT IGNORE INTO search_synonyms (keyword, synonyms) VALUES ('laptop', 'notebook,macbook,computer');
INSERT IGNORE INTO search_synonyms (keyword, synonyms) VALUES ('phone', 'smartphone,mobile,cellphone');
INSERT IGNORE INTO search_synonyms (keyword, synonyms) VALUES ('shoes', 'sneakers,boots,footwear');

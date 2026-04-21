-- AI 模型信息表新增兜底模型字段
-- 作用：当模型本身没有任何可用供应商路由时，可自动切换到指定模型

SET @fallback_model_id_column_exists = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'ai_model_info'
      AND COLUMN_NAME = 'fallback_model_id'
);
SET @fallback_model_id_column_sql = IF(
    @fallback_model_id_column_exists = 0,
    'ALTER TABLE `ai_model_info` ADD COLUMN `fallback_model_id` bigint DEFAULT NULL COMMENT ''兜底模型ID；当前模型不可路由时自动切换到该模型'' AFTER `sort`',
    'SELECT ''fallback_model_id column already exists'''
);
PREPARE stmt FROM @fallback_model_id_column_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @fallback_model_id_index_exists = (
    SELECT COUNT(1)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'ai_model_info'
      AND INDEX_NAME = 'idx_ai_model_info_fallback_model_id'
);
SET @fallback_model_id_index_sql = IF(
    @fallback_model_id_index_exists = 0,
    'ALTER TABLE `ai_model_info` ADD INDEX `idx_ai_model_info_fallback_model_id` (`fallback_model_id`)',
    'SELECT ''idx_ai_model_info_fallback_model_id already exists'''
);
PREPARE stmt FROM @fallback_model_id_index_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

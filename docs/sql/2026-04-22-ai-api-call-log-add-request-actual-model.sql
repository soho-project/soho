-- AI API 调用日志新增请求模型与实际模型字段
-- 作用：区分客户端请求模型与最终上游命中的实际模型，便于排障与对账

SET @request_model_column_exists = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'ai_api_call_log'
      AND COLUMN_NAME = 'request_model'
);
SET @request_model_column_sql = IF(
    @request_model_column_exists = 0,
    'ALTER TABLE `ai_api_call_log` ADD COLUMN `request_model` varchar(128) DEFAULT NULL COMMENT ''客户端请求模型'' AFTER `model`',
    'SELECT ''request_model column already exists'''
);
PREPARE stmt FROM @request_model_column_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @actual_model_column_exists = (
    SELECT COUNT(1)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'ai_api_call_log'
      AND COLUMN_NAME = 'actual_model'
);
SET @actual_model_column_sql = IF(
    @actual_model_column_exists = 0,
    'ALTER TABLE `ai_api_call_log` ADD COLUMN `actual_model` varchar(128) DEFAULT NULL COMMENT ''实际调用模型'' AFTER `request_model`',
    'SELECT ''actual_model column already exists'''
);
PREPARE stmt FROM @actual_model_column_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @request_model_index_exists = (
    SELECT COUNT(1)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'ai_api_call_log'
      AND INDEX_NAME = 'idx_ai_api_call_log_request_model'
);
SET @request_model_index_sql = IF(
    @request_model_index_exists = 0,
    'ALTER TABLE `ai_api_call_log` ADD INDEX `idx_ai_api_call_log_request_model` (`request_model`)',
    'SELECT ''idx_ai_api_call_log_request_model already exists'''
);
PREPARE stmt FROM @request_model_index_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @actual_model_index_exists = (
    SELECT COUNT(1)
    FROM information_schema.STATISTICS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'ai_api_call_log'
      AND INDEX_NAME = 'idx_ai_api_call_log_actual_model'
);
SET @actual_model_index_sql = IF(
    @actual_model_index_exists = 0,
    'ALTER TABLE `ai_api_call_log` ADD INDEX `idx_ai_api_call_log_actual_model` (`actual_model`)',
    'SELECT ''idx_ai_api_call_log_actual_model already exists'''
);
PREPARE stmt FROM @actual_model_index_sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

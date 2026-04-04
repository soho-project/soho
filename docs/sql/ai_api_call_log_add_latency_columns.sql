ALTER TABLE `ai_api_call_log`
    ADD COLUMN IF NOT EXISTS `total_ms` bigint DEFAULT NULL COMMENT '总耗时(毫秒)' AFTER `wallet_log_id`,
    ADD COLUMN IF NOT EXISTS `first_token_ms` bigint DEFAULT NULL COMMENT '首字耗时(毫秒)' AFTER `total_ms`;

-- ai_user_api_key 增加有效期截止时间，NULL 表示永久有效

USE `soho_admin`;

ALTER TABLE `ai_user_api_key`
    ADD COLUMN `expire_end_time` datetime DEFAULT NULL COMMENT '有效期截止时间，NULL表示永久有效' AFTER `status`;

ALTER TABLE `ai_user_api_key`
    ADD KEY `idx_ai_user_api_key_expire_end_time` (`expire_end_time`);

SELECT column_name, data_type, is_nullable
FROM information_schema.columns
WHERE table_schema = 'soho_admin'
  AND table_name = 'ai_user_api_key'
  AND column_name = 'expire_end_time';

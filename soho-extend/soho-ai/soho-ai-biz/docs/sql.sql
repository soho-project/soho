CREATE TABLE `ai_user_api_key` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `provider_config_id` bigint NOT NULL,
  `name` varchar(128) DEFAULT NULL,
  `api_key_prefix` varchar(32) NOT NULL,
  `api_key_hash` varchar(128) NOT NULL,
  `status` tinyint NOT NULL DEFAULT 1,
  `last_used_time` datetime DEFAULT NULL,
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_ai_user_api_key_user_id` (`user_id`),
  KEY `idx_ai_user_api_key_hash` (`api_key_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `ai_api_call_log` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `request_id` varchar(64) NOT NULL,
  `user_id` bigint NOT NULL,
  `api_key_id` bigint NOT NULL,
  `provider_config_id` bigint NOT NULL,
  `endpoint` varchar(255) NOT NULL,
  `model` varchar(128) DEFAULT NULL,
  `prompt_tokens` int NOT NULL DEFAULT 0,
  `completion_tokens` int NOT NULL DEFAULT 0,
  `total_tokens` int NOT NULL DEFAULT 0,
  `amount` decimal(18,4) NOT NULL DEFAULT 0.0000,
  `status` tinyint NOT NULL DEFAULT 1,
  `error_message` varchar(500) DEFAULT NULL,
  `wallet_log_id` bigint DEFAULT NULL,
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ai_api_call_log_request_id` (`request_id`),
  KEY `idx_ai_api_call_log_user_id` (`user_id`),
  KEY `idx_ai_api_call_log_api_key_id` (`api_key_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `ai_chat_session` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `provider_code` varchar(64) NOT NULL,
  `model` varchar(128) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `last_message` varchar(500) DEFAULT NULL,
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_ai_chat_session_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `ai_chat_session_message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `session_id` bigint NOT NULL,
  `role` varchar(32) NOT NULL,
  `content` text,
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_ai_chat_session_message_session_id` (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `ai_model_info` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `model_name` varchar(128) NOT NULL,
  `model_desc` varchar(255) DEFAULT NULL,
  `model_detail` text,
  `status` tinyint NOT NULL DEFAULT 1,
  `prompt_price` decimal(18,4) DEFAULT 0.0000,
  `completion_price` decimal(18,4) DEFAULT 0.0000,
  `sort` int DEFAULT 0,
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_ai_model_info_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `ai_provider_model_rel` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `provider_config_id` bigint NOT NULL,
  `model_info_id` bigint NOT NULL,
  `status` tinyint NOT NULL DEFAULT 1,
  `sort` int DEFAULT 0,
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ai_provider_model_rel` (`provider_config_id`, `model_info_id`),
  KEY `idx_ai_provider_model_rel_provider_config_id` (`provider_config_id`),
  KEY `idx_ai_provider_model_rel_model_info_id` (`model_info_id`),
  KEY `idx_ai_provider_model_rel_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `ai_provider_config`
  ADD COLUMN `supported_models` text COMMENT '支持模型列表，建议按逗号/换行或JSON数组存储' AFTER `default_model`;

ALTER TABLE `ai_provider_config`
  ADD COLUMN `weight` int NOT NULL DEFAULT 1 COMMENT '路由权重（值越大被选中概率越高）' AFTER `timeout_ms`;

ALTER TABLE `ai_provider_config`
  ADD COLUMN `provider_unique_id` varchar(128) DEFAULT NULL COMMENT '服务提供者唯一识别ID（可为空）' AFTER `provider`,
  ADD UNIQUE KEY `uk_ai_provider_config_provider_unique_id` (`provider_unique_id`);

-- config_json 建议增加以下配置：
-- {
--   "adapter": "codexResponses",
--   "codexResponsesPath": "/backend-api/codex/responses",
--   "billingEnabled": true,
--   "billingWalletTypeId": 1,
--   "promptPricePer1kTokens": 0.02,
--   "completionPricePer1kTokens": 0.08
-- }

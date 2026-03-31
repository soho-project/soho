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

CREATE TABLE `ai_member_card` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL COMMENT '会员卡名称',
  `card_type` varchar(32) NOT NULL COMMENT '卡类型: monthly/quarterly/yearly',
  `limit_mode` varchar(32) NOT NULL COMMENT '限制模式: by_request/by_token',
  `validity_days` int NOT NULL DEFAULT 30 COMMENT '有效天数',
  `sale_price` decimal(18,4) NOT NULL DEFAULT 0.0000 COMMENT '销售价格',
  `rate_limit_5h` int DEFAULT 100 COMMENT '5小时最大请求次数',
  `rate_limit_7d` int DEFAULT 300 COMMENT '7天最大请求次数',
  `rate_limit_5h_enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用5小时限制',
  `rate_limit_7d_enabled` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否启用7天限制',
  `rate_limit_window_5h` int NOT NULL DEFAULT 5 COMMENT '5小时窗口(小时)',
  `rate_limit_window_7d` int NOT NULL DEFAULT 7 COMMENT '7天窗口(天)',
  `weekly_prompt_token_limit` int DEFAULT NULL COMMENT '按token限制-周输入token上限',
  `weekly_completion_token_limit` int DEFAULT NULL COMMENT '按token限制-周输出token上限',
  `weekly_total_token_limit` int DEFAULT NULL COMMENT '按token限制-周总token上限',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态:0禁用,1启用',
  `sort` int DEFAULT 0 COMMENT '排序',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_ai_member_card_status` (`status`),
  KEY `idx_ai_member_card_type_mode` (`card_type`, `limit_mode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE `ai_member_card`
  ADD COLUMN `sale_price` decimal(18,4) NOT NULL DEFAULT 0.0000 COMMENT '销售价格' AFTER `validity_days`;

CREATE TABLE `ai_user_member_card` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `member_card_id` bigint NOT NULL COMMENT '会员卡模板ID',
  `no` varchar(64) NOT NULL COMMENT '会员卡号',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态:0未激活,1生效中,2已过期',
  `priority` int NOT NULL DEFAULT 0 COMMENT '优先级，值越大越优先',
  `is_selected` tinyint(1) NOT NULL DEFAULT 0 COMMENT '用户显式选择使用',
  `start_time` datetime NOT NULL COMMENT '生效开始时间',
  `end_time` datetime NOT NULL COMMENT '生效结束时间',
  `activated_time` datetime DEFAULT NULL COMMENT '激活时间',
  `source` varchar(64) DEFAULT NULL COMMENT '来源:order/admin/grant',
  `biz_no` varchar(64) DEFAULT NULL COMMENT '业务单号',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ai_user_member_card_no` (`no`),
  KEY `idx_ai_user_member_card_user_status_time` (`user_id`, `status`, `start_time`, `end_time`),
  KEY `idx_ai_user_member_card_member_card_id` (`member_card_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `ai_member_card_redeem_code` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `member_card_id` bigint NOT NULL COMMENT '会员卡模板ID',
  `batch_no` varchar(64) NOT NULL COMMENT '批次号',
  `redeem_code` varchar(64) NOT NULL COMMENT '兑换码',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态:0未使用,1已使用,2已禁用',
  `used_by_user_id` bigint DEFAULT NULL COMMENT '使用用户ID',
  `user_member_card_id` bigint DEFAULT NULL COMMENT '兑换后生成的用户会员卡ID',
  `used_time` datetime DEFAULT NULL COMMENT '使用时间',
  `expire_time` datetime DEFAULT NULL COMMENT '兑换码过期时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `updated_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_time` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ai_member_card_redeem_code_code` (`redeem_code`),
  KEY `idx_ai_member_card_redeem_code_member_card_id` (`member_card_id`),
  KEY `idx_ai_member_card_redeem_code_batch_no` (`batch_no`),
  KEY `idx_ai_member_card_redeem_code_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

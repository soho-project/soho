ALTER TABLE `ai_api_call_log`
    ADD COLUMN `client_ip` varchar(45) DEFAULT NULL COMMENT '客户端IP' AFTER `first_token_ms`,
      ADD COLUMN `user_agent` text COMMENT '客户端User-Agent' AFTER `client_ip`,
      ADD COLUMN `request_source` varchar(32) DEFAULT NULL COMMENT '请求来源' AFTER `user_agent`,
      ADD COLUMN `reject_reason` varchar(64) DEFAULT NULL COMMENT '拦截原因' AFTER `request_source`,
      ADD COLUMN `risk_hit` tinyint(1) DEFAULT 0 COMMENT '是否命中风险规则' AFTER `reject_reason`,
      ADD COLUMN `ban_hit` tinyint(1) DEFAULT 0 COMMENT '是否命中封禁' AFTER `risk_hit`;
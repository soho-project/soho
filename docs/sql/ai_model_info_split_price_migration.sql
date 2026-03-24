-- ai_model_info 增加输入/输出拆分计价字段
-- 规则：
-- 1. 模型 prompt_price / completion_price 优先
-- 2. 如果模型未配置拆分单价，再回退 provider_config.config_json 里的 prompt/completion 单价

ALTER TABLE ai_model_info
  ADD COLUMN prompt_price decimal(18,4) DEFAULT 0.0000 COMMENT '输入单价，每1K tokens' AFTER status,
  ADD COLUMN completion_price decimal(18,4) DEFAULT 0.0000 COMMENT '输出单价，每1K tokens' AFTER prompt_price;

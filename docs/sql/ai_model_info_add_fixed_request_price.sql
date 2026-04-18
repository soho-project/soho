-- ai_model_info 增加独立的按次计费单价字段
-- 用途：图片/音频等固定价接口单独配置，不再复用 prompt_price / completion_price

USE `soho_admin`;

ALTER TABLE `ai_model_info`
    ADD COLUMN `fixed_request_price` decimal(18,6) DEFAULT 0.000000 COMMENT '按次计费单价' AFTER `completion_price`;

SELECT table_name, column_name, data_type, numeric_precision, numeric_scale
FROM information_schema.columns
WHERE table_schema = 'soho_admin'
  AND table_name = 'ai_model_info'
  AND column_name = 'fixed_request_price';

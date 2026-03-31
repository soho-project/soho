-- AI 模块计费金额字段统一调整为 6 位小数
-- 目标库: soho_admin

USE `soho_admin`;

-- 1) AI 调用日志扣费金额
ALTER TABLE `ai_api_call_log`
    MODIFY COLUMN `amount` decimal(18,6) NOT NULL DEFAULT 0.000000;

-- 2) 模型输入/输出单价（每 1K tokens）
ALTER TABLE `ai_model_info`
    MODIFY COLUMN `prompt_price` decimal(18,6) DEFAULT 0.000000,
    MODIFY COLUMN `completion_price` decimal(18,6) DEFAULT 0.000000;

-- 3) AI 会员卡销售价格
ALTER TABLE `ai_member_card`
    MODIFY COLUMN `sale_price` decimal(18,6) NOT NULL DEFAULT 0.000000 COMMENT '销售价格';

-- 4) 验证
SELECT table_name, column_name, data_type, numeric_precision, numeric_scale
FROM information_schema.columns
WHERE table_schema = 'soho_admin'
  AND (
    (table_name = 'ai_api_call_log' AND column_name IN ('amount'))
    OR (table_name = 'ai_model_info' AND column_name IN ('prompt_price', 'completion_price'))
    OR (table_name = 'ai_member_card' AND column_name IN ('sale_price'))
  )
ORDER BY table_name, column_name;


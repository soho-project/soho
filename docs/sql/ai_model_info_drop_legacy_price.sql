-- 删除 ai_model_info 历史统一计价字段
-- 执行前请确认代码已升级到仅使用 prompt_price / completion_price 的版本

ALTER TABLE ai_model_info
  DROP COLUMN IF EXISTS price;

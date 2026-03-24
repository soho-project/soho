-- 按 2026-03-21 可核实的官方价格，换算为“每千 token”更新模型单价
-- 字段含义：
--   prompt_price = 输入单价（每 1K tokens）
--   completion_price = 输出单价（每 1K tokens）
--
-- 说明：
-- 1. 你的系统只有一组输入/输出单价字段，但部分模型官方是“分档计费”。
--    这里统一取最常见的首档价格，避免高估。
-- 2. 以下模型本次不更新，原因见文末备注：
--    - ark-code-latest
--    - doubao-seed-2.0-code

START TRANSACTION;

-- Doubao-Seed-Code
-- 官方分档（火山引擎）：输入长度 [0,32] 档
-- 输入 1.2 元 / 百万 tokens，输出 8 元 / 百万 tokens
UPDATE ai_model_info
SET
  prompt_price = 0.0012,
  completion_price = 0.0080,
  updated_time = NOW()
WHERE model_name = 'doubao-seed-code';

-- Doubao-Seed-2.0-Pro
-- 官方分档（火山引擎）：输入长度 [0,32] 档
-- 输入 3.2 元 / 百万 tokens，输出 16 元 / 百万 tokens
UPDATE ai_model_info
SET
  prompt_price = 0.0032,
  completion_price = 0.0160,
  updated_time = NOW()
WHERE model_name = 'doubao-seed-2.0-pro';

-- Doubao-Seed-2.0-Lite
-- 官方分档（火山引擎）：输入长度 [0,32] 档
-- 输入 0.6 元 / 百万 tokens，输出 3.6 元 / 百万 tokens
UPDATE ai_model_info
SET
  prompt_price = 0.0006,
  completion_price = 0.0036,
  updated_time = NOW()
WHERE model_name = 'doubao-seed-2.0-lite';

-- DeepSeek-V3.2
-- 官方分档（火山引擎）：输入长度 [0,32] 档
-- 输入 2 元 / 百万 tokens，输出 3 元 / 百万 tokens
UPDATE ai_model_info
SET
  prompt_price = 0.0020,
  completion_price = 0.0030,
  updated_time = NOW()
WHERE model_name = 'deepseek-v3.2';

-- GLM-4.7
-- 官方分档（火山引擎）存在多档：
-- 1) 输入 [0,32] 且输出 <= 0.2K：输入 2 / 百万，输出 8 / 百万
-- 2) 输入 [0,32] 且输出 > 0.2K：输入 3 / 百万，输出 14 / 百万
-- 3) 输入 (32,200]：输入 4 / 百万，输出 16 / 百万
-- 这里取更常见的普通首档（输出 > 0.2K）
UPDATE ai_model_info
SET
  prompt_price = 0.0030,
  completion_price = 0.0140,
  updated_time = NOW()
WHERE model_name = 'glm-4.7';

-- Kimi-K2.5
-- 官方价格（火山引擎）
-- 输入 4 元 / 百万 tokens，输出 21 元 / 百万 tokens
UPDATE ai_model_info
SET
  prompt_price = 0.0040,
  completion_price = 0.0210,
  updated_time = NOW()
WHERE model_name = 'kimi-k2.5';

-- MiniMax-M2.5
-- 官方价格（MiniMax）：
-- 输入 $0.3 / 百万 tokens，输出 $1.2 / 百万 tokens
-- 按 2026-03-12 可查 USD/CNY = 6.8803 换算：
-- 输入 2.06409 元 / 百万 tokens，输出 8.25636 元 / 百万 tokens
-- 本表字段为 decimal(18,4)，因此按每千 token 四舍五入为：
-- 输入 0.0021 元 / 千 tokens，输出 0.0083 元 / 千 tokens
UPDATE ai_model_info
SET
  prompt_price = 0.0021,
  completion_price = 0.0083,
  updated_time = NOW()
WHERE model_name = 'minimax-m2.5';

COMMIT;

-- 未更新模型说明：
-- 1. ark-code-latest
--    这是火山方舟 Coding Plan 的别名/入口模型，不是稳定可对外定价的单一基础模型。
--    更适合保留为 0，或按你在控制台实际绑定的底层模型单价来设。
--
-- 2. doubao-seed-2.0-code
--    本次没有检索到可稳定引用的公开官方价格页，因此不建议直接拍值。

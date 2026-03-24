-- 按 2026-03-21 可核实的官方价格更新模型单价（prompt/completion），单位：元 / 1K tokens
-- OpenAI 价格来源：OpenAI API Pricing（标准/短上下文，非缓存价）
-- 汇率来源：Investing.com CNY/USD = 0.1453 → USD/CNY = 6.8823124569
-- DeepSeek 价格来源：DeepSeek API Docs（CNY 计价，缓存未命中输入价）

START TRANSACTION;

-- OpenAI：gpt-5.4
-- $2.50 / 1M input, $15.00 / 1M output
UPDATE ai_model_info
SET
  prompt_price = 0.0172,
  completion_price = 0.1032,
  updated_time = NOW()
WHERE model_name = 'gpt-5.4';

-- OpenAI：gpt-5.4-mini
-- $0.75 / 1M input, $4.50 / 1M output
UPDATE ai_model_info
SET
  prompt_price = 0.0052,
  completion_price = 0.0310,
  updated_time = NOW()
WHERE model_name = 'gpt-5.4-mini';

-- OpenAI：gpt-5.2
-- $1.75 / 1M input, $14.00 / 1M output
UPDATE ai_model_info
SET
  prompt_price = 0.0120,
  completion_price = 0.0964,
  updated_time = NOW()
WHERE model_name = 'gpt-5.2';

-- OpenAI：gpt-5-codex
-- $1.25 / 1M input, $10.00 / 1M output
UPDATE ai_model_info
SET
  prompt_price = 0.0086,
  completion_price = 0.0688,
  updated_time = NOW()
WHERE model_name = 'gpt-5-codex';

-- OpenAI：gpt-5.1-codex
-- $1.25 / 1M input, $10.00 / 1M output
UPDATE ai_model_info
SET
  prompt_price = 0.0086,
  completion_price = 0.0688,
  updated_time = NOW()
WHERE model_name = 'gpt-5.1-codex';

-- OpenAI：gpt-5.1-codex-max
-- $1.25 / 1M input, $10.00 / 1M output
UPDATE ai_model_info
SET
  prompt_price = 0.0086,
  completion_price = 0.0688,
  updated_time = NOW()
WHERE model_name = 'gpt-5.1-codex-max';

-- OpenAI：gpt-5.1-codex-mini
-- $0.25 / 1M input, $2.00 / 1M output
UPDATE ai_model_info
SET
  prompt_price = 0.0017,
  completion_price = 0.0138,
  updated_time = NOW()
WHERE model_name = 'gpt-5.1-codex-mini';

-- OpenAI：gpt-5.2-codex
-- $1.75 / 1M input, $14.00 / 1M output
UPDATE ai_model_info
SET
  prompt_price = 0.0120,
  completion_price = 0.0964,
  updated_time = NOW()
WHERE model_name = 'gpt-5.2-codex';

-- OpenAI：gpt-5.3-codex
-- $1.75 / 1M input, $14.00 / 1M output
UPDATE ai_model_info
SET
  prompt_price = 0.0120,
  completion_price = 0.0964,
  updated_time = NOW()
WHERE model_name = 'gpt-5.3-codex';

-- OpenAI：codex-mini-latest
-- $1.50 / 1M input, $6.00 / 1M output
UPDATE ai_model_info
SET
  prompt_price = 0.0103,
  completion_price = 0.0413,
  updated_time = NOW()
WHERE model_name = 'codex-mini-latest';

-- DeepSeek：deepseek-chat（CNY 计价，缓存未命中输入价）
-- 2 元 / 1M input, 8 元 / 1M output
UPDATE ai_model_info
SET
  prompt_price = 0.0020,
  completion_price = 0.0080,
  updated_time = NOW()
WHERE model_name = 'deepseek-chat';

-- DeepSeek：deepseek-reasoner（CNY 计价，缓存未命中输入价）
-- 4 元 / 1M input, 16 元 / 1M output
UPDATE ai_model_info
SET
  prompt_price = 0.0040,
  completion_price = 0.0160,
  updated_time = NOW()
WHERE model_name = 'deepseek-reasoner';

COMMIT;

-- 未更新：
-- ark-code-latest
-- 目前没有公开的“按 token 计费”官方价格页（Coding Plan 更偏订阅/额度），建议你确认官方口径后再写入。

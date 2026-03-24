-- OpenClaw -> soho-ai import SQL
-- Source config:
--   baseUrl: https://ark.cn-beijing.volces.com/api/coding/v3
--   api: openai-completions
--   models: 9
--
-- Notes:
-- 1. This script targets soho-ai tables:
--    - ai_provider_config
--    - ai_model_info
--    - ai_provider_model_rel
-- 2. It is written to be repeatable:
--    - update first
--    - insert if not exists
-- 3. Because this gateway appends openaiPath to baseUrl, this provider uses:
--    openaiPath = /chat/completions
--    final upstream endpoint =
--    https://ark.cn-beijing.volces.com/api/coding/v3/chat/completions

START TRANSACTION;

SET @now := NOW();
SET @provider_code := 'openclaw_volcengine_ark_coding';
SET @provider_base_url := 'https://ark.cn-beijing.volces.com/api/coding/v3';
SET @provider_api_key := 'f7faf686-db37-461b-bd7f-e5e92f8d7c62';

-- provider
UPDATE ai_provider_config
SET
  provider = 'openai',
  code = @provider_code,
  api_key_ref = @provider_api_key,
  base_url = @provider_base_url,
  config_json = JSON_OBJECT(
    'provider', 'openai',
    'api', 'openai-completions',
    'source', 'openclaw',
    'openaiPath', '/chat/completions',
    'timeoutMs', 60000,
    'maxTokens', 32000,
    'metadata', JSON_OBJECT(
      'platform', 'volcengine-ark-coding',
      'region', 'cn-beijing'
    )
  ),
  default_model = 'ark-code-latest',
  supported_models = JSON_ARRAY(
    'ark-code-latest',
    'doubao-seed-code',
    'glm-4.7',
    'deepseek-v3.2',
    'doubao-seed-2.0-code',
    'doubao-seed-2.0-pro',
    'doubao-seed-2.0-lite',
    'minimax-m2.5',
    'kimi-k2.5'
  ),
  env = 'prod',
  rate_limit = NULL,
  remark = 'Imported from OpenClaw config on 2026-03-21',
  status = 1,
  timeout_ms = 60000,
  updated_time = @now
WHERE code = @provider_code;

INSERT INTO ai_provider_config (
  api_key_ref,
  base_url,
  code,
  config_json,
  created_time,
  default_model,
  supported_models,
  env,
  provider,
  rate_limit,
  remark,
  status,
  timeout_ms,
  updated_time
)
SELECT
  @provider_api_key,
  @provider_base_url,
  @provider_code,
  JSON_OBJECT(
    'provider', 'openai',
    'api', 'openai-completions',
    'source', 'openclaw',
    'openaiPath', '/chat/completions',
    'timeoutMs', 60000,
    'maxTokens', 32000,
    'metadata', JSON_OBJECT(
      'platform', 'volcengine-ark-coding',
      'region', 'cn-beijing'
    )
  ),
  @now,
  'ark-code-latest',
  JSON_ARRAY(
    'ark-code-latest',
    'doubao-seed-code',
    'glm-4.7',
    'deepseek-v3.2',
    'doubao-seed-2.0-code',
    'doubao-seed-2.0-pro',
    'doubao-seed-2.0-lite',
    'minimax-m2.5',
    'kimi-k2.5'
  ),
  'prod',
  'openai',
  NULL,
  'Imported from OpenClaw config on 2026-03-21',
  1,
  60000,
  @now
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM ai_provider_config WHERE code = @provider_code
);

-- models
UPDATE ai_model_info
SET
  model_desc = 'Volcengine Ark coding model',
  model_detail = JSON_OBJECT(
    'id', 'ark-code-latest',
    'name', 'ark-code-latest',
    'source', 'openclaw',
    'input', JSON_ARRAY('text', 'image'),
    'contextWindow', 256000,
    'maxTokens', 32000
  ),
  status = 1,
  sort = 0,
  updated_time = @now
WHERE model_name = 'ark-code-latest';

INSERT INTO ai_model_info (
  model_name,
  model_desc,
  model_detail,
  status,
  sort,
  created_time,
  updated_time
)
SELECT
  'ark-code-latest',
  'Volcengine Ark coding model',
  JSON_OBJECT(
    'id', 'ark-code-latest',
    'name', 'ark-code-latest',
    'source', 'openclaw',
    'input', JSON_ARRAY('text', 'image'),
    'contextWindow', 256000,
    'maxTokens', 32000
  ),
  1,
  0,
  @now,
  @now
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM ai_model_info WHERE model_name = 'ark-code-latest'
);

UPDATE ai_model_info
SET
  model_desc = 'Volcengine Doubao Seed Code model',
  model_detail = JSON_OBJECT(
    'id', 'doubao-seed-code',
    'name', 'doubao-seed-code',
    'source', 'openclaw',
    'input', JSON_ARRAY('text', 'image'),
    'contextWindow', 256000,
    'maxTokens', 32000
  ),
  status = 1,
  sort = 1,
  updated_time = @now
WHERE model_name = 'doubao-seed-code';

INSERT INTO ai_model_info (
  model_name,
  model_desc,
  model_detail,
  status,
  sort,
  created_time,
  updated_time
)
SELECT
  'doubao-seed-code',
  'Volcengine Doubao Seed Code model',
  JSON_OBJECT(
    'id', 'doubao-seed-code',
    'name', 'doubao-seed-code',
    'source', 'openclaw',
    'input', JSON_ARRAY('text', 'image'),
    'contextWindow', 256000,
    'maxTokens', 32000
  ),
  1,
  1,
  @now,
  @now
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM ai_model_info WHERE model_name = 'doubao-seed-code'
);

UPDATE ai_model_info
SET
  model_desc = 'Zhipu GLM 4.7 model',
  model_detail = JSON_OBJECT(
    'id', 'glm-4.7',
    'name', 'glm-4.7',
    'source', 'openclaw',
    'input', JSON_ARRAY('text'),
    'contextWindow', 200000,
    'maxTokens', 128000
  ),
  status = 1,
  sort = 2,
  updated_time = @now
WHERE model_name = 'glm-4.7';

INSERT INTO ai_model_info (
  model_name,
  model_desc,
  model_detail,
  status,
  sort,
  created_time,
  updated_time
)
SELECT
  'glm-4.7',
  'Zhipu GLM 4.7 model',
  JSON_OBJECT(
    'id', 'glm-4.7',
    'name', 'glm-4.7',
    'source', 'openclaw',
    'input', JSON_ARRAY('text'),
    'contextWindow', 200000,
    'maxTokens', 128000
  ),
  1,
  2,
  @now,
  @now
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM ai_model_info WHERE model_name = 'glm-4.7'
);

UPDATE ai_model_info
SET
  model_desc = 'DeepSeek V3.2 model',
  model_detail = JSON_OBJECT(
    'id', 'deepseek-v3.2',
    'name', 'deepseek-v3.2',
    'source', 'openclaw',
    'contextWindow', 128000,
    'maxTokens', 32000
  ),
  status = 1,
  sort = 3,
  updated_time = @now
WHERE model_name = 'deepseek-v3.2';

INSERT INTO ai_model_info (
  model_name,
  model_desc,
  model_detail,
  status,
  sort,
  created_time,
  updated_time
)
SELECT
  'deepseek-v3.2',
  'DeepSeek V3.2 model',
  JSON_OBJECT(
    'id', 'deepseek-v3.2',
    'name', 'deepseek-v3.2',
    'source', 'openclaw',
    'contextWindow', 128000,
    'maxTokens', 32000
  ),
  1,
  3,
  @now,
  @now
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM ai_model_info WHERE model_name = 'deepseek-v3.2'
);

UPDATE ai_model_info
SET
  model_desc = 'Volcengine Doubao Seed 2.0 Code model',
  model_detail = JSON_OBJECT(
    'id', 'doubao-seed-2.0-code',
    'name', 'doubao-seed-2.0-code',
    'source', 'openclaw',
    'input', JSON_ARRAY('text', 'image'),
    'contextWindow', 256000,
    'maxTokens', 128000
  ),
  status = 1,
  sort = 4,
  updated_time = @now
WHERE model_name = 'doubao-seed-2.0-code';

INSERT INTO ai_model_info (
  model_name,
  model_desc,
  model_detail,
  status,
  sort,
  created_time,
  updated_time
)
SELECT
  'doubao-seed-2.0-code',
  'Volcengine Doubao Seed 2.0 Code model',
  JSON_OBJECT(
    'id', 'doubao-seed-2.0-code',
    'name', 'doubao-seed-2.0-code',
    'source', 'openclaw',
    'input', JSON_ARRAY('text', 'image'),
    'contextWindow', 256000,
    'maxTokens', 128000
  ),
  1,
  4,
  @now,
  @now
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM ai_model_info WHERE model_name = 'doubao-seed-2.0-code'
);

UPDATE ai_model_info
SET
  model_desc = 'Volcengine Doubao Seed 2.0 Pro model',
  model_detail = JSON_OBJECT(
    'id', 'doubao-seed-2.0-pro',
    'name', 'doubao-seed-2.0-pro',
    'source', 'openclaw',
    'input', JSON_ARRAY('text', 'image'),
    'contextWindow', 256000,
    'maxTokens', 128000
  ),
  status = 1,
  sort = 5,
  updated_time = @now
WHERE model_name = 'doubao-seed-2.0-pro';

INSERT INTO ai_model_info (
  model_name,
  model_desc,
  model_detail,
  status,
  sort,
  created_time,
  updated_time
)
SELECT
  'doubao-seed-2.0-pro',
  'Volcengine Doubao Seed 2.0 Pro model',
  JSON_OBJECT(
    'id', 'doubao-seed-2.0-pro',
    'name', 'doubao-seed-2.0-pro',
    'source', 'openclaw',
    'input', JSON_ARRAY('text', 'image'),
    'contextWindow', 256000,
    'maxTokens', 128000
  ),
  1,
  5,
  @now,
  @now
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM ai_model_info WHERE model_name = 'doubao-seed-2.0-pro'
);

UPDATE ai_model_info
SET
  model_desc = 'Volcengine Doubao Seed 2.0 Lite model',
  model_detail = JSON_OBJECT(
    'id', 'doubao-seed-2.0-lite',
    'name', 'doubao-seed-2.0-lite',
    'source', 'openclaw',
    'input', JSON_ARRAY('text', 'image'),
    'contextWindow', 256000,
    'maxTokens', 128000
  ),
  status = 1,
  sort = 6,
  updated_time = @now
WHERE model_name = 'doubao-seed-2.0-lite';

INSERT INTO ai_model_info (
  model_name,
  model_desc,
  model_detail,
  status,
  sort,
  created_time,
  updated_time
)
SELECT
  'doubao-seed-2.0-lite',
  'Volcengine Doubao Seed 2.0 Lite model',
  JSON_OBJECT(
    'id', 'doubao-seed-2.0-lite',
    'name', 'doubao-seed-2.0-lite',
    'source', 'openclaw',
    'input', JSON_ARRAY('text', 'image'),
    'contextWindow', 256000,
    'maxTokens', 128000
  ),
  1,
  6,
  @now,
  @now
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM ai_model_info WHERE model_name = 'doubao-seed-2.0-lite'
);

UPDATE ai_model_info
SET
  model_desc = 'MiniMax M2.5 model',
  model_detail = JSON_OBJECT(
    'id', 'minimax-m2.5',
    'name', 'minimax-m2.5',
    'source', 'openclaw',
    'input', JSON_ARRAY('text'),
    'contextWindow', 200000,
    'maxTokens', 128000
  ),
  status = 1,
  sort = 7,
  updated_time = @now
WHERE model_name = 'minimax-m2.5';

INSERT INTO ai_model_info (
  model_name,
  model_desc,
  model_detail,
  status,
  sort,
  created_time,
  updated_time
)
SELECT
  'minimax-m2.5',
  'MiniMax M2.5 model',
  JSON_OBJECT(
    'id', 'minimax-m2.5',
    'name', 'minimax-m2.5',
    'source', 'openclaw',
    'input', JSON_ARRAY('text'),
    'contextWindow', 200000,
    'maxTokens', 128000
  ),
  1,
  7,
  @now,
  @now
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM ai_model_info WHERE model_name = 'minimax-m2.5'
);

UPDATE ai_model_info
SET
  model_desc = 'Kimi K2.5 model',
  model_detail = JSON_OBJECT(
    'id', 'kimi-k2.5',
    'name', 'kimi-k2.5',
    'source', 'openclaw',
    'input', JSON_ARRAY('text', 'image'),
    'contextWindow', 256000,
    'maxTokens', 32000
  ),
  status = 1,
  sort = 8,
  updated_time = @now
WHERE model_name = 'kimi-k2.5';

INSERT INTO ai_model_info (
  model_name,
  model_desc,
  model_detail,
  status,
  sort,
  created_time,
  updated_time
)
SELECT
  'kimi-k2.5',
  'Kimi K2.5 model',
  JSON_OBJECT(
    'id', 'kimi-k2.5',
    'name', 'kimi-k2.5',
    'source', 'openclaw',
    'input', JSON_ARRAY('text', 'image'),
    'contextWindow', 256000,
    'maxTokens', 32000
  ),
  1,
  8,
  @now,
  @now
FROM DUAL
WHERE NOT EXISTS (
  SELECT 1 FROM ai_model_info WHERE model_name = 'kimi-k2.5'
);

-- provider <-> model relations
UPDATE ai_provider_model_rel rel
JOIN ai_provider_config pc ON pc.id = rel.provider_config_id
JOIN ai_model_info mi ON mi.id = rel.model_info_id
SET rel.status = 1, rel.sort = 0, rel.updated_time = @now
WHERE pc.code = @provider_code AND mi.model_name = 'ark-code-latest';

INSERT INTO ai_provider_model_rel (
  provider_config_id,
  model_info_id,
  status,
  sort,
  created_time,
  updated_time
)
SELECT pc.id, mi.id, 1, 0, @now, @now
FROM ai_provider_config pc
JOIN ai_model_info mi ON mi.model_name = 'ark-code-latest'
WHERE pc.code = @provider_code
  AND NOT EXISTS (
    SELECT 1
    FROM ai_provider_model_rel rel
    WHERE rel.provider_config_id = pc.id
      AND rel.model_info_id = mi.id
  );

UPDATE ai_provider_model_rel rel
JOIN ai_provider_config pc ON pc.id = rel.provider_config_id
JOIN ai_model_info mi ON mi.id = rel.model_info_id
SET rel.status = 1, rel.sort = 1, rel.updated_time = @now
WHERE pc.code = @provider_code AND mi.model_name = 'doubao-seed-code';

INSERT INTO ai_provider_model_rel (
  provider_config_id,
  model_info_id,
  status,
  sort,
  created_time,
  updated_time
)
SELECT pc.id, mi.id, 1, 1, @now, @now
FROM ai_provider_config pc
JOIN ai_model_info mi ON mi.model_name = 'doubao-seed-code'
WHERE pc.code = @provider_code
  AND NOT EXISTS (
    SELECT 1
    FROM ai_provider_model_rel rel
    WHERE rel.provider_config_id = pc.id
      AND rel.model_info_id = mi.id
  );

UPDATE ai_provider_model_rel rel
JOIN ai_provider_config pc ON pc.id = rel.provider_config_id
JOIN ai_model_info mi ON mi.id = rel.model_info_id
SET rel.status = 1, rel.sort = 2, rel.updated_time = @now
WHERE pc.code = @provider_code AND mi.model_name = 'glm-4.7';

INSERT INTO ai_provider_model_rel (
  provider_config_id,
  model_info_id,
  status,
  sort,
  created_time,
  updated_time
)
SELECT pc.id, mi.id, 1, 2, @now, @now
FROM ai_provider_config pc
JOIN ai_model_info mi ON mi.model_name = 'glm-4.7'
WHERE pc.code = @provider_code
  AND NOT EXISTS (
    SELECT 1
    FROM ai_provider_model_rel rel
    WHERE rel.provider_config_id = pc.id
      AND rel.model_info_id = mi.id
  );

UPDATE ai_provider_model_rel rel
JOIN ai_provider_config pc ON pc.id = rel.provider_config_id
JOIN ai_model_info mi ON mi.id = rel.model_info_id
SET rel.status = 1, rel.sort = 3, rel.updated_time = @now
WHERE pc.code = @provider_code AND mi.model_name = 'deepseek-v3.2';

INSERT INTO ai_provider_model_rel (
  provider_config_id,
  model_info_id,
  status,
  sort,
  created_time,
  updated_time
)
SELECT pc.id, mi.id, 1, 3, @now, @now
FROM ai_provider_config pc
JOIN ai_model_info mi ON mi.model_name = 'deepseek-v3.2'
WHERE pc.code = @provider_code
  AND NOT EXISTS (
    SELECT 1
    FROM ai_provider_model_rel rel
    WHERE rel.provider_config_id = pc.id
      AND rel.model_info_id = mi.id
  );

UPDATE ai_provider_model_rel rel
JOIN ai_provider_config pc ON pc.id = rel.provider_config_id
JOIN ai_model_info mi ON mi.id = rel.model_info_id
SET rel.status = 1, rel.sort = 4, rel.updated_time = @now
WHERE pc.code = @provider_code AND mi.model_name = 'doubao-seed-2.0-code';

INSERT INTO ai_provider_model_rel (
  provider_config_id,
  model_info_id,
  status,
  sort,
  created_time,
  updated_time
)
SELECT pc.id, mi.id, 1, 4, @now, @now
FROM ai_provider_config pc
JOIN ai_model_info mi ON mi.model_name = 'doubao-seed-2.0-code'
WHERE pc.code = @provider_code
  AND NOT EXISTS (
    SELECT 1
    FROM ai_provider_model_rel rel
    WHERE rel.provider_config_id = pc.id
      AND rel.model_info_id = mi.id
  );

UPDATE ai_provider_model_rel rel
JOIN ai_provider_config pc ON pc.id = rel.provider_config_id
JOIN ai_model_info mi ON mi.id = rel.model_info_id
SET rel.status = 1, rel.sort = 5, rel.updated_time = @now
WHERE pc.code = @provider_code AND mi.model_name = 'doubao-seed-2.0-pro';

INSERT INTO ai_provider_model_rel (
  provider_config_id,
  model_info_id,
  status,
  sort,
  created_time,
  updated_time
)
SELECT pc.id, mi.id, 1, 5, @now, @now
FROM ai_provider_config pc
JOIN ai_model_info mi ON mi.model_name = 'doubao-seed-2.0-pro'
WHERE pc.code = @provider_code
  AND NOT EXISTS (
    SELECT 1
    FROM ai_provider_model_rel rel
    WHERE rel.provider_config_id = pc.id
      AND rel.model_info_id = mi.id
  );

UPDATE ai_provider_model_rel rel
JOIN ai_provider_config pc ON pc.id = rel.provider_config_id
JOIN ai_model_info mi ON mi.id = rel.model_info_id
SET rel.status = 1, rel.sort = 6, rel.updated_time = @now
WHERE pc.code = @provider_code AND mi.model_name = 'doubao-seed-2.0-lite';

INSERT INTO ai_provider_model_rel (
  provider_config_id,
  model_info_id,
  status,
  sort,
  created_time,
  updated_time
)
SELECT pc.id, mi.id, 1, 6, @now, @now
FROM ai_provider_config pc
JOIN ai_model_info mi ON mi.model_name = 'doubao-seed-2.0-lite'
WHERE pc.code = @provider_code
  AND NOT EXISTS (
    SELECT 1
    FROM ai_provider_model_rel rel
    WHERE rel.provider_config_id = pc.id
      AND rel.model_info_id = mi.id
  );

UPDATE ai_provider_model_rel rel
JOIN ai_provider_config pc ON pc.id = rel.provider_config_id
JOIN ai_model_info mi ON mi.id = rel.model_info_id
SET rel.status = 1, rel.sort = 7, rel.updated_time = @now
WHERE pc.code = @provider_code AND mi.model_name = 'minimax-m2.5';

INSERT INTO ai_provider_model_rel (
  provider_config_id,
  model_info_id,
  status,
  sort,
  created_time,
  updated_time
)
SELECT pc.id, mi.id, 1, 7, @now, @now
FROM ai_provider_config pc
JOIN ai_model_info mi ON mi.model_name = 'minimax-m2.5'
WHERE pc.code = @provider_code
  AND NOT EXISTS (
    SELECT 1
    FROM ai_provider_model_rel rel
    WHERE rel.provider_config_id = pc.id
      AND rel.model_info_id = mi.id
  );

UPDATE ai_provider_model_rel rel
JOIN ai_provider_config pc ON pc.id = rel.provider_config_id
JOIN ai_model_info mi ON mi.id = rel.model_info_id
SET rel.status = 1, rel.sort = 8, rel.updated_time = @now
WHERE pc.code = @provider_code AND mi.model_name = 'kimi-k2.5';

INSERT INTO ai_provider_model_rel (
  provider_config_id,
  model_info_id,
  status,
  sort,
  created_time,
  updated_time
)
SELECT pc.id, mi.id, 1, 8, @now, @now
FROM ai_provider_config pc
JOIN ai_model_info mi ON mi.model_name = 'kimi-k2.5'
WHERE pc.code = @provider_code
  AND NOT EXISTS (
    SELECT 1
    FROM ai_provider_model_rel rel
    WHERE rel.provider_config_id = pc.id
      AND rel.model_info_id = mi.id
  );

COMMIT;

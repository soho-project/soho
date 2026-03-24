-- 将 OpenClaw 导入的模型描述/详情改为中文展示文案

START TRANSACTION;

UPDATE ai_model_info
SET
  model_desc = '火山引擎 Ark 编程模型',
  model_detail = '适合代码生成、代码补全、代码解释、重构和工程问题排查。支持文本与图片输入，上下文窗口 256000，单次最大输出 32000。'
WHERE model_name = 'ark-code-latest';

UPDATE ai_model_info
SET
  model_desc = '豆包 Seed 编程模型',
  model_detail = '面向编程与开发场景优化，适合生成代码、修改代码、解释报错和编写脚本。支持文本与图片输入，上下文窗口 256000，单次最大输出 32000。'
WHERE model_name = 'doubao-seed-code';

UPDATE ai_model_info
SET
  model_desc = '智谱 GLM-4.7 通用模型',
  model_detail = '适合通用问答、内容生成、方案设计与长文本处理，也可用于开发辅助场景。支持文本输入，上下文窗口 200000，单次最大输出 128000。'
WHERE model_name = 'glm-4.7';

UPDATE ai_model_info
SET
  model_desc = 'DeepSeek V3.2 通用模型',
  model_detail = '适合通用对话、内容创作、技术问答和复杂任务拆解，可用于日常开发辅助。上下文窗口 128000，单次最大输出 32000。'
WHERE model_name = 'deepseek-v3.2';

UPDATE ai_model_info
SET
  model_desc = '豆包 Seed 2.0 编程模型',
  model_detail = '新一代代码模型，适合多轮编程协作、复杂代码生成、重构和项目级开发任务。支持文本与图片输入，上下文窗口 256000，单次最大输出 128000。'
WHERE model_name = 'doubao-seed-2.0-code';

UPDATE ai_model_info
SET
  model_desc = '豆包 Seed 2.0 Pro 通用模型',
  model_detail = '适合高质量问答、内容生成、复杂分析和多步骤任务处理，也支持图文混合输入。上下文窗口 256000，单次最大输出 128000。'
WHERE model_name = 'doubao-seed-2.0-pro';

UPDATE ai_model_info
SET
  model_desc = '豆包 Seed 2.0 Lite 轻量模型',
  model_detail = '响应速度较快，适合轻量问答、批量生成、日常办公和低成本调用场景。支持文本与图片输入，上下文窗口 256000，单次最大输出 128000。'
WHERE model_name = 'doubao-seed-2.0-lite';

UPDATE ai_model_info
SET
  model_desc = 'MiniMax M2.5 通用模型',
  model_detail = '适合长文本理解、知识问答、内容整理和综合分析场景。支持文本输入，上下文窗口 200000，单次最大输出 128000。'
WHERE model_name = 'minimax-m2.5';

UPDATE ai_model_info
SET
  model_desc = 'Kimi K2.5 长上下文模型',
  model_detail = '适合超长文本阅读、资料总结、问答检索和图文理解任务。支持文本与图片输入，上下文窗口 256000，单次最大输出 32000。'
WHERE model_name = 'kimi-k2.5';

COMMIT;

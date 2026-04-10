-- AI 聊天会话不再绑定供应商，清理历史 provider_code，避免旧会话继续误导前端状态。
UPDATE ai_chat_session
SET provider_code = NULL
WHERE provider_code IS NOT NULL
  AND provider_code <> '';

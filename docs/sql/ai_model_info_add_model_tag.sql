ALTER TABLE `ai_model_info`
    ADD COLUMN `model_tag` varchar(32) DEFAULT 'chat' COMMENT '模型标记: chat/images/audio/embeddings/fine-tuning' AFTER `model_detail`;

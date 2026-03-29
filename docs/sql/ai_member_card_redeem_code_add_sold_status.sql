-- 为 ai_member_card_redeem_code 增加“是否已售出”标记
ALTER TABLE `ai_member_card_redeem_code`
    ADD COLUMN `sold_status` tinyint NOT NULL DEFAULT 0 COMMENT '是否已售出:0未售出,1已售出' AFTER `status`;

-- 可选索引：便于后台按已售/未售筛选
ALTER TABLE `ai_member_card_redeem_code`
    ADD INDEX `idx_ai_member_card_redeem_code_sold_status` (`sold_status`);

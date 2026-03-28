-- 为 ai_user_member_card 增加会员卡号字段（no）
ALTER TABLE `ai_user_member_card`
    ADD COLUMN `no` varchar(64) DEFAULT NULL COMMENT '会员卡号' AFTER `member_card_id`;

-- 为历史数据回填卡号（格式：MC + 16位十六进制），避免后续唯一约束失败
UPDATE `ai_user_member_card`
SET `no` = CONCAT('MC', UPPER(LEFT(REPLACE(UUID(), '-', ''), 16)))
WHERE (`no` IS NULL OR `no` = '');

-- 强唯一：卡号不能为空 + 唯一索引
ALTER TABLE `ai_user_member_card`
    MODIFY COLUMN `no` varchar(64) NOT NULL COMMENT '会员卡号';

ALTER TABLE `ai_user_member_card`
    ADD UNIQUE KEY `uk_ai_user_member_card_no` (`no`);

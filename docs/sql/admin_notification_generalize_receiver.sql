ALTER TABLE `admin_notification`
    ADD COLUMN `receiver_type` varchar(16) NOT NULL DEFAULT 'admin' COMMENT '接收者类型：admin/user' AFTER `admin_user_id`,
    ADD COLUMN `receiver_id` bigint NOT NULL DEFAULT 0 COMMENT '接收者ID' AFTER `receiver_type`,
    ADD COLUMN `sender_type` varchar(16) NOT NULL DEFAULT 'system' COMMENT '发送者类型：admin/user/system' AFTER `create_admin_user_id`,
    ADD COLUMN `sender_id` bigint NOT NULL DEFAULT 0 COMMENT '发送者ID' AFTER `sender_type`;

UPDATE `admin_notification`
SET `receiver_type` = 'admin',
    `receiver_id` = IFNULL(`admin_user_id`, 0),
    `sender_type` = CASE
        WHEN IFNULL(`create_admin_user_id`, 0) > 0 THEN 'admin'
        ELSE 'system'
    END,
    `sender_id` = IFNULL(`create_admin_user_id`, 0)
WHERE `receiver_id` = 0
   OR `receiver_type` IS NULL
   OR `sender_type` IS NULL
   OR `sender_id` = 0;

CREATE INDEX `idx_receiver_type_receiver_id_is_read`
    ON `admin_notification` (`receiver_type`, `receiver_id`, `is_read`);

ALTER TABLE `admin_notification`
    ADD COLUMN `sender_type` varchar(16) NOT NULL DEFAULT 'system' COMMENT '发送者类型：admin/user/system' AFTER `create_admin_user_id`,
    ADD COLUMN `sender_id` bigint NOT NULL DEFAULT 0 COMMENT '发送者ID' AFTER `sender_type`;

CREATE TABLE IF NOT EXISTS `admin_notification_receiver` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `notification_id` bigint NOT NULL COMMENT '通知ID',
  `receiver_type` varchar(16) NOT NULL COMMENT '接收者类型：admin/user',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `is_read` tinyint NOT NULL DEFAULT 0 COMMENT '是否已读：0未读，1已读',
  `read_time` datetime DEFAULT NULL COMMENT '已读时间',
  PRIMARY KEY (`id`),
  KEY `idx_notification_id` (`notification_id`),
  KEY `idx_receiver_type_receiver_id_is_read` (`receiver_type`, `receiver_id`, `is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知接收人及已读状态';

UPDATE `admin_notification`
SET `sender_type` = CASE
        WHEN IFNULL(`create_admin_user_id`, 0) > 0 THEN 'admin'
        ELSE 'system'
    END,
    `sender_id` = IFNULL(`create_admin_user_id`, 0)
WHERE `sender_id` = 0;

INSERT INTO `admin_notification_receiver` (`notification_id`, `receiver_type`, `receiver_id`, `is_read`, `read_time`)
SELECT n.`id`,
       'admin' AS `receiver_type`,
       n.`admin_user_id` AS `receiver_id`,
       IFNULL(n.`is_read`, 0) AS `is_read`,
       CASE
           WHEN IFNULL(n.`is_read`, 0) = 1 THEN NOW()
           ELSE NULL
       END AS `read_time`
FROM `admin_notification` n
LEFT JOIN `admin_notification_receiver` r
       ON r.`notification_id` = n.`id`
WHERE r.`id` IS NULL
  AND n.`admin_user_id` IS NOT NULL
  AND n.`admin_user_id` > 0;

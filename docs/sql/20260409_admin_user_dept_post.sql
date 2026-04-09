ALTER TABLE `admin_user`
    ADD COLUMN `dept_id` bigint(20) DEFAULT NULL COMMENT '部门ID' AFTER `age`,
    ADD COLUMN `post_id` bigint(20) DEFAULT NULL COMMENT '岗位ID' AFTER `dept_id`,
    ADD KEY `idx_dept_id` (`dept_id`),
    ADD KEY `idx_post_id` (`post_id`);

CREATE TABLE IF NOT EXISTS `admin_dept` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    `parent_id` bigint(20) NOT NULL DEFAULT 0 COMMENT '父部门ID',
    `name` varchar(64) NOT NULL COMMENT '部门名称',
    `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序值',
    `leader` varchar(64) DEFAULT NULL COMMENT '负责人',
    `phone` varchar(32) DEFAULT NULL COMMENT '联系电话',
    `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
    `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态 1启用 0禁用',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标记',
    `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
    `created_time` datetime DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='平台管理部门表';

CREATE TABLE IF NOT EXISTS `admin_post` (
    `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
    `name` varchar(64) NOT NULL COMMENT '岗位名称',
    `code` varchar(64) DEFAULT NULL COMMENT '岗位编码',
    `sort` int(11) NOT NULL DEFAULT 0 COMMENT '排序值',
    `status` tinyint(4) NOT NULL DEFAULT 1 COMMENT '状态 1启用 0禁用',
    `remark` varchar(255) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint(4) NOT NULL DEFAULT 0 COMMENT '删除标记',
    `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
    `created_time` datetime DEFAULT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='平台管理岗位表';

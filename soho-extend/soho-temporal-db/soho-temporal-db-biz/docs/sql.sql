CREATE TABLE `temporal_category` (
                                     `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
                                     `name` varchar(255) DEFAULT NULL COMMENT '分类名',
                                     `title` varchar(255) DEFAULT NULL COMMENT '分类标题',
                                     `parent_id` int(10) unsigned DEFAULT '0' COMMENT '分类父ID',
                                     `notes` varchar(1000) DEFAULT NULL COMMENT '备注',
                                     `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
                                     `created_time` datetime DEFAULT NULL COMMENT '创建时间',
                                     PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='时序数据分类';

CREATE TABLE `temporal_table` (
                                  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
                                  `name` varchar(255) DEFAULT NULL COMMENT '名称',
                                  `title` varchar(255) DEFAULT NULL COMMENT '标题',
                                  `updated_time` datetime DEFAULT NULL COMMENT '更新时间',
                                  `created_time` datetime DEFAULT NULL COMMENT '创建时间',
                                  `category_id` int(10) unsigned DEFAULT NULL COMMENT '分类ID',
                                  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='时序表';

CREATE TABLE `temporal_table_col` (
                                      `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
                                      `name` varchar(255) DEFAULT NULL COMMENT '名称',
                                      `title` varchar(255) DEFAULT NULL COMMENT '标题',
                                      `table_id` int(10) unsigned DEFAULT NULL COMMENT '表ID',
                                      `type` int(10) unsigned DEFAULT NULL COMMENT '数据类型',
                                      `updated_time` datetime DEFAULT NULL,
                                      `created_time` datetime DEFAULT NULL,
                                      `status` tinyint(4) DEFAULT NULL COMMENT '状态;0:禁用,1:活跃',
                                      PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='时序表字段';
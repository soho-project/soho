-- 自定义二维码收款：用户上报与人工审核表
CREATE TABLE IF NOT EXISTS `pay_manual_report` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `pay_id` int NOT NULL COMMENT '支付方式ID（pay_info.id）',
  `pay_order_id` int DEFAULT NULL COMMENT '支付单ID（pay_order.id）',
  `order_no` varchar(64) DEFAULT NULL COMMENT '匹配到的支付单号（pay_order.order_no）',
  `payer_name` varchar(64) NOT NULL COMMENT '付款人姓名',
  `report_amount` decimal(18,6) NOT NULL COMMENT '上报支付金额',
  `report_time` datetime NOT NULL COMMENT '上报支付时间',
  `supplier_trade_no` varchar(128) NOT NULL COMMENT '支付供应商单号',
  `report_remark` varchar(255) DEFAULT NULL COMMENT '用户备注',
  `match_status` tinyint NOT NULL DEFAULT '0' COMMENT '匹配状态：0待匹配 1自动匹配成功 2待人工审核 3人工审核通过 4人工审核拒绝',
  `match_score` int NOT NULL DEFAULT '0' COMMENT '匹配得分',
  `match_note` varchar(255) DEFAULT NULL COMMENT '匹配说明或审核备注',
  `reviewed_by` varchar(64) DEFAULT NULL COMMENT '审核人',
  `reviewed_time` datetime DEFAULT NULL COMMENT '审核时间',
  `created_time` datetime NOT NULL COMMENT '创建时间',
  `updated_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pay_id_supplier_trade_no` (`pay_id`,`supplier_trade_no`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_pay_id_match_status` (`pay_id`,`match_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='自定义二维码支付上报记录';

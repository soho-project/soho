-- 钱包模块金额字段统一调整为 6 位小数
-- 目标库: soho_wallet

USE `soho_wallet`;

ALTER TABLE `wallet_info`
    MODIFY COLUMN `amount` decimal(18,6) DEFAULT NULL COMMENT '钱包金额';

ALTER TABLE `wallet_log`
    MODIFY COLUMN `amount` decimal(18,6) DEFAULT NULL COMMENT '金额',
    MODIFY COLUMN `before_amount` decimal(18,6) DEFAULT NULL COMMENT '变更前金额',
    MODIFY COLUMN `after_amount` decimal(18,6) DEFAULT NULL COMMENT '变更后金额';

ALTER TABLE `wallet_recharge`
    MODIFY COLUMN `amount` decimal(18,6) DEFAULT NULL COMMENT '充值金额';

ALTER TABLE `wallet_transfer`
    MODIFY COLUMN `from_amount` decimal(18,6) DEFAULT NULL COMMENT '来源钱包金额',
    MODIFY COLUMN `to_amount` decimal(18,6) DEFAULT NULL COMMENT '目标钱包金额',
    MODIFY COLUMN `from_pay_amount` decimal(18,6) DEFAULT NULL COMMENT '实际转账金额',
    MODIFY COLUMN `from_commission_amount` decimal(18,6) DEFAULT NULL COMMENT '手续费金额';

ALTER TABLE `wallet_type`
    MODIFY COLUMN `withdrawal_min_amount` decimal(18,6) DEFAULT NULL COMMENT '提现最小金额',
    MODIFY COLUMN `withdrawal_commission_rate` decimal(18,6) DEFAULT NULL COMMENT '手续费率',
    MODIFY COLUMN `withdrawal_min_commission` decimal(18,6) DEFAULT NULL COMMENT '最小手续费',
    MODIFY COLUMN `rate` decimal(18,6) DEFAULT NULL COMMENT '汇率';

ALTER TABLE `wallet_user_order`
    MODIFY COLUMN `amount` decimal(18,6) DEFAULT NULL COMMENT '支付金额';

ALTER TABLE `wallet_withdrawal_order`
    MODIFY COLUMN `amount` decimal(18,6) DEFAULT NULL COMMENT '提现金额',
    MODIFY COLUMN `pay_amount` decimal(18,6) DEFAULT NULL COMMENT '支付金额',
    MODIFY COLUMN `commission_amount` decimal(18,6) DEFAULT NULL COMMENT '服务金额';

-- 验证
SELECT table_name, column_name, data_type, numeric_precision, numeric_scale
FROM information_schema.columns
WHERE table_schema = 'soho_wallet'
  AND table_name IN (
    'wallet_info',
    'wallet_log',
    'wallet_recharge',
    'wallet_transfer',
    'wallet_type',
    'wallet_user_order',
    'wallet_withdrawal_order'
  )
  AND column_name IN (
    'amount',
    'before_amount',
    'after_amount',
    'from_amount',
    'to_amount',
    'from_pay_amount',
    'from_commission_amount',
    'withdrawal_min_amount',
    'withdrawal_commission_rate',
    'withdrawal_min_commission',
    'rate',
    'pay_amount',
    'commission_amount'
  )
ORDER BY table_name, column_name;

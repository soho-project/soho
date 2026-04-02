-- 将 pay_manual_report 唯一索引调整为 (pay_id, supplier_trade_no)
ALTER TABLE `pay_manual_report`
    DROP INDEX `uk_supplier_trade_no`,
    ADD UNIQUE INDEX `uk_pay_id_supplier_trade_no` (`pay_id`, `supplier_trade_no`);

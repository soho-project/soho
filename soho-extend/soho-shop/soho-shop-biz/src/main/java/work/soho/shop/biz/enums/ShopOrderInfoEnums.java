package work.soho.shop.biz.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class ShopOrderInfoEnums {
    @RequiredArgsConstructor
    @Getter
    public enum Status {
        PENDING(0,"待处理"),
        ORDER_CANCELLATION(30,"订单取消"),
        COMPLETED(20,"已完成");
        private final int id;
        private final String name;
    }

    @RequiredArgsConstructor
    @Getter
    public enum PayStatus {
        PENDING_PAYMENT(0,"待支付"),
        PAID(1,"已支付");
        private final int id;
        private final String name;
    }

    @RequiredArgsConstructor
    @Getter
    public enum FreightStatus {
        PENDING(0,"待处理"),
        RECEIVED(20,"已收货"),
        SHIPPED(10,"已发货");
        private final int id;
        private final String name;
    }

    @RequiredArgsConstructor
    @Getter
    public enum OrderType {
        PHYSICAL_ORDER(1,"实物订单"),
        SERVICE_ORDER(20,"服务订单"),
        VIRTUAL_ORDER(10,"虚拟订单");
        private final int id;
        private final String name;
    }

    @RequiredArgsConstructor
    @Getter
    public enum Source {
        WAP(1,"WAP"),
        APP(40,"APP"),
        WEB(30,"WEB"),
        OFFICIAL_ACCOUNT(20,"公众号"),
        MINI_PROGRAM(10,"小程序");
        private final int id;
        private final String name;
    }
}
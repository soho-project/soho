package com.yundasys.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class CreateBillOrderRequest {
    private String appid;
    @JsonProperty("partner_id")
    private String partnerId;
    private String secret;
    private List<Order> orders;

    // 订单内部类
    @Data
    @Accessors(chain = true)
    public static class Order {
        private Double collection_value;
        private String cus_area1;
        private String cus_area2;
        private String isProtectPrivacy;
        private List<Item> items;
        private String khddh;
        private String node_id;
        private String order_serial_no;
        private String order_type;
        private String platform_source;
        private Receiver receiver;
        private String remark;
        private Sender sender;
        private String size;
        private Integer special;
        private Double value;
        private Double weight;
        private MultiPack multi_pack;
        private List<MarkingInfo> markingInfos;
    }

    // 商品项内部类
    @Data
    @Accessors(chain = true)
    public static class Item {
        private String name;
        private Integer number;
        private String remark;

    }

    // 收货人内部类
    @Data
    @Accessors(chain = true)
    public static class Receiver {
        private String address;
        private String city;
        private String company;
        private String county;
        private String mobile;
        private String name;
        private String province;

    }

    // 寄件人内部类（结构与收货人相同）
    @Data
    @Accessors(chain = true)
    public static class Sender {
        private String address;
        private String city;
        private String company;
        private String county;
        private String mobile;
        private String name;
        private String province;

    }

    // 多件打包信息内部类
    @Data
    @Accessors(chain = true)
    public static class MultiPack {
        private String mulpck;
        private Integer total;
        private Integer endmark;

    }

    // 标记信息内部类
    @Data
    @Accessors(chain = true)
    public static class MarkingInfo {
        private String type;
        private MarkingValue markingValue;

    }

    // 标记值内部类
    @Data
    @Accessors(chain = true)
    public static class MarkingValue {
        // 注意：这里使用Object类型，因为value可能是数值或字符串
        private Object value;
    }
}
package work.soho.express.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class PrintInfoDTO {
    // 订单号
    private String partnerCode;
    private Boolean checked;
    private String payType;
    private String sheetMode;

    /**
     * 默认值：0 1: 实名 2: 匿名
     */
    private int realName;

    private PrintParam printParam;
    private Sender sender;
    private Receiver receiver;
    private List<Goods> goods;
    private AppreciationDTOS appreciation;

    @Data
    public static class PrintParam {
        // 参数类型； 模板类型
        private String paramType;
        // 运单号
        private String mailNo;
        // 大头笔
        private String printMark;
        // 集包地
        private String printBagaddr;
        // 备注
        private String remark;
    }

    @Data
    public static class Sender {
        private String name;
        private String mobile;
        private String prov;
        private String city;
        private String county;
        private String address;
    }

    @Data
    public static class Receiver {
        private String name;
        private String mobile;
        private String prov;
        private String city;
        private String county;
        private String address;
    }

    @Data
    public static class Goods {
        private String name;
        private Integer qty;
        private Long weight;
        private Long freight;
        private String remark;
    }

    @Data
    public static class AppreciationDTOS {
        private Integer type;
        private Integer amount;
        private String backBillCode;
    }
}

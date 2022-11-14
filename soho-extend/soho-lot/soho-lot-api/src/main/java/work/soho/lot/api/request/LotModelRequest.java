package work.soho.lot.api.request;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class LotModelRequest {
    /**
     * ID
     */
    private Integer id;

    /**
     * 模型名称
     */
    private String name;

    /**
     * 供应商ID
     */
    private Integer supplierId;

    /**
     * 模型节点信息
     */
    private List<Item> itemList = new ArrayList<>();

    @Data
    public static class Item {
        private Integer id;

        /**
         * 模型ID
         */
        private Integer modelId;

        /**
         * 标题
         */
        private String title;

        /**
         * 单位
         */
        private String unit;

        /**
         * 参数名
         */
        private String paramsName;

        /**
         * 提示
         */
        private String tips;

        /**
         * wifi模块pin编号
         */
        private String extendedData;

        /**
         * 数据类型
         */
        private String type;
    }
}

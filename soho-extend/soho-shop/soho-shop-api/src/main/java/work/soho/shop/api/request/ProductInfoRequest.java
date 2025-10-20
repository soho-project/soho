package work.soho.shop.api.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 商品信息
 *
 * 用来商品新增、修改时使用
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductInfoRequest {
    /**
     * id
     */
    private Long id;

    /**
     * spu_code
     */
    private String spuCode;

    /**
     * name
     */
    private String name;

    /**
     * sub_title
     */
    private String subTitle;

    /**
     * qty
     */
    private Integer qty;

    /**
     * original_price
     */
    private BigDecimal originalPrice;

    /**
     * selling_price
     */
    private BigDecimal sellingPrice;

    /**
     * shop_id
     */
    private Integer shopId;

    /**
     * main_image
     */
    private String mainImage;

    /**
     * thumbnails
     */
    private String thumbnails;

    /**
     * detail_html
     */
    private String detailHtml;

    /**
     * comment_count
     */
    private Integer commentCount;

    /**
     * category_id
     */
    private String categoryId;

    /**
     * shelf_status
     */
    private Integer shelfStatus;

    /**
     * audit_status
     */
    private Integer auditStatus;

    /**
     * 产品运费信息
     */
    private FeightInfo feightInfo;

    /**
     * skus
     */
    private Map<String, SkuInfo> skus;

    /**
     * specs
     */
    private Map<Integer, String> specs;

    /**
     * 产品sku信息
     */
    @Data
    public static class SkuInfo {
        private Integer id;

        /**
         * product_id
         */
        private String productId;

        /**
         * code
         */
        private String code;

        /**
         * qty
         */
        private Integer qty;

        /**
         * original_price
         */
        private BigDecimal originalPrice;

        /**
         * selling_price
         */
        private BigDecimal sellingPrice;

        /**
         * main_image
         */
        private String mainImage;

    }

    /**
     * 产品运费相关信息
     */
    @Data
    public static class FeightInfo {
        private Long id;

        /**
         * product_id
         */
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private Long productId;

        /**
         * template_id
         */
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private Long templateId;

        /**
         * weight
         */
        private BigDecimal weight;

        /**
         * length
         */
        private BigDecimal length;

        /**
         * width
         */
        private BigDecimal width;

        /**
         * height
         */
        private BigDecimal height;

        /**
         * volumetric_weight
         */
        private BigDecimal volumetricWeight;
    }
}

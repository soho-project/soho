package work.soho.shop.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class ProductDetailsVo {
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
    @JsonFormat(shape = JsonFormat.Shape.STRING)
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
     * rating
     */
    private Integer rating;

    /**
     * sold_qty
     */
    private Integer soldQty;

    /**
     * like_count
     */
    private Integer likeCount;

    /**
     * view_count
     */
    private Integer viewCount;

    /**
     * category_id
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long categoryId;

    /**
     * shelf_status
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer shelfStatus;

    /**
     * audit_status
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer auditStatus;

    /**
     *  tags
     */
    private String tags;

    /**
     * freight_template_id
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long freightTemplateId;

    /**
     * 产品运费相关信息
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
     * specs_names
     */
    private Map<Integer, String> specsNames;

    @Data
    public static class SkuInfo {
        private Integer id;

        /**
         * product_id
         */
        private Long productId;

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
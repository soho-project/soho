package work.soho.shop.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class FreightTemplateVo {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    /**
     * shop_id
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer shopId;

    /**
     * name
     */
    private String name;

    /**
     * type
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer type;

    /**
     * valuation_method
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer valuationMethod;

    /**
     * is_free_shipping
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer isFreeShipping;

    /**
     * free_condition_type
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer freeConditionType;

    /**
     * free_condition_value
     */
    private BigDecimal freeConditionValue;

    /**
     * include_special_regions
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Integer includeSpecialRegions;

    /**
     * 规则列表
     */
    private List<FreightTemplateRuleVo> rules;

    @Data
    public static class FreightTemplateRuleVo {
        private Long id;

        /**
         * template_id
         */
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private Long templateId;

        /**
         * region_codes
         */
        private String regionCodes;

        /**
         * first_unit
         */
        private BigDecimal firstUnit;

        /**
         * first_unit_price
         */
        private BigDecimal firstUnitPrice;

        /**
         * continue_unit
         */
        private BigDecimal continueUnit;

        /**
         * continue_unit_price
         */
        private BigDecimal continueUnitPrice;

        /**
         * is_default
         */
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        private Integer isDefault;
    }
}

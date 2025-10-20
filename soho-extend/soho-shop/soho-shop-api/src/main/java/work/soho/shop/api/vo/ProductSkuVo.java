package work.soho.shop.api.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSkuVo {
    // 产品ID； 创建订单时必填
    private Long productId;

    // sku ID； 创建订单时必填
    private Integer skuId = 0;

    // 商品数量； 创建订单时必填
    private Integer qty;

    // 商品名称
    private String name;

    // 图片
    private String mainImage;

    // 价格
    private BigDecimal amount;

    // 单元总价
    private BigDecimal totalAmount;
}

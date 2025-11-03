package work.soho.shop.api.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class UserCartVo {
    private List<ShopGroup> shopGroups;

    @Data
    public static class ShopGroup {
        private ShopVo shop;
        private List<ShopCartItems> cartItems = new ArrayList<>();
    }

    @Data
    public static class ShopCartItems {
        private Long id;
        private Long productId;
        private Long skuId;
        private BigDecimal price;
        private BigDecimal sellingPrice;
        private Integer qty;
        private String name;
        private String mainImage;
        private String spceString;
    }
}

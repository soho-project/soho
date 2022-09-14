package work.soho.api.admin.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class NumberCardVo {
    private String icon;
    private String color;
    private String title;
    private BigDecimal number;
}

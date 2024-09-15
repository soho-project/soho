package work.soho.admin.api.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class NumberCardVo {
    private String icon;
    private String color;
    private String title;
    private BigDecimal number;
}

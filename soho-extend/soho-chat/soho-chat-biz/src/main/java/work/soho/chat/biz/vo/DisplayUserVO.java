package work.soho.chat.biz.vo;

import lombok.Data;

@Data
public class DisplayUserVO extends BaseUserVO {
    private Integer age;
    private String introduction;
    private String address;
    private Integer sex;
}

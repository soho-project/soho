package work.soho.groovy.biz.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TestCodeVO {
    @ApiModelProperty(value = "代码")
    private String code;

    @ApiModelProperty(value = "测试代码（辅助测试，调用入口）")
    private String testCode;
}

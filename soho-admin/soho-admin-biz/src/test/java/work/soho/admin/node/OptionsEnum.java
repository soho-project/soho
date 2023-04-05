package work.soho.admin.node;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum OptionsEnum {

    ADD("add", "新增"),
    EDIT("edit", "修改"),
    GETINFO("getInfo", "详情"),
    LIST("list", "列表"),
    REMOVE("remove", "删除"),
    OPTIONS("options", "下拉选"),
    DEF("default", "默认"),
    ;

    private String code;
    private String desc;

    public static OptionsEnum match(String code) {
        return Arrays.stream(values()).filter(e -> StringUtils.equals(e.getCode(), code)).findFirst().orElse(DEF);
    }
}

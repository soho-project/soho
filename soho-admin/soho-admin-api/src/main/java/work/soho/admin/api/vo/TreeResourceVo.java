package work.soho.admin.api.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class TreeResourceVo {
    private String title;
    private String value;
    private String key;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<TreeResourceVo> children;
}

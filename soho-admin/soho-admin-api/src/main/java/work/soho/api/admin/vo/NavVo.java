package work.soho.api.admin.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.LinkedList;

@Data
public class NavVo {
    private String label;
    private String key;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private LinkedList<NavVo> children = new LinkedList<>();
}

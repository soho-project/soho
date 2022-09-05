package work.soho.api.admin.vo;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class TreeContentCategoryVo {
    private String title;
    private Long key;
    private Long value;
    private Long parentId;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<TreeContentCategoryVo> children = new ArrayList<>();
}

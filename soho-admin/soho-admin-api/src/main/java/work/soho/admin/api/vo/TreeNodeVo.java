package work.soho.admin.api.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TreeNodeVo<T,F,C,X> {
    private T key;
    private F value;
    private C  parentId;
    private X title;


    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    List<TreeNodeVo> children = new ArrayList<>();

    public TreeNodeVo(T id, F id1, C parentId, X title) {
        this.key = id;
        this.value = id1;
        this.parentId = parentId;
        this.title = title;
    }
}

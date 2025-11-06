package work.soho.admin.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TreeNodeVo<T,F,C,X> {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private T key;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private F value;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private C  parentId;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
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

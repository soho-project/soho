package work.soho.api.admin.vo;

import lombok.Data;

import java.util.List;

@Data
public class TreeResourceVo {
    private String title;
    private String value;
    private String key;
    List<TreeResourceVo> children;
}

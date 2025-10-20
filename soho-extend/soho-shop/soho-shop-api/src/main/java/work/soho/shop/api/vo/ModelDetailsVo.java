package work.soho.shop.api.vo;

import lombok.Data;

import java.util.List;

@Data
public class ModelDetailsVo {
    private Integer id;
    private String name;
    private List<ModelSpecVo> specList;
}

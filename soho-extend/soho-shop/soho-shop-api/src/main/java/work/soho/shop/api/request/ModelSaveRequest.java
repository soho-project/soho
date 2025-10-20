package work.soho.shop.api.request;

import lombok.Data;
import work.soho.shop.api.vo.ModelSpecVo;

import java.util.List;

@Data
public class ModelSaveRequest {
    private Integer id;
    private String name;
    private List<ModelSpecVo> specs;
}

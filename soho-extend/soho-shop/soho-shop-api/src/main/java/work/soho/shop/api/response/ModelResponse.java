package work.soho.shop.api.response;

import lombok.Data;
import work.soho.shop.api.vo.ModelSpecVo;

import java.util.List;

@Data
public class ModelResponse {
    private Integer id;
    private String name;
    List<ModelSpecVo> specs;
}

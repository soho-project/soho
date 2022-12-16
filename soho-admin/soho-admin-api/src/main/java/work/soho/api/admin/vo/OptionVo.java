package work.soho.api.admin.vo;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * 选项单元
 *
 * select radio 等
 *
 * @param <T>
 * @param <X>
 */
@Data
public class OptionVo <X, T>{
    private X value;
    private T label;
}

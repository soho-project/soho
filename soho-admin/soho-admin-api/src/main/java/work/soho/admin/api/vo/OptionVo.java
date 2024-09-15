package work.soho.admin.api.vo;

import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * 选项单元
 *
 * 这是一个通用的数据结构，用于前端的 select 等组件
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

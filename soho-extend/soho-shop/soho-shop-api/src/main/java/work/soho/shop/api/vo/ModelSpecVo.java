package work.soho.shop.api.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.util.List;

@Data
public class ModelSpecVo {
    /**
     * id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 是否销售属性
     */
    private Integer isSale;

    /**
     * 类型
     *
     * 10： 文本输入；  20： 单选
     */
    private Integer type;

    /**
     * 选项
     */
    private List<String> options;
}

package work.soho.example.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import lombok.Data;
import java.time.LocalDateTime;

@TableName(value ="example_option")
@Data
public class ExampleOption implements Serializable {
    /**
    * ID;;
    */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
    * key;;
    */
    @TableField(value = "`key`")
    private String key;

    /**
    * 选项值;;
    */
    @TableField(value = "`value`")
    private String value;

    /**
    * 更新时间;;frontType:datetime
    */
    @TableField(value = "updated_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
    * 创建时间;;frontType:datetime
    */
    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

}

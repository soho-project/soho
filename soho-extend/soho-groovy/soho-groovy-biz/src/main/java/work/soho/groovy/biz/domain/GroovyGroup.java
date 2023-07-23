package work.soho.groovy.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import lombok.Data;
import java.time.LocalDateTime;

@TableName(value ="groovy_group")
@Data
public class GroovyGroup implements Serializable {
    /**
    * null
    */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
    * 组名
    */
    @TableField(value = "name")
    private String name;

    /**
    * 组标题
    */
    @TableField(value = "title")
    private String title;

    /**
    * 组状态;1:正常,2:禁用
    */
    @TableField(value = "status")
    private Integer status;

    /**
    * 更新时间
    */
    @TableField(value = "updated_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
    * 创建时间
    */
    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

}

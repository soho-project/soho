package work.soho.groovy.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import lombok.Data;
import java.time.LocalDateTime;

@TableName(value ="groovy_info")
@Data
public class GroovyInfo implements Serializable {
    /**
    * null
    */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
    * 组ID;;frontType:select,foreign:groovy_group.id~title
    */
    @TableField(value = "group_id")
    private Integer groupId;

    /**
    * null
    */
    @TableField(value = "name")
    private String name;

    /**
    * null
    */
    @TableField(value = "code")
    private String code;

    /**
    * null
    */
    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
    * null
    */
    @TableField(value = "updated_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

}
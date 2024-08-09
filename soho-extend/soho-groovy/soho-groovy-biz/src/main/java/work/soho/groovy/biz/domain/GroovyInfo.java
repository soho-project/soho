package work.soho.groovy.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName(value ="groovy_info")
@Data
public class GroovyInfo implements Serializable {
    /**
    * ID
    */
    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
    * 组ID;;frontType:select,foreign:groovy_group.id~title
    */
    @ApiModelProperty(value = "组ID")
    @TableField(value = "group_id")
    private Integer groupId;

    /**
    * 脚本名称
    */
    @ApiModelProperty(value = "脚本名称")
    @TableField(value = "name")
    private String name;

    /**
    * 脚本代码
    */
    @ApiModelProperty(value = "脚本代码")
    @TableField(value = "code")
    private String code;

    /**
    * 创建时间
    */
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
    * 更新时间
    */
    @ApiModelProperty(value = "更新时间")
    @TableField(value = "updated_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

}
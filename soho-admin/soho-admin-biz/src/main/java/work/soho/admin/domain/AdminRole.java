package work.soho.admin.domain;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 
 *
 * @TableName admin_role
 */
@Data
public class AdminRole implements Serializable {


    /**
     * ID
     */
    @ApiModelProperty("ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 
     */
    @ApiModelProperty("角色名")
    private String name;

    /**
     * 
     */
    @ApiModelProperty("备注")
    private String remarks;

    /**
     * 
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    /**
     * 是否启用
     */
    @ApiModelProperty("是否启用")
    private Integer enable;

}

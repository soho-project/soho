package work.soho.admin.biz.domain;

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
 * @TableName admin_role_user
 */
@Data
public class AdminRoleUser implements Serializable {


    /**
     * ID
     */
    @ApiModelProperty("ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     *
     */
    @ApiModelProperty("角色ID")
    private Long roleId;

    /**
     * 用户ID
     */
    @ApiModelProperty("用户ID")
    private Long userId;

    /**
     * 状态
     */
    @ApiModelProperty("状态")
    private Integer status;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

}

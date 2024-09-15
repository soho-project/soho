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
 * @TableName admin_role_resource
 */
@Data
public class AdminRoleResource implements Serializable {


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
     *
     */
    @ApiModelProperty("资源ID")
    private Long resourceId;

    /**
     *
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

}

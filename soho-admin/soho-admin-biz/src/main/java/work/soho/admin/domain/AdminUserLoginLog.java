package work.soho.admin.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName(value ="admin_user_login_log")
@Data
public class AdminUserLoginLog implements Serializable {
    /**
    * ID;;
    */
    @ApiModelProperty(value = "ID;;")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
    * 后台用户ID;admin_user.id~username;frontType:select,
    */
    @ApiModelProperty(value = "后台用户ID;admin_user.id~username;frontType:select,")
    @TableField(value = "admin_user_id")
    private Long adminUserId;

    /**
    * 客户端IP地址考虑IPv6字段适当放宽
    */
    @ApiModelProperty(value = "客户端IP地址考虑IPv6字段适当放宽")
    @TableField(value = "client_ip")
    private String clientIp;

    /**
    * 客户端软件信息
    */
    @ApiModelProperty(value = "客户端软件信息")
    @TableField(value = "client_user_agent")
    private String clientUserAgent;

    /**
    * 给用户发放的token
    */
    @ApiModelProperty(value = "给用户发放的token")
    @TableField(value = "token")
    private String token;

    /**
    * 创建时间
    */
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
}

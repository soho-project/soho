package work.soho.open.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@TableName(value ="open_app")
@Data
public class OpenApp implements Serializable {
    /**
    * ID
    */
    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
    * app名称
    */
    @ApiModelProperty(value = "app名称")
    @TableField(value = "name")
    private String name;

    /**
     * code
     */
    @TableField(value = "code")
    private String code;

    /**
    * 通信鉴权密钥
    */
    @ApiModelProperty(value = "通信鉴权密钥")
    @TableField(value = "app_key")
    private String appKey;

    /**
    * 更新时间
    */
    @TableField(value = "updated_time")
    private String updatedTime;

    /**
    * 创建时间
    */
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "created_time")
    private String createdTime;

    /**
     * 应用访问安全IP地址
     */
    @ApiModelProperty(value = "应用访问安全IP地址")
    @TableField(value = "security_ips")
    private String securityIps;

}

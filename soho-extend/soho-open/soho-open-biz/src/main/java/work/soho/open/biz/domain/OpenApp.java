package work.soho.open.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import lombok.Data;

@TableName(value ="open_app")
@Data
public class OpenApp implements Serializable {
    /**
    * a
    */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
    * app名称
    */
    @TableField(value = "name")
    private String name;

    /**
     * code
     */
    private String code;

    /**
    * 通信鉴权密钥
    */
    @TableField(value = "app_key")
    private String appKey;

    /**
    * null
    */
    @TableField(value = "updated_time")
    private String updatedTime;

    /**
    * null
    */
    @TableField(value = "created_time")
    private String createdTime;

    /**
     * 应用访问安全IP地址
     */
    @TableField(value = "security_ips")
    private String securityIps;

}

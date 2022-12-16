package work.soho.user.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 用户信息;;option:id~username
 * @TableName user_info
 */
@TableName(value ="user_info")
@Data
public class UserInfo implements Serializable {
    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名
     */
    @TableField(value = "username")
    private String username;

    /**
     * 邮箱
     */
    @TableField(value = "email")
    private String email;

    /**
     * 手机号
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 密码;;frontType:password,ignoreInList:true
     */
    @TableField(value = "password")
    private String password;

    /**
     * 头像;;frontType:upload,uploadCount:1,ignoreInList:true
     */
    @TableField(value = "avatar")
    private String avatar;

    /**
     * 状态;0:禁用,1:活跃;frontType:select
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * 年龄;;frontName:年龄
     */
    @TableField(value = "age")
    private Integer age;

    /**
     * 性别;0:女,1:男;frontType:select,frontName:性别
     */
    @TableField(value = "sex")
    private Integer sex;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time")
    private LocalDateTime updatedTime;

    /**
     * 头像;;frontType:upload,uploadCount:1
     */
    @TableField(value = "created_time")
    private LocalDateTime createdTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

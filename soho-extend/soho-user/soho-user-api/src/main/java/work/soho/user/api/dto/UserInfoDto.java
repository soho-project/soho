package work.soho.user.api.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户信息
 */
@Data
public class UserInfoDto implements Serializable {
    /**
     * ID
     */
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像;;frontType:upload,uploadCount:1,ignoreInList:true
     */
    private String avatar;

    /**
     * 状态;0:禁用,1:活跃;frontType:select
     */
    private Integer status;

    /**
     * 年龄;;frontName:年龄
     */
    private Integer age;

    /**
     * 性别;0:女,1:男;frontType:select,frontName:性别
     */
    private Integer sex;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 头像;;frontType:upload,uploadCount:1
     */
    private LocalDateTime createdTime;

    private static final long serialVersionUID = 1L;
}

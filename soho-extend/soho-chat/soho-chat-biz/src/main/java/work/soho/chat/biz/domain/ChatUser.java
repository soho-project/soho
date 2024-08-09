package work.soho.chat.biz.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@TableName(value ="chat_user")
@Data
public class ChatUser implements Serializable {
    /**
    * 聊天用户ID
    */
    @ApiModelProperty(value = "聊天用户ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
    * 用户名
    */
    @ApiModelProperty(value = "用户名")
    @TableField(value = "username")
    private String username;

    /**
     * 用户认证密码
     */
    @ApiModelProperty(value = "用户认证密码")
    @TableField(value = "password")
    private String password;

    /**
     * 用户邮箱
     */
    @ApiModelProperty(value = "用户邮箱")
    @TableField(value = "email")
    private String email;

    /**
     * 用户手机号
     */
    @ApiModelProperty(value = "用户手机号")
    @TableField(value="phone")
    private String phone;

    /**
    * 用户昵称
    */
    @ApiModelProperty(value = "用户昵称")
    @TableField(value = "nickname")
    private String nickname;

    /**
    * 更新时间
    */
    @ApiModelProperty(value = "更新时间")
    @TableField(value = "updated_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
    * 创建时间
    */
    @ApiModelProperty(value = "创建时间")
    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
    * 用户原始类型；admin,user,supper等
    */
    @ApiModelProperty(value = "用户原始类型；admin,user,supper等")
    @TableField(value = "origin_type")
    private String originType;

    /**
    * 原始用户ID;;
    */
    @ApiModelProperty(value = "原始用户ID;;")
    @TableField(value = "origin_id")
    private String originId;

    /**
     * 用户性别 0 没有设置  1 男  2 女
     */
    @ApiModelProperty(value = "用户性别 0 没有设置  1 男  2 女")
    @TableField(value = "sex")
    private Integer sex;

    /**
     * 用户头像
     */
    @ApiModelProperty(value = "用户头像")
    @TableField(value = "avatar")
    private String avatar;

    /**
     * introduction 简介
     */
    @ApiModelProperty(value = "introduction 简介")
    @TableField(value = "introduction")
    private String introduction;

    /**
     * 年龄
     */
    @ApiModelProperty(value = "age")
    @TableField(value = "age")
    private Integer age;

    /**
     * 地址
     */
    @ApiModelProperty(value = "address")
    @TableField(value = "address")
    private String address;

    /**
     * 注册IP
     */
    @ApiModelProperty(value = "注册IP地址")
    @TableField(value = "register_ip")
    private String registerIp;

    /**
     * 登录IP
     */
    @ApiModelProperty(value = "登录IP地址")
    @TableField(value = "login_ip")
    private String loginIp;

    /**
     * 用户生日
     */
    @ApiModelProperty(value = "用户生日")
    @TableField(value = "birthday")
    private Date birthday;

    /**
     * 添加好友认证方式
     */
    @ApiModelProperty(value = "添加好友认证方式")
    @TableField(value = "auth_friend_type")
    private Integer authFriendType;
}

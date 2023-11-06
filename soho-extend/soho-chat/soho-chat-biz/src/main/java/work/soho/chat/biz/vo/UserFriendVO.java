package work.soho.chat.biz.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class UserFriendVO implements Serializable {
    /**
     * ID;;
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 聊天用户ID;;
     */
    @TableField(value = "chat_uid")
    private Long chatUid;

    /**
     * 好友用户ID;;
     */
    @TableField(value = "friend_uid")
    private Long friendUid;

    /**
     * 好友备注名;;
     */
    @TableField(value = "notes_name")
    private String notesName;

    /**
     * 用户名
     */
    @TableField(value = "username")
    private String username;

    /**
     * 用户昵称
     */
    @TableField(value = "nickname")
    private String nickname;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
     * 创建时间
     */
    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
     * 用户原始类型；admin,user,supper等
     */
    @TableField(value = "origin_type")
    private String originType;

    /**
     * 原始用户ID;;
     */
    @TableField(value = "origin_id")
    private String originId;

    /**
     * 用户性别 0 没有设置  1 男  2 女
     */
    @TableField(value = "sex")
    private Integer sex;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 地址
     */
    private String address;

    /**
     * 生日
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;

    /**
     * 用户头像
     */
    @TableField(value = "avatar")
    private String avatar;

    /**
     * introduction 简介
     */
    @TableField(value = "introduction")
    private String introduction;



}

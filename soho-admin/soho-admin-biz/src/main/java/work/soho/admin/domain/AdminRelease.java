package work.soho.admin.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import lombok.Data;
import java.time.LocalDateTime;

@TableName(value ="admin_release")
@Data
public class AdminRelease implements Serializable {
    /**
    * ;;
    */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
    * 版本号
    */
    @TableField(value = "version")
    private String version;

    /**
    * 发版描述
    */
    @TableField(value = "notes")
    private String notes;

    /**
    * 创建时间
    */
    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
    * 更新时间
    */
    @TableField(value = "updated_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
    * 下载地址
    */
    @TableField(value = "url")
    private String url;

    /**
    * 平台类型;1:Windows,2:Linux;frontType:select,
    */
    @TableField(value = "platform_type")
    private Integer platformType;

}
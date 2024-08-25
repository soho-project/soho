package work.soho.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@TableName(value ="upload_file")
@Data
public class UploadFile implements Serializable {
    /**
    * null
    */
    @ApiModelProperty("ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
    * 资源地址
    */
    @ApiModelProperty("资源URL地址")
    @TableField(value = "url")
    private String url;

    /**
    * 资源大小
    */
    @ApiModelProperty("资源大小")
    @TableField(value = "size")
    private Long size;

    /**
    * 引用次数
    */
    @ApiModelProperty("引用次数")
    @TableField(value = "ref_count")
    private Integer refCount;

    /**
     * 文件hash
     */
    @ApiModelProperty("文件hash")
    @TableField(value = "hash")
    private String hash;

    /**
    * 更新时间
    */
    @ApiModelProperty("更新时间")
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
    * 创建时间
    */
    @ApiModelProperty("创建时间")
    @TableField(value = "created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
     * 最右一次引用时间
     */
    @ApiModelProperty("最右一次引用时间")
    @TableField(value = "last_ref_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastRefTime;

    /**
    * 文件类型
    */
    @ApiModelProperty("文件类型")
    @TableField(value = "file_type")
    private String fileType;

    /**
    * 扩展名
    */
    @ApiModelProperty("扩展名")
    @TableField(value = "extension")
    private String extension;
}

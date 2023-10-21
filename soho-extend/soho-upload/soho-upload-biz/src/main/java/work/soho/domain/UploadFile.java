package work.soho.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import lombok.Data;
import java.time.LocalDateTime;

@TableName(value ="upload_file")
@Data
public class UploadFile implements Serializable {
    /**
    * null
    */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
    * 资源地址
    */
    @TableField(value = "url")
    private String url;

    /**
    * 资源大小
    */
    @TableField(value = "size")
    private Long size;

    /**
    * 引用次数
    */
    @TableField(value = "ref_count")
    private Integer refCount;

    /**
     * 文件hash
     */
    @TableField(value = "hash")
    private String hash;

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
     * 最右一次引用时间
     */
    @TableField(value = "last_ref_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastRefTime;

    /**
    * 文件类型
    */
    @TableField(value = "file_type")
    private String fileType;

    /**
    * 扩展名
    */
    @TableField(value = "extension")
    private String extension;
}

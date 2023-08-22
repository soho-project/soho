package work.soho.admin.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName(value = "admin_dict")
public class AdminDict implements Serializable {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 父主键
     */
    @TableField(value = "parent_id")
    private Long parentId;

    /**
     * 字典码;;isFilter:true
     */
    @TableField(value = "code")
    private String code;

    /**
     * 字典值
     */
    @TableField(value = "dict_key")
    private Integer dictKey;

    /**
     * 字典名称;;isFilter:true
     */
    @TableField(value = "dict_value")
    private String dictValue;

    /**
     * 排序
     */
    @TableField(value = "sort")
    private Integer sort;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 删除标志 0:正常 1:已删除
     */
    @TableLogic
    private Integer deleted = 0;

    /**
     * 更新时间
     */
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedTime;

    /**
     * 创建时
     */
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

}
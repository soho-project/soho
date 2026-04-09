package work.soho.admin.biz.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 平台管理岗位实体。
 */
@Data
@TableName("admin_post")
public class AdminPost implements Serializable {

    /**
     * 岗位ID
     */
    @ApiModelProperty("岗位ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 岗位名称
     */
    @ApiModelProperty("岗位名称")
    private String name;

    /**
     * 岗位编码
     */
    @ApiModelProperty("岗位编码")
    private String code;

    /**
     * 排序值
     */
    @ApiModelProperty("排序值")
    private Integer sort;

    /**
     * 状态 1启用 0禁用
     */
    @ApiModelProperty("状态")
    private Integer status;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 删除标记
     */
    @ApiModelProperty("删除标记")
    @TableLogic
    private Integer deleted = 0;

    /**
     * 更新时间
     */
    @ApiModelProperty("更新时间")
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedTime;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;
}

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
import java.util.List;

/**
 * 平台管理部门实体。
 */
@Data
@TableName("admin_dept")
public class AdminDept implements Serializable {

    /**
     * 部门ID
     */
    @ApiModelProperty("部门ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 父部门ID，根节点固定为0
     */
    @ApiModelProperty("父部门ID")
    @TableField("parent_id")
    private Long parentId;

    /**
     * 部门名称
     */
    @ApiModelProperty("部门名称")
    private String name;

    /**
     * 排序值
     */
    @ApiModelProperty("排序值")
    private Integer sort;

    /**
     * 负责人
     */
    @ApiModelProperty("负责人")
    private String leader;

    /**
     * 联系电话
     */
    @ApiModelProperty("联系电话")
    private String phone;

    /**
     * 邮箱
     */
    @ApiModelProperty("邮箱")
    private String email;

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

    /**
     * 子部门列表
     */
    @ApiModelProperty("子部门列表")
    @TableField(exist = false)
    private List<AdminDept> children;
}

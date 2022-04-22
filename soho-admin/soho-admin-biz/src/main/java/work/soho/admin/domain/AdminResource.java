package work.soho.admin.domain;

import java.io.Serializable;

import java.time.LocalDateTime;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 
 *
 * @TableName admin_resource
 */
@Data
public class AdminResource implements Serializable {
    /**
     * ID
     */
    @ApiModelProperty("ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 英文名
     */
    @ApiModelProperty("英文名")
    private String name;

    /**
     * 
     */
    @ApiModelProperty("路由")
    private String route;

    /**
     * 资源类型
     */
    @ApiModelProperty("资源类型")
    private Integer type;

    /**
     * 
     */
    @ApiModelProperty("备注")
    private String remarks;

    /**
     * 
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    /**
     * 资源界面是否可见
     */
    @ApiModelProperty("资源界面是否可见")
    private Integer visible;

    /**
     * 排序
     */
    @ApiModelProperty("排序")
    private Integer sort;

    /**
     * 父ID
     */
    @ApiModelProperty("父ID")
    private Long breadcrumbParentId;

    /**
     * 中文名
     */
    @ApiModelProperty("中文名")
    private String zhName;

    /**
     * 菜单图标
     */
    @ApiModelProperty("菜单图标")
    private String iconName;

}

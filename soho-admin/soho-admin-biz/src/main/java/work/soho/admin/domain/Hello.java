package work.soho.admin.domain;

import java.io.Serializable;

import java.time.LocalDateTime;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import work.soho.common.data.excel.annotation.ExcelColumn;

/**
 * 
 *
 * @TableName hello
 */
@Data
public class Hello implements Serializable {

    @ExcelColumn(value = "ID")
    private Integer id;

    @ExcelColumn("名称")
    private String name;

    @ExcelColumn("值")
    private String value;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ExcelColumn("更新时间")
    private Date updatedTime;

    @ExcelColumn(value = "创建时间", dateFormat = "yyyy-dd-MM")
    private Date createdTime;
}

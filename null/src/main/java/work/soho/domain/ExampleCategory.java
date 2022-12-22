package work.soho.domain;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

@TableName(value ="example_category")
@Data
public class ExampleCategory implements Serializable {
    /**
    * 创建时间
    */
    @TableField(value = "created_time")
    private LocalDateTime createdTime;

    /**
    * ID;;optionKey
    */
    @TableField(value = "id")
    private Integer id;

    /**
    * 图片;;frontType:upload,uploadCount:3
    */
    @TableField(value = "img")
    private String img;

    /**
    * 只是日期;;frontType:date
    */
    @TableField(value = "only_date")
    private String onlyDate;

    /**
    * 父级ID;;frontType:treeSelect,parent:id,foreign:example_category,frontName:Parent_Category
    */
    @TableField(value = "parent_id")
    private Integer parentId;

    /**
    * 日期时间;;frontType:datetime
    */
    @TableField(value = "pay_datetime")
    private LocalDateTime payDatetime;

    /**
    * 标题;;optionValue
    */
    @TableField(value = "title")
    private String title;

    /**
    * 更新时间
    */
    @TableField(value = "updated_time")
    private LocalDateTime updatedTime;

}
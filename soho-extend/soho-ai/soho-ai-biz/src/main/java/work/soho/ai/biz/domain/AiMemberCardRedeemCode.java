package work.soho.ai.biz.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("ai_member_card_redeem_code")
public class AiMemberCardRedeemCode implements Serializable {
    @ExcelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ExcelProperty("member_card_id")
    @TableField("member_card_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long memberCardId;

    @ExcelProperty("batch_no")
    @TableField("batch_no")
    private String batchNo;

    @ExcelProperty("redeem_code")
    @TableField("redeem_code")
    private String redeemCode;

    /**
     * 0:unused 1:used 2:disabled
     */
    @ExcelProperty("status")
    @TableField("status")
    private Integer status;

    @ExcelProperty("used_by_user_id")
    @TableField("used_by_user_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long usedByUserId;

    @ExcelProperty("user_member_card_id")
    @TableField("user_member_card_id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userMemberCardId;

    @ExcelProperty("used_time")
    @TableField("used_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime usedTime;

    @ExcelProperty("expire_time")
    @TableField("expire_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    @ExcelProperty("remark")
    @TableField("remark")
    private String remark;

    @ExcelProperty("updated_time")
    @TableField("updated_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    @ExcelProperty("created_time")
    @TableField("created_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
}

package work.soho.game.biz.wordmatch.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName(value = "game_word_match_battle")
@ApiModel("Word Match 对战记录")
public class WordMatchBattle implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("room_id")
    @TableField("room_id")
    private String roomId;

    @ApiModelProperty("mode")
    @TableField("mode")
    private String mode;

    @ApiModelProperty("status")
    @TableField("status")
    private String status;

    @ApiModelProperty("winner_id")
    @TableField("winner_id")
    private String winnerId;

    @ApiModelProperty("end_reason")
    @TableField("end_reason")
    private String endReason;

    @ApiModelProperty("scores_json")
    @TableField("scores_json")
    private String scoresJson;

    @ApiModelProperty("started_at")
    @TableField("started_at")
    private LocalDateTime startedAt;

    @ApiModelProperty("ended_at")
    @TableField("ended_at")
    private LocalDateTime endedAt;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
}

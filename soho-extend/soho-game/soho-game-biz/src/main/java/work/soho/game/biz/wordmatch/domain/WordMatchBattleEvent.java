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
@TableName(value = "game_word_match_battle_event")
@ApiModel("Word Match 回放事件流")
public class WordMatchBattleEvent implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("battle_id")
    @TableField("battle_id")
    private Long battleId;

    @ApiModelProperty("room_id")
    @TableField("room_id")
    private String roomId;

    @ApiModelProperty("seq")
    @TableField("seq")
    private Long seq;

    @ApiModelProperty("type")
    @TableField("type")
    private String type;

    @ApiModelProperty("payload_json")
    @TableField("payload_json")
    private String payloadJson;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;
}

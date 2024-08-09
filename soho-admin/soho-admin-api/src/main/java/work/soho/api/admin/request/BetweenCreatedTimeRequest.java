package work.soho.api.admin.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class BetweenCreatedTimeRequest {
    @ApiModelProperty(value = "时间范围")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date[] betweenCreatedTime;

    /**
     * 获取开始时间
     *
     * @return
     */
    public Date getStartTime() {
        if(betweenCreatedTime != null && betweenCreatedTime.length == 2) {
            return betweenCreatedTime[0];
        }
        return null;
    }

    /**
     * 获取结束时间
     *
     * @return
     */
    public Date getEndTime() {
        if(betweenCreatedTime != null && betweenCreatedTime.length == 2) {
            return betweenCreatedTime[1];
        }
        return null;
    }
}

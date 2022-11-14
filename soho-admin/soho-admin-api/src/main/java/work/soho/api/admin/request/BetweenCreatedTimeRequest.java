package work.soho.api.admin.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class BetweenCreatedTimeRequest {
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

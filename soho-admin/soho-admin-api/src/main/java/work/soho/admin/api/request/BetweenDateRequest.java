package work.soho.admin.api.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class BetweenDateRequest {
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date[] createTime;

    /**
     * 获取开始时间
     *
     * @return
     */
    public Date getStartTime() {
        if(createTime != null && createTime.length == 2) {
            return createTime[0];
        }
        return null;
    }

    /**
     * 获取结束时间
     *
     * @return
     */
    public Date getEndTime() {
        if(createTime != null && createTime.length == 2) {
            return createTime[1];
        }
        return null;
    }
}

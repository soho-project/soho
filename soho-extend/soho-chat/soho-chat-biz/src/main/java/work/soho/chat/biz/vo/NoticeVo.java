package work.soho.chat.biz.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticeVo<T> {
    /**
     * ID
     */
    private Integer id;

    /**
     * 聊天用户ID
     */
    private Long chatUid;

    /**
     * 通知处理状态;0:待处理,1:同意,2:拒绝,3:已处理
     */
    private Integer status;

    /**
     * 业务类型;1:好友申请
     */
    private Integer type;

    /**
     * 跟踪ID
     */
    private Long trackingId;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
     * 目标对象；
     *
     * 好友申请单/群聊申请单/
     */
    private T target;
}

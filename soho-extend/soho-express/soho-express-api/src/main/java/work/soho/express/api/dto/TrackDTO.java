package work.soho.express.api.dto;

import lombok.Data;

@Data
public class TrackDTO {
    /**
     * 扫描类型
     */
    private String scanType;

    /**
     * 状态
     */
    private String status;

    /**
     * 站点
     */
    private String location;

    /**
     * 扫描时间
     */
    private String scanDate;

    /**
     * 站点城市
     */
    private String scanSiteCity;

    /**
     * 描述
     */
    private String description;
}

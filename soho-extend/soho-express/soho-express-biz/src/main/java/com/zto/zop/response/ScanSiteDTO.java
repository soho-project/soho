package com.zto.zop.response;

import lombok.Data;

@Data
public class ScanSiteDTO {
    private String name;
    private String code;
    private String phone;
    private String prov;
    private String city;

    /** 是否为中心网点，F/N */
    private String isCenter;

    /** 是否转运（0/1） */
    private Integer isTransfer;

    /** 网点唯一ID */
    private Long id;

    /** 网点ID（可能与 id 相同） */
    private Long siteId;
}

package com.zto.zop.dto;

import lombok.Data;

/**
 * 收件人信息
 */
@Data
public class ReceiveInfoDTO {

    private String receiverName;
    private String receiverPhone;
    private String receiverMobile;

    private String receiverProvince;
    private String receiverCity;
    private String receiverDistrict;
    private String receiverAddress;
}

package work.soho.express.api.dto;

import lombok.Data;

@Data
public class BillPrintDTO {
    // 打印机名称
    private String printerId;
    // 设备ID
    private String deviceId;
    // 二维码ID
    private String qrcodeId;
    // 打印渠道
    private String printChannel;



}

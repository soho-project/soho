package com.zto.zop.request;

import com.zto.zop.dto.*;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {

    /** 合作方类型 */
    private String partnerType;

    /** 订单类型 */
    private String orderType;

    /** 合作方订单号（商家自定义） */
    private String partnerOrderCode;

    /** 账户信息 */
    private AccountInfoDTO accountInfo;

    /** 运单号（可空） */
    private String billCode;

    /** 寄件人信息 */
    private SenderInfoDTO senderInfo;

    /** 收件人信息 */
    private ReceiveInfoDTO receiveInfo;

    /** 增值服务列表 */
    private List<OrderVasDTO> orderVasList;

    /** 营业厅编码 */
    private String hallCode;

    /** 网点编码 */
    private String siteCode;

    /** 网点名称 */
    private String siteName;

    /** 汇总信息 */
    private SummaryInfoDTO summaryInfo;

    /** 备注 */
    private String remark;

    /** 订单明细 */
    private List<OrderItemDTO> orderItems;

    /** 智能柜信息 */
    private CabinetDTO cabinet;
}

package work.soho.pay.api.event;

import lombok.Data;

@Data
public class PayCallbackEvent {
    private String orderNo;
    private Integer status;
}

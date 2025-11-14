package work.soho.pay.api.service;

import work.soho.pay.api.dto.CreatePayInfoDto;
import work.soho.pay.api.dto.OrderDetailsDto;

public interface PayOrderApiService {
    CreatePayInfoDto payOrder(OrderDetailsDto orderDetailsDto);
}

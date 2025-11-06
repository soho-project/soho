package work.soho.pay.api.service;

import work.soho.pay.api.dto.OrderDetailsDto;

import java.util.Map;

public interface PayOrderApiService {
    Map<String, String> payOrder(OrderDetailsDto orderDetailsDto);
}

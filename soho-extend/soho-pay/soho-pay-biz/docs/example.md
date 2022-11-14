支付用例
=======

    //支付用例
    OrderDetailsDto orderDetailsDto = new OrderDetailsDto();
        orderDetailsDto.setOrderNo(IDGeneratorUtils.snowflake().toString());
        orderDetailsDto.setAmount(new BigDecimal("0.01"));
        orderDetailsDto.setDescription("支付测试单");
        orderDetailsDto.setNotifyUrl("http://www.baidu.com/");
        orderDetailsDto.setOutTradeNo(IDGeneratorUtils.snowflake().toString());
        orderDetailsDto.setPayInfoId(2); //后台配置支付信息ID

        Map<String, String> result = payOrderService.pay(orderDetailsDto);
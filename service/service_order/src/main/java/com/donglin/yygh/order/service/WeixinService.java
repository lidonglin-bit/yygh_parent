package com.donglin.yygh.order.service;


import java.util.Map;

public interface WeixinService {
    String createNative(Long orderId);

    Map<String, String> queryPayStatus(Long orderId);

    void paySuccess(Long orderId, Map<String, String> map);

    boolean refund(Long orderId);

}
package com.donglin.yygh.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.donglin.yygh.model.order.OrderInfo;
import com.donglin.yygh.model.order.PaymentInfo;

public interface PaymentService extends IService<PaymentInfo> {

    /**
     * 保存交易记录
     * @param order
     * @param paymentType 支付类型（1：微信 2：支付宝）
     */
    void savePaymentInfo(OrderInfo order, Integer paymentType);


}
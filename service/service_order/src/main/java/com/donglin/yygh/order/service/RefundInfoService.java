package com.donglin.yygh.order.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.donglin.yygh.model.order.PaymentInfo;
import com.donglin.yygh.model.order.RefundInfo;

//RefundInfoService
public interface RefundInfoService extends IService<RefundInfo> {
    RefundInfo saveRefundInfo(PaymentInfo paymentInfo);
}
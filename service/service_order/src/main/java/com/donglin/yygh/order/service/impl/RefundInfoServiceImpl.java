package com.donglin.yygh.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.donglin.yygh.enums.PaymentTypeEnum;
import com.donglin.yygh.enums.RefundStatusEnum;
import com.donglin.yygh.model.order.PaymentInfo;
import com.donglin.yygh.model.order.RefundInfo;
import com.donglin.yygh.order.mapper.RefundInfoMapper;
import com.donglin.yygh.order.service.RefundInfoService;
import org.springframework.stereotype.Service;


@Service
public class RefundInfoServiceImpl  extends ServiceImpl<RefundInfoMapper, RefundInfo> implements RefundInfoService {

    @Override
    public RefundInfo saveRefundInfo(PaymentInfo paymentInfo) {
        Long orderId = paymentInfo.getOrderId();
        QueryWrapper<RefundInfo> queryWrapper=new QueryWrapper<RefundInfo>();
        queryWrapper.eq("order_id",orderId);
        RefundInfo refundInfo1 = baseMapper.selectOne(queryWrapper);
        if(refundInfo1 != null){
            return refundInfo1;
        }

        RefundInfo refundInfo=new RefundInfo();
        refundInfo.setOutTradeNo(paymentInfo.getOutTradeNo());
        refundInfo.setOrderId(paymentInfo.getOrderId());
        refundInfo.setPaymentType(PaymentTypeEnum.WEIXIN.getStatus());
        refundInfo.setTotalAmount(paymentInfo.getTotalAmount());
        refundInfo.setSubject("想退款...");
        refundInfo.setRefundStatus(RefundStatusEnum.UNREFUND.getStatus());
        baseMapper.insert(refundInfo);
        return refundInfo;
    }
}

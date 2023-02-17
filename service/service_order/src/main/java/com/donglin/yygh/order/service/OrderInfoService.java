package com.donglin.yygh.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.donglin.yygh.model.order.OrderInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.donglin.yygh.vo.order.OrderCountQueryVo;
import com.donglin.yygh.vo.order.OrderQueryVo;

import java.util.Map;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author donglin
 * @since 2023-02-14
 */
public interface OrderInfoService extends IService<OrderInfo> {

    Long submitOrder(String scheduleId, Long patientId);

    Page<OrderInfo> getOrderInfoPage(Integer pageNum, Integer pageSize, OrderQueryVo orderQueryVo);

    OrderInfo detail(Long orderId);

    void cancelOrder(Long orderId);

    void patientTips();

    Map<String, Object> getCountMap(OrderCountQueryVo orderCountQueryVo);
}

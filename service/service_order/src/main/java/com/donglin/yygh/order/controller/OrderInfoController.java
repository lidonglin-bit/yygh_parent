package com.donglin.yygh.order.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.donglin.yygh.common.result.R;
import com.donglin.yygh.common.utils.JwtHelper;
import com.donglin.yygh.enums.OrderStatusEnum;
import com.donglin.yygh.model.order.OrderInfo;
import com.donglin.yygh.order.service.OrderInfoService;
import com.donglin.yygh.vo.order.OrderCountQueryVo;
import com.donglin.yygh.vo.order.OrderQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author donglin
 * @since 2023-02-14
 */
//创建controller方法
@Api(tags = "订单接口")
@RestController
@RequestMapping("/api/order/orderInfo")
public class OrderInfoController {

    @Autowired
    private OrderInfoService orderInfoService;

    @ApiOperation(value = "获取订单统计数据")
    @PostMapping("inner/getCountMap")
    public Map<String, Object> getCountMap(@RequestBody OrderCountQueryVo orderCountQueryVo) {
        return orderInfoService.getCountMap(orderCountQueryVo);
    }

    @GetMapping("/cancel/{orderId}")
    public R cancelOrder(@PathVariable Long orderId){
        orderInfoService.cancelOrder(orderId);
        return R.ok();
    }

    @GetMapping("/{orderId}")
    public R detail(@PathVariable Long orderId){
        OrderInfo orderInfo = orderInfoService.detail(orderId);
        return R.ok().data("orderInfo",orderInfo);
    }

    @GetMapping("/list")
    public R getOrderList(){
        List<Map<String, Object>> statusList = OrderStatusEnum.getStatusList();
        return R.ok().data("list",statusList);
    }


    @GetMapping("/{pageNum}/{pageSize}")
    public R getOrderInfoPage(@PathVariable Integer pageNum,
                              @PathVariable Integer pageSize,
                              OrderQueryVo orderQueryVo,
                              @RequestHeader String token){

        Long userId = JwtHelper.getUserId(token);
        orderQueryVo.setUserId(userId);
        Page<OrderInfo>  page= orderInfoService.getOrderInfoPage(pageNum,pageSize,orderQueryVo);
        return R.ok().data("page",page);
    }

    @ApiOperation(value = "创建订单")
    @PostMapping("/{scheduleId}/{patientId}")
    public R submitOrder(
            @ApiParam(name = "scheduleId", value = "排班id", required = true)
            @PathVariable String scheduleId,
            @ApiParam(name = "patientId", value = "就诊人id", required = true)
            @PathVariable Long patientId) {

        Long orderId = orderInfoService.submitOrder(scheduleId, patientId);
        return R.ok().data("orderId",orderId);
    }

}


package com.donglin.yygh.order.mapper;

import com.donglin.yygh.model.order.OrderInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.donglin.yygh.vo.order.OrderCountQueryVo;
import com.donglin.yygh.vo.order.OrderCountVo;

import java.util.List;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author donglin
 * @since 2023-02-14
 */
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    //统计每天平台预约数据
    List<OrderCountVo> selectOrderCount(OrderCountQueryVo orderCountQueryVo);

}

package com.donglin.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.donglin.yygh.common.exception.YyghException;
import com.donglin.yygh.enums.OrderStatusEnum;
import com.donglin.yygh.enums.PaymentStatusEnum;
import com.donglin.yygh.hosp.client.HospitalFeignClient;
import com.donglin.yygh.model.order.OrderInfo;
import com.donglin.yygh.model.order.PaymentInfo;
import com.donglin.yygh.model.user.Patient;
import com.donglin.yygh.mq.MqConst;
import com.donglin.yygh.mq.RabbitService;
import com.donglin.yygh.order.mapper.OrderInfoMapper;
import com.donglin.yygh.order.service.OrderInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.donglin.yygh.order.service.PaymentService;
import com.donglin.yygh.order.service.WeixinService;
import com.donglin.yygh.order.utils.HttpRequestHelper;
import com.donglin.yygh.user.client.PatientFeignClient;
import com.donglin.yygh.vo.hosp.ScheduleOrderVo;

import com.donglin.yygh.vo.order.OrderCountQueryVo;
import com.donglin.yygh.vo.order.OrderCountVo;
import com.donglin.yygh.vo.order.OrderMqVo;
import com.donglin.yygh.vo.order.OrderQueryVo;
import com.donglin.yygh.vo.sms.SmsVo;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author donglin
 * @since 2023-02-14
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {


    @Autowired
    private HospitalFeignClient hospitalFeignClient;
    @Autowired
    private PatientFeignClient patientFeignClient;
    @Autowired
    private RabbitService rabbitService;
    @Autowired
    private WeixinService weixinService;
    @Autowired
    private PaymentService paymentService;

    //生成订单
    @Override
    public Long submitOrder(String scheduleId, Long patientId) {
        //1 根据scheduleId获取排班数据
        ScheduleOrderVo scheduleOrderVo = hospitalFeignClient.getScheduleOrderVo(scheduleId);

        //2 根据patientId获取就诊人信息
        Patient patient = patientFeignClient.getPatientOrder(patientId);

        //3 平台里面 ==> 调用医院订单确认接口，
        // 3.1 如果医院返回失败，挂号失败
        //使用map集合封装需要传过医院数据
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("hoscode",scheduleOrderVo.getHoscode());
        paramMap.put("depcode",scheduleOrderVo.getDepcode());
        paramMap.put("hosScheduleId",scheduleOrderVo.getHosScheduleId());
        paramMap.put("reserveDate",new DateTime(scheduleOrderVo.getReserveDate()).toString("yyyy-MM-dd"));
        paramMap.put("reserveTime", scheduleOrderVo.getReserveTime());
        paramMap.put("amount",scheduleOrderVo.getAmount()); //挂号费用
        paramMap.put("name", patient.getName());
        paramMap.put("certificatesType",patient.getCertificatesType());
        paramMap.put("certificatesNo", patient.getCertificatesNo());
        paramMap.put("sex",patient.getSex());
        paramMap.put("birthdate", patient.getBirthdate());
        paramMap.put("phone",patient.getPhone());
        paramMap.put("isMarry", patient.getIsMarry());
        paramMap.put("provinceCode",patient.getProvinceCode());
        paramMap.put("cityCode", patient.getCityCode());
        paramMap.put("districtCode",patient.getDistrictCode());
        paramMap.put("address",patient.getAddress());
        //联系人
        paramMap.put("contactsName",patient.getContactsName());
        paramMap.put("contactsCertificatesType", patient.getContactsCertificatesType());
        paramMap.put("contactsCertificatesNo",patient.getContactsCertificatesNo());
        paramMap.put("contactsPhone",patient.getContactsPhone());
        paramMap.put("timestamp", HttpRequestHelper.getTimestamp());
        //String sign = HttpRequestHelper.getSign(paramMap, signInfoVo.getSignKey());
        paramMap.put("sign", "");

        //使用httpclient发送请求，请求医院接口
        JSONObject result =
                HttpRequestHelper.sendRequest(paramMap, "http://localhost:9998/order/submitOrder");
        //根据医院接口返回状态码判断  200 成功
        if(result.getInteger("code") == 200) { //挂号成功
            // 3.2 如果返回成功，得到返回其他数据
            JSONObject jsonObject = result.getJSONObject("data");
            //预约记录唯一标识（医院预约记录主键）
            String hosRecordId = jsonObject.getString("hosRecordId");
            //预约序号
            Integer number = jsonObject.getInteger("number");;
            //取号时间
            String fetchTime = jsonObject.getString("fetchTime");;
            //取号地址
            String fetchAddress = jsonObject.getString("fetchAddress");;

            //4 如果医院接口返回成功，添加上面三部分数据到数据库
            OrderInfo orderInfo = new OrderInfo();
            //设置添加数据--排班数据
            BeanUtils.copyProperties(scheduleOrderVo, orderInfo);
            //设置添加数据--就诊人数据
            //订单号
            String outTradeNo = System.currentTimeMillis() + ""+ new Random().nextInt(100);
            orderInfo.setOutTradeNo(outTradeNo);
            orderInfo.setScheduleId(scheduleOrderVo.getHosScheduleId());
            orderInfo.setUserId(patient.getUserId());
            orderInfo.setPatientId(patientId);
            orderInfo.setPatientName(patient.getName());
            orderInfo.setPatientPhone(patient.getPhone());
            orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());

            //设置添加数据--医院接口返回数据
            orderInfo.setHosRecordId(hosRecordId);
            orderInfo.setNumber(number);
            orderInfo.setFetchTime(fetchTime);
            orderInfo.setFetchAddress(fetchAddress);

            //调用方法添加
            baseMapper.insert(orderInfo);

            //TODO 5 根据医院返回数据，更新排班数量
            //排班可预约数
            Integer reservedNumber = jsonObject.getInteger("reservedNumber");
            //排班剩余预约数
            Integer availableNumber = jsonObject.getInteger("availableNumber");
            //发送mq信息更新号源和短信通知
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(scheduleId);
            orderMqVo.setReservedNumber(reservedNumber);
            orderMqVo.setAvailableNumber(availableNumber);

            //TODO 6 给就诊人发送短信
            //短信提示
            SmsVo msmVo = new SmsVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            String reserveDate =
                    new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd")
                            + (orderInfo.getReserveTime()==0 ? "上午": "下午");
            Map<String,Object> param = new HashMap<String,Object>(){{
                put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                put("amount", orderInfo.getAmount());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
                put("quitTime", new DateTime(orderInfo.getQuitTime()).toString("yyyy-MM-dd HH:mm"));
            }};
            msmVo.setParam(param);

            orderMqVo.setSmsVo(msmVo);
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);
            //返回订单号
            return orderInfo.getId();
        } else {//下单失败
            System.out.println("下单失败");
            throw new YyghException(20001,"下单失败");
        }
    }

    @Override
    public Page<OrderInfo> getOrderInfoPage(Integer pageNum, Integer pageSize, OrderQueryVo orderQueryVo) {
        Page page=new Page(pageNum,pageSize);
        QueryWrapper<OrderInfo> queryWrapper=new QueryWrapper<OrderInfo>();

        Long userId = orderQueryVo.getUserId(); //用户id
        String outTradeNo = orderQueryVo.getOutTradeNo();//订单号
        String keyword = orderQueryVo.getKeyword();//医院名称
        Long patientId = orderQueryVo.getPatientId(); //就诊人id
        String orderStatus = orderQueryVo.getOrderStatus();//订单状态
        String reserveDate = orderQueryVo.getReserveDate(); //预约日期
        String createTimeBegin = orderQueryVo.getCreateTimeBegin();//下订单时间
        String createTimeEnd = orderQueryVo.getCreateTimeEnd();//下订单时间

        if(!StringUtils.isEmpty(userId)){
            queryWrapper.eq("user_id", userId);
        }
        if(!StringUtils.isEmpty(outTradeNo)){
            queryWrapper.eq("out_trade_no", outTradeNo);
        }
        if(!StringUtils.isEmpty(keyword)){
            queryWrapper.like("hosname", keyword);
        }
        if(!StringUtils.isEmpty(patientId)){
            queryWrapper.eq("patient_id", patientId);
        }
        if(!StringUtils.isEmpty(orderStatus)){
            queryWrapper.eq("order_status", orderStatus);
        }
        if(!StringUtils.isEmpty(reserveDate)){
            queryWrapper.ge("reserve_date", reserveDate);
        }
        if(!StringUtils.isEmpty(createTimeBegin)){
            queryWrapper.ge("create_time", createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)){
            queryWrapper.le("create_time", createTimeEnd);
        }
        Page<OrderInfo> page1 = baseMapper.selectPage(page, queryWrapper);
        page1.getRecords().parallelStream().forEach(item->{
            this.packageOrderInfo(item);
        });

        return page1;
    }

    @Override
    public OrderInfo detail(Long orderId) {
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        this.packageOrderInfo(orderInfo);
        return orderInfo;
    }

    @Override
    public void cancelOrder(Long orderId) {
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        DateTime quitTime = new DateTime(orderInfo.getQuitTime());
        //1.确定当前取消预约的时间 和 挂号订单的取消预约截止时间 对比, 当前时间是否已经超过了 挂号订单的取消预约截止时间
        //1.1 如果超过了，直接抛出异常，不让用户取消
        if(quitTime.isBeforeNow()){
            throw  new YyghException(20001,"超过了退号的截止时间");
        }

        Map<String,Object>  hospitalParamMap=new HashMap<String,Object>();
        hospitalParamMap.put("hoscode",orderInfo.getHoscode());
        hospitalParamMap.put("hosRecordId",orderInfo.getHosRecordId());


        //2.从平台请求第三方医院，通知第三方医院，该用户已取消
        JSONObject jsonObject = HttpRequestHelper.sendRequest(hospitalParamMap, "http://localhost:9998/order/updateCancelStatus");
        //2.1 第三方医院如果不同意取消：抛出异常，不能取消
        if(jsonObject == null || jsonObject.getIntValue("code") != 200){
            throw  new YyghException(20001,"取消失败");
        }
        //3.判断用户是否对当前挂号订单是否已支付
        if(orderInfo.getOrderStatus() == OrderStatusEnum.PAID.getStatus()){
            //3.1.如果已支付，退款
            boolean flag= weixinService.refund(orderId);
            if(!flag){
                throw new YyghException(20001,"退款失败");
            }
        }

        //无论用户是否进了支付

        //4.更新订单的订单状态 及 支付记录表的支付状态
        orderInfo.setOrderStatus(OrderStatusEnum.CANCLE.getStatus());
        baseMapper.updateById(orderInfo);

        UpdateWrapper<PaymentInfo> updateWrapper=new UpdateWrapper<PaymentInfo>();
        updateWrapper.eq("order_id",orderInfo.getId());
        updateWrapper.set("payment_status", PaymentStatusEnum.REFUND.getStatus());
        paymentService.update(updateWrapper);

        //5.更新医生的剩余可预约数信息

        OrderMqVo orderMqVo=new OrderMqVo();
        orderMqVo.setScheduleId(orderInfo.getScheduleId());
        SmsVo msmVo=new SmsVo();
        msmVo.setPhone(orderInfo.getPatientPhone());
        msmVo.setTemplateCode("xxxx.....");
        msmVo.setParam(null);
        orderMqVo.setSmsVo(msmVo);
        //6.给就诊人发送短信提示：
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER,MqConst.ROUTING_ORDER,orderMqVo);
    }

    @Override
    public void patientTips() {
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("reserve_date",new DateTime().toString("yyyy-MM-dd"));
        //这里应该加个条件，订单状态不为-1的订单，-1表示已退号
        queryWrapper.ne("order_status",-1);
        List<OrderInfo> orderInfoList = baseMapper.selectList(queryWrapper);
        for(OrderInfo orderInfo : orderInfoList) {
            //短信提示
            SmsVo smsVo = new SmsVo();
            smsVo.setPhone(orderInfo.getPatientPhone());
            String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime()==0 ? "上午": "下午");
            Map<String,Object> param = new HashMap<String,Object>(){{
                put("title", orderInfo.getHosname()+"|"+orderInfo.getDepname()+"|"+orderInfo.getTitle());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
            }};
            smsVo.setParam(param);
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_SMS, MqConst.ROUTING_SMS_ITEM, smsVo);
        }
    }

    @Override
    public Map<String, Object> getCountMap(OrderCountQueryVo orderCountQueryVo) {
        Map<String, Object> map = new HashMap<>();

        List<OrderCountVo> orderCountVoList
                = baseMapper.selectOrderCount(orderCountQueryVo);
        //日期列表
        List<String> dateList
                =orderCountVoList.stream().map(OrderCountVo::getReserveDate).collect(Collectors.toList());
        //统计列表
        List<Integer> countList
                =orderCountVoList.stream().map(OrderCountVo::getCount).collect(Collectors.toList());
        map.put("dateList", dateList);
        map.put("countList", countList);
        return map;
    }

    private void packageOrderInfo(OrderInfo item) {
        item.getParam().put("orderStatusString",OrderStatusEnum.getStatusNameByStatus(item.getOrderStatus()));
    }

}

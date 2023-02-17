package com.donglin.yygh.hosp.listener;

import com.donglin.yygh.hosp.service.ScheduleService;
import com.donglin.yygh.mq.MqConst;
import com.donglin.yygh.mq.RabbitService;
import com.donglin.yygh.vo.order.OrderMqVo;
import com.donglin.yygh.vo.sms.SmsVo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderMqListener {

    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private RabbitService rabbitService;

    @RabbitListener(bindings = {
            @QueueBinding(
                    value =@Queue(name = MqConst.QUEUE_ORDER,durable = "true"),//创建队列
                    exchange = @Exchange(name = MqConst.EXCHANGE_DIRECT_ORDER), //创建交换机
                    key=MqConst.ROUTING_ORDER
            )
    })
    //确认挂号：走该方法 -n
    //取消预约：走方法 : +1
    public void consume(OrderMqVo orderMqVo, Message message, Channel channel){
        String scheduleId = orderMqVo.getScheduleId();
        Integer availableNumber = orderMqVo.getAvailableNumber();
        SmsVo msmVo = orderMqVo.getSmsVo();
        if(availableNumber != null){
            boolean flag= scheduleService.updateAvailableNumber(scheduleId,availableNumber);

        }else{
            scheduleService.cancelSchedule(scheduleId);
        }

        if(msmVo != null){
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_SMS,MqConst.ROUTING_SMS_ITEM,msmVo);
        }


    }
}

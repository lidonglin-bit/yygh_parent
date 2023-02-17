package com.donglin.yygh.task.job;

import com.donglin.yygh.mq.MqConst;
import com.donglin.yygh.mq.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@EnableScheduling
public class PatientRemindJob {
     //Quartz:
    //Quartz:cron表达式:   秒  分  时  dayofMonth Month dayOfWeek  Year[最高到2099年]
    //*:表示任意xxx
    //?:表示无所谓
    //-:连续的时间段
    // /n:表示每隔多长时间
    // ,：可以使用,隔开没有规律的时间

    @Autowired
    private RabbitService rabbitService;

    @Scheduled(cron="*/30 * * * * *")
    public void printTime(){
       // System.out.println(new DateTime().toString("yyyy-MM-dd HH:mm:ss"));
        System.out.println(new Date().toLocaleString());
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK,MqConst.ROUTING_TASK_8," ");
    }

    //在springboot定时任务使用：1.在类上加@EnableScheduling 2.在定时任务Job的方法上加@Scheduled并指定石英表达式
    //cron表达式写法：七域表达式
}

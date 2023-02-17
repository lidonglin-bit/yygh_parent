package com.donglin.yygh.hosp.controller.user;

import com.donglin.yygh.common.result.R;
import com.donglin.yygh.hosp.service.ScheduleService;
import com.donglin.yygh.model.hosp.Schedule;
import com.donglin.yygh.vo.hosp.ScheduleOrderVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/hosp/schedule")
public class UserScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @ApiOperation(value = "获取排班详情")
    @GetMapping("getSchedule/{id}")
    public R getScheduleList(
            @PathVariable String id) {
        Schedule schedule = scheduleService.getScheduleList(id);
        return R.ok().data("schedule",schedule);
    }

    @GetMapping("/{hoscode}/{depcode}/{pageNum}/{pageSize}")
    public R getSchedulePage(@PathVariable String hoscode,
                             @PathVariable String depcode,
                             @PathVariable Integer pageNum,
                             @PathVariable Integer pageSize){

        Map<String,Object> map=scheduleService.getSchedulePageByCondition(hoscode,depcode,pageNum,pageSize);
        return R.ok().data(map);
    }

    @GetMapping("/{hoscode}/{depcode}/{workdate}")
    public R getScheduleDetail(@PathVariable String hoscode,
                               @PathVariable String depcode,
                               @PathVariable String workdate){

        List<Schedule> details = scheduleService.detail(hoscode, depcode, workdate);
        return R.ok().data("details",details);
    }

    @ApiOperation(value = "根据排班id获取预约下单数据")
    @GetMapping("/{scheduleId}")
    public ScheduleOrderVo getScheduleOrderVo(
            @ApiParam(name = "scheduleId", value = "排班id", required = true)
            @PathVariable("scheduleId") String scheduleId) {
        return scheduleService.getScheduleOrderVo(scheduleId);
    }
}

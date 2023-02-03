package com.donglin.yygh.hosp.controller.admin;

import com.donglin.yygh.common.result.R;
import com.donglin.yygh.hosp.service.ScheduleService;
import com.donglin.yygh.model.hosp.Schedule;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/admin/hosp/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;


    //根据医院编号 、科室编号和工作日期，查询排班详细信息
    @ApiOperation(value = "查询排班详细信息")
    @GetMapping("/{hoscode}/{depcode}/{workdate}")
    public R detail( @PathVariable String hoscode,
                     @PathVariable String depcode,
                     @PathVariable String workdate){

        List<Schedule> scheduleList= scheduleService.detail(hoscode,depcode,workdate);
        return R.ok().data("list",scheduleList);
    }

    //根据医院编号 和 科室编号 ，查询排班规则数据
    @ApiOperation(value ="查询排班规则数据")
    @GetMapping("/{pageNum}/{pageSize}/{hoscode}/{depcode}")
    public R page(@PathVariable Integer pageNum,
                  @PathVariable Integer pageSize,
                  @PathVariable String hoscode,
                  @PathVariable String depcode){

        Map<String,Object> map=scheduleService.page(pageNum,pageSize,hoscode,depcode);
        return R.ok().data(map);
    }

}

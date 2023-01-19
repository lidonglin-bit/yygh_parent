package com.donglin.yygh.hosp.controller.api;

import com.donglin.yygh.hosp.bean.Result;
import com.donglin.yygh.hosp.service.ScheduleService;
import com.donglin.yygh.hosp.utils.HttpRequestHelper;
import com.donglin.yygh.model.hosp.Schedule;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp")
public class ApiScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @ApiOperation(value = "上传排班")
    @PostMapping("saveSchedule")
    public Result saveSchedule(HttpServletRequest request){
        Map<String, Object> map = HttpRequestHelper.switchMap(request.getParameterMap());
        //验证signKey  略
        scheduleService.saveSchedule(map);
        return Result.ok();
    }

    @ApiOperation(value = "获取排班分页列表")
    @PostMapping("/schedule/list")
    public Result getSchedulePage(HttpServletRequest request){
        Map<String, Object> map = HttpRequestHelper.switchMap(request.getParameterMap());
        //验证signKey  略
        Page<Schedule> schedulePage = scheduleService.getSchedulePage(map);
        return Result.ok(schedulePage);
    }

    @ApiOperation(value = "删除排班")
    @PostMapping("schedule/remove")
    public Result remove(HttpServletRequest httpServletRequest){
        Map<String, Object> map = HttpRequestHelper.switchMap(httpServletRequest.getParameterMap());
        //验证signKey  略
        scheduleService.remove(map);
        return Result.ok();
    }


}

package com.donglin.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.donglin.yygh.hosp.repository.ScheduleRepository;
import com.donglin.yygh.hosp.service.ScheduleService;
import com.donglin.yygh.model.hosp.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.json.Json;

import java.util.Date;
import java.util.Map;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Override
    public void saveSchedule(Map<String, Object> map) {
        Schedule schedule = JSONObject.parseObject(JSONObject.toJSONString(map), Schedule.class);
        String hoscode = schedule.getHoscode();
        String depcode = schedule.getDepcode();
        String hosScheduleId = schedule.getHosScheduleId();
        Schedule platformSchedule = scheduleRepository.findByHoscodeAndDepcodeAndHosScheduleId(hoscode,depcode,hosScheduleId);

        if (platformSchedule == null){
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(new Date());
            schedule.setIsDeleted(0);
            scheduleRepository.save(schedule);
        }else {
            schedule.setCreateTime(new Date());
            schedule.setUpdateTime(platformSchedule.getUpdateTime());
            schedule.setIsDeleted(platformSchedule.getIsDeleted());
            schedule.setId(platformSchedule.getId());
            scheduleRepository.save(schedule);
        }
    }

    @Override
    public Page<Schedule> getSchedulePage(Map<String, Object> map) {
        Integer page = Integer.parseInt((String) map.get("page"));
        Integer limit = Integer.parseInt((String) map.get("limit"));
        //0为第一页
        Pageable pageable = PageRequest.of(page-1, limit, Sort.by("createTime").ascending());
        String hoscode = (String)map.get("hoscode");
        Schedule schedule = new Schedule();
        schedule.setHoscode(hoscode);
        Example<Schedule> scheduleExample = Example.of(schedule);
        Page<Schedule> pages = scheduleRepository.findAll(scheduleExample, pageable);
        return pages;
    }

    @Override
    public void remove(Map<String, Object> map) {
        String hoscode = (String)map.get("hoscode");
        String hosScheduleId = (String)map.get("hosScheduleId");
        Schedule schedule = scheduleRepository.findByHoscodeAndHosScheduleId(hoscode,hosScheduleId);
        if (schedule!=null){
            scheduleRepository.deleteById(schedule.getId());
        }
    }
}

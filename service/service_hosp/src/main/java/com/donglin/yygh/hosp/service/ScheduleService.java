package com.donglin.yygh.hosp.service;

import com.donglin.yygh.model.hosp.Schedule;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ScheduleService {
    void saveSchedule(Map<String, Object> map);

    Page<Schedule> getSchedulePage(Map<String, Object> map);

    void remove(Map<String, Object> map);

    List<Schedule> detail(String hoscode, String depcode, String workdate);

    Map<String, Object> page(Integer pageNum, Integer pageSize, String hoscode, String depcode);
}

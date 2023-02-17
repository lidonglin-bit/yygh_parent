package com.donglin.yygh.hosp.service;

import com.donglin.yygh.model.hosp.Schedule;
import com.donglin.yygh.vo.hosp.ScheduleOrderVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ScheduleService {
    void saveSchedule(Map<String, Object> map);

    Page<Schedule> getSchedulePage(Map<String, Object> map);

    void remove(Map<String, Object> map);

    List<Schedule> detail(String hoscode, String depcode, String workdate);

    Map<String, Object> page(Integer pageNum, Integer pageSize, String hoscode, String depcode);

    Map<String, Object> getSchedulePageByCondition(String hoscode, String depcode, Integer pageNum, Integer pageSize);


    Schedule getScheduleList(String id);

    ScheduleOrderVo getScheduleOrderVo(String scheduleId);

    void update(Schedule schedule);

    boolean updateAvailableNumber(String scheduleId, Integer availableNumber);

    void cancelSchedule(String scheduleId);
}

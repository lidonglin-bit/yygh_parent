package com.donglin.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.donglin.yygh.hosp.repository.ScheduleRepository;
import com.donglin.yygh.hosp.service.DepartmentService;
import com.donglin.yygh.hosp.service.HospitalService;
import com.donglin.yygh.hosp.service.ScheduleService;
import com.donglin.yygh.model.hosp.Hospital;
import com.donglin.yygh.model.hosp.Schedule;
import com.donglin.yygh.vo.hosp.BookingScheduleRuleVo;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.json.Json;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ScheduleServiceImpl implements ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private HospitalService hospitalService;
    @Autowired
    private DepartmentService departmentService;

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

    @Override
    public List<Schedule> detail(String hoscode, String depcode, String workdate) {
        Date date = new DateTime(workdate).toDate();
        List<Schedule> scheduleList =scheduleRepository.findByHoscodeAndDepcodeAndWorkDate(hoscode,depcode,date);

        //把得到list集合遍历，向设置其他值：医院名称、科室名称、日期对应星期
        scheduleList.stream().forEach(item->{
            this.packageSchedule(item);
        });

        return scheduleList;
    }

    private void packageSchedule(Schedule schedule) {
        //设置医院名称
        schedule.getParam().put("hosname",hospitalService.getHospitalByHoscode(schedule.getHoscode()).getHosname());
        //设置科室名称
        schedule.getParam().put("depname",departmentService.getDepName(schedule.getHoscode(),schedule.getDepcode()));
        //设置日期对应星期
        schedule.getParam().put("dayOfWeek",this.getDayOfWeek(new DateTime(schedule.getWorkDate())));
    }



    @Override
    public Map<String, Object> page(Integer pageNum, Integer pageSize, String hoscode, String depcode) {
        Criteria criteria = Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode);

        //聚合:最好使用mongoTemplate
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate")
                        .first("workDate").as("workDate")
                        .count().as("docCount")
                        .sum("reservedNumber").as("reservedNumber")
                        .sum("availableNumber").as("availableNumber"),
                Aggregation.sort(Sort.Direction.ASC, "workDate"),
                Aggregation.skip((pageNum - 1) * pageSize),
                Aggregation.limit(pageSize)

        );
        /*=============================================
              第一个参数Aggregation：表示聚合条件
              第二个参数InputType： 表示输入类型，可以根据当前指定的字节码找到mongo对应集合
              第三个参数OutputType： 表示输出类型，封装聚合后的信息
          ============================================*/
        AggregationResults<BookingScheduleRuleVo> aggregate = mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        //当前页对应的列表数据
        List<BookingScheduleRuleVo> mappedResults = aggregate.getMappedResults();
        for (BookingScheduleRuleVo bookingScheduleRuleVo : mappedResults) {
            Date workDate = bookingScheduleRuleVo.getWorkDate();
            //工具类：美年旅游：周几
            String dayOfWeek = this.getDayOfWeek(new DateTime(workDate));
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        }

        Aggregation aggregation2 = Aggregation.newAggregation(
                Aggregation.match(criteria),
                Aggregation.group("workDate"));
        /*=============================================
              第一个参数Aggregation：表示聚合条件
              第二个参数InputType： 表示输入类型，可以根据当前指定的字节码找到mongo对应集合
              第三个参数OutputType： 表示输出类型，封装聚合后的信息
          ============================================*/
        AggregationResults<BookingScheduleRuleVo> aggregate2 = mongoTemplate.aggregate(aggregation2, Schedule.class, BookingScheduleRuleVo.class);

        Map<String, Object> map=new HashMap<String,Object>();
        map.put("list",mappedResults);
        map.put("total",aggregate2.getMappedResults().size());

        //获取医院名称
        Hospital hospital = hospitalService.getHospitalByHoscode(hoscode);
        //其他基础数据
        Map<String, String> baseMap = new HashMap<>();
        baseMap.put("hosname",hospital.getHosname());

        map.put("baseMap",baseMap);

        return map;
    }



    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }
}

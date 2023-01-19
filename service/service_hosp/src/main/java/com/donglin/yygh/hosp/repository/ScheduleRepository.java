package com.donglin.yygh.hosp.repository;

import com.donglin.yygh.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends MongoRepository<Schedule,String> {
    Schedule findByHoscodeAndDepcodeAndHosScheduleId(String hoscode, String depcode, String hosScheduleId);

    Schedule findByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);
}

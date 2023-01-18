package com.donglin.yygh.hosp.repository;

import com.donglin.yygh.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HospitalRepository extends MongoRepository<Hospital,String> {
    Hospital findByHoscode(String hoscode);
}

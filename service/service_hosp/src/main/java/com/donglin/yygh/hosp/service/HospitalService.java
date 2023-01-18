package com.donglin.yygh.hosp.service;

import com.donglin.yygh.model.hosp.Hospital;

import java.util.Map;

public interface HospitalService {
    void saveHospital(Map<String, Object> resultMap);

    String getSignKeyWithHoscode(String requestHoscode);

    Hospital getByHoscode(String hoscode);
}

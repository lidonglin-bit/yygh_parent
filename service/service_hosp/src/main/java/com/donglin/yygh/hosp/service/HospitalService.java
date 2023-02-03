package com.donglin.yygh.hosp.service;

import com.donglin.yygh.model.hosp.Hospital;
import com.donglin.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface HospitalService {
    void saveHospital(Map<String, Object> resultMap);

    String getSignKeyWithHoscode(String requestHoscode);

    Hospital getByHoscode(String hoscode);

    Page<Hospital> getHospitalPage(Integer pageNum, Integer pageSize, HospitalQueryVo hospitalQueryVo);

    void updateStatus(String id, Integer status);

    Hospital detail(String id);

    Hospital getHospitalByHoscode(String hoscode);

    List<Hospital> findByHosname(String name);

    Hospital getHospitalDetail(String hoscode);
}

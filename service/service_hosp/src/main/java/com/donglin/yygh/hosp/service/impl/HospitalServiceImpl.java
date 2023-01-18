package com.donglin.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.donglin.yygh.common.exception.YyghException;
import com.donglin.yygh.hosp.mapper.HospitalSetMapper;
import com.donglin.yygh.hosp.repository.HospitalRepository;
import com.donglin.yygh.hosp.service.HospitalService;
import com.donglin.yygh.model.hosp.Hospital;
import com.donglin.yygh.model.hosp.HospitalSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private HospitalSetMapper hospitalSetMapper;


    @Override
    public void saveHospital(Map<String, Object> resultMap) {
        String s = JSONObject.toJSONString(resultMap);
        Hospital hospital = JSONObject.parseObject(s, Hospital.class);
        //如果医院两次保存那么会保存两次，如果数据没有就保存，数据有就更新     save既可以保存，也可以更新
        String hoscode = hospital.getHoscode();
        Hospital collection = hospitalRepository.findByHoscode(hoscode);
        if (collection == null){   //平台上没有该医院信息做添加
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }else {  //平台上有医院信息做修改
            hospital.setStatus(collection.getStatus());
            hospital.setCreateTime(collection.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(collection.getIsDeleted());
            //要进行id进行修改
            hospital.setId(collection.getId());
            hospitalRepository.save(hospital);
        }

    }

    @Override
    public String getSignKeyWithHoscode(String requestHoscode) {
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        wrapper.eq("hoscode",requestHoscode);
        HospitalSet hospitalSet = hospitalSetMapper.selectOne(wrapper);
        if (hospitalSet == null){
            throw new YyghException(20001,"该医院信息不存在");
        }
        return hospitalSet.getSignKey();
    }

    @Override
    public Hospital getByHoscode(String hoscode) {
        Hospital hospital = hospitalRepository.findByHoscode(hoscode);
        return hospital;
    }
}

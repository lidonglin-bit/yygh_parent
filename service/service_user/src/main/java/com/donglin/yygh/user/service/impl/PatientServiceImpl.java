package com.donglin.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.donglin.yygh.client.DictFeignClient;
import com.donglin.yygh.model.user.Patient;
import com.donglin.yygh.user.mapper.PatientMapper;
import com.donglin.yygh.user.service.PatientService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.donglin.yygh.user.utils.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

/**
 * <p>
 * 就诊人表 服务实现类
 * </p>
 *
 * @author donglin
 * @since 2023-02-09
 */
@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {


    @Autowired
    private DictFeignClient dictFeignClient;

    @Override
    public List<Patient> findAll(@RequestHeader String token) {
        Long userId = JwtHelper.getUserId(token);
        QueryWrapper<Patient> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        List<Patient> patients = baseMapper.selectList(queryWrapper);
        patients.stream().forEach(item->{
            this.packagePatient(item);
        });
        return patients;
    }

    @Override
    public Patient getPatient(Long id) {
        Patient patients = baseMapper.selectById(id);
        this.packagePatient(patients);
        return patients;
    }

    @Override
    public List<Patient> selectList(QueryWrapper<Patient> queryWrapper) {
        List<Patient> patients = baseMapper.selectList(queryWrapper);
        patients.stream().forEach(item->{
            this.packagePatient(item);
        });
        return patients;
    }

    private void packagePatient(Patient item) {
        item.getParam().put("certificatesTypeString",dictFeignClient.getNameByValue(Long.parseLong(item.getCertificatesType())));
        String provinceString = dictFeignClient.getNameByValue(Long.parseLong(item.getProvinceCode()));
        String cityString = dictFeignClient.getNameByValue(Long.parseLong(item.getCityCode()));
        String districtString = dictFeignClient.getNameByValue(Long.parseLong(item.getDistrictCode()));
        item.getParam().put("provinceString",provinceString);
        item.getParam().put("cityString",cityString);
        item.getParam().put("districtString",districtString);
        item.getParam().put("fullAddress",provinceString+cityString+districtString);
    }
}

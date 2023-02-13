package com.donglin.yygh.user.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.donglin.yygh.model.user.Patient;

import java.util.List;

/**
 * <p>
 * 就诊人表 服务类
 * </p>
 *
 * @author donglin
 * @since 2023-02-09
 */
public interface PatientService extends IService<Patient> {


    List<Patient> findAll(String token);

    Patient getPatient(Long id);

    List<Patient> selectList(QueryWrapper<Patient> queryWrapper);
}

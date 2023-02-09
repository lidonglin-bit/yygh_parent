package com.donglin.yygh.user.service.impl;

import com.donglin.yygh.model.user.Patient;
import com.donglin.yygh.user.mapper.PatientMapper;
import com.donglin.yygh.user.service.PatientService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}

package com.donglin.yygh.user.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.donglin.yygh.common.result.R;
import com.donglin.yygh.model.user.Patient;
import com.donglin.yygh.user.service.PatientService;
import com.donglin.yygh.user.utils.AuthContextHolder;
import com.donglin.yygh.user.utils.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 就诊人表 前端控制器
 * </p>
 *
 * @author donglin
 * @since 2023-02-09
 */
@RestController
@RequestMapping("/user/userinfo/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    //获取就诊人列表
    @GetMapping("/all")
    public R findAll(@RequestHeader String token) {
        //获取当前登录用户id
        Long userId = JwtHelper.getUserId(token);
        QueryWrapper<Patient> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        List<Patient> list = patientService.list(queryWrapper);
        return R.ok().data("list",list);
    }
    //添加就诊人
    @PostMapping("/save")
    public R savePatient(@RequestBody Patient patient, @RequestHeader String token) {
        //获取当前登录用户id
        Long userId = JwtHelper.getUserId(token);
        patient.setUserId(userId);
        patientService.save(patient);
        return R.ok();
    }
    //根据id获取就诊人信息，修改就诊人信息时回显
    @GetMapping("detail/{id}")
    public R getPatient(@PathVariable Long id) {
        Patient patient = patientService.getById(id);
        return R.ok().data("patient",patient);
    }
    //修改就诊人
    @PostMapping("/update")
    public R updatePatient(@RequestBody Patient patient) {
        patientService.updateById(patient);
        return R.ok();
    }
    //删除就诊人
    @DeleteMapping("/remove/{id}")
    public R removePatient(@PathVariable Long id) {
        patientService.removeById(id);
        return R.ok();
    }
}


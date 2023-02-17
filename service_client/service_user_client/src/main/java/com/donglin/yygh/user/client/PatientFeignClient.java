package com.donglin.yygh.user.client;

import com.donglin.yygh.model.user.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-user")
@Repository
public interface PatientFeignClient {
    //获取就诊人
    @GetMapping("/user/userinfo/patient/{id}")
    public Patient getPatientOrder(@PathVariable("id") Long id);
}
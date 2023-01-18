package com.donglin.yygh.hosp.controller;

import com.donglin.yygh.common.exception.YyghException;
import com.donglin.yygh.common.utils.MD5;
import com.donglin.yygh.hosp.bean.Result;
import com.donglin.yygh.hosp.service.HospitalService;
import com.donglin.yygh.hosp.utils.HttpRequestHelper;
import com.donglin.yygh.model.hosp.Hospital;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;


    @PostMapping("/hospital/show")
    public Result hospitalShow(HttpServletRequest request){
        Map<String, String[]> map = request.getParameterMap();
        Map<String, Object> switchMap = HttpRequestHelper.switchMap(map);
        //必须参数校验
        String hoscode = (String) switchMap.get("hoscode");
        if (StringUtils.isEmpty(hoscode)){
            throw new YyghException(20001,"失败");
        }
        //签名验证  略
        Hospital hospital = hospitalService.getByHoscode(hoscode);
        return Result.ok(hospital);
    }

    @PostMapping("/saveHospital")
    public Result saveHospital(HttpServletRequest request){
        //1.获取所有的参数（发现一个键对应多个值，需要转换一个键对应一个值） 因为我们的数据都一个键对用一个值
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> resultMap = HttpRequestHelper.switchMap(parameterMap);
        String requestSignKey = (String) resultMap.get("sign");
        String requestHoscode = (String) resultMap.get("hoscode");
        String platformSignKey = hospitalService.getSignKeyWithHoscode(requestHoscode);
        String encrypt = MD5.encrypt(platformSignKey);
        if (!StringUtils.isEmpty(requestSignKey)&&!StringUtils.isEmpty(requestHoscode)&& encrypt.equals(requestSignKey)){
            String logoData = (String) resultMap.get("logoData");

            String result = logoData.replaceAll(" ", "+");
            resultMap.put("logoData",result);
            hospitalService.saveHospital(resultMap);
            return Result.ok();
        }else {
            throw new YyghException(20001,"保存失败");
        }
    }
}

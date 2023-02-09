package com.donglin.yygh.sms.controller;

import com.donglin.yygh.common.result.R;
import com.donglin.yygh.sms.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/sms")
public class SmsController {

    @Autowired
    private SmsService smsService;

    @GetMapping("/send/{phone}")
    public R sendCode(@PathVariable String phone){
        boolean flag=smsService.sendCode(phone);
        if(flag){
            return R.ok();
        }else{
            return R.error();
        }
    }
}

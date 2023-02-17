package com.donglin.yygh.sms.service;

import com.donglin.yygh.vo.sms.SmsVo;

import java.util.Map;

public interface SmsService {
    boolean sendCode(String phone);


    void sendMessage(SmsVo smsVo);
}

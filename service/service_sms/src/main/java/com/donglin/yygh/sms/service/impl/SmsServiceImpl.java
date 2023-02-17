package com.donglin.yygh.sms.service.impl;

import com.donglin.yygh.sms.service.SmsService;
import com.donglin.yygh.sms.uitls.HttpUtils;
import com.donglin.yygh.sms.uitls.RandomUtil;
import com.donglin.yygh.vo.sms.SmsVo;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class SmsServiceImpl implements SmsService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean sendCode(String phone) {
        String host = "http://dingxin.market.alicloudapi.com";
        String path = "/dx/sendSms";
        String method = "POST";
        String appcode = "f5e58dd4cd56493581466733a71b6c31";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", phone);
        String fourBitRandom = RandomUtil.getFourBitRandom();
        querys.put("param", "code:" + fourBitRandom);
        querys.put("tpl_id", "TP1711063");
        Map<String, String> bodys = new HashMap<String, String>();


        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            System.out.println(response.toString());
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));

            //把验证码保存redis中一份
            redisTemplate.opsForValue().set(phone, fourBitRandom, 10, TimeUnit.DAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void sendMessage(SmsVo smsVo) {
        if (!StringUtils.isEmpty(smsVo.getPhone())) {
            //给就诊人短信提醒
            System.out.println("给就诊人短信提醒");
            //仅为了测试
        }
    }

}

package com.donglin.yygh.user.controller.user;


import com.alibaba.fastjson.JSONObject;
import com.donglin.yygh.common.result.R;
import com.donglin.yygh.model.user.UserInfo;
import com.donglin.yygh.user.service.UserInfoService;
import com.donglin.yygh.user.prop.WeixinProperties;
import com.donglin.yygh.user.utils.HttpClientUtils;
import com.donglin.yygh.user.utils.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user/ucenter/wx")
public class WeixinController {

    @Autowired
    private WeixinProperties weixinProperties;

    @Autowired
    private UserInfoService userInfoService;

    @RequestMapping("/param")
    @ResponseBody
    public R getWeixinLoginParam() throws UnsupportedEncodingException {
        String redirectUri = URLEncoder.encode(weixinProperties.getRedirectUrl(), "UTF-8");
        HashMap<String, Object> map = new HashMap<>();
        map.put("appid",weixinProperties.getAppId());
        map.put("scope","snsapi_login");
        map.put("redirect_uri",redirectUri);
        map.put("state",System.currentTimeMillis()+"");
        return R.ok().data(map);
    }

    @GetMapping("/callback")
    public String callback(String code, String state) throws Exception {
        //第一步 获取临时票据 code
        System.out.println("code:"+code);
        //第二步 拿着code和微信id和秘钥，请求微信固定地址 ，得到两个值
        //使用code和appid以及appscrect换取access_token
        //  %s   占位符
        StringBuffer baseAccessTokenUrl = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");
        String accessTokenUrl = String.format(baseAccessTokenUrl.toString(),
                weixinProperties.getAppId(),
                weixinProperties.getAppSecret(),
                code);
        //使用httpclient请求这个地址
        String accesstokenInfo = HttpClientUtils.get(accessTokenUrl);
        //System.out.println("accesstokenInfo:"+accesstokenInfo);
        //从返回字符串获取两个值 openid  和  access_token
        JSONObject jsonObject   = JSONObject.parseObject(accesstokenInfo);
        String access_token = jsonObject.getString("access_token");
        String openid = jsonObject.getString("openid");
        //System.out.println(access_token+" "+openid);
        //-----到这里可以调试能不能打印access_token和openid---------

        //判断数据库是否存在微信的扫描人信息
        //根据openid判断
        UserInfo userInfo = userInfoService.selectWxInfoOpenId(openid);
        if (userInfo == null){
            //第三步 拿着openid  和  access_token请求微信地址，得到扫描人信息
            String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                    "?access_token=%s" +
                    "&openid=%s";
            String userInfoUrl = String.format(baseUserInfoUrl, access_token, openid);
            String resultInfo = HttpClientUtils.get(userInfoUrl);
            System.out.println("resultInfo:"+resultInfo);
            JSONObject resultUserInfoJson = JSONObject.parseObject(resultInfo);
            //解析用户信息
            //用户昵称
            String nickname = resultUserInfoJson.getString("nickname");
            //用户头像
            String headimgurl = resultUserInfoJson.getString("headimgurl");

            //获取扫描人信息添加数据库
            userInfo = new UserInfo();
            userInfo.setNickName(nickname);
            userInfo.setOpenid(openid);
            userInfo.setStatus(1);
            userInfoService.save(userInfo);
        }

        //返回name和token字符串
        Map<String,String> map = new HashMap<>();
        String name = userInfo.getName();
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        map.put("name", name);
        //使用jwt生成token字符串
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token", token);

        //判断userInfo是否有手机号，如果手机号为空，返回openid
        //如果手机号不为空，返回openid值是空字符串
        //前端判断：如果openid不为空，绑定手机号，如果openid为空，不需要绑定手机号
        if(StringUtils.isEmpty(userInfo.getPhone())){
            map.put("openid", userInfo.getOpenid());
        }else {
            map.put("openid", "");
        }

        return "redirect:http://localhost:3000/weixin/callback?token="+map.get("token")+ "&openid="+map.get("openid")+"&name="+URLEncoder.encode(map.get("name"),"utf-8");
    }
}

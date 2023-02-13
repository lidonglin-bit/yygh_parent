package com.donglin.yygh.user.controller.user;


import com.donglin.yygh.common.result.R;
import com.donglin.yygh.enums.AuthStatusEnum;
import com.donglin.yygh.model.user.UserInfo;
import com.donglin.yygh.user.service.UserInfoService;
import com.donglin.yygh.user.utils.AuthContextHolder;
import com.donglin.yygh.user.utils.JwtHelper;
import com.donglin.yygh.vo.user.LoginVo;
import com.donglin.yygh.vo.user.UserAuthVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author donglin
 * @since 2023-02-04
 */
@RestController
@RequestMapping("/user/userinfo")
public class UserInfoController {

    @Autowired
    private UserInfoService userInfoService;

    @ApiOperation(value = "会员登录")
    @PostMapping("login")
    public R login(@RequestBody LoginVo loginVo){
        Map<String,Object> info = userInfoService.login(loginVo);
        return R.ok().data(info);
    }

//    //获取用户id信息接口
//    @GetMapping("auth/getUserInfo")
//    public R getUserInfo(HttpServletRequest request) {
//        Long userId = AuthContextHolder.getUserId(request);
//        UserInfo userInfo = userInfoService.getById(userId);
//        return R.ok().data("userInfo",userInfo);
//    }

    @GetMapping("/info")
    public R getUserInfo(@RequestHeader String token){
        Long userId = JwtHelper.getUserId(token);
        UserInfo byId = userInfoService.getUserInfo(userId);
        return R.ok().data("user",byId);
    }

    @PutMapping("/update")
    public R update(@RequestHeader String token, @RequestBody UserAuthVo userAuthVo){
        Long userId = JwtHelper.getUserId(token);
        UserInfo userInfo = new UserInfo();
        userInfo.setId(userId);
        userInfo.setName(userAuthVo.getName());
        userInfo.setCertificatesType(userAuthVo.getCertificatesType());
        userInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
        userInfo.setCertificatesUrl(userAuthVo.getCertificatesUrl());
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());
        userInfoService.updateById(userInfo);

        return R.ok();
    }



}


package com.donglin.yygh.user.service;

import com.donglin.yygh.model.user.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.donglin.yygh.vo.user.LoginVo;

import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author donglin
 * @since 2023-02-04
 */
public interface UserInfoService extends IService<UserInfo> {

    Map<String, Object> login(LoginVo loginVo);

    UserInfo selectWxInfoOpenId(String openid);

    UserInfo getUserInfo(Long userId);
}

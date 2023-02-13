package com.donglin.yygh.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.donglin.yygh.model.user.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.donglin.yygh.vo.user.LoginVo;
import com.donglin.yygh.vo.user.UserInfoQueryVo;

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

    Page<UserInfo> getUserInfoPage(Long pageNum, Long limit, UserInfoQueryVo userInfoQueryVo);

    void lock(Long userId, Integer status);

    Map<String, Object> show(Long userId);

    void approval(Long userId, Integer authStatus);
}

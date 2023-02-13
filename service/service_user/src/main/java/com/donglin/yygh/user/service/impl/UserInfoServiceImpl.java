package com.donglin.yygh.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.donglin.yygh.common.exception.YyghException;
import com.donglin.yygh.enums.AuthStatusEnum;
import com.donglin.yygh.model.user.Patient;
import com.donglin.yygh.model.user.UserInfo;
import com.donglin.yygh.user.mapper.PatientMapper;
import com.donglin.yygh.user.mapper.UserInfoMapper;
import com.donglin.yygh.user.service.PatientService;
import com.donglin.yygh.user.service.UserInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.donglin.yygh.user.utils.JwtHelper;
import com.donglin.yygh.vo.user.LoginVo;
import com.donglin.yygh.vo.user.UserInfoQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author donglin
 * @since 2023-02-04
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {


    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private PatientService patientService;

    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        //1.首先获取用户输入的手机号和验证码信息
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        //2.对接收的手机号和验证码做一个非空判断
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)){
            throw  new YyghException(20001,"数据为空");
        }
        //TODO 对验证码做进一步确认
        //校验校验验证码
        String mobleCode = redisTemplate.opsForValue().get(phone);
        if(!code.equals(mobleCode)) {
            throw new YyghException(20001,"验证码失败");
        }

        String openid = loginVo.getOpenid();
        Map<String, Object> map = new HashMap<>();
        //openid微信没有绑定过
        if(StringUtils.isEmpty(openid)) {
            //4.是否手机首次登陆，如果首次登陆，就先网表中注册一下当前用户信息
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("phone",phone);
            UserInfo userInfo = baseMapper.selectOne(wrapper);
            if (null == userInfo){
                userInfo = new UserInfo();
                userInfo.setPhone(phone);
                userInfo.setCreateTime(new Date());
                userInfo.setStatus(1);
                baseMapper.insert(userInfo);
            }
            //5.验证用户的status
            if (userInfo.getStatus() == 0){
                throw new YyghException(20001,"用户已经禁用");
            }
            map = get(userInfo);
        }else {
            //1 创建userInfo对象，用于存在最终所有数据
            UserInfo userInfoFinal = new UserInfo();
            //2 根据手机查询数据
            // 如果查询手机号对应数据,封装到userInfoFinal
            UserInfo userInfoPhone =
                    baseMapper.selectOne(new QueryWrapper<UserInfo>().eq("phone", phone));
            if(userInfoPhone != null) {
                // 如果查询手机号对应数据,封装到userInfoFinal
                BeanUtils.copyProperties(userInfoPhone,userInfoFinal);
                //把手机号数据删除
                baseMapper.delete(new QueryWrapper<UserInfo>().eq("phone", phone));
            }
            //3 根据openid查询微信信息
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("openid",openid);
            UserInfo userInfoWX = baseMapper.selectOne(wrapper);

            //4 把微信信息封装userInfoFinal
            userInfoFinal.setOpenid(userInfoWX.getOpenid());
            userInfoFinal.setNickName(userInfoWX.getNickName());
            userInfoFinal.setId(userInfoWX.getId());
            //数据库表没有相同绑定手机号，设置值
            if(userInfoPhone == null) {
                userInfoFinal.setPhone(phone);
                userInfoFinal.setStatus(userInfoWX.getStatus());
            }
            //修改手机号
            baseMapper.updateById(userInfoFinal);

            //5 判断用户是否锁定
            if(userInfoFinal.getStatus() == 0) {
                throw new YyghException(20001,"用户被锁定");
            }
            //6 登录后，返回登录数据
            map = get(userInfoFinal);
        }
        return map;
    }

    private Map<String,Object> get(UserInfo userInfo) {
        //返回页面显示名称
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if(StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        map.put("name", name);
        //根据userid和name生成token字符串
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token", token);
        return map;
    }

    @Override
    public UserInfo selectWxInfoOpenId(String openid) {
        return baseMapper.selectOne(new QueryWrapper<UserInfo>().eq("openid",openid));
    }

    @Override
    public UserInfo getUserInfo(Long userId) {
        UserInfo userInfo = baseMapper.selectById(userId);
        userInfo.getParam().put("authStatusString", AuthStatusEnum.getStatusNameByStatus(userInfo.getStatus()));
        return userInfo;
    }

    @Override
    public Page<UserInfo> getUserInfoPage(Long pageNum, Long limit, UserInfoQueryVo userInfoQueryVo) {
        Page<UserInfo> page = new Page<>(pageNum,limit);
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        String name = userInfoQueryVo.getKeyword(); //用户名称
        Integer status = userInfoQueryVo.getStatus();//用户状态
        Integer authStatus = userInfoQueryVo.getAuthStatus(); //认证状态
        String createTimeBegin = userInfoQueryVo.getCreateTimeBegin(); //开始时间
        String createTimeEnd = userInfoQueryVo.getCreateTimeEnd(); //结束时间
        //对条件值进行非空判断
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(name)) {
            wrapper.like("name",name).or().eq("phone",userInfoQueryVo.getKeyword());
        }
        if(!StringUtils.isEmpty(status)) {
            wrapper.eq("status",status);
        }
        if(!StringUtils.isEmpty(authStatus)) {
            wrapper.eq("auth_status",authStatus);
        }
        if(!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time",createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le("create_time",createTimeEnd);
        }
        Page<UserInfo> page1 = baseMapper.selectPage(page, wrapper);
        page1.getRecords().stream().forEach(item->{
            this.packageUserInfo(item);
        });
        return page1;
    }

    //实现方法
    @Override
    public void lock(Long userId, Integer status) {
        if(status.intValue() == 0 || status.intValue() == 1) {
            UserInfo userInfo = this.getById(userId);
            userInfo.setStatus(status);
            baseMapper.updateById(userInfo);
        }
    }


    @Override
    public Map<String, Object> show(Long userId) {
        UserInfo userInfo = baseMapper.selectById(userId);
        QueryWrapper<Patient> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        List<Patient> patients = patientService.selectList(queryWrapper);
        Map<String,Object> map = new HashMap<>();
        map.put("userInfo",userInfo);
        map.put("patients",patients);
        return map;
    }

    //认证审批  2通过  -1不通过
    @Override
    public void approval(Long userId, Integer authStatus) {
        if(authStatus.intValue()==2 || authStatus.intValue()==-1) {
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setAuthStatus(authStatus);
            baseMapper.updateById(userInfo);
        }
    }

    private void packageUserInfo(UserInfo item) {
        //处理认证状态编码
        item.getParam().put("authStatusString",AuthStatusEnum.getStatusNameByStatus(item.getAuthStatus()));
        //处理用户状态 0  1  intValue()转换成int类型
        String statusString = item.getStatus().intValue()==0 ?"锁定" : "正常";
        item.getParam().put("statusString",statusString);
    }




}

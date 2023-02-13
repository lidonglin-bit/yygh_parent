package com.donglin.yygh.user.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.donglin.yygh.common.result.R;
import com.donglin.yygh.model.user.UserInfo;
import com.donglin.yygh.user.service.UserInfoService;
import com.donglin.yygh.vo.user.UserInfoQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/admin/userinfo")
public class AdminUserInfoController {
    @Autowired
    private UserInfoService userInfoService;

    //用户列表（条件查询带分页）
    @GetMapping("{pageNum}/{limit}")
    public R getUserInfoPage(@PathVariable Long pageNum,
                  @PathVariable Long limit,
                  UserInfoQueryVo userInfoQueryVo) {
        Page<UserInfo> page = userInfoService.getUserInfoPage(pageNum,limit,userInfoQueryVo);
        return R.ok().data("total",page.getTotal()).data("list",page.getRecords());
    }

    @ApiOperation(value = "锁定")
    @GetMapping("lock/{userId}/{status}")
    public R lock(
            @PathVariable("userId") Long userId,
            @PathVariable("status") Integer status){
        userInfoService.lock(userId, status);
        return R.ok();
    }

    //用户详情
    @GetMapping("show/{userId}")
    public R show(@PathVariable Long userId) {
        Map<String,Object> map = userInfoService.show(userId);
        return R.ok().data(map);
    }

    @GetMapping("approval/{userId}/{authStatus}")
    public R approval(@PathVariable Long userId,@PathVariable Integer authStatus) {
        userInfoService.approval(userId,authStatus);
        return R.ok();
    }
}
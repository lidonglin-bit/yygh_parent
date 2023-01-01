package com.donglin.yygh.hosp.controller;

import com.donglin.yygh.common.result.R;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/user")
@CrossOrigin  //解决跨域
public class UserController {

    //login
    @PostMapping("/login")
    public R login() {
        return R.ok().data("token","admin");
    }

    //info
    @GetMapping("/info")
    public R info() {
        return R.ok().data("roles","[admin]").data("name","admin").data("avatar","https://media.tenor.com/wKpR3DfIZPgAAAAC/%E8%94%A1%E5%BE%90%E5%9D%A4-kiss.gif");
    }

    //logout
    @PostMapping("/logout")
    public String logout() {
        return "{\"code\":20000,\"data\":\"success\"}";
    }



}
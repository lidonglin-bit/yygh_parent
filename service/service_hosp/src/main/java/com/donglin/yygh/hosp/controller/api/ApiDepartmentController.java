package com.donglin.yygh.hosp.controller.api;

import com.donglin.yygh.hosp.bean.Result;
import com.donglin.yygh.hosp.service.DepartmentService;
import com.donglin.yygh.hosp.utils.HttpRequestHelper;
import com.donglin.yygh.model.hosp.Department;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/hosp")
public class ApiDepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @ApiOperation(value = "获取分页列表")
    @PostMapping("department/list")
    public Result department(HttpServletRequest request){
        Map<String, Object> map = HttpRequestHelper.switchMap(request.getParameterMap());
        //必须参数校验 略
        //签名校验
        Page<Department> page = departmentService.getDepartmentPage(map);
        return Result.ok(page);
    }

    @ApiOperation(value = "上传科室")
    @PostMapping("saveDepartment")
    public Result saveDepartment(HttpServletRequest request){
        Map<String, Object> map = HttpRequestHelper.switchMap(request.getParameterMap());
        //必须参数校验 略
        //签名校验
        departmentService.save(map);
        return Result.ok();
    }

    @ApiOperation(value = "删除科室")
    @PostMapping("department/remove")
    public Result remove(HttpServletRequest request){
        Map<String, Object> map = HttpRequestHelper.switchMap(request.getParameterMap());
        //必须参数校验 略
        departmentService.remove(map);
        return Result.ok();
    }
}


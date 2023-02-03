package com.donglin.yygh.hosp.controller.user;

import com.donglin.yygh.common.result.R;
import com.donglin.yygh.hosp.service.DepartmentService;
import com.donglin.yygh.hosp.service.HospitalService;
import com.donglin.yygh.model.hosp.Hospital;
import com.donglin.yygh.vo.hosp.DepartmentVo;
import com.donglin.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "医院显示接口")
@RestController
@RequestMapping("/user/hosp/hospital")
public class UserHospitalController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;

    @ApiOperation(value = "获取科室列表")
    @GetMapping("/department/{hoscode}")
    public R findAll(@PathVariable String hoscode) {
        List<DepartmentVo> list = departmentService.getDeptList(hoscode);
        return R.ok().data("list",list);
    }

    @ApiOperation(value = "医院预约挂号详情")
    @GetMapping("/detail/{hoscode}")
    public R getHospitalDetail(@PathVariable String hoscode) {
        Hospital hospital= hospitalService.getHospitalDetail(hoscode);
        return R.ok().data("hospital",hospital);
    }



    @GetMapping ("/list")
    public R getHospitalList(HospitalQueryVo hospitalQueryVo){
        Page<Hospital> page = hospitalService.getHospitalPage(1, 100, hospitalQueryVo);
        return R.ok().data("list",page.getContent());
    }

    @GetMapping("/{name}")
    public R findByHosname(@PathVariable String name){
        List<Hospital> list = hospitalService.findByHosname(name);
        return R.ok().data("list",list);
    }
}

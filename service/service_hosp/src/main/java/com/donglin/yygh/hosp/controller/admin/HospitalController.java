package com.donglin.yygh.hosp.controller.admin;

import com.donglin.yygh.common.result.R;
import com.donglin.yygh.hosp.service.HospitalService;
import com.donglin.yygh.model.hosp.Hospital;
import com.donglin.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/hosp")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    @GetMapping("/{pageNum}/{pageSize}")
    public R getHospitalPage(@PathVariable Integer pageNum, @PathVariable Integer pageSize, HospitalQueryVo hospitalQueryVo){
        Page<Hospital> hospitalPage = hospitalService.getHospitalPage(pageNum,pageSize,hospitalQueryVo);
        return R.ok().data("total",hospitalPage.getTotalPages()).data("list",hospitalPage.getContent());
    }

    @ApiOperation(value = "更新上线状态")
    @PutMapping("/{id}/{status}")
    public R updateStatus(@PathVariable String id,@PathVariable Integer status){
        hospitalService.updateStatus(id,status);
        return R.ok();
    }

    @ApiOperation(value = "获取医院详情")
    @GetMapping("/detail/{id}")
    public R detail(@PathVariable String id){
        Hospital hospital = hospitalService.detail(id);
        return R.ok().data("hospital",hospital);
    }
}

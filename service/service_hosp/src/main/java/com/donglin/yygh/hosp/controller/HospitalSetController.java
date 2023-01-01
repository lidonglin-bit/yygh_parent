package com.donglin.yygh.hosp.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.donglin.yygh.common.exception.YyghException;
import com.donglin.yygh.common.result.R;
import com.donglin.yygh.common.utils.MD5;
import com.donglin.yygh.hosp.service.HospitalSetService;
import com.donglin.yygh.model.hosp.HospitalSet;
import com.donglin.yygh.vo.hosp.HospitalSetQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

/**
 * <p>
 * 医院设置表 前端控制器
 * </p>
 *
 * @author donglin
 * @since 2022-12-29
 */
@Api(tags = "医院设置")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
@CrossOrigin
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;


    @ApiOperation(value = "锁定与解锁")
    @DeleteMapping("/status/{id}/{status}")
    public R status(@PathVariable Long id, @PathVariable Integer status){
        //HospitalSet byId = hospitalSetService.getById(id);  //乐观锁
        HospitalSet hospitalSet = new HospitalSet();
        hospitalSet.setId(id);
        hospitalSet.setStatus(status);
        hospitalSetService.updateById(hospitalSet);
        return R.ok();
    }

    //批量删除医院设置
    @ApiOperation(value = "批量删除")
    @DeleteMapping("/delete")
    public R delete(@RequestBody List<Integer> ids){
        hospitalSetService.removeByIds(ids);
        return R.ok();
    }


    //修改之回显数据
    @ApiOperation(value = "根据id查询医院设置")
    @GetMapping("/detail/{id}")
    public R detail(@PathVariable Integer id){
        return R.ok().data("items",hospitalSetService.getById(id));
    }
    //修改之修改数据
    @ApiOperation(value = "根据ID修改医院设置")
    @PutMapping("/update")
    public R update(@RequestBody HospitalSet hospitalSet){
        hospitalSetService.updateById(hospitalSet);
        return R.ok();
    }


    @ApiOperation(value = "新增接口")
    @PostMapping("save")
    public R save(@RequestBody HospitalSet hospitalSet){
        //设置状态 1 使用 0 不能使用
        hospitalSet.setStatus(1);
        //签名秘钥
        Random random = new Random();
        hospitalSet.setSignKey(MD5.encrypt(System.currentTimeMillis()+""+random.nextInt(1000)));

        hospitalSetService.save(hospitalSet);
        return R.ok();
    }


    @ApiOperation(value = "带查询的条件的分页")
    @PostMapping("/page/{pageNum}/{size}")
    public R getPageInfo(@ApiParam(name = "pageNum",value = "当前页") @PathVariable Integer pageNum,
                         @ApiParam(name = "size",value = "每页显示多少条数据") @PathVariable Integer size,
                         @RequestBody HospitalSetQueryVo hospitalSetQueryVo
                         ){
        Page<HospitalSet> page = new Page<>(pageNum, size);
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(hospitalSetQueryVo.getHosname())){
            queryWrapper.like("hosname",hospitalSetQueryVo.getHosname());
        }
        if (!StringUtils.isEmpty(hospitalSetQueryVo.getHoscode())){
            queryWrapper.eq("hoscode",hospitalSetQueryVo.getHoscode());
        }
        hospitalSetService.page(page,queryWrapper);
        return R.ok().data("total",page.getTotal()).data("rows",page.getRecords());
    }

    @ApiOperation(value = "医院设置列表")
    @GetMapping("findAll")
    public R findAll(){
        List<HospitalSet> list = hospitalSetService.list();
        return R.ok().data("items",list);
    }

    @ApiOperation(value = "医院设置删除")
    @DeleteMapping("{id}")
    public R removeId(@ApiParam(name = "id",value = "医院编号",required = true) @PathVariable Integer id){
        hospitalSetService.removeById(id);
        return R.ok();
    }

}


package com.donglin.yygh.cmn.controller;


import com.donglin.yygh.cmn.service.DictService;
import com.donglin.yygh.common.result.R;
import com.donglin.yygh.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 组织架构表 前端控制器
 * </p>
 *
 * @author donglin
 * @since 2023-01-04
 */
@Api(tags = "数据字典接口")
@RestController
@RequestMapping("/admin/cmn")
public class DictController {

    @Autowired
    private DictService dictService;

    @ApiOperation(value = "根据数据id查询子数据列表")
    @GetMapping("/childList/{pid}")
    @Cacheable(value = "dict", key = "'selectIndexList'+#pid")
    public R getchildListById(@PathVariable Long pid){
        List<Dict> list = dictService.getchildListById(pid);
        return R.ok().data("items",list);
    }

    @ApiOperation(value="导出")
    @GetMapping(value = "/download")
    public void download(HttpServletResponse response) throws IOException {
        dictService.download(response);
    }

    @ApiOperation(value = "导入")
    @PostMapping("/upload")
    @CacheEvict(value = "dict", allEntries = true)
    public R upload(MultipartFile file) throws IOException {
        dictService.uploadFile(file);
        return R.ok();
    }

    //根据医院所属的省市区编号获取省市区文字
    //远程调用@PathVariable指定value属性值
    @GetMapping("/{value}")
    public String getNameByValue(@PathVariable("value") Long value){
        return dictService.getNameByValue(value);
    }

    //根据医院的等级编号获取医院等级信息
    @GetMapping("/{dictCode}/{value}")
    public String getNameByDictCodeAndValue(@PathVariable("dictCode") String dictCode,
                                            @PathVariable("value") Long value){
        return dictService.getNameByDictCodeAndValue(dictCode,value);
    }


}


package com.donglin.yygh.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "service-cmn")  //被调用方  在application.properties去查看服务名称   spring.application.name=service-cmn
public interface DictFeignClient {

    //根据医院所属的省市区编号获取省市区文字
    //远程调用@PathVariable指定value属性值
    @GetMapping("/admin/cmn/{value}")
    public String getNameByValue(@PathVariable("value") Long value);

    //根据医院的等级编号获取医院等级信息
    @GetMapping("/admin/cmn/{dictCode}/{value}")
    public String getNameByDictCodeAndValue(@PathVariable("dictCode") String dictCode,
                                            @PathVariable("value") Long value);

}

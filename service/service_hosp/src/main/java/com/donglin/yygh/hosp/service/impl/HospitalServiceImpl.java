package com.donglin.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.donglin.yygh.client.DictFeignClient;
import com.donglin.yygh.common.exception.YyghException;
import com.donglin.yygh.enums.DictEnum;
import com.donglin.yygh.hosp.mapper.HospitalSetMapper;
import com.donglin.yygh.hosp.repository.HospitalRepository;
import com.donglin.yygh.hosp.service.HospitalService;
import com.donglin.yygh.model.hosp.Hospital;
import com.donglin.yygh.model.hosp.HospitalSet;
import com.donglin.yygh.vo.hosp.HospitalQueryVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private HospitalSetMapper hospitalSetMapper;

    @Autowired
    private DictFeignClient dictFeignClient;


    @Override
    public void saveHospital(Map<String, Object> resultMap) {
        String s = JSONObject.toJSONString(resultMap);
        Hospital hospital = JSONObject.parseObject(s, Hospital.class);
        //如果医院两次保存那么会保存两次，如果数据没有就保存，数据有就更新     save既可以保存，也可以更新
        String hoscode = hospital.getHoscode();
        Hospital collection = hospitalRepository.findByHoscode(hoscode);
        if (collection == null){   //平台上没有该医院信息做添加
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }else {  //平台上有医院信息做修改
            hospital.setStatus(collection.getStatus());
            hospital.setCreateTime(collection.getCreateTime());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(collection.getIsDeleted());
            //要进行id进行修改
            hospital.setId(collection.getId());
            hospitalRepository.save(hospital);
        }

    }

    @Override
    public String getSignKeyWithHoscode(String requestHoscode) {
        QueryWrapper<HospitalSet> wrapper = new QueryWrapper<>();
        wrapper.eq("hoscode",requestHoscode);
        HospitalSet hospitalSet = hospitalSetMapper.selectOne(wrapper);
        if (hospitalSet == null){
            throw new YyghException(20001,"该医院信息不存在");
        }
        return hospitalSet.getSignKey();
    }

    @Override
    public Hospital getByHoscode(String hoscode) {
        Hospital hospital = hospitalRepository.findByHoscode(hoscode);
        return hospital;
    }

    @Override
    public Page<Hospital> getHospitalPage(Integer pageNum, Integer pageSize, HospitalQueryVo hospitalQueryVo) {

        Hospital hospital = new Hospital();
//        if (!StringUtils.isEmpty(hospitalQueryVo.getHosname())){
//            hospital.setHosname(hospitalQueryVo.getHosname());
//        }
//        if (!StringUtils.isEmpty(hospitalQueryVo.getHoscode())){
//            hospital.setHoscode(hospitalQueryVo.getHoscode());
//        }
//        if (!StringUtils.isEmpty(hospitalQueryVo.getCityCode())){
//            hospital.setCityCode(hospitalQueryVo.getCityCode());
//        }
        BeanUtils.copyProperties(hospitalQueryVo, hospital);
        //0为第一页
        Pageable pageable = PageRequest.of(pageNum-1, pageSize);
        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching() //构建对象
//                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) //改变默认字符串匹配方式：模糊查询
                .withMatcher("hosname",ExampleMatcher.GenericPropertyMatchers.contains())
                .withIgnoreCase(true); //改变默认大小写忽略方式：忽略大小写
        Example<Hospital> hospitalExample = Example.of(hospital, matcher);
        Page<Hospital> pages = hospitalRepository.findAll(hospitalExample, pageable);
        pages.getContent().stream().forEach(item->{
            this.packageHospital(item);
        });
        return pages;
    }

    @Override
    public void updateStatus(String id, Integer status) {
        if (status == 0 || status == 1){
            Hospital hospital = hospitalRepository.findById(id).get();
            hospital.setStatus(status);
            hospital.setUpdateTime(new Date());
            hospitalRepository.save(hospital);
        }
    }

    @Override
    public Hospital detail(String id) {
        Hospital hospital = hospitalRepository.findById(id).get();
        this.packageHospital(hospital);
        return hospital;
    }

    private void packageHospital(Hospital item) {
        String hostype = item.getHostype();
        String provinceCode = item.getProvinceCode();
        String cityCode = item.getCityCode();
        String districtCode = item.getDistrictCode();
        String provinceAddress = dictFeignClient.getNameByValue(Long.parseLong(provinceCode));
        String cityAddress = dictFeignClient.getNameByValue(Long.parseLong(cityCode));
        String districtAddress = dictFeignClient.getNameByValue(Long.parseLong(districtCode));

        String level = dictFeignClient.getNameByDictCodeAndValue(DictEnum.HOSTYPE.getDictCode(), Long.parseLong(hostype));
        item.getParam().put("hostypeString",level);
        item.getParam().put("fullAddress",provinceAddress+cityAddress+districtAddress+ item.getAddress());
    }
}

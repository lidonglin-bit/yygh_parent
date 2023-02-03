package com.donglin.yygh.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.donglin.yygh.hosp.repository.DepartmentRepository;
import com.donglin.yygh.hosp.service.DepartmentService;
import com.donglin.yygh.model.hosp.Department;
import com.donglin.yygh.vo.hosp.DepartmentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void save(Map<String, Object> map) {
        //map 转换department对象
        String jsonString = JSONObject.toJSONString(map);
        Department department = JSONObject.parseObject(jsonString, Department.class);
        //根据医院编号 和 科室编号查询
        Department departmentExist = departmentRepository.getDepartmentByHoscodeAndDepcode(department.getHoscode(),department.getDepcode());
        if (departmentExist == null){
            department.setCreateTime(new Date());
            department.setUpdateTime(new Date());
            department.setIsDeleted(0);
            departmentRepository.save(department);
        }else {
            department.setCreateTime(departmentExist.getCreateTime());
            department.setUpdateTime(new Date());
            department.setIsDeleted(departmentExist.getIsDeleted());
            departmentRepository.save(department);
        }

    }

    @Override
    public Page<Department> getDepartmentPage(Map<String, Object> map) {
        Integer page = Integer.parseInt((String) map.get("page"));
        Integer limit = Integer.parseInt((String) map.get("limit"));
        //0默认是第一页
        PageRequest pageable = PageRequest.of(page - 1, limit);
        Department department = new Department();
        department.setHoscode((String) map.get("hoscode"));
        Example<Department> example = Example.of(department);
        Page<Department> all = departmentRepository.findAll(example, pageable);
        return all;
    }

    @Override
    public void remove(Map<String, Object> map) {
        String hoscode = (String)map.get("hoscode");
        String depcode = (String)map.get("depcode");
        Department department = departmentRepository.getDepartmentByHoscodeAndDepcode(hoscode, depcode);
        if (department != null){
            departmentRepository.deleteById(department.getId());
        }
    }

    //根据医院编号，查询医院所有科室列表
    @Override
    public List<DepartmentVo> getDeptList(String hoscode) {
        //创建list集合，用于最终数据封装
        ArrayList<DepartmentVo> result = new ArrayList<>();
        //根据医院编号，查询医院所有科室信息
        Department departmentQuery = new Department();
        departmentQuery.setHoscode(hoscode);
        Example<Department> example = Example.of(departmentQuery);
        //所有科室列表 departmentList
        List<Department> departmentList = departmentRepository.findAll(example);

        //根据大科室编号  bigcode 分组，获取每个大科室里面下级子科室
        Map<String, List<Department>> departmentMap = departmentList.stream().collect(Collectors.groupingBy(Department::getBigcode));
        //遍历map集合 deparmentMap
        for (Map.Entry<String, List<Department>> entry : departmentMap.entrySet()) {
            //大科室编号
            String bigCode = entry.getKey();
            //大科室编号对应的全局数据
            List<Department> department1List = entry.getValue();
            //封装大科室
            DepartmentVo departmentVo1 = new DepartmentVo();
            departmentVo1.setDepcode(bigCode);
            departmentVo1.setDepname(department1List.get(0).getBigname());

            //封装小科室
            List<DepartmentVo> children = new ArrayList<>();
            for (Department department : department1List) {
                DepartmentVo departmentVo2 =  new DepartmentVo();
                departmentVo2.setDepcode(department.getDepcode());
                departmentVo2.setDepname(department.getDepname());
                //封装到list集合
                children.add(departmentVo2);
            }
            //把小科室list集合放到大科室children里面
            departmentVo1.setChildren(children);
            //放到最终result里面
            result.add(departmentVo1);
        }
        return result;
    }

    @Override
    public String getDepName(String hoscode, String depcode) {

        Department department = departmentRepository.findByHoscodeAndDepcode(hoscode, depcode);
        if(department != null){
            return department.getDepname();
        }
        return "";
    }


}

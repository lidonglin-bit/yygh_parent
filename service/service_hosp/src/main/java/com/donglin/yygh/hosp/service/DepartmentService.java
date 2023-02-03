package com.donglin.yygh.hosp.service;

import com.donglin.yygh.model.hosp.Department;
import com.donglin.yygh.vo.hosp.DepartmentVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface DepartmentService {
    void save(Map<String, Object> map);

    Page<Department> getDepartmentPage(Map<String, Object> map);

    void remove(Map<String, Object> map);

    List<DepartmentVo> getDeptList(String hoscode);

    String getDepName(String hoscode, String depcode);


}

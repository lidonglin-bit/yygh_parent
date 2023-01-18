package com.donglin.yygh.hosp.service;

import com.donglin.yygh.model.hosp.Department;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface DepartmentService {
    void save(Map<String, Object> map);

    Page<Department> getDepartmentPage(Map<String, Object> map);
}

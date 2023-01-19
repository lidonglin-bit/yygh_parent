package com.donglin.yygh.cmn.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.donglin.yygh.model.cmn.Dict;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * <p>
 * 组织架构表 服务类
 * </p>
 *
 * @author donglin
 * @since 2023-01-04
 */
public interface DictService extends IService<Dict> {

    //根据数据id查询子数据列表
    List<Dict> getchildListById(Long pid);

    void download(HttpServletResponse response) throws IOException;

    void uploadFile(MultipartFile file) throws IOException;


    String getNameByValue(Long value);

    String getNameByDictCodeAndValue(String dictCode, Long value);
}

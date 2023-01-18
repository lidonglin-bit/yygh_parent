package com.donglin.yygh.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.donglin.yygh.cmn.listener.DictListener;
import com.donglin.yygh.model.cmn.Dict;
import com.donglin.yygh.cmn.mapper.DictMapper;
import com.donglin.yygh.cmn.service.DictService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.donglin.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 组织架构表 服务实现类
 * </p>
 *
 * @author donglin
 * @since 2023-01-04
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    //根据数据id查询子数据列表
    @Override
    public List<Dict> getchildListById(Long pid) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",pid);
        List<Dict> dictList = baseMapper.selectList(wrapper);
        //向list集合每个dict对象中设置hasChildren
        for (Dict dict : dictList) {
            Long dictId = dict.getId();
            boolean isChild = this.isChildren(dictId);
            dict.setHasChildren(isChild);
        }
        return dictList;
    }

    @Override
    public void download(HttpServletResponse response) throws IOException {
        List<Dict> dictList = baseMapper.selectList(null);
        ArrayList<DictEeVo> dictEeVoList = new ArrayList<>(dictList.size());
        for (Dict dict : dictList) {
            DictEeVo dictEeVo = new DictEeVo();
            BeanUtils.copyProperties(dict,dictEeVo);
            dictEeVoList.add(dictEeVo);
        }
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode("数据字典", "UTF-8");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("模板").doWrite(dictEeVoList);

    }

    @Override
    public void uploadFile(MultipartFile file) throws IOException {
        EasyExcel.read(file.getInputStream(),DictEeVo.class,new DictListener(baseMapper)).sheet(0).doRead();
    }

    //判断id下面是否有子节点
    private boolean isChildren(Long dictId) {
        QueryWrapper<Dict> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",dictId);
        Integer count = baseMapper.selectCount(wrapper);
        return count>0;

    }
}

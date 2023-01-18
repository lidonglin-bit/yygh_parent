package com.donglin.yygh.cmn.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.donglin.yygh.cmn.mapper.DictMapper;
import com.donglin.yygh.model.cmn.Dict;
import com.donglin.yygh.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;

public class DictListener extends AnalysisEventListener<DictEeVo> {

   //不交给spring容器@Component 我们使用构造器来实现@Autowire
   private DictMapper dictMapper;

   public DictListener(DictMapper dictMapper){
       this.dictMapper = dictMapper;
   }

    @Override
    public void invoke(DictEeVo dictEeVo, AnalysisContext analysisContext) {
        Dict dict = new Dict();
        BeanUtils.copyProperties(dictEeVo,dict);
        QueryWrapper<Dict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",dictEeVo.getId());
        Integer count = this.dictMapper.selectCount(queryWrapper);
        if (count>0){
            this.dictMapper.updateById(dict);
        }else {
            this.dictMapper.insert(dict);
        }


    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}

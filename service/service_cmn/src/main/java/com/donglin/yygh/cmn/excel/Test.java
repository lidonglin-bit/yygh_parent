package com.donglin.yygh.cmn.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;


import java.util.ArrayList;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        ExcelReader excelReader = EasyExcel.read("F:\\hosptial project\\11.xlsx").build();
        ReadSheet sheet1 = EasyExcel.readSheet(0).head(Stu.class).registerReadListener(new ExcelListener()).build();
        ReadSheet sheet2 = EasyExcel.readSheet(1).head(Stu.class).registerReadListener(new ExcelListener()).build();
        excelReader.read(sheet1,sheet2);
        excelReader.finish();
    }





    //循环设置要添加的数据，最终封装到list集合中
    private static List<Stu> data() {
        List<Stu> list = new ArrayList<Stu>();
        for (int i = 0; i < 10; i++) {
            Stu data = new Stu();
            data.setSno(i);
            data.setSname("张三"+i);
            list.add(data);
        }
        return list;
    }
}

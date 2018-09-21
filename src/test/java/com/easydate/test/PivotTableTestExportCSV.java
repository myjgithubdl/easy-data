package com.easydate.test;

import com.alibaba.fastjson.JSONObject;
import com.easydata.export.ExportCSVUtil;
import com.easydata.pivottable.core.PivotTableDataCore;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class PivotTableTestExportCSV {



    public static void main(String[] args)  {

        //test1();
        test2();


    }



    public static void test1()  {
        PivotTableDataCore pivotTableDataCore = PivotTableTestData.getPivotTableData();
        OutputStream out= null;
        try {
            out = new FileOutputStream("E:\\Myron\\lakala\\需求文档\\数据透视表\\测试数据\\乘车透视表.csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ExportCSVUtil.exportCSV(out ,pivotTableDataCore.getPivotTableTheadColumnList() ,pivotTableDataCore.getPivotTableDataList() );

    }

    public static void test2  (){
        StudentScoreTestData pivotTableTestData=new StudentScoreTestData();

        PivotTableDataCore pivotTableDataCore = pivotTableTestData.getPivotTableData();

        OutputStream out= null;
        try {
            out = new FileOutputStream("E:\\Myron\\lakala\\需求文档\\数据透视表\\测试数据\\学生成绩透视表.csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ExportCSVUtil.exportCSV(out ,pivotTableDataCore.getPivotTableTheadColumnList() ,pivotTableDataCore.getPivotTableDataList() );

    }


}

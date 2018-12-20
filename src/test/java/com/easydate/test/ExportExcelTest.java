package com.easydate.test;

import com.easydata.export.ExportCSVUtil;
import com.easydata.export.ExportExcelUtil;
import com.easydata.head.TheadColumn;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class ExportExcelTest {


    public static void main(String[] args) {
        testExportExcel();

    }


    public static void testExportExcel  (){
        //数据
        List<Map<String, Object>> dataList = MySQLData.getDataList();
        //表头列表
        List<TheadColumn> theadColumnList = MySQLData.getTheadColumnList();


        OutputStream outExcel= null;
        OutputStream outCSV= null;
        try {
            outExcel = new FileOutputStream("E:\\Myron\\天气数据.xlsx");
            outCSV = new FileOutputStream("E:\\Myron\\天气数据.csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //导出Excel
        ExportExcelUtil.exportExcel(outExcel,"天气数据" ,theadColumnList ,dataList);

        //导出CSV(设置的表头样式失效)
        ExportCSVUtil.exportCSV(outCSV,theadColumnList ,dataList);

    }

}

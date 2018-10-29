package com.easydate.test;

import com.easydata.enmus.TextHorizontalAlignment;
import com.easydata.export.ExportCSVUtil;
import com.easydata.export.ExportExcelUtil;
import com.easydata.head.TheadColumn;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExportExcelTest {

    public static void main(String[] args) {
        testExportExcel();

    }


    public static void testExportExcel  (){
        //数据
        List<Map<String, Object>> dataList = MySQLData.getDataList();
        //表头
        TheadColumn provinceTheadColumn=new TheadColumn("province" , null ,"province","省份" );
        provinceTheadColumn.setDownMergeCells(true);//向下合并单元格值相同的列

        TheadColumn areaTheadColumn=new TheadColumn("area" , null ,"area","城市" );
        areaTheadColumn.setDownMergeCells(true);//向下合并单元格值相同的列

        TheadColumn dtTheadColumn=new TheadColumn("dt" , null ,"dt","日期" );
        TheadColumn aqiTheadColumn=new TheadColumn("aqi" , null ,"aqi","空气质量指数" );
        TheadColumn aqiRangeTheadColumn=new TheadColumn("aqi-range" , null ,"aqi-range","空气质量指数范围" );
        TheadColumn qualityTheadColumn=new TheadColumn("quality" , null ,"quality","空气质量" );
        TheadColumn pm25TheadColumn=new TheadColumn("pm25" , null ,"pm25","pm2.5" );
        TheadColumn pm10TheadColumn=new TheadColumn("pm10" , null ,"pm10","pm10" );

        TheadColumn yhwTheadColumn=new TheadColumn("yhw" , null ,null,"氧化物" );
        yhwTheadColumn.setTheadTextAlign(TextHorizontalAlignment.CENTER);//文字居中对齐

        TheadColumn so2TheadColumn=new TheadColumn("so2" , "yhw" ,"so2","二氧化硫" );
        TheadColumn coTheadColumn=new TheadColumn("co" , "yhw" ,"co","一氧化碳" );
        TheadColumn no2TheadColumn=new TheadColumn("no2" , "yhw" ,"no2","二氧化氮" );
        TheadColumn o3TheadColumn=new TheadColumn("o3" , "yhw" ,"o3","臭氧" );

        List<TheadColumn> theadColumnList=new ArrayList<>();
        theadColumnList.add(provinceTheadColumn);
        theadColumnList.add(areaTheadColumn);
        theadColumnList.add(dtTheadColumn);
        theadColumnList.add(aqiTheadColumn);
        theadColumnList.add(aqiRangeTheadColumn);
        theadColumnList.add(qualityTheadColumn);
        theadColumnList.add(pm25TheadColumn);
        theadColumnList.add(pm10TheadColumn);
        theadColumnList.add(yhwTheadColumn);
        theadColumnList.add(so2TheadColumn);
        theadColumnList.add(coTheadColumn);
        theadColumnList.add(no2TheadColumn);
        theadColumnList.add(o3TheadColumn);


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

package com.easydate.test;

import com.easydata.enmus.TextHorizontalAlignment;
import com.easydata.head.TheadColumn;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MySQLData {


    public static List<Map<String, Object>> getDataList() {

        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String> lines = null;
        try {
            lines = FileUtils.readLines(new File("D:\\ProgramFiles\\study\\ideaworkspace\\easy-data\\docs\\manual\\test-data.txt"), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] keyArray = lines.get(0).split(",");

        Arrays.asList(keyArray).stream().forEach(System.out::println);

        int index = 0;
        for (String line : lines) {
            index++;
            if (index == 1) {
                continue;
            }


            Map<String, Object> map = new HashMap<>();
            String[] dataArray = line.split(",");

            for (int j = 0; j < keyArray.length; j++) {
                map.put(keyArray[j], dataArray[j]);
            }

            dataList.add(map);
        }

        return dataList;

    }


    /**
     * 表头列表
     * @return
     */
    public static List<TheadColumn> getTheadColumnList(){
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

        return theadColumnList;
    }

    public static void main(String[] args) {
        System.out.println(getDataList());
    }

}

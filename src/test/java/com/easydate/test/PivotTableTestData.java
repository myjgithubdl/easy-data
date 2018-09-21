package com.easydate.test;

import com.alibaba.fastjson.JSONObject;
import com.easydata.head.TheadColumn;
import com.easydata.pivottable.core.PivotTableDataCore;
import com.easydata.pivottable.domain.PivotTable;
import com.easydata.pivottable.domain.PivotTableCalCol;
import com.easydata.pivottable.enmus.AggFunc;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PivotTableTestData {


    public static List<Map<String, Object>> getData() {
        List<String> lines = null;
        try {
            lines = FileUtils.readLines(new File("E:\\Myron\\lakala\\需求文档\\数据透视表\\测试数据\\new4.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] keys = lines.get(0).split(",");
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            String[] values = lines.get(i).split(",");
            for (int j = 0; j < keys.length; j++) {
                if (j >= 4) {
                    map.put(keys[j], values[j]);
                } else {
                    map.put(keys[j], values[j]);
                }

            }
            dataList.add(map);

        }
        System.out.println(JSONObject.toJSONString(dataList));
        return dataList;
    }


    public static PivotTableDataCore getPivotTableData() {
        List<Map<String, Object>> dataList = PivotTableTestData.getData();

        PivotTable pivotTable = new PivotTable();

        List<String> rows = new ArrayList<>();
        rows.add("start_month");

        pivotTable.setRows(rows);

        List<String> clos = new ArrayList<>();
        clos.add("cls_lv2");
        //clos.add("mx");
        pivotTable.setCols(clos);

        List<PivotTableCalCol> calCols=new ArrayList<>();
        PivotTableCalCol pivotTableCalCol1=new PivotTableCalCol("value" , AggFunc.SUM);
        PivotTableCalCol pivotTableCalCol2=new PivotTableCalCol("value" , AggFunc.COUNT);
        PivotTableCalCol valueAvg=new PivotTableCalCol("value" , AggFunc.AVG);
        PivotTableCalCol valueMax=new PivotTableCalCol("value" , AggFunc.MAX);
        calCols.add(pivotTableCalCol1);
        calCols.add(pivotTableCalCol2);
        calCols.add(valueAvg);
        calCols.add(valueMax);
        pivotTable.setCalCols(calCols);


        List<TheadColumn> theadColumnList = new ArrayList<>();
        TheadColumn theadColumn1 = new TheadColumn("start_month", null, "start_month", "发放月份");
        TheadColumn theadColumn2 = new TheadColumn("cls_lv2", null, "cls_lv2", "产品分类");
        TheadColumn mobFlag = new TheadColumn("mob_flag", null, "mob_flag", "发放后第几个月");
        TheadColumn theadColumn3 = new TheadColumn("mx", null, "mx", "逾期阶段");
        TheadColumn value = new TheadColumn("value", null, "value", "本金余额");
        value.setPrecision(0);
        value.setDefaultValue(0);

        TheadColumn cnt = new TheadColumn("cnt", null, "cnt", "合同笔数");
        cnt.setPrecision(0);
        cnt.setDefaultValue(0);

        theadColumnList.add(theadColumn1);
        theadColumnList.add(theadColumn2);
        theadColumnList.add(mobFlag);
        theadColumnList.add(theadColumn3);
        theadColumnList.add(value);
        theadColumnList.add(cnt);

        PivotTableDataCore pivotTableDataServer = new PivotTableDataCore(pivotTable, theadColumnList, dataList);


        return pivotTableDataServer;

    }


}

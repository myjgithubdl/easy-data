package com.easydate.test;

import com.easydata.head.TheadColumn;
import com.easydata.pivottable.PivotTableDataUtil;
import com.easydata.pivottable.domain.PivotTableCalCol;
import com.easydata.pivottable.enmus.AggFunc;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PivotTableTest {

    public static void main(String[] args) {
        test();
    }


    public static void test() {
        //数据
        List<Map<String, Object>> dataList = MySQLData.getDataList();
        //表头列表
        List<TheadColumn> theadColumnList = MySQLData.getTheadColumnList();
        OutputStream outExcel = null;
        OutputStream outCSV = null;
        try {
            outExcel = new FileOutputStream("E:\\Myron\\天气数据-透视表.xlsx");
            outCSV = new FileOutputStream("E:\\Myron\\天气数据-透视表.csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //行
        List<String> rows = new ArrayList<>();
        rows.add("province");
        rows.add("area");

        //列
        List<String> cols = new ArrayList<>();
        cols.add("quality");

        //计算函数
        List<PivotTableCalCol> calCols = new ArrayList<>();
        AggFunc max = AggFunc.MAX;
        max.setText("pm25最大值");
        PivotTableCalCol pivotTableCalColpm25Max = new PivotTableCalCol("pm25", max);

        AggFunc min = AggFunc.MIN;
        min.setText("pm25最小值");
        PivotTableCalCol pivotTableCalColpm25MIn = new PivotTableCalCol("pm25", min);

        AggFunc avg = AggFunc.AVG;
        avg.setText("pm25均值");
        PivotTableCalCol pivotTableCalColpm25AVG = new PivotTableCalCol("pm25",avg);
        calCols.add(pivotTableCalColpm25Max);
        calCols.add(pivotTableCalColpm25MIn);
        calCols.add(pivotTableCalColpm25AVG);

        //导出CSV
        PivotTableDataUtil.exportPivotTableDataCsvFile(rows, cols, calCols, theadColumnList, dataList, outCSV);

        //导出Excel
        PivotTableDataUtil.exportPivotTableDataExcelFile(rows, cols, calCols, theadColumnList, dataList,"天气数据", outExcel);

    }

}

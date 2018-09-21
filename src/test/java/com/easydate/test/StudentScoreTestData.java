package com.easydate.test;

import com.easydata.head.TheadColumn;
import com.easydata.pivottable.core.PivotTableDataCore;
import com.easydata.pivottable.domain.PivotTable;
import com.easydata.pivottable.domain.PivotTableCalCol;
import com.easydata.pivottable.enmus.AggFunc;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class StudentScoreTestData {

    List<Map<String, Object>> dataList;

    List<TheadColumn> theadColumnList;

    public StudentScoreTestData() {
        this.getData();
    }

    private void getData() {
        List<String> lines = null;
        try {
            URL url3 = StudentScoreTestData.class.getResource("/student_score.txt");
            String path = url3.getPath();
            lines = FileUtils.readLines(new File(path.substring(1)));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] keys = lines.get(0).split(",");

        List<TheadColumn> theadColumnList = new ArrayList<>();
        TheadColumn year = new TheadColumn("year", null, "year", "年份");
        TheadColumn semester = new TheadColumn("semester", null, "semester", "学期");
        TheadColumn student_name = new TheadColumn("student_name", null, "student_name", "姓名");
        TheadColumn course = new TheadColumn("course", null, "course", "课程");
        TheadColumn score = new TheadColumn("score", null, "score", "成绩");
        score.setPrecision(0);

        theadColumnList.add(year);
        theadColumnList.add(semester);
        theadColumnList.add(student_name);
        theadColumnList.add(course);
        theadColumnList.add(score);
        this.theadColumnList = theadColumnList;

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            String[] values = lines.get(i).split(",");
            for (int j = 0; j < keys.length; j++) {
                map.put(keys[j], values[j]);
            }
            dataList.add(map);

        }


        this.dataList = dataList;
    }


    public PivotTableDataCore getPivotTableData() {

        PivotTable pivotTable = new PivotTable();

        List<String> rows = new ArrayList<>();
        rows.add("year");
        rows.add("student_name");

        pivotTable.setRows(rows);

        List<String> clos = new ArrayList<>();
        clos.add("semester");
        //clos.add("course");
        pivotTable.setCols(clos);

        List<PivotTableCalCol> calCols = new ArrayList<>();
        PivotTableCalCol valueAvg = new PivotTableCalCol("score", AggFunc.AVG);
        PivotTableCalCol valueMax = new PivotTableCalCol("score", AggFunc.MAX);
        PivotTableCalCol valueMin = new PivotTableCalCol("score", AggFunc.MIN);
        calCols.add(valueAvg);
        calCols.add(valueMax);
        calCols.add(valueMin);

        pivotTable.setCalCols(calCols);

        PivotTableDataCore pivotTableDataServer = new PivotTableDataCore(pivotTable, theadColumnList, dataList);
        return pivotTableDataServer;

    }

}

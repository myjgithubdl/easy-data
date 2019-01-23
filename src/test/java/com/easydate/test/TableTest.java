package com.easydate.test;

import com.easydata.head.TheadColumn;
import com.easydata.html.HtmlTableUtil;

import java.util.List;
import java.util.Map;

public class TableTest {

    public static void main(String[] args) {
        //数据
        List<Map<String, Object>> dataList = MySQLData.getDataList();
        //表头列表
        List<TheadColumn> theadColumnList = MySQLData.getTheadColumnList();

        String table=HtmlTableUtil.getHtmlTable(theadColumnList ,dataList );
        System.out.println(table);
    }


}

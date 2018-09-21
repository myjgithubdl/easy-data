package com.easydata.export.excel;

import com.alibaba.fastjson.JSONObject;
import com.easydata.head.TheadColumn;
import com.easydata.enmus.ExcelType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Created by MYJ on 2018/1/26.
 */
@Getter
@Setter
public class ExportExcelParams {

    /**
     * 导出Excel的Sheet名称
     */
    private String sheetName;

    /**
     * 导出Excel的类型
     */
    private ExcelType type=ExcelType.XSSF;


    /**
     * 导出的Excel表头  List 类型
     */
    private List<TheadColumn> theadColumnList;

    /**
     *导出的Excel表头  json数组的字符串类型（先查找theadColumnList，如果为空在检查该字符串）
     */
    private String theadColumnStrs;

    /**
     * 导出的Excel数据
     */
    private List<Map<String, Object>> dataList;

    /**
     * 自定义的单元格样式
     */
    private List<ExcelCellStyle> excelCellStyleList;

    public List<TheadColumn> getTheadColumnList() {
        if(theadColumnList != null && theadColumnList.size() > 0){
            return theadColumnList;
        }else{
            if(theadColumnStrs != null && theadColumnStrs.trim().length() > 0 ){
                theadColumnList=JSONObject.parseArray(theadColumnStrs ,TheadColumn.class );
            }
        }
        return theadColumnList;
    }
}

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
     * 原始Excel表头  List 类型
     */
    private List<TheadColumn> theadColumnList;

    /**
     * 导出的Excel数据
     */
    private List<Map<String, Object>> dataList;

    /**
     * 自定义的单元格样式
     */
    private List<ExcelCellStyle> excelCellStyleList;

}

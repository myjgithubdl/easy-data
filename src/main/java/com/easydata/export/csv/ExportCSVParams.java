package com.easydata.export.csv;

import com.alibaba.fastjson.JSONObject;
import com.easydata.head.TheadColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Created by MYJ on 2018/2/1.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExportCSVParams {

    /**
     * 是否导出表头
     */
    private boolean exportThead = true;


    /**
     * 导出的Excel表头  List 类型
     */
    private List<TheadColumn> theadColumnList;

    /**
     * 导出的Excel数据
     */
    private List<Map<String, Object>> dataList;

    /**
     * 导出的文件编码
     */
    private String charsetName = "UTF-8";

    public ExportCSVParams(List<TheadColumn> theadColumnList, List<Map<String, Object>> dataList) {
        this.theadColumnList = theadColumnList;
        this.dataList = dataList;
    }


}

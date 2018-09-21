package com.easydata.pivottable.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 透视表信息
 */
@Setter
@Getter
public class PivotTable {


    /**
     * 透视表中的选择的行名称集
     */
    List<String> rows;

    /**
     * 透视表中的选择的列名称集
     */
    List<String> cols;

    /**
     * 透视表中的选择的值集,计算列和计算的函数
     */
    List<PivotTableCalCol> calCols;

    public PivotTable() {
    }

    public PivotTable(List<String> rows, List<String> cols, List<PivotTableCalCol> calCols) {
        this.rows = rows;
        this.cols = cols;
        this.calCols = calCols;
    }
}

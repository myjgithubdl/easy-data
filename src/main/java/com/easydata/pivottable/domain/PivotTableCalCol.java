package com.easydata.pivottable.domain;

import com.easydata.pivottable.enmus.AggFunc;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 透视表中选择的值列与值的聚合函数
 *
 * 对应于Excel 透视表中的选择值
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PivotTableCalCol {

    /**
     * 值列名
     */
    private String name;


    /**
     * 计算该列需要使用的聚合函数
     */
    private AggFunc aggFunc;


}

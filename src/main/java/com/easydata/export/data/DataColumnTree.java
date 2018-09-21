package com.easydata.export.data;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by MYJ on 2017/6/13.
 */
@Getter
@Setter
public class DataColumnTree extends  DataColumn {

    /**
     * 行索引
     */
    private int rowIndex ;

    /**
     * 跨行数
     */
    private Integer rowspan = 1;

    /**
     * 跨列数
     */
    private Integer colspan = 1;

    /**
     * 是否隐藏（如果被跨行、跨列即设置为true）
     */
    private boolean isHidden = false;


}

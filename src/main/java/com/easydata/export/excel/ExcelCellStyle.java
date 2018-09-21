package com.easydata.export.excel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * 指定单元格的样式
 * Created by MYJ on 2018/1/31.
 */
@Getter
@Setter
@AllArgsConstructor
public class ExcelCellStyle {

    /**
     * 单元格的行索引（如果列为空代表整行）
     */
    private Integer rowIndex;

    /**
     * 单元格列索引(如果行索引为空代表整列)
     */
    private Integer cellIndex;

    /**
     * 行高
     */
    private Double rowHeight;

    /**
     * 列的背景色  如：FFFFFFF
     */
    private String bgColor;

    /**
     * 字体颜色  如：FFFFFFF
     */
    private String fontColor;

    /**
     * 水平对齐方式
     */
    private HorizontalAlignment textAlign;

    /**
     * 文字的垂直对齐方式
     */
    private VerticalAlignment verticalAlign;

    /**
     * 是否加粗
     */
    private boolean bold;

}

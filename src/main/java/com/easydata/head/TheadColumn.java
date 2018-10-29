package com.easydata.head;

/**
 * Created by MYJ on 2017/6/9.
 */

import com.easydata.enmus.DataType;
import com.easydata.enmus.TextHorizontalAlignment;
import com.easydata.enmus.TextVerticalAlignment;
import lombok.Getter;
import lombok.Setter;

/**
 * 表头列
 */
@Getter
@Setter
public class TheadColumn {
    /**
     * 编号
     */
    private String id;

    /**
     * 父级编号
     */
    private String pid;

    /**
     * 列名
     */
    private String name;

    /**
     * 列标题
     */
    private String text;

    /**
     * 当数据中对应的值为空用该值代替该字段的值
     */
    private Object defaultValue;

    /**
     * 精度
     * 用于double类型的数值保留小数位数
     */
    private Integer precision;

    /**
     * 数据类型
     */
    private DataType dataType;

    /**
     * 是否隐藏
     */
    private boolean isHidden = false;

    /**
     * 如果列值相与下一列值相等，是否向下合并单元格
     */
    private boolean downMergeCells = false;

    /**
     * 列宽度
     */
    private Double columnWidth;

    /**
     * 表头行高度
     */
    private Double theadRowHeight;

    /**
     * 数据行高度
     */
    private Double dataRowHeight;

    /**
     * 表头列的背景色  如：FFFFFFF
     */
    private String theadBGColor;

    /**
     * 数据列的背景色  如：FFFFFFF
     */
    private String dataBGColor;

    /**
     * 表头字体颜色  如：FFFFFFF
     */
    private String theadFontColor;

    /**
     * 数据字体颜色  如：FFFFFFF
     */
    private String dataFontColor;

    /**
     * 表头部分对齐方式
     * left,center,right
     */
    private TextHorizontalAlignment theadTextAlign;

    /**
     * 数据部分列对齐方式
     * left,center,right
     */
    private TextHorizontalAlignment dataTextAlign;

    /**
     * 表头文字的垂直对齐
     */
    private TextVerticalAlignment theadVerticalAlign;

    /**
     * 数据文字的垂直对齐
     */
    private TextVerticalAlignment dataVerticalAlign;

    /**
     * 表头是否加粗
     */
    private boolean theadBold;

    /**
     * 数据是否加粗
     */
    private boolean dataBold;

    /**
     * 是否在尾部追加统计值
     */
    private boolean isStatistics;


    public TheadColumn() {
    }

    public TheadColumn(String id, String pid, String name, String text) {
        this.id = id;
        this.pid = pid;
        this.name = name;
        this.text = text;
    }


    public TheadColumn(String id, String pid, String name, String text, DataType dataType, boolean isHidden, boolean downMergeCells) {
        this.id = id;
        this.pid = pid;
        this.name = name;
        this.text = text;
        this.dataType = dataType;
        this.isHidden = isHidden;
        this.downMergeCells = downMergeCells;
    }

    public TheadColumn(String id, String pid, String name, String text, Object defaultValue, Integer precision, DataType dataType, boolean isHidden, boolean downMergeCells) {
        this.id = id;
        this.pid = pid;
        this.name = name;
        this.text = text;
        this.defaultValue = defaultValue;
        this.precision = precision;
        this.dataType = dataType;
        this.isHidden = isHidden;
        this.downMergeCells = downMergeCells;
    }

    public TheadColumn(String id, String pid, String name, String text, boolean downMergeCells, TextHorizontalAlignment theadTextAlign, TextHorizontalAlignment dataTextAlign, TextVerticalAlignment theadVerticalAlign, TextVerticalAlignment dataVerticalAlign) {
        this.id = id;
        this.pid = pid;
        this.name = name;
        this.text = text;
        this.downMergeCells = downMergeCells;
        this.theadTextAlign = theadTextAlign;
        this.dataTextAlign = dataTextAlign;
        this.theadVerticalAlign = theadVerticalAlign;
        this.dataVerticalAlign = dataVerticalAlign;
    }
}

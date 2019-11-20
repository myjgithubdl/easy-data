package com.easydata.head;

/**
 * Created by MYJ on 2017/6/9.
 */

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
     * 元数据列类型，0：普通列，1：布局列，2：维度列,3：统计列
     */
    private String type;


    /**
     * 精度
     * 用于double类型的数值保留小数位数
     */
    private Integer decimals;
    /**
     * 显示精度
     */
    private Integer showDecimals;

    /**
     * 导出精度
     */
    private Integer exportDecimals;

    /**
     * 数据类型
     */
    //private DataType dataType;
    private String dataType;

    /**
     * 是否隐藏
     */
    private boolean hidden = false;

    /**
     * 如果列值相与下一列值相等，是否向下合并单元格
     */
    private boolean downMergeCells = false;

    /**
     * 列宽度
     */
    private Integer width;

    /**
     * 表头行高度
     */
    private Integer theadRowHeight;

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
    //private TextHorizontalAlignment theadTextAlign;
    private String theadTextAlign;

    /**
     * 数据部分列对齐方式
     * left,center,right
     */
    //private TextHorizontalAlignment dataTextAlign;
    private String dataTextAlign;

    /**
     * 表头文字的垂直对齐
     */
    //private TextVerticalAlignment theadVerticalAlign;
    private String theadVerticalAlign;

    /**
     * 数据文字的垂直对齐
     */
    //private TextVerticalAlignment dataVerticalAlign;
    private String dataVerticalAlign;

    /**
     * 表头是否加粗
     */
    private String theadFontWeight;

    /**
     * 数据是否加粗
     */
    private String dataFontWeight;


    /**
     * （一般是生成HTML时有效）
     * 列的连接地址  支持${paramName}或#{paramName}写法，会用该数据航的值替代
     * 如：https://www.baidu.com?name=${name}&v=${version}
     */
    private String href;


    /**
     * 连接的打开方式，在设置了href有效（一般是生成HTML时有效）
     * 值如下
     * _blank：在新窗口中打开被链接文档。
     * _self：默认。在相同的框架中打开被链接文档。
     * _parent：在父框架集中打开被链接文档。
     * _top：在整个窗口中打开被链接文档。
     * framename：在指定的框架中打开被链接文档。
     */
    private String hrefTarget;


    /**
     * 列是否支持排序
     */
    private boolean sort;


    public TheadColumn() {
    }

    public TheadColumn(String id, String pid, String name, String text) {
        this.id = id;
        this.pid = pid;
        this.name = name;
        this.text = text;
    }


    public TheadColumn(String id, String pid, String name, String text, String dataType, boolean hidden, boolean downMergeCells) {
        this.id = id;
        this.pid = pid;
        this.name = name;
        this.text = text;
        this.dataType = dataType;
        this.hidden = hidden;
        this.downMergeCells = downMergeCells;
    }

    public TheadColumn(String id, String pid, String name, String text, Object defaultValue, Integer decimals, String dataType, boolean hidden, boolean downMergeCells) {
        this.id = id;
        this.pid = pid;
        this.name = name;
        this.text = text;
        this.defaultValue = defaultValue;
        this.decimals = decimals;
        this.dataType = dataType;
        this.hidden = hidden;
        this.downMergeCells = downMergeCells;
    }

    public TheadColumn(String id, String pid, String name, String text, boolean downMergeCells, String theadTextAlign, String dataTextAlign, String theadVerticalAlign, String dataVerticalAlign) {
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

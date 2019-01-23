package com.easydata.pivottable.enmus;

/**
 * 透视表选择的列计算函数
 */
public enum AggFunc {
    /**
     * 汇总
     */
    SUM("求和"),
    /**
     * 计数
     */
    COUNT("计数"),
    /**
     * 求均值
     */
    AVG("均值"),
    /**
     * 求最大值
     */
    MAX("最大值"),
    /**
     * 求最小值
     */
    MIN("最小值"),

    /**
     * 乘积
     */
    PRODUCT("乘积");

    // 成员变量
    private String text;


    AggFunc(String text) {
        this.text = text;
    }


    public String getText() {
        return this.text;
    }


    public void setText(String text) {
        this.text = text;
    }

    public static AggFunc getAggFunc(String fun) {
        if (AggFunc.SUM.toString().equals(fun)) {
            return AggFunc.SUM;
        } else if (AggFunc.COUNT.toString().equals(fun)) {
            return AggFunc.COUNT;
        } else if (AggFunc.AVG.toString().equals(fun)) {
            return AggFunc.AVG;
        } else if (AggFunc.MAX.toString().equals(fun)) {
            return AggFunc.MAX;
        } else if (AggFunc.MIN.toString().equals(fun)) {
            return AggFunc.MIN;
        }

        return AggFunc.SUM;
    }

}

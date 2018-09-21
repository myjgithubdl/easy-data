package com.easydata.enmus;

/**
 * Created by MYJ on 2017/7/14.
 */

/**
 * 单元格水平方向的文字对齐方式
 */
public enum TextHorizontalAlignment {
    GENERAL("general"),
    LEFT("left"),
    CENTER("center"),
    RIGHT("right");



    private String value;

    private TextHorizontalAlignment(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

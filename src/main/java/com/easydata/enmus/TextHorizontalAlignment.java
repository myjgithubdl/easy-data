package com.easydata.enmus;

/**
 * Created by MYJ on 2017/7/14.
 */

/**
 * 单元格水平方向的文字对齐方式
 */
public enum TextHorizontalAlignment {

    GENERAL("正常"),
    LEFT("左对齐"),
    CENTER("居中对齐"),
    RIGHT("右对齐"),
    /*FILL(""),
    JUSTIFY(""),
    CENTER_SELECTION(""),
    DISTRIBUTED("")*/;


    private String text;

    private TextHorizontalAlignment(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

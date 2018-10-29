package com.easydata.enmus;

/**
 * 文字的垂直方向对齐方式
 */
public enum TextVerticalAlignment {

    TOP("顶端对齐"),
    CENTER("垂直居中"),
    BOTTOM("底端对齐"),
    /*JUSTIFY,
    DISTRIBUTED*/;

    private String text;

    TextVerticalAlignment(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}

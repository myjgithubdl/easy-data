package com.easydata.enmus;

/**
 * Created by MYJ on 2017/7/14.
 */

/**
 * 数据类型的枚举值
 */
public enum DataType {

    INT("int"),
    DOUBLE("double"),
    STRING("string"),
    BOOLEAN("boolean")

    ;

    private String value;

    private DataType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

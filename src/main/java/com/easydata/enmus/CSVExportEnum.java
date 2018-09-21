package com.easydata.enmus;

/**
 * Created by MYJ on 2018/2/1.
 */
public enum CSVExportEnum {

    PARAMETER_ERROR("CSV 导出   参数错误"),
    EXPORT_ERROR("CSV导出错误");

    private String msg;

    private CSVExportEnum(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

package com.easydata.exception;


import com.easydata.enmus.ExcelExportEnum;

/**
 * Created by MYJ on 2018/1/26.
 */
public class ExcelExportException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private ExcelExportEnum type;

    public ExcelExportException() {
    }

    public ExcelExportException(ExcelExportEnum type) {
        super(type.getMsg());
        this.type = type;
    }

    public ExcelExportException(ExcelExportEnum type, Throwable cause) {
        super(type.getMsg(), cause);
    }

    public ExcelExportException(String message) {
        super(message);
    }

    public ExcelExportException(String message, ExcelExportEnum type) {
        super(message);
        this.type = type;
    }

    public ExcelExportEnum getType() {
        return this.type;
    }

    public void setType(ExcelExportEnum type) {
        this.type = type;
    }
}

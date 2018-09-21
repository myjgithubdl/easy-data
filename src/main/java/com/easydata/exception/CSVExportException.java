package com.easydata.exception;


import com.easydata.enmus.CSVExportEnum;

/**
 * Created by MYJ on 2018/2/1.
 */
public class CSVExportException extends RuntimeException {


    private CSVExportEnum type;

    public CSVExportException() {
    }

    public CSVExportException(CSVExportEnum type) {
        super(type.getMsg());
        this.type = type;
    }

    public CSVExportException(CSVExportEnum type, Throwable cause) {
        super(type.getMsg(), cause);
    }

    public CSVExportException(String message) {
        super(message);
    }

    public CSVExportException(String message, CSVExportEnum type) {
        super(message);
        this.type = type;
    }

    public CSVExportEnum getType() {
        return this.type;
    }

    public void setType(CSVExportEnum type) {
        this.type = type;
    }


}

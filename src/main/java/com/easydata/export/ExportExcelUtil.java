package com.easydata.export;

/**
 * Created by MYJ on 2017/6/14.
 */

import com.easydata.export.excel.ExportExcelCore;
import com.easydata.export.excel.ExportExcelParams;
import com.easydata.head.TheadColumn;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * 导出Excel帮助类
 */
public class ExportExcelUtil {

    /**
     * 根据导出的Excel信息、表头、数据得到Excel的Workbook对象
     *
     * @param exportParams
     * @return
     */
    public static Workbook exportExcel(ExportExcelParams exportParams) {
        ExportExcelCore exportExcelCore = new ExportExcelCore(exportParams);
        Workbook workbook = exportExcelCore.getWorkbook();
        return workbook;
    }

    /**
     * web端导出Excel文件
     *
     * @param fileName     导出文件名
     * @param exportParams Excel导出参数
     */
    public static void exportExcel(HttpServletResponse response, String fileName,
                                   ExportExcelParams exportParams) {
        if (exportParams.getTheadColumnList() == null || exportParams.getTheadColumnList().size() < 1)
            return;

        fileName = fileName == null ? "导出文件" : fileName.replaceAll(" ", "");
        try (OutputStream out = response.getOutputStream()) {
            fileName = new String(fileName.getBytes(), "ISO8859-1") + ".xlsx";
            response.reset();
            response.setHeader("Content-Disposition", String.format("attachment; filename=%s", fileName));
            response.setContentType("application/vnd.ms-excel; charset=UTF-8");
            response.addCookie(new Cookie("fileDownload", "true"));

            Cookie fileDownload = new Cookie("fileDownload", "true");
            fileDownload.setPath("/");
            response.addCookie(fileDownload);

            Workbook workbook = exportExcel(exportParams);
            workbook.write(out);
            out.flush();
            out.close();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 导出Excel文件
     *
     * @param out          输出流
     * @param exportParams Excel导出参数信息
     */
    public static void exportExcel(OutputStream out, ExportExcelParams exportParams) {
        if (exportParams.getTheadColumnList() == null || exportParams.getTheadColumnList().size() < 1)
            return;

        Workbook workbook = exportExcel(exportParams);
        try {
            workbook.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void exportExcel(OutputStream out,
                                   String sheetName,
                                   List<TheadColumn> theadColumnList,
                                   List<Map<String,Object>> dataList) {
        ExportExcelParams exportParams =new ExportExcelParams();
        exportParams.setTheadColumnList(theadColumnList);
        exportParams.setDataList(dataList);
        exportParams.setSheetName(sheetName);
        ExportExcelUtil.exportExcel(out , exportParams);
    }
}

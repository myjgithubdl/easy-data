package com.easydata.export;

import com.easydata.export.csv.ExportCSVCore;
import com.easydata.export.csv.ExportCSVParams;
import com.easydata.head.TheadColumn;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;

/**
 * 导出CSV文件工具类
 */
public class ExportCSVUtil {

    /**
     * 导出CSV
     *
     * @param response
     * @param fileName
     * @param exportCSVParams
     */
    public static void exportCSV(HttpServletResponse response, String fileName, ExportCSVParams exportCSVParams) {
        exportCSV(response, fileName, exportCSVParams.getCharsetName(), exportCSVParams.getTheadColumnList(), exportCSVParams.getDataList());
    }

    /**
     * 适用于web上导出
     *
     * @param response
     * @param fileName
     * @param theadColumnList
     * @param dataList
     */
    public static void exportCSV(HttpServletResponse response, String fileName, String
            charsetName, List<TheadColumn> theadColumnList, List<Map<String, Object>> dataList) {

        fileName = fileName == null ? "导出文件" : fileName.replaceAll(" ", "");
        try (OutputStream out = response.getOutputStream()) {
            fileName = new String(fileName.getBytes(), "ISO8859-1") + ".csv";
            response.reset();
            response.setHeader("Content-Disposition", String.format("attachment; filename=%s", fileName));
            response.setContentType("text/csv; charset=" + charsetName);
            response.addCookie(new Cookie("fileDownload", "true"));

            Cookie fileDownload = new Cookie("fileDownload", "true");
            fileDownload.setPath("/");
            response.addCookie(fileDownload);

            ExportCSVParams exportCSVParams = new ExportCSVParams(theadColumnList, dataList);
            String fileContent = ExportCSVCore.getCSVContent(exportCSVParams);

            OutputStreamWriter outWrite = new OutputStreamWriter(out, charsetName);
            outWrite.write(fileContent);
            outWrite.flush();
            outWrite.close();

            out.flush();
            out.close();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    /**
     * 导出CSV文件（适用于导出到本地）
     *
     * @param out
     * @param theadColumnList
     * @param dataList
     */
    public static void exportCSV(OutputStream
                                         out, List<TheadColumn> theadColumnList, List<Map<String, Object>> dataList) {

        ExportCSVParams exportCSVParams = new ExportCSVParams(theadColumnList, dataList);

        String charsetName = exportCSVParams.getCharsetName();
        if (charsetName == null || charsetName.length() < 1) {
            charsetName = "UTF-8";
        }

        String fileContent = ExportCSVCore.getCSVContent(exportCSVParams);
        try {
            OutputStreamWriter outWrite = new OutputStreamWriter(out, charsetName);
            outWrite.write(fileContent);
            outWrite.flush();
            outWrite.close();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

    }

}

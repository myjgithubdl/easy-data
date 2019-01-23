package com.easydata.pivottable;

import com.easydata.export.ExportCSVUtil;
import com.easydata.export.ExportExcelUtil;
import com.easydata.export.excel.ExportExcelParams;
import com.easydata.head.TheadColumn;
import com.easydata.pivottable.core.PivotTableDataCore;
import com.easydata.pivottable.domain.PivotTable;
import com.easydata.pivottable.domain.PivotTableCalCol;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * 透视表工具类
 */
public class PivotTableDataUtil {


    /**
     * 将数据转为透视表数据
     *
     * @param rows
     * @param cols
     * @param calCols
     * @param theadColumnList
     * @param dataList
     * @return
     */
    public static PivotTableDataCore getPivotTableData(List<String> rows,
                                                       List<String> cols,
                                                       List<PivotTableCalCol> calCols,
                                                       List<TheadColumn> theadColumnList,
                                                       List<Map<String, Object>> dataList) {

        PivotTable pivotTable = new PivotTable(rows, cols, calCols);
        PivotTableDataCore pivotTableDataServer = new PivotTableDataCore(pivotTable, theadColumnList, dataList);

        return pivotTableDataServer;
    }


    /**
     * 将数据转为透视表数据并导出为CSV文件（用于web上导出）
     *
     * @param rows
     * @param cols
     * @param calCols
     * @param theadColumnList
     * @param dataList
     * @param fileName
     * @param response
     */
    public static void exportPivotTableDataCsvFile(List<String> rows,
                                                   List<String> cols,
                                                   List<PivotTableCalCol> calCols,
                                                   List<TheadColumn> theadColumnList,
                                                   List<Map<String, Object>> dataList,
                                                   String fileName,
                                                   String charsetName,
                                                   HttpServletResponse response) {
        PivotTableDataCore pivotTableData = PivotTableDataUtil.getPivotTableData(rows, cols, calCols, theadColumnList, dataList);

        List<TheadColumn> pivotTableTheadColumnList = pivotTableData.getPivotTableTheadColumnList();
        List<Map<String, Object>> pivotTableDataList = pivotTableData.getPivotTableDataList();


        ExportCSVUtil.exportCSV(response, fileName, charsetName, pivotTableTheadColumnList, pivotTableDataList);

    }

    /**
     * 将数据转为透视表数据并导出为CSV文件（用于导出到本地）
     *
     * @param rows
     * @param cols
     * @param calCols
     * @param theadColumnList
     * @param dataList
     * @param out
     */
    public static void exportPivotTableDataCsvFile(List<String> rows,
                                                   List<String> cols,
                                                   List<PivotTableCalCol> calCols,
                                                   List<TheadColumn> theadColumnList,
                                                   List<Map<String, Object>> dataList,
                                                   OutputStream out) {
        PivotTableDataCore pivotTableData = PivotTableDataUtil.getPivotTableData(rows, cols, calCols, theadColumnList, dataList);

        List<TheadColumn> pivotTableTheadColumnList = pivotTableData.getPivotTableTheadColumnList();
        List<Map<String, Object>> pivotTableDataList = pivotTableData.getPivotTableDataList();

        ExportCSVUtil.exportCSV(out, pivotTableTheadColumnList, pivotTableDataList);
    }

    /**
     * 将数据转为透视表数据并导出为CSV文件（用于导出到本地）
     *
     * @param rows
     * @param cols
     * @param calCols
     * @param theadColumnList
     * @param dataList
     * @param sheetName
     * @param out
     */

    public static void exportPivotTableDataExcelFile(List<String> rows,
                                                     List<String> cols,
                                                     List<PivotTableCalCol> calCols,
                                                     List<TheadColumn> theadColumnList,
                                                     List<Map<String, Object>> dataList,
                                                     String sheetName,
                                                     OutputStream out) {
        PivotTableDataCore pivotTableData = PivotTableDataUtil.getPivotTableData(rows, cols, calCols, theadColumnList, dataList);

        List<TheadColumn> pivotTableTheadColumnList = pivotTableData.getPivotTableTheadColumnList();
        List<Map<String, Object>> pivotTableDataList = pivotTableData.getPivotTableDataList();
        ExportExcelUtil.exportExcel(out, sheetName, pivotTableTheadColumnList, pivotTableDataList);
    }

    /**
     * 导出excel（适用于http）
     *
     * @param rows
     * @param cols
     * @param calCols
     * @param theadColumnList
     * @param dataList
     * @param fileName
     * @param sheetName
     * @param response
     */
    public static void exportPivotTableDataExcelFile(List<String> rows,
                                                     List<String> cols,
                                                     List<PivotTableCalCol> calCols,
                                                     List<TheadColumn> theadColumnList,
                                                     List<Map<String, Object>> dataList,
                                                     String fileName,
                                                     String sheetName,
                                                     HttpServletResponse response) {
        PivotTableDataCore pivotTableData = PivotTableDataUtil.getPivotTableData(rows, cols, calCols, theadColumnList, dataList);

        List<TheadColumn> pivotTableTheadColumnList = pivotTableData.getPivotTableTheadColumnList();
        List<Map<String, Object>> pivotTableDataList = pivotTableData.getPivotTableDataList();

        ExportExcelParams exportExcelParams = new ExportExcelParams();
        exportExcelParams.setSheetName(sheetName);
        exportExcelParams.setTheadColumnList(pivotTableTheadColumnList);
        exportExcelParams.setDataList(pivotTableDataList);

        ExportExcelUtil.exportExcel(response, fileName, exportExcelParams);

    }


}

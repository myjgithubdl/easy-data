package com.easydata.pivottable;

import com.easydata.export.ExportCSVUtil;
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
public class PivotTableDataServer {


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
                                                   HttpServletResponse response) {
        PivotTableDataCore pivotTableData = PivotTableDataServer.getPivotTableData(rows, cols, calCols, theadColumnList, dataList);

        List<TheadColumn> pivotTableTheadColumnList = pivotTableData.getPivotTableTheadColumnList();
        List<Map<String, Object>> pivotTableDataList = pivotTableData.getPivotTableDataList();


        ExportCSVUtil.exportCSV(response, fileName, pivotTableTheadColumnList, pivotTableDataList);

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
        PivotTableDataCore pivotTableData = PivotTableDataServer.getPivotTableData(rows, cols, calCols, theadColumnList, dataList);

        List<TheadColumn> pivotTableTheadColumnList = pivotTableData.getPivotTableTheadColumnList();
        List<Map<String, Object>> pivotTableDataList = pivotTableData.getPivotTableDataList();

        ExportCSVUtil.exportCSV(out, pivotTableTheadColumnList, pivotTableDataList);
    }


}

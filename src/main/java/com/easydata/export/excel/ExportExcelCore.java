package com.easydata.export.excel;

import com.easydata.enmus.*;
import com.easydata.head.TheadColumn;
import com.easydata.head.TheadColumnTree;
import com.easydata.head.TheadColumnTreeUtil;
import com.easydata.exception.ExcelExportException;
import com.easydata.data.DataColumnTree;
import com.easydata.data.DataColumnTreeUtil;
import lombok.Setter;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MYJ on 2018/1/26.
 */
public class ExportExcelCore {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ExportExcelCore.class);

    private static int MAX_NUM = '\uea60';

    private static int CELL_STYLES_NUM = 0;

    private Map<String, CellStyle> allCellStyleMap = new HashMap<>();

    @Setter
    private ExportExcelParams exporExcelParams;


    /**
     * 导出的Excel表头  List 类型
     */
    private List<TheadColumn> theadColumns;

    /**
     * 需要导出的数据
     */
    private List<Map<String, Object>> dataList;

    /**
     * 通过传入的表头计算得到的Excel导出的表头
     */
    private List<List<TheadColumnTree>> exportTheadColumnLists;

    /**
     * Excel导出的最后一行表头
     */
    private List<TheadColumnTree> exportLastRowTheadColumns;

    /**
     * 通过传入的数据计算得到的Excel导出表头
     */
    private List<List<DataColumnTree>> exportDataColumnLists;

    /**
     * 需要合并的单元格区域
     */
    private List<CellRangeAddress> cellRangeAddressList = new ArrayList<>();

    public ExportExcelCore(ExportExcelParams exporExcelParams) {
        this.exporExcelParams = exporExcelParams;
        this.theadColumns = exporExcelParams.getTheadColumnList();
        this.dataList = exporExcelParams.getDataList();
    }

    /**
     * 根据设置的Excel对象取得Workbook对象
     *
     * @return
     */
    public Workbook getWorkbook() {
        if (this.theadColumns == null || theadColumns.size() < 1) {
            throw new ExcelExportException("Excel导出的表头信息不能为空！");
        }

        LOGGER.info("开始计算Excel的表头部分 ");
        long startTime1 = System.currentTimeMillis();
        //将表头列转化为Excel导出需要的的表头
        List<List<TheadColumnTree>> theadColumnLists = TheadColumnTreeUtil.getFromtTheadColumnTreeList(theadColumns, false);
        List<TheadColumnTree> lastRowTheadColumns = TheadColumnTreeUtil.getLeafNodes(theadColumnLists);
        for (TheadColumnTree theadColumnTree : lastRowTheadColumns) {
            if (theadColumnTree.getName() == null || theadColumnTree.getName().trim().length() < 1) {
                throw new ExcelExportException("Excel导出的列的nama属性值不能为空！");
            }
        }
        this.exportTheadColumnLists = theadColumnLists;
        this.exportLastRowTheadColumns = lastRowTheadColumns;
        LOGGER.info("结束计算Excel的表头部分耗时：" + (System.currentTimeMillis() - startTime1) + "毫秒。");

        if (this.dataList != null && this.dataList.size() > 0) {
            LOGGER.info("开始计算Excel的数据部分 ");
            startTime1 = System.currentTimeMillis();
            //如果是SXSSFWorkbook的实例，不合并行和列，因为该对象会持久化数据到磁盘
            //if(workbook instanceof  SXSSFWorkbook){
            // for(TheadColumnTree theadColumnTree : lastRowTheadColumns){
            //     theadColumnTree.setDownMergeCells(false);
            // }
            //}
            //转换导出数据
            this.exportDataColumnLists = DataColumnTreeUtil.getExportFormatDataList(lastRowTheadColumns, this.dataList);
            LOGGER.info("结束计算Excel的数据部分，耗时：" + (System.currentTimeMillis() - startTime1) + "毫秒。");
        } else {
            LOGGER.info("导出数据部分为空！");
        }

        Workbook workbook = ExcelType.HSSF.equals(this.exporExcelParams.getType()) ?
                new HSSFWorkbook() : (this.dataList.size() <= 200000 ? new XSSFWorkbook() :
                new SXSSFWorkbook(5000));
        this.createSheet(workbook);

        return workbook;
    }

    /**
     * 写入表头部门
     *
     * @param workbook
     * @param sheet
     * @return
     */
    protected int createHeader(Workbook workbook, Sheet sheet) {
        List<List<TheadColumnTree>> theadColumnLists = this.exportTheadColumnLists;

        int rowIndex = 0;
        /*************  写表头   start  *************/
        if (theadColumnLists != null && theadColumnLists.size() > 0) {

            for (int i = 0, j = theadColumnLists.size(); i < j; i++) {
                //创建Excel行
                Row row = sheet.getRow(rowIndex);
                if (row == null) {
                    row = sheet.createRow(rowIndex);
                }
                List<TheadColumnTree> theadColumnTreeList = theadColumnLists.get(i);
                //设置行高
                short rowHeight = this.getRowHeight(theadColumnTreeList, CellStyleType.HEAD);
                if (rowHeight > 0) {
                    row.setHeightInPoints(20);
                    row.setHeight(rowHeight);
                }

                for (int m = 0, n = theadColumnTreeList.size(); m < n; m++) {
                    TheadColumnTree theadColumnTree = theadColumnTreeList.get(m);
                    int celIndex = theadColumnTree.getCelIndex();
                    int colspan = theadColumnTree.getColspan();
                    int rowspan = theadColumnTree.getRowspan();
                    String text = theadColumnTree.getText();

                    //设置列宽
                    short columnWidth = this.getColumnWidth(theadColumnTree);
                    if (columnWidth > 0) {
                        sheet.setColumnWidth(theadColumnTree.getCelIndex(), columnWidth);
                    }

                    //创建Excel列
                    Cell cell = row.createCell(celIndex);
                    cell.setCellValue(text);

                    CellStyle cellStyle = allCellStyleMap.get(getCellStyleKey(theadColumnTree, CellStyleType.HEAD));
                    cell.setCellStyle(cellStyle);

                    //设置跨行
                    if (rowspan > 1) { //合并行
                        CellRangeAddress cellRangeAddress = new CellRangeAddress(rowIndex, rowIndex + rowspan - 1, celIndex, celIndex);
                        sheet.addMergedRegion(cellRangeAddress);
                        this.setRegionStyle(workbook, sheet, cellRangeAddress, cellStyle, text);
                        //this.cellRangeAddressList.add(cellRangeAddress);
                    }

                    //设置合并列
                    if (colspan > 1) { //合并列
                        CellRangeAddress cellRangeAddress = new CellRangeAddress(rowIndex, rowIndex, celIndex, celIndex + colspan - 1);
                        sheet.addMergedRegion(cellRangeAddress);
                        setRegionStyle(workbook, sheet, cellRangeAddress, cellStyle, text);
                        //this.cellRangeAddressList.add(cellRangeAddress);
                    }
                }
                rowIndex++;
            }
        }
        return rowIndex;
    }

    /**
     * 往Sheet中写入数据部门
     *
     * @param workbook
     * @param sheet
     * @param rowIndex
     * @return
     */
    protected int createData(Workbook workbook, Sheet sheet, int rowIndex) {
        List<TheadColumnTree> lastRowTheadColumns = this.exportLastRowTheadColumns;
        List<List<DataColumnTree>> exportDataLists = this.exportDataColumnLists;
        if (exportDataLists != null && exportDataLists.size() > 0) {
            short rowHeight = this.getRowHeight(lastRowTheadColumns, CellStyleType.HEAD);
            for (int i = 0, j = exportDataLists.size(); i < j; i++) {
                if (i > 0 && i % 10000 == 0) {
                    LOGGER.info("总数据行数" + j + "，当前写入数据行数" + i + "行！");
                }
                //创建行
                Row row = sheet.getRow(rowIndex);
                if (row == null)
                    row = sheet.createRow(rowIndex);

                if (rowHeight > 0) {
                    row.setHeight(rowHeight);
                }

                List<DataColumnTree> exportDataList = exportDataLists.get(i);
                for (int m = 0, n = exportDataList.size(); m < n; m++) {//列
                    DataColumnTree dataColumnTree = exportDataList.get(m);
                    int colspan = dataColumnTree.getColspan();
                    int rowspan = dataColumnTree.getRowspan();
                    boolean isHidden = dataColumnTree.isHidden();
                    Object text = dataColumnTree.getValue() == null ? "" : dataColumnTree.getValue();

                    //if (isHidden)
                    //    continue;

                    //创建列
                    Cell cell = row.createCell(m);
                    cell.setCellValue(text + "");

                    CellStyle cellStyle = allCellStyleMap.get(getCellStyleKey(lastRowTheadColumns.get(m), CellStyleType.DATA));
                    if (cellStyle != null) {
                        cell.setCellStyle(cellStyle);
                    }

                    if (rowspan > 1) { //合并行
                        CellRangeAddress cellRangeAddress = new CellRangeAddress(rowIndex, rowIndex + rowspan - 1, m, m);
                        //sheet.addMergedRegion(cellRangeAddress);
                        //setRegionStyle(workbook, sheet, cellRangeAddress, cellStyle, text + "");
                        this.cellRangeAddressList.add(cellRangeAddress);
                    }
                    if (colspan > 1) { //合并列
                        CellRangeAddress cellRangeAddress = new CellRangeAddress(rowIndex, rowIndex, m, m + colspan - 1);
                        //sheet.addMergedRegion(cellRangeAddress);
                        //setRegionStyle(workbook, sheet, cellRangeAddress, cellStyle, text + "");
                        this.cellRangeAddressList.add(cellRangeAddress);
                    }
                }
                rowIndex++;
            }
        }
        return 0;
    }

    public void createStringCell(Row row, int index, String text, CellStyle style) {
        Cell cell = row.createCell(index);
        if (style != null && style.getDataFormat() > 0 && style.getDataFormat() < 12) {
            cell.setCellValue(Double.parseDouble(text));
            //cell.setCellType(0);
        } else {
            Object rtext;
            if (this.exporExcelParams.getType().equals(ExcelType.HSSF)) {
                rtext = new HSSFRichTextString(text);
            } else {
                rtext = new XSSFRichTextString(text);
            }

            cell.setCellValue((RichTextString) rtext);
        }

        if (style != null) {
            cell.setCellStyle(style);
        }

        //this.addStatisticsData(Integer.valueOf(index), text, entity);
    }

    public void createDoubleCell(Row row, int index, String text, CellStyle style) {
        Cell cell = row.createCell(index);
        if (text != null && text.length() > 0) {
            cell.setCellValue(Double.parseDouble(text));
        }

        cell.setCellType(0);
        if (style != null) {
            cell.setCellStyle(style);
        }

        //this.addStatisticsData(Integer.valueOf(index), text, entity);
    }

    /**
     * 设置跨行跨列的单元格边框样式显示不全的问题
     *
     * @param sheet
     * @param region
     * @param cs
     * @param cellValue 单元格的值
     */
    public static void setRegionStyle(Workbook workbook, Sheet sheet, CellRangeAddress region, CellStyle cs, String cellValue) {
        for (int i = region.getFirstRow(); i <= region.getLastRow(); i++) {
            Row row = sheet.getRow(i);
            if (row == null)
                row = sheet.createRow(i);
            for (int j = region.getFirstColumn(); j <= region.getLastColumn(); j++) {
                Cell cell = row.getCell(j);
                if (cell == null) {
                    cell = row.createCell(j);
                    cell.setCellValue(cellValue);
                }
                if (cs != null) {
                    cell.setCellStyle(cs);
                } else {
                    cs = workbook.createCellStyle();
                    cs.setVerticalAlignment(VerticalAlignment.CENTER);
                    cell.setCellStyle(cs);
                }
            }
        }
    }

    /**
     * 根据表头创建表头样式和单元格样式
     *
     * @param wb
     * @param theadColumnList
     */
    public void createAllCellStyle(Workbook wb, Sheet sheet, List<TheadColumn> theadColumnList) {
        if (wb instanceof HSSFWorkbook) {
            createHSSFWorkbookCellStyle(wb, sheet, theadColumnList);
        } else if (wb instanceof XSSFWorkbook) {
            createXSSFWorkbookCellStyle(wb, sheet, theadColumnList);
        } else if (wb instanceof SXSSFWorkbook) {
            createSXSSFWorkbookCellStyle(wb, sheet, theadColumnList);
        }
    }

    /**
     * 设置SXSSFWorkbook对应的单元格样式
     *
     * @param workbook
     * @param sheet
     * @param theadColumnList
     */
    public void createSXSSFWorkbookCellStyle(Workbook workbook, Sheet sheet, List<TheadColumn> theadColumnList) {
        if (!(workbook instanceof SXSSFWorkbook)) {
            return;
        }
        SXSSFWorkbook sxssfWorkbook = ((SXSSFWorkbook) workbook);
        //XSSFWorkbook xssfWorkbook = sxssfWorkbook.getXSSFWorkbook();
        for (TheadColumn theadColumn : theadColumnList) {
            //表头样式
            String cellHeadStyleKey = this.getCellStyleKey(theadColumn, CellStyleType.HEAD);
            if ((theadColumn.getTheadBGColor() != null && theadColumn.getTheadBGColor().trim().replaceAll("#", "").length() == 6)
                    || (theadColumn.getTheadFontColor() != null && theadColumn.getTheadFontColor().trim().replaceAll("#", "").length() == 6)
                    || theadColumn.isTheadBold()
                    || theadColumn.getTheadTextAlign() != null
                    || theadColumn.getTheadVerticalAlign() != null) {

                XSSFCellStyle xssfCellStyle = (XSSFCellStyle) sxssfWorkbook.createCellStyle();

                if (theadColumn.isTheadBold()
                        || (theadColumn.getTheadFontColor() != null && theadColumn.getTheadFontColor().trim().replaceAll("#", "").length() == 6)
                        ) {
                    XSSFFont font = (XSSFFont) sxssfWorkbook.createFont();
                    //字体颜色
                    if (theadColumn.getTheadFontColor() != null
                            && theadColumn.getTheadFontColor().trim().replaceAll("#", "").length() == 6) {
                        String theadFontColor = theadColumn.getTheadFontColor().trim().replaceAll("#", "");
                        XSSFColor fontXSSFColor = (XSSFColor) sxssfWorkbook.getCreationHelper().createExtendedColor();
                        fontXSSFColor.setARGBHex(theadFontColor);
                        font.setColor(fontXSSFColor);
                    }
                    //字体加粗
                    if (theadColumn.isTheadBold()) {
                        font.setBold(true);
                    }

                    xssfCellStyle.setFont(font);
                }

                //背景颜色
                if (theadColumn.getTheadBGColor() != null
                        && theadColumn.getTheadBGColor().trim().replaceAll("#", "").length() == 6) {
                    XSSFColor xssfColor = (XSSFColor) sxssfWorkbook.getCreationHelper().createExtendedColor();
                    xssfColor.setARGBHex(theadColumn.getTheadBGColor().trim().replaceAll("#", ""));
                    xssfCellStyle.setFillForegroundColor(xssfColor);
                    xssfCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                }

                //文字水平对齐方式
                if (theadColumn.getTheadTextAlign() != null) {
                    xssfCellStyle.setAlignment(this.getHorizontalAlignment(theadColumn.getTheadTextAlign()));
                }

                //文字垂直对齐方式
                if (theadColumn.getTheadVerticalAlign() != null) {
                    xssfCellStyle.setVerticalAlignment(this.getVerticalAlignment(theadColumn.getTheadVerticalAlign()));
                }

                allCellStyleMap.put(cellHeadStyleKey, xssfCellStyle);
            }


            //数据样式
            String cellDataStyleKey = this.getCellStyleKey(theadColumn, CellStyleType.DATA);
            if ((theadColumn.getDataBGColor() != null && theadColumn.getDataBGColor().trim().replaceAll("#", "").length() == 6)
                    || (theadColumn.getDataFontColor() != null && theadColumn.getDataFontColor().trim().replaceAll("#", "").length() == 6)
                    || theadColumn.getDataTextAlign() != null
                    || theadColumn.getDataVerticalAlign() != null
                    || theadColumn.isDataBold()) {

                XSSFCellStyle dataCellStyle = (XSSFCellStyle) sxssfWorkbook.createCellStyle();

                if (theadColumn.isDataBold()
                        || (theadColumn.getDataFontColor() != null
                        && theadColumn.getDataFontColor().trim().replaceAll("#", "").length() == 6)
                        ) {
                    XSSFFont font = (XSSFFont) sxssfWorkbook.createFont();
                    if (theadColumn.getDataFontColor() != null
                            && theadColumn.getDataFontColor().trim().replaceAll("#", "").length() == 6) {
                        String dataFontColor = theadColumn.getDataFontColor().trim().replaceAll("#", "");
                        XSSFColor fontXSSFColor = (XSSFColor) sxssfWorkbook.getCreationHelper().createExtendedColor();
                        fontXSSFColor.setARGBHex(dataFontColor);
                        font.setColor(fontXSSFColor);
                    }

                    if (theadColumn.isDataBold()) {
                        font.setBold(true);
                    }
                    dataCellStyle.setFont(font);
                }

                //文字水平对齐方式
                if (theadColumn.getDataTextAlign() != null) {
                    dataCellStyle.setAlignment(this.getHorizontalAlignment(theadColumn.getDataTextAlign()));
                }

                //文字垂直对齐方式
                if (theadColumn.getDataVerticalAlign() != null) {
                    dataCellStyle.setVerticalAlignment(this.getVerticalAlignment(theadColumn.getDataVerticalAlign()));
                }

                //背景颜色
                if (theadColumn.getDataBGColor() != null && theadColumn.getDataBGColor().trim().replaceAll("#", "").length() == 6) {
                    XSSFColor xssfColor = (XSSFColor) sxssfWorkbook.getCreationHelper().createExtendedColor();
                    xssfColor.setARGBHex(theadColumn.getDataBGColor().trim().replaceAll("#", ""));
                    dataCellStyle.setFillForegroundColor(xssfColor);
                    dataCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                }
                allCellStyleMap.put(cellDataStyleKey, dataCellStyle);
            }
        }


        //创建自定义的样式  start ****************************************
        if (this.exporExcelParams.getExcelCellStyleList() != null
                && this.exporExcelParams.getExcelCellStyleList().size() > 0) {
            for (ExcelCellStyle excelCellStyle : this.exporExcelParams.getExcelCellStyleList()) {
                String cellStyleKey = this.getCellStyleKey(excelCellStyle);
                if (cellStyleKey.length() > 0
                        && this.allCellStyleMap.get(cellStyleKey) == null) {
                    XSSFCellStyle cellStyle = (XSSFCellStyle) sxssfWorkbook.createCellStyle();
                    if (excelCellStyle.getTextAlign() != null) {
                        cellStyle.setAlignment(excelCellStyle.getTextAlign());
                    }

                    if (excelCellStyle.getVerticalAlign() != null) {
                        cellStyle.setVerticalAlignment(excelCellStyle.getVerticalAlign());
                    }
                    //字体颜色
                    if (excelCellStyle.isBold() || (excelCellStyle.getFontColor() != null
                            && excelCellStyle.getFontColor().trim().replaceAll("#", "").length() == 6)) {
                        XSSFFont font = (XSSFFont) sxssfWorkbook.createFont();
                        if (excelCellStyle.getFontColor() != null
                                && excelCellStyle.getFontColor().trim().replaceAll("#", "").length() == 6) {
                            String fontColor = excelCellStyle.getFontColor().trim().replaceAll("#", "");
                            XSSFColor fontXSSFColor = (XSSFColor) sxssfWorkbook.getCreationHelper().createExtendedColor();
                            fontXSSFColor.setARGBHex(fontColor);
                            font.setColor(fontXSSFColor);
                        }

                        if (excelCellStyle.isBold()) {
                            font.setBold(true);
                        }
                        cellStyle.setFont(font);
                    }

                    if (excelCellStyle.getBgColor() != null
                            && excelCellStyle.getBgColor().trim().replaceAll("#", "").length() == 6) {
                        String bgColor = excelCellStyle.getBgColor().trim().replaceAll("#", "");
                        XSSFColor xssfColor = (XSSFColor) sxssfWorkbook.getCreationHelper().createExtendedColor();
                        xssfColor.setARGBHex(bgColor);
                        cellStyle.setFillForegroundColor(xssfColor);
                        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    }

                    if (excelCellStyle.getRowHeight() != null && excelCellStyle.getRowHeight() > 0
                            && excelCellStyle.getRowIndex() != null && excelCellStyle.getRowIndex() > 0) {
                        Row row = sheet.getRow(excelCellStyle.getRowIndex());
                        if (row != null) {
                            row.setHeight(getRowHeight(excelCellStyle.getRowHeight()));
                        }
                    }
                    this.allCellStyleMap.put(cellStyleKey, cellStyle);
                }
            }
        }
        //创建自定义的样式  end ****************************************

    }

    /**
     * 设置XSSFWorkbook对应的单元格样式
     *
     * @param workbook
     * @param sheet
     * @param theadColumnList
     */
    public void createXSSFWorkbookCellStyle(Workbook workbook, Sheet sheet, List<TheadColumn> theadColumnList) {
        if (!(workbook instanceof XSSFWorkbook)) {
            return;
        }
        XSSFWorkbook xssfWorkbook = ((XSSFWorkbook) workbook);

        for (TheadColumn theadColumn : theadColumnList) {
            //表头样式
            String cellHeadStyleKey = this.getCellStyleKey(theadColumn, CellStyleType.HEAD);
            if ((theadColumn.getTheadBGColor() != null && theadColumn.getTheadBGColor().trim().replaceAll("#", "").length() == 6)
                    || (theadColumn.getTheadFontColor() != null && theadColumn.getTheadFontColor().trim().replaceAll("#", "").length() == 6)
                    || theadColumn.isTheadBold()
                    || theadColumn.getTheadTextAlign() != null
                    || theadColumn.getTheadVerticalAlign() != null) {

                XSSFCellStyle xssfCellStyle = xssfWorkbook.createCellStyle();

                if (theadColumn.isTheadBold()
                        || (theadColumn.getTheadFontColor() != null && theadColumn.getTheadFontColor().trim().replaceAll("#", "").length() == 6)
                        ) {
                    XSSFFont font = xssfWorkbook.createFont();
                    //字体颜色
                    if (theadColumn.getTheadFontColor() != null
                            && theadColumn.getTheadFontColor().trim().replaceAll("#", "").length() == 6) {
                        String theadFontColor = theadColumn.getTheadFontColor().trim().replaceAll("#", "");
                        XSSFColor fontXSSFColor = xssfWorkbook.getCreationHelper().createExtendedColor();
                        fontXSSFColor.setARGBHex(theadFontColor);
                        font.setColor(fontXSSFColor);
                    }
                    //字体加粗
                    if (theadColumn.isTheadBold()) {
                        font.setBold(true);
                    }

                    xssfCellStyle.setFont(font);
                }

                //背景颜色
                if (theadColumn.getTheadBGColor() != null
                        && theadColumn.getTheadBGColor().trim().replaceAll("#", "").length() == 6) {
                    XSSFColor xssfColor = xssfWorkbook.getCreationHelper().createExtendedColor();
                    xssfColor.setARGBHex(theadColumn.getTheadBGColor().trim().replaceAll("#", ""));
                    xssfCellStyle.setFillForegroundColor(xssfColor);
                    xssfCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                }

                //文字水平对齐方式
                if (theadColumn.getTheadTextAlign() != null) {
                    xssfCellStyle.setAlignment(this.getHorizontalAlignment(theadColumn.getTheadTextAlign()));
                }

                //文字垂直对齐方式
                if (theadColumn.getTheadVerticalAlign() != null) {
                    xssfCellStyle.setVerticalAlignment(this.getVerticalAlignment(theadColumn.getTheadVerticalAlign()));
                }

                allCellStyleMap.put(cellHeadStyleKey, xssfCellStyle);
            }


            //数据样式
            String cellDataStyleKey = this.getCellStyleKey(theadColumn, CellStyleType.DATA);
            if ((theadColumn.getDataBGColor() != null && theadColumn.getDataBGColor().trim().replaceAll("#", "").length() == 6)
                    || (theadColumn.getDataFontColor() != null && theadColumn.getDataFontColor().trim().replaceAll("#", "").length() == 6)
                    || theadColumn.getDataTextAlign() != null
                    || theadColumn.getDataVerticalAlign() != null
                    || theadColumn.isDataBold()) {

                XSSFCellStyle dataCellStyle = xssfWorkbook.createCellStyle();

                if (theadColumn.isDataBold()
                        || (theadColumn.getDataFontColor() != null
                        && theadColumn.getDataFontColor().trim().replaceAll("#", "").length() == 6)
                        ) {
                    XSSFFont font = xssfWorkbook.createFont();
                    if (theadColumn.getDataFontColor() != null
                            && theadColumn.getDataFontColor().trim().replaceAll("#", "").length() == 6) {
                        String dataFontColor = theadColumn.getDataFontColor().trim().replaceAll("#", "");
                        XSSFColor fontXSSFColor = xssfWorkbook.getCreationHelper().createExtendedColor();
                        fontXSSFColor.setARGBHex(dataFontColor);
                        font.setColor(fontXSSFColor);
                    }

                    if (theadColumn.isDataBold()) {
                        font.setBold(true);
                    }
                    dataCellStyle.setFont(font);
                }

                //文字水平对齐方式
                if (theadColumn.getDataTextAlign() != null) {
                    dataCellStyle.setAlignment(this.getHorizontalAlignment(theadColumn.getDataTextAlign()));
                }

                //文字垂直对齐方式
                if (theadColumn.getDataVerticalAlign() != null) {
                    dataCellStyle.setVerticalAlignment(this.getVerticalAlignment(theadColumn.getDataVerticalAlign()));
                }

                //背景颜色
                if (theadColumn.getDataBGColor() != null && theadColumn.getDataBGColor().trim().replaceAll("#", "").length() == 6) {
                    XSSFColor xssfColor = xssfWorkbook.getCreationHelper().createExtendedColor();
                    xssfColor.setARGBHex(theadColumn.getDataBGColor().trim().replaceAll("#", ""));
                    dataCellStyle.setFillForegroundColor(xssfColor);
                    dataCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                }
                allCellStyleMap.put(cellDataStyleKey, dataCellStyle);
            }
        }

        //创建自定义的样式  start ****************************************
        if (this.exporExcelParams.getExcelCellStyleList() != null
                && this.exporExcelParams.getExcelCellStyleList().size() > 0) {
            for (ExcelCellStyle excelCellStyle : this.exporExcelParams.getExcelCellStyleList()) {
                String cellStyleKey = this.getCellStyleKey(excelCellStyle);
                if (cellStyleKey.length() > 0
                        && this.allCellStyleMap.get(cellStyleKey) == null) {
                    XSSFCellStyle cellStyle = xssfWorkbook.createCellStyle();
                    if (excelCellStyle.getTextAlign() != null) {
                        cellStyle.setAlignment(excelCellStyle.getTextAlign());
                    }

                    if (excelCellStyle.getVerticalAlign() != null) {
                        cellStyle.setVerticalAlignment(excelCellStyle.getVerticalAlign());
                    }
                    //字体颜色
                    if (excelCellStyle.isBold() || (excelCellStyle.getFontColor() != null
                            && excelCellStyle.getFontColor().trim().replaceAll("#", "").length() == 6)) {
                        XSSFFont font = xssfWorkbook.createFont();
                        if (excelCellStyle.getFontColor() != null
                                && excelCellStyle.getFontColor().trim().replaceAll("#", "").length() == 6) {
                            String fontColor = excelCellStyle.getFontColor().trim().replaceAll("#", "");
                            XSSFColor fontXSSFColor = xssfWorkbook.getCreationHelper().createExtendedColor();
                            fontXSSFColor.setARGBHex(fontColor);
                            font.setColor(fontXSSFColor);
                        }

                        if (excelCellStyle.isBold()) {
                            font.setBold(true);
                        }
                        cellStyle.setFont(font);
                    }

                    if (excelCellStyle.getBgColor() != null
                            && excelCellStyle.getBgColor().trim().replaceAll("#", "").length() == 6) {
                        String bgColor = excelCellStyle.getBgColor().trim().replaceAll("#", "");
                        XSSFColor xssfColor = xssfWorkbook.getCreationHelper().createExtendedColor();
                        xssfColor.setARGBHex(bgColor);
                        cellStyle.setFillForegroundColor(xssfColor);
                        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    }

                    if (excelCellStyle.getRowHeight() != null && excelCellStyle.getRowHeight() > 0
                            && excelCellStyle.getRowIndex() != null && excelCellStyle.getRowIndex() > 0) {
                        Row row = sheet.getRow(excelCellStyle.getRowIndex());
                        if (row != null) {
                            row.setHeight(getRowHeight(excelCellStyle.getRowHeight()));
                        }
                    }
                    this.allCellStyleMap.put(cellStyleKey, cellStyle);
                }
            }
        }
        //创建自定义的样式  end ****************************************

    }

    /**
     * 设置HSSFWorkbook对应的单元格样式
     *
     * @param workbook
     * @param sheet
     * @param theadColumnList
     */
    public void createHSSFWorkbookCellStyle(Workbook workbook, Sheet sheet, List<TheadColumn> theadColumnList) {
        if (!(workbook instanceof HSSFWorkbook)) {
            return;
        }
        HSSFWorkbook hssfWorkbook = ((HSSFWorkbook) workbook);

        //设置颜色的索引只能是 8至64，
        short colorIndex = 9;
        //保存16进制颜色值和自定义所索引的对应关系
        Map<String, Short> colorStrToIndexMap = new HashMap<>();

        for (TheadColumn theadColumn : theadColumnList) {
            //表头样式
            String cellHeadStyleKey = this.getCellStyleKey(theadColumn, CellStyleType.HEAD);
            if ((theadColumn.getTheadBGColor() != null && theadColumn.getTheadBGColor().trim().replaceAll("#", "").length() == 6)
                    || (theadColumn.getTheadFontColor() != null && theadColumn.getTheadFontColor().trim().replaceAll("#", "").length() == 6)
                    || theadColumn.getTheadTextAlign() != null
                    || theadColumn.getTheadVerticalAlign() != null
                    || theadColumn.isTheadBold()) {

                HSSFCellStyle headCellStyle = hssfWorkbook.createCellStyle();

                if (theadColumn.isTheadBold()
                        || (theadColumn.getTheadFontColor() != null && theadColumn.getTheadFontColor().trim().replaceAll("#", "").length() == 6)
                        ) {
                    HSSFFont font = hssfWorkbook.createFont();
                    if (theadColumn.getTheadFontColor() != null
                            && theadColumn.getTheadFontColor().trim().replaceAll("#", "").length() == 6) {
                        String theadFontColor = theadColumn.getTheadFontColor().trim().replaceAll("#", "");
                        //先检查颜色有没有
                        if (colorStrToIndexMap.get(theadFontColor) == null) {
                            this.createCustomPalette(hssfWorkbook, colorStrToIndexMap, colorIndex, theadFontColor);
                        }
                        font.setColor(colorStrToIndexMap.get(theadFontColor));
                        colorIndex++;
                    }


                    if (theadColumn.isTheadBold()) {
                        font.setBold(true);
                    }
                    headCellStyle.setFont(font);
                }

                //背景颜色
                if (theadColumn.getTheadBGColor() != null && theadColumn.getTheadBGColor().trim().replaceAll("#", "").length() == 6) {
                    String theadBGColor = theadColumn.getTheadBGColor().trim().replaceAll("#", "");
                    if (colorStrToIndexMap.get(theadBGColor) == null) {
                        this.createCustomPalette(hssfWorkbook, colorStrToIndexMap, colorIndex, theadBGColor);
                    }
                    headCellStyle.setFillForegroundColor(colorStrToIndexMap.get(theadBGColor));
                    headCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    colorIndex++;
                }

                //文字水平对齐方式
                if (theadColumn.getTheadTextAlign() != null) {
                    headCellStyle.setAlignment(this.getHorizontalAlignment(theadColumn.getTheadTextAlign()));
                }

                //文字垂直对齐方式
                if (theadColumn.getTheadVerticalAlign() != null) {
                    headCellStyle.setVerticalAlignment(this.getVerticalAlignment(theadColumn.getTheadVerticalAlign()));
                }

                allCellStyleMap.put(cellHeadStyleKey, headCellStyle);
            }

            //数据样式
            String cellDataStyleKey = this.getCellStyleKey(theadColumn, CellStyleType.DATA);
            if ((theadColumn.getDataBGColor() != null && theadColumn.getDataBGColor().trim().replaceAll("#", "").length() == 6)
                    || (theadColumn.getDataFontColor() != null && theadColumn.getDataFontColor().trim().replaceAll("#", "").length() == 6)
                    || theadColumn.getDataTextAlign() != null
                    || theadColumn.getDataVerticalAlign() != null
                    || theadColumn.isDataBold()) {
                CellStyle dataStyle = hssfWorkbook.createCellStyle();

                if (theadColumn.isDataBold()
                        || (theadColumn.getDataFontColor() != null && theadColumn.getDataFontColor().trim().replaceAll("#", "").length() == 6)
                        ) {
                    Font font = hssfWorkbook.createFont();

                    if (theadColumn.getDataFontColor() != null
                            && theadColumn.getDataFontColor().trim().replaceAll("#", "").length() == 6) {
                        String dataFontColor = theadColumn.getDataFontColor().trim().replaceAll("#", "");
                        //先检查颜色有没有
                        if (colorStrToIndexMap.get(dataFontColor) == null) {
                            this.createCustomPalette(hssfWorkbook, colorStrToIndexMap, colorIndex, dataFontColor);
                        }
                        font.setColor(colorStrToIndexMap.get(dataFontColor));
                        colorIndex++;
                    }

                    if (theadColumn.isDataBold()) {
                        font.setBold(true);
                    }
                    dataStyle.setFont(font);
                }

                //背景颜色
                if (theadColumn.getDataBGColor() != null && theadColumn.getDataBGColor().trim().replaceAll("#", "").length() == 6) {
                    String dataBGColor = theadColumn.getDataBGColor().trim().replaceAll("#", "");
                    if (colorStrToIndexMap.get(dataBGColor) == null) {
                        this.createCustomPalette(hssfWorkbook, colorStrToIndexMap, colorIndex, dataBGColor);
                    }
                    dataStyle.setFillForegroundColor(colorStrToIndexMap.get(dataBGColor));
                    dataStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    colorIndex++;
                }

                //文字水平对齐方式
                if (theadColumn.getDataTextAlign() != null) {
                    dataStyle.setAlignment(this.getHorizontalAlignment(theadColumn.getDataTextAlign()));
                }

                //文字垂直对齐方式
                if (theadColumn.getDataVerticalAlign() != null) {
                    dataStyle.setVerticalAlignment(this.getVerticalAlignment(theadColumn.getDataVerticalAlign()));
                }

                allCellStyleMap.put(cellDataStyleKey, dataStyle);
            }
        }

        //创建自定义的样式  start ****************************************
        if (this.exporExcelParams.getExcelCellStyleList() != null
                && this.exporExcelParams.getExcelCellStyleList().size() > 0) {
            for (ExcelCellStyle excelCellStyle : this.exporExcelParams.getExcelCellStyleList()) {
                String cellStyleKey = this.getCellStyleKey(excelCellStyle);
                if (cellStyleKey.length() > 0
                        && this.allCellStyleMap.get(cellStyleKey) == null) {
                    CellStyle dataStyle = hssfWorkbook.createCellStyle();
                    if (excelCellStyle.getTextAlign() != null) {
                        dataStyle.setAlignment(excelCellStyle.getTextAlign());
                    }

                    if (excelCellStyle.getVerticalAlign() != null) {
                        dataStyle.setVerticalAlignment(excelCellStyle.getVerticalAlign());
                    }

                    //字体颜色
                    if (excelCellStyle.isBold() || (excelCellStyle.getFontColor() != null
                            && excelCellStyle.getFontColor().trim().replaceAll("#", "").length() == 6)) {
                        Font font = hssfWorkbook.createFont();
                        if (excelCellStyle.getFontColor() != null
                                && excelCellStyle.getFontColor().trim().replaceAll("#", "").length() == 6) {
                            String fontColor = excelCellStyle.getFontColor().trim().replaceAll("#", "");
                            //先检查颜色有没有
                            if (colorStrToIndexMap.get(fontColor) == null) {
                                this.createCustomPalette(hssfWorkbook, colorStrToIndexMap, colorIndex, fontColor);
                            }
                            font.setColor(colorStrToIndexMap.get(fontColor));
                            colorIndex++;

                        }

                        if (excelCellStyle.isBold()) {
                            font.setBold(true);
                        }
                        dataStyle.setFont(font);
                    }

                    if (excelCellStyle.getBgColor() != null
                            && excelCellStyle.getBgColor().trim().replaceAll("#", "").length() == 6) {
                        String bgColor = excelCellStyle.getBgColor().trim().replaceAll("#", "");
                        if (colorStrToIndexMap.get(bgColor) == null) {
                            this.createCustomPalette(hssfWorkbook, colorStrToIndexMap, colorIndex, bgColor);
                        }
                        dataStyle.setFillForegroundColor(colorStrToIndexMap.get(bgColor));
                        dataStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        colorIndex++;
                    }

                    if (excelCellStyle.getRowHeight() != null && excelCellStyle.getRowHeight() > 0
                            && excelCellStyle.getRowIndex() != null && excelCellStyle.getRowIndex() > 0) {
                        Row row = sheet.getRow(excelCellStyle.getRowIndex());
                        if (row != null) {
                            row.setHeight(getRowHeight(excelCellStyle.getRowHeight()));
                        }
                    }
                    this.allCellStyleMap.put(cellStyleKey, dataStyle);
                }
            }
        }
        //创建自定义的样式  end ****************************************


    }

    /**
     * 创建HSSFWorkbook对应的索引
     *
     * @param workbook
     * @param colorStrToIndexMap
     * @param colorIndex
     * @param color
     * @return
     */
    public Map<String, Short> createCustomPalette(HSSFWorkbook workbook, Map<String, Short> colorStrToIndexMap,
                                                  short colorIndex, String color) {
        if (color == null || color.trim().replaceAll("#", "").length() != 6) {
            return colorStrToIndexMap;
        }
        System.out.println(color);
        ExtendedColor extendedColor = workbook.getCreationHelper().createExtendedColor();
        extendedColor.setARGBHex("FF" + color);
        byte[] rgb = extendedColor.getARGB();
        if (rgb != null) {
            HSSFPalette palette = workbook.getCustomPalette();
            palette.setColorAtIndex(colorIndex, rgb[1], rgb[2], rgb[3]);
            colorStrToIndexMap.put(color, colorIndex);
        }
        return colorStrToIndexMap;
    }

    /**
     * 是获取表头样式还是数据部分的样式  值为 "head"或"data"
     *
     * @param theadColumn
     * @param cellStyleType
     * @return
     */
    public String getCellStyleKey(TheadColumn theadColumn, CellStyleType cellStyleType) {
        return cellStyleType + "_" + theadColumn.getId() + "_" + theadColumn.getName();
    }

    /**
     * 获取自定义的样式key
     *
     * @param excelCellStyle
     * @return
     */
    public String getCellStyleKey(ExcelCellStyle excelCellStyle) {
        String cellStyleKey = "";
        if (excelCellStyle != null
                && (excelCellStyle.getRowIndex() != null || excelCellStyle.getCellIndex() != null)) {

            if (excelCellStyle.getTextAlign() != null) {
                cellStyleKey += "$textAlign_" + excelCellStyle.getTextAlign();
            }

            if (excelCellStyle.getVerticalAlign() != null) {
                cellStyleKey += "$verticalAlign_" + excelCellStyle.getVerticalAlign();
            }

            if (excelCellStyle.getFontColor() != null
                    && excelCellStyle.getFontColor().trim().replaceAll("#", "").length() == 6) {
                cellStyleKey += "$fontColor_" + excelCellStyle.getFontColor();
            }

            if (excelCellStyle.getBgColor() != null
                    && excelCellStyle.getBgColor().trim().replaceAll("#", "").length() == 6) {
                cellStyleKey += "$bgColor_" + excelCellStyle.getBgColor();
            }

            /*if(excelCellStyle.getRowHeight() != null && excelCellStyle.getRowHeight() > 0){
                cellStyleKey +="$rowHeight_"+excelCellStyle.getRowHeight();
            }*/

            if (excelCellStyle.isBold()) {
                cellStyleKey += "$bold_" + excelCellStyle.isBold();
            }

            if (excelCellStyle.getRowIndex() != null && cellStyleKey.length() > 0) {
                cellStyleKey = "$rowIndex_" + excelCellStyle.getRowIndex() + cellStyleKey;
            }
            if (excelCellStyle.getCellIndex() != null && cellStyleKey.length() > 0) {
                cellStyleKey = "$cellIndex_" + excelCellStyle.getCellIndex() + cellStyleKey;
            }
        }
        if (cellStyleKey.length() > 0) {
            return cellStyleKey.substring(1);
        }
        return cellStyleKey;
    }


    public void createSheet(Workbook workbook) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Excel version is {}", this.exporExcelParams.getType().equals(ExcelType.HSSF) ? "03" : "07");
        }

        if (workbook != null) {
            Sheet sheet;
            try {
                if (this.exporExcelParams.getSheetName() != null) {
                    sheet = workbook.createSheet(this.exporExcelParams.getSheetName());
                } else {
                    sheet = workbook.createSheet();
                }
            } catch (Exception e) {
                sheet = workbook.createSheet();
            }
            this.insertDataToSheet(workbook, sheet);
        } else {
            throw new ExcelExportException(ExcelExportEnum.PARAMETER_ERROR);
        }
    }

    /**
     * 写入表头和数据到Sheet
     *
     * @param workbook
     * @param sheet
     */
    protected void insertDataToSheet(Workbook workbook, Sheet sheet) {
        try {
            long startTime1 = System.currentTimeMillis();
            LOGGER.info("开始写入Excel的表头部分 ");
            this.createAllCellStyle(workbook, sheet, this.theadColumns);
            int rowIndex = this.createHeader(workbook, sheet);
            LOGGER.info("结束写入Excel的表头部分，耗时：" + (System.currentTimeMillis() - startTime1) + "毫秒。");


            startTime1 = System.currentTimeMillis();
            LOGGER.info("开始写入Excel的数据部分 ");
            rowIndex = this.createData(workbook, sheet, rowIndex);
            LOGGER.info("结束写入Excel的数据部分，耗时：" + (System.currentTimeMillis() - startTime1) + "毫秒。");
            if (this.cellRangeAddressList.size() > 0) {
                startTime1 = System.currentTimeMillis();
                LOGGER.info("开始合并Excel的合并部分，序号合并的区域大小为： " + this.cellRangeAddressList.size());
                for (int c = 0, cs = this.cellRangeAddressList.size(); c < cs; c++) {
                    CellRangeAddress cellRangeAddress = this.cellRangeAddressList.get(c);
                    sheet.addMergedRegion(cellRangeAddress);
                    if ((c + 1) % 1000 == 0) {
                        LOGGER.info("已合并区域个数为： " + (c + 1));
                    }
                }
                LOGGER.info("结束合并Excel的合并部分，耗时：" + (System.currentTimeMillis() - startTime1) + "毫秒。");
            }

            if (this.exporExcelParams.getExcelCellStyleList() != null
                    && this.exporExcelParams.getExcelCellStyleList().size() > 0) {
                this.setExcelCellStyle(workbook, sheet);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new ExcelExportException(ExcelExportEnum.EXPORT_ERROR, e.getCause());
        }
    }


    /**
     * 设置自定义参数中的样式
     *
     * @param workbook
     * @param sheet
     */
    private void setExcelCellStyle(Workbook workbook, Sheet sheet) {
        for (ExcelCellStyle excelCellStyle : this.exporExcelParams.getExcelCellStyleList()) {
            if (excelCellStyle == null) {
                continue;
            }
            Integer rowIndex = excelCellStyle.getRowIndex();
            if (rowIndex == null) {
                continue;
            }
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                LOGGER.info("sheet.getRow(" + rowIndex + ")：对象想为空！");
                continue;
            }
            if (excelCellStyle.getRowHeight() != null && excelCellStyle.getRowHeight() > 0) {
                row.setHeight(this.getRowHeight(excelCellStyle.getRowHeight()));
            }
            String cellStyleKey = this.getCellStyleKey(excelCellStyle);
            if (cellStyleKey.length() > 0) {
                Integer cellIndex = excelCellStyle.getCellIndex();
                if (cellIndex != null && cellIndex > 0) {
                    if (this.allCellStyleMap.get(cellStyleKey) != null) {
                        Cell cell = row.getCell(cellIndex);
                        if (cell != null) {
                            cell.setCellStyle(this.allCellStyleMap.get(cellStyleKey));
                        }
                    }
                } else {
                    short lastCellNum = row.getLastCellNum();
                    cellIndex = 0;
                    while (cellIndex <= lastCellNum) {
                        Cell cell = row.getCell(cellIndex);
                        if (cell != null) {
                            cell.setCellStyle(this.allCellStyleMap.get(cellStyleKey));
                        }
                        cellIndex++;
                    }
                }
            }
        }
    }

    /**
     * 计算Excel列的宽度
     *
     * @param theadColumnTree
     * @return
     */
    public short getColumnWidth(TheadColumnTree theadColumnTree) {
        double columnWidth = 0.0D;
        if (theadColumnTree.getColumnWidth() != null && theadColumnTree.getColumnWidth() > 0) {
            columnWidth = theadColumnTree.getColumnWidth();
        }
        //其实，这个参数的单位是1/256个字符宽度，也就是说，这里是把B列的宽度设置为了columnWidth个字符。
        return (short) ((int) (columnWidth * 256.0D));
    }

    /**
     * 计算Excel的行高
     *
     * @param theadColumnTrees
     * @param cellStyleType
     * @return
     */
    public short getRowHeight(List<TheadColumnTree> theadColumnTrees, CellStyleType cellStyleType) {
        double maxHeight = 0.0D;
        for (int i = 0; i < theadColumnTrees.size(); ++i) {
            TheadColumnTree theadColumnTree = theadColumnTrees.get(i);
            if (CellStyleType.HEAD.equals(cellStyleType)) {
                if (theadColumnTree.getTheadRowHeight() != null && theadColumnTree.getTheadRowHeight() > 0) {
                    maxHeight = maxHeight > theadColumnTree.getTheadRowHeight() ? maxHeight : theadColumnTree.getTheadRowHeight();
                }
            } else {
                if (theadColumnTree.getDataRowHeight() != null && theadColumnTree.getDataRowHeight() > 0) {
                    maxHeight = maxHeight > theadColumnTree.getDataRowHeight() ? maxHeight : theadColumnTree.getDataRowHeight();
                }
            }
        }
        return this.getRowHeight(maxHeight);
    }

    public short getRowHeight(double height) {
        short rowHeight = (short) ((int) (height * 50.0D));
        return rowHeight;
    }

    /**
     * 获取单元格水平对齐样式
     *
     * @param textHorizontalAlignment
     * @return
     */
    private HorizontalAlignment getHorizontalAlignment(TextHorizontalAlignment textHorizontalAlignment) {
        if (TextHorizontalAlignment.LEFT == textHorizontalAlignment) {
            return HorizontalAlignment.LEFT;
        } else if (TextHorizontalAlignment.CENTER == textHorizontalAlignment) {
            return HorizontalAlignment.CENTER;
        } else if (TextHorizontalAlignment.RIGHT == textHorizontalAlignment) {
            return HorizontalAlignment.RIGHT;
        } else if (TextHorizontalAlignment.GENERAL == textHorizontalAlignment) {
            return HorizontalAlignment.GENERAL;
        }
        return HorizontalAlignment.GENERAL;
    }

    /**
     * 获取单元格垂直对齐样式
     *
     * @param textVerticalAlignment
     * @return
     */
    private VerticalAlignment getVerticalAlignment(TextVerticalAlignment textVerticalAlignment) {
        if (TextVerticalAlignment.TOP == textVerticalAlignment) {
            return VerticalAlignment.TOP;
        } else if (TextVerticalAlignment.BOTTOM == textVerticalAlignment) {
            return VerticalAlignment.BOTTOM;
        } else if (TextVerticalAlignment.CENTER == textVerticalAlignment) {
            return VerticalAlignment.CENTER;
        }
        return VerticalAlignment.CENTER;
    }

}

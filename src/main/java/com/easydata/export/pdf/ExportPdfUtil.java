package com.easydata.export.pdf;

/**
 * Created by MYJ on 2017/6/14.
 */

/**
 * 导出PDF帮助类
 */
public class ExportPdfUtil {
/*

    public static void exportPDFFile(HttpServletResponse response, String fileName, List<TheadColumn> theadColumnList, List<Map<String, Object>> dataList) {
        if (theadColumnList == null || theadColumnList.size() < 1)
            return;

        fileName = fileName == null ? "导出文件" : fileName.replaceAll(" ", "");
        try (OutputStream out = response.getOutputStream()) {
            fileName = new String(fileName.getBytes(), "ISO8859-1") + ".pdf";
            response.reset();
            response.setHeader("Content-Disposition", String.format("attachment; filename=%s", fileName));
            response.setContentType("application/pdf; charset=UTF-8");
            response.addCookie(new Cookie("fileDownload", "true"));

            Cookie fileDownload = new Cookie("fileDownload", "true");
            fileDownload.setPath("/");
            response.addCookie(fileDownload);

            exportPDFFile(out, theadColumnList, dataList);

            out.flush();
            out.close();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }


    }

    public static void exportPDFFile(OutputStream out, List<TheadColumn> theadColumnList, List<Map<String, Object>> dataList) throws DocumentException {
        if (theadColumnList == null || theadColumnList.size() < 1)
            return;

        */
/************************表头部分 start***************************//*

        //构建表头
        List<List<TheadColumnTree>> theadColumnTreeListList = TheadColumnTreeUtil.getFromtTheadColumnTreeList(theadColumnList, false);

        //列数
        int numColumns = 0;
        for (TheadColumnTree theadColumnTree : theadColumnTreeListList.get(0)) {
            numColumns += theadColumnTree.getColspan();
        }

        BaseFont bfCN = null;
        try {
            bfCN = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", false);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 正文的字体
        Font headFont = new Font(bfCN, 11f, Font.NORMAL, BaseColor.ORANGE);
        Font textFont = new Font(bfCN, 11f, Font.NORMAL, BaseColor.BLUE);

        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();

        //建立一个4列的表格
        PdfPTable table = new PdfPTable(numColumns);

        table.setWidthPercentage(105f);

        */
/*************  写表头   start  *************//*

        if (theadColumnTreeListList != null && theadColumnTreeListList.size() > 0) {
            for (int i = 0, j = theadColumnTreeListList.size(); i < j; i++) {
                List<TheadColumnTree> theadColumnTreeList = theadColumnTreeListList.get(i);
                for (int m = 0, n = theadColumnTreeList.size(); m < n; m++) {
                    TheadColumnTree theadColumnTree = theadColumnTreeList.get(m);
                    int colspan = theadColumnTree.getColspan();
                    int rowspan = theadColumnTree.getRowspan();
                    String text = theadColumnTree.getText();

                    Paragraph p = new Paragraph(text, headFont);
                    PdfPCell cell = new PdfPCell();
                    p.setAlignment(1);
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                    cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);//然并卵
                    cell.setPaddingTop(-2f);//把字垂直居中
                    cell.setPaddingBottom(8f);//把字垂直居中
                    cell.addElement(p);

                    //cell.setBorder(Rectangle.NO_BORDER);
                    //cell.setBorderWidth(0.01f);


                    //设置跨行
                    if (rowspan > 1) { //合并行
                        cell.setRowspan(rowspan);
                    }
                    //设置跨行
                    if (colspan > 1) { //合并列
                        cell.setColspan(colspan);
                    }

                    table.addCell(cell);
                }
            }
        }


        if (dataList != null && dataList.size() > 0) {
            //没有子节点的列
            List<TheadColumnTree> leafNodesTheadColumnTreeList = TheadColumnTreeUtil.getLeafNodes(theadColumnTreeListList);
            //转换导出数据
            List<List<DataColumnTree>> exportDataListList = DataColumnTreeUtil.getExportFormatDataList(leafNodesTheadColumnTreeList, dataList);


            for (int i = 0, j = exportDataListList.size(); i < j; i++) {

                List<DataColumnTree> exportDataList = exportDataListList.get(i);
                for (int m = 0, n = exportDataList.size(); m < n; m++) {//列
                    DataColumnTree dataColumnTree = exportDataList.get(m);
                    int colspan = dataColumnTree.getColspan();
                    int rowspan = dataColumnTree.getRowspan();
                    boolean isHidden = dataColumnTree.isHidden();
                    String text = dataColumnTree.getValue() == null ? "" : dataColumnTree.getValue().toString();

                    if (isHidden)
                        continue;

                    Paragraph p = new Paragraph(text, textFont);
                    PdfPCell cell = new PdfPCell();
                    p.setAlignment(1);
                    cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
                    cell.setVerticalAlignment(PdfPCell.ALIGN_MIDDLE);//然并卵
                    cell.setPaddingTop(-2f);//把字垂直居中
                    cell.setPaddingBottom(8f);//把字垂直居中
                    cell.addElement(p);

                    //设置跨行
                    if (rowspan > 1) { //合并行
                        cell.setRowspan(rowspan);
                    }
                    //设置跨行
                    if (colspan > 1) { //合并列
                        cell.setColspan(colspan);
                    }

                    table.addCell(cell);

                }
            }
        }

        document.add(table);
        document.close();
    }

*/

}

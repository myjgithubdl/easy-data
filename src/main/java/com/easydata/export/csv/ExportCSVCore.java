package com.easydata.export.csv;

/**
 * Created by MYJ on 2017/6/14.
 */


import com.easydata.head.TheadColumn;
import com.easydata.head.TheadColumnTree;
import com.easydata.head.TheadColumnTreeUtil;
import com.easydata.exception.CSVExportException;
import com.easydata.data.DataColumnTree;
import com.easydata.data.DataColumnTreeUtil;
import com.easydata.enmus.CSVExportEnum;

import java.util.List;
import java.util.Map;

/**
 * 导出CSV帮助类
 */
public class ExportCSVCore {

    /**
     * 将表头和数据转化为POI中的Workbook对象  , 如果参数wb为会自动创建Workbook对象
     *
     * @param exportCSVParams
     * @return 返回CSV文件内容字符串
     */
    public static String getCSVContent(ExportCSVParams exportCSVParams ) {
        List<TheadColumn> theadColumnList=exportCSVParams.getTheadColumnList();
        if(theadColumnList == null || theadColumnList.size() < 1){
            throw new CSVExportException(CSVExportEnum.PARAMETER_ERROR);
        }

        List<Map<String, Object>> dataList=exportCSVParams.getDataList();
        //文件内容
        StringBuffer sbFileContent = new StringBuffer();
        //每行内容
        StringBuffer sbLineContent = new StringBuffer();

        /************************表头部分 start***************************/
        //构建表头
        List<List<TheadColumnTree>> theadColumnTreeListList = TheadColumnTreeUtil.getFromtTheadColumnTreeList(theadColumnList, false);
        //转化表头为不跨行跨列的表头
        List<List<TheadColumnTree>> noRowspanAndNoColspanTableHeadListList = TheadColumnTreeUtil.getNoRowspanAndNoColspanTableHead(theadColumnTreeListList);

        /*************  写表头   start  *************/
        if (noRowspanAndNoColspanTableHeadListList != null
                && noRowspanAndNoColspanTableHeadListList.size() > 0 && exportCSVParams.isExportThead()) {
            for (int i = 0, j = noRowspanAndNoColspanTableHeadListList.size(); i < j; i++) {
                //创建行
                List<TheadColumnTree> theadColumnTreeList = noRowspanAndNoColspanTableHeadListList.get(i);
                for (int m = 0, n = theadColumnTreeList.size(); m < n; m++) {
                    TheadColumnTree theadColumnTree = theadColumnTreeList.get(m);
                    String text = theadColumnTree.getText();
                    sbLineContent.append(text);
                    if (m != n - 1)
                        sbLineContent.append(",");
                }
                sbFileContent.append(sbLineContent);
                sbLineContent.setLength(0);
                sbLineContent.append("\n");
            }
        }
        /************************表头部分 end  ***************************/


        /************************数据部分 start  ***************************/
        if (dataList != null && dataList.size() > 0) {
            //没有子节点的列
            List<TheadColumnTree> leafNodesTheadColumnTreeList = TheadColumnTreeUtil.getLeafNodes(theadColumnTreeListList);
            //设置所有的列都不计算值相同的行的列合并
            for (TheadColumnTree theadColumnTree : leafNodesTheadColumnTreeList) {
                theadColumnTree.setDownMergeCells(false);
            }

            //转换导出数据
            List<List<DataColumnTree>> exportDataListList = DataColumnTreeUtil.getExportFormatDataList(leafNodesTheadColumnTreeList, dataList);

            //if(exportDataListList != null ){
                for (int i = 0, j = exportDataListList.size(); i < j; i++) {
                    //创建行
                    List<DataColumnTree> exportDataList = exportDataListList.get(i);

                    for (int m = 0, n = exportDataList.size(); m < n; m++) {//列
                        DataColumnTree dataColumnTree = exportDataList.get(m);
                        Object text = dataColumnTree.getValue();
                        sbLineContent.append(text + "");
                        if (m != n - 1)
                            sbLineContent.append(",");
                    }
                    sbFileContent.append(sbLineContent);
                    sbLineContent.setLength(0);
                    sbLineContent.append("\n");
                }
            //}

        }
        /************************数据部分 end    ***************************/
        return sbFileContent.toString();
    }


}

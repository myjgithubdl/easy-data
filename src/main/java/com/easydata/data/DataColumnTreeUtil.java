package com.easydata.data;

import com.easydata.head.TheadColumnTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by MYJ on 2017/6/13.
 */
public class DataColumnTreeUtil {
    protected static final Logger LOGGER = LoggerFactory.getLogger(DataColumnTreeUtil.class);
    /**
     *  数字格式化
     */
    NumberFormat nf = NumberFormat.getInstance();

    /**
     * 所有表头列
     */
    List<TheadColumnTree> theadColumnTrees = null;

    /**
     * 返回的数据
     */
    List<List<DataColumnTree>> returnList = null;

    /**
     * 保存某一列数据合并行的区域
     */
    List<DataColumnBlock> dataColumnBlocks=null;

    /**
     * 根据表头顺序计算出数据的跨列数
     * @param theadColumnTrees    没有子节点的表头即叶子节点表头
     * @param dataList   数据
     * @return   转换后导出格式的数据
     */
    public static List<List<DataColumnTree>> getExportFormatDataList(List<TheadColumnTree> theadColumnTrees, List<Map<String, Object>> dataList) {
        DataColumnTreeUtil dataColumnTreeUtil = new DataColumnTreeUtil();
        //转换导出数据
        return dataColumnTreeUtil.getExportDataColumn(theadColumnTrees, dataList);
    }

    /**
     * 根据表头顺序计算出数据的跨列数
     *
     * @param theadColumnTrees 没有子节点的表头列表
     * @param dataList         数据
     * @return
     */
    public List<List<DataColumnTree>> getExportDataColumn(List<TheadColumnTree> theadColumnTrees, List<Map<String, Object>> dataList) {
        if (theadColumnTrees == null || theadColumnTrees.size() < 1 || dataList == null || dataList.size() < 1)
            return null;
        this.theadColumnTrees = theadColumnTrees;
        this.returnList = new ArrayList<>();
        int rowIndex=0;
        for (Map<String, Object> map : dataList) {
            List<DataColumnTree> dataColumnTreeList = new ArrayList<>();
            for (TheadColumnTree theadColumnTree : theadColumnTrees) {
                DataColumnTree dataColumnTree = new DataColumnTree();
                dataColumnTree.setValue(this.getDataColumnValue(map , theadColumnTree));
                dataColumnTree.setRowIndex(rowIndex);
                dataColumnTreeList.add(dataColumnTree);
            }
            rowIndex ++;
            returnList.add(dataColumnTreeList);
        }

        //现在不使用递归，数据量超10W会报栈溢出错误，改用循环代替递归
        //this.recursionMergeCellBehindRowsAndColumns(0, 0, returnList.size() - 1);
        this.calcDataRowSpan(theadColumnTrees);

        return this.returnList;
    }

    /**
     * 递归计算跨行数
     * 逻辑大体为：从第startDataRowIndex行开始找第startCellIndex列值相同的所有行，值相同的最后一行索引为endDataRowIndex
     * 将值相同的开始行（行索引为：startDataRowIndex）的跨行数设置为值相同的行数、将之后值相同的行（索引区间为：[startDataRowIndex+1 ,endDataRowIndex]）的isHidden属性设置为true
     * （导出Excel时跨行跨列根据该字段判断是否被跨行）
     * 然后退出本次循环、处理行索引在startDataRowIndex和endDataRowIndex之间的行的tartDataRowIndex+1列
     * 直到列处理完再从行索引为endDataRowIndex+1的好难过开始按照上述处理方法处理
     * <p>
     * 说白了就是：先纵向处理行、当下一行的值和当前行的值不等、暂停纵向处理、开始横向处理所有的列，当列处理完成总暂停的行的下一行继续同样的处理方法
     *
     * @param startCellIndex    表头开始列的索引号   从0开始
     * @param startDataRowIndex 数据开始计算行的索引号  从0开始
     * @param endDataRowIndex   数据结束计算行的索引号  从0开始
     */
    private void recursionMergeCellBehindRowsAndColumns(int startCellIndex, int startDataRowIndex, int endDataRowIndex) {
        if (startCellIndex >= theadColumnTrees.size() || startDataRowIndex >= returnList.size() - 1 || startDataRowIndex >= endDataRowIndex)
            return;

        //列
        TheadColumnTree theadColumnTree = theadColumnTrees.get(startCellIndex);

        //需要向下合并值相同的列
        if (theadColumnTree.isDownMergeCells()) {
            int rowspan = 1;//跨行数

            //循环行
            for (int dataRowIndex = startDataRowIndex, maxDataRowIndex = endDataRowIndex; dataRowIndex <= maxDataRowIndex; dataRowIndex++) {
                String value1 = returnList.get(dataRowIndex).get(startCellIndex).getValue() + "";
                String value2 = null;
                if (dataRowIndex != maxDataRowIndex) {
                    value2 = returnList.get(dataRowIndex + 1).get(startCellIndex).getValue() + "";
                }

                if (value1.equals(value2)) {//等于下一行同列的值
                    rowspan += 1;
                    returnList.get(dataRowIndex + 1).get(startCellIndex).setHidden(true);
                } else {//与下一行同列的值不等

                    //存在值相同的列，转为横向处理
                    if (rowspan > 1) {
                        returnList.get(startDataRowIndex).get(startCellIndex).setRowspan(rowspan);
                        //处理后面需要合并的列
                        if (theadColumnTrees.get(startCellIndex + 1).isDownMergeCells()) {
                            this.recursionMergeCellBehindRowsAndColumns(startCellIndex + 1, startDataRowIndex, dataRowIndex);
                        }
                    }

                    //继续处理后面的行
                    this.recursionMergeCellBehindRowsAndColumns(startCellIndex, dataRowIndex + 1, endDataRowIndex);
                    break;
                }

            }

        }
    }

    /**
     * 曹勇循环的方式计算跨列数
     * @param theadColumnTrees
     */
    private void calcDataRowSpan(List<TheadColumnTree> theadColumnTrees){
        this.dataColumnBlocks=new ArrayList<>();
        for(int i=0 , j=theadColumnTrees.size() ; i< j ; i++){
            if(theadColumnTrees.get(i).isDownMergeCells()  ){
                LOGGER.info("开始计算第"+i+"列的行合并数据！");
                if(i==0){
                    List<DataColumnBlock> dataColumnBlocks =this.calcDataColumnRowSpan(theadColumnTrees.get(i) , 0 , this.returnList.size()-1 );
                    this.dataColumnBlocks.addAll(dataColumnBlocks);
                }else{
                    List<DataColumnBlock> dataColumnBlockList=new ArrayList<>();
                    for(int m=0 , n=this.dataColumnBlocks.size() ; m< n ; m++){
                        DataColumnBlock block = this.dataColumnBlocks.get(m);
                        List<DataColumnBlock> dataColumnBlocks = this.calcDataColumnRowSpan(theadColumnTrees.get(i), block.getStartRowIndex(), block.getEndRowIndex());
                        dataColumnBlockList.addAll(dataColumnBlocks);
                    }
                    //清除上一列的合并数据
                    this.dataColumnBlocks.clear();
                    this.dataColumnBlocks.addAll(dataColumnBlockList);
                }
            }
        }

    }


    /**
     * 根据开始行和结束行计算跨行数，并将合并区域返回
     * @param theadColumnTree
     * @param startRowIndex
     * @param endRowIndex
     * @return
     */
    public List<DataColumnBlock> calcDataColumnRowSpan(TheadColumnTree theadColumnTree ,int startRowIndex, int endRowIndex ){
        if(startRowIndex >= endRowIndex  ){
            return new ArrayList<>();
        }
        List<DataColumnBlock> dataColumnBlocks2=new ArrayList<>();
        Integer celIndex = theadColumnTree.getCelIndex();
        //跨行数
        int rowspan = 1;
        int startRowspanIndex=startRowIndex;
        for (int i = startRowIndex; i <= endRowIndex; i++) {
            Object value1 = this.returnList.get(i).get(celIndex).getValue();
            Object value2 = null;
            if (i != endRowIndex) {
                value2 = this.returnList.get(i + 1).get(celIndex).getValue();
            }
            //等于下一行同列的值
            if (value1.equals(value2)) {
                rowspan += 1;
                this.returnList.get(i + 1).get(celIndex).setHidden(true);
            }
            //与下一行同列的值不等
            else {
                //存在值相同的列，转为横向处理
                if (rowspan > 1) {
                    this.returnList.get(startRowspanIndex).get(celIndex).setRowspan(rowspan);

                    DataColumnBlock dataColumnBlock = new DataColumnBlock(theadColumnTree,startRowspanIndex ,i  );
                    dataColumnBlocks2.add(dataColumnBlock);
                }
                startRowspanIndex= i + 1;
                //跨行数
                rowspan = 1;
            }
        }
        return dataColumnBlocks2;
    }

    /**
     * 从map中获取theadColumnTree对应字段的值
     * @param map
     * @param theadColumnTree
     * @return
     */
    public Object getDataColumnValue( Map<String, Object> map , TheadColumnTree theadColumnTree ){
        Object value=null;
        if(theadColumnTree == null || theadColumnTree.getName() == null ) {
            return value;
        }

        if(map == null){
            return theadColumnTree.getDefaultValue();
        }else{
            value=map.get(theadColumnTree.getName());
            value= value == null ? theadColumnTree.getDefaultValue() : value;
            if(value != null ){
                if(value instanceof String){

                }
                //防止科学计数
                else if(value instanceof Integer){
                    nf.setGroupingUsed(false);
                    value=nf.format(value);
                }
                //防止科学计数
                else if(value instanceof Double){
                    //有精度，采用科学计数法
                    if(theadColumnTree.getDecimals() != null ){
                        value=String.format("%."+theadColumnTree.getDecimals()+"f", value);
                    }else{
                        nf.setGroupingUsed(false);
                        value=nf.format(value);
                    }
                }
            }
        }
        return value;
    }

}

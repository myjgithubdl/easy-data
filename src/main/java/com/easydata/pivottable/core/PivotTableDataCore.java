package com.easydata.pivottable.core;

import com.alibaba.fastjson.JSONObject;
import com.easydata.constant.Constant;
import com.easydata.head.TheadColumn;
import com.easydata.pivottable.domain.PivotTable;
import com.easydata.pivottable.domain.PivotTableCalCol;
import com.easydata.pivottable.enmus.AggFunc;
import com.easydata.utils.ValueUtil;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;

import java.util.*;

/**
 * 透视表服务类
 */
@Getter
@Setter
public class PivotTableDataCore {

    /**
     * 设置的透视表信息
     */
    private PivotTable pivotTable;

    /**
     * 原始表头信息  List
     */
    private List<TheadColumn> theadColumnList;

    /**
     * 需要转换的原始数据
     */
    private List<Map<String, Object>> dataList;


    /**
     * 原始表头信息  Map
     */
    private Map<String, TheadColumn> theadColumnMap = new HashMap<>();

    /**
     * 转化后透视表转化后的表头
     */
    private List<TheadColumn> pivotTableTheadColumnList = new ArrayList<>();

    /**
     * 透视表转化后的数据
     */
    private List<Map<String, Object>> pivotTableDataList;


    /**
     * 透视表按照设置的行将数据转化为的数据，使用LinkedHashMap保证顺序
     * key为所有设置的  列名+Constant.KEY_VALUE_SEPARATOR+列值
     * 如 name:缪应江$sex:男
     */
    LinkedHashMap<String, List<Map<String, Object>>> groupByRowMap;

    /**
     * 在根据透视表设置的列转化出新的表头对应的数据，LinkedHashMap保证顺序
     * 外层可key为groupByRowMap重的key
     * 内层key为新转化出的新列名 ， 值为相关的数据
     * 后面的计算都是根据这个值计算
     */
    LinkedHashMap<String, Map<String, List<Map<String, Object>>>> groupByRowMapNewColCalData = new LinkedHashMap<>();


    /**
     * 透视表中设置的列转化为的透视表新列
     * key为透视表设置的列名称  value为该列在原始数据中去重的值
     */
    Map<String, List<String>> colValuesMap = new TreeMap<>();


    public PivotTableDataCore() {
    }

    public PivotTableDataCore(PivotTable pivotTable, List<TheadColumn> theadColumnList, List<Map<String, Object>> dataList) {
        this.pivotTable = pivotTable;
        this.dataList = dataList;
        this.theadColumnList = theadColumnList;

        /*this.theadColumnsConvertMap();
        this.groupByRow();*/
        calcPivotTableData();
    }

    /**
     * 开始计算透视表数据
     *
     * @return
     */
    public PivotTableDataCore calcPivotTableData() {
        this.theadColumnsConvertMap();
        this.groupByRow();
        return this;
    }

    /**
     * 将List表头转为Map结构  方便根据列名查找
     */
    private void theadColumnsConvertMap() {
        if (theadColumnList != null) {
            theadColumnList.stream().filter(theadColumn -> theadColumn.getName() != null && theadColumn.getName().trim().length() > 0).forEach(theadColumn -> theadColumnMap.put(theadColumn.getName(), theadColumn));
        }
    }

    /**
     * 将数据按照透视表中设置的行进行分组
     *
     * @return
     */
    private void groupByRow() {
        //返回的数据 key为数据部分：字段1+KEY_VALUE_SEPARATOR+字段1对应的值+ITEM_SEPARATOR+字段2+KEY_VALUE_SEPARATOR+字段2对应的值
        List<String> colNameList = this.getPivotTable().getRows();
        this.groupByRowMap = new LinkedHashMap<>();

        if (colNameList != null && colNameList.size() > 0) {
            colNameList.stream().forEach(name -> {
                for (TheadColumn theadColumn : this.theadColumnList) {
                    if (name.equals(theadColumn.getName())) {
                        this.pivotTableTheadColumnList.add(theadColumn);
                        break;
                    }
                }
            });

            //将数据按照透视表设置的行对数据分行处理
            for (Map<String, Object> map : dataList) {
                String groupByKey = "";
                for (String colName : colNameList) {
                    String value = map.get(colName) == null ? "" : map.get(colName).toString();
                    if (groupByKey.length() > 0) {
                        groupByKey += Constant.ITEM_SEPARATOR;
                    }
                    groupByKey += colName + Constant.KEY_VALUE_SEPARATOR + value;
                }
                List<Map<String, Object>> resultList = this.groupByRowMap.get(groupByKey);
                if (resultList == null) {
                    resultList = new ArrayList<>();
                }
                resultList.add(map);
                this.groupByRowMap.put(groupByKey, resultList);
            }
        }
        this.getRowTraColFieldAndColumnData();
    }

    /**
     * 获得透视表中选择的列以及对应的列对应的值（因为列对应值的所有行要转化为透视表的列）
     *
     * @return
     */
    private void getRowTraColFieldAndColumnData() {
        List<String> columnNameList = this.pivotTable.getCols();
        if (columnNameList != null && columnNameList.size() > 0) {
            Set<String> newColIdSet = new TreeSet<>();

            //将第一列放入透视表列
            newColIdSet.add(this.theadColumnMap.get(columnNameList.get(0)).getName());
            this.pivotTableTheadColumnList.add(this.theadColumnMap.get(columnNameList.get(0)));

            LinkedHashMap<String, List<Map<String, Object>>> groupByRowMapData = this.groupByRowMap;

            for (String key : groupByRowMapData.keySet()) {
                List<Map<String, Object>> groupByRowList = groupByRowMapData.get(key);

                for (int dataRowIndex = 0, dataSize = groupByRowList.size(); dataRowIndex < dataSize; dataRowIndex++) {
                    Map<String, Object> rowData = groupByRowList.get(dataRowIndex);

                    String pid = null;
                    for (int colIndex = 0, colSize = columnNameList.size(); colIndex < colSize; colIndex++) {
                        String columnName = columnNameList.get(colIndex);
                        String value = ValueUtil.getValueStr(this.theadColumnMap.get(columnName), rowData, "");
                        String id = null;
                        if (pid == null) {
                            id = columnName + Constant.KEY_VALUE_SEPARATOR + value;
                        } else {
                            id = pid + Constant.ITEM_SEPARATOR + columnName + Constant.KEY_VALUE_SEPARATOR + value;
                        }

                        TheadColumn theadColumn = JSONObject.parseObject(JSONObject.toJSONString(this.theadColumnMap.get(columnName)), TheadColumn.class);//保证属性与原来的列一致
                        if (colIndex == 0) {
                            //theadColumn = new TheadColumn(id, this.theadColumnMap.get(columnName).getId(), id, value);
                            theadColumn.setPid(this.theadColumnMap.get(columnName).getId());
                        } else {
                            //theadColumn = new TheadColumn(id, pid, id, value);
                            theadColumn.setPid(pid);
                        }
                        theadColumn.setId(id);
                        theadColumn.setName(id);
                        theadColumn.setText(value);

                        pid = id;

                        if (!newColIdSet.contains(id)) {
                            this.pivotTableTheadColumnList.add(theadColumn);
                            newColIdSet.add(id);
                        }

                        //将行数据加入计算列
                        if (colIndex == colSize - 1) {
                            Map<String, List<Map<String, Object>>> groupRowByMap = this.groupByRowMapNewColCalData.get(key);
                            if (groupRowByMap == null) {
                                groupRowByMap = new HashMap<>();
                                this.groupByRowMapNewColCalData.put(key, groupRowByMap);
                            }
                            List<Map<String, Object>> groupRowMapNewDataList = groupRowByMap.get(id);
                            if (groupRowMapNewDataList == null) {
                                groupRowMapNewDataList = new ArrayList<>();
                                groupRowByMap.put(id, groupRowMapNewDataList);
                            }
                            groupRowMapNewDataList.add(rowData);
                        }
                    }
                }
            }
        }
        this.calcPivotTableNewColValues();
    }


    /**
     * 计算透视表中设置的列转化为的新列的值
     */
    private void calcPivotTableNewColValues() {
        this.pivotTableDataList = new ArrayList<>();
        Map<String, List<Map<String, Object>>> pivotTableRowMap = this.groupByRowMap;

        Set<String> newColIdSet = new TreeSet<>();

        for (String rowKey : pivotTableRowMap.keySet()) {
            //先保存透视表中设置的行的列的值
            List<Map<String, Object>> rowDataMapList = pivotTableRowMap.get(rowKey);
            Map<String, Object> pivotTableRow = new HashMap<>();
            pivotTableRow.putAll(rowDataMapList.get(0));

            //新转化出来的列的值
            Map<String, List<Map<String, Object>>> colNameAndDataMap = this.groupByRowMapNewColCalData.get(rowKey);

            for (String colName : colNameAndDataMap.keySet()) {

                List<PivotTableCalCol> calCols = this.pivotTable.getCalCols();
                if (calCols != null && calCols.size() > 0) {
                    calCols.forEach(pivotTableCalCol -> {
                        TheadColumn theadColumn = this.theadColumnMap.get(pivotTableCalCol.getName());
                        AggFunc aggFunc = pivotTableCalCol.getAggFunc();
                        //计算
                        String value = ValueUtil.getValue(theadColumn, aggFunc, colNameAndDataMap.get(colName));

                        //再创建表头(如果只有大于一个计算列)
                        String id = colName + Constant.ITEM_SEPARATOR + aggFunc;
                        if (calCols.size() == 1) {
                            id = colName;
                        } else {
                            id = colName + Constant.ITEM_SEPARATOR + aggFunc;

                            if (!newColIdSet.contains(id)) {
                                newColIdSet.add(id);
                                TheadColumn newTheadColumn = new TheadColumn();
                                try {
                                    BeanUtils.copyProperties(newTheadColumn, theadColumn);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                newTheadColumn.setId(id);
                                newTheadColumn.setPid(colName);
                                newTheadColumn.setName(id);
                                newTheadColumn.setText(aggFunc.getText());

                                this.pivotTableTheadColumnList.add(newTheadColumn);
                            }
                        }

                        pivotTableRow.put(id, value);

                    });
                }
            }

            this.pivotTableDataList.add(pivotTableRow);

        }
    }
}
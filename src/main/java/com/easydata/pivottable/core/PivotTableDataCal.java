package com.easydata.pivottable.core;


import com.easydata.pivottable.enmus.AggFunc;

import java.util.*;


/**
 * （旧版本）
 * 值能处理一个列和计算列
 * 透视表数据处理工具核心类
 */
@Deprecated
public class PivotTableDataCal {

    /**
     * 用于透视表中设置的行的数据部分的键值分隔符
     */
    public static final String KEY_VALUE_SEPARATOR = "\001";

    /**
     * 用于透视表中设置的多行的数据部分的键值分隔符
     */
    public static final String ITEM_SEPARATOR = "\002";



    /**
     * 将数据按照透视表中设置的行进行分组
     *
     * @param dataList
     * @param colNameList
     * @return
     */
    public static Map<String, List<Map<String, Object>>> groupByRow(List<Map<String, Object>> dataList, List<String> colNameList) {
        //返回的数据 key为数据部分：字段1+KEY_VALUE_SEPARATOR+字段1对应的值+ITEM_SEPARATOR+字段2+KEY_VALUE_SEPARATOR+字段2对应的值
        Map<String, List<Map<String, Object>>> resultListMap = new TreeMap<>();
        if (colNameList != null && colNameList.size() > 0) {
            for (Map<String, Object> map : dataList) {
                String groupByKey = "";
                for (String colName : colNameList) {
                    String value = map.get(colName) == null ? "" : map.get(colName).toString();
                    if (groupByKey.length() > 0) {
                        groupByKey += ITEM_SEPARATOR;
                    }
                    groupByKey += colName + KEY_VALUE_SEPARATOR + value;
                }
                List<Map<String, Object>> resultList = resultListMap.get(groupByKey);
                if (resultList == null) {
                    resultList = new ArrayList<>();
                }
                resultList.add(map);
                resultListMap.put(groupByKey, resultList);
            }
        }
        return resultListMap;

    }

    /**
     * 获得透视表中选择的列以及对应的列对应的值（因为列对应值的所有行要转化为透视表的列）
     *
     * @param dataList
     * @param columnNameList
     * @return
     */
    public static Map<String, List<String>> getRowTraColFieldAndColumnData(List<Map<String, Object>> dataList, List<String> columnNameList) {
        Map<String, List<String>> respMap = new HashMap();
        if (columnNameList != null) {
            for (String columnName : columnNameList) {
                List<String> respList = new ArrayList<>();
                Set<String> repSet = new HashSet<>();

                for (Map<String, Object> map : dataList) {
                    String value = map.get(columnName) == null ? "" : map.get(columnName).toString();
                    if (value.length() > 0 && !repSet.contains(value)) {
                        repSet.add(value);
                        respList.add(value);
                    }
                }
                Collections.sort(respList);
                respMap.put(columnName, respList);
            }
        }
        return respMap;
    }

    /**
     * 根据透视表的信息将数据转化为透视表的数据
     *
     * @param pivotTableRowColumnList       透视表中设置的行
     * @param pivotTableRowTraColColumnList 透视表中设置的列
     * @param calcColumn                    透视表中的设置的值
     * @param aggFunc                       聚合函数
     * @param oldDataList                   原始数据
     * @return 转化后的透视表数据
     */
    public static Map<String, Object> getPivotTableData(List<String> pivotTableRowColumnList,
                                                        List<String> pivotTableRowTraColColumnList,
                                                        String calcColumn,
                                                        AggFunc aggFunc,
                                                        List<Map<String, Object>> oldDataList) {
        Map<String, List<Map<String, Object>>> resultListMap = PivotTableDataCal.groupByRow(oldDataList, pivotTableRowColumnList);

        Map<String, List<String>> rowTraColFieldAndColumnDataMap = PivotTableDataCal.getRowTraColFieldAndColumnData(oldDataList, pivotTableRowTraColColumnList);

        //透视表上设置的列的行的值转为透视表上的列
        List<String> pivotTableRowTraColFieldColumn = new ArrayList<>();
        for (String oldColumnName : rowTraColFieldAndColumnDataMap.keySet()) {
            pivotTableRowTraColFieldColumn.addAll(rowTraColFieldAndColumnDataMap.get(oldColumnName));
        }

        Map<String, Map<String, Object>> pivotTableCalDataMap =
                PivotTableDataCal.getPivotTableCalData(resultListMap, rowTraColFieldAndColumnDataMap, calcColumn, aggFunc);

        //透视表上的所有列
        List<String> pivotTableAllColumn = new ArrayList<>();
        pivotTableAllColumn.addAll(pivotTableRowColumnList);
        pivotTableAllColumn.addAll(pivotTableRowTraColFieldColumn);
        //透视表的数据
        List<Map<String, Object>> pivotTableData = PivotTableDataCal.getPivotTableRowData(pivotTableCalDataMap, pivotTableRowColumnList, pivotTableRowTraColFieldColumn);


        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("columnList", pivotTableAllColumn);//转化后透视表的列信息
        resultMap.put("pivotTableData", pivotTableData);//转化后透视表的数据
        resultMap.put("rowTraColFieldAndColumnDataMap", rowTraColFieldAndColumnDataMap);//透视表设置列，通过设置的列的行转化后生成的列

        return resultMap;
    }

    /**
     * 根据分组后的数据计算计算透视表数据
     * 计算后的数据相对于原始数据key保持不变，key对应的值是map，其中calData是原始数据、calResult是计算结果数据
     * calResult是Map结构，key是透视表中设置的列对应行的值，value是透视表中设置的值对应的求和（sum）、计数（count）、平均（avg）、最大值（max）、最小值（min）
     *
     * @param resultListMap                  按透视表行设置的键值对应的数据
     * @param rowTraColFieldAndColumnDataMap 透视表中设置的列以及列对应的值
     * @param calcColumnName                 需要使用聚合函数的列名
     * @param aggfunc                        使用的聚合函数求和（sum）、计数（count）、平均（avg）、最大值（max）、最小值（min）
     * @return
     */
    public static Map<String, Map<String, Object>> getPivotTableCalData(Map<String, List<Map<String, Object>>> resultListMap,
                                                                        Map<String, List<String>> rowTraColFieldAndColumnDataMap,
                                                                        String calcColumnName,
                                                                        AggFunc aggfunc) {
        Map<String, Map<String, Object>> pivotTableCalDataMap = new TreeMap<>();

        //循环每行数据
        for (String key : resultListMap.keySet()) {

            //根据透视表设置的行分组后的集合
            List<Map<String, Object>> dataList = resultListMap.get(key);

            //存放计算结果
            Map<String, Object> calResult = new TreeMap<>();

            //循环透视表中设置的列行的值（该值为透视表的列）
            for (String rowTraColName : rowTraColFieldAndColumnDataMap.keySet()) {
                //透视表中设置的列的值的集合
                List<String> columnList = rowTraColFieldAndColumnDataMap.get(rowTraColName);

                //透视表行转化出来的列
                for (String columnName : columnList) {

                    if (AggFunc.SUM.equals(aggfunc)) {//汇总
                        getCalcColumnSum(dataList, calResult, columnName, rowTraColName, calcColumnName);
                    } else if (AggFunc.COUNT.equals(aggfunc)) {
                        getCalcColumnCount(dataList, calResult, columnName, rowTraColName, calcColumnName);
                    } else if (AggFunc.AVG.equals(aggfunc)) {
                        getCalcColumnAvg(dataList, calResult, columnName, rowTraColName, calcColumnName);
                    } else if (AggFunc.MAX.equals(aggfunc)) {
                        getCalcColumnMax(dataList, calResult, columnName, rowTraColName, calcColumnName);
                    } else if (AggFunc.MIN.equals(aggfunc)) {
                        getCalcColumnMin(dataList, calResult, columnName, rowTraColName, calcColumnName);
                    }
                }
            }

            Map<String, Object> map = new HashMap<>();
            map.put("calResult", calResult);
            map.put("calData", dataList);
            pivotTableCalDataMap.put(key, map);
        }
        return pivotTableCalDataMap;

    }

    /**
     * 获得透视表的行数据
     *
     * @param pivotTableCalDataMap           分组后的数据计算计算透视表数据
     * @param pivotTableRowNameList          透视表中设置行的列集合
     * @param pivotTableRowTraColFieldColumn 透视表中设置的列的行数据转化出的新列名
     * @return
     */
    public static List<Map<String, Object>> getPivotTableRowData(Map<String, Map<String, Object>> pivotTableCalDataMap,
                                                                 List<String> pivotTableRowNameList,
                                                                 List<String> pivotTableRowTraColFieldColumn) {
        List<Map<String, Object>> resultMapList = new ArrayList<>();
        //循环每一行
        for (String columnNameAndValuesKey : pivotTableCalDataMap.keySet()) {
            Map<String, Object> resultMap = new HashMap<>();

            Map<String, Object> rowDataMap = pivotTableCalDataMap.get(columnNameAndValuesKey);

            List<Map<String, Object>> calData = (List<Map<String, Object>>) rowDataMap.get("calData");
            Map<String, Double> calResult = (Map<String, Double>) rowDataMap.get("calResult");

            //先处理透视表中设置的行
            for (String pivotTableColumnName : pivotTableRowNameList) {
                resultMap.put(pivotTableColumnName, calData.get(0).get(pivotTableColumnName));
            }

            //处理透视表中通过行转化为的列
            for (String pivotTableColumnName : pivotTableRowTraColFieldColumn) {
                resultMap.put(pivotTableColumnName, calResult.get(pivotTableColumnName));
            }

            resultMapList.add(resultMap);
        }
        return resultMapList;
    }

    /**
     * 求和
     *
     * @param dataList       分组后的数据列表
     * @param calResult      计算的结果
     * @param columnName     透视表中设置的列名称
     * @param rowTraColName  透视表中设置的列对应的值转化为新列的名称
     * @param calcColumnName 需要计算的值的了名称
     */
    public static void getCalcColumnSum(List<Map<String, Object>> dataList,
                                        Map<String, Object> calResult,
                                        String columnName,
                                        String rowTraColName,
                                        String calcColumnName) {
        for (Map<String, Object> map : dataList) {
            Double prevValue = (Double) calResult.get(columnName);
            if (calResult.get(columnName) == null) {
                prevValue = 0d;
            }
            Double value = 0d;
            //通过透视表设置的列的值转化为新的透视表列名与设置的透视表列名对应的值相对
            if (map.get(rowTraColName) != null && columnName.equals(map.get(rowTraColName).toString())) {
                value = map.get(calcColumnName) == null ? 0 : Double.valueOf(map.get(calcColumnName).toString());
            }

            prevValue = prevValue + value;
            calResult.put(columnName, prevValue);

        }
    }

    /**
     * 求均值
     *
     * @param dataList       分组后的数据列表
     * @param calResult      计算的结果
     * @param columnName     透视表中设置的列名称
     * @param rowTraColName  透视表中设置的列对应的值转化为新列的名称
     * @param calcColumnName 需要计算的值的了名称
     */
    public static void getCalcColumnAvg(List<Map<String, Object>> dataList,
                                        Map<String, Object> calResult,
                                        String columnName,
                                        String rowTraColName,
                                        String calcColumnName) {
        String sumKey = columnName + "_sum";
        String countKey = columnName + "_count";
        for (Map<String, Object> map : dataList) {

            Double prevSum = (Double) calResult.get(sumKey);
            Double preCount = (Double) calResult.get(countKey);

            if (calResult.get(sumKey) == null) {
                prevSum = 0d;
            }
            if (calResult.get(countKey) == null) {
                preCount = 0d;
            }
            Double value = 0d;
            //通过透视表设置的列的值转化为新的透视表列名与设置的透视表列名对应的值相对
            //if (columnName.equals(map.get(rowTraColName))) {
            if (map.get(rowTraColName) != null && columnName.equals(map.get(rowTraColName).toString())) {
                value = map.get(calcColumnName) == null ? 0 : Double.valueOf(map.get(calcColumnName).toString());
                preCount += 1;
            }

            prevSum = prevSum + value;
            calResult.put(sumKey, prevSum);
            calResult.put(countKey, preCount);
            if (preCount > 0) {
                calResult.put(columnName, prevSum / preCount);
            } else {
                calResult.put(columnName, 0);
            }
        }
    }


    /**
     * 统计数量
     *
     * @param dataList       分组后的数据列表
     * @param calResult      计算的结果
     * @param columnName     透视表中设置的列名称
     * @param rowTraColName  透视表中设置的列对应的值转化为新列的名称
     * @param calcColumnName 需要计算的值的了名称
     */
    public static void getCalcColumnCount(List<Map<String, Object>> dataList,
                                          Map<String, Object> calResult,
                                          String columnName,
                                          String rowTraColName,
                                          String calcColumnName) {
        for (Map<String, Object> map : dataList) {
            Integer prevValue = (Integer) calResult.get(columnName);
            if (calResult.get(columnName) == null) {
                prevValue = 0;
            }
            //通过透视表设置的列的值转化为新的透视表列名与设置的透视表列名对应的值相对
            //if (columnName.equals(map.get(rowTraColName))) {
            if (map.get(rowTraColName) != null && columnName.equals(map.get(rowTraColName).toString())) {
                prevValue++;
            }
            calResult.put(columnName, prevValue);
        }
    }


    /**
     * 求最大值
     *
     * @param dataList       分组后的数据列表
     * @param calResult      计算的结果
     * @param columnName     透视表中设置的列名称
     * @param rowTraColName  透视表中设置的列对应的值转化为新列的名称
     * @param calcColumnName 需要计算的值的了名称
     */
    public static void getCalcColumnMax(List<Map<String, Object>> dataList,
                                        Map<String, Object> calResult,
                                        String columnName,
                                        String rowTraColName,
                                        String calcColumnName) {
        for (Map<String, Object> map : dataList) {
            Double value = 0d;
            //通过透视表设置的列的值转化为新的透视表列名与设置的透视表列名对应的值相对
            if (map.get(rowTraColName) != null && columnName.equals(map.get(rowTraColName).toString())) {
                //if (columnName.equals(map.get(rowTraColName))) {
                value = map.get(calcColumnName) == null ? 0 : Double.valueOf(map.get(calcColumnName).toString());
            }
            Double prevValue = (Double) calResult.get(columnName);
            if (calResult.get(columnName) == null) {
                prevValue = 0d;
            }

            if (prevValue < value) {
                prevValue = value;
            }
            calResult.put(columnName, prevValue);

        }
    }

    /**
     * 求最大值
     *
     * @param dataList       分组后的数据列表
     * @param calResult      计算的结果
     * @param columnName     透视表中设置的列名称
     * @param rowTraColName  透视表中设置的列对应的值转化为新列的名称
     * @param calcColumnName 需要计算的值的了名称
     */
    public static void getCalcColumnMin(List<Map<String, Object>> dataList,
                                        Map<String, Object> calResult,
                                        String columnName,
                                        String rowTraColName,
                                        String calcColumnName) {
        for (Map<String, Object> map : dataList) {

            Double value = 0d;
            //通过透视表设置的列的值转化为新的透视表列名与设置的透视表列名对应的值相对
            if (map.get(rowTraColName) != null && columnName.equals(map.get(rowTraColName).toString())) {
                //if (columnName.equals(map.get(rowTraColName))) {
                value = map.get(calcColumnName) == null ? 0 : Double.valueOf(map.get(calcColumnName).toString());
            }

            Double prevValue = (Double) calResult.get(columnName);
            if (calResult.get(columnName) == null) {
                prevValue = value;
            }

            if (prevValue > value) {
                prevValue = value;
            }
            calResult.put(columnName, prevValue);

        }
    }

}

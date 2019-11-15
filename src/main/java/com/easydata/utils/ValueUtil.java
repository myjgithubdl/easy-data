package com.easydata.utils;

import com.easydata.head.TheadColumn;
import com.easydata.pivottable.enmus.AggFunc;
import org.apache.commons.collections4.MapUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 获取值的工具类
 */
public class ValueUtil {

    /**
     * 从dataMap中获取theadColumn的值
     *
     * @param theadColumn
     * @param dataMap
     * @return
     */
    public static Object getValue(TheadColumn theadColumn, Map<String, Object> dataMap) {
        if (theadColumn == null || theadColumn.getName() == null || dataMap == null || dataMap.get(theadColumn.getName()) == null) {
            return null;
        }
        return dataMap.get(theadColumn.getName());

    }

    public static String getValueStr(TheadColumn theadColumn, Object value) {
        if (value == null) {
            if (theadColumn.getDefaultValue() != null) {
                return theadColumn.getDefaultValue().toString();
            }
            return null;
        }

        if (value instanceof String) {
            return value.toString();
        } else if (value instanceof Integer) {
            return value.toString();
        } else if (value instanceof Double) {
            Integer precision = theadColumn.getPrecision();
            if (precision == null) {
                return String.format("%.2f", value);
            } else {
                return String.format("%." + precision + "f", value);
            }
        } else if (value instanceof Date) {
            return value.toString();
        }

        return value.toString();
    }

    /**
     * 从dataMap中获取theadColumn的值
     *
     * @param theadColumn
     * @param dataMap
     * @return
     */
    public static String getValueStr(TheadColumn theadColumn, Map<String, Object> dataMap) {
        Object value = getValue(theadColumn, dataMap);

        return getValueStr(theadColumn, value);
    }

    /**
     * 从dataMap中获取theadColumn的值
     *
     * @param theadColumn
     * @param dataMap
     * @param defaultValue 如果值为null，则用defaultValue代替
     * @return
     */
    public static String getValueStr(TheadColumn theadColumn, Map<String, Object> dataMap, String defaultValue) {
        String value = getValueStr(theadColumn, dataMap);
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    /**
     * 根据计算函数计算dataList中theadColumn列的值
     *
     * @param theadColumn
     * @param aggFunc
     * @param dataList
     * @return
     */
    public static String getValue(TheadColumn theadColumn, AggFunc aggFunc, List<Map<String, Object>> dataList) {
        if (AggFunc.SUM == aggFunc) {
            return getValueStr(theadColumn, getSumValue(theadColumn, dataList));
        } else if (AggFunc.COUNT == aggFunc) {
            return getCountValue(theadColumn, dataList) + "";
        } else if (AggFunc.AVG == aggFunc) {
            return getValueStr(theadColumn, getAvgValue(theadColumn, dataList));
        } else if (AggFunc.MAX == aggFunc) {
            return getValueStr(theadColumn, getMaxValue(theadColumn, dataList));
        } else if (AggFunc.MIN == aggFunc) {
            return getValueStr(theadColumn, getMinValue(theadColumn, dataList));
        }
        return null;
    }

    /**
     * 计算列的sum值
     *
     * @param theadColumn
     * @param dataList
     * @return
     */
    public static Object getSumValue(TheadColumn theadColumn, List<Map<String, Object>> dataList) {
        String name = theadColumn.getName();
        if (name == null) {
            return 0d;
        }
        Double sumValue = 0D;
        for (Map<String, Object> rowMap : dataList) {
            sumValue += MapUtils.getDoubleValue(rowMap, name, 0d);
        }
        return sumValue;
    }

    /**
     * 计算列的sum值
     *
     * @param theadColumn
     * @param dataList
     * @return
     */
    public static long getCountValue(TheadColumn theadColumn, List<Map<String, Object>> dataList) {
        long sumValue = dataList.stream().count();
        return sumValue;
    }

    /**
     * 计算列的平均值值
     *
     * @param theadColumn
     * @param dataList
     * @return
     */
    public static Double getAvgValue(TheadColumn theadColumn, List<Map<String, Object>> dataList) {
        Double sumValue = (Double) getSumValue(theadColumn, dataList);
        double avgValue = sumValue / dataList.size();
        return avgValue;
    }

    /**
     * 计算列的最大值
     *
     * @param theadColumn
     * @param dataList
     * @return
     */
    public static Double getMaxValue(TheadColumn theadColumn, List<Map<String, Object>> dataList) {
        String name = theadColumn.getName();
        Double maxValue = null;
        Double value;
        for (Map<String, Object> rowMap : dataList) {
            value=MapUtils.getDoubleValue(rowMap, name, 0d);
            if (maxValue == null) {
                maxValue = value;
            } else {
                maxValue = maxValue > value ? maxValue : value;
            }
        }
        return maxValue;
    }

    /**
     * 计算列的最小值
     *
     * @param theadColumn
     * @param dataList
     * @return
     */
    public static Double getMinValue(TheadColumn theadColumn, List<Map<String, Object>> dataList) {
        String name = theadColumn.getName();
        Double minValue = null;
        Double value;
        for (Map<String, Object> rowMap : dataList) {
            value=MapUtils.getDoubleValue(rowMap, name, 0d);
            if (minValue == null) {
                minValue = value;
            } else {
                minValue = minValue < value ? minValue : value;
            }
        }
        return minValue;
    }

    /**
     * 计算列的乘积
     *
     * @param theadColumn
     * @param dataList
     * @return
     */
    public static Double getProductValue(TheadColumn theadColumn, List<Map<String, Object>> dataList) {
        String name = theadColumn.getName();
        Double productValue = null;
        Double value;
        for (Map<String, Object> rowMap : dataList) {
            value=MapUtils.getDoubleValue(rowMap, name, 0d);
            if (productValue == null) {
                productValue = value;
            } else {
                productValue = productValue * value;
            }
        }
        return productValue;
    }

}

easy-data
==========

easy-data是一个简单的数据处理工具，主要用于根据给定的表头信息和List<Map<String,Object>>数据可以计算出复杂表头（多行表头跨行跨列）、数据合并相同的单元格以及将数据转化为Excel透视表，同时支持导出Excel和CSV文件的工具。 

## 说明 
### 功能
* 根据表头信息计算出多行以及支持跨行跨列的表头
* 根据给定的表头和数据计算出相同的数据合并单元格
* 可以根据设定的表头信息导出各种样式（对齐方式、单元格背景色、字体样式）的Excel文件
* 提供导出CSV文件
* 根据给定的表头、数据、以及透视表相关信息将数据转化为透视表

### 预备知识
#### 表头说明
表头的相关属性定义在类TheadColumn中，其中主要的属性是name和text

|属性|类型|默认值|注释|
|:---|:---|:---|:---|
|id|String|null|列ID|
|pid|String|null|父级列ID|
|name|String|null|列名（一般对应数据库的列名）|
|text|String|null|列的中文名|
|defaultValue|Object|null|列值为空时的默认值|
|precision|Integer|null|列的精度（数值类型有效）|
|dataType|DataType|null|列数据类型|
|isHidden|boolean|false|列是否隐藏（如HTML页面隐藏）|
|downMergeCells|boolean|false|相同的数值列是否向下合并单元格|
|columnWidth|Double|false|列宽度|
|theadRowHeight|Double|null|表头行高|
|dataRowHeight|Double|null|数据行高|
|theadBGColor|String|null|表头单元格的字体颜色（如：FFFFFFF）|
|dataBGColor|String|null|数据单元格的背景颜色（如：FFFFFFF）|
|theadFontColor|String|null|表头的字体颜色（如：FFFFFFF）|
|dataFontColor|String|null|数据部分的字体颜色（如：FFFFFFF）|
|theadTextAlign|TextHorizontalAlignment|null|表头部分对齐方式|
|dataTextAlign|TextHorizontalAlignment|null|数据部分列对齐方式|
|theadVerticalAlign|TextVerticalAlignment|null|表头文字的垂直对齐|
|dataVerticalAlign|TextVerticalAlignment|null|数据文字的垂直对齐|
|theadBold|boolean|false|表头是否加粗|
|dataBold|boolean|false|数据是否加粗|
|isStatistics|boolean|false|否在尾部追加统计值|

#### 数据说明
数据列表List<Map<String,Object>>，其中Map<String,Object>代表数据库的一行记录。

#### 测试数据

系统使用如下测试数据

![](https://github.com/myjgithubdl/easy-data/raw/master/docs/assets/imgs/test-data.png)


#### 导出Excel

* 导出Excel的参数封装在ExportExcelParams类里，支持的参数如下

|属性|类型|默认值|注释|
|:---|:---|:---|:---|
|sheetName|String|null|导出Excel的Sheet名称|
|type|ExcelType|XSSF|导出Excel的类型|
|theadColumnList|List<TheadColumn>|null|表头|
|dataList|List<Map<String, Object>>|null|数据|
|excelCellStyleList|Object|null|自定义的单元格样式|

* 导出示例

```
public static void testExportExcel  (){
        //数据
        List<Map<String, Object>> dataList = MySQLData.getDataList();
        //表头
        TheadColumn provinceTheadColumn=new TheadColumn("province" , null ,"province","省份" );
        provinceTheadColumn.setDownMergeCells(true);//向下合并单元格值相同的列

        TheadColumn areaTheadColumn=new TheadColumn("area" , null ,"area","城市" );
        areaTheadColumn.setDownMergeCells(true);//向下合并单元格值相同的列

        TheadColumn dtTheadColumn=new TheadColumn("dt" , null ,"dt","日期" );
        TheadColumn aqiTheadColumn=new TheadColumn("aqi" , null ,"aqi","空气质量指数" );
        TheadColumn aqiRangeTheadColumn=new TheadColumn("aqi-range" , null ,"aqi-range","空气质量指数范围" );
        TheadColumn qualityTheadColumn=new TheadColumn("quality" , null ,"quality","空气质量" );
        TheadColumn pm25TheadColumn=new TheadColumn("pm25" , null ,"pm25","pm2.5" );
        TheadColumn pm10TheadColumn=new TheadColumn("pm10" , null ,"pm10","pm10" );

        TheadColumn yhwTheadColumn=new TheadColumn("yhw" , null ,null,"氧化物" );
        yhwTheadColumn.setTheadTextAlign(TextHorizontalAlignment.CENTER);//文字居中对齐

        TheadColumn so2TheadColumn=new TheadColumn("so2" , "yhw" ,"so2","二氧化硫" );
        TheadColumn coTheadColumn=new TheadColumn("co" , "yhw" ,"co","一氧化碳" );
        TheadColumn no2TheadColumn=new TheadColumn("no2" , "yhw" ,"no2","二氧化氮" );
        TheadColumn o3TheadColumn=new TheadColumn("o3" , "yhw" ,"o3","臭氧" );

        List<TheadColumn> theadColumnList=new ArrayList<>();
        theadColumnList.add(provinceTheadColumn);
        theadColumnList.add(areaTheadColumn);
        theadColumnList.add(dtTheadColumn);
        theadColumnList.add(aqiTheadColumn);
        theadColumnList.add(aqiRangeTheadColumn);
        theadColumnList.add(qualityTheadColumn);
        theadColumnList.add(pm25TheadColumn);
        theadColumnList.add(pm10TheadColumn);
        theadColumnList.add(yhwTheadColumn);
        theadColumnList.add(so2TheadColumn);
        theadColumnList.add(coTheadColumn);
        theadColumnList.add(no2TheadColumn);
        theadColumnList.add(o3TheadColumn);


        OutputStream outExcel= null;
        OutputStream outCSV= null;
        try {
            outExcel = new FileOutputStream("E:\\Myron\\天气数据.xlsx");
            outCSV = new FileOutputStream("E:\\Myron\\天气数据.csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //导出Excel
        ExportExcelUtil.exportExcel(outExcel,"天气数据" ,theadColumnList ,dataList);

        //导出CSV(设置的表头样式失效)
        ExportCSVUtil.exportCSV(outCSV,theadColumnList ,dataList);

    }
```

利用上面的测试数据执行上面代码后得到的结果如下：

调用导出方法

```
ExportExcelUtil.exportExcel(outExcel,"天气数据" ,theadColumnList ,dataList);
```

![](https://github.com/myjgithubdl/easy-data/raw/master/docs/assets/imgs/export-excel.png)


#### 导出CSV

* 导出CSV的参数封装在ExportCSVParams类，如果在标题一属性中设置了表头和数据部门样式是无效的，支持的参数如下：

|属性|类型|默认值|注释|
|:---|:---|:---|:---|
|exportThead|boolean|true|是否导出表头|
|theadColumnList|List<TheadColumn>|null|表头列| 
|dataList|List<Map<String, Object>>|null|数据|
|charsetName|String|UTF-8|导出的文件编码|

利用上面的测试数据执行上面代码后得到的结果如下：

调用导出方法
```
ExportCSVUtil.exportCSV(outCSV,theadColumnList ,dataList);
```

![](https://github.com/myjgithubdl/easy-data/raw/master/docs/assets/imgs/export-csv.png)

#### 透视表

工具提供了计算类似于Excel透视表数据的功能，透视相关的数据是封装在PivotTableDataCore类里。

|属性|类型|默认值|注释|
|:---|:---|:---|:---|
|pivotTable|PivotTable|null|设置的透视表信息|
|theadColumnList|List<TheadColumn>|null|原始表头信息| 
|dataList|List<Map<String, Object>>|null|数据|
|theadColumnMap|Map<String, TheadColumn>|null|原始表头信息  Map|
|pivotTableTheadColumnList|List<TheadColumn>|null|化后透视表转化后的表头|
|pivotTableDataList|Map<String, List<Map<String, Object>>>|null|透视表转化后的数据|
|groupByRowMap|Map<String, Map<String, List<Map<String, Object>>>>|null|透视表按照设置的行将数据转化为的数据|
|groupByRowMapNewColCalData|Map<String, TheadColumn>|null|在根据透视表设置的列转化出新的表头对应的数据|
|colValuesMap|Map<String, List<String>>|null|透视表中设置的列转化为的透视表新列|

PivotTable封装了透视表的相关信息，其属性如下

|属性|类型|默认值|注释|
|:---|:---|:---|:---|
|rows|List<String>|null|透视表中的选择的行名称集|
|cols|List<String>|null|透视表中的选择的列名称集|
|calCols|List<PivotTableCalCol>|null|透视表中的选择的值集,计算列和计算的函数|

PivotTableCalCol封装了透视表的计算列以及计算的函数类型，其属性如下

|属性|类型|默认值|注释|
|:---|:---|:---|:---|
|name|String|null|值列名|
|aggFunc|AggFunc|null|计算该列需要使用的聚合函数|

支持的函数如下：

SUM("求和"),
COUNT("计数"),
AVG("均值"),
MAX("最大值"),
MIN("最小值"),
PRODUCT("乘积")

导出示例

```
public static void test() {
        //数据
        List<Map<String, Object>> dataList = MySQLData.getDataList();
        //表头列表
        List<TheadColumn> theadColumnList = MySQLData.getTheadColumnList();
        OutputStream outExcel = null;
        OutputStream outCSV = null;
        try {
            outExcel = new FileOutputStream("E:\\Myron\\天气数据-透视表.xlsx");
            outCSV = new FileOutputStream("E:\\Myron\\天气数据-透视表.csv");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //行
        List<String> rows = new ArrayList<>();
        rows.add("province");
        rows.add("area");

        //列
        List<String> cols = new ArrayList<>();
        cols.add("quality");

        //计算函数
        List<PivotTableCalCol> calCols = new ArrayList<>();
        AggFunc max = AggFunc.MAX;
        max.setText("pm25最大值");
        PivotTableCalCol pivotTableCalColpm25Max = new PivotTableCalCol("pm25", max);

        AggFunc min = AggFunc.MIN;
        min.setText("pm25最小值");
        PivotTableCalCol pivotTableCalColpm25MIn = new PivotTableCalCol("pm25", min);

        AggFunc avg = AggFunc.AVG;
        avg.setText("pm25均值");
        PivotTableCalCol pivotTableCalColpm25AVG = new PivotTableCalCol("pm25",avg);
        calCols.add(pivotTableCalColpm25Max);
        calCols.add(pivotTableCalColpm25MIn);
        calCols.add(pivotTableCalColpm25AVG);

        //导出CSV
        PivotTableDataUtil.exportPivotTableDataCsvFile(rows, cols, calCols, theadColumnList, dataList, outCSV);
        
        //导出Excel
        PivotTableDataUtil.exportPivotTableDataExcelFile(rows, cols, calCols, theadColumnList, dataList,"天气数据", outExcel);

    }
```

利用之前截图的测试数据执行代码后生产如下文件内容

![](https://github.com/myjgithubdl/easy-data/raw/master/docs/assets/imgs/pivot-table.png)

## 捐助

![](https://github.com/myjgithubdl/easy-data/raw/master/docs/assets/imgs/alipay-code.png)

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
|theadBGColor|String|null|表头单元格的字体颜色|
|dataBGColor|String|null|数据单元格的背景颜色|
|theadFontColor|String|null|表头的字体颜色|
|dataFontColor|String|null|数据部分的字体颜色|
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
如果有如下测试数据。
![测试数据]:(https://github.com/myjgithubdl/easy-data/raw/master/docs/docs/assets/imgs/test-data.png)


#### 导出Excel


## 捐助

![微信]:(https://github.com/myjgithubdl/easy-data/raw/master/docs/assets/imgs/alipay-code.png)

package com.easydate.test;

import com.easydata.enmus.ExcelType;
import com.easydata.export.ExportExcelUtil;
import com.easydata.export.excel.ExportExcelParams;
import com.easydata.pivottable.core.PivotTableDataCore;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.*;

/**
 * Created by MYJ on 2018/1/29.
 */
public class PivotTableTestExportExcel {

    public static void main(String[] args) throws IOException {
        //test1();
        test2();


    }

    public static void test1()  throws IOException{

        PivotTableDataCore pivotTableDataServer = PivotTableTestData.getPivotTableData();

        File file=new File("E:\\Myron\\lakala\\需求文档\\数据透视表\\测试数据\\透视表测试.xlsx");
        if(file.exists()){
            file.delete();
        }
        OutputStream out1 = new FileOutputStream(file);
        ExportExcelParams exportParams=new ExportExcelParams();
        exportParams.setType(ExcelType.HSSF);
        exportParams.setSheetName("sssssssssssssss");
        exportParams.setDataList(pivotTableDataServer.getPivotTableDataList());
        exportParams.setTheadColumnList(pivotTableDataServer.getPivotTableTheadColumnList());
        Workbook workbook = ExportExcelUtil.exportExcel(exportParams);
        workbook.write(out1);
        out1.flush();
        out1.close();

    }



    public static void test2  (){
        StudentScoreTestData pivotTableTestData=new StudentScoreTestData();

        PivotTableDataCore pivotTableDataCore = pivotTableTestData.getPivotTableData();

        OutputStream out= null;
        try {
            out = new FileOutputStream("E:\\Myron\\lakala\\需求文档\\数据透视表\\测试数据\\学生成绩透视表.xlsx");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ExportExcelUtil.exportExcel(out,"学生成绩透视表" ,pivotTableDataCore.getPivotTableTheadColumnList() ,pivotTableDataCore.getPivotTableDataList() );

    }
}

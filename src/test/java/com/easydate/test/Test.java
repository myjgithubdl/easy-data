package com.easydate.test;

import com.easydata.pivottable.enmus.AggFunc;

public class Test {

    public static void main(String[] args) {

        AggFunc avg = AggFunc.AVG;
        System.out.println(avg+"");
        System.out.println(avg.getText());


        System.out.println(AggFunc.getAggFunc("MIN"));

    }


}

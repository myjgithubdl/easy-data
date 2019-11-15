package com.easydate.test;

import com.easydata.pivottable.enmus.AggFunc;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;
import java.util.Map;

public class Test {

    public static void main(String[] args) {
        test1();

    }

    public static void test1(){

        Map<String , Object> map= new HashMap<>();
        map.put("a","a");
        System.out.println(MapUtils.getDouble(map,"a",0d));;

    }


}

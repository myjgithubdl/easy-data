package com.easydate.test;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MySQLData {


    public static List<Map<String, Object>> getDataList() {

        List<Map<String, Object>> dataList = new ArrayList<>();
        List<String> lines = null;
        try {
            lines = FileUtils.readLines(new File("D:\\ProgramFiles\\study\\ideaworkspace\\easy-data\\docs\\manual\\test-data.txt"), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] keyArray = lines.get(0).split(",");

        Arrays.asList(keyArray).stream().forEach(System.out::println);

        int index = 0;
        for (String line : lines) {
            index++;
            if (index == 1) {
                continue;
            }


            Map<String, Object> map = new HashMap<>();
            String[] dataArray = line.split(",");

            for (int j = 0; j < keyArray.length; j++) {
                map.put(keyArray[j], dataArray[j]);
            }

            dataList.add(map);
        }

        return dataList;

    }


    public static void main(String[] args) {
        System.out.println(getDataList());
    }

}

package com.example.myalarmclock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//放鬧鐘設定資料
public class Post {


//    public String clockDate;
//    public String clockTime;
//    public String clockName;

    public static void Post(String[] args){

        String[] number = {"A", "B", "C"};
        String[] name = {"Amy", "Bessy", "Cathy"};

        List<Map<String, Object>> listitem = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < number.length; i++) {
            Map<String, Object> showitem = new HashMap<String, Object>();
            showitem.put("number", number[i]);
            showitem.put("name", name[i]);
            listitem.add(showitem);
        }

    }

//    public Post(String clockDate, String clockTime, String clockName) {
//        this.clockDate = clockDate;
//        this.clockTime = clockTime;
//        this.clockName = clockName;
//    }
}

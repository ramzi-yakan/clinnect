package com.example.walkinclinic;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Availability {

    public int startHour, startMin, endHour, endMin;
    public String day, id;

    //so many variables! it complements the DatePicker and TimePicker though

    public Availability() {}

    public Availability(int startHour, int startMin, int endHour, int endMin, String day, String id) {
        this.startHour = startHour;
        this.startMin = startMin;
        this.endHour = endHour;
        this.endMin = endMin;
        this.day = day;
        this.id = id;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();

        map.put("startHour",this.startHour);
        map.put("startMin",this.startMin);
        map.put("endHour",this.endHour);
        map.put("endMin",this.endMin);
        map.put("day",this.day);
        map.put("id",this.id);

        return map;
    }

    @Override
    public String toString() {
        String r = day+", ";
        Calendar date = Calendar.getInstance();
        date.set(0,0,0,startHour,startMin,0);
        r += new SimpleDateFormat("HH:mm").format(date.getTime())+"-";
        date.set(0,0,0,endHour,endMin,0);
        r += new SimpleDateFormat("HH:mm").format(date.getTime());
        return r;
    }

}

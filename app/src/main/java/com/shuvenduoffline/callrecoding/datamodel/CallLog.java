package com.shuvenduoffline.callrecoding.datamodel;

import android.content.Context;

import com.shuvenduoffline.callrecoding.Database.Database;

public class CallLog {
    public String name;
    public int id;
    public long start_time;
    public long end_time;
    public String filepath;
    public String phonenumber;


    public CallLog() {
    }

    public CallLog(String name, int id, long start_time, long end_time, String filepath, String phonenumber) {
        this.name = name;
        this.id = id;
        this.start_time = start_time;
        this.end_time = end_time;
        this.filepath = filepath;
        this.phonenumber = phonenumber;
    }

    public void save(Context context) {
        Database.getInstance(context).addCall(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getStart_time() {
        return start_time;
    }

    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    public long getEnd_time() {
        return end_time;
    }

    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getDuration() {
        //duration calculation
        int duration = (int) (end_time - start_time);
        duration = duration/1000;
        int h = duration / 3600;
        int m = (duration % 3600) / 60;
        int s = duration % 60;
        String sh = (h > 0 ? String.valueOf(h) + " " + "h" : "");
        String sm = (m < 10 && m > 0 && h > 0 ? "0" : "") + (m > 0 ? (h > 0 && s == 0 ? String.valueOf(m) : String.valueOf(m) + " " + "min") : "");
        String ss = (s == 0 && (h > 0 || m > 0) ? "" : (s < 10 && (h > 0 || m > 0) ? "0" : "") + String.valueOf(s) + " " + "sec");
        return sh + (h > 0 ? " " : "") + sm + (m > 0 ? " " : "") + ss;

    }
}
package com.java.wuguohao.bean;

import android.util.Log;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.List;

public class NewsData extends SugarRecord {
    String place;
    String begin;
    String confirmed;
    String cured;
    String dead;
    public NewsData(){
        Log.i("in NewsData constructer","call the empty constructer");
    }

    public NewsData(String place_, String begin_, String confirmed_, String cured_, String dead_){
        place = place_;
        begin = begin_;
        confirmed = confirmed_;
        cured = cured_;
        dead = dead_;
    }

    public void print(){
        System.out.println("begin = "+begin+" place = "+place+" confirmed = "+confirmed+" \ncured = "+cured+" \ndead = "+dead);
    }

    public String getBegin() {
        return begin;
    }

    public String getPlace(){
        return place;
    }
    public List<Integer> getConfirmed(){
//        Log.e("in getConfirmed",confirmed);
        String []confirmed_list = confirmed.split(" ");
        List<Integer> toInteger = new ArrayList<>();
        int len = confirmed_list.length;
        for(int i = 0;i < len;i++){
            toInteger.add(Integer.parseInt(confirmed_list[i]));
        }
        return toInteger;
    }
    public List<Integer> getCured(){
        String []cured_list = cured.split(" ");
        List<Integer> toInteger = new ArrayList<>();
        int len = cured_list.length;
        for(int i = 0;i < len;i++){
            toInteger.add(Integer.parseInt(cured_list[i]));
        }
        return toInteger;
    }
    public List<Integer> getDead(){
        String []dead_list = dead.split(" ");
        List<Integer> toInteger = new ArrayList<>();
        int len = dead_list.length;
        for(int i = 0;i < len;i++){
            toInteger.add(Integer.parseInt(dead_list[i]));
        }
        return toInteger;
    }
}

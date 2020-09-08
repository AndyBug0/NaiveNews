package com.java.wuguohao.search;

import android.util.Log;

import com.java.wuguohao.bean.NewsData;

import java.util.ArrayList;
import java.util.List;

public class SearchNewsData extends SearchItem {
     public List<String> searchSubString(String subString){
         NewsData.find(NewsData.class,"place=?",subString);
         List<NewsData> list = NewsData.findWithQuery(NewsData.class, "SELECT * FROM NEWS_DATA WHERE place like ?","%"+subString+"%");
         List<String> strings = new ArrayList<>();
         for(NewsData data : list){
                strings.add(data.getPlace());
         }
         return strings;
     }
}

package com.java.wuguohao.search;

import com.java.wuguohao.bean.NewsData;
import com.java.wuguohao.bean.NewsEvent;

import java.util.ArrayList;
import java.util.List;

public class SearchNewsEvent {

    public List<String> searchTitle(String subString){
        List<NewsEvent> list = NewsEvent.findWithQuery(NewsEvent.class, "SELECT * FROM NEWS_EVENT WHERE title like ?","%"+subString+"%");
        List<String> strings = new ArrayList<>();
        for(NewsEvent event : list){
            strings.add(event.getID());
        }
        return strings;
    }
    public List<String> searchContent(String subString){
        List<NewsEvent> list = NewsEvent.findWithQuery(NewsEvent.class, "SELECT * FROM NEWS_EVENT WHERE content like ?","%"+subString+"%");
        List<String> strings = new ArrayList<>();
        for(NewsEvent event : list){
            strings.add(event.getID());
        }
        return strings;
    }
}

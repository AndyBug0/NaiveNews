package com.java.wuguohao.bean;

import com.java.wuguohao.news.NewsPageActivity;
import com.orm.SugarRecord;

import java.util.List;

public class NewsMapEntity extends SugarRecord {
    String entity;
    List<String> urls;
    public NewsMapEntity(){}
    public NewsMapEntity(String entity, List<String> urls) {
        this.entity = entity;
        this.urls = urls;
    }

    public String getEntity() {
        return entity;
    }

    public List<String> getUrls() {
        return urls;
    }
}

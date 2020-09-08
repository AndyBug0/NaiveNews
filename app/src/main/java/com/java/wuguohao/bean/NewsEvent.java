package com.java.wuguohao.bean;

import com.orm.SugarRecord;

public class NewsEvent extends SugarRecord{
    String _id;
    String aminer_id;
    String authors;
    String category;
    String content;
    String date;
    String doi;
    //entities
    String entities;

    String geoinfo;
    String influence;
    String lang;
    String pdf;
    String regionIDs;
    //related_events
    String related_events;

    String seg_text;
    String source;
    String tflag;
    String time;
    String title;
    String type;
    String urls;
    String year;

    public boolean isRead;

    public String getID() { return _id; }
    public String getTitle() { return title; }
    public String getSource() { return source; }
    public String getDate() { return date; }
    public String getType() { return type; }
    public String getContent() { return content; }
    public  NewsEvent(){}
    public NewsEvent(String _id, String aminer_id, String authors, String category, String content, String date, String doi, String entities,
                     String geoinfo, long id, String influence, String lang, String pdf, String regionIDs, String related_events,
                     String seg_text, String source, String tflag, String time, String title, String type, String urls, String year, boolean isRead){
        this._id = _id; this.aminer_id = aminer_id;  this.category = category; this.content = content; this.date = date; this.doi = doi;
//         this.id = id;    //todo may wrong
        this.influence = influence; this.lang = lang; this.pdf = pdf;  this.year = year;
        this.seg_text = seg_text; this.source = source; this.tflag = tflag; this.time = time; this.title = title; this.type = type;
        this.authors = authors;
        this.entities = entities;
        this.authors = regionIDs;
        this.related_events = related_events;
        this.urls = urls;
        this.isRead = isRead;
    }
}

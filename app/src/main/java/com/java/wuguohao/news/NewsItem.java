package com.java.wuguohao.news;

public class NewsItem {
    private String title;
    private String source;
    private String date;
    private String id;
    private String type;
    public boolean isRead = false;

    public NewsItem(String title, String source, String date, String id, String type, boolean isRead) {
        this.title = title;
        this.source = source;
        this.date = date;
        this.id = id;
        this.type = type;
        this.isRead = isRead;
    }

    public String getTitle() { return title; }
    public String getSource() { return source; }
    public String getDate() { return date; }
    public String getId() { return id; }
    public String getType() { return type; }
}


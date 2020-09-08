package com.java.wuguohao.news;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.java.wuguohao.R;

import java.util.List;

public class NewsAdapter extends BaseAdapter {
    Context context;
    List<NewsItem> itemList;

    public NewsAdapter(Context context, List<NewsItem> list) {
        this.context = context;
        itemList = list;
    }

    @Override
    public int getCount() { return itemList.size(); }

    @Override
    public Object getItem(int pos) { return itemList.get(pos); }

    @Override
    public long getItemId(int pos) { return pos; }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        View view = (convertView == null) ? View.inflate(context, R.layout.item_news, null) : convertView;
        NewsItem newsItem = itemList.get(pos);

        ((TextView) view.findViewById(R.id.item_news_title)).setText(newsItem.getTitle());
        ((TextView) view.findViewById(R.id.item_news_source)).setText(newsItem.getSource());
        ((TextView) view.findViewById(R.id.item_news_date)).setText(newsItem.getDate());

        if (newsItem.isRead) {
            ((TextView) view.findViewById(R.id.item_news_title)).setTextColor(context.getResources().getColor(R.color.gray));
        } else {
            ((TextView) view.findViewById(R.id.item_news_title)).setTextColor(context.getResources().getColor(R.color.black));
        }

        return view;
    }
}

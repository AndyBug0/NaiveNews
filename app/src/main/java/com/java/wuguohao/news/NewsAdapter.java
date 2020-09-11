package com.java.wuguohao.news;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

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

        ((TextView) view.findViewById(R.id.item_news_date)).setText(newsItem.getDate());

        TextView titleView = (TextView) view.findViewById(R.id.item_news_title);
        titleView.setMaxLines(20);
        titleView.setText(newsItem.getTitle());

        if (newsItem.isRead) {
            ((TextView) view.findViewById(R.id.item_news_title)).setTextColor(context.getResources().getColor(R.color.gray));
        } else {
            ((TextView) view.findViewById(R.id.item_news_title)).setTextColor(context.getResources().getColor(R.color.black));
        }

        ImageView sourceTitle = (ImageView) view.findViewById(R.id.item_news_from);
        TextView sourceView = (TextView) view.findViewById(R.id.item_news_source);
        if (newsItem.getType().equals("paper")) {
            sourceTitle.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_author));
            sourceView.setText(newsItem.getSource());
        } else {
            if (newsItem.getSource().isEmpty()) {
                sourceTitle.setVisibility(View.GONE);
                sourceView.setVisibility(View.GONE);
            } else {
                sourceView.setText(newsItem.getSource());
                sourceTitle.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_source));
            }
        }

        return view;
    }
}

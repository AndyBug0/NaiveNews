package com.java.wuguohao.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.java.wuguohao.DataHandler;
import com.java.wuguohao.R;
import com.java.wuguohao.bean.NewsEvent;
import com.java.wuguohao.news.NewsAdapter;
import com.java.wuguohao.news.NewsItem;
import com.java.wuguohao.news.NewsPageActivity;
import com.java.wuguohao.news.RefreshView;

import java.util.ArrayList;
import java.util.List;

public class ContentFragment extends Fragment implements RefreshView.LoadListener {

    private View viewContent;
    private String mType;
    private String mTitle;

    private RefreshView refreshView;
    private NewsAdapter adapter;
    private List<NewsItem> content;
    private int listPageNum;

    public String getTitle() { return mTitle; }
    public String getFragmentType() { return mType; }

    public ContentFragment(String title, String type) {
        this.mTitle = title;
        this.mType = type;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //布局文件中只有一个居中的TextView
        viewContent = inflater.inflate(R.layout.fragment_content,container,false);
        refreshView = (RefreshView) viewContent.findViewById(R.id.refresh_view);
        refreshView.setInteface(this);

        listPageNum = 1;
        content = new ArrayList<NewsItem>();
        List<NewsEvent> newsEventList = NewsEvent.listAll(NewsEvent.class);
        for (NewsEvent news : newsEventList) {
            content.add(new NewsItem(news.getTitle(), news.getSource(), news.getDate(), news.getID(), news.isRead));
        }
        adapter = new NewsAdapter(getActivity(), content);
        refreshView.setAdapter(adapter);
        refreshView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), NewsPageActivity.class);
                NewsItem item = content.get(i - 1);
                item.isRead = true;
                adapter.notifyDataSetChanged();
                intent.putExtra("id", item.getId());
                startActivity(intent);
            }
        });
        return viewContent;
    }

    // 实现refresh接口
    @Override
    public void onRefresh() {
        listPageNum = 1;

        DataHandler dataHandler = new DataHandler();
        try {
            dataHandler.readEvent(listPageNum, 10, mType, this.getContext());
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        content.clear();
        for (String id : dataHandler.getIdList()) {
            NewsEvent news = NewsEvent.find(NewsEvent.class, "_id=?", id).get(0);
            content.add(new NewsItem(news.getTitle(), news.getSource(), news.getDate(), news.getID(), news.isRead));
        }
        adapter.notifyDataSetChanged();
        refreshView.loadComplete();
    }

    // 实现onLoad接口
    @Override
    public void onLoad() {
        listPageNum ++;
        DataHandler dataHandler = new DataHandler();

        try {
            dataHandler.readEvent(listPageNum, 10, mType, this.getContext());
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (String id : dataHandler.getIdList()) {
            NewsEvent news = NewsEvent.find(NewsEvent.class, "_id=?", id).get(0);
            content.add(new NewsItem(news.getTitle(), news.getSource(), news.getDate(), news.getID(), news.isRead));
        }
        adapter.notifyDataSetChanged();
        refreshView.loadComplete();
    }
}
package com.java.wuguohao.ui.home;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.java.wuguohao.DataHandler;
import com.java.wuguohao.R;
import com.java.wuguohao.bean.NewsEvent;
import com.java.wuguohao.news.NewsAdapter;
import com.java.wuguohao.news.NewsItem;
import com.java.wuguohao.news.NewsPageActivity;
import com.java.wuguohao.news.RefreshView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class ContentFragment extends Fragment implements RefreshView.LoadListener {

    private View viewContent;
    private String mType;
    private String mTitle;

    private TextView noContentInfo;
    private RefreshView refreshView;
    private NewsAdapter adapter;
    private List<NewsItem> content = new ArrayList<>();
    private int listPageNum;

    public String getTitle() { return mTitle; }
    public String getFragmentType() { return mType; }

    public int compareDate(NewsEvent event1, NewsEvent event2) {
        String [] date1 = event1.getDate().split("[ ,:]");
        String [] date2 = event2.getDate().split("[ ,:]");

        int year1 = Integer.parseInt(date1[4]), year2 = Integer.parseInt(date2[4]);
        int month1 = month.get(date1[3]), month2 = month.get(date2[3]);
        int day1 = Integer.parseInt(date1[2]), day2 = Integer.parseInt(date2[2]);
        int hour1 = Integer.parseInt(date1[5]), hour2 = Integer.parseInt(date2[5]);
        int minute1 = Integer.parseInt(date1[6]), minute2 = Integer.parseInt(date2[6]);
        int second1 = Integer.parseInt(date1[7]), second2 = Integer.parseInt(date2[7]);
        if (year1 == year2) {
            if (month1 == month2) {
                if (day1 == day2) {
                    if (hour1 == hour2) {
                        if (minute1 == minute2) {
                            return second2 - second1;
                        } else {
                            return minute2 - minute1;
                        }
                    } else {
                        return hour2 - hour1;
                    }
                } else {
                    return day2 - day1;
                }
            } else {
                return month2 - month1;
            }
        } else {
            return year2 - year1;
        }
    }

    private static HashMap<String, Integer> month = new HashMap<>();
    static {
        month.put("Jan", 1);
        month.put("Feb", 2);
        month.put("Mar", 3);
        month.put("Apr", 4);
        month.put("May", 5);
        month.put("Jun", 6);
        month.put("Jul", 7);
        month.put("Aug", 8);
        month.put("Sep", 9);
        month.put("Oct", 10);
        month.put("Nov", 11);
        month.put("Dec", 12);
    }

    public ContentFragment(String title, String type) {
        this.mTitle = title;
        this.mType = type;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //布局文件中只有一个居中的TextView
        viewContent = inflater.inflate(R.layout.fragment_content,container,false);
        noContentInfo = (TextView) viewContent.findViewById(R.id.no_content);
        refreshView = (RefreshView) viewContent.findViewById(R.id.refresh_view);
        refreshView.setInteface(this);

        listPageNum = 1;
        List<NewsEvent> newsEventList;
        if (mType.equals("all")) {
            newsEventList = NewsEvent.listAll(NewsEvent.class);
        } else {
            newsEventList = NewsEvent.find(NewsEvent.class, "type=?", mType);
        }
        if (newsEventList.isEmpty()) {
            refreshView.setVisibility(View.GONE);
            refreshView.setClickable(false);
            noContentInfo.setVisibility(View.VISIBLE);
        } else {
            newsEventList.sort(new Comparator<NewsEvent>() {
                @Override
                public int compare(NewsEvent newsEvent, NewsEvent t1) {
                    return compareDate(newsEvent, t1);
                }
            });
            int num = (newsEventList.size() < 15) ? newsEventList.size() : 15;
            for (int i = 0; i < num; i ++) {
                NewsEvent news = newsEventList.get(i);
                if (news.getType().equals("news")) {
                    content.add(new NewsItem(news.getTitle(), news.getSource(), news.getDate(), news.getID(), news.isRead));
                } else if (news.getType().equals("paper")) {
                    content.add(new NewsItem(news.getTitle(), news.getAuthors(), news.getDate(), news.getID(), news.isRead));
                }
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

            noContentInfo.setVisibility(View.GONE);
            refreshView.setVisibility(View.VISIBLE);
            refreshView.setClickable(true);
        }

        noContentInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRefresh();
            }
        });

        return viewContent;
    }

    // 实现refresh接口
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onRefresh() {
        listPageNum = 1;

        DataHandler dataHandler = new DataHandler();
        try {
            dataHandler.readEvent(listPageNum, 15, mType, this.getContext());
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        content.clear();
        List<NewsEvent> newsEventList;
        if (mType.equals("all")) {
            newsEventList = NewsEvent.listAll(NewsEvent.class);
        } else {
            newsEventList = NewsEvent.find(NewsEvent.class, "type=?", mType);
        }
        if (newsEventList.isEmpty()) {
            refreshView.setVisibility(View.GONE);
            refreshView.setClickable(false);
            noContentInfo.setVisibility(View.VISIBLE);
        } else {
            newsEventList.sort(new Comparator<NewsEvent>() {
                @Override
                public int compare(NewsEvent newsEvent, NewsEvent t1) {
                    return compareDate(newsEvent, t1);
                }
            });
            int num = (newsEventList.size() < 15) ? newsEventList.size() : 15;
            for (int i = 0; i < num; i++) {
                NewsEvent news = newsEventList.get(i);
                if (news.getType().equals("news")) {
                    content.add(new NewsItem(news.getTitle(), news.getSource(), news.getDate(), news.getID(), news.isRead));
                } else if (news.getType().equals("paper")) {
                    content.add(new NewsItem(news.getTitle(), news.getAuthors(), news.getDate(), news.getID(), news.isRead));
                }
            }
            adapter.notifyDataSetChanged();
        }
        refreshView.loadComplete();
    }

    // 实现onLoad接口
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onLoad() {
        listPageNum ++;
        DataHandler dataHandler = new DataHandler();

        try {
            dataHandler.readEvent(listPageNum, 15, mType, this.getContext());
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<NewsEvent> newsEventList;
        if (mType.equals("all")) {
            newsEventList = NewsEvent.listAll(NewsEvent.class);
        } else {
            newsEventList = NewsEvent.find(NewsEvent.class, "type=?", mType);
        }
        newsEventList.sort(new Comparator<NewsEvent>() {
            @Override
            public int compare(NewsEvent newsEvent, NewsEvent t1) {
                return compareDate(newsEvent, t1);
            }
        });
        for (int i = 0; i < 15; i++) {
            NewsEvent news = newsEventList.get(i + 15 * (listPageNum - 1));
            if (news.getType().equals("news")) {
                content.add(new NewsItem(news.getTitle(), news.getSource(), news.getDate(), news.getID(), news.isRead));
            } else if (news.getType().equals("paper")) {
                content.add(new NewsItem(news.getTitle(), news.getAuthors(), news.getDate(), news.getID(), news.isRead));
            }
        }
        adapter.notifyDataSetChanged();

        refreshView.loadComplete();
    }
}
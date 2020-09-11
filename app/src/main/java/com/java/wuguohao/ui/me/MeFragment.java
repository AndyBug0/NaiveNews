package com.java.wuguohao.ui.me;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.java.wuguohao.R;
import com.java.wuguohao.bean.NewsEvent;
import com.java.wuguohao.news.NewsAdapter;
import com.java.wuguohao.news.NewsItem;
import com.java.wuguohao.news.NewsPageActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MeFragment extends Fragment implements SlideListView.RemoveListener {
    private List<NewsItem> newsList = new ArrayList<>();
    private SlideListView historyList;
    private TextView emptyInfo;
    private ImageView clear;
    private NewsAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_me, container, false);
        clear = (ImageView) root.findViewById(R.id.history_clear);
        historyList = (SlideListView) root.findViewById(R.id.history_list);
        emptyInfo = (TextView) root.findViewById(R.id.empty_info);
        historyList.setRemoveListener(this);

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("history", Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = sharedPreferences.edit();
                for (NewsItem item : newsList) {
                    NewsEvent news = NewsEvent.find(NewsEvent.class, "_id=?", item.getId()).get(0);
                    news.isRead = false;
                    news.save();
                }
                newsList.clear();
                editor.clear();
                editor.apply();
                historyList.setVisibility(View.GONE);
                historyList.setClickable(false);
                emptyInfo.setVisibility(View.VISIBLE);
            }
        });

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("history", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        List<String> ids = new ArrayList<String>(sharedPreferences.getStringSet("id", new HashSet<String>()));
        if (ids.isEmpty()) {
            historyList.setVisibility(View.GONE);
            historyList.setClickable(false);
            emptyInfo.setVisibility(View.VISIBLE);
        } else {
            for (int i = ids.size() - 1; i >= 0; i --) {
                NewsEvent news = NewsEvent.find(NewsEvent.class, "_id=?", ids.get(i)).get(0);
                newsList.add(new NewsItem(news.getTitle(), news.getSource(), news.getDate(), news.getID(), news.getType(), false));
            }
            adapter = new NewsAdapter(getActivity(), newsList);
            historyList.setAdapter(adapter);
            emptyInfo.setVisibility(View.GONE);
            historyList.setVisibility(View.VISIBLE);
            historyList.setClickable(true);
        }

        historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), NewsPageActivity.class);
                NewsItem item = newsList.get(i);
                adapter.notifyDataSetChanged();
                intent.putExtra("id", item.getId());
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void removeItem(int position) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("history", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        HashSet<String> ids = (HashSet<String>)sharedPreferences.getStringSet("id", new HashSet<String>());
        String id = newsList.get(position).getId();
        ids.remove(id);
        NewsEvent news = NewsEvent.find(NewsEvent.class, "_id=?", id).get(0);
        news.isRead = false;
        news.save();
        editor.putStringSet("id", ids);
        editor.apply();
        newsList.remove(position);
        adapter.notifyDataSetChanged();
    }
}
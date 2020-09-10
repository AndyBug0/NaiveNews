package com.java.wuguohao.search;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import com.java.wuguohao.MainActivity;
import com.java.wuguohao.R;
import com.java.wuguohao.bean.NewsEvent;
import com.java.wuguohao.news.NewsAdapter;
import com.java.wuguohao.news.NewsItem;
import com.java.wuguohao.news.NewsPageActivity;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SearchDetail extends AppCompatActivity {
    List<NewsItem> searchNews = new ArrayList<>();
    SearchView searchView;
    ListView searchList;
    NewsAdapter adapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_detail_page);

        searchView = findViewById(R.id.search_detail_view);
        searchList = findViewById(R.id.search_detail_list);
        ImageView backButton = findViewById(R.id.search_detail_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SearchDetail.this, MainActivity.class));
                finish();
            }
        });

        String keyWord = getIntent().getStringExtra("KeyWord");
        searchView.setQueryHint(keyWord);
        final SearchNewsEvent search = new SearchNewsEvent();
        LinkedHashSet<String> newsId = new LinkedHashSet<>();
        newsId.addAll(search.searchTitle(keyWord));
        newsId.addAll(search.searchContent(keyWord));

        for (String id : newsId) {
            NewsEvent news = NewsEvent.find(NewsEvent.class, "_id=?", id).get(0);
            searchNews.add(new NewsItem(news.getTitle(), news.getSource(), news.getDate(), news.getID(), news.isRead));
        }

        adapter = new NewsAdapter(this, searchNews);
        searchList.setAdapter(adapter);
        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(SearchDetail.this, NewsPageActivity.class);
                NewsItem item = searchNews.get(i);
                item.isRead = true;
                adapter.notifyDataSetChanged();
                intent.putExtra("id", item.getId());
                startActivity(intent);
            }
        });

        int searchTextId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text",null,null);
        final TextView searchText = (TextView) searchView.findViewById(searchTextId);
        searchText.setTextColor(getResources().getColor(R.color.white));
        final SharedPreferences sharedPreferences = getSharedPreferences("history", MODE_PRIVATE);
        Set<String> set = sharedPreferences.getStringSet("search", new LinkedHashSet<String>());
        final List<String> searchHistory = new ArrayList<>();
        if (set != null)
            searchHistory.addAll(set);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (searchView != null) {
                    //修改搜索记录
                    if (!searchHistory.contains(query)) {
                        searchHistory.add(0, query);
                    }
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    LinkedHashSet<String> newSet = new LinkedHashSet<>(searchHistory);
                    editor.putStringSet("search", newSet);
                    editor.commit();
                    //跳转到搜索得到的新闻列表页面
                    Intent intent = new Intent(SearchDetail.this, SearchDetail.class);
                    intent.putExtra("KeyWord", query);
                    startActivity(intent);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}

package com.java.wuguohao.news;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.java.wuguohao.R;
import com.java.wuguohao.bean.NewsEvent;
import com.java.wuguohao.search.SearchActivity;

public class NewsPageActivity extends AppCompatActivity {
    private TextView titleView;
    private TextView sourceView;
    private TextView dateView;
    private TextView contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.news_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.news_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        String newsId = getIntent().getStringExtra("id");
        NewsEvent news = NewsEvent.find(NewsEvent.class, "_id = ?", newsId).get(0);
        news.isRead = true;

        titleView = (TextView) findViewById(R.id.news_page_title);
        sourceView = (TextView) findViewById(R.id.news_page_source);
        dateView = (TextView) findViewById(R.id.news_page_date);
        contentView = (TextView) findViewById(R.id.news_page_content);
        titleView.setText(news.getTitle());
        sourceView.setText(news.getSource());
        dateView.setText(news.getDate());
        contentView.setText(news.getContent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.news_page_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(this, SearchActivity.class));
                break;
            case R.id.action_collection:

                break;
            case R.id.action_share:

                break;
        }
        return false;
    }
}

package com.java.wuguohao.news;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.java.wuguohao.MainActivity;
import com.java.wuguohao.R;
import com.java.wuguohao.bean.NewsEvent;
import com.java.wuguohao.search.SearchActivity;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.common.UiError;
import com.sina.weibo.sdk.openapi.IWBAPI;
import com.sina.weibo.sdk.openapi.WBAPIFactory;

import java.util.HashSet;

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
        news.save();
        SharedPreferences sharedPreferences = getSharedPreferences("history", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        HashSet<String> ids = (HashSet<String>) sharedPreferences.getStringSet("id", new HashSet<String>());
        ids.add(news.getID());
        editor.putStringSet("id", ids);
        editor.commit();

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
            case R.id.action_share:
                Dialog shareDialog = new Dialog(this, R.style.shareMenu);
                LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.bottom_share_menu, null);
                ImageButton sinaShare = (ImageButton) root.findViewById(R.id.share_to_sina);
                sinaShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(NewsPageActivity.this, "Share to Sina", Toast.LENGTH_SHORT).show();
                        initSdk();
                        startWebAuth();

                        Intent intent = new Intent(NewsPageActivity.this, ShareActivity.class);
                        startActivity(intent);
                    }
                });
                shareDialog.setContentView(root);
                Window shareMenu = shareDialog.getWindow();
                shareMenu.setGravity(Gravity.BOTTOM);
                WindowManager.LayoutParams lp = shareMenu.getAttributes(); // 获取对话框当前的参数值
                lp.x = 0; // 新位置X坐标
                lp.y = 0; // 新位置Y坐标
                lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
                root.measure(0, 0);
                lp.height = root.getMeasuredHeight();
                lp.alpha = 9f; // 透明度
                shareMenu.setAttributes(lp);
                shareDialog.show();
                break;
        }
        return false;
    }

    private void startClientAuth() {
        mWBAPI.authorizeClient(new WbAuthListener() {
            @Override
            public void onComplete(Oauth2AccessToken token) {
                Toast.makeText(NewsPageActivity.this, "微博授权成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(UiError error) {
                Toast.makeText(NewsPageActivity.this, "微博授权出错", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(NewsPageActivity.this, "微博授权取消", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mWBAPI.authorizeCallback(requestCode, resultCode, data);
    }
    private void startWebAuth() {
        mWBAPI.authorizeWeb(new WbAuthListener() {
            @Override
            public void onComplete(Oauth2AccessToken token) {
                Toast.makeText(NewsPageActivity.this, "微博授权成功", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(UiError error) {
                Toast.makeText(NewsPageActivity.this, "微博授权出错:" + error.errorDetail, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(NewsPageActivity.this, "微博授权取消", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //在微博开发平台为应用申请的App Key
    private static final String APP_KY = "2479326284";
    //在微博开放平台设置的授权回调页
    private static final String REDIRECT_URL = "http://www.sina.com";
    //在微博开放平台为应用申请的高级权限
    private static final String SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";
    IWBAPI mWBAPI;
    private void startAuth() {
        //auth
        mWBAPI.authorize(new WbAuthListener() {
            @Override
            public void onComplete(Oauth2AccessToken token) {
                Toast.makeText(NewsPageActivity.this, "微博授权成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(UiError error) {
                Toast.makeText(NewsPageActivity.this, "微博授权出错", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(NewsPageActivity.this, "微博授权取消", Toast.LENGTH_SHORT).show();
            }
        });
    }
    //init sdk
    private void initSdk() {
        AuthInfo authInfo = new AuthInfo(this, APP_KY, REDIRECT_URL, SCOPE);
        mWBAPI = WBAPIFactory.createWBAPI(this);
        mWBAPI.registerApp(this, authInfo);
    }
}

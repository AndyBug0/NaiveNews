package com.java.wuguohao.news;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.java.wuguohao.R;

import java.text.SimpleDateFormat;

import static java.lang.Math.abs;

/**
 * created on 9/4/2020
 */
public class RefreshView extends ListView implements AbsListView.OnScrollListener{
    private View footer; //load more footer view
    private View header; //refresh header view

    private int totalItemCount; //total number of items in list
    private int lassVisible; //the last visable item --> to check whether can pull up to load more
    private int firstVisible; //the first visable item --> to check whether can pull down to refresh

    private LoadListener loadListener;

    private int headerHeight;
    private TextView headerDescription;//头文件textview显示加载文字
    private TextView headerTime;//头文件textview显示加载时间
    private ProgressBar progressBar;//加载进度
    private int downX, downY;  //the position finger puts

    boolean isLoading;  //check whether is loading
    boolean isRefreshing;   //check whether is refreshing

    public RefreshView(Context context) {
        super(context);
        Init(context);
    }

    public RefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context);
    }

    public RefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Init(context);
    }

    private void Init(Context context) {
        //get header xml
        header = LinearLayout.inflate(context, R.layout.header, null);
        headerDescription=(TextView) header.findViewById(R.id.header_description);
        headerTime=(TextView) header.findViewById(R.id.header_time);
        progressBar=(ProgressBar) header.findViewById(R.id.header_progressbar);
        headerTime.setText("上次更新时间:"+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
        //hide header
        header.measure(0, 0);
        headerHeight = header.getMeasuredHeight();
        header.setPadding(0, -1 * headerHeight, 0, 0);
        //add header
        addHeaderView(header);

        //get footer xml
        footer = LinearLayout.inflate(context, R.layout.footer, null);
        //hide footer
        footer.measure(0,0);
        footer.setVisibility(View.GONE);
        //add footer
        addFooterView(footer);

        setOnScrollListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isLoading && !isRefreshing) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downX = (int) event.getX();
                    downY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int moveX = (int) event.getX(), moveY = (int) event.getY();
                    int distX = moveX - downX, distY = moveY - downY;
                    if (abs(distX) < abs(distY)) {
                        int paddingY = Math.min((int)(- headerHeight + distY * 0.75), headerHeight);
                        if (paddingY < 0) {
                            headerDescription.setText(R.string.pull_to_refresh);
                        } else {
                            headerDescription.setText(R.string.release_to_refresh);
                        }
                        progressBar.setVisibility(View.GONE);
                        header.setPadding(0, paddingY, 0, 0);
                    }
                    break;
            }
            return super.onTouchEvent(event);
        }
        return false;
    }

    public void onScrollStateChanged(AbsListView view, final int scrollState) {
        if (!isRefreshing && !isLoading) {
            if (scrollState == SCROLL_STATE_IDLE) {
                if (totalItemCount != 0 && lassVisible == totalItemCount) {
                    isLoading = true;
                    footer.setVisibility(View.VISIBLE);
                    //Load data
                    new Thread() {
                        @Override
                        public void run() {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    loadListener.onLoad();
                                }
                            });
                        }
                    }.start();
                } else if (firstVisible == 0) {
                    isRefreshing = true;
                    header.setPadding(0, 0, 0, 0);
                    headerDescription.setText(R.string.refreshing);
                    progressBar.setVisibility(View.VISIBLE);
                    //refresh data
                    new Thread() {
                        @Override
                        public void run() {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    loadListener.onRefresh();
                                }
                            });
                        }
                    }.start();
                }
            }
        }
    }

    //接口回调
    public interface LoadListener{
        void onLoad();
        void onRefresh();
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {

        this.firstVisible = firstVisibleItem;
        this.lassVisible = firstVisibleItem + visibleItemCount;
        this.totalItemCount = totalItemCount;
    }

    //加载完成
    public void loadComplete(){
        isLoading = false;
        isRefreshing = false;
        footer.setVisibility(View.GONE);
        header.setPadding(0, -headerHeight, 0, 0);
    }

    public void setInteface(LoadListener loadListener){
        this.loadListener = loadListener;
    }

}
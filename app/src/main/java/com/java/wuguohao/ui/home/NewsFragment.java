package com.java.wuguohao.ui.home;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.shapes.PathShape;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.java.wuguohao.R;
import com.java.wuguohao.search.SearchActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class NewsFragment extends Fragment {

    private View viewContent;
    private TabLayout tab_essence;
    private ViewPager vp_essence;
    private ImageButton menu;
    private ImageButton search;
    private ContentAdapter adapter;
    private List<ContentFragment> fragments;

    public static HashMap<String, String> titleMap = new HashMap<>();
    static {
        titleMap.put("all", "全部");
        titleMap.put("news", "新闻");
        titleMap.put("paper", "论文");
        titleMap.put("0", "COV研究");
        titleMap.put("1", "临床试验");
        titleMap.put("2", "对抗新冠");
        titleMap.put("3", "攻关项目");
        titleMap.put("4", "团队协作");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewContent = inflater.inflate(R.layout.fragment_newstab,container,false);
        initContentView(viewContent);
        initData();

        return viewContent;
    }

    public void initContentView(View viewContent) {
        this.tab_essence = (TabLayout) viewContent.findViewById(R.id.news_tab_essence);
        this.vp_essence = (ViewPager) viewContent.findViewById(R.id.news_vp_essence);
        this.menu = (ImageButton) viewContent.findViewById(R.id.class_menu);
        this.search = (ImageButton) viewContent.findViewById(R.id.news_search);
    }

    public void initData() {
        setTab();
        this.vp_essence.setAdapter(adapter);

        //将TabLayout和ViewPager关联起来
        this.tab_essence.setupWithViewPager(this.vp_essence);

        this.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(getActivity(), ChannelPageActivity.class), 1);
            }
        });

        this.search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), SearchActivity.class));
            }
        });
    }

    public void setTab() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("channel", Context.MODE_PRIVATE);

        boolean isSet = sharedPreferences.getBoolean("set", false);
        fragments = new ArrayList<>();
        if (isSet) {
            HashSet<String> types = (HashSet<String>) sharedPreferences.getStringSet("my_channel", new HashSet<String>());
            for (String type : types) {
                fragments.add(new ContentFragment(titleMap.get(type), type));
            }
        } else {
            String [] types = {"all", "news", "paper", "0", "1", "2", "3", "4"};
            for (String type : types) {
                fragments.add(new ContentFragment(titleMap.get(type), type));
            }
        }

        adapter = new ContentAdapter(getFragmentManager(), fragments);
        this.vp_essence.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            setTab();
        }
    }

}

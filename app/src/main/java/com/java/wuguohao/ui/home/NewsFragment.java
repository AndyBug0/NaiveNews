package com.java.wuguohao.ui.home;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.java.wuguohao.R;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment {

    private View viewContent;
    private TabLayout tab_essence;
    private ViewPager vp_essence;
    private List<ContentFragment> fragments;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewContent = inflater.inflate(R.layout.fragment_newstab,container,false);
        initContentView(viewContent);
        initData();

        vp_essence.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                vp_essence.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        return viewContent;
    }

    public void initContentView(View viewContent) {
        this.tab_essence = (TabLayout) viewContent.findViewById(R.id.news_tab_essence);
        this.vp_essence = (ViewPager) viewContent.findViewById(R.id.news_vp_essence);
    }

    public void initData() {
        //获取标签数据
        String[] titles = getResources().getStringArray(R.array.home_video_tab);

        //创建一个viewpager的adapter
        fragments = new ArrayList<>();
        for (String text : titles) {
            String [] textSplit = text.split("@");
            fragments.add(new ContentFragment(textSplit[0], textSplit[1]));
        }
        ContentAdapter adapter = new ContentAdapter(getFragmentManager(), fragments);
        this.vp_essence.setAdapter(adapter);

        //将TabLayout和ViewPager关联起来
        this.tab_essence.setupWithViewPager(this.vp_essence);
    }


}

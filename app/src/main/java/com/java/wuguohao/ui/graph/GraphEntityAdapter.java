package com.java.wuguohao.ui.graph;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.java.wuguohao.R;
import com.java.wuguohao.bean.NewsMap;

import java.util.List;

public class GraphEntityAdapter extends BaseAdapter {
    Context context;
    List<NewsMap> newsMapList;

    public GraphEntityAdapter(Context context, List<NewsMap> newsMapList) {
        this.context = context;
        this.newsMapList = newsMapList;
    };

    @Override
    public int getCount() {
        return newsMapList.size();
    }

    @Override
    public Object getItem(int i) {
        return newsMapList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int pos, View contentView, ViewGroup parent) {
        View view = (contentView == null) ? View.inflate(context, R.layout.graph_entity, null) : contentView;

        LinearLayout entityTitle = (LinearLayout) view.findViewById(R.id.entity_title);
        LinearLayout entityDetail = (LinearLayout) view.findViewById(R.id.entity_detail);
        LinearLayout entityRelation = (LinearLayout) entityDetail.findViewById(R.id.entity_relation);
        LinearLayout entityProperty = (LinearLayout) entityDetail.findViewById(R.id.entity_property);
        NewsMap newsMap = newsMapList.get(pos);
        //title
        float hot = newsMap.getHot();
        int hotLevel = (int) (hot / 0.3);
        ImageView hotIcon = (ImageView) entityTitle.findViewById(R.id.entity_hot);
        if (hotLevel == 0) {
            hotIcon.setVisibility(View.GONE);
        } else {
            for (int i = 1; i < hotLevel; i ++)
                entityTitle.addView(hotIcon);
        }
        ((ImageView) entityTitle.findViewById(R.id.entity_picture)).setTag(newsMap.getImage());
        ((TextView) entityTitle.findViewById(R.id.entity_description)).setText(newsMap.getDescription());

        //relation
        List<String> relations = newsMap.getRelation();
        List<String> relationLabels = newsMap.getRelationLabel();
        List<String> relationForwards = newsMap.getRelationForward();
        int num = relations.size();
        for (int i = 0; i < num; i ++) {
            String relation = relations.get(i);
            String label = relationLabels.get(i);
            boolean forward = Boolean.valueOf(relationForwards.get(i));
            entityRelation.addView(createRelationView(relation, label, forward));
        }

        //properties
        List<String> properties = newsMap.getProperities();
        for (String property : properties) {
            entityProperty.addView(createPropertyItem(property));
        }

        return view;
    }

    private View createRelationView(String relation, String label, boolean forward) {
        View view = View.inflate(context, R.layout.graph_entity_relation, null);
        ((TextView) view.findViewById(R.id.entity_relation_relation)).setText(relation);
        ((TextView) view.findViewById(R.id.entity_relation_label)).setText(label);
        if (forward) {
            ((ImageView) view.findViewById(R.id.entity_relation_forward)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_right));
        } else {
            ((ImageView) view.findViewById(R.id.entity_relation_forward)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_left));
        }
        ((ImageView) view.findViewById(R.id.entity_relation_go)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return view;
    }

    private View createPropertyItem(String property) {
        String [] p = property.split(":");
        View view = View.inflate(context, R.layout.graph_entity_property, null);
        ((TextView) view.findViewById(R.id.entity_property_label)).setText(p[0]);
        ((TextView) view.findViewById(R.id.entity_property_description)).setText(p[1]);
        return view;
    }
}

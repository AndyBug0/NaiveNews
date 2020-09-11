package com.java.wuguohao.ui.graph;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.java.wuguohao.DataHandler;
import com.java.wuguohao.R;
import com.java.wuguohao.bean.NewsMap;

import java.util.List;

public class GraphEntityAdapter extends BaseAdapter {
    Context context;
    ForwardButtonClickListener listener;
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
    public View getView(int pos, View convertView, ViewGroup parent) {
        View view = (convertView == null) ? View.inflate(context, R.layout.graph_entity, null) : convertView;
        final NewsMap newsMap = newsMapList.get(pos);
        TextView entityTitle = ((TextView) view.findViewById(R.id.entity_name));
        final TextView entityRelationTitle = (TextView) view.findViewById(R.id.entity_relation_title);
        TextView entityPropertyTitle = (TextView) view.findViewById(R.id.entity_property_title);

        final ImageView entityPicture = (ImageView) view.findViewById(R.id.entity_picture);
        final TextView entityDescription = (TextView) view.findViewById(R.id.entity_description);
        final LinearLayout entityDetail = (LinearLayout) view.findViewById(R.id.entity_detail);
        final LinearLayout entityRelation = (LinearLayout) view.findViewById(R.id.entity_relation);
        final LinearLayout entityProperty = (LinearLayout) view.findViewById(R.id.entity_property);
        entityRelation.setTag(false);   //是否被初始化
        entityProperty.setTag(false);

        entityTitle.setTag(false);  //是否打开详情
        entityRelationTitle.setTag(false);
        entityPropertyTitle.setTag(false);

        final List<String> relations = newsMap.getRelation();
        final List<String> properties = newsMap.getProperities();
        if (relations.isEmpty()) {
            entityRelationTitle.setVisibility(View.GONE);
        } else {
            entityRelationTitle.setVisibility(View.VISIBLE);
        }
        if (properties.isEmpty()) {
            entityPropertyTitle.setVisibility(View.GONE);
        } else {
            entityPropertyTitle.setVisibility(View.VISIBLE);
        }

        //title
        float hot = newsMap.getHot();
        if (hot > 0.35) {
            view.findViewById(R.id.entity_hot_level_1).setVisibility(View.VISIBLE);
            if (hot > 0.6) {
                view.findViewById(R.id.entity_hot_level_2).setVisibility(View.VISIBLE);
                if (hot > 0.9) {
                    view.findViewById(R.id.entity_hot_level_3).setVisibility(View.VISIBLE);
                }
            }
        }
        entityTitle.setText(newsMap.getLabel());
        String description = newsMap.getDescription();
        if (description.equals("")) {
            entityDescription.setTag(false);
        } else {
            entityDescription.setTag(true);
            entityDescription.setText(description);
        }

        final String imgUrl = newsMap.getImage();
        if (imgUrl.equals("NoImage")) {
            entityPicture.setTag(false);
        } else {
            entityPicture.setTag(true);
            final DataHandler dataHandler = new DataHandler();
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    dataHandler.readImage(imgUrl);
                }
            });

            try {
                t.start();
                t.join();
                entityPicture.setImageBitmap(dataHandler.getBitmap());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        entityTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isOpen = (Boolean) view.getTag();
                view.setTag(!isOpen);
                if (isOpen) {
                    entityPicture.setVisibility(View.GONE);
                    entityDescription.setVisibility(View.GONE);
                    entityRelation.setVisibility(View.GONE);
                    entityProperty.setVisibility(View.GONE);
                    entityDetail.setVisibility(View.GONE);
                } else {
                    boolean hasDescription = (Boolean) entityDescription.getTag();
                    if (hasDescription) {
                        entityDescription.setVisibility(View.VISIBLE);
                    } else {
                        entityDescription.setVisibility(View.GONE);
                    }
                    boolean hasPicture = (Boolean) entityPicture.getTag();
                    if (hasPicture) {
                        entityPicture.setVisibility(View.VISIBLE);
                    } else {
                        entityPicture.setVisibility(View.GONE);
                    }
                    entityDetail.setVisibility(View.VISIBLE);
                }
            }
        });
        //relations
        entityRelationTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isOpen = (Boolean) view.getTag();
                view.setTag(!isOpen);
                if (isOpen) {
                    entityRelation.setVisibility(View.GONE);
                } else {
                    boolean isInitial = (Boolean) entityRelation.getTag();
                    if (!isInitial) {
                        entityRelation.setTag(true);
                        List<String> labels = newsMap.getRelationLabel();
                        List<String> forwards = newsMap.getRelationForward();
                        int num = relations.size();
                        for (int i = 0; i < num; i ++) {
                            entityRelation.addView(createRelationView(relations.get(i), labels.get(i), Boolean.valueOf(forwards.get(i))));
                        }
                    }
                    entityRelation.setVisibility(View.VISIBLE);
                }
            }
        });
        //property
        entityPropertyTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isOpen = (Boolean) view.getTag();
                view.setTag(!isOpen);
                if (isOpen) {
                    entityProperty.setVisibility(View.GONE);
                } else {
                    boolean isInitial = (Boolean) entityProperty.getTag();
                    if (!isInitial) {
                        entityProperty.setTag(true);
                        for (String property : properties) {
                            entityProperty.addView(createPropertyItem(property));
                        }
                    }
                    entityProperty.setVisibility(View.VISIBLE);
                }
            }
        });

        return view;
    }

    private View createRelationView(String relation, final String label, boolean forward) {
        View view = View.inflate(context, R.layout.graph_entity_relation, null);
        ((TextView) view.findViewById(R.id.entity_relation_relation)).setText(relation);
        ((TextView) view.findViewById(R.id.entity_relation_label)).setText(label);
        if (forward) {
            ((ImageView) view.findViewById(R.id.entity_relation_forward)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_right));
        } else {
            ((ImageView) view.findViewById(R.id.entity_relation_forward)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_left));
        }

        ImageButton forwardButton = view.findViewById(R.id.entity_relation_forward_button);
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onForwardButtonClick(label);
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

    public void setListener(ForwardButtonClickListener listener) {
        this.listener = listener;
    }

    interface ForwardButtonClickListener {
        public void onForwardButtonClick(String entity);
    }
}

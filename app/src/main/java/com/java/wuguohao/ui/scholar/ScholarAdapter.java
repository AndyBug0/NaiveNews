package com.java.wuguohao.ui.scholar;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.java.wuguohao.R;
import com.java.wuguohao.bean.NewsScholar;

import java.util.List;
import java.util.Map;

public class ScholarAdapter extends BaseAdapter {
    Context context;
    BitmapFileReader bitmapReader;
    List<NewsScholar> scholarList;

    ScholarAdapter(Context context, List<NewsScholar> list) {
        this.context = context;
        this.scholarList = list;
    }

    @Override
    public int getCount() {
        return scholarList.size();
    }

    @Override
    public Object getItem(int i) {
        return scholarList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup viewGroup) {
        View view = (convertView == null) ? View.inflate(context, R.layout.scholar_abstarct, null) : convertView;
        final NewsScholar scholar = scholarList.get(pos);

        ((TextView) view.findViewById(R.id.scholar_abstract_name)).setText(scholar.getName());
        ((TextView) view.findViewById(R.id.scholar_abstract_position)).setText(scholar.getPosition());
        ((TextView) view.findViewById(R.id.scholar_abstract_affiliation)).setText(scholar.getAffiliation());

        Map<String, Float> indice = scholar.getIndices();
        String h_index = "" + Math.round(indice.get("hindex"));
        String activity = indice.get("activity").toString();
        String star = indice.get("newStar").toString();
        String citations = "" + Math.round(indice.get("citations"));
        ((TextView) view.findViewById(R.id.scholar_abstract_h_index)).setText(h_index);
        ((TextView) view.findViewById(R.id.scholar_abstract_activity)).setText(activity);
        ((TextView) view.findViewById(R.id.scholar_abstract_star)).setText(star);
        ((TextView) view.findViewById(R.id.scholar_abstract_citations)).setText(citations);

        final ImageView avatar = (ImageView) view.findViewById(R.id.scholar_abstract_avatar);
        Bitmap bitmap = bitmapReader.readBitmap(scholar.getID() + "_avatar.png");
        if (bitmap != null)
            avatar.setImageBitmap(bitmap);
        return view;
    }

    interface BitmapFileReader {
        Bitmap readBitmap(String path);
    }

    public void setBitmapFileReader(BitmapFileReader reader) {
        this.bitmapReader = reader;
    }
}

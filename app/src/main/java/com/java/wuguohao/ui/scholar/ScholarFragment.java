package com.java.wuguohao.ui.scholar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.java.wuguohao.R;
import com.java.wuguohao.bean.NewsScholar;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ScholarFragment extends Fragment implements ScholarAdapter.BitmapFileReader {
    private View root;
    private Button button1;     //高关注学者
    private Button button2;     //追忆学者

    private ListView listView;
    private ScholarAdapter adapter1;
    private ScholarAdapter adapter2;
    private List<NewsScholar> scholars1 = new ArrayList<>();
    private List<NewsScholar> scholars2 = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_scholar, container, false);

        initButtonClicker();
        initListView();

        return root;
    }

    private void initListView() {
        listView = (ListView) root.findViewById(R.id.scholar_list);
        adapter1 = new ScholarAdapter(getContext(), scholars1);
        adapter2 = new ScholarAdapter(getContext(), scholars2);
        adapter1.setBitmapFileReader(this);
        adapter2.setBitmapFileReader(this);

        final List<NewsScholar> scholars = NewsScholar.listAll(NewsScholar.class);
        for (NewsScholar scholar : scholars) {
            if (scholar.getIsPassedAway()) {
                scholars2.add(scholar);
            } else {
                scholars1.add(scholar);
            }
        }

        listView.setTag(1); //判断是哪个列表
        listView.setAdapter(adapter1);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ScholarPageActivity.class);
                int type = (int) listView.getTag();
                if (type == 1) {
                    intent.putExtra("id", scholars1.get(i).getID());
                } else {
                    intent.putExtra("id", scholars2.get(i).getID());
                }
                startActivity(intent);
            }
        });
    }

    private void initButtonClicker() {
        button1 = (Button) root.findViewById(R.id.scholar_button_high_focus);
        button2 = (Button) root.findViewById(R.id.scholar_button_passed_away);
        button1.setSelected(true);
        button2.setSelected(false);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (button2.isSelected()) {
                    showProfessor(1);
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (button1.isSelected()) {
                    showProfessor(2);
                }
            }
        });
    }

    private void showProfessor(int type) {
        //1 -- 高关注 ， 2 -- 追忆
        listView.setTag(type);
        if (type == 1) {
            listView.setAdapter(adapter1);
            button1.setSelected(true);
            button1.setClickable(false);
            button2.setSelected(false);
            button2.setClickable(true);
        } else {
            listView.setAdapter(adapter2);
            button1.setSelected(false);
            button1.setClickable(true);
            button2.setSelected(true);
            button2.setClickable(false);
        }
    }

    @Override
    public Bitmap readBitmap(String path) {
        Bitmap bitmap = null;
        try {
            FileInputStream inputStream = getContext().openFileInput(path);
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}

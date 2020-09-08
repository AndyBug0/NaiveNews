package com.java.wuguohao.search;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.java.wuguohao.R;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SearchActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    List<String> searchHistory = new ArrayList<>();
    SearchView searchView;
    ListView searchList;
    SearchAdapter adapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page);

        searchView = (SearchView) findViewById(R.id.search_view);
        searchList = (ListView) findViewById(R.id.search_list);

        String hint = getIntent().getStringExtra("KeyWord");
        searchView.setQueryHint(hint);

        sharedPreferences = getSharedPreferences("history", MODE_PRIVATE);
        searchHistory.clear();
        Set<String> set = sharedPreferences.getStringSet("search", new LinkedHashSet<String>());
        if (set != null)
            searchHistory.addAll(set);
        ImageButton clearButton = (ImageButton) findViewById(R.id.search_clear);
        ImageView backButton = (ImageView) findViewById(R.id.search_back);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!searchHistory.isEmpty()) {
                    searchHistory.clear();
                    adapter.notifyDataSetChanged();
                    searchList.setVisibility(View.GONE);
                    searchList.setClickable(false);
                    savePreferences();
                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        final View.OnClickListener deleteClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.item_search_delete) {
                    int pos = (Integer)((ImageButton) view).getTag();
                    searchHistory.remove(pos);
                    adapter.notifyDataSetChanged();
                    if (searchHistory.isEmpty()) {
                        searchList.setVisibility(View.GONE);
                        searchList.setClickable(false);
                    }
                }
            }
        };
        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String searchText = searchHistory.get(i);
                searchView.setQueryHint(searchText);
                //调整记录为第一个
                searchHistory.remove(i);
                searchHistory.add(searchText);
                adapter.notifyDataSetChanged();
            }
        });
        if (searchHistory.isEmpty()) {
            searchList.setVisibility(View.GONE);
            searchList.setClickable(false);
        } else {
            adapter = new SearchAdapter(this, searchHistory, deleteClickListener);
            searchList.setAdapter(adapter);
            searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String searchText = searchHistory.get(i);
                    searchView.setQueryHint(searchText);
                }
            });
            searchList.setVisibility(View.VISIBLE);
            searchList.setClickable(true);
        }
        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String keyWord = searchHistory.get(i);
            }
        });

        int searchTextId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text",null,null);
        TextView searchText = (TextView) searchView.findViewById(searchTextId);
        searchText.setTextColor(getResources().getColor(R.color.white));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (searchView != null) {
                    if (!searchHistory.contains(query)) {
                        searchHistory.add(0, query);
                        if (adapter == null) {
                            adapter = new SearchAdapter(SearchActivity.this, searchHistory, deleteClickListener);
                            searchList.setAdapter(adapter);
                        }
                        adapter.notifyDataSetChanged();
                        searchList.setVisibility(View.VISIBLE);
                        searchList.setClickable(true);
                    }
                    savePreferences();
                    //跳转到搜索得到的新闻列表页面
                    Intent intent = new Intent(SearchActivity.this, SearchDetail.class);
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

    private void savePreferences() {
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        LinkedHashSet<String> set = new LinkedHashSet<>(searchHistory);
        editor.putStringSet("search", set);
        editor.commit();
    }

    private class SearchAdapter extends BaseAdapter {
        Context context;
        List<String> list;
        View.OnClickListener deleteClickListener;

        public SearchAdapter(Context context, List<String> list, View.OnClickListener listener) {
            this.context = context;
            this.list = list;
            this.deleteClickListener = listener;
        }

        @Override
        public int getCount() { return list.size(); }

        @Override
        public Object getItem(int pos) { return list.get(pos); }

        @Override
        public long getItemId(int pos) { return pos; }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            View view = (convertView == null) ? View.inflate(context, R.layout.item_search, null) : convertView;
            String text = (String) getItem(pos);

            TextView searchText = (TextView) view.findViewById(R.id.item_search_text);
            searchText.setText(text);
            ImageButton deleteButton = (ImageButton) view.findViewById(R.id.item_search_delete);
            deleteButton.setTag(pos);
            deleteButton.setOnClickListener(deleteClickListener);

            return view;
        }
    }
}

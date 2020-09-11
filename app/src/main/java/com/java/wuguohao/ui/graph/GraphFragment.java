package com.java.wuguohao.ui.graph;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.java.wuguohao.DataHandler;
import com.java.wuguohao.R;
import com.java.wuguohao.bean.NewsMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class GraphFragment extends Fragment implements GraphEntityAdapter.ForwardButtonClickListener {
    private View root;
    private SearchView searchView;
    private ListView listView;
    private ImageView backButton;
    private GraphEntityAdapter adapter;

    private String entity;
    private Stack<String> entityHistory = new Stack<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_graph, container, false);

        initEntitySearchView();
        initEntityListView();
        initBackButton();

        return root;
    }

    private void searchEntityGraph(String entity) {
        this.entity = entity;
        searchView.setQuery("", false);
        searchView.clearFocus();
        searchView.setQueryHint(entity);
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        root.findViewById(R.id.graph_loading).setVisibility(View.VISIBLE);
        root.findViewById(R.id.graph_load_fail).setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showEntityList();
            }
        }, 50);
    }

    private void initEntitySearchView() {
        searchView = root.findViewById(R.id.entity_search_bar);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchEntityGraph(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        int editTextId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        EditText editText = (EditText) searchView.findViewById(editTextId);
        editText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        editText.setHintTextColor(getResources().getColor(R.color.gray));
        int searchPlateId = searchView.getContext().getResources().getIdentifier("android:id/search_plate", null, null);
        View searchPlate = searchView.findViewById(searchPlateId);
        searchPlate.setBackgroundColor(getResources().getColor(R.color.transparent));
    }

    private void initEntityListView() {
        listView = root.findViewById(R.id.entity_list);
    }

    private void initBackButton() {
        backButton = (ImageView) root.findViewById(R.id.graph_back);
        backButton.setColorFilter(getResources().getColor(R.color.grayLight));
        backButton.setClickable(false);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchEntity = entityHistory.pop();
                searchEntityGraph(searchEntity);
            }
        });
    }

    private void showEntityList() {
        List<String> urls = new DataHandler().readMap(entity, getContext());
        if (urls.isEmpty()) {
            root.findViewById(R.id.graph_loading).setVisibility(View.GONE);
            root.findViewById(R.id.graph_load_fail).setVisibility(View.VISIBLE);
        } else {
            List<NewsMap> newsMapList = new ArrayList<>();
            for (String url : urls) {
                newsMapList.add(NewsMap.find(NewsMap.class, "url=?", url).get(0));
            }

            adapter = new GraphEntityAdapter(getContext(), newsMapList);
            adapter.setListener(this);
            listView.setAdapter(adapter);
            root.findViewById(R.id.graph_loading).setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
        if (entityHistory.empty()) {
            backButton.setClickable(false);
            backButton.setColorFilter(getResources().getColor(R.color.grayLight));
        } else {
            backButton.setClickable(true);
            backButton.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    @Override
    public void onForwardButtonClick(String entity) {
        entityHistory.push(this.entity);
        searchEntityGraph(entity);
    }
}

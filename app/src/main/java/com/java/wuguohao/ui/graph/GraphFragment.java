package com.java.wuguohao.ui.graph;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.java.wuguohao.R;

public class GraphFragment extends Fragment {
    private View root;
    private SearchView searchView;
    private ListView listView;

    private String entity;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_graph, container, false);

//        initEntitySearchView();
//        initEntityListView();

        return root;
    }

    private void initEntitySearchView() {
        searchView = root.findViewById(R.id.entity_search_bar);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                entity = query;
                searchView.setQueryHint(query);
                showEntityInfo();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void initEntityListView() {
        listView = root.findViewById(R.id.entity_list);

    }

    private void showEntityInfo() {

    }
}

package com.captainhwz.demo.content;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.captainhwz.demo.R;
import com.captainhwz.layout.DefaultContentHandler;
import com.captainhwz.layout.MaterialHeaderLayout;

public class ListFragment extends BaseFragment {

    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listView = (ListView) inflater.inflate(R.layout.fragment_list, container, false);
        listView.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, new String[]{"item1", "item2", "item3", "item4", "item5", "item6", "item7", "item8", "item9", "item10", "item11"}));
        return listView;
    }

    @Override
    public boolean checkCanDoRefresh(MaterialHeaderLayout frame, View content, View header) {
        return DefaultContentHandler.checkContentCanBePulledDown(frame, listView, header);
    }
}

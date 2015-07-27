package com.captainhwz.demo.content;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.captainhwz.demo.R;
import com.captainhwz.layout.DefaultContentHandler;
import com.captainhwz.layout.MaterialHeaderLayout;

public class NoScrollFragment extends BaseFragment {

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frament_no_scroll, container, false);
        return view;
    }

    @Override
    public boolean checkCanDoRefresh(MaterialHeaderLayout frame, View content, View header) {
        return DefaultContentHandler.checkContentCanBePulledDown(frame, view, header);
    }

    @Override
    public void onChange(float ratio, float offsetY) {

    }

    @Override
    public void onOffsetCalculated(int totalOffset) {

    }
}

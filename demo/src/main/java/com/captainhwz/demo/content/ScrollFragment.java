package com.captainhwz.demo.content;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.captainhwz.demo.R;
import com.captainhwz.layout.DefaultContentHandler;
import com.captainhwz.layout.MaterialHeaderLayout;


public class ScrollFragment extends BaseFragment {

    ScrollView scrollView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        scrollView = (ScrollView) inflater.inflate(R.layout.fragment_scroll, container, false);
        return scrollView;
    }

    @Override
    public boolean checkCanDoRefresh(MaterialHeaderLayout frame, View content, View header) {
        return DefaultContentHandler.checkContentCanBePulledDown(frame, scrollView, header);
    }

    @Override
    public void onChange(float ratio, float offsetY) {

    }

    @Override
    public void onOffsetCalculated(int totalOffset) {

    }
}

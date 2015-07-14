package com.captainhwz.demo.content;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.captainhwz.demo.R;
import com.captainhwz.layout.ContentHandler;
import com.captainhwz.layout.MaterialHeaderLayout;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerContentLayout extends LinearLayout implements ContentHandler {

    RadioGroup radioGroup;
    List<RadioButton> radioButtonList;
    ViewPager viewPager;
    MyViewPagerAdapter adapter;

    public ViewPagerContentLayout(Context context, final AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.layout_view_pager, this, true);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        radioButtonList = new ArrayList<RadioButton>();
        for (int ii = 0; ii < 3; ii++) {
            RadioButton radioButton = new RadioButton(context);
            radioButton.setText("page" + (ii + 1));
            radioButton.setGravity(Gravity.CENTER);
            RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            radioButton.setLayoutParams(lp);
            radioButton.setId(ii + 1);
            radioButtonList.add(radioButton);
            radioGroup.addView(radioButton);
        }
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new MyViewPagerAdapter(((FragmentActivity) context).getSupportFragmentManager(), new ArrayList<BaseFragment>() {{
            add(new ScrollFragment());
            add(new ListFragment());
            add(new NoScrollFragment());
        }});
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                radioButtonList.get(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                viewPager.setCurrentItem(i - 1);
            }
        });
    }

    @Override
    public boolean checkCanDoRefresh(MaterialHeaderLayout frame, View content, View header) {
        return adapter.getItem(viewPager.getCurrentItem()).checkCanDoRefresh(frame, content, header);
    }

    static class MyViewPagerAdapter extends FragmentStatePagerAdapter {

        List<BaseFragment> mFragmentList;

        public MyViewPagerAdapter(FragmentManager fm, List<BaseFragment> fragmentList) {
            super(fm);
            mFragmentList = fragmentList;
        }

        @Override
        public int getCount() {
            return mFragmentList == null ? 0 : mFragmentList.size();
        }

        @Override
        public BaseFragment getItem(int position) {
            return mFragmentList.get(position);
        }
    }
}

package com.captainhwz.demo.header;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.captainhwz.demo.R;
import com.captainhwz.layout.HeaderHandler;
import com.nineoldandroids.view.ViewHelper;

public class TranslationHeader extends RelativeLayout implements HeaderHandler {
    RelativeLayout fakeHeader;
    View background;

    public TranslationHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.header, this, true);
        fakeHeader = (RelativeLayout) findViewById(R.id.fake_header);
        background = findViewById(R.id.background);
        ViewHelper.setAlpha(background, 0);
    }

    @Override
    public void onChange(float ratio, float offsetY) {
        ViewHelper.setAlpha(background, (1 - ratio));
        ViewHelper.setTranslationY(fakeHeader, ViewHelper.getTranslationY(fakeHeader) - offsetY);
        ViewHelper.setTranslationY(background, ViewHelper.getTranslationY(background) - offsetY);
    }
}

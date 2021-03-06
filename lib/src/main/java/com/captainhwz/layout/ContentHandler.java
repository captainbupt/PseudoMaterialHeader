package com.captainhwz.layout;

import android.view.View;

public interface ContentHandler {
    boolean checkCanDoRefresh(final MaterialHeaderLayout frame, final View content, final View header);

    void onChange(float ratio, float offsetY);

    void onOffsetCalculated(int totalOffset);
}

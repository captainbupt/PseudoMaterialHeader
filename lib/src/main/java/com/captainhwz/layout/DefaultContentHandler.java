package com.captainhwz.layout;

import android.view.View;
import android.widget.AbsListView;


public class DefaultContentHandler implements ContentHandler {
    public static boolean canChildScrollUp(View view) {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (view instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) view;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return view.getScrollY() > 0;
            }
        } else {
            return view.canScrollVertically(-1);
        }
    }

    /**
     * Default implement for check can perform pull to refresh
     *
     * @param layout
     * @param content
     * @param header
     * @return
     */
    public static boolean checkContentCanBePulledDown(MaterialHeaderLayout layout, View content, View header) {
        if (content instanceof ContentHandler) {
            return ((ContentHandler) content).checkCanDoRefresh(layout, content, header);
        } else {
            return !canChildScrollUp(content);
        }
    }

    @Override
    public boolean checkCanDoRefresh(MaterialHeaderLayout layout, View content, View header) {
        return checkContentCanBePulledDown(layout, content, header);
    }

    @Override
    public void onChange(float ratio, float offsetY) {
        
    }

    @Override
    public void onOffsetCalculated(int totalOffset) {

    }
}
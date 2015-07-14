package com.captainhwz.layout;

import android.graphics.PointF;

public class MotionEventIndicator {

    private PointF mPtLastMove = new PointF();
    private long mLastTime = 0;
    private float mOffsetX;
    private float mOffsetY;
    private long mOffsetTime;
    private int mCurrentPos = 0;
    private int mLastPos = 0;
    private int mHeaderMaxHeight;
    private int mHeaderMinHeight;
    private int mPressedPos = 0;
    private boolean isFirst = true;

    private boolean mIsUnderTouch = false;
    // record the refresh complete position
    private int mRefreshCompleteY = 0;

    public boolean isUnderTouch() {
        return mIsUnderTouch;
    }

    public void onRelease() {
        mIsUnderTouch = false;
    }

    public void onUIRefreshComplete() {
        mRefreshCompleteY = mCurrentPos;
    }

    public boolean goDownCrossFinishPosition() {
        return mCurrentPos >= mRefreshCompleteY;
    }

    protected void processOnMove(float currentX, float currentY, float offsetX, float offsetY) {
        setOffset(offsetX, offsetY);
    }

    public void onPressDown(float x, float y, long time) {
        mIsUnderTouch = true;
        mPressedPos = mCurrentPos;
        mPtLastMove.set(x, y);
        mLastTime = time;
    }

    public final void onMove(float x, float y, long time) {
        float offsetX = x - mPtLastMove.x;
        float offsetY = (y - mPtLastMove.y);
        mOffsetTime = time - mLastTime;
        processOnMove(x, y, offsetX, offsetY);
        mPtLastMove.set(x, y);
        mLastTime = time;
    }

    public long getOffsetTime() {
        return mOffsetTime;
    }

    protected void setOffset(float x, float y) {
        mOffsetX = x;
        mOffsetY = y;
    }

    public float getOffsetX() {
        return mOffsetX;
    }

    public float getOffsetY() {
        return mOffsetY;
    }

    public int getLastPosY() {
        return mLastPos;
    }

    public int getCurrentPosY() {
        return mCurrentPos;
    }

    /**
     * Update current position before update the UI
     */
    public final void setCurrentPos(int current) {
        mLastPos = mCurrentPos;
        mCurrentPos = current;
    }

    public final void setCurrentPosIfFirst(int current) {
        if (isFirst) {
            mCurrentPos = current;
            isFirst = false;
        }
    }

    public int getMinHeight() {
        return mHeaderMinHeight;
    }

    public int getMaxHeight() {
        return mHeaderMaxHeight;
    }

    public void setHeight(int mHeaderMaxHeight, int mHeaderMinHeight) {
        this.mHeaderMaxHeight = mHeaderMaxHeight;
        this.mHeaderMinHeight = mHeaderMinHeight;
    }

    public void setHeight(int mHeaderMaxHeight) {
        setHeight(mHeaderMaxHeight, 0);
    }

    public boolean reachMinHeight() {
        return mCurrentPos <= mHeaderMinHeight;
    }

    public boolean reachMaxHeight() {
        return mCurrentPos >= mHeaderMaxHeight;
    }

    public boolean willOverTop(int to) {
        return to <= mHeaderMinHeight;
    }

    public boolean willOverBottom(int to) {
        return to >= mHeaderMaxHeight;
    }

    public boolean hasMovedAfterPressedDown() {
        return mCurrentPos != mPressedPos;
    }

    public boolean isAlreadyHere(int to) {
        return mCurrentPos == to;
    }

    public float getLastPercent() {
        final float oldPercent = mHeaderMaxHeight == 0 ? 0 : (mLastPos - mHeaderMinHeight) * 1f / (mHeaderMaxHeight - mHeaderMinHeight);
        return oldPercent;
    }

    public float getCurrentPercent() {
        final float currentPercent = mHeaderMaxHeight == 0 ? 0 : (mCurrentPos - mHeaderMinHeight) * 1f / (mHeaderMaxHeight - mHeaderMinHeight);
        return currentPercent;
    }
}

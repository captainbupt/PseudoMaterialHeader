package com.captainhwz.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.TextView;

import com.captainhwz.layout.R;

public class MaterialHeaderLayout extends ViewGroup {

    final boolean DEBUG = true;
    final boolean DEBUG_LAYOUT = true;
    final String LOG_TAG = "HeaderLayout";

    ContentHandler mContentHandler;
    HeaderHandler mHeaderHandler;
    MotionEventIndicator mIndicator;

    View mHeaderView;
    View mContent;
    ScrollChecker mScrollChecker;
    int mHeaderId;
    int mContainerId;

    int mHeaderMaxHeight;
    int mHeaderMinHeight;
    int mOriginHeaderMinHeight; // 不带margin的高度，可直接设置

    MotionEvent mLastMoveEvent;
    private boolean mHasSendCancelEvent;
    private boolean mPreventForHorizontal;
    private boolean mDisableWhenHorizontalMove;
    private float mPagingTouchSlop;

    public MaterialHeaderLayout(Context context) {
        this(context, null);
    }

    public MaterialHeaderLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterialHeaderLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.MaterialHeaderLayout, 0, 0);
        if (arr != null) {

            //mHeaderId = arr.getResourceId(R.styleable.MaterialHeaderLayout_header, mHeaderId);
            mContainerId = arr.getResourceId(R.styleable.MaterialHeaderLayout_content, mContainerId);
            mOriginHeaderMinHeight = arr.getDimensionPixelOffset(R.styleable.MaterialHeaderLayout_minHeight, 0);

            if (DEBUG) {
                Log.d(LOG_TAG, "attr: minHeight: " + mHeaderMinHeight);
            }
            arr.recycle();
        }
        mScrollChecker = new ScrollChecker();
        mIndicator = new MotionEventIndicator();
    }

    @Override
    protected void onFinishInflate() {
        final int childCount = getChildCount();
        if (childCount > 2) {
            throw new IllegalStateException("PtrFrameLayout only can host 2 elements");
        } else if (childCount == 2) {
            if (mHeaderId != 0 && mHeaderView == null) {
                mHeaderView = findViewById(mHeaderId);
            }
            if (mContainerId != 0 && mContent == null) {
                mContent = findViewById(mContainerId);
            }
            // not specify header or content
            if (mContent == null || mHeaderView == null) {

                View child1 = getChildAt(0);
                View child2 = getChildAt(1);
                if (child1 instanceof HeaderHandler) {
                    mHeaderView = child1;
                    mContent = child2;
                } else if (child2 instanceof HeaderHandler) {
                    mHeaderView = child2;
                    mContent = child1;
                } else {
                    // both are not specified
                    if (mContent == null && mHeaderView == null) {
                        mHeaderView = child1;
                        mContent = child2;
                    }
                    // only one is specified
                    else {
                        if (mHeaderView == null) {
                            mHeaderView = mContent == child1 ? child2 : child1;
                        } else {
                            mContent = mHeaderView == child1 ? child2 : child1;
                        }
                    }
                }
            }
        } else if (childCount == 1) {
            mContent = getChildAt(0);
        } else {
            TextView errorView = new TextView(getContext());
            errorView.setClickable(true);
            errorView.setTextColor(0xffff6600);
            errorView.setGravity(Gravity.CENTER);
            errorView.setTextSize(20);
            errorView.setText("The content view in PtrFrameLayout is empty. Do you forget to specify its id in xml layout file?");
            mContent = errorView;
            addView(mContent);
        }
        if (mContent instanceof ContentHandler) {
            mContentHandler = (ContentHandler) mContent;
        }
        if (mHeaderView != null) {
            mHeaderView.bringToFront();
            if (mHeaderView instanceof HeaderHandler) {
                mHeaderHandler = (HeaderHandler) mHeaderView;
            }
        }
        super.onFinishInflate();
    }

/*    void lpTransfer(View view){
        LayoutParams lp = view.getLayoutParams();
        MarginLayoutParams mlp = new MarginLayoutParams(lp.width,lp.height);
    }*/

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (DEBUG && DEBUG_LAYOUT) {
            Log.d(LOG_TAG, String.format("onMeasure frame: width: %s, height: %s, padding: %s %s %s %s",
                    getMeasuredHeight(), getMeasuredWidth(),
                    getPaddingLeft(), getPaddingRight(), getPaddingTop(), getPaddingBottom()));

        }

        if (mHeaderView != null) {
            measureChildWithMargins(mHeaderView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = (MarginLayoutParams) mHeaderView.getLayoutParams();
            mHeaderMaxHeight = mHeaderView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            mHeaderMinHeight = mOriginHeaderMinHeight + lp.topMargin + lp.bottomMargin;
            mIndicator.setHeight(mHeaderMaxHeight, mHeaderMinHeight);
            mIndicator.setCurrentPosIfFirst(mHeaderMaxHeight);
            if (DEBUG && DEBUG_LAYOUT) {
                Log.d(LOG_TAG, String.format("onMeasure, maxHeight: %s, minHeight: %s, margin: %s, %s, %s, %s", mHeaderMaxHeight, mHeaderMinHeight, lp.leftMargin, lp.topMargin, lp.rightMargin, lp.bottomMargin));
            }
            if (mContentHandler != null) {
                mContentHandler.onOffsetCalculated(mHeaderMaxHeight - mHeaderMinHeight);
            }
        }

        if (mContent != null) {
            measureContentView(mContent, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) mContent.getLayoutParams();
            if (DEBUG && DEBUG_LAYOUT) {
                Log.d(LOG_TAG, String.format("onMeasure content, width: %s, height: %s, margin: %s %s %s %s",
                        getMeasuredWidth(), getMeasuredHeight(),
                        lp.leftMargin, lp.topMargin, lp.rightMargin, lp.bottomMargin));
            }
        }
    }

    private void measureContentView(View child,
                                    int parentWidthMeasureSpec,
                                    int parentHeightMeasureSpec) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin, lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                getPaddingTop() + getPaddingBottom() + lp.topMargin + mHeaderMinHeight, lp.height);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean flag, int i, int j, int k, int l) {
        layoutChildren();
    }

    private void layoutChildren() {
        int offsetY = mIndicator.getCurrentPosY();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        if (mHeaderView != null) {
            MarginLayoutParams lp = (MarginLayoutParams) mHeaderView.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int top = paddingTop + lp.topMargin + offsetY - mHeaderMaxHeight;
            final int right = left + mHeaderView.getMeasuredWidth();
            final int bottom = top + mHeaderView.getMeasuredHeight();
            mHeaderView.layout(left, top, right, bottom);
            if (DEBUG && DEBUG_LAYOUT) {
                Log.d(LOG_TAG, String.format("onLayout header: %s %s %s %s", left, top, right, bottom));
            }
        }
        if (mContent != null) {
            MarginLayoutParams lp = (MarginLayoutParams) mContent.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int top = paddingTop + lp.topMargin + offsetY;
            final int right = left + mContent.getMeasuredWidth();
            final int bottom = top + mContent.getMeasuredHeight();
            if (DEBUG && DEBUG_LAYOUT) {
                Log.d(LOG_TAG, String.format("onLayout content: %s %s %s %s", left, top, right, bottom));
            }
            mContent.layout(left, top, right, bottom);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        if (!isEnabled() || mContent == null || mHeaderView == null) {
            return super.dispatchTouchEvent(e);
        }
        int action = e.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIndicator.onRelease();
                if (mIndicator.reachMinHeight()) {
                    if (DEBUG) {
                        Log.d(LOG_TAG, "call onRelease when user release");
                    }
                    if (mIndicator.hasMovedAfterPressedDown()) {
                        sendCancelEvent();
                        return true;
                    }
                    return super.dispatchTouchEvent(e);
                } else {
                    return super.dispatchTouchEvent(e);
                }

            case MotionEvent.ACTION_DOWN:
                mHasSendCancelEvent = false;
                mIndicator.onPressDown(e.getX(), e.getY(), e.getEventTime());

                mScrollChecker.abortIfWorking();

                mPreventForHorizontal = false;
                // The cancel event will be sent once the position is moved.
                // So let the event pass to children.
                // fix #93, #102
                super.dispatchTouchEvent(e);
                return true;

            case MotionEvent.ACTION_MOVE:
                mLastMoveEvent = e;
                mIndicator.onMove(e.getX(), e.getY(), e.getEventTime());
                float offsetX = mIndicator.getOffsetX();
                float offsetY = mIndicator.getOffsetY();

                if (mDisableWhenHorizontalMove && !mPreventForHorizontal && (Math.abs(offsetX) > mPagingTouchSlop && Math.abs(offsetX) > Math.abs(offsetY))) {
                    if (mIndicator.reachMinHeight()) {
                        mPreventForHorizontal = true;
                    }
                }
                if (mPreventForHorizontal) {
                    return super.dispatchTouchEvent(e);
                }

                boolean moveDown = offsetY > 0;
                boolean moveUp = !moveDown;
                boolean canMoveUp = !mIndicator.reachMinHeight();

                boolean canMoveDown = mContentHandler != null ? mContentHandler.checkCanDoRefresh(this, mContent, mHeaderView) : DefaultContentHandler.checkContentCanBePulledDown(this, mContent, mHeaderView);

                if (DEBUG) {
                    Log.v(LOG_TAG, String.format("ACTION_MOVE: offsetY:%s, currentPos: %s, moveUp: %s, canMoveUp: %s, moveDown: %s: canMoveDown: %s", offsetY, mIndicator.getCurrentPosY(), moveUp, canMoveUp, moveDown, canMoveDown));
                }

                // disable move when header not reach top
                if (moveDown && !canMoveDown) {
                    return super.dispatchTouchEvent(e);
                }

                if ((moveUp && canMoveUp) || moveDown) {
                    // has reached the top
                    if ((offsetY < 0 && mIndicator.reachMinHeight())) {
                        if (DEBUG) {
                            Log.v(LOG_TAG, String.format("has reached the top"));
                        }
                        return super.dispatchTouchEvent(e);
                    }
                    // has reached the top
                    if ((offsetY > 0 && mIndicator.reachMaxHeight())) {
                        if (DEBUG) {
                            Log.v(LOG_TAG, String.format("has reached the bottom"));
                        }
                        return super.dispatchTouchEvent(e);
                    }

                    Log.v(LOG_TAG, "move pos: " + offsetY + ", speed: " + offsetY / mIndicator.getOffsetTime());
                    if (Math.abs(offsetY / mIndicator.getOffsetTime()) > 2) {
                        if (offsetY > 0) {
                            mScrollChecker.tryToScrollTo(mHeaderMaxHeight, 300);
                        } else {
                            mScrollChecker.tryToScrollTo(mHeaderMinHeight, 300);
                        }
                    } else {
                        movePos(offsetY);
                    }
                    if (Math.abs(offsetY / offsetX) < Math.tan(Math.PI / 6)) {
                        MotionEvent event = MotionEvent.obtain(e);
                        event.setLocation(e.getX(), e.getY() - offsetY);
                        super.dispatchTouchEvent(event);
                    }
                    return true;
                }
                break;
        }
        return super.dispatchTouchEvent(e);
    }

    /**
     * if deltaY > 0, move the content down
     *
     * @param deltaY
     */
    private void movePos(float deltaY) {

        int to = mIndicator.getCurrentPosY() + (int) deltaY;

        // over top
        if (mIndicator.willOverTop(to)) {
            if (DEBUG) {
                Log.e(LOG_TAG, String.format("over top"));
            }
            to = mIndicator.getMinHeight();
        } else if (mIndicator.willOverBottom(to)) {
            if (DEBUG) {
                Log.e(LOG_TAG, String.format("over bottom"));
            }
            to = mIndicator.getMaxHeight();
        }

        mIndicator.setCurrentPos(to);
        int change = to - mIndicator.getLastPosY();
        updatePos(change);
    }

    private void updatePos(int change) {
        if (mHeaderHandler != null)
            mHeaderHandler.onChange(mIndicator.getCurrentPercent(), change);
        if (mContentHandler != null)
            mContentHandler.onChange(mIndicator.getCurrentPercent(), change);
        mHeaderView.offsetTopAndBottom(change);
        mContent.offsetTopAndBottom(change);
        invalidate();
    }

    private void sendCancelEvent() {
        if (DEBUG) {
            Log.d(LOG_TAG, "send cancel event");
        }
        // The ScrollChecker will update position and lead to send cancel event when mLastMoveEvent is null.
        // fix #104, #80, #92
        if (mLastMoveEvent == null) {
            return;
        }
        MotionEvent last = mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime() + ViewConfiguration.getLongPressTimeout(), MotionEvent.ACTION_CANCEL, last.getX(), last.getY(), last.getMetaState());
        super.dispatchTouchEvent(e);
    }

    public void setHeaderView(View header) {
        if (mHeaderView != null && header != null && mHeaderView != header) {
            removeView(mHeaderView);
        }
        ViewGroup.LayoutParams lp = header.getLayoutParams();
        if (lp == null) {
            lp = new LayoutParams(-1, -2);
            header.setLayoutParams(lp);
        }
        mHeaderView = header;
        addView(header);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        @SuppressWarnings({"unused"})
        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    class ScrollChecker implements Runnable {

        private int mLastFlingY;
        private Scroller mScroller;
        private boolean mIsRunning = false;
        private int mStart;
        private int mTo;

        public ScrollChecker() {
            mScroller = new Scroller(getContext());
        }

        public void run() {
            boolean finish = !mScroller.computeScrollOffset() || mScroller.isFinished();
            int curY = mScroller.getCurrY();
            int deltaY = curY - mLastFlingY;
            if (DEBUG) {
                if (deltaY != 0) {
                    Log.v(LOG_TAG,
                            String.format("scroll: %s, start: %s, to: %s, currentPos: %s, current :%s, last: %s, delta: %s",
                                    finish, mStart, mTo, mIndicator.getCurrentPosY(), curY, mLastFlingY, deltaY));
                }
            }
            if (!finish) {
                mLastFlingY = curY;
                movePos(deltaY);
                post(this);
            } else {
                finish();
            }
        }

        private void finish() {
            if (DEBUG) {
                Log.v(LOG_TAG, String.format("finish, currentPos:%s", mIndicator.getCurrentPosY()));
            }
            reset();
        }

        private void reset() {
            mIsRunning = false;
            mLastFlingY = 0;
            removeCallbacks(this);
        }

        public void abortIfWorking() {
            if (mIsRunning) {
                if (!mScroller.isFinished()) {
                    mScroller.forceFinished(true);
                }
                reset();
            }
        }

        public void tryToScrollTo(int to, int duration) {
            if (mIndicator.isAlreadyHere(to)) {
                return;
            }
            mStart = mIndicator.getCurrentPosY();
            mTo = to;
            int distance = to - mStart;
            if (DEBUG) {
                Log.d(LOG_TAG, String.format("tryToScrollTo: start: %s, distance:%s, to:%s", mStart, distance, to));
            }
            removeCallbacks(this);

            mLastFlingY = 0;

            // fix #47: Scroller should be reused, https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh/issues/47
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
            mScroller.startScroll(0, 0, 0, distance, duration);
            post(this);
            mIsRunning = true;
        }
    }
}

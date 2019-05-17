package com.andy.extend.layout.demo.drag;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

/**
 * Created by luofan on 2019/5/16.
 */
public class DragLayout extends RelativeLayout {

    private int mHeadHeight;

    private ViewDragHelper mDragHelper;

    private ViewDragHelper.Callback mCallback = new DragCallback();

    private DragHeader mHeadView;

    private View mContentView;

    private int mHeadViewHeight;

    private int mContentViewHeight;

    private int mDragFlag = 4;

    private int lastValue;

    private static final int STATE_SHOW_CONTENT = 0;

    private static final int STATE_SHOW_HEAD = 1;

    private static final int STATE_RUN_ANIMATION = 2;

    private ValueAnimator showHeadAnimator;

    private ValueAnimator showContentAnimator;
    /**
     * TODO 上次和本次状态未改变情况后面优化
     */
    private onViewChangedListener mViewChangedListener;

    public DragLayout(Context context) {
        super(context);
        init();
    }

    public DragLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setViewChangedListener(onViewChangedListener mViewChangedListener) {
        this.mViewChangedListener = mViewChangedListener;
    }

    private void init() {
        mDragHelper = ViewDragHelper.create(this, 1.5f, mCallback);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mHeadHeight = getMeasuredHeight();
                View head = getChildAt(0);
                if (head instanceof DragHeader) {
                    mHeadView = (DragHeader) getChildAt(0);
                    mContentView = getChildAt(1);
                    mHeadViewHeight = mHeadView.getMeasuredHeight();
                    mContentViewHeight = mContentView.getMeasuredHeight();
                    ViewCompat.offsetTopAndBottom(mHeadView, -mHeadHeight);
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    throw new IllegalStateException("布局异常");
                }
            }
        });
    }

    private void changePoint(int top) {
        float max = mHeadHeight / 4;
        float percent = top / max;
        mHeadView.setPercent(percent);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //决定是否拦截当前事件
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //处理事件
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (mDragHelper != null && mDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    private int getState() {
        if (mContentView.getTop() == mHeadHeight) {
            return STATE_SHOW_HEAD;
        } else if (mContentView.getTop() == 0) {
            return STATE_SHOW_CONTENT;
        } else {
            return STATE_RUN_ANIMATION;
        }
    }

    public void setShowHead() {
        lastValue = 0;
        if (getState() == STATE_SHOW_HEAD) {
            return;
        }
        if (cancelAnimation()) {
            return;
        }
        if (mViewChangedListener != null) {
            mViewChangedListener.onContentChanged(false);
            mViewChangedListener.onHeadChanged(true);
        }
        if (showHeadAnimator == null) {
            showHeadAnimator = ValueAnimator.ofInt(lastValue, mHeadHeight);
            showHeadAnimator.setDuration(500);
            showHeadAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    ViewCompat.offsetTopAndBottom(mHeadView, mHeadHeight - mContentView.getTop());
                    ViewCompat.offsetTopAndBottom(mContentView, mHeadHeight - mContentView.getTop());
                }
            });
            showHeadAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int result = (int) animation.getAnimatedValue();

                    int dy = result - lastValue;
                    ViewCompat.offsetTopAndBottom(mHeadView, dy);
                    ViewCompat.offsetTopAndBottom(mContentView, dy);
                    lastValue = result;
                }
            });
        }
        showHeadAnimator.start();
    }

    private boolean cancelAnimation() {
        if (showHeadAnimator != null && showHeadAnimator.isRunning()) {
            showHeadAnimator.cancel();
            return true;
        }
        if (showContentAnimator != null && showContentAnimator.isRunning()) {
            showContentAnimator.cancel();
            return true;
        }
        return false;
    }

    public void setShowContent() {
        lastValue = 0;
        if (getState() == STATE_SHOW_CONTENT) {
            return;
        }
        if (cancelAnimation()) {
            return;
        }
        if (mViewChangedListener != null) {
            mViewChangedListener.onContentChanged(true);
            mViewChangedListener.onHeadChanged(false);
        }
        if (showContentAnimator == null) {
            showContentAnimator = ValueAnimator.ofInt(lastValue, mHeadHeight);
            showContentAnimator.setDuration(500);
            showContentAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationCancel(Animator animation) {
                    ViewCompat.offsetTopAndBottom(mHeadView, -mContentView.getTop());
                    ViewCompat.offsetTopAndBottom(mContentView, -mContentView.getTop());
                }
            });
            showContentAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int result = (int) animation.getAnimatedValue();
                    int dy = result - lastValue;
                    ViewCompat.offsetTopAndBottom(mContentView, -dy);
                    ViewCompat.offsetTopAndBottom(mHeadView, -dy);
                    lastValue = result;
                }
            });
        }
        showContentAnimator.start();
    }

    class DragCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(@NonNull View view, int i) {
            return view == mHeadView || view == mContentView;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            Log.e("xxxxxxxxx", mContentView.getTop() + "xxxxxxxonViewPositionChanged" + mHeadView.getTop());
            if (changedView == mContentView) {
                ViewCompat.offsetTopAndBottom(mHeadView, dy);
                changePoint(top);
            } else {
                ViewCompat.offsetTopAndBottom(mContentView, dy);
            }
        }

        @Override
        public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            int end = 0;
            if (releasedChild == mContentView) {
                if (mContentView.getTop() > mContentViewHeight / mDragFlag) {
                    end = mHeadHeight;
                }
                if (mViewChangedListener != null) {
                    if (end == mHeadHeight) {
                        mViewChangedListener.onContentChanged(false);
                        mViewChangedListener.onHeadChanged(true);
                    } else {
                        mViewChangedListener.onContentChanged(true);
                        mViewChangedListener.onHeadChanged(false);
                    }
                }
            } else if (releasedChild == mHeadView) {
                if (mHeadView.getTop() < -mHeadViewHeight / mDragFlag) {
                    end = -mHeadHeight;
                }
                if (mViewChangedListener != null) {
                    if (end == -mHeadHeight) {
                        mViewChangedListener.onContentChanged(true);
                        mViewChangedListener.onHeadChanged(false);
                    } else {
                        mViewChangedListener.onContentChanged(false);
                        mViewChangedListener.onHeadChanged(true);
                    }
                }
            }
            mDragHelper.settleCapturedViewAt(0, end);
            invalidate();
        }

        @Override
        public void onEdgeTouched(int edgeFlags, int pointerId) {
            super.onEdgeTouched(edgeFlags, pointerId);
        }

        @Override
        public boolean onEdgeLock(int edgeFlags) {
            return super.onEdgeLock(edgeFlags);
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            super.onEdgeDragStarted(edgeFlags, pointerId);
        }

        @Override
        public int getOrderedChildIndex(int index) {
            return super.getOrderedChildIndex(index);
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            return 0;
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            if (child == mContentView) {
                if (top > mHeadViewHeight) {
                    return mHeadViewHeight;
                } else if (top < 0) {
                    return 0;
                }
                return top;
            } else {
                if (top > 0) {
                    return 0;
                }
                return top;
            }
        }
    }

    public interface onViewChangedListener {
        void onHeadChanged(boolean isShow);

        void onContentChanged(boolean isShow);
    }
}

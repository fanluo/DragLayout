package com.andy.extend.layout.demo.drag;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.andy.extend.layout.demo.R;

/**
 * Created by luofan on 2019/5/17.
 */
public class DragHeader extends FrameLayout {

    private FrameLayout mContentLayout;

    private DragPoint mExpendPoint;

    private TextView mTipTv;

    public DragHeader(@NonNull Context context) {
        this(context, null);
    }

    public DragHeader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragHeader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.drag_header, this);
        mContentLayout = findViewById(R.id.layout_header_content);
        mExpendPoint = findViewById(R.id.expend_point);
        mTipTv = findViewById(R.id.tv_tip);
    }

    public void setPercent(float percent) {
        if (percent <= 0.5f) {
            mTipTv.setAlpha(1);
            mExpendPoint.setAlpha(1);
        } else {
            float alpha = 1 - percent / 2;
            mTipTv.setAlpha(alpha);
            mExpendPoint.setAlpha(alpha);
        }
        mContentLayout.setAlpha(percent);
        if (percent <= 1) {
            mExpendPoint.setPercent(percent);
        } else {
            mExpendPoint.setPercent(1);
        }
    }
}

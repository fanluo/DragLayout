package com.andy.extend.layout.demo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.andy.extend.layout.demo.drag.DragLayout;

/**
 * Created by luofan on 2019/5/16.
 */
public class DragAct extends AppCompatActivity {

    DragLayout mDragLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag);
        mDragLayout = findViewById(R.id.drag_layout);
        mDragLayout.setViewChangedListener(new DragLayout.onViewChangedListener() {
            @Override
            public void onHeadChanged(boolean isShow) {
                Log.e("xxxxxxxxxx", "xxxxxxxxxonHeadChanged" + isShow);
            }

            @Override
            public void onContentChanged(boolean isShow) {
                Log.e("xxxxxxxxxx", "xxxxxxxxxonContentChanged" + isShow);
            }
        });
        Button head = findViewById(R.id.btn_show_head);
        Button content = findViewById(R.id.btn_show_content);
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDragLayout.setShowContent();
            }
        });
        head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDragLayout.setShowHead();
            }
        });
        addFragment(R.id.layout_head, HeadFragment.class.getName(), new Bundle());
        addFragment(R.id.layout_content, ContentFragment.class.getName(), new Bundle());
    }

    public void addFragment(int layoutId, String fragmentName, @NonNull Bundle bundle) {
        String fragmentTag = fragmentName;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if (fragment == null) {
            fragment = Fragment.instantiate(this, fragmentName);
        }
        if (!fragment.isAdded()) {
            transaction.add(layoutId, fragment, fragmentTag);
        }
        fragment.setArguments(bundle);
        transaction.show(fragment).commit();
    }
}

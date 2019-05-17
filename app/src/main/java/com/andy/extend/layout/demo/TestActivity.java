package com.andy.extend.layout.demo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by luofan on 2019/5/16.
 */
public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        switchFragment(TestFragment.class.getName(), new Bundle());
    }

    public void switchFragment(String fragmentName, @NonNull Bundle bundle) {
        String fragmentTag = fragmentName;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
        if (fragment == null) {
            fragment = Fragment.instantiate(this, fragmentName);
        }
        if (!fragment.isAdded()) {
            transaction.add(R.id.frameLayout, fragment, fragmentTag);
        }
        fragment.setArguments(bundle);
        transaction.show(fragment).commit();
    }
}

package com.shirokuma.musicplayer.common;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public abstract class BaseActivity extends FragmentActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setContentViewRes());
        initData();
        initView();
        process();
    }

    protected abstract void initData();

    protected abstract void initView();

    protected void process() {
    }

    protected abstract int setContentViewRes();
}
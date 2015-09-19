package com.shirokuma.musicplayer.common;

import android.app.Activity;
import android.os.Bundle;

public abstract class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setContentViewRes());
        initData();
        initView();
    }

    protected abstract void initData();

    protected abstract void initView();

    protected abstract int setContentViewRes();
}
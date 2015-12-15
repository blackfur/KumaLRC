package com.shiro.memo;

import android.app.Application;

public class MemoApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Memorize.init(this);
    }
}

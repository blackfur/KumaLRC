package com.shiro.linguistics;

import android.app.Application;

public class LinguisticsApp extends Application {
    public void onCreate() {
        super.onCreate();
        Linguistics.init(this);
    }
}

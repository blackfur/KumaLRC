package com.shirokuma.musicplayer;

import android.app.Application;

public class KumalrcApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        KumaPlayer.init(this);
    }
}

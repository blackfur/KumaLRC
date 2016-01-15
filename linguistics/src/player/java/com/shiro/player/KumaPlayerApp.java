package com.shiro.player;

import android.app.Application;
import com.shirokuma.musicplayer.KumaPlayer;

public class KumaPlayerApp extends Application {
    public void onCreate() {
        super.onCreate();
        KumaPlayer.init(this);
    }
}

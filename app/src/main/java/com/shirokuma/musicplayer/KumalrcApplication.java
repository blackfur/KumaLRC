package com.shirokuma.musicplayer;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import com.shirokuma.musicplayer.playback.MusicService;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class KumalrcApplication extends Application {
    Timer mTimer;
    static List<Activity> mBoundActivities = new ArrayList<Activity>();

    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this, MusicService.class));
    }

    public void addBoundActivity(Activity bound) {
        mBoundActivities.add(bound);
    }

    public void removeBoundActivity(Activity bound) {
        mBoundActivities.remove(bound);
    }

    public void sleepMode(int minutes) {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer.purge();
            mTimer = null;
        }
        if (minutes > 0) {
            mTimer = new Timer();
            long millis = minutes * 60 * 1000;
//            long millis = 3000;
            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    exit();
                }
            }, millis);
        }
    }

    public void exit() {
        for (Activity binded : mBoundActivities) {
            binded.finish();
        }
        stopService(new Intent(this, MusicService.class));
    }
}

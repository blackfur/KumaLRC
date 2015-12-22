package com.shirokuma.musicplayer;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import com.activeandroid.ActiveAndroid;
import com.shirokuma.musicplayer.playback.MusicService;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class KumaPlayer {
    public static final String ARGUMENTS_KEY_FILTER = "filter";
    public static final int SEEK_INTERVAL = 1000;
    static Timer mTimer;
    static List<Activity> mBoundActivities = new ArrayList<Activity>();
    static Application application;
    static boolean initialized = false;

    public static void init(Application app) {
        if (initialized)
            return;
        application = app;
        app.startService(new Intent(app, MusicService.class));
        // database
        ActiveAndroid.initialize(app);
        initialized = true;
    }

    public static void addBoundActivity(Activity bound) {
        mBoundActivities.add(bound);
    }

    public static void removeBoundActivity(Activity bound) {
        mBoundActivities.remove(bound);
    }

    public static void sleepMode(int minutes) {
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

    public static void exit() {
        for (Activity binded : mBoundActivities) {
            binded.finish();
        }
        if (application != null)
            application.stopService(new Intent(application, MusicService.class));
    }
}

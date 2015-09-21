package com.shirokuma.musicplayer.setting;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class MediaSetting {
    private static MediaSetting mInstance;
    private SharedPreferences mSharedPre;
    private final String KEY_LAST_PLAY_INDEX = "last_play_index";
    private final String KEY_FIRST_INSTALL = "first_install";
    private final String KEY_SHUFFLE = "shuffle";
    private final String KEY_LAST_PLAY_PROGRESS = "last_play_progress";

    public MediaSetting(Context context) {
        mInstance = this;
        //实例化SharedPreferences对象（第一步）
        mSharedPre = context.getSharedPreferences(context.getPackageName(),
                Activity.MODE_PRIVATE);
    }

    public static MediaSetting getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MediaSetting(context);
        }
        return mInstance;
    }

    public void setLastPlayIndex(int index) {
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putInt(KEY_LAST_PLAY_INDEX, index);
        editor.commit();
    }

    public int getLastPlayIndex() {
        return mSharedPre.getInt(KEY_LAST_PLAY_INDEX, 0);
    }

    public void setFirstInstall(boolean b) {
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putBoolean(KEY_FIRST_INSTALL, b);
        editor.commit();
    }

    public boolean getFirstInstall() {
        return mSharedPre.getBoolean(KEY_FIRST_INSTALL, true);
    }

    public void setShuffle(boolean b) {
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putBoolean(KEY_SHUFFLE, b);
        editor.commit();
    }

    public boolean getShuffle() {
        return mSharedPre.getBoolean(KEY_SHUFFLE, true);
    }

    public void setLastPlayProgress(int p) {
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putInt(KEY_LAST_PLAY_PROGRESS, p);
        editor.commit();
    }

    public int getLastPlayProgress() {
        return mSharedPre.getInt(KEY_LAST_PLAY_PROGRESS, 0);
    }
}

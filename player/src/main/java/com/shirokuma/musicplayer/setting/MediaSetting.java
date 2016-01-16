package com.shirokuma.musicplayer.setting;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import com.shirokuma.musicplayer.musiclib.Filter;

public class MediaSetting {
    private static MediaSetting mInstance;
    private SharedPreferences mSharedPre;
    private final String KEY_LAST_PLAY_INDEX = "last_play_index";
    private final String KEY_FIRST_INSTALL = "first_install";
    private final String KEY_SHUFFLE = "shuffle";
    private final String KEY_LAST_PLAY_SAVE = "last_play_save";
    private final String KEY_LAST_FITER_TYPE = "last_fiter_type";
    private final String KEY_LAST_FITER_ARTIST = "last_fiter_artist";
    private final String KEY_LAST_FITER_ALBUM = "last_fiter_album";

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
        editor.apply();
    }

    public int getLastPlayIndex() {
        return mSharedPre.getInt(KEY_LAST_PLAY_INDEX, 0);
    }

    public void setFirstInstall(boolean b) {
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putBoolean(KEY_FIRST_INSTALL, b);
        editor.apply();
    }

    public boolean getFirstInstall() {
        return mSharedPre.getBoolean(KEY_FIRST_INSTALL, true);
    }

    public void setShuffle(boolean b) {
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putBoolean(KEY_SHUFFLE, b);
        editor.apply();
    }

    public boolean getShuffle() {
        return mSharedPre.getBoolean(KEY_SHUFFLE, true);
    }

    public void setLastFilter(Filter f) {
        if (f != null) {
            SharedPreferences.Editor editor = mSharedPre.edit();
            editor.putInt(KEY_LAST_FITER_TYPE, f.type.getId());
            editor.putString(KEY_LAST_FITER_ALBUM, f.album);
            editor.putString(KEY_LAST_FITER_ARTIST, f.artist);
            editor.apply();
        }
    }

    public Filter getLastFilter() {
        Filter f = new Filter(null, null, null);
        f.type = Filter.FilterType.valueOfId(mSharedPre.getInt(KEY_LAST_FITER_TYPE, Filter.FilterType.Song.getId()));
        f.album = mSharedPre.getString(KEY_LAST_FITER_ALBUM, null);
        f.artist = mSharedPre.getString(KEY_LAST_FITER_ARTIST, null);
        return f;
    }

    public void setSaveLast(boolean b) {
        SharedPreferences.Editor editor = mSharedPre.edit();
        editor.putBoolean(KEY_LAST_PLAY_SAVE, b);
        editor.apply();
    }

    public boolean getSaveLast() {
        return mSharedPre.getBoolean(KEY_LAST_PLAY_SAVE, true);
    }
}

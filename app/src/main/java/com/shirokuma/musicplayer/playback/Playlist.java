package com.shirokuma.musicplayer.playback;

import android.graphics.Bitmap;
import com.shirokuma.musicplayer.common.Filter;
import com.shirokuma.musicplayer.common.Music;

public class Playlist implements Music {
    String name;

    public Playlist(String[] pars) {
        name = pars[0];
    }

    @Override
    public Bitmap icon() {
        return null;
    }

    @Override
    public String head() {
        return name;
    }

    @Override
    public String subhead() {
        return null;
    }

    @Override
    public String remark() {
        return null;
    }

    @Override
    public Filter.FilterType type() {
        return Filter.FilterType.Playlist;
    }
}

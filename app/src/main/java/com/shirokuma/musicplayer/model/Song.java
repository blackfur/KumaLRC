package com.shirokuma.musicplayer.model;

import android.graphics.Bitmap;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.shirokuma.musicplayer.musiclib.Filter;
import com.shirokuma.musicplayer.musiclib.Music;

@Table(name = "Songs")
public class Song extends Model implements Music {
    public long songid;
    @Column(name = "title", index = true)
    public String title;
    @Column(name = "artist", index = true)
    public String artist;
    @Column(name = "progress")
    public int progress;
    public String lrc;
    public String album;
    public String dir;
    public String path;

    public Song() {
        super();
    }

    public Song(String t, String a, int p) {
        super();
        title = t;
        artist = a;
        progress = p;
    }

    public Song(String[] pars) {
        songid = Long.valueOf(pars[0]);
        title = pars[1];
        artist = pars[2];
        lrc = pars[3];
        album = pars[4];
        dir = pars[5];
        path = pars[6];
    }

    @Override
    public Bitmap icon() {
        return null;
    }

    @Override
    public String head() {
        return title;
    }

    @Override
    public String subhead() {
        return artist;
    }

    @Override
    public String remark() {
        return null;
    }

    @Override
    public Filter.FilterType type() {
        return Filter.FilterType.Song;
    }
}

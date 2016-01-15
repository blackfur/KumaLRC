package com.shirokuma.musicplayer.model;

import android.graphics.Bitmap;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table(name = "albums")
public class Album extends Model implements Music {
    @Column(name = "title", index = true)
    public String album;
    @Column(name = "amount")
    public int numsongs;

    public Album() {
        super();
    }

    public Album(String[] pars) {
        album = pars[0];
        numsongs = Integer.valueOf(pars[1]);
    }

    @Override
    public Bitmap icon() {
        return null;
    }

    @Override
    public String head() {
        return album;
    }

    @Override
    public String subhead() {
        return String.valueOf(numsongs);
    }

    @Override
    public String remark() {
        return null;
    }

    @Override
    public Filter.FilterType type() {
        return Filter.FilterType.Album;
    }
}

package com.shirokuma.musicplayer.model;

import android.graphics.Bitmap;
import android.util.Log;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.shirokuma.musicplayer.PlayerEnv;
import com.shirokuma.musicplayer.musiclib.Filter;

@Table(name = "artists")
public class Artist extends Model implements Music {
    @Column(name = "name", index = true, unique = true, onUniqueConflict = Column.ConflictAction.IGNORE)
    public String name;
    @Column(name = "amount")
    public int amount;

    public Artist() {
        super();
        Log.e(PlayerEnv.TAG,"==== construct artist ====");
    }

    public Artist(String[] pars) {
        name = pars[0];
        amount = Integer.valueOf(pars[1]);
    }

    public Artist(String name) {
        super();
        Log.e(PlayerEnv.TAG,"==== construct artist ====");
        this.name = name;
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
        return String.valueOf(amount);
    }

    @Override
    public String remark() {
        return null;
    }

    @Override
    public Filter.FilterType type() {
        return Filter.FilterType.Artist;
    }
}

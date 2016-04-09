package com.shirokuma.musicplayer.model;

import android.graphics.Bitmap;
import android.util.Log;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.shirokuma.musicplayer.PlayerEnv;
import com.shirokuma.musicplayer.musiclib.Filter;

@Table(name = "albums")
public class Album extends Model implements Music {
    @Column(name = "title", index = true, unique = true, onUniqueConflict = Column.ConflictAction.IGNORE)
    public String title;
    @Column(name = "amount")
    public int numsongs;

    public Album() {
        super();
        Log.e(PlayerEnv.TAG,"==== construct album ====");
    }

    public Album(String[] pars) {
        title = pars[0];
        numsongs = Integer.valueOf(pars[1]);
    }

    public Album(String title) {
        super();
        Log.e(PlayerEnv.TAG,"==== construct album ====");
        this.title = title;
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

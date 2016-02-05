package com.shirokuma.musicplayer.model;

import android.graphics.Bitmap;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.shirokuma.musicplayer.musiclib.Filter;

public class Folder extends Model implements Music {
    @Column(name = "path", index = true, unique = true, onUniqueConflict = Column.ConflictAction.IGNORE)
    public String path;
    @Column(name = "count")
    public int count;

    public Folder() {
        super();
    }

    @Override
    public Bitmap icon() {
        return null;
    }

    @Override
    public String head() {
        return path == null ? "" : path;
    }

    @Override
    public String subhead() {
        return String.valueOf(count);
    }

    @Override
    public String remark() {
        return null;
    }

    @Override
    public Filter.FilterType type() {
        return Filter.FilterType.Folder;
    }
}

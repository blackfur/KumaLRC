package com.shirokuma.musicplayer.model;

import android.graphics.Bitmap;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.util.Log;
import com.shirokuma.musicplayer.KumaPlayer;
import com.shirokuma.musicplayer.musiclib.Filter;

@Table(name = "Songs")
public class Song extends Model implements Music {
    public long songid;
    @Column(name = "title")
    public String title;
    @Column(name = "artist")
    public Artist artist;
    @Column(name = "progress")
    public int progress;
    @Column(name = "lrc")
    public String lrc;
    @Column(name = "album")
    public Album album;
    public String dir;
    @Column(name = "path", index = true, unique = true, onUniqueConflict = Column.ConflictAction.ABORT)
    public String path;
    public Song() {
        super();
    }

    public Song(String t, String a, int p) {
        super();
        title = t;
        artist = new Artist(a);
        progress = p;
    }

    public Song(String t, String a, String album, String path) {
        super();
        Log.e(KumaPlayer.TAG, "==== construct Song ====");
        title = t;
        artist = new Artist(a);
        this.album = new Album(album);
        this.path = path;
        Log.e(KumaPlayer.TAG, "==== constructed ====");
    }

    public Song(String[] pars) {
        songid = Long.valueOf(pars[0]);
        title = pars[1];
        artist = new Artist(pars[2]);
        lrc = pars[3];
        album = new Album();
        album.title = pars[4];
        dir = pars[5];
        path = pars[6];
    }

    @Override
    public Bitmap icon() {
        return null;
    }

    @Override
    public String head() {
        return title == null ? "" : title;
    }

    @Override
    public String subhead() {
        return artist == null ? "" : artist.name;
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

package com.shirokuma.musicplayer.model;

import android.graphics.Bitmap;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.activeandroid.util.Log;
import com.shirokuma.musicplayer.KumaPlayer;
import com.shirokuma.musicplayer.musiclib.Filter;

@Table(name = "songs")
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
    @Column(name = "folder")
    public Folder folder;
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

    /**
     * special for scanning then storing
     *
     * @param t
     * @param a
     * @param albumStr
     * @param path
     */
    public Song(String t, String a, String albumStr, String path) {
        super();
        Log.e(KumaPlayer.TAG, "==== construct Song ====");
        if (t != null)
            title = t;
        // save artist information
        if (a != null) {
            artist = new Select().from(Artist.class).where("name=?", a).executeSingle();
            if (artist != null) {
                artist.amount += 1;
            } else {
                artist = new Artist(a);
                artist.amount = 1;
            }
            long result = artist.save();
            if (result == -1) artist = null;
        }
        // save album information
        if (albumStr != null) {
            album = new Select().from(Album.class).where("title=?", albumStr).executeSingle();
            if (album != null) {
                album.numsongs += 1;
            } else {
                album = new Album(albumStr);
                album.numsongs = 1;
            }
            long result = album.save();
            if (result == -1) album = null;
        }
        this.path = path;
        // save folder information
        {
            String direcotry = path.substring(0, path.lastIndexOf("/"));
            folder = new Select().from(Folder.class).where("path=?", direcotry).executeSingle();
            if (folder != null) {
                folder.count += 1;
            } else {
                folder = new Folder();
                folder.count = 1;
                folder.path = direcotry;
            }
            long result = folder.save();
            if (result == -1) folder = null;
        }
        //
        Log.e(KumaPlayer.TAG, "==== constructed ====");
    }

    public Song(String[] pars) {
        songid = Long.valueOf(pars[0]);
        title = pars[1];
        artist = new Artist(pars[2]);
        lrc = pars[3];
        album = new Album();
        album.title = pars[4];
//        dir = pars[5];
        path = pars[6];
    }

    @Override
    public Bitmap icon() {
        return null;
    }

    @Override
    public String head() {
        if (title == null) {
            return path.substring(path.lastIndexOf('/'));
        }
        return title;
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

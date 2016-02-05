package com.shirokuma.musicplayer.musiclib;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import com.activeandroid.Model;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.shirokuma.musicplayer.KumaPlayer;
import com.shirokuma.musicplayer.model.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

// audio media filter for querying specific info: songs, artists, albums, playlist
public class Filter implements Parcelable {
    public FilterType type;
    public Folder folderFilter;
    public String album, artist;

    public Filter(FilterType type, String album, String artist) {
        this.type = type;
        this.album = album;
        this.artist = artist;
    }

    public Filter(Folder f) {
        type = FilterType.Song;
        folderFilter = f;
    }

    // query media info
    public ArrayList fetch(Context context) {
        return type.musicStore.fetch(context, artist, album);
    }

    // query media info
    public ArrayList fetch() {
//        return type.musicStore.fetch(context, artist, album);
        // query classification, or query all songs without filter
        if (type == FilterType.Folder || type == FilterType.Album || type == FilterType.Artist || type == FilterType.Playlist || (type == FilterType.Song && folderFilter == null && album == null && artist == null))
            return type.fetch();
        // query songs with filter
        if (type == FilterType.Song) {
            if (folderFilter != null)
                return type.fetch(folderFilter);
        }
        return type.fetch(artist, album);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(type.getId());
        parcel.writeString(album);
        parcel.writeString(artist);
    }

    public static final Parcelable.Creator<Filter> CREATOR = new Parcelable.Creator<Filter>() {
        @Override
        public Filter createFromParcel(Parcel source) {
            Filter f = new Filter(null, null, null);
            f.type = FilterType.valueOfId(source.readInt());
            f.album = source.readString();
            f.artist = source.readString();
            return f;
        }

        @Override
        public Filter[] newArray(int size) {
            return null;
        }
    };
    public static MusicStore SongStore = new MusicStore() {
        @Override
        public ArrayList fetch(Context context, String artist, String album) {
            ArrayList mDisplayMusic = new ArrayList();
            //query external audio
            ContentResolver musicResolver = context.getContentResolver();
            Uri musicUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            StringBuilder selection = new StringBuilder();
            if (artist != null)
                selection.append(MediaStore.Audio.Media.ARTIST + "='" + artist + "'");
            if (album != null) {
                if (selection.length() > 0)
                    selection.append(" and ");
                selection.append(MediaStore.Audio.Media.ALBUM + "='" + album + "'");
            }
            Cursor musicCursor = musicResolver.query(musicUri, null, selection.toString(), null, null);
            //iterate over results if valid
            if (musicCursor != null && musicCursor.moveToFirst()) {
                //add mDisplayMusic to list
                do {
                    long id = musicCursor.getLong(musicCursor.getColumnIndex
                            (MediaStore.Audio.Media._ID));
                    String titleTemp = musicCursor.getString(musicCursor.getColumnIndex
                            (MediaStore.Audio.Media.TITLE));
                    String artistTemp = musicCursor.getString(musicCursor.getColumnIndex
                            (MediaStore.Audio.Media.ARTIST));
                    String albumTemp = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    String pathTemp = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA));
//                File file = new File(pathTemp);
                    String dir = pathTemp.substring(0, pathTemp.lastIndexOf(File.separator));
//                String lrcTrim = pathTemp.substring(0, pathTemp.length() - 4).trim() + ".lrc".trim();
                    String lrc = pathTemp.substring(0, pathTemp.lastIndexOf('.')) + ".lrc";
                    mDisplayMusic.add(new Song(new String[]{String.valueOf(id), titleTemp, artistTemp, lrc, albumTemp, dir, pathTemp}));
                } while (musicCursor.moveToNext());
                if (musicCursor != null)
                    musicCursor.close();
                //sort alphabetically by title
                Collections.sort(mDisplayMusic, new Comparator<Song>() {
                    public int compare(Song a, Song b) {
                        return a.title.compareTo(b.title);
                    }
                });
            }
            return mDisplayMusic;
        }
    };
    static MusicStore AlbumStore = new MusicStore() {
        @Override
        public ArrayList fetch(Context context, String artist, String albums) {
            ArrayList mDisplayMusic = new ArrayList();
            //query external audio
            Uri musicUri = android.provider.MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
            Cursor musicCursor = context.getContentResolver().query(musicUri, null, null, null, null);
            //iterate over results if valid
            if (musicCursor != null && musicCursor.moveToFirst()) {
                do {
                    String album = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM));
                    int albumnum = musicCursor.getInt(musicCursor.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS));
                    mDisplayMusic.add(new Album(new String[]{album, String.valueOf(albumnum)}));
                }
                while (musicCursor.moveToNext());
            }
            if (musicCursor != null)
                musicCursor.close();
            return mDisplayMusic;
        }
    };
    static MusicStore ArtistStore = new MusicStore() {
        @Override
        public ArrayList fetch(Context context, String artists, String album) {
            ArrayList mDisplayMusic = new ArrayList();
            //query external audio
            Uri musicUri = android.provider.MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
            Cursor musicCursor = context.getContentResolver().query(musicUri, null, null, null, null);
            //iterate over results if valid
            if (musicCursor != null && musicCursor.moveToFirst()) {
                do {
                    String artist = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST));
                    int numberOfTracks = musicCursor.getInt(musicCursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS));
                    mDisplayMusic.add(new Artist(new String[]{artist, String.valueOf(numberOfTracks)}));
                }
                while (musicCursor.moveToNext());
            }
            if (musicCursor != null)
                musicCursor.close();
            return mDisplayMusic;
        }
    };
    static MusicStore PlaylistStore = new MusicStore() {
        @Override
        public ArrayList fetch(Context context, String artist, String album) {
            ArrayList mDisplayMusic = new ArrayList();
            //query external audio
            Uri musicUri = android.provider.MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI;
            Cursor musicCursor = context.getContentResolver().query(musicUri, null, null, null, null);
            //iterate over results if valid
            if (musicCursor != null && musicCursor.moveToFirst()) {
                do {
                    String name = musicCursor.getString(musicCursor.getColumnIndex(MediaStore.Audio.Playlists.NAME));
                    mDisplayMusic.add(new Playlist(new String[]{name}));
                }
                while (musicCursor.moveToNext());
            }
            if (musicCursor != null)
                musicCursor.close();
            return mDisplayMusic;
        }
    };

    public enum FilterType {
        Song(0, "song", SongStore), Artist(1, "artist", ArtistStore), Album(2, "album", AlbumStore), Playlist(3, "playlist", PlaylistStore), Folder(4, "folder", null);
        int id;
        String tag;
        MusicStore musicStore;

        FilterType(int i, String tag, MusicStore store) {
            id = i;
            this.tag = tag;
            musicStore = store;
        }

        public int getId() {
            return id;
        }

        public ArrayList fetch() {
            List<Model> result = null;
            switch (id) {
                case 0:
                    Log.e(KumaPlayer.TAG, "==== select all songs ====");
                    From from = new Select().all().from(com.shirokuma.musicplayer.model.Song.class);
                    Log.e(KumaPlayer.TAG, from.toString());
                    result = from.execute();
                    break;
                case 1:
                    Log.e(KumaPlayer.TAG, "==== select artists ====");
                    result = new Select().all().distinct().from(com.shirokuma.musicplayer.model.Artist.class).execute();
                    break;
                case 2:
                    Log.e(KumaPlayer.TAG, "==== select albums ====");
                    result = new Select().all().distinct().from(com.shirokuma.musicplayer.model.Album.class).execute();
                    break;
                case 3:
                    Log.e(KumaPlayer.TAG, "==== select playlist ====");
                    break;
                case 4:
                    Log.e(KumaPlayer.TAG, "==== select folder ====");
                    result = new Select().all().distinct().from(com.shirokuma.musicplayer.model.Folder.class).execute();
                    break;
            }
            Log.e(KumaPlayer.TAG, "result: " + result);
            return (result == null ? new ArrayList() : new ArrayList(result));
        }

        public ArrayList fetch(Folder f) {
            List<Model> result = null;
            if (id == 0) {
                Log.e(KumaPlayer.TAG, "==== select songs filtered by folder ====");
                if (f != null) {
                    From from = new Select().from(com.shirokuma.musicplayer.model.Song.class).where("folder=?", f.getId());
                    Log.e(KumaPlayer.TAG, from.toString());
                    result = from.execute();
                }
            }
            Log.e(KumaPlayer.TAG, "result: " + result);
            return (result == null ? new ArrayList() : new ArrayList(result));
        }

        public ArrayList fetch(String artist, String album) {
            List<Model> result = null;
            switch (id) {
                case 0:
                    Log.e(KumaPlayer.TAG, "==== select songs ====");
                    if (album != null) {
                        From from = new Select().from(com.shirokuma.musicplayer.model.Song.class).as("a").innerJoin(com.shirokuma.musicplayer.model.Album.class).as("b").on("a.album=b.Id").where("b.title=?", album);
                        Log.e(KumaPlayer.TAG, from.toString());
                        result = from.execute();
                    } else if (artist != null) {
                        From from = new Select().from(com.shirokuma.musicplayer.model.Song.class).as("a").innerJoin(com.shirokuma.musicplayer.model.Artist.class).as("b").on("a.artist=b.Id").where("b.name=?", artist);
                        Log.e(KumaPlayer.TAG, from.toString());
                        result = from.execute();
                    } else {
                        From from = new Select().all().from(com.shirokuma.musicplayer.model.Song.class);
                        Log.e(KumaPlayer.TAG, from.toString());
                        result = from.execute();
                    }
                    break;
//                case 1:
//                    Log.e(KumaPlayer.TAG, "==== select artists ====");
//                    result = new Select().all().distinct().from(com.shirokuma.musicplayer.model.Artist.class).execute();
//                    break;
//                case 2:
//                    Log.e(KumaPlayer.TAG, "==== select albums ====");
//                    result = new Select().all().distinct().from(com.shirokuma.musicplayer.model.Album.class).execute();
//                    break;
//                case 3:
//                    Log.e(KumaPlayer.TAG, "==== select playlist ====");
//                    break;
//                case 4:
//                    Log.e(KumaPlayer.TAG, "==== select folder ====");
//                    result = new Select().all().distinct().from(com.shirokuma.musicplayer.model.Folder.class).execute();
//                    break;
            }
            Log.e(KumaPlayer.TAG, "result: " + result);
            return (result == null ? new ArrayList() : new ArrayList(result));
        }

        public static FilterType valueOfId(int i) {
            switch (i) {
                case 0:
                    return Song;
                case 1:
                    return Artist;
                case 2:
                    return Album;
                case 3:
                    return Playlist;
            }
            return null;
        }
    }

    public interface MusicStore {
        ArrayList fetch(Context context, String artist, String album);
    }
}

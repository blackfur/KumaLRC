package com.shirokuma.musicplayer.musiclib;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.MediaStore;
import com.shirokuma.musicplayer.common.Utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

// audio media filter for querying specific info: songs, artists, albums, playlist
public class Filter implements Parcelable {
    public FilterType type;
    public String album, artist;

    public Filter(FilterType type, String album, String artist) {
        this.type = type;
        this.album = album;
        this.artist = artist;
    }

    // query media info
    public ArrayList fetch(Context context) {
        return type.musicStore.fetch(context, artist, album);
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
        Song(0, "song", SongStore), Artist(1, "artist", ArtistStore), Album(2, "album", AlbumStore), Playlist(3, "playlist", PlaylistStore);
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

        public String getTag() {
            return tag;
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
